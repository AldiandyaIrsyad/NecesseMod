package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.AdventureParty;

public class PacketAdventurePartyAdd extends Packet {
   public final int mobUniqueID;
   public final int mobsHash;

   public PacketAdventurePartyAdd(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.mobsHash = var2.getNextInt();
   }

   public PacketAdventurePartyAdd(AdventureParty var1, int var2) {
      this.mobUniqueID = var2;
      this.mobsHash = var1.getMobsHash();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var2);
      var3.putNextInt(this.mobsHash);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.adventureParty.clientAdd(this.mobUniqueID);
      if (this.mobsHash != var2.adventureParty.getMobsHash()) {
         var2.network.sendPacket(new PacketAdventurePartyRequestUpdate());
      }

   }
}
