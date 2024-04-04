package necesse.level.maps.presets;

public enum PresetRotation {
   CLOCKWISE(1),
   ANTI_CLOCKWISE(3),
   HALF_180(2);

   public final int dirOffset;

   private PresetRotation(int var3) {
      this.dirOffset = var3;
   }

   public static PresetRotation toRotationAngle(int var0) {
      var0 %= 4;
      if (var0 == 0) {
         return null;
      } else if (var0 != 3 && var0 != -1) {
         return var0 != -3 && var0 != 1 ? HALF_180 : CLOCKWISE;
      } else {
         return ANTI_CLOCKWISE;
      }
   }

   public static PresetRotation addRotations(PresetRotation var0, PresetRotation var1) {
      if (var0 == null) {
         return var1;
      } else {
         return var1 == null ? var0 : toRotationAngle(var0.dirOffset + var1.dirOffset);
      }
   }

   // $FF: synthetic method
   private static PresetRotation[] $values() {
      return new PresetRotation[]{CLOCKWISE, ANTI_CLOCKWISE, HALF_180};
   }
}
