package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.HaveKilledMobsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.RaiderMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.HomestoneObjectItem;
import necesse.inventory.item.placeableItem.objectItem.WaystoneObjectItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SwampGuardianTier extends SimpleSettlementQuestTier {
   public static int totalTierQuests = 1;
   public static int totalBasicQuests = 0;
   public static OneOfTicketLootItems uniqueQuestRewards = new OneOfTicketLootItems(new Object[0]);
   public static ArrayList<Function<SettlementLevelData, Quest>> tierQuests = new ArrayList();
   public static LootTable tierCompleteRewards;
   public static TicketSystemList<RaiderConstructor> raiderStats;

   public SwampGuardianTier() {
      super("swampguardian");
   }

   public Quest getTierCompleteQuest(SettlementLevelData var1) {
      return new HaveKilledMobsSettlementQuest("swampguardian", 1);
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
         return new DeliverItemsSettlementQuest("slimechunk", 1);
      });
      tierQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("swampeel", 1);
      });
      tierQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("slimylauncher", 1);
      });
      tierCompleteRewards = new LootTable(new LootItemInterface[]{new LootItemInterface() {
         public void addPossibleLoot(LootList var1, Object... var2) {
            var1.add("homestone");
            var1.add("waystone");
         }

         public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
            SettlementLevelData var5 = (SettlementLevelData)LootTable.expectExtra(SettlementLevelData.class, var4, 0);
            if (var5 != null) {
               InventoryItem var6 = HomestoneObjectItem.setupHomestoneItem(new InventoryItem("homestone"), var5.getLevel());
               var1.add(var6);
               InventoryItem var7 = WaystoneObjectItem.setupWaystoneItem(new InventoryItem("waystone", 2), var5.getLevel());
               var1.add(var7);
            }

         }
      }, addedQuestRewards});
      raiderStats = new TicketSystemList();
      raiderStats.addObject(100, new RaiderConstructor("demonicsword", (String)null, "demonicchestplate", "demonicboots", 525, 7, 20, 35));
      raiderStats.addObject(50, new RaiderConstructor("demonicbow", (String)null, "ironchestplate", "ironboots", 375, 4, 20, 35));
      raiderStats.addObject(25, new RaiderConstructor("bloodbolt", "voidhat", "voidrobe", "voidboots", 300, 3, 20, 35));
   }
}
