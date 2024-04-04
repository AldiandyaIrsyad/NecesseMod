package necesse.gfx.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.AreaFinder;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.TilePosition;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.SourcedGameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.SemiRegion;

public class HUD {
   public static boolean debugActive;
   public static DebugShow debugShow;
   private static final LinkedList<SubmittedPath> paths;

   public HUD() {
   }

   public static void draw(Level var0, GameCamera var1, PlayerMob var2, TickManager var3) {
      State var4 = GlobalData.getCurrentState();
      int var5 = var1.getMouseLevelPosX();
      int var6 = var1.getMouseLevelPosY();
      int var7 = var1.getMouseLevelTilePosX();
      int var8 = var1.getMouseLevelTilePosY();
      int var12;
      if (var0.isClient()) {
         ClientClient var9 = var0.getClient().getClient();
         if (var9 != null && var9.summonFocusMobUniqueID != -1) {
            Mob var10 = GameUtils.getLevelMob(var9.summonFocusMobUniqueID, var0, false);
            if (var10 != null) {
               Rectangle var11 = var10.getSelectBox();
               var12 = 16;
               GameResources.particles.sprite(0, 0, 8).initDraw().size(var12, var12).color(new Color(250, 50, 50)).rotate((float)var0.getWorldEntity().getLocalTime() / 5.0F, var12 / 2, var12 / 2).draw(var1.getDrawX(var11.x + var11.width / 2) - var12 / 2, var1.getDrawY(var11.y + var11.height / 2) - var12 / 2);
            }
         }
      }

      int var14;
      int var15;
      int var16;
      int var37;
      if (debugShow == HUD.DebugShow.PATHS) {
         synchronized(paths) {
            paths.stream().filter((var1x) -> {
               return ((TilePathfinding)var1x.path.finder).level.isSamePlace(var0);
            }).forEach((var1x) -> {
               TilePathfinding.drawPathLine(var1x.path.path, var1);
            });
         }
      } else {
         int var13;
         int var17;
         SharedTextureDrawOptions var32;
         int var34;
         if (debugShow == HUD.DebugShow.HEIGHT) {
            var32 = new SharedTextureDrawOptions(GameResources.empty);
            var34 = var1.getX() / 32;
            var37 = var1.getY() / 32;
            var12 = var34 + var1.getWidth() / 32 + 1;
            var13 = var37 + var1.getHeight() / 32 + 1;

            for(var14 = var34; var14 <= var12; ++var14) {
               for(var15 = var37; var15 <= var13; ++var15) {
                  var16 = var1.getTileDrawX(var14);
                  var17 = var1.getTileDrawY(var15);
                  int var18 = var0.liquidManager.getHeight(var14, var15);
                  float var19 = var18 >= 0 ? (float)var18 / 10.0F : 1.0F;
                  float var20 = var18 <= 0 ? (float)var18 / -10.0F : 1.0F;
                  var32.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(var19, var20, 1.0F, 0.5F).pos(var16, var17);
               }
            }

            var32.draw();
         } else if (debugShow == HUD.DebugShow.WATER_TYPE) {
            var32 = new SharedTextureDrawOptions(GameResources.empty);
            var34 = var1.getX() / 32;
            var37 = var1.getY() / 32;
            var12 = var34 + var1.getWidth() / 32 + 1;
            var13 = var37 + var1.getHeight() / 32 + 1;

            for(var14 = var34; var14 <= var12; ++var14) {
               for(var15 = var37; var15 <= var13; ++var15) {
                  var16 = var1.getTileDrawX(var14);
                  var17 = var1.getTileDrawY(var15);
                  if (var0.getTile(var14, var15).isLiquid) {
                     boolean var78 = var0.liquidManager.isSaltWater(var14, var15);
                     Color var83 = var78 ? new Color(255, 0, 0) : new Color(0, 255, 0);
                     var32.add(new GameTextureSection(GameResources.empty)).size(32, 32).color(var83).alpha(0.4F).pos(var16, var17);
                  }
               }
            }

            var32.draw();
         }
      }

      if (var2 != null && var4.getFormManager() != null && !var4.getFormManager().isMouseOver()) {
         synchronized(paths) {
            paths.removeIf((var1x) -> {
               return var1x.time + 5000L < var0.getWorldEntity().getLocalTime();
            });
         }

         ArrayList var33;
         long var39;
         Iterator var46;
         StringTooltips var48;
         SemiRegion var50;
         Iterator var59;
         switch (debugShow) {
            case REGIONS:
               Region var35 = var0.regionManager.getRegionByTile(var7, var8);
               StringTooltips var40 = new StringTooltips("Region:");
               var40.add("Pos: " + var35.regionX + ", " + var35.regionY + " (" + var35.getLevelX(0) + ", " + var35.getLevelY(0) + ")");
               var40.add("Dim: " + var35.regionWidth + ", " + var35.regionHeight);
               Screen.addTooltip(var40, TooltipLocation.BOTTOM_LEFT);
               SemiRegion var41 = var35.getSemiRegion(var7 % 15, var8 % 15);
               if (var41 != null) {
                  drawCells(var41, new Color(255, 255, 0), var0, var1);
                  var46 = var41.adjacentRegions.iterator();

                  while(var46.hasNext()) {
                     var50 = (SemiRegion)var46.next();
                     drawCells(var50, new Color(0, 0, 255), var0, var1);
                  }

                  var48 = new StringTooltips("Subregion:");
                  var48.add("Type: " + var41.getType());
                  var48.add("Region ID: " + var41.getRegionID());
                  var48.add("Room ID: " + var41.getRoomID());
                  var48.add("Size: " + var41.size());
                  var48.add("Listeners: " + var41.getListenersSize());
                  Screen.addTooltip(var48, TooltipLocation.BOTTOM_LEFT);
               }
               break;
            case CONNECTED_REGIONS:
               var33 = var0.regionManager.getSameConnected(var7, var8);
               var39 = 0L;
               var46 = var33.iterator();

               while(var46.hasNext()) {
                  var50 = (SemiRegion)var46.next();
                  var39 += (long)var50.size();
                  drawCells(var50, new Color(255, 255, 0), var0, var1);
               }

               var48 = new StringTooltips();
               var48.add("Region ID: " + var0.getRegionID(var7, var8));
               var48.add("Region size: " + var39);
               var48.add("Region type: " + ((SemiRegion)var33.get(0)).getType());
               Screen.addTooltip(var48, TooltipLocation.BOTTOM_LEFT);
               break;
            case ROOMS:
               var33 = var0.regionManager.getRoom(var7, var8);
               if (!var33.isEmpty()) {
                  var39 = 0L;
                  var46 = var33.iterator();

                  while(var46.hasNext()) {
                     var50 = (SemiRegion)var46.next();
                     var39 += (long)var50.size();
                     drawCells(var50, new Color(255, 255, 0), var0, var1);
                  }

                  var48 = new StringTooltips();
                  var48.add("Room ID: " + var0.getRoomID(var7, var8));
                  var48.add("Room size: " + var0.getRoomSize(var7, var8) + " (" + var39 + ")");
                  var48.add("Room type: " + ((SemiRegion)var33.get(0)).getType());
                  Screen.addTooltip(var48, TooltipLocation.BOTTOM_LEFT);
               }
               break;
            case HOUSE:
               var33 = var0.regionManager.getHouse(var7, var8);
               TreeSet var36 = new TreeSet();
               var37 = 0;
               var12 = 0;
               long var45 = 0L;
               var59 = var33.iterator();

               while(var59.hasNext()) {
                  SemiRegion var63 = (SemiRegion)var59.next();
                  var45 += (long)var63.size();
                  if (var63.getType() == SemiRegion.RegionType.DOOR) {
                     ++var37;
                     if (var63.adjacentRegions.stream().anyMatch(SemiRegion::isOutside)) {
                        ++var12;
                     }
                  }

                  var36.add(var63.getRoomID());
                  drawCells(var63, new Color(255, 255, 0), var0, var1);
               }

               StringTooltips var62 = new StringTooltips();
               var62.add("House room IDs: " + Arrays.toString(var36.toArray()));
               var62.add("House doors: " + var37 + ", leading out: " + var12);
               Stream var10000 = var36.stream();
               Objects.requireNonNull(var0);
               var16 = var10000.mapToInt(var0::getRoomSize).sum();
               var62.add("House size: " + var16 + " (" + var45 + ")");
               Screen.addTooltip(var62, TooltipLocation.BOTTOM_LEFT);
         }

         InventoryItem var38 = var2.getSelectedItem();
         Rectangle var52;
         MultiTile var65;
         if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            ControllerInteractTarget var47 = var2.getControllerInteractTarget(false, var2.getCurrentAttackHeight(), var1);
            if (var47 != null) {
               DrawOptions var60 = var47.getDrawOptions();
               if (var60 != null) {
                  var60.draw();
               }

               var47.onCurrentlyFocused();
            }

            AttackHandler var64 = var2.getAttackHandler();
            if (var64 != null) {
               var64.drawControllerAimPos(var1, var0, var2, var38);
            } else if (var38 != null) {
               var38.item.drawControllerAimPos(var1, var0, var2, var38);
            }
         } else {
            NetworkClient var42;
            PlayerMob var43;
            Iterator var44;
            Shape var53;
            Mob var54;
            PickupEntity var57;
            Projectile var61;
            if (!debugActive) {
               var42 = (NetworkClient)streamClients(var0).filter((var2x) -> {
                  return var2x != null && var2x.playerMob != null && var2x.hasSpawned() && !var2x.isDead() && var2x.isSamePlace(var0) && var2x.playerMob != var2;
               }).filter((var2x) -> {
                  return var2x.playerMob.getSelectBox().contains(var5, var6);
               }).findFirst().orElse((Object)null);
               if (var42 != null) {
                  var43 = var42.playerMob;
                  if (var43.onMouseHover(var1, var2, false) | (var38 != null && var38.item.onMouseHoverMob(var38, var1, var2, var43, false))) {
                     if (debugShow == HUD.DebugShow.PATHS) {
                        synchronized(paths) {
                           paths.stream().filter((var1x) -> {
                              return ((TilePathfinding)var1x.path.finder).mob.getUniqueID() == var43.getUniqueID();
                           }).findFirst().ifPresent((var1x) -> {
                              TilePathfinding.drawPathProcess(var1x.path, var1);
                           });
                        }
                     }

                     return;
                  }
               }

               var44 = var0.entityManager.mobs.getInRegionRangeByTile(var7, var8, 1).iterator();

               label416:
               while(true) {
                  do {
                     if (!var44.hasNext()) {
                        var44 = var0.entityManager.pickups.getInRegionRangeByTile(var7, var8, 1).iterator();

                        do {
                           do {
                              do {
                                 if (!var44.hasNext()) {
                                    var44 = var0.entityManager.projectiles.getInRegionRangeByTile(var7, var8, 1).iterator();

                                    while(var44.hasNext()) {
                                       var61 = (Projectile)var44.next();
                                       var53 = var61.getSelectBox();
                                       if (var53.contains((double)var5, (double)var6) && var61.onMouseHover(var1, var2, false)) {
                                          return;
                                       }
                                    }

                                    LevelObject var51 = GameUtils.getInteractObjectHit(var0, var5, var6, (var1x) -> {
                                       return var1x.canInteract(var2);
                                    }, (LevelObject)null);
                                    if (var51 != null) {
                                       Screen.setCursor(Screen.CURSOR.INTERACT);
                                       var51.onMouseHover(var1, var2, false);
                                       if (Settings.showControlTips) {
                                          String var66 = var51.getInteractTip(var2, false);
                                          if (var66 != null) {
                                             Screen.addTooltip(new InputTooltip(Control.MOUSE2, var66, var51.inInteractRange(var2) ? 1.0F : 0.7F), TooltipLocation.INTERACT_FOCUS);
                                          }
                                       }
                                    }

                                    LevelObject var70 = GameUtils.getInteractObjectHit(var0, var5, var6, (Predicate)null);
                                    if (var51 == null || var51.tileX != var70.tileX || var51.tileY != var70.tileY) {
                                       var70.onMouseHover(var1, var2, false);
                                    }

                                    if (var0.drawUtils.drawWire(var2) && var0.logicLayer.hasGate(var7, var8)) {
                                       var0.logicLayer.getLogicGate(var7, var8).onMouseHover(var0, var7, var8, var2, false);
                                    }

                                    if (var38 != null) {
                                       var38.item.onMouseHoverTile(var38, var1, var2, var5, var6, new TilePosition(var0, var7, var8), false);
                                    }
                                    break label416;
                                 }

                                 var57 = (PickupEntity)var44.next();
                              } while(!var57.shouldDraw());
                           } while(!var57.getSelectBox().contains(var5, var6));
                        } while(!(var57.onMouseHover(var1, var2, false) | (var38 != null && var38.item.onMouseHoverPickup(var38, var1, var2, var57, false))));

                        return;
                     }

                     var54 = (Mob)var44.next();
                  } while(!var54.getSelectBox().contains(var5, var6));

                  if (var54.onMouseHover(var1, var2, false) | (var38 != null && var38.item.onMouseHoverMob(var38, var1, var2, var54, false))) {
                     if (debugShow == HUD.DebugShow.PATHS) {
                        synchronized(paths) {
                           paths.stream().filter((var1x) -> {
                              return ((TilePathfinding)var1x.path.finder).mob.getUniqueID() == var54.getUniqueID();
                           }).findFirst().ifPresent((var1x) -> {
                              TilePathfinding.drawPathProcess(var1x.path, var1);
                           });
                        }
                     }

                     return;
                  }
               }
            } else {
               var42 = (NetworkClient)streamClients(var0).filter((var1x) -> {
                  return var1x != null && var1x.playerMob != null && var1x.hasSpawned() && !var1x.isDead() && var1x.isSamePlace(var0);
               }).filter((var2x) -> {
                  return var2x.playerMob.getSelectBox().contains(var5, var6);
               }).findFirst().orElse((Object)null);
               Rectangle var55;
               if (var42 != null) {
                  var43 = var42.playerMob;
                  if (var43.onMouseHover(var1, var2, true) | (var38 != null && var38.item.onMouseHoverMob(var38, var1, var2, var43, true))) {
                     Rectangle var79 = var43.getCollision();
                     var55 = var43.getHitBox();
                     Screen.initQuadDraw(var79.width, var79.height).color(1.0F, 0.0F, 0.0F, 0.3F).draw(var1.getDrawX(var79.x), var1.getDrawY(var79.y));
                     Screen.initQuadDraw(var55.width, var55.height).color(0.0F, 0.0F, 1.0F, 0.3F).draw(var1.getDrawX(var55.x), var1.getDrawY(var55.y));
                     levelBoundOptions(var1, var43.getSelectBox()).draw();
                     if (debugShow == HUD.DebugShow.PATHS) {
                        synchronized(paths) {
                           paths.stream().filter((var1x) -> {
                              return ((TilePathfinding)var1x.path.finder).mob.getUniqueID() == var43.getUniqueID();
                           }).findFirst().ifPresent((var1x) -> {
                              TilePathfinding.drawPathProcess(var1x.path, var1);
                           });
                        }
                     }

                     return;
                  }
               }

               var44 = var0.entityManager.mobs.getInRegionRangeByTile(var7, var8, 1).iterator();

               label513:
               while(true) {
                  do {
                     if (!var44.hasNext()) {
                        var44 = var0.entityManager.pickups.getInRegionRangeByTile(var7, var8, 1).iterator();

                        while(var44.hasNext()) {
                           var57 = (PickupEntity)var44.next();
                           if (var57.shouldDraw() && var57.getSelectBox().contains(var5, var6)) {
                              var55 = var57.getCollision();
                              Screen.initQuadDraw(var55.width, var55.height).color(0.0F, 0.0F, 1.0F, 0.3F).draw(var1.getDrawX(var55.x), var1.getDrawY(var55.y));
                              levelBoundOptions(var1, var57.getSelectBox()).draw();
                              var57.onMouseHover(var1, var2, true);
                              if (var38 != null) {
                                 var38.item.onMouseHoverPickup(var38, var1, var2, var57, true);
                              }

                              return;
                           }
                        }

                        var44 = var0.entityManager.projectiles.getInRegionRangeByTile(var7, var8, 1).iterator();

                        while(var44.hasNext()) {
                           var61 = (Projectile)var44.next();
                           var53 = var61.getSelectBox();
                           if (var53.contains((double)var5, (double)var6)) {
                              Screen.drawShape(var53, var1, true, 1.0F, 0.0F, 0.0F, 0.3F);
                              float var56 = Math.max(var61.getHitLength(), 16.0F);
                              Line2D.Float var69 = new Line2D.Float(var61.x, var61.y, var61.x + var61.dx * var56, var61.y + var61.dy * var56);
                              Screen.drawShape(var61.toHitbox(var69), var1, true, 0.0F, 0.0F, 1.0F, 0.3F);
                              var61.onMouseHover(var1, var2, true);
                              return;
                           }
                        }

                        var37 = var1.getTileDrawX(var7);
                        var12 = var1.getTileDrawY(var8);
                        if (!Screen.isKeyDown(340)) {
                           var65 = var0.getObject(var7, var8).getMultiTile(var0, var7, var8);
                           tileBoundOptions(var1, var65.getTileRectangle(var7, var8)).draw();
                           List var58 = var0.getObject(var7, var8).getCollisions(var0, var7, var8, var0.getObjectRotation(var7, var8));
                           var59 = var58.iterator();

                           while(var59.hasNext()) {
                              Rectangle var72 = (Rectangle)var59.next();
                              Screen.initQuadDraw(var72.width, var72.height).color(1.0F, 0.0F, 0.0F, 0.5F).draw(var1.getDrawX(var72.x), var1.getDrawY(var72.y));
                           }

                           List var73 = var0.getObject(var7, var8).getHoverHitboxes(var0, var7, var8);
                           Iterator var74 = var73.iterator();

                           while(var74.hasNext()) {
                              Rectangle var75 = (Rectangle)var74.next();
                              Screen.initQuadDraw(var75.width, var75.height).color(0.0F, 0.0F, 1.0F, 0.5F).draw(var1.getDrawX(var75.x), var1.getDrawY(var75.y));
                           }

                           StringTooltips var76 = new StringTooltips();
                           var76.add("x: " + var7 + ", y: " + var8);
                           var76.add("Tile: " + var0.getTileName(var7, var8).translate() + " (" + var0.getTileID(var7, var8) + ")");
                           String var77 = var0.getObjectName(var7, var8).translate() + " (" + var0.getObjectID(var7, var8) + "), " + var0.getObjectRotation(var7, var8);
                           var76.add("Object: " + var77);
                           if (var0.logicLayer.hasGate(var7, var8)) {
                              GameLogicGate var80 = var0.logicLayer.getLogicGate(var7, var8);
                              var76.add("Logic gate: " + var80.getDisplayName() + " (" + var80.getID() + ")");
                           }

                           StringBuilder var82 = new StringBuilder();
                           byte var84 = var0.wireManager.getWireData(var7, var8);

                           for(int var85 = 0; var85 < 8; ++var85) {
                              var82.append(var84 >> var85 & 1);
                           }

                           var76.add("Wire: " + var82);
                           var76.add("Light: " + var0.lightManager.getLightLevel(var7, var8));
                           var76.add("Static light: " + var0.lightManager.getStaticLight(var7, var8));
                           var76.add("Particle light: " + var0.lightManager.getParticleLight(var7, var8));
                           List var86 = var0.lightManager.getStaticLightSources(var7, var8);
                           if (!var86.isEmpty()) {
                              var76.add("Light sources:");
                              Iterator var21 = var86.iterator();

                              while(var21.hasNext()) {
                                 SourcedGameLight var22 = (SourcedGameLight)var21.next();
                                 var76.add("  " + var22);
                              }
                           }

                           var76.add("Height: " + var0.liquidManager.getHeight(var7, var8));
                           var76.add("RoomID: " + var0.getRoomID(var7, var8) + ", size: " + var0.getRoomSize(var7, var8));
                           var76.add("RegionID: " + var0.getRegionID(var7, var8));
                           var76.add("Outside: " + var0.isOutside(var7, var8));
                           ObjectEntity var87 = var0.entityManager.getObjectEntity(var7, var8);
                           if (var87 != null) {
                              var76.add("Entity: " + var87.type);
                           }

                           LevelData var88 = var0.getLevelData("jobs");
                           if (var88 instanceof JobsLevelData) {
                              List var23 = (List)((JobsLevelData)var88).streamJobsInTile(var7, var8).collect(Collectors.toList());
                              if (!var23.isEmpty()) {
                                 var76.add("Jobs:");
                                 Iterator var24 = var23.iterator();

                                 while(var24.hasNext()) {
                                    LevelJob var25 = (LevelJob)var24.next();
                                    var76.add("\t" + var25);
                                 }
                              }
                           }

                           var0.splattingManager.addDebugTooltips(var7, var8, var76);
                           Screen.addTooltip(var76, TooltipLocation.BOTTOM_LEFT);
                           LevelObject var89 = GameUtils.getInteractObjectHit(var0, var5, var6, (var1x) -> {
                              return var1x.canInteract(var2);
                           }, (LevelObject)null);
                           if (var89 != null) {
                              Screen.setCursor(Screen.CURSOR.INTERACT);
                              if (Settings.showControlTips) {
                                 String var90 = var89.getInteractTip(var2, true);
                                 if (var90 != null) {
                                    Screen.addTooltip(new InputTooltip(Control.MOUSE2, var90, var89.inInteractRange(var2) ? 1.0F : 0.7F), TooltipLocation.INTERACT_FOCUS);
                                 }
                              }
                           }

                           LevelObject var91 = GameUtils.getInteractObjectHit(var0, var5, var6, (Predicate)null);
                           var91.onMouseHover(var1, var2, true);
                           if (var0.drawUtils.drawWire(var2) && var0.logicLayer.hasGate(var7, var8)) {
                              var0.logicLayer.getLogicGate(var7, var8).onMouseHover(var0, var7, var8, var2, true);
                           }

                           if (var38 != null) {
                              var38.item.onMouseHoverTile(var38, var1, var2, var5, var6, new TilePosition(var0, var7, var8), true);
                           }
                           break label513;
                        } else {
                           tileBoundOptions(var1, var7, var8, var7, var8).draw();
                           FontOptions var68 = new FontOptions(12);
                           var14 = var7 + 1;

                           while(true) {
                              var15 = var1.getTileDrawX(var14);
                              if (var15 > Screen.getSceneWidth()) {
                                 var14 = var7 - 1;

                                 while(true) {
                                    var15 = var1.getTileDrawX(var14);
                                    if (var15 + 32 < 0) {
                                       var15 = var8 + 1;

                                       while(true) {
                                          var16 = var1.getTileDrawY(var15);
                                          if (var16 > Screen.getSceneHeight()) {
                                             var15 = var8 - 1;

                                             while(true) {
                                                var16 = var1.getTileDrawY(var15);
                                                if (var16 + 32 < 0) {
                                                   break label513;
                                                }

                                                if (var15 % 2 == 0) {
                                                   Screen.initQuadDraw(32, 32).color(0.0F, 0.0F, 1.0F, 0.2F).draw(var37, var16);
                                                } else {
                                                   Screen.initQuadDraw(32, 32).color(0.0F, 1.0F, 0.0F, 0.2F).draw(var37, var16);
                                                }

                                                --var15;
                                                FontManager.bit.drawString((float)var37, (float)var16, "" + Math.abs(var15 - var8 + 1), var68);
                                             }
                                          }

                                          if (var15 % 2 == 0) {
                                             Screen.initQuadDraw(32, 32).color(0.0F, 0.0F, 1.0F, 0.2F).draw(var37, var16);
                                          } else {
                                             Screen.initQuadDraw(32, 32).color(0.0F, 1.0F, 0.0F, 0.2F).draw(var37, var16);
                                          }

                                          ++var15;
                                          FontManager.bit.drawString((float)var37, (float)var16, "" + Math.abs(var15 - var8 - 1), var68);
                                       }
                                    }

                                    if (var14 % 2 == 0) {
                                       Screen.initQuadDraw(32, 32).color(0.0F, 0.0F, 1.0F, 0.2F).draw(var15, var12);
                                    } else {
                                       Screen.initQuadDraw(32, 32).color(0.0F, 1.0F, 0.0F, 0.2F).draw(var15, var12);
                                    }

                                    --var14;
                                    FontManager.bit.drawString((float)var15, (float)var12, "" + Math.abs(var14 - var7 + 1), var68);
                                 }
                              }

                              if (var14 % 2 == 0) {
                                 Screen.initQuadDraw(32, 32).color(0.0F, 0.0F, 1.0F, 0.2F).draw(var15, var12);
                              } else {
                                 Screen.initQuadDraw(32, 32).color(0.0F, 1.0F, 0.0F, 0.2F).draw(var15, var12);
                              }

                              ++var14;
                              FontManager.bit.drawString((float)var15, (float)var12, "" + Math.abs(var14 - var7 - 1), var68);
                           }
                        }
                     }

                     var54 = (Mob)var44.next();
                  } while(!var54.getSelectBox().contains(var5, var6));

                  if (var54.onMouseHover(var1, var2, true) | (var38 != null && var38.item.onMouseHoverMob(var38, var1, var2, var54, true))) {
                     var55 = var54.getCollision();
                     var52 = var54.getHitBox();
                     Screen.initQuadDraw(var55.width, var55.height).color(1.0F, 0.0F, 0.0F, 0.3F).draw(var1.getDrawX(var55.x), var1.getDrawY(var55.y));
                     Screen.initQuadDraw(var52.width, var52.height).color(0.0F, 0.0F, 1.0F, 0.3F).draw(var1.getDrawX(var52.x), var1.getDrawY(var52.y));
                     levelBoundOptions(var1, var54.getSelectBox()).draw();
                     if (debugShow == HUD.DebugShow.PATHS) {
                        synchronized(paths) {
                           paths.stream().filter((var1x) -> {
                              return ((TilePathfinding)var1x.path.finder).mob.getUniqueID() == var54.getUniqueID();
                           }).findFirst().ifPresent((var1x) -> {
                              TilePathfinding.drawPathProcess(var1x.path, var1);
                           });
                        }
                     }

                     return;
                  }
               }
            }
         }

         if (Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            Point var49;
            if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
               Point2D.Float var67 = var2.getControllerAimDir();
               var49 = new Point((int)(var2.x + var67.x * 100.0F), (int)(var2.y + var67.y * 100.0F));
            } else {
               var49 = new Point(var1.getMouseLevelPosX(), var1.getMouseLevelPosY());
            }

            SmartMineTarget var71 = getFirstSmartHitTile(var0, var2, var38, var49.x, var49.y);
            if (var71 != null) {
               if (var71.isObject) {
                  GameObject var81 = var0.getObject(var71.x, var71.y);
                  var65 = var81.getMultiTile(var0, var71.x, var71.y);
                  var52 = var65.getTileRectangle(var71.x, var71.y);
                  tileBoundOptions(var1, new Color(255, 255, 255), true, var52).draw();
               } else {
                  tileBoundOptions(var1, new Color(255, 255, 255), true, var71.x, var71.y, var71.x, var71.y).draw();
               }
            }
         }

      }
   }

   private static Stream<NetworkClient> streamClients(Level var0) {
      if (var0.isClient()) {
         return var0.getClient().streamClients().map((var0x) -> {
            return var0x;
         });
      } else {
         return var0.isServer() ? var0.getServer().streamClients().map((var0x) -> {
            return var0x;
         }) : Stream.empty();
      }
   }

   public static void reset() {
      debugActive = false;
   }

   private static void drawCells(SemiRegion var0, Color var1, Level var2, GameCamera var3) {
      int var4 = 0;
      int var5 = 0;
      if (var3.getX() / 32 > 0) {
         var4 = var3.getX() / 32;
      }

      if (var3.getY() / 32 > 0) {
         var5 = var3.getY() / 32;
      }

      int var6 = var3.getX() / 32 + var3.getWidth() / 32 + 2;
      int var7 = var3.getY() / 32 + var3.getHeight() / 32 + 2;
      if (var6 > var2.width) {
         var6 = var2.width;
      }

      if (var7 > var2.height) {
         var7 = var2.height;
      }

      Iterator var8 = var0.getLevelTiles().iterator();

      while(var8.hasNext()) {
         Point var9 = (Point)var8.next();
         if (var9.x >= var4 && var9.x < var6 && var9.y >= var5 && var9.y < var7) {
            Screen.initQuadDraw(32, 32).color(var1).alpha(0.15F).draw(var3.getTileDrawX(var9.x), var3.getTileDrawY(var9.y));
         }
      }

   }

   public static void submitPath(PathResult<Point, TilePathfinding> var0) {
      if (debugActive || debugShow == HUD.DebugShow.PATHS) {
         synchronized(paths) {
            long var2 = ((TilePathfinding)var0.finder).level.getWorldEntity().getLocalTime();
            paths.removeIf((var1) -> {
               return ((TilePathfinding)var1.path.finder).mob.getUniqueID() == ((TilePathfinding)var0.finder).mob.getUniqueID();
            });
            paths.add(new SubmittedPath(var0, var2));
         }
      }
   }

   public static SmartMineTarget getFirstSmartHitTile(final Level var0, final PlayerMob var1, final InventoryItem var2, int var3, int var4) {
      if (var2 != null && var2.item instanceof ToolDamageItem) {
         final boolean var5 = Input.lastInputIsController && !ControllerInput.isCursorVisible();
         final ToolDamageItem var6 = (ToolDamageItem)var2.item;
         ToolType var7 = var6.getToolType();
         int var9;
         if (var7 == ToolType.ALL || var7 == ToolType.AXE || var7 == ToolType.PICKAXE) {
            if (!var5) {
               LevelObject var8 = GameUtils.getInteractObjectHit(var0, var3, var4, (var4x) -> {
                  if (var4x.object.getID() == 0) {
                     return false;
                  } else if (!var6.isTileInRange(var0, var4x.tileX, var4x.tileY, var1, var2)) {
                     return false;
                  } else {
                     return var6.canDamageTile(var0, var4x.tileX, var4x.tileY, var1, var2);
                  }
               }, (LevelObject)null);
               if (var8 != null) {
                  return new SmartMineTarget(var0, var8.tileX, var8.tileY, true, var6);
               }
            }

            Point2D.Float var23 = GameMath.normalize((float)var3 - var1.x, (float)var4 - var1.y);
            var9 = var6.getMiningRange(var2, var1) + 32;
            Line2D.Float var10 = new Line2D.Float(var1.x + var23.x * 16.0F, var1.y + var23.y * 16.0F, var1.x + var23.x * (float)var9, var1.y + var23.y * (float)var9);
            LineHitbox var11 = new LineHitbox(var10, 14.0F);
            LineHitbox var12 = new LineHitbox(var10, 100.0F);
            Rectangle var13 = var12.getBounds();
            int var14 = var13.x / 32 - 1;
            int var15 = var13.y / 32 - 1;
            int var16 = (var13.x + var13.width) / 32 + 1;
            int var17 = (var13.y + var13.height) / 32 + 1;
            if (var14 < 0) {
               var14 = 0;
            }

            if (var15 < 0) {
               var15 = 0;
            }

            if (var16 > var0.width) {
               var16 = var0.width;
            }

            if (var17 > var0.height) {
               var17 = var0.height;
            }

            PriorityMap var18 = new PriorityMap();
            int var19 = var14;

            while(true) {
               if (var19 > var16) {
                  Point var30 = (Point)var18.getBestObjectsList().stream().min(Comparator.comparing((var1x) -> {
                     return var1.getDistance((float)(var1x.x * 32 + 16), (float)(var1x.y * 32 + 16));
                  })).orElse((Object)null);
                  if (var30 != null) {
                     return new SmartMineTarget(var0, var30, true, var6);
                  }
                  break;
               }

               for(int var20 = var15; var20 <= var17; ++var20) {
                  if (var0.getObjectID(var19, var20) != 0) {
                     GameObject var21 = var0.getObject(var19, var20);
                     if (var7.canDealDamageTo(var21.toolType) && var6.isTileInRange(var0, var19, var20, var1, var2) && (var6.canSmartMineTile(var0, var19, var20, var1, var2) || var5 && var21.shouldSnapControllerMining(var0, var19, var20))) {
                        Rectangle var22 = new Rectangle(var19 * 32, var20 * 32, 32, 32);
                        if (var11.intersects(var22)) {
                           var18.addIfHasNoBetter(100, new Point(var19, var20));
                        } else if (!var18.hasBetter(0) && var12.intersects(var22)) {
                           var18.addIfHasNoBetter(0, new Point(var19, var20));
                        }
                     }
                  }
               }

               ++var19;
            }
         }

         if (var7 == ToolType.ALL || var7 == ToolType.SHOVEL) {
            int var24 = var3 / 32;
            var9 = var4 / 32;
            if (!var5 && var0.getTile(var24, var9).canBeMined && var6.canDamageTile(var0, var24, var9, var1, var2) && var6.isTileInRange(var0, var24, var9, var1, var2)) {
               return new SmartMineTarget(var0, var24, var9, false, var6);
            }

            int var25 = var6.getMiningRange(var2, var1);
            final int var26 = var0.getRegionID(var1.getTileX(), var1.getTileY());
            final PriorityMap var27 = new PriorityMap();
            AreaFinder var28 = new AreaFinder(var1.getTileX(), var1.getTileY(), (var25 + 32) / 32, true) {
               public boolean checkPoint(int var1x, int var2x) {
                  GameTile var3 = var0.getTile(var1x, var2x);
                  if (var3.canBeMined && var6.isTileInRange(var0, var1x, var2x, var1, var2) && (var6.canSmartMineTile(var0, var1x, var2x, var1, var2) || var5) && var6.canDamageTile(var0, var1x, var2x, var1, var2)) {
                     if (var0.getRegionID(var1x, var2x) == var26) {
                        if (var3.smartMinePriority) {
                           var27.addIfHasNoBetter(1000, new Point(var1x, var2x));
                        } else {
                           var27.addIfHasNoBetter(900, new Point(var1x, var2x));
                        }
                     } else if (var3.smartMinePriority) {
                        var27.addIfHasNoBetter(100, new Point(var1x, var2x));
                     } else {
                        var27.addIfHasNoBetter(0, new Point(var1x, var2x));
                     }
                  }

                  return false;
               }
            };
            var28.tickFinder(var28.getMaxTicks());
            Point var29 = (Point)var27.getBestObjectsList().stream().min(Comparator.comparing((var2x) -> {
               return (new Point(var3, var4)).distance((double)(var2x.x * 32 + 16), (double)(var2x.y * 32 + 16));
            })).orElse((Object)null);
            if (var29 != null) {
               return new SmartMineTarget(var0, var29, false, var6);
            }
         }
      }

      return null;
   }

   public static DrawOptions selectBoundOptions(Color var0, boolean var1, int var2, int var3, int var4, int var5) {
      DrawOptionsList var6 = new DrawOptionsList();
      GameTexture var7 = var1 ? Settings.UI.select_outline : Settings.UI.select;
      var6.add(var7.initDraw().sprite(0, 0, 16).color(var0).pos(var2, var3));
      var6.add(var7.initDraw().sprite(1, 0, 16).color(var0).pos(var4 - 16, var3));
      var6.add(var7.initDraw().sprite(0, 1, 16).color(var0).pos(var2, var5 - 16));
      var6.add(var7.initDraw().sprite(1, 1, 16).color(var0).pos(var4 - 16, var5 - 16));
      return var6;
   }

   public static DrawOptions selectBoundOptions(Color var0, boolean var1, Rectangle var2) {
      return selectBoundOptions(var0, var1, var2.x, var2.y, var2.x + var2.width - 1, var2.y + var2.height - 1);
   }

   public static DrawOptions levelBoundOptions(GameCamera var0, Color var1, boolean var2, int var3, int var4, int var5, int var6) {
      return selectBoundOptions(var1, var2, var0.getDrawX(var3), var0.getDrawY(var4), var0.getDrawX(var5), var0.getDrawY(var6));
   }

   public static DrawOptions levelBoundOptions(GameCamera var0, int var1, int var2, int var3, int var4) {
      return levelBoundOptions(var0, Color.WHITE, false, var1, var2, var3, var4);
   }

   public static DrawOptions levelBoundOptions(GameCamera var0, Color var1, boolean var2, Rectangle var3) {
      return levelBoundOptions(var0, var1, var2, var3.x, var3.y, var3.x + var3.width - 1, var3.y + var3.height - 1);
   }

   public static DrawOptions levelBoundOptions(GameCamera var0, Rectangle var1) {
      return levelBoundOptions(var0, Color.WHITE, false, var1);
   }

   public static DrawOptions tileBoundOptions(GameCamera var0, Color var1, boolean var2, int var3, int var4, int var5, int var6) {
      DrawOptionsList var7 = new DrawOptionsList();
      GameTexture var8 = var2 ? Settings.UI.select_outline : Settings.UI.select;
      var7.add(var8.initDraw().sprite(0, 0, 16).color(var1).pos(var0.getTileDrawX(var3), var0.getTileDrawY(var4)));
      var7.add(var8.initDraw().sprite(1, 0, 16).color(var1).pos(var0.getTileDrawX(var5) + 16, var0.getTileDrawY(var4)));
      var7.add(var8.initDraw().sprite(0, 1, 16).color(var1).pos(var0.getTileDrawX(var3), var0.getTileDrawY(var6) + 16));
      var7.add(var8.initDraw().sprite(1, 1, 16).color(var1).pos(var0.getTileDrawX(var5) + 16, var0.getTileDrawY(var6) + 16));
      return var7;
   }

   public static DrawOptions tileBoundOptions(GameCamera var0, int var1, int var2, int var3, int var4) {
      return tileBoundOptions(var0, Color.WHITE, false, var1, var2, var3, var4);
   }

   public static DrawOptions tileBoundOptions(GameCamera var0, Color var1, boolean var2, Rectangle var3) {
      return tileBoundOptions(var0, var1, var2, var3.x, var3.y, var3.x + var3.width - 1, var3.y + var3.height - 1);
   }

   public static DrawOptions tileBoundOptions(GameCamera var0, Rectangle var1) {
      return tileBoundOptions(var0, Color.WHITE, false, var1);
   }

   public static DrawOptionsBox getDirectionIndicator(float var0, float var1, float var2, float var3, String var4, FontOptions var5, GameCamera var6) {
      Rectangle var7 = new Rectangle(var6.getX() + 25, var6.getY() + 25, var6.getWidth() - 50, var6.getHeight() - 50);
      int var8 = FontManager.bit.getWidthCeil(var4, var5);
      if (!var7.contains((double)var2, (double)var3)) {
         Point2D.Float var9 = GameMath.normalize(var2 - var0, var3 - var1);
         int var10 = var6.getWidth() / 2 + (int)(var9.x * (float)var6.getWidth() / 3.0F);
         int var11 = var6.getHeight() / 2 + (int)(var9.y * (float)var6.getHeight() / 3.0F);
         String var12 = "(" + (int)GameMath.pixelsToMeters((float)(new Point2D.Float(var0, var1)).distance((double)var2, (double)var3)) + "m)";
         int var13 = FontManager.bit.getWidthCeil(var12, var5);
         int var14 = Math.max(var8, var13);
         final Rectangle var15 = new Rectangle(var10 - var14 / 2, var11, var14 - 16, 36);
         final DrawOptionsList var16 = new DrawOptionsList();
         var16.add((new StringDrawOptions(var5, var4)).pos(var10 - var8 / 2, var15.y));
         var16.add((new StringDrawOptions(var5, var12)).pos(var10 - var13 / 2, var15.y + 16));
         return new DrawOptionsBox() {
            public void draw() {
               var16.draw();
            }

            public Rectangle getBoundingBox() {
               return var15;
            }
         };
      } else {
         return null;
      }
   }

   public static SharedTextureDrawOptions getOutlinesDrawOptions(GameTexture var0, int var1, int var2, int var3, int var4, int var5) {
      SharedTextureDrawOptions var6 = new SharedTextureDrawOptions(var0);
      addOutlines(var6, var1, var2, var3, var4, var5);
      return var6;
   }

   public static SharedTextureDrawOptions getCenterDrawOptions(GameTexture var0, int var1, int var2, int var3, int var4, int var5) {
      SharedTextureDrawOptions var6 = new SharedTextureDrawOptions(var0);
      addCenter(var6, var0, var1, var2, var3, var4, var5);
      return var6;
   }

   public static SharedTextureDrawOptions getBackgroundDrawOptions(GameTexture var0, int var1, int var2, int var3, int var4, int var5) {
      SharedTextureDrawOptions var6 = new SharedTextureDrawOptions(var0);
      addOutlines(var6, var1, var2, var3, var4, var5);
      addCenter(var6, var0, var1, var2, var3, var4, var5);
      return var6;
   }

   protected static void addOutlines(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = Math.min(var1, var4 / 2);
      int var7 = Math.min(var1, var4 - var6);
      int var8 = Math.min(var1, var5 / 2);
      int var9 = Math.min(var1, var5 - var8);
      int var10 = var4 - var1 * 2;
      int var11 = var5 - var1 * 2;
      var0.addSpriteSection(1, 1, var1, var1 - var6, var1, var1 - var8, var1).pos(var2, var3);
      var0.addSpriteSection(1, 0, var1, var1 - var6, var1, 0, var9).pos(var2, var3 + var5 - var9);
      var0.addSpriteSection(0, 1, var1, 0, var7, var1 - var8, var1).pos(var2 + var4 - var7, var3);
      var0.addSpriteSection(0, 0, var1, 0, var7, 0, var9).pos(var2 + var4 - var7, var3 + var5 - var9);
      int var12;
      int var13;
      if (var10 > 0) {
         for(var12 = 0; var12 < var10; var12 += var1) {
            var13 = Math.min(var1, var10 - var12);
            var0.addSpriteSection(1, 1, var1 * 2, var1, 0, var13, var1 - var8, var1).pos(var2 + var1 + var12, var3);
            var0.addSpriteSection(1, 0, var1 * 2, var1, 0, var13, 0, var9).pos(var2 + var1 + var12, var3 + var5 - var9);
         }
      }

      if (var11 > 0) {
         for(var12 = 0; var12 < var11; var12 += var1) {
            var13 = Math.min(var1, var11 - var12);
            var0.addSpriteSection(1, 1, var1, var1 * 2, var1 - var6, var1, 0, var13).pos(var2, var3 + var1 + var12);
            var0.addSpriteSection(0, 1, var1, var1 * 2, 0, var7, 0, var13).pos(var2 + var4 - var7, var3 + var1 + var12);
         }
      }

   }

   protected static void addCenter(SharedTextureDrawOptions var0, GameTexture var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = var5 - var2 * 2;
      int var8 = var6 - var2 * 2;
      byte var9 = 0;
      int var10 = var2 * 4;
      int var11 = var1.getWidth() - var9;
      int var12 = var1.getHeight() - var10;
      if (var7 > 0 && var8 > 0 && var11 > 0 && var12 > 0) {
         for(int var13 = 0; var13 < var7; var13 += var11) {
            int var14 = Math.min(var11, var7 - var13);

            for(int var15 = 0; var15 < var8; var15 += var12) {
               int var16 = Math.min(var12, var8 - var15);
               var0.addSection(var9, var9 + var14, var10, var10 + var16).pos(var3 + var2 + var13, var4 + var2 + var15);
            }
         }
      }

   }

   public static boolean[][] toBooleans(int[][] var0) {
      boolean[][] var1 = new boolean[var0.length][0];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         int[] var3 = var0[var2];
         boolean[] var4 = new boolean[var3.length];

         for(int var5 = 0; var5 < var3.length; ++var5) {
            var4[var5] = var3[var5] > 0;
         }

         var1[var2] = var4;
      }

      return var1;
   }

   public static SharedTextureDrawOptions getBackgroundEdged(GameTexture var0, int var1, int var2, int var3, int var4, int var5, Set<Point> var6, int var7, int var8) {
      SharedTextureDrawOptions var9 = new SharedTextureDrawOptions(var0);
      addBackgroundBoxed(var9, var1, var2, var3, var4, var5, var6, var7, var8);
      return var9;
   }

   public static void addBackgroundBoxed(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, Set<Point> var6, int var7, int var8) {
      addBackgroundBoxed(var0, var1, var3, 0, 0, var2, var4 - var2 / 2, var5 - var3 / 2, (Set)var6, var7, var8);
   }

   public static SharedTextureDrawOptions getBackgroundEdged(GameTexture var0, int var1, int var2, int var3, int var4, int var5, boolean[][] var6, int var7, int var8) {
      SharedTextureDrawOptions var9 = new SharedTextureDrawOptions(var0);
      addBackgroundBoxed(var9, var1, var2, var3, var4, var5, var6, var7, var8);
      return var9;
   }

   public static void addBackgroundBoxed(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, boolean[][] var6, int var7, int var8) {
      addBackgroundBoxed(var0, var1, var3, 0, 0, var2, var4 - var2 / 2, var5 - var3 / 2, (boolean[][])var6, var7, var8);
   }

   protected static SharedTextureDrawOptions getBackgroundEdged(GameTexture var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, Set<Point> var8, int var9, int var10) {
      SharedTextureDrawOptions var11 = new SharedTextureDrawOptions(var0);
      addBackgroundBoxed(var11, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      return var11;
   }

   protected static SharedTextureDrawOptions getBackgroundEdged(GameTexture var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean[][] var8, int var9, int var10) {
      SharedTextureDrawOptions var11 = new SharedTextureDrawOptions(var0);
      addBackgroundBoxed(var11, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      return var11;
   }

   protected static void addBackgroundBoxed(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, Set<Point> var8, int var9, int var10) {
      addBackgroundBoxed(var0, var1, var2, var3, var4, var5, var6, var7, var8, (Function)((var1x) -> {
         boolean[] var2 = new boolean[Level.adjacentGetters.length];

         for(int var3 = 0; var3 < Level.adjacentGetters.length; ++var3) {
            Point var4 = Level.adjacentGetters[var3];
            var2[var3] = var8.contains(new Point(var1x.x + var4.x, var1x.y + var4.y));
         }

         return var2;
      }), var9, var10);
   }

   protected static void addBackgroundBoxed(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean[][] var8, int var9, int var10) {
      Iterable var11 = () -> {
         Stream.Builder var1 = Stream.builder();

         for(int var2 = 0; var2 < var8.length; ++var2) {
            for(int var3 = 0; var3 < var8[var2].length; ++var3) {
               if (var8[var2][var3]) {
                  var1.accept(new Point(var3, var2));
               }
            }
         }

         return var1.build().iterator();
      };
      addBackgroundBoxed(var0, var1, var2, var3, var4, var5, var6, var7, var11, (var1x) -> {
         return getAdjacent(var8, var1x.x, var1x.y);
      }, var9, var10);
   }

   protected static void addBackgroundBoxed(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, Iterable<Point> var8, Predicate<Point> var9, int var10, int var11) {
      addBackgroundBoxed(var0, var1, var2, var3, var4, var5, var6, var7, var8, (var1x) -> {
         boolean[] var2 = new boolean[Level.adjacentGetters.length];

         for(int var3 = 0; var3 < Level.adjacentGetters.length; ++var3) {
            Point var4 = Level.adjacentGetters[var3];
            var2[var3] = var9.test(new Point(var1x.x + var4.x, var1x.y + var4.y));
         }

         return var2;
      }, var10, var11);
   }

   protected static void addBackgroundBoxed(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, Iterable<Point> var8, Function<Point, boolean[]> var9, int var10, int var11) {
      Iterator var12 = var8.iterator();

      while(var12.hasNext()) {
         Point var13 = (Point)var12.next();
         boolean[] var14 = (boolean[])var9.apply(var13);
         addBackgroundBoxTile(var0, var1, var2, var3, var4, var5, var6 + var13.x * var10, var7 + var13.y * var11, var14, var10, var11);
      }

   }

   public static void addBackgroundBoxTile(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, boolean[] var6, int var7, int var8) {
      addBackgroundBoxTile(var0, var1, var3, 0, 0, var2, var4 - var2 / 2, var5 - var3 / 2, var6, var7, var8);
   }

   protected static void addBackgroundBoxTile(SharedTextureDrawOptions var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean[] var8, int var9, int var10) {
      if (var9 < var1) {
         throw new IllegalArgumentException("boxWidth cannot be smaller than spriteRes");
      } else if (var10 < var1) {
         throw new IllegalArgumentException("boxHeight cannot be smaller than spriteRes");
      } else {
         GameTextureSection var11 = new GameTextureSection(var0.texture);
         int var12 = var9 / 2;
         int var13 = var10 / 2;
         int var14 = var9 - var12;
         int var15 = var10 - var13;
         int var16 = var6;
         int var17 = var7;
         int var18 = var9;
         int var19 = var10;
         if (!var8[1]) {
            var19 = var10 - var2;
            var17 = var7 + var2;
         }

         if (!var8[3]) {
            var18 = var9 - var5;
            var16 = var6 + var5;
         }

         if (!var8[4]) {
            var18 -= var3;
         }

         if (!var8[6]) {
            var19 -= var4;
         }

         BoxEdge var20 = getBoxTopLeft(var11, var1, var8, var9, var10, var2, var3, var4, var5);
         int var22;
         if (var20 != null) {
            int var21 = var16 - var1 + var20.xOffset;
            var22 = var17 - var1 + var20.yOffset;
            if (var20.section != null) {
               var0.add(var20.section).pos(var21, var22);
            }

            var20.addExtensionDrawsTopLeft(var0, var21, var22, var1, var12, var13);
         }

         BoxEdge var26 = getBoxTopRight(var11, var1, var8, var9, var10, var2, var3, var4, var5);
         int var23;
         if (var26 != null) {
            var22 = var16 + var9 + var26.xOffset;
            var23 = var17 - var1 + var26.yOffset;
            if (var26.section != null) {
               var0.add(var26.section).pos(var22, var23);
            }

            var26.addExtensionDrawsTopRight(var0, var22, var23, var1, var14, var13);
         }

         BoxEdge var27 = getBoxBotLeft(var11, var1, var8, var9, var10, var2, var3, var4, var5);
         int var24;
         if (var27 != null) {
            var23 = var16 - var1 + var27.xOffset;
            var24 = var17 + var10 + var27.yOffset;
            if (var27.section != null) {
               var0.add(var27.section).pos(var23, var24);
            }

            var27.addExtensionDrawsBotLeft(var0, var23, var24, var1, var12, var15);
         }

         BoxEdge var28 = getBoxBotRight(var11, var1, var8, var9, var10, var2, var3, var4, var5);
         if (var28 != null) {
            var24 = var16 + var9 + var28.xOffset;
            int var25 = var17 + var10 + var28.yOffset;
            if (var28.section != null) {
               var0.add(var28.section).pos(var24, var25);
            }

            var28.addExtensionDrawsBotRight(var0, var24, var25, var1, var14, var15);
         }

         if (!var8[0] && var8[1] && var8[3] && var2 > 0 && var5 > 0) {
            addCenterBoxDraw(var0, var11.sprite(2, 0, var1 * 2), var1 * 2, var16 + var5, var17, var18 - var5, var19);
            addCenterBoxDraw(var0, var11.sprite(2, 0, var1 * 2), var1 * 2, var16, var17 + var2, var9 - (var18 - var5), var19 - var2);
         } else {
            addCenterBoxDraw(var0, var11.sprite(2, 0, var1 * 2), var1 * 2, var16, var17, var18, var19);
         }

      }
   }

   protected static void addCenterBoxDraw(SharedTextureDrawOptions var0, GameTextureSection var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 0; var7 < var5; var7 += var2) {
         int var8 = Math.min(var2, var5 - var7);

         for(int var9 = 0; var9 < var6; var9 += var2) {
            int var10 = Math.min(var2, var6 - var9);
            var0.add(var1.sprite(2, 2, var2).section(0, var8, 0, var10)).pos(var3 + var7, var4 + var9);
         }
      }

   }

   protected static BoxEdge getBoxTopLeft(GameTextureSection var0, int var1, boolean[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      byte var9 = 0;
      byte var10 = 1;
      byte var11 = 3;
      byte var12 = 6;
      byte var13 = 4;
      if (var2[var11]) {
         if (var2[var10]) {
            return var2[var9] ? null : new BoxEdge(var0.sprite(2, 2, var1), var8, var5);
         } else {
            return (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(2, 1, var1), true, 0, 0, 0, var2[var9], false);
         }
      } else {
         return var2[var10] ? (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(1, 2, var1), false, 0, 0, 0, var2[var9], false) : (new BoxEdge(var0.sprite(1, 1, var1), 0, 0)).extend(var0.sprite(2, 1, var1), true, 0, 0, var2[var13] ? 0 : -var8, false, false).extend(var0.sprite(1, 2, var1), false, 0, 0, var2[var12] ? 0 : -var5, false, false);
      }
   }

   protected static BoxEdge getBoxTopRight(GameTextureSection var0, int var1, boolean[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      byte var9 = 2;
      byte var10 = 1;
      byte var11 = 4;
      byte var12 = 3;
      byte var13 = 6;
      if (var2[var11]) {
         if (var2[var10]) {
            return var2[var9] ? null : new BoxEdge(var0.sprite(3, 2, var1), -var6 + (var2[var12] ? 0 : -var8), var5);
         } else {
            return (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(3, 1, var1), true, -var3, 0, (var2[var12] ? 0 : -var8) + (var2[var9] ? var8 : 0), false, var2[var9]);
         }
      } else {
         return var2[var10] ? (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(0, 2, var1), false, var2[var12] ? 0 : -var8, 0, 0, var2[var9], false) : (new BoxEdge(var0.sprite(0, 1, var1), var2[var12] ? 0 : -var8, 0)).extend(var0.sprite(3, 1, var1), true, -var3, 0, 0, false, false).extend(var0.sprite(0, 2, var1), false, 0, 0, !var2[var13] ? -var5 : 0, false, false);
      }
   }

   protected static BoxEdge getBoxBotLeft(GameTextureSection var0, int var1, boolean[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      byte var9 = 5;
      byte var10 = 6;
      byte var11 = 3;
      byte var12 = 1;
      byte var13 = 4;
      if (var2[var11]) {
         if (var2[var10]) {
            return var2[var9] ? null : new BoxEdge(var0.sprite(2, 3, var1), var8, -var7 + (var2[var12] ? 0 : -var5));
         } else {
            return (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(2, 0, var1), true, 0, var2[var12] ? 0 : -var5, 0, var2[var9], false);
         }
      } else {
         return var2[var10] ? (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(1, 3, var1), false, 0, -var4, (var2[var12] ? 0 : -var5) + (var2[var9] ? var5 : 0), false, var2[var9]) : (new BoxEdge(var0.sprite(1, 0, var1), 0, var2[var12] ? 0 : -var5)).extend(var0.sprite(2, 0, var1), true, 0, 0, var2[var13] ? 0 : -var8, false, false).extend(var0.sprite(1, 3, var1), false, 0, -var4, 0, false, false);
      }
   }

   protected static BoxEdge getBoxBotRight(GameTextureSection var0, int var1, boolean[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      byte var9 = 7;
      byte var10 = 6;
      byte var11 = 4;
      byte var12 = 3;
      byte var13 = 1;
      if (var2[var11]) {
         if (var2[var10]) {
            return var2[var9] ? null : new BoxEdge(var0.sprite(3, 3, var1), -var6 + (var2[var12] ? 0 : -var8), -var7 + (var2[var13] ? 0 : -var5));
         } else {
            return (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(3, 0, var1), true, -var3, var2[var13] ? 0 : -var5, (!var2[var12] && !var2[var9] ? -var8 : 0) + (var2[var12] && var2[var9] ? var8 : 0), false, var2[var9]);
         }
      } else {
         return var2[var10] ? (new BoxEdge((GameTextureSection)null, 0, 0)).extend(var0.sprite(0, 3, var1), false, var2[var12] ? 0 : -var8, -var4, (!var2[var9] && !var2[var13] ? -var5 : 0) + (var2[var9] && var2[var13] ? var5 : 0), false, var2[var9]) : (new BoxEdge(var0.sprite(0, 0, var1), var2[var12] ? 0 : -var8, var2[var13] ? 0 : -var5)).extend(var0.sprite(3, 0, var1), true, -var3, 0, 0, false, false).extend(var0.sprite(0, 3, var1), false, 0, -var4, 0, false, false);
      }
   }

   protected static boolean[] getAdjacent(boolean[][] var0, int var1, int var2) {
      boolean[] var3 = new boolean[8];
      if (var2 > 0) {
         if (var1 > 0) {
            var3[0] = var0[var2 - 1][var1 - 1];
         }

         var3[1] = var0[var2 - 1][var1];
         if (var1 < var0[var2 - 1].length - 1) {
            var3[2] = var0[var2 - 1][var1 + 1];
         }
      }

      if (var1 > 0) {
         var3[3] = var0[var2][var1 - 1];
      }

      if (var1 < var0[var2].length - 1) {
         var3[4] = var0[var2][var1 + 1];
      }

      if (var2 < var0.length - 1) {
         if (var1 > 0) {
            var3[5] = var0[var2 + 1][var1 - 1];
         }

         var3[6] = var0[var2 + 1][var1];
         if (var1 < var0[var2 + 1].length - 1) {
            var3[7] = var0[var2 + 1][var1 + 1];
         }
      }

      return var3;
   }

   static {
      debugShow = HUD.DebugShow.NOTHING;
      paths = new LinkedList();
   }

   public static enum DebugShow {
      NOTHING,
      REGIONS,
      CONNECTED_REGIONS,
      ROOMS,
      HOUSE,
      PATHS,
      HEIGHT,
      WATER_TYPE;

      private DebugShow() {
      }

      // $FF: synthetic method
      private static DebugShow[] $values() {
         return new DebugShow[]{NOTHING, REGIONS, CONNECTED_REGIONS, ROOMS, HOUSE, PATHS, HEIGHT, WATER_TYPE};
      }
   }

   public static class SmartMineTarget extends Point {
      public Level level;
      public boolean isObject;
      public Consumer<PacketWriter> setupAttackContentPacket;

      public SmartMineTarget(Level var1, int var2, int var3, boolean var4, ToolDamageItem var5) {
         super(var2, var3);
         this.level = var1;
         this.isObject = var4;
         this.setupAttackContentPacket = (var2x) -> {
            var5.setupAttackContentPacketHitTile(var2x, this.level, this.x, this.y);
         };
      }

      public SmartMineTarget(Level var1, Point var2, boolean var3, ToolDamageItem var4) {
         this(var1, var2.x, var2.y, var3, var4);
      }
   }

   private static class SubmittedPath {
      public final PathResult<Point, TilePathfinding> path;
      public final long time;

      public SubmittedPath(PathResult<Point, TilePathfinding> var1, long var2) {
         this.path = var1;
         this.time = var2;
      }
   }

   protected static class BoxEdge {
      public final GameTextureSection section;
      public final int xOffset;
      public final int yOffset;
      public LinkedList<BoxExtend> extensions = new LinkedList();

      public BoxEdge(GameTextureSection var1, int var2, int var3) {
         this.section = var1;
         this.xOffset = var2;
         this.yOffset = var3;
      }

      public BoxEdge extend(GameTextureSection var1, boolean var2, int var3, int var4, int var5, boolean var6, boolean var7) {
         this.extensions.add(new BoxExtend(var1, var2, var3, var4, var5, var6, var7));
         return this;
      }

      public void addExtensionDrawsTopLeft(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.extensions.iterator();

         while(var7.hasNext()) {
            BoxExtend var8 = (BoxExtend)var7.next();
            var8.addDrawableTopLeft(var1, var2, var3, var4, var5, var6);
         }

      }

      public void addExtensionDrawsTopRight(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.extensions.iterator();

         while(var7.hasNext()) {
            BoxExtend var8 = (BoxExtend)var7.next();
            var8.addDrawableTopRight(var1, var2, var3, var4, var5, var6);
         }

      }

      public void addExtensionDrawsBotLeft(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.extensions.iterator();

         while(var7.hasNext()) {
            BoxExtend var8 = (BoxExtend)var7.next();
            var8.addDrawableBotLeft(var1, var2, var3, var4, var5, var6);
         }

      }

      public void addExtensionDrawsBotRight(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.extensions.iterator();

         while(var7.hasNext()) {
            BoxExtend var8 = (BoxExtend)var7.next();
            var8.addDrawableBotRight(var1, var2, var3, var4, var5, var6);
         }

      }

      protected static class BoxExtend {
         public final GameTextureSection extendSection;
         public final boolean horizontal;
         public final int xOffset;
         public final int yOffset;
         public final int lengthOffset;
         public final boolean skipStart;
         public final boolean skipEnd;

         public BoxExtend(GameTextureSection var1, boolean var2, int var3, int var4, int var5, boolean var6, boolean var7) {
            this.extendSection = var1;
            this.horizontal = var2;
            this.xOffset = var3;
            this.yOffset = var4;
            this.lengthOffset = var5;
            this.skipStart = var6;
            this.skipEnd = var7;
         }

         protected void addDrawable(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, Color var6) {
            int var7 = this.extendSection.getWidth();
            int var8 = this.extendSection.getHeight();
            int var9;
            int var10;
            int var11;
            if (this.horizontal) {
               var9 = var4 - (this.skipEnd ? var7 : 0) + this.lengthOffset;

               for(var10 = this.skipStart ? var7 : 0; var10 < var9; var10 += var7) {
                  var11 = Math.min(var7, var9 - var10);
                  var1.add(this.extendSection.section(0, var11, 0, var8)).pos(var2 + var10 + this.xOffset, var3 + this.yOffset);
               }
            } else {
               var9 = var5 - (this.skipEnd ? var8 : 0) + this.lengthOffset;

               for(var10 = this.skipStart ? var8 : 0; var10 < var9; var10 += var8) {
                  var11 = Math.min(var8, var9 - var10);
                  var1.add(this.extendSection.section(0, var7, 0, var11)).pos(var2 + this.xOffset, var3 + var10 + this.yOffset);
               }
            }

         }

         public void addDrawableTopLeft(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
            if (this.horizontal) {
               var2 += var4;
            } else {
               var3 += var4;
            }

            this.addDrawable(var1, var2, var3, var5, var6, Color.RED);
         }

         public void addDrawableTopRight(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
            if (this.horizontal) {
               var2 += var5;
            } else {
               var3 += var4;
            }

            this.addDrawable(var1, var2, var3, var5, var6, Color.GREEN);
         }

         public void addDrawableBotLeft(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
            if (this.horizontal) {
               var2 += var4;
            } else {
               var3 += var6;
            }

            this.addDrawable(var1, var2, var3, var5, var6, Color.BLUE);
         }

         public void addDrawableBotRight(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, int var6) {
            if (this.horizontal) {
               var2 += var5;
            } else {
               var3 += var6;
            }

            this.addDrawable(var1, var2, var3, var5, var6, Color.WHITE);
         }
      }
   }
}
