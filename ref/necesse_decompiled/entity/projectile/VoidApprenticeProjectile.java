package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.DeathMessageTable;
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

public class VoidApprenticeProjectile extends Projectile {
   private long spawnTime;

   public VoidApprenticeProjectile() {
   }

   public VoidApprenticeProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = 80.0F;
      this.setDamage(var5);
      this.setOwner(var6);
      this.setDistance(400);
   }

   public void init() {
      super.init();
      this.setWidth(8.0F);
      this.height = 18.0F;
      this.spawnTime = this.getWorldEntity().getTime();
      this.trailOffset = 0.0F;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(110, 0, 160), 14.0F, 250, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, var12, this.texture.getHeight() / 2);
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("voidapp", 3);
   }
}
