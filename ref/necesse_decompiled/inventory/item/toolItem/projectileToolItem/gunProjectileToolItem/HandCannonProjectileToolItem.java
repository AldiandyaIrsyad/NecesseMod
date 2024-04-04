package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class HandCannonProjectileToolItem extends GunProjectileToolItem {
   public HandCannonProjectileToolItem() {
      super((String)"cannonball", 700);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(1200);
      this.attackDamage.setBaseValue(50.0F);
      this.attackDamage.setUpgradedValue(1.0F, 200.0F);
      this.attackXOffset = 10;
      this.attackYOffset = 8;
      this.attackRange.setBaseValue(600);
      this.velocity.setBaseValue(200);
      this.controlledRange = true;
      this.controlledMinRange = 32;
      this.controlledInaccuracy = 50;
      this.resilienceGain.setBaseValue(0.0F);
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      var1.add(Localization.translate("itemtooltip", "handcannontip1"));
      var1.add(Localization.translate("itemtooltip", "handcannontip2"));
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.explosionLight, SoundEffect.effect(var1).pitch(1.3F));
   }
}
