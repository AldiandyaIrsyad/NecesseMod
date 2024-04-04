package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class Trail {
   public final Level level;
   public final float thickness;
   public final int fadeTime;
   TrailPointList points;
   TrailPointList.TrailPoint nextPoint;
   protected ArrayList<TrailDrawSection> sections;
   public Color col;
   boolean removed;
   public boolean removeOnFadeOut;
   public boolean drawOnTop;
   public boolean smoothCorners;
   public GameSprite sprite;

   public Trail(TrailVector var1, Level var2, Color var3, int var4) {
      this.level = var2;
      this.col = var3;
      this.thickness = var1.thickness;
      this.fadeTime = var4;
      this.removeOnFadeOut = true;
      this.sprite = new GameSprite(GameResources.chains, 2, 0, 32);
      this.reset(var1);
   }

   public Trail(Projectile var1, Level var2, Color var3, float var4, int var5, float var6) {
      this(new TrailVector(var1.x, var1.y, var1.dx, var1.dy, var4, var6), var2, var3, var5);
   }

   public Trail(Mob var1, Level var2, Color var3, float var4, int var5, float var6) {
      this(new TrailVector(var1.x, var1.y, var1.dx, var1.dy, var4, var6), var2, var3, var5);
   }

   public synchronized void reset(TrailVector var1) {
      this.points = new TrailPointList(this);
      this.points.add(var1, this.level.getWorldEntity().getLocalTime());
      this.sections = new ArrayList();
      this.sections.add(new TrailDrawSection(this, (int)(var1.pos.y / 32.0F), 0, 0));
   }

   public void remove() {
      this.removed = true;
   }

   public boolean isRemoved() {
      return this.removeOnFadeOut && this.points.getLastPoint().spawnTime + (long)this.fadeTime < this.level.getWorldEntity().getLocalTime() ? true : this.removed;
   }

   public void addBreakPoint(TrailVector var1) {
      this.addBreakPoint(var1, 1);
   }

   public void addBreakPoint(TrailVector var1, int var2) {
      this.addPoint(var1, true, var2);
   }

   public void addPoint(TrailVector var1) {
      this.addPoint(var1, 1);
   }

   public void addPoint(TrailVector var1, int var2) {
      this.addPoint(var1, false, var2);
   }

   public synchronized void addPoint(TrailVector var1, boolean var2, int var3) {
      if (!var2 && var1.pos.distance(this.points.getLastPoint().vector.pos) < 8.0) {
         this.nextPoint = this.points.getNextPoint(var1, this.level.getWorldEntity().getLocalTime());
      } else {
         this.addPoints(var3, var1);
      }
   }

   public synchronized void addPoints(int var1, TrailVector... var2) {
      this.nextPoint = null;
      ArrayList var3 = new ArrayList();
      var3.add(this.points.getLastPoint().vector);
      var3.addAll(Arrays.asList(var2));
      var3 = smooth(var3, var1);
      this.addSortPoints(var3);
      this.addMidPoints(var3);

      for(int var4 = 1; var4 < var3.size(); ++var4) {
         TrailVector var5 = (TrailVector)var3.get(var4);
         int var6 = this.points.size();
         this.points.add(var5, this.level.getWorldEntity().getLocalTime());
         ((TrailDrawSection)this.sections.get(this.sections.size() - 1)).pointsEndIndex = var6;
         if ((int)var5.pos.y / 32 != ((TrailDrawSection)this.sections.get(this.sections.size() - 1)).yTile) {
            this.sections.add(new TrailDrawSection(this, (int)var5.pos.y / 32, var6, var6));
         }
      }

   }

   protected void addSortPoints(ArrayList<TrailVector> var1) {
      for(int var2 = 1; var2 < var1.size(); ++var2) {
         TrailVector var3 = (TrailVector)var1.get(var2 - 1);
         TrailVector var4 = (TrailVector)var1.get(var2);
         int var5 = (int)(var3.pos.y / 32.0F);
         int var6 = (int)(var4.pos.y / 32.0F);
         if (var5 != var6) {
            int var7 = var5 < var6 ? 1 : -1;

            for(int var8 = var5 + var7; var8 != var6; var8 += var7) {
               int var9 = var8 * 32;
               float var10 = getXPos(var3.pos, var4.pos, (float)var9);
               float var11 = var3.thickness;
               float var12;
               if (var3.thickness != var4.thickness) {
                  var12 = ((float)var9 - var3.pos.y) / (var4.pos.y - var3.pos.y);
                  var11 = var3.thickness + (var4.thickness - var3.thickness) * var12;
               }

               var12 = var3.height;
               if (var3.height != var4.height) {
                  float var13 = ((float)var9 - var3.pos.y) / (var4.pos.y - var3.pos.y);
                  var12 = var3.height + (var4.height - var3.height) * var13;
               }

               var1.add(var2, new TrailVector(var10, (float)var9, var3.dx, var3.dy, var11, var12));
               ++var2;
            }
         }
      }

   }

   protected void addMidPoints(ArrayList<TrailVector> var1) {
      for(int var2 = 1; var2 < var1.size(); ++var2) {
         TrailVector var3 = (TrailVector)var1.get(var2 - 1);
         TrailVector var4 = (TrailVector)var1.get(var2);
         int var5 = (int)(var3.pos.x / 32.0F);
         int var6 = (int)(var4.pos.x / 32.0F);
         if (var5 != var6) {
            int var7 = var5 < var6 ? 1 : -1;

            for(int var8 = var5 + var7; var8 != var6; var8 += var7) {
               int var9 = var8 * 32;
               float var10 = getYPos(var3.pos, var4.pos, (float)var9);
               float var11 = var3.thickness;
               float var12;
               if (var3.thickness != var4.thickness) {
                  var12 = ((float)var9 - var3.pos.x) / (var4.pos.x - var3.pos.x);
                  var11 = var3.thickness + (var4.thickness - var3.thickness) * var12;
               }

               var12 = var3.height;
               if (var3.height != var4.height) {
                  float var13 = ((float)var9 - var3.pos.x) / (var4.pos.x - var3.pos.x);
                  var12 = var3.height + (var4.height - var3.height) * var13;
               }

               var1.add(var2, new TrailVector((float)var9, var10, var3.dx, var3.dy, var11, var12));
               ++var2;
            }
         }
      }

   }

   protected synchronized void clearFadedSections() {
      int var1;
      for(var1 = 0; this.points.size() > 2 && this.points.get(0).spawnTime + (long)this.fadeTime < this.level.getWorldEntity().getLocalTime(); ++var1) {
         this.points.removeFirst();
      }

      if (var1 > 0) {
         for(int var2 = 0; var2 < this.sections.size(); ++var2) {
            TrailDrawSection var3 = (TrailDrawSection)this.sections.get(var2);
            var3.pointsStartIndex = Math.max(0, var3.pointsStartIndex - var1);
            var3.pointsEndIndex -= var1;
            if (var3.pointsEndIndex < 0) {
               this.sections.remove(0);
               --var2;
            }
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, int var2, int var3, TickManager var4, GameCamera var5) {
      ArrayList var6;
      TrailPointList var7;
      TrailPointList.TrailPoint var8;
      synchronized(this) {
         var6 = new ArrayList(this.sections);
         var7 = this.points.copy();
         var8 = this.nextPoint;
      }

      this.clearFadedSections();
      Iterator var9 = var6.iterator();

      while(var9.hasNext()) {
         final TrailDrawSection var10 = (TrailDrawSection)var9.next();
         final DrawOptions var11 = this.getDrawSection(var10, var5);
         var1.add(new LevelSortedDrawable(this) {
            public int getSortY() {
               return var10.yTile * 32;
            }

            public void draw(TickManager var1) {
               DrawOptions var10002 = var11;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "trailDraw", (Runnable)(var10002::draw));
            }
         });
      }

      if (var8 != null && (int)var8.vector.pos.y / 32 >= var2 && var8.vector.pos.y / 32.0F <= (float)var3) {
         TrailPointList var17 = var7.getNextPointSection(var8);
         float var15 = (float)(this.level.getWorldEntity().getLocalTime() - var8.spawnTime);
         float var16 = 1.0F - var15 / (float)this.fadeTime;
         final TrailDrawSection var12 = (TrailDrawSection)var6.get(var6.size() - 1);
         final DrawOptions var13 = this.getDrawNextSection(var12, var17, var16, var5);
         var1.add(new LevelSortedDrawable(this) {
            public int getSortY() {
               return var12.yTile * 32;
            }

            public void draw(TickManager var1) {
               DrawOptions var10002 = var13;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "trailDraw", (Runnable)(var10002::draw));
            }
         });
      }

   }

   public Color getColor() {
      return this.col;
   }

   protected synchronized DrawOptions getDrawSection(TrailDrawSection var1, GameCamera var2) {
      Color var3 = this.getColor();
      return var1.getSpriteTrailsDraw(this.sprite, var2, TrailDrawSection.fadeLightColorSetter(this.fadeTime, this.level, var3));
   }

   protected synchronized DrawOptions getDrawNextSection(TrailDrawSection var1, TrailPointList var2, float var3, GameCamera var4) {
      Color var5 = this.getColor();
      return TrailDrawSection.getSpriteTrailsDraw(this.sprite, var2, 0, var2.size() - 1, var4, TrailDrawSection.lightColorSetter(this.level, new Color(var5.getRed(), var5.getGreen(), var5.getBlue(), (int)(GameMath.limit(var3, 0.0F, 1.0F) * 255.0F))));
   }

   public static ArrayList<TrailVector> smooth(ArrayList<TrailVector> var0, int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         var0 = smooth(var0);
      }

      return var0;
   }

   public static ArrayList<TrailVector> smooth(ArrayList<TrailVector> var0) {
      ArrayList var1 = new ArrayList();
      var1.ensureCapacity(var0.size() * 2);
      var1.add((TrailVector)var0.get(0));

      for(int var2 = 1; var2 < var0.size(); ++var2) {
         TrailVector var3 = (TrailVector)var0.get(var2 - 1);
         TrailVector var4 = (TrailVector)var0.get(var2);
         Point2D.Float var5 = new Point2D.Float(0.75F * var3.pos.x + 0.25F * var4.pos.x, 0.75F * var3.pos.y + 0.25F * var4.pos.y);
         float var6 = 0.75F * var3.thickness + 0.25F * var4.thickness;
         float var7 = 0.75F * var3.height + 0.25F * var4.height;
         Point2D.Float var8 = new Point2D.Float(0.25F * var3.pos.x + 0.75F * var4.pos.x, 0.25F * var3.pos.y + 0.75F * var4.pos.y);
         float var9 = 0.25F * var3.thickness + 0.75F * var4.thickness;
         float var10 = 0.25F * var3.height + 0.75F * var4.height;
         Point2D.Float var11 = GameMath.normalize(var8.x - var5.x, var8.y - var5.y);
         Point2D.Float var12 = GameMath.normalize(var8.x - var5.x, var8.y - var5.y);
         var1.add(new TrailVector(var5, var11.x, var11.y, var6, var7));
         var1.add(new TrailVector(var8, var12.x, var12.y, var9, var10));
      }

      var1.add((TrailVector)var0.get(var0.size() - 1));
      return var1;
   }

   public static float getXPos(Point2D.Float var0, Point2D.Float var1, float var2) {
      float var3 = var1.x - var0.x;
      float var4 = var1.y - var0.y;
      if (var3 != 0.0F && var4 != 0.0F) {
         float var5 = var4 / var3;
         return (var2 - var0.y + var5 * var0.x) / var5;
      } else {
         return var0.x;
      }
   }

   public static float getYPos(Point2D.Float var0, Point2D.Float var1, float var2) {
      float var3 = var1.x - var0.x;
      float var4 = var1.y - var0.y;
      if (var3 != 0.0F && var4 != 0.0F) {
         float var5 = var3 / var4;
         return (var2 - var0.x + var5 * var0.y) / var5;
      } else {
         return var0.y;
      }
   }
}
