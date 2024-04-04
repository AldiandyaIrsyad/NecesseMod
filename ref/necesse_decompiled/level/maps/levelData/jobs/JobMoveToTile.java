package necesse.level.maps.levelData.jobs;

import java.util.Objects;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.mobMovement.MobMovement;

public class JobMoveToTile {
   public final MobMovement custom;
   public final int tileX;
   public final int tileY;
   public final boolean acceptAdjacentTiles;
   public final int maxPathIterations;

   public JobMoveToTile(int var1, int var2, boolean var3, int var4) {
      this.custom = null;
      this.tileX = var1;
      this.tileY = var2;
      this.acceptAdjacentTiles = var3;
      this.maxPathIterations = var4;
   }

   public JobMoveToTile(int var1, int var2, boolean var3) {
      this(var1, var2, var3, HumanMob.defaultJobPathIterations);
   }

   public JobMoveToTile(MobMovement var1) {
      this.custom = var1;
      this.tileX = -1;
      this.tileY = -1;
      this.acceptAdjacentTiles = false;
      this.maxPathIterations = 0;
   }

   public boolean equals(JobMoveToTile var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!Objects.equals(this.custom, var1.custom)) {
         return false;
      } else if (this.custom != null) {
         return true;
      } else {
         return this.tileX == var1.tileX && this.tileY == var1.tileY && this.acceptAdjacentTiles == var1.acceptAdjacentTiles;
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof JobMoveToTile ? this.equals((JobMoveToTile)var1) : super.equals(var1);
   }
}
