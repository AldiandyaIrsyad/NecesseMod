package necesse.entity.particle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.Entity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ParticleOption {
   protected boolean removed;
   protected int lifeTime = 500;
   protected Point2D.Float pos;
   protected float currentHeight;
   protected Supplier<Point2D.Float> snapPosition;
   protected Mover mover = null;
   protected int lightLevel;
   protected float lightHue;
   protected float lightSat;
   protected SpriteSelector sprite = (var0, var1x, var2x, var3) -> {
      return var0.add(GameResources.particles.sprite(0, 1, 8));
   };
   protected HeightGetter height = null;
   protected FloatGetter rotation = (var0, var1x, var2x) -> {
      return 0.0F;
   };
   protected DrawModifier size = (var0, var1x, var2x, var3) -> {
   };
   protected DrawModifier color = (var0, var1x, var2x, var3) -> {
   };
   protected int minDrawLightLevel;
   protected LinkedList<DrawModifier> extraModifiers = new LinkedList();
   protected GameLinkedList<ProgressEvent> progressEvents = new GameLinkedList();
   protected GameLinkedList<TickEvent> tickEvents = new GameLinkedList();
   public static float defaultFlameHue = 43.0F;
   public static float defaultSmokeHue = 0.0F;

   public static ParticleOption base(float var0, float var1) {
      return new ParticleOption(var0, var1);
   }

   public static ParticleOption standard(float var0, float var1) {
      return base(var0, var1).sizeFades().rotates();
   }

   protected ParticleOption(float var1, float var2) {
      this.pos = new Point2D.Float(var1, var2);
   }

   public ParticleOption changePos(float var1, float var2) {
      this.pos.x = var1;
      this.pos.y = var2;
      return this;
   }

   public Point2D.Float getPos() {
      return this.pos;
   }

   public Point2D.Float getLevelPos() {
      if (this.snapPosition != null) {
         Point2D.Float var1 = (Point2D.Float)this.snapPosition.get();
         if (var1 != null) {
            return new Point2D.Float(this.pos.x + var1.x, this.pos.y + var1.y);
         }
      }

      return new Point2D.Float(this.pos.x, this.pos.y);
   }

   public ParticleOption snapPosition(Supplier<Point2D.Float> var1) {
      this.snapPosition = var1;
      return this;
   }

   public ParticleOption snapPosition(Entity var1) {
      return this.snapPosition(() -> {
         return new Point2D.Float(var1.x, var1.y);
      });
   }

   public ParticleOption moves(Mover var1) {
      this.mover = var1;
      return this;
   }

   public ParticleOption movesFriction(float var1, float var2, float var3) {
      return this.moves(new FrictionMover(var1, var2, var3));
   }

   public ParticleOption movesFrictionAngle(float var1, float var2, float var3) {
      Point2D.Float var4 = GameMath.getAngleDir(var1);
      return this.movesFriction(var4.x * var2, var4.y * var2, var3);
   }

   public ParticleOption movesConstant(float var1, float var2) {
      return this.movesFriction(var1, var2, 0.0F);
   }

   public ParticleOption movesConstantAngle(float var1, float var2) {
      Point2D.Float var3 = GameMath.getAngleDir(var1);
      return this.movesConstant(var3.x * var2, var3.y * var2);
   }

   public ParticleOption lifeTime(int var1) {
      this.lifeTime = var1;
      return this;
   }

   public ParticleOption givesLight(int var1) {
      this.lightLevel = var1;
      return this;
   }

   public ParticleOption givesLight(boolean var1) {
      return var1 ? this.givesLight(100) : this.givesLight(this.lightLevel);
   }

   public ParticleOption givesLight() {
      return this.givesLight(true);
   }

   public ParticleOption givesLight(float var1, float var2) {
      this.lightHue = var1;
      this.lightSat = var2;
      return this.givesLight(true);
   }

   public ParticleOption height(HeightGetter var1) {
      this.height = var1;
      return this;
   }

   public ParticleOption heightMoves(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.height((HeightGetter)(new HeightMover(var1, var2, var3, var4, var5, var6)));
   }

   public ParticleOption height(FloatGetter var1) {
      return this.height((var1x, var2, var3, var4) -> {
         return var1.get(var2, var3, var4);
      });
   }

   public ParticleOption height(float var1) {
      return this.height((var1x, var2, var3) -> {
         return var1;
      });
   }

   public ParticleOption heightMoves(float var1, float var2) {
      float var3 = var2 - var1;
      return this.height((var2x, var3x, var4) -> {
         return var1 + var3 * var4;
      });
   }

   public float getCurrentHeight() {
      return this.currentHeight;
   }

   public ParticleOption sprite(SpriteSelector var1) {
      this.sprite = var1;
      return this;
   }

   public ParticleOption sprite(GameTextureSection var1) {
      return this.sprite((var1x, var2, var3, var4) -> {
         return var1x.add(var1);
      });
   }

   public ParticleOption sprite(int var1, int var2) {
      return this.sprite(GameResources.particles.sprite(var1, var2, 8));
   }

   public ParticleOption size(DrawModifier var1) {
      this.size = var1;
      return this;
   }

   public ParticleOption sizeFades(int var1, int var2) {
      int var3 = GameRandom.globalRandom.getIntBetween(var1, var2);
      return this.size((var1x, var2x, var3x, var4) -> {
         int var5 = (int)((float)var3 * Math.abs(var4 - 1.0F));
         var1x.size(var5, var5);
      });
   }

   public ParticleOption sizeFadesInAndOut(int var1, int var2, float var3) {
      int var4 = GameRandom.globalRandom.getIntBetween(var1, var2);
      return this.size((var2x, var3x, var4x, var5) -> {
         int var6;
         if (var5 < var3) {
            double var7 = (double)(var5 / var3);
            var6 = (int)(var7 * (double)var4);
         } else {
            float var10 = 1.0F - var3;
            double var8 = (double)Math.abs((var5 - var10) / var10 - 1.0F);
            var6 = (int)(var8 * (double)var4);
         }

         var2x.size(var6, var6);
      });
   }

   public ParticleOption sizeFadesInAndOut(int var1, int var2, int var3, int var4) {
      int var5 = GameRandom.globalRandom.getIntBetween(var1, var2);
      return this.size((var3x, var4x, var5x, var6) -> {
         int var7 = var5;
         double var8;
         if (var5x < var3) {
            var8 = (double)var5x / (double)var3;
            var7 = (int)(var8 * (double)var5);
         }

         if (var5x > var4x - var4) {
            var8 = Math.abs((double)(var5x + var4 - var4x) / (double)var4 - 1.0);
            var7 = (int)(var8 * (double)var7);
         }

         var3x.size(var7, var7);
      });
   }

   public ParticleOption sizeFades() {
      return this.sizeFades(10, 18);
   }

   public ParticleOption rotation(FloatGetter var1) {
      this.rotation = var1;
      return this;
   }

   public ParticleOption dontRotate() {
      return this.rotation((var0, var1, var2) -> {
         return 0.0F;
      });
   }

   public ParticleOption rotates(float var1, float var2) {
      float var3 = (float)GameRandom.globalRandom.nextInt(360);
      float var4 = var1 + GameRandom.globalRandom.nextFloat() * var2;
      if (GameRandom.globalRandom.nextBoolean()) {
         var4 = -var4;
      }

      return this.rotation((var2x, var3x, var4x) -> {
         return var3 + var4x * var4;
      });
   }

   public ParticleOption rotates() {
      return this.rotates(50.0F, 150.0F);
   }

   public ParticleOption fadesAlpha(float var1, float var2) {
      return this.modify((var2x, var3, var4, var5) -> {
         if (var1 != 0.0F && var5 <= var1) {
            var2x.alpha(var5 / var1);
         } else if (var2 != 0.0F && var5 >= 1.0F - var2) {
            var2x.alpha(Math.abs((var5 - (1.0F - var2)) / var2 - 1.0F));
         }

      });
   }

   public ParticleOption fadesAlphaTime(int var1, int var2) {
      return this.fadesAlphaTimeToCustomAlpha(var1, var2, 1.0F);
   }

   public ParticleOption fadesAlphaTimeToCustomAlpha(int var1, int var2, float var3) {
      return this.modify((var3x, var4, var5, var6) -> {
         float var7 = var3;
         float var8;
         if (var5 < var1) {
            var8 = (float)var5 / (float)var1;
            var7 = var8 * var3;
         }

         if (var5 > var4 - var2) {
            var8 = Math.abs((float)(var5 + var2 - var4) / (float)var2 - 1.0F);
            var7 = var8 * var7;
         }

         var3x.alpha(var7);
      });
   }

   public ParticleOption color(DrawModifier var1) {
      this.color = var1;
      return this;
   }

   public ParticleOption color(float var1, float var2, float var3, float var4) {
      return this.color((var4x, var5, var6, var7) -> {
         var4x.color(var1, var2, var3, var4);
      });
   }

   public ParticleOption color(Color var1) {
      float var2 = (float)var1.getRed() / 255.0F;
      float var3 = (float)var1.getGreen() / 255.0F;
      float var4 = (float)var1.getBlue() / 255.0F;
      float var5 = (float)var1.getAlpha() / 255.0F;
      return this.color(var2, var3, var4, var5);
   }

   public ParticleOption colorRandom(Color var1, float var2, float var3, float var4) {
      return this.color(randomizeColor(var1, var2, var3, var4));
   }

   public ParticleOption colorRandom(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.color(randomizeColor(var1, var2, var3, var4, var5, var6));
   }

   public ParticleOption flameColor(float var1) {
      return this.color(randomFlameColor(var1));
   }

   public ParticleOption flameColor() {
      return this.color(randomFlameColor());
   }

   public ParticleOption smokeColor(float var1) {
      return this.color(randomSmokeColor(var1));
   }

   public ParticleOption smokeColor() {
      return this.color(randomSmokeColor());
   }

   public static Color randomFlameColor(GameRandom var0, float var1) {
      return randomizeColor(var0, var1, 1.0F, 1.0F, 7.0F, 0.1F, 0.1F);
   }

   public static Color randomFlameColor(float var0) {
      return randomFlameColor(GameRandom.globalRandom, var0);
   }

   public static Color randomFlameColor(GameRandom var0) {
      return randomFlameColor(var0, defaultFlameHue);
   }

   public static Color randomFlameColor() {
      return randomFlameColor(defaultFlameHue);
   }

   public static Color randomSmokeColor(GameRandom var0, float var1) {
      return randomizeColor(var0, var1, 0.0F, 0.24F, 0.0F, 0.0F, 0.1F);
   }

   public static Color randomSmokeColor(float var0) {
      return randomSmokeColor(GameRandom.globalRandom, var0);
   }

   public static Color randomSmokeColor(GameRandom var0) {
      return randomSmokeColor(var0, defaultSmokeHue);
   }

   public static Color randomSmokeColor() {
      return randomSmokeColor(defaultSmokeHue);
   }

   public static Color randomizeColor(Color var0, float var1, float var2, float var3) {
      float[] var4 = Color.RGBtoHSB(var0.getRed(), var0.getGreen(), var0.getBlue(), (float[])null);
      return randomizeColor(var4[0], var4[1], var4[2], var1, var2, var3);
   }

   public static Color randomizeColor(float var0, float var1, float var2, float var3, float var4, float var5) {
      return randomizeColor(GameRandom.globalRandom, var0, var1, var2, var3, var4, var5);
   }

   public static Color randomizeColor(GameRandom var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      if (var4 != 0.0F) {
         var1 = (var1 + var0.getFloatOffset(0.0F, var4)) % 360.0F;
      }

      if (var5 != 0.0F) {
         var2 += var0.getFloatOffset(0.0F, var5);
         if (var2 > 1.0F) {
            var2 -= var2 % 1.0F;
         } else if (var2 < 0.0F) {
            var2 -= var2 % 1.0F;
         }
      }

      if (var6 != 0.0F) {
         var3 += var0.getFloatOffset(0.0F, var6);
         if (var3 > 1.0F) {
            var3 -= var3 % 1.0F;
         } else if (var3 < 0.0F) {
            var3 -= var3 % 1.0F;
         }
      }

      return Color.getHSBColor(var1 / 360.0F, var2, var3);
   }

   public ParticleOption minDrawLight(int var1) {
      this.minDrawLightLevel = var1;
      return this;
   }

   public ParticleOption modify(DrawModifier var1) {
      this.extraModifiers.add(var1);
      return this;
   }

   public ParticleOption onProgress(float var1, Consumer<Point2D.Float> var2) {
      ProgressEvent var3 = new ProgressEvent(var1, var2);
      Iterator var4 = this.progressEvents.elements().iterator();

      GameLinkedList.Element var5;
      do {
         if (!var4.hasNext()) {
            this.progressEvents.addLast(var3);
            return this;
         }

         var5 = (GameLinkedList.Element)var4.next();
      } while(!(((ProgressEvent)var5.object).lifeProgress > var1));

      var5.insertBefore(var3);
      return this;
   }

   public ParticleOption onDied(Consumer<Point2D.Float> var1) {
      return this.onProgress(1.0F, var1);
   }

   public ParticleOption onMoveTick(TickEvent var1) {
      this.tickEvents.addLast(var1);
      return this;
   }

   public void remove() {
      this.removed = true;
   }

   public boolean isRemoved() {
      return this.removed;
   }

   protected void tickProgress(float var1) {
      while(true) {
         if (!this.progressEvents.isEmpty()) {
            GameLinkedList.Element var2 = this.progressEvents.getFirstElement();
            if (var1 >= ((ProgressEvent)var2.object).lifeProgress) {
               ((ProgressEvent)var2.object).consumer.accept(this.pos);
               var2.remove();
               continue;
            }
         }

         return;
      }
   }

   public void addDrawOptions(SharedTextureDrawOptions var1, Level var2, int var3, int var4, float var5, GameCamera var6) {
      if (!this.isRemoved()) {
         float var7 = this.pos.x;
         float var8 = this.pos.y;
         if (this.snapPosition != null) {
            Point2D.Float var9 = (Point2D.Float)this.snapPosition.get();
            if (var9 != null) {
               var7 += var9.x;
               var8 += var9.y;
            }
         }

         GameLight var12 = var2.lightManager.getLightLevel((int)(var7 / 32.0F), (int)(var8 / 32.0F));
         SharedTextureDrawOptions.Wrapper var10 = this.sprite.get(var1, var3, var4, var5);
         this.size.modify(var10, var3, var4, var5);
         this.color.modify(var10, var3, var4, var5);
         float var11 = this.rotation.get(var3, var4, var5);
         if (var11 != 0.0F) {
            var10.rotate(var11);
         }

         if (this.minDrawLightLevel > 0) {
            var12 = var12.minLevelCopy((float)this.minDrawLightLevel);
         }

         var10.light(var12);
         this.extraModifiers.forEach((var4x) -> {
            var4x.modify(var10, var3, var4, var5);
         });
         var10.posMiddle(var6.getDrawX(var7), var6.getDrawY(var8 - this.currentHeight));
      }
   }

   public interface Mover {
      void tick(Point2D.Float var1, float var2, int var3, int var4, float var5);
   }

   public interface SpriteSelector {
      SharedTextureDrawOptions.Wrapper get(SharedTextureDrawOptions var1, int var2, int var3, float var4);
   }

   public interface HeightGetter {
      float tick(float var1, int var2, int var3, float var4);
   }

   public interface FloatGetter {
      float get(int var1, int var2, float var3);
   }

   public interface DrawModifier {
      void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4);
   }

   public static class FrictionMover implements Mover {
      public float dx;
      public float dy;
      public float friction;

      public FrictionMover(float var1, float var2, float var3) {
         this.dx = var1;
         this.dy = var2;
         this.friction = var3;
      }

      public void tick(Point2D.Float var1, float var2, int var3, int var4, float var5) {
         float var6 = var2 / 250.0F;
         if (this.friction != 0.0F) {
            this.dx += -this.friction * this.dx * var6;
            this.dy += -this.friction * this.dy * var6;
         }

         var1.x += this.dx * var6;
         var1.y += this.dy * var6;
      }
   }

   public static class HeightMover implements HeightGetter {
      public float currentHeight;
      public float dh;
      public float gravity;
      public float minHeight;
      public float bouncy;
      public float friction;

      public HeightMover(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.currentHeight = var1;
         this.dh = var2;
         this.gravity = var3;
         this.minHeight = var5;
         this.bouncy = var6;
         this.friction = var4;
      }

      public float tick(float var1, int var2, int var3, float var4) {
         float var5 = var1 / 250.0F;
         if (this.friction != 0.0F) {
            this.dh += (-this.gravity * this.friction - this.friction * this.dh) * var5;
         } else {
            this.dh += (this.gravity - this.dh) * var5;
         }

         this.currentHeight += this.dh * var5;
         if (this.currentHeight < this.minHeight) {
            if (this.bouncy > 0.0F && Math.abs(this.dh) > Math.abs(this.gravity / 20.0F)) {
               this.dh = -this.dh * this.bouncy;
               this.currentHeight = this.minHeight + Math.abs(this.currentHeight - this.minHeight);
            } else {
               this.currentHeight = this.minHeight;
               this.dh = 0.0F;
            }
         }

         return this.currentHeight;
      }
   }

   protected static class ProgressEvent {
      public final float lifeProgress;
      public final Consumer<Point2D.Float> consumer;

      public ProgressEvent(float var1, Consumer<Point2D.Float> var2) {
         this.lifeProgress = var1;
         this.consumer = var2;
      }
   }

   public interface TickEvent {
      void tick(float var1, int var2, int var3, float var4);
   }

   public interface SizeGetter {
      Dimension get(int var1, int var2, float var3);
   }

   public static class CollisionMover implements Mover {
      public final Level level;
      public final CollisionFilter collisionFilter;
      public final Mover parentMover;
      public boolean stopped;
      public float minRandomWallPosOffset;
      public float maxRandomWallPosOffset;

      public CollisionMover(Level var1, Mover var2, CollisionFilter var3) {
         this.minRandomWallPosOffset = -10.0F;
         this.maxRandomWallPosOffset = 10.0F;
         this.level = var1;
         this.parentMover = var2;
         this.collisionFilter = var3;
      }

      public CollisionMover(Level var1, Mover var2) {
         this(var1, var2, (new CollisionFilter()).mobCollision());
      }

      public void tick(Point2D.Float var1, float var2, int var3, int var4, float var5) {
         if (!this.stopped) {
            Point2D.Float var6 = new Point2D.Float(var1.x, var1.y);
            this.parentMover.tick(var1, var2, var3, var4, var5);
            Line2D.Float var7 = new Line2D.Float(var6, var1);
            ArrayList var8 = this.level.getCollisions(var7, this.collisionFilter);
            IntersectionPoint var9 = this.level.getCollisionPoint(var8, var7, false);
            if (var9 != null) {
               float var10 = GameRandom.globalRandom.getFloatBetween(this.minRandomWallPosOffset, this.maxRandomWallPosOffset);
               Point2D.Float var11 = GameMath.normalize(var7.x2 - var7.x1, var7.y2 - var7.y1);
               var1.x = (float)var9.x + var11.x * var10;
               var1.y = (float)var9.y + var11.y * var10;
               this.stopped = true;
            }
         }

      }
   }
}
