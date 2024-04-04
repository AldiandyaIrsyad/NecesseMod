package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitCounterNightSwarmBatStage extends NightSwarmBatStage {
   public NightSwarmCompletedCounter counter;

   public WaitCounterNightSwarmBatStage(boolean var1, NightSwarmCompletedCounter var2) {
      super(var1);
      this.counter = var2;
   }

   public void onStarted(NightSwarmBatMob var1) {
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return this.counter.isComplete();
   }

   public void onCompleted(NightSwarmBatMob var1) {
   }
}
