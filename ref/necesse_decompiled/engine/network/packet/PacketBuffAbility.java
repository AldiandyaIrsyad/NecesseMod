package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PacketBuffAbility extends Packet {
   public final int slot;
   public final int buffID;
   public final Packet content;

   public PacketBuffAbility(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.buffID = var2.getNextShortUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketBuffAbility(int var1, Buff var2, Packet var3) {
      this.slot = var1;
      this.buffID = var2.getID();
      this.content = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var4.putNextShortUnsigned(this.buffID);
      var4.putNextContentPacket(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ClientClient var3 = var2.getClient(this.slot);
         if (var3 != null && var3.playerMob.getLevel() != null) {
            ActiveBuff var4 = var3.playerMob.buffManager.getBuff(this.buffID);
            if (var4 != null && var4.buff instanceof BuffAbility) {
               ((BuffAbility)var4.buff).runAbility(var3.playerMob, var4, this.content);
            }
         } else {
            var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
         }

      }
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.slot == this.slot) {
         if (!var3.checkHasRequestedSelf() || var3.isDead()) {
            return;
         }

         ActiveBuff var4 = var3.playerMob.buffManager.getBuff(this.buffID);
         if (var4 != null && var4.buff instanceof BuffAbility && ((BuffAbility)var4.buff).canRunAbility(var3.playerMob, var4, this.content)) {
            ((BuffAbility)var4.buff).runAbility(var3.playerMob, var4, this.content);
            var2.network.sendToClientsAtExcept(new PacketBuffAbility(this.slot, var4.buff, this.content), (ServerClient)var3, var3);
         }
      } else {
         GameLog.warn.println(var3.getName() + " tried to run buff ability from wrong slot, kicking him for desync");
         var2.disconnectClient(var3, PacketDisconnect.Code.STATE_DESYNC);
      }

   }
}
