package necesse.gfx.drawOptions.texture;

import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL13;

public class ShaderTexture {
   public final int glPos;
   public final GameTexture texture;
   protected float spriteX1;
   protected float spriteY1;
   protected float spriteX2;
   protected float spriteY2;
   private final int[] glTexturePositions;

   public ShaderTexture(int var1, GameTexture var2) {
      this.glTexturePositions = new int[]{33984, 33985, 33986, 33987, 33988, 33989, 33990, 33991, 33992, 33993, 33994, 33995, 33996, 33997, 33998, 33999, 34000, 34001, 34002, 34003, 34004, 34005, 34006, 34007, 34008, 34009, 34010, 34011, 34012, 34013, 34014, 34015};
      this.glPos = this.toGlPos(var1);
      this.texture = var2;
      this.spriteX1 = 0.0F;
      this.spriteX2 = 1.0F;
      this.spriteY1 = 0.0F;
      this.spriteY2 = 1.0F;
   }

   public ShaderTexture(int var1, GameTexture var2, float var3, float var4, float var5, float var6) {
      this(var1, var2);
      this.spriteX1 = var3;
      this.spriteX2 = var4;
      this.spriteY1 = var5;
      this.spriteY2 = var6;
   }

   public ShaderTexture(int var1, GameSprite var2) {
      this(var1, var2.texture);
      this.spriteX1 = TextureDrawOptions.pixel(var2.spriteX, var2.spriteWidth, this.texture.getWidth());
      this.spriteY1 = TextureDrawOptions.pixel(var2.spriteY, var2.spriteHeight, this.texture.getHeight());
      this.spriteX2 = TextureDrawOptions.pixel(var2.spriteX + 1, var2.spriteWidth, this.texture.getWidth());
      this.spriteY2 = TextureDrawOptions.pixel(var2.spriteY + 1, var2.spriteHeight, this.texture.getHeight());
   }

   public ShaderTexture(int var1, GameTexture var2, int var3, int var4, int var5) {
      this(var1, var2);
      this.spriteX1 = TextureDrawOptions.pixel(var3, var5, var2.getWidth());
      this.spriteY1 = TextureDrawOptions.pixel(var4, var5, var2.getHeight());
      this.spriteX2 = TextureDrawOptions.pixel(var3 + 1, var5, var2.getWidth());
      this.spriteY2 = TextureDrawOptions.pixel(var4 + 1, var5, var2.getHeight());
   }

   public ShaderTexture(int var1, GameTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      this(var1, var2);
      this.spriteX1 = TextureDrawOptions.pixel(var3, var6, var5, var2.getWidth());
      this.spriteY1 = TextureDrawOptions.pixel(var4, var8, var5, var2.getHeight());
      this.spriteX2 = TextureDrawOptions.pixel(var3, var7, var5, var2.getWidth());
      this.spriteY2 = TextureDrawOptions.pixel(var4, var9, var5, var2.getHeight());
   }

   private int toGlPos(int var1) {
      if (var1 >= 0 && var1 < this.glTexturePositions.length) {
         return this.glTexturePositions[var1];
      } else {
         System.err.println("Could not find shader texture position " + var1);
         return 33984;
      }
   }

   public void bind() {
      this.texture.bind(this.glPos);
   }

   public void startTopLeft() {
      GL13.glMultiTexCoord2f(this.glPos, this.spriteX1, this.spriteY1);
   }

   public void startTopRight() {
      GL13.glMultiTexCoord2f(this.glPos, this.spriteX2, this.spriteY1);
   }

   public void startBotRight() {
      GL13.glMultiTexCoord2f(this.glPos, this.spriteX2, this.spriteY2);
   }

   public void startBotLeft() {
      GL13.glMultiTexCoord2f(this.glPos, this.spriteX1, this.spriteY2);
   }
}
