package necesse.gfx.drawOptions.texture;

public class TextureDrawOptionsStart extends TextureDrawOptionsEnd {
   TextureDrawOptionsStart(TextureDrawOptions var1) {
      super(var1);
   }

   public TextureDrawOptionsStart copy() {
      return new TextureDrawOptionsStart(super.copy());
   }

   public TextureDrawOptionsEnd section(int var1, int var2, int var3, int var4) {
      this.opts.spriteX1 = pixel(var1, this.texture.getWidth());
      this.opts.spriteX2 = pixel(var2, this.texture.getWidth());
      this.opts.spriteX3 = this.opts.spriteX2;
      this.opts.spriteX4 = this.opts.spriteX1;
      this.opts.spriteY1 = pixel(var3, this.texture.getHeight());
      this.opts.spriteY2 = this.opts.spriteY1;
      this.opts.spriteY3 = pixel(var4, this.texture.getHeight());
      this.opts.spriteY4 = this.opts.spriteY3;
      this.opts.width = Math.abs(var2 - var1);
      this.opts.height = Math.abs(var4 - var3);
      return new TextureDrawOptionsEnd(this);
   }

   public TextureDrawOptionsEnd sprite(int var1, int var2, int var3) {
      return this.sprite(var1, var2, var3, var3);
   }

   public TextureDrawOptionsEnd sprite(int var1, int var2, int var3, int var4) {
      this.opts.spriteX1 = pixel(var1, var3, this.texture.getWidth());
      this.opts.spriteX2 = pixel(var1 + 1, var3, this.texture.getWidth());
      this.opts.spriteX3 = this.opts.spriteX2;
      this.opts.spriteX4 = this.opts.spriteX1;
      this.opts.spriteY1 = pixel(var2, var4, this.texture.getHeight());
      this.opts.spriteY2 = this.opts.spriteY1;
      this.opts.spriteY3 = pixel(var2 + 1, var4, this.texture.getHeight());
      this.opts.spriteY4 = this.opts.spriteY3;
      this.opts.width = var3;
      this.opts.height = var4;
      return new TextureDrawOptionsEnd(this);
   }

   public TextureDrawOptionsEnd spriteSection(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      this.opts.spriteX1 = pixel(var1, var5, var3, this.texture.getWidth());
      this.opts.spriteX2 = pixel(var1, var6, var3, this.texture.getWidth());
      this.opts.spriteX3 = this.opts.spriteX2;
      this.opts.spriteX4 = this.opts.spriteX1;
      this.opts.spriteY1 = pixel(var2, var7, var4, this.texture.getHeight());
      this.opts.spriteY2 = this.opts.spriteY1;
      this.opts.spriteY3 = pixel(var2, var8, var4, this.texture.getHeight());
      this.opts.spriteY4 = this.opts.spriteY3;
      if (var9) {
         this.opts.translateX = var5;
         this.opts.translateY = var7;
      }

      this.opts.width = Math.abs(var6 - var5);
      this.opts.height = Math.abs(var8 - var7);
      return new TextureDrawOptionsEnd(this);
   }

   public TextureDrawOptionsEnd spriteSection(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      return this.spriteSection(var1, var2, var3, var3, var4, var5, var6, var7, var8);
   }

   public TextureDrawOptionsEnd spriteSection(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      return this.spriteSection(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public TextureDrawOptionsEnd spriteSection(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return this.spriteSection(var1, var2, var3, var3, var4, var5, var6, var7);
   }

   public TextureDrawOptionsEnd next() {
      return new TextureDrawOptionsEnd(this);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsEnd copy() {
      return this.copy();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptions copy() {
      return this.copy();
   }
}
