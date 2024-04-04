package necesse.inventory.item.miscItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;

public class CoinItem extends ObjectItem {
   public CoinItem(GameObject var1) {
      super(var1);
      this.keyWords.add("currency");
      this.worldDrawSize = 28;
      this.attackAnimTime.setBaseValue(100);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "cointip"));
      return var4;
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.itemTexture, 0, 0, 32);
   }

   public GameSprite getWorldItemSprite(InventoryItem var1, PlayerMob var2) {
      if (var2 != null && var2.getWorldEntity() != null) {
         long var3 = var2.getWorldEntity().getTime() % 2500L;
         int var5;
         if (var3 < 2000L) {
            var5 = 0;
         } else {
            var5 = GameUtils.getAnim(var3 - 2000L, this.itemTexture.getWidth() / 32, 500);
         }

         return new GameSprite(this.itemTexture, var5, 0, 32);
      } else {
         return super.getWorldItemSprite(var1, var2);
      }
   }

   public void tickPickupEntity(ItemPickupEntity var1) {
      super.tickPickupEntity(var1);
      if (var1.isClient()) {
         boolean var2 = (Math.abs(var1.dx) > 2.0F || Math.abs(var1.dy) > 2.0F) && GameRandom.globalRandom.getChance(2);
         if (var1.getTarget() != null || var2 || GameRandom.globalRandom.getChance(20)) {
            ParticleOption var3 = var1.getLevel().entityManager.addParticle(var1.x + GameRandom.globalRandom.floatGaussian() * 2.0F, var1.y + 2.0F + GameRandom.globalRandom.floatGaussian() * 2.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 4.0F, GameRandom.globalRandom.floatGaussian() * 4.0F).sizeFades(6, 12).color(new Color(-1123718)).heightMoves(0.0F, 10.0F);
            if (var2) {
               var3.givesLight(50.0F, 0.8F);
            }
         }
      }

   }
}
