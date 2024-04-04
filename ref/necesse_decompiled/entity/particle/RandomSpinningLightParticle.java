package necesse.entity.particle;

import java.awt.Color;
import necesse.level.maps.Level;

public class RandomSpinningLightParticle extends RandomSpinningParticle {
   public RandomSpinningLightParticle(Level var1, int var2, int var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public RandomSpinningLightParticle(Level var1, int var2, int var3, float var4, float var5, float var6, float var7, int var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public RandomSpinningLightParticle(Level var1, int var2, int var3, float var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public RandomSpinningLightParticle(Level var1, Color var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public RandomSpinningLightParticle(Level var1, Color var2, float var3, float var4, float var5, float var6, int var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y);
   }
}
