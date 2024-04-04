package necesse.entity.mobs.mobMovement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;

public class MobMovementLevelPos extends MobMovement {
   public float levelX;
   public float levelY;
   private int stuck;
   private float oldX;
   private float oldY;

   public MobMovementLevelPos() {
   }

   public MobMovementLevelPos(float var1, float var2) {
      this();
      this.levelX = var1;
      this.levelY = var2;
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextFloat(this.levelX);
      var2.putNextFloat(this.levelY);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.levelX = var2.getNextFloat();
      this.levelY = var2.getNextFloat();
      this.tick(var1);
   }

   public boolean tick(Mob var1) {
      if (var1.getLevel().tickManager().isGameTick()) {
         if (var1.getDistance(this.oldX, this.oldY) < 2.0F) {
            this.stuck += 50;
         } else {
            this.stuck = 0;
         }
      }

      float var2 = this.stuck > 1000 ? 2.0F : (float)var1.moveAccuracy;
      boolean var3 = false;
      if (this.moveTo(var1, this.levelX, this.levelY, var2)) {
         this.stuck = 0;
         var3 = true;
      }

      if (var1.getLevel().tickManager().isGameTick()) {
         this.oldX = var1.x;
         this.oldY = var1.y;
      }

      return var3;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementLevelPos)) {
         return false;
      } else {
         MobMovementLevelPos var2 = (MobMovementLevelPos)var1;
         return this.levelX == var2.levelX && this.levelY == var2.levelY;
      }
   }
}
