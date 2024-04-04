package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketMobAbilityLevelEventHit;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.regionSystem.RegionPosition;

public class MobAbilityLevelEvent extends LevelEvent implements Attacker {
   public Mob owner;
   public int ownerID;
   public boolean clientHandlesHit;
   public NetworkClient handlingClient;
   public boolean hitsObjects;
   private HashMap<Point, Long> objectHits;

   public MobAbilityLevelEvent() {
   }

   public MobAbilityLevelEvent(Mob var1, GameRandom var2) {
      this.owner = var1;
      if (var1 != null) {
         this.level = var1.getLevel();
         this.ownerID = var1.getUniqueID();
      } else {
         this.ownerID = -1;
      }

      this.resetUniqueID(var2);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.ownerID = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.ownerID);
   }

   public void init() {
      super.init();
      this.objectHits = new HashMap();
      this.owner = GameUtils.getLevelMob(this.ownerID, this.level);
      if (this.owner == null) {
         GameLog.warn.println("Could not find owner for level event " + this.getClass().getSimpleName() + " server level: " + this.isServer());
         this.over();
      } else {
         this.hitsObjects = this.owner.isPlayer;
         if (this.isServer()) {
            if (Settings.giveClientsPower) {
               if (this.owner.isPlayer) {
                  this.handlingClient = ((PlayerMob)this.owner).getServerClient();
               }

               this.clientHandlesHit = true;
            }
         } else if (this.isClient()) {
            ClientClient var1 = this.level.getClient().getClient();
            if (this.level.getClient().allowClientsPower() && var1 != null) {
               if (this.owner == var1.playerMob) {
                  this.handlingClient = var1;
               }

               this.clientHandlesHit = true;
            }
         }
      }

   }

   protected void handleHits(Shape var1, Predicate<Mob> var2, Function<Mob, Packet> var3) {
      if (this.isClient()) {
         if (this.handlingClient != null) {
            GameUtils.streamTargets(this.owner, var1, 1).filter((var0) -> {
               return !var0.isPlayer;
            }).filter(var2).filter((var1x) -> {
               return var1.intersects(var1x.getHitBox());
            }).forEach((var2x) -> {
               this.clientHit(var2x, var3 == null ? null : (Packet)var3.apply(var2x));
            });
         } else if (this.clientHandlesHit) {
            ClientClient var4 = this.level.getClient().getClient();
            if (var4 != null && var4.hasSpawned() && !var4.isDead()) {
               NetworkClient var5 = GameUtils.getAttackerClient(this.owner);
               if ((var5 == null || var4.pvpEnabled() && var5.pvpEnabled()) && var4.playerMob.canBeTargeted(this.owner, var5) && var2.test(var4.playerMob) && var1.intersects(var4.playerMob.getHitBox())) {
                  this.clientHit(var4.playerMob, var3 == null ? null : (Packet)var3.apply(var4.playerMob));
               }
            }
         }
      } else if (this.isServer()) {
         if (this.handlingClient == null) {
            GameUtils.streamTargets(this.owner, var1, 1).filter((var1x) -> {
               return !this.clientHandlesHit || !var1x.isPlayer;
            }).filter(var2).filter((var1x) -> {
               return var1.intersects(var1x.getHitBox());
            }).forEach((var2x) -> {
               this.serverHit(var2x, var3 == null ? null : (Packet)var3.apply(var2x), false);
            });
         }

         if (this.hitsObjects) {
            ArrayList var7 = this.level.getCollisions(var1, (new CollisionFilter()).attackThroughCollision((var1x) -> {
               return this.canHitObject(var1x.object());
            }));
            Iterator var8 = var7.iterator();

            while(var8.hasNext()) {
               LevelObjectHit var6 = (LevelObjectHit)var8.next();
               if (!var6.invalidPos() && this.canHit(var6)) {
                  this.hit(var6);
               }
            }
         }
      }

   }

   public boolean canHitObject(LevelObject var1) {
      return var1.object.attackThrough;
   }

   public boolean canHit(LevelObjectHit var1) {
      if (!this.objectHits.containsKey(var1.getPoint())) {
         return true;
      } else {
         return (Long)this.objectHits.get(var1.getPoint()) + (long)this.getHitCooldown(var1) < this.level.getWorldEntity().getTime();
      }
   }

   public int getHitCooldown(LevelObjectHit var1) {
      return 500;
   }

   public void hit(LevelObjectHit var1) {
      this.objectHits.put(var1.getPoint(), this.level.getWorldEntity().getTime());
   }

   public void clientHit(Mob var1, Packet var2) {
      this.level.getClient().network.sendPacket(new PacketMobAbilityLevelEventHit(this, var1, var2));
   }

   public void serverHit(Mob var1, Packet var2, boolean var3) {
   }

   public GameMessage getAttackerName() {
      return (GameMessage)(this.owner != null ? this.owner.getAttackerName() : new StaticMessage("MOB_ABILITY_EVENT{" + this.getStringID() + "}"));
   }

   public DeathMessageTable getDeathMessages() {
      return this.owner != null ? this.owner.getDeathMessages() : DeathMessageTable.fromRange("generic", 8);
   }

   public Mob getFirstAttackOwner() {
      return this.owner;
   }

   public Collection<RegionPosition> getRegionPositions() {
      return this.owner != null ? this.owner.getRegionPositions() : super.getRegionPositions();
   }
}
