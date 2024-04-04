package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderWebEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class CaveSpiderWebProjectile extends Projectile {
   public CaveSpiderWebProjectile() {
   }

   public CaveSpiderWebProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6, int var7) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = 80.0F;
      this.setDamage(var5);
      this.setOwner(var6);
      this.setDistance(var7);
   }

   public void init() {
      super.init();
      this.height = 16.0F;
      this.canHitMobs = false;
   }

   public Color getParticleColor() {
      return new Color(220, 215, 210);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 16.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         Mob var5 = this.getOwner();
         if (var5 != null && !var5.removed()) {
            CaveSpiderWebEvent var6 = new CaveSpiderWebEvent(var5, (int)var3, (int)var4, GameRandom.globalRandom);
            this.getLevel().entityManager.addLevelEvent(var6);
         }

      }
   }
}
