package necesse.inventory.container.object;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.SalvageStationObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.SalvagableItemContainerSlot;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.level.maps.Level;

public class SalvageStationContainer extends Container {
   public int SALVAGE_INVENTORY_START = -1;
   public int SALVAGE_INVENTORY_END = -1;
   public final SalvageStationObjectEntity salvageEntity;
   public final EmptyCustomAction salvageButton;

   public SalvageStationContainer(NetworkClient var1, int var2, SalvageStationObjectEntity var3, PacketReader var4) {
      super(var1, var2);
      this.salvageEntity = var3;

      for(int var5 = 0; var5 < var3.inventory.getSize(); ++var5) {
         int var6 = this.addSlot(new SalvagableItemContainerSlot(var3.inventory, var5));
         if (this.SALVAGE_INVENTORY_START == -1) {
            this.SALVAGE_INVENTORY_START = var6;
         }

         if (this.SALVAGE_INVENTORY_END == -1) {
            this.SALVAGE_INVENTORY_END = var6;
         }

         this.SALVAGE_INVENTORY_START = Math.min(this.SALVAGE_INVENTORY_START, var6);
         this.SALVAGE_INVENTORY_END = Math.max(this.SALVAGE_INVENTORY_END, var6);
      }

      this.addInventoryQuickTransfer(this.SALVAGE_INVENTORY_START, this.SALVAGE_INVENTORY_END);
      this.salvageButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            SalvageStationContainer.this.salvageItems();
         }
      });
   }

   public ArrayList<InventoryItem> getCurrentSalvageRewards(boolean var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < this.salvageEntity.inventory.getSize(); ++var3) {
         InventoryItem var4 = this.salvageEntity.inventory.getItem(var3);
         if (var4 != null && var4.item instanceof SalvageableItem && ((SalvageableItem)var4.item).getCanBeSalvagedError(var4) == null) {
            Iterator var5 = ((SalvageableItem)var4.item).getSalvageRewards(var4).iterator();

            while(var5.hasNext()) {
               InventoryItem var6 = (InventoryItem)var5.next();
               var6.combineOrAddToList(this.salvageEntity.getLevel(), (PlayerMob)null, var2, "add");
            }

            if (var1) {
               if (this.client.isServer()) {
                  this.client.getServerClient().newStats.items_salvaged.increment(var4.getAmount());
               }

               this.salvageEntity.inventory.clearSlot(var3);
            }
         }
      }

      return var2;
   }

   public void salvageItems() {
      ArrayList var1 = this.getCurrentSalvageRewards(true);
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            InventoryItem var3 = (InventoryItem)var2.next();
            this.client.playerMob.getInv().addItemsDropRemaining(var3, "salvage", this.client.playerMob, !this.client.isServer(), false, true);
         }
      }

   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         return !this.salvageEntity.removed() && this.salvageEntity.getLevelObject().inInteractRange(var1.playerMob);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4, Packet var5) {
      if (!var2.isServer()) {
         throw new IllegalStateException("Level must be a server level");
      } else {
         Packet var6 = new Packet();
         PacketWriter var7 = new PacketWriter(var6);
         if (var5 != null) {
            var7.putNextContentPacket(var5);
         }

         PacketOpenContainer var8 = PacketOpenContainer.LevelObject(var0, var3, var4, var6);
         ContainerRegistry.openAndSendContainer(var1, var8);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4) {
      openAndSendContainer(var0, var1, var2, var3, var4, (Packet)null);
   }
}
