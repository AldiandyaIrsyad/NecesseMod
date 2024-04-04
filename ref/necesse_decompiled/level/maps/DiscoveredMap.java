package necesse.level.maps;

import java.awt.Point;
import java.util.Arrays;
import java.util.function.BiConsumer;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;

public class DiscoveredMap {
   public static final int DISCOVER_DISTANCE = 20;
   public static final int DISCOVER_DIVIDER = 4;
   private boolean[] knownMap;
   private int discoveredWidth;
   private int discoveredHeight;
   private int hashCode;

   private DiscoveredMap(boolean[] var1, int var2, int var3) {
      this.knownMap = var1;
      this.discoveredWidth = var2;
      this.discoveredHeight = var3;
   }

   public DiscoveredMap(Level var1) {
      this.ensureMapSize(var1);
   }

   public DiscoveredMap(DiscoveredMap var1) {
      this.knownMap = Arrays.copyOf(var1.knownMap, var1.knownMap.length);
      this.discoveredWidth = var1.discoveredWidth;
      this.discoveredHeight = var1.discoveredHeight;
      this.hashCode = var1.hashCode;
   }

   public boolean ensureMapSize(Level var1) {
      int var2 = var1.width % 4;
      int var3 = var1.width / 4 + (var2 > 0 ? 1 : 0);
      int var4 = var1.height % 4;
      int var5 = var1.height / 4 + (var4 > 0 ? 1 : 0);
      if (this.discoveredWidth == var3 && this.discoveredHeight == var5) {
         return false;
      } else {
         this.knownMap = new boolean[var3 * var5];
         this.discoveredWidth = var3;
         this.discoveredHeight = var5;
         return true;
      }
   }

   public void overrideKnownMap(boolean[] var1, BiConsumer<Integer, Integer> var2) {
      if (this.knownMap.length != var1.length) {
         throw new IndexOutOfBoundsException();
      } else {
         if (var2 == null) {
            this.knownMap = Arrays.copyOf(var1, var1.length);
         } else {
            for(int var3 = 0; var3 < this.discoveredWidth; ++var3) {
               for(int var4 = 0; var4 < this.discoveredHeight; ++var4) {
                  int var5 = var3 + var4 * this.discoveredWidth;
                  if (this.knownMap[var5] != var1[var5]) {
                     this.knownMap[var5] = var1[var5];

                     for(int var6 = 0; var6 < 4; ++var6) {
                        for(int var7 = 0; var7 < 4; ++var7) {
                           var2.accept(var3 * 4 + var6, var4 * 4 + var7);
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public boolean[] getKnownMap() {
      return this.knownMap;
   }

   public int getDiscoveredWidth() {
      return this.discoveredWidth;
   }

   public int getDiscoveredHeight() {
      return this.discoveredHeight;
   }

   public DiscoveredMap(LoadData var1) {
      this.knownMap = var1.getSmallBooleanArray("mapDiscovered");
      this.discoveredWidth = var1.getInt("discoveredWidth");
      this.discoveredHeight = var1.getInt("discoveredHeight");
   }

   public static DiscoveredMap migrateOldSaveData(LoadData var0) {
      boolean[] var1 = var0.getSmallBooleanArray("data");
      double var2 = Math.sqrt((double)var1.length);
      if ((double)((int)var2) == var2) {
         int var4 = (int)var2;
         return new DiscoveredMap(var1, var4, var4);
      } else {
         return null;
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addSmallBooleanArray("mapDiscovered", this.knownMap);
      var1.addInt("discoveredWidth", this.discoveredWidth);
      var1.addInt("discoveredHeight", this.discoveredHeight);
   }

   public boolean combine(DiscoveredMap var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.knownMap.length; ++var3) {
         if (!this.knownMap[var3] && var1.knownMap[var3]) {
            this.knownMap[var3] = true;
            this.hashCode = 0;
            var2 = true;
         }
      }

      return var2;
   }

   public Point tickDiscovery(PlayerMob var1, Point var2, BiConsumer<Integer, Integer> var3) {
      int var4 = var1.getX() / 32;
      int var5 = var1.getY() / 32;
      Point var6 = new Point(var4 / 4, var5 / 4);
      if (var2 != null && var2.equals(var6)) {
         return var2;
      } else {
         for(int var7 = var4 - 20; var7 <= var4 + 20; var7 += 4) {
            for(int var8 = var5 - 20; var8 <= var5 + 20; var8 += 4) {
               this.discoverMap(var7, var8, var3);
            }
         }

         return var6;
      }
   }

   public void discoverEntireMap() {
      Arrays.fill(this.knownMap, true);
   }

   public void discoverMap(int var1, int var2, BiConsumer<Integer, Integer> var3) {
      this.setDiscovered(var1, var2, true, var3);
   }

   public void setDiscovered(int var1, int var2, boolean var3, BiConsumer<Integer, Integer> var4) {
      int var5 = var1 / 4;
      int var6 = var2 / 4;
      if (var5 >= 0 && var5 < this.discoveredWidth && var6 >= 0 && var6 < this.discoveredHeight) {
         int var7 = var5 + var6 * this.discoveredWidth;
         if (this.knownMap[var7] != var3) {
            this.knownMap[var7] = var3;
            this.hashCode = 0;
         }

         if (var4 != null) {
            for(int var8 = 0; var8 < 4; ++var8) {
               for(int var9 = 0; var9 < 4; ++var9) {
                  var4.accept(var5 * 4 + var8, var6 * 4 + var9);
               }
            }

         }
      }
   }

   public boolean isTileKnown(int var1, int var2) {
      int var3 = var1 / 4;
      int var4 = var2 / 4;
      if (var3 >= 0 && var3 < this.discoveredWidth && var4 >= 0 && var4 < this.discoveredHeight) {
         int var5 = var3 + var4 * this.discoveredWidth;
         return this.knownMap[var5];
      } else {
         return false;
      }
   }

   public int getHashCode() {
      if (this.hashCode == 0) {
         this.hashCode = Arrays.hashCode(this.knownMap);
      }

      return this.hashCode;
   }
}
