package necesse.entity.mobs.job.activeJob;

import java.util.function.Predicate;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public abstract class InteractMobActiveJob<T extends Mob> extends MobActiveJob<T> {
   public Predicate<T> isValid;
   public GameObjectReservable reservable;
   public InventoryItem interactItem;

   public InteractMobActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, T var3, GameTileRange var4, Predicate<T> var5, GameObjectReservable var6, InventoryItem var7) {
      super(var1, var2, var3, var4);
      this.isValid = var5;
      this.reservable = var6;
      this.interactItem = var7;
   }

   public boolean isJobValid(boolean var1) {
      if (this.reservable != null && !this.reservable.isAvailable(this.worker.getMobWorker())) {
         return false;
      } else {
         return this.isValid != null ? this.isValid.test((Mob)this.target) : true;
      }
   }

   public void tick(boolean var1, boolean var2) {
      if (this.reservable != null) {
         this.reservable.reserve(this.worker.getMobWorker());
      }

   }

   public abstract ActiveJobResult onInteracted(T var1);

   public ActiveJobResult performTarget() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         if (this.interactItem != null) {
            this.worker.showAttackAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), this.interactItem.item, 500);
         } else {
            this.worker.showPlaceAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), (Item)null, 500);
         }

         return this.onInteracted((Mob)this.target);
      }
   }
}
