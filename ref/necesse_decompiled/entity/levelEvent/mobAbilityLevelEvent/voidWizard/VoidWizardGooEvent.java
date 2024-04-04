package necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObjectHit;

public class VoidWizardGooEvent extends GroundEffectEvent {
   public static final int GOO_SIZE = 200;
   public static final int GOO_PARTICLES = 8;
   private int tickCounter;
   protected MobHitCooldowns hitCooldowns;
   private Rectangle hitBox;
   private GameRandom random;
   private GameDamage damage;

   public VoidWizardGooEvent() {
   }

   public VoidWizardGooEvent(Mob var1, int var2, int var3, GameRandom var4) {
      super(var1, var2, var3, var4);
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      this.hitCooldowns = new MobHitCooldowns();
      this.hitBox = new Rectangle(this.x - 100, this.y - 100, 200, 200);
      this.random = new GameRandom((long)this.ownerID);
      this.damage = new GameDamage(30.0F, 10.0F);
   }

   public Shape getHitBox() {
      return this.hitBox;
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
      if (this.tickCounter > 200) {
         this.over();
      } else {
         super.clientTick();
      }

      int var1;
      int var2;
      int var3;
      Color var4;
      int var5;
      int var6;
      int var7;
      Particle.GType var8;
      if (this.tickCounter < 60) {
         for(var1 = 0; var1 < 8; ++var1) {
            var2 = this.hitBox.x + 5 + this.random.nextInt(this.hitBox.width - 10);
            var3 = this.hitBox.y + 5 + this.random.nextInt(this.hitBox.height - 10);
            var4 = new Color(49, 39, 39);
            var5 = (int)((float)var4.getRed() + (this.random.nextFloat() - 0.5F) * 10.0F);
            var6 = (int)((float)var4.getGreen() + (this.random.nextFloat() - 0.5F) * 10.0F);
            var7 = (int)((float)var4.getBlue() + (this.random.nextFloat() - 0.5F) * 10.0F);
            var4 = new Color(Math.max(0, Math.min(255, var5)), Math.max(0, Math.min(255, var6)), Math.max(0, Math.min(255, var7)));
            var8 = var1 <= 2 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC;
            this.level.entityManager.addParticle((float)var2, (float)var3, var8).color(var4).givesLight(270.0F, 0.5F);
         }
      } else {
         for(var1 = 0; var1 < 8; ++var1) {
            var2 = this.hitBox.x + 5 + this.random.nextInt(this.hitBox.width - 10);
            var3 = this.hitBox.y + 5 + this.random.nextInt(this.hitBox.height - 10);
            var4 = new Color(50, 0, 102);
            var5 = (int)((float)var4.getRed() + (this.random.nextFloat() - 0.5F) * 10.0F);
            var6 = (int)((float)var4.getGreen() + (this.random.nextFloat() - 0.5F) * 10.0F);
            var7 = (int)((float)var4.getBlue() + (this.random.nextFloat() - 0.5F) * 10.0F);
            var4 = new Color(Math.max(0, Math.min(255, var5)), Math.max(0, Math.min(255, var6)), Math.max(0, Math.min(255, var7)));
            var8 = var1 <= 2 ? Particle.GType.CRITICAL : Particle.GType.IMPORTANT_COSMETIC;
            this.level.entityManager.addParticle((float)var2, (float)var3, var8).color(var4).givesLight(270.0F, 0.5F);
         }
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.owner != null && !this.owner.removed() && this.tickCounter <= 200) {
         super.serverTick();
      } else {
         this.over();
      }

   }
}
