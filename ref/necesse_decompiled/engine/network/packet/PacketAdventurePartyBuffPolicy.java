package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartyBuffPolicy extends Packet {
   public final AdventureParty.BuffPotionPolicy policy;

   public PacketAdventurePartyBuffPolicy(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.policy = (AdventureParty.BuffPotionPolicy)var2.getNextEnum(AdventureParty.BuffPotionPolicy.class);
   }

   public PacketAdventurePartyBuffPolicy(AdventureParty.BuffPotionPolicy var1) {
      this.policy = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextEnum(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.adventureParty.setBuffPotionPolicy(this.policy, false);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.adventureParty.setBuffPotionPolicy(this.policy, false);
   }
}
