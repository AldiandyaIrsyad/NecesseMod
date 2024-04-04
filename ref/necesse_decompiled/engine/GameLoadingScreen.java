package necesse.engine;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;
import necesse.gfx.gameFont.LoadGameFont;

public class GameLoadingScreen {
   public static GameFontHandler font;
   private static String main;
   private static String sub;
   private static LinkedList<String> log = new LinkedList();
   private static Runnable preDrawFunction = null;
   private static Runnable postDrawFunction = null;
   private static boolean isDone;
   private static int maxFPS = 30;
   private static double msPerFrame;
   private static double drawValue;
   private static long lastDrawCallTime;
   private static int rejectedDrawCalls;
   private static int successfulDrawCalls;
   private static double missedDrawCalls;

   public GameLoadingScreen() {
   }

   public static void initLoadingScreen(Runnable var0, Runnable var1) {
      if (preDrawFunction == null && postDrawFunction == null) {
         isDone = false;
         preDrawFunction = var0;
         postDrawFunction = var1;
         if (font == null) {
            font = new GameFontHandler();
            font.regularFonts.add(new LoadGameFont(), true);
         }

         lastDrawCallTime = System.currentTimeMillis();
         Screen.setVSync(false);
      } else {
         throw new IllegalStateException("Loading screen init cannot be done twice");
      }
   }

   public static void markDone() {
      preDrawFunction = null;
      postDrawFunction = null;
      isDone = true;
      Screen.setVSync(Settings.vSyncEnabled);
   }

   public static void addLog(String var0) {
      log.addFirst(var0);
      draw(false);
   }

   public static void clearLog() {
      log.clear();
   }

   public static void drawLoadingString(String var0) {
      main = var0;
      sub = null;
      draw(true);
   }

   public static void drawLoadingSub(String var0) {
      sub = var0;
      draw(false);
   }

   public static void draw(boolean var0) {
      if (!isDone && Screen.isCreated()) {
         long var1 = System.currentTimeMillis();
         drawValue += (double)(var1 - lastDrawCallTime) / msPerFrame;
         lastDrawCallTime = var1;
         if (drawValue >= 1.0) {
            --drawValue;
            missedDrawCalls += drawValue;
            drawValue = 0.0;
         } else if (!var0) {
            ++rejectedDrawCalls;
            return;
         }

         preDrawFunction.run();
         int var3 = 0;
         if (main != null) {
            FontOptions var4 = new FontOptions(32);
            var3 = font.getHeightCeil(main, var4);
            drawMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 - var3 / 2, main, var4);
         }

         if (sub != null) {
            drawMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2 + var3 / 2 + 10, sub, new FontOptions(20));
         }

         int var8 = Screen.getHudHeight() - Math.min(5, log.size()) * 16;
         int var5 = 0;
         Iterator var6 = log.iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            font.drawString(5.0F, (float)(var8 + var5 * 16), var7, new FontOptions(16));
            ++var5;
            if (var5 > 5) {
               break;
            }
         }

         postDrawFunction.run();
         ++successfulDrawCalls;
      }
   }

   private static void drawMiddle(int var0, int var1, String var2, FontOptions var3) {
      int var4 = font.getWidthCeil(var2, var3);
      font.drawString((float)(var0 - var4 / 2), (float)var1, var2, var3);
   }

   static {
      msPerFrame = 1000.0 / (double)maxFPS;
   }
}
