package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.quest.Quest;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public abstract class SimpleSettlementQuestTier extends SettlementQuestTier {
   public SimpleSettlementQuestTier(String var1) {
      super(var1);
   }

   public abstract int getTotalTierQuests();

   public abstract int getTotalBasicQuests();

   public abstract OneOfTicketLootItems getUniqueQuestRewards();

   public abstract ArrayList<Function<SettlementLevelData, Quest>> getTierQuests();

   public abstract LootTable getTierCompleteRewards();

   public abstract Quest getTierCompleteQuest(SettlementLevelData var1);

   public void addUniqueRewards(OneOfTicketLootItems var1) {
      var1.addAll(this.getUniqueQuestRewards());
   }

   public void addBasicQuests(ArrayList<Function<SettlementLevelData, Quest>> var1, int var2) {
      var1.addAll(this.getTierQuests());
   }

   public LootTable rewardsLootTable(SettlementLevelData var1, OneOfTicketLootItems var2, int var3) {
      return new LootTable(new LootItemInterface[]{var2, addedQuestRewards});
   }

   public ArrayList<Function<SettlementLevelData, Quest>> nextQuests(ArrayList<Function<SettlementLevelData, Quest>> var1, int var2) {
      ArrayList var3;
      if (var2 < this.getTotalTierQuests()) {
         var3 = this.getTierQuests();
         return var3.isEmpty() ? var1 : var3;
      } else {
         var3 = new ArrayList();
         var3.addAll(this.getTierQuests());
         var3.addAll(var1);
         return var3;
      }
   }

   public Quest getTierCompleteQuest(SettlementLevelData var1, int var2) {
      return var2 >= this.getTotalTierQuests() + this.getTotalBasicQuests() ? this.getTierCompleteQuest(var1) : null;
   }

   public GameMessage getTierCompleteQuestError(SettlementLevelData var1, int var2) {
      return var2 < this.getTotalTierQuests() + this.getTotalBasicQuests() ? new LocalMessage("ui", "eldertiercompleteerr", new Object[]{"count", this.getTotalTierQuests() + this.getTotalBasicQuests() - var2}) : null;
   }

   public LootTable tierRewardsLootTable(SettlementLevelData var1, int var2) {
      return this.getTierCompleteRewards();
   }
}
