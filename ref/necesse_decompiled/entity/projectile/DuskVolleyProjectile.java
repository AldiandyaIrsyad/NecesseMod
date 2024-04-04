package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DuskVolleyProjectile extends FollowingProjectile {
   private long spawnTime;

   public DuskVolleyProjectile() {
   }

   public DuskVolleyProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9) {
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
      this.setWidth(8.0F);
      this.turnSpeed = 0.5F;
      this.height = 18.0F;
      this.spawnTime = this.getWorldEntity().getTime();
      this.trailOffset = 0.0F;
      this.isSolid = false;
      this.givesLight = true;
   }

   public Color getParticleColor() {
      return new Color(220, 212, 255);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(255, 255, 255), 4.0F, 400, this.getHeight());
   }

   public void updateTarget() {
      if (this.traveledDistance > 50.0F) {
         this.findTarget((var0) -> {
            return var0.isHostile;
         }, 0.0F, 250.0F);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate((float)(-(this.getWorldEntity().getTime() - this.spawnTime)), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
      }
   }
}
