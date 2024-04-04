package necesse.inventory.item.questItem;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.desert.DesertBiome;

public class SandRayQuestItem extends QuestFishItem {
   public SandRayQuestItem() {
      super(new LocalMessage("itemtooltip", "sandrayobtain"));
   }

   public FishingLootTable getExtraFishingLoot(ServerClient var1, FishingSpot var2) {
      return var2.tile.level.biome instanceof DesertBiome && var1.playerMob.getInv().getAmount(this, false, true, true, "questdrop") <= 0 ? (new FishingLootTable()).addWater(400, (String)this.getStringID()) : super.getExtraFishingLoot(var1, var2);
   }
}
