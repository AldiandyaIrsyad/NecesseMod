package necesse.level.maps.levelData.settlementData.settler;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class FarmerSettler extends Settler {
   public FarmerSettler() {
      super("farmerhuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.biomes_visited.stream().filter((var0) -> {
         return (Integer)var0.getValue() > 0;
      }).map((var0) -> {
         return BiomeRegistry.getBiome((String)var0.getKey());
      }).anyMatch(Biome::hasVillage);
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "foundinvillagetip");
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      if (var2 || !this.doesSettlementHaveThisSettler(var1)) {
         var3.addObject(100, this.getNewRecruitMob(var1));
      }

   }
}
