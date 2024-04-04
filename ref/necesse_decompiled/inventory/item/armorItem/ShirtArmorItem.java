package necesse.inventory.item.armorItem;

import java.awt.Color;
import necesse.engine.GlobalData;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public class ShirtArmorItem extends ChestArmorItem {
   public ShirtArmorItem(int var1) {
      super(var1, 0, (String)null, (String)null);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      if (GlobalData.debugActive() && var1 != null) {
         Color var5 = getColor(var1.getGndData());
         var4.add("Color: " + var5.getRed() + ", " + var5.getGreen() + ", " + var5.getBlue());
      }

      return var4;
   }

   public void loadItemTextures() {
      this.itemTexture = GameTexture.fromFile("items/shirt");
   }

   protected void loadArmorTexture() {
      this.armorTexture = GameTexture.fromFile("player/armor/shirt");
      this.leftArmsTexture = GameTexture.fromFile("player/armor/shirtarms_left");
      this.rightArmsTexture = GameTexture.fromFile("player/armor/shirtarms_right");
   }

   public Color getDrawColor(InventoryItem var1, PlayerMob var2) {
      return var1 != null ? getColor(var1.getGndData()) : super.getDrawColor(var1, var2);
   }

   public InventoryItem getDefaultItem(PlayerMob var1, int var2) {
      return addColorData(super.getDefaultItem(var1, var2), var1 == null ? new Color(255, 255, 255) : var1.look.getShirtColor());
   }

   public static Color getColor(GNDItemMap var0) {
      int var1 = var0.getByte("red") & 255;
      int var2 = var0.getByte("green") & 255;
      int var3 = var0.getByte("blue") & 255;
      return new Color(var1, var2, var3);
   }

   public static GNDItemMap getColorData(Color var0) {
      return addColorData((GNDItemMap)null, var0);
   }

   public static GNDItemMap addColorData(GNDItemMap var0, Color var1) {
      if (var0 == null) {
         var0 = new GNDItemMap();
      }

      var0.setByte("red", (byte)var1.getRed());
      var0.setByte("green", (byte)var1.getGreen());
      var0.setByte("blue", (byte)var1.getBlue());
      return var0;
   }

   public static InventoryItem addColorData(InventoryItem var0, Color var1) {
      addColorData(var0.getGndData(), var1);
      return var0;
   }
}
