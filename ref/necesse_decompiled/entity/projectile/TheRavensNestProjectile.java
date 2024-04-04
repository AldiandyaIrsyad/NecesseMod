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

public class TheRavensNestProjectile extends Projectile {
   public static int HEIGHT_BOUNCE = 7;
   public static int DISTANCE_PER_BOUNCE = 250;
   protected GameRandom heightRandom = new GameRandom();

   public TheRavensNestProjectile() {
   }

   public TheRavensNestProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.distance = var8;
      this.setDamage(var9);
      this.knockback = var10;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 5;
      this.setWidth(60.0F, true);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(236, 229, 246, 182), 6.0F, 500, this.getHeight());
   }

   public float getHeight() {
      float var1 = this.traveledDistance + this.heightRandom.seeded((long)this.getUniqueID()).nextFloat() * (float)DISTANCE_PER_BOUNCE;
      float var2 = var1 % (float)DISTANCE_PER_BOUNCE / (float)DISTANCE_PER_BOUNCE;
      return this.height + (float)Math.sin((double)var2 * Math.PI * 2.0) * (float)HEIGHT_BOUNCE;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         float var10 = this.getFadeAlphaTime(200, 200);
         int var11 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         boolean var13 = this.dy > 0.0F;
         final TextureDrawOptionsEnd var14 = this.texture.initDraw().mirror(false, var13).light(var9).alpha(var10).rotate(this.getAngle() - 90.0F, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var11, var12 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var14.draw();
            }
         });
         TextureDrawOptionsEnd var15 = this.shadowTexture.initDraw().mirror(false, var13).light(var9).alpha(var10).rotate(this.getAngle() - 90.0F, this.shadowTexture.getWidth() / 2, this.shadowTexture.getHeight() / 2).pos(var11, var12);
         var2.add((var1x) -> {
            var15.draw();
         });
      }
   }
}
