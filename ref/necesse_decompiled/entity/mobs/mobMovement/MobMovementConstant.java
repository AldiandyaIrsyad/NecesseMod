package necesse.entity.mobs.mobMovement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;

public class MobMovementConstant extends MobMovement {
   private float moveX;
   private float moveY;

   public MobMovementConstant() {
   }

   public MobMovementConstant(float var1, float var2) {
      this();
      this.moveX = var1;
      this.moveY = var2;
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextFloat(this.moveX);
      var2.putNextFloat(this.moveY);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.moveX = var2.getNextFloat();
      this.moveY = var2.getNextFloat();
   }

   public boolean tick(Mob var1) {
      var1.moveX = this.moveX;
      var1.moveY = this.moveY;
      return false;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementConstant)) {
         return false;
      } else {
         MobMovementConstant var2 = (MobMovementConstant)var1;
         return this.moveX == var2.moveX && this.moveY == var2.moveY;
      }
   }
}
