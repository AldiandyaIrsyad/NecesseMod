package necesse.inventory.item.miscItem;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.RecipeBookContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class RecipeBookItem extends Item {
   public RecipeBookItem() {
      super(1);
      this.rarity = Item.Rarity.RARE;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "recipebooktip"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      return var4;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         PlayerInventorySlot var2 = null;
         if (var4.getInventory() == var1.getClient().playerMob.getInv().main) {
            var2 = new PlayerInventorySlot(var1.getClient().playerMob.getInv().main, var4.getInventorySlot());
         }

         if (var4.getInventory() == var1.getClient().playerMob.getInv().cloud) {
            var2 = new PlayerInventorySlot(var1.getClient().playerMob.getInv().cloud, var4.getInventorySlot());
         }

         if (var2 != null) {
            if (var1.getClient().isServer()) {
               ServerClient var3 = var1.getClient().getServerClient();
               PacketOpenContainer var4x = new PacketOpenContainer(ContainerRegistry.RECIPE_BOOK_CONTAINER, RecipeBookContainer.getContainerContent(var3, var2));
               ContainerRegistry.openAndSendContainer(var3, var4x);
            }

            return new ContainerActionResult(-551066694);
         } else {
            return new ContainerActionResult(3784210, Localization.translate("itemtooltip", "rclickinvopenerror"));
         }
      };
   }
}
