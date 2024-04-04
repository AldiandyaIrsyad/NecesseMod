package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class SlimeCaveBiome extends Biome {
   public static MobSpawnTable critters;
   public static MobSpawnTable mobs;

   public SlimeCaveBiome() {
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
      mobs = (new MobSpawnTable()).add(100, (String)"warriorslime").add(75, (String)"leggedslimethrower").add(75, (String)"mageslime").add(50, (String)"ghostslime").addLimited(8, "slimeworm", 1, Mob.MOB_SPAWN_AREA.maxSpawnDistance * 2);
   }
}
