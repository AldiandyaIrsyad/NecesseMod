package necesse.inventory.item;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WorkSpriteAttackItem extends Item {
   public WorkSpriteAttackItem() {
      super(1);
      this.attackAnimTime.setBaseValue(250);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("WORK_ATTACK");
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

   public DrawOptions getAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, float var5, float var6, float var7, GameSprite var8, InventoryItem var9, int var10, int var11, GameLight var12) {
      ItemAttackDrawOptions var13 = this.setupAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var12);
      var7 *= 2.0F;
      this.setDrawAttackRotation(var1, var13, var5, var6, var7);
      if (var7 > 1.0F) {
         --var7;
      }

      int var14 = (int)((double)var5 * Math.sin((double)var7 * Math.PI) * 5.0) - (int)(var5 * 5.0F);
      int var15 = (int)((double)var6 * Math.sin((double)var7 * Math.PI) * 5.0) - (int)(var6 * 5.0F);
      return var13.pos(var10 + var14, var11 + var15);
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      float var6 = (float)Math.sin((double)var5 * Math.PI * 2.0) * 10.0F;
      if (var5 > 0.5F) {
         var6 = -var6;
      }

      var2.pointRotation(var3, var4, var6);
   }
}
