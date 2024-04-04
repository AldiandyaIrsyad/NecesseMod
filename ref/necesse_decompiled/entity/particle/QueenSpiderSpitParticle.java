package necesse.entity.particle;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.QueenSpiderSpitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class QueenSpiderSpitParticle extends Particle {
   public int sprite;
   public boolean mirror;

   public QueenSpiderSpitParticle(Level var1, float var2, float var3, long var4) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(2);
      this.mirror = GameRandom.globalRandom.nextBoolean();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX());
      int var11 = var7.getDrawY(this.getY());
      long var12 = this.getRemainingLifeTime();
      float var14 = 1.0F;
      if (var12 < 500L) {
         var14 = (float)var12 / 500.0F;
      }

      TextureDrawOptionsEnd var15 = MobRegistry.Textures.queenSpider_spit.initDraw().sprite(this.sprite, 0, 64).mirror(this.mirror, false).light(var9).alpha(var14).posMiddle(var10, var11);
      var2.add((var3x) -> {
         if (GlobalData.debugActive()) {
            Rectangle var4 = QueenSpiderSpitEvent.hitBox;
            Screen.drawShape(new Rectangle(this.getX() + var4.x, this.getY() + var4.y, var4.width, var4.height), var7, false, 1.0F, 0.0F, 0.0F, 1.0F);
         }

         var15.draw();
      });
   }
}
