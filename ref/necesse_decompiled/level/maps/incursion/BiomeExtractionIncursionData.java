package necesse.level.maps.incursion;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.ExtractionIncursionEvent;
import necesse.entity.mobs.Mob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;

public class BiomeExtractionIncursionData extends BiomeMissionIncursionData {
   public BiomeExtractionIncursionData() {
   }

   public BiomeExtractionIncursionData(float var1, IncursionBiome var2, int var3) {
      super(var1, var2, var3);
   }

   public GameMessage getDisplayName() {
      return new LocalMessage("biome", "incursionextraction", "incursion", this.biome.getLocalization());
   }

   public GameMessage getIncursionMissionTypeName() {
      return new LocalMessage("biome", "extraction");
   }

   public Collection<FairType> getObjectives(IncursionData var1, FontOptions var2) {
      return (Collection)this.biome.getExtractionItems(var1).stream().map((var1x) -> {
         FairType var2x = new FairType();
         var2x.append(var2, Localization.translate("ui", "incursionextractionobjective", "ore", TypeParsers.getItemParseString(new InventoryItem(var1x))));
         var2x.applyParsers(TypeParsers.ItemIcon(var2.getSize()));
         return var2x;
      }).collect(Collectors.toList());
   }

   protected Iterable<FairType> getLoot(IncursionData var1, FontOptions var2) {
      LootList var3 = new LootList();
      Iterator var4 = this.biome.getExtractionItems(var1).iterator();

      while(var4.hasNext()) {
         Item var5 = (Item)var4.next();
         var3.add(var5);
      }

      LootTable var6 = this.biome.getBossDrop(var1);
      if (var6 != null) {
         var6.addPossibleLoot(var3);
      }

      return (Iterable)var3.streamItemsAndCustomItems().map((var2x) -> {
         return this.getItemMessage(var2x, var2);
      }).collect(Collectors.toList());
   }

   public LootTable getExtraMobDrops(Mob var1) {
      if (!var1.isSummoned && var1.isBoss()) {
         LootTable var2 = this.biome.getBossDrop(this);
         if (var2 != null) {
            return var2;
         }
      }

      return super.getExtraMobDrops(var1);
   }

   public IncursionLevel getNewIncursionLevel(LevelIdentifier var1, Server var2, WorldEntity var3) {
      IncursionLevel var4 = super.getNewIncursionLevel(var1, var2, var3);
      var4.makeServerLevel(var2);
      ExtractionIncursionEvent var5 = new ExtractionIncursionEvent(this.biome.bossMobStringID);
      var4.entityManager.addLevelEventHidden(var5);
      var4.incursionEventUniqueID = var5.getUniqueID();
      return var4;
   }
}
