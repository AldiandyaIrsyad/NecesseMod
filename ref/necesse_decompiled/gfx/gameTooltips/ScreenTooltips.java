package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.gfx.GameBackground;

public class ScreenTooltips {
   private static final int margin = 5;
   public final GameTooltips tooltips;
   public final GameBackground background;
   public final TooltipLocation location;

   public ScreenTooltips(GameTooltips var1, GameBackground var2, TooltipLocation var3) {
      this.tooltips = var1;
      this.background = var2;
      this.location = var3;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      int var4 = this.tooltips.getDrawXOffset();
      int var5 = this.background != null ? this.background.getContentPadding() : 0;
      int var6 = Math.max(0, var1 + var4);
      if (this.background != null) {
         this.background.getDrawOptions(var1 - var5, var2 - var5, this.tooltips.getWidth() + var5 * 2, this.tooltips.getHeight() + var5 * 2).draw();
      } else if (Settings.showBasicTooltipBackground) {
         Screen.initQuadDraw(this.tooltips.getWidth() + 4, this.tooltips.getHeight() + 4).color(0.0F, 0.0F, 0.0F, 0.7F).draw(var1 - 2, var2 - 2);
      }

      this.tooltips.draw(var6 + var5, var2, var3);
      if (this.background != null) {
         this.background.getEdgeDrawOptions(var6 - var5, var2 - var5, this.tooltips.getWidth() + var5 * 2, this.tooltips.getHeight() + var5 * 2).draw();
      }

   }

   static int getDrawX(ScreenTooltips var0, int var1) {
      int var2 = var0.tooltips.getWidth();
      if (var0.background != null) {
         var2 += var0.background.getContentPadding() * 2;
      } else if (Settings.showBasicTooltipBackground) {
         var2 += 4;
      }

      int var3 = var0.tooltips.getDrawXOffset();
      int var4 = 0;
      if (var1 + var2 + 5 - var3 > Screen.getHudWidth()) {
         var4 = Screen.getHudWidth() - (var1 + var2 + 5) + var3;
      }

      return var1 + 5 + var4;
   }

   static int getDrawY(ScreenTooltips var0, int var1) {
      int var2 = var0.tooltips.getHeight();
      if (var0.background != null) {
         var2 += var0.background.getContentPadding() * 2;
      } else if (Settings.showBasicTooltipBackground) {
         var2 += 4;
      }

      if (var1 - 5 - var2 < 0) {
         var1 = var2 + 5;
      }

      return var1 - 5 - var2;
   }

   public static void drawAt(LinkedList<ScreenTooltips> var0, TooltipLocation var1, Point var2, Supplier<Color> var3) {
      if (var2 != null) {
         LinkedList var4 = new LinkedList();
         ListIterator var5 = var0.listIterator();

         while(var5.hasNext()) {
            ScreenTooltips var6 = (ScreenTooltips)var5.next();
            if (var6.location == var1) {
               var4.add(var6);
               var5.remove();
            }
         }

         if (!var4.isEmpty()) {
            drawAt(var4, var2.x, var2.y, var3);
         }

      }
   }

   public static void drawAt(LinkedList<ScreenTooltips> var0, int var1, int var2, Supplier<Color> var3) {
      int var4 = 0;

      int var5;
      for(var5 = 1; var5 < var0.size(); ++var5) {
         var4 += ((ScreenTooltips)var0.get(var5)).tooltips.getHeight() + 5;
      }

      var5 = var1 + 5;

      ScreenTooltips var7;
      for(Iterator var6 = var0.iterator(); var6.hasNext(); var5 = Math.min(var5, getDrawX(var7, var1))) {
         var7 = (ScreenTooltips)var6.next();
      }

      if (var5 < 5) {
         var5 = 5;
      }

      ScreenTooltips var12 = var0.size() > 0 ? (ScreenTooltips)var0.get(0) : null;
      int var13 = var12 != null ? getDrawY(var12, var2 - var4) : 0;
      int var8 = 0;

      ScreenTooltips var10;
      for(Iterator var9 = var0.iterator(); var9.hasNext(); var8 += var10.tooltips.getHeight() + 5) {
         var10 = (ScreenTooltips)var9.next();
         int var11 = var13 + var8;
         var10.draw(var5, var11, var3);
      }

   }
}
