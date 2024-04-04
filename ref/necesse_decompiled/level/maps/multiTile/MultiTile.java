package necesse.level.maps.multiTile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class MultiTile {
   public final int rotation;
   public final int x;
   public final int y;
   public final int width;
   public final int height;
   public final boolean isMaster;
   public final int[] ids;

   public MultiTile(int var1, int var2, int var3, int var4, int var5, boolean var6, int... var7) {
      if (var3 > 0 && var4 > 0) {
         if (var1 < var3 && var2 < var4) {
            if (var7.length != var3 * var4) {
               throw new IllegalArgumentException("IDs must have a length equal width * height to contain all multiTile ids");
            } else {
               this.rotation = var5;
               this.isMaster = var6;
               PresetRotation var8 = PresetRotation.toRotationAngle(var5);
               if (var8 == null || var3 == 1 && var4 == 1) {
                  this.x = var1;
                  this.y = var2;
                  this.width = var3;
                  this.height = var4;
                  this.ids = var7;
               } else {
                  Point var9 = PresetUtils.getRotatedPointInSpace(var1, var2, var3, var4, var8);
                  this.x = var9.x;
                  this.y = var9.y;
                  Point var10 = PresetUtils.getRotatedPoint(var3, var4, 0, 0, var8);
                  this.width = Math.abs(var10.x);
                  this.height = Math.abs(var10.y);
                  this.ids = new int[var7.length];

                  for(int var11 = 0; var11 < var3; ++var11) {
                     for(int var12 = 0; var12 < var4; ++var12) {
                        Point var13 = PresetUtils.getRotatedPointInSpace(var11, var12, var3, var4, var8);
                        this.ids[this.getIndex(var13.x, var13.y, this.width)] = var7[this.getIndex(var11, var12, var3)];
                     }
                  }
               }

            }
         } else {
            throw new IllegalArgumentException("X/Y cannot be higher or equal to width/height");
         }
      } else {
         throw new IllegalArgumentException("Width and height cannot be 0 or below");
      }
   }

   public String checkValid() {
      HashSet var1 = new HashSet();
      boolean var2 = false;
      int[] var3 = this.ids;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         if (var6 < 0 || var6 >= ObjectRegistry.getObjectsCount()) {
            return "Could not find object with id " + var6;
         }

         if (var1.contains(var6)) {
            return ObjectRegistry.getObject(var6).getStringID() + " used multiple times";
         }

         if (ObjectRegistry.getObject(var6).isMultiTileMaster()) {
            if (var2) {
               return "Has multiple master objects";
            }

            var2 = true;
         }

         var1.add(var6);
      }

      if (!var2) {
         return "Does not have a master object";
      } else {
         return null;
      }
   }

   protected int getIndex(int var1, int var2, int var3) {
      return var1 + var2 * var3;
   }

   public Point getCenterLevelPos(int var1, int var2) {
      int var3 = (var1 - this.x) * 32 + this.width * 32 / 2;
      int var4 = (var2 - this.y) * 32 + this.height * 32 / 2;
      return new Point(var3, var4);
   }

   public int getCenterXOffset() {
      float var1 = (float)(this.width - 1) / 2.0F - (float)this.x;
      return (int)(var1 + Math.signum(var1));
   }

   public int getCenterYOffset() {
      float var1 = (float)(this.height - 1) / 2.0F - (float)this.y;
      return (int)(var1 + Math.signum(var1));
   }

   public Point getCenterTileOffset() {
      return new Point(this.getCenterXOffset(), this.getCenterYOffset());
   }

   public Rectangle getTileRectangle(int var1, int var2) {
      return new Rectangle(var1 - this.x, var2 - this.y, this.width, this.height);
   }

   public Rectangle getAdjacentTileRectangle(int var1, int var2) {
      return new Rectangle(var1 - this.x - 1, var2 - this.y - 1, this.width + 2, this.height + 2);
   }

   public Rectangle getLevelRectangle(int var1, int var2) {
      Rectangle var3 = this.getTileRectangle(var1, var2);
      return new Rectangle(var3.x * 32, var3.y * 32, var3.width * 32, var3.height * 32);
   }

   public Point getMirrorXPosOffset() {
      return new Point(0, 0);
   }

   public Point getMirrorYPosOffset() {
      return new Point(0, 0);
   }

   public int getXMirrorRotation() {
      if (this.rotation == 1) {
         return 3;
      } else {
         return this.rotation == 3 ? 1 : this.rotation;
      }
   }

   public int getYMirrorRotation() {
      if (this.rotation == 0) {
         return 2;
      } else {
         return this.rotation == 2 ? 0 : this.rotation;
      }
   }

   public Point getPresetRotationOffset(PresetRotation var1) {
      return new Point(0, 0);
   }

   public int getPresetRotation(PresetRotation var1) {
      return var1 == null ? this.rotation : (this.rotation + var1.dirOffset) % 4;
   }

   public ArrayList<CoordinateValue<Integer>> getIDs(int var1, int var2) {
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < this.width; ++var4) {
         for(int var5 = 0; var5 < this.height; ++var5) {
            int var6 = var1 + var4 - this.x;
            int var7 = var2 + var5 - this.y;
            var3.add(new CoordinateValue(var6, var7, this.ids[this.getIndex(var4, var5, this.width)]));
         }
      }

      return var3;
   }

   public Stream<CoordinateValue<Integer>> streamIDs(int var1, int var2) {
      Stream.Builder var3 = Stream.builder();

      for(int var4 = 0; var4 < this.width; ++var4) {
         for(int var5 = 0; var5 < this.height; ++var5) {
            int var6 = var1 + var4 - this.x;
            int var7 = var2 + var5 - this.y;
            var3.add(new CoordinateValue(var6, var7, this.ids[this.getIndex(var4, var5, this.width)]));
         }
      }

      return var3.build();
   }

   public Stream<CoordinateValue<GameObject>> streamObjects(int var1, int var2) {
      return this.streamIDs(var1, var2).map((var0) -> {
         return new CoordinateValue(var0.tileX, var0.tileY, ObjectRegistry.getObject((Integer)var0.value));
      });
   }

   public IntStream streamIDs() {
      return this.streamIDs(0, 0).mapToInt((var0) -> {
         return (Integer)var0.value;
      });
   }

   public Stream<GameObject> streamObjects() {
      return this.streamIDs().mapToObj(ObjectRegistry::getObject);
   }

   public Stream<CoordinateValue<Integer>> streamOtherIDs(int var1, int var2) {
      return this.streamIDs(var1, var2).filter((var2x) -> {
         return var2x.tileX != var1 || var2x.tileY != var2;
      });
   }

   public Stream<CoordinateValue<GameObject>> streamOtherObjects(int var1, int var2) {
      return this.streamOtherIDs(var1, var2).map((var0) -> {
         return new CoordinateValue(var0.tileX, var0.tileY, ObjectRegistry.getObject((Integer)var0.value));
      });
   }

   public IntStream streamOtherIDs() {
      return this.streamIDs(0, 0).filter((var0) -> {
         return var0.tileX != 0 || var0.tileY != 0;
      }).mapToInt((var0) -> {
         return (Integer)var0.value;
      });
   }

   public Stream<GameObject> streamOtherObjects() {
      return this.streamOtherIDs().mapToObj(ObjectRegistry::getObject);
   }

   public Optional<LevelObject> getMasterLevelObject(Level var1, int var2, int var3) {
      return this.streamObjects(var2, var3).filter((var1x) -> {
         return ((GameObject)var1x.value).isMultiTileMaster() && var1.getObjectID(var1x.tileX, var1x.tileY) == ((GameObject)var1x.value).getID();
      }).findFirst().map((var1x) -> {
         return new LevelObject(var1, var1x.tileX, var1x.tileY);
      });
   }

   public GameObject getMasterObject() {
      return (GameObject)this.streamObjects().filter(GameObject::isMultiTileMaster).findFirst().orElseThrow(() -> {
         return new IllegalStateException("Could not find object master");
      });
   }

   public LinkedList<Point> getAdjacentTiles(int var1, int var2, boolean var3) {
      LinkedList var4 = new LinkedList();

      int var5;
      int var6;
      for(var5 = 0; var5 < this.width; ++var5) {
         var6 = var1 + var5 - this.x;
         var4.add(new Point(var6, var2 - 1 - this.y));
         var4.add(new Point(var6, var2 + this.height - this.y));
      }

      for(var5 = 0; var5 < this.height; ++var5) {
         var6 = var2 + var5 - this.y;
         var4.add(new Point(var1 - 1 - this.x, var6));
         var4.add(new Point(var1 + this.width - this.x, var6));
      }

      if (var3) {
         var4.add(new Point(var1 - 1 - this.x, var2 - 1 - this.y));
         var4.add(new Point(var1 + this.width - this.x, var2 - 1 - this.y));
         var4.add(new Point(var1 - 1 - this.x, var2 + this.height - this.y));
         var4.add(new Point(var1 + this.width - this.x, var2 + this.height - this.y));
      }

      return var4;
   }

   public static class CoordinateValue<T> {
      public final int tileX;
      public final int tileY;
      public final T value;

      public CoordinateValue(int var1, int var2, T var3) {
         this.tileX = var1;
         this.tileY = var2;
         this.value = var3;
      }
   }
}
