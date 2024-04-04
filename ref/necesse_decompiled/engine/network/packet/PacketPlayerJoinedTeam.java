package necesse.engine.network.packet;

import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class PacketPlayerJoinedTeam extends Packet {
   public final long auth;
   public final String name;
   public final int teamID;
   public final String teamName;

   public PacketPlayerJoinedTeam(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.auth = var2.getNextLong();
      this.name = var2.getNextString();
      this.teamID = var2.getNextShort();
      this.teamName = var2.getNextString();
   }

   public PacketPlayerJoinedTeam(Server var1, long var2) {
      this.auth = var2;
      ServerClient var4 = var1.getClientByAuth(var2);
      PlayerTeam var5;
      if (var4 != null) {
         this.name = var4.getName();
         var5 = var4.getPlayerTeam();
         if (var5 == null) {
            throw new IllegalStateException("Could not find team for " + var4.getName());
         }

         this.teamID = var5.teamID;
         this.teamName = var5.getName();
      } else {
         this.name = (String)var1.usedNames.getOrDefault(var2, "N/A");
         var5 = var1.world.getTeams().getPlayerTeam(var2);
         if (var5 == null) {
            throw new IllegalStateException("Could not find team for " + this.name + " auth " + var2);
         }

         this.teamID = var5.teamID;
         this.teamName = var5.getName();
      }

      PacketWriter var6 = new PacketWriter(this);
      var6.putNextLong(var2);
      var6.putNextString(this.name);
      var6.putNextShort((short)this.teamID);
      var6.putNextString(this.teamName);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient();
      if (var3 != null && (var3.authentication == this.auth || var3.getTeamID() == this.teamID) && !this.teamName.isEmpty()) {
         var2.chat.addMessage(Localization.translate("misc", "teamjoin", "player", this.name, "team", this.teamName));
      }

      ClientClient var4 = var2.getClientByAuth(this.auth);
      if (var4 != null) {
         var4.setTeamID(this.teamID);
      }

   }
}
