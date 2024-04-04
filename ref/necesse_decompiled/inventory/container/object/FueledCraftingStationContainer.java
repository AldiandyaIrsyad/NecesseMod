package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.slots.OEInventoryContainerSlot;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class FueledCraftingStationContainer extends CraftingStationContainer {
   public final FueledInventoryObjectEntity objectEntity;
   public BooleanCustomAction setKeepRunning;
   public int INVENTORY_START = -1;
   public int INVENTORY_END = -1;
   public final OEUsers oeUsers;

   public FueledCraftingStationContainer(NetworkClient var1, int var2, final FueledInventoryObjectEntity var3, PacketReader var4) {
      super(var1, var2, new LevelObject(var3.getLevel(), var3.getX(), var3.getY()), var4);
      this.objectEntity = var3;
      this.oeUsers = var3 instanceof OEUsers ? (OEUsers)var3 : null;
      if (var1.isServer() & this.oeUsers != null) {
         this.oeUsers.startUser(var1.playerMob);
      }

      for(int var5 = 0; var5 < var3.getInventory().getSize(); ++var5) {
         int var6 = this.addSlot(new OEInventoryContainerSlot(var3, var5));
         if (this.INVENTORY_START == -1) {
            this.INVENTORY_START = var6;
         }

         if (this.INVENTORY_END == -1) {
            this.INVENTORY_END = var6;
         }

         this.INVENTORY_START = Math.min(this.INVENTORY_START, var6);
         this.INVENTORY_END = Math.max(this.INVENTORY_END, var6);
      }

      this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
      this.setKeepRunning = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            var3.keepRunning = var1;
            var3.markFuelDirty();
         }
      });
   }

   public int applyCraftingAction(int var1, int var2, int var3, boolean var4) {
      if (!this.objectEntity.isFueled()) {
         this.objectEntity.useFuel();
         if (!this.objectEntity.isFueled()) {
            return 0;
         }
      }

      return super.applyCraftingAction(var1, var2, var3, var4);
   }

   public void tick() {
      super.tick();
      if (this.client.isServer() & this.oeUsers != null) {
         this.oeUsers.startUser(this.client.playerMob);
      }

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
