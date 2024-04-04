package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.Level;

public class PacketMobBuffRemove extends Packet {
   public final int mobUniqueID;
   public final int buffID;

   public PacketMobBuffRemove(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.buffID = var2.getNextShortUnsigned();
   }

   public PacketMobBuffRemove(int var1, int var2) {
      this.mobUniqueID = var1;
      this.buffID = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextShortUnsigned(var2);
   }

   public void applyPacket(Level var1) {
      if (var1 != null) {
         this.applyPacket(GameUtils.getLevelMob(this.mobUniqueID, var1));
      }
   }

   public void applyPacket(Mob var1) {
      if (var1 != null) {
         var1.buffManager.removeBuff(BuffRegistry.getBuffStringID(this.buffID), false);
      }
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.mobUniqueID == var3.slot) {
         ActiveBuff var4 = var3.playerMob.buffManager.getBuff(BuffRegistry.getBuffStringID(this.buffID));
         if (var4 != null) {
            if (var4.canCancel()) {
               this.applyPacket((Mob)var3.playerMob);
               var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
            } else if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
               if (var2.world.settings.allowCheats) {
                  this.applyPacket((Mob)var3.playerMob);
                  var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
               } else {
                  System.out.println(var3.getName() + " tried to remove invalid own buff, but cheats aren't allowed");
               }
            } else {
               System.out.println(var3.getName() + " tried to remove invalid own buff, but isn't admin");
            }
         }
      } else if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            this.applyPacket(var2.world.getLevel(var3));
            var2.network.sendToClientsAt(this, (ServerClient)var3);
         } else {
            System.out.println(var3.getName() + " tried to remove mob buff, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to remove mob buff, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (this.mobUniqueID >= 0 && this.mobUniqueID < var2.getSlots()) {
         ClientClient var3 = var2.getClient(this.mobUniqueID);
         if (var3 != null && var3.playerMob != null) {
            this.applyPacket((Mob)var3.playerMob);
         }
      } else {
         this.applyPacket(var2.getLevel());
      }

   }
}
