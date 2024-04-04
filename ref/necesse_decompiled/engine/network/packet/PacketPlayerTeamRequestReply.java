package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PacketPlayerTeamRequestReply extends Packet {
   public final long auth;
   public final boolean accept;

   public PacketPlayerTeamRequestReply(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.auth = var2.getNextLong();
      this.accept = var2.getNextBoolean();
   }

   public PacketPlayerTeamRequestReply(long var1, boolean var3) {
      this.auth = var1;
      this.accept = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextLong(var1);
      var4.putNextBoolean(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      PlayerTeam var4 = var3.getPlayerTeam();
      if (var4 == null) {
         if (var3.joinRequests.contains(this.auth)) {
            var4 = var2.world.getTeams().createNewTeam(var3);
            PlayerTeam.addMember(var2, var4, this.auth);
         } else {
            PvPTeamsContainer.sendSingleUpdate(var3);
         }
      } else if (var4.hasJoinRequested(this.auth)) {
         if (this.accept) {
            PlayerTeam.addMember(var2, var4, this.auth);
         } else {
            PlayerTeam.removeJoinRequest(var2, var4, this.auth);
         }
      } else {
         PvPTeamsContainer.sendSingleUpdate(var3);
      }

   }
}
