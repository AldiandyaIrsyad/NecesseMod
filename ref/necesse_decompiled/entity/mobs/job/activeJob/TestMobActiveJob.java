package necesse.entity.mobs.job.activeJob;

import necesse.engine.GameTileRange;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.item.Item;

public class TestMobActiveJob extends MobActiveJob<Mob> {
   private int work;
   private int totalWork;

   public TestMobActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, Mob var3, GameTileRange var4, int var5) {
      super(var1, var2, var3, var4);
      this.totalWork = var5;
   }

   public boolean isJobValid(boolean var1) {
      return true;
   }

   public void tick(boolean var1, boolean var2) {
   }

   public ActiveJobResult performTarget() {
      if (this.work % 20 == 0) {
         System.out.println(this.worker.getMobWorker().getDisplayName() + " working " + this.work);
      }

      if (!this.worker.isInWorkAnimation()) {
         this.worker.showPlaceAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), (Item)null, 250);
      }

      ++this.work;
      if (this.work < this.totalWork) {
         return ActiveJobResult.PERFORMING;
      } else {
         System.out.println(this.worker.getMobWorker().getDisplayName() + " finished working " + this.work);
         return ActiveJobResult.FINISHED;
      }
   }
}
