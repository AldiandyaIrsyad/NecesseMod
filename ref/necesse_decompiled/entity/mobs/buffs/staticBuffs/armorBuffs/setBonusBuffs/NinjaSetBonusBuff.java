package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.NinjaShadowParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class NinjaSetBonusBuff extends SetBonusBuff {
   public NinjaSetBonusBuff() {
      this.isVisible = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.ATTACK_MOVEMENT_MOD, 0.0F);
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if ((var2.dx != 0.0F || var2.dy != 0.0F) && var2.getLevel().tickManager().getTotalTicks() % 3L == 0L && !var2.isRiding() && var2.isPlayer) {
         var2.getLevel().entityManager.addParticle((Particle)(new NinjaShadowParticle(var2.getLevel(), (PlayerMob)var2, var1 == null)), Particle.GType.COSMETIC);
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add((Object)(new StringTooltips(Localization.translate("itemtooltip", "ninjaset1"), GameColor.ITEM_RARE)));
      var3.add(Localization.translate("itemtooltip", "ninjaset2"));
      return var3;
   }
}
