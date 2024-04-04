package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SandwormBody extends HostileWormMobBody<SandwormHead, SandwormBody> {
   ParticleTypeSwitcher pTypeSwitcher;
   public Point sprite;
   private TicksPerSecond particleSpawner;
   public GameDamage collisionDamage;

   public SandwormBody() {
      super(1200);
      this.pTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC});
      this.sprite = new Point(0, 0);
      this.particleSpawner = TicksPerSecond.ticksPerSecond(20);
      this.setArmor(15);
      this.collision = new Rectangle(-20, -15, 40, 30);
      this.hitBox = new Rectangle(-25, -20, 50, 40);
      this.selectBox = new Rectangle(-32, -60, 64, 64);
   }

   public void init() {
      super.init();
      if (this.getLevel() instanceof IncursionLevel) {
         this.collisionDamage = SandwormHead.bodyCollisionDamage;
      } else {
         this.collisionDamage = SandwormHead.incursionBodyCollisionDamage;
      }

   }

   public GameMessage getLocalization() {
      return new LocalMessage("mob", "sandworm");
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.collisionDamage;
   }

   public void clientTick() {
      super.clientTick();
      if (this.isVisible()) {
         this.particleSpawner.gameTick();

         while(true) {
            while(this.particleSpawner.shouldTick()) {
               ComputedValue var1 = new ComputedValue(() -> {
                  return this.getLevel().getObject(this.getX() / 32, this.getY() / 32);
               });
               if (this.height < 20.0F && (((GameObject)var1.get()).isWall || ((GameObject)var1.get()).isRock)) {
                  this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 10.0F, this.y + GameRandom.globalRandom.floatGaussian() * 7.0F + 5.0F, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(200);
               } else if (this.height < 0.0F) {
                  this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 10.0F, this.y + GameRandom.globalRandom.floatGaussian() * 7.0F + 5.0F, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(200);
               }
            }

            return;
         }
      }
   }

   public void spawnDeathParticles(float var1, float var2) {
      if (this.isVisible()) {
         for(int var3 = 0; var3 < 3; ++var3) {
            this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.sandWorm, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
         }

      }
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 32;
         int var12 = var8.getDrawY(var6);
         WormMobHead.addDrawable(var1, new GameSprite(MobRegistry.Textures.sandWorm, this.sprite.x, this.sprite.y, 64), MobRegistry.Textures.sandWorm_mask, var10, (int)this.height, var11, var12, 64);
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.sandWorm_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2;
      var7 += this.getBobbing(var1, var2);
      return var5.initDraw().light(var3).pos(var6, var7);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F)).max(0.0F));
   }
}
