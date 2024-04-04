package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class IceJavelinProjectile extends Projectile {
   public IceJavelinProjectile() {
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.setWidth(8.0F);
      this.trailOffset = -50.0F;
      this.heightBasedOnDistance = true;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(150, 150, 150), 10.0F, 250, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - 2;
         int var11 = var7.getDrawY(this.y) - 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle() + 45.0F, 2, 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle() + 45.0F, 2, 2);
      }
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isClient() && this.traveledDistance < (float)this.distance) {
         final float var5 = this.getHeight();
         this.getLevel().entityManager.addParticle((Particle)(new ProjectileHitStuckParticle(var1, this, var3, var4, var1 == null ? 10.0F : 40.0F, 5000L) {
            public void addDrawables(Mob var1, float var2, float var3, float var4, List<LevelSortedDrawable> var5x, OrderableDrawables var6, OrderableDrawables var7, Level var8, TickManager var9, GameCamera var10, PlayerMob var11) {
               GameLight var12 = var8.getLightLevel(this);
               int var13 = var10.getDrawX(var2) - 2;
               int var14 = var10.getDrawY(var3 - var5) - 2;
               float var15 = 1.0F;
               long var16 = this.getLifeCycleTime();
               short var18 = 1000;
               if (var16 >= this.lifeTime - (long)var18) {
                  var15 = Math.abs((float)(var16 - (this.lifeTime - (long)var18)) / (float)var18 - 1.0F);
               }

               int var19 = var1 == null ? 8 : 0;
               final TextureDrawOptionsEnd var20 = IceJavelinProjectile.this.texture.initDraw().section(var19, IceJavelinProjectile.this.texture.getWidth(), var19, IceJavelinProjectile.this.texture.getHeight()).light(var12).rotate(IceJavelinProjectile.this.getAngle() + 45.0F, 2, 2).alpha(var15).pos(var13, var14);
               EntityDrawable var21 = new EntityDrawable(this) {
                  public void draw(TickManager var1) {
                     var20.draw();
                  }
               };
               if (var1 != null) {
                  var7.add(var21);
               } else {
                  var5x.add(var21);
               }

            }
         }), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.tap, SoundEffect.effect(var1, var2).volume(0.5F).pitch(0.8F));
   }
}
