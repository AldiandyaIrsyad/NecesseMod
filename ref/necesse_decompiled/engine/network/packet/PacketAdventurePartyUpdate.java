package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartyUpdate extends Packet {
   private final PacketReader reader;

   public PacketAdventurePartyUpdate(byte[] var1) {
      super(var1);
      this.reader = new PacketReader(this);
   }

   public PacketAdventurePartyUpdate(ServerClient var1) {
      PacketWriter var2 = new PacketWriter(this);
      this.reader = new PacketReader(var2);
      var1.adventureParty.writeUpdatePacket(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.adventureParty.readUpdatePacket(new PacketReader(this.reader));
      var2.adventureParty.updateMobsFromLevel(var2.getLevel());
   }
}
