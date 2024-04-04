package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class MobilityCloakBuff extends OutOfCombatBuff {
   public MobilityCloakBuff() {
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if (this.isActive(var1) && (var2.dx != 0.0F || var2.dy != 0.0F) && var2.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), Particle.GType.IMPORTANT_COSMETIC).color(new Color(65, 30, 109)).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).lifeTime(300).height(2.0F);
      }

   }

   protected void updateActive(ActiveBuff var1, boolean var2) {
      if (var2) {
         var1.setModifier(BuffModifiers.SPEED_FLAT, 5.0F);
         var1.setModifier(BuffModifiers.SPEED, 0.15F);
      }

   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "mobilitycloak"));
      return var4;
   }
}
