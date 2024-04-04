package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class Bathtub2Object extends FurnitureObject {
   protected String texturePath;
   public GameTexture texture;
   protected int counterID;

   public Bathtub2Object(String var1, ToolType var2, Color var3) {
      super(new Rectangle(0, 10, 24, 2));
      this.texturePath = var1;
      this.counterID = -1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(1, 0, 2, 1, false, new int[]{this.counterID, this.getID()});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile(this.texturePath);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(1, 0, 32).light(var9).pos(var10, var11);
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(1, 1, 32).light(var9).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 5;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 28;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }
}
