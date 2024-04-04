package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.levelEvent.explosionEvent.CannonBallExplosionEvent;
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
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class CannonBallProjectile extends Projectile {
   private long spawnTime;

   public CannonBallProjectile() {
   }

   public CannonBallProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this();
      this.setLevel(var9.getLevel());
      this.applyData(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void init() {
      super.init();
      this.setWidth(15.0F);
      this.height = 18.0F;
      this.heightBasedOnDistance = true;
      this.spawnTime = this.getWorldEntity().getTime();
      this.doesImpactDamage = false;
      this.trailOffset = 0.0F;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(30, 30, 30), 14.0F, 250, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
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
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         CannonBallExplosionEvent var5 = new CannonBallExplosionEvent(var3, var4, this.getDamage(), this.getOwner());
         this.getLevel().entityManager.addLevelEvent(var5);
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("explosion", 3);
   }
}
