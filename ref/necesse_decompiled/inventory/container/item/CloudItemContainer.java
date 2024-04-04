package necesse.inventory.container.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.CloudContainerSlot;
import necesse.inventory.item.Item;

public class CloudItemContainer extends Container {
   public final EmptyCustomAction quickStackButton;
   public final EmptyCustomAction transferAll;
   public final EmptyCustomAction restockButton;
   public final EmptyCustomAction lootButton;
   public final EmptyCustomAction sortButton;
   public final int itemID;
   public final int startSlot;
   public final int endSlot;
   public int CLOUD_START = -1;
   public int CLOUD_END = -1;

   public CloudItemContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      this.itemID = var4.getNextShortUnsigned();
      PlayerInventory var5 = var1.playerMob.getInv().cloud;
      this.startSlot = Math.max(0, var4.getNextShortUnsigned());
      this.endSlot = Math.max(var4.getNextShortUnsigned(), this.startSlot);
      if (var5.getSize() <= this.endSlot) {
         var5.changeSize(this.endSlot + 1);
      }

      for(int var6 = this.startSlot; var6 <= this.endSlot; ++var6) {
         int var7 = this.addSlot(new CloudContainerSlot(var5, var6, this.itemID));
         if (this.CLOUD_START == -1) {
            this.CLOUD_START = var7;
         }

         if (this.CLOUD_END == -1) {
            this.CLOUD_END = var7;
         }

         this.CLOUD_START = Math.min(this.CLOUD_START, var7);
         this.CLOUD_END = Math.max(this.CLOUD_END, var7);
      }

      this.addInventoryQuickTransfer(this.CLOUD_START, this.CLOUD_END);
      this.quickStackButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(new InventoryRange(var1.playerMob.getInv().cloud, CloudItemContainer.this.startSlot, CloudItemContainer.this.endSlot)));
            CloudItemContainer.this.quickStackToInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.transferAll = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = CloudItemContainer.this.CLIENT_INVENTORY_START; var1 <= CloudItemContainer.this.CLIENT_INVENTORY_END; ++var1) {
               if (!CloudItemContainer.this.getSlot(var1).isItemLocked()) {
                  CloudItemContainer.this.transferToSlots(CloudItemContainer.this.getSlot(var1), CloudItemContainer.this.CLOUD_START, CloudItemContainer.this.CLOUD_END, "transferall");
               }
            }

         }
      });
      this.restockButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(new InventoryRange(var1.playerMob.getInv().cloud, CloudItemContainer.this.startSlot, CloudItemContainer.this.endSlot)));
            CloudItemContainer.this.restockFromInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.lootButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = CloudItemContainer.this.CLOUD_START; var1 <= CloudItemContainer.this.CLOUD_END; ++var1) {
               if (!CloudItemContainer.this.getSlot(var1).isItemLocked()) {
                  CloudItemContainer.this.transferToSlots(CloudItemContainer.this.getSlot(var1), Arrays.asList(new SlotIndexRange(CloudItemContainer.this.CLIENT_HOTBAR_START, CloudItemContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(CloudItemContainer.this.CLIENT_INVENTORY_START, CloudItemContainer.this.CLIENT_INVENTORY_END)), "lootall");
               }
            }

         }
      });
      this.sortButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            var1.playerMob.getInv().cloud.sortItems(CloudItemContainer.this.startSlot, CloudItemContainer.this.endSlot);
         }
      });
   }

   public void lootAllControlPressed() {
      this.lootButton.runAndSend();
   }

   public void sortInventoryControlPressed() {
      this.sortButton.runAndSend();
   }

   public void quickStackControlPressed() {
      this.quickStackButton.runAndSend();
   }

   public static Packet getContainerContent(ServerClient var0, Item var1, int var2, int var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextShortUnsigned(var1.getID());
      var5.putNextShortUnsigned(var2);
      var5.putNextShortUnsigned(var3);
      return var4;
   }
}
