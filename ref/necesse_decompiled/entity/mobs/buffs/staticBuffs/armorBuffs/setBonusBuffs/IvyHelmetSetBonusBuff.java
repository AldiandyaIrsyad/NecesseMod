package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderSpitEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class IvyHelmetSetBonusBuff extends SetBonusBuff {
   public FloatUpgradeValue spitDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(35.0F).setUpgradedValue(1.0F, 75.0F);
   public IntUpgradeValue maxResilience = (new IntUpgradeValue()).setBaseValue(20).setUpgradedValue(1.0F, 30);

   public IvyHelmetSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(this.getUpgradeTier(var1)));
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      if (!var2.wasPrevented && var1.owner.isServer()) {
         CaveSpiderSpitEvent var3 = new CaveSpiderSpitEvent(var1.owner, var1.owner.getX(), var1.owner.getY(), GameRandom.globalRandom, GiantCaveSpiderMob.Variant.SWAMP, new GameDamage(DamageTypeRegistry.MELEE, this.spitDamage.getValue(this.getUpgradeTier(var1))), 6);
         var1.owner.getLevel().entityManager.addLevelEvent(var3);
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "ivyhelmetset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
