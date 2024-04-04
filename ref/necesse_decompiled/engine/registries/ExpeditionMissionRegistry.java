package necesse.engine.registries;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import necesse.engine.expeditions.CaveExpedition;
import necesse.engine.expeditions.CommonFishingTripExpedition;
import necesse.engine.expeditions.DeepCaveExpedition;
import necesse.engine.expeditions.DeepDesertCaveExpedition;
import necesse.engine.expeditions.DeepSnowCaveExpedition;
import necesse.engine.expeditions.DeepSwampCaveExpedition;
import necesse.engine.expeditions.DesertCaveExpedition;
import necesse.engine.expeditions.DungeonExpedition;
import necesse.engine.expeditions.FishingTripExpedition;
import necesse.engine.expeditions.MiningTripExpedition;
import necesse.engine.expeditions.PirateExpedition;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.expeditions.SnowCaveExpedition;
import necesse.engine.expeditions.SwampCaveExpedition;
import necesse.engine.expeditions.TypesFishingTripExpedition;

public class ExpeditionMissionRegistry extends GameRegistry<SettlerExpedition> {
   public static final ExpeditionMissionRegistry instance = new ExpeditionMissionRegistry();
   public static Set<Integer> explorerExpeditionIDs = new HashSet();
   public static Set<Integer> minerExpeditionIDs = new HashSet();
   public static Set<Integer> fishingTripIDs = new HashSet();

   private ExpeditionMissionRegistry() {
      super("Expedition", 32762);
   }

   public void registerCore() {
      registerExplorerExpedition("cave", new CaveExpedition());
      registerExplorerExpedition("snowcave", new SnowCaveExpedition());
      registerExplorerExpedition("dungeon", new DungeonExpedition());
      registerExplorerExpedition("swampcave", new SwampCaveExpedition());
      registerExplorerExpedition("desertcave", new DesertCaveExpedition());
      registerExplorerExpedition("pirate", new PirateExpedition());
      registerExplorerExpedition("deepcave", new DeepCaveExpedition());
      registerExplorerExpedition("deepsnowcave", new DeepSnowCaveExpedition());
      registerExplorerExpedition("deepswampcave", new DeepSwampCaveExpedition());
      registerExplorerExpedition("deepdesertcave", new DeepDesertCaveExpedition());
      registerMinerExpedition("copperminingtrip", new MiningTripExpedition((String)null, 100, 75, 100, "stone", "copperore", new String[0]));
      registerMinerExpedition("ironminingtrip", new MiningTripExpedition((String)null, 150, 100, 150, "stone", "ironore", new String[0]));
      registerMinerExpedition("goldminingtrip", new MiningTripExpedition("evilsprotector", 250, 175, 250, "stone", "goldore", new String[0]));
      registerMinerExpedition("frostshardminingtrip", new MiningTripExpedition("queenspider", 300, 200, 300, "snowstone", "frostshard", new String[0]));
      registerMinerExpedition("ivyminingtrip", new MiningTripExpedition("swampguardian", 300, 200, 300, "swampstone", "ivyore", new String[0]));
      registerMinerExpedition("quartzminingtrip", new MiningTripExpedition("ancientvulture", 450, 300, 450, "sandstone", "quartz", new String[0]));
      registerMinerExpedition("lifequartzminingtrip", new MiningTripExpedition("reaper", 600, 400, 600, "deepstone", "lifequartz", new String[0]));
      registerMinerExpedition("tungstenminingtrip", new MiningTripExpedition("reaper", 600, 400, 600, "deepstone", "tungstenore", new String[0]));
      registerMinerExpedition("glacialminingtrip", new MiningTripExpedition("cryoqueen", 600, 400, 600, "deepsnowstone", "glacialore", new String[0]));
      registerMinerExpedition("myceliumminingtrip", new MiningTripExpedition("pestwarden", 600, 400, 600, "deepswampstone", "myceliumore", new String[0]));
      registerMinerExpedition("ancientfossilminingtrip", new MiningTripExpedition("sageandgrit", 600, 400, 600, "deepsandstone", "ancientfossilore", new String[0]));
      registerFishingTrip("commonfishtrip", new CommonFishingTripExpedition());
      registerFishingTrip("gobfishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "gobfish", new String[]{"halffish", "furfish"}));
      registerFishingTrip("terrorfishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "terrorfish", new String[]{"rockfish"}));
      registerFishingTrip("halffishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "halffish", new String[]{"gobfish", "furfish"}));
      registerFishingTrip("rockfishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "rockfish", new String[]{"terrorfish"}));
      registerFishingTrip("furfishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "furfish", new String[]{"gobfish", "halffish"}));
      registerFishingTrip("icefishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "icefish", new String[]{"gobfish", "halffish"}));
      registerFishingTrip("swampfishtrip", new TypesFishingTripExpedition((String)null, 500, 400, 500, "swampfish", new String[]{"gobfish", "halffish"}));
   }

   protected void onRegister(SettlerExpedition var1, int var2, String var3, boolean var4) {
      var1.initDisplayName();
   }

   protected void onRegistryClose() {
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         SettlerExpedition var2 = (SettlerExpedition)var1.next();
         var2.onExpeditionMissionRegistryClosed();
      }

      explorerExpeditionIDs = Collections.unmodifiableSet(explorerExpeditionIDs);
      minerExpeditionIDs = Collections.unmodifiableSet(minerExpeditionIDs);
      fishingTripIDs = Collections.unmodifiableSet(fishingTripIDs);
   }

   public static int registerExplorerExpedition(String var0, SettlerExpedition var1) {
      int var2 = registerExpedition(var0, var1);
      explorerExpeditionIDs.add(var2);
      return var2;
   }

   public static int registerMinerExpedition(String var0, SettlerExpedition var1) {
      int var2 = registerExpedition(var0, var1);
      minerExpeditionIDs.add(var2);
      return var2;
   }

   public static int registerFishingTrip(String var0, FishingTripExpedition var1) {
      int var2 = registerExpedition(var0, var1);
      fishingTripIDs.add(var2);
      return var2;
   }

   public static int registerExpedition(String var0, SettlerExpedition var1) {
      return instance.register(var0, var1);
   }

   public static int replaceExpedition(String var0, SettlerExpedition var1) {
      return instance.replace(var0, var1);
   }

   public static Iterable<SettlerExpedition> getExpeditions() {
      return instance.getElements();
   }

   public static Stream<SettlerExpedition> streamExpeditions() {
      return instance.streamElements();
   }

   public static SettlerExpedition getExpedition(int var0) {
      if (var0 >= instance.size()) {
         var0 = 0;
      }

      return (SettlerExpedition)instance.getElement(var0);
   }

   public static int getExpeditionID(String var0) {
      return instance.getElementID(var0);
   }

   public static SettlerExpedition getExpedition(String var0) {
      return (SettlerExpedition)instance.getElement(var0);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((SettlerExpedition)var1, var2, var3, var4);
   }
}
