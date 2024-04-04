package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
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

public class CryptVampireBoltProjectile extends FollowingProjectile {
   public CryptVampireBoltProjectile() {
   }

   public CryptVampireBoltProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.x = var3;
      this.y = var4;
      this.speed = var7;
      this.setTarget(var5, var6);
      this.setDamage(var9);
      this.knockback = var10;
      this.setDistance(var8);
      this.setOwner(var2);
   }

   public void init() {
      super.init();
      this.turnSpeed = 0.13F;
      this.givesLight = true;
      this.height = 10.0F;
      this.piercing = 0;
      this.isSolid = true;
      this.particleDirOffset = -30.0F;
      this.particleRandomOffset = 3.0F;
      this.setWidth(5.0F);
   }

   public float getTurnSpeed(int var1, int var2, float var3) {
      return this.getTurnSpeed(var3) * this.invDynamicTurnSpeedMod(var1, var2, (float)this.getTurnRadius());
   }

   public Color getParticleColor() {
      return new Color(177, 5, 0);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(177, 5, 0), 16.0F, 500, 10.0F);
   }

   public void updateTarget() {
      if (!this.isClient()) {
         if (this.target != null && this.target != this.getOwner()) {
            if (!this.isSamePlace(this.target) || ((Mob)this.target).getDistance(this.getOwner()) > 960.0F) {
               this.target = this.getOwner();
               this.sendServerTargetUpdate();
            }
         } else {
            ServerClient var1 = (ServerClient)GameUtils.streamServerClients(this.getLevel()).min(Comparator.comparing((var1x) -> {
               return var1x.playerMob.getDistance(this.getOwner());
            })).orElse((Object)null);
            if (var1 != null && var1.playerMob != this.getOwner() && var1.playerMob.getDistance(this.getOwner()) < 960.0F) {
               this.target = var1.playerMob;
               this.sendServerTargetUpdate();
            }
         }

      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - 16;
         int var11 = var7.getDrawY(this.y);
         int var12 = GameUtils.getAnim(this.getWorldEntity().getTime(), 6, 400);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12, 0, 32, 64).light(var9).rotate(this.getAngle(), 16, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         TextureDrawOptionsEnd var14 = this.shadowTexture.initDraw().sprite(var12, 0, 32, 64).light(var9).rotate(this.getAngle(), 16, 0).pos(var10, var11);
         var2.add((var1x) -> {
            var14.draw();
         });
      }
   }
}
