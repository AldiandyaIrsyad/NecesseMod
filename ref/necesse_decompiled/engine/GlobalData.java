package necesse.engine;

import com.codedisaster.steamworks.SteamID;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.commands.CommandsManager;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.IncursionDataRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.registries.LevelLayerRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.registries.PickupRegistry;
import necesse.engine.registries.ProjectileModifierRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.registries.SettlementStorageIndexRegistry;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.WorldGenerator;
import necesse.entity.mobs.hostile.HumanRaiderMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.gfx.GameHair;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.res.ResourceEncoder;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.recipe.RecipeFilter;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameObject.WallObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;
import org.lwjgl.system.Platform;

public class GlobalData {
   private static boolean devMode;
   private static boolean isServer;
   private static boolean lowMemoryMode;
   public static final String saveSuffix = ".dat";
   private static String rootPath = "";
   private static String appDataPath = "";
   private static AchievementManager achievements;
   private static PlayerStats stats;
   static State currentState = null;
   public static final List<FormRecipeList> craftingLists = Collections.synchronizedList(new LinkedList());
   public static final RecipeFilter masterFilter = new RecipeFilter(false);

   public GlobalData() {
   }

   public static void setDevMode() {
      devMode = true;
      GameLog.debug.println("Started development mode");
   }

   public static boolean isDevMode() {
      return devMode;
   }

   public static boolean isServer() {
      return isServer;
   }

   public static void setLowMemoryMode() {
      lowMemoryMode = true;
   }

   public static boolean isLowMemoryMode() {
      return lowMemoryMode;
   }

   public static void setup(String[] var0) {
      HashMap var1 = GameLaunch.parseLaunchOptions(var0);
      String var2 = System.getProperty("file.separator");
      if (var2 == null || var2.isEmpty()) {
         var2 = "/";
      }

      URL var3 = GlobalData.class.getProtectionDomain().getCodeSource().getLocation();
      if (var3 != null) {
         try {
            String var4 = Paths.get(var3.toURI()).toFile().getAbsolutePath();
            if (var4.endsWith(".jar")) {
               rootPath = (new File(var4)).getParent();
               if (rootPath == null) {
                  rootPath = "";
               }
            }
         } catch (URISyntaxException var6) {
            var6.printStackTrace();
         }
      }

      if (var1.containsKey("rootdir")) {
         rootPath = (String)var1.get("rootdir");
      }

      if (!rootPath.isEmpty() && !rootPath.endsWith(var2)) {
         rootPath = rootPath + var2;
      }

      if (!var1.containsKey("localdir")) {
         try {
            switch (Platform.get()) {
               case WINDOWS:
                  appDataPath = System.getenv("APPDATA") + var2 + "Necesse" + var2;
                  break;
               case MACOSX:
                  appDataPath = System.getProperty("user.home") + var2 + "Library" + var2 + "Application Support" + var2 + "Necesse" + var2;
                  break;
               case LINUX:
                  appDataPath = System.getProperty("user.home") + var2 + ".config" + var2 + "Necesse" + var2;
            }
         } catch (Exception var5) {
            appDataPath = "";
         }
      }

      (new File(appDataPath())).mkdirs();
      GameCache.checkCacheVersion();
   }

   private static String nativesPath(String var0) {
      File var1 = new File("natives/" + var0);
      if (!var1.exists()) {
         throw new RuntimeException("Could not find " + var0 + " natives files.");
      } else {
         return var1.getAbsolutePath();
      }
   }

   public static String rootPath() {
      return rootPath;
   }

   public static String appDataPath() {
      return appDataPath;
   }

   public static String cfgPath() {
      return appDataPath + "cfg/";
   }

   public static AchievementManager achievements() {
      return achievements;
   }

   public static PlayerStats stats() {
      return stats;
   }

   public static void resetStatsAndAchievements() {
      stats = new PlayerStats(true, PlayerStats.Mode.READ_ONLY);
      AchievementManager var0 = achievements;
      achievements = new AchievementManager(stats);
      achievements.loadTextures(var0);
   }

   public static Server startServer(ServerSettings var0, ServerHostSettings var1) throws IOException, FileSystemClosedException {
      Server var2 = new Server(var0);
      var2.start(var1);
      GameEvents.triggerEvent(new ServerStartEvent(var2));
      String var3 = var0.password != null && !var0.password.isEmpty() ? " with password \"" + var0.password + "\"" : "";
      System.out.println("Started server using " + var2.network.getDebugString() + " with " + var2.getSlots() + " slots on world \"" + var2.world.filePath.getName() + "\"" + var3 + ", game version " + "0.24.2" + ".");
      System.out.println("Found " + var2.usedNames.values().size() + " saved players.");
      String var4 = var2.network.getAddress();
      if (var4 != null) {
         System.out.println("Local address: " + var4);
      }

      System.out.println("Type help for list of commands.");
      return var2;
   }

