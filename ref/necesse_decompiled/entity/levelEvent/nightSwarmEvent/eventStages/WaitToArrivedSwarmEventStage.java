package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.util.Iterator;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitToArrivedNightSwarmBatStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public class WaitToArrivedSwarmEventStage extends NightSwarmEventStage {
   public WaitToArrivedSwarmEventStage() {
   }

   public void onStarted(NightSwarmLevelEvent var1) {
      Iterator var2 = var1.getBats(false).iterator();

      while(var2.hasNext()) {
         NightSwarmBatMob var3 = (NightSwarmBatMob)var2.next();
         var3.stages.add(new WaitToArrivedNightSwarmBatStage());
      }

   }

   public void serverTick(NightSwarmLevelEvent var1) {
   }

   public boolean hasCompleted(NightSwarmLevelEvent var1) {
      return true;
   }

   public void onCompleted(NightSwarmLevelEvent var1) {
   }
}
