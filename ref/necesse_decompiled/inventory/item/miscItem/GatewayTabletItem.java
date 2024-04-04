package necesse.inventory.item.miscItem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDIncursionDataItem;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class GatewayTabletItem extends Item implements UpgradableItem, SalvageableItem {
   public GatewayTabletItem() {
      super(1);
      this.rarity = Item.Rarity.UNIQUE;
      this.setItemCategory(new String[]{"misc"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      IncursionData var5 = getIncursionData(var1);
      Color var6 = new Color(231, 97, 16);
      Color var7 = new Color(184, 69, 227);
      int var8 = var1.getGndData().getInt("displayTier", Integer.MIN_VALUE);
      if (var8 == Integer.MIN_VALUE && var5 != null) {
         var8 = var5.getTabletTier();
      }

      String var9 = Localization.translate("item", "tier", "tiernumber", (Object)var8);
      var4.add((Object)(new StringTooltips(var9, var7)));
      if (var5 != null) {
         if (!var3.getBoolean("hideModifierAndRewards")) {
            Iterator var10 = var5.getUniqueIncursionModifiers().iterator();

            while(var10.hasNext()) {
               UniqueIncursionModifier var11 = (UniqueIncursionModifier)var10.next();
               String var12 = Localization.translate("ui", "incursionmodifier" + var11.getStringID());
               var4.add((Object)(new StringTooltips(var12, var6)));
            }

            ArrayList var13 = this.getTooltipTextFromRewards(var5.playerPersonalIncursionCompleteRewards);
            ArrayList var14 = this.getTooltipTextFromRewards(var5.playerSharedIncursionCompleteRewards);
            var4.addAll(var13);
            var4.addAll(var14);
         } else {
            var4.add((Object)(new StringTooltips(Localization.translate("ui", "incursionrandommodifier"), var6)));
            var4.add((Object)(new StringTooltips(Localization.translate("ui", "incursionrandomreward"), var6)));
         }
      } else {
         var4.add((Object)(new StringTooltips(Localization.translate("ui", "incursionrandommodifier"), var6)));
         var4.add((Object)(new StringTooltips(Localization.translate("ui", "incursionrandomreward"), var6)));
      }

      var4.add((String)Localization.translate("itemtooltip", "gatewaytablettip"), 400);
      return var4;
   }

   public ArrayList<StringTooltips> getTooltipTextFromRewards(ArrayList<ArrayList<Supplier<InventoryItem>>> var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ArrayList var4 = (ArrayList)var3.next();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Supplier var6 = (Supplier)var5.next();
            InventoryItem var7 = (InventoryItem)var6.get();
            String var9;
            if (var7.item instanceof GatewayTabletItem) {
               IncursionData var8 = getIncursionData(var7);
               if (var8 != null) {
                  var9 = Localization.translate("item", "tier", "tiernumber", (Object)var8.getTabletTier());
                  String var10 = var9 + " " + var8.getIncursionBiome().displayName.translate();
                  var2.add(new StringTooltips(var10, var7.item.getRarityColor(var7)));
               }
            } else {
               String var11 = Localization.translate("item", "tier", "tiernumber", (Object)((int)var7.item.getUpgradeTier(var7)));
               var9 = var11 + " " + Localization.translate("item", var7.item.getStringID());
               var2.add(new StringTooltips(var9, var7.item.getRarityColor(var7)));
            }
         }
      }

      return var2;
   }

   protected ListGameTooltips getCraftingMatTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      return new ListGameTooltips();
   }

   public GameMessage getLocalization(InventoryItem var1) {
      String var2 = var1.getGndData().getString("recipeBiome");
      if (var2 != null) {
         IncursionBiome var3 = IncursionBiomeRegistry.getBiome(var2);
         if (var3 != null) {
            return new LocalMessage("item", "formattablet", new Object[]{"biome", var3.displayName, "tablet", (new LocalMessage("item", "tablet")).translate()});
         }
      }

      IncursionData var4 = getIncursionData(var1);
      return var4 != null ? new LocalMessage("item", "formattablet", new Object[]{"biome", var4.getDisplayName(), "tablet", (new LocalMessage("item", "tablet")).translate()}) : new LocalMessage("item", "gatewaytablet");
   }

   public InventoryItem getDefaultItem(PlayerMob var1, int var2) {
      InventoryItem var3 = super.getDefaultItem(var1, var2);
      setIncursionData(var3, new BiomeExtractionIncursionData(1.0F, IncursionBiomeRegistry.getBiome("forestcave"), 1));
      return var3;
   }

   public InventoryItem getDefaultLootItem(GameRandom var1, int var2) {
      InventoryItem var3 = super.getDefaultLootItem(var1, var2);
      initializeGatewayTablet(var3, var1, 1);
      return var3;
   }

   public void addDefaultItems(List<InventoryItem> var1, PlayerMob var2) {
      boolean var3 = false;

      for(int var4 = 1; var4 <= 10; ++var4) {
         Iterator var5 = IncursionBiomeRegistry.getBiomes().iterator();

         while(var5.hasNext()) {
            IncursionBiome var6 = (IncursionBiome)var5.next();
            TicketSystemList var7 = var6.getAvailableIncursions(var4);

            for(Iterator var8 = var7.getAll().iterator(); var8.hasNext(); var3 = true) {
               Supplier var9 = (Supplier)var8.next();
               InventoryItem var10 = new InventoryItem(this);
               setIncursionData(var10, (IncursionData)var9.get());
               var1.add(var10);
            }
         }
      }

      if (!var3) {
         super.addDefaultItems(var1, var2);
      }

   }

   public static void setIncursionData(InventoryItem var0, IncursionData var1) {
      if (var1 == null) {
         var0.getGndData().setItem("incursionData", (GNDItem)null);
      } else {
         var0.getGndData().setItem("incursionData", new GNDIncursionDataItem(var1));
      }

   }

   public static IncursionData getIncursionData(InventoryItem var0) {
      GNDItem var1 = var0.getGndData().getItem("incursionData");
      return var1 instanceof GNDIncursionDataItem ? ((GNDIncursionDataItem)var1).incursionData : null;
   }

   public static void initializeGatewayTablet(InventoryItem var0, GameRandom var1, int var2) {
      if (var0.item instanceof GatewayTabletItem) {
         IncursionBiome var3 = getRandomIncursionBasedOnTier(var1, var2);
         initializeCustomGateTablet(var0, var1, var2, var3);
      } else {
         GameLog.warn.println("Inventory item generated is not a gateway tablet. Can't generate tablet data.");
      }

   }

   public static void initializeCustomGateTablet(InventoryItem var0, GameRandom var1, int var2, IncursionBiome var3) {
      if (var0.item instanceof GatewayTabletItem) {
         IncursionData var4 = var3.getRandomIncursion(var1, var2);
         setIncursionData(var0, var4);
      } else {
         GameLog.warn.println("Inventory item generated is not a gateway tablet. Can't generate tablet data.");
      }

   }

   public static IncursionBiome getRandomIncursionBasedOnTier(GameRandom var0, int var1) {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      Iterator var4 = IncursionBiomeRegistry.getBiomes().iterator();

      IncursionBiome var5;
      while(var4.hasNext()) {
         var5 = (IncursionBiome)var4.next();
         if (IncursionBiomeRegistry.getBiomeTier(var5.getID()) <= var1) {
            if (IncursionBiomeRegistry.getBiomeTier(var5.getID()) == var1) {
               var3.add(var5);
            } else {
               var2.add(var5);
            }
         }
      }

      ArrayList var6;
      if (!var2.isEmpty() && !var3.isEmpty()) {
         boolean var7 = var0.getChance(0.65F);
         if (var7) {
            var6 = var3;
         } else {
            var6 = var2;
         }
      } else if (!var2.isEmpty()) {
         var6 = var2;
      } else {
         var6 = var3;
      }

      if (!var6.isEmpty()) {
         var5 = (IncursionBiome)var6.get(var0.getIntBetween(0, var6.size() - 1));
      } else {
         GameLog.warn.println("Incursion list did not contain any incursions. No incursions with the tier: " + var1 + " was found.Generating default forest cave incursion");
         var5 = IncursionBiomeRegistry.getBiome("forestcave");
      }

      return var5;
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      String var3 = var1.getGndData().getString("recipeBiome");
      if (var3 != null) {
         IncursionBiome var4 = IncursionBiomeRegistry.getBiome(var3);
         if (var4 != null) {
            return var4.getTabletSprite();
         }
      }

      IncursionData var6 = getIncursionData(var1);
      if (var6 != null) {
         GameSprite var5 = var6.getTabletSprite();
         if (var5 != null) {
            return var5;
         }
      }

      return super.getItemSprite(var1, var2);
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "incursionData");
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      IncursionData var2 = getIncursionData(var1);
      if (var2 == null) {
         return Localization.translate("ui", "itemnotupgradable");
      } else {
         return var2.getTabletTier() >= 4 ? Localization.translate("ui", "itemupgradelimit") : null;
      }
   }

   public void addUpgradeStatTips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, Mob var5) {
      IncursionData var6 = getIncursionData(var3);
      IncursionData var7 = getIncursionData(var2);
      if (var6 != null && var7 != null) {
         DoubleItemStatTip var8 = (new LocalMessageDoubleItemStatTip("item", "tier", "tiernumber", (double)var6.getTabletTier(), 2)).setCompareValue((double)var7.getTabletTier());
         var1.add(Integer.MIN_VALUE, var8);
         var1.add(Integer.MAX_VALUE, new ItemStatTip() {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("ui", "tabletupgradetip");
            }
         });
      }

   }

   public UpgradedItem getUpgradedItem(InventoryItem var1) {
      InventoryItem var2 = var1.copy();
      IncursionData var3 = getIncursionData(var2);
      if (var3 != null) {
         int var4 = var3.getTabletTier() + 1;
         var3.setTabletTier(var4);
         int var5 = Math.max(1, var4 - 1) * 25;
         return new UpgradedItem(var1, var2, new Ingredient[]{new Ingredient("upgradeshard", var5)});
      } else {
         return null;
      }
   }

   public String getCanBeSalvagedError(InventoryItem var1) {
      IncursionData var2 = getIncursionData(var1);
      return var2 == null ? Localization.translate("ui", "itemnotsalvageable") : null;
   }

   public Collection<InventoryItem> getSalvageRewards(InventoryItem var1) {
      IncursionData var2 = getIncursionData(var1);
      int var3 = var2 == null ? 1 : var2.getTabletTier();
      int var4 = var1.getAmount() * var3 * 5;
      return Collections.singleton(new InventoryItem("upgradeshard", var4));
   }

   public int compareToSameItem(InventoryItem var1, InventoryItem var2) {
      IncursionData var3 = getIncursionData(var1);
      if (var3 == null) {
         return 1;
      } else {
         IncursionData var4 = getIncursionData(var2);
         if (var4 == null) {
            return -1;
         } else {
            int var5 = Integer.compare(var3.getTabletTier(), var4.getTabletTier());
            return var5 != 0 ? var5 : super.compareToSameItem(var1, var2);
         }
      }
   }
}
