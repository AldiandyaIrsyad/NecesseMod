package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;

public class PacketPlayerItemInteract extends Packet {
   public final float playerX;
   public final float playerY;
   public final int inventoryID;
   public final int inventorySlot;
   public final int itemID;
   public final int attackX;
   public final int attackY;
   public final int animAttack;
   public final int seed;
   public final Packet content;

   public PacketPlayerItemInteract(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.playerX = var2.getNextFloat();
      this.playerY = var2.getNextFloat();
      this.inventoryID = var2.getNextShortUnsigned();
      this.inventorySlot = var2.getNextShortUnsigned();
      this.itemID = var2.getNextShortUnsigned();
      this.attackX = var2.getNextShortUnsigned();
      this.attackY = var2.getNextShortUnsigned();
      this.animAttack = var2.getNextShortUnsigned();
      this.seed = var2.getNextShortUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketPlayerItemInteract(PlayerMob var1, PlayerInventorySlot var2, Item var3, int var4, int var5, int var6, int var7, Packet var8) {
      this.playerX = var1.x;
      this.playerY = var1.y;
      this.inventoryID = var2.inventoryID;
      this.inventorySlot = var2.slot;
      this.itemID = var3.getID();
      this.attackX = var4;
      this.attackY = var5;
      this.animAttack = var6;
      this.seed = var7;
      this.content = var8;
      PacketWriter var9 = new PacketWriter(this);
      var9.putNextFloat(this.playerX);
      var9.putNextFloat(this.playerY);
      var9.putNextShortUnsigned(this.inventoryID);
      var9.putNextShortUnsigned(this.inventorySlot);
      var9.putNextShortUnsigned(this.itemID);
      var9.putNextShortUnsigned(this.attackX);
      var9.putNextShortUnsigned(this.attackY);
      var9.putNextShortUnsigned(var6);
      var9.putNextShortUnsigned(this.seed);
      var9.putNextContentPacket(var8);
   }

   public PlayerInventorySlot getSlot() {
      return new PlayerInventorySlot(this.inventoryID, this.inventorySlot);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.checkHasRequestedSelf() && !var3.isDead()) {
         var3.checkSpawned();
         var3.playerMob.runInteract(this);
         var3.refreshAFKTimer();
      }
   }
}
