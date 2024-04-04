package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitNightSwarmBatStage extends NightSwarmBatStage {
   public int time;
   private long endTime;

   public WaitNightSwarmBatStage(boolean var1, int var2) {
      super(var1);
      this.time = var2;
   }

   public void onStarted(NightSwarmBatMob var1) {
      this.endTime = var1.getWorldEntity().getTime() + (long)this.time;
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return this.endTime <= var1.getWorldEntity().getTime();
   }

   public void onCompleted(NightSwarmBatMob var1) {
   }
}
