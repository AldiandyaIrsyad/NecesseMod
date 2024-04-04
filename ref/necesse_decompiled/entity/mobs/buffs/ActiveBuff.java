package necesse.entity.mobs.buffs;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.MobWasKilledEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class ActiveBuff extends ModifierContainer {
   public final Buff buff;
   public final Mob owner;
   private int stacks;
   private LinkedList<BuffTime> stackTimes;
   private Attacker source;
   private GNDItemMap gndData;
   private boolean isRemoved;
   protected float dotBuffer;

   public ActiveBuff(Buff var1, Mob var2, int var3, Attacker var4) {
      super(BuffModifiers.LIST);
      if (var1.getID() == -1) {
         throw new NullPointerException("Buff \"" + var1.getClass().getSimpleName() + "\" has an invalid id.");
      } else {
         this.buff = var1;
         this.owner = var2;
         this.stacks = 1;
         this.stackTimes = new LinkedList(Collections.singleton(new BuffTime(var3, this.getTime())));
         this.source = var1.getSource(var4);
         this.gndData = new GNDItemMap();
      }
   }

   public ActiveBuff(int var1, Mob var2, int var3, Attacker var4) {
      this(BuffRegistry.getBuff(var1), var2, var3, var4);
   }

   public ActiveBuff(String var1, Mob var2, int var3, Attacker var4) {
      this(BuffRegistry.getBuff(BuffRegistry.getBuffID(var1)), var2, var3, var4);
   }

   public ActiveBuff(Buff var1, Mob var2, float var3, Attacker var4) {
      this(var1, var2, (int)(var3 * 1000.0F), var4);
   }

   public ActiveBuff(int var1, Mob var2, float var3, Attacker var4) {
      this(BuffRegistry.getBuff(var1), var2, var3, var4);
   }

   public ActiveBuff(String var1, Mob var2, float var3, Attacker var4) {
      this(BuffRegistry.getBuff(var1), var2, var3, var4);
   }

   public void init(BuffEventSubscriber var1) {
      this.buff.init(this, var1);
      this.buff.init(this);
   }

   public void addStack(int var1, Attacker var2) {
      this.stack(new ActiveBuff(this.buff, this.owner, var1, var2));
   }

   public void stack(ActiveBuff var1) {
      int var2 = this.buff.getStackSize();
      BuffTime var4;
      BuffTime var5;
      if (var2 > 1 && !this.buff.overridesStackDuration()) {
         Iterator var6 = var1.stackTimes.iterator();

         while(var6.hasNext()) {
            var4 = (BuffTime)var6.next();
            if (this.stacks >= var2) {
               var5 = (BuffTime)this.stackTimes.getFirst();
               if (var5.getDurationLeft() < var4.getDurationLeft()) {
                  this.stackTimes.removeFirst();
                  this.addTimeOrdered(var4);
               }
            } else {
               this.addTimeOrdered(var4);
               ++this.stacks;
               this.buff.onStacksUpdated(this);
            }
         }
      } else {
         BuffTime var3 = (BuffTime)this.stackTimes.removeLast();
         var4 = (BuffTime)var1.stackTimes.getLast();
         var5 = var3.getDurationLeft() < var4.getDurationLeft() ? var4 : var3;
         this.stackTimes.add(var5);
         this.stacks = Math.min(var2, this.stacks + var1.stacks);
         this.buff.onStacksUpdated(this);
      }

      if (this.source == null) {
         this.source = var1.source;
      }

   }

   private void addTimeOrdered(BuffTime var1) {
      ListIterator var2 = this.stackTimes.listIterator();
      int var3 = var1.getDurationLeft();

      BuffTime var4;
      do {
         if (!var2.hasNext()) {
            this.stackTimes.addLast(var1);
            return;
         }

         var4 = (BuffTime)var2.next();
      } while(var3 >= var4.getDurationLeft());

      if (var2.previous() != null) {
         var2.add(var1);
      } else {
         this.stackTimes.addFirst(var1);
      }

   }

   public boolean tickExpired() {
      if (this.buff.isPassive()) {
         return false;
      } else {
         boolean var1 = false;
         if (this.stackTimes.isEmpty()) {
            this.remove();
            return true;
         } else {
            while(!this.stackTimes.isEmpty()) {
               BuffTime var2 = (BuffTime)this.stackTimes.getFirst();
               if (var2.getModifiedDurationLeft() > 0) {
                  break;
               }

               var1 = true;
               if (this.stacks > 1) {
                  --this.stacks;
               }

               if (this.stackTimes.size() <= 1) {
                  this.remove();
                  break;
               }

               this.stackTimes.removeFirst();
            }

            return var1;
         }
      }
   }

   public void onBeforeHit(MobBeforeHitEvent var1) {
      this.buff.onBeforeHit(this, var1);
   }

   public void onBeforeAttacked(MobBeforeHitEvent var1) {
      this.buff.onBeforeAttacked(this, var1);
   }

   public void onBeforeHitCalculated(MobBeforeHitCalculatedEvent var1) {
      this.buff.onBeforeHitCalculated(this, var1);
   }

   public void onBeforeAttackedCalculated(MobBeforeHitCalculatedEvent var1) {
      this.buff.onBeforeAttackedCalculated(this, var1);
   }

   public void onWasHit(MobWasHitEvent var1) {
      this.buff.onWasHit(this, var1);
   }

   public void onHasAttacked(MobWasHitEvent var1) {
      this.buff.onHasAttacked(this, var1);
   }

   public void onItemAttacked(int var1, int var2, PlayerMob var3, int var4, InventoryItem var5, PlayerInventorySlot var6, int var7) {
      this.buff.onItemAttacked(this, var1, var2, var3, var4, var5, var6, var7);
   }

   public void onHasKilledTarget(MobWasKilledEvent var1) {
      this.buff.onHasKilledTarget(this, var1);
   }

   public void serverTick() {
      this.buff.serverTick(this);
   }

   public void clientTick() {
      this.buff.clientTick(this);
   }

   public void drawIcon(int var1, int var2) {
      this.buff.drawIcon(var1, var2, this);
   }

   public boolean isVisible() {
      return this.buff.isVisible(this);
   }

   public boolean canCancel() {
      return this.buff.canCancel(this);
   }

   public boolean shouldDrawDuration() {
      return this.buff.shouldDrawDuration(this);
   }

   public boolean isExpired() {
      if (this.buff.isPassive()) {
         return false;
      } else {
         return this.getDurationLeft() < 0;
      }
   }

   public ListGameTooltips getTooltips(GameBlackboard var1) {
      return this.buff.getTooltip(this, var1);
   }

   public int getStacks() {
      return this.stacks;
   }

   public void setDurationLeftSeconds(float var1) {
      this.setDurationLeft((int)(var1 * 1000.0F));
   }

   public void setDurationLeft(int var1) {
      BuffTime var2 = (BuffTime)this.stackTimes.getLast();
      var2.startTime = this.getTime();
      var2.duration = var1;
   }

   public int getDuration() {
      return ((BuffTime)this.stackTimes.getLast()).duration;
   }

   public int getDurationLeft() {
      return this.buff.isPassive() ? 0 : ((BuffTime)this.stackTimes.getLast()).getDurationLeft();
   }

   public int getModifiedDurationLeft() {
      return this.buff.isPassive() ? 0 : ((BuffTime)this.stackTimes.getLast()).getModifiedDurationLeft();
   }

   public float getDurationModifier() {
      return this.owner.buffManager.getBuffDurationModifier(this);
   }

   public String getDurationText() {
      return this.buff.showsFirstStackDurationText() ? convertSecondsToText((float)((BuffTime)this.stackTimes.getFirst()).getModifiedDurationLeft() / 1000.0F) : convertSecondsToText((float)((BuffTime)this.stackTimes.getLast()).getModifiedDurationLeft() / 1000.0F);
   }

   public LinkedList<BuffTime> getStackTimes() {
      return this.stackTimes;
   }

   protected long getTime() {
      return this.owner.getWorldEntity() == null ? 0L : this.owner.getWorldEntity().getTime();
   }

   public static String convertSecondsToText(float var0) {
      String var1;
      if (var0 > 60.0F) {
         int var2 = (int)(var0 / 60.0F);
         int var3 = (int)var0 % 60;
         if (var2 > 60) {
            int var4 = var2 / 60;
            int var5 = var2 % 60;
            if (var4 >= 1000) {
               var1 = "LONG";
            } else if (var4 > 10) {
               var1 = var4 + "h";
            } else if (var5 < 10) {
               var1 = var4 + "h0" + var5;
            } else {
               var1 = var4 + "h" + var5;
            }
         } else if (var2 > 10) {
            var1 = var2 + "m";
         } else if (var3 < 10) {
            var1 = var2 + ":0" + var3;
         } else {
            var1 = var2 + ":" + var3;
         }
      } else if (var0 > 10.0F) {
         var1 = (int)var0 + "s";
      } else {
         var1 = (float)((int)(var0 * 10.0F)) / 10.0F + "s";
      }

      return var1;
   }

   public void remove() {
      this.isRemoved = true;
   }

   public boolean isRemoved() {
      return this.isRemoved;
   }

   public Attacker getAttacker() {
      return this.source;
   }

   public GNDItemMap getGndData() {
      return this.gndData;
   }

   public <T> void setModifier(Modifier<T> var1, T var2) {
      super.setModifier(var1, var2);
      this.forceManagerUpdate();
   }

   public <T> void addModifier(Modifier<T> var1, T var2, int var3) {
      super.addModifier(var1, var2, var3);
      this.forceManagerUpdate();
   }

   public void onUpdate() {
      this.buff.onUpdate(this);
   }

   public void forceManagerUpdate() {
      this.owner.buffManager.updateBuffs();
   }

   public void addDebugTooltips(ListGameTooltips var1) {
      StringBuilder var2 = new StringBuilder();
      if (this.stacks > 1) {
         var2.append(", ").append(this.stacks).append(" stacks");
      }

      if (!this.buff.isPassive()) {
         Iterator var3 = this.stackTimes.iterator();

         while(var3.hasNext()) {
            BuffTime var4 = (BuffTime)var3.next();
            var2.append(", ").append(convertSecondsToText((float)var4.getDurationLeft() / 1000.0F));
         }
      } else {
         var2.append(", passive");
      }

      var1.add(this.buff.getDisplayName() + var2.toString());
   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.buff.getID());
      var1.putNextShortUnsigned(this.stacks);
      if (!this.buff.isPassive()) {
         Iterator var2 = this.stackTimes.iterator();

         while(var2.hasNext()) {
            BuffTime var3 = (BuffTime)var2.next();
            var1.putNextInt(var3.getDurationLeft());
         }
      }

      var1.putNextContentPacket(this.gndData.getContentPacket());
   }

   public Packet getContentPacket() {
      Packet var1 = new Packet();
      this.setupContentPacket(new PacketWriter(var1));
      return var1;
   }

   private BuffTime createBuffTime(int var1) {
      return new BuffTime(var1, this.getTime());
   }

   public static ActiveBuff fromPacketIterator(PacketReader var0, Mob var1) {
      int var2 = var0.getNextShortUnsigned();
      Buff var3 = BuffRegistry.getBuff(var2);
      ActiveBuff var4 = new ActiveBuff(var3, var1, 1000, (Attacker)null);
      var4.stacks = var0.getNextShortUnsigned();
      int var5 = var3.overridesStackDuration() ? 1 : var4.stacks;
      var4.stackTimes = new LinkedList();

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var3.isPassive() ? 0 : var0.getNextInt();
         var4.stackTimes.addLast(var4.createBuffTime(var7));
      }

      var4.gndData = new GNDItemMap(var0.getNextContentPacket());
      return var4;
   }

   public static ActiveBuff fromContentPacket(Packet var0, Mob var1) {
      return fromPacketIterator(new PacketReader(var0), var1);
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("stringID", this.buff.getStringID());
      if (this.stacks <= 1) {
         var1.addInt("duration", this.getDurationLeft());
      } else {
         var1.addInt("stacks", this.stacks);
         int var2 = 0;

         for(Iterator var3 = this.stackTimes.iterator(); var3.hasNext(); ++var2) {
            BuffTime var4 = (BuffTime)var3.next();
            var1.addInt("stack" + var2, var4.getDurationLeft());
         }
      }

      if (this.gndData.getMapSize() > 0) {
         SaveData var5 = new SaveData("GNDData");
         this.gndData.addSaveData(var5);
         var1.addSaveData(var5);
      }

   }

   public static ActiveBuff fromLoadData(LoadData var0, Mob var1) {
      String var2 = var0.getFirstDataByName("stringID");
      Buff var3 = BuffRegistry.getBuff(var2);
      if (var3 == null) {
         return null;
      } else {
         ActiveBuff var4 = new ActiveBuff(var3, var1, 1000, (Attacker)null);
         var4.stacks = var0.getInt("stacks", 1, false);
         var4.stackTimes = new LinkedList();
         int var5;
         if (var4.stacks > 1) {
            var5 = var3.overridesStackDuration() ? 1 : var4.stacks;

            for(int var6 = 0; var6 < var5; ++var6) {
               int var7 = var0.getInt("stack" + var6, 0);
               var4.addTimeOrdered(var4.createBuffTime(var7));
            }
         } else {
            var5 = var0.getInt("duration", 0, false);
            var4.addTimeOrdered(var4.createBuffTime(var5));
         }

         LoadData var8 = var0.getFirstLoadDataByName("GNDData");
         if (var8 != null) {
            var4.gndData = new GNDItemMap(var8);
         }

         return var4;
      }
   }

   public class BuffTime {
      public int duration;
      public long startTime;

      private BuffTime(int var2, long var3) {
         this.duration = var2;
         this.startTime = var3;
      }

      public int getDurationLeft() {
         return (int)((long)this.duration + this.startTime - ActiveBuff.this.getTime());
      }

      public int getModifiedDuration() {
         return (int)((float)this.duration * ActiveBuff.this.getDurationModifier());
      }

      public int getModifiedDurationLeft() {
         return (int)((long)this.getModifiedDuration() + this.startTime - ActiveBuff.this.getTime());
      }

      // $FF: synthetic method
      BuffTime(int var2, long var3, Object var5) {
         this(var2, var3);
      }
   }
}
