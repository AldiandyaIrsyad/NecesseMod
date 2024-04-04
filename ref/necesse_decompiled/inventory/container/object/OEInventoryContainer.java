package necesse.inventory.container.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEInventoryNameUpdate;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.OEInventoryContainerSlot;
import necesse.level.maps.Level;

public class OEInventoryContainer extends SettlementDependantContainer {
   public StringCustomAction renameButton;
   public EmptyCustomAction quickStackButton;
   public EmptyCustomAction transferAll;
   public EmptyCustomAction restockButton;
   public EmptyCustomAction lootButton;
   public EmptyCustomAction sortButton;
   public final OEInventory oeInventory;
   public final ObjectEntity objectEntity;
   public final OEUsers oeUsers;
   public int INVENTORY_START = -1;
   public int INVENTORY_END = -1;
   public SettlementContainerObjectStatusManager settlementObjectManager;

   public OEInventoryContainer(final NetworkClient var1, int var2, final OEInventory var3, PacketReader var4) {
      super(var1, var2);
      this.oeInventory = var3;
      this.objectEntity = (ObjectEntity)var3;
      var3.triggerInteracted();
      this.oeUsers = this.objectEntity instanceof OEUsers ? (OEUsers)this.objectEntity : null;
      if (var1.isServer() & this.oeUsers != null) {
         this.oeUsers.startUser(var1.playerMob);
      }

      this.settlementObjectManager = new SettlementContainerObjectStatusManager(this, this.objectEntity.getLevel(), this.objectEntity.getX(), this.objectEntity.getY(), var4);
      InventoryRange var5 = this.getOEInventoryRange();

      for(int var6 = var5.startSlot; var6 <= var5.endSlot; ++var6) {
         int var7 = this.addSlot(this.getOEContainerSlot(var3, var6));
         if (this.INVENTORY_START == -1) {
            this.INVENTORY_START = var7;
         }

         if (this.INVENTORY_END == -1) {
            this.INVENTORY_END = var7;
         }

         this.INVENTORY_START = Math.min(this.INVENTORY_START, var7);
         this.INVENTORY_END = Math.max(this.INVENTORY_END, var7);
      }

      this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
      this.renameButton = (StringCustomAction)this.registerAction(new StringCustomAction() {
         protected void run(String var1x) {
            if (var3.canSetInventoryName()) {
               var3.setInventoryName(var1x);
               if (var1.isServer()) {
                  var1.getServerClient().getServer().network.sendToClientsWithEntity(new PacketOEInventoryNameUpdate(var3, var1x), OEInventoryContainer.this.objectEntity);
               }
            }

         }
      });
      this.quickStackButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var3.canQuickStackInventory()) {
               ArrayList var1x = new ArrayList(Collections.singleton(OEInventoryContainer.this.getOEInventoryRange()));
               OEInventoryContainer.this.quickStackToInventories(var1x, var1.playerMob.getInv().main);
            }

         }
      });
      this.transferAll = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = OEInventoryContainer.this.CLIENT_INVENTORY_START; var1 <= OEInventoryContainer.this.CLIENT_INVENTORY_END; ++var1) {
               if (!OEInventoryContainer.this.getSlot(var1).isItemLocked()) {
                  OEInventoryContainer.this.transferToSlots(OEInventoryContainer.this.getSlot(var1), OEInventoryContainer.this.INVENTORY_START, OEInventoryContainer.this.INVENTORY_END, "transferall");
               }
            }

         }
      });
      this.restockButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(OEInventoryContainer.this.getOEInventoryRange()));
            OEInventoryContainer.this.restockFromInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.lootButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = OEInventoryContainer.this.INVENTORY_START; var1 <= OEInventoryContainer.this.INVENTORY_END; ++var1) {
               if (!OEInventoryContainer.this.getSlot(var1).isItemLocked()) {
                  OEInventoryContainer.this.transferToSlots(OEInventoryContainer.this.getSlot(var1), Arrays.asList(new SlotIndexRange(OEInventoryContainer.this.CLIENT_HOTBAR_START, OEInventoryContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(OEInventoryContainer.this.CLIENT_INVENTORY_START, OEInventoryContainer.this.CLIENT_INVENTORY_END)), "lootall");
               }
            }

         }
      });
      this.sortButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var3.canSortInventory()) {
               InventoryRange var1 = OEInventoryContainer.this.getOEInventoryRange();
               var1.inventory.sortItems(var1.startSlot, var1.endSlot);
            }

         }
      });
   }

   public ContainerSlot getOEContainerSlot(OEInventory var1, int var2) {
      return new OEInventoryContainerSlot(var1, var2);
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
      if (this.client.isServer() & this.oeUsers != null) {
         this.oeUsers.startUser(this.client.playerMob);
      }

   }

   protected Level getLevel() {
      return this.objectEntity.getLevel();
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         Level var2 = var1.getLevel();
         return !this.objectEntity.removed() && var2.getObject(this.objectEntity.getX(), this.objectEntity.getY()).inInteractRange(var2, this.objectEntity.getX(), this.objectEntity.getY(), var1.playerMob);
      }
   }

   public OEInventory getOEInventory() {
      return this.oeInventory;
   }

   public InventoryRange getOEInventoryRange() {
      return new InventoryRange(this.oeInventory.getInventory());
   }

   public void onClose() {
      super.onClose();
      if (this.client.isServer() & this.oeUsers != null) {
         this.oeUsers.stopUser(this.client.playerMob);
      }

   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4, Packet var5) {
      if (!var2.isServer()) {
         throw new IllegalStateException("Level must be a server level");
      } else {
         Packet var6 = new Packet();
         PacketWriter var7 = new PacketWriter(var6);
         SettlementContainerObjectStatusManager.writeContent(var1, var2, var3, var4, var7);
         if (var5 != null) {
            var7.putNextContentPacket(var5);
         }

         ObjectEntity var8 = var2.entityManager.getObjectEntity(var3, var4);
         PacketOpenContainer var9 = PacketOpenContainer.ObjectEntity(var0, var8, var6);
         ContainerRegistry.openAndSendContainer(var1, var9);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4) {
      openAndSendContainer(var0, var1, var2, var3, var4, (Packet)null);
   }
}
