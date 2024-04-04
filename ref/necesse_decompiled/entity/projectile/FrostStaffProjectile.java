package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrostStaffProjectile extends Projectile {
   private float startVelocity;
   private float maxVelocity;
   private float reachMaxVelocityAtDistance;

   public FrostStaffProjectile() {
   }

   public FrostStaffProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, GameDamage var11, int var12) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setDir(var5, var6);
      this.speed = var7;
      this.startVelocity = var7;
      this.maxVelocity = var8;
      this.reachMaxVelocityAtDistance = var9;
      this.distance = var10;
      this.setDamage(var11);
      this.knockback = var12;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.startVelocity);
      var1.putNextFloat(this.maxVelocity);
      var1.putNextFloat(this.reachMaxVelocityAtDistance);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startVelocity = var1.getNextFloat();
      this.maxVelocity = var1.getNextFloat();
      this.reachMaxVelocityAtDistance = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 0;
      this.width = 6.0F;
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      float var4 = this.traveledDistance / this.reachMaxVelocityAtDistance;
      this.speed = GameMath.lerp(var4, this.startVelocity, this.maxVelocity);
   }

   public Color getParticleColor() {
      return new Color(63, 105, 151);
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      var1.lifeTime(1000);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(63, 105, 151), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }
}
