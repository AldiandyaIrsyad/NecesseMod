package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class GuardianShellTrinketBuff extends TrinketBuff implements BuffAbility {
   public GuardianShellTrinketBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      float var4 = 5.0F;
      float var5 = 30.0F;
      var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.GUARDIAN_SHELL_COOLDOWN, var1, var5, (Attacker)null), false);
      var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.GUARDIAN_SHELL_ACTIVE, var1, var4, (Attacker)null), false);
      var1.buffManager.forceUpdateBuffs();
      byte var6 = 0;
      byte var7 = 40;
      byte var8 = 40;

      for(int var9 = 0; var9 < var8; ++var9) {
         float var10 = (float)var6 + (float)(var7 - var6) * (float)var9 / (float)var8;
         AtomicReference var11 = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
         float var12 = 20.0F;
         var1.getLevel().entityManager.addParticle(var1.x + GameMath.sin((Float)var11.get()) * var12, var1.y + GameMath.cos((Float)var11.get()) * var12 * 0.75F, Particle.GType.CRITICAL).color(new Color(78, 112, 31)).height(var10).moves((var3x, var4x, var5x, var6x, var7x) -> {
            float var8 = (Float)var11.accumulateAndGet(var4x * 150.0F / 250.0F, Float::sum);
            float var9 = var12 * 0.75F;
            var3x.x = var1.x + GameMath.sin(var8) * var12;
            var3x.y = var1.y + GameMath.cos(var8) * var9 * 0.75F;
         }).lifeTime((int)(var4 * 1000.0F)).sizeFades(16, 24);
      }

   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.buffManager.hasBuff(BuffRegistry.Debuffs.GUARDIAN_SHELL_COOLDOWN);
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      var4.add(Localization.translate("itemtooltip", "guardianshelltip"));
      return var4;
   }
}
