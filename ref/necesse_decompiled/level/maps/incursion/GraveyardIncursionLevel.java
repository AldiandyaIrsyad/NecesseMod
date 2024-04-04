package necesse.level.maps.incursion;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.AreaFinder;
import necesse.engine.Settings;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.util.voronoi.DelaunayTriangulator;
import necesse.engine.util.voronoi.TriangleLine;
import necesse.engine.world.WorldEntity;
import necesse.gfx.drawables.WallShadowVariables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.FurnitureHousePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.furniturePresets.BedDresserPreset;
import necesse.level.maps.presets.furniturePresets.BenchPreset;
import necesse.level.maps.presets.furniturePresets.BookshelfClockPreset;
import necesse.level.maps.presets.furniturePresets.BookshelvesPreset;
import necesse.level.maps.presets.furniturePresets.CabinetsPreset;
import necesse.level.maps.presets.furniturePresets.DeskBookshelfPreset;
import necesse.level.maps.presets.furniturePresets.DinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.DisplayStandClockPreset;
import necesse.level.maps.presets.furniturePresets.ModularDinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.ModularTablesPreset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class GraveyardIncursionLevel extends IncursionLevel {
   public static int GRAVEYARD_AMBIENT_LIGHT = 60;

   public GraveyardIncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public GraveyardIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, WorldEntity var3) {
      super(var1, 150, 150, var2, var3);
      this.biome = BiomeRegistry.GRAVEYARD;
      this.generateLevel(var2);
   }

   public void generateLevel(BiomeMissionIncursionData var1) {
      GameRandom var2 = new GameRandom((long)var1.getUniqueID());
      int var3 = TileRegistry.getTileID("cryptash");

      for(int var4 = 0; var4 < this.width; ++var4) {
         for(int var5 = 0; var5 < this.height; ++var5) {
            this.setTile(var4, var5, var3);
         }
      }

      TicketSystemList var35 = new TicketSystemList();
      GameObject var36 = ObjectRegistry.getObject("air");
      var35.addObject(2000, var36);
      var35.addObject(100, ObjectRegistry.getObject("cryptcoffin"));
      var35.addObject(100, ObjectRegistry.getObject("cryptcolumn"));
      var35.addObject(100, ObjectRegistry.getObject("cryptgravestone1"));
      var35.addObject(100, ObjectRegistry.getObject("cryptgravestone2"));
      var35.addObject(100, ObjectRegistry.getObject("deadwoodtree"));
      var35.addObject(100, ObjectRegistry.getObject("vase"));
      var35.addObject(30, ObjectRegistry.getObject("deadwoodcandles"));
      var35.addObject(5, ObjectRegistry.getObject("deadwoodbench"));
      var35.addObject(5, ObjectRegistry.getObject("deadwoodchair"));

      int var9;
      for(int var6 = 0; var6 < this.width; ++var6) {
         for(int var7 = 0; var7 < this.height; ++var7) {
            GameObject var8 = (GameObject)var35.getRandomObject(var2);
            if (var8.getID() != var36.getID()) {
               var9 = var2.nextInt(4);
               if (var8.canPlace(this, var6, var7, var9) == null) {
                  MultiTile var10 = var8.getMultiTile(var9);
                  if (!var10.streamOtherIDs(var6, var7).anyMatch((var1x) -> {
                     return this.getObjectID(var1x.tileX, var1x.tileY) != 0;
                  }) && !var10.getAdjacentTiles(var6, var7, true).stream().anyMatch((var1x) -> {
                     return this.getObjectID(var1x.x, var1x.y) != 0;
                  })) {
                     var8.placeObject(this, var6, var7, var9);
                  }
               }
            }
         }
      }

      GenerationTools.fillMap(this, var2, var3, -1, 0.0F, ObjectRegistry.getObjectID("cryptgrass"), -1, 0.2F, false, true);
      ArrayList var37 = new ArrayList();
      byte var38 = -20;
      int var40 = this.width - var38 * 2;
      var9 = this.height - var38 * 2;
      int var42 = 30;
      int var11 = var40 / var42;
      int var12 = var9 / var42;
      int var13 = var40 % var42 / 2;
      int var14 = var9 % var42 / 2;

      int var16;
      int var17;
      int var18;
      int var19;
      int var20;
      for(int var15 = 0; var15 < var11; ++var15) {
         var16 = var15 * var42 + var13 + var38;
         var17 = var16 + var42;

         for(var18 = 0; var18 < var12; ++var18) {
            var19 = var18 * var42 + var14 + var38;
            var20 = var19 + var42;
            Point2D.Float var21 = new Point2D.Float((float)var2.getIntBetween(var16, var17), (float)var2.getIntBetween(var19, var20));
            var37.add(var21);
         }
      }

      ArrayList var39 = new ArrayList();
      DelaunayTriangulator.compute(var37, false, var39);
      var40 = TileRegistry.getTileID("cryptpath");
      Iterator var41 = var39.iterator();

      while(var41.hasNext()) {
         TriangleLine var43 = (TriangleLine)var41.next();
         LinesGeneration.pathTiles(new Line2D.Float(var43.p1, var43.p2), true, (var2x, var3x) -> {
            for(int var4 = 0; var4 < 2; ++var4) {
               for(int var5 = 0; var5 < 2; ++var5) {
                  int var6 = var3x.x + var4;
                  int var7 = var3x.y + var5;
                  if (var6 >= 0 && var7 >= 0 && var6 < this.width && var7 < this.height) {
                     this.setTile(var6, var7, var40);
                     this.setObject(var6, var7, 0);
                  }
               }
            }

         });
      }

      var9 = Math.min(var37.size(), var2.getIntBetween(12, 18));
      var42 = ObjectRegistry.getObjectID("cryptfence");
      var11 = ObjectRegistry.getObjectID("cryptfencegate");
      BiConsumer var44 = (var0, var1x) -> {
         switch (var1x) {
            case 0:
               --var0.y;
               break;
            case 1:
               ++var0.x;
               break;
            case 2:
               ++var0.y;
               break;
            default:
               --var0.x;
         }

      };

      int var23;
      int var28;
      label262:
      for(var13 = 0; var13 < var9 && !var37.isEmpty(); ++var13) {
         var14 = var2.nextInt(var37.size());
         Point2D.Float var45 = (Point2D.Float)var37.remove(var14);
         Point var47 = new Point((int)var45.x, (int)var45.y);
         if (var47.x >= 0 && var47.y >= 0 && var47.x < this.width && var47.y < this.height) {
            ArrayList var48 = new ArrayList();
            var18 = var2.nextInt(4);

            for(var19 = 0; var19 < 4; ++var19) {
               if (var19 == 0 || var2.getChance(2)) {
                  var48.add(new OpenDirection(var47.x, var47.y, (var19 + var18) % 4, 0));
               }
            }

            ArrayList var51 = new ArrayList();

            while(true) {
               int var25;
               OpenDirection var55;
               do {
                  do {
                     if (var48.isEmpty()) {
                        if (!var51.isEmpty()) {
                           var20 = 1 + var2.nextInt(var51.size());

                           for(int var56 = 0; var56 < var20 && !var51.isEmpty(); ++var56) {
                              int var58 = var2.nextInt(var51.size());
                              ArrayList var61 = (ArrayList)var51.remove(var58);
                              Point var62 = (Point)var2.getOneOf((List)var61);
                              if (var62 != null) {
                                 this.setObject(var62.x, var62.y, var11);
                              }
                           }
                        }
                        continue label262;
                     }

                     ArrayList var52 = new ArrayList();
                     var55 = (OpenDirection)var48.remove(0);
                     Point var22 = new Point(var55.x, var55.y);
                     var23 = var2.getIntBetween(8, 20);
                     int var24 = var2.nextInt(5);

                     for(var25 = 0; var25 < var24; ++var25) {
                        var44.accept(var22, var55.dir);
                     }

                     for(var25 = 0; var25 < var23 && !this.getTile(var55.x, var55.y).isFloor && !this.getTile(var22.x, var22.y).isFloor && (var25 == 0 || !this.getObject(var55.x, var55.y).isFence && !this.getObject(var22.x, var22.y).isFence); ++var25) {
                        if (var25 > 0 && var25 < var23 - 1) {
                           var52.add(new Point(var55));
                        }

                        this.setObject(var55.x, var55.y, var42);
                        var44.accept(var55, var55.dir);
                        var44.accept(var22, var55.dir);
                     }

                     var51.add(var52);
                  } while(var25 <= 3);
               } while(!var2.getChance(var55.depth + 2));

               int var26 = var2.nextInt(4);

               for(int var27 = 0; var27 < 4; ++var27) {
                  var28 = (var27 + var26) % 4;
                  if (var28 != var55.dir && var2.getChance(1 + var27)) {
                     var48.add(new OpenDirection(var55.x, var55.y, var28, var55.depth + 1));
                  }
               }
            }
         }
      }

      var13 = Math.min(var37.size(), var2.getIntBetween(12, 18));
      var14 = TileRegistry.getTileID("deadwoodfloor");
      TicketSystemList var46 = new TicketSystemList();
      var46.addObject(100, new BedDresserPreset(FurnitureSet.deadwood, 2));
      var46.addObject(100, new BenchPreset(FurnitureSet.deadwood, 2));
      var46.addObject(100, new BookshelfClockPreset(FurnitureSet.deadwood, 2));
      var46.addObject(100, new BookshelvesPreset(FurnitureSet.deadwood, 2, 3));
      var46.addObject(100, new CabinetsPreset(FurnitureSet.deadwood, 2, 3));
      var46.addObject(100, new DeskBookshelfPreset(FurnitureSet.deadwood, 2));
      var46.addObject(100, new DinnerTablePreset(FurnitureSet.deadwood, 2));
      var46.addObject(100, new DisplayStandClockPreset(FurnitureSet.deadwood, 2, (GameRandom)null, (LootTable)null, new Object[0]));
      var46.addObject(100, new ModularDinnerTablePreset(FurnitureSet.deadwood, 2, 1));
      var46.addObject(50, new ModularTablesPreset(FurnitureSet.deadwood, 2, 2, true));

      for(var16 = 0; var16 < var13 && !var37.isEmpty(); ++var16) {
         var17 = var2.nextInt(var37.size());
         Point2D.Float var50 = (Point2D.Float)var37.remove(var17);
         if (!(var50.x < 0.0F) && !(var50.y < 0.0F) && !(var50.x >= (float)this.width) && !(var50.y >= (float)this.height)) {
            var19 = var2.getIntBetween(8, 14);
            var20 = var2.getIntBetween(7, 10);
            final Object var57 = new FurnitureHousePreset(var19, var20, var14, WallSet.crypt, var2, var46, 0.8F);
            ((Preset)var57).addCanApplyRectEachPredicate(0, 0, var19, var20, 0, (var0, var1x, var2x, var3x) -> {
               return !var0.getTile(var1x, var2x).isFloor;
            });
            AtomicReference var59 = new AtomicReference();
            ((Preset)var57).onObjectApply((var1x, var2x, var3x, var4x, var5x) -> {
               if (var4x == WallSet.crypt.doorClosed || var4x == WallSet.crypt.doorOpen) {
                  var59.set(new Point(var2x, var3x));
               }

            });
            var23 = var2.nextInt(4);

            try {
               var57 = ((Preset)var57).rotate(PresetRotation.toRotationAngle(var23));
            } catch (PresetRotateException var34) {
            }

            AreaFinder var63 = new AreaFinder((int)var50.x, (int)var50.y, 5, true) {
               public boolean checkPoint(int var1, int var2) {
                  return ((Preset)var57).canApplyToLevelCentered(GraveyardIncursionLevel.this, var1, var2);
               }
            };
            var63.runFinder();
            if (var63.hasFound()) {
               Point var64 = var63.getFirstFind();
               ((Preset)var57).applyToLevelCentered(this, var64.x, var64.y);
               Point var65 = (Point)var59.get();
               if (var65 != null) {
                  var28 = 0;
                  int var29;
                  int var30;
                  byte var31;
                  byte var32;
                  boolean var33;
                  switch (var23) {
                     case 0:
                        var28 = var2.getIntOffset(270, 45);
                        var33 = var19 % 2 == 0;
                        var29 = var33 ? 2 : 3;
                        var30 = 2;
                        var31 = -1;
                        var32 = -1;
                        break;
                     case 1:
                        var28 = var2.getIntOffset(0, 45);
                        var33 = var20 % 2 == 0;
                        var29 = 2;
                        var30 = var33 ? 2 : 3;
                        var31 = 1;
                        var32 = -1;
                        break;
                     case 2:
                        var28 = var2.getIntOffset(90, 45);
                        var33 = var19 % 2 == 0;
                        var29 = var33 ? 2 : 3;
                        var30 = 2;
                        var31 = -1;
                        var32 = 1;
                        break;
                     case 3:
                        var28 = var2.getIntOffset(180, 45);
                        var33 = var20 % 2 == 0;
                        var29 = 2;
                        var30 = var33 ? 2 : 3;
                        var31 = -1;
                        var32 = -1;
                        break;
                     default:
                        var29 = 0;
                        var30 = 0;
                        var31 = 0;
                        var32 = 0;
                  }

                  Point2D.Float var66 = GameMath.getAngleDir((float)var28);
                  LinesGeneration.pathTilesBreak(new Line2D.Float((float)var65.x, (float)var65.y, (float)var65.x + var66.x * 30.0F, (float)var65.y + var66.y * 30.0F), true, (var6x, var7x) -> {
                     boolean var8 = false;

                     for(int var9 = 0; var9 < var29; ++var9) {
                        for(int var10 = 0; var10 < var30; ++var10) {
                           int var11 = var7x.x + var9 + var31;
                           int var12 = var7x.y + var10 + var32;
                           if (var11 >= 0 && var12 >= 0 && var11 < this.width && var12 < this.height && !this.getTile(var11, var12).isFloor) {
                              this.setTile(var11, var12, var40);
                              this.setObject(var11, var12, 0);
                              var8 = true;
                           }
                        }
                     }

                     return var8;
                  });
               }
            }
         }
      }

      byte var49 = 40;
      var17 = var2.getIntOffset(this.width / 2, this.width / 2 - var49 * 2);
      var18 = var2.getIntOffset(this.height / 2, this.height / 2 - var49 * 2);
      TriangleLine var53 = (TriangleLine)var39.stream().min(Comparator.comparingDouble((var2x) -> {
         return var2x.p1.distance((double)var17, (double)var18);
      })).orElse((Object)null);
      if (var53 != null) {
         IncursionBiome.generateEntrance(this, var2, (int)var53.p1.x + 1, (int)var53.p1.y + 1, 30, var3, "cryptpath", (String)null, "cryptcolumn");
      } else {
         IncursionBiome.generateEntrance(this, var2, this.width / 2, this.height / 2, 30, var3, "cryptpath", (String)null, "cryptcolumn");
      }

      GameObject var54;
      if (var1 instanceof BiomeExtractionIncursionData) {
         var54 = ObjectRegistry.getObject("cryptnightsteelorerocksmall");
         GenerationTools.generateGuaranteedRandomVeins(this, var2, 25, 1, 1, (var2x, var3x, var4x) -> {
            return var2x.getTileID(var3x, var4x) == var3 && var54.canPlace(var2x, var3x, var4x, 0) == null;
         }, (var1x, var2x, var3x) -> {
            var54.placeObject(var1x, var2x, var3x, 0);
         });
      }

      var54 = ObjectRegistry.getObject("cryptupgradeshardorerocksmall");
      GenerationTools.generateGuaranteedRandomVeins(this, var2, 30, 1, 1, (var2x, var3x, var4x) -> {
         return var2x.getTileID(var3x, var4x) == var3 && var54.canPlace(var2x, var3x, var4x, 0) == null;
      }, (var1x, var2x, var3x) -> {
         var54.placeObject(var1x, var2x, var3x, 0);
      });
      GameObject var60 = ObjectRegistry.getObject("cryptalchemyshardorerocksmall");
      GenerationTools.generateGuaranteedRandomVeins(this, var2, 30, 1, 1, (var2x, var3x, var4x) -> {
         return var2x.getTileID(var3x, var4x) == var3 && var60.canPlace(var2x, var3x, var4x, 0) == null;
      }, (var1x, var2x, var3x) -> {
         var60.placeObject(var1x, var2x, var3x, 0);
      });
      GenerationTools.checkValid(this);
   }

   public LightManager constructLightManager() {
      return new LightManager(this) {
         public void updateAmbientLight() {
            if (this.ambientLightOverride != null) {
               this.ambientLight = this.ambientLightOverride;
            } else if (Settings.alwaysLight) {
               this.ambientLight = this.newLight(150.0F);
            } else {
               this.ambientLight = GraveyardIncursionLevel.this.lightManager.newLight((float)GraveyardIncursionLevel.GRAVEYARD_AMBIENT_LIGHT);
            }
         }
      };
   }

   public Stream<WallShadowVariables> getWallShadows() {
      Stream var1 = super.getWallShadows();
      float var2 = this.getWorldEntity().getMoonLightFloat();
      if (var2 <= 0.0F) {
         return var1;
      } else {
         float var3 = this.getWorldEntity().getMoonProgress();
         return var3 <= 0.0F ? var1 : Stream.concat(var1, Stream.of(WallShadowVariables.fromProgress(var2, var3, 16.0F, 160.0F)));
      }
   }

   protected static class OpenDirection extends Point {
      public int dir;
      public int depth;

      public OpenDirection(int var1, int var2, int var3, int var4) {
         super(var1, var2);
         this.dir = var3;
         this.depth = var4;
      }
   }
}
