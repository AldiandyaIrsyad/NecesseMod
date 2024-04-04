package necesse.engine.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.incursion.UniqueIncursionReward;

public class UniqueIncursionRewardsRegistry extends GameRegistry<UniqueIncursionReward> {
   public static final UniqueIncursionRewardsRegistry instance = new UniqueIncursionRewardsRegistry();
   public static int closeRangeWeapons;
   public static int greatswordWeapons;
   public static int spearWeapons;
   public static int glaiveWeapons;
   public static int bowWeapons;
   public static int greatBowWeapons;
   public static int gunWeapons;
   public static int magicWeapons;
   public static int throwWeapons;
   public static int summonWeapons;
   public static int incursionCloseRangeWeapons;
   public static int incursionGreatswordWeapons;
   public static int incursionGlaiveWeapons;
   public static int incursionBowWeapons;
   public static int incursionGreatBowWeapons;
   public static int incursionMagicWeapons;
   public static int incursionThrowWeapons;
   public static int incursionSummonWeapons;
   public static int tools;
   public static int headArmors;
   public static int bodyArmors;
   public static int feetArmors;
   public static int incursionHeadArmors;
   public static int incursionBodyArmors;
   public static int incursionFeetArmors;
   public static int rareIncursionRewards;
   public static int maximumIncursionTierLevel = 3;
   public static int minimumIncursionTierLevel = 1;

   private UniqueIncursionRewardsRegistry() {
      super("UniqueIncursionReward", 32766);
   }

