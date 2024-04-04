package necesse.entity.particle;

import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveSpiderWebParticle extends Particle {
   public int sprite;

   public CaveSpiderWebParticle(Level var1, float var2, float var3, long var4) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(4);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX()) - 48;
      int var11 = var7.getDrawY(this.getY()) - 48;
      float var12 = this.getLifeCyclePercent();
      float var13 = 1.0F;
      if (var12 >= 0.9F) {
         var13 = Math.abs(GameMath.limit((var12 - 0.9F) * 10.0F, 0.0F, 1.0F) - 1.0F);
      }

      TextureDrawOptionsEnd var14 = MobRegistry.Textures.giantCaveSpider.body.initDraw().sprite(this.sprite, 4, 96).light(var9).alpha(var13).pos(var10, var11);
      var2.add((var1x) -> {
         var14.draw();
      });
   }
}
