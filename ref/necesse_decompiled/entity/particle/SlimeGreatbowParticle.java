package necesse.entity.particle;

import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeGreatbowParticle extends Particle {
   public int sprite;
   GameTextureSection texture;

   public SlimeGreatbowParticle(Level var1, float var2, float var3, long var4) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(4);
   }

   public void despawnNow() {
      if (this.getRemainingLifeTime() > 500L) {
         this.lifeTime = 500L;
         this.spawnTime = this.getWorldEntity().getLocalTime();
      }

   }

   public void init() {
      super.init();
      GameTexture var1 = GameResources.slimeGreatbowSlime;
      int var2 = var1.getHeight();
      int var3 = GameRandom.globalRandom.nextInt(var1.getWidth() / var2);
      this.texture = (new GameTextureSection(GameResources.slimeGreatbowSlime)).sprite(var3, 0, var2);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX());
      int var11 = var7.getDrawY(this.getY());
      long var12 = this.getRemainingLifeTime();
      float var14 = 1.0F;
      if (var12 < 500L) {
         var14 = Math.max(0.0F, (float)var12 / 500.0F);
      }

      final TextureDrawOptionsEnd var15 = this.texture.initDraw().light(var9).alpha(var14).posMiddle(var10, var11);
      var1.add(new LevelSortedDrawable(this) {
         public int getSortY() {
            return SlimeGreatbowParticle.this.getY();
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }
}
