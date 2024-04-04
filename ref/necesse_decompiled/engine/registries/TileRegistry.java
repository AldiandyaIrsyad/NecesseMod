package necesse.engine.registries;

import java.awt.Color;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.gameTile.CryptAshTile;
import necesse.level.gameTile.DeepIceTile;
import necesse.level.gameTile.DeepRockTile;
import necesse.level.gameTile.DeepSandstoneTile;
import necesse.level.gameTile.DeepSnowRockTile;
import necesse.level.gameTile.DeepSwampRockTile;
import necesse.level.gameTile.DirtTile;
import necesse.level.gameTile.DungeonFloorTile;
import necesse.level.gameTile.EmptyTile;
import necesse.level.gameTile.FarmlandTile;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.GravelTile;
import necesse.level.gameTile.IceTile;
import necesse.level.gameTile.LavaPathTile;
import necesse.level.gameTile.LavaTile;
import necesse.level.gameTile.MoonPath;
import necesse.level.gameTile.MudTile;
import necesse.level.gameTile.PathTiledTile;
import necesse.level.gameTile.RockTile;
import necesse.level.gameTile.SandBrickTile;
import necesse.level.gameTile.SandGravelTile;
import necesse.level.gameTile.SandTile;
import necesse.level.gameTile.SandstoneTile;
import necesse.level.gameTile.SimpleFloorTile;
import necesse.level.gameTile.SimpleTerrainTile;
import necesse.level.gameTile.SimpleTiledFloorTile;
import necesse.level.gameTile.SlimeLiquidTile;
import necesse.level.gameTile.SlimeRockTile;
import necesse.level.gameTile.SnowRockTile;
import necesse.level.gameTile.SnowTile;
import necesse.level.gameTile.SpiderNestTile;
import necesse.level.gameTile.SwampGrassTile;
import necesse.level.gameTile.SwampRockTile;
import necesse.level.gameTile.WaterTile;

public class TileRegistry extends GameRegistry<TileRegistryElement> {
   public static final TileRegistry instance = new TileRegistry();
   private static String[] stringIDs = null;
   public static int waterID;
   public static int emptyID;
   public static int dirtID;
   public static int sandID;
   public static int grassID;
   public static int gravelID;
   public static int swampRockID;
   public static int deepSwampRockID;
   public static int swampGrassID;
   public static int snowID;
   public static int iceID;
   public static int mudID;
   public static int spiderNestID;
   public static int cryptAshID;
   public static int woodFloorID;
   public static int pineFloorID;
   public static int palmFloorID;
   public static int woodPathID;
   public static int stoneFloorID;
   public static int stoneBrickFloorID;
   public static int stoneTiledFloorID;
   public static int stonePathID;
   public static int snowStoneFloorID;
   public static int snowStoneBrickFloorID;
   public static int snowStonePathID;
   public static int swampStoneFloorID;
   public static int swampStoneBrickFloorID;
   public static int swampStonePathID;
   public static int sandstoneFloorID;
   public static int sandstoneBrickFloorID;
   public static int sandstonePathID;
   public static int deepStoneFloorID;
   public static int deepStoneBrickFloorID;
   public static int deepStoneTiledFloorID;
   public static int deepSnowStoneFloorID;
   public static int deepSnowStoneBrickFloorID;
   public static int deepSwampStoneFloorID;
   public static int deepSwampStoneBrickFloorID;

