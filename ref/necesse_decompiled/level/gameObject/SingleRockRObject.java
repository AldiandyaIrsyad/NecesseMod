package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class SingleRockRObject extends GameObject {
   protected RockObject type;
   protected int counterID;
   protected String textureName;
   public GameTexture texture;
   protected final GameRandom drawRandom;

   public SingleRockRObject(RockObject var1, String var2, Color var3) {
      super(new Rectangle(0, 14, 18, 10));
      this.type = var1;
      this.textureName = var2;
      this.mapColor = var3;
      this.toolTier = var1.toolTier;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
      this.canPlaceOnLiquid = true;
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", "singlerock", "rock", this.type.getNewLocalization());
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(1, 0, 2, 1, false, new int[]{this.counterID, this.getID()});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public int getRandomYOffset(int var1, int var2) {
      synchronized(this.drawRandom) {
         return (int)((this.drawRandom.seeded(this.getTileSeed(var1 - 1, var2, 1)).nextFloat() * 2.0F - 1.0F) * 8.0F) - 4;
      }
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      Rectangle var5 = super.getCollision(var1, var2, var3, var4);
      var5.y += this.getRandomYOffset(var2, var3);
      return var5;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final int var13 = this.getRandomYOffset(var4, var5);
      int var12;
      synchronized(this.drawRandom) {
         var12 = this.drawRandom.seeded(this.getTileSeed(var4 - 1, var5)).nextInt(this.texture.getWidth() / 64);
      }

      var11 += var13;
      final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(var12 * 2 + 1, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16 + var13;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }
}
