package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;

public class WaitNightSwarmEventStage extends NightSwarmEventStage {
   public int minTime;
   public int maxTime;
   public int timer;

   public WaitNightSwarmEventStage(int var1, int var2) {
      this.minTime = var1;
      this.maxTime = var2;
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      int var2 = this.maxTime - this.minTime;
      this.timer = this.minTime + (int)((1.0F - var1.lastHealthProgress) * (float)var2);
   }

   public void serverTick(NightSwarmLevelEvent var1) {
      this.timer -= 50;
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return this.timer <= 0;
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
