package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.SetIdleNightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class MoveAroundNightSwarmEventStage extends NightSwarmEventStage {
   public int points;
   public int range;
   public int maxAngle;
   public boolean forceSpread;

   public MoveAroundNightSwarmEventStage(int var1, int var2, boolean var3, int var4) {
      this.points = var1;
      this.range = var2;
      this.forceSpread = var3;
      this.maxAngle = GameMath.limit(var4, 0, 360);
   }

   public MoveAroundNightSwarmEventStage(int var1, int var2, boolean var3) {
      this(var1, var2, var3, 360);
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      ArrayList var2 = new ArrayList();
      int var3 = this.maxAngle / this.points;
      float var4;
      float var6;
      if (this.maxAngle >= 360) {
         var4 = (float)GameRandom.globalRandom.nextInt(360);
      } else {
         Point2D.Float var5 = GameMath.normalize(var1.nextLevelX - var1.currentTarget.x, var1.nextLevelY - var1.currentTarget.y);
         var6 = GameMath.fixAngle(GameMath.getAngle(var5));
         var4 = var6 - (float)this.maxAngle / 2.0F;
      }

      float var12 = 0.0F;
      var6 = 0.0F;

      Point2D.Float var9;
      for(int var7 = 0; var7 < this.points; ++var7) {
         float var8;
         if (this.forceSpread) {
            var8 = var4 + (float)(var7 * var3) + (float)GameRandom.globalRandom.nextInt(var3);
         } else {
            var8 = var4 + (float)GameRandom.globalRandom.nextInt(this.maxAngle);
         }

         var8 = GameMath.fixAngle(var8);
         var9 = GameMath.getAngleDir(var8);
         float var10 = var1.currentTarget.x + var9.x * (float)this.range;
         float var11 = var1.currentTarget.y + var9.y * (float)this.range;
         var2.add(new Point2D.Float(var10, var11));
         var12 += var10;
         var6 += var11;
      }

      Iterator var13 = var1.getBats(false).iterator();

      while(var13.hasNext()) {
         NightSwarmBatMob var14 = (NightSwarmBatMob)var13.next();
         var9 = (Point2D.Float)GameRandom.globalRandom.getOneOf((List)var2);
         var14.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var9.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), var9.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50))));
         var14.stages.add(new SetIdleNightSwarmBatStage(var9.x, var9.y));
      }

      var1.nextLevelX = var12 / (float)var2.size();
      var1.nextLevelY = var6 / (float)var2.size();
   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return true;
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
