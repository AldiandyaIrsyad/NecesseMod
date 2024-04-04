package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.level.maps.Level;

public class ThrowToolItem extends ProjectileToolItem {
   public ThrowToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons", "throwweapons"});
      this.keyWords.add("throw");
      this.attackXOffset = 16;
      this.attackYOffset = 16;
      this.knockback.setBaseValue(25);
      this.enchantCost.setUpgradedValue(1.0F, 1950);
   }

   public ThrowToolItem() {
      super(0);
      this.stackSize = 250;
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.getItemSprite(var1, var2), 32);
   }

   protected ListGameTooltips getBaseTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getBaseTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "throwtip"));
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.swing1, SoundEffect.effect(var4));
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      if (this.animInverted) {
         var2.swingRotationInv(var5);
      } else {
         var2.swingRotation(var5);
      }

   }

   public boolean animDrawBehindHand() {
      return false;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return false;
   }
}
