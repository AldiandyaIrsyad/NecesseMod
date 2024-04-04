package necesse.inventory.item;

import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;

public abstract class ItemControllerInteract {
   public int levelX;
   public int levelY;

   public ItemControllerInteract(int var1, int var2) {
      this.levelX = var1;
      this.levelY = var2;
   }

   public abstract DrawOptions getDrawOptions(GameCamera var1);

   public abstract void onCurrentlyFocused(GameCamera var1);
}
