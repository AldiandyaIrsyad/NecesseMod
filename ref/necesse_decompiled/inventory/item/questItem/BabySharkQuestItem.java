package necesse.inventory.item.questItem;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;

public class BabySharkQuestItem extends QuestFishItem {
   public BabySharkQuestItem() {
      super(new LocalMessage("itemtooltip", "babysharkobtain"));
   }

   public FishingLootTable getExtraFishingLoot(ServerClient var1, FishingSpot var2) {
      return var2.tile.getHeight() <= -10 && var1.playerMob.getInv().getAmount(this, false, true, true, "questdrop") <= 0 ? (new FishingLootTable()).addWater(400, (String)this.getStringID()) : super.getExtraFishingLoot(var1, var2);
   }
}
