package necesse.inventory.item.trinketItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class CactusShieldTrinketItem extends ShieldTrinketItem {
   public CactusShieldTrinketItem(Item.Rarity var1, int var2) {
      super(var1, 4, 0.5F, 8000, 0.25F, 40, 360.0F, var2);
   }

   public ListGameTooltips getExtraShieldTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getExtraShieldTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "cactusshieldtip"));
      return var4;
   }

   public void onShieldHit(InventoryItem var1, Mob var2, MobWasHitEvent var3) {
      super.onShieldHit(var1, var2, var3);
      if (var2.isServer() && !var3.wasPrevented) {
         Mob var4 = var3.attacker != null ? var3.attacker.getAttackOwner() : null;
         boolean var5 = var3.attacker != null && var3.attacker.isInAttackOwnerChain(var2);
         if (var4 != null && !var5) {
            float var6 = var4.x - var2.x;
            float var7 = var4.y - var2.y;
            float var8 = this.getShieldFinalDamageMultiplier(var1, var2);
            if (var8 > 0.0F) {
               float var9 = (float)var3.damage / var8;
               if (var4.isPlayer) {
                  var9 /= 2.0F;
               }

               var4.isServerHit(new GameDamage(var9, 0.0F), var6, var7, 0.0F, var2);
            }
         }
      }

   }
}
