package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import necesse.engine.modifiers.ModifierUpgradeValue;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class DemonicSetBonusBuff extends SimpleUpgradeSetBonusBuff {
   public DemonicSetBonusBuff() {
      super(new ModifierUpgradeValue(BuffModifiers.MAX_RESILIENCE_FLAT, (new IntUpgradeValue()).setBaseValue(10).setUpgradedValue(1.0F, 30)), new ModifierUpgradeValue(BuffModifiers.ARMOR_FLAT, (new IntUpgradeValue()).setBaseValue(3).setUpgradedValue(1.0F, 6)), new ModifierUpgradeValue(BuffModifiers.CRIT_CHANCE, (new FloatUpgradeValue()).setBaseValue(0.1F).setUpgradedValue(1.0F, 0.2F)));
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if ((var2.dx != 0.0F || var2.dy != 0.0F) && var2.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), Particle.GType.COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(83, 67, 119)).height((float)(5 + GameRandom.globalRandom.nextInt(20)));
      }

   }
}
