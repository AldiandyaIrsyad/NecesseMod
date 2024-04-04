package necesse.entity.levelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.bosses.SpiderEmpressMob;
import necesse.entity.particle.EmpressAcidParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class EmpressAcidGroundEvent extends GroundEffectEvent {
   private GameDamage damage;
   protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(1000);
   protected int tickCounter;
   private EmpressAcidParticle particle;
   private long lifetime;

   public EmpressAcidGroundEvent() {
   }

   public EmpressAcidGroundEvent(Mob var1, int var2, int var3, GameDamage var4, GameRandom var5) {
      super(var1, var2, var3, var5);
      this.damage = var4;
   }

   public void init() {
      super.init();
      if (this.owner instanceof SpiderEmpressMob) {
         if (((SpiderEmpressMob)this.owner).isRaging) {
            this.lifetime = (long)SpiderEmpressMob.ACID_LINGER_SECONDS_RAGE * 1000L;
         } else {
            this.lifetime = (long)SpiderEmpressMob.ACID_LINGER_SECONDS * 1000L;
         }
      }

      this.tickCounter = 0;
      if (this.isClient() && this.lifetime != 0L) {
         this.level.entityManager.addParticle(this.particle = new EmpressAcidParticle(this.level, (float)this.x, (float)this.y, this.lifetime), true, Particle.GType.CRITICAL);
         Screen.playSound(GameResources.fizz, SoundEffect.effect((float)this.x, (float)this.y).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.5F, 1.0F)));
      }

   }

   public Shape getHitBox() {
      byte var1 = 24;
      byte var2 = 24;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         this.hitCooldowns.startCooldown(var1);
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
      if ((long)this.tickCounter > 20L * (this.lifetime / 1000L)) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if ((long)this.tickCounter > 20L * (this.lifetime / 1000L)) {
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
