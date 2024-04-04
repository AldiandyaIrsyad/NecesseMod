package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.FeedingTroughMob;
import necesse.inventory.InventoryItem;

public class PacketTroughFeed extends Packet {
   public final int mobUniqueID;
   public final Packet itemContent;

   public PacketTroughFeed(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.itemContent = var2.getNextContentPacket();
   }

   public PacketTroughFeed(Mob var1, InventoryItem var2) {
      this.mobUniqueID = var1.getUniqueID();
      this.itemContent = InventoryItem.getContentPacket(var2);
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextContentPacket(this.itemContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 instanceof FeedingTroughMob) {
            ((FeedingTroughMob)var3).onFed(InventoryItem.fromContentPacket(this.itemContent));
         }

      }
   }
}
