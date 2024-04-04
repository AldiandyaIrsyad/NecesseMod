package necesse.level.maps.light;

import java.awt.Point;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.BoundsPointIterator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;

public abstract class LightMap implements LightMapInterface {
   public boolean printDebug;
   protected final Object updateLock = new Object();
   protected final LightManager manager;
   protected final Level level;
   protected final int maxLightDistance;
   protected final int startX;
   protected final int startY;
   protected final int endX;
   protected final int endY;
   protected final int width;
   protected final LinkedList<SourcedGameLight>[] lightSources;
   protected final GameLight[] lights;
   public static final Point[] crossChecks = new Point[]{new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1)};

   public LightMap(LightManager var1, int var2, int var3, int var4, int var5, int var6) {
      this.manager = var1;
      this.level = var1.level;
      this.startX = var2;
      this.startY = var3;
      this.endX = var4;
      this.endY = var5;
      this.width = var4 - var2 + 1;
      int var7 = var5 - var3 + 1;
      this.maxLightDistance = var6;
      this.lightSources = new LinkedList[this.width * var7];
      this.lights = new GameLight[this.width * var7];
      this.resetLights(var1);
   }

   public void resetLights(LightManager var1) {
      synchronized(this.lights) {
         synchronized(this.lightSources) {
            Arrays.fill(this.lightSources, (Object)null);
         }

      }
   }

   public GameLight getLight(int var1, int var2) {
      var1 = GameMath.limit(var1, this.startX, this.endX);
      var2 = GameMath.limit(var2, this.startY, this.endY);
      int var3 = this.getIndex(var1, var2);
      synchronized(this.lights) {
         GameLight var5 = this.lights[var3];
         if (var5 == null) {
            var5 = this.manager.newLight(0.0F);
            this.lights[var3] = var5;
         }

         return var5;
      }
   }

   public List<SourcedGameLight> getLightSources(int var1, int var2) {
      if (this.isOutsideMap(var1, var2)) {
         return Collections.emptyList();
      } else {
         int var3 = this.getIndex(var1, var2);
         synchronized(this.lightSources) {
            LinkedList var5 = this.lightSources[var3];
            return (List)(var5 == null ? Collections.emptyList() : new ArrayList(var5));
         }
      }
   }

   protected int getIndex(int var1, int var2) {
      return var1 - this.startX + (var2 - this.startY) * this.width;
   }

   public boolean isOutsideMap(int var1, int var2) {
      return var1 < this.startX || var2 < this.startY || var1 > this.endX || var2 > this.endY;
   }

   protected void addSourcedLight(int var1, int var2, SourcedGameLight var3, Collection<Integer> var4) {
      int var5 = this.getIndex(var1, var2);
      synchronized(this.lightSources) {
         LinkedList var7 = this.lightSources[var5];
         if (var7 == null) {
            var7 = new LinkedList();
            this.lightSources[var5] = var7;
         }

         var7.removeIf((var3x) -> {
            if (var3x.light.getColorHash() != var3.light.getColorHash()) {
               return false;
            } else if (var3x.light.getLevel() > var3.light.getLevel()) {
               return false;
            } else if (var3.sourceX == var1 && var3.sourceY == var2) {
               return true;
            } else {
               return var3x.sourceX != var1 || var3x.sourceY != var2;
            }
         });
         var7.add(var3);
         var4.add(var5);
      }
   }

   protected boolean hasNoBetterSameSource(int var1, int var2, SourcedGameLight var3) {
      if (this.isOutsideMap(var1, var2)) {
         return false;
      } else {
         int var4 = this.getIndex(var1, var2);
         synchronized(this.lightSources) {
            LinkedList var6 = this.lightSources[var4];
            return var6 == null ? true : var6.stream().filter((var1x) -> {
               return var1x.sourceX == var3.sourceX && var1x.sourceY == var3.sourceY;
            }).noneMatch((var1x) -> {
               return var1x.light.getLevel() >= var3.light.getLevel();
            });
         }
      }
   }

   protected boolean hasNoBetterSameColor(int var1, int var2, SourcedGameLight var3) {
      if (this.isOutsideMap(var1, var2)) {
         return false;
      } else {
         int var4 = this.getIndex(var1, var2);
         synchronized(this.lightSources) {
            LinkedList var6 = this.lightSources[var4];
            return var6 == null ? true : var6.stream().filter((var1x) -> {
               return var1x.sourceX != var3.sourceX || var1x.sourceY != var3.sourceY;
            }).noneMatch((var1x) -> {
               return var1x.light.getColorHash() == var3.light.getColorHash() && var1x.light.getLevel() >= var3.light.getLevel();
            });
         }
      }
   }

   public void update(int var1, int var2, boolean var3) {
      if (!this.isOutsideMap(var1, var2)) {
         this.update(var1, var2, var1, var2, var3);
      }
   }

   public void update(int var1, int var2, int var3, int var4, boolean var5) {
      int var6 = Math.max(var1, this.startX);
      int var7 = Math.max(var2, this.startY);
      int var8 = Math.min(var3, this.endX);
      int var9 = Math.min(var4, this.endY);
      this.update(() -> {
         return new BoundsPointIterator(var6, var8, var7, var9);
      }, var5);
   }

   public void update(Iterable<Point> var1, boolean var2) {
      if (this.level.isLoadingComplete()) {
         long var3 = System.nanoTime();
         HashMap var5 = new HashMap();
         LinkedList var6 = new LinkedList();
         HashSet var7 = new HashSet();
         HashSet var8 = new HashSet();
         Runnable var9 = () -> {
            Iterator var6x = var1.iterator();

            while(true) {
               label124:
               while(true) {
                  Point var7x;
                  int var9;
                  do {
                     do {
                        do {
                           do {
                              if (!var6x.hasNext()) {
                                 HashSet var20 = new HashSet();
                                 Iterator var21 = var7.iterator();

                                 while(var21.hasNext()) {
                                    Point var22 = (Point)var21.next();
                                    this.removeSource(var22.x, var22.y, var20, var8);
                                 }

                                 synchronized(this.lightSources) {
                                    Iterator var23 = var20.iterator();

                                    label76:
                                    while(true) {
                                       LinkedList var10;
                                       Point var26;
                                       do {
                                          if (!var23.hasNext()) {
                                             break label76;
                                          }

                                          var26 = (Point)var23.next();
                                          var10 = this.lightSources[this.getIndex(var26.x, var26.y)];
                                       } while(var10 == null);

                                       Iterator var28 = var10.iterator();

                                       while(var28.hasNext()) {
                                          SourcedGameLight var30 = (SourcedGameLight)var28.next();
                                          var5.compute(var30.light.getColorHash(), (var2, var3) -> {
                                             if (var3 == null) {
                                                return new LightSources(var26.x, var26.y, var30);
                                             } else {
                                                var3.addSource(var26.x, var26.y, var30);
                                                return var3;
                                             }
                                          });
                                       }
                                    }
                                 }

                                 var21 = var5.values().iterator();

                                 while(var21.hasNext()) {
                                    LightSources var24 = (LightSources)var21.next();
                                    var9 = Math.max(this.startX, var24.minX - this.maxLightDistance);
                                    int var27 = Math.max(this.startY, var24.minY - this.maxLightDistance);
                                    int var29 = Math.min(this.endX, var24.maxX + this.maxLightDistance);
                                    int var31 = Math.min(this.endY, var24.maxY + this.maxLightDistance);
                                    LightCompute var32 = new LightCompute(this.level, this, var9, var27, var29, var31);
                                    Iterator var33 = var24.iterator();

                                    while(var33.hasNext()) {
                                       PointSourcedGameLight var34 = (PointSourcedGameLight)var33.next();
                                       var32.addSource(var34.x, var34.y, var34.source);
                                    }

                                    var6.add(new LightComputeFuture(var32));
                                 }

                                 return;
                              }

                              var7x = (Point)var6x.next();
                           } while(var7x.x < this.startX);
                        } while(var7x.y < this.startY);
                     } while(var7x.x > this.endX);
                  } while(var7x.y > this.endY);

                  GameLight var8x = this.getNewLight(var7x.x, var7x.y);
                  if (var8x.getLevel() > 0.0F) {
                     SourcedGameLight var25 = new SourcedGameLight(var7x.x, var7x.y, var8x);
                     var5.compute(var8x.getColorHash(), (var1x, var2) -> {
                        if (var2 == null) {
                           return new LightSources(var25);
                        } else {
                           var2.addSource(var25);
                           return var2;
                        }
                     });
                     var7.add(var7x);
                  } else {
                     var9 = this.getIndex(var7x.x, var7x.y);
                     synchronized(this.lightSources) {
                        LinkedList var11 = this.lightSources[var9];
                        if (var11 != null) {
                           Iterator var12 = var11.iterator();

                           while(true) {
                              while(true) {
                                 if (!var12.hasNext()) {
                                    continue label124;
                                 }

                                 SourcedGameLight var13 = (SourcedGameLight)var12.next();
                                 if (var13.sourceX == var7x.x && var13.sourceY == var7x.y) {
                                    var7.add(var7x);
                                 } else {
                                    GameLight var14 = this.getNewLight(var13.sourceX, var13.sourceY);
                                    if (var14.getLevel() > 0.0F) {
                                       SourcedGameLight var15 = new SourcedGameLight(var13.sourceX, var13.sourceY, var14);
                                       var5.compute(var14.getColorHash(), (var1x, var2) -> {
                                          if (var2 == null) {
                                             return new LightSources(var15);
                                          } else {
                                             var2.addSource(var15);
                                             return var2;
                                          }
                                       });
                                       var7.add(new Point(var13.sourceX, var13.sourceY));
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         };
         AtomicInteger var10 = new AtomicInteger();
         AtomicInteger var11 = new AtomicInteger();
         ExecutorService var12 = this.level.lightManager.updateExecutor;
         if (var12 != null) {
            ThreadPoolExecutor var13 = this.level.lightManager.computeExecutor;
            if (var13 != null) {
               if (var2) {
                  var12.submit(() -> {
                     synchronized(this.updateLock) {
                        if (this.printDebug) {
                           System.out.println((this.level.isServer() ? "S" : "C") + " SETUP, E: " + var13.getActiveCount() + "/" + var13.getQueue().size() + ", " + var1 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var3));
                        }

                        var9.run();
                        if (this.printDebug) {
                           System.out.println((this.level.isServer() ? "S" : "C") + " WAITING, S: " + var5.size() + "/" + var7.size() + ", C: " + var6.size() + ", E: " + var13.getActiveCount() + "/" + var13.getQueue().size() + ", " + var1 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var3));
                        }

                        Iterator var13x;
                        LightComputeFuture var14;
                        if (var6.size() > 1) {
                           LightCompute var10002;
                           for(var13x = var6.iterator(); var13x.hasNext(); var14.future = var13.submit(var10002::compute)) {
                              var14 = (LightComputeFuture)var13x.next();
                              var10002 = var14.compute;
                              Objects.requireNonNull(var10002);
                           }

                           this.waitForComputes(var6, var10);
                        } else {
                           for(var13x = var6.iterator(); var13x.hasNext(); var14.complete = true) {
                              var14 = (LightComputeFuture)var13x.next();
                              var14.compute.compute();
                           }
                        }

                        var13x = var6.iterator();

                        while(var13x.hasNext()) {
                           var14 = (LightComputeFuture)var13x.next();
                           if (var14.complete) {
                              var11.addAndGet(var14.compute.apply(var8));
                           }
                        }

                        synchronized(this.lights) {
                           Iterator var20 = var8.iterator();

                           while(true) {
                              if (!var20.hasNext()) {
                                 break;
                              }

                              Integer var15 = (Integer)var20.next();
                              this.updateLightSources(var15);
                           }
                        }

                        if (this.printDebug) {
                           System.out.println((this.level.isServer() ? "S" : "C") + " DONE, S: " + var5.size() + "/" + var7.size() + ", C: " + var6.size() + ", E: " + var13.getActiveCount() + "/" + var13.getQueue().size() + ", I: " + var10 + ", A: " + var11 + ", " + var1 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var3));
                        }

                     }
                  });
                  if (this.printDebug) {
                     System.out.println((this.level.isServer() ? "S" : "C") + " INITIAL, E: " + var13.getActiveCount() + "/" + var13.getQueue().size() + ", " + var1 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var3));
                  }
               } else {
                  synchronized(this.updateLock) {
                     var9.run();
                     if (this.printDebug) {
                        System.out.println((this.level.isServer() ? "S" : "C") + " START, S: " + var5.size() + "/" + var7.size() + ", C: " + var6.size() + ", " + var1 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var3));
                     }

                     Iterator var15;
                     LightComputeFuture var16;
                     if (var6.size() <= 1) {
                        for(var15 = var6.iterator(); var15.hasNext(); var16.complete = true) {
                           var16 = (LightComputeFuture)var15.next();
                           var10.addAndGet(var16.compute.compute());
                        }
                     } else {
                        LightCompute var10002;
                        for(var15 = var6.iterator(); var15.hasNext(); var16.future = var13.submit(var10002::compute)) {
                           var16 = (LightComputeFuture)var15.next();
                           var10002 = var16.compute;
                           Objects.requireNonNull(var10002);
                        }

                        this.waitForComputes(var6, var10);
                     }

                     var15 = var6.iterator();

                     while(var15.hasNext()) {
                        var16 = (LightComputeFuture)var15.next();
                        if (var16.complete) {
                           var11.addAndGet(var16.compute.apply(var8));
                        }
                     }

                     synchronized(this.lights) {
                        Iterator var22 = var8.iterator();

                        while(true) {
                           if (!var22.hasNext()) {
                              break;
                           }

                           Integer var17 = (Integer)var22.next();
                           this.updateLightSources(var17);
                        }
                     }

                     if (this.printDebug) {
                        System.out.println((this.level.isServer() ? "S" : "C") + ", S: " + var5.size() + "/" + var7.size() + ", C: " + var6.size() + ", I: " + var10 + ", a: " + var11 + ", " + var1 + ", " + GameUtils.getTimeStringNano(System.nanoTime() - var3));
                     }
                  }
               }

            }
         }
      }
   }

   private void removeSource(int var1, int var2, HashSet<Point> var3, HashSet<Integer> var4) {
      synchronized(this.lightSources) {
         LinkedList var6 = new LinkedList();
         HashSet var7 = new HashSet();
         var6.add(new Point(var1, var2));

         while(true) {
            while(true) {
               Point var8;
               int var9;
               LinkedList var10;
               do {
                  if (var6.isEmpty()) {
                     return;
                  }

                  var8 = (Point)var6.removeFirst();
                  var7.add(new Point(var8.x, var8.y));
                  var9 = this.getIndex(var8.x, var8.y);
                  var10 = this.lightSources[var9];
               } while(var10 == null);

               ListIterator var11 = var10.listIterator();

               while(var11.hasNext()) {
                  SourcedGameLight var12 = (SourcedGameLight)var11.next();
                  if (var12.sourceX == var1 && var12.sourceY == var2) {
                     var11.remove();
                     var4.add(var9);
                     Point[] var13 = crossChecks;
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        Point var16 = var13[var15];
                        Point var17 = new Point(var8.x + var16.x, var8.y + var16.y);
                        if (!this.isOutsideMap(var17.x, var17.y) && !var7.contains(var17)) {
                           var6.add(var17);
                           var3.add(var17);
                        }
                     }
                     break;
                  }
               }
            }
         }
      }
   }

   private void updateLightSources(int var1) {
      GameLight var2 = null;
      synchronized(this.lightSources) {
         LinkedList var4 = this.lightSources[var1];
         if (var4 != null) {
            if (var4.isEmpty()) {
               this.lightSources[var1] = null;
            } else {
               Comparator var5 = Comparator.comparingDouble((var0) -> {
                  return (double)var0.light.getLevel();
               });
               var5 = var5.thenComparingInt((var0) -> {
                  return var0.sourceX;
               });
               var5 = var5.thenComparingInt((var0) -> {
                  return var0.sourceY;
               });
               var2 = (GameLight)var4.stream().sorted(var5).map((var0) -> {
                  return var0.light;
               }).reduce((Object)null, (var0, var1x) -> {
                  if (var0 == null) {
                     return var1x.copy();
                  } else {
                     var0.combine(var1x);
                     return var0;
                  }
               });
            }
         }
      }

      if (var2 == null) {
         var2 = this.manager.newLight(0.0F);
      }

      synchronized(this.lights) {
         this.lights[var1] = var2;
      }
   }

   private void waitForComputes(LinkedList<LightComputeFuture> var1, AtomicInteger var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         LightComputeFuture var4 = (LightComputeFuture)var3.next();

         try {
            var2.addAndGet((Integer)var4.future.get(20L, TimeUnit.SECONDS));
            var4.complete = true;
         } catch (InterruptedException var6) {
            var4.complete = false;
         } catch (ExecutionException var7) {
            var4.complete = false;
            System.err.println("Error executing light update. I: " + var2.get());
            var7.printStackTrace();
         } catch (TimeoutException var8) {
            var4.complete = false;
            System.err.println("Timed out executing light update. I: " + var2.get());
            var4.printDebug(System.err);
            var8.printStackTrace();
         }
      }

   }

   protected abstract GameLight getNewLight(int var1, int var2);

   protected static class LightComputeFuture {
      public final LightCompute compute;
      public boolean complete;
      public Future<Integer> future;

      public LightComputeFuture(LightCompute var1) {
         this.compute = var1;
         this.complete = false;
      }

      public void printDebug(PrintStream var1) {
         this.compute.printDebug(var1);
         var1.println(this.complete);
         var1.println(this.future);
      }
   }

   protected static class LightSources implements Iterable<PointSourcedGameLight> {
      public int minX;
      public int maxX;
      public int minY;
      public int maxY;
      private LinkedList<PointSourcedGameLight> sources;

      public LightSources(int var1, int var2, SourcedGameLight var3) {
         this.sources = new LinkedList();
         this.minX = var3.sourceX;
         this.maxX = var3.sourceX;
         this.minY = var3.sourceY;
         this.maxY = var3.sourceY;
         this.sources.add(new PointSourcedGameLight(var1, var2, var3));
      }

      public LightSources(SourcedGameLight var1) {
         this(var1.sourceX, var1.sourceY, var1);
      }

      public void addSource(int var1, int var2, SourcedGameLight var3) {
         this.minX = Math.min(this.minX, var3.sourceX);
         this.maxX = Math.max(this.maxX, var3.sourceX);
         this.minY = Math.min(this.minY, var3.sourceY);
         this.maxY = Math.max(this.maxY, var3.sourceY);
         this.sources.add(new PointSourcedGameLight(var1, var2, var3));
      }

      public void addSource(SourcedGameLight var1) {
         this.addSource(var1.sourceX, var1.sourceY, var1);
      }

      public Iterator<PointSourcedGameLight> iterator() {
         return this.sources.iterator();
      }
   }

   protected static class PointSourcedGameLight {
      public final int x;
      public final int y;
      public final SourcedGameLight source;

      public PointSourcedGameLight(int var1, int var2, SourcedGameLight var3) {
         this.x = var1;
         this.y = var2;
         this.source = var3;
      }
   }
}
