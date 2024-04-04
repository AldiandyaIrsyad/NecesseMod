package necesse.level.maps.biomes.temple;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestBiome;

public class TempleBiome extends ForestBiome {
   public static MobSpawnTable templeMobs = (new MobSpawnTable()).add(50, (String)"ancientskeleton").add(50, (String)"ancientarmoredskeleton").add(40, (String)"ancientskeletonthrower").add(30, (String)"ancientskeletonmage");

   public TempleBiome() {
   }

   public float getSpawnRateMod(Level var1) {
      return super.getSpawnRateMod(var1) * 0.75F;
   }

   public float getSpawnCapMod(Level var1) {
      return super.getSpawnCapMod(var1) * 0.75F;
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      return var1.getIslandDimension() == -201 ? new MobSpawnTable() : templeMobs;
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return var1.isCave ? defaultCaveCritters : defaultSurfaceCritters;
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return new MusicList(new GameMusic[]{MusicRegistry.Kronos});
   }
}
