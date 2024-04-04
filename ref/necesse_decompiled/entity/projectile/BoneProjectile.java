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
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BoneProjectile extends Projectile {
   private long spawnTime;

   public BoneProjectile() {
   }

   public BoneProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = 100.0F;
      this.setDamage(var5);
      this.setOwner(var6);
      this.setDistance(600);
   }

   public void init() {
      super.init();
      this.setWidth(10.0F);
      this.height = 18.0F;
      this.spawnTime = this.getWorldEntity().getTime();
      this.trailOffset = 0.0F;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(170, 170, 170), 10.0F, 250, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.getX()) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.getY()) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.texture.getHeight() / 2);
      }
   }

   public float getAngle() {
      return this.dx < 0.0F ? (float)(this.spawnTime - this.getWorldEntity().getTime()) : (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }
}
