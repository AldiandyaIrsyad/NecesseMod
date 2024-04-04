package necesse.inventory.container.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.InternalInventoryItemContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class ItemInventoryContainer extends Container {
   public StringCustomAction renameButton;
   public final EmptyCustomAction quickStackButton;
   public final EmptyCustomAction transferAll;
   public final EmptyCustomAction restockButton;
   public final EmptyCustomAction lootButton;
   public final EmptyCustomAction sortButton;
   public final Inventory inventory;
   public final int itemID;
   public final InternalInventoryItemInterface inventoryItem;
   public final PlayerInventorySlot inventoryItemSlot;
   private InventoryItem item;
   public int INVENTORY_START = -1;
   public int INVENTORY_END = -1;

   public ItemInventoryContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      this.itemID = var4.getNextShortUnsigned();
      int var5 = var4.getNextInt();
      int var6 = var4.getNextInt();
      this.inventoryItemSlot = new PlayerInventorySlot(var5, var6);
      this.item = this.inventoryItemSlot.getItem(var1.playerMob.getInv());
      if (this.item != null && this.item.item.getID() == this.itemID && this.item.item instanceof InternalInventoryItemInterface) {
         this.inventoryItem = (InternalInventoryItemInterface)this.item.item;
         this.lockSlot(this.inventoryItemSlot);
         InternalInventoryItemInterface var7 = (InternalInventoryItemInterface)this.item.item;
         this.inventory = var7.getInternalInventory(this.item);

         for(int var8 = 0; var8 < this.inventory.getSize(); ++var8) {
            int var9 = this.addSlot(this.getItemContainerSlot(this.inventory, var8, var7));
            if (this.INVENTORY_START == -1) {
               this.INVENTORY_START = var9;
            }

            if (this.INVENTORY_END == -1) {
               this.INVENTORY_END = var9;
            }

            this.INVENTORY_START = Math.min(this.INVENTORY_START, var9);
            this.INVENTORY_END = Math.max(this.INVENTORY_END, var9);
         }

         this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
      } else {
         this.inventoryItem = null;
         this.inventory = null;
      }

      this.renameButton = (StringCustomAction)this.registerAction(new StringCustomAction() {
         protected void run(String var1x) {
            if (ItemInventoryContainer.this.inventoryItem != null && ItemInventoryContainer.this.item != null && ItemInventoryContainer.this.inventoryItem.canChangePouchName()) {
               ItemInventoryContainer.this.inventoryItem.setPouchName(ItemInventoryContainer.this.item, var1x);
               if (var1.isServer()) {
                  ItemInventoryContainer.this.inventoryItemSlot.markDirty(var1.playerMob.getInv());
               }
            }

         }
      });
      this.quickStackButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(new InventoryRange(ItemInventoryContainer.this.inventory)));
            ItemInventoryContainer.this.quickStackToInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.transferAll = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = ItemInventoryContainer.this.CLIENT_INVENTORY_START; var1 <= ItemInventoryContainer.this.CLIENT_INVENTORY_END; ++var1) {
               if (!ItemInventoryContainer.this.getSlot(var1).isItemLocked()) {
                  ItemInventoryContainer.this.transferToSlots(ItemInventoryContainer.this.getSlot(var1), ItemInventoryContainer.this.INVENTORY_START, ItemInventoryContainer.this.INVENTORY_END, "transferall");
               }
            }

         }
      });
      this.restockButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(new InventoryRange(ItemInventoryContainer.this.inventory)));
            ItemInventoryContainer.this.restockFromInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.lootButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = ItemInventoryContainer.this.INVENTORY_START; var1 <= ItemInventoryContainer.this.INVENTORY_END; ++var1) {
               if (!ItemInventoryContainer.this.getSlot(var1).isItemLocked()) {
                  ItemInventoryContainer.this.transferToSlots(ItemInventoryContainer.this.getSlot(var1), Arrays.asList(new SlotIndexRange(ItemInventoryContainer.this.CLIENT_HOTBAR_START, ItemInventoryContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(ItemInventoryContainer.this.CLIENT_INVENTORY_START, ItemInventoryContainer.this.CLIENT_INVENTORY_END)), "lootallpouch");
               }
            }

         }
      });
      this.sortButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ItemInventoryContainer.this.inventory.sortItems();
         }
      });
   }

   public ContainerSlot getItemContainerSlot(Inventory var1, int var2, InternalInventoryItemInterface var3) {
      return new InternalInventoryItemContainerSlot(var1, var2, var3);
   }

   public InventoryItem getInventoryItem() {
      return this.item;
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

   public void tick() {
      super.tick();
      InventoryItem var1;
      if (this.client.isClient()) {
         if (this.inventory == null) {
            this.client.getClientClient().getClient().closeContainer(true);
            return;
         }

         var1 = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
         if (this.item != var1 && var1 != null) {
            this.inventory.override(((InternalInventoryItemInterface)var1.item).getInternalInventory(var1));
            this.item = var1;
         }
      }

      if (this.inventory.isDirty()) {
         var1 = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
         if (var1 != null) {
            ((InternalInventoryItemInterface)var1.item).saveInternalInventory(var1, this.inventory);
         }

         this.inventory.clean();
         this.inventoryItemSlot.markDirty(this.client.playerMob.getInv());
      }

   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else if (this.inventoryItemSlot == null) {
         return false;
      } else {
         InventoryItem var2 = this.inventoryItemSlot.getItem(var1.playerMob.getInv());
         return var2 != null && var2.item.getID() == this.itemID && var2.item instanceof InternalInventoryItemInterface;
      }
   }

   public static Packet getContainerContent(InternalInventoryItemInterface var0, PlayerInventorySlot var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextShortUnsigned(((Item)var0).getID());
      var3.putNextInt(var1.inventoryID);
      var3.putNextInt(var1.slot);
      return var2;
   }
}
