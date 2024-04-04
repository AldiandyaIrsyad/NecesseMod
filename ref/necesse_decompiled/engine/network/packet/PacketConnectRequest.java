package necesse.engine.network.packet;

import necesse.engine.GameAuth;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketConnectRequest extends Packet {
   public final long auth;
   public final int modHash;
   public final int passwordHash;
   public final String version;
   public final boolean craftingUsesNearbyInventories;

   public PacketConnectRequest(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.auth = var2.getNextLong();
      this.modHash = var2.getNextInt();
      this.passwordHash = var2.getNextInt();
      this.version = var2.getNextString();
      this.craftingUsesNearbyInventories = var2.getNextBoolean();
   }

   public PacketConnectRequest(String var1) {
      this.auth = GameAuth.getAuthentication();
      this.modHash = ModLoader.getModsHash();
      this.passwordHash = var1.hashCode();
      this.version = "0.24.2";
      this.craftingUsesNearbyInventories = (Boolean)Settings.craftingUseNearby.get();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextLong(this.auth);
      var2.putNextInt(this.modHash);
      var2.putNextInt(this.passwordHash);
      var2.putNextString(this.version);
      var2.putNextBoolean(this.craftingUsesNearbyInventories);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      String var4 = var1.networkInfo == null ? null : var1.networkInfo.getDisplayName();
      if (this.auth > 0L && this.auth <= 500L && !GlobalData.isDevMode()) {
         var2.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, new StaticMessage("Denied: Invalid dev auth")), var1.networkInfo));
         System.out.println("Denied connection from auth " + this.auth + " (" + var4 + ") because server is not running as dev");
      } else if (!var2.settings.password.equals("") && this.passwordHash == 0) {
         var2.network.sendPacket(new NetworkPacket(new PacketRequestPassword(var2), var1.networkInfo));
         System.out.println("Auth " + this.auth + " (" + var4 + ") connected with no password");
      } else if (!var2.settings.password.equals("") && this.passwordHash != var2.settings.password.hashCode()) {
         var2.network.sendPacket(new NetworkPacket(PacketDisconnect.wrongPassword(var2), var1.networkInfo));
         System.out.println("Auth " + this.auth + " (" + var4 + ") connected with wrong password");
      } else if (ModLoader.getModsHash() != this.modHash) {
         var2.network.sendPacket(new NetworkPacket(new PacketModsMismatch(), var1.networkInfo));
         System.out.println("Auth " + this.auth + " (" + var4 + ") connected with wrong mods");
      } else {
         var2.addClient(var1.networkInfo, this.auth, this.version, this.craftingUsesNearbyInventories);
      }

   }
}
