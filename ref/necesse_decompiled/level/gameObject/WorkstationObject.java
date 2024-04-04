package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WorkstationObject extends CraftingStationObject {
   public GameTexture texture;

   public WorkstationObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(150, 119, 70);
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/workstation");
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return var4 % 2 == 0 ? new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 28, 20) : new Rectangle(var2 * 32 + 6, var3 * 32 + 2, 20, 28);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      int var12 = var3.getObjectRotation(var4, var5) % 4;
      int var13 = var12 % 2 == 0 ? -2 : 0;
      final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(var12 % 4, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32 + var13);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      int var10 = var4 % 2 == 0 ? -2 : 0;
      this.texture.initDraw().sprite(var4 % 4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32 + var10);
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      super.interact(var1, var2, var3, var4);
      if (var1.isClient() && var1.getClient().getPlayer() == var4) {
         var1.getClient().tutorial.usedWorkstation();
      }

   }

   public Tech[] getCraftingTechs() {
      return new Tech[]{RecipeTechRegistry.WORKSTATION, RecipeTechRegistry.NONE};
   }
}
