package necesse.inventory.item.placeableItem.objectItem;

import java.util.function.Supplier;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;

public class CustomObjectItem extends ObjectItem {
   private Supplier<GameTexture> textureSupplier;
   protected int spriteX;
   protected int spriteY;

   public CustomObjectItem(GameObject var1, Supplier<GameTexture> var2, int var3, int var4) {
      super(var1);
      this.textureSupplier = var2;
      this.spriteX = var3;
      this.spriteY = var4;
   }

   public CustomObjectItem(GameObject var1, String var2, int var3, int var4) {
      this(var1, () -> {
         return GameTexture.fromFile(var2);
      }, var3, var4);
   }

   public void loadItemTextures() {
      this.itemTexture = (GameTexture)this.textureSupplier.get();
      this.textureSupplier = null;
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.itemTexture, this.spriteX, this.spriteY, 32);
   }
}
