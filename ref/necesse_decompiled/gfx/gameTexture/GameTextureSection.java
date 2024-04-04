package necesse.gfx.gameTexture;

import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;

public class GameTextureSection {
   protected GameTexture texture;
   protected int startX;
   protected int startY;
   protected int endX;
   protected int endY;

   protected GameTextureSection() {
      this.texture = null;
   }

   public GameTextureSection(GameTexture var1, int var2, int var3, int var4, int var5) {
      this.texture = var1;
      this.startX = var2;
      this.endX = var3;
      this.startY = var4;
      this.endY = var5;
   }

   public GameTextureSection(GameTexture var1) {
      this(var1, 0, var1.getWidth(), 0, var1.getHeight());
   }

   public boolean isGenerated() {
      return this.texture != null;
   }

   public GameTexture getTexture() {
      return this.texture;
   }

   public String toString() {
      return super.toString() + "[" + this.texture + ", " + this.startX + ", " + this.endX + ", " + this.startY + ", " + this.endY + "]";
   }

   public TextureDrawOptionsEnd initDraw() {
      return this.texture.initDraw().section(this.startX, this.endX, this.startY, this.endY);
   }

   public GameTextureSection sprite(int var1, int var2, int var3, int var4) {
      int var5 = this.startX + var1 * var3;
      int var6 = this.startY + var2 * var4;
      return new GameTextureSection(this.texture, var5, var5 + var3, var6, var6 + var4);
   }

   public GameTextureSection sprite(int var1, int var2, int var3) {
      return this.sprite(var1, var2, var3, var3);
   }

   public GameTextureSection section(int var1, int var2, int var3, int var4) {
      return new GameTextureSection(this.texture, this.startX + var1, this.startX + var2, this.startY + var3, this.startY + var4);
   }

   public GameTexture createNewTexture() {
      GameTexture var1 = new GameTexture(this.texture.debugName + " section copy", this.getWidth(), this.getHeight());
      var1.copy(this.texture, 0, 0, this.startX, this.startY, var1.getWidth(), var1.getHeight());
      return var1;
   }

   public int getWidth() {
      return this.endX - this.startX;
   }

   public int getHeight() {
      return this.endY - this.startY;
   }

   public int getStartX() {
      return this.startX;
   }

   public int getStartY() {
      return this.startY;
   }

   public int getEndX() {
      return this.endX;
   }

   public int getEndY() {
      return this.endY;
   }
}
