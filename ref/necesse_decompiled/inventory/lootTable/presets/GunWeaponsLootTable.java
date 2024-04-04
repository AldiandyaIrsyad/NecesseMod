package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GunWeaponsLootTable extends LootTable {
   public static final LootTable gunWeapons = new LootTable(new LootItemInterface[]{new LootItem("handgun"), new LootItem("webbedgun"), new LootItem("machinegun"), new LootItem("shotgun"), new LootItem("sniperrifle"), new LootItem("flintlock"), new LootItem("handcannon"), new LootItem("deathripper"), new LootItem("cryoblaster"), new LootItem("livingshotty"), new LootItem("antiquerifle")});
   public static final GunWeaponsLootTable instance = new GunWeaponsLootTable();

   private GunWeaponsLootTable() {
      super(gunWeapons);
   }
}
