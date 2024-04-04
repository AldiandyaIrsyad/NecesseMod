package necesse.engine.expeditions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class TypesFishingTripExpedition extends FishingTripExpedition {
   public String questTierStringID;
   public int baseCost;
   public int valueGottenMin;
   public int valueGottenMax;
   public String[] primaryFishStringIDs;
   public String[] secondaryFishStringIDs;

   public TypesFishingTripExpedition(String var1, int var2, int var3, int var4, String[] var5, String... var6) {
      this.questTierStringID = var1;
      this.baseCost = var2;
      this.valueGottenMin = var3;
      this.valueGottenMax = var4;
      this.primaryFishStringIDs = var5;
      this.secondaryFishStringIDs = var6;
   }

   public TypesFishingTripExpedition(String var1, int var2, int var3, int var4, String var5, String... var6) {
      this(var1, var2, var3, var4, new String[]{var5}, var6);
   }

   public void initDisplayName() {
      if (this.primaryFishStringIDs.length != 0) {
         this.displayName = ItemRegistry.getItem(this.primaryFishStringIDs[0]).getNewLocalization();
      } else {
         super.initDisplayName();
      }

   }

   public GameMessage getUnavailableMessage() {
      return new LocalMessage("expedition", "completequests");
   }

   public int getBaseCost(SettlementLevelData var1, HumanMob var2) {
      return this.baseCost;
   }

   public List<InventoryItem> getRewardItems(SettlementLevelData var1, HumanMob var2) {
      TicketSystemList var3 = new TicketSystemList();
      String[] var4 = this.primaryFishStringIDs;
      int var5 = var4.length;

      int var6;
      String var7;
      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var3.addObject(400, new InventoryItem(var7, Integer.MAX_VALUE));
      }

      var4 = this.secondaryFishStringIDs;
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var3.addObject(100, new InventoryItem(var7, Integer.MAX_VALUE));
      }

      int var8 = GameRandom.globalRandom.getIntBetween(this.valueGottenMin, this.valueGottenMax);
      ArrayList var9 = GameLootUtils.getItemsValuedAt(GameRandom.globalRandom, var8, 0.8F, var3);
      var9.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
      return var9;
   }

   public float getSuccessChance(SettlementLevelData var1, HumanMob var2) {
      return this.questTierStringID == null ? 1.0F : questProgressSuccessChance(var1, this.questTierStringID, 2);
   }
}
