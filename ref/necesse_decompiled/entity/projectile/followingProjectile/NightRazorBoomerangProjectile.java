package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class NightRazorBoomerangProjectile extends FollowingProjectile {
   protected long spawnTime;

   public NightRazorBoomerangProjectile() {
      this.isBoomerang = true;
   }

   public void init() {
      super.init();
      if (this.getOwner() == null) {
         this.remove();
      }

      this.returningToOwner = false;
      this.spawnTime = this.getWorldEntity().getTime();
      this.trailOffset = 0.0F;
      this.setWidth(16.0F, true);
      this.height = 18.0F;
      this.bouncing = 10;
      this.turnSpeed = 0.5F;
      this.givesLight = true;
      this.lightSaturation = 1.0F;
   }

   public Color getParticleColor() {
      return new Color(108, 37, 92);
   }

   protected int getExtraSpinningParticles() {
      return 1;
   }

   protected void spawnDeathParticles() {
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(108, 37, 92), 30.0F, 400, this.height);
   }

   protected Color getWallHitColor() {
      return new Color(108, 37, 92);
   }

   public void updateTarget() {
      if (this.traveledDistance > 40.0F) {
         this.findTarget((var0) -> {
            return var0.isHostile;
         }, 160.0F, 160.0F);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9.minLevelCopy(100.0F)).rotate(var12 * 1.5F, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.shadowTexture.getHeight() / 2);
      }
   }
}
