package necesse.gfx;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public abstract class GameBackground {
   public static GameBackground itemTooltip = new GameBackground() {
      public SharedTextureDrawOptions getOutlineDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.tooltip.getOutlineDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getCenterDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.tooltip.getCenterDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.tooltip.getDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.tooltip.getOutlineEdgeDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getCenterEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.tooltip.getCenterEdgeDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.tooltip.getEdgeDrawOptions(var1, var2, var3, var4);
      }

      public Color getCenterColor() {
         return Settings.UI.tooltip.getCenterColor();
      }

      public int getContentPadding() {
         return Settings.UI.tooltip.contentPadding;
      }
   };
   public static GameBackground form = new GameBackground() {
      public SharedTextureDrawOptions getOutlineDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.form.getOutlineDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getCenterDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.form.getCenterDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.form.getDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.form.getOutlineEdgeDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getCenterEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.form.getCenterEdgeDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.form.getEdgeDrawOptions(var1, var2, var3, var4);
      }

      public Color getCenterColor() {
         return Settings.UI.form.getCenterColor();
      }

      public int getContentPadding() {
         return Settings.UI.form.contentPadding;
      }
   };
   public static GameBackground textBox = new GameBackground() {
      public SharedTextureDrawOptions getOutlineDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.textBox.getOutlineDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getCenterDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.textBox.getCenterDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.textBox.getDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.textBox.getOutlineEdgeDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getCenterEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.textBox.getCenterEdgeDrawOptions(var1, var2, var3, var4);
      }

      public SharedTextureDrawOptions getEdgeDrawOptions(int var1, int var2, int var3, int var4) {
         return Settings.UI.textBox.getEdgeDrawOptions(var1, var2, var3, var4);
      }

      public Color getCenterColor() {
         return Settings.UI.textBox.getCenterColor();
      }

      public int getContentPadding() {
         return Settings.UI.textBox.contentPadding;
      }
   };

   public static GameBackground getItemTooltipBackground() {
      return Settings.showItemTooltipBackground ? itemTooltip : null;
   }

   public GameBackground() {
   }

   public abstract SharedTextureDrawOptions getOutlineDrawOptions(int var1, int var2, int var3, int var4);

   public abstract SharedTextureDrawOptions getCenterDrawOptions(int var1, int var2, int var3, int var4);

   public abstract SharedTextureDrawOptions getDrawOptions(int var1, int var2, int var3, int var4);

   public abstract SharedTextureDrawOptions getOutlineEdgeDrawOptions(int var1, int var2, int var3, int var4);

   public abstract SharedTextureDrawOptions getCenterEdgeDrawOptions(int var1, int var2, int var3, int var4);

   public abstract SharedTextureDrawOptions getEdgeDrawOptions(int var1, int var2, int var3, int var4);

   public abstract Color getCenterColor();

   public abstract int getContentPadding();
}
