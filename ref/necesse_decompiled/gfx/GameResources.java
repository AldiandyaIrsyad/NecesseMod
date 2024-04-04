package necesse.gfx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.gfx.shader.ColorShader;
import necesse.gfx.shader.DarknessShader;
import necesse.gfx.shader.EdgeMaskShader;
import necesse.gfx.shader.FormShader;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.MaskShader;
import necesse.gfx.shader.RectangleShader;
import necesse.gfx.shader.SharpenShader;
import necesse.gfx.shader.TextureOffsetShader;
import necesse.gfx.shader.WaveShader;
import necesse.gfx.ui.GameInterfaceStyle;
import necesse.inventory.item.Item;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.WallObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.incursion.IncursionBiome;

public class GameResources {
   private static HashMap<String, GameShader> shaders;
   public static GameTexture error;
   public static GameTexture empty;
   public static GameTexture cursors;
   public static GameTexture wire;
   public static GameTexture item_shadow;
   public static GameTexture chains;
   public static GameTexture aim;
   public static GameTexture aimArrow;
   public static GameTexture gradient;
   public static GameTexture dredgingStaffPillars;
   public static GameTexture ancientDredgingStaffPillars;
   public static GameTexture slimeGround;
   public static GameTexture slimeSpike;
   public static GameTexture webParticles;
   public static GameTexture empressAcid;
   public static GameTexture spideriteStaffWeb;
   public static GameTexture smokeParticles;
   public static GameTexture hydroPumpParticles;
   public static GameTexture fallenWizardPortalParticle;
   public static GameTextureSection particles;
   public static GameTextureSection fogParticles;
   public static GameTextureSection debrisParticles;
   public static GameTextureSection puffParticles;
   public static GameTextureSection magicSparkParticles;
   public static GameTextureSection rainBlobParticle;
   public static GameTextureSection liquidBlobParticle;
   public static GameTextureSection bubbleParticle;
   public static GameTextureSection explosiveModifierChargeUp;
   public static GameTexture generatedParticlesTexture;
   public static GameTexture theCrimsonSkyBloodPool;
   public static GameTexture slimeGreatbowSlime;
   public static GameTexture cutSnowballParticles;
   public static GameTexture starBarrierPickup;
   public static SharedGameTexture particlesTextureGenerator;
   public static GameSound rain1;
   public static GameSound rain2;
   public static GameSound rain3;
   public static GameSound rain4;
   public static GameSound rain5;
   public static GameSound tap;
   public static GameSound tap2;
   public static GameSound tick;
   public static GameSound pop;
   public static GameSound swing1;
   public static GameSound swing2;
   public static GameSound flick;
   public static GameSound spit;
   public static GameSound crack;
   public static GameSound crackdeath;
   public static GameSound bow;
   public static GameSound drink;
   public static GameSound eat;
   public static GameSound bowhit;
   public static GameSound gunhit;
   public static GameSound jingle;
   public static GameSound jinglehit;
   public static GameSound magicbolt1;
   public static GameSound magicbolt2;
   public static GameSound magicbolt3;
   public static GameSound magicbolt4;
   public static GameSound firespell1;
   public static GameSound dragonfly1;
   public static GameSound swoosh;
   public static GameSound swoosh2;
   public static GameSound handgun;
   public static GameSound sniperrifle;
   public static GameSound shotgun;
   public static GameSound hurt;
   public static GameSound npchurt;
   public static GameSound npcdeath;
   public static GameSound fizz;
   public static GameSound roar;
   public static GameSound magicroar;
   public static GameSound explosionHeavy;
   public static GameSound explosionLight;
   public static GameSound teleport;
   public static GameSound teleportfail;
   public static GameSound splash;
   public static GameSound punch;
   public static GameSound slimesplash;
   public static GameSound cratebreak1;
   public static GameSound cratebreak2;
   public static GameSound cratebreak3;
   public static GameSound waterblob;
   public static GameSound watersplash;
   public static GameSound grass;
   public static GameSound coins;
   public static GameSound shatter1;
   public static GameSound shatter2;
   public static GameSound squeak;
   public static GameSound chestopen;
   public static GameSound chestclose;
   public static GameSound dooropen;
   public static GameSound doorclose;
   public static GameSound blunthit;
   public static GameSound shake;
   public static GameSound wind;
   public static GameSound rumble;
   public static GameSound shears;
   public static GameSound run;
   public static GameSound train;
   public static GameSound trainBrake;
   public static GameSound cling;
   public static GameSound laserBeam1;
   public static GameSound laserBlast1;
   public static GameSound laserBlast2;
   public static GameSound fadedeath1;
   public static GameSound fadedeath2;
   public static GameSound fadedeath3;
   public static GameSound cameraShutter;
   public static GameSound fireworkFuse;
   public static GameSound fireworkExplosion;
   public static GameSound fireworkCrack;
   public static GameSound campfireAmbient;
   public static GameSound bassNote;
   public static GameSound hatNote;
   public static GameSound kickNote;
   public static GameSound pianoNote;
   public static GameSound snareNote;
   public static WaveShader waveShader;
   public static TextureOffsetShader textureOffsetShader;
   public static RectangleShader rectangleShader;
   public static MaskShader maskShader;
   public static EdgeMaskShader edgeMaskShader;
   public static FormShader formShader;
   public static SharpenShader sharpenShader;
   public static ColorShader colorSettingShader;
   public static ColorShader debugColorShader;
   public static ColorShader brightnessShader;
   public static ColorShader windowColorShader;
   public static GameShader horizontalGaussShader;
   public static GameShader verticalGaussShader;
   public static DarknessShader darknessShader;

