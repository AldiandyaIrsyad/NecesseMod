package necesse.entity.mobs.jumping;

import necesse.entity.mobs.Mob;

public class JumpingMobStats {
   public final Mob mob;
   private int jumpAnimationTime = 500;
   private int jumpCooldown = 250;
   private float jumpStrength = 60.0F;
   public boolean jumpAnimationUseSpeedMod = true;
   public boolean jumpCooldownUseSpeedMod = true;
   public boolean jumpStrengthUseSpeedMod = true;
   long lastJumpTime;
   int lastJumpAnimationTime;

   public JumpingMobStats(Mob var1) {
      if (!(var1 instanceof JumpingMobInterface)) {
         throw new IllegalArgumentException("Mob must implement JumpingMobInterface");
      } else {
         this.mob = var1;
      }
   }

   public void setJumpAnimationTime(int var1) {
      this.jumpAnimationTime = var1;
   }

   public void setJumpCooldown(int var1) {
      this.jumpCooldown = var1;
   }

   public void setJumpStrength(float var1) {
      this.jumpStrength = var1;
   }

   private float getTimeModifier() {
      return 1.0F / this.mob.getSpeedModifier();
   }

   public int getJumpAnimationTime() {
      return this.jumpAnimationUseSpeedMod ? (int)((float)this.jumpAnimationTime * this.getTimeModifier()) : this.jumpAnimationTime;
   }

   public int getJumpCooldown() {
      return this.jumpCooldownUseSpeedMod ? (int)((float)this.jumpCooldown * this.getTimeModifier()) : this.jumpCooldown;
   }

   public float getJumpStrength() {
      return this.jumpStrengthUseSpeedMod ? (float)((int)(this.jumpStrength * this.mob.getSpeedModifier())) : this.jumpStrength;
   }
}
