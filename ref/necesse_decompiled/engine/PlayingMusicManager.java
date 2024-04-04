package necesse.engine;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ComparableSequence;
import necesse.gfx.GameMusic;

public class PlayingMusicManager {
   public static int DEFAULT_FADE_OUT_TIME = 2000;
   public static int DEFAULT_FADE_IN_TIME = 5000;
   public static int MAX_MILLIS_MUSIC_DELTA_OFFSET = 500;
   private PlayingMusic currentMusic;
   private ArrayList<PlayingMusic> fadeOutMusic = new ArrayList();
   private AbstractMusicList currentMusicList;
   private ComparableSequence<Integer> nextMusicPriority;
   private boolean forceChangeMusic;

   public PlayingMusicManager() {
   }

   public void clearNextMusic() {
      this.nextMusicPriority = null;
   }

   public void tick() {
      MusicOptionsOffset var1 = null;
      String var2 = null;
      if (this.currentMusic == null || !this.currentMusic.player.isDone() && !(this.currentMusic.player.getSecondsLeft() < (float)this.currentMusic.options.fadeOutMillis / 1000.0F)) {
         if (this.currentMusicList != null && this.nextMusicPriority != null) {
            var1 = this.currentMusicList.getExpectedMusicPlaying();
            if (this.currentMusicList.shouldChangeTrack(this.currentMusic == null ? null : this.currentMusic.options)) {
               var2 = "changetrack";
            } else if (this.currentMusic != null && var1 != null && this.currentMusic.options.music != var1.options.music) {
               var2 = "expectedwrong";
            }
         }
      } else {
         var2 = "ended";
      }

      if (this.forceChangeMusic) {
         var2 = "forcechanged";
         this.forceChangeMusic = false;
      }

      if (var2 != null) {
         MusicOptionsOffset var3 = var1;
         if (this.currentMusicList != null && var1 == null) {
            MusicOptions var4 = this.currentMusicList.getNextMusic(this.currentMusic == null ? null : this.currentMusic.options);
            if (var4 != null) {
               var3 = new MusicOptionsOffset(var4, -1, -1L);
            }
         }

         if (var3 != null) {
            GameLog.debug.println("Now playing " + var3.options.music.filePath + " at " + (int)(var3.options.volume * 100.0F) + "% volume fading in over " + var3.options.fadeInMillis + "ms. Reason: " + var2);
            float var16 = this.currentMusic == null ? 0.0F : Math.max((float)this.currentMusic.options.fadeOutMillis / 1000.0F - this.currentMusic.player.getSecondsLeft(), 0.0F);
            if (this.currentMusic != null) {
               GameLog.debug.println("Fading " + this.currentMusic.options.music.filePath + " out over " + this.currentMusic.options.fadeOutMillis + "ms");
               this.fadeOutMusic.add(this.currentMusic);
            }

            int var5 = var3.options.previousFadeoutMaxMillis;
            if (var5 != -1) {
               this.fadeOutMusic.forEach((var1x) -> {
                  var1x.options.fadeOutMillis = Math.min(var1x.options.fadeOutMillis, var5);
               });
            }

            this.currentMusic = new PlayingMusic(var3.options, new SoundPlayer(var3.options.music.sound, SoundEffect.music()));
            this.currentMusic.player.effect.volume(0.0F);
            if (var3.offset != -1L) {
               long var6 = (long)((this.currentMusic.player.getLengthInSeconds() - (float)this.currentMusic.options.fadeOutMillis / 1000.0F) * 1000.0F);
               if (var6 > 0L) {
                  long var8 = Math.floorMod(var3.offset, var6);
                  long var10 = Math.floorMod((long)(var16 * 1000.0F) + var8, var6);
                  GameLog.debug.println("Offset music by " + (float)var10 / 1000.0F + " seconds");
                  this.currentMusic.player.playSound((float)var10 / 1000.0F);
               } else {
                  this.currentMusic.player.playSound();
               }
            } else {
               this.currentMusic.player.playSound();
            }
         } else {
            GameLog.debug.println("Ended music and nothing else to play. Reason: " + var2);
            if (this.currentMusic != null) {
               this.fadeOutMusic.add(this.currentMusic);
            }

            this.currentMusic = null;
         }
      }

      if (this.nextMusicPriority != null && this.currentMusic != null && var1 != null) {
         long var15 = var1.offset;
         if (var15 != -1L) {
            long var19 = (long)((this.currentMusic.player.getLengthInSeconds() - (float)this.currentMusic.options.fadeOutMillis / 1000.0F) * 1000.0F);
            long var7 = Math.floorMod(var15, var19);
            long var9 = (long)((double)this.currentMusic.player.getPositionSeconds() * 1000.0);
            long var11 = var15 - var9;
            if (Math.abs(var11) > (long)MAX_MILLIS_MUSIC_DELTA_OFFSET) {
               long var13 = Math.floorMod(var7, var19);
               GameLog.debug.println("Adjusted music offset to " + (float)var13 / 1000.0F + " seconds");
               this.currentMusic.player.setPosition((float)var13 / 1000.0F);
            }
         }
      }

      if (this.currentMusic != null && this.currentMusic.player.effect.getVolume() < this.currentMusic.options.volume) {
         if (this.currentMusic.options.fadeInMillis > 0) {
            double var17 = TickManager.getTickDelta((long)this.currentMusic.options.fadeInMillis);
            if (this.currentMusic.options.volume > 0.0F) {
               var17 *= (double)this.currentMusic.options.volume;
            }

            this.currentMusic.player.effect.volume(Math.min(this.currentMusic.player.effect.getVolume() + (float)var17, this.currentMusic.options.volume));
         } else {
            this.currentMusic.player.effect.volume(this.currentMusic.options.volume);
         }
      }

      for(int var20 = 0; var20 < this.fadeOutMusic.size(); ++var20) {
         PlayingMusic var18 = (PlayingMusic)this.fadeOutMusic.get(var20);
         if (var18.options.fadeOutMillis > 0) {
            double var21 = TickManager.getTickDelta((long)var18.options.fadeOutMillis);
            if (var18.options.volume > 0.0F) {
               var21 *= (double)var18.options.volume;
            }

            var18.player.effect.volume(var18.player.effect.getVolume() - (float)var21);
         } else {
            var18.player.effect.volume(0.0F);
         }

         if (var18.player.isDone() || var18.player.effect.getVolume() <= 0.0F) {
            var18.player.dispose();
            this.fadeOutMusic.remove(var20);
            --var20;
         }
      }

      if (this.currentMusic != null) {
         this.currentMusic.player.update();
      }

      this.fadeOutMusic.forEach((var0) -> {
         var0.player.update();
      });
   }

