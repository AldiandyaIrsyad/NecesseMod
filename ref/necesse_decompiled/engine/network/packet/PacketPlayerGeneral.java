package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketPlayerGeneral extends Packet {
   public final int slot;
   public final long authentication;
   public final int characterUniqueID;
   public final LevelIdentifier levelIdentifier;
   public final boolean pvpEnabled;
   public final boolean isDead;
   public final boolean hasSpawned;
   public final int remainingRespawnTime;
   public final int remainingSpawnInvincibilityTime;
   public final int team;
   public final String name;
   public final Packet playerSpawnContent;

   public PacketPlayerGeneral(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.authentication = var2.getNextLong();
      this.characterUniqueID = var2.getNextInt();
      this.levelIdentifier = new LevelIdentifier(var2);
      this.pvpEnabled = var2.getNextBoolean();
      this.isDead = var2.getNextBoolean();
      if (this.isDead) {
         this.remainingRespawnTime = var2.getNextInt();
      } else {
         this.remainingRespawnTime = 0;
      }

      this.hasSpawned = var2.getNextBoolean();
      boolean var3 = var2.getNextBoolean();
      if (var3) {
         this.remainingSpawnInvincibilityTime = var2.getNextInt();
      } else {
         this.remainingSpawnInvincibilityTime = 0;
      }

      this.team = var2.getNextShort();
      this.name = var2.getNextString();
      this.playerSpawnContent = var2.getNextContentPacket();
   }

   public PacketPlayerGeneral(ServerClient var1) {
      this.slot = var1.slot;
      this.authentication = var1.authentication;
      this.characterUniqueID = var1.getCharacterUniqueID();
      this.levelIdentifier = var1.getLevelIdentifier();
      this.pvpEnabled = var1.pvpEnabled;
      this.isDead = var1.isDead();
      this.remainingRespawnTime = var1.getRespawnTimeRemaining();
      this.hasSpawned = var1.hasSpawned();
      this.remainingSpawnInvincibilityTime = var1.playerMob == null ? 0 : var1.playerMob.getRemainingSpawnInvincibilityTime();
      this.team = var1.getTeamID();
      this.name = var1.getName();
      this.playerSpawnContent = new Packet();
      var1.playerMob.setupSpawnPacket(new PacketWriter(this.playerSpawnContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      var2.putNextLong(this.authentication);
      var2.putNextInt(this.characterUniqueID);
      this.levelIdentifier.writePacket(var2);
      var2.putNextBoolean(this.pvpEnabled);
      var2.putNextBoolean(this.isDead);
      if (this.isDead) {
         var2.putNextInt(this.remainingRespawnTime);
      }

      var2.putNextBoolean(this.hasSpawned);
      if (this.remainingSpawnInvincibilityTime > 0) {
         var2.putNextBoolean(true);
         var2.putNextInt(this.remainingSpawnInvincibilityTime);
      } else {
         var2.putNextBoolean(false);
      }

      var2.putNextShort((short)this.team);
      var2.putNextString(this.name);
      var2.putNextContentPacket(this.playerSpawnContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.applyPlayerGeneralPacket(this);
   }
}
