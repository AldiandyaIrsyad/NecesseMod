package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public abstract class BombProjectile extends Projectile {
   public float startHeight;
   public float throwHeight;
   protected long spawnTime;
   protected float finalAngle;
   protected boolean stopsRotatingOnStationary;
   protected boolean isStationary;
   protected float lastSpeed;
   protected float particleTicker;

   public BombProjectile() {
      this.finalAngle = -1.0F;
      this.stopsRotatingOnStationary = false;
   }

   public BombProjectile(float var1, float var2, float var3, float var4, int var5, int var6, GameDamage var7, Mob var8) {
      this();
      this.setLevel(var8.getLevel());
      this.applyData(var1, var2, var3, var4, (float)var5, var6, var7, 0, var8);
   }

   public void init() {
      super.init();
      this.givesLight = true;
      this.setWidth(15.0F);
      this.startHeight = 18.0F;
      this.throwHeight = (float)this.distance * 0.1F;
      this.height = 18.0F;
      this.spawnTime = this.getWorldEntity().getTime();
      this.doesImpactDamage = false;
      this.trailOffset = 0.0F;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.isStationary);
      if (this.isStationary) {
         var1.putNextFloat(this.lastSpeed);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isStationary = var1.getNextBoolean();
      if (this.isStationary) {
         this.lastSpeed = var1.getNextFloat();
      }

   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(160, 160, 160), 14.0F, 150, 18.0F);
   }

   public void refreshParticleLight() {
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, 0.5F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.texture.getHeight() / 2);
      }
   }

   public float getAngle() {
      return this.finalAngle != -1.0F ? this.finalAngle : (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }

   public float tickMovement(float var1) {
      float var2 = super.tickMovement(var1);
      this.updateHeight(var1);
      return var2;
   }

   public void serverTick() {
      super.serverTick();
      if (this.spawnTime + (long)this.getFuseTime() < this.getWorldEntity().getTime()) {
         ExplosionEvent var1 = this.getExplosionEvent(this.x, this.y);
         this.getLevel().entityManager.addLevelEvent(var1);
         this.remove();
      }

   }

   public void clientTick() {
      super.clientTick();

      for(this.particleTicker += 0.5F; this.particleTicker >= 1.0F; --this.particleTicker) {
         Point2D.Float var1 = GameMath.getAngleDir(GameMath.fixAngle(this.getParticleAngle() + this.getAngle()));
         float var2 = this.height + 2.0F - (float)((int)(var1.y * this.getParticleDistance()));
         spawnFuseParticle(this.getLevel(), this.x + var1.x * this.getParticleDistance(), this.y + 2.0F, var2);
      }

   }

   private void updateHeight(float var1) {
      float var2;
      float var3;
      float var4;
      if (this.isStationary) {
         this.traveledDistance += this.lastSpeed * var1 / 250.0F;
         var2 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
         var3 = (float)Math.max(0, (int)(this.startHeight * Math.abs(var2 - 1.0F)));
         var4 = GameMath.sin(var2 * 180.0F) * this.throwHeight;
         this.height = var3 + var4;
         if (var2 >= 1.0F) {
            this.finalAngle = this.getAngle();
         }
      } else {
         var2 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
         var3 = (float)Math.max(0, (int)(this.startHeight * Math.abs(var2 - 1.0F)));
         var4 = GameMath.sin(var2 * 180.0F) * this.throwHeight;
         this.height = var3 + var4;
      }

   }

   public void onHit(Mob var1, LevelObjectHit var2, float var3, float var4, boolean var5, ServerClient var6) {
      if (var1 == null) {
         if (!this.isStationary) {
            this.lastSpeed = this.speed;
            this.speed = 0.0F;
            if (this.stopsRotatingOnStationary) {
               this.finalAngle = this.getAngle();
            }

            this.isStationary = true;
         }

      }
   }

   public void checkRemoved() {
      if (!this.isStationary && this.traveledDistance >= (float)this.distance) {
         this.lastSpeed = this.speed;
         this.speed = 0.0F;
         if (this.stopsRotatingOnStationary) {
            this.finalAngle = this.getAngle();
         }

         this.isStationary = true;
      }

   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("explosion", 3);
   }

   public abstract int getFuseTime();

   public abstract float getParticleAngle();

   public abstract float getParticleDistance();

   public abstract ExplosionEvent getExplosionEvent(float var1, float var2);

   public int getOwnerToolTier() {
      Mob var1 = this.getOwner();
      if (var1 != null && var1.isPlayer) {
         PlayerInventoryManager var2 = ((PlayerMob)var1).getInv();
         return var2.streamSlots(false, false, false).map((var1x) -> {
            return var1x.getItem(var2);
         }).filter((var0) -> {
            return var0 != null && var0.item instanceof ToolDamageItem;
         }).mapToInt((var0) -> {
            return ((ToolDamageItem)var0.item).getToolTier(var0);
         }).max().orElse(-1);
      } else {
         return -1;
      }
   }

   public static void spawnFuseParticle(Level var0, float var1, float var2, float var3) {
      spawnFuseParticle(var0, var1, var2, var3, ParticleOption.defaultFlameHue, ParticleOption.defaultSmokeHue);
   }

   public static void spawnFuseParticle(Level var0, float var1, float var2, float var3, float var4, float var5) {
      var0.entityManager.addParticle(var1 + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), var2, Particle.GType.COSMETIC).heightMoves(var3, var3 + 8.0F).flameColor(var4).sizeFades(8, 12).lifeTime(1000).onProgress(0.8F, (var3x) -> {
         for(int var4 = 0; var4 < GameRandom.globalRandom.getIntBetween(1, 2); ++var4) {
            var0.entityManager.addParticle(var3x.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), var3x.y, Particle.GType.COSMETIC).smokeColor(var5).sizeFades(8, 12).heightMoves(var3 + 6.0F, var3 + 26.0F);
         }

      });
   }
}
