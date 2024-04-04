package necesse.entity.mobs.buffs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.Settings;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainerLimits;
import necesse.engine.modifiers.ModifierManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.packet.PacketMobBuffRemove;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobGenericEvent;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.level.maps.Level;

public class BuffManager extends ModifierManager<ActiveBuff> {
   private Mob owner;
   private final HashMap<Integer, ActiveBuff> buffs = new HashMap();
   private final BuffEventSubscriptions eventSubscriptions = new BuffEventSubscriptions();
   private final HashSet<Integer> movementTickBuffs = new HashSet();
   private final HashSet<Integer> humanDrawBuffs = new HashSet();
   private HashSet<Attacker> attackers = new HashSet();
   private int updateTimer;
   private boolean updateModifiers;

   public BuffManager(Mob var1) {
      super(BuffModifiers.LIST);
      this.owner = var1;
      this.makeQueryable(BuffModifiers.FIRE_DAMAGE_FLAT);
      this.makeQueryable(BuffModifiers.POISON_DAMAGE_FLAT);
      this.makeQueryable(BuffModifiers.FROST_DAMAGE_FLAT);
      super.updateModifiers();
   }

   public ActiveBuff addBuff(ActiveBuff var1, boolean var2) {
      return this.addBuff(var1, var2, false, true);
   }

   public ActiveBuff addBuff(ActiveBuff var1, boolean var2, boolean var3) {
      return this.addBuff(var1, var2, var3, true);
   }

