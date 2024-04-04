package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class ChallengersPauldronBuff extends TrinketBuff {
   public ChallengersPauldronBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void clientTick(ActiveBuff var1) {
      this.updateModifiers(var1);
      if (var1.owner.isVisible() && var1.owner.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY)) {
         Mob var2 = var1.owner;
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 3), 0, 22)).sizeFades(10, 30).movesConstant(var2.dx / 10.0F, -20.0F).flameColor(50.0F).givesLight(50.0F, 1.0F).height(16.0F);
      }

   }

   public void serverTick(ActiveBuff var1) {
      this.updateModifiers(var1);
   }

   public void updateModifiers(ActiveBuff var1) {
      if (var1.owner.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY)) {
         var1.setModifier(BuffModifiers.RESILIENCE_GAIN, 1.0F);
      } else {
         var1.setModifier(BuffModifiers.RESILIENCE_GAIN, 0.0F);
      }

   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "challengerspauldrontip"));
      return var4;
   }
}
