package necesse.inventory.item.trinketItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SiphonShieldTrinketItem extends ShieldTrinketItem {
   public SiphonShieldTrinketItem(Item.Rarity var1, int var2) {
      super(var1, 5, 0.5F, 3000, 0.1F, 50, 360.0F, var2);
   }

   public ListGameTooltips getExtraShieldTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getExtraShieldTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "siphonshieldtip"));
      return var4;
   }

   public void onShieldHit(InventoryItem var1, Mob var2, MobWasHitEvent var3) {
      if (var2.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
         var2.isManaExhausted = false;
         var2.buffManager.removeBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, false);
      }

      if (var2.isServer()) {
         float var4 = (float)var3.damage / (float)var2.getMaxHealth();
         var2.setMana(var2.getMana() + var4 * (float)var2.getMaxMana());
      } else {
         this.drawBlockParticles(var2);
      }

      super.onShieldHit(var1, var2, var3);
   }

   private void drawBlockParticles(Mob var1) {
      ParticleTypeSwitcher var2 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      byte var3 = 10;
      float var4 = 360.0F / (float)var3;

      for(int var5 = 0; var5 < var3; ++var5) {
         int var6 = (int)((float)var5 * var4 + GameRandom.globalRandom.nextFloat() * var4);
         float var7 = (float)Math.sin(Math.toRadians((double)var6)) * (float)GameRandom.globalRandom.getIntBetween(25, 50);
         float var8 = (float)Math.cos(Math.toRadians((double)var6)) * (float)GameRandom.globalRandom.getIntBetween(25, 50);
         var1.getLevel().entityManager.addParticle((Entity)var1, var2.next()).color(new Color(131, 198, 247)).movesFriction(var7, var8, 0.8F).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(30, 40).givesLight(180.0F, 200.0F).lifeTime(500);
      }

   }
}
