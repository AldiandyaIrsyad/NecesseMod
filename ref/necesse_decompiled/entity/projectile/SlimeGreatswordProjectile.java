package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeGreatswordProjectile extends Projectile {
   public SlimeGreatswordProjectile() {
   }

   public SlimeGreatswordProjectile(Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = var6;
      this.setDamage(var8);
      this.setOwner(var9);
      this.setDistance(var7);
   }

   public void init() {
      super.init();
      this.piercing = 3;
      this.height = 16.0F;
      this.setWidth(55.0F, true);
      this.particleRandomOffset = 14.0F;
   }

   public Color getParticleColor() {
      return new Color(70, 178, 170);
   }

   protected void spawnDeathParticles() {
      Color var1 = this.getParticleColor();
      if (var1 != null) {
         float var2 = this.getHeight();

         for(int var3 = 0; var3 < 40; ++var3) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(10, 20).movesConstant((float)(GameRandom.globalRandom.getIntBetween((int)(-this.dy * 30.0F), (int)(this.dy * 30.0F)) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3)), (float)(GameRandom.globalRandom.getIntBetween((int)(-this.dx * 30.0F), (int)(this.dx * 30.0F)) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3))).color(this.getParticleColor()).height(var2);
         }
      }

   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         float var9 = this.getFadeAlphaTime(300, 200);
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y - this.getHeight()) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var10).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).alpha(var9).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
      }
   }
}
