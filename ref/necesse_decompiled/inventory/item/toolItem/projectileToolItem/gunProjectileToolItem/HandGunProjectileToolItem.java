package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;

public class HandGunProjectileToolItem extends GunProjectileToolItem {
   public HandGunProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 500);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(17.0F).setUpgradedValue(1.0F, 85.0F);
      this.attackXOffset = 12;
      this.attackYOffset = 14;
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(350);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.handgun, SoundEffect.effect(var1));
   }
}
