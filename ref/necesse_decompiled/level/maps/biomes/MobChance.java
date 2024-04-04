package necesse.level.maps.biomes;

public abstract class MobChance implements MobSpawnTable.CanSpawnPredicate, MobSpawnTable.MobProducer {
   public final int tickets;

   public MobChance(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Tickets must be above 0");
      } else {
         this.tickets = var1;
      }
   }
}
