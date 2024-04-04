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

public class PacketActiveTrinketBuffAbilityUpdate extends Packet {
   public final int slot;
   public final int uniqueID;
   public final Packet content;

   public PacketActiveTrinketBuffAbilityUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.uniqueID = var2.getNextInt();
      this.content = var2.getNextContentPacket();
   }

   public PacketActiveTrinketBuffAbilityUpdate(int var1, int var2, Packet var3) {
      this.slot = var1;
      this.uniqueID = var2;
      this.content = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var4.putNextInt(var2);
      var4.putNextContentPacket(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ClientClient var3 = var2.getClient(this.slot);
         if (var3 != null && var3.playerMob.getLevel() != null) {
            if (!var3.playerMob.runActiveTrinketBuffAbilityUpdate(this.uniqueID, this.content)) {
               var2.network.sendPacket(new PacketRequestActiveTrinketBuffAbility(this.slot));
            }
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

         if (var3.playerMob.runActiveTrinketBuffAbilityUpdate(this.uniqueID, this.content)) {
            var2.network.sendToClientsAtExcept(new PacketActiveTrinketBuffAbilityUpdate(this.slot, this.uniqueID, this.content), (ServerClient)var3, var3);
         } else {
            var3.playerMob.sendActiveTrinketBuffAbilityState(var2, var3);
         }
      } else {
         GameLog.warn.println(var3.getName() + " tried to run active buff ability update from wrong slot, kicking him for desync");
         var2.disconnectClient(var3, PacketDisconnect.Code.STATE_DESYNC);
      }

   }
}
