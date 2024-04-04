package necesse.engine;

import necesse.gfx.GameMusic;

public class MusicOptions {
   public final GameMusic music;
   protected int fadeInMillis;
   protected int fadeOutMillis;
   protected float volume = 1.0F;
   protected int previousFadeoutMaxMillis = -1;

   public MusicOptions(GameMusic var1) {
      this.music = var1;
      this.fadeInMillis = var1.fadeInMillis;
      this.fadeOutMillis = var1.fadeOutMillis;
   }

   public MusicOptions fadeInTime(int var1) {
      this.fadeInMillis = var1;
      return this;
   }

   public int getFadeInTime() {
      return this.fadeOutMillis;
   }

   public MusicOptions fadeOutTime(int var1) {
      this.fadeOutMillis = var1;
      return this;
   }

   public int getFadeOutTime() {
      return this.fadeOutMillis;
   }

   public MusicOptions forcePreviousMaxFadeout(int var1) {
      this.previousFadeoutMaxMillis = var1;
      return this;
   }

   public MusicOptions volume(float var1) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException("Volume cannot be below 0");
      } else {
         this.volume = var1;
         return this;
      }
   }

   public float getVolume() {
      return this.volume;
   }
}
