package necesse.engine.expeditions;

import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SnowCaveExpedition extends SettlerExpedition {
   public static LootTable ores = new LootTable(new LootItemInterface[]{LootItem.between("copperore", 16, 26), LootItem.between("ironore", 12, 22), LootItem.between("goldore", 8, 18), LootItem.between("frostshard", 6, 14)});
   public static LootTable extra = new LootTable(new LootItemInterface[]{LootItem.between("batwing", 4, 12), LootItem.between("cavespidergland", 5, 8, MultiTextureMatItem.getGNDData(1))});
   public static LootTable rewards;

   public SnowCaveExpedition() {
   }

   public GameMessage getUnavailableMessage() {
      return new LocalMessage("expedition", "completequests");
   }

   public float getSuccessChance(SettlementLevelData var1, HumanMob var2) {
      return questProgressSuccessChance(var1, "queenspider");
   }

   public int getBaseCost(SettlementLevelData var1, HumanMob var2) {
      return 400;
   }

   public List<InventoryItem> getRewardItems(SettlementLevelData var1, HumanMob var2) {
      return rewards.getNewList(GameRandom.globalRandom, 1.0F);
   }

   static {
      rewards = new LootTable(new LootItemInterface[]{LootTablePresets.snowCaveChest, ores, extra});
   }
}
