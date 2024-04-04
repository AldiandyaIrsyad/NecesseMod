package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BloodClawProjectile extends Projectile {
   private long spawnTime;
   protected float angleChangeCounter = 0.2F;
   protected Trail trail;

   public BloodClawProjectile() {
   }

   public BloodClawProjectile(Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = var6;
      this.setDamage(var8);
      this.setOwner(var9);
      this.setDistance(var7);
   }

   public void init() {
      super.init();
      this.piercing = 3;
      this.height = 16.0F;
      this.setWidth(45.0F, true);
      this.isSolid = true;
      this.bouncing = 0;
      this.givesLight = true;
      this.particleRandomOffset = 14.0F;
      this.spawnTime = this.getWorldEntity().getTime();
   }

   public Color getParticleColor() {
      return new Color(145, 0, 0);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(164, 0, 0), this.getTrailThickness(), 340, this.getHeight());
   }

   public float getTrailThickness() {
      return this.traveledDistance / 18.0F;
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      if (this.traveledDistance > (float)this.distance * this.angleChangeCounter) {
         this.angleChangeCounter += 0.2F;
         GameRandom var4 = new GameRandom((long)(this.angleChangeCounter * (float)this.getUniqueID() * (float)GameRandom.prime(this.getUniqueID())));
         this.setAngle(this.angle += (float)var4.getIntBetween(-50, 50));
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.angleChangeCounter);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.angleChangeCounter = var1.getNextFloat();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         float var9 = this.getFadeAlphaTime(300, 200);
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y - this.getHeight()) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var10.minLevelCopy(Math.min(var10.getLevel() + 100.0F, 150.0F))).rotate((float)(this.getWorldEntity().getTime() - this.spawnTime)).alpha(var9).color(new Color(164, 0, 0)).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
      }
   }
}
