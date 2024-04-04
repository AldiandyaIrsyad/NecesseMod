package necesse.engine;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Zoning;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.level.maps.Level;

public class GameTileRange {
   public final int minRange;
   public final int maxRange;
   private Rectangle drawBounds;
   private HashSet<Point> validTiles;
   private Zoning zoning;
   private ArrayList<DrawData> drawData;

   public GameTileRange(int var1, Point... var2) {
      this.minRange = 0;
      this.maxRange = var1;
      this.zoning = new Zoning();
      this.validTiles = new HashSet();
      float var3 = (float)var1 + 0.5F;

      for(int var4 = -var1; var4 <= var1; ++var4) {
         for(int var5 = -var1; var5 <= var1; ++var5) {
            double var6 = Math.sqrt((double)(var4 * var4 + var5 * var5));
            if (var6 <= (double)var3) {
               this.zoning.addTile(var4, var5);
               this.validTiles.add(new Point(var4, var5));
               Point[] var8 = var2;
               int var9 = var2.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  Point var11 = var8[var10];
                  Point var12 = new Point(var4 + var11.x, var5 + var11.y);
                  if (!this.validTiles.contains(var12)) {
                     this.zoning.addTile(var12.x, var12.y);
                     this.validTiles.add(var12);
                  }
               }
            }
         }
      }

   }

   public GameTileRange(int var1, int var2, Point... var3) {
      this.minRange = Math.min(var1, var2);
      this.maxRange = var2;
      this.zoning = new Zoning();
      this.validTiles = new HashSet();
      float var4 = (float)var1 - 0.5F;
      float var5 = (float)var2 + 0.5F;
      HashSet var6 = new HashSet();

      for(int var7 = -var2; var7 <= var2; ++var7) {
         for(int var8 = -var2; var8 <= var2; ++var8) {
            double var9 = Math.sqrt((double)(var7 * var7 + var8 * var8));
            if (var9 <= (double)var5) {
               boolean var11 = var9 < (double)var4;
               if (var11) {
                  var6.add(new Point(var7, var8));
               } else {
                  this.zoning.addTile(var7, var8);
                  this.validTiles.add(new Point(var7, var8));
               }

               Point[] var12 = var3;
               int var13 = var3.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  Point var15 = var12[var14];
                  Point var16 = new Point(var7 + var15.x, var8 + var15.y);
                  if (var11) {
                     var6.add(var16);
                  } else if (!this.validTiles.contains(var16)) {
                     this.zoning.addTile(var16.x, var16.y);
                     this.validTiles.add(var16);
                  }
               }
            }
         }
      }

      Iterator var17 = var6.iterator();

      while(var17.hasNext()) {
         Point var18 = (Point)var17.next();
         this.zoning.removeTile(var18.x, var18.y);
         this.validTiles.remove(var18);
      }

   }

   public GameTileRange(int var1, Rectangle var2) {
      this.minRange = 0;
      this.maxRange = var1;
      this.zoning = new Zoning();
      this.validTiles = new HashSet();
      if (var2.width > 0 && var2.height > 0) {
         float var3 = (float)var1 + 0.5F;

         for(int var4 = -var1; var4 <= var1; ++var4) {
            for(int var5 = -var1; var5 <= var1; ++var5) {
               double var6 = Math.sqrt((double)(var4 * var4 + var5 * var5));
               if (var6 <= (double)var3) {
                  for(int var8 = 0; var8 < var2.width; ++var8) {
                     for(int var9 = 0; var9 < var2.height; ++var9) {
                        Point var10 = new Point(var4 + var2.x + var8, var5 + var2.y + var9);
                        if (!this.validTiles.contains(var10)) {
                           this.zoning.addTile(var10.x, var10.y);
                           this.validTiles.add(var10);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public GameTileRange(int var1, int var2, Rectangle var3) {
      this.minRange = Math.min(var1, var2);
      this.maxRange = var2;
      this.zoning = new Zoning();
      this.validTiles = new HashSet();
      if (var3.width > 0 && var3.height > 0) {
         float var4 = (float)var1 - 0.5F;
         float var5 = (float)var2 + 0.5F;
         HashSet var6 = new HashSet();

         for(int var7 = -var2; var7 <= var2; ++var7) {
            for(int var8 = -var2; var8 <= var2; ++var8) {
               double var9 = Math.sqrt((double)(var7 * var7 + var8 * var8));
               if (var9 <= (double)var5) {
                  boolean var11 = var9 < (double)var4;

                  for(int var12 = 0; var12 < var3.width; ++var12) {
                     for(int var13 = 0; var13 < var3.height; ++var13) {
                        Point var14 = new Point(var7 + var3.x + var12, var8 + var3.y + var13);
                        if (var11) {
                           var6.add(var14);
                        } else if (!this.validTiles.contains(var14)) {
                           this.zoning.addTile(var14.x, var14.y);
                           this.validTiles.add(var14);
                        }
                     }
                  }
               }
            }
         }

         Iterator var15 = var6.iterator();

         while(var15.hasNext()) {
            Point var16 = (Point)var15.next();
            this.zoning.removeTile(var16.x, var16.y);
            this.validTiles.remove(var16);
         }
      }

   }

   private void generateDrawData() {
      this.drawData = new ArrayList();
      Iterator var1 = this.zoning.getTiles().iterator();

      while(var1.hasNext()) {
         Point var2 = (Point)var1.next();
         boolean[] var3 = new boolean[Level.adjacentGetters.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            Point var5 = Level.adjacentGetters[var4];
            var3[var4] = this.zoning.containsTile(var2.x + var5.x, var2.y + var5.y);
         }

         this.drawData.add(new DrawData(var2.x * 32, var2.y * 32, var3));
      }

      Rectangle var6 = this.zoning.getTileBounds();
      this.drawBounds = new Rectangle(var6.x * 32, var6.y * 32, var6.width * 32, var6.height * 32);
   }

   public boolean isWithinRange(Point var1, Point var2) {
      return this.isWithinRange(var1.x, var1.y, var2.x, var2.y);
   }

   public boolean isWithinRange(int var1, int var2, Point var3) {
      return this.isWithinRange(var1, var2, var3.x, var3.y);
   }

   public boolean isWithinRange(Point var1, int var2, int var3) {
      return this.isWithinRange(var1.x, var1.y, var2, var3);
   }

   public boolean isWithinRange(int var1, int var2, int var3, int var4) {
      return this.validTiles.contains(new Point(var3 - var1, var4 - var2));
   }

   public Rectangle getRangeBounds(Point var1) {
      return this.getRangeBounds(var1.x, var1.y);
   }

   public Rectangle getRangeBounds(int var1, int var2) {
      return GameUtils.rangeBounds(var1 * 32 + 16, var2 * 32 + 16, (this.maxRange + 1) * 32);
   }

   public Iterable<Point> getValidTiles(int var1, int var2) {
      return () -> {
         return GameUtils.mapIterator(this.validTiles.iterator(), (var2x) -> {
            return new Point(var1 + var2x.x, var2 + var2x.y);
         });
      };
   }

   public Stream<Point> streamValidTiles(int var1, int var2) {
      return this.validTiles.stream().map((var2x) -> {
         return new Point(var1 + var2x.x, var2 + var2x.y);
      });
   }

   public SharedTextureDrawOptions getDrawOptions(Color var1, Color var2, int var3, int var4, GameCamera var5) {
      return this.getDrawOptions(var1, var2, var5.getTileDrawX(var3), var5.getTileDrawY(var4), var5.getWidth(), var5.getHeight());
   }

   public SharedTextureDrawOptions getDrawOptions(Color var1, Color var2, int var3, int var4, int var5, int var6) {
      if (this.drawData == null) {
         this.generateDrawData();
      }

      SharedTextureDrawOptions var7 = new SharedTextureDrawOptions(Screen.getQuadTexture());
      Rectangle var8 = new Rectangle(var5, var6);
      if (var8.intersects((double)(this.drawBounds.x + var3), (double)(this.drawBounds.y + var4), (double)this.drawBounds.width, (double)this.drawBounds.height)) {
         Iterator var9 = this.drawData.iterator();

         while(var9.hasNext()) {
            DrawData var10 = (DrawData)var9.next();
            int var11 = var10.drawOffsetX + var3;
            int var12 = var10.drawOffsetY + var4;
            if (var8.intersects((double)var11, (double)var12, 32.0, 32.0)) {
               Zoning.addDrawOptions(var7, var11, var12, var10.adj, var1, var2);
            }
         }
      }

      return var7;
   }

   private static class DrawData {
      public final int drawOffsetX;
      public final int drawOffsetY;
      public final boolean[] adj;

      public DrawData(int var1, int var2, boolean[] var3) {
         this.drawOffsetX = var1;
         this.drawOffsetY = var2;
         this.adj = var3;
      }
   }
}
