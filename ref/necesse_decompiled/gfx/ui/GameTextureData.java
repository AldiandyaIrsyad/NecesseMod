package necesse.gfx.ui;

import java.io.Serializable;
import java.util.Arrays;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTexture.GameTexture;

public class GameTextureData implements Serializable {
   public int width;
   public int height;
   private byte[] bufferCompressed;
   public boolean isFinal;
   public GameTexture.BlendQuality blendQuality;

   public GameTextureData(int var1, int var2, byte[] var3, boolean var4, GameTexture.BlendQuality var5) {
      this.width = var1;
      this.height = var2;

      try {
         this.bufferCompressed = GameUtils.compressData(var3);
      } catch (Exception var7) {
         var7.printStackTrace();
         this.bufferCompressed = new byte[0];
      }

      this.isFinal = var4;
      this.blendQuality = var5;
   }

   public byte[] getBuffer() {
      try {
         return GameUtils.decompressData(this.bufferCompressed);
      } catch (Exception var2) {
         var2.printStackTrace();
         return new byte[0];
      }
   }

   public void printData() {
      System.out.println("Width:  " + this.width);
      System.out.println("Height: " + this.height);
      System.out.println("Buffer: " + Arrays.toString(this.bufferCompressed));
   }

   public static GameTextureData fromCompressed(int var0, int var1, byte[] var2, boolean var3, GameTexture.BlendQuality var4) {
      GameTextureData var5 = new GameTextureData(0, 0, new byte[0], var3, var4);
      var5.width = var0;
      var5.height = var1;
      var5.bufferCompressed = var2;
      return var5;
   }
}
