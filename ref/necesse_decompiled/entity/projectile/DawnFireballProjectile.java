package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class DawnFireballProjectile extends FollowingProjectile {
   private long spawnTime;
   boolean empowered = false;

   public DawnFireballProjectile() {
   }

   public DawnFireballProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9, boolean var10) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setAngle(var5);
      this.speed = var6;
      this.distance = var7;
      this.setDamage(var8);
      this.knockback = var9;
      this.empowered = var10;
   }

   public void init() {
      super.init();
      this.setWidth(16.0F);
      this.turnSpeed = this.empowered ? 0.25F : 0.15F;
      this.height = 18.0F;
      this.spawnTime = this.getWorldEntity().getTime();
      this.trailOffset = 0.0F;
      this.isSolid = false;
      this.givesLight = true;
   }

   public Color getParticleColor() {
      return new Color(249, 155, 78);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(255, 233, 73), 10.0F, 400, this.getHeight());
   }

   public void updateTarget() {
      if (this.traveledDistance > 50.0F) {
         this.findTarget((var0) -> {
            return var0.isHostile;
         }, 0.0F, 250.0F);
      }

   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         if (var1 != null) {
            ActiveBuff var5 = new ActiveBuff(BuffRegistry.Debuffs.SPIDER_VENOM, var1, 10.0F, this.getOwner());
            var1.addBuff(var5, true);
            if (this.modifier != null) {
               this.modifier.doHitLogic(var1, var2, var3, var4);
            }
         }

      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate((float)(-(this.getWorldEntity().getTime() - this.spawnTime)), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
      }
   }
}
