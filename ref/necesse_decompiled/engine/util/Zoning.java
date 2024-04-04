package necesse.engine.util;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.level.maps.Level;

public class Zoning {
   public Rectangle limits;
   protected TreeSet<Point> tiles;
   protected LinkedList<Rectangle> rectangles;
   protected Rectangle bounds;

   public Zoning(Rectangle var1) {
      this.tiles = getNewZoneSet();
      this.limits = var1;
   }

   public Zoning() {
      this((Rectangle)null);
   }

   public void addZoneSaveData(String var1, SaveData var2) {
      LinkedList var3 = this.getTileRectangles();
      if (!var3.isEmpty()) {
         SaveData var4 = new SaveData(var1);
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            Rectangle var6 = (Rectangle)var5.next();
            var4.addIntArray("", new int[]{var6.x, var6.y, var6.width, var6.height});
         }

         var2.addSaveData(var4);
      }

   }

   public void applyZoneSaveData(String var1, LoadData var2) throws LoadDataException {
      LoadData var3 = var2.getFirstLoadDataByName(var1);
      this.rectangles = new LinkedList();
      if (var3 != null) {
         Iterator var4 = var3.getLoadData().iterator();

         while(var4.hasNext()) {
            LoadData var5 = (LoadData)var4.next();

            try {
               int[] var6 = LoadData.getIntArray(var5);
               Rectangle var7 = new Rectangle(var6[0], var6[1], var6[2], var6[3]);
               if (!var7.isEmpty()) {
                  if (this.limits != null) {
                     var7 = var7.intersection(this.limits);
                  }

                  this.rectangles.add(var7);
               }
            } catch (Exception var8) {
               throw new LoadDataException("Could not load zone area: " + var5.getData());
            }
         }
      }

      this.tiles.clear();
      this.addRectangles(this.rectangles);
   }

   public void writeZonePacket(PacketWriter var1) {
      LinkedList var2 = this.getTileRectangles();
      var1.putNextShortUnsigned(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Rectangle var4 = (Rectangle)var3.next();
         var1.putNextShortUnsigned(var4.x);
         var1.putNextShortUnsigned(var4.y);
         var1.putNextShortUnsigned(var4.width);
         var1.putNextShortUnsigned(var4.height);
      }

   }

   public void readZonePacket(PacketReader var1) {
      this.rectangles = new LinkedList();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         int var5 = var1.getNextShortUnsigned();
         int var6 = var1.getNextShortUnsigned();
         int var7 = var1.getNextShortUnsigned();
         Rectangle var8 = new Rectangle(var4, var5, var6, var7);
         if (!var8.isEmpty()) {
            if (this.limits != null) {
               var8 = var8.intersection(this.limits);
            }

            this.rectangles.add(var8);
         }
      }

      this.tiles.clear();
      this.addRectangles(this.rectangles);
   }

   public boolean containsTile(int var1, int var2) {
      return this.tiles.contains(new Point(var1, var2));
   }

   public boolean addTile(int var1, int var2) {
      if (this.tiles.add(new Point(var1, var2))) {
         this.bounds = null;
         this.rectangles = null;
         return true;
      } else {
         return false;
      }
   }

   public boolean removeTile(int var1, int var2) {
      if (this.tiles.remove(new Point(var1, var2))) {
         this.bounds = null;
         this.rectangles = null;
         return true;
      } else {
         return false;
      }
   }

   public TreeSet<Point> getTiles() {
      return this.tiles;
   }

   public int size() {
      return this.tiles.size();
   }

   public boolean isEmpty() {
      return this.tiles.isEmpty();
   }

   public LinkedList<Rectangle> getTileRectangles() {
      if (this.rectangles == null) {
         this.rectangles = toRectangles(this.tiles);
      }

      return this.rectangles;
   }

   public Rectangle getTileBounds() {
      if (this.tiles.isEmpty()) {
         return null;
      } else {
         if (this.bounds == null) {
            int var1 = Integer.MAX_VALUE;
            int var2 = Integer.MAX_VALUE;
            int var3 = Integer.MIN_VALUE;
            int var4 = Integer.MIN_VALUE;

            Point var6;
            for(Iterator var5 = this.tiles.iterator(); var5.hasNext(); var4 = Math.max(var6.y, var4)) {
               var6 = (Point)var5.next();
               var1 = Math.min(var6.x, var1);
               var2 = Math.min(var6.y, var2);
               var3 = Math.max(var6.x, var3);
            }

            this.bounds = new Rectangle(var1, var2, var3 - var1 + 1, var4 - var2 + 1);
         }

         return this.bounds;
      }
   }

   public boolean removeDisconnected() {
      return this.removeDisconnected(Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public boolean removeDisconnected(int var1, int var2) {
      LinkedList var3 = this.getTileRectangles();
      LinkedList var4 = new LinkedList();
      Section var5 = null;
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         Rectangle var7 = (Rectangle)var6.next();
         boolean var8 = var7.contains(var1, var2);
         ListIterator var9 = var4.listIterator();
         Section var10 = null;

         Section var11;
         while(var9.hasNext()) {
            var11 = (Section)var9.next();
            if (var11.isConnected(var7)) {
               if (var10 != null) {
                  if (var5 == var11) {
                     var5 = var10;
                  }

                  var10.merge(var11);
                  var9.remove();
               }

               if (var8) {
                  var5 = var11;
               }

               var11.add(var7);
               var10 = var11;
            }
         }

         if (var10 == null) {
            var11 = new Section();
            if (var8) {
               var5 = var11;
            }

            var11.add(var7);
            var4.add(var11);
         }
      }

      if (var4.size() <= 1) {
         return false;
      } else {
         Section var13 = var5;
         if (var5 == null) {
            var13 = (Section)var4.stream().max(Comparator.comparingInt((var0) -> {
               return var0.totalSize;
            })).orElse((Object)null);
         }

         Iterator var14 = var4.iterator();

         while(true) {
            Section var15;
            do {
               if (!var14.hasNext()) {
                  return true;
               }

               var15 = (Section)var14.next();
            } while(var15 == var13);

            Iterator var16 = var15.rectangles.iterator();

            while(var16.hasNext()) {
               Rectangle var17 = (Rectangle)var16.next();

               for(int var18 = 0; var18 < var17.width; ++var18) {
                  for(int var12 = 0; var12 < var17.height; ++var12) {
                     this.removeTile(var17.x + var18, var17.y + var12);
                  }
               }
            }
         }
      }
   }

   public void invert(Rectangle var1) {
      if (!var1.isEmpty()) {
         if (this.limits != null) {
            var1 = var1.intersection(this.limits);
         }

         if (!var1.isEmpty()) {
            for(int var2 = 0; var2 < var1.width; ++var2) {
               for(int var3 = 0; var3 < var1.height; ++var3) {
                  if (this.containsTile(var1.x + var2, var1.y + var3)) {
                     this.removeTile(var1.x + var2, var1.y + var3);
                  } else {
                     this.addTile(var1.x + var2, var1.y + var3);
                  }
               }
            }

         }
      }
   }

   public void invert() {
      if (this.limits == null) {
         throw new UnsupportedOperationException("Cannot invert full zones with no limit");
      } else {
         this.invert(this.limits);
      }
   }

   public boolean addRectangle(Rectangle var1) {
      boolean var2 = false;
      if (var1.isEmpty()) {
         return false;
      } else {
         if (this.limits != null) {
            var1 = var1.intersection(this.limits);
         }

         if (var1.isEmpty()) {
            return false;
         } else {
            for(int var3 = 0; var3 < var1.width; ++var3) {
               for(int var4 = 0; var4 < var1.height; ++var4) {
                  var2 = this.addTile(var1.x + var3, var1.y + var4) || var2;
               }
            }

            return var2;
         }
      }
   }

   public boolean addRectangles(List<Rectangle> var1) {
      boolean var2 = false;

      Rectangle var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 = this.addRectangle(var4) || var2) {
         var4 = (Rectangle)var3.next();
      }

      return var2;
   }

   public boolean removeRectangle(Rectangle var1) {
      boolean var2 = false;
      if (var1.isEmpty()) {
         return false;
      } else {
         if (this.limits != null) {
            var1 = var1.intersection(this.limits);
         }

         if (var1.isEmpty()) {
            return false;
         } else {
            for(int var3 = 0; var3 < var1.width; ++var3) {
               for(int var4 = 0; var4 < var1.height; ++var4) {
                  var2 = this.removeTile(var1.x + var3, var1.y + var4) || var2;
               }
            }

            return var2;
         }
      }
   }

   public boolean removeRectangles(List<Rectangle> var1) {
      boolean var2 = false;

      Rectangle var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 = this.removeRectangle(var4) || var2) {
         var4 = (Rectangle)var3.next();
      }

      return var2;
   }

   public static boolean isConnected(Rectangle var0, Rectangle var1) {
      return (new Rectangle(var0.x - 1, var0.y, var0.width + 2, var0.height)).intersects(var1) || (new Rectangle(var0.x, var0.y - 1, var0.width, var0.height + 2)).intersects(var1);
   }

   public static TreeSet<Point> getNewZoneSet() {
      Comparator var0 = Comparator.comparingInt((var0x) -> {
         return var0x.y;
      });
      var0 = var0.thenComparingInt((var0x) -> {
         return var0x.x;
      });
      return new TreeSet(var0);
   }

   public static LinkedList<Rectangle> toRectangles(TreeSet<Point> var0) {
      if (var0.isEmpty()) {
         return new LinkedList();
      } else {
         LinkedList var1 = new LinkedList();
         TreeSet var2 = getNewZoneSet();
         HashSet var3 = new HashSet();
         var2.add((Point)var0.first());

         while(!var2.isEmpty()) {
            Point var4 = (Point)var2.pollFirst();
            if (!var3.contains(var4)) {
               var3.add(var4);
               int var5 = fillWidth(var4, var0, var2, var3);
               int var6 = fillHeight(var4, var5, var0, var2, var3);
               var1.add(new Rectangle(var4.x, var4.y, var5, var6));
            }
         }

         return var1;
      }
   }

   private static int fillWidth(Point var0, TreeSet<Point> var1, TreeSet<Point> var2, HashSet<Point> var3) {
      Point var4 = var0;
      int var5 = 1;

      while(true) {
         Point var6 = (Point)var1.higher(var4);
         if (var6 == null) {
            break;
         }

         if (var3.contains(var6) || var6.x != var4.x + 1 || var6.y != var0.y) {
            if (var6.x != var0.x || var6.y != var0.y + 1) {
               var2.add(var6);
            }
            break;
         }

         ++var5;
         var3.add(var6);
         var4 = var6;
      }

      return var5;
   }

   private static int fillHeight(Point var0, int var1, TreeSet<Point> var2, TreeSet<Point> var3, HashSet<Point> var4) {
      int var5 = 1;

      while(true) {
         Point var6 = new Point(var0.x, var0.y + var5);
         if (!var2.contains(var6) || var4.contains(var6)) {
            if (var2.contains(var6)) {
               var3.add(var6);
            }
            break;
         }

         boolean var7 = true;

         int var8;
         for(var8 = 1; var8 < var1; ++var8) {
            Point var9 = new Point(var6.x + var8, var6.y);
            if (!var2.contains(var9) || var4.contains(var9)) {
               var7 = false;
               break;
            }
         }

         if (!var7) {
            var3.add(var6);
            break;
         }

         ++var5;

         for(var8 = 0; var8 < var1; ++var8) {
            var4.add(new Point(var6.x + var8, var6.y));
         }

         Point var10 = (Point)var2.higher(new Point(var6.x + var1 - 1, var6.y));
         if (var10 != null) {
            var3.add(var10);
         }
      }

      return var5;
   }

   public SharedTextureDrawOptions getDrawOptions(Color var1, Color var2, GameCamera var3) {
      Rectangle var4 = this.getTileBounds();
      if (var4 != null) {
         Rectangle var5 = var3.getBounds();
         if (var5.intersects((double)(var4.x * 32), (double)(var4.y * 32), (double)(var4.width * 32), (double)(var4.height * 32))) {
            SharedTextureDrawOptions var6 = new SharedTextureDrawOptions(Screen.getQuadTexture());
            Iterator var7 = this.getTiles().iterator();

            while(true) {
               Point var8;
               do {
                  if (!var7.hasNext()) {
                     return var6;
                  }

                  var8 = (Point)var7.next();
               } while(!var5.intersects((double)(var8.x * 32), (double)(var8.y * 32), 32.0, 32.0));

               boolean[] var9 = new boolean[Level.adjacentGetters.length];

               for(int var10 = 0; var10 < var9.length; ++var10) {
                  Point var11 = Level.adjacentGetters[var10];
                  var9[var10] = this.containsTile(var8.x + var11.x, var8.y + var11.y);
               }

               addDrawOptions(var6, var8, var9, var1, var2, var3);
            }
         }
      }

      return null;
   }

   public static void addDrawOptions(SharedTextureDrawOptions var0, Point var1, boolean[] var2, Color var3, Color var4, GameCamera var5) {
      addDrawOptions(var0, var5.getTileDrawX(var1.x), var5.getTileDrawY(var1.y), var2, var3, var4);
   }

   public static void addDrawOptions(SharedTextureDrawOptions var0, int var1, int var2, boolean[] var3, Color var4, Color var5) {
      Color[] var6 = getEdgeColors(var3[3], var3[0], var3[1], var4, var5);
      addDrawOptions(var0, var1, var2, 0, var6);
      Color[] var7 = getEdgeColors(var3[1], var3[2], var3[4], var4, var5);
      addDrawOptions(var0, var1 + 16, var2, 3, var7);
      Color[] var8 = getEdgeColors(var3[4], var3[7], var3[6], var4, var5);
      addDrawOptions(var0, var1 + 16, var2 + 16, 2, var8);
      Color[] var9 = getEdgeColors(var3[6], var3[5], var3[3], var4, var5);
      addDrawOptions(var0, var1, var2 + 16, 1, var9);
   }

   public static void addDrawOptions(SharedTextureDrawOptions var0, Point var1, int var2, Color[] var3, int var4, int var5, GameCamera var6) {
      addDrawOptions(var0, var6.getTileDrawX(var1.x) + var4, var6.getTileDrawY(var1.y) + var5, var2, var3);
   }

   public static void addDrawOptions(SharedTextureDrawOptions var0, int var1, int var2, int var3, Color[] var4) {
      float[] var5 = new float[16];

      for(int var6 = 0; var6 < var4.length; ++var6) {
         Color var7 = var4[(var6 + var3) % var4.length];
         var5[var6 * 4] = (float)var7.getRed() / 255.0F;
         var5[var6 * 4 + 1] = (float)var7.getGreen() / 255.0F;
         var5[var6 * 4 + 2] = (float)var7.getBlue() / 255.0F;
         var5[var6 * 4 + 3] = (float)var7.getAlpha() / 255.0F;
      }

      var0.addFull().size(16, 16).advColor(var5).pos(var1, var2);
   }

   public static Color[] getEdgeColors(boolean var0, boolean var1, boolean var2, Color var3, Color var4) {
      if (var0) {
         if (var2) {
            return var1 ? new Color[]{var4, var4, var4, var4} : new Color[]{var3, var4, var4, var4};
         } else {
            return new Color[]{var3, var3, var4, var4};
         }
      } else {
         return var2 ? new Color[]{var3, var4, var4, var3} : new Color[]{var3, var3, var4, var3};
      }
   }

   public static SharedTextureDrawOptions getRectangleDrawOptions(Rectangle var0, Color var1, Color var2, GameCamera var3) {
      SharedTextureDrawOptions var4 = new SharedTextureDrawOptions(Screen.getQuadTexture());
      addRectangleDrawOptions(var4, var0, var1, var2, var3);
      return var4;
   }

   public static SharedTextureDrawOptions getRectangleDrawOptions(Rectangle var0, Color var1, Color var2, int var3, GameCamera var4) {
      SharedTextureDrawOptions var5 = new SharedTextureDrawOptions(Screen.getQuadTexture());
      addRectangleDrawOptions(var5, var0, var1, var2, var3, var4);
      return var5;
   }

   public static void addRectangleDrawOptions(SharedTextureDrawOptions var0, Rectangle var1, Color var2, Color var3, GameCamera var4) {
      addRectangleDrawOptions(var0, var1, var2, var3, 16, var4);
   }

   public static void addRectangleDrawOptions(SharedTextureDrawOptions var0, Rectangle var1, Color var2, Color var3, int var4, GameCamera var5) {
      float var6 = (float)var2.getRed() / 255.0F;
      float var7 = (float)var2.getGreen() / 255.0F;
      float var8 = (float)var2.getBlue() / 255.0F;
      float var9 = (float)var2.getAlpha() / 255.0F;
      float var10 = (float)var3.getRed() / 255.0F;
      float var11 = (float)var3.getGreen() / 255.0F;
      float var12 = (float)var3.getBlue() / 255.0F;
      float var13 = (float)var3.getAlpha() / 255.0F;
      int var14 = Math.min(var1.width / 2, var4);
      int var15 = Math.min(var1.height / 2, var4);
      var0.addFull().size(var1.width - var14 * 2, var1.height - var15 * 2).color(var10, var11, var12, var13).pos(var5.getDrawX(var1.x + var14), var5.getDrawY(var1.y + var15));
      float[] var16 = new float[]{var6, var7, var8, var9, var6, var7, var8, var9, var10, var11, var12, var13, var6, var7, var8, var9};
      var0.addFull().size(var14, var15).advColor(var16).pos(var5.getDrawX(var1.x), var5.getDrawY(var1.y));
      float[] var17 = new float[]{var6, var7, var8, var9, var6, var7, var8, var9, var10, var11, var12, var13, var10, var11, var12, var13};
      var0.addFull().size(var1.width - var14 * 2, var15).advColor(var17).pos(var5.getDrawX(var1.x + var14), var5.getDrawY(var1.y));
      float[] var18 = new float[]{var6, var7, var8, var9, var6, var7, var8, var9, var6, var7, var8, var9, var10, var11, var12, var13};
      var0.addFull().size(var14, var15).advColor(var18).pos(var5.getDrawX(var1.x + var1.width - var14), var5.getDrawY(var1.y));
      float[] var19 = new float[]{var10, var11, var12, var13, var6, var7, var8, var9, var6, var7, var8, var9, var10, var11, var12, var13};
      var0.addFull().size(var14, var1.height - var15 * 2).advColor(var19).pos(var5.getDrawX(var1.x + var1.width - var14), var5.getDrawY(var1.y + var15));
      float[] var20 = new float[]{var10, var11, var12, var13, var6, var7, var8, var9, var6, var7, var8, var9, var6, var7, var8, var9};
      var0.addFull().size(var14, var15).advColor(var20).pos(var5.getDrawX(var1.x + var1.width - var14), var5.getDrawY(var1.y + var1.height - var15));
      float[] var21 = new float[]{var10, var11, var12, var13, var10, var11, var12, var13, var6, var7, var8, var9, var6, var7, var8, var9};
      var0.addFull().size(var1.width - var14 * 2, var15).advColor(var21).pos(var5.getDrawX(var1.x + var14), var5.getDrawY(var1.y + var1.height - var15));
      float[] var22 = new float[]{var6, var7, var8, var9, var10, var11, var12, var13, var6, var7, var8, var9, var6, var7, var8, var9};
      var0.addFull().size(var14, var15).advColor(var22).pos(var5.getDrawX(var1.x), var5.getDrawY(var1.y + var1.height - var15));
      float[] var23 = new float[]{var6, var7, var8, var9, var10, var11, var12, var13, var10, var11, var12, var13, var6, var7, var8, var9};
      var0.addFull().size(var14, var1.height - var15 * 2).advColor(var23).pos(var5.getDrawX(var1.x), var5.getDrawY(var1.y + var15));
   }

   private static class Section {
      private LinkedList<Rectangle> rectangles = new LinkedList();
      private int totalSize;

      public Section() {
      }

      public boolean isConnected(Rectangle var1) {
         Iterator var2 = this.rectangles.iterator();

         Rectangle var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (Rectangle)var2.next();
         } while(!Zoning.isConnected(var1, var3));

         return true;
      }

      public void add(Rectangle var1) {
         this.rectangles.add(var1);
         this.totalSize += var1.width * var1.height;
      }

      public void merge(Section var1) {
         this.rectangles.addAll(var1.rectangles);
         this.totalSize += var1.totalSize;
      }
   }
}
