package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitFromNowNightSwarmBatStage extends NightSwarmBatStage {
   private long endTime;

   public WaitFromNowNightSwarmBatStage(boolean var1, NightSwarmBatMob var2, int var3) {
      super(var1);
      this.endTime = var2.getWorldEntity().getTime() + (long)var3;
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
