package necesse.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;
import necesse.engine.control.Control;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModRuntimeException;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.EventVariable;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.components.FormTravelContainerGrid;
import necesse.gfx.res.ResourceEncoder;
import necesse.gfx.ui.GameInterfaceStyle;
import necesse.inventory.recipe.RecipeFilter;

public class Settings {
   private static boolean loadedClientSettings;
   private static boolean firstTimeSavedSettings;
   public static float sceneSize = 1.25F;
   public static boolean dynamicSceneSize = true;
   public static boolean limitCameraToLevelBounds = true;
   public static boolean pauseOnFocusLoss = true;
   public static boolean savePerformanceOnFocusLoss = true;
   public static boolean alwaysSkipTutorial = false;
   public static boolean showSettlerHeadArmor = true;
   public static boolean useTileObjectHitboxes = false;
   public static boolean loadLevelBeforeSpawn = false;
   public static boolean instantLevelChange = true;
   public static boolean smartMining = false;
   public static EventVariable<Boolean> craftingUseNearby = new EventVariable(true);
   public static boolean minimapHidden = false;
   public static int minimapZoomLevel = 3;
   public static int islandMapZoomLevel = 3;
   public static boolean menuCameraPan = true;
   public static boolean showEANotice = true;
   public static float cursorJoystickSensitivity = 1.0F;
   public static float interfaceSize = 1.0F;
   public static GameInterfaceStyle UI;
   public static boolean dynamicInterfaceSize;
   public static boolean sharpenInterface;
   public static boolean pixelFont;
   public static boolean showDebugInfo;
   public static boolean showQuestMarkers;
   public static boolean showTeammateMarkers;
   public static boolean showPickupText;
   public static boolean showDamageText;
   public static boolean showDoTText;
   public static boolean showMobHealthBars;
   public static boolean showBossHealthBars;
   public static boolean showControlTips;
   public static boolean showBasicTooltipBackground;
   public static boolean showItemTooltipBackground;
   public static boolean showLogicGateTooltips;
   public static boolean showIngredientsAvailable;
   public static boolean alwaysShowQuickbar;
   public static int tooltipTextSize;
   public static Color cursorColor;
   public static int cursorSize;
   public static boolean trackNewQuests;
   public static EventVariable<Boolean> hasCraftingFilterExpanded;
   public static EventVariable<Boolean> hasCraftingListExpanded;
   public static EventVariable<Boolean> craftingListShowHidden;
   public static EventVariable<Boolean> craftingListOnlyCraftable;
   public static EventVariable<Boolean> hideSettlementStorage;
   public static EventVariable<Boolean> hideSettlementWorkstations;
   public static EventVariable<Boolean> hideSettlementForestryZones;
   public static EventVariable<Boolean> hideSettlementHusbandryZones;
   public static EventVariable<Boolean> hideSettlementFertilizeZones;
   public static FormTravelContainerGrid.CoordinateSetting mapCoordinates;
   public static String outputDevice;
   public static float masterVolume;
   public static float effectsVolume;
   public static float weatherVolume;
   public static float UIVolume;
   public static float musicVolume;
   public static boolean muteOnFocusLoss;
   public static DisplayMode displayMode;
   public static int monitor;
   public static Dimension displaySize;
   public static boolean vSyncEnabled;
   public static int maxFPS;
   public static boolean reduceUIFramerate;
   public static SceneColorSetting sceneColors;
   public static float brightness;
   public static boolean smoothLighting;
   public static LightSetting lights;
   public static ParticleSetting particles;
   public static boolean wavyGrass;
   public static boolean denseGrass;
   public static boolean cameraShake;
   public static boolean alwaysLight;
   public static boolean alwaysRain;
   public static final int DEFAULT_SERVER_PORT = 14159;
   public static int serverPort;
   public static int serverSlots;
   public static String serverPassword;
   public static int maxClientLatencySeconds;
   public static boolean pauseWhenEmpty;
   public static boolean giveClientsPower;
   public static boolean serverLogging;
   public static String serverMOTD;
   public static String serverWorld;
   public static String serverOwnerName;
   public static long serverOwnerAuth;
   public static int unloadLevelsCooldown;
   public static int worldBorderSize;
   public static int droppedItemsLifeMinutes;
   public static boolean unloadSettlements;
   public static int maxSettlementsPerPlayer;
   public static int maxSettlersPerSettlement;
   public static GameTileRange jobSearchRange;
   public static boolean zipSaves;
   public static String language;
   public static ArrayList<String> banned;
   public static boolean serverPerspective;
   public static boolean hideUI;
   protected static HashMap<Object, RecipeFilter> recipeFilters;
   protected static HashMap<Object, ItemCategoryExpandedSetting> itemCategoriesExpanded;

   public Settings() {
   }

