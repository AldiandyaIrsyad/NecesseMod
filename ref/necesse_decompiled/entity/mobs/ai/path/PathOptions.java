package necesse.entity.mobs.ai.path;

import java.awt.Shape;
import java.util.function.Supplier;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class PathOptions {
   public final TilePathfinding.NodePriority nodePriority;

   public PathOptions(TilePathfinding.NodePriority var1) {
      this.nodePriority = var1;
   }

   public PathOptions() {
      this(TilePathfinding.NodePriority.TOTAL_COST);
   }

   public boolean canPassTile(TickManager var1, Level var2, Mob var3, PathDoorOption var4, CollisionFilter var5, int var6, int var7) {
      return var4.canPass(var6, var7);
   }

   public boolean checkCanPassDoorOrTile(TickManager var1, Level var2, Mob var3, PathDoorOption var4, CollisionFilter var5, int var6, int var7) {
      GameObject var8 = var2.getObject(var6, var7);
      if (var8.isDoor && var4.canPassDoor((DoorObject)var8, var6, var7)) {
         return true;
      } else {
         return var4.canBreakDown(var6, var7) || this.canPassTile(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public double getTileCost(Level var1, Mob var2, PathDoorOption var3, int var4, int var5) {
      double var6 = var1.getTile(var4, var5).getPathCost(var1, var4, var5, var2);
      GameObject var8 = var1.getObject(var4, var5);
      var6 += var8.getPathCost(var1, var4, var5);
      if (var3 == null) {
         return var6;
      } else if (var8.isDoor && var3.canOpen(var4, var5)) {
         return var6;
      } else {
         if (var8.isSolid(var1, var4, var5) && var3.canBreakDown(var4, var5)) {
            var6 += var8.getBreakDownPathCost(var1, var4, var5);
         }

         return var6;
      }
   }

   public boolean canMoveLine(TickManager var1, Level var2, Mob var3, CollisionFilter var4, int var5, int var6, int var7, int var8) {
      return (Boolean)Performance.record(var1, "canPassLine", (Supplier)(() -> {
         return !var2.collides((Shape)(new MovedRectangle(var3, var5, var6, var7, var8)), (CollisionFilter)var4);
      }));
   }
}
