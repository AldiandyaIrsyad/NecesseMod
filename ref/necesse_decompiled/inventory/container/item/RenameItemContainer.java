package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.slots.ContainerSlot;

public class RenameItemContainer extends Container {
   public static int MAX_NAME_LENGTH = 40;
   public final StringCustomAction renameButton;
   public final ContainerSlot itemSlot;

   public RenameItemContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      int var5 = var4.getNextInt();
      ContainerSlot var6 = this.getSlot(var5);
      if (var6 != null && !var6.isClear()) {
         this.lockSlot(var5);
         this.itemSlot = var6;
      } else {
         this.itemSlot = null;
      }

      this.renameButton = (StringCustomAction)this.registerAction(new StringCustomAction() {
         protected void run(String var1x) {
            if (var1.isServer()) {
               ServerClient var2 = var1.getServerClient();
               if (RenameItemContainer.this.canRename(var1x) && RenameItemContainer.this.isValid(var2) && RenameItemContainer.this.itemSlot != null) {
                  InventoryItem var3 = RenameItemContainer.this.itemSlot.getItem();
                  if (var3 != null) {
                     if (var1x.isEmpty()) {
                        var3.getGndData().setItem("name", (GNDItem)null);
                     } else {
                        var3.getGndData().setItem("name", new GNDItemString(var1x));
                     }

                     RenameItemContainer.this.itemSlot.markDirty();
                  }

                  RenameItemContainer.this.close();
               } else {
                  RenameItemContainer.this.close();
               }
            }

         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else if (this.itemSlot == null) {
         return false;
      } else {
         return !this.itemSlot.isClear();
      }
   }

   public boolean canRename(String var1) {
      return var1.length() <= MAX_NAME_LENGTH;
   }

   public static Packet getContainerContent(int var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextInt(var0);
      return var1;
   }
}
