package necesse.level.maps.light;

public class ParticleLightMap extends LightMap {
   public ParticleLightMap(LightManager var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5, 15);
   }

   protected GameLight getNewLight(int var1, int var2) {
      return this.level.lightManager.getPLightUpdate(var1, var2);
   }
}
