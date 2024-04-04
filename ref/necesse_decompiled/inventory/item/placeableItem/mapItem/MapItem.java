package necesse.inventory.item.placeableItem.mapItem;

import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class MapItem extends PlaceableItem {
   public MapItem(int var1) {
      super(var1, true);
      this.dropsAsMatDeathPenalty = true;
      this.keyWords.add("map");
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }
}
