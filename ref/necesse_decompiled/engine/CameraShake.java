package necesse.engine;

import java.awt.geom.Point2D;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.util.GameRandom;

public class CameraShake {
   private final GameRandom random;
   public final long startTime;
   public final int duration;
   public final int frequency;
   private final int samples;
   public final float xIntensity;
   public final float yIntensity;
   private final float[] xValues;
   private final float[] yValues;
   public PrimitiveSoundEmitter emitter;
   public int minDistance;
   public int listenDistance;
   public float falloffExponent;
   private boolean removed;

   public CameraShake(long var1, int var3, int var4, float var5, float var6, boolean var7, Point2D.Float var8) {
      this.random = new GameRandom();
      this.minDistance = 50;
      this.listenDistance = 1500;
      this.falloffExponent = 0.01F;
      this.startTime = var1;
      this.duration = var3;
      this.frequency = var4;
      this.xIntensity = var5;
      this.yIntensity = var6;
      this.samples = var3 / var4 + 2;
      this.xValues = this.generateValues(var5, var8.x, var7);
      this.yValues = this.generateValues(var6, var8.y, var7);
   }

   public CameraShake(long var1, int var3, int var4, float var5, float var6, boolean var7) {
      this(var1, var3, var4, var5, var6, var7, new Point2D.Float());
   }

   public CameraShake from(PrimitiveSoundEmitter var1) {
      this.emitter = var1;
      return this;
   }

   public CameraShake from(PrimitiveSoundEmitter var1, int var2, int var3) {
      this.emitter = var1;
      this.minDistance = var2;
      this.listenDistance = var3;
      return this;
   }

   public CameraShake from(PrimitiveSoundEmitter var1, int var2, int var3, float var4) {
      this.emitter = var1;
      this.minDistance = var2;
      this.listenDistance = var3;
      this.falloffExponent = var4;
      return this;
   }

   private float[] generateValues(float var1, float var2, boolean var3) {
      float[] var4 = new float[this.samples];

      for(int var5 = 1; var5 < var4.length - 1; ++var5) {
         float var6 = 1.0F;
         if (var3) {
            var6 = Math.abs((float)var5 / (float)var4.length - 1.0F);
         }

         var4[var5] = (float)this.random.nextGaussian() * var1 * var6;
      }

      var4[0] = var2;
      var4[this.samples - 1] = 0.0F;
      return var4;
   }

   public Point2D.Float getCurrentShake(long var1) {
      long var3 = var1 - this.startTime;
      if (var3 >= 0L && var3 < (long)this.duration) {
         float var5 = (float)var3 / (float)this.duration;
         int var6 = (int)(var5 * (float)(this.samples - 1));
         Point2D.Float var7 = new Point2D.Float(this.xValues[var6], this.yValues[var6]);
         Point2D.Float var8 = new Point2D.Float(this.xValues[var6 + 1], this.yValues[var6 + 1]);
         float var9 = (float)this.duration / (float)(this.samples - 1);
         float var10 = ((float)var3 - (float)var6 * var9) / var9;
         float var11 = (float)((Math.cos(Math.PI * (double)(1.0F + var10)) + 1.0) / 2.0);
         return new Point2D.Float(var7.x + (var8.x - var7.x) * var11, var7.y + (var8.y - var7.y) * var11);
      } else {
         return new Point2D.Float();
      }
   }

   public float getDistanceIntensity(SoundEmitter var1) {
      float var2 = 0.0F;
      if (this.emitter != null && var1 != null) {
         var2 = this.emitter.getSoundDistance(var1.getSoundPositionX(), var1.getSoundPositionY());
      }

      return PositionSoundEffect.getGain(this.falloffExponent, var2, this.minDistance, this.listenDistance);
   }

   public Point2D.Float getCurrentShake(long var1, SoundEmitter var3) {
      Point2D.Float var4 = this.getCurrentShake(var1);
      float var5 = this.getDistanceIntensity(var3);
      return new Point2D.Float(var4.x * var5, var4.y * var5);
   }

   public void remove() {
      this.removed = true;
   }

   public boolean isOver(long var1) {
      return this.removed || var1 - this.startTime >= (long)this.duration;
   }
}
