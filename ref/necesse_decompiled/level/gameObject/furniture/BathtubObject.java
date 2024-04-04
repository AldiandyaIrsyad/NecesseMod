package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class BathtubObject extends FurnitureObject {
   protected String texturePath;
   public GameTexture texture;
   protected int counterID;

   private BathtubObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(8, 10, 24, 2));
      this.texturePath = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "bathtub";
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(0, 0, 2, 1, true, new int[]{this.getID(), this.counterID});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile(this.texturePath);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11);
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(0, 1, 32).light(var9).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 5;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 28;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 1, 32).alpha(var5).draw(var8, var9);
      this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9);
      this.texture.initDraw().sprite(1, 1, 32).alpha(var5).draw(var8 + 32, var9);
      this.texture.initDraw().sprite(1, 0, 32).alpha(var5).draw(var8 + 32, var9);
   }

   public static int[] registerBathtub(String var0, String var1, ToolType var2, Color var3, float var4) {
      String var5 = "objects/" + var1;
      BathtubObject var6 = new BathtubObject(var5, var2, var3);
      Bathtub2Object var7 = new Bathtub2Object(var5, var2, var3);
      int var8 = ObjectRegistry.registerObject(var0, var6, var4, true);
      int var9 = ObjectRegistry.registerObject(var0 + "2", var7, 0.0F, false);
      var6.counterID = var9;
      var7.counterID = var8;
      return new int[]{var8, var9};
   }

   public static int[] registerBathtub(String var0, String var1, Color var2, float var3) {
      return registerBathtub(var0, var1, ToolType.ALL, var2, var3);
   }
}
