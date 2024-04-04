package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class CounterNightSwarmBatStage extends NightSwarmBatStage {
   public CounterNightSwarmBatStage(boolean var1, NightSwarmCompletedCounter var2) {
      super(var1);
      this.addCompletedCounter(var2);
   }

   public void onStarted(NightSwarmBatMob var1) {
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return true;
   }

   public void onCompleted(NightSwarmBatMob var1) {
   }
}
