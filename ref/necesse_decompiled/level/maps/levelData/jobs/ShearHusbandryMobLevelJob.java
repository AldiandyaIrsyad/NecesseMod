package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import necesse.engine.GameTileRange;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.InteractMobActiveJob;
import necesse.entity.mobs.job.activeJob.PickupItemEntityActiveJob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class ShearHusbandryMobLevelJob extends EntityLevelJob<HusbandryMob> {
   public SettlementHusbandryZone zone;

   public ShearHusbandryMobLevelJob(HusbandryMob var1, SettlementHusbandryZone var2) {
      super((Entity)var1);
      this.zone = var2;
   }

   public ShearHusbandryMobLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isValid() {
      if (!super.isValid()) {
         return false;
      } else if (this.zone == null) {
         return false;
      } else {
         return this.zone.isRemoved() ? false : this.zone.containsTile(((HusbandryMob)this.target).getX() / 32, ((HusbandryMob)this.target).getY() / 32);
      }
   }

   public static <T extends ShearHusbandryMobLevelJob> JobSequence getJobSequence(EntityJobWorker var0, final FoundJob<T> var1) {
      final InventoryItem var2 = new InventoryItem("shears");
      if (((HusbandryMob)((ShearHusbandryMobLevelJob)var1.job).target).canShear(var2)) {
         GameMessage var3 = ((HusbandryMob)((ShearHusbandryMobLevelJob)var1.job).target).getLocalization();
         LocalMessage var4 = new LocalMessage("activities", "shearing", "target", var3);
         final GameLinkedListJobSequence var5 = new GameLinkedListJobSequence(var4);
         var5.add(new InteractMobActiveJob<HusbandryMob>(var0, var1.priority, (HusbandryMob)((ShearHusbandryMobLevelJob)var1.job).target, (GameTileRange)var1.handler.tileRange.apply(var0.getLevel()), (var2x) -> {
            return !((ShearHusbandryMobLevelJob)var1.job).isRemoved() && ((ShearHusbandryMobLevelJob)var1.job).isValid() && var2x.canShear(var2);
         }, ((ShearHusbandryMobLevelJob)var1.job).reservable, var2) {
            public ActiveJobResult onInteracted(HusbandryMob var1x) {
               ArrayList var2x = new ArrayList();
               var1x.onShear(var2, var2x);
               ArrayList var3 = new ArrayList(var2x.size());
               Iterator var4 = var2x.iterator();

               while(var4.hasNext()) {
                  InventoryItem var5x = (InventoryItem)var4.next();
                  ItemPickupEntity var6 = var5x.getPickupEntity(var1x.getLevel(), var1x.x, var1x.y);
                  var1x.getLevel().entityManager.pickups.add(var6);
                  var3.add(var6);
               }

               PickupItemEntityActiveJob.addItemPickupJobs(this.worker, var1.priority, var3, var5);
               return ActiveJobResult.FINISHED;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public ActiveJobResult onInteracted(Mob var1x) {
               return this.onInteracted((HusbandryMob)var1x);
            }
         });
         return var5;
      } else {
         return null;
      }
   }

   public static JobTypeHandler.JobStreamSupplier<? extends ShearHusbandryMobLevelJob> getJobStreamer() {
      return (var0, var1) -> {
         Mob var2 = var0.getMobWorker();
         ZoneTester var3 = var0.getJobRestrictZone();
         Point var4 = var0.getJobSearchTile();
         GameTileRange var5 = (GameTileRange)var1.tileRange.apply(var2.getLevel());
         return var2.getLevel().entityManager.mobs.streamInRegionsShape(var5.getRangeBounds(var4), 0).filter((var1x) -> {
            return !var1x.removed() && var2.isSamePlace(var1x) && var1x instanceof HusbandryMob;
         }).filter((var1x) -> {
            return var3.containsTile(var1x.getTileX(), var1x.getTileY());
         }).filter((var2x) -> {
            return var5.isWithinRange(var4, var2x.getTileX(), var2x.getTileY());
         }).map((var0x) -> {
            return ((HusbandryMob)var0x).shearJob;
         }).filter(Objects::nonNull).filter(ShearHusbandryMobLevelJob::isValid);
      };
   }
}
