package necesse.entity.mobs.job.activeJob;

import java.util.function.Predicate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.TileDamageResult;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.item.Item;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class MineObjectActiveJob extends TileActiveJob {
   public Predicate<LevelObject> isValidObject;
   public GameObjectReservable reservable;
   public String attackItemStringID;
   public int objectDamage;
   public int animTime;
   public int attackCooldown;
   public long lastAttackTime;

   public MineObjectActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, int var3, int var4, Predicate<LevelObject> var5, GameObjectReservable var6, String var7, int var8, int var9, int var10) {
      super(var1, var2, var3, var4);
      this.isValidObject = var5;
      this.reservable = var6;
      this.attackItemStringID = var7;
      this.objectDamage = var8;
      this.animTime = var9;
      this.attackCooldown = var10;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return new JobMoveToTile(this.tileX, this.tileY, true);
   }

   public void tick(boolean var1, boolean var2) {
      if (this.reservable != null) {
         this.reservable.reserve(this.worker.getMobWorker());
      }

   }

   public boolean isValid(boolean var1) {
      if (this.reservable != null && !this.reservable.isAvailable(this.worker.getMobWorker())) {
         return false;
      } else if (this.isValidObject != null) {
         LevelObject var2 = this.getLevel().getLevelObject(this.tileX, this.tileY);
         return this.isValidObject.test(var2);
      } else {
         return true;
      }
   }

   public void addItemPickupJobs(JobTypeHandler.TypePriority var1, TileDamageResult var2, GameLinkedListJobSequence var3) {
      PickupItemEntityActiveJob.addItemPickupJobs(this.worker, var1, var2.itemsDropped, var3);
   }

   public abstract void onObjectDestroyed(TileDamageResult var1);

   public ActiveJobResult perform() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         Mob var1 = this.worker.getMobWorker();
         if (this.lastAttackTime + (long)this.attackCooldown > var1.getWorldEntity().getTime()) {
            return ActiveJobResult.PERFORMING;
         } else {
            if (this.attackItemStringID != null) {
               this.worker.showAttackAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, ItemRegistry.getItem(this.attackItemStringID), this.animTime);
            } else {
               this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, (Item)null, this.animTime);
            }

            int var10008 = this.tileX * 32 + 16;
            int var10009 = this.tileY * 32;
            TileDamageResult var2 = this.getLevel().entityManager.doDamage(this.tileX, this.tileY, this.objectDamage, (TileDamageType)TileDamageType.Object, -1, (ServerClient)null, true, var10008, var10009 + 16);
            if (var2.destroyed) {
               this.onObjectDestroyed(var2);
               return ActiveJobResult.FINISHED;
            } else {
               return var2.addedDamage > 0 ? ActiveJobResult.PERFORMING : ActiveJobResult.FAILED;
            }
         }
      }
   }
}
