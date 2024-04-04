package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class ForestDeepCaveBiome extends Biome {
   public static MobSpawnTable critters;
   public static MobSpawnTable mobs;

   public ForestDeepCaveBiome() {
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("biome", "deepcave", "biome", new LocalMessage("biome", "forest"));
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return critters;
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      return mobs;
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return new MusicList(new GameMusic[]{MusicRegistry.SecretsOfTheForest});
   }

   static {
      critters = (new MobSpawnTable()).include(Biome.defaultCaveCritters);
      mobs = (new MobSpawnTable()).add(100, (String)"skeleton").add(40, (String)"skeletonthrower").add(45, (String)"deepcavespirit");
   }
}
