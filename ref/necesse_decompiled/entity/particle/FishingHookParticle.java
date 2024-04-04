package necesse.entity.particle;

import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FishingHookParticle extends Particle {
   private static final float BLOC_DEC_PER_TICK = 0.025F;
   private float initY;
   private float blob;
   private GameSprite projectileSprite;
   private GameSprite waterSprite;

   public FishingHookParticle(Level var1, float var2, float var3, FishingRodItem var4) {
      super(var1, var2, var3, 5000L);
      this.initY = var3;
      this.projectileSprite = var4.getHookProjectileSprite();
      this.waterSprite = var4.getHookParticleSprite();
   }

   public void clientTick() {
      if (this.blob > 0.0F) {
         this.blob -= 0.025F;
      }

      if (this.blob < 0.0F) {
         this.blob = 0.0F;
      }

      int var1 = this.getLevel().getLevelTile(this.getX() / 32, (int)this.initY / 32).getLiquidBobbing();
      this.y = this.initY + this.blob / 4.0F + (float)var1;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this);
      int var10 = this.getX() - var7.getX() - 16;
      int var11 = this.getY() - var7.getY() - 26;
      final TextureDrawOptionsEnd var12;
      if (var5.isLiquidTile(this.getX() / 32, (int)this.initY / 32)) {
         int var13 = (int)(this.blob * 10.0F);
         var12 = this.waterSprite.initDrawSection(0, 32, 0, 32 - var13).light(var9).size(32, 32 - var13).pos(var10, var11);
      } else {
         var12 = this.projectileSprite.initDraw().light(var9).pos(var10, var11 + 10);
      }

      var1.add(new EntityDrawable(this) {
         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void blob() {
      this.blob = 1.0F;
   }
}
