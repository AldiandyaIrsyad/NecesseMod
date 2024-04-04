package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class QueenSpiderEggProjectile extends Projectile {
   protected long spawnTime;

   public QueenSpiderEggProjectile() {
   }

   public QueenSpiderEggProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.x = var3;
      this.y = var4;
      this.speed = var7;
      this.setTarget(var5, var6);
      this.setDamage(var9);
      this.knockback = var10;
      this.setDistance(var8);
      this.setOwner(var2);
   }

   public QueenSpiderEggProjectile(Level var1, Mob var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8) {
      this(var1, var2, var2.x, var2.y, var3, var4, var5, var6, var7, var8);
   }

   public void init() {
      super.init();
      this.spawnTime = this.getWorldEntity().getTime();
      this.isSolid = false;
      this.canHitMobs = false;
      this.trailOffset = 0.0F;
   }

   public float tickMovement(float var1) {
      float var2 = super.tickMovement(var1);
      float var3 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
      float var4 = Math.abs(var3 - 1.0F);
      float var5 = GameMath.sin(var3 * 180.0F);
      this.height = (float)((int)(var5 * 200.0F + 50.0F * var4));
      return var2;
   }

   public Color getParticleColor() {
      return new Color(216, 213, 221);
   }

   public Trail getTrail() {
      return null;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         Mob var5 = this.getOwner();
         if (var5 != null && !var5.removed()) {
            Mob var6 = MobRegistry.getMob("spiderhatchling", this.getLevel());
            if (!var6.collidesWith(this.getLevel(), (int)var3, (int)var4)) {
               this.getLevel().entityManager.addMob(var6, (float)((int)var3), (float)((int)var4));
            }
         }

      }
   }

   protected void spawnDeathParticles() {
      for(int var1 = 0; var1 < 6; ++var1) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), this.texture, GameRandom.globalRandom.nextInt(4), 1, 32, this.x, this.y, 10.0F, this.dx, this.dy)), Particle.GType.IMPORTANT_COSMETIC);
      }

      Color var5 = this.getParticleColor();
      if (var5 != null) {
         for(int var2 = 0; var2 < 10; ++var2) {
            int var3 = GameRandom.globalRandom.nextInt(360);
            Point2D.Float var4 = GameMath.getAngleDir((float)var3);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.getIntBetween(20, 50) * var4.x, (float)GameRandom.globalRandom.getIntBetween(20, 50) * var4.y).color(this.getParticleColor()).height(this.getHeight());
         }
      }

      Float var6 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.9F, 0.95F, 1.0F));
      Screen.playSound(GameResources.crackdeath, SoundEffect.effect(this.x, this.y).volume(0.7F).pitch(var6));
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         int var10 = var7.getDrawX(this.x) - 16;
         int var11 = var7.getDrawY(this.y) - 16;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5F;
         if (this.dx < 0.0F) {
            var12 = -var12;
         }

         float var13 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
         int var14 = (int)(var13 * 4.0F);
         TextureDrawOptionsEnd var15 = this.texture.initDraw().sprite(var14, 0, 32).light(var9).rotate(var12, 16, 16).pos(var10, var11 - (int)this.getHeight());
         float var16 = Math.abs(GameMath.limit(this.height / 300.0F, 0.0F, 1.0F) - 1.0F);
         int var17 = var7.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
         int var18 = var7.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
         TextureDrawOptionsEnd var19 = this.shadowTexture.initDraw().light(var9).rotate(var12).alpha(var16).pos(var17, var18);
         var3.add((var2x) -> {
            var19.draw();
            var15.draw();
         });
      }
   }
}
