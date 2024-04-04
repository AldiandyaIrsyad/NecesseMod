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

public class MiningTripExpedition extends SettlerExpedition {
   public String questTierStringID;
   public int baseCost;
   public int valueGottenMin;
   public int valueGottenMax;
   public String[] stoneStringIDs;
   public String[] primaryOreStringIDs;
   public String[] secondaryOreStringIDs;

   public MiningTripExpedition(String var1, int var2, int var3, int var4, String[] var5, String[] var6, String... var7) {
      this.questTierStringID = var1;
      this.baseCost = var2;
      this.valueGottenMin = var3;
      this.valueGottenMax = var4;
      this.stoneStringIDs = var5;
      this.primaryOreStringIDs = var6;
      this.secondaryOreStringIDs = var7;
   }

   public MiningTripExpedition(String var1, int var2, int var3, int var4, String var5, String var6, String... var7) {
      this(var1, var2, var3, var4, var5 == null ? null : new String[]{var5}, new String[]{var6}, var7);
   }

   public void initDisplayName() {
      if (this.primaryOreStringIDs.length != 0) {
         this.displayName = ItemRegistry.getItem(this.primaryOreStringIDs[0]).getNewLocalization();
      } else {
         super.initDisplayName();
      }

   }

   public float getSuccessChance(SettlementLevelData var1, HumanMob var2) {
      if (this.questTierStringID == null) {
         return 1.0F;
      } else {
         return (double)questProgressSuccessChance(var1, this.questTierStringID) > 0.5 ? 1.0F : 0.0F;
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
      String[] var4 = this.primaryOreStringIDs;
      int var5 = var4.length;

      int var6;
      String var7;
      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var3.addObject(400, new InventoryItem(var7, Integer.MAX_VALUE));
      }

      var4 = this.secondaryOreStringIDs;
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var3.addObject(100, new InventoryItem(var7, Integer.MAX_VALUE));
      }

      int var9 = GameRandom.globalRandom.getIntBetween(this.valueGottenMin, this.valueGottenMax);
      ArrayList var10 = GameLootUtils.getItemsValuedAt(GameRandom.globalRandom, var9, 0.8F, var3);
      var10.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
      if (this.stoneStringIDs != null) {
         String var11 = (String)GameRandom.globalRandom.getOneOf((Object[])this.stoneStringIDs);
         if (var11 != null) {
            int var12 = var10.stream().mapToInt(InventoryItem::getAmount).sum();
            int var8 = (int)((float)var12 * GameRandom.globalRandom.getFloatBetween(2.0F, 3.5F));
            var10.add(new InventoryItem(var11, var8));
         }
      }

      return var10;
   }
}
