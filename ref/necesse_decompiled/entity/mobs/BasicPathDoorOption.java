package necesse.entity.mobs;

import necesse.entity.mobs.ai.path.SemiRegionPathResult;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

public class BasicPathDoorOption extends PathDoorOption {
   public final boolean canBreakDown;
   public final boolean canOpen;
   public final boolean canClose;

   public BasicPathDoorOption(String var1, Level var2, boolean var3, boolean var4, boolean var5) {
      super(var1, var2);
      this.canBreakDown = var3;
      this.canOpen = var4;
      this.canClose = var5;
   }

   public BasicPathDoorOption(String var1, Level var2, boolean var3, boolean var4) {
      this(var1, var2, false, var3, var4);
   }

   public SemiRegionPathResult canPathThrough(SemiRegion var1) {
      if (this.canBreakDown) {
         return SemiRegionPathResult.VALID;
      } else if (var1.getType().isDoor) {
         return this.canOpen ? SemiRegionPathResult.VALID : SemiRegionPathResult.CHECK_EACH_TILE;
      } else {
         return var1.getType().isSolid ? SemiRegionPathResult.INVALID : SemiRegionPathResult.VALID;
      }
   }

   public boolean canPathThroughCheckTile(SemiRegion var1, int var2, int var3) {
      GameObject var4 = this.level.getObject(var2, var3);
      if (!var4.isDoor) {
         return false;
      } else {
         return ((DoorObject)var4).isOpen(this.level, var2, var3, this.level.getObjectRotation(var2, var3)) || this.canOpen(var2, var3);
      }
   }

   public boolean canBreakDown(int var1, int var2) {
      return this.canBreakDown;
   }

   public boolean canOpen(int var1, int var2) {
      return this.canOpen;
   }

   public boolean canClose(int var1, int var2) {
      return this.canClose;
   }

   public boolean doorChangeInvalidatesCache(DoorObject var1, DoorObject var2, int var3, int var4) {
      return !this.canOpen(var3, var4) && !this.canBreakDown(var3, var4);
   }
}
