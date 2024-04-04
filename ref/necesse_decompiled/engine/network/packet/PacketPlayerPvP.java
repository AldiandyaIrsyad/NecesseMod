package necesse.engine.network.packet;

import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerPvP extends Packet {
   public final int slot;
   public final boolean pvpEnabled;

   public PacketPlayerPvP(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      int var3 = var2.getNextByteUnsigned();
      if (var3 > 250) {
         var3 = (byte)var3;
      }

      this.slot = var3;
      this.pvpEnabled = var2.getNextBoolean();
   }

   public PacketPlayerPvP(int var1, boolean var2) {
      this.slot = var1;
      this.pvpEnabled = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextBoolean(this.pvpEnabled);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot == var3.slot) {
         if (!var2.world.settings.forcedPvP && var3.pvpSetCooldown <= System.currentTimeMillis()) {
            var3.pvpEnabled = this.pvpEnabled;
            var3.pvpSetCooldown = System.currentTimeMillis() + 4800L;
            System.out.println(var3.getName() + " " + (this.pvpEnabled ? "enabled" : "disabled") + " PvP");
         }

         var2.network.sendToAllClients(new PacketPlayerPvP(var3.slot, var3.pvpEnabled));
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (this.slot == -1) {
         var2.worldSettings.forcedPvP = this.pvpEnabled;
      } else if (var2.getClient(this.slot) != null && var2.getClient(this.slot).pvpEnabled != this.pvpEnabled) {
         var2.getClient(this.slot).pvpEnabled = this.pvpEnabled;
         if (var2.loading.isDone()) {
            if (this.pvpEnabled) {
               var2.chat.addMessage(Localization.translate("misc", "pvpenable", "player", var2.getClient(this.slot).getName()));
            } else {
               var2.chat.addMessage(Localization.translate("misc", "pvpdisable", "player", var2.getClient(this.slot).getName()));
            }
         }
      }

   }
}
