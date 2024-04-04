package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.particle.Particle;

public class VoidWizardExplosionEvent extends ExplosionEvent implements Attacker {
   public VoidWizardExplosionEvent() {
      this(0.0F, 0.0F, (Mob)null);
   }

   public VoidWizardExplosionEvent(float var1, float var2, Mob var3) {
      super(var1, var2, VoidWizard.homingExplosionRange, VoidWizard.homingExplosion, false, 0, var3);
      this.sendCustomData = false;
      this.sendOwnerData = true;
      this.hitsOwner = false;
      this.knockback = 50;
   }

   protected void playExplosionEffects() {
   }

   public void spawnExplosionParticle(float var1, float var2, float var3, float var4, int var5, float var6) {
      this.level.entityManager.addParticle(var1, var2, Particle.GType.CRITICAL).movesConstant(var3, var4).color(new Color(50, 0, 102)).height(10.0F).givesLight(270.0F, 0.5F).lifeTime(var5);
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("voidwiz", 4);
   }
}
