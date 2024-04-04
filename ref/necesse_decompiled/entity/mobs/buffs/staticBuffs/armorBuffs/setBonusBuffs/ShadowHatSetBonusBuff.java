package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class ShadowHatSetBonusBuff extends SetBonusBuff {
   public FloatUpgradeValue speed = (new FloatUpgradeValue()).setBaseValue(0.05F).setUpgradedValue(1.0F, 0.1F);
   public FloatUpgradeValue projectileVelocity = (new FloatUpgradeValue()).setBaseValue(0.15F).setUpgradedValue(1.0F, 0.3F);
   public IntUpgradeValue maxMana = (new IntUpgradeValue(0, 0.1F)).setBaseValue(200).setUpgradedValue(1.0F, 250);

   public ShadowHatSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(var1)));
      var1.setModifier(BuffModifiers.PROJECTILE_VELOCITY, this.projectileVelocity.getValue(this.getUpgradeTier(var1)));
      var1.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(this.getUpgradeTier(var1)));
   }

   public void onHasAttacked(ActiveBuff var1, MobWasHitEvent var2) {
      super.onHasAttacked(var1, var2);
      if (!var2.wasPrevented && var2.damageType == DamageTypeRegistry.MAGIC) {
         var2.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.HAUNTED, var2.target, 5.0F, var2.attacker), var2.target.isServer());
      }

   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if (var2.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(33, 35, 42)).height(16.0F);
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "shadowhatset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
