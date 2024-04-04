package necesse.inventory.container.teams;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.customAction.ContainerCustomAction;

public class InviteMembersAction extends ContainerCustomAction {
   public final PvPTeamsContainer container;

   public InviteMembersAction(PvPTeamsContainer var1) {
      this.container = var1;
   }

   public void runAndSend(ClientClient... var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextShortUnsigned(var1.length);
      ClientClient[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ClientClient var7 = var4[var6];
         var3.putNextByteUnsigned(var7.slot);
      }

      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      if (this.container.client.isServer()) {
         ServerClient var2 = this.container.client.getServerClient();
         PlayerTeam var3 = var2.getPlayerTeam();
         if (var3 != null) {
            int var4 = var1.getNextShortUnsigned();

            for(int var5 = 0; var5 < var4; ++var5) {
               int var6 = var1.getNextByteUnsigned();
               ServerClient var7 = var2.getServer().getClient(var6);
               if (var7 != null) {
                  PlayerTeam.invitePlayer(var2.getServer(), var3, var7);
               }
            }

         }
      }
   }
}
