package necesse.level.gameTile;

import java.awt.Point;
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
import necesse.level.maps.splattingManager.SplattingOptions;

public abstract class TerrainSplatterTile extends GameTile {
   public static final int PRIORITY_TERRAIN_BOT = 0;
   public static final int PRIORITY_TERRAIN = 100;
   public static final int PRIORITY_TERRAIN_TOP = 200;
   public static final int PRIORITY_FLOOR_BOT = 300;
   public static final int PRIORITY_FLOOR = 400;
   public static final int PRIORITY_FLOOR_TOP = 500;
   protected String alphaMaskTextureName;
   protected String terrainTextureName;
   public GameTextureSection[][] splattingTextures;
   public GameTextureSection terrainTexture;

   public TerrainSplatterTile(boolean var1, String var2, String var3) {
      super(var1);
      this.terrainTextureName = var2;
      this.alphaMaskTextureName = var3;
   }

   public TerrainSplatterTile(boolean var1, String var2) {
      this(var1, var2, "splattingmask");
   }

   public abstract Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4);

   public abstract int getTerrainPriority();

   public static int comparePriority(TerrainSplatterTile var0, TerrainSplatterTile var1) {
      int var2 = var0.getTerrainPriority();
      int var3 = var1.getTerrainPriority();
      int var4 = Integer.compare(var2, var3);
      return var4 != 0 ? var4 : Integer.compare(var0.getID(), var1.getID());
   }

   public GameTexture generateItemTexture() {
      GameTexture var1 = GameTexture.fromFile("tiles/" + this.terrainTextureName, true);
      GameTexture var2 = GameTexture.fromFile("tiles/itemmask", true);
      GameTexture var3 = new GameTexture("tiles/" + this.terrainTextureName + " item", 32, 32);
      var3.copy(var1, 0, 0, 0, 0, 32, 32);
      var3.merge(var2, 0, 0, MergeFunction.MULTIPLY);
      var3.makeFinal();
      var1.makeFinal();
      return var3;
   }

   public GameTextureSection getSplattingTexture(Level var1, int var2, int var3) {
      Point var4 = this.getTerrainSprite(this.terrainTexture, var1, var2, var3);
      return this.splattingTextures[var4.x][var4.y];
   }

   public GameTextureSection getTerrainTexture(Level var1, int var2, int var3) {
      Point var4 = this.getTerrainSprite(this.terrainTexture, var1, var2, var3);
      return this.terrainTexture.sprite(var4.x, var4.y, 32);
   }

   public void generateSplattingTextures() {
      GameTexture var1 = GameTexture.fromFile("tiles/" + this.terrainTextureName, true);
      this.terrainTexture = tileTextures.addTexture(var1);
      GameTexture var2 = GameTexture.fromFile("tiles/" + this.alphaMaskTextureName, true);
      if (var2.getWidth() != var2.getHeight()) {
         throw new IllegalStateException("Terrain splatting alpha mask must have same width and height");
      } else {
         this.splattingTextures = new GameTextureSection[var1.getWidth() / 32][var1.getHeight() / 32];

         for(int var3 = 0; var3 < var1.getWidth() / 32; ++var3) {
            for(int var4 = 0; var4 < var1.getHeight() / 32; ++var4) {
               GameTexture var5 = new GameTexture("tiles/" + this.terrainTextureName + " splat" + var3 + "x" + var4, var2.getWidth(), var2.getHeight());

               for(int var6 = 0; var6 < var2.getWidth() / 32; ++var6) {
                  for(int var7 = 0; var7 < var2.getHeight() / 32; ++var7) {
                     var5.mergeSprite(var1, var3, var4, 32, var6 * 32, var7 * 32);
                  }
               }

               var5.merge(var2, 0, 0, MergeFunction.MULTIPLY);
               var5.makeFinal();
               this.splattingTextures[var3][var4] = tileTextures.addTexture(var5);
            }
         }

         var1.makeFinal();
      }
   }

   public void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      this.addTerrainDrawables(var1, var3, var4, var5, var6, var7);
   }

   public void drawPreview(Level var1, int var2, int var3, float var4, PlayerMob var5, GameCamera var6) {
      int var7 = var6.getTileDrawX(var2);
      int var8 = var6.getTileDrawY(var3);
      this.getTerrainTexture(var1, var2, var3).initDraw().alpha(var4).draw(var7, var8);
   }

   public void addTerrainDrawables(LevelTileDrawOptions var1, Level var2, int var3, int var4, GameCamera var5, TickManager var6) {
      SplattingOptions var7 = var2.splattingManager.getSplatTiles(var3, var4);
      if (var7 != null) {
         var7.addTileDrawables(var1, this, var2, var3, var4, var5, var6);
      } else {
         int var8 = var5.getTileDrawX(var3);
         int var9 = var5.getTileDrawY(var4);
         var1.add(this.getTerrainTexture(var2, var3, var4)).pos(var8, var9);
      }

   }
}