   public void registerCore() {
      registerCloseRangeWeapon("closerangeweapon", new UniqueIncursionReward(LootTablePresets.closeRangeWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionCloseRangeWeapon("incursioncloserangeweapon", new UniqueIncursionReward(LootTablePresets.incursionCloseRangeWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerGreatswordWeapon("greatsword", new UniqueIncursionReward(LootTablePresets.greatswordWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionGreatswordWeapon("incursiongreatsword", new UniqueIncursionReward(LootTablePresets.incursionGreatswordWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerSpearWeapon("spear", new UniqueIncursionReward(LootTablePresets.spearWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerGlaiveWeapon("glaive", new UniqueIncursionReward(LootTablePresets.glaiveWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionGlaiveWeapon("incursionglaive", new UniqueIncursionReward(LootTablePresets.incursionGlaiveWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerBowWeapon("bow", new UniqueIncursionReward(LootTablePresets.bowWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionBowWeapon("incursionbow", new UniqueIncursionReward(LootTablePresets.incursionBowWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerGreatBowWeapon("greatbow", new UniqueIncursionReward(LootTablePresets.greatBowWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionGreatBowWeapon("incursiongreatbow", new UniqueIncursionReward(LootTablePresets.incursionGreatBowWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerGunWeapon("gun", new UniqueIncursionReward(LootTablePresets.gunWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerMagicWeapon("magicweapon", new UniqueIncursionReward(LootTablePresets.magicWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionMagicWeapon("incursionmagicweapon", new UniqueIncursionReward(LootTablePresets.incursionMagicWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerThrowWeapon("throwingweapon", new UniqueIncursionReward(LootTablePresets.throwWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionThrowWeapon("incursionthrowingweapon", new UniqueIncursionReward(LootTablePresets.incursionThrowWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerSummonWeapon("summonweapon", new UniqueIncursionReward(LootTablePresets.summonWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionSummonWeapon("incursionsummonweapon", new UniqueIncursionReward(LootTablePresets.incursionSummonWeapons, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerTools("tools", new UniqueIncursionReward(LootTablePresets.tools, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerHeadArmors("headArmor", new UniqueIncursionReward(LootTablePresets.headArmor, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerBodyArmors("bodyArmor", new UniqueIncursionReward(LootTablePresets.bodyArmor, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerFeetArmors("feetArmor", new UniqueIncursionReward(LootTablePresets.feetArmor, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerIncursionHeadArmors("incursionHeadArmor", new UniqueIncursionReward(LootTablePresets.incursionHeadArmor, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerIncursionBodyArmors("incursionBodyArmor", new UniqueIncursionReward(LootTablePresets.incursionBodyArmor, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerIncursionFeetArmors("incursionFeetArmor", new UniqueIncursionReward(LootTablePresets.incursionFeetArmor, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerRareIncursionRewards("rareIncursionRewards", new UniqueIncursionReward(LootTablePresets.rareIncursionRewards, UniqueIncursionModifierRegistry.ModifierChallengeLevel.Hard));
   }

   protected void onRegister(UniqueIncursionReward var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
   }

   public static List<UniqueIncursionReward> getUniqueIncursionRewards() {
      return (List)instance.streamElements().collect(Collectors.toList());
   }

   public static int registerReward(String var0, UniqueIncursionReward var1) {
      return instance.register(var0, var1);
   }

   public static int registerCloseRangeWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      closeRangeWeapons = var2;
      return var2;
   }

   public static int registerIncursionCloseRangeWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionCloseRangeWeapons = var2;
      return var2;
   }

   public static int registerGreatswordWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      greatswordWeapons = var2;
      return var2;
   }

   public static int registerIncursionGreatswordWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionGreatswordWeapons = var2;
      return var2;
   }

   public static int registerSpearWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      spearWeapons = var2;
      return var2;
   }

   public static int registerGlaiveWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      glaiveWeapons = var2;
      return var2;
   }

   public static int registerIncursionGlaiveWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionGlaiveWeapons = var2;
      return var2;
   }

   public static int registerBowWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      bowWeapons = var2;
      return var2;
   }

   public static int registerIncursionBowWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionBowWeapons = var2;
      return var2;
   }

   public static int registerGreatBowWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      greatBowWeapons = var2;
      return var2;
   }

   public static int registerIncursionGreatBowWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionGreatBowWeapons = var2;
      return var2;
   }

   public static int registerGunWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      gunWeapons = var2;
      return var2;
   }

   public static int registerMagicWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      magicWeapons = var2;
      return var2;
   }

   public static int registerIncursionMagicWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionMagicWeapons = var2;
      return var2;
   }

   public static int registerThrowWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      throwWeapons = var2;
      return var2;
   }

   public static int registerIncursionThrowWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionThrowWeapons = var2;
      return var2;
   }

   public static int registerSummonWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      summonWeapons = var2;
      return var2;
   }

   public static int registerIncursionSummonWeapon(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionSummonWeapons = var2;
      return var2;
   }

   public static int registerTools(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      tools = var2;
      return var2;
   }

   public static int registerHeadArmors(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      headArmors = var2;
      return var2;
   }

   public static int registerBodyArmors(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      bodyArmors = var2;
      return var2;
   }

   public static int registerFeetArmors(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      feetArmors = var2;
      return var2;
   }

   public static int registerIncursionHeadArmors(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionHeadArmors = var2;
      return var2;
   }

   public static int registerIncursionBodyArmors(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionBodyArmors = var2;
      return var2;
   }

   public static int registerIncursionFeetArmors(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      incursionFeetArmors = var2;
      return var2;
   }

   public static int registerRareIncursionRewards(String var0, UniqueIncursionReward var1) {
      int var2 = registerReward(var0, var1);
      rareIncursionRewards = var2;
      return var2;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXWeaponReward(GameRandom var0, int var1) {
      UniqueIncursionReward var2 = (UniqueIncursionReward)instance.getElement((Integer)var0.getOneOf((Object[])(closeRangeWeapons, greatswordWeapons, spearWeapons, glaiveWeapons, bowWeapons, greatBowWeapons, gunWeapons, magicWeapons, throwWeapons, summonWeapons, tools)));
      ArrayList var3 = new ArrayList();
      long var4 = var0.nextLong();
      var3.add(() -> {
         InventoryItem var4x = (InventoryItem)(new GameRandom(var4)).getOneOf((List)var2.lootTable.getNewList(new GameRandom(var4), 1.0F));
         if (var4x.item instanceof BoomerangToolItem) {
            BoomerangToolItem var5 = (BoomerangToolItem)var4x.item;
            var4x.setAmount(var5.getStackSize());
         }

         var4x.item.setUpgradeTier(var4x, (float)var1);
         return var4x;
      });
      return var3;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXIncursionWeaponReward(GameRandom var0, int var1) {
      UniqueIncursionReward var2 = (UniqueIncursionReward)instance.getElement((Integer)var0.getOneOf((Object[])(incursionCloseRangeWeapons, incursionGreatswordWeapons, incursionGlaiveWeapons, incursionBowWeapons, incursionGreatBowWeapons, incursionMagicWeapons, incursionThrowWeapons, incursionSummonWeapons)));
      ArrayList var3 = new ArrayList();
      long var4 = var0.nextLong();
      var3.add(() -> {
         InventoryItem var4x = (InventoryItem)(new GameRandom(var4)).getOneOf((List)var2.lootTable.getNewList(new GameRandom(var4), 1.0F));
         if (var4x.item instanceof BoomerangToolItem) {
            BoomerangToolItem var5 = (BoomerangToolItem)var4x.item;
            var4x.setAmount(var5.getStackSize());
         }

         var4x.item.setUpgradeTier(var4x, (float)var1);
         return var4x;
      });
      return var3;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXArmorReward(GameRandom var0, int var1) {
      UniqueIncursionReward var2 = (UniqueIncursionReward)instance.getElement((Integer)var0.getOneOf((Object[])(headArmors, bodyArmors, feetArmors)));
      ArrayList var3 = new ArrayList();
      long var4 = var0.nextLong();
      var3.add(() -> {
         InventoryItem var4x = (InventoryItem)(new GameRandom(var4)).getOneOf((List)var2.lootTable.getNewList(new GameRandom(var4), 1.0F));
         var4x.item.setUpgradeTier(var4x, (float)var1);
         return var4x;
      });
      return var3;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXIncursionArmorReward(GameRandom var0, int var1) {
      UniqueIncursionReward var2 = (UniqueIncursionReward)instance.getElement((Integer)var0.getOneOf((Object[])(incursionHeadArmors, incursionBodyArmors, incursionFeetArmors)));
      ArrayList var3 = new ArrayList();
      long var4 = var0.nextLong();
      var3.add(() -> {
         InventoryItem var4x = (InventoryItem)(new GameRandom(var4)).getOneOf((List)var2.lootTable.getNewList(new GameRandom(var4), 1.0F));
         var4x.item.setUpgradeTier(var4x, (float)var1);
         return var4x;
      });
      return var3;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXArmorSetReward(GameRandom var0, int var1) {
      Item var2 = ((InventoryItem)var0.getOneOf((List)((UniqueIncursionReward)instance.getElement(headArmors)).lootTable.getNewList(var0, 1.0F))).item;
      if (var2 instanceof SetHelmetArmorItem) {
         return getTierXFullSetFromHeadArmorItem(var2, var1);
      } else {
         GameLog.warn.println(var2 + " is not a set item.");
         return null;
      }
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXIncursionArmorSetReward(GameRandom var0, int var1) {
      Item var2 = ((InventoryItem)var0.getOneOf((List)((UniqueIncursionReward)instance.getElement(incursionHeadArmors)).lootTable.getNewList(var0, 1.0F))).item;
      if (var2 instanceof SetHelmetArmorItem) {
         return getTierXFullSetFromHeadArmorItem(var2, var1);
      } else {
         GameLog.warn.println(var2 + " is not a set item.");
         return null;
      }
   }

   public static ArrayList<Supplier<InventoryItem>> getTierXFullSetFromHeadArmorItem(Item var0, int var1) {
      ArrayList var2 = new ArrayList();
      InventoryItem var3 = new InventoryItem(var0, 1, false);
      var2.add(() -> {
         var3.item.setUpgradeTier(var3, (float)var1);
         return var3;
      });
      var2.add(() -> {
         InventoryItem var2 = new InventoryItem(((SetHelmetArmorItem)var0).setChestStringID, 1);
         var2.item.setUpgradeTier(var2, (float)var1);
         return var2;
      });
      var2.add(() -> {
         InventoryItem var2 = new InventoryItem(((SetHelmetArmorItem)var0).setBootsStringID, 1);
         var2.item.setUpgradeTier(var2, (float)var1);
         return var2;
      });
      return var2;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXGatewayTablets(GameRandom var0, int var1, int var2) {
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < var2; ++var4) {
         long var5 = var0.nextLong();
         var3.add(() -> {
            InventoryItem var3 = new InventoryItem("gatewaytablet");
            GatewayTabletItem.initializeGatewayTablet(var3, new GameRandom(var5), var1);
            return var3;
         });
      }

      return var3;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomTierXRareReward(GameRandom var0, int var1) {
      InventoryItem var2 = (InventoryItem)var0.getOneOf((List)((UniqueIncursionReward)instance.getElement(rareIncursionRewards)).lootTable.getNewList(var0, 1.0F));
      return var2.item instanceof SetHelmetArmorItem ? getTierXFullSetFromHeadArmorItem(var2.item, var1) : getTierXWeaponFromInventoryItem(var1, var2);
   }

   private static ArrayList<Supplier<InventoryItem>> getTierXWeaponFromInventoryItem(int var0, InventoryItem var1) {
      ArrayList var2 = new ArrayList();
      var2.add(() -> {
         if (var1.item instanceof BoomerangToolItem) {
            BoomerangToolItem var2 = (BoomerangToolItem)var1.item;
            var1.setAmount(var2.getStackSize());
         }

         var1.item.setUpgradeTier(var1, (float)var0);
         return var1;
      });
      return var2;
   }

   public static ArrayList<Supplier<InventoryItem>> getSeededRandomRewardBasedOnModifierChallengeLevel(GameRandom var0, UniqueIncursionModifierRegistry.ModifierChallengeLevel var1, int var2) {
      TicketSystemList var3 = new TicketSystemList();
      int var4 = 10 * var2;
      int var5 = GameMath.limit(var2, minimumIncursionTierLevel, maximumIncursionTierLevel);
      int var6 = GameMath.limit(var2 + 1, minimumIncursionTierLevel, maximumIncursionTierLevel);
      int var7 = GameMath.limit(var2 - 1, minimumIncursionTierLevel, Integer.MAX_VALUE);
      switch (var1) {
         case Easy:
            var3.addObject(100, getSeededRandomTierXGatewayTablets(var0, var5, 1));
            var3.addObject(85, getSeededRandomTierXWeaponReward(var0, var2));
            var3.addObject(85, getSeededRandomTierXArmorReward(var0, var2));
            break;
         case Medium:
            var3.addObject(100, getSeededRandomTierXGatewayTablets(var0, var6, 1));
            var3.addObject(85, getSeededRandomTierXIncursionWeaponReward(var0, var2));
            var3.addObject(85, getSeededRandomTierXIncursionArmorReward(var0, var2));
            var3.addObject(60 + var4, getSeededRandomTierXArmorSetReward(var0, var7));
            var3.addObject(var4 * 2 - 20, getSeededRandomTierXRareReward(var0, 1));
            break;
         case Hard:
            var3.addObject(100, getSeededRandomTierXGatewayTablets(var0, var6, 2));
            var3.addObject(60 + var4, getSeededRandomTierXArmorSetReward(var0, var2));
            var3.addObject(60 + var4, getSeededRandomTierXIncursionArmorSetReward(var0, var7));
            var3.addObject(var4 * 3 - 20, getSeededRandomTierXRareReward(var0, 1));
      }

      if (var3.isEmpty()) {
         GameLog.warn.println("No rewards found at the same challenge level as: " + var1);
         return null;
      } else {
         return (ArrayList)var3.getRandomObject(var0);
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((UniqueIncursionReward)var1, var2, var3, var4);
   }
}
