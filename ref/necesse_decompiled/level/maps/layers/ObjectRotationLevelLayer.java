package necesse.level.maps.layers;

import necesse.level.maps.Level;

public class ObjectRotationLevelLayer extends ByteLevelLayer {
   public ObjectRotationLevelLayer(Level var1) {
      super(var1);
   }

   public void init() {
   }

   public void onLoadingComplete() {
   }

   public byte getObjectRotation(int var1, int var2) {
      return this.get(var1, var2);
   }

   public void setObjectRotation(int var1, int var2, int var3) {
      this.set(var1, var2, (byte)((var3 % 4 + 4) % 4));
   }

   protected boolean isValidRegionValue(int var1, int var2, byte var3) {
      return true;
   }
}
