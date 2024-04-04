package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class SunlightOrbEvent extends GroundEffectEvent {
   private long startDelay = 1000L;
   private MobHitCooldowns hitCooldowns;
   private GameDamage damage = new GameDamage(90.0F);
   private float resilienceGain;
   private int tickCounter;
   private long startTime;
   private boolean isActive;
   protected ParticleTypeSwitcher particleTypeSwitcher;

   public SunlightOrbEvent() {
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
   }

   public SunlightOrbEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5, long var6) {
      super(var1, var2, var3, var4);
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
      this.damage = var5;
      this.startDelay = var6;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextLong(this.startDelay);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startDelay = var1.getNextLong();
   }

   public void init() {
      super.init();
      this.hitCooldowns = new MobHitCooldowns();
      this.startTime = this.level.getWorldEntity().getTime();
      this.isActive = false;

      for(int var1 = 0; var1 < 20; ++var1) {
         int var2 = (int)(18.0F * (float)var1);
         float var3 = 100.0F;
         if (this.isClient()) {
            this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin((float)var2) * var3, (float)this.y + GameMath.cos((float)var2) * var3, Particle.GType.CRITICAL).color(new Color(249, 155, 78)).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).height(0.0F).movesFriction(-GameMath.sin((float)var2) * 100.0F, -GameMath.cos((float)var2) * 100.0F, 0.8F).lifeTime((int)this.startDelay + 250).sizeFades(50, 50);
         }
      }

   }

   public Shape getHitBox() {
      short var1 = 180;
      short var2 = 136;
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
         if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0F;
         }
      }

   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.tickCounter > 200) {
         this.over();
      } else {
         super.clientTick();
      }

      if (this.level.getWorldEntity().getTime() - this.startTime > this.startDelay) {
         if (!this.isActive) {
            Screen.playSound(GameResources.firespell1, SoundEffect.globalEffect().volume(0.5F).pitch(1.0F));
            this.isActive = true;
         }

         float var10001;
         int var1;
         for(var1 = 0; var1 < 4; ++var1) {
            var10001 = (float)this.x;
            this.getLevel().entityManager.addTopParticle(var10001, (float)this.y, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(30, 40).rotates().movesFrictionAngle((float)(this.tickCounter * 10 + var1 * 90), 85.0F, 0.8F).color((var0, var1x, var2, var3) -> {
               float var4 = Math.max(0.0F, Math.min(1.0F, var3));
               var0.color(new Color((int)(255.0F - 55.0F * var4), (int)(225.0F - 200.0F * var4), (int)(155.0F - 125.0F * var4)));
            }).givesLight(50.0F, 1.0F).fadesAlphaTime(100, 50).lifeTime(1000);
         }

         for(var1 = 0; var1 < 4; ++var1) {
            var10001 = (float)this.x;
            this.getLevel().entityManager.addTopParticle(var10001, (float)this.y, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(20, 30).rotates().movesFrictionAngle((float)(this.tickCounter * 15 - var1 * 90), 35.0F, 0.8F).color(new Color(255, 233, 73)).givesLight(50.0F, 1.0F).fadesAlphaTime(100, 50).lifeTime(500);
         }
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.tickCounter > 200) {
         this.over();
      } else {
         super.serverTick();
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      if (!this.canDamageAnythingYet()) {
         return false;
      } else {
         return super.canHit(var1) && this.hitCooldowns.canHit(var1);
      }
   }

   public boolean canHit(LevelObjectHit var1) {
      return !this.canDamageAnythingYet() ? false : super.canHit(var1);
   }

   public boolean canDamageAnythingYet() {
      return this.level.getWorldEntity().getTime() >= this.startTime + this.startDelay + 100L;
   }

   public void over() {
      super.over();
   }
}
