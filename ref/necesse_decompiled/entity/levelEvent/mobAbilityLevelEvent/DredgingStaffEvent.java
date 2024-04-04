package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.function.Function;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class DredgingStaffEvent extends GroundEffectEvent {
   private static final float pillarSpread = 12.0F;
   protected GameDamage damage;
   protected float resilienceGain;
   public float speed;
   public int distance;
   public int width;
   public int targetX;
   public int targetY;
   protected HashSet<Integer> hits;
   protected final GroundPillarList<DredgePillar> pillars = new GroundPillarList();
   protected float currentDistance;
   protected float lastPillarDist;
   protected int pillarRow;
   protected Point2D.Float dir;

   public DredgingStaffEvent() {
   }

   public DredgingStaffEvent(Mob var1, int var2, int var3, GameRandom var4, int var5, int var6, GameDamage var7, float var8, float var9, int var10, int var11) {
      super(var1, var2, var3, var4);
      this.targetX = var5;
      this.targetY = var6;
      this.damage = var7;
      this.resilienceGain = var8;
      this.speed = var9;
      this.distance = var10;
      this.width = var11;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.speed);
      var1.putNextInt(this.distance);
      var1.putNextInt(this.width);
      var1.putNextInt(this.targetX);
      var1.putNextInt(this.targetY);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.speed = var1.getNextFloat();
      this.distance = var1.getNextInt();
      this.width = var1.getNextInt();
      this.targetX = var1.getNextInt();
      this.targetY = var1.getNextInt();
   }

   public void init() {
      super.init();
      this.hits = new HashSet();
      this.dir = GameMath.normalize((float)(this.targetX - this.x), (float)(this.targetY - this.y));
      if (this.dir.x == 0.0F && this.dir.y == 0.0F) {
         this.dir.x = 1.0F;
      }

      if (this.isClient()) {
         this.level.entityManager.addPillarHandler(new GroundPillarHandler<DredgePillar>(this.pillars) {
            protected boolean canRemove() {
               return DredgingStaffEvent.this.isOver();
            }

            public double getCurrentDistanceMoved() {
               return 0.0;
            }
         });
      }

      this.lastPillarDist = (float)this.distance % 12.0F;
   }

   public Shape getHitBox() {
      return null;
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hits.add(var1.getUniqueID());
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || !this.hits.contains(var1.getUniqueID())) {
         this.hits.add(var1.getUniqueID());
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0F;
         }
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && !this.hits.contains(var1.getUniqueID());
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      float var2 = this.currentDistance;
      this.currentDistance += this.speed * var1 / 250.0F;
      if (this.currentDistance > (float)this.distance) {
         this.currentDistance = (float)this.distance;
         this.over();
      }

      if (this.currentDistance > var2) {
         float var4;
         float var5;
         Point2D.Float var9;
         Point2D.Float var10;
         if (this.isClient()) {
            synchronized(this.pillars) {
               while(true) {
                  if (!(this.lastPillarDist <= this.currentDistance)) {
                     break;
                  }

                  var4 = this.lastPillarDist / (float)this.distance;
                  var5 = (float)this.width * var4 / 2.0F;
                  Point2D.Float var6 = new Point2D.Float((float)this.x + this.dir.x * this.lastPillarDist, (float)this.y + this.dir.y * this.lastPillarDist);
                  float var7;
                  if (this.pillarRow % 2 == 0) {
                     var7 = 12.0F;
                     this.pillars.add(new DredgePillar((int)var6.x, (int)var6.y, (double)this.lastPillarDist, this.level.getWorldEntity().getLocalTime()));
                  } else {
                     var7 = 6.0F;
                  }

                  for(float var8 = var7; var8 < var5; var8 += 12.0F) {
                     var9 = GameMath.getPerpendicularPoint(var6, -var8, this.dir);
                     this.pillars.add(new DredgePillar((int)var9.x, (int)var9.y, (double)this.lastPillarDist, this.level.getWorldEntity().getLocalTime()));
                     var10 = GameMath.getPerpendicularPoint(var6, var8, this.dir);
                     this.pillars.add(new DredgePillar((int)var10.x, (int)var10.y, (double)this.lastPillarDist, this.level.getWorldEntity().getLocalTime()));
                  }

                  ++this.pillarRow;
                  this.lastPillarDist += 12.0F;
               }
            }
         }

         float var3 = var2 / (float)this.distance;
         var4 = this.currentDistance / (float)this.distance;
         var5 = (float)this.width * var3 / 2.0F;
         float var17 = (float)this.width * var4 / 2.0F;
         Point2D.Float var18 = new Point2D.Float((float)this.x + this.dir.x * var2, (float)this.y + this.dir.y * var2);
         Point2D.Float var19 = new Point2D.Float((float)this.x + this.dir.x * this.currentDistance, (float)this.y + this.dir.y * this.currentDistance);
         var9 = GameMath.getPerpendicularPoint(var18, -var5, this.dir);
         var10 = GameMath.getPerpendicularPoint(var18, var5, this.dir);
         Point2D.Float var11 = GameMath.getPerpendicularPoint(var19, -var17, this.dir);
         Point2D.Float var12 = GameMath.getPerpendicularPoint(var19, var17, this.dir);
         int[] var13 = new int[]{(int)var9.x, (int)var10.x, (int)var12.x, (int)var11.x};
         int[] var14 = new int[]{(int)var9.y, (int)var10.y, (int)var12.y, (int)var11.y};
         Polygon var15 = new Polygon(var13, var14, 4);
         this.handleHits(var15, this::canHit, (Function)null);
      }

   }

   protected Point2D.Float getLeftPos(float var1) {
      float var2 = var1 / (float)this.distance;
      Point2D.Float var3 = new Point2D.Float((float)this.x + this.dir.x * var2, (float)this.y + this.dir.y * var2);
      float var4 = (float)this.width * var2;
      return GameMath.getPerpendicularPoint(var3, -var4, this.dir);
   }

   protected Point2D.Float getRightPos(float var1) {
      float var2 = var1 / (float)this.distance;
      Point2D.Float var3 = new Point2D.Float((float)this.x + this.dir.x * var2, (float)this.y + this.dir.y * var2);
      float var4 = (float)this.width * var2;
      return GameMath.getPerpendicularPoint(var3, var4, this.dir);
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public static class DredgePillar extends GroundPillar {
      public GameTextureSection texture;
      public boolean mirror;

      public DredgePillar(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         this.mirror = GameRandom.globalRandom.nextBoolean();
         this.texture = null;
         GameTexture var7 = GameResources.dredgingStaffPillars;
         if (var7 != null) {
            int var8 = var7.getHeight();
            int var9 = GameRandom.globalRandom.nextInt(var7.getWidth() / var8);
            this.texture = (new GameTextureSection(GameResources.dredgingStaffPillars)).sprite(var9, 0, var8);
         }

         this.behaviour = new GroundPillar.TimedBehaviour(200, 100, 200);
      }

      public DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6) {
         GameLight var7 = var1.getLightLevel(this.x / 32, this.y / 32);
         int var8 = var6.getDrawX(this.x);
         int var9 = var6.getDrawY(this.y);
         double var10 = this.getHeight(var2, var4);
         int var12 = (int)(var10 * (double)this.texture.getHeight());
         return this.texture.section(0, this.texture.getWidth(), 0, var12).initDraw().mirror(this.mirror, false).light(var7).pos(var8 - this.texture.getWidth() / 2, var9 - var12);
      }
   }
}
