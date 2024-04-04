package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class HeadArmorLootTable extends LootTable {
   public static final LootTable headArmor = new LootTable(new LootItemInterface[]{new LootItem("leatherhood"), new LootItem("clothhat"), new LootItem("copperhelmet"), new LootItem("ironhelmet"), new LootItem("goldcrown"), new LootItem("goldhelmet"), new LootItem("spiderhelmet"), new LootItem("frosthelmet"), new LootItem("frosthood"), new LootItem("frosthat"), new LootItem("demonichelmet"), new LootItem("voidmask"), new LootItem("voidhat"), new LootItem("bloodplatecowl"), new LootItem("ivyhelmet"), new LootItem("ivyhood"), new LootItem("ivyhat"), new LootItem("ivycirclet"), new LootItem("quartzhelmet"), new LootItem("quartzcrown"), new LootItem("tungstenhelmet"), new LootItem("shadowhat"), new LootItem("shadowhood"), new LootItem("ninjahood"), new LootItem("glacialcirclet"), new LootItem("glacialhelmet"), new LootItem("myceliumhood"), new LootItem("myceliumscarf"), new LootItem("widowhelmet"), new LootItem("ancientfossilmask"), new LootItem("ancientfossilhelmet")});
   public static final HeadArmorLootTable instance = new HeadArmorLootTable();

   private HeadArmorLootTable() {
      super(headArmor);
   }
}
