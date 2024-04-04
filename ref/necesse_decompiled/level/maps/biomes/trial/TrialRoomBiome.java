package necesse.level.maps.biomes.trial;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class TrialRoomBiome extends Biome {
   public TrialRoomBiome() {
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      return new MobSpawnTable();
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return new MobSpawnTable();
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return new MusicList(new GameMusic[]{MusicRegistry.Kronos});
   }
}
