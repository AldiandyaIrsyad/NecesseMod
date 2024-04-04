package necesse.inventory.item;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.light.GameLight;

public class FishingPoleHolding extends Item {
   public FishingPoleHolding() {
      super(1);
      this.attackAnimTime.setBaseValue(5000);
   }

   public static InventoryItem setGNDData(InventoryItem var0, FishingRodItem var1) {
      var0.getGndData().setInt("fishingRod", var1.getID());
      return var0;
   }

   public FishingRodItem getFishingRodItem(InventoryItem var1) {
      int var2 = var1.getGndData().getInt("fishingRod");
      Item var3 = ItemRegistry.getItem(var2);
      return var3 instanceof FishingRodItem ? (FishingRodItem)var3 : null;
   }

   public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions var1, InventoryItem var2, PlayerMob var3, int var4, float var5, float var6, float var7, Color var8, GameLight var9) {
      FishingRodItem var10 = this.getFishingRodItem(var2);
      if (var10 != null) {
         ItemAttackDrawOptions.AttackItemSprite var11 = var1.itemSprite(this.getAttackSprite(var2, var3));
         var11.itemRotatePoint(var10.attackXOffset, var10.attackYOffset);
         if (var8 != null) {
            var11.itemColor(var8);
         }

         return var11.itemEnd();
      } else {
         return super.setupItemSpriteAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      FishingRodItem var3 = this.getFishingRodItem(var1);
      return var3 != null ? var3.getAttackSprite(var1, var2) : super.getAttackSprite(var1, var2);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage(this.getStringID());
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add("NOT OBTAINABLE");
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.rotation(0.0F);
   }
}
