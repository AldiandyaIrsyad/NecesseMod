package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;

public class AntiqueRifleProjectileToolItem extends GunProjectileToolItem {
   public AntiqueRifleProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 1500);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(105.0F).setUpgradedValue(1.0F, 120.0F);
      this.attackXOffset = 20;
      this.attackYOffset = 10;
      this.velocity.setBaseValue(650);
      this.moveDist = 65;
      this.attackRange.setBaseValue(2000);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(var1).volume(0.5F).pitch(2.0F));
   }
}
