package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.mountItem.MountItem;

public class PacketPlayerUseMount extends Packet {
   public final int slot;
   public final int itemID;
   public final float playerX;
   public final float playerY;

   public PacketPlayerUseMount(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.itemID = var2.getNextShortUnsigned();
      this.playerX = var2.getNextFloat();
      this.playerY = var2.getNextFloat();
   }

   public PacketPlayerUseMount(int var1, PlayerMob var2, Item var3) {
      this.slot = var1;
      this.itemID = var3.getID();
      this.playerX = var2.x;
      this.playerY = var2.y;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var4.putNextShortUnsigned(this.itemID);
      var4.putNextFloat(this.playerX);
      var4.putNextFloat(this.playerY);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot == var3.slot) {
         if (var3.checkHasRequestedSelf() && !var3.isDead()) {
            var3.checkSpawned();
            InventoryItem var4 = var3.playerMob.getInv().equipment.getItem(0);
            if (var4 != null && var4.item.getID() == this.itemID && var4.item.isMountItem()) {
               MountItem var5 = (MountItem)var4.item;
               if (var5.canUseMount(var4, var3.playerMob, var3.getLevel()) == null || Settings.giveClientsPower) {
                  double var6 = var3.playerMob.allowServerMovement(var2, var3, this.playerX, this.playerY);
                  if (var6 <= 0.0) {
                     var3.playerMob.setPos(this.playerX, this.playerY, false);
                  } else {
                     GameLog.warn.println(var3.getName() + " attempted to use mount from wrong position, snapping back " + var6);
                     var2.network.sendToClientsAt(new PacketPlayerMovement(var3, false), (ServerClient)var3);
                  }

                  var3.playerMob.getInv().equipment.setItem(0, var5.useMount(var3, this.playerX, this.playerY, var4, var3.getLevel()));
               }
            } else {
               var3.playerMob.getInv().equipment.markDirty(0);
            }

         }
      }
   }
}
