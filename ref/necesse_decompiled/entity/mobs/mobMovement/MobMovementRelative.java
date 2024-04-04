package necesse.entity.mobs.mobMovement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class MobMovementRelative extends MobMovement {
   public int targetUniqueID;
   public float relativeX;
   public float relativeY;
   public boolean imitateDir;
   public boolean stopWhenColliding;
   protected MobMovementLevelPos mover;
   protected Mob targetMob;
   private long requestTargetMobTime;

   public MobMovementRelative() {
      this.mover = new MobMovementLevelPos();
   }

   public MobMovementRelative(Mob var1, float var2, float var3, boolean var4, boolean var5) {
      this();
      this.targetMob = var1;
      this.targetUniqueID = var1.getUniqueID();
      this.relativeX = var2;
      this.relativeY = var3;
      this.imitateDir = var4;
      this.stopWhenColliding = var5;
   }

   public MobMovementRelative(Mob var1, float var2, float var3) {
      this(var1, var2, var3, false, false);
   }

   public MobMovementRelative(Mob var1, boolean var2) {
      this(var1, 0.0F, 0.0F, false, var2);
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextInt(this.targetUniqueID);
      var2.putNextFloat(this.relativeX);
      var2.putNextFloat(this.relativeY);
      var2.putNextBoolean(this.imitateDir);
      var2.putNextBoolean(this.stopWhenColliding);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.targetUniqueID = var2.getNextInt();
      this.relativeX = var2.getNextFloat();
      this.relativeY = var2.getNextFloat();
      this.imitateDir = var2.getNextBoolean();
      this.stopWhenColliding = var2.getNextBoolean();
   }

   public boolean tick(Mob var1) {
      if (this.targetMob == null || this.targetMob.getUniqueID() != this.targetUniqueID) {
         this.targetMob = GameUtils.getLevelMob(this.targetUniqueID, var1.getLevel());
         if (this.targetMob == null && var1.isClient() && this.requestTargetMobTime < var1.getWorldEntity().getTime()) {
            var1.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.targetUniqueID));
            this.requestTargetMobTime = var1.getWorldEntity().getTime() + 1000L;
         }
      }

      if (this.targetMob != null) {
         this.mover.levelX = this.targetMob.x + this.relativeX;
         this.mover.levelY = this.targetMob.y + this.relativeY;
         if (this.stopWhenColliding && this.targetMob.getCollision().intersects(var1.getCollision())) {
            if (this.imitateDir && this.targetMob.dx == 0.0F && this.targetMob.dy == 0.0F && !var1.isAttacking) {
               var1.dir = this.targetMob.dir;
            }

            return true;
         } else if (this.mover.tick(var1)) {
            if (this.imitateDir && this.targetMob.dx == 0.0F && this.targetMob.dy == 0.0F && !var1.isAttacking) {
               var1.dir = this.targetMob.dir;
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementRelative)) {
         return false;
      } else {
         MobMovementRelative var2 = (MobMovementRelative)var1;
         return this.targetUniqueID == var2.targetUniqueID && this.relativeX == var2.relativeX && this.relativeY == var2.relativeY;
      }
   }

   public String toString() {
      String var1 = this.targetMob == null ? "" + this.targetUniqueID : this.targetMob.getStringID() + "@" + this.targetUniqueID;
      return "MobMovementRelative@" + Integer.toHexString(this.hashCode()) + "[" + var1 + ", " + GameMath.toDecimals(this.relativeX, 2) + ", " + GameMath.toDecimals(this.relativeY, 2) + "]";
   }
}
