package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HydroPumpParticle extends Particle {
   private Color color;
   private int fadeInTime;
   private int stayTime;
   private int fadeOutTime;

   public HydroPumpParticle(Level var1, float var2, float var3, Color var4, int var5, int var6, int var7) {
      super(var1, var2, var3, (long)(var5 + var6 + var5));
      this.color = var4;
      this.fadeInTime = var5;
      this.stayTime = var6;
      this.fadeOutTime = var7;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX()) - 24;
      int var11 = var7.getDrawY(this.getY()) - 24;
      byte var12 = 0;
      long var13 = this.getLifeCycleTime();
      byte var15 = 0;
      float var16 = 1.0F;
      if (var13 < (long)this.fadeInTime) {
         var16 = (float)var13 / (float)this.fadeInTime;
      } else {
         var13 -= (long)this.fadeInTime;
         if (var13 >= (long)this.stayTime) {
            var13 -= (long)this.stayTime;
            if (var13 < (long)this.fadeOutTime) {
               var16 = Math.abs((float)var13 / (float)this.fadeOutTime - 1.0F);
            } else {
               var16 = 0.0F;
            }
         }
      }

      int var17 = GameUtils.getAnim(var13, 5, 400);
      TextureDrawOptionsEnd var18 = GameResources.hydroPumpParticles.initDraw().sprite(var17, var15, 48, 96).colorLight(this.color, var9).alpha(var16).pos(var10, var11 - var12);
      var2.add((var1x) -> {
         var18.draw();
      });
   }
}
