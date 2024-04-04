package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class AncientFossilSetBonusBuff extends SetBonusBuff implements BuffAbility {
   public AncientFossilSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      Mob var4 = var2.owner;
      float var5 = 5.0F;
      float var6 = 60.0F;
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ANCIENT_FOSSIL_SET_COOLDOWN, var4, var6, (Attacker)null), false);
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.ANCIENT_FOSSIL_ACTIVE, var4, var5, (Attacker)null), false);
      byte var7 = 0;
      byte var8 = 40;
      byte var9 = 40;

      for(int var10 = 0; var10 < var9; ++var10) {
         float var11 = (float)var7 + (float)(var8 - var7) * (float)var10 / (float)var9;
         AtomicReference var12 = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
         float var13 = 20.0F;
         var1.getLevel().entityManager.addParticle(var1.x + GameMath.sin((Float)var12.get()) * var13, var1.y + GameMath.cos((Float)var12.get()) * var13 * 0.75F, Particle.GType.CRITICAL).color(new Color(95, 82, 44)).height(var11).moves((var3x, var4x, var5x, var6x, var7x) -> {
            float var8 = (Float)var12.accumulateAndGet(var4x * 150.0F / 250.0F, Float::sum);
            float var9 = var13 * 0.75F;
            var3x.x = var1.x + GameMath.sin(var8) * var13;
            var3x.y = var1.y + GameMath.cos(var8) * var9 * 0.75F;
         }).lifeTime((int)(var5 * 1000.0F)).sizeFades(16, 24);
      }

   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.buffManager.hasBuff(BuffRegistry.Debuffs.ANCIENT_FOSSIL_SET_COOLDOWN.getID());
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "ancientfossilset"));
      return var3;
   }
}
