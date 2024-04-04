package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class StarVeilProjectile extends Projectile {
   private int spriteIndex;
   private long spawnTime;

   public StarVeilProjectile() {
   }

   public StarVeilProjectile(float var1, float var2, float var3, GameDamage var4, float var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setAngle(var3);
      this.setDamage(var4);
      this.setOwner(var6);
      this.setDistance(1500);
      this.speed = var5;
   }

   public StarVeilProjectile(float var1, float var2, float var3, float var4, GameDamage var5, float var6, Mob var7) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.setDamage(var5);
      this.setOwner(var7);
      this.setDistance(1500);
      this.speed = var6;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 1;
      this.spawnTime = this.getLevel().getWorldEntity().getTime();
      this.isSolid = false;
      this.givesLight = true;
      this.trailOffset = 0.0F;
      this.spriteIndex = GameRandom.globalRandom.getIntBetween(0, 4);
   }

   public Color getParticleColor() {
      return new Color(184, 174, 255);
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      var1.lifeTime(200);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(184, 174, 255), 12.0F, 1000, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - 15;
         int var11 = var7.getDrawY(this.y) - 15;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(this.spriteIndex, 0, 30).light(var9).rotate(this.getAngle() / 2.0F, 15, 15).pos(var10, var11 - (int)this.getHeight()).alpha(this.getAngle());
         var3.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }
}
