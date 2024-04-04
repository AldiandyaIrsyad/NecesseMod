package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;

public class PacketPlayerItemMobInteract extends Packet {
   public final float playerX;
   public final float playerY;
   public final int inventoryID;
   public final int inventorySlot;
   public final int itemID;
   public final int attackX;
   public final int attackY;
   public final int mobUniqueID;
   public final int animAttack;
   public final int seed;
   public final Packet content;

   public PacketPlayerItemMobInteract(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.playerX = var2.getNextFloat();
      this.playerY = var2.getNextFloat();
      this.inventoryID = var2.getNextShortUnsigned();
      this.inventorySlot = var2.getNextShortUnsigned();
      this.itemID = var2.getNextShortUnsigned();
      this.attackX = var2.getNextInt();
      this.attackY = var2.getNextInt();
      this.mobUniqueID = var2.getNextInt();
      this.animAttack = var2.getNextShortUnsigned();
      this.seed = var2.getNextShortUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketPlayerItemMobInteract(PlayerMob var1, PlayerInventorySlot var2, Item var3, int var4, int var5, Mob var6, int var7, int var8, Packet var9) {
      this.playerX = var1.x;
      this.playerY = var1.y;
      this.inventoryID = var2.inventoryID;
      this.inventorySlot = var2.slot;
      this.itemID = var3.getID();
      this.attackX = var4;
      this.attackY = var5;
      this.mobUniqueID = var6.getUniqueID();
      this.animAttack = var7;
      this.seed = var8;
      this.content = var9;
      PacketWriter var10 = new PacketWriter(this);
      var10.putNextFloat(this.playerX);
      var10.putNextFloat(this.playerY);
      var10.putNextShortUnsigned(this.inventoryID);
      var10.putNextShortUnsigned(this.inventorySlot);
      var10.putNextShortUnsigned(this.itemID);
      var10.putNextInt(var4);
      var10.putNextInt(var5);
      var10.putNextInt(this.mobUniqueID);
      var10.putNextShortUnsigned(var7);
      var10.putNextShortUnsigned(this.seed);
      var10.putNextContentPacket(var9);
   }

   public PlayerInventorySlot getSlot() {
      return new PlayerInventorySlot(this.inventoryID, this.inventorySlot);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.checkHasRequestedSelf() && !var3.isDead()) {
         var3.checkSpawned();
         Mob var4 = (Mob)var3.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         if (var4 != null) {
            var3.playerMob.runInteract(this, var4);
         } else {
            var3.sendPacket(new PacketRemoveMob(this.mobUniqueID));
         }

         var3.refreshAFKTimer();
      }
   }
}
