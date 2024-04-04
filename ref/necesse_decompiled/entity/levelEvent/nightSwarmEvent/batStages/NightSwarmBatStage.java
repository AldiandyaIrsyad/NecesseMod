package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public abstract class NightSwarmBatStage {
   public final boolean idleAllowed;
   private LinkedList<NightSwarmCompletedCounter> completedOrRemoved = new LinkedList();

   public NightSwarmBatStage(boolean var1) {
      this.idleAllowed = var1;
   }

   public abstract void onStarted(NightSwarmBatMob var1);

   public abstract void serverTick(NightSwarmBatMob var1);

   public abstract boolean hasCompleted(NightSwarmBatMob var1);

   public abstract void onCompleted(NightSwarmBatMob var1);

   public final void onCompletedOrRemoved(NightSwarmBatMob var1, boolean var2) {
      if (!var2) {
         this.onCompleted(var1);
      }

      Iterator var3 = this.completedOrRemoved.iterator();

      while(var3.hasNext()) {
         NightSwarmCompletedCounter var4 = (NightSwarmCompletedCounter)var3.next();
         var4.done.addAndGet(1);
      }

      this.completedOrRemoved.clear();
   }

   public NightSwarmBatStage addCompletedCounter(NightSwarmCompletedCounter var1) {
      var1.total.addAndGet(1);
      this.completedOrRemoved.add(var1);
      return this;
   }

   public void onCollisionHit(NightSwarmBatMob var1, Mob var2) {
   }

   public void onWasHit(NightSwarmBatMob var1, MobWasHitEvent var2) {
   }
}