   public GameResources() {
   }

   public static void loadTextures() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "textures"));
      GameTexture var0 = new GameTexture("empty", 1, 1);
      var0.setPixel(0, 0, 255, 255, 255, 255);
      var0.makeFinal();
      empty = var0;
      error = GameTexture.fromFile("error");
      cursors = GameTexture.fromFile("cursors");
      wire = GameTexture.fromFile("wire");
      item_shadow = GameTexture.fromFile("items/item_shadow");
      Iterator var1 = GameInterfaceStyle.styles.iterator();

      while(var1.hasNext()) {
         GameInterfaceStyle var2 = (GameInterfaceStyle)var1.next();
         var2.loadTextures();
      }

      chains = GameTexture.fromFile("chains");
      aim = GameTexture.fromFile("aim");
      aimArrow = GameTexture.fromFile("aimarrow");
      gradient = GameTexture.fromFile("gradient");
      SettlerRegistry.loadSettlerIconTextures();
      GlobalData.achievements().loadTextures();
      HumanLook.loadTextures();
      MobRegistry.Textures.load();
      ProjectileRegistry.Textures.load();
      particlesTextureGenerator = new SharedGameTexture("particlesShared");
      particles = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/particles"));
      fogParticles = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/fog"));
      debrisParticles = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/debris"));
      puffParticles = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/puffs"));
      magicSparkParticles = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/magicalspark"));
      rainBlobParticle = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/rainblob"));
      liquidBlobParticle = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/liquidblob"));
      bubbleParticle = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/bubble"));
      explosiveModifierChargeUp = particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/incursionexplosivemodifierchargeup"));
      smokeParticles = GameTexture.fromFile("particles/smoke");
      hydroPumpParticles = GameTexture.fromFile("particles/hydropump");
      dredgingStaffPillars = GameTexture.fromFile("particles/dredgingstaff");
      slimeGround = GameTexture.fromFile("particles/slimeground");
      slimeSpike = GameTexture.fromFile("particles/slimespike");
      webParticles = GameTexture.fromFile("particles/smallweb");
      empressAcid = GameTexture.fromFile("particles/empressacid");
      spideriteStaffWeb = GameTexture.fromFile("particles/spideritestaffweb");
      ancientDredgingStaffPillars = GameTexture.fromFile("particles/ancientdredgingstaff");
      fallenWizardPortalParticle = GameTexture.fromFile("particles/fallenwizardportal");
      theCrimsonSkyBloodPool = GameTexture.fromFile("particles/thecrimsonskybloodpool");
      slimeGreatbowSlime = GameTexture.fromFile("particles/slimegreatbowslime");
      cutSnowballParticles = GameTexture.fromFile("particles/snowball_cut");
      starBarrierPickup = GameTexture.fromFile("particles/starbarrier_pickup");
      GameTile.setupTileTextures();
      WallObject.setupWallTextures();
      var1 = TileRegistry.getTiles().iterator();

      while(var1.hasNext()) {
         GameTile var3 = (GameTile)var1.next();
         var3.loadTileTextures();
      }

      var1 = ObjectRegistry.getObjects().iterator();

      while(var1.hasNext()) {
         GameObject var4 = (GameObject)var1.next();
         var4.loadTextures();
      }

      var1 = LogicGateRegistry.getLogicGates().iterator();

      while(var1.hasNext()) {
         GameLogicGate var5 = (GameLogicGate)var1.next();
         var5.loadTextures();
      }

      var1 = ItemRegistry.getItems().iterator();

      while(var1.hasNext()) {
         Item var6 = (Item)var1.next();
         var6.loadTextures();
      }

      var1 = BuffRegistry.getBuffs().iterator();

      while(var1.hasNext()) {
         Buff var7 = (Buff)var1.next();
         var7.loadTextures();
      }

      var1 = BiomeRegistry.getBiomes().iterator();

      while(var1.hasNext()) {
         Biome var9 = (Biome)var1.next();
         var9.loadTextures();
      }

      var1 = IncursionBiomeRegistry.getBiomes().iterator();

      while(var1.hasNext()) {
         IncursionBiome var10 = (IncursionBiome)var1.next();
         var10.loadTextures();
      }

      if (GlobalData.isDevMode()) {
         ArrayList var8 = new ArrayList(Arrays.asList("cursors", "fonts/bit12", "fonts/bit16", "fonts/bit20", "fonts/bitoutline12", "fonts/bitoutline16", "fonts/bitoutline20", "player/skin/head", "player/skin/body", "player/skin/arms_left", "player/skin/arms_right", "player/skin/feet", "player/hair/haircolors"));

         for(int var11 = 0; var11 < GameHair.getTotalHair(); ++var11) {
            var8.add("player/hair/hair" + var11);
            var8.add("player/hair/hair" + var11 + "_back");
         }

         GameTexture.listUnloadedTextures(var8);
      }

   }

   public static void startSoundLoading() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "sound"));
      GameSound.startLoaderThreads();
      Iterator var0 = MusicRegistry.getMusic().iterator();

      while(var0.hasNext()) {
         GameMusic var1 = (GameMusic)var0.next();
         var1.loadSound();
      }

      rain1 = GameSound.fromFile("weather/rain1");
      rain2 = GameSound.fromFile("weather/rain2");
      rain3 = GameSound.fromFile("weather/rain3");
      rain4 = GameSound.fromFile("weather/rain4");
      rain5 = GameSound.fromFile("weather/rain5");
      tap = GameSound.fromFile("tap", 10);
      tap2 = GameSound.fromFile("tap2", 10);
      tick = GameSound.fromFile("tick", 10);
      pop = GameSound.fromFile("pop", 100);
      swing1 = GameSound.fromFile("swing1");
      swing2 = GameSound.fromFile("swing2");
      flick = GameSound.fromFile("flick");
      spit = GameSound.fromFile("spit");
      crack = GameSound.fromFile("crack", 100);
      crackdeath = GameSound.fromFile("crackdeath", 100);
      bow = GameSound.fromFile("bow");
      drink = GameSound.fromFile("drink");
      eat = GameSound.fromFile("eat");
      bowhit = GameSound.fromFile("bowhit");
      gunhit = GameSound.fromFile("gunhit");
      jingle = GameSound.fromFile("jingle");
      jinglehit = GameSound.fromFile("jinglehit");
      handgun = GameSound.fromFile("handgun");
      sniperrifle = GameSound.fromFile("sniperrifle");
      shotgun = GameSound.fromFile("shotgun");
      hurt = GameSound.fromFile("hurt", 100);
      npchurt = GameSound.fromFile("npchurt", 100);
      npcdeath = GameSound.fromFile("npcdeath", 100);
      fizz = GameSound.fromFile("fizz");
      roar = GameSound.fromFile("roar");
      magicroar = GameSound.fromFile("magicroar");
      explosionHeavy = GameSound.fromFile("explosionheavy");
      explosionLight = GameSound.fromFile("explosionlight");
      teleport = GameSound.fromFile("teleport", 100);
      teleportfail = GameSound.fromFile("teleportfail");
      splash = GameSound.fromFile("splash");
      punch = GameSound.fromFile("punch", 10);
      slimesplash = GameSound.fromFile("slimesplash", 10);
      cratebreak1 = GameSound.fromFile("cratebreak1");
      cratebreak2 = GameSound.fromFile("cratebreak2");
      cratebreak3 = GameSound.fromFile("cratebreak3");
      waterblob = GameSound.fromFile("waterblob");
      watersplash = GameSound.fromFile("watersplash");
      grass = GameSound.fromFile("grass", 10);
      coins = GameSound.fromFile("coins", 10);
      shatter1 = GameSound.fromFile("shatter1");
      shatter2 = GameSound.fromFile("shatter2");
      squeak = GameSound.fromFile("squeak", 100);
      fadedeath1 = GameSound.fromFile("fadedeath1", 100);
      fadedeath2 = GameSound.fromFile("fadedeath2", 100);
      fadedeath3 = GameSound.fromFile("fadedeath3", 100);
      chestopen = GameSound.fromFile("chestopen");
      chestclose = GameSound.fromFile("chestclose");
      dooropen = GameSound.fromFile("dooropen");
      doorclose = GameSound.fromFile("doorclose");
      blunthit = GameSound.fromFile("blunthit");
      shake = GameSound.fromFile("shake");
      wind = GameSound.fromFile("wind");
      rumble = GameSound.fromFile("rumble");
      shears = GameSound.fromFile("shears");
      run = GameSound.fromFile("run");
      train = GameSound.fromFile("train");
      trainBrake = GameSound.fromFile("trainbrake");
      cling = GameSound.fromFile("cling");
      laserBeam1 = GameSound.fromFile("laserbeam1");
      laserBlast1 = GameSound.fromFile("laserblast1");
      laserBlast2 = GameSound.fromFile("laserblast2");
      magicbolt1 = GameSound.fromFile("magicbolt1");
      magicbolt2 = GameSound.fromFile("magicbolt2");
      magicbolt3 = GameSound.fromFile("magicbolt3");
      magicbolt4 = GameSound.fromFile("magicbolt4");
      firespell1 = GameSound.fromFile("firespell1");
      dragonfly1 = GameSound.fromFile("dragonfly1");
      swoosh = GameSound.fromFile("swoosh");
      swoosh2 = GameSound.fromFile("swoosh2");
      cameraShutter = GameSound.fromFile("camerashutter");
      fireworkFuse = GameSound.fromFile("fireworkfuse");
      fireworkExplosion = GameSound.fromFile("fireworkexplosion");
      fireworkCrack = GameSound.fromFile("fireworkcrack");
      campfireAmbient = GameSound.fromFile("campfire");
      bassNote = GameSound.fromFile("notes/bass");
      hatNote = GameSound.fromFile("notes/hat");
      kickNote = GameSound.fromFile("notes/kick");
      pianoNote = GameSound.fromFile("notes/piano");
      snareNote = GameSound.fromFile("notes/snare");
   }

   public static void finishLoadingSounds() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "sound"));
      GameSound.endLoaderThreads();
   }

   public static void loadShaders() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "shaders"));
      deleteShaders();
      shaders = new HashMap();
      addShader("waveShader", waveShader = new WaveShader());
      addShader("textureOffsetShader", textureOffsetShader = new TextureOffsetShader());
      addShader("rectangleShader", rectangleShader = new RectangleShader());
      addShader("maskShader", maskShader = new MaskShader());
      addShader("edgeMaskShader", edgeMaskShader = new EdgeMaskShader());
      addShader("formShader", formShader = new FormShader());
      addShader("sharpenShader", sharpenShader = new SharpenShader());
      addShader("colorSettingShader", colorSettingShader = new ColorShader());
      addShader("debugColorShader", debugColorShader = new ColorShader());
      addShader("brightnessShader", brightnessShader = new ColorShader());
      addShader("windowColorShader", windowColorShader = new ColorShader());
      addShader("horizontalGaussShader", horizontalGaussShader = new GameShader("vertHorizontalGaussBlur", "fragGaussBlur"));
      addShader("verticalGaussShader", verticalGaussShader = new GameShader("vertVerticalGaussBlur", "fragGaussBlur"));
      addShader("darknessShader", darknessShader = new DarknessShader());
   }

   public static void loadCursors() {
      GameTexture var0 = GameTexture.fromFile("cursors");
      GameTexture var1;
      if (var0.isFinal()) {
         var0.restoreFinal();
         var1 = new GameTexture(var0);
         var0.makeFinal();
      } else {
         var1 = new GameTexture(var0);
      }

      var1.applyColor(Settings.cursorColor, (var0x, var1x) -> {
         int var2 = GameMath.max(var0x.getRed(), var0x.getGreen(), var0x.getBlue());
         int var3 = GameMath.min(var0x.getRed(), var0x.getGreen(), var0x.getBlue());
         float var4 = var2 != 0 ? (float)(var2 - var3) / (float)var2 : 0.0F;
         if (var4 != 0.0F) {
            return var0x;
         } else {
            int var5 = (int)((float)var0x.getRed() / 255.0F * ((float)var1x.getRed() / 255.0F) * 255.0F);
            int var6 = (int)((float)var0x.getGreen() / 255.0F * ((float)var1x.getGreen() / 255.0F) * 255.0F);
            int var7 = (int)((float)var0x.getBlue() / 255.0F * ((float)var1x.getBlue() / 255.0F) * 255.0F);
            return new Color(var5, var6, var7, var0x.getAlpha());
         }
      });
      Screen.CURSOR[] var2 = Screen.CURSOR.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Screen.CURSOR var5 = var2[var4];
         var5.loadCursor(var1, Settings.cursorSize);
      }

   }

   public static void deleteShaders() {
      if (shaders != null) {
         Iterator var0 = shaders.keySet().iterator();

         while(var0.hasNext()) {
            String var1 = (String)var0.next();
            ((GameShader)shaders.get(var1)).delete();
         }

         shaders.clear();
      }
   }

   private static void addShader(String var0, GameShader var1) {
      if (shaders.containsKey(var0)) {
         throw new NullPointerException("ERROR: Conflicted shader name: " + var0);
      } else {
         shaders.put(var0, var1);
      }
   }

   public static GameShader getShader(String var0) {
      return (GameShader)shaders.get(var0);
   }

   public static HashMap<String, GameShader> getShaders() {
      return shaders;
   }
}
