package necesse.entity.mobs.job;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class LinkedListJobSequence extends LinkedList<ActiveJob> implements JobSequence {
   public GameMessage activityDescription;

   public LinkedListJobSequence(GameMessage var1) {
      this.activityDescription = var1;
   }

   public GameMessage getActivityDescription() {
      return this.activityDescription;
   }

   public void setActivityDescription(GameMessage var1) {
      this.activityDescription = var1;
   }

   public void tick() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         ActiveJob var2 = (ActiveJob)var1.next();
         var2.tick(false, false);
      }

   }

   public boolean hasNext() {
      return !this.isEmpty();
   }

   public ActiveJob next() {
      return (ActiveJob)this.removeFirst();
   }

   public boolean isValid() {
      ListIterator var1 = this.listIterator();

      while(var1.hasNext()) {
         ActiveJob var2 = (ActiveJob)var1.next();
         if (!var2.isValid(false)) {
            if (var2.shouldClearSequence()) {
               return false;
            }

            var1.remove();
         }
      }

      return true;
   }

   public ActiveJobTargetFoundResult onTargetFound(Mob var1) {
      Iterator var2 = this.iterator();

      ActiveJobTargetFoundResult var4;
      do {
         if (!var2.hasNext()) {
            return ActiveJobTargetFoundResult.CONTINUE;
         }

         ActiveJob var3 = (ActiveJob)var2.next();
         var4 = var3.onTargetFound(var1, false, false);
      } while(var4 != ActiveJobTargetFoundResult.FAIL);

      return var4;
   }

   public void cancel(boolean var1) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         ActiveJob var3 = (ActiveJob)var2.next();
         var3.onCancelled(var1, false, false);
      }

   }
}
