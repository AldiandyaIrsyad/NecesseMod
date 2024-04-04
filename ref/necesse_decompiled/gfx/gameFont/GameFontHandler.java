package necesse.gfx.gameFont;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.GameMath;

public class GameFontHandler {
   public final SizeManager regularFonts = new SizeManager();
   public final SizeManager outlineFonts = new SizeManager();

   public GameFontHandler() {
   }

   public void updateFont(String var1) {
      this.regularFonts.fonts.forEach((var1x) -> {
         var1x.updateFont(var1);
      });
      this.outlineFonts.fonts.forEach((var1x) -> {
         var1x.updateFont(var1);
      });
   }

   private GameFont get(FontOptions var1) {
      return var1.getOutline() ? this.outlineFonts.getFont(var1.getSize(), var1.isPixelFont()) : this.regularFonts.getFont(var1.getSize(), var1.isPixelFont());
   }

   public float drawChar(float var1, float var2, char var3, FontOptions var4) {
      return this.get(var4).drawChar(var1, var2, var3, var4);
   }

   public float drawString(float var1, float var2, String var3, FontOptions var4) {
      return this.get(var4).drawString(var1, var2, var3, var4);
   }

   public float getWidth(char var1, FontOptions var2) {
      return this.get(var2).getWidth(var1, var2);
   }

   public float getWidth(String var1, FontOptions var2) {
      return this.get(var2).getWidth(var1, var2);
   }

   public float getHeight(char var1, FontOptions var2) {
      return this.get(var2).getHeight(var1, var2);
   }

   public float getHeight(String var1, FontOptions var2) {
      return this.get(var2).getHeight(var1, var2);
   }

   public int getWidthCeil(char var1, FontOptions var2) {
      return this.get(var2).getWidthCeil(var1, var2);
   }

   public int getWidthCeil(String var1, FontOptions var2) {
      return this.get(var2).getWidthCeil(var1, var2);
   }

   public int getHeightCeil(char var1, FontOptions var2) {
      return this.get(var2).getHeightCeil(var1, var2);
   }

   public int getHeightCeil(String var1, FontOptions var2) {
      return this.get(var2).getHeightCeil(var1, var2);
   }

   public void deleteFonts() {
      this.regularFonts.deleteFonts();
      this.outlineFonts.deleteFonts();
   }

   public static class SizeManager {
      private LinkedList<GameFont> fonts = new LinkedList();
      private GameFont[] sizeFonts = new GameFont[0];
      private GameFont[] sizePixelFonts = new GameFont[0];

      public SizeManager() {
      }

      public GameFont add(GameFont var1, boolean var2) {
         this.fonts.add(var1);
         int var3;
         Iterator var4;
         GameFont var5;
         int var6;
         int var7;
         if (var2) {
            if (this.sizePixelFonts.length < var1.getFontHeight()) {
               this.sizePixelFonts = new GameFont[var1.getFontHeight()];
            }

            for(var3 = 0; var3 < this.sizePixelFonts.length; ++var3) {
               var4 = this.fonts.iterator();

               while(var4.hasNext()) {
                  var5 = (GameFont)var4.next();
                  if (this.sizePixelFonts[var3] == null) {
                     this.sizePixelFonts[var3] = var5;
                  } else {
                     var6 = Math.abs(this.sizePixelFonts[var3].getFontHeight() - var3);
                     var7 = Math.abs(var5.getFontHeight() - var3);
                     if (var7 < var6) {
                        this.sizePixelFonts[var3] = var5;
                     }
                  }
               }
            }
         }

         if (this.sizeFonts.length < var1.getFontHeight()) {
            this.sizeFonts = new GameFont[var1.getFontHeight()];
         }

         for(var3 = 0; var3 < this.sizeFonts.length; ++var3) {
            var4 = this.fonts.iterator();

            while(var4.hasNext()) {
               var5 = (GameFont)var4.next();
               if (this.sizeFonts[var3] == null) {
                  this.sizeFonts[var3] = var5;
               } else {
                  var6 = Math.abs(this.sizeFonts[var3].getFontHeight() - var3);
                  var7 = Math.abs(var5.getFontHeight() - var3);
                  if (var7 < var6) {
                     this.sizeFonts[var3] = var5;
                  }
               }
            }
         }

         return var1;
      }

      public GameFont getFont(int var1, boolean var2) {
         if (var2) {
            var1 = GameMath.limit(var1, 0, this.sizePixelFonts.length - 1);
            return this.sizePixelFonts[var1];
         } else {
            var1 = GameMath.limit(var1, 0, this.sizeFonts.length - 1);
            return this.sizeFonts[var1];
         }
      }

      public void forEachFont(Consumer<? super GameFont> var1) {
         this.fonts.forEach(var1);
      }

      protected void deleteFonts() {
         this.fonts.forEach(AbstractGameFont::deleteTextures);
         this.fonts.clear();
         this.sizeFonts = new GameFont[0];
      }
   }
}
