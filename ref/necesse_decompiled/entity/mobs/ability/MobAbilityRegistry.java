package necesse.entity.mobs.ability;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.Mob;

public class MobAbilityRegistry {
   private final Mob mob;
   private boolean registryOpen = true;
   private ArrayList<MobAbility> abilities = new ArrayList();

   public MobAbilityRegistry(Mob var1) {
      this.mob = var1;
   }

   public void closeRegistry() {
      this.registryOpen = false;
   }

   public final void runAbility(int var1, PacketReader var2) {
      if (var1 >= 0 && var1 < this.abilities.size()) {
         ((MobAbility)this.abilities.get(var1)).executePacket(var2);
      } else {
         System.err.println("Could not find and run ability " + var1 + " for " + this.mob.toString());
      }

   }

   public final <T extends MobAbility> T registerAbility(T var1) {
      if (!this.registryOpen) {
         throw new IllegalStateException("Cannot register mob abilities after initialization, must be done in constructor");
      } else if (this.abilities.size() >= 32767) {
         throw new IllegalStateException("Cannot register any more mob abilities for " + this.mob.toString());
      } else {
         this.abilities.add(var1);
         var1.onRegister(this.mob, this.abilities.size() - 1);
         return var1;
      }
   }
}
