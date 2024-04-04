package necesse.entity.mobs.friendly;

import necesse.entity.mobs.AttackAnimMob;

public class FriendlyMob extends AttackAnimMob {
   public FriendlyMob(int var1) {
      super(var1);
      this.isHostile = false;
   }
}
