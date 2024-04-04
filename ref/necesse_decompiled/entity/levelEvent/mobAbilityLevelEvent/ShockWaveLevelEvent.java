package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class ShockWaveLevelEvent extends GroundEffectEvent {
   public int circumferencePerSegment = 30;
   public boolean allowConsecutiveHits = false;
   protected float targetAngle;
   protected float angleExtent;
   protected float expandSpeed;
   protected float maxDistance;
   protected float distancePerHit;
   protected float hitboxWidth;
   protected float hitDistanceOffset;
   protected float currentDistance;
   protected float lastHitDistance;
   protected HashSet<Integer> lastTickHits = new HashSet();
   private LinkedList<HudDrawElement> debugHitboxes = new LinkedList();
   protected boolean drawDebugHitboxes = false;

   public ShockWaveLevelEvent(float var1, float var2, float var3, float var4, float var5) {
      this.angleExtent = var1;
      this.expandSpeed = var2;
      this.maxDistance = var3;
      this.distancePerHit = var4;
      this.hitboxWidth = var5;
   }

   public ShockWaveLevelEvent(Mob var1, int var2, int var3, GameRandom var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      super(var1, var2, var3, var4);
      this.targetAngle = var5;
      this.angleExtent = var6;
      this.expandSpeed = var7;
      this.maxDistance = var8;
      this.distancePerHit = var9;
      this.hitboxWidth = var10;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.targetAngle);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.targetAngle = var1.getNextFloat();
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      float var2 = this.currentDistance;
      this.currentDistance += this.expandSpeed * var1 / 250.0F;
      boolean var3 = false;
      if (this.currentDistance > this.maxDistance) {
         this.currentDistance = this.maxDistance;
         var3 = true;
      }

      if (this.currentDistance > var2) {
         float var4 = this.hitDistanceOffset % this.distancePerHit;
         if (var4 < 0.0F) {
            var4 = this.distancePerHit - var4;
         }

         float var5 = this.currentDistance - var4;

         for(float var6 = this.lastHitDistance - var4 + this.distancePerHit; var6 <= var5; var6 += this.distancePerHit) {
            this.lastHitDistance = var6 + var4;
            if (this.allowConsecutiveHits) {
               this.lastTickHits.clear();
            }

            float var7 = this.angleExtent / 2.0F;
            float var8 = this.targetAngle - var7;
            float var9 = this.targetAngle + var7;
            float var10 = (float)(Math.PI * (double)var6 * 2.0 * (double)this.angleExtent / 360.0);
            float var11 = (float)Math.ceil((double)(var10 / (float)this.circumferencePerSegment));
            float var12 = this.angleExtent / var11;
            float var13 = this.hitboxWidth / 2.0F;

            for(int var14 = 0; (float)var14 < var11; ++var14) {
               float var15 = var8 + (float)var14 * var12;
               float var16 = var15 + var12;
               Point2D.Float var17 = this.getAngledPos(var6 - var13, var15);
               Point2D.Float var18 = this.getAngledPos(var6 + var13, var15);
               Point2D.Float var19 = this.getAngledPos(var6 + var13, var16);
               Point2D.Float var20 = this.getAngledPos(var6 - var13, var16);
               int[] var21 = new int[]{(int)var17.x, (int)var18.x, (int)var19.x, (int)var20.x};
               int[] var22 = new int[]{(int)var17.y, (int)var18.y, (int)var19.y, (int)var20.y};
               final Polygon var23 = new Polygon(var21, var22, 4);
               this.spawnHitboxParticles(var23);
               this.handleHits(var23, this::canHit, (Function)null);
               if (this.drawDebugHitboxes && this.isClient()) {
                  HudDrawElement var24 = new HudDrawElement() {
                     public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
                        var1.add(new SortedDrawable() {
                           public int getPriority() {
                              return -10000;
                           }

                           public void draw(TickManager var1) {
                              Screen.drawShape(var23, var2, false, 1.0F, 0.0F, 0.0F, 1.0F);
                           }
                        });
                     }
                  };
                  this.debugHitboxes.addLast(var24);
                  this.level.hudManager.addElement(var24);
               }
            }

            this.spawnHitboxParticles(var6, var8, var9);
         }
      }

      if (var3) {
         this.over();
      }

   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.lastTickHits.add(var1.getUniqueID());
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || !this.lastTickHits.contains(var1.getUniqueID())) {
         this.lastTickHits.add(var1.getUniqueID());
         this.damageTarget(var1);
      }

   }

   public abstract void damageTarget(Mob var1);

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && !this.lastTickHits.contains(var1.getUniqueID());
   }

   protected Point2D.Float getAngledPos(float var1, float var2) {
      return new Point2D.Float((float)this.x + GameMath.cos(var2) * var1, (float)this.y + GameMath.sin(var2) * var1);
   }

   protected abstract void spawnHitboxParticles(Polygon var1);

   protected abstract void spawnHitboxParticles(float var1, float var2, float var3);

   protected Iterable<Point2D.Float> getPositionsAlongHit(float var1, float var2, float var3, float var4, boolean var5) {
      float var6 = var3 - var2;
      float var7 = (float)(Math.PI * (double)var1 * 2.0 * (double)var6 / 360.0);
      float var8 = var7 / var4;
      int var9 = (int)Math.ceil((double)var8);
      float var10 = var6 / (float)var9;
      float var11 = var9 == 0 ? var6 / 2.0F : 0.0F;
      return () -> {
         final AtomicInteger var7 = new AtomicInteger();
         return new Iterator<Point2D.Float>() {
            public boolean hasNext() {
               return var7.get() < var1 + (var2 ? 1 : 0);
            }

            public Point2D.Float next() {
               int var1x = var7.getAndAdd(1);
               float var2x = var3 + (float)var1x * var4 + var5;
               return ShockWaveLevelEvent.this.getAngledPos(var6, var2x);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Object next() {
               return this.next();
            }
         };
      };
   }

   protected Iterable<Point2D.Float> getPositionsThroughHit(float var1, float var2, float var3, float var4, boolean var5) {
      float var6 = var3 - var2;
      double var7 = Math.PI * (double)var1 * 2.0;
      double var9 = var7 / 360.0;
      float var11 = (float)((double)var4 / var9);
      float var12 = (float)(var9 * (double)var6);
      int var13 = (int)Math.max(Math.ceil((double)(var12 / var4)), 1.0);
      float var14 = var5 ? var6 % var11 / 2.0F : 0.0F;
      return () -> {
         final AtomicInteger var6 = new AtomicInteger();
         return new Iterator<Point2D.Float>() {
            public boolean hasNext() {
               return var6.get() < var1;
            }

            public Point2D.Float next() {
               int var1x = var6.getAndAdd(1);
               float var2x = var2 + (float)var1x * var3 + var4;
               return ShockWaveLevelEvent.this.getAngledPos(var5, var2x);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Object next() {
               return this.next();
            }
         };
      };
   }

   public Shape getHitBox() {
      return null;
   }

   public void over() {
      super.over();
      this.debugHitboxes.forEach(HudDrawElement::remove);
      this.debugHitboxes.clear();
   }
}
