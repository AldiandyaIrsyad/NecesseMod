package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

public class CookingStationObject extends FueledCraftingStationObject {
   public GameTexture texture;
   protected int counterID;

   private CookingStationObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(51, 53, 56);
      this.rarity = Item.Rarity.UNCOMMON;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      FueledInventoryObjectEntity var4 = this.getFueledObjectEntity(var1, var2, var3);
      return var4 != null && var4.isFueled() ? 100 : 0;
   }

   public MultiTile getMultiTile(int var1) {
      return new SideMultiTile(0, 1, 1, 2, var1, true, new int[]{this.counterID, this.getID()});
   }

   public int getPlaceRotation(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      return Math.floorMod(super.getPlaceRotation(var1, var2, var3, var4, var5) - 1, 4);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/cookingstation");
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 4, var3 * 32, 24, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 26, 20);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 4, var3 * 32 + 4, 24, 28) : new Rectangle(var2 * 32, var3 * 32 + 6, 26, 20);
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
      byte var12 = var3.getObjectRotation(var4, var5);
      boolean var13 = (Boolean)this.getMultiTile(var12).getMasterLevelObject(var3, var4, var5).map((var0) -> {
         FueledInventoryObjectEntity var1 = ((FueledCraftingStationObject)var0.object).getFueledObjectEntity(var0.level, var0.tileX, var0.tileY);
         return var1 != null && var1.isFueled();
      }).orElse(false);
      final DrawOptionsList var14 = new DrawOptionsList();
      if (var12 == 0) {
         var14.add(this.texture.initDraw().sprite(0, 2, 32).light(var9).pos(var10, var11));
         if (var13) {
            var14.add(this.texture.initDraw().sprite(0, 8, 32).light(var9).pos(var10, var11 - 32 + 14));
         }
      } else if (var12 == 1) {
         var14.add(this.texture.initDraw().sprite(0, 5, 32).light(var9).pos(var10, var11 - 32));
         var14.add(this.texture.initDraw().sprite(0, 6, 32).light(var9).pos(var10, var11));
         if (var13) {
            var14.add(this.texture.initDraw().sprite(0, 7, 32).light(var9).pos(var10, var11 - 14));
         }
      } else if (var12 == 2) {
         var14.add(this.texture.initDraw().sprite(1, 0, 32).light(var9).pos(var10, var11 - 32));
         var14.add(this.texture.initDraw().sprite(1, 1, 32).light(var9).pos(var10, var11));
         if (var13) {
            var14.add(this.texture.initDraw().sprite(1, 8, 32).light(var9).pos(var10, var11 - 14));
         }
      } else {
         var14.add(this.texture.initDraw().sprite(1, 3, 32).light(var9).pos(var10, var11 - 32));
         var14.add(this.texture.initDraw().sprite(1, 4, 32).light(var9).pos(var10, var11));
         if (var13) {
            var14.add(this.texture.initDraw().sprite(1, 7, 32).light(var9).pos(var10, var11 - 14));
         }
      }

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
      if (var4 == 0) {
         this.texture.initDraw().sprite(0, 2, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9 - 64);
         this.texture.initDraw().sprite(0, 1, 32).alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 1) {
         this.texture.initDraw().sprite(0, 5, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(0, 6, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(1, 5, 32).alpha(var5).draw(var8 + 32, var9 - 32);
         this.texture.initDraw().sprite(1, 6, 32).alpha(var5).draw(var8 + 32, var9);
      } else if (var4 == 2) {
         this.texture.initDraw().sprite(1, 0, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(1, 1, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(1, 2, 32).alpha(var5).draw(var8, var9 + 32);
      } else {
         this.texture.initDraw().sprite(1, 3, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(1, 4, 32).alpha(var5).draw(var8, var9);
         this.texture.initDraw().sprite(0, 3, 32).alpha(var5).draw(var8 - 32, var9 - 32);
         this.texture.initDraw().sprite(0, 4, 32).alpha(var5).draw(var8 - 32, var9);
      }

   }

   public Tech[] getCraftingTechs() {
      return new Tech[]{RecipeTechRegistry.COOKING_STATION, RecipeTechRegistry.COOKING_POT, RecipeTechRegistry.ROASTING_STATION};
   }

   public static int[] registerCookingStation() {
      CookingStationObject var0 = new CookingStationObject();
      CookingStation2Object var1 = new CookingStation2Object();
      int var2 = ObjectRegistry.registerObject("cookingstation", var0, 140.0F, true);
      int var3 = ObjectRegistry.registerObject("cookingstation2", var1, 0.0F, false);
      var0.counterID = var3;
      var1.counterID = var2;
      return new int[]{var2, var3};
   }
}
