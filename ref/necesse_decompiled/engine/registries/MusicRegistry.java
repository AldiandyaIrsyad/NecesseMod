package necesse.engine.registries;

import java.awt.Color;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.gfx.GameMusic;

public class MusicRegistry extends StaticObjectGameRegistry<MusicRegistryElement> {
   public static GameMusic AdventureBegins;
   public static GameMusic ForestPath;
   public static GameMusic AwakeningTwilight;
   public static GameMusic DepthsOfTheForest;
   public static GameMusic SecretsOfTheForest;
   public static GameMusic AuroraTundra;
   public static GameMusic PolarNight;
   public static GameMusic GlaciersEmbrace;
   public static GameMusic SubzeroSanctum;
   public static GameMusic WatersideSerenade;
   public static GameMusic GatorsLullaby;
   public static GameMusic SwampCavern;
   public static GameMusic MurkyMire;
   public static GameMusic SandCatacombs;
   public static GameMusic OasisSerenade;
   public static GameMusic NightInTheDunes;
   public static GameMusic DustyHollows;
   public static GameMusic VoidsEmbrace;
   public static GameMusic PiratesHorizon;
   public static GameMusic VenomousReckoning;
   public static GameMusic StormingTheHamletPart1;
   public static GameMusic StormingTheHamletPart2;
   public static GameMusic TheFirstTrial;
   public static GameMusic QueenSpidersDance;
   public static GameMusic PestWardensCharge;
   public static GameMusic WizardsRematch;
   public static GameMusic WizardsAwakening;
   public static GameMusic RumbleOfTheSwampGuardian;
   public static GameMusic AncientVulturesFeast;
   public static GameMusic PirateCaptainsLair;
   public static GameMusic WrathOfTheEmpress;
   public static GameMusic MoonlightsRehearsal;
   public static GameMusic SunlightsExam;
   public static GameMusic TheEldersJingleJam;
   public static GameMusic Home;
   public static GameMusic WaterFae;
   public static GameMusic Muses;
   public static GameMusic Running;
   public static GameMusic GrindTheAlarms;
   public static GameMusic ByTheField;
   public static GameMusic SunStones;
   public static GameMusic CaravanTusks;
   public static GameMusic HomeAtLast;
   public static GameMusic TellTale;
   public static GameMusic IcyRuse;
   public static GameMusic IceStar;
   public static GameMusic EyesOfTheDesert;
   public static GameMusic Rialto;
   public static GameMusic SilverLake;
   public static GameMusic Away;
   public static GameMusic Kronos;
   public static GameMusic LostGrip;
   public static GameMusic ElekTrak;
   public static GameMusic TheControlRoom;
   public static GameMusic AirlockFailure;
   public static GameMusic KonsoleGlitch;
   public static GameMusic Beatdown;
   public static GameMusic Siege;
   public static GameMusic Halodrome;
   public static GameMusic Millenium;
   public static GameMusic Kandiru;
   public static final MusicRegistry instance = new MusicRegistry();

