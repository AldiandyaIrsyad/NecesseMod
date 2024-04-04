package necesse.gfx.gameTexture;

import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;

public class GameSprite {
   public final GameTexture texture;
   public final int spriteX;
   public final int spriteY;
   public final int spriteWidth;
   public final int spriteHeight;
   public final int width;
   public final int height;
   public final boolean mirrorX;
   public final boolean mirrorY;

   public GameSprite(GameTexture var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9) {
      this.texture = var1;
      this.spriteX = var2;
      this.spriteY = var3;
      this.spriteWidth = var4;
      this.spriteHeight = var5;
      this.width = var6;
      this.height = var7;
      this.mirrorX = var8;
      this.mirrorY = var9;
   }

   public GameSprite(GameTexture var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, false, false);
   }

   public GameSprite(GameTexture var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var4, var5, var6);
   }

   public GameSprite(GameSprite var1, int var2, int var3) {
      this(var1.texture, var1.spriteX, var1.spriteY, var1.spriteWidth, var1.spriteHeight, var2, var3);
   }

   public GameSprite(GameTexture var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, var5);
   }

   public GameSprite(GameSprite var1, int var2) {
      this(var1.texture, var1.spriteX, var1.spriteY, var1.spriteWidth, var1.spriteHeight, var1.width < var1.height ? (int)((float)var2 * ((float)var1.width / (float)var1.height)) : var2, var1.height < var1.width ? (int)((float)var2 * ((float)var1.height / (float)var1.width)) : var2);
   }

   public GameSprite(GameTexture var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, var4, var4);
   }

   public GameSprite(GameTexture var1, int var2) {
      this(var1, 0, 0, var1.getWidth(), var1.getHeight(), var1.getWidth() < var1.getHeight() ? (int)((float)var2 * ((float)var1.getWidth() / (float)var1.getHeight())) : var2, var1.getHeight() < var1.getWidth() ? (int)((float)var2 * ((float)var1.getHeight() / (float)var1.getWidth())) : var2);
   }

   public GameSprite(GameTexture var1) {
      this(var1, 0, 0, var1.getWidth(), var1.getHeight(), var1.getWidth(), var1.getHeight());
   }

   public GameSprite mirrorX() {
      return new GameSprite(this.texture, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, this.width, this.height, !this.mirrorX, this.mirrorY);
   }

   public GameSprite mirrorY() {
      return new GameSprite(this.texture, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, this.width, this.height, this.mirrorX, !this.mirrorY);
   }

   public GameSprite mirrored(boolean var1, boolean var2) {
      return new GameSprite(this.texture, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, this.width, this.height, var1 != this.mirrorX, var2 != this.mirrorY);
   }

   public TextureDrawOptionsEnd initDraw() {
      return this.texture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY);
   }

   public TextureDrawOptionsEnd initDrawSection(int var1, int var2, int var3, int var4, boolean var5) {
      return this.texture.initDraw().spriteSection(this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, var1, var2, var3, var4, var5).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY);
   }

   public TextureDrawOptionsEnd initDrawSection(int var1, int var2, int var3, int var4) {
      return this.texture.initDraw().spriteSection(this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight, var1, var2, var3, var4).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY);
   }

   public GameSprite subSprite(int var1, int var2, int var3, int var4) {
      if (this.spriteWidth % var3 != 0) {
         throw new IllegalArgumentException("Super spriteWidth must be divisible by new spriteWidth");
      } else if (this.spriteHeight % var4 != 0) {
         throw new IllegalArgumentException("Super spriteHeight must be divisible by new spriteHeight");
      } else {
         int var5 = this.spriteWidth / var3;
         int var6 = this.spriteHeight / var4;
         return new GameSprite(this.texture, this.spriteX * var5 + var1, this.spriteY * var6 + var2, var3, var4);
      }
   }

   public GameSprite subSprite(int var1, int var2, int var3) {
      return this.subSprite(var1, var2, var3, var3);
   }
}
