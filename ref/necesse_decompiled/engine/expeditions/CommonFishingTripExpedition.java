package necesse.engine.expeditions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class CommonFishingTripExpedition extends FishingTripExpedition {
   public static List<String> commonFishStringIDs = new ArrayList();

   public CommonFishingTripExpedition() {
      commonFishStringIDs.add("carp");
      commonFishStringIDs.add("cod");
      commonFishStringIDs.add("herring");
      commonFishStringIDs.add("mackerel");
      commonFishStringIDs.add("salmon");
      commonFishStringIDs.add("trout");
      commonFishStringIDs.add("tuna");
   }

   public GameMessage getUnavailableMessage() {
      return new LocalMessage("expedition", "completequests");
   }

   public int getBaseCost(SettlementLevelData var1, HumanMob var2) {
      return 300;
   }

   public List<InventoryItem> getRewardItems(SettlementLevelData var1, HumanMob var2) {
      TicketSystemList var3 = new TicketSystemList();
      Iterator var4 = commonFishStringIDs.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.addObject(100, new InventoryItem(var5, Integer.MAX_VALUE));
      }

      int var6 = GameRandom.globalRandom.getIntBetween(400, 500);
      ArrayList var7 = GameLootUtils.getItemsValuedAt(GameRandom.globalRandom, var6, 0.8F, var3);
      var7.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
      return var7;
   }
}
