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

public class EvilsProtectorAttack2Projectile extends FollowingProjectile {
   public EvilsProtectorAttack2Projectile() {
   }

   public EvilsProtectorAttack2Projectile(Level var1, Mob var2, float var3, float var4, Mob var5, GameDamage var6) {
      this.setLevel(var1);
      this.x = var3;
      this.y = var4;
      this.speed = 60.0F;
      this.setTarget(var5.x, var5.y);
      this.target = var5;
      this.setDamage(var6);
      this.knockback = 0;
      this.setDistance(2000);
      this.setOwner(var2);
   }

   public EvilsProtectorAttack2Projectile(Level var1, Mob var2, Mob var3, GameDamage var4) {
      this(var1, var2, var2.x, var2.y, var3, var4);
   }

   public void init() {
      super.init();
      this.turnSpeed = 0.13F;
      this.givesLight = true;
      this.height = 16.0F;
      this.piercing = 0;
      this.isSolid = false;
      this.particleDirOffset = -24.0F;
      this.trailOffset = -24.0F;
      this.setWidth(8.0F);
   }

   public float getTurnSpeed(int var1, int var2, float var3) {
      return this.getTurnSpeed(var3) * this.invDynamicTurnSpeedMod(var1, var2, (float)this.getTurnRadius());
   }

   public Color getParticleColor() {
      return new Color(50, 0, 45);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(50, 0, 45), 6.0F, 500, 18.0F);
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
