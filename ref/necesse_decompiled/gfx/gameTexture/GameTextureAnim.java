package necesse.gfx.gameTexture;

public class GameTextureAnim extends GameTexture {
   private GameTexture[] textures;
   private long speed;

   public GameTextureAnim(String var1, int var2, int var3, float var4, GameTexture[] var5) {
      super(var1, var2, var3);
      this.textures = var5;
      this.speed = (long)(var4 * 1000.0F);
      GameTexture[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         GameTexture var9 = var6[var8];
         var9.getTextureID();
      }

   }

   public int getTextureID() {
      float var1 = (float)(System.currentTimeMillis() % this.speed) / (float)this.speed;
      int var2 = (int)(var1 * (float)this.textures.length) % this.textures.length;
      return this.textures[var2].getTextureID();
   }
}
