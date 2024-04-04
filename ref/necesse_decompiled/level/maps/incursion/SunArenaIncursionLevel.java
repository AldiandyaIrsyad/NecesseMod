package necesse.level.maps.incursion;

import necesse.engine.Settings;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.presets.SunArenaBottomPreset;
import necesse.level.maps.presets.SunArenaTopPreset;

public class SunArenaIncursionLevel extends IncursionLevel {
   public static int SUN_ARENA_AMBIENT_LIGHT = 150;

   public SunArenaIncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SunArenaIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, WorldEntity var3) {
      super(var1, 92, 92, var2, var3);
      this.biome = BiomeRegistry.SUN_ARENA;
      this.isCave = true;
      this.generateLevel(var2);
   }

   public void generateLevel(BiomeMissionIncursionData var1) {
      PresetGeneration var2 = new PresetGeneration(this);
      var2.applyPreset(new SunArenaTopPreset(), 0, 0);
      var2.applyPreset(new SunArenaBottomPreset(), 0, 46);
      IncursionBiome.addReturnPortal(this, 1471.5F, 2720.5F);
      GenerationTools.checkValid(this);
   }

   public LightManager constructLightManager() {
      return new LightManager(this) {
         public void updateAmbientLight() {
            if (this.ambientLightOverride != null) {
               this.ambientLight = this.ambientLightOverride;
            } else if (Settings.alwaysLight) {
               this.ambientLight = this.newLight(150.0F);
            } else {
               this.ambientLight = SunArenaIncursionLevel.this.lightManager.newLight((float)SunArenaIncursionLevel.SUN_ARENA_AMBIENT_LIGHT);
            }
         }
      };
   }
}
