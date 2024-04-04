package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class ShadowHoodSetBonusBuff extends SetBonusBuff {
   public FloatUpgradeValue speed = (new FloatUpgradeValue()).setBaseValue(0.1F).setUpgradedValue(1.0F, 0.2F);
   public FloatUpgradeValue critChance = (new FloatUpgradeValue()).setBaseValue(0.05F).setUpgradedValue(1.0F, 0.1F);
   public FloatUpgradeValue projectileVelocity = (new FloatUpgradeValue()).setBaseValue(0.15F).setUpgradedValue(1.0F, 0.3F);

   public ShadowHoodSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(var1)));
      var1.setModifier(BuffModifiers.CRIT_CHANCE, this.critChance.getValue(this.getUpgradeTier(var1)));
      var1.setModifier(BuffModifiers.PROJECTILE_VELOCITY, this.projectileVelocity.getValue(this.getUpgradeTier(var1)));
   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      Mob var2 = var1.owner;

      while(true) {
         Mob var3 = var2.getMount();
         if (var3 == null) {
            if (var2.dx == 0.0F && var2.dy == 0.0F) {
               if (!var1.owner.buffManager.hasBuff(BuffRegistry.SHADOWHOOD_SET.getID())) {
                  var1.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.SHADOWHOOD_SET, var1.owner, 0, (Attacker)null), false);
               }
            } else {
               if (var1.owner.buffManager.hasBuff(BuffRegistry.SHADOWHOOD_SET.getID())) {
                  var1.owner.buffManager.removeBuff(BuffRegistry.SHADOWHOOD_SET.getID(), false);
               }

               var1.setModifier(BuffModifiers.ALL_DAMAGE, (Float)BuffModifiers.ALL_DAMAGE.defaultBuffValue);
            }

            return;
         }

         var2 = var3;
      }
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      Mob var2 = var1.owner;

      while(true) {
         Mob var3 = var2.getMount();
         if (var3 == null) {
            if (var2.dx == 0.0F && var2.dy == 0.0F) {
               if (!var1.owner.buffManager.hasBuff(BuffRegistry.SHADOWHOOD_SET.getID())) {
                  var1.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.SHADOWHOOD_SET, var1.owner, 0, (Attacker)null), false);
               }
            } else {
               if (var1.owner.buffManager.hasBuff(BuffRegistry.SHADOWHOOD_SET.getID())) {
                  var1.owner.buffManager.removeBuff(BuffRegistry.SHADOWHOOD_SET.getID(), false);
               }

               var1.setModifier(BuffModifiers.ALL_DAMAGE, (Float)BuffModifiers.ALL_DAMAGE.defaultBuffValue);
            }

            return;
         }

         var2 = var3;
      }
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if (var2.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(33, 35, 42)).height(16.0F);
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "shadowhoodset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
