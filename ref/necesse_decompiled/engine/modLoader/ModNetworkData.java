package necesse.engine.modLoader;

import com.codedisaster.steamworks.SteamNativeHandle;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class ModNetworkData {
   public final String id;
   public final String name;
   public final String version;
   public final boolean clientside;
   public final long workshopID;

   public ModNetworkData(LoadedMod var1) {
      this.id = var1.id;
      this.version = var1.version;
      this.name = var1.name;
      this.clientside = var1.clientside;
      if (var1.workshopFileID != null) {
         this.workshopID = SteamNativeHandle.getNativeHandle(var1.workshopFileID);
      } else {
         this.workshopID = -1L;
      }

   }

   public ModNetworkData(PacketReader var1) {
      this.id = var1.getNextString();
      this.version = var1.getNextString();
      this.name = var1.getNextString();
      this.clientside = var1.getNextBoolean();
      if (var1.getNextBoolean()) {
         this.workshopID = var1.getNextLong();
      } else {
         this.workshopID = -1L;
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextString(this.id);
      var1.putNextString(this.version);
      var1.putNextString(this.name);
      var1.putNextBoolean(this.clientside);
      var1.putNextBoolean(this.workshopID != -1L);
      if (this.workshopID != -1L) {
         var1.putNextLong(this.workshopID);
      }

   }
}
