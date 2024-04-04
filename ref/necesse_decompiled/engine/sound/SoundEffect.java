package necesse.engine.sound;

import java.nio.FloatBuffer;
import necesse.engine.Settings;
import org.lwjgl.BufferUtils;

public abstract class SoundEffect {
   protected float volume = 1.0F;

   public SoundEffect() {
   }

   public abstract void updateSound(SoundPlayer var1, int var2, float var3, SoundEmitter var4);

   public SoundEffect volume(float var1) {
      this.volume = var1;
      return this;
   }

   public float getVolume() {
      return this.volume;
   }

   public abstract PrimitiveSoundEmitter getEmitter();

   protected static FloatBuffer getSoundPosition(float[] var0) {
      return BufferUtils.createFloatBuffer(3).put(var0).rewind();
   }

   public static GlobalSoundEffect music() {
      return new GlobalSoundEffect(() -> {
         return Settings.masterVolume * Settings.musicVolume * 0.75F;
      });
   }

   public static GlobalSoundEffect ui() {
      return new GlobalSoundEffect(() -> {
         return Settings.masterVolume * Settings.UIVolume;
      });
   }

   public static GlobalSoundEffect globalEffect() {
      return new GlobalSoundEffect(() -> {
         return Settings.masterVolume * Settings.effectsVolume;
      });
   }

   public static PositionSoundEffect effect(PrimitiveSoundEmitter var0) {
      return new PositionSoundEffect(() -> {
         return Settings.masterVolume * Settings.effectsVolume;
      }, var0);
   }

   public static PositionSoundEffect effect(float var0, float var1) {
      return effect(SoundPlayer.SimpleEmitter(var0, var1));
   }
}
