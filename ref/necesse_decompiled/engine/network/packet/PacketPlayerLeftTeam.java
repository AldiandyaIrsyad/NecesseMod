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

public class PacketPlayerLeftTeam extends Packet {
   public final long auth;
   public final String name;
   public final int teamID;
   public final String teamName;
   public final boolean isKick;

   public PacketPlayerLeftTeam(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.auth = var2.getNextLong();
      this.name = var2.getNextString();
      this.teamID = var2.getNextShort();
      this.teamName = var2.getNextString();
      this.isKick = var2.getNextBoolean();
   }

   public PacketPlayerLeftTeam(Server var1, long var2, PlayerTeam var4, boolean var5) {
      this.auth = var2;
      ServerClient var6 = var1.getClientByAuth(var2);
      if (var6 != null) {
         this.name = var6.getName();
         this.teamID = var4.teamID;
         this.teamName = var4.getName();
      } else {
         this.name = (String)var1.usedNames.getOrDefault(var2, "N/A");
         this.teamID = var4.teamID;
         this.teamName = var4.getName();
      }

      this.isKick = var5;
      PacketWriter var7 = new PacketWriter(this);
      var7.putNextLong(var2);
      var7.putNextString(this.name);
      var7.putNextShort((short)this.teamID);
      var7.putNextString(this.teamName);
      var7.putNextBoolean(var5);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient();
      if (var3 != null && (var3.authentication == this.auth || var3.getTeamID() == this.teamID) && !this.teamName.isEmpty()) {
         var2.chat.addMessage(Localization.translate("misc", this.isKick ? "teamkick" : "teamleft", "player", this.name, "team", this.teamName));
      }

      ClientClient var4 = var2.getClientByAuth(this.auth);
      if (var4 != null) {
         var4.setTeamID(-1);
      }

   }
}
