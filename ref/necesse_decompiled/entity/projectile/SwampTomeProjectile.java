package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
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

public class SwampTomeProjectile extends Projectile {
   private long spawnTime;

   public SwampTomeProjectile() {
   }

   public SwampTomeProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
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
      this.setWidth(12.0F);
      this.bouncing = 15;
      this.piercing = 3;
      this.spawnTime = this.getWorldEntity().getTime();
      this.givesLight = true;
      this.trailOffset = 0.0F;
   }

   public Color getParticleColor() {
      return new Color(31, 112, 57);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 16.0F, 300, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         long var10 = this.getWorldEntity().getTime() - this.spawnTime;
         float var12 = (float)var10 / 10.0F;
         float var13 = GameUtils.getAnimFloatContinuous(var10, 300);
         int var14 = (int)((float)this.texture.getWidth() * (var13 / 3.0F + 1.0F));
         int var15 = (int)((float)this.texture.getHeight() * (var13 / 3.0F + 1.0F));
         int var16 = var7.getDrawX(this.x) - var14 / 2;
         int var17 = var7.getDrawY(this.y) - var15 / 2;
         final TextureDrawOptionsEnd var18 = this.texture.initDraw().light(var9).size(var14, var15).rotate(var12, var14 / 2, var15 / 2).pos(var16, var17 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var18.draw();
            }
         });
         this.addShadowDrawables(var2, var16, var17, var9, var12, var15 / 2);
      }
   }
}
