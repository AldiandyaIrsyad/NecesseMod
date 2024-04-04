package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;

public abstract class NightSwarmEventStage {
   public NightSwarmEventStage() {
   }

   public abstract void onStarted(NightSwarmLevelEvent var1);

   public abstract void serverTick(NightSwarmLevelEvent var1);

   public abstract boolean hasCompleted(NightSwarmLevelEvent var1);

   public abstract void onCompleted(NightSwarmLevelEvent var1);

   public boolean majorityOfBatsCompleted(NightSwarmLevelEvent var1) {
      return var1.batsDoneWithStages >= Math.min((int)((float)Math.max(var1.bats.size(), 10) / 1.5F), var1.bats.size());
   }
}
