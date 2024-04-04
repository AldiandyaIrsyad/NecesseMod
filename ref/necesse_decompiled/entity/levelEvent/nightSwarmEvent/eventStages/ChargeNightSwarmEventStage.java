package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.SetIdleNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitToTimeNightSwarmBatStage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class ChargeNightSwarmEventStage extends NightSwarmEventStage {
   public int endDistance;
   public NightSwarmCompletedCounter counter;

   public ChargeNightSwarmEventStage(int var1) {
      this.endDistance = var1;
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      double var2 = 0.0;
      double var4 = 0.0;
      int var6 = 0;
      long var7 = var1.level.getWorldEntity().getTime();
      this.counter = new NightSwarmCompletedCounter();

      for(Iterator var9 = var1.getBats(false).iterator(); var9.hasNext(); ++var6) {
         NightSwarmBatMob var10 = (NightSwarmBatMob)var9.next();
         GameAreaStream var10000 = var1.level.entityManager.players.streamAreaTileRange(var10.getX(), var10.getY(), 31).filter((var0) -> {
            return var0 != null && !var0.removed() && var0.isVisible();
         });
         Objects.requireNonNull(var10);
         PlayerMob var11 = (PlayerMob)var10000.findBestDistance(0, Comparator.comparingDouble(var10::getDistance)).orElse((Object)null);
         var10.stages.add(new WaitToTimeNightSwarmBatStage(true, var7 + (long)GameRandom.globalRandom.nextInt(500)));
         if (var11 != null) {
            Point2D.Float var12 = GameMath.normalize(var11.x - var10.x, var11.y - var10.y);
            float var13 = var11.x + var12.x * (float)this.endDistance;
            float var14 = var11.y + var12.y * (float)this.endDistance;
            var10.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(var13 + (float)GameRandom.globalRandom.getIntBetween(-50, 50), var14 + (float)GameRandom.globalRandom.getIntBetween(-50, 50))));
            var10.stages.add(new SetIdleNightSwarmBatStage(var13, var14));
            var2 += (double)var13;
            var4 += (double)var14;
         } else {
            var2 += (double)var10.x;
            var4 += (double)var10.y;
         }

         var10.stages.add(new CounterNightSwarmBatStage(true, this.counter));
      }

      var1.nextLevelX = (float)(var2 / (double)var6);
      var1.nextLevelY = (float)(var4 / (double)var6);
   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return this.counter.isMajorityComplete();
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
