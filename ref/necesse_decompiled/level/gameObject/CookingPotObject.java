package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CookingPotObject extends CampfireAddonObject {
   public GameTexture texture;
   public GameTexture potTexture;

   public CookingPotObject() {
      this.mapColor = new Color(115, 110, 102);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/campfire");
      this.potTexture = GameTexture.fromFile("objects/campfire_pot");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).pos(var10, var11 - (this.texture.getHeight() - 32));
      final TextureDrawOptionsEnd var13 = this.potTexture.initDraw().light(var9).pos(var10, var11 - (this.potTexture.getHeight() - 32));
      var2.add((var1x) -> {
         var12.draw();
      });
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 32));
      this.potTexture.initDraw().alpha(var5).draw(var8, var9 - (this.potTexture.getHeight() - 32));
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }

   public Tech[] getCraftingTechs() {
      return new Tech[]{RecipeTechRegistry.COOKING_POT};
   }
}
