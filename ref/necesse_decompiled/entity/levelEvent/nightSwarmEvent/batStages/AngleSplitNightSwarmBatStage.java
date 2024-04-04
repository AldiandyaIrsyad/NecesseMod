package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class AngleSplitNightSwarmBatStage extends NightSwarmBatStage {
   public float midX;
   public float midY;
   public int angleSegments;
   public float angleOffset;
   public int range;

   public AngleSplitNightSwarmBatStage(float var1, float var2, int var3, float var4, int var5) {
      super(false);
      this.midX = var1;
      this.midY = var2;
      this.angleSegments = var3;
      this.angleOffset = var4;
      this.range = var5;
   }

   public void onStarted(NightSwarmBatMob var1) {
      float var2 = 360.0F / (float)this.angleSegments;
      Point2D.Float var3 = GameMath.normalize(var1.x - this.midX, var1.y - this.midY);
      float var4 = GameMath.getAngle(var3);
      var4 = GameMath.fixAngle(var4 + var2 / 2.0F + this.angleOffset);
      int var5 = (int)(var4 / var2);
      float var6 = GameMath.fixAngle((float)var5 * var2 - this.angleOffset);
      Point2D.Float var7 = GameMath.getAngleDir(var6);
      var1.setMovement(new MobMovementLevelPos(this.midX + var7.x * (float)this.range, this.midY + var7.y * (float)this.range));
      var1.idleXPos = this.midX + var7.x * (float)this.range;
      var1.idleYPos = this.midY + var7.y * (float)this.range;
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return var1.hasArrivedAtTarget() || !var1.hasCurrentMovement();
   }

   public void onCompleted(NightSwarmBatMob var1) {
      var1.setMovement((MobMovement)null);
   }
}
