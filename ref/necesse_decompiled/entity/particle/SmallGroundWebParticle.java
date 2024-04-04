package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SmallGroundWebParticle extends Particle {
   public int sprite;

   public SmallGroundWebParticle(Level var1, float var2, float var3, long var4) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(4);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX()) - 16;
      int var11 = var7.getDrawY(this.getY()) - 16;
      float var12 = this.getLifeCyclePercent();
      long var13 = this.getRemainingLifeTime();
      float var15 = Math.max(0.0F, (float)var13 / 500.0F);
      TextureDrawOptionsEnd var16 = GameResources.webParticles.initDraw().sprite(this.sprite, 0, 32).color(new Color(204, 195, 177)).light(var9).pos(var10, var11).alpha(var15);
      var2.add((var1x) -> {
         var16.draw();
      });
   }
}
