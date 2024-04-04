package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

public class PirateVillagePathPreset extends PirateVillagePreset {
   public PirateVillagePathPreset() {
      this(true, true, true, true);
   }

   public PirateVillagePathPreset(boolean var1, boolean var2, boolean var3, boolean var4) {
      super(1, 1, true);
      this.fillTile(0, 0, this.width, this.height, this.path);
      this.fillObject(0, 0, this.width, this.height, 0);
      if (var1) {
         this.open(0, 0, 0);
      }

      if (var2) {
         this.open(0, 0, 1);
      }

      if (var3) {
         this.open(0, 0, 2);
      }

      if (var4) {
         this.open(0, 0, 3);
      }

   }
}
