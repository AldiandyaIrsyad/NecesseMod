package necesse.engine;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameMusic;

public class MusicList extends AbstractMusicList {
   private ArrayList<MusicOptions> musicList;
   private long totalMusicListMillis;
   private long timeOffset;

   public MusicList(long var1) {
      this.musicList = new ArrayList();
      this.totalMusicListMillis = 0L;
      this.timeOffset = var1;
   }

   public MusicList() {
      this(-1L);
   }

   public MusicList(long var1, GameMusic... var3) {
      this(var1);
      this.addMusic(var3);
   }

   public MusicList(GameMusic... var1) {
      this();
      this.addMusic(var1);
   }

   public MusicList(long var1, MusicOptions... var3) {
      this(var1);
      this.addMusic(var3);
   }

   public MusicList(MusicOptions... var1) {
      this();
      this.addMusic(var1);
   }

   public MusicOptions getNextMusic(MusicOptions var1) {
      MusicOptions[] var2 = (MusicOptions[])this.musicList.stream().filter((var1x) -> {
         return var1 == null || var1x.music != var1.music;
      }).toArray((var0) -> {
         return new MusicOptions[var0];
      });
      return var2.length == 0 ? (MusicOptions)GameRandom.globalRandom.getOneOf((List)this.musicList) : (MusicOptions)GameRandom.globalRandom.getOneOf((Object[])var2);
   }

   public MusicOptionsOffset getExpectedMusicPlaying() {
      if (this.timeOffset != -1L) {
         long var1 = Math.floorMod(this.timeOffset, this.totalMusicListMillis);

         for(int var3 = 0; var3 < this.musicList.size(); ++var3) {
            MusicOptions var4 = (MusicOptions)this.musicList.get(var3);
            long var5 = var4.music.sound.getLengthInMillis() - (long)var4.fadeOutMillis;
            if (var1 < var5) {
               return new MusicOptionsOffset(var4, var3, var1);
            }

            var1 -= var5;
         }
      }

      return null;
   }

   public boolean shouldChangeTrack(MusicOptions var1) {
      return var1 == null && !this.musicList.isEmpty() ? true : this.musicList.stream().noneMatch((var1x) -> {
         return var1x.music == var1.music;
      });
   }

   public Iterable<GameMusic> getMusicInList() {
      return GameUtils.mapIterable(this.musicList.iterator(), (var0) -> {
         return var0.music;
      });
   }

   public MusicList setTimeOffset(long var1) {
      this.timeOffset = var1;
      return this;
   }

   public MusicList addMusic(MusicOptions var1) {
      this.musicList.add(var1);
      if (var1.music != null && var1.music.sound != null) {
         this.totalMusicListMillis += var1.music.sound.getLengthInMillis() - (long)var1.fadeOutMillis;
      }

      return this;
   }

   public MusicList addMusic(MusicOptions... var1) {
      MusicOptions[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MusicOptions var5 = var2[var4];
         this.addMusic(var5);
      }

      return this;
   }

   public MusicList addMusic(GameMusic var1) {
      return this.addMusic(new MusicOptions(var1));
   }

   public MusicList addMusic(GameMusic... var1) {
      GameMusic[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameMusic var5 = var2[var4];
         this.addMusic(var5);
      }

      return this;
   }

   public MusicList addMusic(GameMusic var1, float var2) {
      return this.addMusic((new MusicOptions(var1)).volume(var2));
   }

   public MusicOptions addMusicConfig(GameMusic var1) {
      MusicOptions var2 = new MusicOptions(var1);
      this.addMusic(var2);
      return var2;
   }
}
