package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

public class CarpentersBenchObject extends CraftingStationObject {
   public GameTexture texture;
   protected int counterID;

   private CarpentersBenchObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(150, 119, 70);
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new SideMultiTile(0, 1, 1, 2, var1, true, new int[]{this.counterID, this.getID()});
   }

   public int getPlaceRotation(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      return Math.floorMod(super.getPlaceRotation(var1, var2, var3, var4, var5) - 1, 4);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/carpentersbench");
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 5, var3 * 32, 22, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 12, var3 * 32 + 6, 20, 20);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 5, var3 * 32 + 16, 22, 16) : new Rectangle(var2 * 32, var3 * 32 + 6, 20, 20);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      byte var5 = var1.getObjectRotation(var2, var3);
      if (var5 == 1 || var5 == 3) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      }

      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      final DrawOptionsList var13 = new DrawOptionsList();
      if (var12 == 0) {
         var13.add(this.texture.initDraw().sprite(1, 1, 32).light(var9).pos(var10, var11 + 2));
      } else if (var12 == 1) {
         var13.add(this.texture.initDraw().sprite(1, 2, 32).mirrorX().light(var9).pos(var10, var11 - 24));
         var13.add(this.texture.initDraw().sprite(1, 3, 32).mirrorX().light(var9).pos(var10, var11 + 8));
      } else if (var12 == 2) {
         var13.add(this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11 + 2));
      } else {
         var13.add(this.texture.initDraw().sprite(1, 2, 32).light(var9).pos(var10, var11 - 24));
         var13.add(this.texture.initDraw().sprite(1, 3, 32).light(var9).pos(var10, var11 + 8));
      }

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
      if (var4 == 0) {
         this.texture.initDraw().sprite(1, 1, 32).alpha(var5).draw(var8, var9 + 2);
         this.texture.initDraw().sprite(1, 0, 32).alpha(var5).draw(var8, var9 - 32 + 2);
      } else if (var4 == 1) {
         this.texture.initDraw().sprite(0, 2, 32).mirrorX().alpha(var5).draw(var8 + 32, var9 - 24);
         this.texture.initDraw().sprite(1, 2, 32).mirrorX().alpha(var5).draw(var8, var9 - 24);
         this.texture.initDraw().sprite(0, 3, 32).mirrorX().alpha(var5).draw(var8 + 32, var9 + 8);
         this.texture.initDraw().sprite(1, 3, 32).mirrorX().alpha(var5).draw(var8, var9 + 8);
      } else if (var4 == 2) {
         this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9 + 2);
         this.texture.initDraw().sprite(0, 1, 32).alpha(var5).draw(var8, var9 + 32 + 2);
      } else {
         this.texture.initDraw().sprite(0, 2, 32).alpha(var5).draw(var8 - 32, var9 - 24);
         this.texture.initDraw().sprite(1, 2, 32).alpha(var5).draw(var8, var9 - 24);
         this.texture.initDraw().sprite(0, 3, 32).alpha(var5).draw(var8 - 32, var9 + 8);
         this.texture.initDraw().sprite(1, 3, 32).alpha(var5).draw(var8, var9 + 8);
      }

   }

   public Tech[] getCraftingTechs() {
      return new Tech[]{RecipeTechRegistry.CARPENTER};
   }

   public static int[] registerCarpentersBench() {
      CarpentersBenchObject var0 = new CarpentersBenchObject();
      CarpentersBench2Object var1 = new CarpentersBench2Object();
      int var2 = ObjectRegistry.registerObject("carpentersbench", var0, 20.0F, true);
      int var3 = ObjectRegistry.registerObject("carpentersbench2", var1, 0.0F, false);
      var0.counterID = var3;
      var1.counterID = var2;
      return new int[]{var2, var3};
   }
}
