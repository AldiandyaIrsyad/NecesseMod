package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class CaveCryptLootTable extends LootTable {
   public static final RotationLootItem uniqueItems = RotationLootItem.presetRotation(new LootItem("vampiresgift"), new LootItem("bloodbolt"));
   public static final RotationLootItem uniqueBloodPlateItems = RotationLootItem.presetRotation(new LootItem("bloodplatecowl"), new LootItem("bloodplatechestplate"), new LootItem("bloodplateboots"));
   public static final CaveCryptLootTable instance = new CaveCryptLootTable();

   private CaveCryptLootTable() {
      super(new ChanceLootItem(0.15F, "enchantingscroll"), CrateLootTable.basicCrate, CrateLootTable.basicCrate, LootItem.offset("coin", 35, 5), ChanceLootItem.between(0.7F, "batwing", 3, 5));
   }
}
