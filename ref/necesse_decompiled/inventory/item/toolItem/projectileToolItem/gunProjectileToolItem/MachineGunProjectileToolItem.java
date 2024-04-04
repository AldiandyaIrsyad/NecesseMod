package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class MachineGunProjectileToolItem extends GunProjectileToolItem {
   public MachineGunProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 700);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(200);
      this.attackDamage.setBaseValue(12.0F).setUpgradedValue(1.0F, 45.0F);
      this.attackXOffset = 10;
      this.attackYOffset = 10;
      this.moveDist = 50;
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(400);
      this.knockback.setBaseValue(25);
      this.ammoConsumeChance = 0.5F;
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      super.addAmmoTooltips(var1, var2);
      var1.add(Localization.translate("itemtooltip", "machineguntip"));
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.handgun, SoundEffect.effect(var1));
   }
}
