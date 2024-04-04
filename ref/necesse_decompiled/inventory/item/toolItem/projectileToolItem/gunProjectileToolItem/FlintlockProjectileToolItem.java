package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;

public class FlintlockProjectileToolItem extends GunProjectileToolItem {
   public FlintlockProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 800);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(28.0F).setUpgradedValue(1.0F, 85.0F);
      this.attackXOffset = 8;
      this.attackYOffset = 10;
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(450);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.handgun, SoundEffect.effect(var1));
   }
}
