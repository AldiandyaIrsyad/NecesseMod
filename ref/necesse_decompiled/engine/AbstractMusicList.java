package necesse.engine;

import necesse.gfx.GameMusic;

public abstract class AbstractMusicList {
   public AbstractMusicList() {
   }

   public abstract MusicOptions getNextMusic(MusicOptions var1);

   public abstract MusicOptionsOffset getExpectedMusicPlaying();

   public abstract boolean shouldChangeTrack(MusicOptions var1);

   public abstract Iterable<GameMusic> getMusicInList();
}
