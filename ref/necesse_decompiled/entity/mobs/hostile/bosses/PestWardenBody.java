package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PestWardenBody extends BossWormMobBody<PestWardenHead, PestWardenBody> {
   public Point sprite = new Point(0, 0);
   public boolean showLegs;
   public int shadowSprite = 0;
   public int index;
   private TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(30);
   public GameDamage collisionDamage;

   public PestWardenBody() {
      super(1000);
      this.isSummoned = true;
      this.collision = new Rectangle(-35, -20, 70, 40);
      this.hitBox = new Rectangle(-40, -25, 80, 50);
      this.selectBox = new Rectangle(-50, -80, 100, 100);
   }

   public GameMessage getLocalization() {
      return new LocalMessage("mob", "pestwarden");
   }

   public void init() {
      super.init();
      if (this.getLevel() instanceof IncursionLevel) {
         this.collisionDamage = PestWardenHead.baseBodyCollisionDamage;
      } else {
         this.collisionDamage = PestWardenHead.incursionBodyCollisionDamage;
      }

   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
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
                  this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 10.0F + 5.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(200);
               } else if (this.height < 0.0F) {
                  this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 10.0F + 5.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(200);
               }
            }

            return;
         }
      }
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), (new ModifierValue(BuffModifiers.POISON_DAMAGE, 1.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 1.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FROST_DAMAGE, 1.0F)).max(0.2F));
   }

   public float getIncomingDamageModifier() {
      return this.moveLine != null && this.moveLine.object instanceof PestWardenMoveLine ? super.getIncomingDamageModifier() * (((PestWardenMoveLine)this.moveLine.object).isHardened ? 0.1F : 1.0F) : super.getIncomingDamageModifier();
   }

   public void spawnDeathParticles(float var1, float var2) {
      if (this.isVisible()) {
         for(int var3 = 0; var3 < 4; ++var3) {
            this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.pestWarden, 8 + GameRandom.globalRandom.nextInt(6), 4, 64, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
         }

      }
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }

   protected void addExtraDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, final int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addExtraDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 64;
         int var12 = var8.getDrawY(var6);
         Point2D.Float var13 = null;
         if (this.moveLine != null) {
            ComputedObjectValue var14 = WormMobHead.moveDistance(this.moveLine, -40.0);
            if (var14.object != null) {
               Point2D.Double var15 = WormMobHead.linePos(var14);
               var13 = GameMath.normalize((float)var15.x - (float)var5, (float)var15.y - (float)var6);
            }
         }

         if (var13 == null) {
            PestWardenHead var28 = (PestWardenHead)this.master.get(this.getLevel());
            if (var28 != null) {
               var13 = GameMath.normalize(var28.dx, var28.dy);
            } else {
               var13 = GameMath.normalize(this.dx, this.dy);
            }
         }

         float var29 = 0.7F;
         int var30;
         if (Math.abs(var13.x) - Math.abs(var13.y) <= var29) {
            var30 = var13.y < 0.0F ? 0 : 2;
         } else {
            var30 = var13.x < 0.0F ? 3 : 1;
         }

         final MobDrawable var16 = WormMobHead.getDrawable(new GameSprite(MobRegistry.Textures.pestWarden, this.sprite.x, this.sprite.y, 128), MobRegistry.Textures.pestWarden_mask, var10, (int)this.height, var11, var12, 112);
         final DrawOptions var17;
         if (this.showLegs) {
            PestWardenHead var18 = (PestWardenHead)this.master.get(this.getLevel());
            float var19 = var18 != null ? var18.getSpeed() : 100.0F;
            int var20 = (int)(400.0F * (100.0F / var19));
            int var21 = GameUtils.getAnim(this.getWorldEntity().getLocalTime(), 8, var20);
            var21 = (var21 + this.index) % 8;
            int var22 = (var21 + 1) % 8;
            float var23 = PestWardenHead.lengthPerBodyPart / 4.0F;
            int var24 = (int)(var13.x * var23);
            int var25 = (int)(var13.y * var23);
            TextureDrawOptionsEnd var26 = MobRegistry.Textures.pestWarden.initDraw().sprite(var21, 3 + var30, 128).light(var10).pos(var11 + var24, var12 - 96 - 16 + 8 + var25);
            TextureDrawOptionsEnd var27 = MobRegistry.Textures.pestWarden.initDraw().sprite(var22, 3 + var30, 128).light(var10).pos(var11 - var24, var12 - 96 - 16 + 8 - var25);
            var17 = () -> {
               var26.draw();
               var27.draw();
            };
         } else {
            var17 = null;
         }

         if (var30 != 0 && var30 != 2) {
            var1.add(new LevelSortedDrawable(this) {
               public int getSortY() {
                  return var6;
               }

               public void draw(TickManager var1) {
                  var16.draw(var1);
               }
            });
            if (var17 != null) {
               var1.add(new LevelSortedDrawable(this) {
                  public int getSortY() {
                     return var6 + 10;
                  }

                  public void draw(TickManager var1) {
                     var17.draw();
                  }
               });
            }
         } else {
            var1.add(new LevelSortedDrawable(this) {
               public int getSortY() {
                  return var6;
               }

               public void draw(TickManager var1) {
                  var16.draw(var1);
                  if (var17 != null) {
                     var17.draw();
                  }

               }
            });
         }

      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.pestWarden_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(this.shadowSprite, 0, var6).light(var3).pos(var7, var8 - 40);
   }
}
