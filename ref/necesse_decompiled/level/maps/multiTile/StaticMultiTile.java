package necesse.level.maps.multiTile;

import java.awt.Point;
import necesse.level.maps.presets.PresetRotation;

public class StaticMultiTile extends MultiTile {
   protected boolean allowRotation;

   public StaticMultiTile(int var1, int var2, int var3, int var4, int var5, boolean var6, int... var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
      this.allowRotation = true;
   }

   public StaticMultiTile(int var1, int var2, int var3, int var4, boolean var5, int... var6) {
      this(var1, var2, var3, var4, 0, var5, var6);
      this.allowRotation = false;
   }

   public Point getMirrorXPosOffset() {
      return new Point(-this.getCenterXOffset(), 0);
   }

   public Point getMirrorYPosOffset() {
      return new Point(0, -this.getCenterYOffset());
   }

   public int getXMirrorRotation() {
      return this.rotation;
   }

   public int getYMirrorRotation() {
      return this.rotation;
   }

   public Point getPresetRotationOffset(PresetRotation var1) {
      return this.allowRotation ? super.getPresetRotationOffset(var1) : null;
   }
}
