package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class PacketPlayerPlaceItem extends Packet {
   public final int levelIdentifierHashCode;
   public final int clientSlot;
   public final int inventoryID;
   public final int inventorySlot;
   public final int itemID;
   public final int attackX;
   public final int attackY;
   public final String error;
   public final Packet content;

   public PacketPlayerPlaceItem(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.clientSlot = var2.getNextByteUnsigned();
      this.inventoryID = var2.getNextShortUnsigned();
      this.inventorySlot = var2.getNextShortUnsigned();
      this.itemID = var2.getNextShortUnsigned();
      this.attackX = var2.getNextShortUnsigned();
      this.attackY = var2.getNextShortUnsigned();
      if (var2.getNextBoolean()) {
         this.error = var2.getNextString();
      } else {
         this.error = null;
      }

      this.content = var2.getNextContentPacket();
   }

   public PacketPlayerPlaceItem(Level var1, ServerClient var2, PlayerInventorySlot var3, PlaceableItem var4, int var5, int var6, String var7, Packet var8) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.clientSlot = var2.slot;
      this.inventoryID = var3.inventoryID;
      this.inventorySlot = var3.slot;
      this.itemID = var4.getID();
      this.attackX = var5;
      this.attackY = var6;
      this.error = var7;
      this.content = var8;
      PacketWriter var9 = new PacketWriter(this);
      var9.putNextInt(this.levelIdentifierHashCode);
      var9.putNextByteUnsigned(this.clientSlot);
      var9.putNextShortUnsigned(this.inventoryID);
      var9.putNextShortUnsigned(this.inventorySlot);
      var9.putNextShortUnsigned(this.itemID);
      var9.putNextShortUnsigned(this.attackX);
      var9.putNextShortUnsigned(this.attackY);
      var9.putNextBoolean(var7 != null);
      if (var7 != null) {
         var9.putNextString(var7);
      }

      var9.putNextContentPacket(var8);
   }

   public PlayerInventorySlot getSlot() {
      return new PlayerInventorySlot(this.inventoryID, this.inventorySlot);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         ClientClient var3 = var2.getClient(this.clientSlot);
         if (var3 != null && var3.isSamePlace(var2.getLevel())) {
            InventoryItem var4 = var3.playerMob.getInv().getItem(this.getSlot());
            if (var4 != null && var4.item.getID() == this.itemID && var4.item instanceof PlaceableItem) {
               PlaceableItem var5 = (PlaceableItem)var4.item;
               if (this.error == null) {
                  var5.onOtherPlayerPlace(var2.getLevel(), this.attackX, this.attackY, var3.playerMob, var4, new PacketReader(this.content));
               } else {
                  var5.onOtherPlayerPlaceAttempt(var2.getLevel(), this.attackX, this.attackY, var3.playerMob, var4, new PacketReader(this.content), this.error);
               }
            }
         }

      }
   }
}
