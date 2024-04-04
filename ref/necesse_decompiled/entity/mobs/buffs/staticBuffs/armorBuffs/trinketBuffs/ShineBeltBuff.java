package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class ShineBeltBuff extends TrinketBuff {
   public ShineBeltBuff() {
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "shinebelttip"));
      return var4;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      Level var2 = var1.owner.getLevel();
      if (var2.tickManager().getTotalTicks() % 5L == 0L) {
         var2.entityManager.addParticle(var1.owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var1.owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(var1.owner.dx / 10.0F, var1.owner.dy / 10.0F).color(new Color(249, 226, 117)).sizeFades(6, 10).givesLight(50.0F, 0.4F).height(16.0F);
      }

      var2.lightManager.refreshParticleLightFloat(var1.owner.x, var1.owner.y, 50.0F, 0.4F, 135);
   }
}
