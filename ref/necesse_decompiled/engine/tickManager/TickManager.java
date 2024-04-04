package necesse.engine.tickManager;

import necesse.gfx.ui.debug.Debug;

public class TickManager extends PerformanceTimerManager {
   public static float globalTimeMod = 1.0F;
   public static boolean skipDrawIfBehind = false;
   public static final int ticksPerSec = 20;
   public static final int msPerTick = 50;
   public static final int nsPerTick = 50000000;
   private double gameTickValue;
   private boolean gameTick;
   private long lastTickTime;
   private long totalTicks;
   private long lastResetTime;
   private long loopTime;
   private int fps;
   private int tps;
   private int frame;
   private int tick;
   private long totalFrames;
   private float delta;
   private float fullDelta;
   private long totalTicksSecond;
   private long totalExpectedTicks;
   private int maxFPS;
   private double sleepCount;
   private double msPerFrame;
   private String name;

   public static float getTickDelta(float var0) {
      return 1.0F / var0 / 20.0F;
   }

   public static double getTickDelta(long var0) {
      return 1000.0 / (double)var0 / 20.0;
   }

   public TickManager(String var1, int var2) {
      this.name = var1;
      this.setMaxFPS(var2);
   }

   public void init() {
      long var1 = System.nanoTime();
      long var3 = System.currentTimeMillis();
      this.gameTickValue = 0.0;
      this.gameTick = false;
      this.lastTickTime = var1;
      this.tick = 0;
      this.totalTicks = 0L;
      this.totalTicksSecond = 0L;
      this.totalExpectedTicks = 0L;
      this.lastResetTime = var3;
      this.loopTime = var1;
      this.totalFrames = 0L;
      this.fps = 0;
      this.delta = 0.0F;
      this.fullDelta = 0.0F;
      this.sleepCount = (double)var3;
   }

   public void tickLogic() {
      long var1 = System.nanoTime();
      long var3 = System.currentTimeMillis();
      if (this.maxFPS > 0) {
         this.sleepCount += this.msPerFrame / (double)globalTimeMod;
         int var5 = (int)(this.sleepCount - (double)var3);
         if (var5 > 1) {
            try {
               Thread.sleep((long)var5);
            } catch (InterruptedException var7) {
               Thread.currentThread().interrupt();
            }
         }
      }

      this.gameTickValue += (double)(var1 - this.lastTickTime) / 5.0E7 * (double)globalTimeMod;
      this.lastTickTime = var1;
      this.gameTick = false;
      if (this.gameTickValue >= 1.0) {
         this.gameTick = true;
         ++this.totalTicks;
         --this.gameTickValue;
         ++this.tick;
      }

      ++this.frame;
      ++this.totalFrames;
      this.fullDelta = (float)((double)(var1 - this.loopTime) / 1000000.0 * (double)globalTimeMod);
      this.delta = Math.min(this.fullDelta, 100.0F);
      this.loopTime = var1;
      this.update();
      if (var3 - this.lastResetTime > 1000L) {
         this.tps = this.tick;
         this.tick = 0;
         this.fps = this.frame;
         this.frame = 0;
         this.lastResetTime += 1000L;
         this.totalTicksSecond = this.totalTicks;
         this.totalExpectedTicks += 20L;
         this.sleepCount = (double)System.currentTimeMillis();
         this.updateSecond();
         this.calcFrame(true, this.frame, this.totalFrames);
         this.nextRunOnlyConstantTimers = !Debug.isActive();
      } else {
         this.calcFrame(false, this.frame, this.totalFrames);
      }

   }

   public void update() {
   }

   public void updateSecond() {
   }

   public void setMaxFPS(int var1) {
      if (var1 < 0) {
         var1 = Math.max(20, var1);
      }

      this.maxFPS = var1;
      if (var1 != 0) {
         this.msPerFrame = 1000.0 / (double)var1;
      } else {
         this.msPerFrame = 0.0;
      }

      this.sleepCount = (double)System.currentTimeMillis();
   }

   public boolean isBehind() {
      return this.gameTickValue >= 1.0;
   }

   public int getMaxFPS() {
      return this.maxFPS;
   }

   public String getName() {
      return this.name;
   }

   public float getDelta() {
      return this.delta;
   }

   public float getFullDelta() {
      return this.fullDelta;
   }

   public boolean isGameTick() {
      return this.gameTick;
   }

   public boolean isFirstGameTickInSecond() {
      return this.gameTick && this.tick == 1;
   }

   public boolean isGameTickInSecond(int var1) {
      return this.gameTick && this.tick == var1;
   }

   public long getTotalTicks() {
      return this.totalTicks;
   }

   public long getTotalExpectedTicks() {
      return this.totalExpectedTicks;
   }

   public long getSkippedTicks() {
      return this.totalExpectedTicks - this.totalTicksSecond;
   }

   public long getTotalFrames() {
      return this.totalFrames;
   }

   public int getFPS() {
      return this.fps;
   }

   public int getTPS() {
      return this.tps;
   }

   public int getFrame() {
      return this.frame;
   }

   public int getTick() {
      return this.tick;
   }

   public TickManager getChild() {
      TickManager var1 = new TickManager(this.name, this.maxFPS);
      this.applyChildProperties(var1);
      var1.delta = this.delta;
      var1.fullDelta = this.fullDelta;
      var1.gameTick = this.gameTick;
      var1.totalTicks = this.totalTicks;
      var1.fps = this.fps;
      var1.tps = this.tps;
      var1.tick = this.tick;
      return var1;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public PerformanceTimerManager getChild() {
      return this.getChild();
   }
}
