package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class DawnSwirlEvent extends GroundEffectEvent {
   private int particleBuffer;
   private final int particleThreshold = 3;
   private final float secondsDuration = 0.6F;
   private int tickCounter;
   private MobHitCooldowns hitCooldowns;
   private GameDamage damage;
   protected ParticleTypeSwitcher particleTypeSwitcher;

   public DawnSwirlEvent() {
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
   }

   public DawnSwirlEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5) {
      super(var1, var2, var3, var4);
      this.particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
      this.damage = var5;
   }

   public Shape getHitBox() {
      short var1 = 180;
      short var2 = 136;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void init() {
      super.init();
      this.hitCooldowns = new MobHitCooldowns();
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE.getID(), var1, 5000, this.owner), true);
         this.hitCooldowns.startCooldown(var1);
      }

   }

   public void clientTick() {
      ++this.tickCounter;
      if ((float)this.tickCounter > 12.0F) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if ((float)this.tickCounter > 12.0F) {
         this.over();
      } else {
         super.serverTick();
      }

   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      this.x = (int)this.owner.x;
      this.y = (int)this.owner.y;
   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && this.hitCooldowns.canHit(var1);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, LevelDrawUtils.DrawArea var4, Level var5, TickManager var6, GameCamera var7) {
      if (this.particleBuffer > 3) {
         if (this.isClient()) {
            GameRandom var8 = GameRandom.globalRandom;
            AtomicReference var9 = new AtomicReference(var8.nextFloat() * 360.0F);
            float var10 = 150.0F;

            for(int var11 = 0; var11 < 1; ++var11) {
               this.owner.getLevel().entityManager.addParticle(this.owner.x + GameMath.sin((Float)var9.get()) * var10 + (float)var8.getIntBetween(-5, 5), this.owner.y + GameMath.cos((Float)var9.get()) * var10 + (float)var8.getIntBetween(-5, 5) * 0.85F, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(var8.getIntBetween(0, 4), 0, 12)).height(0.0F).moves((var3x, var4x, var5x, var6x, var7x) -> {
                  float var8 = (Float)var9.accumulateAndGet(var4x * 150.0F / 250.0F, Float::sum);
                  float var9x = var10 * var7x * 0.85F;
                  var3x.x = this.owner.x + GameMath.sin(var8) * var10 * var7x;
                  var3x.y = this.owner.y + GameMath.cos(var8) * var9x * 0.85F;
               }).color((var0, var1x, var2x, var3x) -> {
                  float var4 = Math.max(0.0F, Math.min(1.0F, var3x));
                  var0.color(new Color((int)(255.0F - 55.0F * var4), (int)(225.0F - 200.0F * var4), (int)(155.0F - 125.0F * var4)));
               }).lifeTime(1000).sizeFades(50, 24);
            }
         }

         this.particleBuffer -= 3;
      } else {
         ++this.particleBuffer;
      }

   }
}
