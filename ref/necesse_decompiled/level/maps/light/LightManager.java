package necesse.level.maps.light;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.Settings;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;

public class LightManager {
   public static final int fullLight = 255;
   public static final int maxLight = 150;
   public static final int oneThirdsLight = 50;
   public static final int twoThirdsLight = 100;
   public static final int solidObjectMod = 40;
   public static final int airMod = 10;
   public static final int lightDistance = 25;
   public static final float insideLightMod = 0.7F;
   public static final int pLightFadeTime = 750;
   public static final int pLightStartLevel = 255;
   public static final int pLightDistance = 15;
   public Settings.LightSetting setting;
   protected ExecutorService updateExecutor;
   private static final int COMPUTE_THREADS = 4;
   protected ThreadPoolExecutor computeExecutor;
   public final Level level;
   private final LightMapInterface lightLevels;
   private LightMapInterface particleLightLevels;
   private final GameParticleLight[][] pLights;
   public GameLight ambientLightOverride;
   public GameLight ambientLight;

   public LightManager(Level var1) {
      this.setting = Settings.lights;
      this.ambientLightOverride = null;
      this.ambientLight = this.newLight(0.0F);
      synchronized(this) {
         this.level = var1;
         this.updateExecutor = Executors.newSingleThreadExecutor(this.defaultThreadFactory("update", false));
         this.computeExecutor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), this.defaultThreadFactory("compute", true));
         this.pLights = new GameParticleLight[var1.width][var1.height];
         this.lightLevels = new StaticLightMap(this, 0, 0, var1.width - 1, var1.height - 1);
         this.particleLightLevels = new FastParticleLightMap(this, 0, 0, var1.width - 1, var1.height - 1);
      }
   }

   public void onLoadingComplete() {
      this.updateStaticLight(0, 0, this.level.width - 1, this.level.height - 1, false);
   }

   public Settings.LightSetting getCurrentSetting() {
      return this.setting;
   }

   public void ensureSetting(Settings.LightSetting var1) {
      if (this.setting != var1) {
         this.setting = var1;
         this.updateStaticLight(0, 0, this.level.width - 1, this.level.height - 1, true);
         synchronized(this.pLights) {
            GameParticleLight[][] var3 = this.pLights;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               GameParticleLight[] var6 = var3[var5];
               Arrays.fill(var6, (Object)null);
            }
         }
      }

   }

   private ThreadFactory defaultThreadFactory(String var1, boolean var2) {
      AtomicInteger var3 = new AtomicInteger(0);
      return (var4) -> {
         return new Thread((ThreadGroup)null, var4, "level-" + this.level.getHostString() + "-" + this.level.getIdentifier() + "-light-" + var1 + (var2 ? "-" + var3.incrementAndGet() : ""));
      };
   }

   public GameLight newLight(float var1) {
      return (GameLight)(this.setting == Settings.LightSetting.Color ? new GameLightColor(var1) : new GameLight(var1));
   }

   public GameLight newLight(float var1, float var2, float var3) {
      return (GameLight)(this.setting == Settings.LightSetting.Color ? new GameLightColor(var1, var2, var3) : new GameLight(var3));
   }

   public GameLight newLight(Color var1, float var2, float var3) {
      return (GameLight)(this.setting == Settings.LightSetting.Color ? GameLightColor.fromColor(var1, var2, var3) : new GameLight(var3));
   }

   public float getAmbientLight() {
      return this.ambientLight.getLevel();
   }

   public void updateAmbientLight() {
      if (this.ambientLightOverride != null) {
         this.ambientLight = this.ambientLightOverride;
      } else if (Settings.alwaysLight) {
         this.ambientLight = this.newLight(150.0F);
      } else {
         if (this.level.isCave) {
            this.ambientLight = this.newLight(0.0F, 0.0F, 0.0F);
         } else {
            float var1 = this.level.getWorldEntity().getAmbientLight();
            float var2 = 1.0F;
            if (Settings.brightness > 0.7F) {
               var2 *= GameMath.lerp((float)Math.pow((double)(Settings.brightness - 0.7F), 0.30000001192092896), 1.0F, 0.4F);
            }

            float var3 = 150.0F / (10.0F * var2);
            if (var1 < var3) {
               var1 = var3;
            }

            float var4 = Math.abs(var1 / 150.0F - 1.0F);
            var4 = (float)Math.pow((double)var4, 2.0);
            this.ambientLight = this.newLight(240.0F, var4 * 0.85F, var1);
         }

      }
   }

   public void setParticleLights(LightMapInterface var1) {
      this.particleLightLevels = var1;
   }

   public List<SourcedGameLight> getStaticLightSources(int var1, int var2) {
      synchronized(this) {
         return this.lightLevels.getLightSources(var1, var2);
      }
   }

   public GameLight getStaticLight(int var1, int var2) {
      synchronized(this) {
         return this.lightLevels.getLight(var1, var2);
      }
   }

   public GameLight getPLightUpdate(int var1, int var2) {
      var1 = GameMath.limit(var1, 0, this.level.width - 1);
      var2 = GameMath.limit(var2, 0, this.level.height - 1);
      synchronized(this.pLights) {
         GameParticleLight var4 = this.pLights[var1][var2];
         if (var4 == null) {
            return this.newLight(0.0F);
         } else {
            var4.updateLevel(this.level.getWorldEntity().getLocalTime());
            return var4.light;
         }
      }
   }

   public GameLight getParticleLight(int var1, int var2) {
      synchronized(this) {
         return this.particleLightLevels.getLight(var1, var2);
      }
   }

   public void refreshParticleLight(int var1, int var2) {
      this.refreshParticleLight(var1, var2, 100);
   }

   public void refreshParticleLight(int var1, int var2, int var3) {
      this.refreshParticleLight(var1, var2, this.newLight(0.0F), var3);
   }

   public void refreshParticleLight(int var1, int var2, float var3, float var4) {
      this.refreshParticleLight(var1, var2, var3, var4, 100);
   }

   public void refreshParticleLight(int var1, int var2, float var3, float var4, int var5) {
      this.refreshParticleLight(var1, var2, this.newLight(var3, var4, 0.0F), var5);
   }

   public void refreshParticleLight(int var1, int var2, Color var3, float var4) {
      this.refreshParticleLight(var1, var2, var3, var4, 100);
   }

   public void refreshParticleLight(int var1, int var2, Color var3, float var4, int var5) {
      this.refreshParticleLight(var1, var2, this.newLight(var3, var4, 0.0F), var5);
   }

   private void refreshParticleLight(int var1, int var2, GameLight var3, int var4) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         synchronized(this.pLights) {
            long var6 = this.level.getLocalTime();
            int var8 = (int)(750.0F * ((float)var4 / 255.0F));
            long var9 = var6 + (long)var8;
            GameParticleLight var11 = this.pLights[var1][var2];
            var3.setLevel((float)var4);
            if (var11 == null) {
               var11 = new GameParticleLight(this.newLight(0.0F));
               var11.light.combine(var3);
               var11.endTime = Math.max(var11.endTime, var9);
               this.pLights[var1][var2] = var11;
            } else {
               var11.updateLevel(var6);
               var11.light.combine(var3);
               var11.endTime = Math.max(var11.endTime, var9);
            }

         }
      }
   }

   public void refreshParticleLightFloat(float var1, float var2) {
      this.refreshParticleLightFloat(var1, var2, 100);
   }

   public void refreshParticleLightFloat(float var1, float var2, int var3) {
      this.refreshParticleLightFloat(var1, var2, this.newLight(0.0F), var3);
   }

   public void refreshParticleLightFloat(float var1, float var2, float var3, float var4) {
      this.refreshParticleLightFloat(var1, var2, var3, var4, 100);
   }

   public void refreshParticleLightFloat(float var1, float var2, float var3, float var4, int var5) {
      this.refreshParticleLightFloat(var1, var2, this.newLight(var3, var4, 0.0F), var5);
   }

   public void refreshParticleLightFloat(float var1, float var2, Color var3, float var4) {
      this.refreshParticleLightFloat(var1, var2, var3, var4, 100);
   }

   public void refreshParticleLightFloat(float var1, float var2, Color var3, float var4, int var5) {
      this.refreshParticleLightFloat(var1, var2, this.newLight(var3, var4, 0.0F), var5);
   }

   private void refreshParticleLightFloat(float var1, float var2, GameLight var3, int var4) {
      this.refreshParticleLight((int)(var1 / 32.0F), (int)(var2 / 32.0F), var3, var4);
   }

   public GameLight getLightLevelWall(int var1, int var2) {
      GameLight var3 = this.ambientLight.copy();
      if (!this.level.isCave && !this.level.isOutside(var1, var2) && !this.level.isOutside(var1 - 1, var2) && !this.level.isOutside(var1 + 1, var2) && !this.level.isOutside(var1, var2 - 1) && !this.level.isOutside(var1, var2 + 1)) {
         var3.setLevel(var3.getLevel() * 0.7F);
      }

      var3.combine(this.getStaticLight(var1, var2));
      var3.combine(this.getParticleLight(var1, var2));
      return var3;
   }

   public GameLight getAmbientAndStaticLightLevel(int var1, int var2) {
      GameLight var3 = this.ambientLight.copy();
      if (!this.level.isCave && !this.level.isOutside(var1, var2)) {
         var3.setLevel(var3.getLevel() * 0.7F);
      }

      var3.combine(this.getStaticLight(var1, var2));
      return var3;
   }

   public GameLight getLightLevel(int var1, int var2) {
      GameLight var3 = this.getAmbientAndStaticLightLevel(var1, var2);
      var3.combine(this.getParticleLight(var1, var2));
      return var3;
   }

   public void updateStaticLight(int var1, int var2) {
      synchronized(this) {
         this.lightLevels.update(var1, var2, true);
      }
   }

   public void updateStaticLight(int var1, int var2, int var3, int var4, boolean var5) {
      synchronized(this) {
         this.lightLevels.update(var1, var2, var3, var4, var5);
      }
   }

   public void updateStaticLight(Iterable<Point> var1, boolean var2) {
      synchronized(this) {
         this.lightLevels.update(var1, var2);
      }
   }

   public void dispose() {
      if (this.updateExecutor != null) {
         this.updateExecutor.shutdownNow();
      }

      this.updateExecutor = null;
      if (this.computeExecutor != null) {
         this.computeExecutor.shutdownNow();
      }

      this.computeExecutor = null;
   }

   public boolean isDisposed() {
      return this.updateExecutor == null || this.computeExecutor == null;
   }
}
