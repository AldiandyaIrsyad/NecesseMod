package necesse.level.maps.layers;

import necesse.level.maps.Level;

public class WireLevelLayer extends ByteLevelLayer {
   public WireLevelLayer(Level var1) {
      super(var1);
   }

   public void init() {
   }

   public void onLoadingComplete() {
   }

   public byte getWireData(int var1, int var2) {
      return this.get(var1, var2);
   }

   public void setWireData(int var1, int var2, byte var3) {
      this.set(var1, var2, var3);
   }

   protected boolean isValidRegionValue(int var1, int var2, byte var3) {
      return true;
   }
}
