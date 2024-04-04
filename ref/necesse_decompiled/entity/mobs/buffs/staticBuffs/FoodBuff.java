package necesse.entity.mobs.buffs.staticBuffs;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.gameNetworkData.GNDItemGameItem;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;

public class FoodBuff extends Buff {
   public FoodBuff(boolean var1) {
      this.canCancel = var1;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      FoodConsumableItem var3 = getFoodItem(var1);
      if (var3 != null) {
         ModifierValue[] var4 = var3.modifiers;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ModifierValue var7 = var4[var6];
            var7.apply(var1);
         }
      }

   }

   public void setFoodItem(ActiveBuff var1, FoodConsumableItem var2) {
      var1.getGndData().setItem("foodItem", new GNDItemGameItem(var2));
   }

   public static FoodConsumableItem getFoodItem(ActiveBuff var0) {
      int var1 = GNDItemGameItem.getItemID(var0.getGndData(), "foodItem");
      Item var2 = ItemRegistry.getItem(var1);
      return var2 != null && var2.isFoodItem() ? (FoodConsumableItem)var2 : null;
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      LinkedList var4 = var1.getModifierTooltips();
      if (var4.isEmpty()) {
         var3.add(Localization.translate("bufftooltip", "nomodifiers"));
      } else {
         Stream var10000 = var1.getModifierTooltips().stream().map((var0) -> {
            return var0.toTooltip(true);
         });
         Objects.requireNonNull(var3);
         var10000.forEach(var3::add);
      }

      return var3;
   }

   public void drawIcon(int var1, int var2, ActiveBuff var3) {
      FoodConsumableItem var4 = getFoodItem(var3);
      if (var4 != null) {
         this.iconTexture = var4.buffTexture;
      }

      super.drawIcon(var1, var2, var3);
   }
}
