package necesse.entity.particle;

import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderEggBrokenParticle extends Particle {
   public GameTexture texture;

   public SpiderEggBrokenParticle(Level var1, float var2, float var3, long var4, GameTexture var6) {
      super(var1, var2, var3, var4);
      this.texture = var6;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX()) - 32;
      int var11 = var7.getDrawY(this.getY()) - 40;
      float var12 = this.getLifeCyclePercent();
      float var13 = 1.0F;
      if (var12 >= 0.9F) {
         var13 = Math.abs(GameMath.limit((var12 - 0.9F) * 10.0F, 0.0F, 1.0F) - 1.0F);
      }

      TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(0, 0, 64).light(var9).alpha(var13).pos(var10, var11);
      var2.add((var1x) -> {
         var14.draw();
      });
   }
}
