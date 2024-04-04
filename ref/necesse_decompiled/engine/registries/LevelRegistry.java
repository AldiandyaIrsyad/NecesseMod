package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.DebugLevel;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.TemporaryDummyLevel;
import necesse.level.maps.biomes.BasicCaveLevel;
import necesse.level.maps.biomes.BasicDeepCaveLevel;
import necesse.level.maps.biomes.BasicSurfaceLevel;
import necesse.level.maps.biomes.desert.DesertCaveLevel;
import necesse.level.maps.biomes.desert.DesertDeepCaveLevel;
import necesse.level.maps.biomes.desert.DesertSurfaceLevel;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;
import necesse.level.maps.biomes.dungeon.DungeonLevel;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.biomes.plains.PlainsSurfaceLevel;
import necesse.level.maps.biomes.snow.SnowCaveLevel;
import necesse.level.maps.biomes.snow.SnowDeepCaveLevel;
import necesse.level.maps.biomes.snow.SnowSurfaceLevel;
import necesse.level.maps.biomes.swamp.SwampCaveLevel;
import necesse.level.maps.biomes.swamp.SwampDeepCaveLevel;
import necesse.level.maps.biomes.swamp.SwampSurfaceLevel;
import necesse.level.maps.biomes.temple.TempleArenaLevel;
import necesse.level.maps.biomes.temple.TempleLevel;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.incursion.DesertDeepCaveIncursionLevel;
import necesse.level.maps.incursion.ForestDeepCaveIncursionLevel;
import necesse.level.maps.incursion.GraveyardIncursionLevel;
import necesse.level.maps.incursion.MoonArenaIncursionLevel;
import necesse.level.maps.incursion.SlimeCaveIncursionLevel;
import necesse.level.maps.incursion.SnowDeepCaveIncursionLevel;
import necesse.level.maps.incursion.SpiderCastleIncursionLevel;
import necesse.level.maps.incursion.SunArenaIncursionLevel;
import necesse.level.maps.incursion.SwampDeepCaveIncursionLevel;

public class LevelRegistry extends ClassedGameRegistry<Level, LevelRegistryElement> {
   public static final LevelRegistry instance = new LevelRegistry();

   public LevelRegistry() {
      super("Level", 32762);
   }

   public void registerCore() {
      registerLevel("level", Level.class);
      registerLevel("dummy", TemporaryDummyLevel.class);
      registerLevel("debug", DebugLevel.class);
      registerLevel("basicsurface", BasicSurfaceLevel.class);
      registerLevel("basiccave", BasicCaveLevel.class);
      registerLevel("basicdeepcave", BasicDeepCaveLevel.class);
      registerLevel("forestsurface", ForestSurfaceLevel.class);
      registerLevel("forestcave", ForestCaveLevel.class);
      registerLevel("forestdeepcave", ForestDeepCaveLevel.class);
      registerLevel("plainssurface", PlainsSurfaceLevel.class);
      registerLevel("snowsurface", SnowSurfaceLevel.class);
      registerLevel("snowcave", SnowCaveLevel.class);
      registerLevel("snowdeepcave", SnowDeepCaveLevel.class);
      registerLevel("swampsurface", SwampSurfaceLevel.class);
      registerLevel("swampcave", SwampCaveLevel.class);
      registerLevel("swampdeepcave", SwampDeepCaveLevel.class);
      registerLevel("desertsurface", DesertSurfaceLevel.class);
      registerLevel("desertcave", DesertCaveLevel.class);
      registerLevel("desertdeepcave", DesertDeepCaveLevel.class);
      registerLevel("trialroom", TrialRoomLevel.class);
      registerLevel("dungeon", DungeonLevel.class);
      registerLevel("dungeonarena", DungeonArenaLevel.class);
      registerLevel("temple", TempleLevel.class);
      registerLevel("templearena", TempleArenaLevel.class);
      registerLevel("incursion", IncursionLevel.class);
      registerLevel("forestdeepcaveincursion", ForestDeepCaveIncursionLevel.class);
      registerLevel("snowdeepcaveincursion", SnowDeepCaveIncursionLevel.class);
      registerLevel("swampdeepcaveincursion", SwampDeepCaveIncursionLevel.class);
      registerLevel("desertdeepcaveincursion", DesertDeepCaveIncursionLevel.class);
      registerLevel("slimecaveincursion", SlimeCaveIncursionLevel.class);
      registerLevel("graveyardincursion", GraveyardIncursionLevel.class);
      registerLevel("spidercastleincursion", SpiderCastleIncursionLevel.class);
      registerLevel("sunarenaincursion", SunArenaIncursionLevel.class);
      registerLevel("moonarenaincursion", MoonArenaIncursionLevel.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerLevel(String var0, Class<? extends Level> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register levels");
      } else {
         try {
            return instance.register(var0, new LevelRegistryElement(var1));
         } catch (NoSuchMethodException var3) {
            System.err.println("Could not register level " + var1.getSimpleName() + ": Missing constructor with parameters: LevelIdentifier, int (width), int (height), WorldEntity");
            return -1;
         }
      }
   }

   public static Level getNewLevel(int var0, LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      try {
         return (Level)((LevelRegistryElement)instance.getElement(var0)).newInstance(new Object[]{var1, var2, var3, var4});
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var6) {
         throw new RuntimeException(var6);
      }
   }

   public static int getLevelID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getLevelID(Class<? extends Level> var0) {
      return instance.getElementID(var0);
   }

   protected static class LevelRegistryElement extends ClassIDDataContainer<Level> {
      public LevelRegistryElement(Class<? extends Level> var1) throws NoSuchMethodException {
         super(var1, LevelIdentifier.class, Integer.TYPE, Integer.TYPE, WorldEntity.class);
      }
   }
}
