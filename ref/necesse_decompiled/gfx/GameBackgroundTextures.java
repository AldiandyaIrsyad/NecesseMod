package necesse.gfx;

import java.awt.Color;
import java.io.IOException;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.HUD;

public class GameBackgroundTextures {
   public int edgeResolution;
   public int edgeMargin;
   public int contentPadding;
   public TextureGetterRaw backgroundTextureLoader;
   public TextureGetterRaw edgeTextureLoader;
   public GameTexture backgroundTexture;
   public GameTexture edgeTexture;
   public Color centerColor;

   public GameBackgroundTextures(int var1, int var2, int var3, TextureGetterRaw var4, TextureGetterRaw var5) {
      this.edgeResolution = var1;
      this.edgeMargin = var2;
      this.contentPadding = var3;
      this.backgroundTextureLoader = var4;
      this.edgeTextureLoader = var5;
   }

   public void loadTextures() {
      try {
         this.backgroundTexture = this.backgroundTextureLoader.get();
      } catch (IOException var3) {
         this.backgroundTexture = null;
      }

      try {
         this.edgeTexture = this.edgeTextureLoader.get();
      } catch (IOException var2) {
         this.edgeTexture = null;
      }

   }

   public SharedTextureDrawOptions getOutlineDrawOptions(int var1, int var2, int var3, int var4) {
      return this.backgroundTexture == null ? new SharedTextureDrawOptions(GameResources.error) : HUD.getOutlinesDrawOptions(this.backgroundTexture, this.edgeResolution, var1 - this.edgeMargin, var2 - this.edgeMargin, var3 + this.edgeMargin * 2, var4 + this.edgeMargin * 2);
   }

   public SharedTextureDrawOptions getCenterDrawOptions(int var1, int var2, int var3, int var4) {
      return this.backgroundTexture == null ? new SharedTextureDrawOptions(GameResources.error) : HUD.getCenterDrawOptions(this.backgroundTexture, this.edgeResolution, var1 - this.edgeMargin, var2 - this.edgeMargin, var3 + this.edgeMargin * 2, var4 + this.edgeMargin * 2);
   }

   public SharedTextureDrawOptions getDrawOptions(int var1, int var2, int var3, int var4) {
      return this.backgroundTexture == null ? new SharedTextureDrawOptions(GameResources.error) : HUD.getBackgroundDrawOptions(this.backgroundTexture, this.edgeResolution, var1 - this.edgeMargin, var2 - this.edgeMargin, var3 + this.edgeMargin * 2, var4 + this.edgeMargin * 2);
   }

   public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int var1, int var2, int var3, int var4) {
      return this.edgeTexture == null ? new SharedTextureDrawOptions(GameResources.error) : HUD.getOutlinesDrawOptions(this.edgeTexture, this.edgeResolution, var1 - this.edgeMargin, var2 - this.edgeMargin, var3 + this.edgeMargin * 2, var4 + this.edgeMargin * 2);
   }

   public SharedTextureDrawOptions getCenterEdgeDrawOptions(int var1, int var2, int var3, int var4) {
      return this.edgeTexture == null ? new SharedTextureDrawOptions(GameResources.error) : HUD.getCenterDrawOptions(this.edgeTexture, this.edgeResolution, var1 - this.edgeMargin, var2 - this.edgeMargin, var3 + this.edgeMargin * 2, var4 + this.edgeMargin * 2);
   }

   public SharedTextureDrawOptions getEdgeDrawOptions(int var1, int var2, int var3, int var4) {
      return this.edgeTexture == null ? new SharedTextureDrawOptions(GameResources.error) : HUD.getBackgroundDrawOptions(this.edgeTexture, this.edgeResolution, var1 - this.edgeMargin, var2 - this.edgeMargin, var3 + this.edgeMargin * 2, var4 + this.edgeMargin * 2);
   }

   public Color getCenterColor() {
      if (this.centerColor != null) {
         return this.centerColor;
      } else {
         if (this.backgroundTexture.isFinal()) {
            this.backgroundTexture.restoreFinal();
         }

         byte var1 = 0;
         int var2 = this.edgeResolution * 4;
         int var3 = this.backgroundTexture.getWidth() - var1;
         int var4 = this.backgroundTexture.getHeight() - var2;
         return this.backgroundTexture.getColor(var1 + var3 / 2, var2 + var4 / 2);
      }
   }

   @FunctionalInterface
   public interface TextureGetterRaw {
      GameTexture get() throws IOException;
   }
}
