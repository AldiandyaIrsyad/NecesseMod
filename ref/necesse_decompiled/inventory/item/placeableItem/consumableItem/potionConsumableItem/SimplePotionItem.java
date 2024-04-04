package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SimplePotionItem extends PotionConsumableItem {
   private GameMessage[] tooltips;

   public SimplePotionItem(int var1, Item.Rarity var2, String var3, int var4, GameMessage... var5) {
      super(var1, var3, var4);
      this.rarity = var2;
      this.tooltips = var5;
   }

   public SimplePotionItem(int var1, Item.Rarity var2, String var3, int var4, String... var5) {
      this(var1, var2, var3, var4, toTooltips(var5));
   }

   private static GameMessage[] toTooltips(String... var0) {
      GameMessage[] var1 = new GameMessage[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = new LocalMessage("itemtooltip", var0[var2]);
      }

      return var1;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      GameMessage[] var5 = this.tooltips;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         GameMessage var8 = var5[var7];
         if (var8 != null) {
            var4.add(var8.translate());
         }
      }

      var4.add(this.getDurationMessage());
      return var4;
   }

   public boolean isPotion() {
      return true;
   }

   public void setSpoilTime(InventoryItem var1, long var2) {
      super.setSpoilTime(var1, var2);
   }

   public PotionConsumableItem overridePotion(String var1) {
      return super.overridePotion(var1);
   }
}
