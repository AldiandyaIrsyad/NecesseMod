package necesse.inventory.item.miscItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootTablePresets;

public class ChristmasPresentItem extends MultiTextureMatItem {
   public ChristmasPresentItem() {
      super(4, 50);
      this.rarity = Item.Rarity.COMMON;
      this.setItemCategory(new String[]{"misc"});
      this.incinerationTimeMillis = 30000;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "christmaspresenttip"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      return var4;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (var1.getClient().isServer()) {
            ArrayList var3 = new ArrayList();
            LootTablePresets.christmasPresents.addItems(var3, GameRandom.globalRandom, 1.0F, var1.getClient());
            Iterator var4x = var3.iterator();

            while(var4x.hasNext()) {
               InventoryItem var5 = (InventoryItem)var4x.next();
               var1.getClient().playerMob.getInv().addItemsDropRemaining(var5, "addback", var1.getClient().playerMob, true, false);
            }
         }

         var4.setAmount(var2.getAmount() - 1);
         if (var2.getAmount() <= 0) {
            var4.setItem((InventoryItem)null);
         }

         return new ContainerActionResult(154617259 * (var2.getAmount() + GameRandom.prime(4)));
      };
   }
}
