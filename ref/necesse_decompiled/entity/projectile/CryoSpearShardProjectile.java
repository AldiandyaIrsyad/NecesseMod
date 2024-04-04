package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
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
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoSpearShardProjectile extends Projectile {
   private long spawnTime;
   private float startSpeed;
   private int sprite;

   public CryoSpearShardProjectile() {
   }

   public CryoSpearShardProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9, int var10) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDistance(var6);
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
      this.height = (float)var10;
   }

   public void init() {
      super.init();
      this.startSpeed = this.speed;
      this.setWidth(10.0F);
      this.spawnTime = this.getLevel().getWorldEntity().getTime();
      if (this.texture != null) {
         this.sprite = GameRandom.globalRandom.nextInt(this.texture.getHeight() / 32);
      }

   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      if (this.startSpeed != 0.0F) {
         float var4 = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F) - 1.0F);
         this.speed = Math.max(10.0F, var4 * this.startSpeed);
      }
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && this.startSpeed != 0.0F;
   }

   public Color getParticleColor() {
      return new Color(203, 169, 136);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         GameTextureSection var10 = (new GameTextureSection(this.texture)).sprite(0, this.sprite, 32);
         GameTextureSection var11 = (new GameTextureSection(this.shadowTexture)).sprite(0, this.sprite, 32);
         int var12 = var7.getDrawX(this.x) - var10.getWidth() / 2;
         int var13 = var7.getDrawY(this.y) - var10.getHeight() / 2;
         final TextureDrawOptionsEnd var14 = var10.initDraw().light(var9).rotate(this.getAngle(), var10.getWidth() / 2, var10.getHeight() / 2).pos(var12, var13 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var14.draw();
            }
         });
         TextureDrawOptionsEnd var15 = var11.initDraw().light(var9).rotate(this.getAngle(), var10.getWidth() / 2, var10.getHeight() / 2).pos(var12, var13);
         var2.add((var1x) -> {
            var15.draw();
         });
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }
}
