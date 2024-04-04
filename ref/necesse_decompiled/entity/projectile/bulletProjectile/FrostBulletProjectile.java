package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class FrostBulletProjectile extends BulletProjectile {
   public FrostBulletProjectile() {
   }

   public FrostBulletProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void init() {
      super.init();
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         if (var1 != null) {
            ActiveBuff var5 = new ActiveBuff(BuffRegistry.Debuffs.FROSTSLOW, var1, 10.0F, this.getOwner());
            var1.addBuff(var5, true);
         }

      }
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(48, 105, 157), 22.0F, 100, this.getHeight());
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   protected Color getWallHitColor() {
      return new Color(48, 105, 157);
   }
}
