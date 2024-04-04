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
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.HuntMobActiveJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HuntMobLevelJob extends EntityLevelJob<Mob> {
   public HuntMobLevelJob(Mob var1) {
      super((Entity)var1);
   }

   public HuntMobLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean shouldSave() {
      return false;
   }

   public static <T extends HuntMobLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1) {
      GameMessage var2 = ((Mob)((HuntMobLevelJob)var1.job).target).getLocalization();
      LocalMessage var3 = new LocalMessage("activities", "hunting", "target", var2);
      GameLinkedListJobSequence var4 = new GameLinkedListJobSequence(var3);
      var4.add(((HuntMobLevelJob)var1.job).getActiveJob(var0, var1.priority, (GameTileRange)var1.handler.tileRange.apply(var0.getLevel()), var4, "woodsword", new GameDamage(DamageTypeRegistry.MELEE, 20.0F), 64, 500, 500).addRangedAttack("woodbow", new GameDamage(DamageTypeRegistry.RANGED, 20.0F), 256, 500, 1000, "stonearrow", 100));
      return var4;
   }

   public static JobTypeHandler.JobStreamSupplier<? extends HuntMobLevelJob> getJobStreamer() {
      return (var0, var1) -> {
         Mob var2 = var0.getMobWorker();
         ZoneTester var3 = var0.getJobRestrictZone();
         Point var4 = var0.getJobSearchTile();
         GameTileRange var5 = (GameTileRange)var1.tileRange.apply(var2.getLevel());
         return var2.getLevel().entityManager.mobs.streamInRegionsShape(var5.getRangeBounds(var4), 0).filter((var1x) -> {
            return !var1x.removed() && var2.isSamePlace(var1x) && var1x.isCritter;
         }).filter((var1x) -> {
            return var3.containsTile(var1x.getTileX(), var1x.getTileY());
         }).filter((var2x) -> {
            return var5.isWithinRange(var4, var2x.getTileX(), var2x.getTileY());
         }).map((var0x) -> {
            return ((CritterMob)var0x).huntJob;
         }).filter(Objects::nonNull).filter(EntityLevelJob::isValid);
      };
   }

   public HuntMobActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, GameTileRange var3, GameLinkedListJobSequence var4, String var5, GameDamage var6, int var7, int var8, int var9) {
      return new HuntMobActiveJob(var1, var2, this, var3, var4, var5, var6, var7, var8, var9);
   }
}
