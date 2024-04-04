package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VenomStaffEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class VenomStaffProjectile extends Projectile {
   protected float eventResilienceGain;

   public VenomStaffProjectile() {
   }

   public VenomStaffProjectile(Level var1, float var2, float var3, float var4, float var5, int var6, int var7, GameDamage var8, float var9, Mob var10) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = (float)var6;
      this.setDamage(var8);
      this.eventResilienceGain = var9;
      this.setOwner(var10);
      this.setDistance(var7);
   }

   public void init() {
      super.init();
      this.height = 16.0F;
      this.canHitMobs = false;
      this.canBreakObjects = false;
   }

   public Color getParticleColor() {
      return new Color(160, 200, 65);
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
            VenomStaffEvent var6 = new VenomStaffEvent(var5, (int)var3, (int)var4, GameRandom.globalRandom, this.getDamage(), this.eventResilienceGain);
            this.getLevel().entityManager.addLevelEvent(var6);
         }

      }
   }
}
