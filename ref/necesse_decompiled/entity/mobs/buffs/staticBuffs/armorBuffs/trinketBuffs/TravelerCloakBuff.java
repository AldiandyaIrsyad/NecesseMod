package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LiquidTile;

public class TravelerCloakBuff extends OutOfCombatBuff {
   public TravelerCloakBuff() {
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
      if (this.isActive(var1) && (var2.dx != 0.0F || var2.dy != 0.0F)) {
         Color var3;
         if (var2.inLiquid()) {
            GameTile var4 = var2.getLevel().getTile(var2.getX() / 32, var2.getY() / 32);
            if (var4.isLiquid) {
               var3 = ((LiquidTile)var4).getLiquidColor(var2.getLevel(), var2.getX() / 32, var2.getY() / 32).brighter();
            } else {
               var3 = new Color(89, 139, 224);
            }
         } else {
            var3 = new Color(65, 30, 109);
         }

         boolean var6 = var1.getGndData().getBoolean("pAlt");
         var1.getGndData().setBoolean("pAlt", !var6);
         Point2D.Float var5;
         if (var2.dir != 0 && var2.dir != 2) {
            var5 = new Point2D.Float(var2.x, var2.y + (float)(var6 ? -4 : 4));
         } else {
            var5 = new Point2D.Float(var2.x + (float)(var6 ? -4 : 4), var2.y);
         }

         var2.getLevel().entityManager.addParticle(var5.x + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), var5.y + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), Particle.GType.IMPORTANT_COSMETIC).color(var3).sizeFades(10, 12).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).lifeTime(300).height(0.0F);
      }

   }

   protected void updateActive(ActiveBuff var1, boolean var2) {
      if (var2) {
         var1.setModifier(BuffModifiers.SPEED_FLAT, 5.0F);
         var1.setModifier(BuffModifiers.SPEED, 0.15F);
         var1.setModifier(BuffModifiers.SWIM_SPEED, 1.0F);
      }

   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "travelercloak"));
      return var4;
   }
}
