package necesse.gfx.ui.debug;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import necesse.engine.Settings;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.PerformanceTimerAverage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.TableContentDraw;
import necesse.gfx.gameFont.FontOptions;

public class DebugTimers extends Debug {
   public static String timerPath = "";
   private boolean sortByTime = false;
   private final LinkedHashMap<Integer, String> inputPaths = new LinkedHashMap();
   private final int[] hotkeyList = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 81, 87, 69, 82, 84, 89, 85, 73, 79, 65, 83, 68, 70, 71, 72, 74, 75, 76, 90, 88, 67, 86, 66, 78, 77};

   public DebugTimers() {
   }

   public static String getTimerPathParent() {
      String[] var0 = timerPath.split("/");
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length - 1; ++var2) {
         var1.append(var0[var2]);
         if (var2 < var0.length - 2) {
            var1.append("/");
         }
      }

      return var1.toString();
   }

   protected void submitDebugInputEvent(InputEvent var1, Client var2) {
      if (var1.state && var1.getID() == 80) {
         this.sortByTime = !this.sortByTime;
      } else if (var1.state && var1.isKeyboardEvent()) {
         Iterator var3 = this.inputPaths.keySet().iterator();

         while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            if (var1.getID() == var4) {
               timerPath = (String)this.inputPaths.getOrDefault(var4, "");
               var1.use();
               break;
            }
         }
      }

   }

   protected void drawDebug(Client var1) {
      this.drawString("Press '" + Input.getName(80) + "' to toggle sorting by name and time");
      this.inputPaths.clear();
      FontOptions var2 = new FontOptions(16);
      TableContentDraw var3 = new TableContentDraw();
      if (!timerPath.equals("")) {
         this.inputPaths.put(48, getTimerPathParent());
         var3.newRow().addTextColumn("0)", var2, 5, 0).addTextColumn("back (current: " + timerPath + ")", var2);
      }

      TickManager var4 = this.tickManager(var1);
      ArrayList var5 = new ArrayList();
      HashMap var6 = var4.getPreviousAverage().getPerformanceTimers(timerPath);
      if (var6 != null) {
         var5.addAll(var6.values());
      }

      if (this.sortByTime) {
         var5.sort((var0, var1x) -> {
            return Long.compare(var1x.getAverageTime(), var0.getAverageTime());
         });
      } else {
         var5.sort(Comparator.comparing((var0) -> {
            return var0.name;
         }));
      }

      TableContentDraw var7 = new TableContentDraw();
      var7.setMinimumColumnWidth(1, 150);
      TableContentDraw var8 = new TableContentDraw(var7.colWidths);
      if (!var5.isEmpty()) {
         var7.newRow().addEmptyColumn().addTextColumn("Identifier", var2, 10, 0).addTextColumn("% time", var2, 20, 0).addTextColumn("Time", var2, 20, 0);

         for(int var9 = 0; var9 < var5.size(); ++var9) {
            PerformanceTimerAverage var10 = (PerformanceTimerAverage)var5.get(var9);
            int var11 = var9 < this.hotkeyList.length ? this.hotkeyList[var9] : -1;
            this.inputPaths.put(var11, timerPath + (timerPath.equals("") ? "" : "/") + var10.name);
            String var12 = var11 != -1 ? Input.getName(var11) : "?";
            if (var12.length() > 0) {
               var12 = String.valueOf(var12.charAt(0));
            }

            var8.newRow().addTextColumn(var12 + ")", var2, 5, 0).addTextColumn(var10.name, var2, 10, 0).addTextColumn(GameMath.toDecimals(var10.getAverageTimePercent(), 3) + "%", var2, 20, 0).addTextColumn(GameUtils.getTimeStringNano(var10.getAverageTime()), var2, 20, 0).addTextColumn(GameUtils.formatNumber(var10.getAverageCalls()), var2, 20, 0);
         }
      }

      TableContentDraw.drawSeries(10, 45, (TableContentDraw[])(var3, var7, var8));
   }

   private TickManager tickManager(Client var1) {
      return Settings.serverPerspective && var1.getLocalServer() != null ? var1.getLocalServer().tickManager() : var1.tickManager();
   }
}
