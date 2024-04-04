package necesse.engine.sound;

import java.awt.geom.Point2D;
import java.util.function.Supplier;
import org.lwjgl.openal.AL10;

public class PositionSoundEffect extends GlobalSoundEffect {
   protected int minDistance = 75;
   protected int listenDistance = 750;
   protected float falloffExponent = 0.01F;
   protected PrimitiveSoundEmitter emitter;

   PositionSoundEffect(Supplier<Float> var1, PrimitiveSoundEmitter var2) {
      super(var1);
      this.emitter = var2;
   }

   public PositionSoundEffect pitch(float var1) {
      super.pitch(var1);
      return this;
   }

   public PositionSoundEffect volume(float var1) {
      super.volume(var1);
      return this;
   }

   public PositionSoundEffect minFalloffDistance(int var1) {
      this.minDistance = var1;
      return this;
   }

   public PositionSoundEffect falloffDistance(int var1) {
      this.listenDistance = var1;
      return this;
   }

   public PositionSoundEffect falloffExponent(float var1) {
      this.falloffExponent = var1;
      return this;
   }

   public PositionSoundEffect emitter(PrimitiveSoundEmitter var1) {
      this.emitter = var1;
      return this;
   }

   public static float getGain(float var0, float var1, int var2, int var3) {
      return var1 < 0.0F ? 0.0F : (float)Math.pow((double)var0, (double)Math.max(0.0F, (var1 - (float)var2) / (float)var3));
   }

   public void updateSound(SoundPlayer var1, int var2, float var3, SoundEmitter var4) {
      PrimitiveSoundEmitter var5 = this.getEmitter();
      float var6 = (Float)this.settingVolumeMod.get() * this.volume * var3;
      AL10.alSourcef(var2, 4099, this.pitch);
      float var7 = var5.getSoundDistance(var4.getSoundPositionX(), var4.getSoundPositionY());
      float var8 = getGain(this.falloffExponent, var7, this.minDistance, this.listenDistance);
      AL10.alSourcef(var2, 4106, var6 * var8);
      Point2D.Float var9 = var5.getSoundDirection(var4.getSoundPositionX(), var4.getSoundPositionY());
      if (var9 != null && var7 > (float)this.minDistance) {
         float var10 = 1.0F;
         if (var7 - (float)this.minDistance < 150.0F) {
            var10 = (var7 - (float)this.minDistance) / 150.0F;
         }

         AL10.alSourcefv(var2, 4100, getSoundPosition(new float[]{var9.x * var10, var9.y * var10, 1.0F}));
      } else {
         AL10.alSourcefv(var2, 4100, getSoundPosition(new float[]{0.0F, 0.0F, 1.0F}));
      }

   }

   public PrimitiveSoundEmitter getEmitter() {
      return this.emitter;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GlobalSoundEffect volume(float var1) {
      return this.volume(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GlobalSoundEffect pitch(float var1) {
      return this.pitch(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public SoundEffect volume(float var1) {
      return this.volume(var1);
   }
}
