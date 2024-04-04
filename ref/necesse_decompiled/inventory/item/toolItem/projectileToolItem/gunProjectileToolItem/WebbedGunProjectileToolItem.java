package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.WebbedGunBulletProjectile;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;

public class WebbedGunProjectileToolItem extends GunProjectileToolItem {
   public WebbedGunProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 600);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(26.0F).setUpgradedValue(1.0F, 100.0F);
      this.attackXOffset = 12;
      this.attackYOffset = 14;
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(400);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.spit, SoundEffect.effect(var1).pitch(1.2F));
   }

   public Projectile getNormalProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return new WebbedGunBulletProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
