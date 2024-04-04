package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;

public class SandKnifeWoundBuff extends Buff {
   public SandKnifeWoundBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, 100.0F);
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         AtomicReference var3 = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
         float var4 = 20.0F;
         var2.getLevel().entityManager.addParticle(var2.x + GameMath.sin((Float)var3.get()) * var4, var2.y + GameMath.cos((Float)var3.get()) * var4 * 0.75F, Particle.GType.CRITICAL).color(new Color(192, 200, 170)).height(0.0F).moves((var3x, var4x, var5, var6, var7) -> {
            float var8 = (Float)var3.accumulateAndGet(var4x * 150.0F / 250.0F, Float::sum);
            float var9 = var4 * 0.75F;
            var3x.x = var2.x + GameMath.sin(var8) * var4;
            var3x.y = var2.y + GameMath.cos(var8) * var9 * 0.75F;
         }).lifeTime(1000).sizeFades(16, 24);
      }

   }
}
