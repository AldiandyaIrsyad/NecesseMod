package necesse.entity.levelEvent.explosionEvent;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;

public class ExplosionEvent extends LevelEvent implements Attacker {
   public float x;
   public float y;
   public int tileX;
   public int tileY;
   public int range;
   public GameDamage damage;
   public boolean destroysObjects;
   public boolean destroysTiles;
   public int toolTier;
   public int owner;
   public Mob ownerMob;
   public boolean hitsOwner;
   public int knockback;
   public float targetRangeMod;
   public boolean sendCustomData;
   public boolean sendOwnerData;
   private int tickCounter;
   private int maxTicks;
   private HashMap<Integer, Float> tileDistances;
   private LinkedList<Integer> hits;

   public ExplosionEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, int var6) {
      this.destroysTiles = false;
      this.targetRangeMod = 0.66F;
      this.tileDistances = new HashMap();
      this.hits = new LinkedList();
      this.x = var1;
      this.y = var2;
      this.range = Math.max(0, Math.min(var3, 32767));
      this.damage = var4;
      this.destroysObjects = var5;
      this.toolTier = var6;
      this.owner = -1;
      this.knockback = 500;
      this.hitsOwner = true;
      this.sendOwnerData = false;
      this.sendCustomData = true;
   }

   public ExplosionEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, int var6, Mob var7) {
      this(var1, var2, var3, var4, var5, var6);
      if (var7 != null) {
         this.owner = var7.getUniqueID();
      }

      this.ownerMob = var7;
      this.sendOwnerData = true;
   }

   public ExplosionEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, boolean var6, int var7, Mob var8) {
      this(var1, var2, var3, var4, var5, var7);
      this.destroysTiles = var6;
      if (var8 != null) {
         this.owner = var8.getUniqueID();
      }

      this.ownerMob = var8;
      this.sendOwnerData = true;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.x = var1.getNextFloat();
      this.y = var1.getNextFloat();
      if (this.sendOwnerData) {
         this.owner = var1.getNextInt();
      }

      if (this.sendCustomData) {
         this.range = var1.getNextShortUnsigned();
         this.damage = GameDamage.fromReader(var1);
         this.toolTier = var1.getNextShortUnsigned();
         this.destroysObjects = var1.getNextBoolean();
         this.destroysTiles = var1.getNextBoolean();
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.x);
      var1.putNextFloat(this.y);
      if (this.sendOwnerData) {
         var1.putNextInt(this.owner);
      }

      if (this.sendCustomData) {
         var1.putNextShortUnsigned(this.range);
         this.damage.writePacket(var1);
         var1.putNextShortUnsigned(this.toolTier);
         var1.putNextBoolean(this.destroysObjects);
         var1.putNextBoolean(this.destroysTiles);
      }

   }

   public void init() {
      super.init();
      this.tileX = (int)(this.x / 32.0F);
      this.tileY = (int)(this.y / 32.0F);
      if (this.owner != -1) {
         this.ownerMob = GameUtils.getLevelMob(this.owner, this.level, true);
         if (this.ownerMob == null) {
            GameLog.warn.println("Could not find owner with unique id " + this.owner + " for explosion event.");
            this.over();
         }
      }

      this.maxTicks = Math.max(5, this.range / 200 * 20 / 4);
   }

   public void clientTick() {
      if (this.tickCounter == 0) {
         this.playExplosionEffects();
      }

      int var1 = this.range - this.range / 10;
      float var2 = Math.max(0.0F, (float)var1 * ((float)(this.tickCounter - 2) / (float)this.maxTicks));
      float var3 = (float)var1 * ((float)this.tickCounter / (float)this.maxTicks);
      int var4 = (int)this.getParticleCount(var3, var2);
      spawnExplosionParticles(this.level, this.x, this.y, var4, var2, var3, (var1x, var2x, var3x, var4x, var5, var6, var7) -> {
         this.spawnExplosionParticle(var2x, var3x, var4x, var5, var6, var7);
      });
      ++this.tickCounter;
      if (this.tickCounter > this.maxTicks) {
         this.over();
      }
   }

   public float getParticleCount(float var1, float var2) {
      return (float)(Math.PI * (double)(var1 + (var1 - var2) / 2.0F) / 4.0);
   }

   public void spawnExplosionParticle(float var1, float var2, float var3, float var4, int var5, float var6) {
      spawnExplosionParticle(this.level, var1, var2, var3, var4, var5);
   }

   public static void spawnExplosionParticles(Level var0, float var1, float var2, int var3, float var4, float var5) {
      spawnExplosionParticles(var0, var1, var2, var3, var4, var5, (var0x, var1x, var2x, var3x, var4x, var5x, var6) -> {
         spawnExplosionParticle(var0x, var1x, var2x, var3x, var4x, var5x);
      });
   }

   public static void spawnExplosionParticles(Level var0, float var1, float var2, int var3, float var4, float var5, ExplosionSpawnFunction var6) {
      for(int var7 = 0; var7 <= var3; ++var7) {
         float var8 = 360.0F / (float)var3;
         int var9 = (int)((float)var7 * var8 + GameRandom.globalRandom.nextFloat() * var8);
         float var10 = GameRandom.globalRandom.getFloatBetween(var4, var5);
         float var11 = (float)Math.sin(Math.toRadians((double)var9));
         float var12 = (float)Math.cos(Math.toRadians((double)var9));
         var6.spawn(var0, var1 + var11 * var10, var2 + var12 * var10, var11 * 20.0F, var12 * 20.0F, 400, var10);
      }

   }

   public static void spawnExplosionParticle(Level var0, float var1, float var2, float var3, float var4, int var5) {
      var0.entityManager.addParticle(var1, var2, Particle.GType.CRITICAL).movesConstant(var3, var4).flameColor().height(10.0F).sizeFades(15, 25).givesLight(0.0F, 0.5F).onProgress(0.4F, (var4x) -> {
         Point2D.Float var5x = GameMath.normalize(var3, var4);
         var0.entityManager.addParticle(var4x.x + var5x.x * 20.0F, var4x.y + var5x.y * 20.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(var3, var4).smokeColor().heightMoves(10.0F, 40.0F).lifeTime(var5);
      }).lifeTime(var5);
   }

   public void serverTick() {
      int var1 = this.range;
      float var2 = Math.max(0.0F, (float)var1 * ((float)(this.tickCounter - 2) / (float)this.maxTicks));
      float var3 = (float)var1 * ((float)this.tickCounter / (float)this.maxTicks);
      if (this.destroysObjects || this.destroysTiles) {
         int var4 = (int)(var3 / 32.0F) + 1;

         for(int var5 = this.tileX - var4; var5 <= this.tileX + var4; ++var5) {
            for(int var6 = this.tileY - var4; var6 <= this.tileY + var4; ++var6) {
               GameObject var7 = this.level.getObject(var5, var6);
               if (var7.toolType != ToolType.UNBREAKABLE && var7.toolTier <= this.toolTier) {
                  float var10 = (Float)this.tileDistances.compute(var5 + var6 * this.level.width, (var3x, var4x) -> {
                     return var4x != null && var4x != 0.0F ? var4x : (float)(new Point2D.Float(this.x, this.y)).distance((double)(var5 * 32 + 16), (double)(var6 * 32 + 16));
                  });
                  if (!(var10 < var2) && !(var10 > var3)) {
                     GameDamage var11 = this.getTotalObjectDamage(this.getDistanceMod(var10));
                     int var12 = var11.getTotalDamage((Mob)null, this, 1.0F);
                     ServerClient var13 = null;
                     if (this.ownerMob != null && this.ownerMob.isPlayer) {
                        PlayerMob var14 = (PlayerMob)this.ownerMob;
                        if (var14.isServerClient()) {
                           var13 = var14.getServerClient();
                        }
                     }

                     if (this.destroysObjects) {
                        this.level.getObject(var5, var6).doExplosionDamage(this.level, var5, var6, var12, this.toolTier, var13);
                     }

                     if (this.destroysTiles) {
                        this.level.getTile(var5, var6).doExplosionDamage(this.level, var5, var6, var12, this.toolTier, var13);
                     }
                  }
               }
            }
         }
      }

      this.streamTargets().filter((var1x) -> {
         return var1x.canBeHit(this);
      }).filter((var1x) -> {
         return !this.hits.contains(var1x.getUniqueID());
      }).forEach((var3x) -> {
         float var4 = var3x.getDistance(this.x, this.y);
         if (var4 >= var2 && var4 <= var3) {
            float var5 = this.getDistanceMod(var4);
            GameDamage var6 = this.getTotalMobDamage(var5);
            float var7 = (float)this.knockback * var5;
            var3x.isServerHit(var6, (float)var3x.getX() - this.x, (float)var3x.getY() - this.y, var7, this);
            this.hits.add(var3x.getUniqueID());
         }

      });
      ++this.tickCounter;
      if (this.tickCounter > this.maxTicks) {
         this.over();
      }
   }

   protected float getDistanceMod(float var1) {
      float var2 = GameMath.limit(var1 * this.targetRangeMod / (float)this.range, 0.0F, 1.0F);
      float var3 = Math.abs(var2 - 1.0F);
      return (float)Math.pow((double)var3, 2.5);
   }

   protected GameDamage getTotalMobDamage(float var1) {
      return this.damage.modDamage(var1);
   }

   protected GameDamage getTotalObjectDamage(float var1) {
      return this.getTotalMobDamage(var1);
   }

   protected Stream<Mob> streamTargets() {
      if (this.ownerMob == null) {
         return Stream.concat(this.level.entityManager.mobs.getInRegionByTileRange((int)this.x / 32, (int)this.y / 32, this.range / 32 + 2).stream(), GameUtils.streamServerClients(this.level).map((var0) -> {
            return var0.playerMob;
         }));
      } else {
         Stream var1 = GameUtils.streamTargets(this.ownerMob, GameUtils.rangeBounds((int)this.x, (int)this.y, this.range + 64));
         if (this.hitsOwner) {
            var1 = Stream.concat(var1, Stream.of(this.ownerMob));
         }

         return var1;
      }
   }

   public GameMessage getAttackerName() {
      return (GameMessage)(this.ownerMob != null ? this.ownerMob.getAttackerName() : new LocalMessage("deaths", "explosionname"));
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("explosion", 3);
   }

   public Mob getFirstAttackOwner() {
      return this.ownerMob;
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.5F));
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile((int)(this.x / 32.0F), (int)(this.y / 32.0F)));
   }

   public interface ExplosionSpawnFunction {
      void spawn(Level var1, float var2, float var3, float var4, float var5, int var6, float var7);
   }
}
