package necesse.engine.network.packet;

import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.world.WorldSettings;

public class PacketServerStatus extends Packet {
   public final int state;
   public final long uniqueID;
   public final int playersOnline;
   public final int slots;
   public final boolean passwordProtected;
   public final int modsHash;
   public final String version;
   public final WorldSettings worldSettings;

   public PacketServerStatus(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.state = var2.getNextInt();
      this.uniqueID = var2.getNextLong();
      this.playersOnline = var2.getNextByteUnsigned();
      this.slots = var2.getNextByteUnsigned();
      this.passwordProtected = var2.getNextBoolean();
      this.modsHash = var2.getNextInt();
      this.version = var2.getNextString();
      if (this.version.equals("0.24.2")) {
         this.worldSettings = new WorldSettings((Client)null, var2);
      } else {
         this.worldSettings = null;
      }

   }

   public PacketServerStatus(Server var1, int var2) {
      this.state = var2;
      this.uniqueID = var1.world.getUniqueID();
      this.playersOnline = var1.getPlayersOnline();
      this.slots = var1.getSlots();
      this.passwordProtected = var1.settings.password != null && !var1.settings.password.isEmpty();
      this.modsHash = ModLoader.getModsHash();
      this.version = "0.24.2";
      this.worldSettings = var1.world.settings;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var2);
      var3.putNextLong(this.uniqueID);
      var3.putNextByteUnsigned(this.playersOnline);
      var3.putNextByteUnsigned(this.slots);
      var3.putNextBoolean(this.passwordProtected);
      var3.putNextInt(this.modsHash);
      var3.putNextString(this.version);
      this.worldSettings.setupContentPacket(var3);
   }
}
