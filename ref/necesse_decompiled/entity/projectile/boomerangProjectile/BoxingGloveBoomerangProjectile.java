package necesse.entity.projectile.boomerangProjectile;

import java.util.List;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.chains.Chain;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
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

public class BoxingGloveBoomerangProjectile extends BoomerangProjectile {
   private Chain chain;

   public BoxingGloveBoomerangProjectile() {
   }

   public BoxingGloveBoomerangProjectile(Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9, Mob var10) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = var6;
      this.setDistance(var7);
      this.setDamage(var8);
      this.knockback = var9;
      this.setOwner(var10);
   }

   public void init() {
      super.init();
      this.setWidth(8.0F);
      this.height = 18.0F;
      this.bouncing = 0;
      this.piercing = 0;
      Mob var1 = this.getOwner();
      if (var1 != null) {
         this.chain = new Chain(var1, this) {
            public int getDrawY() {
               return super.getDrawY() - 30;
            }
         };
         this.chain.height = this.getHeight();
         this.getLevel().entityManager.addChain(this.chain);
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
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle() + (this.returningToOwner ? 180.0F : 0.0F), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
      }
   }

   public float getAngle() {
      return this.angle % 360.0F;
   }

   public void playMoveSound() {
   }

   public void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.punch, SoundEffect.effect(var1, var2).volume(1.5F));
   }

   public void remove() {
      if (this.chain != null) {
         this.chain.remove();
      }

      super.remove();
   }
}
