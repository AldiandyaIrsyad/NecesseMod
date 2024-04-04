package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class SettlerSprintBuff extends Buff implements MovementTickBuff {
   public SettlerSprintBuff() {
      this.shouldSave = false;
      this.isVisible = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SPEED_FLAT, 15.0F);
      var1.setModifier(BuffModifiers.SPEED, 0.5F);
   }

   public void tickMovement(ActiveBuff var1, float var2) {
      Mob var3 = var1.owner;
      if (var3.isClient() && (var3.dx != 0.0F || var3.dy != 0.0F)) {
         float var4 = var3.getCurrentSpeed() * var2 / 250.0F;
         GNDItemMap var5 = var1.getGndData();
         float var6 = var5.getFloat("particleBuffer") + var4;
         float var7;
         if (var6 >= 15.0F) {
            var6 -= 15.0F;
            var7 = GameRandom.globalRandom.floatGaussian() * 2.0F;
            float var8 = GameRandom.globalRandom.floatGaussian() * 2.0F;
            boolean var9 = var5.getBoolean("particleAlternate");
            var5.setBoolean("particleAlternate", !var9);
            if (var3.dir != 0 && var3.dir != 2) {
               var8 += var9 ? 4.0F : -4.0F;
            } else {
               var7 += var9 ? 4.0F : -4.0F;
            }

            float var10001 = var3.x + var7;
            float var10002 = var3.y + var8 - 2.0F;
            var3.getLevel().entityManager.addParticle(var10001, var10002, Particle.GType.IMPORTANT_COSMETIC).color(new Color(195, 222, 202)).sizeFadesInAndOut(10, 16, 50, 200).movesConstant(var3.dx / 10.0F, var3.dy / 10.0F).lifeTime(300).height(2.0F);
         }

         var5.setFloat("particleBuffer", var6);
         var7 = var5.getFloat("soundBuffer") + Math.min(var4, 80.0F * var2 / 250.0F);
         if (var7 >= 45.0F) {
            var7 -= 45.0F;
            Screen.playSound(GameResources.run, SoundEffect.effect(var3).volume(0.1F).pitch(1.1F));
         }

         var5.setFloat("soundBuffer", var7);
      }

   }
}
