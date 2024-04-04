package necesse.engine.registries;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.AdvancedWorkstationObject;
import necesse.level.gameObject.AirObject;
import necesse.level.gameObject.AlchemyTableObject;
import necesse.level.gameObject.AncientTotemObject;
import necesse.level.gameObject.ApiaryObject;
import necesse.level.gameObject.BannerStandObject;
import necesse.level.gameObject.BeeHiveObject;
import necesse.level.gameObject.CactusObject;
import necesse.level.gameObject.CampfireObject;
import necesse.level.gameObject.CandlePedestalObject;
import necesse.level.gameObject.CarpentersBenchObject;
import necesse.level.gameObject.CartographerTableObject;
import necesse.level.gameObject.CattailObject;
import necesse.level.gameObject.CheesePressObject;
import necesse.level.gameObject.ChristmasTreeObject;
import necesse.level.gameObject.ChristmasWreathObject;
import necesse.level.gameObject.CobwebObject;
import necesse.level.gameObject.CoinPileObject;
import necesse.level.gameObject.ColumnObject;
import necesse.level.gameObject.CompostBinObject;
import necesse.level.gameObject.CookingPotObject;
import necesse.level.gameObject.CookingStationObject;
import necesse.level.gameObject.CryptGrassObject;
import necesse.level.gameObject.CustomWildFlowerObject;
import necesse.level.gameObject.DeepSwampGrassObject;
import necesse.level.gameObject.DeepSwampTallGrassObject;
import necesse.level.gameObject.DemonicWorkstationObject;
import necesse.level.gameObject.DoubleStreetlampObject;
import necesse.level.gameObject.DungeonEntranceObject;
import necesse.level.gameObject.DungeonExitObject;
import necesse.level.gameObject.FallenAlchemyTableObject;
import necesse.level.gameObject.FallenAltarObject;
import necesse.level.gameObject.FallenWorkstationObject;
import necesse.level.gameObject.FeedingTroughObject;
import necesse.level.gameObject.FenceGateObject;
import necesse.level.gameObject.FenceObject;
import necesse.level.gameObject.FireChaliceObject;
import necesse.level.gameObject.FireworkDispenserObject;
import necesse.level.gameObject.FlowerPotObject;
import necesse.level.gameObject.FruitBushObject;
import necesse.level.gameObject.FruitTreeObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.GrainMillObject;
import necesse.level.gameObject.GravestoneObject;
import necesse.level.gameObject.GroundSlimeObject;
import necesse.level.gameObject.HomestoneObject;
import necesse.level.gameObject.IncineratorInventoryObject;
import necesse.level.gameObject.IronAnvilObject;
import necesse.level.gameObject.LEDPanelObject;
import necesse.level.gameObject.LadderDownObject;
import necesse.level.gameObject.LeverObject;
import necesse.level.gameObject.MaskedPressurePlateObject;
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.gameObject.ModularCarpetObject;
import necesse.level.gameObject.MusicPlayerObject;
import necesse.level.gameObject.ProcessingForgeObject;
import necesse.level.gameObject.RandomCoinStackObject;
import necesse.level.gameObject.RandomCrateObject;
import necesse.level.gameObject.RandomVaseObject;
import necesse.level.gameObject.RoastingStationObject;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.RockOreObject;
import necesse.level.gameObject.RoyalEggObject;
import necesse.level.gameObject.SalvageStationObject;
import necesse.level.gameObject.SaplingObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.gameObject.SettlementFlagObject;
import necesse.level.gameObject.SignObject;
import necesse.level.gameObject.SingleOreRockSmall;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.gameObject.SingleRockSmall;
import necesse.level.gameObject.SnowManTrainingDummyObject;
import necesse.level.gameObject.SnowPileObject;
import necesse.level.gameObject.SpiderCastleWallCandleObject;
import necesse.level.gameObject.SpiderEggObject;
import necesse.level.gameObject.SpiderThroneObject;
import necesse.level.gameObject.SpideriteArmorStandObject;
import necesse.level.gameObject.SpikeTrapObject;
import necesse.level.gameObject.StoneCoffinObject;
import necesse.level.gameObject.StreetlampObject;
import necesse.level.gameObject.SurfaceGrassObject;
import necesse.level.gameObject.SwampGrassObject;
import necesse.level.gameObject.TNTObject;
import necesse.level.gameObject.TempleEntranceObject;
import necesse.level.gameObject.TempleExitObject;
import necesse.level.gameObject.TemplePedestalObject;
import necesse.level.gameObject.ThornsObject;
import necesse.level.gameObject.TikiTorchObject;
import necesse.level.gameObject.TorchObject;
import necesse.level.gameObject.TrainingDummyObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.gameObject.TravelstoneObject;
import necesse.level.gameObject.TreeObject;
import necesse.level.gameObject.TreeSaplingObject;
import necesse.level.gameObject.TrialEntranceObject;
import necesse.level.gameObject.TrialExitObject;
import necesse.level.gameObject.UpgradeStationObject;
import necesse.level.gameObject.WallArrowTrapObject;
import necesse.level.gameObject.WallCandleObject;
import necesse.level.gameObject.WallFlameTrapObject;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.WallSawTrapObject;
import necesse.level.gameObject.WallTorchObject;
import necesse.level.gameObject.WallVoidTrapObject;
import necesse.level.gameObject.WaterPlantObject;
import necesse.level.gameObject.WaystoneObject;
import necesse.level.gameObject.WorkstationObject;
import necesse.level.gameObject.furniture.ArmorStandObject;
import necesse.level.gameObject.furniture.BathtubObject;
import necesse.level.gameObject.furniture.BedObject;
import necesse.level.gameObject.furniture.BenchObject;
import necesse.level.gameObject.furniture.BookshelfObject;
import necesse.level.gameObject.furniture.CabinetObject;
import necesse.level.gameObject.furniture.CandelabraObject;
import necesse.level.gameObject.furniture.CandlesObject;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.gameObject.furniture.ClockObject;
import necesse.level.gameObject.furniture.CoolingBoxInventoryObject;
import necesse.level.gameObject.furniture.DeskObject;
import necesse.level.gameObject.furniture.DinnerTableObject;
import necesse.level.gameObject.furniture.DisplayStandObject;
import necesse.level.gameObject.furniture.DresserObject;
import necesse.level.gameObject.furniture.FlowerObject;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.gameObject.furniture.InventoryObject;
import necesse.level.gameObject.furniture.LampObject;
import necesse.level.gameObject.furniture.ModularTableObject;
import necesse.level.gameObject.furniture.StorageBoxInventoryObject;
import necesse.level.gameObject.furniture.ToiletObject;

public class ObjectRegistry extends GameRegistry<ObjectRegistryElement> {
   public static int cobWebID;
   public static int thornsID;
   public static final ObjectRegistry instance = new ObjectRegistry();
   private static String[] stringIDs = null;

