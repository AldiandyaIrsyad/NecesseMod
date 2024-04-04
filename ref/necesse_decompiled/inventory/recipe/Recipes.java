package necesse.inventory.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveSyntaxException;
import necesse.engine.util.GameRandom;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.incursion.IncursionBiome;

public class Recipes {
   private static RecipeList defaultRecipes = null;
   private static boolean canRegisterModRecipes = true;
   private static List<Recipe> modRecipes = new ArrayList();

   public Recipes() {
   }

   public static void loadDefaultRecipes() {
      defaultRecipes = new RecipeList();
      defaultRecipes.addModRecipes(modRecipes);
   }

   public static void registerModRecipe(Recipe var0) {
      if (!canRegisterModRecipes) {
         throw new IllegalStateException("Mod recipe registry is closed");
      } else {
         Objects.requireNonNull(var0);
         modRecipes.add(var0);
      }
   }

   public static void closeModRecipeRegistry() {
      canRegisterModRecipes = false;
   }

   public static List<Recipe> getResultItems(int var0) {
      return defaultRecipes.getResultItems(var0);
   }

   public static boolean isCraftingMat(int var0) {
      return defaultRecipes.isCraftingMat(var0);
   }

   public static HashSet<Tech> getCraftingMatTechs(int var0) {
      return defaultRecipes.getCraftingMatTechs(var0);
   }

   public static int getHash() {
      return defaultRecipes.getHash();
   }

   public static int getDefaultHash() {
      return defaultRecipes.getHash();
   }

   public static Recipe getRecipe(int var0) {
      return defaultRecipes.getRecipe(var0);
   }

   public static Iterable<Recipe> getRecipes() {
      return defaultRecipes.getRecipes();
   }

   public static Stream<Recipe> streamRecipes() {
      return defaultRecipes.streamRecipes();
   }

   public static Iterable<Recipe> getRecipes(Tech var0) {
      return defaultRecipes.getRecipes(var0);
   }

   public static Iterable<Recipe> getRecipes(Tech... var0) {
      return defaultRecipes.getRecipes(var0);
   }

   public static Stream<Recipe> streamRecipes(Tech... var0) {
      return defaultRecipes.streamRecipes(var0);
   }

   public static int getTotalRecipes() {
      return defaultRecipes.getTotalRecipes();
   }

   public static int getDefaultTotalRecipes() {
      return defaultRecipes.getTotalRecipes();
   }

