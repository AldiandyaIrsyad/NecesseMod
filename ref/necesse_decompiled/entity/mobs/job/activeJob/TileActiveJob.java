package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.multiTile.MultiTile;

public abstract class TileActiveJob extends ActiveJob {
   public final int tileX;
   public final int tileY;

   public TileActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, int var3, int var4) {
      super(var1, var2);
      this.tileX = var3;
      this.tileY = var4;
   }

   public boolean isAt(JobMoveToTile var1) {
      Mob var2 = this.worker.getMobWorker();
      if (var1.acceptAdjacentTiles) {
         MultiTile var3 = this.getLevel().getObject(var1.tileX, var1.tileY).getMultiTile(this.getLevel(), var1.tileX, var1.tileY);
         if (var3.getAdjacentTileRectangle(var1.tileX, var1.tileY).contains(var2.getX() / 32, var2.getY() / 32) && !var2.hasCurrentMovement()) {
            return true;
         }
      }

      double var5 = (new Point(var2.getX(), var2.getY())).distance((double)(var1.tileX * 32 + 16), (double)(var1.tileY * 32 + 16));
      return var5 <= (double)this.getCompleteRange() ? this.hasLOS() : false;
   }

   public int getCompleteRange() {
      return 48;
   }

   public boolean hasLOS() {
      Mob var1 = this.worker.getMobWorker();
      Line2D.Float var2 = new Line2D.Float(var1.x, var1.y, (float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16));
      return !var1.getLevel().collides((Line2D)var2, (CollisionFilter)(new CollisionFilter()).mobCollision().addFilter((var0) -> {
         Rectangle var1 = var0.object().getMultiTile().getTileRectangle(var0.tileX, var0.tileY);
         return !var1.contains(var0.tileX, var0.tileY);
      }));
   }

   public String toString() {
      return super.toString() + "{" + this.tileX + ", " + this.tileY + "}";
   }

   protected Inventory getTileInventory() {
      ObjectEntity var1 = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
      return var1 instanceof OEInventory ? ((OEInventory)var1).getInventory() : null;
   }
}
