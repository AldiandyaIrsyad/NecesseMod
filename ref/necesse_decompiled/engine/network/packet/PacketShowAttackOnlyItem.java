package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;

public class PacketShowAttackOnlyItem extends PacketShowAttack {
   public PacketShowAttackOnlyItem(byte[] var1) {
      super(var1);
   }

   public PacketShowAttackOnlyItem(PlayerMob var1, InventoryItem var2, int var3, int var4, int var5, Packet var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null && var3.getLevel() != null) {
         var3.setPos(this.playerX, this.playerY, false);
         InventoryItem var4 = InventoryItem.fromContentPacket(this.itemContent);
         if (var4 != null) {
            var4.item.showAttack(var3.getLevel(), this.attackX, this.attackY, var3, var3.getCurrentAttackHeight(), var4, this.seed, new PacketReader(this.attackContent));
         }
      }

   }
}
