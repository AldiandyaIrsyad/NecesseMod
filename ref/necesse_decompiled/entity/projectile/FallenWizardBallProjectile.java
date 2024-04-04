package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenWizardBallProjectile extends Projectile {
   private long spawnTime;

   public FallenWizardBallProjectile() {
   }

   public FallenWizardBallProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setAngle(var5);
      this.speed = var6;
      this.distance = var7;
      this.setDamage(var8);
      this.knockback = var9;
   }

   public void init() {
      super.init();
      this.givesLight = true;
      this.height = 18.0F;
      this.setWidth(8.0F);
      this.spawnTime = this.getWorldEntity().getTime();
      this.particleRandomPerpOffset = 8.0F;
   }

   protected CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   protected int getExtraSpinningParticles() {
      return 0;
   }

   public Color getParticleColor() {
      return new Color(50, 0, 102);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 35.0F, 300, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9.minLevelCopy(Math.min(var9.getLevel() + 100.0F, 150.0F))).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, var12, this.texture.getHeight() / 2);
      }
   }
}
