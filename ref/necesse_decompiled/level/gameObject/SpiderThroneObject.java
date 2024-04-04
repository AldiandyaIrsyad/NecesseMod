package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class SpiderThroneObject extends StaticMultiObject {
   protected GameTexture texture;

   public SpiderThroneObject(int var1, int var2, int var3, int var4, int[] var5, Rectangle var6) {
      super(var1, var2, var3, var4, var5, var6, "spiderthrone");
      this.stackSize = 1;
      this.rarity = Item.Rarity.LEGENDARY;
      this.mapColor = new Color(130, 105, 52);
      this.objectHealth = 500;
      this.toolType = ToolType.ALL;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.texturePath);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameTexture var9 = this.texture;
      final DrawOptions var10 = this.getMultiTextureDrawOptions(var9, var3, var4, var5, var7);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var10.draw();
         }
      });
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return false;
   }

   public static int[] registerSpiderThrone() {
      int[] var0 = new int[8];
      Rectangle var1 = new Rectangle(0, 0, 128, 64);
      var0[0] = ObjectRegistry.registerObject("spiderthrone", new SpiderThroneObject(0, 0, 4, 2, var0, var1), 0.0F, true);
      var0[1] = ObjectRegistry.registerObject("spiderthrone2", new SpiderThroneObject(1, 0, 4, 2, var0, var1), 0.0F, false);
      var0[2] = ObjectRegistry.registerObject("spiderthrone3", new SpiderThroneObject(2, 0, 4, 2, var0, var1), 0.0F, false);
      var0[3] = ObjectRegistry.registerObject("spiderthrone4", new SpiderThroneObject(3, 0, 4, 2, var0, var1), 0.0F, false);
      var0[4] = ObjectRegistry.registerObject("spiderthrone5", new SpiderThroneObject(0, 1, 4, 2, var0, var1), 0.0F, false);
      var0[5] = ObjectRegistry.registerObject("spiderthrone6", new SpiderThroneObject(1, 1, 4, 2, var0, var1), 0.0F, false);
      var0[6] = ObjectRegistry.registerObject("spiderthrone7", new SpiderThroneObject(2, 1, 4, 2, var0, var1), 0.0F, false);
      var0[7] = ObjectRegistry.registerObject("spiderthrone8", new SpiderThroneObject(3, 1, 4, 2, var0, var1), 0.0F, false);
      return var0;
   }
}