   public static Ingredient[] ingredientsFromScript(String var0) {
      LoadData var1 = new LoadData(var0);
      if (!var1.isArray()) {
         throw new SaveSyntaxException("Ingredients script is not an array");
      } else {
         List var2 = var1.getLoadData();
         Ingredient[] var3 = new Ingredient[var2.size()];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = (new IngredientData((LoadData)var2.get(var4), (RecipeData)null)).validate();
         }

         return var3;
      }
   }

   public static ArrayList<Recipe> getDefaultRecipes() {
      ArrayList var0 = new ArrayList();
      var0.add(new Recipe("fallenalchemytable", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{anytier1essence, 10}, {alchemyshard, 10}}}")));
      var0.add(new Recipe("upgradestation", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{anytier1essence, 10}, {upgradeshard, 10}}}")));
      var0.add(new Recipe("salvagestation", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{anytier1essence, 10}, {upgradeshard, 10}}}")));
      var0.add(new Recipe("tabletbox", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{anytier1essence, 50}}}")));
      var0.add((new Recipe("gatewaytablet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{tungstenbar, 10}, {ectoplasm, 10}}}"), false, (new GNDItemMap()).setString("recipeBiome", "forestcave").setInt("displayTier", 1))).onCrafted((var0x) -> {
         var0x.resultItem.getGndData().setItem("recipeBiome", (GNDItem)null);
         IncursionBiome var1 = IncursionBiomeRegistry.getBiome("forestcave");
         GatewayTabletItem.initializeCustomGateTablet(var0x.resultItem, GameRandom.globalRandom, 1, var1);
      }));
      var0.add((new Recipe("gatewaytablet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{glacialbar, 10}, {glacialshard, 10}}}"), false, (new GNDItemMap()).setString("recipeBiome", "snowcave").setInt("displayTier", 1))).onCrafted((var0x) -> {
         var0x.resultItem.getGndData().setItem("recipeBiome", (GNDItem)null);
         IncursionBiome var1 = IncursionBiomeRegistry.getBiome("snowcave");
         GatewayTabletItem.initializeCustomGateTablet(var0x.resultItem, GameRandom.globalRandom, 1, var1);
      }));
      var0.add((new Recipe("gatewaytablet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{myceliumbar, 10}, {silk, 10}}}"), false, (new GNDItemMap()).setString("recipeBiome", "swampcave").setInt("displayTier", 1))).onCrafted((var0x) -> {
         var0x.resultItem.getGndData().setItem("recipeBiome", (GNDItem)null);
         IncursionBiome var1 = IncursionBiomeRegistry.getBiome("swampcave");
         GatewayTabletItem.initializeCustomGateTablet(var0x.resultItem, GameRandom.globalRandom, 1, var1);
      }));
      var0.add((new Recipe("gatewaytablet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 10}, {wormcarapace, 5}}}"), false, (new GNDItemMap()).setString("recipeBiome", "desertcave").setInt("displayTier", 1))).onCrafted((var0x) -> {
         var0x.resultItem.getGndData().setItem("recipeBiome", (GNDItem)null);
         IncursionBiome var1 = IncursionBiomeRegistry.getBiome("desertcave");
         GatewayTabletItem.initializeCustomGateTablet(var0x.resultItem, GameRandom.globalRandom, 1, var1);
      }));
      var0.add(new Recipe("shadowessence", 2, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 1}}}")));
      var0.add(new Recipe("cryoessence", 2, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 1}}}")));
      var0.add(new Recipe("bioessence", 2, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 1}}}")));
      var0.add(new Recipe("primordialessence", 2, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 1}}}")));
      var0.add(new Recipe("slimeessence", 2, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bloodessence, 1}}}")));
      var0.add(new Recipe("bloodessence", 2, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideressence, 1}}}")));
      var0.add((new Recipe("potionbag", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{potionpouch, 1}, {shadowessence, 5}, {cryoessence, 5}, {bioessence, 5}, {primordialessence, 5}}}"))).onCrafted((var0x) -> {
         var0x.itemsUsed.stream().filter((var0) -> {
            return var0.invItem.item.getStringID().equals("potionpouch");
         }).findFirst().ifPresent((var1) -> {
            var0x.resultItem.setGndData(var1.invItem.getGndData());
         });
      }));
      var0.add((new Recipe("ammobag", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{ammopouch, 1}, {shadowessence, 5}, {cryoessence, 5}, {bioessence, 5}, {primordialessence, 5}}}"))).onCrafted((var0x) -> {
         var0x.itemsUsed.stream().filter((var0) -> {
            return var0.invItem.item.getStringID().equals("ammopouch");
         }).findFirst().ifPresent((var1) -> {
            var0x.resultItem.setGndData(var1.invItem.getGndData());
         });
      }));
      var0.add(new Recipe("slimegreatsword", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 5}, {slimematter, 12}, {slimeum, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("slimeglaive", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 5}, {slimematter, 20}, {slimeum, 16}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("slimestaff", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 5}, {slimematter, 20}, {slimeum, 16}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("slimegreatbow", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 5}, {slimematter, 16}, {slimeum, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("orbofslimes", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimeessence, 5}, {slimematter, 16}, {slimeum, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("gelatincasings", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimematter, 20}, {slimeum, 12}}")));
      var0.add(new Recipe("slimehelmet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimematter, 24}, {slimeum, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("slimehat", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimematter, 24}, {slimeum, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("slimechestplate", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimematter, 32}, {slimeum, 32}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("slimeboots", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{slimematter, 16}, {slimeum, 16}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightrazorboomerang", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bloodessence, 2}, {nightsteelbar, 5}, {phantomdust, 10}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightpiercer", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bloodessence, 5}, {nightsteelbar, 20}, {phantomdust, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("phantompopper", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bloodessence, 5}, {nightsteelbar, 16}, {phantomdust, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("phantomcaller", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bloodessence, 5}, {nightsteelbar, 12}, {phantomdust, 32}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightsteelhelmet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{nightsteelbar, 12}, {phantomdust, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightsteelmask", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{nightsteelbar, 12}, {phantomdust, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightsteelveil", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{nightsteelbar, 12}, {phantomdust, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightsteelcirclet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{nightsteelbar, 12}, {phantomdust, 24}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightsteelchestplate", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{nightsteelbar, 16}, {phantomdust, 32}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("nightsteelboots", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{nightsteelbar, 8}, {phantomdust, 16}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("causticexecutioner", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideressence, 5}, {spideritebar, 18}, {spidervenom, 15}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("arachnidwebbow", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideressence, 5}, {spideritebar, 15}, {spidervenom, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("webweaver", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideressence, 5}, {spideritebar, 20}, {spidervenom, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("empresscommand", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideressence, 5}, {spideritebar, 25}, {spidervenom, 10}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spideritearrow", 100, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 1}}")));
      var0.add(new Recipe("spideritehelmet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 12}, {spidervenom, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spideritehood", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 12}, {spidervenom, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spideritehat", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 12}, {spidervenom, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spideritecrown", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 12}, {spidervenom, 20}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spideritechestplate", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 16}, {spidervenom, 30}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spideritegreaves", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spideritebar, 8}, {spidervenom, 15}}}"), false, (new GNDItemMap()).setInt("upgradeLevel", 100)));
      var0.add(new Recipe("spidercastlewall", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spiderstone, 2}}")));
      var0.add(new Recipe("spidercastledoor", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spiderstone, 4}}")));
      var0.add(new Recipe("spidercastlefloor", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{spiderstone, 5}}")));
      var0.add(new Recipe("spidercastlecarpet", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{silk, 1}}")));
      var0.add(new Recipe("scryingmirror", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("diggingclaw", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("antiquerifle", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("clockworkheart", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("dragonsrebound", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("dragonlance", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("bowofdualism", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("skeletonstaff", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{primordialessence, 20}}}")));
      var0.add(new Recipe("agedchampionshield", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("swampdwellerstaff", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("druidsgreatbow", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("venomshower", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("venomslasher", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("livingshotty", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("swampsgrasp", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{bioessence, 20}}}")));
      var0.add(new Recipe("spikedboots", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("froststone", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("siphonshield", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("icepickaxe", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("cryoquake", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("cryospear", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("cryoblaster", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("cryoglaive", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{cryoessence, 20}}}")));
      var0.add(new Recipe("elderlywand", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("firestone", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("depthscatcher", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("shadowbeam", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("reaperscall", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("deathripper", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("reaperscythe", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{shadowessence, 20}}}")));
      var0.add(new Recipe("dawndoor", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{dawnwall, 5}}")));
      var0.add(new Recipe("duskdoor", 1, RecipeTechRegistry.FALLEN_WORKSTATION, ingredientsFromScript("{{duskwall, 5}}")));
      var0.add(new Recipe("superiorhealthpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{greaterhealthpotion, 1}, {alchemyshard, 1}}")));
      var0.add(new Recipe("superiormanapotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{greatermanapotion, 1}, {alchemyshard, 1}}")));
      var0.add(new Recipe("greaterspeedpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{speedpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterhealthregenpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{healthregenpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterresistancepotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{resistancepotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterbattlepotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{battlepotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterattackspeedpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{attackspeedpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greatermanaregenpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{manaregenpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greateraccuracypotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{accuracypotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterrapidpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{rapidpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterfishingpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{fishingpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterminingpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{miningpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("greaterbuildingpotion", 1, RecipeTechRegistry.FALLEN_ALCHEMY, ingredientsFromScript("{{buildingpotion, 1}, {alchemyshard, 4}}")));
      var0.add(new Recipe("lifeelixir", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{lifequartz, 10}, {sunflower, 10}, {glassbottle, 1}}}")));
      var0.add(new Recipe("fallenaltar", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{gatewaytablet, 0}, {tungstenbar, 10}, {glacialbar, 10}, {myceliumbar, 10}, {ancientfossilbar, 10}}}")));
      var0.add(new Recipe("fallenworkstation", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{upgradeshard, 15}, {alchemyshard, 15}}}")));
      var0.add(new Recipe("shadowgate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 4}, {ectoplasm, 8}, {bone, 8}}")));
      var0.add(new Recipe("icecrown", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 4}, {glacialshard, 8}, {bone, 8}}")));
      var0.add(new Recipe("decayingleaf", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 4}, {silk, 8}, {bone, 8}}")));
      var0.add(new Recipe("dragonsouls", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 4}, {wormcarapace, 4}, {bone, 8}}")));
      var0.add(new Recipe("spikedfossil", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 4}, {ivybar, 4}, {voidshard, 4}}")));
      var0.add(new Recipe("ancientstatue", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 4}, {quartz, 4}, {voidshard, 4}}")));
      var0.add(new Recipe("voidcaller", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 4}, {voidshard, 4}}")));
      var0.add(new Recipe("royalegg", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 4}, {frostshard, 4}, {cavespidergland, 4}}")));
      var0.add(new Recipe("mysteriousportal", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 4}, {batwing, 4}}")));
      var0.add(new Recipe("advancedworkstation", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{tungstenbar, 8}, {quartz, 4}}")));
      var0.add(new Recipe("cheesepress", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ironbar, 5}, {demonicbar, 5}}")));
      var0.add(new Recipe("apiary", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{anylog, 20}, {ironbar, 10}, {honey, 5}}")));
      var0.add(new Recipe("apiaryframe", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{anylog, 5}, {wool, 5}}")));
      var0.add(new Recipe("net", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{anylog, 10}, {wool, 5}}")));
      var0.add(new Recipe("tungstenbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{tungstenore, 4}}"), true));
      var0.add(new Recipe("glacialbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{glacialore, 4}}"), true));
      var0.add(new Recipe("myceliumbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{myceliumore, 4}}"), true));
      var0.add(new Recipe("ancientfossilbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{ancientfossilore, 4}}"), true));
      var0.add(new Recipe("nightsteelbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{nightsteelore, 4}}"), true));
      var0.add(new Recipe("spideritebar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{spideriteore, 4}}"), true));
      var0.add(new Recipe("deepladderdown", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 8}}")));
      var0.add(new Recipe("cookingstation", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 8}, {obsidian, 12}}")));
      var0.add(new Recipe("tungstenpickaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 16}}")));
      var0.add(new Recipe("tungstenaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 16}}")));
      var0.add(new Recipe("tungstenshovel", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 16}}")));
      var0.add(new Recipe("tungstensword", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 12}}")));
      var0.add(new Recipe("tungstenspear", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 18}}")));
      var0.add(new Recipe("tungstenboomerang", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 5}, {obsidian, 10}}")));
      var0.add(new Recipe("tungstenbow", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 12}}")));
      var0.add(new Recipe("tungstengreatbow", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 16}, {ectoplasm, 8}}")));
      var0.add(new Recipe("shadowbolt", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ectoplasm, 15}, {book, 1}}")));
      var0.add(new Recipe("tungstenhelmet", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 12}, {obsidian, 12}}")));
      var0.add(new Recipe("tungstenchestplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 16}, {obsidian, 16}}")));
      var0.add(new Recipe("tungstenboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 8}, {obsidian, 8}}")));
      var0.add(new Recipe("tungstenshield", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 16}, {obsidian, 8}}")));
      var0.add(new Recipe("shadowhat", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ectoplasm, 12}, {bone, 10}}")));
      var0.add(new Recipe("shadowhood", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ectoplasm, 12}, {bone, 10}}")));
      var0.add(new Recipe("shadowmantle", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ectoplasm, 16}, {bone, 14}}")));
      var0.add(new Recipe("shadowboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ectoplasm, 8}, {bone, 8}}")));
      var0.add(new Recipe("iciclestaff", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 12}, {glacialshard, 6}}")));
      var0.add(new Recipe("cryostaff", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 14}, {glacialshard, 15}}")));
      var0.add(new Recipe("glacialpickaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 16}}")));
      var0.add(new Recipe("glacialaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 16}}")));
      var0.add(new Recipe("glacialshovel", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 16}}")));
      var0.add(new Recipe("glacialgreatsword", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 20}, {glacialshard, 12}}")));
      var0.add(new Recipe("glacialboomerang", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 12}, {glacialshard, 5}}")));
      var0.add(new Recipe("glacialbow", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 12}, {glacialshard, 5}}")));
      var0.add(new Recipe("glacialcirclet", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 12}, {glacialshard, 3}}")));
      var0.add(new Recipe("glacialhelmet", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 12}, {glacialshard, 3}}")));
      var0.add(new Recipe("glacialchestplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 16}, {glacialshard, 5}}")));
      var0.add(new Recipe("glacialboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{glacialbar, 8}, {glacialshard, 2}}")));
      var0.add(new Recipe("myceliumpickaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 16}}")));
      var0.add(new Recipe("myceliumaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 16}}")));
      var0.add(new Recipe("myceliumshovel", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 16}}")));
      var0.add(new Recipe("myceliumgreatbow", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 20}}")));
      var0.add(new Recipe("myceliumhood", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 12}}")));
      var0.add(new Recipe("myceliumscarf", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 12}}")));
      var0.add(new Recipe("myceliumchestplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 16}}")));
      var0.add(new Recipe("myceliumboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{myceliumbar, 8}}")));
      var0.add(new Recipe("widowhelmet", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{silk, 12}, {myceliumbar, 8}, {cavespidergland, 6}}")));
      var0.add(new Recipe("widowchestplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{silk, 16}, {myceliumbar, 10}, {cavespidergland, 8}}")));
      var0.add(new Recipe("widowboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{silk, 8}, {myceliumbar, 6}, {cavespidergland, 4}}")));
      var0.add(new Recipe("ancientfossilpickaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 16}}")));
      var0.add(new Recipe("ancientfossilaxe", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 16}}")));
      var0.add(new Recipe("ancientfossilshovel", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 16}}")));
      var0.add(new Recipe("ancientdredgingstaff", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{dredgingstaff, 1}, {ancientfossilbar, 15, true}}")));
      var0.add(new Recipe("sandknife", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{quartz, 20}, {ancientfossilbar, 5}, {wormcarapace, 5}}"), true));
      var0.add(new Recipe("carapacedagger", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{bonehilt, 1}, {wormcarapace, 5}}"), true));
      var0.add(new Recipe("carapaceshield", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{wormcarapace, 15}}"), true));
      var0.add(new Recipe("ancientfossilmask", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 8}, {wormcarapace, 5}}"), true));
      var0.add(new Recipe("ancientfossilhelmet", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 12}, {wormcarapace, 3}}"), true));
      var0.add(new Recipe("ancientfossilchestplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 16}, {wormcarapace, 4}}"), true));
      var0.add(new Recipe("ancientfossilboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ancientfossilbar, 8}, {wormcarapace, 2}}"), true));
      var0.add(new Recipe("bonearrow", 10, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{bone, 1, true}}")));
      var0.add(new Recipe("bonehilt", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{bone, 40}, {tungstenbar, 4}}")));
      var0.add(new Recipe("frenzyorb", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{ectoplasm, 15}, {voidshard, 5}}")));
      var0.add(new Recipe("lifependant", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{lifequartz, 10}, {tungstenbar, 4}, {regenpendant, 1}}")));
      var0.add(new Recipe("spellstone", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{sparegemstones, 1}, {ectoplasm, 10}}")));
      var0.add(new Recipe("manica", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{chainshirt, 1}, {vambrace, 1}}"), true));
      var0.add(new Recipe("explorercloak", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{travelercloak, 1}, {piratetelescope, 1}}"), true));
      var0.add(new Recipe("ancientrelics", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{airvessel, 1}, {templependant, 1}}"), true));
      var0.add(new Recipe("spikedbatboots", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{vampiresgift, 1}, {spikedboots, 1}}"), true));
      var0.add(new Recipe("frostflame", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{firestone, 1}, {froststone, 1}}"), true));
      var0.add(new Recipe("balancedfrostfirefoci", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{balancedfoci, 1}, {frostflame, 1}}"), true));
      var0.add(new Recipe("hysteriatablet", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{mesmertablet, 1}, {inducingamulet, 1}}"), true));
      var0.add(new Recipe("frozensoul", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{lifeline, 1}, {frozenheart, 1}}"), true));
      var0.add(new Recipe("toolbox", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{constructionhammer, 1}, {telescopicladder, 1}, {toolextender, 1}, {itemattractor, 1}}"), true));
      var0.add(new Recipe("lantern", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 1}}")));
      var0.add(new Recipe("walllantern", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{lantern, 1}, {anylog, 1}}")));
      var0.add(new Recipe("tungstenstreetlamp", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 5}, {torch, 1}}")));
      var0.add(new Recipe("tungstendoublestreetlamp", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{tungstenbar, 7}, {torch, 2}}")));
      var0.add(new Recipe("piratemap", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{mapfragment, 4}, {coin, 50}}")));
      var0.add(new Recipe("demonicbar", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{copperbar, 3}}")));
      var0.add(new Recipe("demonicbar", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ironbar, 2}}")));
      var0.add(new Recipe("demonicbar", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{goldbar, 1}}")));
      var0.add(new Recipe("demonicpickaxe", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 10}}")));
      var0.add(new Recipe("demonicaxe", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 10}}")));
      var0.add(new Recipe("demonicshovel", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 10}}")));
      var0.add(new Recipe("demonicsword", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 12}}")));
      var0.add(new Recipe("demonicspear", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 18}}")));
      var0.add(new Recipe("demonicbow", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 12}}")));
      var0.add(new Recipe("demonichelmet", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 12}, {voidshard, 1}}")));
      var0.add(new Recipe("demonicchestplate", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 16}, {voidshard, 1}}")));
      var0.add(new Recipe("demonicboots", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 8}, {voidshard, 1}}")));
      var0.add(new Recipe("voidmask", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 12}, {voidshard, 4}}")));
      var0.add(new Recipe("voidhat", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 12}, {voidshard, 4}}")));
      var0.add(new Recipe("voidrobe", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 16}, {voidshard, 6}}")));
      var0.add(new Recipe("voidboots", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 8}, {voidshard, 3}}")));
      var0.add(new Recipe("voidspear", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{voidshard, 16}, {demonicbar, 8}}")));
      var0.add(new Recipe("voidboomerang", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{voidshard, 8}, {demonicbar, 4}}")));
      var0.add(new Recipe("voidgreatbow", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{voidshard, 16}, {demonicbar, 8}}")));
      var0.add(new Recipe("bloodvolley", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{bloodbolt, 1}, {voidshard, 10}, {batwing, 10}}")));
      var0.add(new Recipe("bloodplatecowl", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 14}, {batwing, 8}}")));
      var0.add(new Recipe("bloodplatechestplate", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 18}, {batwing, 10}}")));
      var0.add(new Recipe("bloodplateboots", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 10}, {batwing, 6}}")));
      var0.add(new Recipe("ivypickaxe", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 10}}")));
      var0.add(new Recipe("ivyaxe", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 10}}")));
      var0.add(new Recipe("ivyshovel", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 10}}")));
      var0.add(new Recipe("ivysword", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 12}}")));
      var0.add(new Recipe("ivygreatsword", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 20}}")));
      var0.add(new Recipe("ivyspear", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 18}}")));
      var0.add(new Recipe("ivybow", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 12}}")));
      var0.add(new Recipe("ivygreatbow", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 16}, {swampsludge, 8}}")));
      var0.add(new Recipe("boulderstaff", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 14}, {swampsludge, 10}, {voidshard, 5}}")));
      var0.add(new Recipe("ivyhelmet", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 12}, {swampsludge, 6}}")));
      var0.add(new Recipe("ivyhood", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 12}, {swampsludge, 6}}")));
      var0.add(new Recipe("ivyhat", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 12}, {swampsludge, 6}}")));
      var0.add(new Recipe("ivycirclet", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 12}, {swampsludge, 6}}")));
      var0.add(new Recipe("ivychestplate", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 16}, {swampsludge, 8}}")));
      var0.add(new Recipe("ivyboots", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ivybar, 8}, {swampsludge, 4}}")));
      var0.add(new Recipe("quartzgreatsword", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 20}}")));
      var0.add(new Recipe("quartzglaive", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 15}}")));
      var0.add(new Recipe("quartzstaff", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 10}, {voidshard, 5}}")));
      var0.add(new Recipe("quartzhelmet", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 12}}")));
      var0.add(new Recipe("quartzcrown", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 12}}")));
      var0.add(new Recipe("quartzchestplate", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 16}}")));
      var0.add(new Recipe("quartzboots", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{quartz, 8}}")));
      var0.add(new Recipe("bannerofspeed", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 30}, {speedpotion, 10}}")));
      var0.add(new Recipe("bannerofdamage", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 30}, {battlepotion, 10}}")));
      var0.add(new Recipe("bannerofdefense", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 30}, {resistancepotion, 10}}")));
      var0.add(new Recipe("bannerofsummonspeed", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{wool, 30}, {voidshard, 20}}")));
      var0.add(new Recipe("balancedfoci", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{meleefoci, 1}, {rangefoci, 1}, {magicfoci, 1}, {summonfoci, 1}}")));
      var0.add(new Recipe("zephyrboots", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{leatherdashers, 1}, {zephyrcharm, 1}}"), true));
      var0.add(new Recipe("explorersatchel", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{leatherglove, 1}, {trackerboot, 1}, {shinebelt, 1}}"), true));
      var0.add(new Recipe("shellofretribution", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{cactusshield, 1}, {spidercharm, 1}}"), true));
      var0.add(new Recipe("assassinscowl", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{luckycape, 1}, {ancientfeather, 1}}"), true));
      var0.add(new Recipe("demonclaw", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{demonicbar, 5}}")));
      var0.add(new Recipe("nightmaretalisman", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{dreamcatcher, 1}, {voidshard, 10}}")));
      var0.add(new Recipe("scryingcards", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{prophecyslab, 1}, {magicmanual, 1}}")));
      var0.add(new Recipe("travelercloak", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{mobilitycloak, 1}, {fins, 1}}"), true));
      var0.add(new Recipe("calmingminersbouquet", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{calmingrose, 1}, {miningcharm, 1}}"), true));
      var0.add(new Recipe("claygauntlet", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{clay, 25}}")));
      var0.add(new Recipe("chainshirt", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{ironbar, 10}, {leather, 10}}")));
      var0.add(new Recipe("steelboat", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{spareboatparts, 1}, {coin, 500}}")));
      var0.add(new Recipe("tnt", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{dynamitestick, 4}, {wire, 10}}")));
      var0.add(new Recipe("glassbottle", 2, RecipeTechRegistry.FORGE, ingredientsFromScript("{{sandtile, 1}}")));
      var0.add(new Recipe("healthpotion", 2, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{sunflower, 3}, {glassbottle, 2}}")));
      var0.add(new Recipe("greaterhealthpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{healthpotion, 1}, {caveglow, 1}}"), true));
      var0.add(new Recipe("manapotion", 2, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{sunflower, 1}, {blueberry, 4}, {glassbottle, 2}}")));
      var0.add(new Recipe("greatermanapotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{manapotion, 1}, {caveglow, 1}}"), true));
      var0.add(new Recipe("speedpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{batwing, 2}, {glassbottle, 1}}")));
      var0.add(new Recipe("healthregenpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{firemone, 5}, {glassbottle, 1}}")));
      var0.add(new Recipe("resistancepotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{rockfish, 2}, {anystone, 10}, {glassbottle, 1}}")));
      var0.add(new Recipe("battlepotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{terrorfish, 1}, {swampfish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("attackspeedpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{voidshard, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("manaregenpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{iceblossom, 5}, {glassbottle, 1}}")));
      var0.add(new Recipe("accuracypotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{firemone, 2}, {thorns, 3}, {glassbottle, 1}}")));
      var0.add(new Recipe("rapidpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{swampfish, 1}, {gobfish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("knockbackpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{rockfish, 1}, {iceblossom, 2}, {glassbottle, 1}}")));
      var0.add(new Recipe("thornspotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{thorns, 4}, {mushroom, 2}, {glassbottle, 1}}")));
      var0.add(new Recipe("fireresistancepotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{icefish, 1}, {halffish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("invisibilitypotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{terrorfish, 2}, {icefish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("fishingpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{wormbait, 2}, {gobfish, 2}, {glassbottle, 1}}")));
      var0.add(new Recipe("miningpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{batwing, 2}, {ironbar, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("spelunkerpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{voidshard, 2}, {goldbar, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("treasurepotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{ectoplasm, 2}, {iceblossom, 2}, {glassbottle, 1}}")));
      var0.add(new Recipe("passivepotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{sunflower, 3}, {furfish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("buildingpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{clay, 4}, {mushroom, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("strengthpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{obsidian, 2}, {caveglow, 2}, {rockfish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("rangerpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{bone, 1}, {caveglow, 2}, {swampfish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("wisdompotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{ectoplasm, 1}, {caveglow, 2}, {furfish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("minionpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{glacialshard, 1}, {caveglow, 2}, {icefish, 1}, {glassbottle, 1}}")));
      var0.add(new Recipe("webpotion", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{silk, 1}, {caveglow, 2}, {glassbottle, 1}}")));
      var0.add(new Recipe("regenpendant", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{goldbar, 10}, {healthregenpotion, 5}}")));
      var0.add(new Recipe("sandtile", 1, RecipeTechRegistry.ALCHEMY, ingredientsFromScript("{{anystone, 5}}")));
      var0.add(new Recipe("craftingguide", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 2}, {leather, 5}}")));
      var0.add(new Recipe("woodpickaxe", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 8}}")));
      var0.add(new Recipe("woodaxe", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 8}}")));
      var0.add(new Recipe("woodshovel", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 8}}")));
      var0.add(new Recipe("woodsword", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("woodspear", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 15}}")));
      var0.add(new Recipe("woodbow", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("woodboomerang", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("woodshield", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {ironbar, 2}}")));
      var0.add(new Recipe("woodstaff", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {goldbar, 5}, {batwing, 2}}")));
      var0.add(new Recipe("leatherdashers", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{leather, 10}, {wool, 5}}")));
      var0.add(new Recipe("leatherglove", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{leather, 10}}")));
      var0.add(new Recipe("trackerboot", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{leather, 10}, {ironbar, 2}}")));
      var0.add(new Recipe("dreamcatcher", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anysapling, 5}, {wool, 5}}")));
      var0.add(new Recipe("luckycape", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{fuzzydice, 1}, {noblehorseshoe, 1}}"), true));
      var0.add(new Recipe("woodfishingrod", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {ironbar, 1}, {anysapling, 1}}")));
      var0.add(new Recipe("bloodbolt", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{book, 1}, {batwing, 10}}")));
      var0.add(new Recipe("spiderboomerang", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{cavespidergland, 8}}"), true));
      var0.add(new Recipe("venomstaff", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{cavespidergland, 10}}"), true));
      var0.add(new Recipe("spiderstaff", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{cavespidergland, 12}}"), true));
      var0.add(new Recipe("spiderhelmet", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{cavespidergland, 7}}"), true));
      var0.add(new Recipe("spiderchestplate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{cavespidergland, 10}}"), true));
      var0.add(new Recipe("spiderboots", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{cavespidergland, 4}}"), true));
      var0.add(new Recipe("leatherhood", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{leather, 12}}")));
      var0.add(new Recipe("leathershirt", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{leather, 16}}")));
      var0.add(new Recipe("leatherboots", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{leather, 8}}")));
      var0.add(new Recipe("clothhat", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{wool, 12}}")));
      var0.add(new Recipe("clothrobe", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{wool, 16}}")));
      var0.add(new Recipe("clothboots", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{wool, 8}}")));
      var0.add(new Recipe("copperhelmet", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 12}}")));
      var0.add(new Recipe("copperchestplate", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 16}}")));
      var0.add(new Recipe("copperboots", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 8}}")));
      var0.add(new Recipe("copperpickaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("copperaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("coppershovel", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("coppersword", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 10}, {anylog, 1}}")));
      var0.add(new Recipe("copperspear", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 15}, {anylog, 2}}")));
      var0.add(new Recipe("copperbow", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 10}}")));
      var0.add(new Recipe("ironhelmet", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 12}}")));
      var0.add(new Recipe("ironchestplate", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 16}}")));
      var0.add(new Recipe("ironboots", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 8}}")));
      var0.add(new Recipe("ironpickaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("ironaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("ironshovel", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("ironsword", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 10}, {anylog, 1}}")));
      var0.add(new Recipe("irongreatsword", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 20}, {anylog, 2}}")));
      var0.add(new Recipe("ironspear", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 15}, {anylog, 2}}")));
      var0.add(new Recipe("ironbow", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 10}}")));
      var0.add(new Recipe("sparkler", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{torch, 10}, {ironbar, 5}, {anylog, 5}}")));
      var0.add(new Recipe("hardenedshield", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{woodshield, 1}, {ironbar, 10}, {leather, 10}}")));
      var0.add(new Recipe("goldcrown", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 12}}")));
      var0.add(new Recipe("goldhelmet", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 12}}")));
      var0.add(new Recipe("goldchestplate", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 16}}")));
      var0.add(new Recipe("goldboots", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 8}}")));
      var0.add(new Recipe("goldpickaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("goldaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("goldshovel", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 8}, {anylog, 1}}")));
      var0.add(new Recipe("goldsword", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 10}, {anylog, 1}}")));
      var0.add(new Recipe("goldspear", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 15}, {anylog, 2}}")));
      var0.add(new Recipe("goldglaive", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 16}}")));
      var0.add(new Recipe("goldbow", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 10}}")));
      var0.add(new Recipe("goldgreatbow", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 16}}")));
      var0.add(new Recipe("sickle", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 10}, {anylog, 1}}")));
      var0.add(new Recipe("shears", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 10}, {anylog, 1}}")));
      var0.add(new Recipe("bucket", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 3}}")));
      var0.add(new Recipe("cannonball", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 1}, {handcannon, 0, true}}")));
      var0.add(new Recipe("oillantern", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 1}}")));
      var0.add(new Recipe("copperstreetlamp", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 5}, {torch, 1}}")));
      var0.add(new Recipe("copperdoublestreetlamp", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{copperbar, 7}, {torch, 2}}")));
      var0.add(new Recipe("ironstreetlamp", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 5}, {torch, 1}}")));
      var0.add(new Recipe("irondoublestreetlamp", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{ironbar, 7}, {torch, 2}}")));
      var0.add(new Recipe("goldstreetlamp", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 5}, {torch, 1}}")));
      var0.add(new Recipe("golddoublestreetlamp", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{goldbar, 7}, {torch, 2}}")));
      var0.add(new Recipe("frosthelmet", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 12}}")));
      var0.add(new Recipe("frosthood", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 12}}")));
      var0.add(new Recipe("frosthat", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 12}}")));
      var0.add(new Recipe("frostchestplate", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 16}}")));
      var0.add(new Recipe("frostboots", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 8}}")));
      var0.add(new Recipe("frostpickaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 10}}")));
      var0.add(new Recipe("frostaxe", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 10}}")));
      var0.add(new Recipe("frostshovel", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 10}}")));
      var0.add(new Recipe("frostsword", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 12}}")));
      var0.add(new Recipe("frostgreatsword", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 20}}")));
      var0.add(new Recipe("frostspear", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 15}}")));
      var0.add(new Recipe("frostglaive", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 16}}")));
      var0.add(new Recipe("frostboomerang", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 12}}")));
      var0.add(new Recipe("frostbow", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 12}}")));
      var0.add(new Recipe("froststaff", 1, RecipeTechRegistry.IRON_ANVIL, ingredientsFromScript("{{frostshard, 16}, {batwing, 8}}")));
      var0.add(new Recipe("torch", 4, RecipeTechRegistry.NONE, ingredientsFromScript("{{anylog, 1}, {anysapling, 1}}")));
      var0.add(new Recipe("workstation", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("ladderdown", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("woodboat", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{anylog, 8}}")));
      var0.add(new Recipe("villagemap", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{mapfragment, 2}}"), true));
      var0.add(new Recipe("dungeonmap", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{mapfragment, 2}}"), true));
      var0.add(new Recipe("ironbomb", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{tilebomb, 1}}"), true));
      var0.add(new Recipe("tilebomb", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{ironbomb, 1}}"), true));
      var0.add(new Recipe("storagebox", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 8}}")));
      var0.add(new Recipe("coolingbox", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {anystone, 10}, {frostshard, 5}}")));
      var0.add(new Recipe("forge", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anystone, 20}}")));
      var0.add(new Recipe("carpentersbench", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {ironbar, 5}}")));
      var0.add(new Recipe("ironanvil", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 6}}")));
      var0.add(new Recipe("demonicworkstation", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{demonicbar, 5}}")));
      var0.add(new Recipe("alchemytable", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {glassbottle, 2}, {anystone, 10}}")));
      var0.add(new Recipe("cartographertable", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {mapfragment, 2}}")));
      var0.add(new Recipe("settlementflag", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anystone, 50}, {goldbar, 10}, {coin, 1000}}")));
      var0.add(new Recipe("farmland", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 5}}")));
      var0.add(new Recipe("compostbin", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 20}, {ironbar, 5}}")));
      var0.add(new Recipe("campfire", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}, {anystone, 20}}")));
      var0.add(new Recipe("roastingstation", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("cookingpot", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{clay, 10}, {ironbar, 4}}")));
      var0.add(new Recipe("grainmill", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 20}, {wool, 10}, {ironbar, 10}}")));
      var0.add(new Recipe("feedingtrough", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 20}, {ironbar, 5}}")));
      var0.add(new Recipe("incinerator", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 10}, {clay, 10}}")));
      var0.add(new Recipe("sign", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("tikitorch", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{torch, 1}, {anylog, 1}}")));
      var0.add(new Recipe("landfill", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anystone, 2}}")));
      var0.add(new Recipe("woodwall", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 2}}")));
      var0.add(new Recipe("wooddoor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 4}}")));
      var0.add(new Recipe("woodfloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 1}}")));
      var0.add(new Recipe("woodpathtile", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 2}}")));
      var0.add(new Recipe("woodfence", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 2}}")));
      var0.add(new Recipe("woodfencegate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 4}}")));
      var0.add(new Recipe("stonefence", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 4}}")));
      var0.add(new Recipe("stonefencegate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 4}, {anylog, 2}}")));
      var0.add(new Recipe("ironfence", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 1}}")));
      var0.add(new Recipe("ironfencegate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 4}}")));
      var0.add(new Recipe("pinewall", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{pinelog, 2}}"), true));
      var0.add(new Recipe("pinedoor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{pinelog, 4}}"), true));
      var0.add(new Recipe("pinefloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{pinelog, 1}}"), true));
      var0.add(new Recipe("palmwall", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{palmlog, 2}}"), true));
      var0.add(new Recipe("palmdoor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{palmlog, 4}}"), true));
      var0.add(new Recipe("palmfloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{palmlog, 1}}"), true));
      var0.add(new Recipe("deadwoodfloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{deadwoodlog, 1}}"), true));
      var0.add(new Recipe("strawtile", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{wheat, 1}}"), true));
      var0.add(new Recipe("stonewall", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 5}}")));
      var0.add(new Recipe("stonedoor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 15}}")));
      var0.add(new Recipe("stonefloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 1}}")));
      var0.add(new Recipe("stonebrickfloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 1}}")));
      var0.add(new Recipe("stonetiledfloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 1}}")));
      var0.add(new Recipe("stonepathtile", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 2}}")));
      var0.add(new Recipe("brickwall", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{clay, 2}}")));
      var0.add(new Recipe("brickdoor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{clay, 4}}")));
      var0.add(new Recipe("snowstonewall", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{snowstone, 5}}"), true));
      var0.add(new Recipe("snowstonedoor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{snowstone, 15}}"), true));
      var0.add(new Recipe("snowstonefloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{snowstone, 1}}"), true));
      var0.add(new Recipe("snowstonebrickfloor", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{snowstone, 1}}"), true));
      var0.add(new Recipe("snowstonepathtile", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{snowstone, 2}}"), true));
      var0.add(new Recipe("swampstonewall", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{swampstone, 5}}"), true));
      var0.add(new Recipe("swampstonedoor", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{swampstone, 15}}"), true));
      var0.add(new Recipe("swampstonefloor", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{swampstone, 1}}"), true));
      var0.add(new Recipe("swampstonebrickfloor", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{swampstone, 1}}"), true));
      var0.add(new Recipe("swampstonepathtile", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{swampstone, 2}}"), true));
      var0.add(new Recipe("sandstonewall", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{sandstone, 5}}"), true));
      var0.add(new Recipe("sandstonedoor", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{sandstone, 15}}"), true));
      var0.add(new Recipe("sandstonefloor", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{sandstone, 1}}"), true));
      var0.add(new Recipe("sandstonebrickfloor", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{sandstone, 1}}"), true));
      var0.add(new Recipe("sandstonepathtile", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{sandstone, 2}}"), true));
      var0.add(new Recipe("deepstonewall", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepstone, 5}}"), true));
      var0.add(new Recipe("deepstonedoor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepstone, 15}}"), true));
      var0.add(new Recipe("deepstonefloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepstone, 1}}"), true));
      var0.add(new Recipe("deepstonebrickfloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepstone, 1}}"), true));
      var0.add(new Recipe("deepstonetiledfloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepstone, 1}}"), true));
      var0.add(new Recipe("obsidianwall", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{obsidian, 2}}"), true));
      var0.add(new Recipe("obsidiandoor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{obsidian, 8}}"), true));
      var0.add(new Recipe("deepsnowstonewall", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsnowstone, 5}}"), true));
      var0.add(new Recipe("deepsnowstonedoor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsnowstone, 15}}"), true));
      var0.add(new Recipe("deepsnowstonefloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsnowstone, 1}}"), true));
      var0.add(new Recipe("deepsnowstonebrickfloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsnowstone, 1}}"), true));
      var0.add(new Recipe("deepsandstonewall", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsandstone, 5}}"), true));
      var0.add(new Recipe("deepsandstonedoor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsandstone, 15}}"), true));
      var0.add(new Recipe("deepswampstonewall", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepswampstone, 5}}"), true));
      var0.add(new Recipe("deepswampstonedoor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepswampstone, 15}}"), true));
      var0.add(new Recipe("deepswampstonefloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepswampstone, 1}}"), true));
      var0.add(new Recipe("deepswampstonebrickfloor", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepswampstone, 1}}"), true));
      var0.add(new Recipe("stonearrow", 5, RecipeTechRegistry.NONE, ingredientsFromScript("{{anylog, 1}, {anystone, 5}}")));
      var0.add(new Recipe("ironarrow", 5, RecipeTechRegistry.NONE, ingredientsFromScript("{{ironbar, 1}}")));
      var0.add(new Recipe("firearrow", 10, RecipeTechRegistry.NONE, ingredientsFromScript("{{stonearrow, 10}, {torch, 1}}")));
      var0.add(new Recipe("frostarrow", 10, RecipeTechRegistry.NONE, ingredientsFromScript("{{stonearrow, 10}, {frostshard, 1}}")));
      var0.add(new Recipe("bouncingarrow", 25, RecipeTechRegistry.NONE, ingredientsFromScript("{{stonearrow, 25, true}, {halffish, 1}}")));
      var0.add(new Recipe("poisonarrow", 50, RecipeTechRegistry.NONE, ingredientsFromScript("{{stonearrow, 50, true}, {cavespidergland, 1}}")));
      var0.add(new Recipe("simplebullet", 50, RecipeTechRegistry.NONE, ingredientsFromScript("{{ironbar, 1}}")));
      var0.add(new Recipe("bouncingbullet", 50, RecipeTechRegistry.NONE, ingredientsFromScript("{{simplebullet, 50, true}, {halffish, 1}}")));
      var0.add(new Recipe("frostbullet", 50, RecipeTechRegistry.NONE, ingredientsFromScript("{{simplebullet, 50, true}, {frostshard, 1}}")));
      var0.add(new Recipe("voidbullet", 100, RecipeTechRegistry.NONE, ingredientsFromScript("{{simplebullet, 100, true}, {voidshard, 1}}")));
      var0.add(new Recipe("snowball", 5, RecipeTechRegistry.NONE, ingredientsFromScript("{{snowtile, 1}}"), true));
      var0.add(new Recipe("snowmantrainingdummy", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{snowtile, 10}, {anystone, 4}, {carrot, 1}}"), true));
      var0.add(new Recipe("snowmantrainingdummy", 1, RecipeTechRegistry.NONE, ingredientsFromScript("{{snowball, 50}, {anystone, 4}, {carrot, 1}}"), true));
      var0.add(new Recipe("ironbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{ironore, 4}}")));
      var0.add(new Recipe("ironbar", 2, RecipeTechRegistry.FORGE, ingredientsFromScript("{{brokenirontool, 1}}"), true));
      var0.add(new Recipe("copperbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{copperore, 4}}")));
      var0.add(new Recipe("copperbar", 2, RecipeTechRegistry.FORGE, ingredientsFromScript("{{brokencoppertool, 1}}"), true));
      var0.add(new Recipe("goldbar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{goldore, 4}}")));
      var0.add(new Recipe("ivybar", 1, RecipeTechRegistry.FORGE, ingredientsFromScript("{{ivyore, 4}}"), true));
      var0.add(new Recipe("roastedfish", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{anycommonfish, 1}}")));
      var0.add(new Recipe("roastedrabbitleg", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{rabbitleg, 1}}")));
      var0.add(new Recipe("roastedduckbreast", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{duckbreast, 1}}")));
      var0.add(new Recipe("roastedfrogleg", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{frogleg, 1}}")));
      var0.add(new Recipe("roastedmutton", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{rawmutton, 1}}")));
      var0.add(new Recipe("roastedpork", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{rawpork, 1}}")));
      var0.add(new Recipe("steak", 1, RecipeTechRegistry.ROASTING_STATION, ingredientsFromScript("{{beef, 1}}")));
      var0.add(new Recipe("bread", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{flour, 2}}")));
      var0.add(new Recipe("candyapple", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{apple, 1}, {sugar, 2}}")));
      var0.add(new Recipe("popcorn", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{corn, 4}}")));
      var0.add(new Recipe("donut", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{flour, 1}, {milk, 1}, {sugar, 1}}")));
      var0.add(new Recipe("cookies", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{flour, 2}, {milk, 2}, {sugar, 2}}")));
      var0.add(new Recipe("meatballs", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{anyrawmeat, 1}, {flour, 1}, {tomato, 1}}")));
      var0.add(new Recipe("smokedfillet", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{anycommonfish, 1}, {cabbage, 1}, {chilipepper, 1}}")));
      var0.add(new Recipe("blueberrycake", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{blueberry, 5}, {flour, 1}, {sugar, 1}}")));
      var0.add(new Recipe("blackberryicecream", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{blackberry, 4}, {milk, 2}, {sugar, 2}}")));
      var0.add(new Recipe("fruitsmoothie", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{anyfruit, 4}, {milk, 2}, {coconut, 1}, {sugar, 1}}")));
      var0.add(new Recipe("fishtaco", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{anycommonfish, 1}, {corn, 2}, {cabbage, 1}, {chilipepper, 1}}")));
      var0.add(new Recipe("juniorburger", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{beef, 1}, {bread, 2}, {cabbage, 1}, {tomato, 2}}")));
      var0.add(new Recipe("cheeseburger", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{beef, 1}, {bread, 2}, {cabbage, 1}, {tomato, 2}, {cheese, 2}}")));
      var0.add(new Recipe("nachos", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{corn, 2}, {flour, 1}, {cheese, 2}, {tomato, 2}, {chilipepper, 1}}")));
      var0.add(new Recipe("eggplantparmesan", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{eggplant, 3}, {cheese, 2}, {tomato, 2}, {flour, 1}}")));
      var0.add(new Recipe("tropicalstew", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{coconut, 2}, {eggplant, 2}, {chilipepper, 1}, {corn, 1}}")));
      var0.add(new Recipe("fishandchips", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{anycommonfish, 1}, {potato, 4}, {flour, 1}, {tomato, 2}}")));
      var0.add(new Recipe("freshpotatosalad", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{potato, 4}, {cabbage, 1}, {corn, 2}, {apple, 2}}")));
      var0.add(new Recipe("hotdog", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{bread, 1}, {rawpork, 2}, {tomato, 2}, {onion, 1}}")));
      var0.add(new Recipe("ricepudding", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{rice, 4}, {milk, 2}, {sugar, 2}, {blueberry, 2}}")));
      var0.add(new Recipe("minersstew", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{mushroom, 4}, {carrot, 2}, {eggplant, 2}, {tomato, 2}}")));
      var0.add(new Recipe("sushirolls", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{rice, 4}, {anycommonfish, 1}, {eggplant, 2}, {carrot, 2}, {chilipepper, 2}}")));
      var0.add(new Recipe("friedpork", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{rawpork, 2}, {potato, 3}, {milk, 2}}")));
      var0.add(new Recipe("bananapudding", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{banana, 4}, {milk, 2}, {sugar, 2}}")));
      var0.add(new Recipe("lemontart", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{lemon, 4}, {flour, 2}, {milk, 2}, {sugar, 3}}")));
      var0.add(new Recipe("spaghettibolognese", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{flour, 2}, {beef, 2}, {tomato, 3}, {onion, 2}}")));
      var0.add(new Recipe("porktenderloin", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{rawpork, 3}, {potato, 2}, {onion, 1}, {carrot, 1}, {eggplant, 1}}")));
      var0.add(new Recipe("beefgoulash", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{beef, 2}, {tomato, 2}, {onion, 2}, {potato, 3}}")));
      var0.add(new Recipe("shishkebab", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{beef, 1}, {rawpork, 1}, {tomato, 2}, {eggplant, 2}, {onion, 1}, {chilipepper, 1}}")));
      var0.add(new Recipe("pumpkinpie", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{pumpkin, 2}, {flour, 2}, {milk, 2}, {sugar, 3}}")));
      var0.add(new Recipe("sweetlemonade", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{lemon, 5}, {sugar, 3}}")));
      var0.add(new Recipe("strawberrypie", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{strawberry, 5}, {flour, 2}, {milk, 2}, {sugar, 3}}")));
      var0.add(new Recipe("blackcoffee", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{groundcoffee, 6}}")));
      var0.add(new Recipe("cappuccino", 1, RecipeTechRegistry.COOKING_POT, ingredientsFromScript("{{groundcoffee, 8}, {milk, 1}}")));
      var0.add(new Recipe("woolcarpet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{wool, 10}}")));
      var0.add(new Recipe("leathercarpet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{leather, 10}}")));
      var0.add(new Recipe("firechalice", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{stone, 50}, {torch, 10}}")));
      var0.add(new Recipe("wallcandle", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{anylog, 1}, {honey, 1}}")));
      var0.add(new Recipe("flowerpot", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{clay, 5}}")));
      var0.add(new Recipe("barrel", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{anylog, 8}}")));
      var0.add(new Recipe("demonchest", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{demonicbar, 3}}"), true));
      var0.add(new Recipe("walltorch", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{anylog, 2}, {anysapling, 1}}")));
      var0.add(new Recipe("ironlamp", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{ironbar, 3}}")));
      var0.add(new Recipe("goldlamp", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{goldbar, 3}}")));
      var0.add(new Recipe("woodcolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("stonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{stone, 20}}")));
      var0.add(new Recipe("snowstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{snowstone, 20}}"), true));
      var0.add(new Recipe("swampstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{swampstone, 20}}"), true));
      var0.add(new Recipe("sandstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sandstone, 20}}"), true));
      var0.add(new Recipe("deepstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deepstone, 20}}"), true));
      var0.add(new Recipe("obsidiancolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{obsidian, 5}}"), true));
      var0.add(new Recipe("deepsnowstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deepsnowstone, 20}}"), true));
      var0.add(new Recipe("deepswampstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deepswampstone, 20}}"), true));
      var0.add(new Recipe("deepsandstonecolumn", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deepsandstone, 20}}"), true));
      var0.add(new Recipe("stonecandlepedestal", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{stone, 20}, {honey, 1}}")));
      var0.add(new Recipe("snowcandlepedestal", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{snowstone, 20}, {honey, 1}}")));
      var0.add(new Recipe("swampcandlepedestal", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{swampstone, 20}, {honey, 1}}")));
      var0.add(new Recipe("desertcandlepedestal", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sandstone, 20}, {honey, 1}}")));
      var0.add(new Recipe("armorstand", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{anylog, 10}}")));
      var0.add(new Recipe("trainingdummy", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{anylog, 10}, {wool, 10}}")));
      var0.add(new Recipe("oakchest", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 8}}"), true));
      var0.add(new Recipe("oakdinnertable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 16}}"), true));
      var0.add(new Recipe("oakdesk", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 8}}"), true));
      var0.add(new Recipe("oakmodulartable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 8}}"), true));
      var0.add(new Recipe("oakchair", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 4}}"), true));
      var0.add(new Recipe("oakbench", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 10}}"), true));
      var0.add(new Recipe("oakbookshelf", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 10}}"), true));
      var0.add(new Recipe("oakcabinet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 10}}"), true));
      var0.add(new Recipe("oakbed", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 10}, {wool, 10}}"), true));
      var0.add(new Recipe("oakdresser", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 8}}"), true));
      var0.add(new Recipe("oakclock", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 8}, {ironbar, 2}}"), true));
      var0.add(new Recipe("oakcandelabra", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 6}, {torch, 3}}"), true));
      var0.add(new Recipe("oakdisplay", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 10}}"), true));
      var0.add(new Recipe("oakbathtub", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 12}}"), true));
      var0.add(new Recipe("oaktoilet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{oaklog, 6}}"), true));
      var0.add(new Recipe("sprucechest", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 8}}"), true));
      var0.add(new Recipe("sprucedinnertable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 16}}"), true));
      var0.add(new Recipe("sprucedesk", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 8}}"), true));
      var0.add(new Recipe("sprucemodulartable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 8}}"), true));
      var0.add(new Recipe("sprucechair", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 4}}"), true));
      var0.add(new Recipe("sprucebench", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 10}}"), true));
      var0.add(new Recipe("sprucebookshelf", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 10}}"), true));
      var0.add(new Recipe("sprucecabinet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 10}}"), true));
      var0.add(new Recipe("sprucebed", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 10}, {wool, 10}}"), true));
      var0.add(new Recipe("sprucedresser", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 8}}"), true));
      var0.add(new Recipe("spruceclock", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 8}, {ironbar, 2}}"), true));
      var0.add(new Recipe("sprucecandelabra", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 6}, {torch, 3}}"), true));
      var0.add(new Recipe("sprucedisplay", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 10}}"), true));
      var0.add(new Recipe("sprucebathtub", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 12}}"), true));
      var0.add(new Recipe("sprucetoilet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{sprucelog, 6}}"), true));
      var0.add(new Recipe("pinechest", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 8}}"), true));
      var0.add(new Recipe("pinedinnertable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 16}}"), true));
      var0.add(new Recipe("pinedesk", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 8}}"), true));
      var0.add(new Recipe("pinemodulartable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 8}}"), true));
      var0.add(new Recipe("pinechair", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 4}}"), true));
      var0.add(new Recipe("pinebench", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 10}}"), true));
      var0.add(new Recipe("pinebookshelf", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 10}}"), true));
      var0.add(new Recipe("pinecabinet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 10}}"), true));
      var0.add(new Recipe("pinebed", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 10}, {wool, 10}}"), true));
      var0.add(new Recipe("pinedresser", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 8}}"), true));
      var0.add(new Recipe("pineclock", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 8}, {ironbar, 2}}"), true));
      var0.add(new Recipe("pinecandelabra", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 6}, {torch, 3}}"), true));
      var0.add(new Recipe("pinedisplay", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 10}}"), true));
      var0.add(new Recipe("pinebathtub", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 12}}"), true));
      var0.add(new Recipe("pinetoilet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{pinelog, 6}}"), true));
      var0.add(new Recipe("palmchest", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 8}}"), true));
      var0.add(new Recipe("palmdinnertable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 16}}"), true));
      var0.add(new Recipe("palmdesk", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 8}}"), true));
      var0.add(new Recipe("palmmodulartable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 8}}"), true));
      var0.add(new Recipe("palmchair", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 4}}"), true));
      var0.add(new Recipe("palmbench", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 10}}"), true));
      var0.add(new Recipe("palmbookshelf", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 10}}"), true));
      var0.add(new Recipe("palmcabinet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 10}}"), true));
      var0.add(new Recipe("palmbed", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 10}, {wool, 10}}"), true));
      var0.add(new Recipe("palmdresser", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 8}}"), true));
      var0.add(new Recipe("palmclock", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 8}, {ironbar, 2}}"), true));
      var0.add(new Recipe("palmcandelabra", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 6}, {torch, 3}}"), true));
      var0.add(new Recipe("palmdisplay", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 10}}"), true));
      var0.add(new Recipe("palmbathtub", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 12}}"), true));
      var0.add(new Recipe("palmtoilet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{palmlog, 6}}"), true));
      var0.add(new Recipe("deadwoodchest", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 8}}"), true));
      var0.add(new Recipe("deadwooddinnertable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 16}}"), true));
      var0.add(new Recipe("deadwooddesk", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 8}}"), true));
      var0.add(new Recipe("deadwoodmodulartable", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 8}}"), true));
      var0.add(new Recipe("deadwoodchair", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 4}}"), true));
      var0.add(new Recipe("deadwoodbench", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 10}}"), true));
      var0.add(new Recipe("deadwoodbookshelf", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 10}}"), true));
      var0.add(new Recipe("deadwoodcabinet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 10}}"), true));
      var0.add(new Recipe("deadwoodbed", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 10}, {wool, 10}}"), true));
      var0.add(new Recipe("deadwooddresser", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 8}}"), true));
      var0.add(new Recipe("deadwoodclock", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 8}, {ironbar, 2}}"), true));
      var0.add(new Recipe("deadwoodcandelabra", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 6}, {torch, 3}}"), true));
      var0.add(new Recipe("deadwooddisplay", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 10}}"), true));
      var0.add(new Recipe("deadwoodbathtub", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 12}}"), true));
      var0.add(new Recipe("deadwoodtoilet", 1, RecipeTechRegistry.CARPENTER, ingredientsFromScript("{{deadwoodlog, 6}}"), true));
      var0.add(new Recipe("minecart", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 10}, {anylog, 10}}")));
      var0.add(new Recipe("minecarttrack", 10, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 1}, {anylog, 1}}")));
      var0.add(new Recipe("wrench", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 10}}")));
      var0.add(new Recipe("cutter", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 10}}")));
      var0.add(new Recipe("wire", 10, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{copperbar, 1}}")));
      var0.add(new Recipe("woodpressureplate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anylog, 5}, {wire, 5}}")));
      var0.add(new Recipe("stonepressureplate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{stone, 10}, {wire, 5}}")));
      var0.add(new Recipe("snowstonepressureplate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{snowstone, 10}, {wire, 5}}"), true));
      var0.add(new Recipe("swampstonepressureplate", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{swampstone, 10}, {wire, 5}}"), true));
      var0.add(new Recipe("sandstonepressureplate", 1, RecipeTechRegistry.DEMONIC, ingredientsFromScript("{{sandstone, 10}, {wire, 5}}"), true));
      var0.add(new Recipe("deepstonepressureplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepstone, 10}, {wire, 5}}"), true));
      var0.add(new Recipe("deepsnowstonepressureplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepsnowstone, 10}, {wire, 5}}"), true));
      var0.add(new Recipe("deepswampstonepressureplate", 1, RecipeTechRegistry.ADVANCED_WORKSTATION, ingredientsFromScript("{{deepswampstone, 10}, {wire, 5}}"), true));
      var0.add(new Recipe("rocklever", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{anystone, 10}, {wire, 5}}")));
      var0.add(new Recipe("ledpanel", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{torch, 5}, {wire, 5}, {ironbar, 2}}")));
      var0.add(new Recipe("fireworkdispenser", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 5}, {wire, 10}}")));
      var0.add(new Recipe("andgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{copperbar, 1}, {wire, 5}}")));
      var0.add(new Recipe("nandgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{copperbar, 1}, {wire, 5}}")));
      var0.add(new Recipe("orgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{copperbar, 1}, {wire, 5}}")));
      var0.add(new Recipe("norgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{copperbar, 1}, {wire, 5}}")));
      var0.add(new Recipe("xorgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{copperbar, 1}, {wire, 5}}")));
      var0.add(new Recipe("srlatchgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{norgate, 2}, {wire, 5}}")));
      var0.add(new Recipe("tflipflopgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{norgate, 1}, {andgate, 1}, {wire, 5}}")));
      var0.add(new Recipe("countergate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 2}, {wire, 10}}")));
      var0.add(new Recipe("timergate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 2}, {wire, 10}}")));
      var0.add(new Recipe("delaygate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 2}, {wire, 10}}")));
      var0.add(new Recipe("buffergate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 2}, {wire, 10}}")));
      var0.add(new Recipe("sensorgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 2}, {wire, 10}}")));
      var0.add(new Recipe("soundgate", 1, RecipeTechRegistry.WORKSTATION, ingredientsFromScript("{{ironbar, 2}, {wire, 10}}")));
      var0.add(new Recipe("fertilizer", 1, RecipeTechRegistry.COMPOST_BIN, ingredientsFromScript("{{anycompostable, 2}}")));
      var0.add(new Recipe("groundcoffee", 1, RecipeTechRegistry.GRAIN_MILL, ingredientsFromScript("{{coffeebeans, 1}}")));
      var0.add(new Recipe("rice", 1, RecipeTechRegistry.GRAIN_MILL, ingredientsFromScript("{{riceseed, 1}}")));
      var0.add(new Recipe("flour", 1, RecipeTechRegistry.GRAIN_MILL, ingredientsFromScript("{{wheat, 1}}")));
      var0.add(new Recipe("sugar", 1, RecipeTechRegistry.GRAIN_MILL, ingredientsFromScript("{{sugarbeet, 1}}")));
      var0.add(new Recipe("sugar", 2, RecipeTechRegistry.GRAIN_MILL, ingredientsFromScript("{{honey, 1}}")));
      var0.add(new Recipe("cheese", 1, RecipeTechRegistry.CHEESE_PRESS, ingredientsFromScript("{{milk, 1}}")));
      return var0;
   }
}
