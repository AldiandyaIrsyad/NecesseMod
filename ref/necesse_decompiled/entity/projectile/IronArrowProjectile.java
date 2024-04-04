package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class IronArrowProjectile extends Projectile {
   public IronArrowProjectile() {
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.heightBasedOnDistance = true;
      this.setWidth(8.0F);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(150, 150, 150), 10.0F, 250, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }

   public void dropItem() {
      if (GameRandom.globalRandom.getChance(0.5F)) {
         this.getLevel().entityManager.pickups.add((new InventoryItem("ironarrow")).getPickupEntity(this.getLevel(), this.x, this.y));
      }

   }

   protected void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.bowhit, SoundEffect.effect(var1, var2));
   }
}
