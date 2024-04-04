package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoWaveProjectile extends Projectile {
   public CryoWaveProjectile() {
   }

   public CryoWaveProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDistance(var6);
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
   }

   public void init() {
      super.init();
      this.piercing = 9999;
      this.setWidth(75.0F);
      this.isSolid = false;
      this.givesLight = true;
      this.particleRandomOffset = 8.0F;
   }

   public Color getParticleColor() {
      return new Color(130, 155, 227);
   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - 64;
         int var11 = var7.getDrawY(this.y) - 64;
         TextureDrawOptionsEnd var12 = MobRegistry.Textures.cryoQueen.initDraw().sprite(2, 3, 128).light(var9).rotate(this.getAngle() - 135.0F, 64, 64).pos(var10, var11);
         var3.add((var1x) -> {
            var12.draw();
         });
      }
   }
}
