package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.HaveKilledMobsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.RaiderMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class VoidWizardTier extends SimpleSettlementQuestTier {
   public static int totalTierQuests = 1;
   public static int totalBasicQuests = 0;
   public static OneOfTicketLootItems uniqueQuestRewards = new OneOfTicketLootItems(new Object[0]);
   public static ArrayList<Function<SettlementLevelData, Quest>> tierQuests = new ArrayList();
   public static LootTable tierCompleteRewards;
   public static TicketSystemList<RaiderConstructor> raiderStats;

   public VoidWizardTier() {
      super("voidwizard");
   }

   public Quest getTierCompleteQuest(SettlementLevelData var1) {
      return new HaveKilledMobsSettlementQuest("voidwizard", 1);
   }

   public int getTotalTierQuests() {
      return totalTierQuests;
   }

   public int getTotalBasicQuests() {
      return totalBasicQuests;
   }

   public OneOfTicketLootItems getUniqueQuestRewards() {
      return uniqueQuestRewards;
   }

   public ArrayList<Function<SettlementLevelData, Quest>> getTierQuests() {
      return tierQuests;
   }

   public LootTable getTierCompleteRewards() {
      return tierCompleteRewards;
   }

   public ModifierValue<?>[] getTierCompletedSettlerModifiers(SettlerMob var1) {
      return new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 100)};
   }

   public RaiderMob getRandomRaider(Level var1, Point var2) {
      return ((RaiderConstructor)raiderStats.getRandomObject(GameRandom.globalRandom)).getRaider(var1);
   }

   static {
      tierQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("enchantedcollar", 1);
      });
      tierQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("apprenticescroll", 1);
      });
      tierQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("darkgem", 1);
      });
      tierQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("book", 4);
      });
      tierCompleteRewards = new LootTable(new LootItemInterface[]{new LootItem("voidpouch"), addedQuestRewards});
      raiderStats = new TicketSystemList();
      raiderStats.addObject(100, new RaiderConstructor("ironsword", "ironhelmet", "ironchestplate", "ironboots", 375, 4, 10, 25));
      raiderStats.addObject(50, new RaiderConstructor("ironbow", "leatherhood", "leathershirt", "leatherboots", 300, 2, 10, 25));
      raiderStats.addObject(25, new RaiderConstructor("spiderboomerang", "spiderhelmet", "spiderchestplate", "spiderboots", 225, 4, 10, 30));
   }
}
