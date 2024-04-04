package necesse.level.maps.incursion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.IncursionLevel;

public class DesertDeepCaveIncursionBiome extends IncursionBiome {
   public DesertDeepCaveIncursionBiome() {
      super("sageandgrit");
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("biome", "deepcave", "biome", BiomeRegistry.getBiome("desert").getLocalization());
   }

   public Collection<Item> getExtractionItems(IncursionData var1) {
      return Collections.singleton(ItemRegistry.getItem("ancientfossilore"));
   }

   public LootTable getHuntDrop(IncursionData var1) {
      return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.19800001F, "wormcarapace")});
   }

   public LootTable getBossDrop(final IncursionData var1) {
      return new LootTable(new LootItemInterface[]{LootItem.between("primordialessence", 20, 25), new LootItemInterface() {
         public void addPossibleLoot(LootList var1x, Object... var2) {
            InventoryItem var3 = new InventoryItem("gatewaytablet");
            var3.getGndData().setInt("displayTier", DesertDeepCaveIncursionBiome.this.increaseTabletTierByX(var1.getTabletTier(), 1));
            var1x.addCustom(var3);
         }

         public void addItems(List<InventoryItem> var1x, GameRandom var2, float var3, Object... var4) {
            InventoryItem var5 = new InventoryItem("gatewaytablet");
            GatewayTabletItem.initializeGatewayTablet(var5, var2, DesertDeepCaveIncursionBiome.this.increaseTabletTierByX(var1.getTabletTier(), 1));
            var1x.add(var5);
         }
      }});
   }

   public TicketSystemList<Supplier<IncursionData>> getAvailableIncursions(int var1) {
      TicketSystemList var2 = new TicketSystemList();
      var2.addObject(100, () -> {
         return new BiomeHuntIncursionData(1.0F, this, var1);
      });
      var2.addObject(100, () -> {
         return new BiomeExtractionIncursionData(1.0F, this, var1);
      });
      return var2;
   }

   public IncursionLevel getNewIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, Server var3, WorldEntity var4) {
      return new DesertDeepCaveIncursionLevel(var1, var2, var4);
   }

   public ArrayList<Color> getFallenAltarGatewayColorsForBiome() {
      ArrayList var1 = new ArrayList();
      var1.add(new Color(140, 122, 50));
      var1.add(new Color(255, 213, 3));
      var1.add(new Color(150, 127, 13));
      var1.add(new Color(255, 196, 3));
      var1.add(new Color(244, 232, 152));
      var1.add(new Color(253, 249, 236));
      return var1;
   }
}
