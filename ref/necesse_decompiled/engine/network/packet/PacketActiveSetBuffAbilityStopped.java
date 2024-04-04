package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketActiveSetBuffAbilityStopped extends Packet {
   public final int slot;
   public final int uniqueID;

   public PacketActiveSetBuffAbilityStopped(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.uniqueID = var2.getNextInt();
   }

   public PacketActiveSetBuffAbilityStopped(int var1, int var2) {
      this.slot = var1;
      this.uniqueID = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextInt(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ClientClient var3 = var2.getClient(this.slot);
         if (var3 != null && var3.playerMob.getLevel() != null) {
            var3.playerMob.onActiveSetBuffAbilityStopped(this.uniqueID);
         } else {
            var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
         }

      }
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.slot == this.slot) {
         if (!var3.checkHasRequestedSelf() || var3.isDead()) {
            return;
         }

         var3.playerMob.onActiveSetBuffAbilityStopped(this.uniqueID);
         var2.network.sendToClientsAtExcept(new PacketActiveSetBuffAbilityStopped(this.slot, this.uniqueID), (ServerClient)var3, var3);
      } else {
         GameLog.warn.println(var3.getName() + " tried to run active set buff ability update from wrong slot, kicking him for desync");
         var2.disconnectClient(var3, PacketDisconnect.Code.STATE_DESYNC);
      }

   }
}
