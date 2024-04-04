package necesse.entity.projectile.boomerangProjectile;

import java.util.List;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.chains.Chain;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HookBoomerangProjectile extends BoomerangProjectile {
   public HookBoomerangProjectile() {
   }

   public void init() {
      super.init();
      this.setWidth(8.0F);
      this.height = 18.0F;
      this.bouncing = 0;
      this.piercing = 0;
      if (this.getOwner() != null) {
         Chain var1 = new Chain(this.getOwner(), this);
         var1.height = this.getHeight();
         this.getLevel().entityManager.addChain(var1);
      }

   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = this.getAngle() + (this.returningToOwner ? 180.0F : 0.0F);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, var12, this.texture.getHeight() / 2);
      }
   }

   public float getAngle() {
      return this.angle % 360.0F;
   }

   public void playMoveSound() {
   }

   public void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.tap, SoundEffect.effect(var1, var2));
   }
}
