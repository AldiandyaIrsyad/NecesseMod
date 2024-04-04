package necesse.level.maps.incursion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;

public class SunArenaIncursionBiome extends IncursionBiome {
   public SunArenaIncursionBiome() {
      super("sunlightchampion");
   }

   public Collection<Item> getExtractionItems(IncursionData var1) {
      return Collections.EMPTY_LIST;
   }

   public LootTable getHuntDrop(IncursionData var1) {
      return new LootTable();
   }

   public LootTable getBossDrop(final IncursionData var1) {
      return new LootTable(new LootItemInterface[]{new LootItemInterface() {
         public void addPossibleLoot(LootList var1x, Object... var2) {
            InventoryItem var3 = new InventoryItem("gatewaytablet");
            var3.getGndData().setInt("displayTier", SunArenaIncursionBiome.this.increaseTabletTierByX(var1.getTabletTier(), 1));
            var1x.addCustom(var3);
         }

         public void addItems(List<InventoryItem> var1x, GameRandom var2, float var3, Object... var4) {
            InventoryItem var5 = new InventoryItem("gatewaytablet");
            GatewayTabletItem.initializeGatewayTablet(var5, var2, SunArenaIncursionBiome.this.increaseTabletTierByX(var1.getTabletTier(), 1));
            var1x.add(var5);
         }
      }});
   }

   public ArrayList<FairType> getPrivateDropsDisplay(FontOptions var1) {
      ArrayList var2 = new ArrayList();
      FairType var3 = new FairType();
      ArrayList var4 = new ArrayList();
      InventoryItem var5 = new InventoryItem("dawnhelmet", 1);
      var5.item.setUpgradeTier(var5, 1.0F);
      var4.add(var5);
      InventoryItem var6 = new InventoryItem("dawnchestplate", 1);
      var6.item.setUpgradeTier(var6, 1.0F);
      var4.add(var6);
      InventoryItem var7 = new InventoryItem("dawnboots", 1);
      var7.item.setUpgradeTier(var7, 1.0F);
      var4.add(var7);
      var3.append(new FairItemGlyph(var1.getSize(), var4));
      var3.append(var1, " " + Localization.translate("incursion", "dawnarmorloot", "tier", (int)1));
      var2.add(var3);
      return var2;
   }

   public TicketSystemList<Supplier<IncursionData>> getAvailableIncursions(int var1) {
      TicketSystemList var2 = new TicketSystemList();
      var2.addObject(100, () -> {
         return new BiomeTrialIncursionData(1.0F, this, var1);
      });
      return var2;
   }

   public IncursionLevel getNewIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, Server var3, WorldEntity var4) {
      return new SunArenaIncursionLevel(var1, var2, var4);
   }

   public ArrayList<Color> getFallenAltarGatewayColorsForBiome() {
      ArrayList var1 = new ArrayList();
      var1.add(new Color(249, 155, 78));
      var1.add(new Color(255, 233, 73));
      var1.add(new Color(249, 155, 78));
      var1.add(new Color(255, 188, 78));
      var1.add(new Color(255, 233, 73));
      var1.add(new Color(245, 247, 250));
      return var1;
   }
}
