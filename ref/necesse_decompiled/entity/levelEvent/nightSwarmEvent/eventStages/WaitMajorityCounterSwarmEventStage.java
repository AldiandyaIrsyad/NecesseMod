package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.util.Iterator;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.CounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitMajorityCounterSwarmEventStage extends NightSwarmEventStage {
   public NightSwarmCompletedCounter counter;

   public WaitMajorityCounterSwarmEventStage() {
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      this.counter = new NightSwarmCompletedCounter();
      Iterator var2 = var1.getBats(false).iterator();

      while(var2.hasNext()) {
         NightSwarmBatMob var3 = (NightSwarmBatMob)var2.next();
         var3.stages.add(new CounterNightSwarmBatStage(true, this.counter));
      }

   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return this.counter == null || this.counter.isMajorityComplete();
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
