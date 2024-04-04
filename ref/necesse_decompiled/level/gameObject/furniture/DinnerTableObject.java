package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class DinnerTableObject extends TableObject {
   protected String textureName;
   public GameTexture texture;
   protected int counterID;

   private DinnerTableObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(32, 32), var3);
      this.textureName = var1;
      this.toolType = var2;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 1, 1, 2, var1, true, new int[]{this.counterID, this.getID()});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 6, var3 * 32, 20, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 26, 20);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 20, 26) : new Rectangle(var2 * 32, var3 * 32 + 6, 26, 20);
      }
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
      final DrawOptionsList var12 = new DrawOptionsList();
      byte var13 = var3.getObjectRotation(var4, var5);
      if (var13 == 0) {
         var12.add(this.texture.initDraw().sprite(3, 3, 32).light(var9).pos(var10, var11));
      } else if (var13 == 1) {
         var12.add(this.texture.initDraw().sprite(0, 1, 32, 64).light(var9).pos(var10, var11 - 32));
      } else if (var13 == 2) {
         var12.add(this.texture.initDraw().sprite(2, 1, 32).light(var9).pos(var10, var11 - 32));
         var12.add(this.texture.initDraw().sprite(2, 2, 32).light(var9).pos(var10, var11));
      } else {
         var12.add(this.texture.initDraw().sprite(1, 0, 32, 64).light(var9).pos(var10, var11 - 32));
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      if (var4 == 0) {
         this.texture.initDraw().sprite(3, 1, 32).alpha(var5).draw(var8, var9 - 64);
         this.texture.initDraw().sprite(3, 2, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(3, 3, 32).alpha(var5).draw(var8, var9);
      } else if (var4 == 1) {
         this.texture.initDraw().sprite(0, 1, 64).alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 2) {
         this.texture.initDraw().sprite(2, 1, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(2, 2, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(2, 3, 32).alpha(var5).draw(var8, var9 + 32);
      } else {
         this.texture.initDraw().sprite(0, 0, 64).alpha(var5).draw(var8 - 32, var9 - 32);
      }

   }

   public static int[] registerDinnerTable(String var0, String var1, ToolType var2, Color var3, float var4) {
      DinnerTableObject var5 = new DinnerTableObject(var1, var2, var3);
      DinnerTable2Object var6 = new DinnerTable2Object(var1, var2, var3);
      int var7 = ObjectRegistry.registerObject(var0, var5, var4, true);
      int var8 = ObjectRegistry.registerObject(var0 + "2", var6, 0.0F, false);
      var5.counterID = var8;
      var6.counterID = var7;
      return new int[]{var7, var8};
   }

   public static int[] registerDinnerTable(String var0, String var1, Color var2, float var3) {
      return registerDinnerTable(var0, var1, ToolType.ALL, var2, var3);
   }
}
