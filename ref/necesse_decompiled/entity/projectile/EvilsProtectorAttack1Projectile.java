package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsProtectorAttack1Projectile extends Projectile {
   public EvilsProtectorAttack1Projectile() {
   }

   public EvilsProtectorAttack1Projectile(float var1, float var2, float var3, float var4, int var5, GameDamage var6, Mob var7) {
      this.x = var1;
      this.y = var2;
      this.setAngle(var3);
      this.speed = var4;
      this.setDamage(var6);
      this.setOwner(var7);
      this.setDistance(var5);
   }

   public void init() {
      super.init();
      this.height = 16.0F;
      this.isSolid = false;
      this.givesLight = true;
      this.setWidth(5.0F);
      this.particleDirOffset = -30.0F;
      this.particleRandomOffset = 3.0F;
   }

   public Color getParticleColor() {
      return new Color(220, 40, 20);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(220, 40, 20), 16.0F, 500, 16.0F);
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 1;
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      super.modifySpinningParticle(var1);
      var1.sizeFades(8, 14);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - 16;
         int var11 = var7.getDrawY(this.y);
         int var12 = GameUtils.getAnim(this.getWorldEntity().getTime(), 6, 400);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12, 0, 32, 64).light(var9).rotate(this.getAngle(), 16, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         TextureDrawOptionsEnd var14 = this.shadowTexture.initDraw().sprite(var12, 0, 32, 64).light(var9).rotate(this.getAngle(), 16, 0).pos(var10, var11);
         var2.add((var1x) -> {
            var14.draw();
         });
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("evilsproj", 3);
   }
}
