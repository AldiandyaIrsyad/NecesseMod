package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RainBlobParticle extends Particle {
   public GameTextureSection texture;
   public int spriteRes;
   public Color color;
   public float alpha;

   public RainBlobParticle(Level var1, float var2, float var3, GameTextureSection var4, int var5, Color var6, float var7) {
      super(var1, var2, var3, 200L);
      this.texture = var4;
      this.spriteRes = var5;
      this.color = var6;
      this.alpha = (float)var6.getAlpha() * var7 / 255.0F;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9 = this.getLifeCyclePercent();
      if (!this.removed()) {
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.getX()) - this.spriteRes / 2;
         int var12 = var7.getDrawY(this.getY()) - this.spriteRes / 2;
         int var13 = this.texture.getWidth() / this.spriteRes;
         final TextureDrawOptionsEnd var14 = this.texture.sprite((int)(var9 * (float)var13), 0, this.spriteRes).initDraw().color(this.color).alpha(this.alpha).light(var10).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var14.draw();
            }
         });
      }
   }
}
