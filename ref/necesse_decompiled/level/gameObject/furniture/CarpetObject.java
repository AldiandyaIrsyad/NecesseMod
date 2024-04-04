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

public class CarpetObject extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;
   private int counterIDTopRight;
   private int counterIDBotLeft;
   private int counterIDBotRight;

   protected CarpetObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(0, 0));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "carpet";
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(0, 0, 2, 2, true, new int[]{this.getID(), this.counterIDTopRight, this.counterIDBotLeft, this.counterIDBotRight});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11);
      var2.add((var1x) -> {
         var12.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9);
      this.texture.initDraw().sprite(1, 0, 32).alpha(var5).draw(var8 + 32, var9);
      this.texture.initDraw().sprite(0, 1, 32).alpha(var5).draw(var8, var9 + 32);
      this.texture.initDraw().sprite(1, 1, 32).alpha(var5).draw(var8 + 32, var9 + 32);
   }

   protected void setCounterIDs(int var1, int var2, int var3) {
      this.counterIDTopRight = var1;
      this.counterIDBotLeft = var2;
      this.counterIDBotRight = var3;
   }

   public static int[] registerCarpet(String var0, String var1, ToolType var2, Color var3, float var4) {
      CarpetObject var5 = new CarpetObject(var1, var2, var3);
      CarpetRObject var6 = new CarpetRObject(var1, var2, var3);
      CarpetDObject var7 = new CarpetDObject(var1, var2, var3);
      CarpetDRObject var8 = new CarpetDRObject(var1, var2, var3);
      int var9 = ObjectRegistry.registerObject(var0, var5, var4, true);
      int var10 = ObjectRegistry.registerObject(var0 + "r", var6, 0.0F, false);
      int var11 = ObjectRegistry.registerObject(var0 + "d", var7, 0.0F, false);
      int var12 = ObjectRegistry.registerObject(var0 + "dr", var8, 0.0F, false);
      var5.setCounterIDs(var10, var11, var12);
      var6.setCounterIDs(var9, var11, var12);
      var7.setCounterIDs(var9, var10, var12);
      var8.setCounterIDs(var9, var10, var11);
      return new int[]{var9, var10, var11, var12};
   }

   public static int[] registerCarpet(String var0, String var1, Color var2, float var3) {
      return registerCarpet(var0, var1, ToolType.ALL, var2, var3);
   }
}
