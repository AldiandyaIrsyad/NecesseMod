package necesse.entity.particle;

import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsProtectorBombParticle extends Particle {
   protected static final int[] frameTimes = new int[]{30, 30, 30, 30, 30, 60, 60, 60, 60, 60, 60, 120, 120, 120, 120};
   private final long spawnTime;
   private final long delay;
   private final boolean mirror;

   public EvilsProtectorBombParticle(Level var1, float var2, float var3, long var4, long var6) {
      super(var1, var2, var3, var6 + 1500L);
      this.spawnTime = var4;
      this.delay = var6;
      this.mirror = GameRandom.globalRandom.nextBoolean();
   }

   public void clientTick() {
      super.clientTick();
      long var1 = this.getWorldEntity().getTime() - this.spawnTime;
      if (var1 >= this.delay) {
         long var3 = var1 - this.delay;
         int var5 = GameUtils.getAnim(var3, frameTimes);
         if (var5 == -1) {
            this.remove();
         } else if (var5 < 10) {
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, 0.5F);
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.x);
      int var11 = var7.getDrawY(this.y);
      long var12 = this.getWorldEntity().getTime() - this.spawnTime;
      int var14;
      if (var12 >= this.delay) {
         long var15 = var12 - this.delay;
         var14 = GameUtils.getAnim(var15, frameTimes);
         if (var14 == -1) {
            return;
         }

         int var17 = var11;
         if (var14 < 5) {
            var17 = var11 - (32 - var14 * 7);
         }

         int var18 = var14 % 5;
         int var19 = var14 / 5;
         final TextureDrawOptionsEnd var20 = MobRegistry.Textures.evilsProtectorBomb.initDraw().sprite(var18, var19, 128, 192).mirror(this.mirror, false).light(var9).posMiddle(var10, var17);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var20.draw();
            }
         });
      } else {
         var14 = 0;
      }

      var14 = Math.max(var14 - 5, 0);
      if (var14 < 5) {
         float var21 = 0.0F;
         float var16 = 1.0F;
         if (var14 == 0) {
            var21 = (float)((double)var12 / 4.0);
            var16 += (float)(Math.sin((double)var12 / 80.0) / 10.0);
         }

         TextureDrawOptionsEnd var22 = MobRegistry.Textures.evilsProtectorBomb_shadow.initDraw().sprite(var14, 0, 128, 192).mirror(this.mirror, false).rotate(var21, (int)(64.0F * var16), (int)(96.0F * var16)).size((int)(128.0F * var16), (int)(192.0F * var16)).light(var9).posMiddle(var10, var11);
         var2.add((var1x) -> {
            var22.draw();
         });
      }
   }
}
