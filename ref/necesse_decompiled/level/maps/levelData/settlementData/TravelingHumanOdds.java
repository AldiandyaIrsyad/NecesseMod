package necesse.level.maps.levelData.settlementData;

public abstract class TravelingHumanOdds {
   public final String identifier;

   public TravelingHumanOdds(String var1) {
      this.identifier = var1;
   }

   public abstract boolean canSpawn(SettlementLevelData var1);

   public abstract int getTickets(SettlementLevelData var1);

   public abstract TravelingHumanArrive getNewHuman(SettlementLevelData var1);
}
