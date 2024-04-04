package necesse.level.maps.light;

public class FastStaticLightMap extends FastLightMap {
   public FastStaticLightMap(LightManager var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5, 25);
   }

   protected GameLight getNewLight(int var1, int var2) {
      GameLight var3 = this.level.getLevelObject(var1, var2).getLight();
      var3.combine(this.level.getTile(var1, var2).getLight(this.level));
      return var3;
   }
}
