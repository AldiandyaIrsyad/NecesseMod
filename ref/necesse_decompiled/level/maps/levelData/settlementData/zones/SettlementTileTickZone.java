package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Point;
import java.util.Iterator;
import java.util.SortedSet;
import necesse.engine.tickManager.Performance;

public abstract class SettlementTileTickZone extends SettlementWorkZone {
   protected Point lastProcessingTile;
   protected float iterations;

   public SettlementTileTickZone() {
   }

   public void tickSecond() {
      Performance.record(this.manager.data.getLevel().tickManager(), "forestryJobs", (Runnable)(() -> {
         Object var1;
         if (this.lastProcessingTile == null) {
            var1 = this.zoning.getTiles();
         } else {
            var1 = this.zoning.getTiles().tailSet(this.lastProcessingTile, false);
         }

         this.iterations += (float)this.size() / 10.0F;
         Iterator var2 = ((SortedSet)var1).iterator();

         Point var3;
         while(var2.hasNext()) {
            var3 = (Point)var2.next();
            if (this.iterations < 1.0F) {
               break;
            }

            --this.iterations;
            this.lastProcessingTile = var3;
            this.handleTile(var3);
         }

         if (this.iterations > 1.0F) {
            var2 = this.zoning.getTiles().iterator();

            while(var2.hasNext()) {
               var3 = (Point)var2.next();
               if (this.iterations < 1.0F) {
                  break;
               }

               --this.iterations;
               this.lastProcessingTile = var3;
               this.handleTile(var3);
            }
         }

      }));
   }

   protected abstract void handleTile(Point var1);
}
