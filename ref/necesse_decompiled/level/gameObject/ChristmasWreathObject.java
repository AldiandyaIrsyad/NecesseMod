package necesse.level.gameObject;

import java.util.List;
import java.util.Objects;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChristmasWreathObject extends GameObject {
   public GameTexture texture;

   public ChristmasWreathObject() {
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.rarity = Item.Rarity.UNCOMMON;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/christmaswreath");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      int var13 = this.getSprite(var3, var4, var5, var12);
      if (var13 == 0) {
         var11 -= 16;
      } else if (var13 == 2) {
         var11 += 16;
      }

      final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(0, var13, 32).pos(var10, var11 - 16).light(var9);
      final byte var15;
      if (var13 == 0) {
         var15 = 0;
      } else if (var13 == 2) {
         var15 = 32;
      } else {
         var15 = 16;
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var15;
         }

         public void draw(TickManager var1) {
            TextureDrawOptions var10002 = var14;
            Objects.requireNonNull(var10002);
            Performance.record(var1, "christmaswreathDraw", (Runnable)(var10002::draw));
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      int var10 = this.getSprite(var1, var2, var3, var4);
      if (var10 == 0) {
         var9 -= 16;
      } else if (var10 == 2) {
         var9 += 16;
      }

      this.texture.initDraw().sprite(0, var10, 32).alpha(var5).draw(var8, var9 - 16);
   }

   protected int getSprite(Level var1, int var2, int var3, int var4) {
      GameObject var5 = var1.getObject(var2, var3 - 1);
      GameObject var6 = var1.getObject(var2 + 1, var3);
      GameObject var7 = var1.getObject(var2, var3 + 1);
      GameObject var8 = var1.getObject(var2 - 1, var3);
      boolean var9 = var5.isWall && !var5.isDoor;
      boolean var10 = var6.isWall && !var6.isDoor;
      boolean var11 = var7.isWall && !var7.isDoor;
      boolean var12 = var8.isWall && !var8.isDoor;
      if (var9) {
         if (var4 == 0 && var11) {
            return 2;
         } else if (var4 == 1 && var10) {
            return 1;
         } else {
            return var4 == 3 && var12 ? 3 : 0;
         }
      } else if (var11) {
         if (var4 == 1 && var10) {
            return 1;
         } else {
            return var4 == 3 && var12 ? 3 : 2;
         }
      } else if (var10) {
         return var4 == 3 && var12 ? 3 : 1;
      } else {
         return var12 ? 3 : 0;
      }
   }

   public int getPlaceRotation(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      if (var5 == 1) {
         return 3;
      } else {
         return var5 == 3 ? 1 : super.getPlaceRotation(var1, var2, var3, var4, var5);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      byte var5 = var1.getObjectRotation(var2, var3);
      int var6 = this.getSprite(var1, var2, var3, var5);
      if (var6 == 0) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32, 0));
      } else if (var6 == 1 || var6 == 3) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16, 0));
      }

      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      if (var1.getObjectID(var2, var3) != 0 && !var1.getObject(var2, var3).isGrass) {
         return "occupied";
      } else {
         boolean var5 = false;
         if (var1.getObject(var2 - 1, var3).isWall && !var1.getObject(var2 - 1, var3).isDoor) {
            var5 = true;
         } else if (var1.getObject(var2 + 1, var3).isWall && !var1.getObject(var2 + 1, var3).isDoor) {
            var5 = true;
         } else if (var1.getObject(var2, var3 - 1).isWall && !var1.getObject(var2, var3 - 1).isDoor) {
            var5 = true;
         } else if (var1.getObject(var2, var3 + 1).isWall && !var1.getObject(var2, var3 + 1).isDoor) {
            var5 = true;
         }

         return !var5 ? "nowall" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      boolean var4 = false;
      if (var1.getObject(var2 - 1, var3).isWall && !var1.getObject(var2 - 1, var3).isDoor) {
         var4 = true;
      } else if (var1.getObject(var2 + 1, var3).isWall && !var1.getObject(var2 + 1, var3).isDoor) {
         var4 = true;
      } else if (var1.getObject(var2, var3 - 1).isWall && !var1.getObject(var2, var3 - 1).isDoor) {
         var4 = true;
      } else if (var1.getObject(var2, var3 + 1).isWall && !var1.getObject(var2, var3 + 1).isDoor) {
         var4 = true;
      }

      return var4;
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "christmastreetip"));
      return var3;
   }
}
