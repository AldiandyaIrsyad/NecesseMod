package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.util.Iterator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CircleEndNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitCounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitNightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class CircleChargeNightSwarmEventStage extends NightSwarmEventStage {
   public float midX;
   public float midY;
   public int circleRange;
   public boolean forceComplete;
   public NightSwarmCompletedCounter completedCounter = new NightSwarmCompletedCounter();

   public CircleChargeNightSwarmEventStage() {
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      this.midX = var1.currentTarget.x;
      this.midY = var1.currentTarget.y;
      this.forceComplete = false;
      long var2 = var1.level.getWorldEntity().getTime();
      int var4 = GameMath.lerp(var1.lastHealthProgress, 6000, 2000);
      long var5 = (long)GameMath.lerp(var1.lastHealthProgress, 800, 200);
      long var7 = var2 + (long)GameMath.lerp(var1.lastHealthProgress, 12000, 6000);
      this.circleRange = GameMath.lerp(var1.lastHealthProgress, 450, 300);
      this.completedCounter = new NightSwarmCompletedCounter();
      int var9 = 0;
      int var10 = GameRandom.prime(GameRandom.globalRandom.getIntBetween(var1.bats.size(), var1.bats.size() + 10));

      for(Iterator var11 = var1.getBats(true).iterator(); var11.hasNext(); ++var9) {
         NightSwarmBatMob var12 = (NightSwarmBatMob)var11.next();
         if (var12 != null && !var12.removed()) {
            float var13 = this.midX + GameRandom.globalRandom.getFloatBetween(-50.0F, 50.0F);
            float var14 = this.midY + GameRandom.globalRandom.getFloatBetween(-50.0F, 50.0F);
            int var15 = var9 * var10 % var1.bats.size();
            var12.stages.add(new WaitNightSwarmBatStage(true, GameRandom.globalRandom.getIntBetween(0, (int)((float)var4 / 1.5F))));
            var12.stages.add(new CircleEndNightSwarmBatStage(var13, var14, var13, var14, this.circleRange, var2 + (long)var4 + (long)var15 * var5, var7));
            var12.stages.add((new WaitNightSwarmBatStage(false, GameRandom.globalRandom.getIntBetween(0, 1000))).addCompletedCounter(this.completedCounter));
            var12.stages.add(new WaitCounterNightSwarmBatStage(false, this.completedCounter));
         }
      }

      var1.nextLevelX = var1.currentTarget.x;
      var1.nextLevelY = var1.currentTarget.y;
   }

   public void serverTick(NightSwarmLevelEvent var1) {
      boolean var2 = var1.level.entityManager.players.streamAreaTileRange((int)this.midX, (int)this.midY, (this.circleRange + 50) / 32).filter((var0) -> {
         return var0 != null && !var0.removed() && var0.isVisible();
      }).anyMatch((var1x) -> {
         return var1x.getDistance(this.midX, this.midY) <= (float)this.circleRange;
      });
      if (!var2) {
         this.forceComplete = true;
      }

   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return this.completedCounter.isComplete() || this.forceComplete;
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
      Iterator var2 = var1.getBats(false).iterator();

      while(var2.hasNext()) {
         NightSwarmBatMob var3 = (NightSwarmBatMob)var2.next();
         var3.clearStages();
      }

   }
}
