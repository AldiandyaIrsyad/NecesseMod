package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;

public class CryoBlasterProjectileToolItem extends GunProjectileToolItem {
   public CryoBlasterProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 1500);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 65.0F);
      this.attackXOffset = 10;
      this.attackYOffset = 12;
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(550);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   public boolean animDrawBehindHand() {
      return super.animDrawBehindHand();
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.jinglehit, SoundEffect.effect(var1).pitch(GameRandom.globalRandom.getFloatBetween(1.2F, 1.3F)));
   }
}
