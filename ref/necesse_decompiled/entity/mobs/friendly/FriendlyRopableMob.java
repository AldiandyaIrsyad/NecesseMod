package necesse.entity.mobs.friendly;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.RopeTrail;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.RopeItem;
import necesse.level.maps.Level;

public class FriendlyRopableMob extends FriendlyMob {
   private RopeTrail ropeTrail;
   private final LevelMob<Mob> ropeMob = new LevelMob<Mob>() {
      public void onMobChanged(Mob var1, Mob var2) {
         FriendlyRopableMob.this.respawnRopeTrail(var2);
      }

      public Level onMobChangedLevel(Mob var1, Level var2) {
         if (var2.isServer()) {
            var2.entityManager.changeMobLevel(FriendlyRopableMob.this, var1.getLevel(), var1.getX(), var1.getY(), true);
            FriendlyRopableMob.this.respawnRopeTrail(var1);
            return var1.getLevel();
         } else {
            return super.onMobChangedLevel(var1, var2);
         }
      }
   };
   private InventoryItem ropeItem;

   public FriendlyRopableMob(int var1) {
      super(var1);
   }

   protected int getDragRange() {
      return 100;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("ropeMobUniqueID", this.ropeMob.uniqueID);
      if (this.ropeItem != null) {
         SaveData var2 = new SaveData("ropeItem");
         this.ropeItem.addSaveData(var2);
         var1.addSaveData(var2);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.ropeMob.uniqueID = var1.getInt("ropeMobUniqueID", -1);
      this.respawnRopeTrail(this.getRopeMob());
      LoadData var2 = var1.getFirstLoadDataByName("ropeItem");
      if (var2 != null) {
         this.ropeItem = InventoryItem.fromLoadData(var2);
      }

   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextInt(this.ropeMob.uniqueID);
      InventoryItem.addPacketContent(this.ropeItem, var1);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.ropeMob.uniqueID = var1.getNextInt();
      this.ropeMob.get(this.getLevel());
      this.ropeItem = InventoryItem.fromContentPacket(var1);
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      Mob var2 = this.ropeMob.get(this.getLevel());
      if (var2 != null) {
         float var3 = this.getDistance(var2);
         if (var3 > (float)this.getDragRange()) {
            float var4 = (float)(var2.getX() - this.getX());
            float var5 = (float)(var2.getY() - this.getY());
            Point2D.Float var6 = GameMath.normalize(var4, var5);
            this.dx = var6.x * var3 / 2.0F;
            this.dy = var6.y * var3 / 2.0F;
            this.setFacingDir(var4, var5);
         }
      } else if (this.ropeMob.uniqueID != -1) {
         this.ropeMob.uniqueID = -1;
         if (this.isServer() && this.ropeItem != null) {
            boolean var7 = true;
            if (this.ropeItem.item instanceof RopeItem) {
               var7 = ((RopeItem)this.ropeItem.item).consumesRope();
            }

            if (var7) {
               this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), (float)this.getX(), (float)this.getY()));
            }
         }

         this.ropeItem = null;
      }

      if (this.ropeTrail != null) {
         this.ropeTrail.update(20.0F, 20.0F);
      }

   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.CANNOT_PASS_DOORS_OPTIONS : null;
   }

   public void clientTick() {
      super.clientTick();
   }

   public void serverTick() {
      super.serverTick();
      Mob var1 = this.ropeMob.get(this.getLevel());
      if (var1 == null && this.ropeItem != null) {
         boolean var2 = true;
         if (this.ropeItem.item instanceof RopeItem) {
            var2 = ((RopeItem)this.ropeItem.item).consumesRope();
         }

         if (var2) {
            this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), (float)this.getX(), (float)this.getY()));
         }

         this.ropeItem = null;
      }

   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (this.ropeMob.uniqueID != -1 && this.ropeMob.uniqueID == var1.getUniqueID()) {
         this.ropeMob.uniqueID = -1;
         this.respawnRopeTrail(this.getRopeMob());
         if (this.ropeItem != null && this.isServer()) {
            boolean var2 = true;
            if (this.ropeItem.item instanceof RopeItem) {
               var2 = ((RopeItem)this.ropeItem.item).consumesRope();
            }

            if (var2) {
               this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), (float)this.getX(), (float)this.getY()));
            }
         }

         this.ropeItem = null;
      }

   }

   public boolean canInteract(Mob var1) {
      return this.ropeMob.uniqueID != -1 && this.ropeMob.uniqueID == var1.getUniqueID();
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return Localization.translate("controls", "removeropetip");
   }

   public boolean canRope(int var1, InventoryItem var2) {
      return var2.item instanceof RopeItem && !this.isRoped();
   }

   public InventoryItem onRope(int var1, InventoryItem var2) {
      boolean var3 = true;
      if (var2.item instanceof RopeItem) {
         var3 = ((RopeItem)var2.item).consumesRope();
      }

      this.startRope(var1, var2.copy(1));
      if (var3) {
         var2.setAmount(var2.getAmount() - 1);
      }

      return var2;
   }

   public void startRope(int var1, InventoryItem var2) {
      this.ropeMob.uniqueID = var1;
      this.ropeItem = var2;
      this.sendMovementPacket(false);
      this.respawnRopeTrail(this.getRopeMob());
   }

   public void respawnRopeTrail(Mob var1) {
      if (this.ropeTrail != null) {
         this.ropeTrail.remove();
      }

      this.ropeTrail = null;
      if (var1 != null) {
         this.getLevel().entityManager.addTrail(this.ropeTrail = this.getNewRopeTrail(var1, this.ropeItem));
      }

   }

   protected RopeTrail getNewRopeTrail(Mob var1, InventoryItem var2) {
      return var2 != null && var2.item instanceof RopeItem ? new RopeTrail(this.getLevel(), this, var1, 20.0F, 20.0F, ((RopeItem)var2.item).getRopeColor()) : new RopeTrail(this.getLevel(), this, var1, 20.0F, 20.0F);
   }

   protected Mob getRopeMob() {
      return this.ropeMob.get(this.getLevel());
   }

   protected boolean isBeingDragged() {
      Mob var1 = this.getRopeMob();
      return var1 != null && this.getDistance(var1) > (float)this.getDragRange();
   }

   public boolean isRoped() {
      return this.ropeMob.uniqueID != -1;
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      if (this.ropeTrail != null) {
         this.ropeTrail.remove();
      }

      this.ropeTrail = null;
      if (this.ropeItem != null && this.isServer()) {
         boolean var5 = true;
         if (this.ropeItem.item instanceof RopeItem) {
            var5 = ((RopeItem)this.ropeItem.item).consumesRope();
         }

         if (var5) {
            this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), (float)this.getX(), (float)this.getY()));
         }
      }

   }
}