   private MusicRegistry() {
      super("Music", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "loading"));
      AdventureBegins = registerMusic("adventurebegins", "music/adventurebegins", (GameMessage)null, new StaticMessage("Adventure Begins"), new Color(47, 105, 12), new Color(47, 105, 12));
      ForestPath = registerMusic("forestpath", "music/forestpath", (GameMessage)null, new StaticMessage("Forest Path"), new Color(47, 105, 12), new Color(81, 136, 34));
      AwakeningTwilight = registerMusic("awakeningtwilight", "music/awakeningtwilight", (GameMessage)null, new StaticMessage("Awakening Twilight"), new Color(47, 105, 12), new Color(81, 101, 174));
      DepthsOfTheForest = registerMusic("depthsoftheforest", "music/depthsoftheforest", (GameMessage)null, new StaticMessage("Depths of the Forest"), new Color(47, 105, 12), new Color(126, 108, 84));
      SecretsOfTheForest = registerMusic("secretsoftheforest", "music/secretsoftheforest", (GameMessage)null, new StaticMessage("Secrets of The Forest"), new Color(47, 105, 12), new Color(209, 170, 57));
      AuroraTundra = registerMusic("auroratundra", "music/auroratundra", (GameMessage)null, new StaticMessage("Aurora Tundra"), new Color(183, 232, 245), new Color(49, 142, 184));
      PolarNight = registerMusic("polarnight", "music/polarnight", (GameMessage)null, new StaticMessage("Polar Night"), new Color(183, 232, 245), new Color(121, 100, 186));
      GlaciersEmbrace = registerMusic("glaciersembrace", "music/glaciersembrace", (GameMessage)null, new StaticMessage("Glaciers Embrace"), new Color(183, 232, 245), new Color(138, 193, 150));
      SubzeroSanctum = registerMusic("subzerosanctum", "music/subzerosanctum", (GameMessage)null, new StaticMessage("Subzero Sanctum"), new Color(20, 118, 192), new Color(183, 232, 245));
      WatersideSerenade = registerMusic("watersideserenade", "music/watersideserenade", (GameMessage)null, new StaticMessage("Waterside Serenade"), new Color(150, 154, 38), new Color(120, 158, 36));
      GatorsLullaby = registerMusic("gatorslullaby", "music/gatorslullaby", (GameMessage)null, new StaticMessage("Gator's Lullaby"), new Color(150, 154, 38), new Color(81, 101, 174));
      MurkyMire = registerMusic("murkymire", "music/murkymire", (GameMessage)null, new StaticMessage("Murky Mire"), new Color(150, 154, 38), new Color(126, 97, 68));
      SwampCavern = registerMusic("swampcavern", "music/swampcavern", (GameMessage)null, new StaticMessage("Swamp Cavern"), new Color(150, 154, 38), new Color(191, 90, 62));
      OasisSerenade = registerMusic("oasisserenade", "music/oasisserenade", (GameMessage)null, new StaticMessage("Oasis Serenade"), new Color(232, 203, 130), new Color(178, 144, 98));
      NightInTheDunes = registerMusic("nightinthedunes", "music/nightinthedunes", (GameMessage)null, new StaticMessage("Night in the Dunes"), new Color(232, 203, 130), new Color(100, 101, 157));
      DustyHollows = registerMusic("dustyhollows", "music/dustyhollows", (GameMessage)null, new StaticMessage("Dusty Hollows"), new Color(232, 203, 130), new Color(172, 111, 110));
      SandCatacombs = registerMusic("sandcatacombs", "music/sandcatacombs", (GameMessage)null, new StaticMessage("Sand Catacombs"), new Color(232, 203, 130), new Color(182, 60, 53));
      VoidsEmbrace = registerMusic("voidsembrace", "music/voidsembrace", (GameMessage)null, new StaticMessage("Void's Embrace"), new Color(121, 100, 186), new Color(169, 150, 236));
      PiratesHorizon = registerMusic("pirateshorizon", "music/pirateshorizon", (GameMessage)null, new StaticMessage("Pirate's Horizon"), new Color(255, 177, 8), new Color(156, 51, 39));
      VenomousReckoning = registerMusic("venomousreckoning", "music/venomousreckoning", (GameMessage)null, new StaticMessage("Venomous Reckoning"), new Color(127, 189, 57), new Color(208, 204, 50));
      StormingTheHamletPart1 = registerMusic("stormingthehamletpart1", "music/stormingthehamletpart1", (GameMessage)null, new StaticMessage("Storming the Hamlet Part 1"), new Color(255, 177, 8), new Color(191, 90, 62));
      StormingTheHamletPart2 = registerMusic("stormingthehamletpart2", "music/stormingthehamletpart2", (GameMessage)null, new StaticMessage("Storming the Hamlet Part 2"), new Color(255, 177, 8), new Color(156, 51, 39));
      TheFirstTrial = registerMusic("firsttrial", "music/firsttrial", (GameMessage)null, new StaticMessage("First Trial"), new Color(156, 51, 39), new Color(81, 136, 34), 1000, 4000);
      QueenSpidersDance = registerMusic("queenspidersdance", "music/queenspidersdance", (GameMessage)null, new StaticMessage("Queen Spider's Dance"), new Color(156, 51, 39), new Color(81, 101, 174), 1000, 4000);
      WizardsAwakening = registerMusic("wizardsawakening", "music/wizardsawakening", (GameMessage)null, new StaticMessage("Wizard's Awakening"), new Color(156, 51, 39), new Color(149, 133, 241), 1000, 4000);
      RumbleOfTheSwampGuardian = registerMusic("rumbleoftheswampguardian", "music/rumbleoftheswampguardian", (GameMessage)null, new StaticMessage("Rumble of the Swamp Guardian"), new Color(156, 51, 39), new Color(150, 154, 38), 1000, 4000);
      AncientVulturesFeast = registerMusic("ancientvulturesfeast", "music/ancientvulturesfeast", (GameMessage)null, new StaticMessage("Ancient Vulture's Feast"), new Color(97, 115, 8), new Color(156, 51, 39), 1000, 4000);
      PirateCaptainsLair = registerMusic("piratecaptainslair", "music/piratecaptainslair", (GameMessage)null, new StaticMessage("Pirate Captain's Lair"), new Color(255, 177, 8), new Color(156, 51, 39), 1000, 4000);
      PestWardensCharge = registerMusic("pestwardenscharge", "music/pestwardenscharge", (GameMessage)null, new StaticMessage("Pest Warden's Charge"), new Color(156, 51, 39), new Color(97, 115, 8), 1000, 4000);
      WizardsRematch = registerMusic("wizardsrematch", "music/wizardsrematch", (GameMessage)null, new StaticMessage("Wizard's Rematch"), new Color(156, 51, 39), new Color(121, 100, 186), 1000, 4000);
      WrathOfTheEmpress = registerMusic("wrathoftheempress", "music/wrathoftheempress", (GameMessage)null, new StaticMessage("Wrath of the Empress"), new Color(156, 51, 39), new Color(0, 107, 109), 1000, 4000);
      MoonlightsRehearsal = registerMusic("moonlightsrehearsal", "music/moonlightsrehearsal", (GameMessage)null, new StaticMessage("Moonlight's Rehearsal"), new Color(156, 51, 39), new Color(4, 100, 194), 1000, 4000);
      SunlightsExam = registerMusic("sunlightsexam", "music/sunlightsexam", (GameMessage)null, new StaticMessage("Sunlight's Exam"), new Color(156, 51, 39), new Color(255, 207, 67), 1000, 4000);
      TheEldersJingleJam = registerMusic("theeldersjinglejam", "music/theeldersjinglejam", (GameMessage)null, new StaticMessage("The Elder's Jingle Jam"), new Color(228, 92, 95), new Color(205, 210, 218));
      float var1 = 0.6F;
      Home = registerMusic("home", "music/home", (GameMessage)null, new StaticMessage("Home"), new Color(125, 164, 45), new Color(47, 105, 12)).setVolumeModifier(var1);
      WaterFae = registerMusic("waterfae", "music/waterfae", (GameMessage)null, new StaticMessage("WaterFae"), new Color(81, 136, 34), new Color(47, 105, 12)).setVolumeModifier(var1);
      Muses = registerMusic("muses", "music/muses", (GameMessage)null, new StaticMessage("Muses"), new Color(81, 101, 174), new Color(47, 105, 12)).setVolumeModifier(var1);
      Running = registerMusic("running", "music/running", (GameMessage)null, new StaticMessage("Running"), new Color(126, 108, 84), new Color(47, 105, 12)).setVolumeModifier(var1);
      GrindTheAlarms = registerMusic("grindthealarms", "music/grindthealarms", (GameMessage)null, new StaticMessage("GrindTheAlarms"), new Color(209, 170, 57), new Color(47, 105, 12)).setVolumeModifier(var1);
      HomeAtLast = registerMusic("homeatlast", "music/homeatlast", (GameMessage)null, new StaticMessage("HomeAtLast"), new Color(49, 142, 184), new Color(183, 232, 245)).setVolumeModifier(var1);
      TellTale = registerMusic("telltale", "music/telltale", (GameMessage)null, new StaticMessage("TellTale"), new Color(121, 100, 186), new Color(183, 232, 245)).setVolumeModifier(var1);
      IcyRuse = registerMusic("icyruse", "music/icyruse", (GameMessage)null, new StaticMessage("IcyRuse"), new Color(138, 193, 150), new Color(183, 232, 245)).setVolumeModifier(var1);
      IceStar = registerMusic("icestar", "music/icestar", (GameMessage)null, new StaticMessage("IceStar"), new Color(20, 118, 192), new Color(183, 232, 245)).setVolumeModifier(var1);
      EyesOfTheDesert = registerMusic("eyesofthedesert", "music/eyesofthedesert", (GameMessage)null, new StaticMessage("EyesOfTheDesert"), new Color(120, 158, 36), new Color(150, 154, 38)).setVolumeModifier(var1);
      Rialto = registerMusic("rialto", "music/rialto", (GameMessage)null, new StaticMessage("Rialto"), new Color(81, 101, 174), new Color(150, 154, 38)).setVolumeModifier(var1);
      SilverLake = registerMusic("silverlake", "music/silverlake", (GameMessage)null, new StaticMessage("SilverLake"), new Color(126, 97, 68), new Color(150, 154, 38)).setVolumeModifier(var1);
      ByTheField = registerMusic("bythefield", "music/bythefield", (GameMessage)null, new StaticMessage("ByTheField"), new Color(178, 144, 98), new Color(232, 203, 130)).setVolumeModifier(var1);
      SunStones = registerMusic("sunstones", "music/sunstones", (GameMessage)null, new StaticMessage("SunStones"), new Color(100, 101, 157), new Color(232, 203, 130)).setVolumeModifier(var1);
      CaravanTusks = registerMusic("caravantusks", "music/caravantusks", (GameMessage)null, new StaticMessage("CaravanTusks"), new Color(172, 111, 110), new Color(232, 203, 130)).setVolumeModifier(var1);
      Away = registerMusic("away", "music/away", (GameMessage)null, new StaticMessage("Away"), new Color(255, 177, 8), new Color(156, 51, 39)).setVolumeModifier(var1);
      Kronos = registerMusic("kronos", "music/kronos", (GameMessage)null, new StaticMessage("Kronos"), new Color(169, 150, 236), new Color(121, 100, 186)).setVolumeModifier(var1);
      LostGrip = registerMusic("lostgrip", "music/lostgrip", (GameMessage)null, new StaticMessage("LostGrip"), new Color(191, 90, 62), new Color(255, 177, 8)).setVolumeModifier(var1);
      ElekTrak = registerMusic("elektrak", "music/elektrak", (GameMessage)null, new StaticMessage("ElekTrak"), new Color(81, 136, 34), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      TheControlRoom = registerMusic("thecontrolroom", "music/thecontrolroom", (GameMessage)null, new StaticMessage("TheControlRoom"), new Color(81, 101, 174), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      AirlockFailure = registerMusic("airlockfailure", "music/airlockfailure", (GameMessage)null, new StaticMessage("AirlockFailure"), new Color(149, 133, 241), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      KonsoleGlitch = registerMusic("konsoleglitch", "music/konsoleglitch", (GameMessage)null, new StaticMessage("KonsoleGlitch"), new Color(150, 154, 38), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      Beatdown = registerMusic("beatdown", "music/beatdown", (GameMessage)null, new StaticMessage("Beatdown"), new Color(97, 115, 8), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      Siege = registerMusic("siege", "music/siege", (GameMessage)null, new StaticMessage("Siege"), new Color(255, 177, 8), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      Halodrome = registerMusic("halodrome", "music/halodrome", (GameMessage)null, new StaticMessage("Halodrome"), new Color(65, 116, 85), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      Millenium = registerMusic("millenium", "music/millenium", (GameMessage)null, new StaticMessage("Millenium"), new Color(183, 232, 245), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
      Kandiru = registerMusic("kandiru", "music/kandiru", (GameMessage)null, new StaticMessage("Kandiru"), new Color(232, 203, 130), new Color(156, 51, 39), 1000, 4000).setVolumeModifier(var1);
   }

   protected void onRegister(MusicRegistryElement var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
   }

   public static GameMusic registerMusic(String var0, String var1, GameMessage var2, GameMessage var3, Color var4, Color var5) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register music");
      } else {
         return ((MusicRegistryElement)instance.registerObj(var0, new MusicRegistryElement(new GameMusic(var1, var2, var3, var4, var5)))).music;
      }
   }

   public static GameMusic registerMusic(String var0, String var1, GameMessage var2, GameMessage var3, Color var4, Color var5, int var6, int var7) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register music");
      } else {
         return ((MusicRegistryElement)instance.registerObj(var0, new MusicRegistryElement(new GameMusic(var1, var2, var3, var4, var5, var6, var7)))).music;
      }
   }

   public static List<GameMusic> getMusic() {
      return (List)instance.streamElements().map((var0) -> {
         return var0.music;
      }).collect(Collectors.toList());
   }

   public static GameMusic getMusic(String var0) {
      return getMusic(getMusicID(var0));
   }

   public static GameMusic getMusic(int var0) {
      return ((MusicRegistryElement)instance.getElement(var0)).music;
   }

   public static int getMusicID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getMusicIDRaw(String var0) throws NoSuchElementException {
      return instance.getElementIDRaw(var0);
   }

   public static String getMusicStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((MusicRegistryElement)var1, var2, var3, var4);
   }

   protected static class MusicRegistryElement implements IDDataContainer {
      public final GameMusic music;

      public MusicRegistryElement(GameMusic var1) {
         this.music = var1;
      }

      public IDData getIDData() {
         return this.music.idData;
      }
   }
}
