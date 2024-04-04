package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FrenzyOrbBuff extends TrinketBuff {
   public FrenzyOrbBuff() {
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "frenzyorbtip"));
      return var4;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      this.updateBuff(var1);
   }

   public void serverTick(ActiveBuff var1) {
      this.updateBuff(var1);
   }

   public void clientTick(ActiveBuff var1) {
      this.updateBuff(var1);
   }

   private void updateBuff(ActiveBuff var1) {
      float var2 = (Float)var1.getModifier(BuffModifiers.ALL_DAMAGE);
      float var3 = getAttackBonusPerc((float)var1.owner.getHealth() / (float)var1.owner.getMaxHealth(), 0.1F) * 0.4F;
      var3 = GameMath.toDecimals(var3, 2);
      if (var2 != var3) {
         var1.setModifier(BuffModifiers.ALL_DAMAGE, var3);
         var1.forceManagerUpdate();
      }

   }

   public static float getAttackBonusPerc(float var0, float var1) {
      if (var1 != 0.0F) {
         var0 = (var1 - var0) / (var1 - 1.0F);
      }

      var0 = GameMath.limit(var0, 0.0F, 1.0F);
      return Math.abs(var0 - 1.0F);
   }
}
