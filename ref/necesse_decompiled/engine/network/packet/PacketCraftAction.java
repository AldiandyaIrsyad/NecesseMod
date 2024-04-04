package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.Container;

public class PacketCraftAction extends Packet {
   public final int recipeID;
   public final int recipeHash;
   public final int craftAmount;
   public final int actionResult;
   public final boolean transferToInventory;

   public PacketCraftAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.recipeID = var2.getNextInt();
      this.recipeHash = var2.getNextInt();
      this.craftAmount = var2.getNextShortUnsigned();
      this.actionResult = var2.getNextByteUnsigned();
      this.transferToInventory = var2.getNextBoolean();
   }

   public PacketCraftAction(int var1, int var2, int var3, int var4, boolean var5) {
      this.recipeID = var1;
      this.recipeHash = var2;
      this.craftAmount = var3;
      this.actionResult = var4;
      this.transferToInventory = var5;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(var1);
      var6.putNextInt(var2);
      var6.putNextShortUnsigned(var3);
      var6.putNextByteUnsigned(var4);
      var6.putNextBoolean(var5);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      int var4 = var3.getContainer().applyCraftingAction(this.recipeID, this.recipeHash, this.craftAmount, this.transferToInventory);
      if (((byte)var4 & 255) != this.actionResult) {
         var3.getContainer().markFullDirty();
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      Container var3 = var2.getContainer();
      if (var3 != null) {
         var3.applyCraftingAction(this.recipeID, this.recipeHash, this.craftAmount, this.transferToInventory);
      }

   }
}
