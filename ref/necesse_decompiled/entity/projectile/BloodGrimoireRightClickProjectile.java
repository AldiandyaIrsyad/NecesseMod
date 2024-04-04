package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class BloodGrimoireRightClickProjectile extends Projectile {
   public BloodGrimoireRightClickProjectile() {
   }

   public BloodGrimoireRightClickProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.distance = var8;
      this.setDamage(var9);
      this.knockback = var10;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 9999;
      this.setWidth(200.0F, 20.0F);
      this.isSolid = false;
      this.givesLight = true;
      this.doesImpactDamage = false;
   }

   public void applyDamage(Mob var1, float var2, float var3) {
      super.applyDamage(var1, var2, var3);
   }

   public Color getParticleColor() {
      return null;
   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         float var9 = this.getFadeAlphaTime(300, 300);
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().colorLight(new Color(147, 16, 45), var10.minLevelCopy(Math.min(var10.getLevel() + 100.0F, 150.0F))).alpha(var9).rotate(this.getAngle() - 135.0F, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var11, var12 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
      }
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         if (var1 != null) {
            ActiveBuff var5 = new ActiveBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_MARKED_DEBUFF, var1, 10.0F, this.getOwner());
            var1.addBuff(var5, true);
         }

      }
   }
}
