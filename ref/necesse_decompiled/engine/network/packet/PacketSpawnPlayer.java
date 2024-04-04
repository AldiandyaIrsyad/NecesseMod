package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketSpawnPlayer extends Packet {
   public final int slot;
   public final LevelIdentifier levelIdentifier;
   public final int remainingSpawnInvincibilityTime;

   public PacketSpawnPlayer(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      if (var2.getNextBoolean()) {
         this.levelIdentifier = new LevelIdentifier(var2);
         this.remainingSpawnInvincibilityTime = var2.getNextInt();
      } else {
         this.levelIdentifier = null;
         this.remainingSpawnInvincibilityTime = 0;
      }

   }

   public PacketSpawnPlayer(Client var1) {
      this.slot = var1.getSlot();
      this.levelIdentifier = null;
      this.remainingSpawnInvincibilityTime = 0;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      var2.putNextBoolean(false);
   }

   public PacketSpawnPlayer(ServerClient var1) {
      this.slot = var1.slot;
      this.levelIdentifier = var1.getLevelIdentifier();
      this.remainingSpawnInvincibilityTime = var1.playerMob == null ? 0 : var1.playerMob.getRemainingSpawnInvincibilityTime();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      var2.putNextBoolean(true);
      this.levelIdentifier.writePacket(var2);
      var2.putNextInt(this.remainingSpawnInvincibilityTime);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot == var3.slot) {
         var3.submitSpawnPacket(this);
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 != null && this.levelIdentifier != null) {
         var3.applySpawned(this.remainingSpawnInvincibilityTime);
         var3.setLevelIdentifier(this.levelIdentifier);
      } else {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      }

   }
}
