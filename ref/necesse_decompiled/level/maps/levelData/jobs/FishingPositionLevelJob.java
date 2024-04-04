package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketFishingStatus;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobHitResult;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelTile;
import necesse.level.maps.liquidManager.ClosestHeightResult;

public class FishingPositionLevelJob extends LevelJob {
   public FishingPositionLevelJob(int var1, int var2) {
      super(var1, var2);
   }

   public FishingPositionLevelJob(LoadData var1) {
      super(var1);
   }

   public LevelTile getTile() {
      return this.getLevel().getLevelTile(this.tileX, this.tileY);
   }

   public int getLiquidHeight() {
      return this.getLevel().liquidManager.getHeight(this.tileX, this.tileY);
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isValid() {
      int var1 = this.getLevel().liquidManager.getHeight(this.tileX, this.tileY);
      return -5 < var1 && var1 < -1;
   }

   public int getSameJobPriority() {
      return -this.getLiquidHeight();
   }

   protected static Point findClosestHeightTileSimple(Entity var0, int var1, int var2, int var3) {
      int var4 = var0.getLevel().liquidManager.getHeight(var1, var2);
      Point var5 = new Point(var1, var2);
      if (var4 == var3) {
         return var5;
      } else {
         boolean var6 = var3 < var4;
         int var7 = var0.getX() / 32;
         int var8 = var0.getY() / 32;
         int[][] var9 = new int[][]{{-1, 0, 1}, {0, 1, -1}, {1, 0, -1}};

         while(true) {
            int[] var10 = var9[Integer.compare(var7 - var5.x, 0) + 1];
            int[] var11 = var9[Integer.compare(var8 - var5.y, 0) + 1];
            Point var12 = null;
            int[] var13 = var10;
            int var14 = var10.length;

            for(int var15 = 0; var15 < var14; ++var15) {
               int var16 = var13[var15];
               int var17 = var5.x + var16;
               if (var17 >= 0 && var17 < var0.getLevel().width) {
                  int[] var18 = var11;
                  int var19 = var11.length;

                  for(int var20 = 0; var20 < var19; ++var20) {
                     int var21 = var18[var20];
                     if (var16 != 0 || var21 != 0) {
                        int var22 = var5.y + var21;
                        if (var22 >= 0 && var22 < var0.getLevel().height && !var0.getLevel().isSolidTile(var17, var22)) {
                           int var23 = var0.getLevel().liquidManager.getHeight(var17, var22);
                           if (var23 == var3) {
                              return new Point(var17, var22);
                           }

                           if (var6 && var23 < var4) {
                              var4 = var23;
                              var12 = new Point(var17, var22);
                           } else if (var23 > var4) {
                              var4 = var23;
                              var12 = new Point(var17, var22);
                           }
                        }
                     }
                  }
               }
            }

            if (var12 == null) {
               return null;
            }

            var5 = var12;
         }
      }
   }

   public static <T extends FishingPositionLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FishingMob var1, FoundJob<T> var2) {
      LocalMessage var3 = new LocalMessage("activities", "fishing");
      return new SingleJobSequence(((FishingPositionLevelJob)var2.job).getActiveJob(var0, var2.priority, var1), var3);
   }

   public ActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, FishingMob var3) {
      return new FishingActiveJob(var1, var2, this.tileX, this.tileY, var3);
   }

   private class FishingActiveJob extends TileActiveJob {
      protected FishingMob fishingMob;
      protected FishingEvent fishingEvent;
      protected int maxWaitTicker;

      public FishingActiveJob(EntityJobWorker var2, JobTypeHandler.TypePriority var3, int var4, int var5, FishingMob var6) {
         super(var2, var3, var4, var5);
         this.fishingMob = var6;
      }

      public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
         if (var1 != null && !this.getLevel().isSolidTile(var1.tileX, var1.tileY) && !this.getLevel().collides(new Line2D.Float((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16), (float)(var1.tileX * 32 + 16), (float)(var1.tileY * 32 + 16)), 16.0F, 4.0F, (new CollisionFilter()).projectileCollision())) {
            return var1;
         } else {
            ClosestHeightResult var2 = this.getLevel().liquidManager.findClosestHeightTile(this.tileX, this.tileY, 1, (var1x) -> {
               if (this.getLevel().isSolidTile(var1x.x, var1x.y)) {
                  return false;
               } else {
                  return !this.getLevel().collides(new Line2D.Float((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16), (float)(var1x.x * 32 + 16), (float)(var1x.y * 32 + 16)), 16.0F, 4.0F, (new CollisionFilter()).projectileCollision());
               }
            });
            return new JobMoveToTile(var2.best.x, var2.best.y, false);
         }
      }

      public void tick(boolean var1, boolean var2) {
      }

      public boolean isAt(JobMoveToTile var1) {
         Mob var2 = this.worker.getMobWorker();
         double var3 = (new Point(var2.getX(), var2.getY())).distance((double)(var1.tileX * 32 + 16), (double)(var1.tileY * 32 + 16));
         return var3 < 32.0;
      }

      public boolean isValid(boolean var1) {
         return FishingPositionLevelJob.this.isRemoved() ? false : FishingPositionLevelJob.this.isValid();
      }

      public ActiveJobHitResult onHit(MobWasHitEvent var1, boolean var2) {
         if (this.fishingEvent != null && !this.fishingEvent.isReeled) {
            this.fishingEvent.reel();
            if (FishingPositionLevelJob.this.isServer()) {
               this.getLevel().getServer().network.sendToClientsAt(PacketFishingStatus.getReelPacket(this.fishingEvent), (Level)this.getLevel());
            }
         }

         return ActiveJobHitResult.CLEAR_SEQUENCE;
      }

      public void onCancelled(boolean var1, boolean var2, boolean var3) {
         if (this.fishingEvent != null && !this.fishingEvent.isReeled) {
            this.fishingEvent.reel();
            if (FishingPositionLevelJob.this.isServer()) {
               this.getLevel().getServer().network.sendToClientsAt(PacketFishingStatus.getReelPacket(this.fishingEvent), (Level)this.getLevel());
            }
         }

      }

      public ActiveJobResult perform() {
         if (this.fishingEvent == null) {
            FishingRodItem var1 = (FishingRodItem)ItemRegistry.getItem("ironfishingrod");
            BaitItem var2 = (BaitItem)ItemRegistry.getItem("wormbait");
            this.fishingEvent = new FishingEvent(this.fishingMob, this.tileX * 32 + 16, this.tileY * 32 + 16, var1, var2);
            this.getLevel().entityManager.addLevelEvent(this.fishingEvent);
            this.worker.showAttackAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var1, var1.getAttackAnimTime(new InventoryItem(var1), (Mob)null));
            this.maxWaitTicker = 600;
         } else if (!this.fishingEvent.isReeled) {
            --this.maxWaitTicker;
            if (this.maxWaitTicker <= 0 || this.fishingEvent.getTicksToNextCatch() == -10 && GameRandom.globalRandom.getChance(0.7F)) {
               this.fishingEvent.reel();
               if (FishingPositionLevelJob.this.isServer()) {
                  this.getLevel().getServer().network.sendToClientsAt(PacketFishingStatus.getReelPacket(this.fishingEvent), (Level)this.getLevel());
               }
            }
         }

         if (this.fishingEvent.isOver()) {
            this.fishingEvent = null;
            return ActiveJobResult.FINISHED;
         } else {
            return ActiveJobResult.PERFORMING;
         }
      }
   }
}
