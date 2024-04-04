package necesse.level.maps.biomes;

import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.LevelTile;

public class FishingSpot {
   public final LevelTile tile;
   public final FishingRodItem fishingRod;
   public final BaitItem bait;

   public FishingSpot(LevelTile var1, FishingRodItem var2, BaitItem var3) {
      this.tile = var1;
      this.fishingRod = var2;
      this.bait = var3;
   }
}
