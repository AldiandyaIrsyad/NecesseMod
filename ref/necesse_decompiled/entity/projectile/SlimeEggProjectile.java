package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.PlayerMob;
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

public class SlimeEggProjectile extends Projectile {
   protected long spawnTime;
   protected int sprite;

   public SlimeEggProjectile() {
      this.sprite = GameRandom.globalRandom.nextInt(4);
   }

   public SlimeEggProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.sprite = GameRandom.globalRandom.nextInt(4);
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

   public SlimeEggProjectile(Level var1, Mob var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8) {
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
      this.height = (float)((int)(var5 * 200.0F + 70.0F * var4));
      return var2;
   }

   public Color getParticleColor() {
      return new Color(177, 121, 31);
   }

   public Trail getTrail() {
      return null;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         Mob var5 = this.getOwner();
         if (var5 != null && !var5.removed()) {
            Mob var6 = MobRegistry.getMob("warriorslime", this.getLevel());
            var6.isSummoned = true;
            var6.canDespawn = false;
            if (!var6.collidesWith(this.getLevel(), (int)var3, (int)var4)) {
               this.getLevel().entityManager.addMob(var6, (float)((int)var3), (float)((int)var4));
            }
         }

      }
   }

   protected void spawnDeathParticles() {
      byte var1 = 20;
      float var2 = 360.0F / (float)var1;

      for(int var3 = 0; var3 < var1; ++var3) {
         int var4 = (int)((float)var3 * var2 + GameRandom.globalRandom.nextFloat() * var2);
         int var5 = GameRandom.globalRandom.getIntBetween(0, 10);
         float var6 = this.x + (float)Math.sin(Math.toRadians((double)var4)) * (float)var5;
         float var7 = this.y + (float)Math.cos(Math.toRadians((double)var4)) * (float)var5 * 0.6F;
         float var8 = (float)Math.sin(Math.toRadians((double)var4)) * (float)GameRandom.globalRandom.getIntBetween(40, 60);
         float var9 = (float)Math.cos(Math.toRadians((double)var4)) * (float)GameRandom.globalRandom.getIntBetween(40, 60) * 0.6F;
         this.getLevel().entityManager.addParticle(var6, var7, var3 % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(var8, var9, 0.8F).colorRandom(36.0F, 0.7F, 0.6F, 10.0F, 0.1F, 0.1F).heightMoves(0.0F, 50.0F).lifeTime(1500);
      }

      Screen.playSound(GameResources.slimesplash, SoundEffect.effect(this).pitch(0.9F).volume(0.5F));
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         int var10 = var7.getDrawX(this.x) - 32;
         int var11 = var7.getDrawY(this.y) - 44;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5F;
         if (this.dx < 0.0F) {
            var12 = -var12;
         }

         MobTexture var13 = MobRegistry.Textures.warriorSlime;
         TextureDrawOptionsEnd var14 = var13.body.initDraw().sprite(1, this.sprite, 64).light(var9).rotate(var12, 32, 44).pos(var10, var11 - (int)this.getHeight());
         float var15 = Math.abs(GameMath.limit(this.height / 400.0F, 0.0F, 1.0F) - 1.0F);
         int var16 = var7.getDrawX(this.x) - 32;
         int var17 = var7.getDrawY(this.y) - 54;
         TextureDrawOptionsEnd var18 = var13.shadow.initDraw().sprite(1, this.sprite, 64).light(var9).rotate(var12, 32, 54).alpha(var15).pos(var16, var17);
         var3.add((var2x) -> {
            var18.draw();
            var14.draw();
         });
      }
   }
}
