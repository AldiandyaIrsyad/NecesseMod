package necesse.inventory.item.trinketItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class CombinedTrinketItem extends TrinketItem {
   public ArrayList<String> trinketStringIDs;

   public CombinedTrinketItem(Item.Rarity var1, int var2, String... var3) {
      super(var1, var2);
      this.trinketStringIDs = new ArrayList(Arrays.asList(var3));
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      TrinketBuff[] var2 = new TrinketBuff[0];
      TrinketItem[] var3 = (TrinketItem[])this.streamCombinedTrinkets().toArray((var0) -> {
         return new TrinketItem[var0];
      });
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TrinketItem var6 = var3[var5];
         var2 = (TrinketBuff[])GameUtils.concat(var2, var6.getBuffs(var1));
      }

      return var2;
   }

   public boolean disabledBy(InventoryItem var1) {
      return super.disabledBy(var1) ? true : this.streamCombinedTrinkets().anyMatch((var1x) -> {
         return var1x.disabledBy(var1);
      });
   }

   public boolean disables(InventoryItem var1) {
      if (super.disables(var1)) {
         return true;
      } else {
         return this.trinketStringIDs.stream().anyMatch((var1x) -> {
            return var1x.equals(var1.item.getStringID());
         }) ? true : this.streamCombinedTrinkets().anyMatch((var1x) -> {
            return var1x.disables(var1);
         });
      }
   }

   public Stream<TrinketItem> streamCombinedTrinkets() {
      return this.trinketStringIDs.stream().map((var0) -> {
         return (TrinketItem)ItemRegistry.getItem(var0);
      });
   }
}
