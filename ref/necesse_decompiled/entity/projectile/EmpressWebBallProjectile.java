package necesse.entity.projectile;

import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.chains.Chain;
import necesse.entity.chains.ChainLocation;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EmpressWebBallProjectile extends BoomerangProjectile {
   private Chain chain;

   public EmpressWebBallProjectile() {
   }

   public EmpressWebBallProjectile(float var1, float var2, float var3, GameDamage var4, float var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setAngle(var3);
      this.setDamage(var4);
      this.setOwner(var6);
      this.setDistance(500);
      this.speed = var5;
   }

   public void init() {
      super.init();
      this.setWidth(10.0F, true);
      this.height = 18.0F;
      this.piercing = Integer.MAX_VALUE;
      this.isSolid = false;
      final Mob var1 = this.getOwner();
      if (var1 != null) {
         this.chain = new Chain(new ChainLocation() {
            public int getX() {
               return (int)var1.x;
            }

            public int getY() {
               return (int)var1.y - 60;
            }

            public boolean removed() {
               return false;
            }
         }, this) {
            public int getDrawY() {
               return super.getDrawY() - 30;
            }
         };
         this.chain.sprite = new GameSprite(GameResources.chains, 5, 0, 32);
         this.chain.height = this.getHeight();
         this.getLevel().entityManager.addChain(this.chain);
      }

   }

   protected void returnToOwner() {
      if (!this.returningToOwner) {
         this.speed *= 2.0F;
      }

      super.returnToOwner();
   }

   public void playMoveSound() {
   }

   public Trail getTrail() {
      return null;
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
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.shadowTexture.getHeight() / 2);
      }
   }

   public void remove() {
      if (this.chain != null) {
         this.chain.remove();
      }

      super.remove();
   }
}
