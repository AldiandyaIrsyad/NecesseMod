package necesse.entity.mobs;

public class MobWasKilledEvent {
   public final Mob target;
   public final Attacker attacker;

   public MobWasKilledEvent(Mob var1, Attacker var2) {
      this.target = var1;
      this.attacker = var2;
   }
}
