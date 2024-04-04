package necesse.level.maps.incursion;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.TrialIncursionEvent;
import necesse.entity.mobs.Mob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;

public class BiomeTrialIncursionData extends BiomeMissionIncursionData {
   public BiomeTrialIncursionData() {
   }

   public BiomeTrialIncursionData(float var1, IncursionBiome var2, int var3) {
      super(var1, var2, var3);
   }

   public GameMessage getDisplayName() {
      return new LocalMessage("biome", "incursiontrial", "incursion", this.biome.getLocalization());
   }

   public GameMessage getIncursionMissionTypeName() {
      return new LocalMessage("biome", "trial");
   }

   public Collection<FairType> getObjectives(IncursionData var1, FontOptions var2) {
      return Collections.singleton((new FairType()).append(var2, Localization.translate("ui", "incursiontrialobjective")));
   }

   protected Iterable<FairType> getLoot(IncursionData var1, FontOptions var2) {
      LootList var3 = new LootList();
      LootTable var4 = this.biome.getBossDrop(var1);
      if (var4 != null) {
         var4.addPossibleLoot(var3);
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
      TrialIncursionEvent var5 = new TrialIncursionEvent(this.biome.bossMobStringID);
      var4.entityManager.addLevelEventHidden(var5);
      var4.incursionEventUniqueID = var5.getUniqueID();
      return var4;
   }
}
