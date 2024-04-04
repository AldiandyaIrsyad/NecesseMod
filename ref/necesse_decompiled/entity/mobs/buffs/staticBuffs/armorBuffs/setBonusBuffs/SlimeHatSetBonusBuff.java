package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class SlimeHatSetBonusBuff extends SetBonusBuff implements BuffAbility {
   public IntUpgradeValue maxMana = (new IntUpgradeValue(0, 0.1F)).setBaseValue(250).setUpgradedValue(1.0F, 250);

   public SlimeHatSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(this.getUpgradeTier(var1)));
      var1.setModifier(BuffModifiers.PROJECTILE_BOUNCES, 2);
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      Mob var4 = var2.owner;
      float var5 = 5.0F;
      float var6 = 60.0F;
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_SET_COOLDOWN, var4, var6, (Attacker)null), false);
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.SLIME_DOME_ACTIVE, var4, var5, (Attacker)null), false);
   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.buffManager.hasBuff(BuffRegistry.Debuffs.SLIME_SET_COOLDOWN.getID());
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "slimeset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
