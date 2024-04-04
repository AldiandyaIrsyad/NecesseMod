package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.entity.mobs.RaiderMob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public abstract class SettlementQuestTier {
   public static OneOfTicketLootItems basicUniqueQuestRewards = new OneOfTicketLootItems(new Object[]{400, LootItem.between("recallscroll", 2, 5), 400, LootItem.between("travelscroll", 2, 5), 1800, new OneOfTicketLootItems(new Object[]{100, LootItem.between("healthregenpotion", 3, 6), 100, LootItem.between("attackspeedpotion", 3, 6), 100, LootItem.between("fishingpotion", 3, 6), 100, LootItem.between("battlepotion", 3, 6), 100, LootItem.between("resistancepotion", 3, 6), 100, LootItem.between("thornspotion", 3, 6), 100, LootItem.between("accuracypotion", 3, 6), 100, LootItem.between("minionpotion", 3, 6), 100, LootItem.between("knockbackpotion", 3, 6), 100, LootItem.between("rapidpotion", 3, 6), 100, LootItem.between("spelunkerpotion", 3, 6), 100, LootItem.between("treasurepotion", 3, 6), 100, LootItem.between("passivepotion", 3, 6)}), 200, new LootItem("constructionhammer"), 200, new LootItem("telescopicladder"), 200, new LootItem("toolextender"), 200, new LootItem("itemattractor"), 200, new LootItem("boxingglovegun"), 100, new LootItem("infinitewaterbucket"), 100, new LootItem("infiniterope"), 50, new LootItemList(new LootItemInterface[]{new LootItem("horsemask"), new LootItem("horsecostumeshirt"), new LootItem("horsecostumeboots")}), 50, new LootItemList(new LootItemInterface[]{new LootItem("chickenmask"), new LootItem("chickencostumeshirt"), new LootItem("chickencostumeboots")}), 50, new LootItemList(new LootItemInterface[]{new LootItem("frogmask"), new LootItem("frogcostumeshirt"), new LootItem("frogcostumeboots")}), 50, new LootItemList(new LootItemInterface[]{new LootItem("alienmask"), new LootItem("aliencostumeshirt"), new LootItem("aliencostumeboots")})});
   public static LootItemList addedQuestRewards = new LootItemList(new LootItemInterface[]{LootItem.between("coin", 20, 50)});
   public static ArrayList<Function<SettlementLevelData, Quest>> basicQuests = new ArrayList();
   public static ArrayList<SettlementQuestTier> questTiers;
   public final String stringID;

   public static int getTierIndex(String var0) {
      for(int var1 = 0; var1 < questTiers.size(); ++var1) {
         if (((SettlementQuestTier)questTiers.get(var1)).stringID.equals(var0)) {
            return var1;
         }
      }

      return 0;
   }

   public static SettlementQuestTier getTier(String var0) {
      return (SettlementQuestTier)questTiers.get(getTierIndex(var0));
   }

   public static SettlementQuestTier getTier(int var0) {
      return var0 < questTiers.size() ? (SettlementQuestTier)questTiers.get(var0) : null;
   }

   public static ArrayList<Function<SettlementLevelData, Quest>> getBasicQuests(String var0, int var1) {
      ArrayList var2 = new ArrayList(basicQuests);
      int var3 = getTierIndex(var0);
      if (var3 > 0) {
         for(int var4 = 0; var4 < Math.min(var3, questTiers.size()); ++var4) {
            ((SettlementQuestTier)questTiers.get(var4)).addBasicQuests(var2, var1);
         }
      }

      return var2;
   }

   public static OneOfTicketLootItems getUniqueRewards(String var0) {
      OneOfTicketLootItems var1 = new OneOfTicketLootItems(basicUniqueQuestRewards, new Object[0]);
      int var2 = getTierIndex(var0);
      if (var2 > 0) {
         for(int var3 = 0; var3 < Math.min(var2, questTiers.size()); ++var3) {
            ((SettlementQuestTier)questTiers.get(var3)).addUniqueRewards(var1);
         }
      }

      return var1;
   }

   public SettlementQuestTier(String var1) {
      this.stringID = var1;
   }

   public abstract void addUniqueRewards(OneOfTicketLootItems var1);

   public abstract void addBasicQuests(ArrayList<Function<SettlementLevelData, Quest>> var1, int var2);

   public abstract LootTable rewardsLootTable(SettlementLevelData var1, OneOfTicketLootItems var2, int var3);

   public abstract ArrayList<Function<SettlementLevelData, Quest>> nextQuests(ArrayList<Function<SettlementLevelData, Quest>> var1, int var2);

   public abstract Quest getTierCompleteQuest(SettlementLevelData var1, int var2);

   public abstract GameMessage getTierCompleteQuestError(SettlementLevelData var1, int var2);

   public abstract LootTable tierRewardsLootTable(SettlementLevelData var1, int var2);

   public abstract ModifierValue<?>[] getTierCompletedSettlerModifiers(SettlerMob var1);

   public abstract RaiderMob getRandomRaider(Level var1, Point var2);

   public float getExpectedSettlersIncrease() {
      return 1.34F;
   }

   static {
      basicQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("babyshark", 1);
      });
      basicQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("crabclaw", 1);
      });
      basicQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("sandray", 1);
      });
      basicQuests.add((var0) -> {
         return new DeliverItemsSettlementQuest("babyswordfish", 1);
      });
      questTiers = new ArrayList();
      questTiers.add(new EvilsProtectorTier());
      questTiers.add(new QueenSpiderTier());
      questTiers.add(new VoidWizardTier());
      questTiers.add(new SwampGuardianTier());
      questTiers.add(new AncientVultureTier());
      questTiers.add(new PirateCaptainTier());
      questTiers.add(new ReaperTier());
      questTiers.add(new CryoQueenTier());
      questTiers.add(new PestWardenTier());
      questTiers.add(new SageAndGritTier());
      questTiers.add(new FallenWizardTier());
   }
}
