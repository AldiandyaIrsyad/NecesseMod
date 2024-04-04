package necesse.level.maps;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;

public class LevelMap {
   public static final int TEXTURE_TILE_SIZE = 30;
   public static final int MAP_SCALE = 8;
   public final Level level;
   public final boolean includeTextures;
   protected Settings.LightSetting currentSetting;
   public Point lastDiscoveryPoint;
   public DiscoveredMap discoveredMap;
   private LinkedList<MapDrawElement> drawElements = new LinkedList();
   private int mapUpdateTicks;
   private HashSet<Point> mapUpdatePoints;
   private MapTexture[][] mapTextures;
   private MapTexture[][] cheatMapTextures;

   public LevelMap(Level var1, boolean var2) {
      this.level = var1;
      this.currentSetting = var1.lightManager.getCurrentSetting();
      this.includeTextures = var2;
      this.discoveredMap = new DiscoveredMap(var1);
   }

   public void addDrawElement(MapDrawElement var1) {
      this.cleanDrawElements();
      this.drawElements.add(var1);
      var1.init(this.level);
   }

   private void cleanDrawElements() {
      ListIterator var1 = this.drawElements.listIterator();

      while(true) {
         MapDrawElement var2;
         do {
            if (!var1.hasNext()) {
               return;
            }

            var2 = (MapDrawElement)var1.next();
         } while(!var2.isRemoved() && !var2.shouldRemove());

         var2.onRemove();
         var1.remove();
      }
   }

   public Iterable<MapDrawElement> getDrawElements() {
      this.cleanDrawElements();
      return this.drawElements;
   }

   public void setMapDiscovered(int var1, int var2, boolean var3) {
      this.discoveredMap.setDiscovered(var1, var2, var3, this::updateMapTexture);
   }

   public void tickDiscovery(PlayerMob var1) {
      this.lastDiscoveryPoint = this.discoveredMap.tickDiscovery(var1, this.lastDiscoveryPoint, this::updateMapTexture);
   }

   public void discoverEntireMap() {
      this.discoveredMap.discoverEntireMap();
      this.generateMapTextures();
   }

   public boolean isTileKnown(int var1, int var2) {
      return this.discoveredMap.isTileKnown(var1, var2);
   }

   public void tickMapTexture() {
      if (this.includeTextures) {
         if (this.currentSetting != this.level.lightManager.getCurrentSetting()) {
            this.dispose();
            this.generateMapTextures();
            this.currentSetting = this.level.lightManager.setting;
         }

         ++this.mapUpdateTicks;
         if (this.mapUpdateTicks > 20) {
            Performance.record(this.level.tickManager(), "tickMap", (Runnable)(() -> {
               this.mapUpdateTicks = 0;
               if (this.mapUpdatePoints != null) {
                  Iterator var1 = this.mapUpdatePoints.iterator();

                  while(var1.hasNext()) {
                     Point var2 = (Point)var1.next();
                     MapTexture var3 = getMapTexture(this.mapTextures, var2.x, var2.y);
                     if (var3 != null) {
                        var3.updateMapTile(var2.x, var2.y, false);
                     }

                     MapTexture var4 = getMapTexture(this.cheatMapTextures, var2.x, var2.y);
                     if (var4 != null) {
                        var4.updateMapTile(var2.x, var2.y, true);
                     }
                  }

                  this.mapUpdatePoints.clear();
               }

            }));
         }

      }
   }

   public static MapTexture getMapTexture(MapTexture[][] var0, int var1, int var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var1 / 30;
         int var4 = var2 / 30;
         return var3 >= 0 && var3 < var0.length && var4 >= 0 && var4 < var0[var3].length ? var0[var3][var4] : null;
      }
   }

   public void generateMapTextures() {
      if (this.includeTextures) {
         this.mapUpdatePoints = new HashSet();
         this.mapUpdateTicks = 0;
         if (this.mapTextures != null) {
            Arrays.stream(this.mapTextures).flatMap(Arrays::stream).forEach(GameTexture::delete);
         }

         this.mapTextures = this.generateMapTextures(false);
         if (this.cheatMapTextures != null) {
            Arrays.stream(this.cheatMapTextures).flatMap(Arrays::stream).forEach(GameTexture::delete);
         }

         this.cheatMapTextures = this.generateMapTextures(true);
      }
   }

   public MapTexture[][] generateMapTextures(boolean var1) {
      int var2 = this.level.width % 30;
      int var3 = this.level.height % 30;
      int var4 = this.level.width / 30 + (var2 > 0 ? 1 : 0);
      int var5 = this.level.height / 30 + (var3 > 0 ? 1 : 0);
      MapTexture[][] var6 = new MapTexture[var4][var5];

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = 30;
            int var10 = 30;
            if (var7 == var4 - 1 && var2 > 0) {
               var9 = var2;
            }

            if (var8 == var5 - 1 && var3 > 0) {
               var10 = var3;
            }

            MapTexture var11 = new MapTexture(this, var7, var8, var9, var10);
            var11.setBlendQuality(GameTexture.BlendQuality.NEAREST);

            for(int var12 = 0; var12 < var9; ++var12) {
               for(int var13 = 0; var13 < var10; ++var13) {
                  var11.updateMap(var12, var13, var1);
               }
            }

            var6[var7][var8] = var11;
         }
      }

      return var6;
   }

   public void updateMapTexture(int var1, int var2) {
      if (this.mapUpdatePoints != null && this.includeTextures) {
         if (this.mapUpdatePoints.size() == 0) {
            this.mapUpdateTicks = 0;
         }

         this.mapUpdatePoints.add(new Point(var1, var2));
      }

   }

   public MapTexture[][] getMapTextures() {
      return GlobalData.debugCheatActive() ? this.cheatMapTextures : this.mapTextures;
   }

   public void setDiscoveredData(boolean[] var1) {
      try {
         this.discoveredMap.overrideKnownMap(var1, this::updateMapTexture);
      } catch (IndexOutOfBoundsException var3) {
         GameLog.warn.println("Client received invalid map data, from server.");
      }

   }

   public void dispose() {
      if (this.mapTextures != null) {
         Arrays.stream(this.mapTextures).flatMap(Arrays::stream).forEach(GameTexture::delete);
      }

      this.mapTextures = null;
      if (this.cheatMapTextures != null) {
         Arrays.stream(this.cheatMapTextures).flatMap(Arrays::stream).forEach(GameTexture::delete);
      }

      this.cheatMapTextures = null;
   }
}
