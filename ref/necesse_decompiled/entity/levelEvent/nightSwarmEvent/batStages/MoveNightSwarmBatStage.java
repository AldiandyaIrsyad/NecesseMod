package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovement;

public class MoveNightSwarmBatStage extends NightSwarmBatStage {
   public MobMovement movement;

   public MoveNightSwarmBatStage(MobMovement var1) {
      super(false);
      this.movement = var1;
   }

   public void onStarted(NightSwarmBatMob var1) {
      var1.setMovement(this.movement);
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return this.movement == null || var1.hasArrivedAtTarget() || !var1.hasCurrentMovement();
   }

   public void onCompleted(NightSwarmBatMob var1) {
      var1.setMovement((MobMovement)null);
   }
}
