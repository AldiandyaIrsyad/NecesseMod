package necesse.entity;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class ParticleBeamHandler {
   public ParticleTypeSwitcher pTypeSwitcher;
   public int startThickness;
   public int endThickness;
   public float particleThicknessMod;
   public Supplier<Color> trailColor;
   public Supplier<Color> particleColor;
   public float particleSpeed;
   public float distPerParticle;
   public float height;
   private Level level;
   private Trail trail;
   private GameLinkedList<GameLinkedList<BeamParticle>> particles;

   public ParticleBeamHandler(Level var1) {
      this.pTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC});
      this.startThickness = 10;
      this.endThickness = 0;
      this.particleThicknessMod = 0.5F;
      this.trailColor = () -> {
         return Color.WHITE;
      };
      this.particleColor = () -> {
         return Color.WHITE;
      };
      this.particleSpeed = 50.0F;
      this.distPerParticle = 14.0F;
      this.height = 18.0F;
      this.level = var1;
      this.trail = new Trail(new TrailVector(0.0F, 0.0F, 1.0F, 0.0F, (float)this.startThickness, this.height), var1, (Color)null, 10000) {
         public Color getColor() {
            return (Color)ParticleBeamHandler.this.trailColor.get();
         }
      };
      var1.entityManager.addTrail(this.trail);
      this.particles = new GameLinkedList();
      this.particles.addLast(new GameLinkedList());
   }

   public ParticleBeamHandler sprite(GameSprite var1) {
      this.trail.sprite = var1;
      return this;
   }

   public ParticleBeamHandler particleColor(Supplier<Color> var1) {
      this.particleColor = var1;
      return this;
   }

   public ParticleBeamHandler particleColor(Color var1) {
      return this.particleColor(() -> {
         return var1;
      });
   }

   public ParticleBeamHandler trailColor(Supplier<Color> var1) {
      this.trailColor = var1;
      return this;
   }

   public ParticleBeamHandler trailColor(Color var1) {
      return this.trailColor(() -> {
         return var1;
      });
   }

   public ParticleBeamHandler color(Supplier<Color> var1) {
      this.particleColor(var1);
      this.trailColor(var1);
      return this;
   }

   public ParticleBeamHandler color(Color var1) {
      this.particleColor(var1);
      this.trailColor(var1);
      return this;
   }

   public ParticleBeamHandler thickness(int var1, int var2) {
      this.startThickness = var1;
      this.endThickness = var2;
      return this;
   }

   public ParticleBeamHandler particleThicknessMod(float var1) {
      this.particleThicknessMod = var1;
      return this;
   }

   public ParticleBeamHandler speed(float var1) {
      this.particleSpeed = var1;
      return this;
   }

   public ParticleBeamHandler distPerParticle(float var1) {
      this.distPerParticle = var1;
      return this;
   }

   public ParticleBeamHandler height(float var1) {
      this.height = var1;
      return this;
   }

   public void update(RayLinkedList<LevelObjectHit> var1, float var2) {
      GameLinkedList.Element var3 = var1.getFirstElement();
      GameLinkedList.Element var4 = this.particles.getFirstElement();
      double var5 = var1.totalDist;
      double var7 = 0.0;
      boolean var9 = true;

      while(true) {
         int var10 = this.endThickness + (int)((double)(this.startThickness - this.endThickness) * Math.abs(var7 / var5 - 1.0));
         Ray var11 = (Ray)var3.object;
         TrailVector var12 = new TrailVector((float)var11.getX1(), (float)var11.getY1(), (float)(var11.getX2() - var11.getX1()), (float)(var11.getY2() - var11.getY1()), (float)var10, this.height);
         if (var9) {
            this.trail.reset(var12);
            var9 = false;
         } else {
            this.trail.addPoint(var12, true, 0);
         }

         var7 += var11.dist;
         int var13 = this.endThickness + (int)((double)(this.startThickness - this.endThickness) * Math.abs(var7 / var5 - 1.0));
         TrailVector var14 = new TrailVector((float)var11.getX2(), (float)var11.getY2(), (float)(var11.getX2() - var11.getX1()), (float)(var11.getY2() - var11.getY1()), (float)var13, this.height);
         this.trail.addPoint(var14, true, 0);
         this.refreshParticles(var3, var4, var2);
         if (this.level.tickManager().isGameTick()) {
            this.spawnRayParticles(var3, (GameLinkedList)var4.object, var10, var13);
         }

         if (((Ray)var3.object).targetHit != null && this.level.tickManager().isGameTick()) {
            this.spawnCollisionEndParticles(var11);
         }

         if (!var3.hasNext()) {
            if (this.level.tickManager().isGameTick()) {
               this.spawnEndParticles(var11);
            }

            while(var4.hasNext()) {
               GameLinkedList.Element var15 = var4.next();
               Iterator var16 = ((GameLinkedList)var15.object).iterator();

               while(var16.hasNext()) {
                  BeamParticle var17 = (BeamParticle)var16.next();
                  var17.option.remove();
               }

               var4.remove();
               var4 = var15;
            }

            return;
         }

         var3 = var3.next();
         if (!var4.hasNext()) {
            var4 = var4.insertAfter(new GameLinkedList());
         } else {
            var4 = var4.next();
         }
      }
   }

   protected void spawnCollisionEndParticles(Ray<LevelObjectHit> var1) {
      if (this.particleColor != null) {
         for(int var2 = 0; var2 < 3; ++var2) {
            Color var3 = (Color)this.particleColor.get();
            this.level.lightManager.refreshParticleLightFloat((float)var1.getX2(), (float)var1.getY2(), var3, 1.0F);
            this.level.entityManager.addParticle((float)var1.getX2() + GameRandom.globalRandom.floatGaussian() * 4.0F, (float)var1.getY2() + GameRandom.globalRandom.floatGaussian() * 4.0F, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 8.0F, GameRandom.globalRandom.floatGaussian() * 8.0F).lifeTime(750).color(var3).height(this.height);
         }
      }

   }

   protected void spawnEndParticles(Ray<LevelObjectHit> var1) {
      if (this.particleColor != null) {
         for(int var2 = 0; var2 < 2; ++var2) {
            this.level.entityManager.addParticle((float)var1.getX2() + GameRandom.globalRandom.floatGaussian() * 4.0F, (float)var1.getY2() + GameRandom.globalRandom.floatGaussian() * 4.0F, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 15.0F, GameRandom.globalRandom.floatGaussian() * 15.0F).lifeTime(500).color((Color)this.particleColor.get()).height(this.height);
         }
      }

   }

   protected void refreshParticles(GameLinkedList<Ray<LevelObjectHit>>.Element var1, GameLinkedList<GameLinkedList<BeamParticle>>.Element var2, float var3) {
      Iterator var4 = ((GameLinkedList)var2.object).iterator();

      while(var4.hasNext()) {
         BeamParticle var5 = (BeamParticle)var4.next();
         var5.increaseDist(var1, var3);
      }

      var4 = ((GameLinkedList)var2.object).elementIterator();

      while(true) {
         while(var4.hasNext()) {
            GameLinkedList.Element var7 = (GameLinkedList.Element)var4.next();
            BeamParticle var6 = (BeamParticle)var7.object;
            if (!var7.isRemoved() && !var6.option.isRemoved() && !(Math.abs(var6.dist) > ((Ray)var1.object).dist)) {
               var6.updatePos(var7, var1, var2);
            } else {
               var6.option.remove();
               var7.remove();
            }
         }

         return;
      }
   }

   protected void spawnRayParticles(GameLinkedList<Ray<LevelObjectHit>>.Element var1, GameLinkedList<BeamParticle> var2, int var3, int var4) {
      if (this.particleColor != null) {
         Ray var5 = (Ray)var1.object;
         double var6 = var5.getP1().distance(var5.getP2());

         for(double var8 = 0.0; var8 < var6 - (double)this.distPerParticle; var8 += (double)this.distPerParticle) {
            double var10 = var5.getX2() - var5.getX1();
            double var12 = var5.getY2() - var5.getY1();
            double var14 = GameRandom.globalRandom.getDoubleBetween(var8 / var6, (var8 + (double)this.distPerParticle) / var6);
            double var16 = (double)(var3 - var4) * Math.abs(var14 - 1.0);
            float var18 = (float)(var5.getX1() + var10 * var14);
            float var19 = (float)(var5.getY1() + var12 * var14);
            Color var20 = (Color)this.particleColor.get();
            this.level.lightManager.refreshParticleLightFloat(var18, var19, var20, 1.0F);
            Point2D.Double var21 = GameMath.normalize(var10, var12);
            Point2D.Double var22 = GameMath.getPerpendicularDir(var21.x, var21.y);
            double var23 = (double)GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F) * var16 / 2.0 * (double)this.particleThicknessMod;
            float var25 = (float)(var22.x * var23);
            float var26 = (float)(var22.y * var23);
            ParticleOption var27 = this.level.entityManager.addParticle(var18 + var25, var19 + var26, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 5.0F, GameRandom.globalRandom.floatGaussian() * 5.0F).sizeFades(16, 24).lifeTime(250).color(var20).height(this.height);
            float var28 = (Float)GameRandom.globalRandom.getOneOf((Object[])(1.0F, -1.0F)) * this.particleSpeed;
            var2.addLast(new BeamParticle(var14, var28, var27, var25, var26));
         }
      }

   }

   public void dispose() {
      this.trail.remove();
   }

   private static class BeamParticle {
      public double dist;
      public float speed;
      public float lastX;
      public float lastY;
      public float movedX;
      public float movedY;
      public ParticleOption option;

      public BeamParticle(double var1, float var3, ParticleOption var4, float var5, float var6) {
         this.dist = var1;
         this.speed = var3;
         this.option = var4;
         this.movedX = var5;
         this.movedY = var6;
         Point2D.Float var7 = var4.getPos();
         this.lastX = var7.x;
         this.lastY = var7.y;
      }

      public void increaseDist(GameLinkedList<Ray<LevelObjectHit>>.Element var1, float var2) {
         this.dist += (double)(this.speed * var2 / 250.0F) / ((Ray)var1.object).dist;
      }

      private void updatePos(GameLinkedList<BeamParticle>.Element var1, GameLinkedList<Ray<LevelObjectHit>>.Element var2, GameLinkedList<GameLinkedList<BeamParticle>>.Element var3) {
         Ray var4 = (Ray)var2.object;
         GameLinkedList.Element var5 = null;
         GameLinkedList.Element var6 = null;
         byte var7 = 0;
         if (this.dist < 0.0) {
            var7 = -1;
            var5 = var2.prevWrap();
            var6 = var3.prevWrap();
         } else if (this.dist > 1.0) {
            var7 = 1;
            var5 = var2.nextWrap();
            var6 = var3.nextWrap();
         }

         if (var7 != 0) {
            if (var5 != null) {
               this.dist -= (double)var7;
               var1.remove();
               GameLinkedList.Element var13 = ((GameLinkedList)var6.object).addLast((BeamParticle)var1.object);
               this.updatePos(var13, var5, var6);
               return;
            }

            this.option.remove();
         }

         double var8 = var4.getX2() - var4.getX1();
         double var10 = var4.getY2() - var4.getY1();
         Point2D.Float var12 = this.option.getPos();
         this.movedX += var12.x - this.lastX;
         this.movedY += var12.y - this.lastY;
         this.lastX = (float)(var4.getX1() + var8 * this.dist + (double)this.movedX);
         this.lastY = (float)(var4.getY1() + var10 * this.dist + (double)this.movedY);
         this.option.changePos(this.lastX, this.lastY);
      }
   }
}
