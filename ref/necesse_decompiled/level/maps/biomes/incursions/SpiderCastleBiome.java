package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class SpiderCastleBiome extends Biome {
   public static MobSpawnTable critters;
   public static MobSpawnTable mobs;

   public SpiderCastleBiome() {
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return critters;
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      return mobs;
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return new MusicList(new GameMusic[]{MusicRegistry.VenomousReckoning});
   }

   static {
      critters = (new MobSpawnTable()).include(defaultCaveCritters);
      mobs = (new MobSpawnTable()).add(50, (String)"webspinner").add(30, (String)"bloatedspider").add(30, (String)"spiderkin").add(75, (String)"spiderkinwarrior").add(75, (String)"spiderkinarcher").add(50, (String)"spiderkinmage");
   }
}
