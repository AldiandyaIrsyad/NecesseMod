package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.gameTexture.GameTexture;

public class SpriteTooltip implements GameTooltips {
   public final GameTexture texture;
   public final int spriteX;
   public final int spriteY;
   public final int spriteRes;
   public final int width;
   public final int height;

   public SpriteTooltip(GameTexture var1, int var2, int var3, int var4, int var5, int var6) {
      this.texture = var1;
      this.spriteX = var2;
      this.spriteY = var3;
      this.spriteRes = var4;
      this.width = var5;
      this.height = var6;
   }

   public SpriteTooltip(GameTexture var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, var4, var4);
   }

   public SpriteTooltip(GameTexture var1) {
      this(var1, 0, 0, var1.getWidth());
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      this.texture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).draw(var1, var2);
   }

   public int getDrawOrder() {
      return Integer.MIN_VALUE;
   }
}
