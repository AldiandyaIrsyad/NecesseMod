package necesse.entity.projectile.boomerangProjectile;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;

public abstract class BoomerangProjectile extends Projectile {
   protected int soundTimer;
   protected long spawnTime;

   public BoomerangProjectile() {
      this.isBoomerang = true;
   }

   public void init() {
      super.init();
      if (this.getOwner() == null) {
         this.remove();
      }

      this.returningToOwner = false;
      this.spawnTime = this.getWorldEntity().getTime();
      this.trailOffset = 0.0F;
   }

   protected void spawnDeathParticles() {
   }

   public void clientTick() {
      super.clientTick();
      --this.soundTimer;
      if (this.soundTimer <= 0) {
         this.soundTimer = 5;
         this.playMoveSound();
      }

   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }

   public void playMoveSound() {
      Screen.playSound(GameResources.swing2, SoundEffect.effect(this));
   }
}
