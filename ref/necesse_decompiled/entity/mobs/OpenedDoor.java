package necesse.entity.mobs;

import java.awt.Shape;
import java.util.Objects;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CustomTilePosition;
import necesse.level.maps.Level;

public class OpenedDoor {
   public final int tileX;
   public final int tileY;
   public int mobX;
   public int mobY;
   public final boolean isSwitched;

   public OpenedDoor(int var1, int var2, int var3, int var4, boolean var5) {
      this.tileX = var1;
      this.tileY = var2;
      this.mobX = var3;
      this.mobY = var4;
      this.isSwitched = var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         OpenedDoor var2 = (OpenedDoor)var1;
         return this.tileX == var2.tileX && this.tileY == var2.tileY && this.mobX == var2.mobX && this.mobY == var2.mobY && this.isSwitched == var2.isSwitched;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.tileX, this.tileY, this.mobX, this.mobY, this.isSwitched});
   }

   public boolean isValid(Level var1) {
      GameObject var2 = var1.getObject(this.tileX, this.tileY);
      return var2.isDoor && var2.isSwitched != this.isSwitched;
   }

   public boolean switchedDoorCollides(Level var1, Shape var2, CollisionFilter var3) {
      if (var3 == null) {
         return false;
      } else {
         GameObject var4 = var1.getObject(this.tileX, this.tileY);
         return var3.check(var2, new CustomTilePosition(var1, this.tileX, this.tileY, var1.getTileID(this.tileX, this.tileY), ((SwitchObject)var4).counterID, var1.getObjectRotation(this.tileX, this.tileY)));
      }
   }

   public boolean entityCollidesWithSwitchedDoor(Level var1) {
      return var1.entityManager.mobs.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, 4).filter((var0) -> {
         return !var0.removed() && var0.canLevelInteract();
      }).anyMatch((var2) -> {
         return this.switchedDoorCollides(var1, var2.getCollision(), var2.getLevelCollisionFilter());
      });
   }

   public boolean clientCollidesWithSwitchedDoor(Level var1) {
      return var1.entityManager.players.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, 4).filter((var0) -> {
         return !var0.removed() && var0.canLevelInteract();
      }).anyMatch((var2) -> {
         return this.switchedDoorCollides(var1, var2.getCollision(), var2.getLevelCollisionFilter());
      });
   }

   public void switchDoor(Level var1) {
      GameObject var2 = var1.getObject(this.tileX, this.tileY);
      if (var2.isDoor && var2.isSwitched != this.isSwitched) {
         ((SwitchObject)var2).onSwitched(var1, this.tileX, this.tileY);
      }

   }
}
