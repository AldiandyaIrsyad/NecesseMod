package necesse.entity.particle;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.function.Function;
import necesse.engine.Settings;
import necesse.entity.Entity;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public abstract class Particle extends Entity {
   public float dx;
   public float dy;
   public long lifeTime;
   public long spawnTime;
   public float friction;
   public boolean hasCollision;
   public Rectangle collision;

   public Particle(Level var1, float var2, float var3, long var4) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.dx = 0.0F;
      this.dy = 0.0F;
      this.lifeTime = var4;
      this.refreshSpawnTime();
      this.friction = 0.0F;
      this.hasCollision = false;
      this.collision = new Rectangle(0, 0);
   }

   public Particle(Level var1, float var2, float var3, float var4, float var5, long var6) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.dx = var4;
      this.dy = var5;
      this.lifeTime = var6;
      this.refreshSpawnTime();
      this.friction = 0.0F;
      this.hasCollision = false;
      this.collision = new Rectangle(0, 0);
   }

   public void init() {
      super.init();
   }

   public void clientTick() {
      if (this.lifeTime + this.spawnTime < this.getWorldEntity().getLocalTime()) {
         this.remove();
      }

   }

   public void serverTick() {
   }

   public void refreshSpawnTime() {
      this.spawnTime = this.getWorldEntity().getLocalTime();
   }

   public void tickMovement(float var1) {
      if (!this.removed() && (this.dx != 0.0F || this.dy != 0.0F)) {
         this.calcMovement(var1);
         if (this.hasCollision) {
            this.moveX(var1);
            if (this.getLevel().collides((Shape)this.getCollision(), (CollisionFilter)(new CollisionFilter()).projectileCollision())) {
               this.moveX(-var1);
               this.dx = 0.0F;
            }

            this.moveY(var1);
            if (this.getLevel().collides((Shape)this.getCollision(), (CollisionFilter)(new CollisionFilter()).projectileCollision())) {
               this.moveY(-var1);
               this.dy = 0.0F;
            }
         } else {
            this.moveX(var1);
            this.moveY(var1);
         }

      }
   }

   public Rectangle getCollision() {
      return new Rectangle((int)((double)this.x + this.collision.getX()), (int)((double)this.y + this.collision.getY()), (int)this.collision.getWidth(), (int)this.collision.getHeight());
   }

   public void calcMovement(float var1) {
      if (this.dx != 0.0F || this.dy != 0.0F) {
         if (this.friction != 0.0F) {
            this.dx += (0.0F - this.friction * this.dx) * var1 / 250.0F;
            this.dy += (0.0F - this.friction * this.dy) * var1 / 250.0F;
         }

      }
   }

   public void moveX(float var1) {
      this.x += this.dx * var1 / 250.0F;
   }

   public void moveY(float var1) {
      this.y += this.dy * var1 / 250.0F;
   }

   public float getLifeCyclePercent() {
      float var1 = (float)this.getLifeCycleTime() / (float)this.lifeTime;
      if (var1 >= 1.0F) {
         this.remove();
      }

      return Math.min(var1, 1.0F);
   }

   public long getLifeCycleTime() {
      return this.getWorldEntity().getLocalTime() - this.spawnTime;
   }

   public long getRemainingLifeTime() {
      return this.lifeTime - this.getLifeCycleTime();
   }

   public static enum GType {
      COSMETIC((var0) -> {
         return Settings.particles.ordinal() >= Settings.ParticleSetting.Maximum.ordinal();
      }),
      IMPORTANT_COSMETIC((var0) -> {
         return Settings.particles.ordinal() >= Settings.ParticleSetting.Decreased.ordinal();
      }),
      CRITICAL((var0) -> {
         return true;
      });

      private final Function<Level, Boolean> canAdd;

      private GType(Function var3) {
         this.canAdd = var3;
      }

      public boolean canAdd(Level var1) {
         return (Boolean)this.canAdd.apply(var1);
      }

      // $FF: synthetic method
      private static GType[] $values() {
         return new GType[]{COSMETIC, IMPORTANT_COSMETIC, CRITICAL};
      }
   }
}