   public static String clientDir() {
      return GlobalData.cfgPath() + "settings.cfg";
   }

   public static String clientModDir(LoadedMod var0) {
      return GlobalData.cfgPath() + "/mods/" + var0.id + ".cfg";
   }

   public static String serverDir() {
      return GlobalData.cfgPath() + "server.cfg";
   }

   public static RecipeFilter getRecipeFilterSetting(Object var0) {
      return (RecipeFilter)recipeFilters.compute(var0, (var0x, var1) -> {
         return var1 == null ? new RecipeFilter(true) : var1;
      });
   }

   public static ItemCategoryExpandedSetting getItemCategoryExpandedSetting(Object var0) {
      return (ItemCategoryExpandedSetting)itemCategoriesExpanded.compute(var0, (var0x, var1) -> {
         return var1 == null ? new ItemCategoryExpandedSetting(false) : var1;
      });
   }

   private static SaveData getClientSaveData() {
      SaveData var0 = new SaveData("SETTINGS");
      SaveData var1 = new SaveData("GENERAL");
      var1.addFloat("sceneSize", sceneSize, "[" + GameWindow.minSceneSize + " - " + GameWindow.maxSceneSize + "] Higher = more zoomed in");
      var1.addBoolean("dynamicSceneSize", dynamicSceneSize);
      var1.addBoolean("limitCameraToLevelBounds", limitCameraToLevelBounds);
      var1.addBoolean("pauseOnFocusLoss", pauseOnFocusLoss);
      var1.addBoolean("savePerformanceOnFocusLoss", savePerformanceOnFocusLoss);
      var1.addBoolean("alwaysSkipTutorial", alwaysSkipTutorial);
      var1.addBoolean("showSettlerHeadArmor", showSettlerHeadArmor);
      var1.addBoolean("useTileObjectHitboxes", useTileObjectHitboxes);
      var1.addBoolean("loadLevelBeforeSpawn", loadLevelBeforeSpawn);
      var1.addBoolean("smartMining", smartMining);
      var1.addBoolean("craftingUseNearby", (Boolean)craftingUseNearby.get());
      var1.addBoolean("minimapHidden", minimapHidden);
      var1.addBoolean("menuCameraPan", menuCameraPan);
      var1.addInt("minimapZoomLevel", minimapZoomLevel);
      var1.addInt("islandMapZoomLevel", islandMapZoomLevel);
      var1.addBoolean("showEANotice", showEANotice);
      var1.addInt("unloadLevelsCooldown", unloadLevelsCooldown);
      var1.addInt("worldBorderSize", worldBorderSize);
      var1.addInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes);
      var1.addBoolean("unloadSettlements", unloadSettlements);
      var1.addInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer);
      var1.addInt("maxSettlersPerSettlement", maxSettlersPerSettlement);
      var1.addInt("jobSearchRange", jobSearchRange.maxRange);
      var1.addBoolean("zipSaves", zipSaves);
      var1.addSafeString("language", language);
      var0.addSaveData(var1);
      if (!Control.isControlsLoaded()) {
         Control.loadControls();
      }

      SaveData var2 = new SaveData("CONTROLS");
      var2.addFloat("cursorJoystickSensitivity", cursorJoystickSensitivity);
      Iterator var3 = Control.getControls().iterator();

      while(var3.hasNext()) {
         Control var4 = (Control)var3.next();
         if (var4 != null && var4.mod == null) {
            var2.addInt(var4.id, var4.getKey());
         }
      }

      var0.addSaveData(var2);
      SaveData var6 = new SaveData("INTERFACE");
      var6.addFloat("interfaceSize", interfaceSize, "[" + GameWindow.interfaceSizes[0] + " - " + GameWindow.interfaceSizes[GameWindow.interfaceSizes.length - 1] + "] Higher = bigger");
      var6.addSafeString("interfaceStyle", UI.texturesPath);
      var6.addBoolean("dynamicInterfaceSize", dynamicInterfaceSize);
      var6.addBoolean("sharpenInterface", sharpenInterface);
      var6.addBoolean("pixelFont", pixelFont);
      var6.addBoolean("showDebugInfo", showDebugInfo);
      var6.addBoolean("showQuestMarkers", showQuestMarkers);
      var6.addBoolean("showTeammateMarkers", showTeammateMarkers);
      var6.addBoolean("showPickupText", showPickupText);
      var6.addBoolean("showDamageText", showDamageText);
      var6.addBoolean("showDoTText", showDoTText);
      var6.addBoolean("showMobHealthBars", showMobHealthBars);
      var6.addBoolean("showBossHealthBars", showBossHealthBars);
      var6.addBoolean("showBasicTooltipBackground", showBasicTooltipBackground);
      var6.addBoolean("showItemTooltipBackground", showItemTooltipBackground);
      var6.addBoolean("showControlTips", showControlTips);
      var6.addBoolean("showLogicGateTooltips", showLogicGateTooltips);
      var6.addBoolean("showIngredientsAvailable", showIngredientsAvailable);
      var6.addBoolean("alwaysShowQuickbar", alwaysShowQuickbar);
      var6.addInt("tooltipTextSize", tooltipTextSize);
      var6.addColor("cursorColor", cursorColor);
      var6.addInt("cursorSize", cursorSize);
      var6.addBoolean("trackNewQuests", trackNewQuests);
      var6.addBoolean("craftingFilterExpanded", (Boolean)hasCraftingFilterExpanded.get());
      var6.addBoolean("craftingListExpanded", (Boolean)hasCraftingListExpanded.get());
      var6.addBoolean("craftingListShowHidden", (Boolean)craftingListShowHidden.get());
      var6.addBoolean("craftingListOnlyCraftable", (Boolean)craftingListOnlyCraftable.get());
      var6.addBoolean("hideSettlementStorage", (Boolean)hideSettlementStorage.get());
      var6.addBoolean("hideSettlementWorkstations", (Boolean)hideSettlementWorkstations.get());
      var6.addBoolean("hideSettlementForestryZones", (Boolean)hideSettlementForestryZones.get());
      var6.addBoolean("hideSettlementHusbandryZones", (Boolean)hideSettlementHusbandryZones.get());
      var6.addBoolean("hideSettlementFertilizeZones", (Boolean)hideSettlementFertilizeZones.get());
      var6.addEnum("mapCoordinates", mapCoordinates);
      var0.addSaveData(var6);
      SaveData var7 = new SaveData("SOUND");
      if (outputDevice != null) {
         var7.addSafeString("outputDevice", outputDevice);
      }

      var7.addInt("masterVolume", (int)(masterVolume * 100.0F), "[0 - 100]");
      var7.addInt("effectsVolume", (int)(effectsVolume * 100.0F), "[0 - 100]");
      var7.addInt("weatherVolume", (int)(weatherVolume * 100.0F), "[0 - 100]");
      var7.addInt("UIVolume", (int)(UIVolume * 100.0F), "[0 - 100]");
      var7.addInt("musicVolume", (int)(musicVolume * 100.0F), "[0 - 100]");
      var7.addBoolean("muteOnFocusLoss", muteOnFocusLoss);
      var0.addSaveData(var7);
      SaveData var5 = new SaveData("GRAPHICS");
      var5.addEnum("displayMode", displayMode, (String)Arrays.stream(DisplayMode.values()).map(Enum::toString).collect(Collectors.joining(", ")));
      if (displaySize != null) {
         var5.addDimension("displaySize", displaySize);
      }

      var5.addInt("monitor", monitor);
      var5.addBoolean("vSyncEnabled", vSyncEnabled);
      var5.addInt("maxFPS", maxFPS, "0 = uncapped");
      var5.addBoolean("reduceUIFramerate", reduceUIFramerate);
      var5.addEnum("sceneColors", sceneColors, (String)Arrays.stream(SceneColorSetting.values()).map(Enum::toString).collect(Collectors.joining(", ")));
      var5.addFloat("brightness", brightness);
      var5.addBoolean("smoothLighting", smoothLighting);
      var5.addEnum("lights", lights, (String)Arrays.stream(Settings.LightSetting.values()).map(Enum::toString).collect(Collectors.joining(", ")));
      var5.addEnum("particles", particles, (String)Arrays.stream(Settings.ParticleSetting.values()).map(Enum::toString).collect(Collectors.joining(", ")));
      var5.addBoolean("wavyGrass", wavyGrass);
      var5.addBoolean("denseGrass", denseGrass);
      var5.addBoolean("cameraShake", cameraShake);
      var0.addSaveData(var5);
      return var0;
   }

   private static SaveData getServerSaveData() {
      SaveData var0 = new SaveData("SERVER");
      var0.addInt("port", serverPort, "[0 - 65535] Server default port");
      var0.addInt("slots", serverSlots, "[1 - 250] Server default slots");
      var0.addSafeString("password", serverPassword, "Leave blank for no password");
      var0.addInt("maxClientLatencySeconds", maxClientLatencySeconds);
      var0.addBoolean("pauseWhenEmpty", pauseWhenEmpty);
      var0.addBoolean("giveClientsPower", giveClientsPower, "If true, clients will have much more power over what hits them, their position etc");
      var0.addBoolean("logging", serverLogging, "If true, will create log files for each server start");
      var0.addSafeString("language", language);
      var0.addInt("unloadLevelsCooldown", unloadLevelsCooldown, "The number of seconds a level will stay loaded after the last player has left it");
      var0.addInt("worldBorderSize", worldBorderSize, "The max distance from spawn players can travel. -1 for no border");
      var0.addInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes, "Minutes that dropped items will stay in the world. 0 or less for indefinite");
      var0.addBoolean("unloadSettlements", unloadSettlements, "If the server should unload player settlements or keep them loaded");
      var0.addInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer, "The maximum amount of settlements per player. -1 or less means infinite");
      var0.addInt("maxSettlersPerSettlement", maxSettlersPerSettlement, "The maximum amount of settlers per settlement. -1 or less means infinite");
      var0.addInt("jobSearchRange", jobSearchRange.maxRange, "The tile search range of settler jobs");
      var0.addBoolean("zipSaves", zipSaves, "If true, will create new saves uncompressed");
      var0.addSafeString("MOTD", serverMOTD, "Message of the day");
      return var0;
   }

   private static SaveData getModSaveData(LoadedMod var0, ModSettings var1) {
      SaveData var2 = new SaveData("");
      SaveData var3;
      if (var1 != null) {
         var3 = new SaveData("SETTINGS");
         var1.addSaveData(var3);
         var2.addSaveData(var3);
      }

      var3 = new SaveData("CONTROLS");
      Iterator var4 = Control.getControls().iterator();

      while(var4.hasNext()) {
         Control var5 = (Control)var4.next();
         if (var5 != null && var5.mod == var0) {
            var3.addInt(var5.id, var5.getKey());
         }
      }

      if (!var3.isEmpty()) {
         var2.addSaveData(var3);
      }

      return var2;
   }

   private static void loadClientData(LoadData var0) {
      LoadData var1 = var0.getFirstLoadDataByName("GENERAL");
      if (var1 != null) {
         sceneSize = var1.getFloat("sceneSize", sceneSize, GameWindow.minSceneSize, GameWindow.maxSceneSize);
         dynamicSceneSize = var1.getBoolean("dynamicSceneSize", dynamicSceneSize);
         limitCameraToLevelBounds = var1.getBoolean("limitCameraToLevelBounds", limitCameraToLevelBounds, false);
         pauseOnFocusLoss = var1.getBoolean("pauseOnFocusLoss", pauseOnFocusLoss);
         savePerformanceOnFocusLoss = var1.getBoolean("savePerformanceOnFocusLoss", savePerformanceOnFocusLoss);
         alwaysSkipTutorial = var1.getBoolean("alwaysSkipTutorial", alwaysSkipTutorial);
         showSettlerHeadArmor = var1.getBoolean("showSettlerHeadArmor", showSettlerHeadArmor);
         useTileObjectHitboxes = var1.getBoolean("useTileObjectHitboxes", useTileObjectHitboxes, false);
         loadLevelBeforeSpawn = var1.getBoolean("loadLevelBeforeSpawn", loadLevelBeforeSpawn, false);
         smartMining = var1.getBoolean("smartMining", smartMining);
         craftingUseNearby.set(var1.getBoolean("craftingUseNearby", (Boolean)craftingUseNearby.get()));
         minimapHidden = var1.getBoolean("minimapHidden", minimapHidden);
         menuCameraPan = var1.getBoolean("menuCameraPan", menuCameraPan);
         minimapZoomLevel = var1.getInt("minimapZoomLevel", minimapZoomLevel);
         islandMapZoomLevel = var1.getInt("islandMapZoomLevel", islandMapZoomLevel);
         showEANotice = var1.getBoolean("showEANotice", showEANotice, false);
         unloadLevelsCooldown = var1.getInt("unloadLevelsCooldown", unloadLevelsCooldown, 2, Integer.MAX_VALUE, false);
         worldBorderSize = var1.getInt("worldBorderSize", worldBorderSize, -1, Integer.MAX_VALUE, false);
         droppedItemsLifeMinutes = var1.getInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes, false);
         unloadSettlements = var1.getBoolean("unloadSettlements", unloadSettlements, false);
         maxSettlementsPerPlayer = var1.getInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer, false);
         maxSettlersPerSettlement = var1.getInt("maxSettlersPerSettlement", maxSettlersPerSettlement, false);
         int var2 = var1.getInt("jobSearchRange", jobSearchRange.maxRange, 1, 300);
         if (jobSearchRange.maxRange != var2) {
            jobSearchRange = new GameTileRange(var2, new Point[0]);
         }

         zipSaves = var1.getBoolean("zipSaves", zipSaves);
         language = var1.getSafeString("language", language);
         Language var3 = Localization.getLanguageStringID(language);
         if (var3 == null) {
            var3 = Localization.defaultLang;
         }

         var3.setCurrent();
      } else {
         System.err.println("Could not load general settings.");
      }

      if (!Control.isControlsLoaded()) {
         Control.loadControls();
      }

      LoadData var6 = var0.getFirstLoadDataByName("CONTROLS");
      if (var6 != null && Control.getControls() != null) {
         cursorJoystickSensitivity = var6.getFloat("cursorJoystickSensitivity", cursorJoystickSensitivity, false);
         Iterator var7 = Control.getControls().iterator();

         while(var7.hasNext()) {
            Control var4 = (Control)var7.next();
            if (var4 != null && var4.mod == null) {
               var4.changeKey(var6.getInt(var4.id, var4.getKey()));
            }
         }
      } else if (var6 == null) {
         System.err.println("Could not load controls settings.");
      }

      LoadData var8 = var0.getFirstLoadDataByName("INTERFACE");
      if (var8 != null) {
         interfaceSize = var8.getFloat("interfaceSize", interfaceSize, 1.0F, 2.0F);
         String var9 = var8.getSafeString("interfaceStyle", (String)null, false);
         if (var9 != null) {
            UI = GameInterfaceStyle.getStyle(var9);
         }

         dynamicInterfaceSize = var8.getBoolean("dynamicInterfaceSize", dynamicInterfaceSize);
         sharpenInterface = var8.getBoolean("sharpenInterface", sharpenInterface);
         pixelFont = var8.getBoolean("pixelFont", pixelFont);
         showDebugInfo = var8.getBoolean("showDebugInfo", showDebugInfo);
         showQuestMarkers = var8.getBoolean("showQuestMarkers", showQuestMarkers);
         showTeammateMarkers = var8.getBoolean("showTeammateMarkers", showTeammateMarkers);
         showPickupText = var8.getBoolean("showPickupText", showPickupText);
         showDamageText = var8.getBoolean("showDamageText", showDamageText);
         showDoTText = var8.getBoolean("showDoTText", showDoTText);
         showMobHealthBars = var8.getBoolean("showMobHealthBars", showMobHealthBars);
         showBossHealthBars = var8.getBoolean("showBossHealthBars", showBossHealthBars);
         showBasicTooltipBackground = var8.getBoolean("showBasicTooltipBackground", showBasicTooltipBackground);
         showItemTooltipBackground = var8.getBoolean("showItemTooltipBackground", showItemTooltipBackground);
         showControlTips = var8.getBoolean("showControlTips", showControlTips);
         showLogicGateTooltips = var8.getBoolean("showLogicGateTooltips", showLogicGateTooltips);
         showIngredientsAvailable = var8.getBoolean("showIngredientsAvailable", showIngredientsAvailable);
         alwaysShowQuickbar = var8.getBoolean("alwaysShowQuickbar", alwaysShowQuickbar);
         tooltipTextSize = var8.getInt("tooltipTextSize", tooltipTextSize, 12, 32);
         cursorColor = var8.getColor("cursorColor", cursorColor);
         cursorSize = var8.getInt("cursorSize", cursorSize, -Screen.cursorSizeOffset, Screen.cursorSizes.length - Screen.cursorSizeOffset - 1);
         trackNewQuests = var8.getBoolean("trackNewQuests", trackNewQuests);
         hasCraftingFilterExpanded.set(var8.getBoolean("craftingFilterExpanded", (Boolean)hasCraftingFilterExpanded.get()));
         hasCraftingListExpanded.set(var8.getBoolean("craftingListExpanded", (Boolean)hasCraftingListExpanded.get()));
         craftingListShowHidden.set(var8.getBoolean("craftingListShowHidden", (Boolean)craftingListShowHidden.get()));
         craftingListOnlyCraftable.set(var8.getBoolean("craftingListOnlyCraftable", (Boolean)craftingListOnlyCraftable.get()));
         hideSettlementStorage.set(var8.getBoolean("hideSettlementStorage", (Boolean)hideSettlementStorage.get(), false));
         hideSettlementWorkstations.set(var8.getBoolean("hideSettlementWorkstations", (Boolean)hideSettlementWorkstations.get(), false));
         hideSettlementForestryZones.set(var8.getBoolean("hideSettlementForestryZones", (Boolean)hideSettlementForestryZones.get(), false));
         hideSettlementHusbandryZones.set(var8.getBoolean("hideSettlementHusbandryZones", (Boolean)hideSettlementHusbandryZones.get(), false));
         hideSettlementFertilizeZones.set(var8.getBoolean("hideSettlementFertilizeZones", (Boolean)hideSettlementFertilizeZones.get(), false));
         mapCoordinates = (FormTravelContainerGrid.CoordinateSetting)var8.getEnum(FormTravelContainerGrid.CoordinateSetting.class, "mapCoordinates", mapCoordinates, false);
      } else {
         System.err.println("Could not load interface settings.");
      }

      LoadData var10 = var0.getFirstLoadDataByName("SOUND");
      if (var10 != null) {
         outputDevice = var10.getSafeString("outputDevice", (String)null, false);
         masterVolume = (float)var10.getInt("masterVolume", (int)(masterVolume * 100.0F), 0, 100) / 100.0F;
         effectsVolume = (float)var10.getInt("effectsVolume", (int)(effectsVolume * 100.0F), 0, 100) / 100.0F;
         weatherVolume = (float)var10.getInt("weatherVolume", (int)(weatherVolume * 100.0F), 0, 100) / 100.0F;
         UIVolume = (float)var10.getInt("UIVolume", (int)(UIVolume * 100.0F), 0, 100) / 100.0F;
         musicVolume = (float)var10.getInt("musicVolume", (int)(musicVolume * 100.0F), 0, 100) / 100.0F;
         muteOnFocusLoss = var10.getBoolean("muteOnFocusLoss", muteOnFocusLoss);
      } else {
         System.err.println("Could not load sound settings.");
      }

      LoadData var5 = var0.getFirstLoadDataByName("GRAPHICS");
      if (var5 != null) {
         displayMode = (DisplayMode)var5.getEnum(DisplayMode.class, "displayMode", displayMode);
         displaySize = var5.getDimension("displaySize", displaySize, false);
         monitor = var5.getInt("monitor", monitor, 0, Integer.MAX_VALUE);
         vSyncEnabled = var5.getBoolean("vSyncEnabled", vSyncEnabled);
         maxFPS = var5.getInt("maxFPS", maxFPS);
         reduceUIFramerate = var5.getBoolean("reduceUIFramerate", reduceUIFramerate);
         sceneColors = (SceneColorSetting)var5.getEnum(SceneColorSetting.class, "sceneColors", sceneColors);
         brightness = var5.getFloat("brightness", brightness, false);
         smoothLighting = var5.getBoolean("smoothLighting", smoothLighting);
         lights = (LightSetting)var5.getEnum(LightSetting.class, "lights", lights);
         particles = (ParticleSetting)var5.getEnum(ParticleSetting.class, "particles", particles);
         wavyGrass = var5.getBoolean("wavyGrass", wavyGrass);
         denseGrass = var5.getBoolean("denseGrass", denseGrass);
         cameraShake = var5.getBoolean("cameraShake", cameraShake);
      } else {
         System.err.println("Could not load graphics settings.");
      }

   }

   private static void loadServerData(LoadData var0) {
      serverPort = var0.getInt("port", serverPort, 0, 65535);
      serverSlots = var0.getInt("slots", serverSlots, 1, 250);
      serverPassword = var0.getSafeString("password", serverPassword);
      maxClientLatencySeconds = var0.getInt("maxClientLatencySeconds", maxClientLatencySeconds);
      pauseWhenEmpty = var0.getBoolean("pauseWhenEmpty", pauseWhenEmpty);
      giveClientsPower = var0.getBoolean("giveClientsPower", giveClientsPower);
      serverLogging = var0.getBoolean("logging", serverLogging);
      unloadLevelsCooldown = var0.getInt("unloadLevelsCooldown", unloadLevelsCooldown, 2, Integer.MAX_VALUE, false);
      worldBorderSize = var0.getInt("worldBorderSize", worldBorderSize, -1, Integer.MAX_VALUE, false);
      droppedItemsLifeMinutes = var0.getInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes, false);
      unloadSettlements = var0.getBoolean("unloadSettlements", unloadSettlements, false);
      maxSettlementsPerPlayer = var0.getInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer, false);
      maxSettlersPerSettlement = var0.getInt("maxSettlersPerSettlement", maxSettlersPerSettlement, false);
      int var1 = var0.getInt("jobSearchRange", jobSearchRange.maxRange, 1, 300);
      if (jobSearchRange.maxRange != var1) {
         jobSearchRange = new GameTileRange(var1, new Point[0]);
      }

      zipSaves = var0.getBoolean("zipSaves", zipSaves);
      language = var0.getSafeString("language", language);
      Language var2 = Localization.getLanguageStringID(language);
      if (var2 == null) {
         var2 = Localization.defaultLang;
      }

      var2.setCurrent();
      serverMOTD = var0.getSafeString("MOTD", serverMOTD, false);
      serverWorld = var0.getSafeString("world", serverWorld, false);
   }

   private static void loadModSettings(LoadedMod var0, ModSettings var1, LoadData var2) {
      LoadData var3;
      if (var1 != null) {
         var3 = var2.getFirstLoadDataByName("SETTINGS");
         if (var3 != null && var3.isArray()) {
            var1.applyLoadData(var3);
         }
      }

      var3 = var2.getFirstLoadDataByName("CONTROLS");
      Iterator var4 = Control.getControls().iterator();

      while(var4.hasNext()) {
         Control var5 = (Control)var4.next();
         if (var5 != null && var5.mod == var0) {
            var5.changeKey(var3.getInt(var5.id, var5.getKey()));
         }
      }

   }

   public static void loadClientSettings() {
      File var0 = new File(clientDir());
      if (!var0.exists()) {
         System.out.println("Could not load settings file, does not exist. Creating new default " + var0.getName());
         firstTimeSavedSettings = true;
         saveClientSettings();
      }

      Color var1 = cursorColor;

      try {
         loadClientData(new LoadData(var0));
      } catch (Exception var3) {
         System.err.println("Error loading client settings, some settings may be reset");
         var3.printStackTrace();
         saveClientSettings();
      }

      Screen.updateVolume();
      if (ResourceEncoder.isLoaded() && !cursorColor.equals(var1)) {
         Screen.setCursorColor(cursorColor);
      }

      if (Screen.isCreated()) {
         Screen.setVSync(vSyncEnabled);
         Screen.updateSceneSize();
         Screen.updateHudSize();
      }

      loadModSettings(true);
      loadedClientSettings = true;
   }

   public static boolean hasLoadedClientSettings() {
      return loadedClientSettings;
   }

   public static boolean isFirstTimeSavedSettings() {
      return firstTimeSavedSettings;
   }

   public static boolean loadServerSettings(File var0, boolean var1) {
      boolean var2 = var0.exists();
      if (!var2 && var1) {
         System.out.println("Could not load server settings file, does not exist. Creating new default " + var0.getName());
         saveServerSettings();
         var2 = true;
      }

      if (var2) {
         loadServerData(new LoadData(var0));
      }

      return var2;
   }

   public static void loadServerSettings() {
      File var0 = new File(serverDir());
      loadServerSettings(var0, true);
      loadModSettings(true);
   }

   public static void loadModSettings(boolean var0) {
      if (ModLoader.hasLoadedMods()) {
         Iterator var1 = ModLoader.getEnabledMods().iterator();

         while(var1.hasNext()) {
            LoadedMod var2 = (LoadedMod)var1.next();
            ModSettings var3 = var2.getSettings();

            try {
               File var4 = new File(clientModDir(var2));
               boolean var5 = var4.exists();
               if (!var5 && var0) {
                  var5 = saveModSettings(var2);
               }

               if (var5) {
                  loadModSettings(var2, var3, new LoadData(var4));
               }
            } catch (Exception var6) {
               throw new ModRuntimeException(var2, "Error loading mod settings", var6);
            }
         }
      }

   }

   public static void saveClientSettings() {
      File var0 = new File(clientDir());
      getClientSaveData().saveScript(var0);
      saveModSettings();
   }

   public static void saveServerSettings() {
      File var0 = new File(serverDir());
      getServerSaveData().saveScript(var0);
      saveModSettings();
   }

   private static void saveModSettings() {
      if (ModLoader.hasLoadedMods()) {
         Iterator var0 = ModLoader.getEnabledMods().iterator();

         while(var0.hasNext()) {
            LoadedMod var1 = (LoadedMod)var0.next();
            saveModSettings(var1);
         }
      }

   }

   private static boolean saveModSettings(LoadedMod var0) {
      ModSettings var1 = var0.getSettings();

      try {
         File var2 = new File(clientModDir(var0));
         SaveData var3 = getModSaveData(var0, var1);
         if (!var3.isEmpty()) {
            var3.saveScript(var2);
            return true;
         } else {
            return false;
         }
      } catch (Exception var4) {
         throw new ModRuntimeException(var0, "Error saving mod settings", var4);
      }
   }

   public static void loadBanned() {
      banned = new ArrayList();
      File var0 = new File(GlobalData.cfgPath() + "banned.cfg");

      try {
         if (!var0.exists()) {
            if (GameUtils.mkDirs(var0)) {
               FileOutputStream var1 = new FileOutputStream(var0);
               var1.write(new byte[0]);
               var1.close();
            } else {
               GameLog.warn.println("Could not create folders for file: " + var0.getAbsolutePath());
            }
         }

         BufferedReader var4 = new BufferedReader(new FileReader(var0));

         String var2;
         while((var2 = var4.readLine()) != null) {
            banned.add(var2.trim().toLowerCase());
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static boolean isBanned(String var0) {
      return banned.contains(var0.toLowerCase());
   }

   public static boolean isBanned(long var0) {
      return isBanned("" + var0);
   }

   public static void addBanned(String var0) {
      if (!banned.contains(var0.trim().toLowerCase())) {
         banned.add(var0.trim().toLowerCase());
         File var1 = new File(GlobalData.cfgPath() + "banned.cfg");

         try {
            FileOutputStream var2 = new FileOutputStream(var1);
            Iterator var3 = banned.iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               var2.write((var4 + "\n").getBytes());
            }

            var2.close();
         } catch (IOException var5) {
            var5.printStackTrace();
         }

      }
   }

   public static boolean removeBanned(String var0) {
      if (banned.contains(var0.trim().toLowerCase())) {
         banned.remove(var0.trim().toLowerCase());
         File var1 = new File(GlobalData.cfgPath() + "banned.cfg");

         try {
            FileOutputStream var2 = new FileOutputStream(var1);
            Iterator var3 = banned.iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               var2.write((var4 + "\n").getBytes());
            }

            var2.close();
         } catch (IOException var5) {
            var5.printStackTrace();
         }

         return true;
      } else {
         return false;
      }
   }

   static {
      UI = (GameInterfaceStyle)GameInterfaceStyle.styles.get(0);
      dynamicInterfaceSize = true;
      sharpenInterface = true;
      pixelFont = false;
      showDebugInfo = false;
      showQuestMarkers = true;
      showTeammateMarkers = true;
      showPickupText = true;
      showDamageText = true;
      showDoTText = true;
      showMobHealthBars = true;
      showBossHealthBars = true;
      showControlTips = true;
      showBasicTooltipBackground = false;
      showItemTooltipBackground = true;
      showLogicGateTooltips = false;
      showIngredientsAvailable = true;
      alwaysShowQuickbar = false;
      tooltipTextSize = 16;
      cursorColor = new Color(255, 255, 255);
      cursorSize = 0;
      trackNewQuests = true;
      hasCraftingFilterExpanded = new EventVariable(false);
      hasCraftingListExpanded = new EventVariable(false);
      craftingListShowHidden = new EventVariable(false);
      craftingListOnlyCraftable = new EventVariable(false);
      hideSettlementStorage = new EventVariable(false);
      hideSettlementWorkstations = new EventVariable(false);
      hideSettlementForestryZones = new EventVariable(false);
      hideSettlementHusbandryZones = new EventVariable(false);
      hideSettlementFertilizeZones = new EventVariable(false);
      mapCoordinates = FormTravelContainerGrid.CoordinateSetting.RELATIVE_SELF;
      outputDevice = null;
      masterVolume = 0.5F;
      effectsVolume = 1.0F;
      weatherVolume = 0.2F;
      UIVolume = 1.0F;
      musicVolume = 0.5F;
      muteOnFocusLoss = false;
      displayMode = DisplayMode.Borderless;
      monitor = 0;
      displaySize = null;
      vSyncEnabled = true;
      maxFPS = 0;
      reduceUIFramerate = true;
      sceneColors = SceneColorSetting.Vibrant;
      brightness = 1.0F;
      smoothLighting = true;
      lights = Settings.LightSetting.Color;
      particles = Settings.ParticleSetting.Maximum;
      wavyGrass = true;
      denseGrass = true;
      cameraShake = true;
      alwaysLight = false;
      alwaysRain = false;
      serverPort = 14159;
      serverSlots = 10;
      serverPassword = "";
      maxClientLatencySeconds = 30;
      pauseWhenEmpty = true;
      giveClientsPower = true;
      serverLogging = true;
      serverMOTD = "";
      serverWorld = "";
      serverOwnerName = null;
      serverOwnerAuth = -1L;
      unloadLevelsCooldown = 30;
      worldBorderSize = -1;
      droppedItemsLifeMinutes = 0;
      unloadSettlements = false;
      maxSettlementsPerPlayer = -1;
      maxSettlersPerSettlement = -1;
      jobSearchRange = new GameTileRange(100, new Point[0]);
      zipSaves = true;
      language = Localization.English.stringID;
      serverPerspective = false;
      hideUI = false;
      recipeFilters = new HashMap();
      itemCategoriesExpanded = new HashMap();
   }

   public static enum LightSetting {
      Color(new LocalMessage("settingsui", "lightscolor")),
      White(new LocalMessage("settingsui", "lightswhite"));

      public final GameMessage displayName;

      private LightSetting(GameMessage var3) {
         this.displayName = var3;
      }

      // $FF: synthetic method
      private static LightSetting[] $values() {
         return new LightSetting[]{Color, White};
      }
   }

   public static enum ParticleSetting {
      Minimal(new LocalMessage("settingsui", "particlesmin")),
      Decreased(new LocalMessage("settingsui", "particlesdec")),
      Maximum(new LocalMessage("settingsui", "particlesmax"));

      public final GameMessage displayName;

      private ParticleSetting(GameMessage var3) {
         this.displayName = var3;
      }

      // $FF: synthetic method
      private static ParticleSetting[] $values() {
         return new ParticleSetting[]{Minimal, Decreased, Maximum};
      }
   }
}
