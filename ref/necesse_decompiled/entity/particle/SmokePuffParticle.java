package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SmokePuffParticle extends Particle {
   Color color;
   int res;

   public SmokePuffParticle(Level var1, float var2, float var3, int var4, Color var5) {
      super(var1, var2, var3, 500L);
      this.color = var5;
      this.res = var4;
   }

   public SmokePuffParticle(Level var1, float var2, float var3, Color var4) {
      this(var1, var2, var3, 64, var4);
   }

   public SmokePuffParticle(Level var1, float var2, float var3) {
      this(var1, var2, var3, new Color(255, 255, 255));
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9 = this.getLifeCyclePercent();
      if (!this.removed()) {
         GameLight var10 = var5.getLightLevel(this);
         int var11 = this.getX() - var7.getX() - this.res / 2;
         int var12 = this.getY() - var7.getY() - (int)(0.84375F * (float)this.res);
         int var13 = var9 < 0.5F ? 0 : 1;
         byte var14 = 4;
         if (var9 < 0.1F) {
            var14 = 0;
         } else if (var9 < 0.2F) {
            var14 = 1;
         } else if (var9 < 0.3F) {
            var14 = 2;
         } else if (var9 < 0.4F) {
            var14 = 3;
         } else if (var9 < 0.5F) {
            var14 = 4;
         } else if (var9 < 0.6F) {
            var14 = 0;
         } else if (var9 < 0.7F) {
            var14 = 1;
         } else if (var9 < 0.8F) {
            var14 = 2;
         } else if (var9 < 0.9F) {
            var14 = 3;
         }

         final TextureDrawOptionsEnd var15 = GameResources.smokeParticles.initDraw().sprite(var14, var13, 64).colorLight(this.color, var10).size(this.res, this.res).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var15.draw();
            }
         });
      }
   }
}
