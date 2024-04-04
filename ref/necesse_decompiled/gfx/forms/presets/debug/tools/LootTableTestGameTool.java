package necesse.gfx.forms.presets.debug.tools;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class LootTableTestGameTool extends MouseDebugGameTool {
   public static ArrayList<LabeledLootTable> lootTables = new ArrayList();
   private LabeledLootTable lootTable;

   public static SelectionFloatMenu getSelectionMenu(DebugForm var0) {
      SelectionFloatMenu var1 = new SelectionFloatMenu(var0);
      Iterator var2 = lootTables.iterator();

      while(var2.hasNext()) {
         LabeledLootTable var3 = (LabeledLootTable)var2.next();
         var1.add(var3.name, () -> {
            LootTableTestGameTool var3x = new LootTableTestGameTool(var0, var3);
            Screen.clearGameTools(var0);
            Screen.setGameTool(var3x, var0);
            var1.remove();
         });
      }

      return var1;
   }

   public LootTableTestGameTool(DebugForm var1, LabeledLootTable var2) {
      super(var1, "Test " + var2.name);
      this.lootTable = var2;
   }

   public void init() {
      this.onLeftClick((var1) -> {
         GameObject var2 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("storagebox"));
         GameRandom var3 = new GameRandom();
         Level var4 = this.getLevel();
         int var5 = this.getMouseTileX();
         int var6 = this.getMouseTileY();
         if (var4 != null) {
            if (var4.getObjectID(var5, var6) != var2.getID()) {
               var2.placeObject(var4, var5, var6, 2);
            }

            this.lootTable.table.applyToLevel(var3, (Float)var4.buffManager.getModifier(LevelModifiers.LOOT), var4, var5, var6);
         }

         Level var7 = this.getServerLevel();
         if (var7 != null) {
            if (var7.getObjectID(var5, var6) != var2.getID()) {
               var2.placeObject(var7, var5, var6, 2);
            }

            this.lootTable.table.applyToLevel(var3, (Float)var7.buffManager.getModifier(LevelModifiers.LOOT), var7, var5, var6);
         }

         return true;
      }, "Place chest");
      this.onRightClick((var1) -> {
         GameObject var2 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("air"));
         Level var3 = this.getLevel();
         if (var3 != null) {
            var2.placeObject(var3, this.getMouseTileX(), this.getMouseTileY(), 0);
         }

         Level var4 = this.getServerLevel();
         if (var4 != null) {
            var2.placeObject(var4, this.getMouseTileX(), this.getMouseTileY(), 0);
         }

         return true;
      }, "Remove object");
   }

   static {
      lootTables.add(new LabeledLootTable("Start chest", LootTablePresets.startChest));
      lootTables.add(new LabeledLootTable("Ruins chest", LootTablePresets.surfaceRuinsChest));
      lootTables.add(new LabeledLootTable("Crate", LootTablePresets.basicCrate));
      lootTables.add(new LabeledLootTable("Swamp Crate", LootTablePresets.swampCrate));
      lootTables.add(new LabeledLootTable("Desert Crate", LootTablePresets.desertCrate));
      lootTables.add(new LabeledLootTable("Deep crate", LootTablePresets.basicDeepCrate));
      lootTables.add(new LabeledLootTable("Snow Deep crate", LootTablePresets.snowDeepCrate));
      lootTables.add(new LabeledLootTable("Desert Deep crate", LootTablePresets.desertDeepCrate));
      lootTables.add(new LabeledLootTable("Basic cave chest", LootTablePresets.basicCaveChest));
      lootTables.add(new LabeledLootTable("Snow cave chest", LootTablePresets.snowCaveChest));
      lootTables.add(new LabeledLootTable("Swamp cave chest", LootTablePresets.swampCaveChest));
      lootTables.add(new LabeledLootTable("Desert cave chest", LootTablePresets.desertCaveChest));
      lootTables.add(new LabeledLootTable("Basic cave ruins chest", LootTablePresets.basicCaveRuinsChest));
      lootTables.add(new LabeledLootTable("Snow cave ruins chest", LootTablePresets.snowCaveRuinsChest));
      lootTables.add(new LabeledLootTable("Swamp cave ruins chest", LootTablePresets.swampCaveRuinsChest));
      lootTables.add(new LabeledLootTable("Desert cave ruins chest", LootTablePresets.desertCaveRuinsChest));
      lootTables.add(new LabeledLootTable("Dungeon chest", LootTablePresets.dungeonChest));
      lootTables.add(new LabeledLootTable("Pirate chest", LootTablePresets.pirateChest));
      lootTables.add(new LabeledLootTable("Pirate display stand", LootTablePresets.pirateDisplayStand));
      lootTables.add(new LabeledLootTable("Deep cave chest", LootTablePresets.deepCaveChest));
      lootTables.add(new LabeledLootTable("Abandoned mine chest", LootTablePresets.abandonedMineChest));
   }

   public static class LabeledLootTable {
      public final String name;
      public final LootTable table;

      public LabeledLootTable(String var1, LootTable var2) {
         this.name = var1;
         this.table = var2;
      }
   }
}