   private ActiveBuff addBuff(ActiveBuff var1, boolean var2, boolean var3, boolean var4) {
      final int var5 = var1.buff.getID();
      ActiveBuff var6 = this.getBuff(var5);
      if (var6 != null && !var3) {
         var1.init(new BuffEventSubscriber() {
            public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2) {
               BuffManager.this.eventSubscriptions.addSubscription(var5, var1, var2);
            }
         });
         var6.stack(var1);
         var1 = var6;
      } else {
         synchronized(this.buffs) {
            this.buffs.put(var5, var1);
            if (var1.buff instanceof MovementTickBuff) {
               this.movementTickBuffs.add(var5);
            }

            if (var1.buff instanceof HumanDrawBuff) {
               this.humanDrawBuffs.add(var5);
            }
         }

         var1.init(new BuffEventSubscriber() {
            public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2) {
               BuffManager.this.eventSubscriptions.addSubscription(var5, var1, var2);
            }
         });
         if (var4) {
            var1.buff.firstAdd(var1);
         }
      }

      if (var2) {
         if (this.owner.isServer()) {
            this.owner.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobBuff(this.owner.getUniqueID(), var1), this.owner);
         } else if (this.owner.isClient()) {
            this.owner.getLevel().getClient().network.sendPacket(new PacketMobBuff(this.owner.getUniqueID(), var1));
         }
      }

      this.updateBuffs();
      return var6 != null ? var6 : var1;
   }

   public void tickMovement(float var1) {
      if (this.buffs != null) {
         HashSet var2;
         synchronized(this.buffs) {
            var2 = new HashSet(this.movementTickBuffs);
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            ActiveBuff var5;
            synchronized(this.buffs) {
               var5 = (ActiveBuff)this.buffs.get(var4);
            }

            if (var5 != null && !var5.isRemoved()) {
               ((MovementTickBuff)var5.buff).tickMovement(var5, var1);
            }
         }

      }
   }

   public void addHumanDraws(HumanDrawOptions var1) {
      if (this.buffs != null) {
         HashSet var2;
         synchronized(this.buffs) {
            var2 = new HashSet(this.humanDrawBuffs);
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            ActiveBuff var5;
            synchronized(this.buffs) {
               var5 = (ActiveBuff)this.buffs.get(var4);
            }

            if (var5 != null && !var5.isRemoved()) {
               ((HumanDrawBuff)var5.buff).addHumanDraw(var5, var1);
            }
         }

      }
   }

   public void serverTick() {
      boolean var1 = false;
      if (this.buffs != null) {
         HashSet var2;
         synchronized(this.buffs) {
            var2 = new HashSet(this.buffs.keySet());
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            ActiveBuff var5;
            synchronized(this.buffs) {
               var5 = (ActiveBuff)this.buffs.get(var4);
            }

            if (var5 != null) {
               var1 = var5.tickExpired() || var1;
               if (var5.isRemoved()) {
                  var5.buff.onRemoved(var5);
                  synchronized(this.buffs) {
                     this.buffs.remove(var4);
                     this.eventSubscriptions.removeSubscriptions(var4);
                     this.movementTickBuffs.remove(var4);
                     this.humanDrawBuffs.remove(var4);
                  }

                  var1 = true;
               } else {
                  var5.serverTick();
               }
            }
         }

         if (this.updateModifiers || ++this.updateTimer > 20) {
            var1 = true;
         }

         if (var1) {
            synchronized(this.buffs) {
               this.updateModifiers();
            }
         }

      }
   }

   public void clientTick() {
      boolean var1 = false;
      if (this.buffs != null) {
         HashSet var2;
         synchronized(this.buffs) {
            var2 = new HashSet(this.buffs.keySet());
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            ActiveBuff var5;
            synchronized(this.buffs) {
               var5 = (ActiveBuff)this.buffs.get(var4);
            }

            if (var5 != null) {
               var1 = var5.tickExpired() || var1;
               if (var5.isRemoved()) {
                  var5.buff.onRemoved(var5);
                  synchronized(this.buffs) {
                     this.buffs.remove(var4);
                     this.eventSubscriptions.removeSubscriptions(var4);
                     this.movementTickBuffs.remove(var4);
                     this.humanDrawBuffs.remove(var4);
                  }

                  var1 = true;
               } else {
                  var5.clientTick();
               }
            }
         }

         if (this.updateModifiers || ++this.updateTimer > 20) {
            var1 = true;
         }

         if (var1) {
            synchronized(this.buffs) {
               this.updateModifiers();
            }
         }

      }
   }

   public void tickDamageOverTime() {
      if (!this.owner.removed()) {
         if (this.owner.canTakeDamage()) {
            AtomicInteger var1 = new AtomicInteger();
            this.tickDamageOverTime(BuffModifiers.POISON_DAMAGE_FLAT, BuffModifiers.POISON_DAMAGE, var1);
            this.tickDamageOverTime(BuffModifiers.FIRE_DAMAGE_FLAT, BuffModifiers.FIRE_DAMAGE, var1);
            this.tickDamageOverTime(BuffModifiers.FROST_DAMAGE_FLAT, BuffModifiers.FROST_DAMAGE, var1);
            if (this.owner.isClient() && Settings.showDoTText && var1.get() > 0) {
               this.owner.spawnDamageText(var1.get(), 12, false);
            }
         }

      }
   }

   protected void tickDamageOverTime(Modifier<Float> var1, Modifier<Float> var2, AtomicInteger var3) {
      float var4 = (Float)this.getModifier(var2);
      if (!(var4 <= 0.0F)) {
         if (this.owner.isPlayer) {
            var4 *= this.owner.getLevel().getWorldSettings().difficulty.damageTakenModifier;
         }

         ModifierContainerLimits var5 = this.getLimits(var1);
         float var6 = var5.hasMax() ? (Float)var5.max() : Float.MAX_VALUE;
         float var7 = 0.0F;
         Iterator var8 = this.queryContainers(var1).iterator();

         while(var8.hasNext()) {
            ActiveBuff var9 = (ActiveBuff)var8.next();
            float var10 = (Float)var9.getModifier(var1) * (float)var9.getStacks();
            var7 += var10;
            if (var7 > var6) {
               var10 -= var7 - var6;
            }

            var9.dotBuffer += var10 * var4 / 20.0F;
            if (var10 > 0.0F) {
               this.owner.lastCombatTime = this.owner.getWorldEntity().getTime();
            }

            if (var9.dotBuffer >= 1.0F) {
               int var11 = (int)var9.dotBuffer;
               var9.dotBuffer -= (float)var11;
               Attacker var12 = var9.getAttacker();
               if (var12 == null) {
                  var12 = Mob.TOO_BUFFED_ATTACKER;
               }

               this.owner.setHealth(this.owner.getHealth() - var11, var12);
               var3.addAndGet(var11);
            }
         }

      }
   }

   public void updateBuffs() {
      this.updateModifiers = true;
   }

   public void forceUpdateBuffs() {
      synchronized(this.buffs) {
         this.updateModifiers();
      }
   }

   protected void updateModifiers() {
      Level var1 = this.owner.getLevel();
      Performance.record(var1 == null ? null : var1.tickManager(), "updateBuffs", (Runnable)(() -> {
         synchronized(this.buffs) {
            this.updateTimer = 0;
            this.updateModifiers = false;
            super.updateModifiers();
            if (this.owner.getHealth() > this.owner.getMaxHealth()) {
               this.owner.setHealth(this.owner.getMaxHealth());
            }

            this.attackers.clear();
            Stream var10000 = this.buffs.values().stream().map(ActiveBuff::getAttacker).filter(Objects::nonNull);
            HashSet var10001 = this.attackers;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
         }
      }));
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      Stream var1 = this.owner.getDefaultModifiers();
      if (this.owner.getLevel() != null) {
         var1 = Stream.concat(var1, this.owner.getLevel().getMobModifiers(this.owner));
      }

      Mob var2 = this.owner.getMount();
      if (var2 != null) {
         var1 = Stream.concat(var1, var2.getDefaultRiderModifiers());
      }

      return var1;
   }

   protected Iterable<ActiveBuff> getModifierContainers() {
      synchronized(this.buffs) {
         return this.buffs.values();
      }
   }

   public void submitMobEvent(MobGenericEvent var1) {
      this.eventSubscriptions.submitEvent(var1);
   }

   public float getBuffDurationModifier(ActiveBuff var1) {
      float var2 = 1.0F;
      if (var1.buff.isPotionBuff()) {
         var2 *= (Float)this.getModifier(BuffModifiers.POTION_DURATION);
      }

      return var2;
   }

   public HashMap<Integer, ActiveBuff> getBuffs() {
      return this.buffs;
   }

   public ArrayList<ActiveBuff> getArrayBuffs() {
      synchronized(this.buffs) {
         return new ArrayList(this.getBuffs().values());
      }
   }

   public ActiveBuff getBuff(int var1) {
      synchronized(this.buffs) {
         return (ActiveBuff)this.buffs.get(var1);
      }
   }

   public ActiveBuff getBuff(Buff var1) {
      synchronized(this.buffs) {
         return (ActiveBuff)this.buffs.get(var1.getID());
      }
   }

   public ActiveBuff getBuff(String var1) {
      return this.getBuff(BuffRegistry.getBuffID(var1));
   }

   public void clearBuffs() {
      synchronized(this.buffs) {
         this.buffs.clear();
         this.eventSubscriptions.clear();
         this.movementTickBuffs.clear();
         this.humanDrawBuffs.clear();
      }

      this.updateBuffs();
   }

   public boolean hasBuff(Buff var1) {
      return this.hasBuff(var1.getID());
   }

   public boolean hasBuff(int var1) {
      synchronized(this.buffs) {
         return this.buffs.containsKey(var1);
      }
   }

   public boolean hasBuff(String var1) {
      return this.hasBuff(BuffRegistry.getBuffID(var1));
   }

   public int getBuffDurationLeft(int var1) {
      synchronized(this.buffs) {
         ActiveBuff var3 = (ActiveBuff)this.buffs.get(var1);
         return var3 != null ? var3.getDurationLeft() : 0;
      }
   }

   public int getBuffDurationLeft(String var1) {
      return this.getBuffDurationLeft(BuffRegistry.getBuffID(var1));
   }

   public int getBuffDurationLeft(Buff var1) {
      return this.getBuffDurationLeft(var1.getID());
   }

   public float getBuffDurationLeftSeconds(int var1) {
      return (float)this.getBuffDurationLeft(var1) / 1000.0F;
   }

   public float getBuffDurationLeftSeconds(String var1) {
      return (float)this.getBuffDurationLeft(var1) / 1000.0F;
   }

   public float getBuffDurationLeftSeconds(Buff var1) {
      return (float)this.getBuffDurationLeft(var1) / 1000.0F;
   }

   public int getStacks(Buff var1) {
      synchronized(this.buffs) {
         ActiveBuff var3 = (ActiveBuff)this.buffs.get(var1.getID());
         return var3 != null ? var3.getStacks() : 0;
      }
   }

   public void removeBuff(Buff var1, boolean var2) {
      this.removeBuff(var1.getID(), var2);
   }

   public void removeBuff(int var1, boolean var2) {
      ActiveBuff var3 = this.getBuff(var1);
      if (var3 != null) {
         if (var2) {
            if (this.owner.isServer()) {
               this.owner.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobBuffRemove(this.owner.getRealUniqueID(), var1), this.owner);
            } else if (this.owner.isClient()) {
               this.owner.getLevel().getClient().network.sendPacket(new PacketMobBuffRemove(this.owner.getRealUniqueID(), var1));
            }
         }

         synchronized(this.buffs) {
            var3.buff.onRemoved(var3);
            this.buffs.remove(var1);
            this.eventSubscriptions.removeSubscriptions(var1);
            this.movementTickBuffs.remove(var1);
            this.humanDrawBuffs.remove(var1);
            this.updateBuffs();
         }
      }

   }

   public void removeBuff(String var1, boolean var2) {
      this.removeBuff(BuffRegistry.getBuffID(var1), var2);
   }

   public Iterable<Attacker> getAttackers() {
      return this.attackers;
   }

   public Stream<Attacker> streamAttackers() {
      return this.attackers.stream();
   }

   public void setupContentPacket(PacketWriter var1) {
      synchronized(this.buffs) {
         ArrayList var3 = (ArrayList)this.buffs.values().stream().filter((var0) -> {
            return var0.buff.shouldNetworkSync();
         }).collect(Collectors.toCollection(ArrayList::new));
         var1.putNextShortUnsigned(var3.size());
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ActiveBuff var5 = (ActiveBuff)var4.next();
            var5.setupContentPacket(var1);
         }

      }
   }

   public void applyContentPacket(PacketReader var1) {
      this.clearBuffs();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         ActiveBuff var4 = ActiveBuff.fromPacketIterator(var1, this.owner);
         this.addBuff(var4, false, true);
      }

      this.updateBuffs();
   }

   public void addSaveData(SaveData var1) {
      synchronized(this.buffs) {
         Iterator var3 = this.getBuffs().values().iterator();

         while(var3.hasNext()) {
            ActiveBuff var4 = (ActiveBuff)var3.next();
            if (var4.buff.shouldSave()) {
               SaveData var5 = new SaveData("");
               var4.addSaveData(var5);
               var1.addSaveData(var5);
            }
         }

      }
   }

   public void applyLoadData(LoadData var1) {
      this.clearBuffs();
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            ActiveBuff var4 = ActiveBuff.fromLoadData(var3, this.owner);
            if (var4 != null) {
               this.addBuff(var4, false, true, false);
            }
         } catch (Exception var5) {
            System.err.println("Could not load buff");
         }
      }

      this.updateBuffs();
   }
}
