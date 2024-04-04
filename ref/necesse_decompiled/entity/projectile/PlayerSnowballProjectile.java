package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class PlayerSnowballProjectile extends Projectile {
   private long spawnTime;
   private int sprite;

   public PlayerSnowballProjectile() {
   }

   public PlayerSnowballProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = 100.0F;
      this.setDamage(var5);
      this.setOwner(var6);
      this.setDistance(400);
   }

   public void init() {
      super.init();
      this.setWidth(10.0F);
      this.height = 18.0F;
      this.spawnTime = this.getWorldEntity().getTime();
      this.heightBasedOnDistance = true;
      this.trailOffset = 0.0F;
      if (this.texture != null) {
         this.sprite = (new GameRandom((long)this.getUniqueID())).nextInt(this.texture.getWidth() / 32);
      }

   }

   protected Stream<Mob> streamTargets(Mob var1, Shape var2) {
      return Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(var2, 1), GameUtils.streamNetworkClients(this.getLevel()).filter((var0) -> {
         return !var0.isDead() && var0.hasSpawned();
      }).map((var0) -> {
         return var0.playerMob;
      })).filter((var1x) -> {
         return var1x != var1;
      });
   }

   public boolean canHit(Mob var1) {
      if (!var1.canTakeDamage()) {
         return false;
      } else {
         Mob var2 = var1.getRider();
         return var2 == null || var2.canTakeDamage();
      }
   }

   public void applyDamage(Mob var1, float var2, float var3) {
      if (var1.isPlayer) {
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SNOW_COVERED_DEBUFF, var1, 8000, (Attacker)null), true);
      } else {
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SNOW_COVERED_SLOW_DEBUFF, var1, 8000, (Attacker)null), true);
      }

   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isClient() && this.traveledDistance < (float)this.distance) {
         this.getLevel().entityManager.addParticle((Particle)(new ProjectileHitStuckParticle(var1, this, var3, var4, var1 == null ? 2.0F : 6.0F, 8000L) {
            public void addDrawables(Mob var1, float var2, float var3, float var4, List<LevelSortedDrawable> var5, OrderableDrawables var6, OrderableDrawables var7, Level var8, TickManager var9, GameCamera var10, PlayerMob var11) {
               GameLight var12 = var8.getLightLevel(this);
               float var13 = 1.0F;
               long var14 = this.getLifeCycleTime();
               short var16 = 500;
               if (var14 >= this.lifeTime - (long)var16) {
                  var13 = Math.abs((float)(var14 - (this.lifeTime - (long)var16)) / (float)var16 - 1.0F);
               }

               byte var17 = 32;
               int var18 = var17 / 2;
               int var19 = var10.getDrawX(var2) - var18;
               int var20 = var10.getDrawY(var3) - var18;
               GameTextureSection var21 = (new GameTextureSection(GameResources.cutSnowballParticles)).sprite(PlayerSnowballProjectile.this.sprite, 0, var17);
               final TextureDrawOptionsEnd var22 = var21.initDraw().alpha(var13).light(var12).rotate(PlayerSnowballProjectile.this.getAngle() - 90.0F).pos(var19, var20 - (int)PlayerSnowballProjectile.this.getHeight());
               EntityDrawable var23 = new EntityDrawable(this) {
                  public void draw(TickManager var1) {
                     var22.draw();
                  }
               };
               if (var1 != null) {
                  var7.add(var23);
               } else {
                  var5.add(var23);
               }

            }
         }), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(227, 241, 240), 12.0F, 150, 18.0F);
   }

   public Color getParticleColor() {
      return new Color(227, 241, 240);
   }

   public float getParticleChance() {
      return super.getParticleChance() * 0.5F;
   }

   protected int getExtraSpinningParticles() {
      return 0;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         byte var10 = 32;
         int var11 = var10 / 2;
         int var12 = var7.getDrawX(this.x) - var11;
         int var13 = var7.getDrawY(this.y) - var11;
         final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(this.sprite, 0, var10).light(var9).rotate(this.rotateBasedOnTime()).pos(var12, var13 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var14.draw();
            }
         });
         TextureDrawOptionsEnd var15 = this.shadowTexture.initDraw().sprite(this.sprite, 0, var10).light(var9).rotate(this.getAngle(), var11, var11).pos(var12, var13);
         var2.add((var1x) -> {
            var15.draw();
         });
      }
   }

   public float rotateBasedOnTime() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }

   protected void playHitSound(float var1, float var2) {
   }
}
