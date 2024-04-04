package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;

public class GreatswordChargeLevel {
   public int timeToCharge;
   public float damageModifier;
   public Color particleColors;

   public GreatswordChargeLevel(int var1, float var2, Color var3) {
      this.timeToCharge = var1;
      this.damageModifier = var2;
      this.particleColors = var3;
   }

   public void setupAttackItem(GreatswordAttackHandler var1, InventoryItem var2) {
      var2.getGndData().setFloat("chargeDamageMultiplier", this.damageModifier);
   }

   public void onReachedLevel(GreatswordAttackHandler var1) {
      if (var1.player.isClient()) {
         if (this.particleColors != null) {
            var1.drawParticleExplosion(30, this.particleColors, 30, 50);
         }

         int var2 = var1.chargeLevels.length;
         float var3 = (float)(var1.currentChargeLevel + 1) / (float)var2;
         float var4 = Math.max(0.7F, 1.0F - (float)var2 * 0.1F);
         float var5 = GameMath.lerp(var3, 1.0F, var4);
         Screen.playSound(GameResources.cling, SoundEffect.effect(var1.player).volume(0.5F).pitch(var5));
      }

   }

   public void updateAtLevel(GreatswordAttackHandler var1, InventoryItem var2) {
      if (var1.player.isClient() && this.particleColors != null) {
         var1.drawWeaponParticles(var2, this.particleColors);
      }

   }
}
