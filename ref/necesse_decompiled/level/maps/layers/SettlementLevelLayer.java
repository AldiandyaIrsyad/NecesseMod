package necesse.level.maps.layers;

import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketLevelLayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.HumanLook;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.regionSystem.Region;

public class SettlementLevelLayer extends LevelLayer {
   private boolean nameSet;
   private PlayerStats stats;
   private boolean active;
   private GameMessage settlementName;
   private long ownerAuth = -1L;
   private String ownerName;
   private int teamID = -1;
   private HumanLook look;
   private boolean isPrivate = true;
   private long raidActiveTick = -2147483648L;
   private long raidApproachingTick = -2147483648L;
   private boolean isDirty;

   public SettlementLevelLayer(Level var1) {
      super(var1);
   }

   public void init() {
      if (this.settlementName == null) {
         this.settlementName = new LocalMessage("settlement", "defname", "biome", this.level.biome.getLocalization());
      }

   }

   public void clientTick() {
      super.clientTick();
      if (this.isActive()) {
         GameUtils.streamClientClients(this.level).forEach((var0) -> {
            ActiveBuff var1 = new ActiveBuff(BuffRegistry.SETTLEMENT_FLAG, var0.playerMob, 100, (Attacker)null);
            var0.playerMob.buffManager.addBuff(var1, false);
         });
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.isActive()) {
         GameUtils.streamServerClients(this.level).forEach((var0) -> {
            ActiveBuff var1 = new ActiveBuff(BuffRegistry.SETTLEMENT_FLAG, var0.playerMob, 100, (Attacker)null);
            var0.playerMob.buffManager.addBuff(var1, false);
         });
      }

      if (this.isDirty) {
         if (this.level.isServer()) {
            this.level.getServer().network.sendToClientsAt(new PacketLevelLayerData(this), (Level)this.level);
            SettlementLevelData var1 = SettlementLevelData.getSettlementData(this.level);
            if (var1 != null) {
               (new SettlementBasicsEvent(var1)).applyAndSendToClientsAt(this.level);
            }
         }

         this.isDirty = false;
      }

   }

   public void onLoadingComplete() {
      if (this.level.isServer()) {
         this.updateOwnerVariables();
      }

      if (this.settlementName == null) {
         this.settlementName = new LocalMessage("settlement", "defname", "biome", this.level.biome.getLocalization());
      }

   }

   public void writeLevelDataPacket(PacketWriter var1) {
      super.writeLevelDataPacket(var1);
      var1.putNextBoolean(this.active);
      var1.putNextContentPacket(this.settlementName.getContentPacket());
      var1.putNextLong(this.ownerAuth);
      var1.putNextBoolean(this.ownerName != null);
      if (this.ownerName != null) {
         var1.putNextString(this.ownerName);
      }

      var1.putNextInt(this.teamID);
      var1.putNextBoolean(this.look != null);
      if (this.look != null) {
         this.look.setupContentPacket(var1, true);
      }

      var1.putNextBoolean(this.isPrivate);
   }

   public void readLevelDataPacket(PacketReader var1) {
      super.readLevelDataPacket(var1);
      this.active = var1.getNextBoolean();
      this.settlementName = GameMessage.fromContentPacket(var1.getNextContentPacket());
      this.ownerAuth = var1.getNextLong();
      if (var1.getNextBoolean()) {
         this.ownerName = var1.getNextString();
      } else {
         this.ownerName = null;
      }

      this.teamID = var1.getNextInt();
      if (var1.getNextBoolean()) {
         this.look = new HumanLook(var1);
      } else {
         this.look = null;
      }

      this.isPrivate = var1.getNextBoolean();
   }

   public void writeRegionPacket(Region var1, PacketWriter var2) {
   }

   public boolean readRegionPacket(Region var1, PacketReader var2) {
      return true;
   }

   public void unloadRegion(Region var1) {
   }

   public void addSaveData(SaveData var1) {
      SaveData var2 = new SaveData("settlement");
      var2.addBoolean("active", this.active);
      var2.addBoolean("nameSet", this.nameSet);
      if (this.settlementName != null) {
         var2.addSaveData(this.settlementName.getSaveData("name"));
      }

      var2.addLong("ownerAuth", this.ownerAuth);
      var2.addBoolean("isPrivate", this.isPrivate);
      var1.addSaveData(var2);
   }