   public static Client startMultiplayerClient(TickManager var0, SteamID var1) {
      Client var2 = new Client(var0, var1, new LocalMessage("ui", "characterlastfriendsworld"));
      var2.start();
      System.out.println("Started client connecting to " + var2.network.getDebugString() + ", game version " + "0.24.2");
      return var2;
   }

   public static Client startMultiplayerClient(TickManager var0, String var1, int var2, GameMessage var3) {
      Client var4 = new Client(var0, var1, var2, var3);
      var4.start();
      System.out.println("Started client connecting to " + var4.network.getDebugString() + ", game version " + "0.24.2");
      return var4;
   }

   public static Client startSingleplayerClient(TickManager var0, ServerSettings var1) throws IOException, FileSystemClosedException {
      Server var2 = new Server(var1);
      Client var3 = new Client(var0, var2, true);
      var2.makeSingleplayer(var3);
      var2.start((ServerHostSettings)null);
      GameEvents.triggerEvent(new ServerStartEvent(var2));
      var3.start();
      System.out.println("Started singleplayer server on world " + var2.world.filePath.getName() + ", game version " + "0.24.2");
      System.out.println("Found " + var2.usedNames.values().size() + " saved players.");
      return var3;
   }

   public static Client startHostClient(TickManager var0, ServerSettings var1, ServerHostSettings var2) throws IOException, FileSystemClosedException {
      Server var3 = new Server(var1);
      Client var4 = new Client(var0, var3, false);
      var3.makeHosted(var4);
      var3.start(var2);
      GameEvents.triggerEvent(new ServerStartEvent(var3));
      var4.start();
      String var5 = var1.password != null && !var1.password.isEmpty() ? " with password \"" + var1.password + "\"" : "";
      System.out.println("Started hosting using " + var3.network.getDebugString() + " with " + var3.getSlots() + " slots on world \"" + var3.world.filePath.getName() + "\"" + var5 + ", game version " + "0.24.2" + ".");
      System.out.println("Found " + var3.usedNames.values().size() + " saved players.");
      String var6 = var3.network.getAddress();
      if (var6 != null) {
         System.out.println("Local address: " + var6);
      }

      return var4;
   }

   public static void updateCraftable() {
      craftingLists.forEach(FormRecipeList::updateCraftable);
   }

   public static void updateRecipes() {
      craftingLists.forEach(FormRecipeList::updateRecipes);
   }

   public static void resetDebug() {
      Debug.reset();
      HUD.reset();
   }

   public static void loadAll(boolean var0) throws ModLoadException {
      GlobalData.isServer = var0;
      GameLoadingScreen.drawLoadingString("...");
      if (!var0) {
         ResourceEncoder.loadResourceFile();
         Screen.queryGLError("PostResourceFile");
         FontManager.loadFonts();
         Screen.queryGLError("PostLoadFonts");
      }

      ModLoader.loadMods(var0);
      Localization.loadModsLanguage();
      Settings.loadModSettings(false);
      if (!ModLoader.getEnabledMods().isEmpty()) {
         GameLoadingScreen.drawLoadingString(Localization.translate("loading", "preinitmods"));
         Iterator var1 = ModLoader.getEnabledMods().iterator();

         while(var1.hasNext()) {
            LoadedMod var2 = (LoadedMod)var1.next();

            try {
               var2.preInit();
            } catch (Exception var11) {
               throw new ModLoadException(var2, "Error during ModEntry.preInit", var11);
            }
         }
      }

      GameSeasons.loadSeasons();
      GameRegistry[] var12 = new GameRegistry[]{GNDRegistry.instance, DamageTypeRegistry.instance, LevelRegistry.instance, LevelLayerRegistry.instance, GlobalIngredientRegistry.instance, MusicRegistry.instance, TileRegistry.instance, ObjectRegistry.instance, LogicGateRegistry.instance, BiomeRegistry.instance, IncursionDataRegistry.instance, IncursionBiomeRegistry.instance, UniqueIncursionModifierRegistry.instance, UniqueIncursionRewardsRegistry.instance, BuffRegistry.instance, RecipeTechRegistry.instance, ItemRegistry.instance, EnchantmentRegistry.instance, MobRegistry.instance, MobMovement.registry, PickupRegistry.instance, ProjectileRegistry.instance, ProjectileModifierRegistry.instance, WorldEventRegistry.instance, LevelEventRegistry.instance, LevelDataRegistry.instance, WorldDataRegistry.instance, JobTypeRegistry.instance, LevelJobRegistry.instance, SettlerRegistry.instance, ContainerRegistry.instance, PacketRegistry.instance, QuestRegistry.instance, ExpeditionMissionRegistry.instance, HumanRaiderMob.weaponRegistry, SettlementWorkZoneRegistry.instance, SettlementStorageIndexRegistry.instance};
      GameRegistry[] var13 = var12;
      int var3 = var12.length;

      int var4;
      GameRegistry var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var13[var4];
         var5.registerCore();
      }

