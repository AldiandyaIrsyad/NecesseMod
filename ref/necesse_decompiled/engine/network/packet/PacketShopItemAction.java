package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.ShopContainer;

public class PacketShopItemAction extends Packet {
   public final int networkShopItemID;
   public final int times;
   public final int actionResult;

   public PacketShopItemAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.networkShopItemID = var2.getNextShortUnsigned();
      this.times = var2.getNextShortUnsigned();
      this.actionResult = var2.getNextInt();
   }

   public PacketShopItemAction(ShopContainer.NetworkShopItem var1, int var2, int var3) {
      this.networkShopItemID = var1.networkID;
      this.times = var2;
      this.actionResult = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextShortUnsigned(this.networkShopItemID);
      var4.putNextShortUnsigned(var2);
      var4.putNextInt(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Container var4 = var3.getContainer();
      if (var4 instanceof ShopContainer) {
         ShopContainer var5 = (ShopContainer)var4;
         int var6 = var5.fulfillTrade(this.networkShopItemID, this.times);
         if (var6 != this.actionResult) {
            var3.getContainer().markFullDirty();
         }
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      Container var3 = var2.getContainer();
      if (var3 instanceof ShopContainer) {
         ShopContainer var4 = (ShopContainer)var3;
         var4.fulfillTrade(this.networkShopItemID, this.times);
      }

   }
}
