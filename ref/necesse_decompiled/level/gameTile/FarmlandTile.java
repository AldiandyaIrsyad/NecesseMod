package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FarmlandTile extends GameTile {
   public GameTextureSection texture;

   public FarmlandTile() {
      super(false);
      this.mapColor = new Color(54, 48, 29);
      this.canBeMined = true;
      this.smartMinePriority = true;
      this.tileHealth = 50;
   }

   protected void loadTextures() {
      super.loadTextures();
      this.texture = tileTextures.addTexture(GameTexture.fromFile("tiles/farmland"));
   }

   protected GameTextureSection getTopLeftDrawOptions(GameTextureSection var1, boolean[] var2) {
      byte var3 = 0;
      byte var4 = 1;
      byte var5 = 3;
      if (var2[var5]) {
         if (var2[var4]) {
            return var2[var3] ? var1.sprite(2, 4, 16) : var1.sprite(3, 1, 16);
         } else {
            return var1.sprite(2, 4, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(0, 2, 16) : var1.sprite(0, 2, 16);
      }
   }

   protected GameTextureSection getTopRightDrawOptions(GameTextureSection var1, boolean[] var2) {
      byte var3 = 2;
      byte var4 = 1;
      byte var5 = 4;
      if (var2[var5]) {
         if (var2[var4]) {
            return var2[var3] ? var1.sprite(3, 4, 16) : var1.sprite(2, 1, 16);
         } else {
            return var1.sprite(3, 4, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(1, 2, 16) : var1.sprite(1, 2, 16);
      }
   }

   protected GameTextureSection getBotLeftDrawOptions(GameTextureSection var1, boolean[] var2) {
      byte var3 = 3;
      byte var4 = 5;
      byte var5 = 6;
      if (var2[var3]) {
         if (var2[var5]) {
            return var2[var4] ? var1.sprite(2, 5, 16) : var1.sprite(3, 0, 16);
         } else {
            return var1.sprite(2, 3, 16);
         }
      } else {
         return var2[var5] ? var1.sprite(0, 1, 16) : var1.sprite(0, 3, 16);
      }
   }

   protected GameTextureSection getBotRightDrawOptions(GameTextureSection var1, boolean[] var2) {
      byte var3 = 4;
      byte var4 = 6;
      byte var5 = 7;
      if (var2[var3]) {
         if (var2[var4]) {
            return var2[var5] ? var1.sprite(3, 5, 16) : var1.sprite(2, 0, 16);
         } else {
            return var1.sprite(3, 3, 16);
         }
      } else {
         return var2[var4] ? var1.sprite(1, 1, 16) : var1.sprite(1, 3, 16);
      }
   }

   public void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      int var8 = var6.getTileDrawX(var4);
      int var9 = var6.getTileDrawY(var5);
      int var10 = this.getID();
      Integer[] var11 = var3.getAdjacentTilesInt(var4, var5);
      boolean var12 = true;
      boolean[] var13 = new boolean[var11.length];

      for(int var14 = 0; var14 < var11.length; ++var14) {
         var13[var14] = var11[var14] == var10;
         var12 = var12 && var13[var14];
      }

      if (var12) {
         var1.add(this.texture.sprite(1, 2, 32)).pos(var8, var9);
      } else {
         var1.add(this.getTopLeftDrawOptions(this.texture, var13)).pos(var8, var9);
         var1.add(this.getTopRightDrawOptions(this.texture, var13)).pos(var8 + 16, var9);
         var1.add(this.getBotLeftDrawOptions(this.texture, var13)).pos(var8, var9 + 16);
         var1.add(this.getBotRightDrawOptions(this.texture, var13)).pos(var8 + 16, var9 + 16);
      }

      if (var3.getTileID(var4, var5 - 1) != var10) {
         GameLight var17 = var3.getLightLevel(var4, var5);
         final TextureDrawOptionsEnd var15;
         if (var3.getTileID(var4 - 1, var5) == var10) {
            var15 = this.texture.sprite(2, 2, 16).initDraw().light(var17).pos(var8, var9 - 12);
         } else {
            var15 = this.texture.sprite(0, 0, 16).initDraw().light(var17).pos(var8, var9 - 12);
         }

         final TextureDrawOptionsEnd var16;
         if (var3.getTileID(var4 + 1, var5) == var10) {
            var16 = this.texture.sprite(3, 2, 16).initDraw().light(var17).pos(var8 + 16, var9 - 12);
         } else {
            var16 = this.texture.sprite(1, 0, 16).initDraw().light(var17).pos(var8 + 16, var9 - 12);
         }

         var2.add(new LevelSortedDrawable(this, var4) {
            public int getSortY() {
               return 0;
            }

            public void draw(TickManager var1) {
               var15.draw();
               var16.draw();
            }
         });
      }

   }

   public void drawPreview(Level var1, int var2, int var3, float var4, PlayerMob var5, GameCamera var6) {
      int var7 = var6.getTileDrawX(var2);
      int var8 = var6.getTileDrawY(var3);
      int var9 = this.getID();
      Integer[] var10 = var1.getAdjacentTilesInt(var2, var3);
      boolean var11 = true;
      boolean[] var12 = new boolean[var10.length];

      for(int var13 = 0; var13 < var10.length; ++var13) {
         var12[var13] = var10[var13] == var9;
         var11 = var11 && var12[var13];
      }

      this.getTopLeftDrawOptions(this.texture, var12).initDraw().alpha(var4).draw(var7, var8);
      this.getTopRightDrawOptions(this.texture, var12).initDraw().alpha(var4).draw(var7 + 16, var8);
      this.getBotLeftDrawOptions(this.texture, var12).initDraw().alpha(var4).draw(var7, var8 + 16);
      this.getBotRightDrawOptions(this.texture, var12).initDraw().alpha(var4).draw(var7 + 16, var8 + 16);
      if (var1.getTileID(var2, var3 - 1) != var9) {
         this.texture.sprite(0, 0, 16).initDraw().alpha(var4).draw(var7, var8 - 12);
         this.texture.sprite(1, 0, 16).initDraw().alpha(var4).draw(var7 + 16, var8 - 12);
      }

   }

   public GameTexture generateItemTexture() {
      GameTexture var1 = GameTexture.fromFile("tiles/farmland", true);
      GameTexture var2 = GameTexture.fromFile("tiles/itemmask", true);
      GameTexture var3 = new GameTexture("tiles/farmland item", 32, 32);
      var3.copy(var1, 0, 0, 0, 64, 32, 32);
      var3.merge(var2, 0, 0, MergeFunction.MULTIPLY);
      var3.makeFinal();
      var1.makeFinal();
      return var3;
   }

   public String canPlace(Level var1, int var2, int var3) {
      String var4 = super.canPlace(var1, var2, var3);
      if (var4 != null) {
         return var4;
      } else {
         return var1.isShore(var2, var3) ? "isshore" : null;
      }
   }

   public boolean canBePlacedOn(Level var1, int var2, int var3, GameTile var4) {
      return false;
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return !var1.isShore(var2, var3);
   }
}
