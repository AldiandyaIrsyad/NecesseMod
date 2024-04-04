package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketPlayerRespawn extends Packet {
   public final int slot;
   public final LevelIdentifier levelIdentifier;
   public final int playerX;
   public final int playerY;

   public PacketPlayerRespawn(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.levelIdentifier = new LevelIdentifier(var2);
      this.playerX = var2.getNextInt();
      this.playerY = var2.getNextInt();
   }

   public PacketPlayerRespawn(ServerClient var1) {
      this.slot = var1.slot;
      this.levelIdentifier = var1.getLevelIdentifier();
      this.playerX = var1.playerMob.getX();
      this.playerY = var1.playerMob.getY();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      this.levelIdentifier.writePacket(var2);
      var2.putNextInt(this.playerX);
      var2.putNextInt(this.playerY);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         if (this.slot == var2.getSlot()) {
            var2.respawn(this);
            return;
         }

         var3.respawn(this);
      }

   }
}
