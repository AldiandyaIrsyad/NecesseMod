package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
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
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenDragonBody extends BossWormMobBody<FallenDragonHead, FallenDragonBody> {
   public int shadowSprite = 0;
   public int spriteY;
   public boolean spawnsParticles;
   public TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(10);

   public FallenDragonBody() {
      super(1000);
      this.isSummoned = true;
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-25, -20, 50, 40);
      this.selectBox = new Rectangle(-32, -80, 64, 84);
   }

   public GameMessage getLocalization() {
      FallenDragonHead var1 = (FallenDragonHead)this.master.get(this.getLevel());
      return (GameMessage)(var1 != null ? var1.getLocalization() : new StaticMessage("flyingspiritsbody"));
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return FallenWizardMob.dragonBodyDamage;
   }

   public boolean canCollisionHit(Mob var1) {
      return this.height < 45.0F && super.canCollisionHit(var1);
   }

   public void clientTick() {
      super.clientTick();
      if (this.spawnsParticles && this.isVisible()) {
         this.particleSpawner.gameTick();

         while(this.particleSpawner.shouldTick()) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 10.0F + 5.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).sizeFades(15, 25).color(new Color(51, 46, 59)).heightMoves(this.height + 10.0F, this.height + GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(350);
         }
      }

   }

   public int getFlyingHeight() {
      return 20;
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.POISON_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FROST_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F)).max(0.0F));
   }

   public void spawnDeathParticles(float var1, float var2) {
      if (this.isVisible()) {
         for(int var3 = 0; var3 < 4; ++var3) {
            this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.fallenWizardDragon, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
         }

      }
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 32;
         int var12 = var8.getDrawY(var6);
         if (this.next != null) {
            Point2D.Float var13 = new Point2D.Float(((FallenDragonBody)this.next).x - (float)var5, ((FallenDragonBody)this.next).y - ((FallenDragonBody)this.next).height - ((float)var6 - this.height));
            float var14 = GameMath.fixAngle(GameMath.getAngle(var13));
            MobDrawable var15 = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.fallenWizardDragon, 0, this.spriteY, 64), (GameTexture)null, var10, (int)this.height, var14, var11, var12, 96);
            var3.add(var15);
         }

         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.swampGuardian_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(this.shadowSprite, 0, var6).light(var3).pos(var7, var8);
   }
}
