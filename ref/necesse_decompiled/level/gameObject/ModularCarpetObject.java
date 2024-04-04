package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.CustomObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ModularCarpetObject extends GameObject implements RoomFurniture {
   protected String textureName;
   protected int textureTileWidth;
   protected int textureTileHeight;
   protected GameTexture itemTexture;
   protected GameTexture[][] masked;

   public ModularCarpetObject(String var1, Color var2) {
      this.textureName = var1;
      this.mapColor = var2;
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName, true);
      this.textureTileWidth = var1.getWidth() / 32;
      this.textureTileHeight = var1.getHeight() / 32;
      GameTexture var2 = GameTexture.fromFile("objects/" + this.textureName + "mask", true);
      int var3 = var2.getWidth() / 32 + 1;
      int var4 = var2.getHeight() / 32;
      this.masked = new GameTexture[this.textureTileWidth][this.textureTileHeight];

      for(int var5 = 0; var5 < this.textureTileWidth; ++var5) {
         for(int var6 = 0; var6 < this.textureTileHeight; ++var6) {
            GameTexture var7 = new GameTexture("objects/" + this.textureName + " part" + var5 + "x" + var6, var3 * 32, var4 * 32);

            for(int var8 = 0; var8 < var3; ++var8) {
               for(int var9 = 0; var9 < var4; ++var9) {
                  var7.mergeSprite(var1, var5, var6, 32, var8 * 32, var9 * 32);
               }
            }

            var7.merge(var2, 0, 0, MergeFunction.MULTIPLY);
            var7.makeFinal();
            this.masked[var5][var6] = var7;
         }
      }

      this.itemTexture = new GameTexture(var1, 0, 0, 32);
      GameTexture var10 = GameTexture.fromFile("items/" + this.textureName, true);
      this.itemTexture.merge(var10, 0, 0, MergeFunction.MULTIPLY);
      this.itemTexture.makeFinal();
      var1.makeFinal();
      var2.makeFinal();
      var10.makeFinal();
   }

   public GameTextureSection getTopLeft(GameTextureSection var1, boolean[] var2) {
      byte var3 = 0;
      byte var4 = 1;
      byte var5 = 3;
      if (var2[var5]) {
         if (var2[var4]) {
            return var2[var3] ? var1.sprite(4, 0, 16) : var1.sprite(2, 2, 16);
         } else {
            return var1.sprite(2, 0, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(0, 2, 16) : var1.sprite(0, 0, 16);
      }
   }

   public GameTextureSection getTopRight(GameTextureSection var1, boolean[] var2) {
      byte var3 = 2;
      byte var4 = 1;
      byte var5 = 4;
      if (var2[var5]) {
         if (var2[var4]) {
            return var2[var3] ? var1.sprite(5, 0, 16) : var1.sprite(3, 2, 16);
         } else {
            return var1.sprite(3, 0, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(1, 2, 16) : var1.sprite(1, 0, 16);
      }
   }

   public GameTextureSection getBotLeft(GameTextureSection var1, boolean[] var2) {
      byte var3 = 3;
      byte var4 = 5;
      byte var5 = 6;
      if (var2[var3]) {
         if (var2[var5]) {
            return var2[var4] ? var1.sprite(4, 1, 16) : var1.sprite(2, 3, 16);
         } else {
            return var1.sprite(2, 1, 16);
         }
      } else {
         return var2[var5] ? var1.sprite(0, 3, 16) : var1.sprite(0, 1, 16);
      }
   }

   public GameTextureSection getBotRight(GameTextureSection var1, boolean[] var2) {
      byte var3 = 4;
      byte var4 = 6;
      byte var5 = 7;
      if (var2[var3]) {
         if (var2[var4]) {
            return var2[var5] ? var1.sprite(5, 1, 16) : var1.sprite(3, 3, 16);
         } else {
            return var1.sprite(3, 1, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(1, 3, 16) : var1.sprite(1, 1, 16);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      Integer[] var12 = var3.getAdjacentObjectsInt(var4, var5);
      boolean[] var13 = new boolean[var12.length];
      boolean var14 = true;
      int var15 = this.getID();

      int var16;
      for(var16 = 0; var16 < var12.length; ++var16) {
         boolean var17 = var12[var16] == var15;
         var13[var16] = var17;
         if (!var17) {
            var14 = false;
         }
      }

      var16 = var4 % this.textureTileWidth;
      int var21 = var5 % this.textureTileHeight;
      GameTexture var18 = this.masked[var16][var21];
      GameTextureSection var19 = new GameTextureSection(var18);
      SharedTextureDrawOptions var20 = new SharedTextureDrawOptions(var19.getTexture());
      if (var14) {
         var20.add(var19.sprite(2, 0, 32)).light(var9).pos(var10, var11);
      } else {
         var20.add(this.getTopLeft(var19, var13)).light(var9).pos(var10, var11);
         var20.add(this.getTopRight(var19, var13)).light(var9).pos(var10 + 16, var11);
         var20.add(this.getBotLeft(var19, var13)).light(var9).pos(var10, var11 + 16);
         var20.add(this.getBotRight(var19, var13)).light(var9).pos(var10 + 16, var11 + 16);
      }

      var2.add((var1x) -> {
         var20.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      Integer[] var10 = var1.getAdjacentObjectsInt(var2, var3);
      boolean[] var11 = new boolean[var10.length];
      boolean var12 = true;
      int var13 = this.getID();

      int var14;
      for(var14 = 0; var14 < var10.length; ++var14) {
         boolean var15 = var10[var14] == var13;
         var11[var14] = var15;
         if (!var15) {
            var12 = false;
         }
      }

      var14 = var2 % this.textureTileWidth;
      int var19 = var3 % this.textureTileHeight;
      GameTexture var16 = this.masked[var14][var19];
      GameTextureSection var17 = new GameTextureSection(var16);
      SharedTextureDrawOptions var18 = new SharedTextureDrawOptions(var17.getTexture());
      if (var12) {
         var18.add(var17.sprite(4, 0, 32)).alpha(var5).pos(var8, var9);
      } else {
         var18.add(this.getTopLeft(var17, var11)).alpha(var5).pos(var8, var9);
         var18.add(this.getTopRight(var17, var11)).alpha(var5).pos(var8 + 16, var9);
         var18.add(this.getBotLeft(var17, var11)).alpha(var5).pos(var8, var9 + 16);
         var18.add(this.getBotRight(var17, var11)).alpha(var5).pos(var8 + 16, var9 + 16);
      }

      var18.draw();
   }

   public Item generateNewObjectItem() {
      return new CustomObjectItem(this, () -> {
         return this.itemTexture;
      }, 0, 0);
   }

   public String getFurnitureType() {
      return "carpet";
   }
}
