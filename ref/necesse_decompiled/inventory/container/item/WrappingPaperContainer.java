package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.PresentItem;
import necesse.inventory.item.miscItem.WrappingPaperItem;

public class WrappingPaperContainer extends Container {
   public final StringCustomAction wrapButton;
   public final int CONTENT_SLOT;
   public final PlayerTempInventory contentInventory;
   public final ContainerSlot paperSlot;

   public WrappingPaperContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      int var5 = var4.getNextInt();
      Packet var6 = var4.getNextContentPacket();
      this.contentInventory = var1.playerMob.getInv().applyTempInventoryPacket(var6, (var1x) -> {
         return this.isClosed();
      });
      this.CONTENT_SLOT = this.addSlot(new ContainerSlot(this.contentInventory, 0) {
         public String getItemInvalidError(InventoryItem var1) {
            String var2 = super.getItemInvalidError(var1);
            if (var2 != null) {
               return var2;
            } else {
               return !(var1.item instanceof PresentItem) ? null : "";
            }
         }
      });
      this.addInventoryQuickTransfer(this.CONTENT_SLOT, this.CONTENT_SLOT);
      ContainerSlot var7 = this.getSlot(var5);
      if (var7 != null && !var7.isClear() && var7.getItem().item instanceof WrappingPaperItem) {
         this.lockSlot(var5);
         this.paperSlot = var7;
      } else {
         this.paperSlot = null;
      }

      this.wrapButton = (StringCustomAction)this.registerAction(new StringCustomAction() {
         protected void run(String var1x) {
            if (var1.isServer()) {
               ServerClient var2 = var1.getServerClient();
               if (WrappingPaperContainer.this.canWrap() && WrappingPaperContainer.this.isValid(var2) && WrappingPaperContainer.this.paperSlot != null) {
                  InventoryItem var3 = WrappingPaperContainer.this.paperSlot.getItem();
                  if (var3 != null && var3.item instanceof WrappingPaperItem) {
                     InventoryItem var4 = new InventoryItem(((WrappingPaperItem)var3.item).presentItemStringID);
                     PresentItem.setupPresent(var4, WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CONTENT_SLOT).getItem(), var1x);
                     InventoryItem var5 = WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CLIENT_DRAGGING_SLOT).getItem();
                     if (var5 != null) {
                        var2.playerMob.getInv().addItemsDropRemaining(var5, "addback", var1.playerMob, false, true);
                     }

                     WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CONTENT_SLOT).setItem((InventoryItem)null);
                     WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CLIENT_DRAGGING_SLOT).setItem(var4);
                     WrappingPaperContainer.this.paperSlot.setAmount(WrappingPaperContainer.this.paperSlot.getItemAmount() - 1);
                  }

                  WrappingPaperContainer.this.close();
               } else {
                  WrappingPaperContainer.this.close();
               }
            }

         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else if (this.paperSlot == null) {
         return false;
      } else if (this.paperSlot.isClear()) {
         return false;
      } else {
         InventoryItem var2 = this.paperSlot.getItem();
         return var2.item instanceof WrappingPaperItem;
      }
   }

   public boolean canWrap() {
      if (this.getSlot(this.CONTENT_SLOT).isClear()) {
         return false;
      } else {
         InventoryItem var1 = this.getSlot(this.CONTENT_SLOT).getItem();
         return !(var1.item instanceof PresentItem);
      }
   }

   public static Packet getContainerContent(ServerClient var0, int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      var3.putNextContentPacket(var0.playerMob.getInv().getTempInventoryPacket(1));
      return var2;
   }
}
