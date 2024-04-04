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

public class SwampDeepCaveIncursionBiome extends IncursionBiome {
   public SwampDeepCaveIncursionBiome() {
      super("pestwarden");
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("biome", "deepcave", "biome", BiomeRegistry.getBiome("swamp").getLocalization());
   }

   public Collection<Item> getExtractionItems(IncursionData var1) {
      return Collections.singleton(ItemRegistry.getItem("myceliumore"));
   }

   public LootTable getHuntDrop(IncursionData var1) {
      return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.66F, "silk")});
   }

   public LootTable getBossDrop(final IncursionData var1) {
      return new LootTable(new LootItemInterface[]{LootItem.between("bioessence", 20, 25), new LootItemInterface() {
         public void addPossibleLoot(LootList var1x, Object... var2) {
            InventoryItem var3 = new InventoryItem("gatewaytablet");
            var3.getGndData().setInt("displayTier", SwampDeepCaveIncursionBiome.this.increaseTabletTierByX(var1.getTabletTier(), 1));
            var1x.addCustom(var3);
         }

         public void addItems(List<InventoryItem> var1x, GameRandom var2, float var3, Object... var4) {
            InventoryItem var5 = new InventoryItem("gatewaytablet");
            GatewayTabletItem.initializeGatewayTablet(var5, var2, SwampDeepCaveIncursionBiome.this.increaseTabletTierByX(var1.getTabletTier(), 1));
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
      return new SwampDeepCaveIncursionLevel(var1, var2, var4);
   }

   public ArrayList<Color> getFallenAltarGatewayColorsForBiome() {
      ArrayList var1 = new ArrayList();
      var1.add(new Color(94, 140, 50));
      var1.add(new Color(133, 255, 3));
      var1.add(new Color(95, 150, 13));
      var1.add(new Color(142, 255, 3));
      var1.add(new Color(210, 244, 152));
      var1.add(new Color(247, 253, 236));
      return var1;
   }
}
