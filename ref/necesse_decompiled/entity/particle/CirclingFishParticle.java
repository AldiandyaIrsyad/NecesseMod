package necesse.entity.particle;

import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CirclingFishParticle extends Particle {
   private boolean hMirror;
   private boolean vMirror;
   private GameTexture animTexture;
   private int spriteRes;

   public CirclingFishParticle(Level var1, float var2, float var3, long var4, GameTexture var6, int var7) {
      super(var1, var2, var3, var4);
      this.animTexture = var6;
      this.spriteRes = var7;
      this.hMirror = GameRandom.globalRandom.nextBoolean();
      this.vMirror = GameRandom.globalRandom.nextBoolean();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this);
      int var10 = var7.getDrawX(this.x) - this.spriteRes / 2;
      int var11 = var7.getDrawY(this.y) - this.spriteRes / 2 + 4;
      int var12 = this.animTexture.getWidth() / this.spriteRes;
      long var13 = this.getRemainingLifeTime();
      byte var15 = 125;
      int var16 = var12 - (int)(var13 / (long)var15);
      if (var16 >= 0) {
         float var17 = Math.min(1.0F, this.getLifeCyclePercent() * 1.5F);
         TextureDrawOptionsEnd var18 = this.animTexture.initDraw().sprite(var16, 0, this.spriteRes).mirror(this.hMirror, this.vMirror).light(var9).alpha(var17).pos(var10, var11);
         var2.add((var1x) -> {
            var18.draw();
         });
      }

   }
}
