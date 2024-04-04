package necesse.inventory.item;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;

public class SwingSpriteAttackItem extends Item {
   public SwingSpriteAttackItem() {
      super(1);
      this.attackAnimTime.setBaseValue(250);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("SWING_ATTACK");
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      int var3 = var1.getGndData().getInt("itemID");
      if (var3 != -1) {
         Item var4 = ItemRegistry.getItem(var3);
         if (var4 != null) {
            return new GameSprite(var4.getItemSprite(var1, var2), 24);
         }
      }

      return null;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      boolean var6 = var1.getGndData().getBoolean("inverted");
      if (var6) {
         var2.swingRotationInv(var5);
      } else {
         var2.swingRotation(var5);
      }

   }
}
