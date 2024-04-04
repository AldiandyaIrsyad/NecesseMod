package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitToTimeNightSwarmBatStage extends NightSwarmBatStage {
   private long endTime;

   public WaitToTimeNightSwarmBatStage(boolean var1, long var2) {
      super(var1);
      this.endTime = var2;
   }

   public void onStarted(NightSwarmBatMob var1) {
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return this.endTime <= var1.getWorldEntity().getTime();
   }

   public void onCompleted(NightSwarmBatMob var1) {
   }
}
