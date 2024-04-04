package necesse.inventory.item.miscItem;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class PresentItem extends Item {
   public PresentItem() {
      super(1);
      this.rarity = Item.Rarity.COMMON;
      this.setItemCategory(new String[]{"misc"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      GNDItemMap var5 = var1.getGndData();
      String var6 = var5.getString("message");
      if (var6 != null && !var6.isEmpty()) {
         var4.add((String)var6, 292);
      }

      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      return var4;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         GNDItemMap var3 = var2.getGndData();
         GNDItem var4x = var3.getItem("content");
         InventoryItem var5 = null;
         if (var4x instanceof GNDItemInventoryItem) {
            var5 = ((GNDItemInventoryItem)var4x).invItem;
         }

         if (var2.getAmount() <= 1) {
            var4.setItem(var5);
            return new ContainerActionResult(154456235 * (var2.getAmount() + GameRandom.prime(4)));
         } else {
            if (var5 != null && var1.getClient().isServer()) {
               var1.getClient().playerMob.getInv().addItemsDropRemaining(var5, "addback", var1.getClient().playerMob, false, false);
            }

            var4.setAmount(var2.getAmount() - 1);
            return new ContainerActionResult(60793022 * (var2.getAmount() + GameRandom.prime(4)));
         }
      };
   }

   public float getBrokerValue(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      GNDItem var3 = var2.getItem("content");
      if (var3 instanceof GNDItemInventoryItem) {
         InventoryItem var4 = ((GNDItemInventoryItem)var3).invItem;
         if (var4 != null) {
            return super.getBrokerValue(var1) + var4.getBrokerValue();
         }
      }

      return super.getBrokerValue(var1);
   }

   public static void setupPresent(InventoryItem var0, InventoryItem var1, String var2) {
      GNDItemMap var3 = var0.getGndData();
      var3.setItem("content", new GNDItemInventoryItem(var1));
      var3.setString("message", var2);
   }
}
