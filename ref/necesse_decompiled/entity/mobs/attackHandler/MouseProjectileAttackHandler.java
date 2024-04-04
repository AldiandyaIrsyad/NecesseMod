package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public class MouseProjectileAttackHandler extends MousePositionAttackHandler {
   private ArrayList<FollowingProjectile> projectiles;
   private int travelDistanceAfter;
   private int travelDistanceDuring;
   private float currentAimX;
   private float currentAimY;

   public MouseProjectileAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3, int var4, FollowingProjectile... var5) {
      super(var1, var2, 50);
      if (var5.length == 0) {
         throw new IllegalArgumentException("Must give at least one projectile");
      } else {
         Objects.requireNonNull(var5);
         FollowingProjectile[] var6 = var5;
         int var7 = var5.length;

         int var8;
         FollowingProjectile var9;
         for(var8 = 0; var8 < var7; ++var8) {
            var9 = var6[var8];
            Objects.requireNonNull(var9);
         }

         this.projectiles = new ArrayList(Arrays.asList(var5));
         this.travelDistanceAfter = var4;
         this.travelDistanceDuring = var3;
         var6 = var5;
         var7 = var5.length;

         for(var8 = 0; var8 < var7; ++var8) {
            var9 = var6[var8];
            if (var3 >= 0) {
               var9.setDistance(var3);
            } else {
               var9.setDistance(10000);
            }

            var9.traveledDistance = 0.0F;
         }

         FollowingProjectile var10 = var5[0];
         this.currentAimX = (float)var10.getX() + var10.dx * 64.0F;
         this.currentAimY = (float)var10.getY() + var10.dy * 64.0F;
      }
   }

   public MouseProjectileAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3, FollowingProjectile... var4) {
      this(var1, var2, -1, var3, var4);
   }

   public void addProjectiles(FollowingProjectile... var1) {
      this.projectiles.ensureCapacity(this.projectiles.size() + var1.length);
      FollowingProjectile[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FollowingProjectile var5 = var2[var4];
         this.projectiles.add(var5);
         if (this.travelDistanceDuring >= 0) {
            var5.setDistance(this.travelDistanceDuring);
         } else {
            var5.setDistance(10000);
         }

         var5.traveledDistance = 0.0F;
         var5.targetPos = new Point(this.lastX, this.lastY);
         var5.target = null;
      }

   }

   public int getCurrentProjectilesCount() {
      return this.projectiles.size();
   }

   public Point getNextLevelPos(GameCamera var1) {
      float var2 = 0.0F;
      if (!this.projectiles.isEmpty()) {
         var2 = ((FollowingProjectile)this.projectiles.get(0)).getHeight();
      }

      if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
         return new Point((int)this.currentAimX, (int)(this.currentAimY + var2));
      } else {
         Point var3 = super.getNextLevelPos(var1);
         return new Point(var3.x, var3.y + (int)var2);
      }
   }

   public void onPacketUpdate(PacketReader var1) {
      super.onPacketUpdate(var1);

      FollowingProjectile var3;
      for(Iterator var2 = this.projectiles.iterator(); var2.hasNext(); var3.target = null) {
         var3 = (FollowingProjectile)var2.next();
         var3.targetPos = new Point(this.lastX, this.lastY);
      }

      this.sendTargetUpdate();
   }

   public void onUpdate() {
      super.onUpdate();
      FollowingProjectile var2;
      if (this.player.isClient() && Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
         float var1 = 0.0F;
         if (!this.projectiles.isEmpty()) {
            var2 = (FollowingProjectile)this.projectiles.get(0);
            var1 = Math.max(var2.speed, var2.getOriginalSpeed());
         }

         float var4 = var1 * (float)this.updateInterval / 250.0F;
         this.currentAimX += ControllerInput.getAimX() * var4;
         this.currentAimY += ControllerInput.getAimY() * var4;
      }

      ListIterator var3 = this.projectiles.listIterator();

      while(var3.hasNext()) {
         var2 = (FollowingProjectile)var3.next();
         if (var2.removed()) {
            var3.remove();
         } else if (this.travelDistanceDuring < 0) {
            var2.traveledDistance = 0.0F;
         }
      }

      if (this.projectiles.isEmpty()) {
         this.player.endAttackHandler(false);
      }

   }

   public void onEndAttack(boolean var1) {
      boolean var2 = false;
      Iterator var3 = this.projectiles.iterator();

      while(var3.hasNext()) {
         FollowingProjectile var4 = (FollowingProjectile)var3.next();
         var4.targetPos = null;
         var4.target = null;
         if (this.travelDistanceAfter >= 0 && !var4.returningToOwner()) {
            var4.setDistance(this.travelDistanceAfter);
            var4.traveledDistance = 0.0F;
         }

         if (!var4.removed()) {
            var2 = true;
         }
      }

      if (var2) {
         this.sendTargetUpdate();
      }

   }

   protected void sendTargetUpdate() {
      Iterator var1 = this.projectiles.iterator();

      while(var1.hasNext()) {
         FollowingProjectile var2 = (FollowingProjectile)var1.next();
         if (var2.handlingClient != null) {
            if (this.player.isClient()) {
               var2.sendClientTargetUpdate();
            }
         } else if (this.player.isServer()) {
            var2.sendServerTargetUpdate();
         }
      }

   }

   public boolean canRunAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PlayerInventorySlot var6) {
      return this.isFrom(var5, var6);
   }

   public void drawControllerAimPos(GameCamera var1, Level var2, PlayerMob var3, InventoryItem var4) {
   }
}
