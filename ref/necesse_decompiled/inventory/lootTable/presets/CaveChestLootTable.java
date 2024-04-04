package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class CaveChestLootTable {
   public static final RotationLootItem basicMainItems = RotationLootItem.presetRotation(new LootItem("zephyrcharm"), new LootItem("shinebelt"), new LootItem("heavyhammer"), new LootItem("noblehorseshoe"));
   public static final RotationLootItem desertMainItems = RotationLootItem.presetRotation(new LootItem("ancientfeather"), new LootItem("miningcharm"), new LootItem("cactusshield"), new LootItem("airvessel"), new LootItem("prophecyslab"));
   public static final RotationLootItem swampMainItems = RotationLootItem.presetRotation(new LootItem("swamptome"), new LootItem("slimecanister"), new LootItem("stinkflask"), new LootItem("vambrace"), new LootItem("overgrownfishingrod"));
   public static final RotationLootItem snowMainItems = RotationLootItem.presetRotation(new LootItem("frozenwave"), new LootItem("calmingrose"), new LootItem("frozenheart"), new LootItem("sparegemstones"), new LootItem("magicbranch"));
   public static final OneOfLootItems potions = new OneOfLootItems(LootItem.between("attackspeedpotion", 2, 4), new LootItemInterface[]{LootItem.between("healthregenpotion", 2, 4), LootItem.between("speedpotion", 2, 4), LootItem.between("battlepotion", 2, 4), LootItem.between("resistancepotion", 2, 4)});
   public static final OneOfLootItems bars = new OneOfLootItems(LootItem.offset("ironbar", 8, 2), new LootItemInterface[]{LootItem.offset("copperbar", 10, 2), LootItem.offset("goldbar", 5, 2)});
   public static final LootTable extraItems = new LootTable(new LootItemInterface[]{ChanceLootItem.offset(0.5F, "torch", 15, 5), new ChanceLootItemList(0.75F, new LootItemInterface[]{new OneOfTicketLootItems(new Object[]{3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3)})}), LootItem.between("recallscroll", 1, 2), new ChanceLootItem(0.33F, "enchantingscroll")});
   public static final RotationLootItem basicVinyls = RotationLootItem.presetRotation(new LootItem("forestpathvinyl"), new LootItem("awakeningtwilightvinyl"), new LootItem("depthsoftheforestvinyl"));
   public static final RotationLootItem desertVinyls = RotationLootItem.presetRotation(new LootItem("oasisserenadevinyl"), new LootItem("nightinthedunesvinyl"), new LootItem("dustyhollowsvinyl"));
   public static final RotationLootItem swampVinyls = RotationLootItem.presetRotation(new LootItem("watersideserenadevinyl"), new LootItem("gatorslullabyvinyl"), new LootItem("murkymirevinyl"));
   public static final RotationLootItem snowVinyls = RotationLootItem.presetRotation(new LootItem("auroratundravinyl"), new LootItem("polarnightvinyl"), new LootItem("glaciersembracevinyl"));
   public static final LootTable basicChest;
   public static final LootTable desertChest;
   public static final LootTable swampChest;
   public static final LootTable snowChest;

   public CaveChestLootTable() {
   }

   static {
      basicChest = new LootTable(new LootItemInterface[]{basicMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4F, new LootItemInterface[]{basicVinyls}), new ChanceLootItem(0.4F, "mysteriousportal")});
      desertChest = new LootTable(new LootItemInterface[]{desertMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4F, new LootItemInterface[]{desertVinyls}), new ChanceLootItem(0.4F, "ancientstatue")});
      swampChest = new LootTable(new LootItemInterface[]{swampMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4F, new LootItemInterface[]{swampVinyls}), new ChanceLootItem(0.4F, "spikedfossil")});
      snowChest = new LootTable(new LootItemInterface[]{snowMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4F, new LootItemInterface[]{snowVinyls}), new ChanceLootItem(0.4F, "royalegg")});
   }
}
