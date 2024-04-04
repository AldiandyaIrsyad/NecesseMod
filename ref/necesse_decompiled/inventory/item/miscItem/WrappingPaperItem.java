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
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.WrappingPaperContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class WrappingPaperItem extends Item {
   public String presentItemStringID;

   public WrappingPaperItem(String var1) {
      super(100);
      this.presentItemStringID = var1;
      this.rarity = Item.Rarity.COMMON;
      this.setItemCategory(new String[]{"misc"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "wrappingpapertip"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      return var4;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (var4.getInventory() == var1.getClient().playerMob.getInv().main) {
            if (var1.getClient().isServer()) {
               ServerClient var3x = var1.getClient().getServerClient();
               PacketOpenContainer var4x = new PacketOpenContainer(ContainerRegistry.WRAPPING_PAPER_CONTAINER, WrappingPaperContainer.getContainerContent(var3x, var3));
               ContainerRegistry.openAndSendContainer(var3x, var4x);
            }

            return new ContainerActionResult(181503442);
         } else {
            return new ContainerActionResult(188304063, Localization.translate("itemtooltip", "rclickinvopenerror"));
         }
      };
   }
}
