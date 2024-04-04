package necesse.inventory.item.placeableItem.consumableItem.food;

import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MatItem;

public class FoodMatItem extends MatItem {
   protected String cropTextureName;

   public FoodMatItem(int var1, String... var2) {
      super(var1, var2);
      this.setItemCategory(new String[]{"consumable", "rawfood"});
      this.dropDecayTimeMillis = 1800000L;
   }

   public FoodMatItem(int var1, Item.Rarity var2, String... var3) {
      super(var1, var2, var3);
      this.setItemCategory(new String[]{"consumable", "rawfood"});
      this.dropDecayTimeMillis = 1800000L;
   }

   public FoodMatItem(int var1, Item.Rarity var2, String var3) {
      super(var1, var2, var3);
      this.dropDecayTimeMillis = 1800000L;
   }

   public FoodMatItem(int var1, Item.Rarity var2, String var3, String... var4) {
      super(var1, var2, var3, var4);
      this.dropDecayTimeMillis = 1800000L;
   }

   public FoodMatItem cropTexture(String var1) {
      this.cropTextureName = var1;
      return this;
   }

   protected void loadItemTextures() {
      if (this.cropTextureName != null) {
         this.itemTexture = new GameTexture(GameTexture.fromFile("objects/" + this.cropTextureName), 0, 0, 32);
      } else {
         super.loadItemTextures();
      }

   }
}
