package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class SetIdleNightSwarmBatStage extends NightSwarmBatStage {
   public float idleXPos;
   public float idleYPos;

   public SetIdleNightSwarmBatStage(float var1, float var2) {
      super(true);
      this.idleXPos = var1;
      this.idleYPos = var2;
   }

   public void onStarted(NightSwarmBatMob var1) {
      var1.idleXPos = this.idleXPos;
      var1.idleYPos = this.idleYPos;
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return true;
   }

   public void onCompleted(NightSwarmBatMob var1) {
   }
}
