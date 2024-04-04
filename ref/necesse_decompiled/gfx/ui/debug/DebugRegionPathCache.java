package necesse.gfx.ui.debug;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.TableContentDraw;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

public class DebugRegionPathCache extends Debug {
   public static ArrayList<Function<Level, PathDoorOption>> cacheGetters = new ArrayList();
   private PathDoorOption selectedCache;
   private PathDoorOption currentCache;
   private int selectedSourceID = -1;
   private int currentSourceID = -1;
   private int selectedDestinationID = -1;
   private int currentDestinationID = -1;
   private ArrayList<SourcePaths> lastSourcePaths = new ArrayList();
   private ArrayList<SourceDestinations> lastDestinations = new ArrayList();
   private ArrayList<DestinationPath> lastDestinationPaths = new ArrayList();
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

   public DebugRegionPathCache() {
   }

   protected void onReset() {
      this.selectedCache = null;
      this.currentCache = null;
      this.selectedSourceID = -1;
      this.currentSourceID = -1;
      this.selectedDestinationID = -1;
      this.currentDestinationID = -1;
      this.lastSourcePaths.clear();
      this.lastDestinations.clear();
      this.lastDestinationPaths.clear();
   }

   protected void submitDebugInputEvent(InputEvent var1, Client var2) {
      int var3 = 0;
      if (var1.isMouseWheelEvent()) {
         this.wheelBuffer.add(var1);
         var3 = this.wheelBuffer.useAllScrollY();
         var1.use();
      }

      if (var1.getID() == 265) {
         if (var1.state) {
            var3 = 1;
         }

         var1.use();
      } else if (var1.getID() == 264) {
         if (var1.state) {
            var3 = -1;
         }

         var1.use();
      }

      while(var3 != 0) {
         int var4 = var3 > 0 ? 1 : -1;
         if (this.currentDestinationID == -1) {
            int var5;
            int var6;
            if (this.currentSourceID != -1) {
               if (this.lastDestinationPaths.isEmpty()) {
                  return;
               }

               var5 = -1;

               for(var6 = 0; var6 < this.lastDestinationPaths.size(); ++var6) {
                  if (((DestinationPath)this.lastDestinationPaths.get(var6)).destinationRegionID == this.selectedDestinationID) {
                     var5 = var6;
                     break;
                  }
               }

               if (var5 == -1 && var4 > 0) {
                  this.selectedDestinationID = ((DestinationPath)this.lastDestinationPaths.get(this.lastDestinationPaths.size() - 1)).destinationRegionID;
               } else {
                  this.selectedDestinationID = ((DestinationPath)this.lastDestinationPaths.get(Math.floorMod(var5 - var4, this.lastDestinationPaths.size()))).destinationRegionID;
               }
            } else if (this.currentCache != null) {
               if (this.lastDestinations.isEmpty()) {
                  return;
               }

               var5 = -1;

               for(var6 = 0; var6 < this.lastDestinations.size(); ++var6) {
                  if (((SourceDestinations)this.lastDestinations.get(var6)).sourceRegionID == this.selectedSourceID) {
                     var5 = var6;
                     break;
                  }
               }

               if (var5 == -1 && var4 > 0) {
                  this.selectedSourceID = ((SourceDestinations)this.lastDestinations.get(this.lastDestinations.size() - 1)).sourceRegionID;
               } else {
                  this.selectedSourceID = ((SourceDestinations)this.lastDestinations.get(Math.floorMod(var5 - var4, this.lastDestinations.size()))).sourceRegionID;
               }
            } else {
               if (this.lastSourcePaths.isEmpty()) {
                  return;
               }

               var5 = -1;

               for(var6 = 0; var6 < this.lastSourcePaths.size(); ++var6) {
                  if (((SourcePaths)this.lastSourcePaths.get(var6)).option == this.selectedCache) {
                     var5 = var6;
                     break;
                  }
               }

               if (var5 == -1 && var4 > 0) {
                  this.selectedCache = ((SourcePaths)this.lastSourcePaths.get(this.lastSourcePaths.size() - 1)).option;
               } else {
                  this.selectedCache = ((SourcePaths)this.lastSourcePaths.get(Math.floorMod(var5 - var4, this.lastSourcePaths.size()))).option;
               }
            }
         }

         if (var3 > 0) {
            --var3;
         } else {
            ++var3;
         }
      }

      if (var1.getID() == 257 && (this.selectedCache != null || this.selectedSourceID != -1 || this.selectedDestinationID != -1)) {
         if (var1.state) {
            if (this.selectedDestinationID != -1) {
               this.currentDestinationID = this.selectedDestinationID;
               this.selectedDestinationID = -1;
            } else if (this.selectedSourceID != -1) {
               this.currentSourceID = this.selectedSourceID;
               this.selectedSourceID = -1;
            } else {
               this.currentCache = this.selectedCache;
               this.selectedCache = null;
            }
         }

         var1.use();
      } else if (var1.getID() == 259 && (this.currentCache != null || this.currentSourceID != -1 || this.currentDestinationID != -1)) {
         if (var1.state) {
            if (this.currentDestinationID != -1) {
               this.selectedDestinationID = -1;
               this.currentDestinationID = -1;
            } else if (this.currentSourceID != -1) {
               this.selectedSourceID = -1;
               this.currentSourceID = -1;
            } else {
               this.selectedCache = null;
               this.currentCache = null;
            }
         }

         var1.use();
      }

   }

