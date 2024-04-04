package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.MobRegistry;
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
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoShardProjectile extends Projectile {
   public GameTextureSection shardTexture;
   protected long spawnTime;

   public CryoShardProjectile() {
   }

   public CryoShardProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDistance(var6);
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
   }

   public void init() {
      super.init();
      this.isSolid = false;
      this.setWidth(10.0F);
      this.spawnTime = this.getLevel().getWorldEntity().getTime();
      this.shardTexture = MobRegistry.Textures.cryoQueen == null ? null : (new GameTextureSection(MobRegistry.Textures.cryoQueen)).sprite(GameRandom.globalRandom.nextInt(5), 16, 32);
   }

   public Color getParticleColor() {
      return new Color(130, 155, 227);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.shardTexture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.shardTexture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.shardTexture.initDraw().light(var9).rotate(this.getAngle(), this.shardTexture.getWidth() / 2, this.shardTexture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }
}
