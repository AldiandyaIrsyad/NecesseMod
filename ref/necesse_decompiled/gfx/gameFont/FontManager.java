package necesse.gfx.gameFont;

import java.io.IOException;
import java.util.function.Consumer;
import necesse.engine.GameLaunch;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.gfx.gameTexture.GameTexture;

public class FontManager {
   public static GameFontHandler bit;
   private static TrueTypeGameFontInfo[] fontInfo;

   public FontManager() {
   }

   public static void loadFonts() {
      GameLoadingScreen.drawLoadingString("Loading fonts");
      deleteFonts();
      bit = new GameFontHandler();

      try {
         fontInfo = new TrueTypeGameFontInfo[]{new TrueTypeGameFontInfo("base"), new TrueTypeGameFontInfo("japanese"), new TrueTypeGameFontInfo("korean"), new TrueTypeGameFontInfo("chinese"), new TrueTypeGameFontInfo("backup")};
         bit.outlineFonts.add(loadTrueTypeFont(12, 1, 3, -1, "bitoutline12", 8, 12, fontInfo), true);
         bit.outlineFonts.add(loadTrueTypeFont(16, 2, 2, -2, "bitoutline16", 11, 16, fontInfo), true);
         bit.outlineFonts.add(loadTrueTypeFont(20, 2, 1, -3, "bitoutline20", 13, 20, fontInfo), true);
         bit.outlineFonts.add(new TrueTypeGameFont(32, 2, 0, -3, (CustomGameFont.CharArray)null, fontInfo), false);
         bit.regularFonts.add(loadTrueTypeFont(12, 0, 2, -1, "bit12", 8, 12, fontInfo), true);
         bit.regularFonts.add(loadTrueTypeFont(16, 0, 2, -2, "bit16", 11, 16, fontInfo), true);
         bit.regularFonts.add(loadTrueTypeFont(20, 0, 1, -3, "bit20", 13, 20, fontInfo), true);
         bit.regularFonts.add(new TrueTypeGameFont(32, 0, 0, -3, (CustomGameFont.CharArray)null, fontInfo), false);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      Consumer var0 = (var0x) -> {
         if (var0x instanceof TrueTypeGameFont) {
            TrueTypeGameFont var1 = (TrueTypeGameFont)var0x;
            var1.addCharOffset('_', 0.0F, (float)(-var1.fontSize) / 8.0F);
            var1.addCharOffset('-', 0.0F, (float)(-var1.fontSize) / 16.0F);
            var1.addCharOffset("+=<>~", 0.0F, (float)(-var1.fontSize) / 10.0F);
            var1.addCharOffset("({[]})|", 0.0F, (float)(-var1.fontSize) / 12.0F);
         }

      };
      bit.outlineFonts.forEachFont(var0);
      bit.regularFonts.forEachFont(var0);
      long var1 = System.currentTimeMillis();
      if (updateFont(Localization.getCurrentLang())) {
         GameLog.debug.println("Initial font update took " + (System.currentTimeMillis() - var1) + " ms");
      }

      GameLoadingScreen.font = bit;
   }

   public static boolean updateFont(Language var0) {
      StringBuilder var1 = new StringBuilder();
      if (var0 != Localization.defaultLang) {
         var1.append(Localization.getCurrentLang().getCharactersUsed());
      }

      Language[] var2 = Localization.getLanguages();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Language var5 = var2[var4];
         if (!var5.isDebugOnly() && var5 != Localization.defaultLang) {
            var1.append(var5.localDisplayName);
            var1.append(var5.credits);
         }
      }

      String var6 = var1.toString();
      if (!var6.isEmpty()) {
         bit.updateFont(var6);
         return true;
      } else {
         return false;
      }
   }

   public static void deleteFonts() {
      if (bit != null) {
         bit.deleteFonts();
      }

      if (fontInfo != null) {
         TrueTypeGameFontInfo[] var0 = fontInfo;
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            TrueTypeGameFontInfo var3 = var0[var2];
            var3.dispose();
         }
      }

      bit = null;
   }

   public static boolean isLoaded() {
      return bit != null;
   }

   private static TrueTypeGameFontOld loadTrueTypeFont(String var0, int var1, boolean var2, boolean var3, int var4, int var5, String var6, int var7, int var8) {
      CustomGameFont.CharArray var9 = null;
      if (!GameLaunch.launchOptions.containsKey("altfont")) {
         var9 = new CustomGameFont.CharArray(GameTexture.fromFile("fonts/" + var6), var7, var8);
      }

      return new TrueTypeGameFontOld(var0, var1, var2, var3, var4, var5, var9);
   }

   private static TrueTypeGameFont loadTrueTypeFont(int var0, int var1, int var2, int var3, String var4, int var5, int var6, TrueTypeGameFontInfo... var7) {
      GameTexture var8 = GameTexture.fromFile("fonts/" + var4);
      Screen.queryGLError("postFontTexture");
      CustomGameFont.CharArray var9 = new CustomGameFont.CharArray(var8, var5, var6);
      return new TrueTypeGameFont(var0, var1, var2, var3, var9, var7);
   }
}
