package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class VoidMaskSetBonusBuff extends SetBonusBuff implements BuffAbility {
   public IntUpgradeValue maxSummons = (new IntUpgradeValue()).setBaseValue(1).setUpgradedValue(1.0F, 2);

   public VoidMaskSetBonusBuff() {
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if (var2.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(65, 30, 109)).height(16.0F);
      }

   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.addModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(this.getUpgradeTier(var1)));
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "voidset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      Mob var4 = var2.owner;
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, var4, 3.0F, (Attacker)null), false);
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.VOID_SET_COOLDOWN, var4, 30.0F, (Attacker)null), false);
      if (var4.isClient()) {
         for(int var5 = 0; var5 < 12; ++var5) {
            var1.getLevel().entityManager.addParticle(var1.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var1.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(var1.dx, var1.dy).color(new Color(65, 30, 109)).height(16.0F);
         }

         Screen.playSound(GameResources.teleportfail, SoundEffect.effect(var4).pitch(1.3F));
      }

   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.buffManager.hasBuff(BuffRegistry.Debuffs.VOID_SET_COOLDOWN.getID());
   }
}
