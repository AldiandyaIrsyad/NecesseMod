package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PhantomMissileProjectile extends FollowingProjectile {
   public PhantomMissileProjectile() {
      this.height = 18.0F;
   }

   public PhantomMissileProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this();
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.setDistance(var8);
      this.setDamage(var9);
      this.knockback = var10;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.height);
      var1.putNextFloat(this.turnSpeed);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.height = var1.getNextFloat();
      this.turnSpeed = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.turnSpeed = 0.3F;
      this.givesLight = true;
      this.trailOffset = -8.0F;
      this.clearTargetPosWhenAligned = true;
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(108, 37, 92), 30.0F, 250, this.height);
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   protected Color getWallHitColor() {
      return new Color(108, 37, 92);
   }

   public void refreshParticleLight() {
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 260.0F, this.lightSaturation);
   }

   public void updateTarget() {
      this.findTarget((var0) -> {
         return var0.isHostile;
      }, 100.0F, 300.0F);
      if (this.target != null) {
         this.targetPos = null;
      }

   }

   public float getTurnSpeed(int var1, int var2, float var3) {
      return this.getTurnSpeed(var3) * this.getTurnSpeedMod(var1, var2, 20.0F, 90.0F, 160.0F);
   }

   public float getTurnSpeedMod(int var1, int var2, float var3, float var4, float var5) {
      float var6 = (float)(new Point(var1, var2)).distance((double)this.getX(), (double)this.getY());
      if (var6 < var5 && var6 > 5.0F) {
         float var7 = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)var1, (float)var2)));
         float var8 = var7 > var4 ? 1.0F : (var7 - var4) / var4;
         float var9 = Math.abs(var6 - var5) / var5;
         return 1.0F + var9 * var3 + var8 * var3;
      } else {
         return 1.0F;
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9.minLevelCopy(Math.min(var9.getLevel() + 100.0F, 150.0F))).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }
}
