package necesse.inventory.item.trinketItem;

import java.util.Arrays;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SimpleTrinketItem extends TrinketItem {
   private String[] buffStringIDs;

   public SimpleTrinketItem(Item.Rarity var1, String[] var2, int var3) {
      super(var1, var3);
      this.buffStringIDs = var2;
   }

   public SimpleTrinketItem(Item.Rarity var1, String var2, int var3) {
      this(var1, new String[]{var2}, var3);
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      return (TrinketBuff[])Arrays.stream(this.buffStringIDs).map((var0) -> {
         return (TrinketBuff)BuffRegistry.getBuff(var0);
      }).toArray((var0) -> {
         return new TrinketBuff[var0];
      });
   }
}