   private String getSelectedString() {
      ArrayList var1 = new ArrayList();
      if (this.currentCache != null) {
         var1.add(this.currentCache.debugName);
      }

      if (this.currentSourceID != -1) {
         if (this.currentCache == null) {
            var1.add("NULL");
         }

         var1.add("" + this.currentSourceID);
      }

      if (this.currentDestinationID != -1) {
         if (this.currentCache == null) {
            var1.add("NULL");
         }

         if (this.currentSourceID == -1) {
            var1.add("NULL");
         }

         var1.add("" + this.currentDestinationID);
      }

      return GameUtils.join(var1.toArray(), " > ");
   }

   protected void drawDebug(Client var1) {
      this.drawString("Use scroll wheel to change selection");
      this.drawString("Press '" + Input.getName(257) + "' to select");
      this.drawString("Press '" + Input.getName(259) + "' to go back");
      this.drawString("Current: " + this.getSelectedString());
      if (this.currentDestinationID == -1) {
         Iterator var2;
         int var3;
         Iterator var4;
         Color var6;
         FontOptions var7;
         float var10;
         TableContentDraw var11;
         if (this.currentSourceID != -1) {
            this.lastDestinationPaths.clear();
            var2 = this.currentCache.getDestinationPathRegionIDs(this.currentSourceID).iterator();

            while(var2.hasNext()) {
               var3 = (Integer)var2.next();
               this.lastDestinationPaths.add(new DestinationPath(this.currentCache, this.currentSourceID, var3));
            }

            this.lastDestinationPaths.sort(Comparator.comparingInt((var0) -> {
               return var0.path.size();
            }));
            var10 = 0.0F;
            var11 = new TableContentDraw();
            var4 = this.lastDestinationPaths.iterator();

            while(var4.hasNext()) {
               DestinationPath var5 = (DestinationPath)var4.next();
               var6 = Color.getHSBColor(var10 += 0.15F, 1.0F, 1.0F);
               var7 = (new FontOptions(16)).outline().color(var6);
               var11.newRow().addTextColumn(this.selectedDestinationID == var5.destinationRegionID ? ">" : " ", (new FontOptions(16)).outline()).addTextColumn("ID " + var5.sourceRegionID + " > " + var5.destinationRegionID + ":", var7, 10, 0).addTextColumn(var5.path.size() + " regions", var7, 10, 0);
            }

            var11.draw(10, this.skipY(var11.getHeight()));
         } else if (this.currentCache != null) {
            this.lastDestinations.clear();
            var2 = this.currentCache.getSourcePathRegionIDs().iterator();

            while(var2.hasNext()) {
               var3 = (Integer)var2.next();
               this.lastDestinations.add(new SourceDestinations(this.currentCache, var3));
            }

            this.lastDestinations.sort(Comparator.comparingInt((var0) -> {
               return var0.destinations;
            }));
            var10 = 0.0F;
            var11 = new TableContentDraw();
            var4 = this.lastDestinations.iterator();

            while(var4.hasNext()) {
               SourceDestinations var15 = (SourceDestinations)var4.next();
               var6 = Color.getHSBColor(var10 += 0.15F, 1.0F, 1.0F);
               var7 = (new FontOptions(16)).outline().color(var6);
               var11.newRow().addTextColumn(this.selectedSourceID == var15.sourceRegionID ? ">" : " ", (new FontOptions(16)).outline()).addTextColumn("ID " + var15.sourceRegionID + ":", var7, 10, 0).addTextColumn(var15.destinations + " destinations", var7, 10, 0);
            }

            var11.draw(10, this.skipY(var11.getHeight()));
         } else {
            this.lastSourcePaths.clear();
            Level var12 = var1.getLevel();
            if (var1.getLocalServer() != null) {
               Level var13 = var1.getLocalServer().world.getLevel(var12.getIdentifier());
               if (Settings.serverPerspective) {
                  var12 = var13;
               }
            }

            if (var12 != null) {
               var4 = cacheGetters.iterator();

               while(var4.hasNext()) {
                  Function var16 = (Function)var4.next();
                  PathDoorOption var18 = (PathDoorOption)var16.apply(var12);
                  if (var18 != null) {
                     this.lastSourcePaths.add(new SourcePaths(var18));
                  }
               }

               this.lastSourcePaths.sort(Comparator.comparingInt((var0) -> {
                  return var0.paths;
               }));
               float var14 = 0.0F;
               TableContentDraw var17 = new TableContentDraw();
               Iterator var19 = this.lastSourcePaths.iterator();

               while(var19.hasNext()) {
                  SourcePaths var20 = (SourcePaths)var19.next();
                  Color var8 = Color.getHSBColor(var14 += 0.15F, 1.0F, 1.0F);
                  FontOptions var9 = (new FontOptions(16)).outline().color(var8);
                  var17.newRow().addTextColumn(this.selectedCache == var20.option ? ">" : " ", (new FontOptions(16)).outline()).addTextColumn(var20.option.debugName, var9, 10, 0).addTextColumn("" + var20.paths, var9, 10, 0);
               }

               var17.draw(10, this.skipY(var17.getHeight()));
            } else {
               this.drawString("--- No level found ---");
            }
         }
      }

   }

