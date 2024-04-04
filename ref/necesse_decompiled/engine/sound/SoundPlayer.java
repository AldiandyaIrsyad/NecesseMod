package necesse.engine.sound;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameSound.GameSoundStreamer;
import org.lwjgl.openal.AL10;

public class SoundPlayer {
   public final GameSound gameSound;
   private GameSoundStreamer streamer;
   private int maxLoadedSamples;
   private int maxQueuedSamples;
   private int startSamples;
   private long startTime;
   public SoundEffect effect;
   private boolean playing;
   private boolean disposed;
   private int source;
   private int pauseSamplesPosition;
   private float fadeInVolume;
   private float fadeInSeconds = -1.0F;
   private float loopFadeVolume;
   private float loopFadeSeconds = -1.0F;

   public SoundPlayer(GameSound var1, SoundEffect var2) {
      this.gameSound = var1;
      this.streamer = var1.getStreamer();
      this.effect = var2;
   }

   public void playSound() {
      this.playSound(0.0F);
   }

   public void playSound(float var1) {
      if (!this.disposed) {
         if (this.source != 0) {
            AL10.alSourceStop(this.source);
            AL10.alDeleteSources(this.source);
         }

         this.source = AL10.alGenSources();
         AL10.alSourcei(this.source, 514, 1);
         AL10.alSourcef(this.source, 4131, 0.0F);
         this.startSamples = (int)(var1 * (float)this.streamer.getSampleRate());
         this.maxLoadedSamples = this.startSamples;
         this.maxQueuedSamples = this.startSamples;
         this.startTime = System.currentTimeMillis();
         this.update();
         AL10.alSourcePlay(this.source);
         this.playing = true;
      }
   }

   public SoundPlayer refreshLooping(float var1) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException("fadeTimeSeconds must be above 0");
      } else {
         this.loopFadeSeconds = var1;
         float var2 = 1.0F / this.loopFadeSeconds / 20.0F;
         this.loopFadeVolume = 1.0F + var2;
         AL10.alSourcei(this.source, 4103, 1);
         return this;
      }
   }

   public SoundPlayer fadeIn(float var1) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException("fadeInTimeSeconds must be above 0");
      } else {
         this.fadeInSeconds = var1;
         this.fadeInVolume = 0.0F;
         return this;
      }
   }

   public boolean isDone() {
      if (!this.playing) {
         return false;
      } else {
         boolean var1;
         if (this.loopFadeSeconds > 0.0F) {
            var1 = this.loopFadeVolume <= 0.0F;
         } else {
            var1 = AL10.alGetSourcei(this.source, 4112) == 4116;
         }

         return var1 && !this.streamer.isWorking() && this.streamer.isDone(this.maxLoadedSamples);
      }
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public int getPositionSamples() {
      if (!this.isPlaying()) {
         return this.pauseSamplesPosition;
      } else {
         long var1 = System.currentTimeMillis() - this.startTime;
         return this.startSamples + (int)((float)var1 / 1000.0F * (float)this.streamer.getSampleRate());
      }
   }

   public float getPositionSeconds() {
      return (float)this.getPositionSamples() / (float)this.streamer.getSampleRate();
   }

   private String state() {
      int var1 = AL10.alGetSourcei(this.source, 4112);
      switch (var1) {
         case 4113:
            return "AL_INITIAL";
         case 4114:
            return "AL_PLAYING";
         case 4115:
            return "AL_PAUSED";
         case 4116:
            return "AL_STOPPED";
         default:
            return "N/A";
      }
   }

   public static String getError() {
      int var0 = AL10.alGetError();
      switch (var0) {
         case 40961:
            return "AL_INVALID_NAME";
         case 40962:
            return "AL_INVALID_ENUM";
         case 40963:
            return "AL_INVALID_VALUE";
         case 40964:
            return "AL_INVALID_OPERATION";
         case 40965:
            return "AL_OUT_OF_MEMORY";
         default:
            return null;
      }
   }

   private void updateStreamer() {
      if (!this.streamer.isWorking() && !this.streamer.isDone(this.maxLoadedSamples)) {
         int var1 = this.streamer.getSampleRate() * 5;
         if (this.getPositionSamples() > this.maxQueuedSamples - var1) {
            this.streamer.getNextBuffers(this.maxQueuedSamples, var1, (var1x, var2, var3) -> {
               if (!this.isDisposed()) {
                  AL10.alSourceQueueBuffers(this.source, var1x);
                  if (this.isPlaying() && AL10.alGetSourcei(this.source, 4112) == 4116) {
                     AL10.alSourcePlay(this.source);
                  }

                  this.maxLoadedSamples = var2 + var3;
               }
            });
            this.maxQueuedSamples += var1;
         }
      }

   }

   public void update() {
      if (this.source != 0) {
         this.updateStreamer();
         float var1 = 1.0F;
         if (Settings.muteOnFocusLoss && !Screen.isFocused()) {
            var1 = 0.0F;
         } else {
            float var2;
            if (this.fadeInSeconds > 0.0F) {
               var2 = 1.0F / this.fadeInSeconds / 20.0F;
               var1 *= this.fadeInVolume;
               this.fadeInVolume = Math.min(this.fadeInVolume + var2, 1.0F);
            }

            if (this.loopFadeSeconds > 0.0F) {
               var2 = 1.0F / this.loopFadeSeconds / 20.0F;
               var1 *= this.loopFadeVolume;
               this.loopFadeVolume = Math.max(0.0F, this.loopFadeVolume - var2);
            }
         }

         this.effect.updateSound(this, this.source, var1 * this.gameSound.getVolumeModifier(), Screen.getALListener());
      }
   }

   public void setPosition(float var1) {
      if (!this.disposed) {
         this.playSound(var1);
      }
   }

   public float getLengthInSeconds() {
      return this.streamer.getLengthInSeconds();
   }

   public float getSecondsLeft() {
      return this.getLengthInSeconds() - this.getPositionSeconds();
   }

   public void startOver() {
      this.playSound(0.0F);
   }

   public void pause() {
      if (!this.disposed && this.playing) {
         AL10.alSourcePause(this.source);
         this.pauseSamplesPosition = this.getPositionSamples();
         this.playing = false;
      }
   }

   public void dispose() {
      if (!this.disposed) {
         this.disposed = true;
         AL10.alSourceStop(this.source);
         AL10.alDeleteSources(this.source);
         this.source = 0;
         this.streamer.dispose();
      }
   }

   public boolean isDisposed() {
      return this.disposed;
   }

   public static SoundEmitter SimpleEmitter(float var0, float var1) {
      return new SimpleSoundEmitter(var0, var1);
   }

   private static class SimpleSoundEmitter implements SoundEmitter {
      private float x;
      private float y;

      public SimpleSoundEmitter(float var1, float var2) {
         this.x = var1;
         this.y = var2;
      }

      public float getSoundPositionX() {
         return this.x;
      }

      public float getSoundPositionY() {
         return this.y;
      }
   }
}
