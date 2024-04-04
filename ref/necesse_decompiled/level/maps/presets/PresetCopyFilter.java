package necesse.level.maps.presets;

public class PresetCopyFilter {
   public boolean acceptTiles = true;
   public boolean acceptObjects = true;
   public boolean acceptWires = true;

   public PresetCopyFilter() {
   }

   public PresetCopyFilter acceptTiles() {
      this.acceptTiles = true;
      return this;
   }

   public PresetCopyFilter ignoreTiles() {
      this.acceptTiles = false;
      return this;
   }

   public PresetCopyFilter acceptObjects() {
      this.acceptObjects = true;
      return this;
   }

   public PresetCopyFilter ignoreObjects() {
      this.acceptObjects = false;
      return this;
   }

   public PresetCopyFilter acceptWires() {
      this.acceptWires = true;
      return this;
   }

   public PresetCopyFilter ignoreWires() {
      this.acceptWires = false;
      return this;
   }
}
