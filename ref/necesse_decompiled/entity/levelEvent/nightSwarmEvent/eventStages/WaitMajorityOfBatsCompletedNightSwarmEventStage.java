package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;

public class WaitMajorityOfBatsCompletedNightSwarmEventStage extends NightSwarmEventStage {
   public WaitMajorityOfBatsCompletedNightSwarmEventStage() {
   }

   public void onStarted(NightSwarmLevelEvent var1) {
   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return this.majorityOfBatsCompleted(var1);
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
