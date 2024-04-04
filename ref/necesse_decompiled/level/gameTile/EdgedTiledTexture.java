package necesse.level.gameTile;

import java.util.Arrays;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.maps.Level;

public class EdgedTiledTexture extends GameTile {
   protected GameTextureSection[][] textures;
   protected String textureName;
   protected int tilesWidth = -1;
   protected int tilesHeight = -1;

   public EdgedTiledTexture(boolean var1, String var2) {
      super(var1);
      this.textureName = var2;
   }

   protected void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("tiles/" + this.textureName);
      int var2 = this.tilesWidth == -1 ? var1.getWidth() / 32 - 2 : this.tilesWidth;
      int var3 = this.tilesHeight == -1 ? var1.getHeight() / 32 : this.tilesHeight;
      this.textures = new GameTextureSection[var2][var3];

      for(int var4 = 0; var4 < this.textures.length; ++var4) {
         for(int var5 = 0; var5 < this.textures[var4].length; ++var5) {
            GameTexture var6 = new GameTexture("tiles/" + this.textureName + " tile" + var4 + "x" + var5, 64, 96);

            for(int var7 = 0; var7 < var6.getWidth() / 32; ++var7) {
               for(int var8 = 0; var8 < var6.getHeight() / 32; ++var8) {
                  var6.copy(var1, var7 * 32, var8 * 32, 64 + var4 * 32, var5 * 32, 32, 32);
               }
            }

            var6.merge(var1, 0, 0, this.getEdgeMergeFunction());
            this.textures[var4][var5] = tileTextures.addTexture(var6);
            var6.makeFinal();
         }
      }

      var1.makeFinal();
   }

   protected MergeFunction getEdgeMergeFunction() {
      return MergeFunction.NORMAL;
   }

   protected GameTextureSection getTopLeftDrawOptions(GameTextureSection var1, Boolean[] var2) {
      byte var3 = 0;
      byte var4 = 1;
      byte var5 = 3;
      if (var2[var5]) {
         if (var2[var4]) {
            return var2[var3] ? var1.sprite(0, 0, 16) : var1.sprite(3, 3, 16);
         } else {
            return var1.sprite(2, 4, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(0, 4, 16) : var1.sprite(0, 2, 16);
      }
   }

   protected GameTextureSection getTopRightDrawOptions(GameTextureSection var1, Boolean[] var2) {
      byte var3 = 2;
      byte var4 = 1;
      byte var5 = 4;
      if (var2[var5]) {
         if (var2[var4]) {
            return var2[var3] ? var1.sprite(1, 0, 16) : var1.sprite(2, 3, 16);
         } else {
            return var1.sprite(3, 4, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(1, 4, 16) : var1.sprite(1, 2, 16);
      }
   }

   protected GameTextureSection getBotLeftDrawOptions(GameTextureSection var1, Boolean[] var2) {
      byte var3 = 3;
      byte var4 = 5;
      byte var5 = 6;
      if (var2[var3]) {
         if (var2[var5]) {
            return var2[var4] ? var1.sprite(0, 1, 16) : var1.sprite(3, 2, 16);
         } else {
            return var1.sprite(2, 5, 16);
         }
      } else {
         return var2[var5] ? var1.sprite(0, 3, 16) : var1.sprite(0, 5, 16);
      }
   }

   protected GameTextureSection getBotRightDrawOptions(GameTextureSection var1, Boolean[] var2) {
      byte var3 = 4;
      byte var4 = 6;
      byte var5 = 7;
      if (var2[var3]) {
         if (var2[var4]) {
            return var2[var5] ? var1.sprite(1, 1, 16) : var1.sprite(2, 2, 16);
         } else {
            return var1.sprite(3, 5, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(1, 3, 16) : var1.sprite(1, 5, 16);
      }
   }

   public void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      int var8 = var6.getTileDrawX(var4);
      int var9 = var6.getTileDrawY(var5);
      Boolean[] var10 = (Boolean[])var3.getRelative(var4, var5, Level.adjacentGetters, (var2x, var3x) -> {
         return this.isMergeTile(var3, var2x, var3x);
      }, (var0) -> {
         return new Boolean[var0];
      });
      boolean var11 = Arrays.stream(var10).allMatch((var0) -> {
         return var0;
      });
      int var12 = var4 % this.textures.length;
      int var13 = var5 % this.textures[0].length;
      GameTextureSection var14 = this.textures[var12][var13];
      if (var11) {
         var1.add(var14.sprite(0, 0, 32)).pos(var8, var9);
      } else {
         var1.add(this.getTopLeftDrawOptions(var14, var10)).pos(var8, var9);
         var1.add(this.getTopRightDrawOptions(var14, var10)).pos(var8 + 16, var9);
         var1.add(this.getBotLeftDrawOptions(var14, var10)).pos(var8, var9 + 16);
         var1.add(this.getBotRightDrawOptions(var14, var10)).pos(var8 + 16, var9 + 16);
      }

   }

   protected boolean isMergeTile(Level var1, int var2, int var3) {
      return var1.getTileID(var2, var3) == this.getID();
   }

   public void drawPreview(Level var1, int var2, int var3, float var4, PlayerMob var5, GameCamera var6) {
      int var7 = var6.getTileDrawX(var2);
      int var8 = var6.getTileDrawY(var3);
      Boolean[] var9 = (Boolean[])var1.getRelative(var2, var3, Level.adjacentGetters, (var2x, var3x) -> {
         return this.isMergeTile(var1, var2x, var3x);
      }, (var0) -> {
         return new Boolean[var0];
      });
      boolean var10 = Arrays.stream(var9).allMatch((var0) -> {
         return var0;
      });
      int var11 = var2 % this.textures.length;
      int var12 = var3 % this.textures[0].length;
      GameTextureSection var13 = this.textures[var11][var12];
      if (var10) {
         var13.sprite(0, 0, 32).initDraw().alpha(var4).draw(var7, var8);
      } else {
         this.getTopLeftDrawOptions(var13, var9).initDraw().alpha(var4).draw(var7, var8);
         this.getTopRightDrawOptions(var13, var9).initDraw().alpha(var4).draw(var7 + 16, var8);
         this.getBotLeftDrawOptions(var13, var9).initDraw().alpha(var4).draw(var7, var8 + 16);
         this.getBotRightDrawOptions(var13, var9).initDraw().alpha(var4).draw(var7 + 16, var8 + 16);
      }

   }

   public GameTexture generateItemTexture() {
      GameTexture var1 = GameTexture.fromFile("tiles/" + this.textureName, true);
      GameTexture var2 = GameTexture.fromFile("tiles/itemmask", true);
      GameTexture var3 = new GameTexture("tiles/" + this.textureName + " item", 32, 32);
      var3.copy(var1, 0, 0, 32, 0, 32, 32);
      var3.merge(var2, 0, 0, MergeFunction.MULTIPLY);
      var3.makeFinal();
      var1.makeFinal();
      return var3;
   }
}
