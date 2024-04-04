package necesse.gfx.ui.debug;

import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.PerformanceTimer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.TableContentDraw;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class DebugPerformanceHistory extends Debug {
   private boolean isPaused = false;
   private boolean sortByTime = false;
   private boolean forceUpdate = false;
   private String lastTimerPath;
   private final FrameData[] frames;
   private long historyTotalTime;
   private int historyTotalCalls;
   private long historyMaxFrame;
   private LinkedHashMap<String, HistoryNameData> historyNames;
   private LinkedList<HistoryNameData> historyNamesSorted;
   private final LinkedHashMap<Integer, String> inputPaths;
   private int pausedFocusIndex;
   private final int[] hotkeyList;

   public DebugPerformanceHistory() {
      this.lastTimerPath = DebugTimers.timerPath;
      this.frames = new FrameData[500];
      this.historyTotalTime = 0L;
      this.historyTotalCalls = 0;
      this.historyMaxFrame = 0L;
      this.historyNames = new LinkedHashMap();
      this.historyNamesSorted = new LinkedList();
      this.inputPaths = new LinkedHashMap();
      this.pausedFocusIndex = -1;
      this.hotkeyList = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 81, 87, 69, 82, 84, 89, 85, 73, 65, 83, 68, 70, 71, 72, 74, 75, 76, 90, 88, 67, 86, 66, 78, 77};

      for(int var1 = 0; var1 < this.frames.length; ++var1) {
         this.frames[var1] = new FrameData();
      }

   }

   protected void submitDebugInputEvent(InputEvent var1, Client var2) {
      if (var1.state && var1.getID() == 80) {
         this.sortByTime = !this.sortByTime;
         this.forceUpdate = true;
         var1.use();
      } else if (var1.state && var1.getID() == 79) {
         this.isPaused = !this.isPaused;
         this.pausedFocusIndex = -1;
         this.forceUpdate = true;
         var1.use();
      } else {
         byte var5;
         if (var1.state && this.isPaused && var1.getID() == 263) {
            var5 = 1;
            if (Screen.isKeyDown(340)) {
               var5 = 10;
            }

            if (Screen.isKeyDown(341)) {
               var5 = 100;
            }

            if (this.pausedFocusIndex >= 0) {
               this.pausedFocusIndex = Math.max(this.pausedFocusIndex - var5, -1);
            }

            this.forceUpdate = true;
            var1.use();
         } else if (var1.state && this.isPaused && var1.getID() == 262) {
            var5 = 1;
            if (Screen.isKeyDown(340)) {
               var5 = 10;
            }

            if (Screen.isKeyDown(341)) {
               var5 = 100;
            }

            if (this.pausedFocusIndex < this.frames.length - 1) {
               this.pausedFocusIndex = Math.min(this.pausedFocusIndex + var5, this.frames.length - 1);
            }

            this.forceUpdate = true;
            var1.use();
         } else if (var1.state && var1.isKeyboardEvent()) {
            Iterator var3 = this.inputPaths.keySet().iterator();

            while(var3.hasNext()) {
               int var4 = (Integer)var3.next();
               if (var1.getID() == var4) {
                  DebugTimers.timerPath = (String)this.inputPaths.getOrDefault(var4, "");
                  var1.use();
                  break;
               }
            }
         }
      }

   }

   protected void drawDebug(Client var1) {
      this.drawString("Press '" + Input.getName(79) + "' to pause/resume history");
      this.drawString("Press '" + Input.getName(80) + "' to toggle sorting by name and time");
      this.inputPaths.clear();
      FontOptions var2 = (new FontOptions(16)).outline();
      if (!DebugTimers.timerPath.equals("")) {
         this.inputPaths.put(48, DebugTimers.getTimerPathParent());
         FontManager.bit.drawString(10.0F, 65.0F, "0) back (current: " + DebugTimers.timerPath + ")", var2);
      }

      this.updateFrames(this.isPaused ? null : this.tickManager(var1));
      int var3 = 0;
      FrameData[] var4 = this.frames;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         FrameData var7 = var4[var6];
         var7.drawFrame(var3 + 10, 300, 200, this.pausedFocusIndex == var3);
         ++var3;
      }

      FontManager.bit.drawString(10.0F, 100.0F, GameUtils.getTimeStringNano(this.historyMaxFrame), (new FontOptions(12)).outline());
      if (this.pausedFocusIndex < 0) {
         if (this.isPaused) {
            FontManager.bit.drawString(10.0F, 305.0F, "Use arrow keys to focus on frame", var2);
         }

         if (!this.historyNamesSorted.isEmpty()) {
            TableContentDraw var17 = new TableContentDraw();
            var17.newRow().addEmptyColumn().addTextColumn("Identifier", var2).addTextColumn("% time", var2, 10, 0).addTextColumn("Time", var2, 10, 0).addTextColumn("Calls", var2, 10, 0);
            var5 = 0;

            for(Iterator var20 = this.historyNamesSorted.iterator(); var20.hasNext(); ++var5) {
               HistoryNameData var21 = (HistoryNameData)var20.next();

               int var8;
               for(var8 = -1; var5 < this.hotkeyList.length; ++var5) {
                  var8 = this.hotkeyList[var5];
                  if (!Control.streamControls().anyMatch((var1x) -> {
                     return var1x.getKey() == var8;
                  })) {
                     break;
                  }
               }

               this.inputPaths.put(var8, DebugTimers.timerPath + (DebugTimers.timerPath.equals("") ? "" : "/") + var21.name);
               String var9 = var8 != -1 ? Input.getName(var8) : "?";
               if (var9.length() > 0) {
                  var9 = String.valueOf(var9.charAt(0));
               }

               float var10 = (float)((double)var21.totalTime / (double)this.historyTotalTime) * 100.0F;
               var10 = GameMath.toDecimals(var10, 3);
               FontOptions var11 = (new FontOptions(var2)).color(var21.color);
               var17.newRow().addTextColumn(var9 + ")", var2, 5, 0).addTextColumn(var21.name, var11, 10, 0).addTextColumn(var10 + " %", var11, 20, 0).addTextColumn(GameUtils.getTimeStringNano(var21.totalTime / (long)this.frames.length), var11, 20, 0).addTextColumn(GameUtils.formatNumber(GameMath.toDecimals((double)var21.totalCalls / (double)this.frames.length, 2)), var11, 20, 0);
            }

            var17.setMinimumColumnWidth(1, 150);
            var17.draw(520, 85);
         }
      } else {
         FontManager.bit.drawString(10.0F, 305.0F, "Focusing on frame " + (this.pausedFocusIndex + 1), var2);
         FrameData var18 = this.frames[this.pausedFocusIndex];
         long var19 = 0L;
         if (var18.sourceRoot != null) {
            PerformanceTimer var22 = (PerformanceTimer)var18.sourceRoot.getPerformanceTimer(DebugTimers.timerPath);
            if (var22 != null) {
               var19 += var22.getTime();
            }

            FontManager.bit.drawString(10.0F, 325.0F, "Total frame " + var18.sourceRoot.totalFrame + " (" + var18.sourceRoot.secondFrame + " within second)", var2);
         }

         int var23 = 0;
         if (!var18.times.isEmpty()) {
            TableContentDraw var24 = new TableContentDraw();
            var24.newRow().addEmptyColumn().addTextColumn("Identifier", var2).addTextColumn("% time", var2, 10, 0).addTextColumn("Time", var2, 10, 0).addTextColumn("Calls", var2, 10, 0);

            for(Iterator var25 = var18.times.iterator(); var25.hasNext(); ++var23) {
               FrameData.FrameTime var26 = (FrameData.FrameTime)var25.next();

               int var27;
               for(var27 = -1; var23 < this.hotkeyList.length; ++var23) {
                  var27 = this.hotkeyList[var23];
                  if (!Control.streamControls().anyMatch((var1x) -> {
                     return var1x.getKey() == var27;
                  })) {
                     break;
                  }
               }

               HistoryNameData var12 = (HistoryNameData)this.historyNames.get(var26.name);
               Color var13 = var12 == null ? Color.WHITE : var12.color;
               this.inputPaths.put(var27, DebugTimers.timerPath + (DebugTimers.timerPath.equals("") ? "" : "/") + var26.name);
               String var14 = var27 != -1 ? Input.getName(var27) : "?";
               if (var14.length() > 0) {
                  var14 = String.valueOf(var14.charAt(0));
               }

               float var15 = 0.0F;
               if (var19 != 0L) {
                  var15 = (float)((double)var26.time / (double)var19) * 100.0F;
               }

               var15 = GameMath.toDecimals(var15, 3);
               FontOptions var16 = (new FontOptions(var2)).color(var13);
               var24.newRow().addTextColumn(var14 + ")", var2, 5, 0).addTextColumn(var26.name, var16, 10, 0).addTextColumn(var15 + " %", var16, 20, 0).addTextColumn(GameUtils.getTimeStringNano(var26.time), var16, 20, 0).addTextColumn("" + var26.calls, var16, 20, 0);
            }

            var24.setMinimumColumnWidth(1, 150);
            var24.draw(520, 85);
         }
      }

      this.lastTimerPath = DebugTimers.timerPath;
      this.forceUpdate = false;
   }

   private void updateFrames(TickManager var1) {
      FrameData[] var3;
      int var4;
      int var5;
      FrameData var6;
      if (var1 != null) {
         this.historyTotalTime = 0L;
         this.historyTotalCalls = 0;
         this.historyMaxFrame = 1L;
         this.historyNames.clear();
         int var2 = 0;
         var3 = this.frames;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            var6.reset();
         }

         synchronized(var1.historyLock) {
            Iterator var16 = var1.getPerformanceHistory().descendingIterator();

            while(true) {
               if (var2 >= this.frames.length || !var16.hasNext()) {
                  break;
               }

               PerformanceTimer var17 = (PerformanceTimer)var16.next();
               this.handlePerformanceTimer(var17, var2);
               ++var2;
            }
         }

         var3 = this.frames;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            this.historyMaxFrame = Math.max(this.historyMaxFrame, var6.frameTotalTime);
         }

         this.updateColors();
      } else if (!this.lastTimerPath.equals(DebugTimers.timerPath) || this.forceUpdate) {
         this.historyTotalTime = 0L;
         this.historyTotalCalls = 0;
         this.historyMaxFrame = 1L;
         this.historyNames.clear();
         LinkedList var15 = new LinkedList();
         var3 = this.frames;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            var6.reset();
            PerformanceTimer var7 = (PerformanceTimer)var6.sourceRoot.getPerformanceTimer(DebugTimers.timerPath);
            if (var7 != null) {
               if (!var15.contains(var7)) {
                  this.historyTotalTime += var7.getTime();
                  this.historyTotalCalls += var7.getCalls();
                  var15.add(var7);
               }

               HistoryNameData var13;
               for(Iterator var8 = var7.getChildren().values().iterator(); var8.hasNext(); ++var13.totalFrames) {
                  PerformanceTimer var9 = (PerformanceTimer)var8.next();
                  long var10 = var9.getTime();
                  int var12 = var9.getCalls();
                  var6.addTime(var9.name, var10, var12);
                  var13 = (HistoryNameData)this.historyNames.get(var9.name);
                  if (var13 == null) {
                     var13 = new HistoryNameData(var9.name);
                     this.historyNames.put(var9.name, var13);
                  }

                  var13.totalTime += var10;
                  var13.totalCalls += var12;
               }

               this.historyMaxFrame = Math.max(this.historyMaxFrame, var6.frameTotalTime);
            }
         }

         this.updateColors();
      }

   }

   private void sort() {
      this.historyNamesSorted = new LinkedList(this.historyNames.values());
      if (this.sortByTime) {
         this.historyNamesSorted.sort((var0, var1x) -> {
            return Long.compare(var1x.totalTime, var0.totalTime);
         });
      } else {
         this.historyNamesSorted.sort(Comparator.comparing((var0) -> {
            return var0.name;
         }));
      }

      LinkedList var1 = (LinkedList)this.historyNamesSorted.stream().map((var0) -> {
         return var0.name;
      }).collect(Collectors.toCollection(LinkedList::new));
      FrameData[] var2 = this.frames;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FrameData var5 = var2[var4];
         var5.sortTimes(var1);
      }

   }

   private void updateColors() {
      this.sort();
      float var1 = 0.0F;

      for(Iterator var2 = this.historyNamesSorted.iterator(); var2.hasNext(); var1 += 0.15F) {
         HistoryNameData var3 = (HistoryNameData)var2.next();
         var3.color = Color.getHSBColor(var1, 1.0F, 1.0F);
      }

   }

   private void handlePerformanceTimer(PerformanceTimer var1, int var2) {
      FrameData var3 = this.frames[var2];
      var3.sourceRoot = var1;
      PerformanceTimer var4 = (PerformanceTimer)var1.getPerformanceTimer(DebugTimers.timerPath);
      if (var4 != null) {
         this.historyTotalTime += var4.getTime();
         this.historyTotalCalls += var4.getCalls();

         HistoryNameData var7;
         for(Iterator var5 = var4.getChildren().values().iterator(); var5.hasNext(); ++var7.totalFrames) {
            PerformanceTimer var6 = (PerformanceTimer)var5.next();
            var3.addTime(var6.name, var6.getTime(), var6.getCalls());
            var7 = (HistoryNameData)this.historyNames.get(var6.name);
            if (var7 == null) {
               var7 = new HistoryNameData(var6.name);
               this.historyNames.put(var6.name, var7);
            }

            var7.totalTime += var6.getTime();
            var7.totalCalls += var6.getCalls();
         }

      }
   }

   private TickManager tickManager(Client var1) {
      return Settings.serverPerspective && var1.getLocalServer() != null ? var1.getLocalServer().tickManager() : var1.tickManager();
   }

   private class FrameData {
      public long frameTotalTime;
      public int frameTotalCalls;
      private final LinkedList<FrameTime> times;
      public PerformanceTimer sourceRoot;

      private FrameData() {
         this.frameTotalTime = 0L;
         this.frameTotalCalls = 0;
         this.times = new LinkedList();
      }

      public void sortTimes(LinkedList<String> var1) {
         this.times.sort(Comparator.comparingInt((var1x) -> {
            return var1.indexOf(var1x.name);
         }));
      }

      public void reset() {
         this.frameTotalTime = 0L;
         this.times.clear();
      }

      public void addTime(String var1, long var2, int var4) {
         this.frameTotalTime += var2;
         this.frameTotalCalls += var4;
         this.times.add(new FrameTime(var1, var2, var4));
      }

      public void drawFrame(int var1, int var2, int var3, boolean var4) {
         int var13;
         for(Iterator var6 = this.times.iterator(); var6.hasNext(); var2 -= var13) {
            FrameTime var7 = (FrameTime)var6.next();
            Color var8 = ((HistoryNameData)DebugPerformanceHistory.this.historyNames.get(var7.name)).color;
            if (var4) {
               var8 = var8.darker().darker();
            }

            float var9 = (float)var8.getRed() / 255.0F;
            float var10 = (float)var8.getGreen() / 255.0F;
            float var11 = (float)var8.getBlue() / 255.0F;
            float var12 = (float)var8.getAlpha() / 255.0F;
            var13 = (int)((double)var7.time / (double)DebugPerformanceHistory.this.historyMaxFrame * (double)var3);
            Screen.drawLineRGBA(var1, var2, var1, var2 - var13, var9, var10, var11, var12);
         }

         if (var4) {
            Screen.drawLineRGBA(var1, var2, var1, var2 - var3, 1.0F, 1.0F, 1.0F, 0.5F);
         } else {
            Screen.drawLineRGBA(var1, var2, var1, var2 - var3, 0.0F, 0.0F, 0.0F, 0.5F);
         }

      }

      // $FF: synthetic method
      FrameData(Object var2) {
         this();
      }

      private class FrameTime {
         public final String name;
         public final long time;
         public final int calls;

         public FrameTime(String var2, long var3, int var5) {
            this.name = var2;
            this.time = var3;
            this.calls = var5;
         }
      }
   }

   private class HistoryNameData {
      public final String name;
      public long totalTime = 0L;
      public int totalCalls = 0;
      public long totalFrames = 0L;
      public Color color = new Color(0, 0, 0);

      public HistoryNameData(String var2) {
         this.name = var2;
      }
   }
}
