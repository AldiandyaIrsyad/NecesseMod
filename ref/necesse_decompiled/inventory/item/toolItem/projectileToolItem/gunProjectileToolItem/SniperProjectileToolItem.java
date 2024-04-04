package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.SniperBulletProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SniperProjectileToolItem extends GunProjectileToolItem {
   public SniperProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 1000);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(43.0F).setUpgradedValue(1.0F, 160.0F);
      this.attackXOffset = 20;
      this.attackYOffset = 10;
      this.attackCooldownTime.setBaseValue(1000);
      this.velocity.setBaseValue(650);
      this.moveDist = 65;
      this.attackRange.setBaseValue(1600);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   protected void addExtraGunTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraGunTooltips(var1, var2, var3, var4);
      var1.add(Localization.translate("itemtooltip", "snipertip"));
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(var1));
   }

   public float zoomAmount() {
      return 300.0F;
   }

   public Projectile getNormalProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return new SniperBulletProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
