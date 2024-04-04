package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.Objects;
import necesse.engine.GameTileRange;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.HuntMobActiveJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class SlaughterHusbandryMobLevelJob extends EntityLevelJob<Mob> {
   public SettlementHusbandryZone zone;

   public SlaughterHusbandryMobLevelJob(Mob var1, SettlementHusbandryZone var2) {
      super((Entity)var1);
      this.zone = var2;
   }

   public SlaughterHusbandryMobLevelJob(LoadData var1) {
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
         return this.zone.isRemoved() ? false : this.zone.containsTile(((Mob)this.target).getX() / 32, ((Mob)this.target).getY() / 32);
      }
   }

   public static <T extends SlaughterHusbandryMobLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1) {
      GameMessage var2 = ((Mob)((SlaughterHusbandryMobLevelJob)var1.job).target).getLocalization();
      LocalMessage var3 = new LocalMessage("activities", "slaughtering", "target", var2);
      GameLinkedListJobSequence var4 = new GameLinkedListJobSequence(var3);
      var4.add(((SlaughterHusbandryMobLevelJob)var1.job).getActiveJob(var0, var1.priority, (GameTileRange)var1.handler.tileRange.apply(var0.getLevel()), var4, "woodsword", new GameDamage(DamageTypeRegistry.MELEE, 20.0F), 50, 500, 500));
      return var4;
   }

   public static JobTypeHandler.JobStreamSupplier<? extends SlaughterHusbandryMobLevelJob> getJobStreamer() {
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
            return ((HusbandryMob)var0x).slaughterJob;
         }).filter(Objects::nonNull).filter(SlaughterHusbandryMobLevelJob::isValid);
      };
   }

   public HuntMobActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, GameTileRange var3, GameLinkedListJobSequence var4, String var5, GameDamage var6, int var7, int var8, int var9) {
      return new HuntMobActiveJob(var1, var2, this, var3, var4, var5, var6, var7, var8, var9);
   }
}
