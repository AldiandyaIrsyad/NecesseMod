package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;

public class PacketOpenPartyConfig extends Packet {
   public PacketOpenPartyConfig(byte[] var1) {
      super(var1);
   }

   public PacketOpenPartyConfig() {
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ContainerRegistry.openAndSendContainer(var3, new PacketOpenContainer(ContainerRegistry.PARTY_CONFIG_CONTAINER));
   }
}