   private TileRegistry() {
      super("Tile", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "tiles"));
      emptyID = registerTile("emptytile", new EmptyTile(), 0.0F, false);
      dirtID = registerTile("dirttile", new DirtTile(), 0.0F, false);
      waterID = registerTile("watertile", new WaterTile(), 20.0F, true);
      grassID = registerTile("grasstile", new GrassTile(), 0.0F, false);
      sandID = registerTile("sandtile", new SandTile(), 0.5F, true);
      swampGrassID = registerTile("swampgrasstile", new SwampGrassTile(), 0.0F, false);
      mudID = registerTile("mudtile", new MudTile(), 5.0F, true);
      registerTile("rocktile", new RockTile(), 5.0F, true);
      registerTile("dungeonfloor", new DungeonFloorTile(), 5.0F, true);
      registerTile("farmland", new FarmlandTile(), 5.0F, true);
      woodFloorID = registerTile("woodfloor", new SimpleFloorTile("woodfloor", new Color(170, 132, 80)), 2.0F, true);
      pineFloorID = registerTile("pinefloor", new SimpleFloorTile("pinefloor", new Color(125, 89, 59)), 2.0F, true);
      palmFloorID = registerTile("palmfloor", new SimpleTiledFloorTile("palmfloor", new Color(139, 104, 52)), 2.0F, true);
      woodPathID = registerTile("woodpathtile", new PathTiledTile("woodpath", new Color(115, 91, 55)), 5.0F, true);
      registerTile("deadwoodfloor", new SimpleFloorTile("deadwoodfloor", new Color(63, 60, 49)), 2.0F, true);
      stoneFloorID = registerTile("stonefloor", new SimpleFloorTile("stonefloor", new Color(130, 130, 130)), 2.0F, true);
      stoneBrickFloorID = registerTile("stonebrickfloor", new SimpleFloorTile("stonebrickfloor", new Color(130, 130, 130)), 2.0F, true);
      stoneTiledFloorID = registerTile("stonetiledfloor", new SimpleFloorTile("stonetiledfloor", new Color(130, 130, 130)), 2.0F, true);
      stonePathID = registerTile("stonepathtile", new PathTiledTile("stonepath", new Color(107, 107, 107)), 5.0F, true);
      registerTile("sandstonetile", new SandstoneTile(), 5.0F, true);
      sandstoneFloorID = registerTile("sandstonefloor", new SimpleFloorTile("sandstonefloor", new Color(209, 194, 148)), 2.0F, true);
      sandstoneBrickFloorID = registerTile("sandstonebrickfloor", new SimpleFloorTile("sandstonebrickfloor", new Color(209, 194, 148)), 2.0F, true);
      sandstonePathID = registerTile("sandstonepathtile", new PathTiledTile("sandstonepath", new Color(180, 168, 127)), 5.0F, true);
      swampRockID = registerTile("swamprocktile", new SwampRockTile(), 5.0F, true);
      swampStoneFloorID = registerTile("swampstonefloor", new SimpleFloorTile("swampstonefloor", new Color(81, 88, 81)), 2.0F, true);
      swampStoneBrickFloorID = registerTile("swampstonebrickfloor", new SimpleFloorTile("swampstonebrickfloor", new Color(81, 88, 81)), 2.0F, true);
      swampStonePathID = registerTile("swampstonepathtile", new PathTiledTile("swampstonepath", new Color(65, 71, 65)), 5.0F, true);
      registerTile("snowrocktile", new SnowRockTile(), 5.0F, true);
      registerTile("lavatile", new LavaTile(), 20.0F, true);
      snowID = registerTile("snowtile", new SnowTile(), 5.0F, true);
      iceID = registerTile("icetile", new IceTile(), 10.0F, true);
      snowStoneFloorID = registerTile("snowstonefloor", new SimpleFloorTile("snowstonefloor", new Color(176, 176, 176)), 2.0F, true);
      snowStoneBrickFloorID = registerTile("snowstonebrickfloor", new SimpleFloorTile("snowstonebrickfloor", new Color(176, 176, 176)), 2.0F, true);
      snowStonePathID = registerTile("snowstonepathtile", new PathTiledTile("snowstonepath", new Color(153, 153, 153)), 5.0F, true);
      gravelID = registerTile("graveltile", new GravelTile(), 0.0F, false);
      registerTile("sandbrick", new SandBrickTile(), 5.0F, true);
      registerTile("deeprocktile", new DeepRockTile(), 5.0F, true);
      deepStoneFloorID = registerTile("deepstonefloor", new SimpleFloorTile("deepstonefloor", new Color(60, 63, 64)), 2.0F, true);
      deepStoneBrickFloorID = registerTile("deepstonebrickfloor", new SimpleFloorTile("deepstonebrickfloor", new Color(60, 63, 64)), 2.0F, true);
      deepStoneTiledFloorID = registerTile("deepstonetiledfloor", new SimpleFloorTile("deepstonetiledfloor", new Color(60, 63, 64)), 2.0F, true);
      registerTile("deepsnowrocktile", new DeepSnowRockTile(), 5.0F, true);
      deepSnowStoneFloorID = registerTile("deepsnowstonefloor", new SimpleFloorTile("deepsnowstonefloor", new Color(57, 75, 85)), 2.0F, true);
      deepSnowStoneBrickFloorID = registerTile("deepsnowstonebrickfloor", new SimpleFloorTile("deepsnowstonebrickfloor", new Color(57, 75, 85)), 2.0F, true);
      deepSwampRockID = registerTile("deepswamprocktile", new DeepSwampRockTile(), 5.0F, true);
      deepSwampStoneFloorID = registerTile("deepswampstonefloor", new SimpleFloorTile("deepswampstonefloor", new Color(50, 78, 51)), 2.0F, true);
      deepSwampStoneBrickFloorID = registerTile("deepswampstonebrickfloor", new SimpleFloorTile("deepswampstonebrickfloor", new Color(50, 78, 51)), 2.0F, true);
      registerTile("deepicetile", new DeepIceTile(), 10.0F, true);
      registerTile("sandgraveltile", new SandGravelTile(), 0.0F, false);
      spiderNestID = registerTile("spidernesttile", new SpiderNestTile(), 0.0F, false);
      cryptAshID = registerTile("cryptash", new CryptAshTile(), 0.0F, false);
      registerTile("deepsandstonetile", new DeepSandstoneTile(), 5.0F, true);
      registerTile("strawtile", new SimpleTerrainTile("strawtile", new Color(184, 148, 31)), 2.0F, true);
      registerTile("liquidslimetile", new SlimeLiquidTile(), 20.0F, true);
      registerTile("slimerocktile", new SlimeRockTile(), 5.0F, true);
      registerTile("cryptpath", new PathTiledTile("cryptpath", new Color(46, 46, 52)), 5.0F, true);
      registerTile("spidercastlefloor", new SimpleFloorTile("spidercastlefloor", new Color(24, 31, 47)), 5.0F, true);
      registerTile("spidercobbletile", new SimpleFloorTile("spidercobbletile", new Color(59, 56, 85)), 5.0F, true);
      registerTile("spidercastlecarpet", new PathTiledTile("spidercastlecarpet", new Color(151, 84, 117)), 5.0F, true);
      registerTile("dawnpath", new PathTiledTile("dawnpath", new Color(199, 130, 130)), 5.0F, false);
      registerTile("lavapath", new LavaPathTile(), 5.0F, false);
      registerTile("moonpath", new MoonPath(), 5.0F, false);
      registerTile("darkmoonpath", new PathTiledTile("darkmoonpath", new Color(50, 30, 80)), 5.0F, false);
      registerTile("darkfullmoonpath", new PathTiledTile("darkfullmoonpath", new Color(30, 10, 60)), 5.0F, false);
   }

   protected void onRegister(TileRegistryElement var1, int var2, String var3, boolean var4) {
      TileItem var5 = var1.tile.generateNewTileItem();
      if (var5 != null) {
         ItemRegistry.replaceItem(var1.tile.getStringID(), var5, var1.itemBrokerValue, var1.itemObtainable, var1.itemCountInStats);
      }

   }

   protected void onRegistryClose() {
      this.streamElements().map((var0) -> {
         return var0.tile;
      }).forEach(GameTile::updateLocalDisplayName);
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         TileRegistryElement var2 = (TileRegistryElement)var1.next();
         var2.tile.onTileRegistryClosed();
      }

      stringIDs = (String[])instance.streamElements().map((var0) -> {
         return var0.tile.getStringID();
      }).toArray((var0) -> {
         return new String[var0];
      });
   }

   public static int registerTile(String var0, GameTile var1, float var2, boolean var3) {
      return registerTile(var0, var1, var2, var3, var3);
   }

   public static int registerTile(String var0, GameTile var1, float var2, boolean var3, boolean var4) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register tiles");
      } else {
         return instance.register(var0, new TileRegistryElement(var1, var2, var3, var4));
      }
   }

   public static int replaceTile(String var0, GameTile var1, float var2, boolean var3) {
      return replaceTile(var0, var1, var2, var3, var3);
   }

   public static int replaceTile(String var0, GameTile var1, float var2, boolean var3, boolean var4) {
      return instance.replace(var0, new TileRegistryElement(var1, var2, var3, var4));
   }

   public static Stream<GameTile> streamTiles() {
      return instance.streamElements().map((var0) -> {
         return var0.tile;
      });
   }

   public static Iterable<GameTile> getTiles() {
      return (Iterable)streamTiles().collect(Collectors.toList());
   }

   public static GameTile getTile(int var0) {
      if (var0 >= instance.size()) {
         var0 = 0;
      }

      TileRegistryElement var1 = (TileRegistryElement)instance.getElement(var0);
      if (var1 == null) {
         throw new NullPointerException(instance.objectCallName + " ID " + var0 + " seems to be missing or corrupted");
      } else {
         return var1.tile;
      }
   }

   public static int getTileID(String var0) {
      return instance.getElementID(var0);
   }

   public static GameTile getTile(String var0) {
      TileRegistryElement var1 = (TileRegistryElement)instance.getElement(var0);
      if (var1 == null) {
         throw new NullPointerException(instance.objectCallName + " stringID " + var0 + " seems to be missing or corrupted");
      } else {
         return var1.tile;
      }
   }

   public static String[] getTileStringIDs() {
      if (stringIDs == null) {
         throw new IllegalStateException("TileRegistry not yet closed");
      } else {
         return stringIDs;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((TileRegistryElement)var1, var2, var3, var4);
   }

   protected static class TileRegistryElement implements IDDataContainer {
      public final GameTile tile;
      public final float itemBrokerValue;
      public final boolean itemObtainable;
      public final boolean itemCountInStats;

      public TileRegistryElement(GameTile var1, float var2, boolean var3, boolean var4) {
         this.tile = var1;
         this.itemBrokerValue = var2;
         this.itemObtainable = var3;
         this.itemCountInStats = var4;
      }

      public IDData getIDData() {
         return this.tile.idData;
      }
   }
}
