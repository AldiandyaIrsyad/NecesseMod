package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SnowballProjectile extends Projectile {
   private long spawnTime;
   private int sprite;

   public SnowballProjectile() {
   }

   public SnowballProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6) {
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

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(155, 155, 155), 16.0F, 150, 18.0F);
   }

   public Color getParticleColor() {
      return new Color(155, 155, 155);
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
         final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(this.sprite, 0, var10).light(var9).rotate(this.getAngle(), var11, var11).pos(var12, var13 - (int)this.getHeight());
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

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }

   protected void playHitSound(float var1, float var2) {
   }
}
