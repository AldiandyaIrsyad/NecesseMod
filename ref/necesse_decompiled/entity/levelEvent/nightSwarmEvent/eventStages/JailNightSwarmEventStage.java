package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.AngleSplitNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitCounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitNightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class JailNightSwarmEventStage extends NightSwarmEventStage {
   public JailNightSwarmEventStage() {
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      float var2 = var1.currentTarget.x;
      float var3 = var1.currentTarget.y;
      int var4 = GameRandom.globalRandom.nextInt(360);
      byte var5 = 4;
      short var6 = 150;
      short var7 = 500;
      short var8 = 700;
      float var9 = 360.0F / (float)var5;
      ArrayList var10 = new ArrayList();
      ArrayList var11 = new ArrayList();

      float var16;
      BatLine var17;
      for(int var12 = 0; var12 < var5; ++var12) {
         float var13 = GameMath.fixAngle((float)var12 * var9 + (float)var4);
         Point2D.Float var14 = GameMath.getAngleDir(var13);
         float var15 = var2 + var14.x * (float)var7;
         var16 = var3 + var14.y * (float)var7;
         var17 = new BatLine(var15, var16, -var14.x, -var14.y, var8);
         var10.add(var17);
         var17.addJailLines(var11, var6, var7 * 2);
      }

      var11.sort(Comparator.comparingDouble((var2x) -> {
         return GameMath.diagonalMoveDistance((double)(var2x.startX - var2), (double)(var2x.startY - var3));
      }));
      ArrayList var21 = new ArrayList(var1.bats.size());
      Iterator var22 = var1.getBats(false).iterator();

      while(var22.hasNext()) {
         NightSwarmBatMob var24 = (NightSwarmBatMob)var22.next();
         var21.add(var24);
      }

      ArrayList var23 = new ArrayList(var11);
      NightSwarmCompletedCounter var25 = new NightSwarmCompletedCounter();

      while(!var23.isEmpty() && !var21.isEmpty()) {
         JailLine var26 = (JailLine)var23.remove(0);
         var16 = 0.0F;
         int var29 = -1;

         for(int var18 = 0; var18 < var21.size(); ++var18) {
            NightSwarmBatMob var19 = (NightSwarmBatMob)var21.get(var18);
            float var20 = (float)GameMath.diagonalMoveDistance((double)(var19.x - var26.startX), (double)(var19.y - var26.startY));
            if (var29 == -1 || var20 < var16) {
               var29 = var18;
               var16 = var20;
            }
         }

         if (var29 != -1) {
            NightSwarmBatMob var30 = (NightSwarmBatMob)var21.remove(var29);
            var30.stages.add((new MoveNightSwarmBatStage(new MobMovementLevelPos(var26.startX, var26.startY))).addCompletedCounter(var25));
            var30.stages.add(new WaitCounterNightSwarmBatStage(false, var25));
            var30.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var26.endX, var26.endY)));
            var30.stages.add(new AngleSplitNightSwarmBatStage(var2, var3, 4, (float)var4, var7));
         }
      }

      Iterator var27 = var21.iterator();

      while(true) {
         while(var27.hasNext()) {
            NightSwarmBatMob var28 = (NightSwarmBatMob)var27.next();
            var17 = (BatLine)var10.stream().min(Comparator.comparingDouble((var1x) -> {
               return (double)var28.getDistance(var1x.midX, var1x.midY);
            })).orElse((Object)null);
            if (var17 != null && !var17.jailLines.isEmpty()) {
               JailLine var31 = (JailLine)GameRandom.globalRandom.getOneOf((List)var17.jailLines);
               var28.stages.add((new MoveNightSwarmBatStage(new MobMovementLevelPos(var31.startX, var31.startY))).addCompletedCounter(var25));
               var28.stages.add(new WaitCounterNightSwarmBatStage(false, var25));
               var28.stages.add(new WaitNightSwarmBatStage(false, GameRandom.globalRandom.getIntBetween(100, 400)));
               var28.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var31.endX, var31.endY)));
               var28.stages.add(new AngleSplitNightSwarmBatStage(var2, var3, 4, (float)var4, var7));
            } else {
               var28.stages.add(new AngleSplitNightSwarmBatStage(var2, var3, 4, (float)var4, var7));
            }
         }

         return;
      }
   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return true;
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }

   private static class BatLine {
      public float midX;
      public float midY;
      public float x1;
      public float y1;
      public float x2;
      public float y2;
      public float length;
      public float lineDirX;
      public float lineDirY;
      public float facingX;
      public float facingY;
      public ArrayList<JailLine> jailLines = new ArrayList();

      public BatLine(float var1, float var2, float var3, float var4, int var5) {
         this.midX = var1;
         this.midY = var2;
         this.facingX = var3;
         this.facingY = var4;
         Point2D.Float var6 = GameMath.getPerpendicularDir(var3, var4);
         this.lineDirX = var6.x;
         this.lineDirY = var6.y;
         this.x1 = var1 + this.lineDirX * (float)var5;
         this.y1 = var2 + this.lineDirY * (float)var5;
         this.x2 = var1 - this.lineDirX * (float)var5;
         this.y2 = var2 - this.lineDirY * (float)var5;
         this.length = (float)(var5 * 2);
      }

      public void addJailLines(ArrayList<JailLine> var1, int var2, int var3) {
         int var4 = Math.max((int)(this.length / (float)var2), 1);
         int var5 = (var4 - 1) * var2;
         float var6 = (this.length - (float)var5) / 2.0F;
         this.jailLines = new ArrayList(var4);

         for(int var7 = 0; var7 < var4; ++var7) {
            float var8 = var6 + (float)(var7 * var2);
            float var9 = this.x1 - this.lineDirX * var8;
            float var10 = this.y1 - this.lineDirY * var8;
            float var11 = var9 + this.facingX * (float)var3;
            float var12 = var10 + this.facingY * (float)var3;
            this.jailLines.add(new JailLine(var9, var10, var11, var12));
         }

         var1.addAll(this.jailLines);
      }
   }

   private static class JailLine {
      public float startX;
      public float startY;
      public float endX;
      public float endY;

      public JailLine(float var1, float var2, float var3, float var4) {
         this.startX = var1;
         this.startY = var2;
         this.endX = var3;
         this.endY = var4;
      }
   }
}
