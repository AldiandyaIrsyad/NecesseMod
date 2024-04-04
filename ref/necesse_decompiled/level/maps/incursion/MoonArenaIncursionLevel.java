package necesse.level.maps.incursion;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.MoonArenaBottomPreset;
import necesse.level.maps.presets.MoonArenaTopPreset;

public class MoonArenaIncursionLevel extends IncursionLevel {
   public MoonArenaIncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public MoonArenaIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, WorldEntity var3) {
      super(var1, 92, 92, var2, var3);
      this.biome = BiomeRegistry.MOON_ARENA;
      this.isCave = true;
      this.generateLevel(var2);
   }

   public void generateLevel(BiomeMissionIncursionData var1) {
      PresetGeneration var2 = new PresetGeneration(this);
      var2.applyPreset(new MoonArenaTopPreset(), 0, 0);
      var2.applyPreset(new MoonArenaBottomPreset(), 0, 46);
      IncursionBiome.addReturnPortal(this, 1471.5F, 2720.5F);
      GenerationTools.checkValid(this);
   }
}
