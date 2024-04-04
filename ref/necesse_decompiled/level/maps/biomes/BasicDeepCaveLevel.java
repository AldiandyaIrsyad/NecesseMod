package necesse.level.maps.biomes;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;

public class BasicDeepCaveLevel extends Level {
   public BasicDeepCaveLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public BasicDeepCaveLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public void generateLevel() {
      CaveGeneration var1 = new CaveGeneration(this, "deeprocktile", "deeprock");
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var1), (var1x) -> {
         var1.generateLevel(0.44F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var1), (var1x) -> {
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverock"), 0.005F);
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveOresEvent(this, var1), (var0) -> {
      });
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var1));
      PresetGeneration var2 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var1, var2), (var1x) -> {
         var1.generateRandomCrates(0.03F, ObjectRegistry.getObjectID("crate"));
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var1, var2));
      GenerationTools.checkValid(this);
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.basicDeepCrate;
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
   }
}
