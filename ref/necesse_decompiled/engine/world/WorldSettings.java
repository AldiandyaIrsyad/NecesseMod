package necesse.engine.world;

import java.awt.Point;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameDifficulty;
import necesse.engine.GameLog;
import necesse.engine.GameRaidFrequency;
import necesse.engine.GameTileRange;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class WorldSettings {
   public boolean allowCheats = false;
   public GameDifficulty difficulty;
   public GameDeathPenalty deathPenalty;
   public GameRaidFrequency raidFrequency;
   public boolean survivalMode;
   public boolean playerHunger;
   public boolean disableMobSpawns;
   public boolean forcedPvP;
   public boolean allowOutsideCharacters;
   public float dayTimeMod;
   public float nightTimeMod;
   public int droppedItemsLifeMinutes;
   public boolean unloadSettlements;
   public int maxSettlementsPerPlayer;
   public int maxSettlersPerSettlement;
   public GameTileRange jobSearchRange;
   public String gameVersion;
   private World world;
   private Client client;

   public WorldSettings(World var1) {
      this.difficulty = GameDifficulty.CLASSIC;
      this.deathPenalty = GameDeathPenalty.DROP_MATS;
      this.raidFrequency = GameRaidFrequency.OCCASIONALLY;
      this.survivalMode = true;
      this.playerHunger = true;
      this.disableMobSpawns = false;
      this.forcedPvP = false;
      this.allowOutsideCharacters = true;
      this.dayTimeMod = 1.0F;
      this.nightTimeMod = 1.0F;
      this.droppedItemsLifeMinutes = Settings.droppedItemsLifeMinutes;
      this.unloadSettlements = Settings.unloadSettlements;
      this.maxSettlementsPerPlayer = Settings.maxSettlementsPerPlayer;
      this.maxSettlersPerSettlement = Settings.maxSettlersPerSettlement;
      this.jobSearchRange = Settings.jobSearchRange;
      this.gameVersion = "0.24.2";
      this.world = var1;
   }

   public WorldSettings(Client var1, PacketReader var2) {
      this.difficulty = GameDifficulty.CLASSIC;
      this.deathPenalty = GameDeathPenalty.DROP_MATS;
      this.raidFrequency = GameRaidFrequency.OCCASIONALLY;
      this.survivalMode = true;
      this.playerHunger = true;
      this.disableMobSpawns = false;
      this.forcedPvP = false;
      this.allowOutsideCharacters = true;
      this.dayTimeMod = 1.0F;
      this.nightTimeMod = 1.0F;
      this.droppedItemsLifeMinutes = Settings.droppedItemsLifeMinutes;
      this.unloadSettlements = Settings.unloadSettlements;
      this.maxSettlementsPerPlayer = Settings.maxSettlementsPerPlayer;
      this.maxSettlersPerSettlement = Settings.maxSettlersPerSettlement;
      this.jobSearchRange = Settings.jobSearchRange;
      this.gameVersion = "0.24.2";
      this.world = null;
      this.client = var1;
      this.applyContentPacket(var2);
   }

   public void sendSettingsPacket() {
      if (this.world == null) {
         throw new NullPointerException("Cannot send settings packet from null world.");
      } else {
         this.world.server.network.sendToAllClients(new PacketSettings(this));
      }
   }

   public void saveSettings() {
      this.saveSettings(this.world.fileSystem.getWorldSettingsFile());
   }

   public void saveSettings(WorldFile var1) {
      if (this.world == null) {
         throw new NullPointerException("Cannot save settings from null world.");
      } else {
         this.getSaveScript().saveScript(var1);
      }
   }

   public void loadSettings() {
      this.loadSettings(true);
   }

   public void loadSettings(boolean var1) {
      if (this.world == null) {
         throw new NullPointerException("Cannot load settings from null world.");
      } else {
         WorldFile var2 = this.world.fileSystem.getWorldSettingsFile();
         if (var2.exists() && !var2.isDirectory()) {
            this.loadSaveScript(new LoadData(var2));
         } else if (var1) {
            this.saveSettings();
         }

      }
   }

   private SaveData getSaveScript() {
      SaveData var1 = new SaveData("WORLDSETTINGS");
      var1.addBoolean("allowCheats", this.allowCheats);
      var1.addEnum("difficulty", this.difficulty);
      var1.addEnum("deathPenalty", this.deathPenalty);
      var1.addEnum("raidFrequency", this.raidFrequency);
      var1.addBoolean("survivalMode", this.survivalMode);
      var1.addBoolean("playerHunger", this.playerHunger);
      var1.addBoolean("disableMobSpawns", this.disableMobSpawns);
      var1.addBoolean("forcedPvP", this.forcedPvP, "True = players will always have PvP enabled");
      var1.addBoolean("allowOutsideCharacters", this.allowOutsideCharacters);
      var1.addFloat("dayTimeMod", this.dayTimeMod, "Day time modifier (The higher, the longer day will last, max 10)");
      var1.addFloat("nightTimeMod", this.nightTimeMod, "Night time modifier (The higher, the longer night will last, max 10)");
      var1.addSafeString("gameVersion", "0.24.2");
      return var1;
   }

   private void loadSaveScript(LoadData var1) {
      this.allowCheats = var1.getBoolean("allowCheats", this.allowCheats);
      this.difficulty = (GameDifficulty)var1.getEnum(GameDifficulty.class, "difficulty", this.difficulty, false);
      if ("EASY".equals(var1.getUnsafeString("difficulty", (String)null, false))) {
         this.difficulty = GameDifficulty.ADVENTURE;
      }

      this.deathPenalty = (GameDeathPenalty)var1.getEnum(GameDeathPenalty.class, "deathPenalty", this.deathPenalty, false);
      this.raidFrequency = (GameRaidFrequency)var1.getEnum(GameRaidFrequency.class, "raidFrequency", this.raidFrequency, false);
      this.survivalMode = var1.getBoolean("survivalMode", this.survivalMode, false);
      this.playerHunger = var1.getBoolean("playerHunger", this.playerHunger, false);
      this.disableMobSpawns = var1.getBoolean("disableMobSpawns", this.disableMobSpawns);
      this.forcedPvP = var1.getBoolean("forcedPvP", this.forcedPvP);
      if (var1.hasLoadDataByName("allowOutsideCharacters")) {
         this.allowOutsideCharacters = var1.getBoolean("allowOutsideCharacters", this.allowOutsideCharacters, false);
      } else if (this.world != null && this.world.server != null && !this.world.server.isHosted() && !this.world.server.isSingleplayer()) {
         this.allowOutsideCharacters = false;
         GameLog.warn.println("Set default allow outside characters to false for " + this.world.filePath.getName());
      }

      if (var1.hasLoadDataByName("dayTimeMod")) {
         this.dayTimeMod = var1.getFloat("dayTimeMod", this.dayTimeMod);
         this.nightTimeMod = var1.getFloat("nightTimeMod", this.nightTimeMod);
         this.dayTimeMod = Math.min(Math.max(0.0F, this.dayTimeMod), 10.0F);
         this.nightTimeMod = Math.min(Math.max(0.0F, this.nightTimeMod), 10.0F);
         this.dayTimeMod = (float)((int)(this.dayTimeMod * 10.0F)) / 10.0F;
         this.nightTimeMod = (float)((int)(this.nightTimeMod * 10.0F)) / 10.0F;
         if (this.dayTimeMod == 0.0F && this.nightTimeMod == 0.0F) {
            this.dayTimeMod = 1.0F;
         }
      }

      LoadData var2 = var1.getFirstLoadDataByName("gameVersion");
      if (var2 != null) {
         this.gameVersion = LoadData.getSafeString(var2);
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextBoolean(this.allowCheats);
      var1.putNextByteUnsigned(this.difficulty.ordinal());
      var1.putNextByteUnsigned(this.deathPenalty.ordinal());
      var1.putNextByteUnsigned(this.raidFrequency.ordinal());
      var1.putNextBoolean(this.survivalMode);
      var1.putNextBoolean(this.playerHunger);
      var1.putNextBoolean(this.disableMobSpawns);
      var1.putNextBoolean(this.forcedPvP);
      var1.putNextBoolean(this.allowOutsideCharacters);
      var1.putNextFloat(this.dayTimeMod);
      var1.putNextFloat(this.nightTimeMod);
      var1.putNextInt(this.droppedItemsLifeMinutes);
      var1.putNextBoolean(this.unloadSettlements);
      var1.putNextInt(this.maxSettlementsPerPlayer);
      var1.putNextInt(this.maxSettlersPerSettlement);
      var1.putNextInt(this.jobSearchRange.maxRange);
   }

   public void applyContentPacket(PacketReader var1) {
      this.allowCheats = var1.getNextBoolean();
      this.difficulty = GameDifficulty.values()[var1.getNextByteUnsigned()];
      this.deathPenalty = GameDeathPenalty.values()[var1.getNextByteUnsigned()];
      this.raidFrequency = GameRaidFrequency.values()[var1.getNextByteUnsigned()];
      this.survivalMode = var1.getNextBoolean();
      this.playerHunger = var1.getNextBoolean();
      this.disableMobSpawns = var1.getNextBoolean();
      this.forcedPvP = var1.getNextBoolean();
      this.allowOutsideCharacters = var1.getNextBoolean();
      this.dayTimeMod = var1.getNextFloat();
      this.nightTimeMod = var1.getNextFloat();
      if (this.world != null) {
         this.world.worldEntity.calculateAmbientLightValues();
      }

      if (this.client != null && this.client.worldEntity != null) {
         this.client.worldEntity.calculateAmbientLightValues();
      }

      this.droppedItemsLifeMinutes = var1.getNextInt();
      this.unloadSettlements = var1.getNextBoolean();
      this.maxSettlementsPerPlayer = var1.getNextInt();
      this.maxSettlersPerSettlement = var1.getNextInt();
      int var2 = GameMath.limit(var1.getNextInt(), 1, 300);
      if (this.jobSearchRange.maxRange != var2) {
         if (var2 == Settings.jobSearchRange.maxRange) {
            this.jobSearchRange = Settings.jobSearchRange;
         } else {
            this.jobSearchRange = new GameTileRange(var2, new Point[0]);
         }
      }

   }

   public GameTooltips getTooltips(LocalMessage var1) {
      StringTooltips var2 = new StringTooltips(var1.translate());
      var2.add((new LocalMessage("ui", "difficultytip", "difficulty", this.difficulty.displayName)).translate());
      var2.add((new LocalMessage("ui", "dptip", "penalty", this.deathPenalty.displayName)).translate());
      if (this.survivalMode) {
         var2.add(Localization.translate("ui", "survivalmode"));
      } else {
         var2.add(Localization.translate("ui", this.playerHunger ? "hungerenabled" : "hungerdisabled"));
      }

      var2.add(Localization.translate("ui", this.disableMobSpawns ? "mobsdisabled" : "mobsenabled"));
      var2.add(Localization.translate("ui", this.forcedPvP ? "forcepvpon" : "forcepvpoff"));
      if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
         var2.add(Localization.translate("ui", "showadvanced"), GameColor.LIGHT_GRAY);
      } else {
         var2.add(Localization.translate("ui", "daymod", "mod", (int)(this.dayTimeMod * 100.0F) + "%"));
         var2.add(Localization.translate("ui", "nightmod", "mod", (int)(this.nightTimeMod * 100.0F) + "%"));
      }

      return var2;
   }

   public boolean playerHunger() {
      return this.survivalMode || this.playerHunger;
   }

   public void enableCheats() {
      if (!this.allowCheats) {
         this.allowCheats = true;
         this.saveSettings();
         this.sendSettingsPacket();
      }
   }

   public boolean achievementsEnabled() {
      return !this.allowCheats;
   }
}
