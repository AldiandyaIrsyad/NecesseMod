package necesse.gfx.shader;

import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class WaveShader extends GameShader {
   public WaveShader() {
      super("vert", "fragWave");
   }

   public WaveState setupGrassWave(Level var1, int var2, int var3, long var4, final float var6, final float var7, int var8, final float var9, GameRandom var10, long var11) {
      long var13 = var1.grassWeave(var2, var3);
      if (var13 > 0L) {
         synchronized(var10) {
            if (var10.seeded(var11).nextBoolean()) {
               var13 = -var13;
            }
         }

         final float var15 = (float)var13 / ((float)var4 / (float)var8 / 3.1415927F);
         return new WaveState() {
            public void start() {
               WaveShader.this.use();
               WaveShader.this.pass1f("bendAmount", var6);
               WaveShader.this.pass1f("bendHeight", var7);
               WaveShader.this.pass1f("bendTime", var15);
               WaveShader.this.pass1f("heightOffset", var9);
            }

            public void end() {
               WaveShader.this.stop();
            }
         };
      } else {
         return null;
      }
   }

   public WaveState setupGrassWave(Level var1, int var2, int var3, long var4, float var6, float var7, GameRandom var8, long var9) {
      return this.setupGrassWave(var1, var2, var3, var4, var6, var7, 2, 0.0F, var8, var9);
   }

   public WaveState setupContinuousWave(Level var1, int var2, int var3, int var4, final float var5, final float var6, GameRandom var7, long var8) {
      int var10 = 0;
      if (var7 != null) {
         synchronized(var7) {
            var10 = var7.nextInt(var4);
         }
      }

      final float var11 = (float)var1.getWorldEntity().getLocalTime() / (float)var4 + (float)var10;
      return new WaveState() {
         public void start() {
            WaveShader.this.use();
            WaveShader.this.pass1f("bendAmount", var5);
            WaveShader.this.pass1f("bendHeight", var6);
            WaveShader.this.pass1f("bendTime", var11);
            WaveShader.this.pass1f("heightOffset", 0.0F);
         }

         public void end() {
            WaveShader.this.stop();
         }
      };
   }

   public abstract static class WaveState {
      public WaveState() {
      }

      public abstract void start();

      public abstract void end();
   }
}