   private ObjectRegistry() {
      super("Object", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "objects"));
      Color var1 = new Color(150, 119, 70);
      Color var2 = new Color(255, 190, 45);
      registerObject("air", new AirObject(), 0.0F, false);
      registerObject("coin", new CoinPileObject(), 1.0F, true);
      registerObject("oaktree", new TreeObject("oaktree", "oaklog", "oaksapling", new Color(86, 69, 40), 45, 60, 110, "oakleaves"), 0.0F, false);
      registerObject("oaksapling", new TreeSaplingObject("oaksapling", "oaktree", 1800, 2700, true), 5.0F, true);
      registerObject("sprucetree", new TreeObject("sprucetree", "sprucelog", "sprucesapling", new Color(86, 69, 40), 45, 60, 110, "spruceleaves"), 0.0F, false);
      registerObject("sprucesapling", new TreeSaplingObject("sprucesapling", "sprucetree", 1800, 2700, true), 5.0F, true);
      registerObject("pinetree", new TreeObject("pinetree", "pinelog", "pinesapling", new Color(86, 69, 40), 32, 60, 120, "pineleaves"), 0.0F, false);
      registerObject("pinesapling", new TreeSaplingObject("pinesapling", "pinetree", 1800, 2700, true), 5.0F, true);
      registerObject("palmtree", new TreeObject("palmtree", "palmlog", "palmsapling", new Color(86, 69, 40), 40, 80, 120, "palmleaves"), 0.0F, false);
      registerObject("palmsapling", new TreeSaplingObject("palmsapling", "palmtree", 1800, 2700, true, new String[]{"sandtile", "grasstile", "dirttile", "farmland", "snowtile"}), 5.0F, true);
      registerObject("willowtree", new TreeObject("willowtree", "willowlog", "willowsapling", new Color(86, 69, 40), 42, 70, 110, "willowleaves"), 0.0F, false);
      registerObject("willowsapling", new TreeSaplingObject("willowsapling", "willowtree", 1800, 2700, true), 5.0F, true);
      registerObject("cactus", new CactusObject("cactussapling"), 0.0F, false);
      registerObject("cactussapling", new TreeSaplingObject("cactussapling", "cactus", 1800, 2700, true, new String[]{"sandtile", "farmland"}), 5.0F, true);
      registerObject("deadwoodtree", new TreeObject("deadwood", "deadwoodlog", (String)null, new Color(47, 43, 30), 42, 42, 70, "deadwoodleaves"), 0.0F, false);
      registerObject("appletree", new FruitTreeObject("appletree", "sprucelog", "applesapling", 900.0F, 1800.0F, "apple", 1.5F, 4, new Color(205, 47, 15), 30, 60, 100, "appleleaves"), 0.0F, false);
      registerObject("applesapling", new TreeSaplingObject("applesapling", "appletree", 1800, 2700, false), 50.0F, true);
      registerObject("lemontree", new FruitTreeObject("lemontree", "sprucelog", "lemonsapling", 900.0F, 1800.0F, "lemon", 1.5F, 4, new Color(214, 213, 65), 30, 60, 100, "lemonleaves"), 0.0F, false);
      registerObject("lemonsapling", new TreeSaplingObject("lemonsapling", "lemontree", 1800, 2700, false), 50.0F, true);
      registerObject("coconuttree", new FruitTreeObject("coconuttree", "palmlog", "coconutsapling", 900.0F, 1800.0F, "coconut", 1.5F, 4, new Color(134, 86, 52), 42, 70, 110, "fruitpalmleaves"), 0.0F, false);
      registerObject("coconutsapling", new TreeSaplingObject("coconutsapling", "coconuttree", 1800, 2700, false, new String[]{"sandtile", "grasstile", "dirttile", "farmland", "snowtile"}), 50.0F, true);
      registerObject("bananatree", new FruitTreeObject("bananatree", "palmlog", "bananasapling", 900.0F, 1800.0F, "banana", 1.5F, 4, new Color(200, 177, 18), 42, 60, 80, "fruitpalmleaves"), 0.0F, false);
      registerObject("bananasapling", new TreeSaplingObject("bananasapling", "bananatree", 1800, 2700, false, new String[]{"sandtile", "grasstile", "dirttile", "farmland", "snowtile"}), 50.0F, true);
      registerObject("blueberrybush", (new FruitBushObject("blueberrybush", "blueberrysapling", 900.0F, 1800.0F, "blueberry", 1.0F, 2, new Color(15, 108, 205))).setDebrisColor(new Color(46, 99, 39)), 0.0F, false);
      registerObject("blueberrysapling", new SaplingObject("blueberrysapling", "blueberrybush", 1200, 2100, false), 30.0F, true);
      registerObject("blackberrybush", (new FruitBushObject("blackberrybush", "blackberrysapling", 900.0F, 1800.0F, "blackberry", 1.0F, 2, new Color(60, 29, 95))).setDebrisColor(new Color(50, 115, 44)), 0.0F, false);
      registerObject("blackberrysapling", new SaplingObject("blackberrysapling", "blackberrybush", 1200, 2100, false), 30.0F, true);
      registerObject("workstation", new WorkstationObject(), 10.0F, true);
      registerObject("forge", new ProcessingForgeObject(), 2.0F, true);
      CarpentersBenchObject.registerCarpentersBench();
      registerObject("ironanvil", new IronAnvilObject(), 20.0F, true);
      registerObject("demonicworkstation", new DemonicWorkstationObject(), 40.0F, true);
      registerObject("alchemytable", new AlchemyTableObject(), 10.0F, true);
      AdvancedWorkstationObject.registerAdvancedWorkstation();
      FallenWorkstationObject.registerFallenWorkstation();
      registerObject("fallenalchemytable", new FallenAlchemyTableObject(), 50.0F, true);
      registerObject("cartographertable", new CartographerTableObject(), 10.0F, true);
      int[] var3 = WallObject.registerWallObjects("wood", "woodwall", 0, new Color(86, 69, 40), ToolType.ALL, 2.0F, 6.0F);
      WallObject var4 = (WallObject)getObject(var3[0]);
      int[] var5 = WallObject.registerWallObjects("pine", "pinewall", 0, new Color(104, 69, 34), ToolType.ALL, 2.0F, 6.0F);
      WallObject var6 = (WallObject)getObject(var5[0]);
      int[] var7 = WallObject.registerWallObjects("palm", "palmwall", 0, new Color(104, 79, 39), ToolType.ALL, 2.0F, 6.0F);
      WallObject var8 = (WallObject)getObject(var7[0]);
      int[] var9 = WallObject.registerWallObjects("stone", "stonewall", 0, new Color(105, 105, 105), 0.5F, 1.0F);
      WallObject var10 = (WallObject)getObject(var9[0]);
      int[] var11 = WallObject.registerWallObjects("sandstone", "sandstonewall", 0, new Color(215, 215, 125), 2.0F, 6.0F);
      WallObject var12 = (WallObject)getObject(var11[0]);
      int[] var13 = WallObject.registerWallObjects("swampstone", "swampstonewall", 0, new Color(56, 69, 53), 2.0F, 6.0F);
      WallObject var14 = (WallObject)getObject(var13[0]);
      int[] var15 = WallObject.registerWallObjects("snowstone", "snowstonewall", 0, new Color(207, 207, 207), 2.0F, 6.0F);
      WallObject var16 = (WallObject)getObject(var15[0]);
      int[] var17 = WallObject.registerWallObjects("ice", "icewall", 0, new Color(134, 152, 193), 5.0F, 10.0F);
      WallObject var18 = (WallObject)getObject(var17[0]);
      WallObject.registerWallObjects("brick", "brickwall", 0, new Color(165, 89, 61), 5.0F, 10.0F);
      int[] var19 = WallObject.registerWallObjects("dungeon", "dungeonwall", 1, new Color(29, 32, 43), 10.0F, 20.0F);
      WallObject var20 = (WallObject)getObject(var19[0]);
      int[] var21 = WallObject.registerWallObjects("deepstone", "deepstonewall", 1, new Color(55, 60, 62), 0.5F, 1.0F);
      WallObject var22 = (WallObject)getObject(var21[0]);
      int[] var23 = WallObject.registerWallObjects("obsidian", "obsidianwall", 2, new Color(37, 27, 40), 2.0F, 10.0F);
      WallObject var24 = (WallObject)getObject(var23[0]);
      int[] var25 = WallObject.registerWallObjects("deepsnowstone", "deepsnowstonewall", 2, new Color(59, 68, 76), 2.0F, 10.0F);
      WallObject var26 = (WallObject)getObject(var25[0]);
      int[] var27 = WallObject.registerWallObjects("deepswampstone", "deepswampstonewall", 3, new Color(37, 80, 37), 2.0F, 10.0F);
      WallObject var28 = (WallObject)getObject(var27[0]);
      int[] var29 = WallObject.registerWallObjects("deepsandstone", "deepsandstonewall", 4, new Color(130, 105, 52), 2.0F, 10.0F);
      WallObject var30 = (WallObject)getObject(var29[0]);
      int[] var31 = WallObject.registerWallObjects("crypt", "cryptwall", "cryptwalloutlines", 4, new Color(41, 38, 44), ToolType.PICKAXE, 5.0F, 15.0F);
      WallObject var32 = (WallObject)getObject(var31[0]);
      int[] var33 = WallObject.registerWallObjects("spidercastle", "spidercastlewall", 4, new Color(86, 80, 111), ToolType.PICKAXE, 5.0F, 15.0F);
      WallObject var34 = (WallObject)getObject(var33[0]);
      int[] var35 = WallObject.registerWallObjects("dawn", "dawnwall", 4, new Color(216, 159, 117), ToolType.PICKAXE, 5.0F, 15.0F);
      WallObject var36 = (WallObject)getObject(var35[0]);
      int[] var37 = WallObject.registerWallObjects("dusk", "duskwall", 4, new Color(50, 53, 88), ToolType.PICKAXE, 5.0F, 15.0F);
      WallObject var38 = (WallObject)getObject(var37[0]);
      int var39 = registerObject("woodfence", new FenceObject("woodfence", new Color(86, 69, 40), 12, 10), 2.0F, true);
      FenceGateObject.registerGatePair(var39, "woodfencegate", "woodfencegate", new Color(86, 69, 40), 12, 10, 4.0F);
      int var40 = registerObject("stonefence", new FenceObject("stonefence", new Color(105, 105, 105), 16, 14), 2.0F, true);
      FenceGateObject.registerGatePair(var40, "stonefencegate", "stonefencegate", new Color(105, 105, 105), 16, 14, 4.0F);
      int var41 = registerObject("ironfence", new FenceObject("ironfence", new Color(165, 165, 165), 12, 10), 2.0F, true);
      FenceGateObject.registerGatePair(var41, "ironfencegate", "ironfencegate", new Color(165, 165, 165), 12, 10, 4.0F);
      int var42 = registerObject("cryptfence", new FenceObject("cryptfence", new Color(58, 57, 66), 12, 10), 2.0F, true);
      FenceGateObject.registerGatePair(var42, "cryptfencegate", "cryptfencegate", new Color(58, 57, 66), 12, 10, 4.0F);
      registerObject("woodcolumn", new ColumnObject("woodcolumn", var4.mapColor, ToolType.ALL), 10.0F, true);
      registerObject("stonecolumn", new ColumnObject("stonecolumn", var10.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("snowstonecolumn", new ColumnObject("snowstonecolumn", var16.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("sandstonecolumn", new ColumnObject("sandstonecolumn", var12.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("swampstonecolumn", new ColumnObject("swampstonecolumn", var14.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("deepstonecolumn", new ColumnObject("deepstonecolumn", var22.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("obsidiancolumn", new ColumnObject("obsidiancolumn", var24.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("deepsnowstonecolumn", new ColumnObject("deepsnowstonecolumn", var26.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("deepswampstonecolumn", new ColumnObject("deepswampstonecolumn", var28.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("deepsandstonecolumn", new ColumnObject("deepsandstonecolumn", var30.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("cryptcolumn", new ColumnObject("cryptcolumn", var32.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("spidercastlecolumn", new ColumnObject("spidercastlecolumn", var32.mapColor, ToolType.PICKAXE), 2.0F, true);
      registerObject("storagebox", new StorageBoxInventoryObject("storagebox", 40, var1), 10.0F, true);
      registerObject("barrel", new InventoryObject("barrel", 40, new Rectangle(4, 4, 24, 24), var1), 10.0F, true);
      registerObject("demonchest", new StorageBoxInventoryObject("demonchest", 40, new Color(83, 67, 119)), 20.0F, true);
      registerObject("coolingbox", new CoolingBoxInventoryObject("coolingbox", 40, new Color(159, 180, 196)), 10.0F, true);
      registerObject("incinerator", new IncineratorInventoryObject(), 50.0F, true);
      registerObject("torch", new TorchObject("torch", new Color(240, 200, 10), 50.0F, 0.2F), 0.1F, true);
      registerObject("walltorch", new WallTorchObject(), 2.0F, true);
      registerObject("ironlamp", new LampObject("ironlamp", new Rectangle(), new Color(175, 175, 175), 0.0F, 0.0F), 10.0F, true);
      int var43 = registerObject("goldlamp", new LampObject("goldlamp", new Rectangle(), new Color(249, 234, 12), 0.0F, 0.0F), 20.0F, true);
      getObject(var43).roomProperties.add("goldfurniture");
      FireChaliceObject.registerFireChalice("firechalice", "firechalice", new Color(73, 69, 75), true, true);
      FireChaliceObject.registerFireChalice("templefirechalice", "templefirechalice", new Color(122, 102, 60), false, false);
      registerObject("copperstreetlamp", new StreetlampObject(), 20.0F, true);
      registerObject("ironstreetlamp", new StreetlampObject(), 30.0F, true);
      registerObject("goldstreetlamp", new StreetlampObject(), 50.0F, true);
      registerObject("tungstenstreetlamp", new StreetlampObject(), 100.0F, true);
      DoubleStreetlampObject.registerDoubleStreetlamp("copperdoublestreetlamp", "copperdoublestreetlamp", ToolType.ALL, new Color(148, 93, 79), 28.0F);
      DoubleStreetlampObject.registerDoubleStreetlamp("irondoublestreetlamp", "irondoublestreetlamp", ToolType.ALL, new Color(166, 174, 186), 42.0F);
      DoubleStreetlampObject.registerDoubleStreetlamp("golddoublestreetlamp", "golddoublestreetlamp", ToolType.ALL, new Color(192, 166, 74), 70.0F);
      DoubleStreetlampObject.registerDoubleStreetlamp("tungstendoublestreetlamp", "tungstendoublestreetlamp", ToolType.ALL, new Color(15, 27, 31), 140.0F);
      registerObject("oillantern", new TorchObject("oillantern", new Color(240, 200, 10), 50.0F, 0.2F), 4.0F, true);
      registerObject("lantern", new TorchObject("lantern", new Color(240, 200, 10), 50.0F, 0.2F), 20.0F, true);
      registerObject("walllantern", new WallCandleObject(), 20.0F, true);
      registerObject("wallcandle", new WallCandleObject(), 5.0F, true);
      registerObject("stonecandlepedestal", new CandlePedestalObject(), 7.0F, true);
      registerObject("snowcandlepedestal", new CandlePedestalObject(), 7.0F, true);
      registerObject("swampcandlepedestal", new CandlePedestalObject(), 7.0F, true);
      registerObject("desertcandlepedestal", new CandlePedestalObject(), 7.0F, true);
      registerObject("tikitorch", new TikiTorchObject(), 2.0F, true);
      registerObject("sign", new SignObject(), 10.0F, true);
      registerObject("flowerpot", new FlowerPotObject(), 10.0F, true);
      registerObject("armorstand", new ArmorStandObject(), 10.0F, true);
      registerObject("trainingdummy", new TrainingDummyObject(), 60.0F, true);
      registerObject("snowmantrainingdummy", new SnowManTrainingDummyObject(), 60.0F, true);
      StoneCoffinObject.registerCoffinObject("stonecoffin", "stonecoffin", "stone", ToolType.PICKAXE, new Color(75, 75, 75));
      StoneCoffinObject.registerCoffinObject("cryptcoffin", "cryptcoffin", "cryptstone", ToolType.PICKAXE, new Color(46, 46, 59));
      registerObject("gravestone1", new GravestoneObject("gravestone1", ToolType.PICKAXE, new Color(75, 75, 75)), 0.0F, false);
      registerObject("gravestone2", new GravestoneObject("gravestone2", ToolType.PICKAXE, new Color(75, 75, 75)), 0.0F, false);
      registerObject("cryptgravestone1", new GravestoneObject("cryptgravestone1", ToolType.PICKAXE, new Color(46, 46, 59)), 0.0F, false);
      registerObject("cryptgravestone2", new GravestoneObject("cryptgravestone2", ToolType.PICKAXE, new Color(46, 46, 59)), 0.0F, false);
      registerObject("minecarttrack", new MinecartTrackObject(), 0.5F, true);
      registerObject("traptrack", new TrapTrackObject(), 0.5F, false);
      registerObject("spideregg", new SpiderEggObject(), 0.0F, false);
      Color var44 = new Color(153, 127, 98);
      registerObject("oakchest", new StorageBoxInventoryObject("oakchest", 40, var44), 10.0F, true);
      DinnerTableObject.registerDinnerTable("oakdinnertable", "oakdinnertable", var44, 20.0F);
      registerObject("oakdesk", new DeskObject("oakdesk", var44), 10.0F, true);
      registerObject("oakmodulartable", new ModularTableObject("oakmodulartable", var44), 10.0F, true);
      registerObject("oakchair", new ChairObject("oakchair", var44), 5.0F, true);
      BenchObject.registerBench("oakbench", "oakbench", var44, 10.0F);
      registerObject("oakbookshelf", new BookshelfObject("oakbookshelf", var44), 10.0F, true);
      registerObject("oakcabinet", new CabinetObject("oakcabinet", var44), 10.0F, true);
      BedObject.registerBed("oakbed", "oakbed", var44, 100.0F);
      registerObject("oakdresser", new DresserObject("oakdresser", var44), 10.0F, true);
      registerObject("oakclock", new ClockObject("oakclock", var44), 10.0F, true);
      registerObject("oakcandelabra", new CandelabraObject("oakcandelabra", var44, 50.0F, 0.1F), 10.0F, true);
      registerObject("oakdisplay", new DisplayStandObject("oakdisplay", var44, 20), 10.0F, true);
      BathtubObject.registerBathtub("oakbathtub", "oakbathtub", var44, 10.0F);
      registerObject("oaktoilet", new ToiletObject("oaktoilet", var44), 5.0F, true);
      var44 = new Color(132, 104, 62);
      registerObject("sprucechest", new StorageBoxInventoryObject("sprucechest", 40, var44), 10.0F, true);
      DinnerTableObject.registerDinnerTable("sprucedinnertable", "sprucedinnertable", var44, 20.0F);
      registerObject("sprucedesk", new DeskObject("sprucedesk", var44), 10.0F, true);
      registerObject("sprucemodulartable", new ModularTableObject("sprucemodulartable", var44), 10.0F, true);
      registerObject("sprucechair", new ChairObject("sprucechair", var44), 5.0F, true);
      BenchObject.registerBench("sprucebench", "sprucebench", var44, 10.0F);
      registerObject("sprucebookshelf", new BookshelfObject("sprucebookshelf", var44), 10.0F, true);
      registerObject("sprucecabinet", new CabinetObject("sprucecabinet", var44), 10.0F, true);
      BedObject.registerBed("sprucebed", "sprucebed", var44, 100.0F);
      registerObject("sprucedresser", new DresserObject("sprucedresser", var44), 10.0F, true);
      registerObject("spruceclock", new ClockObject("spruceclock", var44), 10.0F, true);
      registerObject("sprucecandelabra", new CandelabraObject("sprucecandelabra", var44, 50.0F, 0.1F), 10.0F, true);
      registerObject("sprucedisplay", new DisplayStandObject("sprucedisplay", var44, 20), 10.0F, true);
      BathtubObject.registerBathtub("sprucebathtub", "sprucebathtub", var44, 10.0F);
      registerObject("sprucetoilet", new ToiletObject("sprucetoilet", var44), 5.0F, true);
      var44 = new Color(117, 79, 41);
      registerObject("pinechest", new StorageBoxInventoryObject("pinechest", 40, var44), 10.0F, true);
      DinnerTableObject.registerDinnerTable("pinedinnertable", "pinedinnertable", var44, 20.0F);
      registerObject("pinedesk", new DeskObject("pinedesk", var44), 10.0F, true);
      registerObject("pinemodulartable", new ModularTableObject("pinemodulartable", var44), 10.0F, true);
      registerObject("pinechair", new ChairObject("pinechair", var44), 5.0F, true);
      BenchObject.registerBench("pinebench", "pinebench", var44, 10.0F);
      registerObject("pinebookshelf", new BookshelfObject("pinebookshelf", var44), 10.0F, true);
      registerObject("pinecabinet", new CabinetObject("pinecabinet", var44), 10.0F, true);
      BedObject.registerBed("pinebed", "pinebed", var44, 100.0F);
      registerObject("pinedresser", new DresserObject("pinedresser", var44), 10.0F, true);
      registerObject("pineclock", new ClockObject("pineclock", var44), 10.0F, true);
      registerObject("pinecandelabra", new CandelabraObject("pinecandelabra", var44, 50.0F, 0.1F), 10.0F, true);
      registerObject("pinedisplay", new DisplayStandObject("pinedisplay", var44, 20), 10.0F, true);
      BathtubObject.registerBathtub("pinebathtub", "pinebathtub", var44, 10.0F);
      registerObject("pinetoilet", new ToiletObject("pinetoilet", var44), 5.0F, true);
      var44 = new Color(121, 90, 37);
      registerObject("palmchest", new StorageBoxInventoryObject("palmchest", 40, var44), 10.0F, true);
      DinnerTableObject.registerDinnerTable("palmdinnertable", "palmdinnertable", var44, 20.0F);
      registerObject("palmdesk", new DeskObject("palmdesk", var44), 10.0F, true);
      registerObject("palmmodulartable", new ModularTableObject("palmmodulartable", var44), 10.0F, true);
      registerObject("palmchair", new ChairObject("palmchair", var44), 5.0F, true);
      BenchObject.registerBench("palmbench", "palmbench", var44, 10.0F);
      registerObject("palmbookshelf", new BookshelfObject("palmbookshelf", var44), 10.0F, true);
      registerObject("palmcabinet", new CabinetObject("palmcabinet", var44), 10.0F, true);
      BedObject.registerBed("palmbed", "palmbed", var44, 100.0F);
      registerObject("palmdresser", new DresserObject("palmdresser", var44), 10.0F, true);
      registerObject("palmclock", new ClockObject("palmclock", var44), 10.0F, true);
      registerObject("palmcandelabra", new CandelabraObject("palmcandelabra", var44, 50.0F, 0.1F), 10.0F, true);
      registerObject("palmdisplay", new DisplayStandObject("palmdisplay", var44, 20), 10.0F, true);
      BathtubObject.registerBathtub("palmbathtub", "palmbathtub", var44, 10.0F);
      registerObject("palmtoilet", new ToiletObject("palmtoilet", var44), 5.0F, true);
      var44 = new Color(78, 19, 19);
      registerObject("dungeonchest", new StorageBoxInventoryObject("dungeonchest", 40, var44), 10.0F, true);
      DinnerTableObject.registerDinnerTable("dungeondinnertable", "dungeondinnertable", var44, 20.0F);
      registerObject("dungeondesk", new DeskObject("dungeondesk", var44), 10.0F, true);
      registerObject("dungeonmodulartable", new ModularTableObject("dungeonmodulartable", var44), 10.0F, true);
      registerObject("dungeonchair", new ChairObject("dungeonchair", var44), 5.0F, true);
      BenchObject.registerBench("dungeonbench", "dungeonbench", var44, 10.0F);
      registerObject("dungeonbookshelf", new BookshelfObject("dungeonbookshelf", var44), 10.0F, true);
      registerObject("dungeoncabinet", new CabinetObject("dungeoncabinet", var44), 10.0F, true);
      BedObject.registerBed("dungeonbed", "dungeonbed", var44, 100.0F);
      registerObject("dungeondresser", new DresserObject("dungeondresser", var44), 10.0F, true);
      registerObject("dungeonclock", new ClockObject("dungeonclock", var44), 10.0F, true);
      CandelabraObject var45 = new CandelabraObject("dungeoncandelabra", var44, 270.0F, 0.4F);
      var45.flameHue = var45.lightHue;
      var45.smokeHue = var45.lightHue;
      registerObject("dungeoncandelabra", var45, 10.0F, true);
      registerObject("dungeondisplay", new DisplayStandObject("dungeondisplay", var44, 20), 10.0F, true);
      BathtubObject.registerBathtub("dungeonbathtub", "dungeonbathtub", var44, 10.0F);
      registerObject("dungeontoilet", new ToiletObject("dungeontoilet", var44), 5.0F, true);
      var44 = new Color(59, 54, 39);
      registerObject("deadwoodchest", new StorageBoxInventoryObject("deadwoodchest", 40, var44), 10.0F, true);
      DinnerTableObject.registerDinnerTable("deadwooddinnertable", "deadwooddinnertable", var44, 20.0F);
      registerObject("deadwooddesk", new DeskObject("deadwooddesk", var44), 10.0F, true);
      registerObject("deadwoodmodulartable", new ModularTableObject("deadwoodmodulartable", var44), 10.0F, true);
      registerObject("deadwoodchair", new ChairObject("deadwoodchair", var44), 5.0F, true);
      BenchObject.registerBench("deadwoodbench", "deadwoodbench", var44, 10.0F);
      registerObject("deadwoodbookshelf", new BookshelfObject("deadwoodbookshelf", var44), 10.0F, true);
      registerObject("deadwoodcabinet", new CabinetObject("deadwoodcabinet", var44), 10.0F, true);
      BedObject.registerBed("deadwoodbed", "deadwoodbed", var44, 100.0F);
      registerObject("deadwooddresser", new DresserObject("deadwooddresser", var44), 10.0F, true);
      registerObject("deadwoodclock", new ClockObject("deadwoodclock", var44), 10.0F, true);
      var45 = new CandelabraObject("deadwoodcandelabra", var44, 20.0F, 0.4F);
      var45.flameHue = var45.lightHue;
      var45.smokeHue = var45.lightHue;
      registerObject("deadwoodcandelabra", var45, 10.0F, true);
      CandlesObject var46 = new CandlesObject("deadwoodcandles", var44, 20.0F, 0.4F);
      var46.flameHue = var46.lightHue;
      var46.smokeHue = var46.lightHue;
      registerObject("deadwoodcandles", var46, 10.0F, true);
      registerObject("deadwooddisplay", new DisplayStandObject("deadwooddisplay", var44, 20), 10.0F, true);
      BathtubObject.registerBathtub("deadwoodbathtub", "deadwoodbathtub", var44, 10.0F);
      registerObject("deadwoodtoilet", new ToiletObject("deadwoodtoilet", var44), 5.0F, true);
      var44 = new Color(74, 53, 60);
      var45 = new CandelabraObject("spidercastlecandelabra", var44, 75.0F, 0.4F);
      var45.flameHue = var45.lightHue;
      var45.smokeHue = var45.lightHue;
      registerObject("spidercastlecandelabra", var45, 10.0F, true);
      registerObject("spidercastlewallcandle", new SpiderCastleWallCandleObject(), 2.0F, true);
      registerObject("spideritearmorstand", new SpideriteArmorStandObject(var44), 0.0F, false);
      registerObject("spidercastlemodulartable", new ModularTableObject("spidercastlemodulartable", var44), 10.0F, true);
      registerObject("spidercastlechair", new ChairObject("spidercastlechair", var44), 5.0F, true);
      BenchObject.registerBench("spidercastlebench", "spidercastlebench", var44, 10.0F);
      registerObject("spidercastlebookshelf", new BookshelfObject("spidercastlebookshelf", var44), 10.0F, true);
      int[] var64 = DinnerTableObject.registerDinnerTable("golddinnertable", "golddinnertable", var2, 50.0F);
      ((FurnitureObject)getObject(var64[0])).roomProperties.add("goldfurniture");
      ((FurnitureObject)getObject(var64[1])).roomProperties.add("goldfurniture");
      ((FurnitureObject)getObject(registerObject("goldchair", new ChairObject("goldchair", var2), 25.0F, true))).roomProperties.add("goldfurniture");
      registerObject("woolcarpet", new ModularCarpetObject("woolcarpet", new Color(230, 230, 230)), 25.0F, true);
      registerObject("leathercarpet", new ModularCarpetObject("leathercarpet", new Color(96, 65, 53)), 25.0F, true);
      registerObject("groundslime", new GroundSlimeObject(), 5.0F, false);
      registerObject("woodpressureplate", new MaskedPressurePlateObject("pressureplatemask", "woodfloor", new Color(170, 132, 80)), 5.0F, true);
      registerObject("stonepressureplate", new MaskedPressurePlateObject("pressureplatemask", "stonefloor", new Color(130, 130, 130)), 1.0F, true);
      registerObject("snowstonepressureplate", new MaskedPressurePlateObject("pressureplatemasklight", "snowstonefloor", new Color(176, 176, 176)), 1.0F, true);
      registerObject("swampstonepressureplate", new MaskedPressurePlateObject("pressureplatemask", "swampstonefloor", new Color(81, 88, 81)), 1.0F, true);
      registerObject("sandstonepressureplate", new MaskedPressurePlateObject("pressureplatemasklight", "sandstonefloor", new Color(209, 194, 148)), 1.0F, true);
      registerObject("dungeonpressureplate", new MaskedPressurePlateObject("pressureplatemaskdark", "dungeonfloor", new Color(45, 47, 54)), 10.0F, true);
      registerObject("deepstonepressureplate", new MaskedPressurePlateObject("pressureplatemaskdark", "deepstonefloor", new Color(60, 63, 64)), 15.0F, true);
      registerObject("deepsnowstonepressureplate", new MaskedPressurePlateObject("pressureplatemask", "deepsnowstonefloor", new Color(57, 75, 85)), 15.0F, true);
      registerObject("deepswampstonepressureplate", new MaskedPressurePlateObject("pressureplatemask", "deepswampstonefloor", new Color(50, 78, 51)), 15.0F, true);
      LeverObject.registerLeverPair("rocklever", "rocklever", 1.0F);
      registerObject("ledpanel", new LEDPanelObject(), 10.0F, true);
      registerObject("woodarrowtrap", new WallArrowTrapObject(var4), 50.0F, true);
      registerObject("woodsawtrap", new WallSawTrapObject(var4), 50.0F, false);
      registerObject("stoneflametrap", new WallFlameTrapObject(var10), 50.0F, true);
      registerObject("stonearrowtrap", new WallArrowTrapObject(var10), 50.0F, true);
      registerObject("stonesawtrap", new WallSawTrapObject(var10), 50.0F, false);
      registerObject("sandstoneflametrap", new WallFlameTrapObject(var12), 50.0F, true);
      registerObject("sandstonearrowtrap", new WallArrowTrapObject(var12), 50.0F, true);
      registerObject("sandstonesawtrap", new WallSawTrapObject(var12), 50.0F, false);
      registerObject("swampstoneflametrap", new WallFlameTrapObject(var14), 50.0F, true);
      registerObject("swampstonearrowtrap", new WallArrowTrapObject(var14), 50.0F, true);
      registerObject("swampstonesawtrap", new WallSawTrapObject(var14), 50.0F, false);
      registerObject("snowstonearrowtrap", new WallArrowTrapObject(var16), 50.0F, true);
      registerObject("snowstonesawtrap", new WallSawTrapObject(var16), 50.0F, false);
      registerObject("icearrowtrap", new WallArrowTrapObject(var18), 50.0F, true);
      registerObject("icesawtrap", new WallSawTrapObject(var18), 50.0F, false);
      registerObject("dungeonflametrap", new WallFlameTrapObject(var20), 50.0F, true);
      registerObject("dungeonarrowtrap", new WallArrowTrapObject(var20), 50.0F, true);
      registerObject("dungeonvoidtrap", new WallVoidTrapObject(var20), 50.0F, true);
      registerObject("dungeonsawtrap", new WallSawTrapObject(var20), 50.0F, false);
      registerObject("deepstoneflametrap", new WallFlameTrapObject(var22), 50.0F, true);
      registerObject("deepstonearrowtrap", new WallArrowTrapObject(var22), 50.0F, true);
      registerObject("deepstonesawtrap", new WallSawTrapObject(var22), 50.0F, false);
      registerObject("obsidianflametrap", new WallFlameTrapObject(var24), 50.0F, true);
      registerObject("obsidianarrowtrap", new WallArrowTrapObject(var24), 50.0F, true);
      registerObject("obsidiansawtrap", new WallSawTrapObject(var24), 50.0F, false);
      registerObject("deepsnowstoneflametrap", new WallFlameTrapObject(var26), 50.0F, true);
      registerObject("deepsnowstonearrowtrap", new WallArrowTrapObject(var26), 50.0F, true);
      registerObject("deepsnowstonesawtrap", new WallSawTrapObject(var26), 50.0F, false);
      registerObject("deepswampstoneflametrap", new WallFlameTrapObject(var28), 50.0F, true);
      registerObject("deepswampstonearrowtrap", new WallArrowTrapObject(var28), 50.0F, true);
      registerObject("deepswampstonesawtrap", new WallSawTrapObject(var28), 50.0F, false);
      registerObject("deepsandstoneflametrap", new WallFlameTrapObject(var30), 50.0F, true);
      registerObject("deepsandstonearrowtrap", new WallArrowTrapObject(var30), 50.0F, true);
      registerObject("deepsandstonesawtrap", new WallSawTrapObject(var30), 50.0F, false);
      registerObject("spidercastlearrowtrap", new WallArrowTrapObject(var34), 50.0F, true);
      registerObject("tnt", new TNTObject(), 150.0F, true);
      registerObject("fireworkdispenser", new FireworkDispenserObject(), 30.0F, true);
      registerObject("spiketrap", new SpikeTrapObject(), 50.0F, false);
      SeedObject.registerSeedObjects("wheatseed", "wheat", "wheat", 4, 0, 5, 1200.0F, 2100.0F, new Color(249, 166, 21), 1.0F);
      SeedObject.registerSeedObjects("cornseed", "corn", "corn", 3, 0, 5, 2100.0F, 3000.0F, new Color(248, 188, 6), 1.0F);
      SeedObject.registerSeedObjects("tomatoseed", "tomato", "tomato", 3, 0, 5, 2100.0F, 3000.0F, new Color(205, 15, 15), 1.0F);
      SeedObject.registerSeedObjects("cabbageseed", "cabbage", "cabbage", 2, 0, 5, 2100.0F, 3000.0F, new Color(56, 161, 50), 1.0F);
      SeedObject.registerSeedObjects("chilipepperseed", "chilipepper", "chilipepper", 3, 0, 5, 2100.0F, 3000.0F, new Color(232, 17, 17), 1.0F);
      SeedObject.registerSeedObjects("sugarbeetseed", "sugarbeet", "sugarbeet", 2, 0, 5, 2100.0F, 3000.0F, new Color(202, 181, 146), 1.0F);
      SeedObject.registerSeedObjects("eggplantseed", "eggplant", "eggplant", 2, 0, 5, 2100.0F, 3000.0F, new Color(101, 28, 126), 1.0F);
      SeedObject.registerSeedObjects("potatoseed", "potato", "potato", 3, 0, 5, 2100.0F, 3000.0F, new Color(90, 61, 48), 1.0F);
      int[] var63 = SeedObject.registerSeedObjects("riceseed", "rice", "riceseed", 3, 0, 5, 2100.0F, 3000.0F, new Color(239, 233, 202), Item.Rarity.NORMAL, 960, 1.0F);
      Arrays.stream(var63).mapToObj((var0) -> {
         return getObject(var0).getObjectItem();
      }).forEach((var0) -> {
         var0.setItemCategory(new String[]{"consumable", "rawfood"});
      });
      SeedObject.registerSeedObjects("carrotseed", "carrot", "carrot", 2, 0, 5, 2100.0F, 3000.0F, new Color(209, 102, 26), 1.0F);
      SeedObject.registerSeedObjects("onionseed", "onion", "onion", 2, 0, 5, 2100.0F, 3000.0F, new Color(161, 18, 90), 1.0F);
      SeedObject.registerSeedObjects("pumpkinseed", "pumpkin", "pumpkin", 2, 0, 5, 2100.0F, 3000.0F, new Color(228, 137, 25), 1.0F);
      SeedObject.registerSeedObjects("strawberryseed", "strawberry", "strawberry", 4, 0, 5, 2100.0F, 3000.0F, new Color(197, 15, 30), 1.0F);
      int[] var65 = SeedObject.registerSeedObjects("coffeebeans", "coffee", "coffeebeans", 5, 0, 5, 2100.0F, 3000.0F, new Color(85, 28, 16), Item.Rarity.NORMAL, 960, 3.0F);
      Arrays.stream(var65).mapToObj((var0) -> {
         return getObject(var0).getObjectItem();
      }).forEach((var0) -> {
         var0.setItemCategory(new String[]{"consumable", "rawfood"});
      });
      SeedObject.registerSeedObjects("sunflowerseed", "sunflower", "sunflower", 1, 0, 5, 1500.0F, 2100.0F, new Color(245, 235, 55), 1.0F);
      registerObject("wildsunflower", new CustomWildFlowerObject("sunflower", 5, "sunflowerseed", "sunflower", 1, new Color(245, 235, 55)), 0.0F, false);
      registerObject("sunflower", new FlowerObject("sunflower", 6, 100, 960, new Color(245, 235, 55)), 3.0F, true);
      SeedObject.registerSeedObjects("firemoneseed", "firemone", "firemone", 3, 0, 5, 1500.0F, 2100.0F, new Color(180, 35, 0), 1.0F);
      registerObject("wildfiremone", new CustomWildFlowerObject("firemone", 5, "firemoneseed", "firemone", 3, new Color(180, 35, 0)), 0.0F, false);
      registerObject("firemone", new FlowerObject("firemone", 6, 100, 960, new Color(180, 35, 0)), 3.0F, true);
      SeedObject.registerSeedObjects("iceblossomseed", "iceblossom", "iceblossom", 3, 0, 5, 1500.0F, 2100.0F, new Color(150, 205, 245), 1.0F);
      registerObject("wildiceblossom", new CustomWildFlowerObject("iceblossom", 5, "iceblossomseed", "iceblossom", 3, new Color(150, 205, 245), new String[]{"snowtile"}), 0.0F, false);
      registerObject("iceblossom", (new FlowerObject("iceblossom", 6, 100, 960, new Color(150, 205, 245))).addGlobalIngredient(new String[]{"anycoolingfuel"}), 3.0F, true);
      int var47 = registerObject("mushroomflower", new FlowerObject("mushroom", 6, 100, 960, "mushroom", new Color(115, 85, 70)), 0.0F, false);
      SeedObject.registerSeedObjects("mushroom", "mushroom", "mushroom", 4, 0, 5, 1500.0F, 2100.0F, true, var47, new Color(115, 85, 70), Item.Rarity.NORMAL, 600, 1.0F);
      registerObject("wildmushroom", new CustomWildFlowerObject("mushroom", 5, "mushroom", "mushroom", 1, new Color(115, 85, 70), new String[]{"swampgrasstile", "mudtile"}), 0.0F, false);
      int[] var48 = SeedObject.registerSeedObjects("caveglowsprout", "caveglow", "caveglow", 2, 0, 5, 1500.0F, 2100.0F, false, 0, new Color(29, 156, 91), Item.Rarity.UNCOMMON, 0, 1.0F);
      getObject(var48[var48.length - 1]).lightLevel = 50;
      CustomWildFlowerObject var49 = new CustomWildFlowerObject("wildcaveglow", 0, "caveglowsprout", "caveglow", 4, new Color(6, 83, 10), new String[]{"deeprocktile"});
      var49.lightLevel = 50;
      registerObject("wildcaveglow", var49, 0.0F, false);
      FlowerObject var50 = new FlowerObject("caveglow", 6, 100, 960, new Color(29, 156, 91));
      var50.rarity = Item.Rarity.UNCOMMON;
      var50.lightLevel = 50;
      registerObject("caveglow", var50, 2.0F, true);
      registerObject("campfire", new CampfireObject(), 10.0F, true);
      registerObject("cookingpot", new CookingPotObject(), 10.0F, true);
      registerObject("roastingstation", new RoastingStationObject(), 10.0F, true);
      CookingStationObject.registerCookingStation();
      registerObject("compostbin", new CompostBinObject(), 30.0F, true);
      GrainMillObject.registerGrainMill();
      registerObject("cheesepress", new CheesePressObject(), 50.0F, true);
      registerObject("beehive", new BeeHiveObject(), 10.0F, false);
      registerObject("apiary", new ApiaryObject(), 80.0F, true);
      FeedingTroughObject.registerFeedingTrough();
      RockObject var51;
      registerObject("rock", var51 = new RockObject("rock", new Color(105, 105, 105), "stone"), 0.0F, false);
      SingleRockObject.registerSurfaceRock(var51, "surfacerock", new Color(127, 127, 127));
      registerObject("surfacerocksmall", new SingleRockSmall(var51, "surfacerocksmall", new Color(127, 127, 127)), 0.0F, false);
      SingleRockObject.registerSurfaceRock(var51, "caverock", new Color(127, 127, 127));
      registerObject("caverocksmall", new SingleRockSmall(var51, "caverocksmall", new Color(127, 127, 127)), 0.0F, false);
      registerObject("ironorerock", new RockOreObject(var51, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperorerock", new RockOreObject(var51, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldorerock", new RockOreObject(var51, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      RockObject var52;
      registerObject("snowrock", var52 = new RockObject("snowrock", new Color(200, 200, 200), "snowstone"), 0.0F, false);
      SingleRockObject.registerSurfaceRock(var52, "snowsurfacerock", new Color(182, 182, 182));
      registerObject("snowsurfacerocksmall", new SingleRockSmall(var52, "snowsurfacerocksmall", new Color(182, 182, 182)), 0.0F, false);
      SingleRockObject.registerSurfaceRock(var52, "snowcaverock", new Color(213, 213, 213));
      registerObject("snowcaverocksmall", new SingleRockSmall(var52, "snowcaverocksmall", new Color(213, 213, 213)), 0.0F, false);
      registerObject("ironoresnow", new RockOreObject(var52, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoresnow", new RockOreObject(var52, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoresnow", new RockOreObject(var52, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("frostshardsnow", new RockOreObject(var52, "oremask", "frostshardore", new Color(121, 201, 224), "frostshard", 1, 1), 0.0F, false);
      RockObject var53;
      registerObject("sandstonerock", var53 = new RockObject("sandstonerock", new Color(215, 215, 125), "sandstone"), 0.0F, false);
      var53.toolTier = 1;
      SingleRockObject.registerSurfaceRock(var53, "sandsurfacerock", new Color(212, 199, 161));
      registerObject("sandsurfacerocksmall", new SingleRockSmall(var53, "sandsurfacerocksmall", new Color(212, 199, 161)), 0.0F, false);
      SingleRockObject.registerSurfaceRock(var53, "sandcaverock", new Color(208, 208, 140));
      registerObject("sandcaverocksmall", new SingleRockSmall(var53, "sandcaverocksmall", new Color(208, 208, 140)), 0.0F, false);
      registerObject("ironoresandstone", new RockOreObject(var53, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoresandstone", new RockOreObject(var53, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoresandstone", new RockOreObject(var53, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("quartzsandstone", new RockOreObject(var53, "oremask", "quartzore", new Color(163, 181, 127), "quartz", 1, 1), 0.0F, false);
      RockObject var54;
      registerObject("swamprock", var54 = new RockObject("swamprock", new Color(50, 62, 48), "swampstone"), 0.0F, false);
      var54.toolTier = 1;
      SingleRockObject.registerSurfaceRock(var54, "swampsurfacerock", new Color(69, 86, 68));
      registerObject("swampsurfacerocksmall", new SingleRockSmall(var54, "swampsurfacerocksmall", new Color(69, 86, 68)), 0.0F, false);
      SingleRockObject.registerSurfaceRock(var54, "swampcaverock", new Color(60, 75, 58));
      registerObject("swampcaverocksmall", new SingleRockSmall(var54, "swampcaverocksmall", new Color(60, 75, 58)), 0.0F, false);
      registerObject("ironoreswamp", new RockOreObject(var54, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoreswamp", new RockOreObject(var54, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoreswamp", new RockOreObject(var54, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("ivyoreswamp", new RockOreObject(var54, "oremask", "ivyore", new Color(91, 130, 36), "ivyore"), 0.0F, false);
      RockObject var55 = new RockObject("clayrock", new Color(150, 90, 65), "clay");
      var55.displayMapTooltip = true;
      registerObject("clayrock", var55, 0.0F, false);
      RockObject var56;
      registerObject("deeprock", var56 = new RockObject("deeprock", new Color(38, 42, 44), "deepstone"), 0.0F, false);
      var56.toolTier = 1;
      SingleRockObject.registerSurfaceRock(var56, "deepcaverock", new Color(42, 46, 47));
      registerObject("deepcaverocksmall", new SingleRockSmall(var56, "deepcaverocksmall", new Color(42, 46, 47)), 0.0F, false);
      registerObject("ironoredeeprock", new RockOreObject(var56, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoredeeprock", new RockOreObject(var56, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoredeeprock", new RockOreObject(var56, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("tungstenoredeeprock", new RockOreObject(var56, "oremask", "tungstenore", new Color(40, 49, 57), "tungstenore"), 0.0F, false);
      registerObject("lifequartzdeeprock", new RockOreObject(var56, "oremask", "lifequartzore", new Color(180, 50, 61), "lifequartz", 1, 1), 0.0F, false);
      RockObject var60;
      registerObject("obsidianrock", var60 = new RockObject("obsidianrock", new Color(37, 27, 40), "obsidian", 2, 3), 0.0F, false);
      var60.displayMapTooltip = true;
      var60.toolTier = 2;
      registerObject("upgradesharddeeprock", new RockOreObject(var56, "oremask", "upgradeshardore", new Color(0, 27, 107), "upgradeshard", 1, 1, false), 0.0F, false);
      registerObject("alchemysharddeeprock", new RockOreObject(var56, "oremask", "alchemyshardore", new Color(102, 0, 61), "alchemyshard", 1, 1, false), 0.0F, false);
      RockObject var57;
      registerObject("deepsnowrock", var57 = new RockObject("deepsnowrock", new Color(49, 56, 60), "deepsnowstone"), 0.0F, false);
      var57.toolTier = 2;
      SingleRockObject.registerSurfaceRock(var57, "deepsnowcaverock", new Color(50, 54, 59));
      registerObject("deepsnowcaverocksmall", new SingleRockSmall(var57, "deepsnowcaverocksmall", new Color(50, 54, 59)), 0.0F, false);
      registerObject("ironoredeepsnowrock", new RockOreObject(var57, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoredeepsnowrock", new RockOreObject(var57, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoredeepsnowrock", new RockOreObject(var57, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("tungstenoredeepsnowrock", new RockOreObject(var57, "oremask", "tungstenore", new Color(40, 49, 57), "tungstenore"), 0.0F, false);
      registerObject("lifequartzdeepsnowrock", new RockOreObject(var57, "oremask", "lifequartzore", new Color(180, 50, 61), "lifequartz", 1, 1), 0.0F, false);
      registerObject("glacialoredeepsnowrock", new RockOreObject(var57, "oremask", "glacialore", new Color(88, 105, 218), "glacialore"), 0.0F, false);
      registerObject("upgradesharddeepsnowrock", new RockOreObject(var57, "oremask", "upgradeshardore", new Color(0, 27, 107), "upgradeshard", 1, 1, false), 0.0F, false);
      registerObject("alchemysharddeepsnowrock", new RockOreObject(var57, "oremask", "alchemyshardore", new Color(102, 0, 61), "alchemyshard", 1, 1, false), 0.0F, false);
      RockObject var58;
      registerObject("deepswamprock", var58 = new RockObject("deepswamprock", new Color(34, 50, 37), "deepswampstone"), 0.0F, false);
      var58.toolTier = 3;
      SingleRockObject.registerSurfaceRock(var58, "deepswampcaverock", new Color(40, 56, 42));
      registerObject("deepswampcaverocksmall", new SingleRockSmall(var58, "deepswampcaverocksmall", new Color(40, 56, 42)), 0.0F, false);
      registerObject("ironoredeepswamprock", new RockOreObject(var58, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoredeepswamprock", new RockOreObject(var58, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoredeepswamprock", new RockOreObject(var58, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("tungstenoredeepswamprock", new RockOreObject(var58, "oremask", "tungstenore", new Color(40, 49, 57), "tungstenore"), 0.0F, false);
      registerObject("lifequartzdeepswamprock", new RockOreObject(var58, "oremask", "lifequartzore", new Color(180, 50, 61), "lifequartz", 1, 1), 0.0F, false);
      registerObject("myceliumoredeepswamprock", new RockOreObject(var58, "oremask", "myceliumore", new Color(170, 87, 24), "myceliumore"), 0.0F, false);
      registerObject("upgradesharddeepswamprock", new RockOreObject(var58, "oremask", "upgradeshardore", new Color(0, 27, 107), "upgradeshard", 1, 1, false), 0.0F, false);
      registerObject("alchemysharddeepswamprock", new RockOreObject(var58, "oremask", "alchemyshardore", new Color(102, 0, 61), "alchemyshard", 1, 1, false), 0.0F, false);
      RockObject var59;
      registerObject("deepsandstonerock", var59 = new RockObject("deepsandstonerock", new Color(144, 117, 58), "deepsandstone"), 0.0F, false);
      var59.toolTier = 4;
      SingleRockObject.registerSurfaceRock(var59, "deepsandcaverock", new Color(152, 125, 65));
      registerObject("deepsandcaverocksmall", new SingleRockSmall(var59, "deepsandcaverocksmall", new Color(152, 125, 65)), 0.0F, false);
      registerObject("ironoredeepsandstonerock", new RockOreObject(var59, "oremask", "ironore", new Color(150, 115, 65), "ironore"), 0.0F, false);
      registerObject("copperoredeepsandstonerock", new RockOreObject(var59, "oremask", "copperore", new Color(110, 52, 29), "copperore"), 0.0F, false);
      registerObject("goldoredeepsandstonerock", new RockOreObject(var59, "oremask", "goldore", new Color(255, 195, 50), "goldore"), 0.0F, false);
      registerObject("ancientfossiloredeepsnowrock", new RockOreObject(var59, "oremask", "ancientfossilore", new Color(63, 56, 34), "ancientfossilore"), 0.0F, false);
      registerObject("lifequartzdeepsandstonerock", new RockOreObject(var59, "oremask", "lifequartzore", new Color(180, 50, 61), "lifequartz", 1, 1), 0.0F, false);
      registerObject("upgradesharddeepsandstonerock", new RockOreObject(var59, "oremask", "upgradeshardore", new Color(0, 27, 107), "upgradeshard", 1, 1, false), 0.0F, false);
      registerObject("alchemysharddeepsandstonerock", new RockOreObject(var59, "oremask", "alchemyshardore", new Color(102, 0, 61), "alchemyshard", 1, 1, false), 0.0F, false);
      RockObject var61;
      registerObject("slimerock", var61 = new RockObject("slimerock", new Color(50, 87, 28), (String)null), 0.0F, false);
      var61.toolTier = 4;
      SingleRockObject.registerSurfaceRock(var61, "slimecaverock", new Color(50, 87, 28));
      registerObject("slimecaverocksmall", new SingleRockSmall(var61, "slimecaverocksmall", new Color(50, 87, 28)), 0.0F, false);
      registerObject("slimeumslimerock", new RockOreObject(var61, "oremask", "slimeum", new Color(231, 220, 6), "slimeum", 1, 2), 0.0F, false);
      registerObject("upgradeshardslimerock", new RockOreObject(var61, "oremask", "upgradeshardore", new Color(0, 27, 107), "upgradeshard", 1, 1, false), 0.0F, false);
      registerObject("alchemyshardslimerock", new RockOreObject(var61, "oremask", "alchemyshardore", new Color(102, 0, 61), "alchemyshard", 1, 1, false), 0.0F, false);
      registerObject("cryptnightsteelorerocksmall", new SingleOreRockSmall("cryptstone", 4, "cryptorerock", "cryptorerock_nightsteelore", new Color(63, 7, 156), "nightsteelore", 3, 7), 0.0F, false);
      registerObject("cryptupgradeshardorerocksmall", new SingleOreRockSmall("cryptstone", 4, "cryptorerock", "cryptorerock_upgradeshard", new Color(0, 27, 107), "upgradeshard", 3, 4, false), 0.0F, false);
      registerObject("cryptalchemyshardorerocksmall", new SingleOreRockSmall("cryptstone", 4, "cryptorerock", "cryptorerock_alchemyshard", new Color(102, 0, 61), "alchemyshard", 3, 4, false), 0.0F, false);
      RockObject var62;
      registerObject("spiderrock", var62 = new RockObject("spiderrock", new Color(0, 77, 94), "spiderstone"), 0.0F, false);
      var62.toolTier = 4;
      SingleRockObject.registerSurfaceRock(var62, "spidercaverock", new Color(0, 77, 94));
      registerObject("spidercaverocksmall", new SingleRockSmall(var62, "spidercaverocksmall", new Color(0, 77, 94)), 0.0F, false);
      registerObject("spideritespiderrock", new RockOreObject(var62, "oremask", "spideriteore", new Color(166, 204, 52), "spideriteore", 1, 2), 0.0F, false);
      registerObject("upgradeshardspiderrock", new RockOreObject(var62, "oremask", "upgradeshardore", new Color(0, 27, 107), "upgradeshard", 1, 1, false), 0.0F, false);
      registerObject("alchemyshardspiderrock", new RockOreObject(var62, "oremask", "alchemyshardore", new Color(102, 0, 61), "alchemyshard", 1, 1, false), 0.0F, false);
      registerObject("grass", new SurfaceGrassObject(), 0.0F, false);
      registerObject("swampgrass", new SwampGrassObject(), 0.0F, false);
      registerObject("cryptgrass", new CryptGrassObject(), 0.0F, false);
      registerObject("cattail", new CattailObject("cattail", 32, new Color(50, 24, 35)), 1.0F, true);
      registerObject("watergrass", new WaterPlantObject("watergrass", 32, new Color(63, 143, 59)), 1.0F, true);
      registerObject("deepswampgrass", new DeepSwampGrassObject(), 0.0F, false);
      registerObject("deepswamptallgrass", new DeepSwampTallGrassObject(), 0.0F, false);
      int var66 = registerObject("snowpile3", new SnowPileObject(3, -1), 0.0F, false);
      var66 = registerObject("snowpile2", new SnowPileObject(2, var66), 0.0F, false);
      var66 = registerObject("snowpile1", new SnowPileObject(1, var66), 0.0F, false);
      registerObject("snowpile0", new SnowPileObject(0, var66), 0.0F, false);
      cobWebID = registerObject("cobweb", new CobwebObject(), 0.0F, false);
      thornsID = registerObject("thorns", new ThornsObject(), 5.0F, true);
      LadderDownObject.registerLadderPair("ladder", -1, new Color(86, 69, 40), Item.Rarity.NORMAL, 10);
      registerObject("trialentrance", new TrialEntranceObject(), 0.0F, false);
      registerObject("trialexit", new TrialExitObject(), 0.0F, false);
      registerObject("dungeonentrance", new DungeonEntranceObject(), 0.0F, false);
      registerObject("dungeonexit", new DungeonExitObject(), 0.0F, false);
      LadderDownObject.registerLadderPair("deepladder", -2, new Color(56, 70, 84), Item.Rarity.UNCOMMON, 20);
      TempleEntranceObject.registerTempleEntrance();
      TempleExitObject.registerTempleExit();
      FallenAltarObject.registerFallenAltar();
      SpiderThroneObject.registerSpiderThrone();
      registerObject("upgradestation", new UpgradeStationObject(), 100.0F, true);
      registerObject("salvagestation", new SalvageStationObject(), 100.0F, true);
      registerObject("coinstack", new RandomCoinStackObject(), 0.0F, false);
      registerObject("crate", new RandomCrateObject("crates"), 0.0F, false);
      registerObject("snowcrate", new RandomCrateObject("snowcrates"), 0.0F, false);
      registerObject("swampcrate", new RandomCrateObject("swampcrates"), 0.0F, false);
      registerObject("vase", new RandomVaseObject("vases"), 0.0F, false);
      registerObject("royaleggobject", new RoyalEggObject(), 0.0F, false);
      registerObject("ancienttotem", new AncientTotemObject(), 0.0F, false);
      registerObject("templepedestal", new TemplePedestalObject(), 0.0F, false);
      registerObject("settlementflag", new SettlementFlagObject(), 500.0F, true);
      registerObject("homestone", new HomestoneObject(), 400.0F, true);
      registerObject("waystone", new WaystoneObject(), 100.0F, true);
      registerObject("bannerstand", new BannerStandObject(), 200.0F, true);
      registerObject("travelstone", new TravelstoneObject(), 1000.0F, false);
      registerObject("musicplayer", new MusicPlayerObject(), 800.0F, true);
      registerObject("christmastree", new ChristmasTreeObject(), 500.0F, true, false);
      registerObject("christmaswreath", new ChristmasWreathObject(), 50.0F, true, false);
   }

   protected void onRegister(ObjectRegistryElement var1, int var2, String var3, boolean var4) {
      Item var5 = var1.object.generateNewObjectItem();
      if (var5 != null) {
         ItemRegistry.replaceItem(var1.object.getStringID(), var5, var1.itemBrokerValue, var1.itemObtainable, var1.itemCountInStats);
      }

   }

   protected void onRegistryClose() {
      this.streamElements().map((var0) -> {
         return var0.object;
      }).forEach((var0) -> {
         String var1 = var0.getMultiTile(0).checkValid();
         if (var1 != null) {
            throw new IllegalStateException(var0.getStringID() + " has invalid multi tile setup: " + var1);
         }
      });
      this.streamElements().map((var0) -> {
         return var0.object;
      }).forEach(GameObject::updateLocalDisplayName);
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         ObjectRegistryElement var2 = (ObjectRegistryElement)var1.next();
         var2.object.onObjectRegistryClosed();
      }

      stringIDs = (String[])instance.streamElements().map((var0) -> {
         return var0.object.getStringID();
      }).toArray((var0) -> {
         return new String[var0];
      });
   }

   public static int registerObject(String var0, GameObject var1, float var2, boolean var3) {
      return registerObject(var0, var1, var2, var3, var3);
   }

   public static int registerObject(String var0, GameObject var1, float var2, boolean var3, boolean var4) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register objects");
      } else {
         return instance.register(var0, new ObjectRegistryElement(var1, var2, var3, var4));
      }
   }

   public static int replaceObject(String var0, GameObject var1, float var2, boolean var3) {
      return replaceObject(var0, var1, var2, var3, var3);
   }

   public static int replaceObject(String var0, GameObject var1, float var2, boolean var3, boolean var4) {
      return instance.replace(var0, new ObjectRegistryElement(var1, var2, var3, var4));
   }

   public static GameObject getObject(int var0) {
      if (var0 >= instance.size()) {
         var0 = 0;
      }

      ObjectRegistryElement var1 = (ObjectRegistryElement)instance.getElement(var0);
      if (var1 == null) {
         throw new NullPointerException(instance.objectCallName + " ID " + var0 + " seems to be missing or corrupted");
      } else {
         return var1.object;
      }
   }

   public static int getObjectID(String var0) {
      return instance.getElementID(var0);
   }

   public static GameObject getObject(String var0) {
      ObjectRegistryElement var1 = (ObjectRegistryElement)instance.getElement(var0);
      if (var1 == null) {
         throw new NullPointerException(instance.objectCallName + " stringID " + var0 + " seems to be missing or corrupted");
      } else {
         return var1.object;
      }
   }

   public static int getObjectIDRaw(String var0) throws NoSuchElementException {
      return instance.getElementIDRaw(var0);
   }

   public static Stream<GameObject> streamObjects() {
      return instance.streamElements().map((var0) -> {
         return var0.object;
      });
   }

   public static Iterable<GameObject> getObjects() {
      return (Iterable)streamObjects().collect(Collectors.toList());
   }

   public static int getObjectsCount() {
      return instance.size();
   }

   public static String[] getObjectStringIDs() {
      if (stringIDs == null) {
         throw new IllegalStateException("ObjectRegistry not yet closed");
      } else {
         return stringIDs;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((ObjectRegistryElement)var1, var2, var3, var4);
   }

   protected static class ObjectRegistryElement implements IDDataContainer {
      public final GameObject object;
      public final float itemBrokerValue;
      public final boolean itemObtainable;
      public final boolean itemCountInStats;

      public ObjectRegistryElement(GameObject var1, float var2, boolean var3, boolean var4) {
         this.object = var1;
         this.itemBrokerValue = var2;
         this.itemObtainable = var3;
         this.itemCountInStats = var4;
      }

      public IDData getIDData() {
         return this.object.idData;
      }
   }
}
