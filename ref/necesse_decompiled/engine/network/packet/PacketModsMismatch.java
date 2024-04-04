package necesse.engine.network.packet;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.presets.ModNetworkListMismatchForm;

public class PacketModsMismatch extends Packet {
   public final ArrayList<ModNetworkData> mods;

   public PacketModsMismatch(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      int var3 = var2.getNextShortUnsigned();
      this.mods = new ArrayList(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         ModNetworkData var5 = new ModNetworkData(var2);
         this.mods.add(var5);
      }

   }

   public PacketModsMismatch() {
      PacketWriter var1 = new PacketWriter(this);
      this.mods = new ArrayList(ModLoader.getEnabledMods().size());
      Iterator var2 = ModLoader.getEnabledMods().iterator();

      while(var2.hasNext()) {
         LoadedMod var3 = (LoadedMod)var2.next();
         this.mods.add(new ModNetworkData(var3));
      }

      var1.putNextShortUnsigned(this.mods.size());
      var2 = this.mods.iterator();

      while(var2.hasNext()) {
         ModNetworkData var4 = (ModNetworkData)var2.next();
         var4.write(var1);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      ModNetworkListMismatchForm var3 = new ModNetworkListMismatchForm();
      var3.setup(this.mods);
      var2.error(Localization.translate("disconnect", "modsmismatch"), false, var3);
   }
}
