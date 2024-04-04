package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;

public class MyceliumHoodActiveBuff extends Buff {
   public MyceliumHoodActiveBuff() {
      this.shouldSave = false;
      this.isVisible = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setMinModifier(BuffModifiers.SLOW, 1.0F, 1000000);
      var1.setModifier(BuffModifiers.RANGED_ATTACK_SPEED, 1.0F);
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
         float[] var2 = new float[]{40.0F, 140.0F, 220.0F, 320.0F};
         float[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            float var6 = var3[var5];
            Point2D.Float var7 = GameMath.getAngleDir(var6);
            var1.owner.getLevel().entityManager.addParticle(var1.owner.x, var1.owner.y, Particle.GType.IMPORTANT_COSMETIC).color(new Color(158, 82, 8)).sizeFadesInAndOut(5, 10, 50, 200).movesConstant(var7.x * 10.0F, var7.y * 10.0F).lifeTime(500).heightMoves(8.0F, 0.0F);
         }
      }

   }
}
