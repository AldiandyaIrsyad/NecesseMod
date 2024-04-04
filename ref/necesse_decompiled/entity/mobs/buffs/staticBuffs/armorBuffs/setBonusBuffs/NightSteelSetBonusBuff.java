package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

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
import necesse.gfx.gameTooltips.ListGameTooltips;

public class NightSteelSetBonusBuff extends SetBonusBuff implements BuffAbility {
   public NightSteelSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      Mob var4 = var2.owner;
      float var5 = 3.0F;
      float var6 = 60.0F;
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.NIGHTSTEEL_SET_COOLDOWN, var4, var6, (Attacker)null), false);
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.NIGHTSTEEL_ACTIVE, var4, var5, (Attacker)null), false);
   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.buffManager.hasBuff(BuffRegistry.Debuffs.NIGHTSTEEL_SET_COOLDOWN.getID());
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "nightsteelset"), 400);
      return var3;
   }
}