   public void loadSaveData(LoadData var1) {
      LoadData var2 = var1.getFirstLoadDataByName("settlement");
      if (var2 != null) {
         this.active = var2.getBoolean("active", false, false);
         this.nameSet = var2.getBoolean("nameSet", this.nameSet, false);
         this.settlementName = getName(var1);
         if (this.settlementName == null) {
            this.settlementName = new LocalMessage("settlement", "defname", "biome", this.level.biome.getLocalization());
         }

         this.ownerAuth = var2.getLong("ownerAuth", -1L);
         this.isPrivate = var2.getBoolean("isPrivate", true);
      }

      this.updateOwnerVariables();
   }

   public void migrate(boolean var1, GameMessage var2, long var3, boolean var5) {
      this.nameSet = var1;
      this.settlementName = var2;
      this.ownerAuth = var3;
      this.isPrivate = var5;
      this.markDirty();
   }

   public static boolean getActive(LoadData var0) {
      LoadData var1 = var0.getFirstLoadDataByName("settlement");
      return var1 != null ? var1.getBoolean("active", false, false) : false;
   }

   public static GameMessage getName(LoadData var0) {
      LoadData var1 = var0.getFirstLoadDataByName("settlement");
      if (var1 != null) {
         LoadData var2 = var1.getFirstLoadDataByName("name");
         if (var2 != null) {
            return GameMessage.loadSave(var2);
         }
      }

      return null;
   }

   public static long getOwnerAuth(LoadData var0) {
      LoadData var1 = var0.getFirstLoadDataByName("settlement");
      return var1 != null ? var1.getLong("ownerAuth", -1L, false) : -1L;
   }

