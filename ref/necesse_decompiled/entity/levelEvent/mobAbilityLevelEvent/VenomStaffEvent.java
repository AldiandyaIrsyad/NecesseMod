package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.particle.CaveSpiderSpitParticle;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObjectHit;

public class VenomStaffEvent extends GroundEffectEvent {
   protected int tickCounter;
   protected int hitCounter;
   protected MobHitCooldowns hitCooldowns;
   protected GameDamage damage;
   protected float resilienceGain;
   private CaveSpiderSpitParticle particle;

   public VenomStaffEvent() {
   }

   public VenomStaffEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5, float var6) {
      super(var1, var2, var3, var4);
      this.damage = var5;
      this.resilienceGain = var6;
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      this.hitCooldowns = new MobHitCooldowns();
      if (this.isClient()) {
         this.level.entityManager.addParticle((Particle)(this.particle = new CaveSpiderSpitParticle(this.level, (float)this.x, (float)this.y, 5000L, GiantCaveSpiderMob.Variant.NORMAL)), Particle.GType.CRITICAL);
      }

   }

   public Shape getHitBox() {
      byte var1 = 40;
      byte var2 = 30;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
      ++this.hitCounter;
      if (this.hitCounter >= 12) {
         this.over();
      }

   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         this.hitCooldowns.startCooldown(var1);
         if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0F;
         }

         ++this.hitCounter;
         if (this.hitCounter >= 12) {
            this.over();
         }
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
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

   public void over() {
      super.over();
      if (this.particle != null) {
         this.particle.despawnNow();
      }

   }
}
