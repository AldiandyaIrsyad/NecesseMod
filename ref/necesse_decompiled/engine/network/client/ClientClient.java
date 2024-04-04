package necesse.engine.network.client;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerBuffs;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerInventoryPart;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerRespawn;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class ClientClient extends NetworkClient {
   private Client client;
   private LevelIdentifier levelIdentifier;
   public int latency;
   public int summonFocusMobUniqueID = -1;
   public boolean loadedPlayer;

   public ClientClient(Client var1, int var2, PacketPlayerGeneral var3) {
      super(var2, var3.authentication);
      this.client = var1;
      this.makeClientClient();
      this.pvpEnabled = false;
      this.setTeamID(-1);
      this.applyGeneralPacket(var3);
   }

   public void applySyncPacket(PacketReader var1) {
      if (var1.getNextBoolean()) {
         if (!this.hasSpawned) {
            this.applySpawned(0);
         }
      } else {
         this.hasSpawned = false;
      }

      if (var1.getNextBoolean()) {
         this.die(var1.getNextInt());
      } else {
         this.isDead = false;
      }

      this.pvpEnabled = var1.getNextBoolean();
      this.setTeamID(var1.getNextInt());
      if (var1.getNextBoolean() && this.client.getLevel() != null && !this.isSamePlace(this.client.getLevel())) {
         if (this.slot == this.client.slot) {
            this.client.reloadMap();
         } else {
            this.client.network.sendPacket(new PacketRequestPlayerData(this.slot));
         }
      }

      if (var1.getNextBoolean()) {
         this.summonFocusMobUniqueID = var1.getNextInt();
      } else {
         this.summonFocusMobUniqueID = -1;
      }

      if (this.slot == this.client.slot) {
         if (var1.getNextBoolean()) {
            this.client.syncOpenContainer(var1.getNextInt());
         } else {
            this.client.syncOpenContainer(-1);
         }
      }

   }

   public void tickMovement(Client var1, float var2) {
      if (this.playerMob != null) {
         if (this.loadedPlayer) {
            if (this.isSamePlace(var1.getLevel())) {
               this.playerMob.setLevel(var1.getLevel());
               synchronized(this.playerMob.getLevel().entityManager.lock) {
                  this.playerMob.getLevel().entityManager.players.updateRegion(this.playerMob);
                  if (!this.isDead() && this.hasSpawned) {
                     this.playerMob.tickMovement(var2);
                  }
               }
            } else {
               this.playerMob.setLevel((Level)null);
               this.playerMob.updateRegion((GameLinkedList)null);
            }
         } else {
            this.playerMob.setLevel((Level)null);
            this.playerMob.updateRegion((GameLinkedList)null);
         }
      }

   }

   public void tick(Client var1) {
      if (this.playerMob != null && this.loadedPlayer && this.isSamePlace(var1.getLevel())) {
         this.playerMob.setLevel(var1.getLevel());
         synchronized(this.playerMob.getLevel().entityManager.lock) {
            if (!this.isDead() && this.hasSpawned) {
               this.playerMob.clientTick();
            }
         }
      }

   }

   public void die(int var1) {
      if (!this.isDead()) {
         this.playerMob.remove(0.0F, 0.0F, (Attacker)null, true);
         this.isDead = true;
         if (this.client.getSlot() == this.slot) {
            this.client.isDead = true;
            this.client.respawnTime = this.client.worldEntity == null ? (long)var1 : this.client.worldEntity.getTime() + (long)var1;
            this.client.closeContainer(false);
         }
      }

   }

   public void respawn(PacketPlayerRespawn var1) {
      if (!this.isSamePlace(var1.levelIdentifier)) {
         this.hasSpawned = false;
      }

      this.setLevelIdentifier(var1.levelIdentifier);
      if (this.playerMob != null) {
         this.playerMob.setHealth(Math.max(this.playerMob.getMaxHealth() / 2, 1));
         this.playerMob.hungerLevel = Math.max(0.5F, this.playerMob.hungerLevel);
         this.playerMob.setPos((float)var1.playerX, (float)var1.playerY, true);
         this.playerMob.dx = 0.0F;
         this.playerMob.dy = 0.0F;
         this.playerMob.restore();
      }

      this.isDead = false;
   }

   public void applyLevelChangePacket(PacketPlayerLevelChange var1) {
      this.setLevelIdentifier(var1.identifier);
      if (this.playerMob != null && !var1.mountFollow) {
         this.playerMob.dismount();
      }

      this.hasSpawned = false;
   }

   public void applyInventoryPacket(PacketPlayerInventory var1) {
      if (this.loadedPlayer) {
         this.playerMob.applyInventoryPacket(var1);
      }

   }

   public void applyInventoryPartPacket(PacketPlayerInventoryPart var1) {
      if (this.loadedPlayer) {
         this.playerMob.getInv().applyInventoryPartPacket(var1);
      }

   }

   public void applyAppearancePacket(PacketPlayerAppearance var1) {
      if (this.loadedPlayer) {
         this.playerMob.applyAppearancePacket(var1);
      }

   }

   public void applyGeneralPacket(PacketPlayerGeneral var1) {
      this.setLevelIdentifier(var1.levelIdentifier);
      Level var2 = null;
      if (this.client.getLevel() != null && this.isSamePlace(this.client.getLevel())) {
         var2 = this.client.getLevel();
      }

      if (this.playerMob == null) {
         this.playerMob = new PlayerMob((long)this.slot, this);
         if (this.client.getSlot() == this.slot) {
            this.playerMob.staySmoothSnapped = true;
         }
      }

      this.playerMob.setLevel(var2);
      this.playerMob.setWorldData(this.client.worldEntity, this.client.worldSettings);
      this.playerMob.applySpawnPacket(new PacketReader(var1.playerSpawnContent));
      this.pvpEnabled = var1.pvpEnabled;
      if (var1.hasSpawned) {
         this.applySpawned(var1.remainingSpawnInvincibilityTime);
      } else {
         this.hasSpawned = false;
      }

      this.setTeamID(var1.team);
      this.playerMob.playerName = var1.name;
      this.playerMob.setUniqueID(this.slot);
      if (!this.isDead) {
         this.playerMob.restore();
      }

      this.playerMob.init();
      if (var1.isDead) {
         this.die(var1.remainingRespawnTime);
      } else {
         this.isDead = false;
      }

      this.loadedPlayer = true;
      this.client.loading.playersPhase.submitLoadedPlayer(this.slot);
   }

   public void applyPacketPlayerBuffs(PacketPlayerBuffs var1) {
      if (this.loadedPlayer) {
         var1.apply(this.playerMob);
         this.playerMob.equipmentBuffManager.updateAll();
      }

   }

   public void resetLoaded() {
      this.loadedPlayer = false;
   }

   public void applySpawned(int var1) {
      this.hasSpawned = true;
      if (this.playerMob != null) {
         this.playerMob.refreshSpawnTime(var1);
         this.playerMob.updateMount();
         this.playerMob.updateRider();
      }

   }

   public boolean pvpEnabled() {
      return this.client.worldSettings.forcedPvP || this.pvpEnabled;
   }

   public String getName() {
      return this.playerMob == null ? "N/A" : this.playerMob.getDisplayName();
   }

   public void setLevelIdentifier(LevelIdentifier var1) {
      if ((this.levelIdentifier == null || !this.levelIdentifier.equals(var1)) && this.playerMob != null) {
         this.playerMob.boomerangs.clear();
         this.playerMob.toolHits.clear();
      }

      this.levelIdentifier = var1;
   }

   public LevelIdentifier getLevelIdentifier() {
      return this.levelIdentifier;
   }

   public Client getClient() {
      return this.client;
   }

   public boolean isLocalClient() {
      return this.slot == this.client.getSlot();
   }
}
