package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartySync extends Packet {
   public final int mobsHash;

   public PacketAdventurePartySync(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobsHash = var2.getNextInt();
   }

   public PacketAdventurePartySync(ServerClient var1) {
      this.mobsHash = var1.adventureParty.getMobsHash();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobsHash);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (this.mobsHash != var2.adventureParty.getMobsHash()) {
         var2.network.sendPacket(new PacketAdventurePartyRequestUpdate());
      }

   }
}
