package necesse.gfx.drawOptions.human;

import necesse.engine.util.GameMath;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class HumanMask {
   public final GameSprite sprite64;
   public final GameSprite sprite128;

   public HumanMask(GameSprite var1, GameSprite var2) {
      this.sprite64 = var1;
      this.sprite128 = var2;
   }

   public HumanMask(GameTexture var1, GameTexture var2) {
      this(new GameSprite(var1), new GameSprite(var2));
   }

   public HumanMask(GameSprite var1) {
      this.sprite64 = var1;
      GameTexture var2 = new GameTexture("humanmask", 128, 128);
      int var3 = var1.spriteX * var1.spriteWidth;
      int var4 = var1.spriteY * var1.spriteHeight;

      for(int var5 = 0; var5 < 128; ++var5) {
         for(int var6 = 0; var6 < 128; ++var6) {
            int var7 = var3 + var5;
            int var8 = var4 + var6;
            int var9 = GameMath.limit(var7 - 32, var3, var3 + 63);
            int var10 = GameMath.limit(var8 - 32, var4, var4 + 63);
            var2.setPixel(var5, var6, var1.texture.getPixel(var9, var10));
         }
      }

      this.sprite128 = new GameSprite(var2);
   }

   public HumanMask(GameTexture var1) {
      this(new GameSprite(var1));
   }
}
