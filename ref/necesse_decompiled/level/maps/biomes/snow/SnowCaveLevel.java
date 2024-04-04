package necesse.level.maps.biomes.snow;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.RandomLootAreaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class SnowCaveLevel extends SnowSurfaceLevel {
   public SnowCaveLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SnowCaveLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public void generateLevel() {
      CaveGeneration var1 = new CaveGeneration(this, "snowrocktile", "snowrock");
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var1), (var1x) -> {
         var1.generateLevel();
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var1));
      int var2 = ObjectRegistry.getObjectID("snowcrate");
      int var3 = ObjectRegistry.getObjectID("minecarttrack");
      LinkedList var4 = new LinkedList();
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var1), (var5x) -> {
         GenerationTools.generateRandomSmoothVeinsC(this, var1.random, 0.04F, 5, 7.0F, 15.0F, 3.0F, 5.0F, (var2x) -> {
            var2x.forEachTile(this, (var1x, var2, var3x) -> {
               var1x.setTile(var2, var3x, TileRegistry.spiderNestID);
               if (var1.random.getChance(0.95F)) {
                  var1x.setObject(var2, var3x, ObjectRegistry.cobWebID);
               } else {
                  var1x.setObject(var2, var3x, 0);
               }

            });
            ArrayList var3 = new ArrayList(var2x.getAliveUnordered().size());
            byte var4 = 3;
            ArrayList var5 = new ArrayList((var4 * 2 + 1) * 2);

            for(int var6 = -var4; var6 <= var4; ++var6) {
               for(int var7 = -var4; var7 <= var4; ++var7) {
                  if (var6 != 0 || var7 != 0) {
                     var5.add(new Point(var6, var7));
                  }
               }
            }

            Point[] var8 = (Point[])var5.toArray(new Point[0]);
            var2x.forEachTile(this, (var3x, var4x, var5x) -> {
               if (var2x.isAllAlive(var4x, var5x, var8)) {
                  var3.add(new Point(var4x, var5x));
               }

            });
            if (var1.random.getChance(0.5F) && !var3.isEmpty()) {
               Point var9 = (Point)var1.random.getOneOf((List)var3);
               this.setObject(var9.x, var9.y, ObjectRegistry.getObjectID("royaleggobject"));
               this.setObject(var9.x, var9.y + 1, 0);
            }

            var2x.spawnMobs(this, var1.random, "blackcavespider", 4, 8, 1, 4);
         });
         GenerationTools.generateRandomPoints(this, var1.random, 0.01F, 15, (var5) -> {
            LinesGeneration var6 = new LinesGeneration(var5.x, var5.y);
            ArrayList var7 = new ArrayList();
            int var8 = var1.random.nextInt(4) * 90;
            int var9 = var1.random.getIntBetween(3, 10);

            for(int var10 = 0; var10 < var9; ++var10) {
               var6 = var6.addArm((float)var1.random.getIntOffset(var8, 20), (float)var1.random.getIntBetween(5, 25), 3.0F);
               var7.add(var6);
               int var11 = (Integer)var1.random.getOneOfWeighted(Integer.class, 15, 0, 5, 90, 5, -90);
               var8 += var11;
            }

            CellAutomaton var28 = var6.doCellularAutomaton(var1.random);
            var28.forEachTile(this, (var2x, var3x, var4x) -> {
               if (var2x.isSolidTile(var3x, var4x)) {
                  var2x.setObject(var3x, var4x, 0);
               }

               if (var1.random.getChance(0.05)) {
                  var2x.setObject(var3x, var4x, var2);
               }

            });
            ArrayList var29 = new ArrayList();
            var6.getRoot().recursiveLines((var3x) -> {
               GameLinkedList var4 = new GameLinkedList();
               LinesGeneration.pathTiles(var3x.getTileLine(), true, (var1, var2) -> {
                  var4.add(var2);
               });
               Iterator var5 = var4.elements().iterator();

               while(var5.hasNext()) {
                  GameLinkedList.Element var6 = (GameLinkedList.Element)var5.next();
                  Point var7 = (Point)var6.object;
                  if ((new Rectangle(2, 2, this.width - 4, this.height - 4)).contains(var7)) {
                     var29.add(var7);
                     GameLinkedList.Element var8 = var6.next();
                     if (var8 != null) {
                        Point var9 = (Point)var8.object;
                        if (var9.x < var7.x) {
                           this.setObject(var7.x, var7.y, var3, 3);
                        } else if (var9.x > var7.x) {
                           this.setObject(var7.x, var7.y, var3, 1);
                        } else if (var9.y < var7.y) {
                           this.setObject(var7.x, var7.y, var3, 0);
                        } else if (var9.y > var7.y) {
                           this.setObject(var7.x, var7.y, var3, 2);
                        }
                     } else {
                        GameLinkedList.Element var11 = var6.prev();
                        if (var11 != null) {
                           Point var10 = (Point)var11.object;
                           if (var10.x < var7.x) {
                              this.setObject(var7.x, var7.y, var3, 1);
                           } else if (var10.x > var7.x) {
                              this.setObject(var7.x, var7.y, var3, 3);
                           } else if (var10.y < var7.y) {
                              this.setObject(var7.x, var7.y, var3, 2);
                           } else if (var10.y > var7.y) {
                              this.setObject(var7.x, var7.y, var3, 0);
                           }
                        } else {
                           this.setObject(var7.x, var7.y, var3, 0);
                        }
                     }
                  }
               }

               return true;
            });
            int var12 = (Integer)var1.random.getOneOfWeighted(Integer.class, 100, 0, 200, 1, 50, 2);

            int var13;
            int var14;
            for(var13 = 0; var13 < var12 && !var29.isEmpty(); ++var13) {
               var14 = var1.random.nextInt(var29.size());
               Point var15 = (Point)var29.remove(var14);
               Mob var16 = MobRegistry.getMob("minecart", this);
               this.entityManager.addMob(var16, (float)(var15.x * 32 + 16), (float)(var15.y * 32 + 16));
               var4.add(var16);
            }

            var13 = (Integer)var1.random.getOneOfWeighted(Integer.class, 100, 0, 200, 1, 50, 2);

            for(var14 = 0; var14 < var13 && !var7.isEmpty(); ++var14) {
               int var30 = var1.random.nextInt(var7.size());
               LinesGeneration var31 = (LinesGeneration)var7.remove(var30);
               int var17 = var1.random.getIntBetween(10, 14) * (Integer)var1.random.getOneOf((Object[])(1, -1));
               float var18 = (float)(new Point(var31.x1, var31.y1)).distance((double)var31.x2, (double)var31.y2);
               Point2D.Float var19 = GameMath.normalize((float)(var31.x1 - var31.x2), (float)(var31.y1 - var31.y2));
               float var20 = var1.random.getFloatBetween(0.0F, var18);
               Point2D.Float var21 = new Point2D.Float((float)var31.x2 + var19.x * var20, (float)var31.y2 + var19.y * var20);
               Point2D.Float var22 = GameMath.getPerpendicularPoint(var21, 2.0F * Math.signum((float)var17), var19);
               Point2D.Float var23 = GameMath.getPerpendicularPoint(var21, (float)var17, var19);
               Line2D.Float var24 = new Line2D.Float(var22, var23);
               LinkedList var25 = new LinkedList();
               LinesGeneration.pathTiles(var24, true, (var1x, var2x) -> {
                  var25.add(var2x);
               });
               Iterator var26 = var25.iterator();

               Point var27;
               while(var26.hasNext()) {
                  var27 = (Point)var26.next();
                  this.wireManager.setWire(var27.x, var27.y, 0, true);
                  if (this.getObject(var27.x, var27.y).isSolid) {
                     this.setObject(var27.x, var27.y, 0);
                  }
               }

               Point var32 = (Point)var25.getFirst();
               var27 = (Point)var25.getLast();
               this.setObject(var32.x, var32.y, ObjectRegistry.getObjectID("rocklever"));
               this.setObject(var27.x, var27.y, ObjectRegistry.getObjectID("tnt"));
            }

         });
         AtomicInteger var6 = new AtomicInteger();
         GameObject var7 = ObjectRegistry.getObject("cryptgrass");
         GenerationTools.generateRandomSmoothVeinsC(this, var1.random, 0.0075F, 2, 3.0F, 5.0F, 8.0F, 10.0F, (var4x) -> {
            var4x.streamAliveOrdered().forEachOrdered((var2) -> {
               var1.addIllegalCrateTile(var2.x, var2.y);
               this.setTile(var2.x, var2.y, TileRegistry.cryptAshID);
               this.setObject(var2.x, var2.y, 0);
            });
            var4x.placeEdgeWalls(this, WallSet.snowStone.wall, true);
            ArrayList var5 = new ArrayList();
            var4x.streamAliveOrdered().forEachOrdered((var5x) -> {
               if (var1.random.getChance(0.2F) && var7.canPlace(this, var5x.x, var5x.y, 0) == null) {
                  var7.placeObject(this, var5x.x, var5x.y, 0);
               }

               if (this.getObjectID(var5x.x, var5x.y) == 0 && this.getObjectID(var5x.x - 1, var5x.y) == 0 && this.getObjectID(var5x.x + 1, var5x.y) == 0 && this.getObjectID(var5x.x, var5x.y - 1) == 0 && this.getObjectID(var5x.x, var5x.y + 1) == 0) {
                  if (var1.random.getChance(0.2F)) {
                     int var6 = var1.random.nextInt(4);
                     Point[] var7x = new Point[]{new Point(-1, -1), new Point(1, -1), new Point(0, -2)};
                     if (this.getRelativeAnd(var5x.x, var5x.y, PresetUtils.getRotatedPoints(0, 0, var6, var7x), (var2, var3) -> {
                        return var4x.isAlive(var2, var3) && this.getObjectID(var2, var3) == 0;
                     })) {
                        ObjectRegistry.getObject(ObjectRegistry.getObjectID("stonecoffin")).placeObject(this, var5x.x, var5x.y, var6);
                        var5.add(var5x);
                     }
                  } else if (var1.random.getChance(0.06F)) {
                     this.setObject(var5x.x, var5x.y, ObjectRegistry.getObjectID("stonecolumn"));
                  } else if (var1.random.getChance(0.3F)) {
                     var1.random.runOneOf(() -> {
                        this.setObject(var5x.x, var5x.y, ObjectRegistry.getObjectID("gravestone1"), var1.random.nextInt(4));
                     }, () -> {
                        this.setObject(var5x.x, var5x.y, ObjectRegistry.getObjectID("gravestone2"), var1.random.nextInt(4));
                     });
                  }
               }

            });
            Point var7x;
            if (!var5.isEmpty()) {
               Point var6x = (Point)var1.random.getOneOf((List)var5);
               var7x = (Point)var1.random.getOneOf((List)var5);
               LootTablePresets.caveCryptUniqueItems.applyToLevel(var1.random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, var6x.x, var6x.y, this, var6);
               LootTablePresets.caveCryptBloodPlateItems.applyToLevel(var1.random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, var7x.x, var7x.y, this, var6);
            }

            Iterator var8 = var5.iterator();

            while(var8.hasNext()) {
               var7x = (Point)var8.next();
               LootTablePresets.caveCryptCoffin.applyToLevel(var1.random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, var7x.x, var7x.y, this, var6);
            }

            var4x.spawnMobs(this, var1.random, "vampire", 25, 45, 1, 4);
         });
         int var8 = TileRegistry.waterID;
         int var9 = TileRegistry.iceID;
         GenerationTools.generateRandomSmoothVeinsC(this, var1.random, 0.07F, 2, 2.0F, 10.0F, 3.0F, 5.0F, (var4x) -> {
            boolean var5 = var1.random.getChance(0.5F);
            if (var5) {
               CellAutomaton var6 = new CellAutomaton();
               var4x.forEachTile(this, (var3, var4, var5x) -> {
                  if (var3.getTileID(var4, var5x) == var1.rockTile) {
                     var3.setTile(var4, var5x, var9);
                     var6.setAlive(var4, var5x);
                  }

               });
               var4x.forEachTile(this, (var2, var3, var4) -> {
                  if (var4x.countDead(var3, var4, CellAutomaton.allNeighbours) > 0) {
                     var6.setDead(var3, var4);
                  }

               });
               var6.doCellularAutomaton(4, 100, 2);
               var6.forEachTile(this, (var1x, var2, var3) -> {
                  var1x.setTile(var2, var3, var8);
               });
            } else {
               var4x.forEachTile(this, (var2, var3, var4) -> {
                  if (var2.getTileID(var3, var4) == var1.rockTile) {
                     var2.setTile(var3, var4, var9);
                  }

               });
            }

         });
         GenerationTools.iterateLevel(this, (var2x, var3x) -> {
            return this.getTileID(var2x, var3x) == var9;
         }, (var1x, var2x) -> {
            for(int var3 = -1; var3 <= 1; ++var3) {
               for(int var4 = -1; var4 <= 1; ++var4) {
                  if (this.getObject(var1x + var3, var2x + var4).isRock) {
                     this.setObject(var1x + var3, var2x + var4, 0);
                  }
               }
            }

         });
         this.liquidManager.calculateShores();
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("snowcaverock"), 0.005F);
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("snowcaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveOresEvent(this, var1), (var1x) -> {
         var1.generateOreVeins(0.3F, 3, 6, ObjectRegistry.getObjectID("frostshardsnow"));
         var1.generateOreVeins(0.3F, 3, 6, ObjectRegistry.getObjectID("copperoresnow"));
         var1.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoresnow"));
         var1.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoresnow"));
      });
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var1));
      PresetGeneration var5 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var1, var5), (var3x) -> {
         AtomicInteger var4 = new AtomicInteger();
         int var5x = var1.random.getIntBetween(13, 18);

         int var6;
         for(var6 = 0; var6 < var5x; ++var6) {
            RandomCaveChestRoom var7 = new RandomCaveChestRoom(var1.random, LootTablePresets.snowCaveChest, var4, new ChestRoomSet[]{ChestRoomSet.snowStone, ChestRoomSet.ice, ChestRoomSet.wood});
            var7.replaceTile(TileRegistry.stoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID)));
            var7.replaceTile(TileRegistry.snowStoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.snowStoneFloorID, TileRegistry.snowStoneBrickFloorID)));
            var5.findRandomValidPositionAndApply(var1.random, 5, var7, 10, true, true);
         }

         var6 = var1.random.getIntBetween(5, 10);

         for(int var14 = 0; var14 < var6; ++var14) {
            RandomLootAreaPreset var8 = new RandomLootAreaPreset(var1.random, 15, "snowstonecolumn", new String[]{"frozendwarf"});
            var5.findRandomValidPositionAndApply(var1.random, 5, var8, 10, true, true);
         }

         AtomicInteger var15 = new AtomicInteger();
         int var16 = var1.random.getIntBetween(25, 35);

         for(int var9 = 0; var9 < var16; ++var9) {
            WallSet var10 = (WallSet)var1.random.getOneOf((Object[])(WallSet.snowStone, WallSet.wood));
            FurnitureSet var11 = (FurnitureSet)var1.random.getOneOf((Object[])(FurnitureSet.pine, FurnitureSet.spruce));
            String var12 = (String)var1.random.getOneOf((Object[])("woodfloor", "woodfloor", "snowstonefloor", "snowstonebrickfloor"));
            Preset var13 = ((CaveRuins.CaveRuinGetter)var1.random.getOneOf((List)CaveRuins.caveRuinGetters)).get(var1.random, var10, var11, var12, LootTablePresets.snowCaveRuinsChest, var15);
            var5.findRandomValidPositionAndApply(var1.random, 5, var13, 10, true, true);
         }

         var1.generateRandomCrates(0.03F, var2);
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var1, var5));
      GenerationTools.checkValid(this);
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         Mob var7 = (Mob)var6.next();
         if (this.getObjectID(var7.getTileX(), var7.getTileY()) != var3) {
            var7.remove();
         }
      }

   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.snowCrate;
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
   }
}
