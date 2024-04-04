package necesse.entity.mobs.friendly.critters;

import necesse.entity.mobs.jumping.JumpingMobInterface;
import necesse.entity.mobs.jumping.JumpingMobStats;

public class CritterJumpingMob extends CritterMob implements JumpingMobInterface {
   protected JumpingMobStats jumpStats = new JumpingMobStats(this);

   public CritterJumpingMob() {
   }

   protected void calcAcceleration(float var1, float var2, float var3, float var4, float var5) {
      boolean var6 = this.inLiquid();
      if (var6) {
         super.calcAcceleration(var1, var2, var3, var4, var5);
      } else {
         this.tickJump(var3, var4);
         super.calcAcceleration(var1, var2, 0.0F, 0.0F, var5);
      }

   }

   public JumpingMobStats getJumpStats() {
      return this.jumpStats;
   }
}
