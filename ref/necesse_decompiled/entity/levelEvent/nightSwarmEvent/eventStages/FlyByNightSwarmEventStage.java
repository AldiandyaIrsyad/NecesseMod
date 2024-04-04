package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.Iterator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.SetIdleNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitFromNowNightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class FlyByNightSwarmEventStage extends NightSwarmEventStage {
   public int startOffset;
   public int endOffset;
   public int startMaxRandomOffset;
   public int endMaxRandomOffset;
   public int startEndDistance;
   public int endEndDistance;

   public FlyByNightSwarmEventStage(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.startOffset = var1;
      this.endOffset = var2;
      this.startMaxRandomOffset = var3;
      this.endMaxRandomOffset = var4;
      this.startEndDistance = var5;
      this.endEndDistance = var6;
   }

   public FlyByNightSwarmEventStage(int var1, int var2, int var3) {
      this(var1, var1, var2, var2, var3, var3);
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      Point2D.Float var2 = GameMath.normalize(var1.currentTarget.x - var1.nextLevelX, var1.currentTarget.y - var1.nextLevelY);
      float var3 = GameMath.getAngle(var2);
      float var4 = var3 + (float)GameRandom.globalRandom.getIntBetween(-20, 20);
      var2 = GameMath.getAngleDir(var4);
      int var5 = GameMath.lerp(var1.lastHealthProgress, this.startOffset, this.endOffset);
      int var6 = GameMath.lerp(var1.lastHealthProgress, this.startMaxRandomOffset, this.endMaxRandomOffset);
      int var7 = GameMath.lerp(var1.lastHealthProgress, this.startEndDistance, this.endEndDistance);
      int var8 = GameRandom.globalRandom.getIntBetween(-var6, var6);
      Point2D.Float var9 = GameMath.getPerpendicularPoint(var1.currentTarget.x, var1.currentTarget.y, (float)(var5 + var8), var2);
      Point2D.Float var10 = GameMath.getPerpendicularPoint(var1.currentTarget.x, var1.currentTarget.y, (float)(-var5 + var8), var2);
      Point2D.Float var11 = new Point2D.Float(var1.currentTarget.x + var2.x * (float)var7, var1.currentTarget.y + var2.y * (float)var7);
      int var12 = 0;

      for(Iterator var13 = var1.getBats(false).iterator(); var13.hasNext(); ++var12) {
         NightSwarmBatMob var14 = (NightSwarmBatMob)var13.next();
         int var15 = GameRandom.globalRandom.getIntBetween(-60, 60);
         int var16 = GameRandom.globalRandom.getIntBetween(-60, 60);
         var14.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var1.nextLevelX + (float)var15, var1.nextLevelY + (float)var16)));
         var14.stages.add(new WaitFromNowNightSwarmBatStage(true, var14, var12 * 25));
         Point2D.Float var17 = (Point2D.Float)GameRandom.globalRandom.getOneOf((Object[])(var9, var10));
         var14.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var17.x + (float)var15, var17.y + (float)var16)));
         var14.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var11.x + (float)var15, var11.y + (float)var16)));
         var14.stages.add(new SetIdleNightSwarmBatStage(var11.x, var11.y));
      }

      var1.nextLevelX = var11.x;
      var1.nextLevelY = var11.y;
   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return true;
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
