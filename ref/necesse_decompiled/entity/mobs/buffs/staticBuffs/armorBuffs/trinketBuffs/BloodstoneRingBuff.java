package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class BloodstoneRingBuff extends TrinketBuff {
   public BloodstoneRingBuff() {
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.ARMOR_FLAT, -20);
   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      this.updateActiveBuff(var1);
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      this.updateActiveBuff(var1);
   }

   public void updateActiveBuff(ActiveBuff var1) {
      float var2 = var1.owner.getHealthPercent();
      if (var2 < 0.5F && var1.owner.buffManager.getBuffDurationLeftSeconds(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF) <= 1.0F) {
         ActiveBuff var3 = new ActiveBuff(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF, var1.owner, 4.0F, (Attacker)null);
         var1.owner.buffManager.addBuff(var3, true);
      } else if (var2 >= 0.5F && var1.owner.buffManager.hasBuff(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF)) {
         var1.owner.buffManager.removeBuff(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF, true);
      }

   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "bloodstoneringtip"), 400);
      return var4;
   }
}
