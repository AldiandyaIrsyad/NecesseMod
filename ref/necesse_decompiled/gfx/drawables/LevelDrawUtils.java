package necesse.gfx.drawables;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceWrapper;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.Entity;
import necesse.entity.chains.Chain;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ImpossibleDrawException;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.FastParticleLightMap;
import necesse.level.maps.regionSystem.SemiRegion;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class LevelDrawUtils {
   private static final int DRAW_THREADS = 4;
   private static long lastConcurrencyException;
   private Level level;
   private ThreadPoolExecutor executor;
   private LevelTileDrawOptions tileDrawables;
   private SharedTextureDrawOptions logicDrawables;
   private SharedTextureDrawOptions wireDrawables;
   private OrderableDrawables objectTileDrawables;
   private OrderableDrawables entityTileDrawables;
   private OrderableDrawables entityTopDrawables;
   private OrderableDrawables overlayDrawables;
   private List<Drawable> wallShadowDrawables;
   private List<LevelSortedDrawable> sortedDrawables;
   private List<LevelSortedDrawable> trailChainTopDrawables;
   private AtomicReference<Drawable> rainDrawable;
   private List<SortedDrawable> hudDrawables;
   private List<SortedDrawable> lastHudDrawables;
   private List<Future<?>> setupLogic;
   private TickManager lastTickManager;

   public LevelDrawUtils(Level var1) {
      this.level = var1;
      this.resetDrawables();
      this.executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), this.defaultThreadFactory());
   }

   private void resetDrawables() {
      this.tileDrawables = new LevelTileDrawOptions();
      this.logicDrawables = new SharedTextureDrawOptions(GameLogicGate.generatedLogicGateTexture);
      this.wireDrawables = new SharedTextureDrawOptions(GameResources.wire);
      this.objectTileDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> {
         return Collections.synchronizedList(new ArrayList());
      });
      this.wallShadowDrawables = Collections.synchronizedList(new ArrayList());
      this.entityTileDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> {
         return Collections.synchronizedList(new ArrayList());
      });
      this.trailChainTopDrawables = Collections.synchronizedList(new ArrayList());
      this.entityTopDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> {
         return Collections.synchronizedList(new ArrayList());
      });
      this.overlayDrawables = new OrderableDrawables(Collections.synchronizedNavigableMap(new TreeMap()), () -> {
         return Collections.synchronizedList(new ArrayList());
      });
      this.hudDrawables = Collections.synchronizedList(new ArrayList());
      this.sortedDrawables = Collections.synchronizedList(new ArrayList());
      this.rainDrawable = new AtomicReference((var0) -> {
      });
      this.setupLogic = new ArrayList();
   }

   private ThreadFactory defaultThreadFactory() {
      AtomicInteger var1 = new AtomicInteger(0);
      return (var2) -> {
         return new Thread((ThreadGroup)null, var2, "level-" + this.level.getHostString() + "-" + this.level.getIdentifier() + "-draw-" + var1.incrementAndGet());
      };
   }

   public void dispose() {
      if (this.executor != null) {
         this.executor.shutdownNow();
      }

      this.executor = null;
   }

   private Future<?> executeLogic(TickManager var1, Consumer<TickManager> var2) {
      TickManager var3 = var1 == null ? null : var1.getChild();
      return this.executor.submit(() -> {
         var2.accept(var3);
         return null;
      });
   }

   private Future<?> addExecuteList(List<Future<?>> var1, TickManager var2, Consumer<TickManager> var3) {
      Future var4 = this.executeLogic(var2, var3);
      var1.add(var4);
      return var4;
   }

   private void awaitExecuteList(List<Future<?>> var1) {
      int var2 = 0;

      InterruptedException var3;
      for(var3 = null; var2 < var1.size(); ++var2) {
         try {
            ((Future)var1.get(var2)).get();
         } catch (ExecutionException | InterruptedException var5) {
            if (var3 == null) {
               var3 = var5;
            }
         }
      }

      if (var3 != null) {
         throw new RuntimeException(var3.getMessage(), var3.getCause());
      }
   }

   @SafeVarargs
   private final void runParallel(TickManager var1, Consumer<TickManager>... var2) {
      ArrayList var3 = new ArrayList();
      Consumer[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Consumer var7 = var4[var6];
         this.addExecuteList(var3, var1, var7);
      }

      this.awaitExecuteList(var3);
   }

   private void runParallel(TickManager var1, List<Consumer<TickManager>> var2) {
      this.runParallel(var1, (Consumer[])var2.toArray(new Consumer[0]));
   }

   private void addEntityDrawProcesses(TickManager var1, GameCamera var2, DrawArea var3, List<LevelSortedDrawable> var4, OrderableDrawables var5, OrderableDrawables var6, OrderableDrawables var7, PlayerMob var8) {
      this.addExecuteList(this.setupLogic, var1, (var7x) -> {
         Performance.record(var7x, "particleSetup", (Runnable)(() -> {
            this.level.entityManager.particleOptions.addDrawables(var4, var5, var6, this.level, var1, var2, var8);
         }));
      });
      this.addExecuteList(this.setupLogic, var1, (var8x) -> {
         Performance.record(var8x, "entitySetup", (Runnable)(() -> {
            int var9 = this.level.regionManager.getRegionYByTile(var3.startY);
            int var10 = this.level.regionManager.getRegionYByTile(var3.endY);
            int var11 = this.level.regionManager.getRegionXByTile(var3.startX);
            int var12 = this.level.regionManager.getRegionXByTile(var3.endX);

            for(int var13 = var10; var13 >= var9; --var13) {
               for(int var14 = var11; var14 <= var12; ++var14) {
                  this.level.entityManager.forEachRegionDrawStreams(var14, var13, (var9x) -> {
                     Objects.requireNonNull(var3);
                     var9x.filter(var3::isIn).forEach((var8xx) -> {
                        var8xx.addDrawables(var4, var5, var6, var7, this.level, var8x, var2, var8);
                     });
                  });
               }
            }

         }));
      });
   }

   private void addTrailDrawProcesses(TickManager var1, GameCamera var2, DrawArea var3, List<LevelSortedDrawable> var4, List<LevelSortedDrawable> var5) {
      this.addExecuteList(this.setupLogic, var1, (var5x) -> {
         Performance.record(var5x, "trailSetup", (Runnable)(() -> {
            synchronized(this.level.entityManager.trails) {
               Iterator var7 = this.level.entityManager.trails.iterator();

               while(var7.hasNext()) {
                  Trail var8 = (Trail)var7.next();
                  if (var8.drawOnTop) {
                     var8.addDrawables(var5, var3.startY, var3.endY, var5x, var2);
                  } else {
                     var8.addDrawables(var4, var3.startY, var3.endY, var5x, var2);
                  }
               }

            }
         }));
      });
   }

   private void addChainDrawProcesses(TickManager var1, GameCamera var2, DrawArea var3, List<LevelSortedDrawable> var4, List<LevelSortedDrawable> var5) {
      this.addExecuteList(this.setupLogic, var1, (var5x) -> {
         Performance.record(var5x, "chainSetup", (Runnable)(() -> {
            synchronized(this.level.entityManager.chains) {
               Iterator var7 = this.level.entityManager.chains.iterator();

               while(var7.hasNext()) {
                  Chain var8 = (Chain)var7.next();
                  if (var8.drawOnTop) {
                     var8.addDrawables(var5, var3.startY, var3.endY, this.level, var5x, var2);
                  } else {
                     var8.addDrawables(var4, var3.startY, var3.endY, this.level, var5x, var2);
                  }
               }

            }
         }));
      });
   }

   private void addGroundPillarHandlers(TickManager var1, GameCamera var2, DrawArea var3, List<LevelSortedDrawable> var4) {
      this.addExecuteList(this.setupLogic, var1, (var5) -> {
         Performance.record(var5, "groundPillarSetup", (Runnable)(() -> {
            synchronized(this.level.entityManager.pillarHandlers) {
               Iterator var6 = this.level.entityManager.pillarHandlers.iterator();

               while(var6.hasNext()) {
                  GroundPillarHandler var7 = (GroundPillarHandler)var6.next();
                  var7.addDrawables(var4, var3, this.level, var1, var2);
               }

            }
         }));
      });
   }

   private void addLevelEventDrawProcesses(TickManager var1, GameCamera var2, DrawArea var3, List<LevelSortedDrawable> var4, OrderableDrawables var5, OrderableDrawables var6) {
      this.addExecuteList(this.setupLogic, var1, (var7) -> {
         Performance.record(var7, "eventSetup", (Runnable)(() -> {
            synchronized(this.level.entityManager.lock) {
               Iterator var8 = this.level.entityManager.getLevelEvents().iterator();

               while(var8.hasNext()) {
                  LevelEvent var9 = (LevelEvent)var8.next();
                  var9.addDrawables(var4, var5, var6, var3, this.level, var1, var2);
               }

            }
         }));
      });
   }

   private void addTileBasedDrawProcesses(TickManager var1, GameCamera var2, DrawArea var3, DrawArea var4, LevelTileDrawOptions var5, SharedTextureDrawOptions var6, SharedTextureDrawOptions var7, OrderableDrawables var8, List<LevelSortedDrawable> var9, boolean var10, PlayerMob var11) {
      byte var12 = 4;
      int var13 = (var4.endY - var4.startY) / var12;

      for(int var14 = 0; var14 <= var13; ++var14) {
         this.addExecuteList(this.setupLogic, var1, (var13x) -> {
            Performance.record(var13x, "tileSetup", (Runnable)(() -> {
               int var14x = var4.startY + var14 * var12;

               for(int var15 = Math.min(var14x + var12 - 1, var4.endY); var15 >= var14x; --var15) {
                  for(int var16 = var4.startX; var16 <= var4.endX; ++var16) {
                     GameObject var17 = this.level.getObject(var16, var15);
                     var17.addDrawables(var9, var8, this.level, var16, var15, var13x, var2, var11);
                     if (var16 >= var3.startX && var16 <= var3.endX && var15 >= var3.startY && var15 <= var3.endY) {
                        if (Settings.smoothLighting) {
                           if (var16 == 0 && var15 == 0) {
                              var5.addLight(var13x, this.level, var16 - 1, var15 - 1, var2);
                           }

                           if (var16 == 0) {
                              var5.addLight(var13x, this.level, var16 - 1, var15, var2);
                           }

                           if (var15 == 0) {
                              var5.addLight(var13x, this.level, var16, var15 - 1, var2);
                           }
                        }

                        GameTile var18 = this.level.getTile(var16, var15);
                        if (!var17.drawsFullTile()) {
                           var18.addDrawables(var5, var9, this.level, var16, var15, var2, var13x);
                        }

                        var18.addLightDrawables(var5, var9, this.level, var16, var15, var2, var13x);
                        if (var10) {
                           if (this.level.logicLayer.hasGate(var16, var15)) {
                              LogicGateEntity var19 = this.level.logicLayer.getEntity(var16, var15);
                              if (var19 != null) {
                                 var19.getLogicGate().addDrawables(var6, this.level, var16, var15, var19, var13x, var2);
                              }
                           }

                           this.level.wireManager.addWireDrawables(var7, var16, var15, var2, var13x);
                        }
                     }
                  }
               }

            }));
         });
      }

   }

   private void addWallShadowDrawables(TickManager var1, GameCamera var2, DrawArea var3, List<Drawable> var4) {
      this.addExecuteList(this.setupLogic, var1, (var4x) -> {
         if (!this.level.isCave) {
            Performance.record(var4x, "wallShadowSetup", (Runnable)(() -> {
               LinkedList var4x = (LinkedList)this.level.getWallShadows().filter((var0) -> {
                  return var0.lightLevel > 0.0F && var0.range > 0.0F;
               }).collect(Collectors.toCollection(LinkedList::new));
               if (!var4x.isEmpty()) {
                  int var5 = var3.startX;
                  int var6 = var3.endX;
                  int var7 = var3.startY;
                  int var8 = var3.endY;
                  Iterator var9 = var4x.iterator();

                  int var11;
                  while(var9.hasNext()) {
                     WallShadowVariables var10 = (WallShadowVariables)var9.next();
                     var10.calculate();
                     var11 = (int)Math.ceil((double)Math.abs(var10.dirXOffset / 32.0F));
                     if (var10.dirXOffset > 0.0F) {
                        var5 = Math.max(var5 - var11, 0);
                     }

                     if (var10.dirXOffset < 0.0F) {
                        var6 = Math.min(var6 + var11, this.level.width - 1);
                     }

                     int var12 = (int)Math.ceil((double)Math.abs(var10.dirYOffset / 32.0F));
                     if (var10.dirYOffset > 0.0F) {
                        var7 = Math.max(var7 - var12, 0);
                     }

                     if (var10.dirYOffset < 0.0F) {
                        var8 = Math.min(var8 + var12, this.level.height - 1);
                     }
                  }

                  HashSet var19 = new HashSet();

                  for(int var20 = var7; var20 <= var8; ++var20) {
                     for(var11 = var5; var11 <= var6; ++var11) {
                        if (!this.level.isOutside(var11, var20)) {
                           var19.add(new Point(var11, var20));
                        }
                     }
                  }

                  var4.add((var0) -> {
                     GameResources.empty.bind();
                     GL14.glBlendEquation(32776);
                     GL11.glBegin(7);
                  });
                  Iterator var21 = var4x.iterator();

                  Point var13;
                  int var14;
                  int var15;
                  WallShadowVariables var22;
                  Iterator var23;
                  while(var21.hasNext()) {
                     var22 = (WallShadowVariables)var21.next();
                     var23 = var19.iterator();

                     while(var23.hasNext()) {
                        var13 = (Point)var23.next();
                        var14 = var2.getTileDrawX(var13.x);
                        var15 = var2.getTileDrawY(var13.y);
                        byte var16 = 32;
                        byte var17 = 32;
                        var4.add((var5x) -> {
                           GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.startAlpha);
                           GL11.glVertex2f((float)var14, (float)var15);
                           GL11.glVertex2f((float)(var14 + var16), (float)var15);
                           GL11.glVertex2f((float)(var14 + var16), (float)(var15 + var17));
                           GL11.glVertex2f((float)var14, (float)(var15 + var17));
                        });
                        Point var18;
                        if (var22.east) {
                           var18 = new Point(var13.x + 1, var13.y);
                           if (!var19.contains(var18)) {
                              var4.add((var5x) -> {
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.startAlpha);
                                 GL11.glVertex2f((float)(var14 + var16), (float)var15);
                                 GL11.glVertex2f((float)(var14 + var16), (float)(var15 + var17));
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.endAlpha);
                                 GL11.glVertex2f((float)(var14 + var16) + var22.dirXOffset, (float)(var15 + var17) + var22.dirYOffset);
                                 GL11.glVertex2f((float)(var14 + var16) + var22.dirXOffset, (float)var15 + var22.dirYOffset);
                              });
                           }
                        }

                        if (var22.south) {
                           var18 = new Point(var13.x, var13.y + 1);
                           if (!var19.contains(var18)) {
                              var4.add((var5x) -> {
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.startAlpha);
                                 GL11.glVertex2f((float)var14, (float)(var15 + var17));
                                 GL11.glVertex2f((float)(var14 + var16), (float)(var15 + var17));
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.endAlpha);
                                 GL11.glVertex2f((float)(var14 + var16) + var22.dirXOffset, (float)(var15 + var17) + var22.dirYOffset);
                                 GL11.glVertex2f((float)var14 + var22.dirXOffset, (float)(var15 + var17) + var22.dirYOffset);
                              });
                           }
                        }

                        if (var22.west) {
                           var18 = new Point(var13.x - 1, var13.y);
                           if (!var19.contains(var18)) {
                              var4.add((var4xx) -> {
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.startAlpha);
                                 GL11.glVertex2f((float)var14, (float)var15);
                                 GL11.glVertex2f((float)var14, (float)(var15 + var17));
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.endAlpha);
                                 GL11.glVertex2f((float)var14 + var22.dirXOffset, (float)(var15 + var17) + var22.dirYOffset);
                                 GL11.glVertex2f((float)var14 + var22.dirXOffset, (float)var15 + var22.dirYOffset);
                              });
                           }
                        }

                        if (var22.north) {
                           var18 = new Point(var13.x, var13.y - 1);
                           if (!var19.contains(var18)) {
                              var4.add((var4xx) -> {
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.startAlpha);
                                 GL11.glVertex2f((float)var14, (float)var15);
                                 GL11.glVertex2f((float)(var14 + var16), (float)var15);
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, var22.endAlpha);
                                 GL11.glVertex2f((float)(var14 + var16) + var22.dirXOffset, (float)var15 + var22.dirYOffset);
                                 GL11.glVertex2f((float)var14 + var22.dirXOffset, (float)var15 + var22.dirYOffset);
                              });
                           }
                        }
                     }
                  }

                  var4.add((var0) -> {
                     GL11.glEnd();
                  });
                  var4.add((var0) -> {
                     GameResources.empty.bind();
                     GL14.glBlendEquation(32774);
                     GL11.glBlendFunc(770, 0);
                     GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
                     GL11.glBegin(7);
                  });
                  var21 = var4x.iterator();

                  label109:
                  while(var21.hasNext()) {
                     var22 = (WallShadowVariables)var21.next();
                     var23 = var19.iterator();

                     while(true) {
                        do {
                           while(true) {
                              if (!var23.hasNext()) {
                                 continue label109;
                              }

                              var13 = (Point)var23.next();
                              var14 = var2.getTileDrawX(var13.x);
                              var15 = var2.getTileDrawY(var13.y);
                              if (var19.contains(new Point(var13.x, var13.y + 1))) {
                                 break;
                              }

                              if (var19.contains(new Point(var13.x, var13.y - 1))) {
                                 SemiRegion.RegionType var24 = this.level.regionManager.getRegionType(var13.x, var13.y - 1);
                                 if (var24 != SemiRegion.RegionType.WALL && var24 != SemiRegion.RegionType.DOOR) {
                                    break;
                                 }
                              }
                           }
                        } while((var22.west || var22.east) && (!var19.contains(new Point(var13.x + 1, var13.y)) || !var19.contains(new Point(var13.x + 1, var13.y))));

                        var4.add((var2x) -> {
                           GL11.glVertex2f((float)var14, (float)var15);
                           GL11.glVertex2f((float)(var14 + 32), (float)var15);
                           GL11.glVertex2f((float)(var14 + 32), (float)(var15 + 32));
                           GL11.glVertex2f((float)var14, (float)(var15 + 32));
                        });
                     }
                  }

                  var4.add((var0) -> {
                     GL11.glEnd();
                  });
               }
            }));
         }
      });
   }

   private void addRainDrawProcesses(TickManager var1, AtomicReference<Drawable> var2, GameCamera var3, DrawArea var4, long var5) {
      this.addExecuteList(this.setupLogic, var1, (var6) -> {
         Performance.record(var6, "rainSetup", (Runnable)(() -> {
            float var7 = this.level.rainingLayer.getRainAlpha();
            if (!(var7 <= 0.0F)) {
               GameTexture var8 = this.level.biome.getRainTexture(this.level);
               Color var9 = this.level.biome.getRainColor(this.level);
               ArrayList var10 = new ArrayList();
               SharedTextureDrawOptions var11 = new SharedTextureDrawOptions(var8);
               byte var12 = 4;
               int var13 = (var4.endY - var4.startY) / var12;
               int var14 = var8.getWidth() / 32;
               int var15 = var8.getHeight() / 32;
               float var16 = (float)var9.getRed() / 255.0F;
               float var17 = (float)var9.getGreen() / 255.0F;
               float var18 = (float)var9.getBlue() / 255.0F;
               float var19 = (float)var9.getAlpha() / 255.0F * var7;

               for(int var20 = 0; var20 <= var13; ++var20) {
                  var10.add((var12x) -> {
                     int var13 = var4.startY + var20 * var12;

                     for(int var14x = Math.min(var13 + var12 - 1, var4.endY); var14x >= var13; --var14x) {
                        int var15x = var3.getTileDrawY(var14x);
                        int var16x = var14x % var15;
                        int var17x = var16x * 32;

                        for(int var18x = var4.startX; var18x <= var4.endX; ++var18x) {
                           if (this.level.isOutside(var18x, var14x)) {
                              int var19x = var3.getTileDrawX(var18x);
                              int var20x = var18x % var14;
                              int var21 = var20x * 32;
                              var11.addSection(var21, var21 + 32, var17x, var17x + 32).colorLight(var16, var17, var18, var19, this.level.getLightLevel(var18x, var14x)).size(32, 32).pos(var19x, var15x);
                           }
                        }
                     }

                  });
               }

               this.runParallel(var6, (List)var10);
               double var28 = 2.0;
               double var22 = 10.0;
               float var24 = (float)((double)var5 / var22 % (double)var8.getWidth()) / (float)var8.getWidth();
               var24 = Math.abs(var24 - 1.0F);
               float var25 = (float)((double)var5 / var28 % (double)var8.getHeight()) / (float)var8.getHeight();
               var25 = Math.abs(var25 - 1.0F);
               var2.set((var3x) -> {
                  GameResources.textureOffsetShader.use(var24, var25);
                  var11.draw();
                  GameResources.textureOffsetShader.stop();
               });
            }
         }));
      });
   }

   public void addHudDrawProcesses(TickManager var1, List<SortedDrawable> var2, GameCamera var3, PlayerMob var4) {
      this.addExecuteList(this.setupLogic, var1, (var4x) -> {
         Performance.record(var4x, "hudSetup", (Runnable)(() -> {
            this.level.hudManager.addDrawables(var2, var3, var4);
            final DrawOptions var4x;
            if (var4 != null) {
               PlayerInventorySlot var5 = var4.getSelectedItemSlot();
               InventoryItem var6 = var5.getInv(var4.getInv()).getItem(var5.slot);
               if (var6 != null && var6.item.isPlaceable()) {
                  var4x = () -> {
                     Point var5x;
                     if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                        Point2D.Float var6x = var4.getControllerAimDir();
                        var5x = var6.item.getControllerAttackLevelPos(this.level, var6x.x, var6x.y, var4, var6);
                     } else {
                        var5x = new Point(var3.getMouseLevelPosX(), var3.getMouseLevelPosY());
                     }

                     var6.item.getPlaceable().drawPlacePreview(this.level, var5x.x, var5x.y, var3, var4, var6, var5);
                  };
               } else {
                  var4x = null;
               }
            } else {
               var4x = null;
            }

            var2.add(new SortedDrawable() {
               public int getPriority() {
                  return Integer.MIN_VALUE;
               }

               public void draw(TickManager var1) {
                  if (var4 != null) {
                     var4.draw();
                  }

               }
            });
            var2.add(new SortedDrawable() {
               public int getPriority() {
                  return Integer.MAX_VALUE;
               }

               public void draw(TickManager var1) {
                  HUD.draw(LevelDrawUtils.this.level, var2, var3, var1);
                  Debug.drawHUD(LevelDrawUtils.this.level, var2, var3);
               }
            });
         }));
      });
   }

   public void draw(GameCamera var1, PlayerMob var2, TickManager var3, boolean var4) {
      GameCamera var5 = new GameCamera(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
      Performance.record(var3, "levelDraw", (Runnable)(() -> {
         if (var4 || this.setupLogic.size() == 0) {
            this.setupNextLogic(var5, var2, var3, var4);
         }

         PerformanceWrapper var5x = Performance.wrapTimer(var3, "awaitLast");

         try {
            this.awaitExecuteList(this.setupLogic);
         } catch (Exception var19) {
            if (!(var19 instanceof ConcurrentModificationException) && !(var19.getCause() instanceof ConcurrentModificationException)) {
               throw var19;
            }

            long var7 = System.currentTimeMillis() - lastConcurrencyException;
            if (lastConcurrencyException != 0L && var7 < 10000L) {
               throw var19;
            }

            System.err.println("Detected a concurrency error under rendering");
            var19.printStackTrace(System.err);
            lastConcurrencyException = System.currentTimeMillis();
         } finally {
            var5x.end();
         }

         this.lastTickManager = null;
         Performance.record(var3, "sortDraws", (Runnable)(() -> {
            try {
               this.sortedDrawables.sort((Comparator)null);
               this.hudDrawables.sort((Comparator)null);
            } catch (Exception var2) {
               ImpossibleDrawException.submitDrawError(var2);
            }

         }));
         LevelTileDrawOptions var6 = this.tileDrawables;
         SharedTextureDrawOptions var21 = this.logicDrawables;
         SharedTextureDrawOptions var8 = this.wireDrawables;
         OrderableDrawables var9 = this.objectTileDrawables;
         List var10 = this.wallShadowDrawables;
         OrderableDrawables var11 = this.entityTileDrawables;
         OrderableDrawables var12 = this.entityTopDrawables;
         OrderableDrawables var13 = this.overlayDrawables;
         this.lastHudDrawables = this.hudDrawables;
         List var14 = this.trailChainTopDrawables;
         List var15 = this.sortedDrawables;
         AtomicReference var16 = this.rainDrawable;
         if (!var4) {
            this.setupNextLogic(var5, var2, var3, var4);
         }

         Performance.record(var3, "draw", (Runnable)(() -> {
            Performance.record(var3, "tileDraw", (Runnable)(() -> {
               try {
                  var6.draw();
               } catch (Exception var2) {
                  ImpossibleDrawException.submitDrawError(var2);
               }

               Screen.applyDraw(() -> {
                  var6.lightOverlays.draw();
               }, () -> {
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL14.glBlendFuncSeparate(0, 768, 1, 771);
               });
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
            }));
            Performance.record(var3, "oTileDraw", (Runnable)(() -> {
               try {
                  var9.draw(var3);
               } catch (Exception var3x) {
                  ImpossibleDrawException.submitDrawError(var3x);
               }

            }));
            Performance.record(var3, "eTileDraw", (Runnable)(() -> {
               try {
                  var11.forEach((var1) -> {
                     var1.draw(var3);
                  });
               } catch (Exception var3x) {
                  ImpossibleDrawException.submitDrawError(var3x);
               }

            }));
            Performance.record(var3, "wallShadowDraw", (Runnable)(() -> {
               Screen.applyDraw(() -> {
                  var10.forEach((var1) -> {
                     var1.draw(var3);
                  });
               }, () -> {
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
               });
            }));
            Performance.record(var3, "sortedDraw", (Runnable)(() -> {
               try {
                  var15.forEach((var1) -> {
                     var1.draw(var3);
                  });
               } catch (Exception var3x) {
                  ImpossibleDrawException.submitDrawError(var3x);
               }

            }));
            Performance.record(var3, "wireDraw", (Runnable)(() -> {
               try {
                  var8.draw();
               } catch (Exception var2) {
                  ImpossibleDrawException.submitDrawError(var2);
               }

            }));
            Performance.record(var3, "logicDraw", (Runnable)(() -> {
               try {
                  var21.draw();
               } catch (Exception var2) {
                  ImpossibleDrawException.submitDrawError(var2);
               }

            }));
            Performance.record(var3, "eTopDraw", (Runnable)(() -> {
               try {
                  var14.forEach((var1) -> {
                     var1.draw(var3);
                  });
                  var12.forEach((var1) -> {
                     var1.draw(var3);
                  });
                  var13.forEach((var1) -> {
                     var1.draw(var3);
                  });
               } catch (Exception var5) {
                  ImpossibleDrawException.submitDrawError(var5);
               }

            }));
            Performance.record(var3, "rainDraw", (Runnable)(() -> {
               try {
                  if (var16.get() != null) {
                     ((Drawable)var16.get()).draw(var3);
                  }
               } catch (Exception var3x) {
                  ImpossibleDrawException.submitDrawError(var3x);
               }

            }));
         }));
      }));
   }

   public void drawLastHudDrawables(GameCamera var1, PlayerMob var2, TickManager var3) {
      Performance.record(var3, "levelHud", (Runnable)(() -> {
         this.lastHudDrawables.forEach((var1) -> {
            var1.draw(var3);
         });
      }));
   }

   public boolean drawWire(PlayerMob var1) {
      if (this.level.isTrialRoom && !GlobalData.isDevMode() && !GlobalData.debugCheatActive()) {
         return false;
      } else if (GlobalData.debugActive()) {
         return true;
      } else {
         return var1 != null && var1.getSelectedItem() != null && var1.getSelectedItem().item.showWires();
      }
   }

   private void setupNextLogic(GameCamera var1, PlayerMob var2, TickManager var3, boolean var4) {
      this.lastTickManager = var3 == null ? null : var3.getChild();
      this.level.entityManager.updateParticlesAllowed(var1);
      DrawArea var5 = new DrawArea(this.level, var1);
      DrawArea var6 = new DrawArea(this.level, var5.startX - 2, var5.endX + 2, var5.startY - 1, var5.endY + 3);
      DrawArea var7 = new DrawArea(this.level, var6.startX - 2, var6.endX + 2, var6.startY - 3, var6.endY + 7);
      LinkedList var8 = new LinkedList();
      this.addExecuteList(var8, var3, (var3x) -> {
         Performance.record(this.lastTickManager, "pLight", (Runnable)(() -> {
            if (this.level.tickManager().isGameTick() || var4) {
               DrawArea var3 = new DrawArea(this.level, var6.startX - 15, var6.endX + 15, var6.startY - 15, var6.endY + 15);
               FastParticleLightMap var4x = new FastParticleLightMap(this.level.lightManager, var3.startX, var3.startY, var3.endX, var3.endY);
               var4x.update(var3.startX, var3.startY, var3.endX, var3.endY, false);
               this.level.lightManager.setParticleLights(var4x);
            }

         }));
      });
      boolean var9 = this.drawWire(var2);
      this.resetDrawables();
      this.awaitExecuteList(var8);
      Performance.record(this.lastTickManager, "setup", (Runnable)(() -> {
         this.addTileBasedDrawProcesses(this.lastTickManager, var1, var5, var6, this.tileDrawables, this.logicDrawables, this.wireDrawables, this.objectTileDrawables, this.sortedDrawables, var9, var2);
         this.addWallShadowDrawables(this.lastTickManager, var1, var5, this.wallShadowDrawables);
         this.addEntityDrawProcesses(this.lastTickManager, var1, var7, this.sortedDrawables, this.entityTileDrawables, this.entityTopDrawables, this.overlayDrawables, var2);
         this.addTrailDrawProcesses(this.lastTickManager, var1, var6, this.sortedDrawables, this.trailChainTopDrawables);
         this.addChainDrawProcesses(this.lastTickManager, var1, var6, this.sortedDrawables, this.trailChainTopDrawables);
         this.addGroundPillarHandlers(this.lastTickManager, var1, var6, this.sortedDrawables);
         this.addLevelEventDrawProcesses(this.lastTickManager, var1, var6, this.sortedDrawables, this.entityTileDrawables, this.entityTopDrawables);
         this.addRainDrawProcesses(this.lastTickManager, this.rainDrawable, var1, var5, this.level.getWorldEntity().getTime());
         this.addHudDrawProcesses(this.lastTickManager, this.hudDrawables, var1, var2);
      }));
   }

   public static class DrawArea {
      public final int startX;
      public final int endX;
      public final int startY;
      public final int endY;

      public DrawArea(Level var1, GameCamera var2) {
         this(var1, (var2.getX() - 16) / 32, var2.getX() / 32 + var2.getWidth() / 32 + 1, (var2.getY() - 16) / 32, var2.getY() / 32 + var2.getHeight() / 32 + 1);
      }

      public DrawArea(Level var1, int var2, int var3, int var4, int var5) {
         this.startX = GameMath.limit(var2, 0, var1.width - 1);
         this.endX = GameMath.limit(var3, this.startX, var1.width - 1);
         this.startY = GameMath.limit(var4, 0, var1.height - 1);
         this.endY = GameMath.limit(var5, this.startY, var1.height - 1);
      }

      public boolean isIn(int var1, int var2) {
         return var1 >= this.startX && var1 <= this.endX && var2 >= this.startY && var2 <= this.endY;
      }

      public boolean isIn(Entity var1) {
         return var1 == null ? false : this.isIn(var1.getX() / 32, var1.getY() / 32);
      }

      public boolean isInPos(float var1, float var2) {
         return this.isIn((int)(var1 / 32.0F), (int)(var2 / 32.0F));
      }
   }
}
