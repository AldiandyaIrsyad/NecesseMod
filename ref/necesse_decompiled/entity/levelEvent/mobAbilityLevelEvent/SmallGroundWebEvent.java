package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmallGroundWebParticle;
import necesse.level.maps.LevelObjectHit;

public class SmallGroundWebEvent extends GroundEffectEvent {
   protected int tickCounter;
   protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(750);

   public SmallGroundWebEvent() {
   }

   public SmallGroundWebEvent(Mob var1, int var2, int var3, GameRandom var4) {
      super(var1, var2, var3, var4);
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      if (this.isClient()) {
         this.level.entityManager.addParticle((Particle)(new SmallGroundWebParticle(this.level, (float)this.x, (float)this.y, 5000L)), Particle.GType.CRITICAL);
      }

   }

   public Shape getHitBox() {
      byte var1 = 24;
      byte var2 = 24;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void clientHit(Mob var1) {
      this.hitCooldowns.startCooldown(var1);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, var1, 1.0F, this.owner), true);
         this.hitCooldowns.startCooldown(var1);
      }

   }

   public void hitObject(LevelObjectHit var1) {
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && this.hitCooldowns.canHit(var1);
   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.tickCounter > 100) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.tickCounter > 100) {
         this.over();
      } else {
         super.serverTick();
      }

   }
}
