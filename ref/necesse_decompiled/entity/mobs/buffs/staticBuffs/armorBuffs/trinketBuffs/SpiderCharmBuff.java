package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SpiderCharmBuff extends TrinketBuff {
   public SpiderCharmBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      if (!var2.wasPrevented && var1.owner.isServer()) {
         Mob var3 = var2.attacker != null ? var2.attacker.getAttackOwner() : null;
         if (var3 != null) {
            var3.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_CHARM_POISON, var3, 5.0F, var1.owner), true);
         }
      }

   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "spidercharm"));
      return var4;
   }
}
