package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.LightningTrail;
import necesse.entity.trails.TrailVector;
import necesse.level.maps.LevelObjectHit;

public class LightningTrailEvent extends MobAbilityLevelEvent implements Attacker {
   private static final int totalPoints = 16;
   private static final int distance = 120;
   private static final float distanceMod = 1.0F;
   private static final int ticksToComplete = 5;
   private int startX;
   private int startY;
   private int targetX;
   private int targetY;
   private float xDir;
   private float yDir;
   private GameDamage damage;
   private float resilienceGain;
   private int seed;
   private int tickCounter;
   private int pointCounter;
   private ArrayList<Point2D.Float> points;
   private ArrayList<Integer> hits;
   private LightningTrail trail;

   public LightningTrailEvent() {
   }

   public LightningTrailEvent(Mob var1, GameDamage var2, float var3, int var4, int var5, int var6, int var7, int var8) {
      super(var1, new GameRandom((long)var8));
      if (var2 != null) {
         this.damage = var2;
      } else {
         this.damage = new GameDamage(0.0F);
      }

      this.resilienceGain = var3;
      this.startX = var4;
      this.startY = var5;
      this.targetX = var6;
      this.targetY = var7;
      this.seed = var8;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startX = var1.getNextInt();
      this.startY = var1.getNextInt();
      this.targetX = var1.getNextInt();
      this.targetY = var1.getNextInt();
      this.seed = var1.getNextInt();
      this.tickCounter = var1.getNextShortUnsigned();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.startX);
      var1.putNextInt(this.startY);
      var1.putNextInt(this.targetX);
      var1.putNextInt(this.targetY);
      var1.putNextInt(this.seed);
      var1.putNextShortUnsigned(this.tickCounter);
   }

   public void init() {
      super.init();
      float var1 = (float)(new Point(this.startX, this.startY)).distance((double)this.targetX, (double)this.targetY);
      this.xDir = (float)(this.targetX - this.startX) / var1;
      this.yDir = (float)(this.targetY - this.startY) / var1;
      this.points = this.generatePoints();
      this.trail = new LightningTrail(new TrailVector((float)this.startX, (float)this.startY, this.xDir, this.yDir, 35.0F, 18.0F), this.level, new Color(50, 0, 102));
      this.trail.addNewPoint(new TrailVector((Point2D.Float)this.points.get(0), this.xDir, this.yDir, this.trail.thickness, 18.0F));
      if (this.isClient()) {
         this.level.entityManager.addTrail(this.trail);
      }

      this.hits = new ArrayList();
   }

   public void clientTick() {
      if (!this.isOver()) {
         ++this.tickCounter;
         int var1 = this.tickCounter * 16 / 5;

         while(this.pointCounter < var1) {
            ++this.pointCounter;
            if (this.pointCounter >= 16) {
               this.over();
               break;
            }

            Point2D.Float var2 = (Point2D.Float)this.points.get(this.pointCounter);
            this.trail.addNewPoint(new TrailVector(var2, this.xDir, this.yDir, this.trail.thickness, 18.0F));
            Point2D.Float var3 = (Point2D.Float)this.points.get(this.pointCounter - 1);
            Point2D.Float var4 = new Point2D.Float((var2.x + var3.x) / 2.0F, (var2.y + var3.y) / 2.0F);
            Point2D.Float var5 = GameMath.normalize(var2.x - var3.x, var2.y - var3.y);
            float var6 = (float)var2.distance(var3);

            int var7;
            for(var7 = 0; var7 < 2; ++var7) {
               this.level.entityManager.addParticle(var4.x + var5.x * GameRandom.globalRandom.nextFloat() * var6, var4.y + var5.y * GameRandom.globalRandom.nextFloat() * var6, Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 4.0), (float)(GameRandom.globalRandom.nextGaussian() * 4.0)).color(this.trail.col).height(18.0F);
            }

            if (this.pointCounter == 15) {
               for(var7 = 0; var7 < 20; ++var7) {
                  this.level.entityManager.addParticle(var3.x + var5.x * 4.0F, var3.y + var5.y * 4.0F, Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 20.0), (float)(GameRandom.globalRandom.nextGaussian() * 20.0)).color(this.trail.col).height(18.0F).lifeTime(250);
               }
            }

            Line2D.Double var9 = new Line2D.Double(var3.getX(), var3.getY(), var2.getX(), var2.getY());
            LineHitbox var8 = new LineHitbox(var9, 20.0F);
            this.handleHits(var8, (var1x) -> {
               return var1x.canBeHit(this) && !this.hasHit(var1x);
            }, (Function)null);
         }

      }
   }

   public void serverTick() {
      if (!this.isOver()) {
         ++this.tickCounter;
         int var1 = this.tickCounter * 16 / 5;

         while(this.pointCounter < var1) {
            ++this.pointCounter;
            if (this.pointCounter >= 16) {
               this.over();
               break;
            }

            Point2D var2 = (Point2D)this.points.get(this.pointCounter - 1);
            Point2D var3 = (Point2D)this.points.get(this.pointCounter);
            Line2D.Double var4 = new Line2D.Double(var2.getX(), var2.getY(), var3.getX(), var3.getY());
            LineHitbox var5 = new LineHitbox(var4, 20.0F);
            this.handleHits(var5, (var1x) -> {
               return !this.hasHit(var1x);
            }, (Function)null);
         }

      }
   }

   private ArrayList<Point2D.Float> generatePoints() {
      ArrayList var1 = new ArrayList();
      GameRandom var2 = new GameRandom((long)this.seed);
      Point2D.Float var3 = new Point2D.Float(-this.yDir, this.xDir);
      float var4 = 0.0F;
      Point2D.Float var5 = new Point2D.Float((float)this.startX, (float)this.startY);
      var1.add(var5);

      for(int var6 = 0; var6 < 16; ++var6) {
         float var7 = (var2.nextFloat() - 0.5F) * var4 * 2.0F;
         var4 = (var2.nextFloat() + 1.0F) * 7.5F;
         var5 = new Point2D.Float(var5.x + this.xDir * var4 - var3.x * var7, var5.y + this.yDir * var4 - var3.y * var7);
         var1.add(var5);
      }

      return var1;
   }

   public void clientHit(Mob var1, Packet var2) {
      super.clientHit(var1, var2);
      this.hits.add(var1.getUniqueID());
   }

   public void serverHit(Mob var1, Packet var2, boolean var3) {
      super.serverHit(var1, var2, var3);
      var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this);
      this.hits.add(var1.getUniqueID());
      if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
         this.owner.addResilience(this.resilienceGain);
         this.resilienceGain = 0.0F;
      }

   }

   public void hit(LevelObjectHit var1) {
      super.hit(var1);
      var1.getLevelObject().attackThrough(this.damage, this);
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("lightning", 2);
   }

   public GameMessage getAttackerName() {
      return (GameMessage)(this.owner != null ? this.owner.getAttackerName() : new LocalMessage("deaths", "unknownatt"));
   }

   public Mob getFirstAttackOwner() {
      return this.owner;
   }

   public boolean hasHit(Mob var1) {
      return this.hits.contains(var1.getUniqueID());
   }
}
