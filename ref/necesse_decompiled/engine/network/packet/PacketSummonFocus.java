package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketSummonFocus extends Packet {
   public final int slot;
   public final int mobUniqueID;

   public PacketSummonFocus(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.mobUniqueID = var2.getNextInt();
   }

   public PacketSummonFocus(int var1, Mob var2) {
      this.slot = var1;
      this.mobUniqueID = var2 == null ? -1 : var2.getUniqueID();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextInt(this.mobUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot == var3.slot) {
         if (var3.checkHasRequestedSelf() && !var3.isDead()) {
            Mob var4 = var3.summonFocus;
            if (this.mobUniqueID == -1) {
               var3.summonFocus = null;
            } else {
               if (var3.getLevel() == null) {
                  return;
               }

               var3.summonFocus = GameUtils.getLevelMob(this.mobUniqueID, var3.getLevel());
               if (var3.summonFocus == null) {
                  var3.sendPacket(new PacketRemoveMob(this.mobUniqueID));
               }
            }

            if (var4 != var3.summonFocus) {
               var2.network.sendToAllClients(new PacketSummonFocus(this.slot, var3.summonFocus));
            }

         }
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 != null) {
         var3.summonFocusMobUniqueID = this.mobUniqueID;
      }

   }
}
