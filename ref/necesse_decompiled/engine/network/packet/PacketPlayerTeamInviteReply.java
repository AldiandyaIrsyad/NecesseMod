package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PacketPlayerTeamInviteReply extends Packet {
   public final int teamID;
   public final boolean accept;

   public PacketPlayerTeamInviteReply(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.teamID = var2.getNextInt();
      this.accept = var2.getNextBoolean();
   }

   public PacketPlayerTeamInviteReply(int var1, boolean var2) {
      this.teamID = var1;
      this.accept = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextBoolean(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      boolean var4 = false;
      if (var3.teamInvites.contains(this.teamID)) {
         var3.teamInvites.remove(this.teamID);
         if (this.accept) {
            PlayerTeam var5 = var2.world.getTeams().getTeam(this.teamID);
            if (var5 != null) {
               PlayerTeam.addMember(var2, var5, var3.authentication);
            } else {
               var4 = true;
            }
         } else {
            var4 = true;
         }
      } else {
         var4 = true;
      }

      if (var4) {
         PvPTeamsContainer.sendSingleUpdate(var3);
      }

   }
}
