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

public class SwampCaveExpedition extends SettlerExpedition {
   public static LootTable ores = new LootTable(new LootItemInterface[]{LootItem.between("copperore", 16, 26), LootItem.between("ironore", 12, 22), LootItem.between("goldore", 8, 18), LootItem.between("ivyore", 8, 18)});
   public static LootTable extra = new LootTable(new LootItemInterface[]{LootItem.between("swamplarva", 10, 20), LootItem.between("swampsludge", 6, 12), LootItem.between("cavespidergland", 5, 8, MultiTextureMatItem.getGNDData(2))});
   public static LootTable rewards;

   public SwampCaveExpedition() {
   }

   public GameMessage getUnavailableMessage() {
      return new LocalMessage("expedition", "completequests");
   }

   public float getSuccessChance(SettlementLevelData var1, HumanMob var2) {
      return questProgressSuccessChance(var1, "swampguardian");
   }

   public int getBaseCost(SettlementLevelData var1, HumanMob var2) {
      return 500;
   }

   public List<InventoryItem> getRewardItems(SettlementLevelData var1, HumanMob var2) {
      return rewards.getNewList(GameRandom.globalRandom, 1.0F);
   }

   static {
      rewards = new LootTable(new LootItemInterface[]{LootTablePresets.swampCaveChest, ores, extra});
   }
}
