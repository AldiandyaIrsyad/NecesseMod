package necesse.engine.network.packet;

import com.codedisaster.steamworks.SteamID;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.network.server.network.ServerOpenNetwork;

public class PacketAddSteamInvite extends Packet {
   public final SteamID steamID;

   public PacketAddSteamInvite(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.steamID = SteamID.createFromNativeHandle(var2.getNextLong());
   }

   public PacketAddSteamInvite(SteamID var1) {
      this.steamID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextLong(SteamID.getNativeHandle(var1));
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.steamID.isValid() && (var2.settings.steamLobbyType == ServerSettings.SteamLobbyType.InviteOnly || var2.settings.steamLobbyType == ServerSettings.SteamLobbyType.VisibleToFriends) && var2.network instanceof ServerOpenNetwork) {
         ((ServerOpenNetwork)var2.network).addInvitedUser(this.steamID);
      }

   }
}
