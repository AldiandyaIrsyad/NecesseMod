package necesse.entity.particle;

import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveSpiderSpitParticle extends Particle {
   public GiantCaveSpiderMob.Variant variant;
   public int sprite;

   public CaveSpiderSpitParticle(Level var1, float var2, float var3, long var4, GiantCaveSpiderMob.Variant var6) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(4);
      this.variant = var6;
   }

   public void despawnNow() {
      if (this.getRemainingLifeTime() > 500L) {
         this.lifeTime = 500L;
         this.spawnTime = this.getWorldEntity().getLocalTime();
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX()) - 48;
      int var11 = var7.getDrawY(this.getY()) - 48;
      long var12 = this.getRemainingLifeTime();
      float var14 = 1.0F;
      if (var12 < 500L) {
         var14 = Math.max(0.0F, (float)var12 / 500.0F);
      }

      TextureDrawOptionsEnd var15 = ((MobTexture)this.variant.texture.get()).body.initDraw().sprite(4 + this.sprite, 4, 96).light(var9).alpha(var14).pos(var10, var11);
      var2.add((var1x) -> {
         var15.draw();
      });
   }
}