   public void updateVolume() {
      if (this.currentMusic != null) {
         this.currentMusic.player.update();
      }

      this.fadeOutMusic.forEach((var0) -> {
         var0.player.update();
      });
   }

   public int getPlayingCount() {
      return this.fadeOutMusic.size() + (this.currentMusic == null ? 0 : 1);
   }

   public GameMusic getCurrentMusic() {
      return this.currentMusic == null ? null : this.currentMusic.options.music;
   }

   public void dispose() {
      if (this.currentMusic != null) {
         this.currentMusic.player.dispose();
         this.currentMusic = null;
      }

      Iterator var1 = this.fadeOutMusic.iterator();

      while(var1.hasNext()) {
         PlayingMusic var2 = (PlayingMusic)var1.next();
         var2.player.dispose();
      }

      this.fadeOutMusic.clear();
   }

   public void forceChangeMusic() {
      this.forceChangeMusic = true;
   }

   public void setNextMusic(AbstractMusicList var1, ComparableSequence<Integer> var2) {
      if (this.nextMusicPriority == null || this.nextMusicPriority.compareTo(var2) < 0) {
         this.currentMusicList = var1;
         this.nextMusicPriority = var2;
      }

   }

   private static class PlayingMusic {
      public final MusicOptions options;
      public final SoundPlayer player;

      public PlayingMusic(MusicOptions var1, SoundPlayer var2) {
         this.options = var1;
         this.player = var2;
      }
   }
}
