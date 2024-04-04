package necesse.engine.expeditions;

import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class DungeonExpedition extends SettlerExpedition {
   public static LootTable extra = new LootTable(new LootItemInterface[]{LootItem.between("voidshard", 2, 6)});
   public static LootTable rewards;

   public DungeonExpedition() {
   }

   public GameMessage getUnavailableMessage() {
      return new LocalMessage("expedition", "completequests");
   }

   public float getSuccessChance(SettlementLevelData var1, HumanMob var2) {
      return questProgressSuccessChance(var1, "voidwizard");
   }

   public int getBaseCost(SettlementLevelData var1, HumanMob var2) {
      return 450;
   }

   public List<InventoryItem> getRewardItems(SettlementLevelData var1, HumanMob var2) {
      return rewards.getNewList(GameRandom.globalRandom, 1.0F);
   }

   static {
      rewards = new LootTable(new LootItemInterface[]{LootTablePresets.dungeonChest, extra});
   }
}
