package necesse.inventory.item.miscItem;

import java.util.function.Supplier;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.CloudItemContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CloudInventoryOpenItem extends Item {
   private boolean easyInsert;
   private int startSlot;
   private int endSlot;

   public CloudInventoryOpenItem(boolean var1, int var2, int var3) {
      super(1);
      this.easyInsert = var1;
      this.startSlot = var2;
      this.endSlot = var3;
      this.worldDrawSize = 32;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (var1.isServer()) {
         ServerClient var11 = var4.getServerClient();
         if (!(var11.getContainer() instanceof CloudItemContainer) || ((CloudItemContainer)var11.getContainer()).itemID != this.getID()) {
            PacketOpenContainer var12 = new PacketOpenContainer(ContainerRegistry.CLOUD_INVENTORY_CONTAINER, CloudItemContainer.getContainerContent(var11, this, this.startSlot, this.endSlot));
            ContainerRegistry.openAndSendContainer(var11, var12);
         }
      }

      return var6;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         if (var1.getClient().isServer()) {
            ServerClient var2 = var1.getClient().getServerClient();
            if (!(var2.getContainer() instanceof CloudItemContainer) || ((CloudItemContainer)var2.getContainer()).itemID != this.getID()) {
               PacketOpenContainer var3 = new PacketOpenContainer(ContainerRegistry.CLOUD_INVENTORY_CONTAINER, CloudItemContainer.getContainerContent(var2, this, this.startSlot, this.endSlot));
               ContainerRegistry.openAndSendContainer(var2, var3);
            }
         }

         return new ContainerActionResult(208675834);
      };
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      if (var4 == null) {
         return false;
      } else {
         return this.isSameItem(var1, var3, var4, var5) || this.easyInsert && var2 != null && (var5.equals("leftclick") || var5.equals("leftclickinv") || var5.equals("rightclick"));
      }
   }

   public boolean onCombine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, int var7, int var8, boolean var9, String var10, InventoryAddConsumer var11) {
      if (this.easyInsert && var2 != null && (var10.equals("leftclick") || var10.equals("leftclickinv") || var10.equals("rightclick"))) {
         PlayerInventory var12 = var2.getInv().cloud;
         int var13 = Math.min(var8, var6.getAmount());
         InventoryItem var14 = var6.copy(var13);
         var12.addItem(var1, var2, var14, "pouchinsert", var11);
         if (var14.getAmount() != var13) {
            int var15 = var13 - var14.getAmount();
            var6.setAmount(var6.getAmount() - var15);
            return true;
         } else {
            return false;
         }
      } else {
         return super.onCombine(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }
}
