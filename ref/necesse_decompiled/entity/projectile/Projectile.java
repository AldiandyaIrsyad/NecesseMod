package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.packet.PacketProjectilePositionUpdate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ProjectileModifierRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.util.LineHitbox;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ProjectileHitboxMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public abstract class Projectile extends Entity implements Attacker {
   public final IDData idData;
   protected ProjectileModifier modifier;
   public float dx;
   public float dy;
   public float targetX;
   public float targetY;
   public float speed;
   public float traveledDistance;
   public float height;
   public int knockback;
   public int distance;
   public boolean heightBasedOnDistance;
   private GameDamage damage;
   public boolean doesImpactDamage;
   public boolean canBreakObjects;
   public boolean canHitMobs;
   public boolean clientHandlesHit;
   public NetworkClient handlingClient;
   public boolean isSolid;
   protected boolean canBounce;
   public int bouncing;
   public int piercing;
   protected int bounced;
   protected float angle;
   public boolean dropItem;
   public boolean sendPositionUpdate;
   public int maxMovePerTick;
   public float particleRandomOffset;
   public float particleRandomPerpOffset;
   public float particleDirOffset;
   public float particleSpeedMod;
   public float trailOffset;
   public final boolean hasHitbox;
   protected int hitboxMobID;
   public float hitboxOffset;
   protected boolean isBoomerang;
   protected boolean returningToOwner;
   public boolean removeIfOutOfBounds;
   public boolean sendRemovePacket;
   public boolean isCircularHitbox;
   int team;
   public Trail trail;
   protected float width;
   protected float hitLength;
   protected boolean useWidthForCollision;
   public boolean givesLight;
   public float lightSaturation;
   protected float lightDistMoved;
   protected float tickDistMoved;
   protected double trailParticles;
   private float distToMove;
   private float angleToTurn;
   protected int amountHit;
   protected final MobHitCooldowns hitCooldowns;
   protected final HashSet<Point> objectHits;
   private int owner;
   private Mob ownerMob;
   protected GameTexture texture;
   protected GameTexture shadowTexture;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public Projectile(boolean var1, boolean var2) {
      this.idData = new IDData();
      this.canHitMobs = true;
      this.canBounce = true;
      this.maxMovePerTick = 32;
      this.particleRandomOffset = 4.0F;
      this.particleRandomPerpOffset = 0.0F;
      this.particleDirOffset = -8.0F;
      this.particleSpeedMod = 0.1F;
      this.trailOffset = -10.0F;
      this.hitboxMobID = -1;
      this.hitboxOffset = -5.0F;
      this.isBoomerang = false;
      this.returningToOwner = false;
      this.removeIfOutOfBounds = true;
      this.sendRemovePacket = true;
      this.isCircularHitbox = false;
      this.lightSaturation = 0.75F;
      if (var1) {
         ProjectileRegistry.instance.applyIDData(this.getClass(), this.idData);
         this.texture = ProjectileRegistry.Textures.getTexture(this.getID());
         this.shadowTexture = ProjectileRegistry.Textures.getShadowTexture(this.getID());
      } else {
         this.idData.setData(-1, "unknown");
         this.texture = GameResources.error;
         this.shadowTexture = null;
      }

      this.hasHitbox = var2;
      this.setDamage(new GameDamage(0.0F));
      this.hitCooldowns = new MobHitCooldowns();
      this.objectHits = new HashSet();
      this.traveledDistance = 0.0F;
      this.lightDistMoved = 0.0F;
      this.tickDistMoved = 0.0F;
      this.isSolid = true;
      this.height = 18.0F;
      this.doesImpactDamage = true;
      this.knockback = 25;
      this.owner = -1;
   }

   public Projectile(boolean var1) {
      this(true, var1);
   }

   public Projectile() {
      this(false);
   }

   public void applyData(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, Mob var8) {
      this.applyData(var1, var2, var3, var4, var5, var6, var7, 25, var8);
   }

   public void applyData(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
      this.setDistance(var6);
   }

   public boolean shouldSendSpawnPacket() {
      return this.getID() != -1;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      var1.putNextInt(this.getUniqueID());
      var1.putNextBoolean(this.modifier != null);
      if (this.modifier != null) {
         var1.putNextShortUnsigned(this.modifier.getID());
         this.modifier.setupSpawnPacket(var1);
      }

      this.setupPositionPacket(var1);
      var1.putNextFloat(this.speed);
      var1.putNextInt(this.distance);
      var1.putNextShort((short)this.knockback);
      var1.putNextInt(this.getOwnerID());
      this.damage.writePacket(var1);
      if (this.hasHitbox) {
         var1.putNextBoolean(true);
         if (this.hitboxMobID == -1) {
            this.generateHitboxMob(true);
         }

         var1.putNextInt(this.hitboxMobID);
      } else {
         var1.putNextBoolean(false);
      }

      var1.putNextInt(this.amountHit);
      this.hitCooldowns.setupPacket(var1, false, this.getWorldEntity().getTime());
      var1.putNextShortUnsigned(this.objectHits.size());
      Iterator var2 = this.objectHits.iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         var1.putNextShortUnsigned(var3.x);
         var1.putNextShortUnsigned(var3.y);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      this.setUniqueID(var1.getNextInt());
      boolean var2 = var1.getNextBoolean();
      int var3;
      if (var2) {
         var3 = var1.getNextShortUnsigned();
         this.modifier = ProjectileModifierRegistry.getModifier(var3);
         if (this.modifier == null) {
            throw new IllegalStateException("Could not find projectile modifier with ID " + var3);
         }

         this.modifier.projectile = this;
         this.modifier.applySpawnPacket(var1);
      }

      this.applyPositionPacket(var1);
      this.updateAngle();
      this.speed = var1.getNextFloat();
      this.setDistance(var1.getNextInt());
      this.knockback = var1.getNextShort();
      this.setOwner(var1.getNextInt());
      this.damage = GameDamage.fromReader(var1);
      if (var1.getNextBoolean()) {
         this.hitboxMobID = var1.getNextInt();
      }

      this.amountHit = var1.getNextInt();
      this.hitCooldowns.applyPacket(var1, this.getWorldEntity().getTime());
      var3 = var1.getNextShortUnsigned();

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var1.getNextShortUnsigned();
         int var6 = var1.getNextShortUnsigned();
         this.objectHits.add(new Point(var5, var6));
      }

   }

   public void setupPositionPacket(PacketWriter var1) {
      var1.putNextFloat(this.x);
      var1.putNextFloat(this.y);
      var1.putNextFloat(this.dx);
      var1.putNextFloat(this.dy);
      var1.putNextFloat(this.traveledDistance);
      if (this.modifier != null) {
         this.modifier.setupPositionPacket(var1);
      }

   }

   public void applyPositionPacket(PacketReader var1) {
      float var2 = var1.getNextFloat();
      float var3 = var1.getNextFloat();
      this.changePosition(var2, var3);
      this.dx = var1.getNextFloat();
      this.dy = var1.getNextFloat();
      this.traveledDistance = var1.getNextFloat();
      this.updateAngle();
      if (this.modifier != null) {
         this.modifier.applyPositionPacket(var1);
      }

   }

   public void changePosition(float var1, float var2) {
      double var3 = (new Point2D.Float(this.x, this.y)).distance((double)var1, (double)var2);
      this.x = var1;
      this.y = var2;
      if (var3 > 10.0) {
         this.replaceTrail();
      }

   }

   public Projectile setModifier(ProjectileModifier var1) {
      if (this.isInitialized()) {
         throw new IllegalStateException("Cannot set projectile modifier after initialization");
      } else {
         this.modifier = var1;
         var1.projectile = this;
         return this;
      }
   }

   public void init() {
      super.init();
      Mob var1 = this.getOwner();
      if (var1 != null) {
         this.setTeam(var1.getTeam());
      }

      this.canBreakObjects = var1 != null && var1.isPlayer;
      if (var1 != null) {
         if (this.isServer()) {
            if (Settings.giveClientsPower) {
               if (var1.isPlayer) {
                  this.handlingClient = ((PlayerMob)var1).getServerClient();
               }

               this.clientHandlesHit = true;
            }
         } else if (this.isClient()) {
            ClientClient var2 = this.getLevel().getClient().getClient();
            if (this.getLevel().getClient().allowClientsPower()) {
               if (var2 != null && var1 == var2.playerMob) {
                  this.handlingClient = var2;
               }

               this.clientHandlesHit = true;
            }
         }
      }

      if (this.getLevel() != null && this.isClient()) {
         if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
         }

         this.trail = this.getTrail();
         if (this.trail != null) {
            this.trail.removeOnFadeOut = false;
            this.getLevel().entityManager.addTrail(this.trail);
         }
      }

      if (this.hasHitbox) {
         this.generateHitboxMob(this.hitboxMobID == -1);
      }

      if (this.modifier != null) {
         this.modifier.init();
      }

   }

   public void postInit() {
      super.postInit();
      if (this.modifier != null) {
         this.modifier.postInit();
      }

   }

   protected void replaceTrail() {
      if (this.trail != null) {
         this.trail.removeOnFadeOut = true;
         if (this.getLevel() != null && this.isClient()) {
            this.trail = this.getTrail();
            if (this.trail != null) {
               this.trail.removeOnFadeOut = false;
               this.getLevel().entityManager.addTrail(this.trail);
            }
         }
      }

   }

   public float tickMovement(float var1) {
      if (this.removed()) {
         return 0.0F;
      } else {
         if (this.isBoomerang) {
            Mob var2 = this.getOwner();
            if (var2 == null) {
               this.remove();
               return 0.0F;
            }

            if (this.returningToOwner) {
               this.setTarget(var2.x, var2.y);
            }
         }

         float var6 = this.getMoveDist(this.dx * this.speed, var1);
         float var3 = this.getMoveDist(this.dy * this.speed, var1);
         double var4 = Math.sqrt((double)(var6 * var6 + var3 * var3));
         if (Double.isNaN(var4) || Double.isInfinite(var4)) {
            var4 = 0.0;
         }

         this.moveDist(var4);
         if (this.removeIfOutOfBounds && (this.getX() < -100 || this.getY() < -100 || this.getX() > this.getLevel().width * 32 + 100 || this.getY() > this.getLevel().height * 32 + 100)) {
            this.remove();
         }

         return (float)var4;
      }
   }

   public void serverTick() {
      if (this.sendPositionUpdate && this.handlingClient == null) {
         this.sendServerUpdatePacket();
         this.sendPositionUpdate = false;
      }

   }

   public void clientTick() {
      if (this.sendPositionUpdate) {
         if (this.isClient() && this.handlingClient == this.getLevel().getClient().getClient()) {
            this.sendClientUpdatePacket();
         }

         this.sendPositionUpdate = false;
      }

      if (this.givesLight && this.isClient()) {
         this.refreshParticleLight();
      }

      float var1 = this.getParticleChance();
      if (var1 > 0.0F && (var1 >= 1.0F || GameRandom.globalRandom.nextFloat() <= var1)) {
         this.spawnSpinningParticle();
      }

   }

   public void refreshParticleLight() {
      Color var1 = this.getParticleColor();
      if (var1 == null) {
         this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y);
      } else {
         this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, var1, this.lightSaturation);
      }

   }

   protected int getExtraSpinningParticles() {
      return 1;
   }

   protected void spawnSpinningParticle() {
      Color var1 = this.getParticleColor();
      if (var1 != null) {
         Point2D.Float var2 = GameMath.getPerpendicularDir(this.dx, this.dy);
         float var3 = this.getHeight();
         float var4 = GameRandom.globalRandom.floatGaussian();
         this.modifySpinningParticle(this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dx * this.particleDirOffset + var2.x * var4 * this.particleRandomPerpOffset, this.y + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dy * this.particleDirOffset + var2.y * var4 * this.particleRandomPerpOffset, Particle.GType.COSMETIC).movesConstant(this.dx * this.speed * this.particleSpeedMod, this.dy * this.speed * this.particleSpeedMod).color(var1).height(var3));
         int var5 = this.getExtraSpinningParticles();

         for(int var6 = 0; var6 < var5; ++var6) {
            var4 = GameRandom.globalRandom.floatGaussian();
            this.modifySpinningParticle(this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dx * GameRandom.globalRandom.nextFloat() * (float)this.maxMovePerTick + this.dx * this.particleDirOffset + var2.x * var4 * this.particleRandomPerpOffset, this.y + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dy * GameRandom.globalRandom.nextFloat() * (float)this.maxMovePerTick + this.dy * this.particleDirOffset + var2.y * var4 * this.particleRandomPerpOffset, Particle.GType.COSMETIC).movesConstant(this.dx * this.speed * this.particleSpeedMod, this.dy * this.speed * this.particleSpeedMod).color(this.getParticleColor()).height(var3));
         }
      }

   }

   protected void modifySpinningParticle(ParticleOption var1) {
   }

   protected void spawnDeathParticles() {
      Color var1 = this.getParticleColor();
      if (var1 != null) {
         float var2 = this.getHeight();

         for(int var3 = 0; var3 < 8; ++var3) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)), (float)(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1))).color(this.getParticleColor()).height(var2);
         }
      }

   }

   public float getParticleChance() {
      return 1.0F;
   }

   public Color getParticleColor() {
      return null;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(150, 150, 150), 6.0F, 250, 18.0F);
   }

   public float getTrailThickness() {
      return this.trail.thickness;
   }

   protected Mob generateHitboxMob(boolean var1) {
      Mob var2 = this.getHitboxMob();
      if (var2 != null) {
         var2.remove();
      }

      Mob var3 = this.constructHitboxMob();
      var3.setLevel(this.getLevel());
      if (var1) {
         var3.getUniqueID(new GameRandom((long)this.getUniqueID()));
      } else {
         var3.setUniqueID(this.hitboxMobID);
      }

      this.getLevel().entityManager.addMob(var3, 10.0F, 10.0F);
      this.hitboxMobID = var3.getUniqueID();
      return var3;
   }

   public Mob getHitboxMob() {
      return this.hitboxMobID == -1 ? null : (Mob)this.getLevel().entityManager.mobs.get(this.hitboxMobID, false);
   }

   protected Mob constructHitboxMob() {
      ProjectileHitboxMob var1 = new ProjectileHitboxMob();
      var1.projectile = this;
      return var1;
   }

   public Rectangle getHitbox() {
      float var1 = Math.max(16.0F, this.getWidth());
      return new Rectangle((int)(this.x + this.dx * this.hitboxOffset - var1 / 2.0F), (int)(this.y + this.dy * this.hitboxOffset - var1 / 2.0F), (int)var1, (int)var1);
   }

   public void setDistance(int var1) {
      this.distance = var1;
   }

   public void checkHitCollision(Line2D var1) {
      this.checkCollision(this.toHitbox(var1));
   }

   public final void setWidth(float var1) {
      this.setWidth(var1, false);
   }

   public final void setWidth(float var1, float var2) {
      this.setWidth(var1, false, var2);
   }

   public void setWidth(float var1, boolean var2) {
      this.setWidth(var1, var2, var2 ? var1 / 2.0F : 0.0F);
   }

   public void setWidth(float var1, boolean var2, float var3) {
      this.width = var1;
      this.useWidthForCollision = var2;
      this.hitLength = var3;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHitLength() {
      return this.hitLength;
   }

   public int movePerIteration() {
      return GameMath.limit((int)(Math.pow((double)this.speed, 1.7000000476837158) / 250.0), 1, 8);
   }

   public void moveDist(double var1) {
      var1 = Math.min(var1, (double)((float)this.distance + this.distToMove - this.traveledDistance));
      this.distToMove = (float)((double)this.distToMove + var1);
      if (this.distToMove < 0.0F || Float.isNaN(this.distToMove) || Float.isInfinite(this.distToMove)) {
         this.distToMove = 0.0F;
      }

      if (this.distToMove == 0.0F) {
         this.checkRemoved();
      }

      int var3 = this.movePerIteration();

      while(this.distToMove > (float)var3 && !this.removed()) {
         Point2D.Float var4 = new Point2D.Float(this.x, this.y);
         double var5 = this.getDistanceMovedBeforeCollision((double)Math.min(var3, this.maxMovePerTick));
         this.distToMove = (float)((double)this.distToMove - var5);
         this.traveledDistance = (float)((double)this.traveledDistance + var5);
         this.tickDistMoved = (float)((double)this.tickDistMoved + var5);
         this.onMoveTick(var4, var5);
         if (this.tickDistMoved >= (float)this.maxMovePerTick) {
            this.onMaxMoveTick();
            this.tickDistMoved -= (float)this.maxMovePerTick;
         }

         if (this.isClient() && this.givesLight) {
            this.lightDistMoved = (float)((double)this.lightDistMoved + var5);
            if (this.lightDistMoved > 32.0F) {
               this.lightDistMoved -= 32.0F;
               if (this.isClient()) {
                  this.refreshParticleLight();
               }
            }
         }

         this.checkRemoved();
         if (this.trail != null) {
            this.trail.addPoint(new TrailVector(this.x + this.dx * this.trailOffset, this.y + this.dy * this.trailOffset, this.dx, this.dy, this.getTrailThickness(), this.getHeight()));
         }
      }

   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      if (this.modifier != null) {
         this.modifier.onMoveTick(var1, var2);
      }

   }

   public void spawnTrailParticle(Point2D.Float var1, double var2, Color var4, float var5, int var6) {
      if (this.isClient()) {
         float var7 = this.getHeight();
         this.trailParticles += var2 / (double)var5;
         if (this.trailParticles > 1.0) {
            Point2D.Float var8 = new Point2D.Float(this.x, this.y);
            Point2D.Float var9 = GameMath.normalize(var1.x - var8.x, var1.y - var8.y);
            Point2D.Float var10 = GameMath.getPerpendicularDir(var9.x, var9.y);

            while(this.trailParticles > 1.0) {
               --this.trailParticles;
               float var11 = (float)((double)GameRandom.globalRandom.nextFloat() * var2);
               float var12 = (GameRandom.globalRandom.nextFloat() - 0.5F) * 8.0F;
               float var13 = var9.x * var11 + var10.x * var12;
               float var14 = var9.y * var11 + var10.y * var12;
               float var10001 = var1.x + var13;
               float var10002 = var1.y + var14;
               this.getLevel().entityManager.addParticle(var10001, var10002, Particle.GType.COSMETIC).color(var4).height(var7).lifeTime(var6);
            }
         }
      }

   }

   public void onMaxMoveTick() {
   }

   public void checkRemoved() {
      if (this.traveledDistance >= (float)this.distance) {
         if (this.isBoomerang) {
            if (!this.returningToOwner) {
               this.returnToOwner();
            } else {
               this.remove();
            }
         } else {
            this.doHitLogic((Mob)null, (LevelObjectHit)null, this.x, this.y);
            if (this.isServer() && this.dropItem) {
               this.dropItem();
            }

            this.remove();
            this.sendRemovePacket = false;
         }
      }

   }

   protected void returnToOwner() {
      if (!this.returningToOwner) {
         Mob var1 = this.getOwner();
         if (var1 != null) {
            this.distance = (int)((float)this.distance + var1.getDistance(this.x, this.y) * 4.0F);
            this.setTarget(var1.x, var1.y);
            if (this.trail != null) {
               this.trail.addBreakPoint(new TrailVector(this.x + this.dx * this.trailOffset, this.y + this.dy * this.trailOffset, this.dx, this.dy, this.getTrailThickness(), this.getHeight()));
            }
         } else {
            this.distance *= 5;
         }

         if (this.piercing > 0) {
            this.clearHits();
         }

         this.returningToOwner = true;
         this.isSolid = false;
      }
   }

   public boolean returningToOwner() {
      return this.returningToOwner;
   }

   public float getBoomerangUsage() {
      return 1.0F;
   }

   public void remove() {
      if (!this.removed()) {
         if (this.isClient()) {
            this.spawnDeathParticles();
         }

         Mob var1;
         if (this.isBoomerang) {
            var1 = this.getOwner();
            if (var1 instanceof PlayerMob) {
               ((PlayerMob)var1).boomerangs.remove(this);
            }
         }

         if (this.hasHitbox) {
            var1 = this.getHitboxMob();
            if (var1 != null) {
               var1.remove();
            }
         }
      }

      this.sendRemovePacket = true;
      super.remove();
   }

   public final Shape toHitbox(Line2D var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = this.getWidth();
         if (var2 > 0.0F) {
            return this.isCircularHitbox ? new LineHitbox(var1, this.x, this.y, var2) : new LineHitbox(var1, var2);
         } else {
            return var1;
         }
      }
   }

   protected CollisionFilter getLevelCollisionFilter() {
      CollisionFilter var1 = (new CollisionFilter()).projectileCollision();
      return this.canBreakObjects ? var1.addFilter((var0) -> {
         return !var0.object().object.attackThrough;
      }) : var1;
   }

   protected double getDistanceMovedBeforeCollision(double var1) {
      float var3 = this.getWidth();
      float var4 = this.getHitLength();
      double var5 = var1 + (double)var4;
      Point2D.Float var7 = new Point2D.Float(this.x, this.y);
      Point2D.Float var8 = new Point2D.Float(this.x + (float)((double)this.dx * var5), this.y + (float)((double)this.dy * var5));
      Line2D.Float var9 = new Line2D.Float(var7, var8);
      if (!this.isSolid) {
         this.x = (float)((double)this.x + (double)this.dx * var1);
         this.y = (float)((double)this.y + (double)this.dy * var1);
         this.checkHitCollision(var9);
         return var1;
      } else {
         Point2D.Float var10 = var7;
         Line2D.Float var11 = var9;
         CollisionFilter var12 = this.getLevelCollisionFilter();
         ArrayList var13 = this.getLevel().getCollisions(var9, var12);
         Point2D.Float var15;
         if (var13.isEmpty() && this.useWidthForCollision) {
            for(int var14 = 8; (float)var14 < var3 / 2.0F; var14 += 8) {
               var10 = GameMath.getPerpendicularPoint(var7, (float)var14, this.dx, this.dy);
               var15 = new Point2D.Float(var10.x + (float)((double)this.dx * var1), var10.y + (float)((double)this.dy * var1));
               Line2D.Float var16 = new Line2D.Float(var10, var15);
               var13 = this.getLevel().getCollisions(var16, var12);
               if (!var13.isEmpty()) {
                  var11 = var16;
                  break;
               }

               var10 = GameMath.getPerpendicularPoint(var7, (float)(-var14), this.dx, this.dy);
               var15 = new Point2D.Float(var10.x + (float)((double)this.dx * var1), var10.y + (float)((double)this.dy * var1));
               var16 = new Line2D.Float(var10, var15);
               var13 = this.getLevel().getCollisions(var16, var12);
               if (!var13.isEmpty()) {
                  var11 = var16;
                  break;
               }
            }
         }

         IntersectionPoint var19 = this.getLevel().getCollisionPoint(var13, var11, false);
         if (var19 != null) {
            var15 = new Point2D.Float(this.x - var10.x, this.y - var10.y);
            this.x = (float)var19.getX() + var15.x;
            this.y = (float)var19.getY() + var15.y;
            int var20 = this.bouncing;
            Mob var17 = this.getOwner();
            if (var17 != null) {
               var20 += (Integer)var17.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES);
            }

            if (this.bounced < var20 && this.canBounce) {
               if (this.trail != null) {
                  this.trail.addBreakPoint(new TrailVector((float)var19.getX(), (float)var19.getY(), this.dx, this.dy, this.getTrailThickness(), this.getHeight()));
               }

               if (var19.dir != IntersectionPoint.Dir.RIGHT && var19.dir != IntersectionPoint.Dir.LEFT) {
                  if (var19.dir == IntersectionPoint.Dir.UP || var19.dir == IntersectionPoint.Dir.DOWN) {
                     this.dy = -this.dy;
                     this.y += Math.signum(this.dy) * 4.0F;
                  }
               } else {
                  this.dx = -this.dx;
                  this.x += Math.signum(this.dx) * 4.0F;
               }

               this.updateAngle();
               this.sendPositionUpdate = true;
            } else if (var19.dir == IntersectionPoint.Dir.UP) {
               this.y += 8.0F;
            } else if (var19.dir == IntersectionPoint.Dir.RIGHT) {
               this.x -= 8.0F;
            } else if (var19.dir == IntersectionPoint.Dir.DOWN) {
               this.y -= 8.0F;
            } else if (var19.dir == IntersectionPoint.Dir.LEFT) {
               this.x += 8.0F;
            }

            this.onHit((Mob)null, (LevelObjectHit)var19.target, (float)var19.getX(), (float)var19.getY(), false, (ServerClient)null);
            ++this.bounced;
            float var18 = (float)var10.distance(var19);
            this.checkHitCollision(new Line2D.Float(this.x, this.y, this.x + this.dx * var18, this.y + this.dy * var18));
            return (double)Math.max(var18, 1.0F);
         } else {
            this.x = (float)((double)this.x + (double)this.dx * var1);
            this.y = (float)((double)this.y + (double)this.dy * var1);
            this.checkHitCollision(var9);
            return var1;
         }
      }
   }

   public float getMoveDist(float var1, float var2) {
      return var1 * var2 / 250.0F;
   }

   public void onHit(Mob var1, LevelObjectHit var2, float var3, float var4, boolean var5, ServerClient var6) {
      if (this.modifier == null || !this.modifier.onHit(var1, var2, var3, var4, var5, var6)) {
         this.hit(var1, var3, var4, var5, var6);
         if (var1 == null) {
            if (this.isClient()) {
               this.spawnWallHitParticles(var3, var4);
               this.playHitSound(var3, var4);
            }

            this.doHitLogic(var1, var2, var3, var4);
            int var7 = this.bouncing;
            Mob var8 = this.getOwner();
            if (var8 != null) {
               var7 += (Integer)var8.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES);
            }

            if (this.bounced >= var7 || !this.canBounce) {
               if (this.isBoomerang) {
                  this.returnToOwner();
               } else {
                  if (this.dropItem && this.isServer()) {
                     this.dropItem();
                  }

                  this.remove();
                  this.sendRemovePacket = false;
               }
            }
         } else {
            boolean var10 = this.checkHitCooldown(var1, !this.isServer() || var6 == null && this.handlingClient == null && (!this.clientHandlesHit || !var1.isPlayer) ? 0 : 100);
            if (var10 && (this.amountHit() <= this.piercing || this.returningToOwner)) {
               boolean var11 = true;
               if (this.isServer()) {
                  boolean var9 = this.handlingClient != null || this.clientHandlesHit && var1.isPlayer;
                  if (var6 == null && var9) {
                     var11 = false;
                  } else {
                     if (this.doesImpactDamage) {
                        this.applyDamage(var1, var3, var4);
                     }

                     this.doHitLogic(var1, var2, var3, var4);
                     if (var6 != null) {
                        this.getLevel().getServer().network.sendToClientsAtExcept(new PacketProjectileHit(this, var3, var4, var1), (ServerClient)var6, var6);
                     } else {
                        this.getLevel().getServer().network.sendToClientsAt(new PacketProjectileHit(this, var3, var4, var1), (Level)this.getLevel());
                     }
                  }
               } else if (this.isClient()) {
                  if (var5) {
                     this.doHitLogic(var1, var2, var3, var4);
                  } else if (this.getLevel().getClient().allowClientsPower()) {
                     ClientClient var12 = this.getLevel().getClient().getClient();
                     if (this.clientHandlesHit && var1 == var12.playerMob || this.handlingClient == var12) {
                        this.getLevel().getClient().network.sendPacket(new PacketProjectileHit(this, var3, var4, var1));
                        var1.startHitCooldown();
                        this.doHitLogic(var1, var2, var3, var4);
                     }
                  }
               } else if (this.doesImpactDamage) {
                  this.applyDamage(var1, var3, var4);
               }

               if (var11) {
                  this.addHit(var1);
               }
            }

            if (this.amountHit() > this.piercing) {
               if (this.isBoomerang) {
                  if (!this.returningToOwner) {
                     this.returnToOwner();
                  }
               } else {
                  this.remove();
               }
            }
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void hit(Mob var1, float var2, float var3, boolean var4, ServerClient var5) {
   }

   public void applyDamage(Mob var1, float var2, float var3) {
      var1.isServerHit(this.getDamage(), var1.x - var2 * -this.dx * 50.0F, var1.y - var3 * -this.dy * 50.0F, (float)this.knockback, this);
   }

   public void attackThrough(LevelObjectHit var1) {
      if (!this.hasHit(var1.getPoint())) {
         var1.getLevelObject().attackThrough(this.getDamage(), this);
         this.addHit(var1.getPoint());
      }
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      if (this.modifier != null) {
         this.modifier.doHitLogic(var1, var2, var3, var4);
      }

      this.doHitLogic(var1, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public void doHitLogic(Mob var1, float var2, float var3) {
   }

   protected Color getWallHitColor() {
      Color var1 = this.getParticleColor();
      return var1 == null ? new Color(0.5F, 0.5F, 0.5F) : var1;
   }

   protected void spawnWallHitParticles(float var1, float var2) {
      Color var3 = this.getWallHitColor();
      if (var3 != null) {
         float var4 = this.getHeight();

         for(int var5 = 0; var5 < 5; ++var5) {
            this.getLevel().entityManager.addParticle(var1, var2, Particle.GType.COSMETIC).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 10.0F, (float)GameRandom.globalRandom.nextGaussian() * 10.0F).height(var4).color(var3).size((var0, var1x, var2x, var3x) -> {
               var0.size(8, 8);
            }).lifeTime(200);
         }

      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("projectile", 3);
   }

   public GameMessage getAttackerName() {
      Mob var1 = this.getOwner();
      return (GameMessage)(var1 != null ? var1.getAttackerName() : new LocalMessage("deaths", "unknownatt"));
   }

   public Mob getFirstAttackOwner() {
      return this.getOwner();
   }

   public int getAttackerUniqueID() {
      return this.getUniqueID();
   }

   protected void playHitSound(float var1, float var2) {
   }

   protected CollisionFilter getAttackThroughCollisionFilter() {
      return (new CollisionFilter()).projectileCollision().attackThroughCollision();
   }

   protected final void checkCollision(Shape var1) {
      Mob var2 = this.getOwner();
      if (var2 != null && this.isBoomerang && this.returningToOwner && var1.intersects(var2.getHitBox())) {
         this.remove();
      }

      Iterator var4;
      if (this.isServer() && this.canBreakObjects) {
         ArrayList var3 = this.getLevel().getCollisions(var1, this.getAttackThroughCollisionFilter());
         var4 = var3.iterator();

         while(var4.hasNext()) {
            LevelObjectHit var5 = (LevelObjectHit)var4.next();
            if (!var5.invalidPos() && var5.getObject().attackThrough) {
               this.attackThrough(var5);
            }
         }
      }

      if (this.canHitMobs) {
         List var6 = (List)this.streamTargets(var2, var1).filter((var2x) -> {
            return this.canHit(var2x) && var1.intersects(var2x.getHitBox());
         }).filter((var1x) -> {
            return !this.isSolid || var1x.canHitThroughCollision() || !this.perpLineCollidesWithLevel(var1x.x, var1x.y);
         }).collect(Collectors.toCollection(LinkedList::new));
         var4 = var6.iterator();

         while(var4.hasNext()) {
            Mob var7 = (Mob)var4.next();
            this.onHit(var7, (LevelObjectHit)null, this.x, this.y, false, (ServerClient)null);
         }
      }

   }

   public boolean canHit(Mob var1) {
      return var1.canBeHit(this);
   }

   protected boolean perpLineCollidesWithLevel(float var1, float var2) {
      Line2D.Float var3 = new Line2D.Float(var1, var2, var1 + -this.dy, var2 + this.dx);
      Point2D var4 = GameMath.getIntersectionPoint(new Line2D.Float(this.x, this.y, this.x + this.dx, this.y + this.dy), var3, true);
      if (var4 != null) {
         Line2D.Float var5 = new Line2D.Float(var1, var2, (float)var4.getX(), (float)var4.getY());
         return this.getLevel().collides((Line2D)var5, (CollisionFilter)this.getLevelCollisionFilter());
      } else {
         return false;
      }
   }

   protected Stream<Mob> streamTargets(Mob var1, Shape var2) {
      return var1 != null ? GameUtils.streamTargets(var1, var2) : Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(var2, 1), GameUtils.streamNetworkClients(this.getLevel()).filter((var0) -> {
         return !var0.isDead() && var0.hasSpawned();
      }).map((var0) -> {
         return var0.playerMob;
      }));
   }

   protected void dropItem() {
   }

   public void setDamage(GameDamage var1) {
      if (var1 != null) {
         this.damage = var1;
      }
   }

   public GameDamage getDamage() {
      return this.damage;
   }

   public void setDir(float var1, float var2) {
      this.setTarget(this.x + var1 * 100.0F, this.y + var2 * 100.0F);
   }

   public void setTarget(float var1, float var2) {
      this.targetX = var1;
      this.targetY = var2;
      Point2D.Float var3 = GameMath.normalize(var1 - this.x, var2 - this.y);
      if (var3.x == 0.0F && var3.y == 0.0F) {
         this.dx = 1.0F;
         this.dy = 0.0F;
      } else {
         this.dx = var3.x;
         this.dy = var3.y;
      }

      this.angle = (float)Math.toDegrees(Math.atan2((double)this.dy, (double)this.dx));
      this.angle += 90.0F;
      this.fixAngle();
   }

   public static Point2D.Float getPredictedTargetPos(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = (float)(new Point2D.Float(var4, var5)).distance((double)var0, (double)var1) + var7;
      float var9 = getTravelTimeMillis(var6, var8);
      float var10 = getPositionAfterMillis(var2, var9);
      float var11 = getPositionAfterMillis(var3, var9);
      return new Point2D.Float(var0 + var10, var1 + var11);
   }

   public static Point2D.Float getPredictedTargetPos(Mob var0, float var1, float var2, float var3, float var4) {
      return getPredictedTargetPos(var0.x, var0.y, var0.dx, var0.dy, var1, var2, var3, var4);
   }

   public void setTargetPrediction(float var1, float var2, float var3, float var4, float var5) {
      Point2D.Float var6 = getPredictedTargetPos(var1, var2, var3, var4, this.x, this.y, this.speed, var5);
      this.setTarget(var6.x, var6.y);
   }

   public void setTargetPrediction(Mob var1, float var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         Mob var4 = var1.getMount();
         if (var4 == null) {
            break;
         }

         var1 = var4;
      }

      this.setTargetPrediction(var1.x, var1.y, var1.dx, var1.dy, var2);
   }

   public void setTargetPrediction(Mob var1) {
      this.setTargetPrediction(var1, 0.0F);
   }

   public void fixAngle() {
      this.angle = GameMath.fixAngle(this.angle);
   }

   public void setAngle(float var1) {
      this.angle = var1;
      this.fixAngle();
      Point2D.Float var2 = GameMath.getAngleDir(this.angle - 90.0F);
      this.dx = var2.x;
      this.dy = var2.y;
      this.targetX = this.x + this.dx * 100.0F;
      this.targetY = this.y + this.dy * 100.0F;
   }

   public void updateAngle() {
      this.angle = (float)Math.toDegrees(Math.atan2((double)this.dy, (double)this.dx));
      this.angle += 90.0F;
      this.fixAngle();
   }

   public float getAngle() {
      return this.angle % 360.0F;
   }

   public boolean turnToward(float var1, float var2, float var3) {
      this.angleToTurn += var3;
      float var4 = 2.0F;

      boolean var5;
      for(var5 = false; this.angleToTurn >= var4; this.angleToTurn -= var4) {
         float var6 = this.getAngleToTarget(var1, var2);
         float var7 = this.getAngleDifference(var6);
         if (Math.abs(var7) - var4 < 1.0F) {
            var4 = Math.abs(var7) - 1.0F;
            this.angleToTurn = 0.0F;
            var5 = true;
         }

         this.setAngle(this.angle + var4 * Math.signum(var7));
         if (var5) {
            break;
         }
      }

      return var5;
   }

   public static float getAngleToTarget(float var0, float var1, float var2, float var3) {
      float var4 = var2 - var0;
      float var5 = var3 - var1;
      float var6 = (float)Math.toDegrees(Math.atan((double)(var5 / var4)));
      if (var4 < 0.0F) {
         var6 += 270.0F;
      } else {
         var6 += 90.0F;
      }

      return var6;
   }

   public float getAngleToTarget(float var1, float var2) {
      return getAngleToTarget(this.x, this.y, var1, var2);
   }

   public float getAngleDifference(float var1) {
      return GameMath.getAngleDifference(var1, this.getAngle());
   }

   public float getHeight() {
      if (!this.heightBasedOnDistance) {
         return this.height;
      } else {
         float var1 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
         return this.height - (float)((int)((this.height - 4.0F) * var1));
      }
   }

   public void setOwner(int var1, Level var2) {
      this.owner = var1;
      if (var2 != null) {
         this.ownerMob = GameUtils.getLevelMob(var1, var2);
         this.setOwner(this.ownerMob);
      }
   }

   public void setOwner(int var1) {
      this.setOwner(var1, this.getLevel());
   }

   public void setOwner(Mob var1) {
      if (var1 != null) {
         this.owner = var1.getUniqueID();
         this.team = var1.getTeam();
         this.ownerMob = var1;
      }
   }

   public Mob getOwner() {
      if (this.ownerMob == null && this.owner != 0) {
         this.setOwner(this.owner);
      }

      return this.ownerMob;
   }

   public int getOwnerID() {
      return this.owner;
   }

   public boolean checkHitCooldown(Mob var1, int var2) {
      return this.hitCooldowns.canHit(var1, this.getWorldEntity().getTime(), var2);
   }

   public void addHit(Mob var1) {
      this.startHitCooldown(var1);
      ++this.amountHit;
   }

   public void startHitCooldown(Mob var1) {
      this.hitCooldowns.startCooldown(var1, this.getWorldEntity().getTime());
   }

   public int amountHit() {
      return this.amountHit;
   }

   public boolean hasHit(Point var1) {
      return this.objectHits.contains(var1);
   }

   public void addHit(Point var1) {
      this.objectHits.add(var1);
   }

   public void clearHits() {
      this.hitCooldowns.resetCooldowns();
      this.objectHits.clear();
   }

   public int getTeam() {
      return this.team;
   }

   public boolean isSameTeam(Mob var1) {
      if (this.getTeam() != -1 && var1.getTeam() != -1) {
         return this.getTeam() == var1.getTeam();
      } else {
         return false;
      }
   }

   public void setTeam(int var1) {
      this.team = var1;
   }

   public float getLifeTime() {
      return this.traveledDistance / (float)this.distance;
   }

   public void sendServerUpdatePacket() {
      if (this.handlingClient != null) {
         this.getLevel().getServer().network.sendToClientsAtExcept(new PacketProjectilePositionUpdate(this), (ServerClient)((ServerClient)this.handlingClient), (ServerClient)this.handlingClient);
      } else {
         this.getLevel().getServer().network.sendToClientsAt(new PacketProjectilePositionUpdate(this), (Level)this.getLevel());
      }

   }

   public void sendClientUpdatePacket() {
      this.getLevel().getClient().network.sendPacket(new PacketProjectilePositionUpdate(this));
   }

   public Shape getSelectBox() {
      float var1 = this.getHeight();
      float var2 = Math.max(this.getWidth(), 16.0F);
      float var3 = var2 / 2.0F;
      return new LineHitbox(new Line2D.Float(this.x - this.dx * var3, this.y - this.dy * var3 - var1, this.x + this.dx * var3, this.y + this.dy * var3 - var1), var2);
   }

   public boolean onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      if (var3) {
         StringTooltips var4 = new StringTooltips();
         var4.add("Projectile: " + this.getStringID() + " (" + this.getID() + ")");
         var4.add("UniqueID: " + this.getUniqueID());
         Mob var5 = this.getOwner();
         if (var5 != null) {
            var4.add("Owner: " + var5.getDisplayName() + " (" + this.getOwnerID() + ")");
         } else {
            var4.add("Owner: " + this.getOwnerID());
         }

         var4.add("Pos: " + this.x + ", " + this.y);
         var4.add("Delta: " + this.dx + ", " + this.dy);
         var4.add("Speed: " + this.speed + ", Angle: " + this.getAngle());
         var4.add("Width: " + this.getWidth());
         if (this.hasHitbox) {
            var4.add("HitboxMobID: " + this.hitboxMobID);
         }

         Screen.addTooltip(var4, TooltipLocation.INTERACT_FOCUS);
         return true;
      } else {
         return false;
      }
   }

   public void dispose() {
      super.dispose();
      if (this.trail != null) {
         this.trail.removeOnFadeOut = true;
      }

   }

   protected float getFadeAlphaDistance(int var1, int var2) {
      if (var1 > 0 && this.traveledDistance < (float)var1) {
         return this.traveledDistance / (float)var1;
      } else {
         return var2 > 0 && this.traveledDistance > (float)(this.distance - var2) ? Math.abs((Math.min(this.traveledDistance, (float)this.distance) - (float)(this.distance - var2)) / (float)var2 - 1.0F) : 1.0F;
      }
   }

   protected float getFadeAlphaTime(int var1, int var2) {
      float var3;
      if (var1 > 0) {
         var3 = this.getMoveDist(this.speed, (float)var1);
         if (this.traveledDistance < var3) {
            return this.traveledDistance / var3;
         }
      }

      if (var2 > 0) {
         var3 = this.getMoveDist(this.speed, (float)var2);
         if (this.traveledDistance > (float)this.distance - var3) {
            return Math.abs((Math.min(this.traveledDistance, (float)this.distance) - ((float)this.distance - var3)) / var3 - 1.0F);
         }
      }

      return 1.0F;
   }

   protected void addShadowDrawables(OrderableDrawables var1, int var2, int var3, GameLight var4, float var5, int var6) {
      this.addShadowDrawables(var1, var2, var3, var4, (var3x) -> {
         return var3x.rotate(var5, this.shadowTexture.getWidth() / 2, var6);
      });
   }

   protected void addShadowDrawables(OrderableDrawables var1, int var2, int var3, GameLight var4, float var5, int var6, int var7) {
      this.addShadowDrawables(var1, var2, var3, var4, (var3x) -> {
         return var3x.rotate(var5, var6, var7);
      });
   }

   protected void addShadowDrawables(OrderableDrawables var1, int var2, int var3, GameLight var4, Function<TextureDrawOptionsEnd, TextureDrawOptionsEnd> var5) {
      this.addShadowDrawables(var1, this.shadowTexture.initDraw().next(), var2, var3, var4, var5);
   }

   protected void addShadowDrawables(OrderableDrawables var1, TextureDrawOptionsEnd var2, int var3, int var4, GameLight var5, Function<TextureDrawOptionsEnd, TextureDrawOptionsEnd> var6) {
      if (this.shadowTexture != null) {
         var2 = (TextureDrawOptionsEnd)var6.apply(var2.light(var5));
         TextureDrawOptionsEnd var7 = var2.pos(var3, var4);
         if (var7 != null) {
            var1.add((var1x) -> {
               var7.draw();
            });
         }

      }
   }

   protected void addServerProjectileDrawable(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (GlobalData.debugActive() && this.isClient()) {
         Server var9 = this.getLevel().getClient().getLocalServer();
         if (var9 != null) {
            Projectile var10 = (Projectile)var9.world.getLevel(this.getLevel().getIdentifier()).entityManager.projectiles.get(this.getUniqueID(), false);
            if (var10 != null) {
               var10.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8);
               int var11 = var7.getDrawX(var10.x);
               int var12 = var7.getDrawY(var10.y - var10.getHeight());
               final DrawOptions var13 = () -> {
                  FontManager.bit.drawChar((float)var11, (float)var12, '*', new FontOptions(12));
               };
               var1.add(new EntityDrawable(this) {
                  public int getSortY() {
                     return Integer.MAX_VALUE;
                  }

                  public void draw(TickManager var1) {
                     var13.draw();
                  }
               });
            }
         }
      }

   }
}
