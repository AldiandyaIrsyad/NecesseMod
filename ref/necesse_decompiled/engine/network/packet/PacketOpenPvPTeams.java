package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PacketOpenPvPTeams extends Packet {
   public PacketOpenPvPTeams(byte[] var1) {
      super(var1);
   }

   public PacketOpenPvPTeams() {
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ContainerRegistry.openAndSendContainer(var3, new PacketOpenContainer(ContainerRegistry.PVP_TEAMS_CONTAINER, PvPTeamsContainer.getContainerContent(var3)));
   }
}
