package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public class SettlementClientQuests {
   public final long clientAuth;
   public final SettlementLevelData data;
   private String questTier;
   private boolean hasCompletedQuestTier;
   private int tierQuestsCompleted;
   private int totalCompletedQuests;
   private int lastRandomQuest;
   private int questUniqueID;
   private int tierQuestUniqueID;
   private long questGeneratedWorldTime;

   public SettlementClientQuests(SettlementLevelData var1, long var2) {
      this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(0)).stringID;
      this.data = var1;
      this.clientAuth = var2;
   }

   public SettlementClientQuests(SettlementLevelData var1, LoadData var2) {
      this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(0)).stringID;
      this.data = var1;
      this.clientAuth = var2.getLong("clientAuth");
      this.questTier = var2.getUnsafeString("questTier", this.questTier);
      this.hasCompletedQuestTier = var2.getBoolean("hasCompletedQuestTier", false);
      if (SettlementQuestTier.questTiers.stream().noneMatch((var1x) -> {
         return var1x.stringID.equals(this.questTier);
      })) {
         this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(0)).stringID;
      }

      if (this.hasCompletedQuestTier) {
         int var3 = SettlementQuestTier.getTierIndex(this.questTier);
         if (var3 < SettlementQuestTier.questTiers.size() - 1) {
            this.hasCompletedQuestTier = false;
            this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var3 + 1)).stringID;
         }
      }

      this.tierQuestsCompleted = var2.getInt("tierQuestsCompleted", 0);
      this.totalCompletedQuests = var2.getInt("totalCompletedQuests", 0);
      this.lastRandomQuest = var2.getInt("lastRandomQuest", 0);
      this.questUniqueID = var2.getInt("questUniqueID", 0);
      this.questGeneratedWorldTime = var2.getLong("questGeneratedWorldTime", 0L);
      this.tierQuestUniqueID = var2.getInt("tierQuestUniqueID", 0);
   }

   public void addSaveData(SaveData var1) {
      var1.addLong("clientAuth", this.clientAuth);
      var1.addUnsafeString("questTier", this.questTier);
      var1.addBoolean("hasCompletedQuestTier", this.hasCompletedQuestTier);
      var1.addInt("tierQuestsCompleted", this.tierQuestsCompleted);
      var1.addInt("totalCompletedQuests", this.totalCompletedQuests);
      var1.addInt("lastRandomQuest", this.lastRandomQuest);
      var1.addInt("questUniqueID", this.questUniqueID);
      var1.addLong("questGeneratedWorldTime", this.questGeneratedWorldTime);
      var1.addInt("tierQuestUniqueID", this.tierQuestUniqueID);
   }

   public QuestManager getQuestManager() {
      return this.data.getLevel().getServer().world.getQuests();
   }

   public ServerClient getClient() {
      return this.data.getLevel().getServer().getClientByAuth(this.clientAuth);
   }

   public void setTier(SettlementQuestTier var1, boolean var2) {
      if (!this.questTier.equals(var1.stringID) || this.hasCompletedQuestTier != var2) {
         this.questTier = var1.stringID;
         this.hasCompletedQuestTier = var2;
         if (var2) {
            int var3 = SettlementQuestTier.getTierIndex(this.questTier);
            if (var3 < SettlementQuestTier.questTiers.size() - 1) {
               this.hasCompletedQuestTier = false;
               this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var3 + 1)).stringID;
            }
         }

         if (this.questUniqueID != 0) {
            this.getQuestManager().removeQuest(this.questUniqueID);
            this.questUniqueID = 0;
         }

         if (this.tierQuestUniqueID != 0) {
            this.getQuestManager().removeQuest(this.tierQuestUniqueID);
            this.tierQuestUniqueID = 0;
         }

      }
   }

   public Quest getQuest() {
      Quest var1 = null;
      if (this.questUniqueID != 0) {
         var1 = this.getQuestManager().getQuest(this.questUniqueID);
      }

      if (var1 == null) {
         var1 = this.getNewQuest();
         this.getQuestManager().addQuest(var1, true);
         this.questUniqueID = var1.getUniqueID();
         this.questGeneratedWorldTime = this.data.getLevel().getWorldEntity().getWorldTime();
      }

      return var1;
   }

   public Quest getTierQuest() {
      Quest var1 = null;
      if (this.tierQuestUniqueID != 0) {
         var1 = this.getQuestManager().getQuest(this.tierQuestUniqueID);
      }

      if (var1 == null) {
         SettlementQuestTier var2 = this.getCurrentQuestTier();
         if (var2 != null) {
            var1 = var2.getTierCompleteQuest(this.data, this.tierQuestsCompleted);
            if (var1 != null) {
               this.getQuestManager().addQuest(var1, true);
               this.tierQuestUniqueID = var1.getUniqueID();
            }
         }
      }

      return var1;
   }

   public GameMessage getTierQuestError() {
      SettlementQuestTier var1 = this.getCurrentQuestTier();
      return var1 != null ? var1.getTierCompleteQuestError(this.data, this.tierQuestsCompleted) : null;
   }

   public int getQuestTiersCompleted() {
      return SettlementQuestTier.getTierIndex(this.questTier) + (this.hasCompletedQuestTier ? 1 : 0);
   }

   public boolean hasCompletedQuestTier(String var1) {
      return this.getQuestTiersCompleted() > SettlementQuestTier.getTierIndex(var1);
   }

   public int getQuestsCompletedInCurrentTier() {
      return this.tierQuestsCompleted;
   }

   public int getTotalCompletedQuests() {
      return this.totalCompletedQuests;
   }

   public SettlementQuestTier getCurrentQuestTier() {
      SettlementQuestTier var1 = SettlementQuestTier.getTier(this.getQuestTiersCompleted());
      if (var1 == null) {
         this.tierQuestsCompleted = 0;
      } else {
         this.hasCompletedQuestTier = false;
      }

      return var1;
   }

   protected Quest getNewQuest() {
      SettlementQuestTier var1 = this.getCurrentQuestTier();
      ArrayList var2 = SettlementQuestTier.getBasicQuests(this.questTier, this.tierQuestsCompleted);
      ArrayList var3;
      if (var1 != null) {
         var3 = var1.nextQuests(var2, this.tierQuestsCompleted);
      } else {
         var3 = var2;
      }

      if (var3.size() <= 1) {
         this.lastRandomQuest = 0;
         return (Quest)((Function)var3.get(0)).apply(this.data);
      } else {
         int var4 = GameRandom.globalRandom.nextInt(var3.size());
         if (var4 == this.lastRandomQuest) {
            var4 = (var4 + 1) % var3.size();
         }

         this.lastRandomQuest = var4;
         return (Quest)((Function)var3.get(var4)).apply(this.data);
      }
   }

   public GameMessage canSkipQuest() {
      int var1 = this.data.getLevel().getWorldEntity().getDayTimeMax();
      long var2 = this.data.getLevel().getWorldEntity().getWorldTime();
      return this.questGeneratedWorldTime + (long)(var1 * 1000) > var2 ? new LocalMessage("ui", "elderskipquesttime") : null;
   }

   public void removeCurrentQuest() {
      this.getQuest().remove();
      this.questUniqueID = 0;
   }

   public void removeCurrentTierQuest() {
      Quest var1 = this.getTierQuest();
      if (var1 != null) {
         var1.remove();
      }

      this.tierQuestUniqueID = 0;
   }

   public List<InventoryItem> completeQuestAndGetReward() {
      this.getQuest().remove();
      this.questUniqueID = 0;
      ArrayList var1 = new ArrayList();
      SettlementQuestTier var2 = this.getCurrentQuestTier();
      OneOfTicketLootItems var3 = SettlementQuestTier.getUniqueRewards(this.questTier);
      if (var2 != null) {
         var2.rewardsLootTable(this.data, var3, this.tierQuestsCompleted).addItems(var1, GameRandom.globalRandom, 1.0F, this, this.totalCompletedQuests);
      } else {
         var3.addItems(var1, GameRandom.globalRandom, 1.0F, this, this.totalCompletedQuests);
      }

      ++this.tierQuestsCompleted;
      ++this.totalCompletedQuests;
      return var1;
   }

   public List<InventoryItem> completeTierQuestAndGetReward() {
      this.getTierQuest().remove();
      this.tierQuestUniqueID = 0;
      ArrayList var1 = new ArrayList();
      SettlementQuestTier var2 = this.getCurrentQuestTier();
      if (var2 != null) {
         var2.tierRewardsLootTable(this.data, this.tierQuestsCompleted).addItems(var1, GameRandom.globalRandom, 1.0F, this.data, this, this.totalCompletedQuests);
      }

      int var3 = this.getQuestTiersCompleted() + 1;
      if (var3 >= SettlementQuestTier.questTiers.size()) {
         this.hasCompletedQuestTier = true;
      } else {
         this.questTier = ((SettlementQuestTier)SettlementQuestTier.questTiers.get(var3)).stringID;
      }

      this.tierQuestsCompleted = 0;
      if (var2 != null) {
         this.data.onCompletedQuestTier(var2);
      }

      ++this.totalCompletedQuests;
      return var1;
   }
}
