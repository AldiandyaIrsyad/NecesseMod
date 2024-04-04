package necesse.level.maps.light;

import java.awt.Point;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;

public abstract class FastLightMap implements LightMapInterface {
   public boolean printDebug;
   protected final Level level;
   protected final int maxLightDistance;
   protected final LightArea area;

   public FastLightMap(LightManager var1, int var2, int var3, int var4, int var5, int var6) {
      this.level = var1.level;
      this.maxLightDistance = var6;
      this.area = new LightArea(var2, var3, var4, var5);
      this.area.initLights();
      this.resetLights(var1);
   }

   public void resetLights(LightManager var1) {
      synchronized(this.area) {
         for(int var3 = 0; var3 < this.area.lights.length; ++var3) {
            this.area.lights[var3] = var1.newLight(0.0F);
         }

      }
   }

   public GameLight getLight(int var1, int var2) {
      var1 = GameMath.limit(var1, this.area.startX, this.area.endX);
      var2 = GameMath.limit(var2, this.area.startY, this.area.endY);
      synchronized(this.area) {
         return this.area.lights[this.area.getIndex(var1, var2)];
      }
   }

   public List<SourcedGameLight> getLightSources(int var1, int var2) {
      return Collections.singletonList(new SourcedGameLight(var1, var2, this.getLight(var1, var2)));
   }

   public void update(int var1, int var2, boolean var3) {
      this.update(var1 - this.maxLightDistance, var2 - this.maxLightDistance, var1 + this.maxLightDistance, var2 + this.maxLightDistance, var3);
   }

   public void update(int var1, int var2, int var3, int var4, boolean var5) {
      if (this.level.isLoadingComplete()) {
         long var6 = System.nanoTime();
         boolean var8 = false;
         boolean var9 = false;
         boolean var10 = false;
         boolean var11 = false;
         if (var1 < this.area.startY) {
            var1 = this.area.startX;
            var8 = true;
         }

         if (var3 > this.area.endX) {
            var3 = this.area.endX;
            var9 = true;
         }

         if (var2 < this.area.startY) {
            var2 = this.area.startY;
            var10 = true;
         }

         if (var4 > this.area.endY) {
            var4 = this.area.endY;
            var11 = true;
         }

         LinkedList var12 = new LinkedList();
         FastLightCompute var13 = new FastLightCompute(this.level, this, var1, var2, var3, var4);
         var12.add(new LightComputeFuture(var13));
         LightArea var14 = var5 ? new LightArea(var1, var2, var3, var4) : this.area;
         synchronized(var14) {
            var14.initLights();

            int var19;
            int var20;
            for(int var16 = var1; var16 <= var3; ++var16) {
               for(int var17 = var2; var17 <= var4; ++var17) {
                  GameLight var18;
                  if (var16 == var1 && !var8 || var16 == var3 && !var9 || var17 == var2 && !var10 || var17 == var4 && !var11) {
                     var18 = this.getLight(var16, var17);
                     if (var18.getLevel() > 0.0F) {
                        var13.addSource(var16, var17, var18);
                     }
                  } else {
                     var18 = this.getNewLight(var16, var17);
                     if (var18.getLevel() > 0.0F) {
                        var19 = (int)(var18.getLevel() / 10.0F) + 1;
                        var20 = Math.max(var1, var16 - var19);
                        int var21 = Math.max(var2, var17 - var19);
                        int var22 = Math.min(var3, var16 + var19);
                        int var23 = Math.min(var4, var17 + var19);
                        FastLightCompute var24 = new FastLightCompute(this.level, this, var20, var21, var22, var23);
                        var24.addSource(var16, var17, var18);
                        var12.add(new LightComputeFuture(var24));
                     }
                  }

                  var14.lights[var14.getIndex(var16, var17)] = this.level.lightManager.newLight(0.0F);
               }
            }

            AtomicInteger var27 = new AtomicInteger();
            AtomicInteger var28 = new AtomicInteger();
            ExecutorService var29 = this.level.lightManager.updateExecutor;
            if (var29 != null) {
               var19 = var3 - var1;
               var20 = var4 - var2;
               Iterator var30;
               LightComputeFuture var31;
               if (var5) {
                  FastLightCompute var10002;
                  for(var30 = var12.iterator(); var30.hasNext(); var31.future = var29.submit(var10002::compute)) {
                     var31 = (LightComputeFuture)var30.next();
                     var10002 = var31.compute;
                     Objects.requireNonNull(var10002);
                  }

                  var29.submit(() -> {
                     this.waitForComputes(var12, var27);
                     Iterator var9 = var12.iterator();

                     while(var9.hasNext()) {
                        LightComputeFuture var10 = (LightComputeFuture)var9.next();
                        if (var10.complete) {
                           var28.addAndGet(var10.compute.apply(var14));
                        }
                     }

                     synchronized(this.area) {
                        var14.overwriteArea(this.area);
                     }

                     if (this.printDebug) {
                        System.out.println((this.level.isServer() ? "S" : "C") + ", C: " + var12.size() + ", DONE, I: " + var27 + ", a: " + var28 + ", " + var19 + "x" + var20 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var6));
                     }

                     return null;
                  });
                  if (this.printDebug) {
                     System.out.println((this.level.isServer() ? "S" : "C") + ", C: " + var12.size() + ", WAITING, " + var19 + "x" + var20 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var6));
                  }
               } else {
                  if (this.printDebug) {
                     System.out.println((this.level.isServer() ? "S" : "C") + " START, C: " + var12.size() + ", " + var19 + "x" + var20 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var6));
                  }

                  for(var30 = var12.iterator(); var30.hasNext(); var31.complete = true) {
                     var31 = (LightComputeFuture)var30.next();
                     var27.addAndGet(var31.compute.compute());
                  }

                  var30 = var12.iterator();

                  while(var30.hasNext()) {
                     var31 = (LightComputeFuture)var30.next();
                     if (var31.complete) {
                        var28.addAndGet(var31.compute.apply(var14));
                     }
                  }

                  if (this.printDebug) {
                     System.out.println((this.level.isServer() ? "S" : "C") + ", C: " + var12.size() + ", I: " + var27 + ", a: " + var28 + ", " + var19 + "x" + var20 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var6));
                  }
               }

            }
         }
      }
   }

   public void update(Iterable<Point> var1, boolean var2) {
      throw new UnsupportedOperationException("Cannot update using iterable and FastLightMap");
   }

   private void waitForComputes(LinkedList<LightComputeFuture> var1, AtomicInteger var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         LightComputeFuture var4 = (LightComputeFuture)var3.next();

         try {
            var2.addAndGet((Integer)var4.future.get(5L, TimeUnit.SECONDS));
            var4.complete = true;
         } catch (InterruptedException var6) {
            var4.complete = false;
         } catch (ExecutionException var7) {
            var4.complete = false;
            System.err.println("Error executing light update");
            var7.printStackTrace();
         } catch (TimeoutException var8) {
            var4.complete = false;
            System.err.println("Timed out executing light update");
            var8.printStackTrace();
         }
      }

   }

   protected abstract GameLight getNewLight(int var1, int var2);

   protected static class LightComputeFuture {
      public final FastLightCompute compute;
      public boolean complete;
      public Future<Integer> future;

      public LightComputeFuture(FastLightCompute var1) {
         this.compute = var1;
         this.complete = false;
      }
   }
}
