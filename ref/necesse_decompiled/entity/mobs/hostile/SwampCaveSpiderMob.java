package necesse.entity.mobs.hostile;

import necesse.entity.mobs.GameDamage;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class SwampCaveSpiderMob extends GiantCaveSpiderMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("cavespidergland", 1, 2, MultiTextureMatItem.getGNDData(2))});

   public SwampCaveSpiderMob() {
      super(GiantCaveSpiderMob.Variant.SWAMP, 300, new GameDamage(40.0F), new GameDamage(30.0F));
      this.setSpeed(35.0F);
      this.setArmor(10);
   }

   public LootTable getLootTable() {
      return lootTable;
   }
}