   public static int getTeamID(Server var0, long var1) {
      return var1 == -1L ? -1 : var0.world.getTeams().getPlayerTeamID(var1);
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public void setActive(boolean var1) {
      if (var1 != this.active) {
         this.active = var1;
         this.markDirty();
         this.updateLevelCache();
      }

   }

   public boolean isActive() {
      return this.active;
   }

   public void refreshRaidActive() {
      this.raidActiveTick = this.level.getWorldEntity().getGameTicks();
   }

   public boolean isRaidActive() {
      return this.raidActiveTick > this.level.getWorldEntity().getGameTicks() - 20L;
   }

   public void refreshRaidApproaching() {
      this.raidApproachingTick = this.level.getWorldEntity().getGameTicks();
   }

   public boolean isRaidApproaching() {
      return this.raidApproachingTick > this.level.getWorldEntity().getGameTicks() - 20L;
   }

   public void tryChangeOwner(ServerClient var1) {
      if (this.getOwnerAuth() == -1L || !this.isActive()) {
         this.setOwner(var1);
      }

   }

   public void setOwner(ServerClient var1) {
      if (var1 != null) {
         if (this.ownerAuth != var1.authentication) {
            this.markDirty();
         }

         this.updateOwnerVariables(var1);
         if (var1.achievementsLoaded()) {
            var1.achievements().START_SETTLEMENT.markCompleted(var1);
         }
      } else {
         if (this.ownerAuth != -1L) {
            this.markDirty();
         }

         this.ownerAuth = -1L;
         this.ownerName = null;
         this.teamID = -1;
         this.look = null;
         this.stats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
      }

      this.updateLevelCache();
   }

   public GameMessage getSettlementName() {
      return this.settlementName;
   }

   public boolean isSettlementNameSet() {
      return this.nameSet;
   }

   public void setName(GameMessage var1) {
      Objects.requireNonNull(var1);
      if (this.settlementName == null || !this.settlementName.isSame(var1)) {
         this.markDirty();
      }

      this.settlementName = var1;
      this.nameSet = true;
      this.updateLevelCache();
   }

   public long getOwnerAuth() {
      return this.ownerAuth;
   }

   public String getOwnerName() {
      return this.ownerName == null ? "N/A" : this.ownerName;
   }

   public PlayerTeam getTeam() {
      if (!this.level.isServer()) {
         throw new IllegalStateException("Cannot get player team on client levels");
      } else {
         if (this.ownerAuth != -1L) {
            ServerClient var1 = this.level.getServer().getClientByAuth(this.ownerAuth);
            if (var1 != null) {
               this.teamID = var1.getTeamID();
            }
         }

         if (this.ownerAuth == -1L && this.teamID != -1) {
            this.teamID = -1;
            this.markDirty();
            this.updateLevelCache();
         }

         if (this.teamID == -1) {
            return null;
         } else {
            PlayerTeam var2 = this.level.getServer().world.getTeams().getTeam(this.teamID);
            if (var2 != null && var2.isMember(this.ownerAuth)) {
               return var2;
            } else {
               this.teamID = -1;
               this.markDirty();
               this.updateLevelCache();
               return null;
            }
         }
      }
   }

   public int getTeamID() {
      if (this.level.isServer()) {
         this.getTeam();
      }

      return this.teamID;
   }

   public static Stream<ServerClient> streamTeamMembers(Server var0, SettlementCache var1) {
      PlayerTeam var2 = var0.world.getTeams().getTeam(var1.teamID);
      return var2 == null ? Stream.of(var0.getClientByAuth(var1.ownerAuth)).filter(Objects::nonNull) : var2.streamOnlineMembers(var0);
   }

   public Stream<ServerClient> streamTeamMembers() {
      if (!this.level.isServer()) {
         throw new IllegalStateException("Cannot stream team members on client levels");
      } else {
         PlayerTeam var1 = this.getTeam();
         if (var1 != null) {
            return var1.streamOnlineMembers(this.level.getServer());
         } else {
            return this.ownerAuth != -1L ? Stream.of(this.level.getServer().getClientByAuth(this.ownerAuth)).filter(Objects::nonNull) : Stream.empty();
         }
      }
   }

   public Stream<ServerClient> streamTeamMembersAndOnIsland() {
      return Stream.concat(this.level.settlementLayer.streamTeamMembers(), this.level.getServer().streamClients().filter((var1) -> {
         LevelIdentifier var2 = var1.getLevelIdentifier();
         LevelIdentifier var3 = this.level.getIdentifier();
         if (var2.equals(var3)) {
            return true;
         } else if (var2.isIslandPosition() && var3.isIslandPosition()) {
            return var2.getIslandX() == var3.getIslandX() && var2.getIslandY() == var3.getIslandY();
         } else {
            return false;
         }
      })).distinct();
   }

   public Stream<ServerClient> streamTeamMembersAndOnLevel() {
      return Stream.concat(this.level.settlementLayer.streamTeamMembers(), this.level.getServer().streamClients().filter((var1) -> {
         return var1.isSamePlace(this.level);
      })).distinct();
   }

   public HumanLook getLook() {
      if (this.look == null && this.ownerAuth != -1L && this.level.isServer()) {
         this.updateOwnerVariables();
      }

      return this.look;
   }

   public PlayerStats getStats() {
      return this.stats;
   }

   public boolean isPrivate() {
      return this.isPrivate;
   }

   public void setPrivate(boolean var1) {
      if (this.isPrivate != var1) {
         this.isPrivate = var1;
         this.markDirty();
      }

   }

   public boolean doesClientHaveOwnerAccess(ServerClient var1) {
      long var2 = this.getOwnerAuth();
      return var2 == -1L || var2 == var1.authentication;
   }

   public boolean doesClientHaveAccess(ServerClient var1) {
      if (!this.isPrivate()) {
         return true;
      } else {
         return this.doesClientHaveOwnerAccess(var1) ? true : var1.isSameTeam(this.getTeamID());
      }
   }

   public boolean doIHaveOwnerAccess(Client var1) {
      long var2 = this.getOwnerAuth();
      return var2 == -1L || var2 == GameAuth.getAuthentication();
   }

   public boolean doIHaveAccess(Client var1) {
      if (!this.isPrivate()) {
         return true;
      } else if (this.doIHaveOwnerAccess(var1)) {
         return true;
      } else if (this.getTeamID() != -1 && var1.getTeam() != -1) {
         return this.getTeamID() == var1.getTeam();
      } else {
         return false;
      }
   }

   public void updateOwnerVariables() {
      if (this.level.isServer()) {
         if (this.ownerAuth != -1L) {
            ServerClient var1 = this.level.getServer().getClientByAuth(this.ownerAuth);
            if (var1 != null) {
               this.updateOwnerVariables(var1);
            } else {
               LoadData var2 = this.level.getServer().world.loadClientScript(this.ownerAuth);
               if (var2 != null) {
                  this.ownerName = ServerClient.loadClientName(var2);
                  this.teamID = this.level.getServer().world.getTeams().getPlayerTeamID(this.ownerAuth);
                  this.look = ServerClient.loadClientLook(var2);
                  this.stats = ServerClient.loadClientStats(var2);
               } else {
                  this.ownerAuth = -1L;
                  this.ownerName = null;
                  this.teamID = -1;
                  this.look = new HumanLook();
                  this.stats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
                  this.markDirty();
               }
            }
         } else {
            this.ownerName = null;
            this.look = null;
            this.teamID = -1;
            this.stats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
         }

         this.updateLevelCache();
      }
   }

   private void updateOwnerVariables(ServerClient var1) {
      this.ownerAuth = var1.authentication;
      this.ownerName = var1.getName();
      this.teamID = var1.getTeamID();
      this.look = new HumanLook(var1.playerMob.look);
      this.stats = var1.characterStats();
   }

   private void updateLevelCache() {
      this.level.getServer().levelCache.updateLevel(this.level);
   }
}