      CommandsManager.registerCoreCommands();
      Iterator var14;
      LoadedMod var15;
      if (!ModLoader.getEnabledMods().isEmpty()) {
         GameLoadingScreen.drawLoadingString(Localization.translate("loading", "initmods"));
         var14 = ModLoader.getEnabledMods().iterator();

         while(var14.hasNext()) {
            var15 = (LoadedMod)var14.next();

            try {
               var15.init();
            } catch (Exception var10) {
               throw new ModLoadException(var15, "Error during ModEntry.init", var10);
            }
         }
      }

      GameMessage.registry.closeRegistry();
      var13 = var12;
      var3 = var12.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var13[var4];
         var5.closeRegistry();
      }

      GameHair.loadHair();
      if (!var0) {
         var14 = ModLoader.getAllMods().iterator();

         while(var14.hasNext()) {
            var15 = (LoadedMod)var14.next();

            try {
               var15.loadPreviewImage();
            } catch (Exception var9) {
               System.err.println("Error loading mod " + var15.getModDebugString() + " preview image");
               var9.printStackTrace();
            }
         }

         var14 = ModLoader.getEnabledMods().iterator();

         while(var14.hasNext()) {
            var15 = (LoadedMod)var14.next();

            try {
               ResourceEncoder.addModResources(var15);
            } catch (Exception var8) {
               throw new ModLoadException(var15, "Error during adding mod resources", var8);
            }
         }

         stats = new PlayerStats(true, PlayerStats.Mode.READ_ONLY);
         stats.loadStatsFile();
         achievements = new AchievementManager(stats);
         achievements.loadAchievementsFile();
         GameLoadingScreen.drawLoadingSub("...");
         TrackedSidebarForm.loadTrackedAchievements();
         GameSeasons.loadResources();
         Screen.queryGLError("PostGameSeasons");
         GameResources.loadShaders();
         Screen.queryGLError("PostShaders");
         GameResources.loadCursors();
         Screen.queryGLError("PostCursors");
         GameResources.loadTextures();
         Screen.queryGLError("PostTextures");
         GameResources.startSoundLoading();
         Screen.queryGLError("PostStartSound");
         Settings.loadClientSettings();
         Settings.saveClientSettings();
         if (!ModLoader.getEnabledMods().isEmpty()) {
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "initmodsres"));
            var14 = ModLoader.getEnabledMods().iterator();

            while(var14.hasNext()) {
               var15 = (LoadedMod)var14.next();

               try {
                  var15.initResources();
               } catch (Exception var7) {
                  throw new ModLoadException(var15, "Error during ModEntry.initResources", var7);
               }
            }
         }

         GameResources.finishLoadingSounds();
         Screen.queryGLError("PostFinishSound");
         GameTile.generateTileTextures();
         GameLogicGate.generateLogicGateTextures();
         WallObject.generateWallTextures();
         GameResources.generatedParticlesTexture = GameResources.particlesTextureGenerator.generate();
         Screen.queryGLError("PostGenerateTileTextures");
         GameTexture.finalizeLoadedTextures();
         Screen.queryGLError("PostFinalizeLoadedTextures");
      }

      Settings.loadBanned();
      if (!ModLoader.getEnabledMods().isEmpty()) {
         GameLoadingScreen.drawLoadingString(Localization.translate("loading", "postinitmods"));
         var14 = ModLoader.getEnabledMods().iterator();

         while(var14.hasNext()) {
            var15 = (LoadedMod)var14.next();

            try {
               var15.postInit();
            } catch (Exception var6) {
               throw new ModLoadException(var15, "Error during ModEntry.postInit", var6);
            }
         }
      }

      Recipes.loadDefaultRecipes();
      Recipes.closeModRecipeRegistry();
      WorldGenerator.closeRegistry();
      CommandsManager.closeRegistry();
      if (GameSeasons.isAprilFools()) {
         System.out.println("It's April Fools' day!");
      }

      if (GameSeasons.isHalloween()) {
         System.out.println("Happy Halloween!");
      }

      if (GameSeasons.isChristmas()) {
         System.out.println("Merry Christmas!");
      }

      if (GameSeasons.isNewYear()) {
         System.out.println("Happy New Year!");
      }

   }

   public static void setCurrentState(State var0) {
      if (currentState != null) {
         currentState.dispose();
      }

      currentState = var0;
   }

   public static State getCurrentState() {
      return currentState;
   }

   public static boolean debugActive() {
      return Debug.isActive() || HUD.debugActive || MainGame.debugForm != null && !MainGame.debugForm.isHidden();
   }

   public static boolean debugCheatActive() {
      return MainGame.debugForm != null && !MainGame.debugForm.isHidden();
   }
}
