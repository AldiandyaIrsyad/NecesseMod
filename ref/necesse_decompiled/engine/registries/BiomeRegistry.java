package necesse.engine.registries;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldGenerator;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.desert.DesertVillageBiome;
import necesse.level.maps.biomes.dungeon.DungeonBiome;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.forest.ForestVillageBiome;
import necesse.level.maps.biomes.incursions.ArenaBiome;
import necesse.level.maps.biomes.incursions.DesertDeepCaveBiome;
import necesse.level.maps.biomes.incursions.ForestDeepCaveBiome;
import necesse.level.maps.biomes.incursions.GraveyardBiome;
import necesse.level.maps.biomes.incursions.SlimeCaveBiome;
import necesse.level.maps.biomes.incursions.SnowDeepCaveBiome;
import necesse.level.maps.biomes.incursions.SpiderCastleBiome;
import necesse.level.maps.biomes.incursions.SwampDeepCaveBiome;
import necesse.level.maps.biomes.pirate.PirateVillageBiome;
import necesse.level.maps.biomes.plains.PlainsBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.snow.SnowVillageBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.biomes.temple.TempleBiome;
import necesse.level.maps.biomes.trial.TrialRoomBiome;

public class BiomeRegistry extends GameRegistry<BiomeRegistryElement<?>> {
   public static Biome UNKNOWN;
   public static Biome FOREST;
   public static Biome PLAINS;
   public static Biome DESERT;
   public static Biome DESERT_VILLAGE;
   public static Biome SWAMP;
   public static Biome SNOW;
   public static Biome SNOW_VILLAGE;
   public static Biome DUNGEON_ISLAND;
   public static Biome FOREST_VILLAGE;
   public static Biome PIRATE_ISLAND;
   public static Biome TEMPLE;
   public static Biome TRIAL_ROOM;
   public static Biome FOREST_DEEP_CAVE_INCURSION;
   public static Biome SNOW_DEEP_CAVE_INCURSION;
   public static Biome SWAMP_DEEP_CAVE_INCURSION;
   public static Biome DESERT_DEEP_CAVE_INCURSION;
   public static Biome SLIME_CAVE;
   public static Biome GRAVEYARD;
   public static Biome SPIDER_CASTLE;
   public static Biome SUN_ARENA;
   public static Biome MOON_ARENA;
   private static HashSet<String> biomeTypes = new HashSet();
   private static int totalBiomesDiscoverable = 0;
   private static int totalBiomesTypesDiscoverable = 0;
   public static Predicate<Point> defaultSpawnIslandFilter = (var0) -> {
      if (WorldGenerator.getBiome(var0.x, var0.y) != FOREST) {
         return false;
      } else if (WorldGenerator.getIslandSize(var0.x, var0.y) < 0.8F) {
         return false;
      } else {
         boolean var1 = false;
         boolean var2 = false;
         boolean var3 = false;
         Point[] var4 = Level.adjacentGetters;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Point var7 = var4[var6];
            Biome var8 = WorldGenerator.getBiome(var0.x + var7.x, var0.y + var7.y);
            if (!var1 && (var8 == SNOW || var8 == SNOW_VILLAGE)) {
               var1 = true;
            }

            if (!var2 && (var8 == SWAMP || var8 == DESERT || var8 == DESERT_VILLAGE)) {
               var2 = true;
            }

            if (!var3 && var8.hasVillage()) {
               var3 = true;
            }

            if (var1 && var2 && var3) {
               break;
            }
         }

         for(int var9 = var0.x - 2; var9 <= var0.x + 2; ++var9) {
            for(var5 = var0.y - 2; var5 <= var0.y + 2; ++var5) {
               Biome var10 = WorldGenerator.getBiome(var9, var5);
               if (var10 == PIRATE_ISLAND) {
                  return false;
               }
            }
         }

         return var1 && var2 && var3;
      }
   };
   public static final BiomeRegistry instance = new BiomeRegistry();
   private static int ticketCounter = 0;

   private BiomeRegistry() {
      super("Biome", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "biomes"));
      UNKNOWN = registerBiome("unknown", new Biome(), 0, (String)null);
      FOREST = registerBiome("forest", new ForestBiome(), 1000, "forest");
      PLAINS = registerBiome("plains", new PlainsBiome(), 600, "plains");
      DESERT = registerBiome("desert", new DesertBiome(), 400, "desert");
      DESERT_VILLAGE = registerBiome("desertvillage", new DesertVillageBiome(), 100, "desert");
      SWAMP = registerBiome("swamp", new SwampBiome(), 300, "swamp");
      SNOW = registerBiome("snow", new SnowBiome(), 400, "snow");
      SNOW_VILLAGE = registerBiome("snowvillage", new SnowVillageBiome(), 100, "snow");
      DUNGEON_ISLAND = registerBiome("dungeon", new DungeonBiome(), 100, "forest");
      FOREST_VILLAGE = registerBiome("forestvillage", new ForestVillageBiome(), 300, "forest");
      PIRATE_ISLAND = registerBiome("piratevillage", new PirateVillageBiome(), 100, "forest");
      TEMPLE = registerBiome("temple", new TempleBiome(), 0, (String)null);
      TRIAL_ROOM = registerBiome("trialroom", new TrialRoomBiome(), 0, (String)null);
      FOREST_DEEP_CAVE_INCURSION = registerBiome("forestdeepcave", new ForestDeepCaveBiome(), 0, (String)null);
      SNOW_DEEP_CAVE_INCURSION = registerBiome("snowdeepcave", new SnowDeepCaveBiome(), 0, (String)null);
      SWAMP_DEEP_CAVE_INCURSION = registerBiome("swampdeepcave", new SwampDeepCaveBiome(), 0, (String)null);
      DESERT_DEEP_CAVE_INCURSION = registerBiome("desertdeepcave", new DesertDeepCaveBiome(), 0, (String)null);
      SLIME_CAVE = registerBiome("slimecave", new SlimeCaveBiome(), 0, (String)null);
      GRAVEYARD = registerBiome("graveyard", new GraveyardBiome(), 0, (String)null);
      SPIDER_CASTLE = registerBiome("spidercastle", new SpiderCastleBiome(), 0, (String)null);
      SUN_ARENA = registerBiome("sunarena", new ArenaBiome(), 0, (String)null);
      MOON_ARENA = registerBiome("moonarena", new ArenaBiome(), 0, (String)null);
   }

   protected void onRegister(BiomeRegistryElement<?> var1, int var2, String var3, boolean var4) {
      var1.startTicket = ticketCounter;
      ticketCounter += var1.tickets;
      var1.endTicket = ticketCounter;
   }

   protected void onRegistryClose() {
      instance.streamElements().map((var0) -> {
         return var0.biome;
      }).forEach(Biome::updateLocalDisplayName);
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         BiomeRegistryElement var2 = (BiomeRegistryElement)var1.next();
         var2.biome.onBiomeRegistryClosed();
      }

      totalBiomesDiscoverable = (int)this.streamElements().filter((var0) -> {
         return var0.isDiscoverable;
      }).count();
      this.streamElements().filter((var0) -> {
         return var0.discoverType != null;
      }).forEach((var0) -> {
         biomeTypes.add(var0.discoverType);
      });
      totalBiomesTypesDiscoverable = biomeTypes.size();
   }

   public static <T extends Biome> T registerBiome(String var0, T var1, int var2, String var3) {
      return registerBiome(var0, var1, var2, var2 > 0, var3);
   }

   public static <T extends Biome> T registerBiome(String var0, T var1, int var2, boolean var3, String var4) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register biomes");
      } else {
         return ((BiomeRegistryElement)instance.registerObj(var0, new BiomeRegistryElement(var1, var2, var3, var4))).biome;
      }
   }

   public static List<Biome> getBiomes() {
      return (List)instance.streamElements().map((var0) -> {
         return var0.biome;
      }).collect(Collectors.toList());
   }

   public static int getTotalBiomes() {
      return instance.size() - 1;
   }

   public static int getTotalDiscoverableBiomes() {
      return totalBiomesDiscoverable;
   }

   public static int getTotalDiscoverableBiomesTypes() {
      return totalBiomesTypesDiscoverable;
   }

   public static Biome getBiome(String var0) {
      return getBiome(getBiomeID(var0));
   }

   public static Biome getBiome(int var0) {
      return ((BiomeRegistryElement)instance.getElement(var0)).biome;
   }

   public static int getBiomeID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getBiomeIDRaw(String var0) throws NoSuchElementException {
      return instance.getElementIDRaw(var0);
   }

   public static String getBiomeStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static Biome getRandomBiome(long var0) {
      int var2 = (new GameRandom(var0)).nextInt(ticketCounter);
      return (Biome)instance.streamElements().filter((var1) -> {
         return var1.matchTicket(var2);
      }).map((var0x) -> {
         return var0x.biome;
      }).findFirst().orElse((Object)null);
   }

   public static int getTickets(int var0) {
      return var0 == -1 ? 0 : ((BiomeRegistryElement)instance.getElement(var0)).tickets;
   }

   public static boolean isDiscoverable(int var0) {
      return var0 == -1 ? false : ((BiomeRegistryElement)instance.getElement(var0)).isDiscoverable;
   }

   public static String getDiscoverType(int var0) {
      return var0 == -1 ? null : ((BiomeRegistryElement)instance.getElement(var0)).discoverType;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((BiomeRegistryElement)var1, var2, var3, var4);
   }

   protected static class BiomeRegistryElement<T extends Biome> implements IDDataContainer {
      public final T biome;
      public final int tickets;
      public int startTicket;
      public int endTicket;
      public final boolean isDiscoverable;
      public final String discoverType;

      public BiomeRegistryElement(T var1, int var2, boolean var3, String var4) {
         this.biome = var1;
         this.tickets = var2;
         this.isDiscoverable = var3;
         this.discoverType = var4;
      }

      public boolean matchTicket(int var1) {
         return this.startTicket <= var1 && var1 < this.endTicket;
      }

      public IDData getIDData() {
         return this.biome.idData;
      }
   }
}
