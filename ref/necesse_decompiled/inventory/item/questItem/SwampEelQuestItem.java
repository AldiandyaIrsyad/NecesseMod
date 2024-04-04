package necesse.inventory.item.questItem;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.swamp.SwampBiome;

public class SwampEelQuestItem extends QuestFishItem {
   public SwampEelQuestItem() {
      super(new LocalMessage("itemtooltip", "swampeelobtain"));
   }

   public FishingLootTable getExtraFishingLoot(ServerClient var1, FishingSpot var2) {
      return var2.tile.level.biome instanceof SwampBiome && var2.tile.level.isCave && var1.playerMob.getInv().getAmount(this, false, true, true, "questdrop") <= 0 ? (new FishingLootTable()).addWater(400, (String)this.getStringID()) : super.getExtraFishingLoot(var1, var2);
   }
}
