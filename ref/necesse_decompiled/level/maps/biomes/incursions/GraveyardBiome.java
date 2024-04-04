package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class GraveyardBiome extends Biome {
   public static MobSpawnTable critters;
   public static MobSpawnTable mobs;

   public GraveyardBiome() {
   }

   public boolean canRain(Level var1) {
      return false;
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return critters;
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      return mobs;
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return new MusicList(new GameMusic[]{MusicRegistry.GrindTheAlarms});
   }

   static {
      critters = (new MobSpawnTable()).include(Biome.defaultCaveCritters);
      mobs = (new MobSpawnTable()).add(100, (String)"cryptbat").add(75, (String)"cryptvampire").add(50, (String)"phantom");
   }
}
