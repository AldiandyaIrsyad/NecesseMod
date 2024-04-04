package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitToArrivedNightSwarmBatStage extends NightSwarmBatStage {
   public WaitToArrivedNightSwarmBatStage() {
      super(false);
   }

   public void onStarted(NightSwarmBatMob var1) {
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return !var1.hasCurrentMovement() || var1.hasArrivedAtTarget();
   }

   public void onCompleted(NightSwarmBatMob var1) {
   }
}