   protected void drawDebugHUD(Level var1, GameCamera var2, PlayerMob var3) {
      if (this.currentDestinationID != -1) {
         Collection var4 = this.currentCache.getCachedPath(this.currentSourceID, this.currentDestinationID);
         SemiRegion var5 = null;
         int var6 = 0;

         SemiRegion var8;
         for(Iterator var7 = var4.iterator(); var7.hasNext(); var5 = var8) {
            var8 = (SemiRegion)var7.next();
            Point var9 = var8.getLevelTile(var8.getAverageCell());
            int var10 = var2.getTileDrawX(var9.x) + 16;
            int var11 = var2.getTileDrawY(var9.y) + 16;
            Color var12 = Color.getHSBColor((float)var6 / (float)var4.size(), 1.0F, 1.0F);
            if (var5 != null) {
               Point var13 = var5.getLevelTile(var5.getAverageCell());
               int var14 = var2.getTileDrawX(var13.x) + 16;
               int var15 = var2.getTileDrawY(var13.y) + 16;
               Screen.drawLineRGBA(var14, var15, var10, var11, (float)var12.getRed() / 255.0F, (float)var12.getGreen() / 255.0F, (float)var12.getBlue() / 255.0F, 1.0F);
            }

            Screen.drawCircle(var2.getTileDrawX(var9.x) + 16, var2.getTileDrawY(var9.y) + 16, 8, 10, (float)var12.getRed() / 255.0F, (float)var12.getGreen() / 255.0F, (float)var12.getBlue() / 255.0F, 1.0F, true);
            ++var6;
         }
      }

   }

   static {
      cacheGetters.add((var0) -> {
         return var0.regionManager.BASIC_DOOR_OPTIONS;
      });
      cacheGetters.add((var0) -> {
         return var0.regionManager.CAN_OPEN_DOORS_OPTIONS;
      });
      cacheGetters.add((var0) -> {
         return var0.regionManager.CANNOT_OPEN_CAN_CLOSE_DOORS_OPTIONS;
      });
      cacheGetters.add((var0) -> {
         return var0.regionManager.CANNOT_PASS_DOORS_OPTIONS;
      });
      cacheGetters.add((var0) -> {
         return var0.regionManager.CAN_BREAK_OBJECTS_OPTIONS;
      });
   }

   private static class DestinationPath {
      public final PathDoorOption option;
      public final int sourceRegionID;
      public final int destinationRegionID;
      public final Collection<SemiRegion> path;

      public DestinationPath(PathDoorOption var1, int var2, int var3) {
         this.option = var1;
         this.sourceRegionID = var2;
         this.destinationRegionID = var3;
         this.path = var1.getCachedPath(var2, var3);
      }
   }

   private static class SourceDestinations {
      public final PathDoorOption option;
      public final int sourceRegionID;
      public final int destinations;

      public SourceDestinations(PathDoorOption var1, int var2) {
         this.option = var1;
         this.sourceRegionID = var2;
         this.destinations = var1.getDestinationPathRegionIDs(var2).size();
      }
   }

   private static class SourcePaths {
      public final PathDoorOption option;
      public final int paths;

      public SourcePaths(PathDoorOption var1) {
         this.option = var1;
         this.paths = var1.getTotalCachedPaths();
      }
   }
}
