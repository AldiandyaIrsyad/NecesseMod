package necesse.level.gameObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CoinPileObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.CoinItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CoinPileObject extends GameObject {
   private GameTexture texture;
   private int maxStackSize = 300;
   private Point[] stackOffsets = new Point[]{new Point(-10, -14), new Point(-10, -14), new Point(-4, -10), new Point(4, -14), new Point(10, -10), new Point(-10, -6), new Point(-4, -2), new Point(4, -6), new Point(10, -2), new Point(-10, 2), new Point(-6, 6), new Point(4, 2), new Point(10, 6)};

   public CoinPileObject() {
      super(new Rectangle(32, 32));
      this.setItemCategory(new String[]{"misc"});
      this.drawDamage = false;
      this.objectHealth = 10;
      this.toolType = ToolType.ALL;
      this.rarity = Item.Rarity.RARE;
      this.stackSize = 5000;
      this.attackThrough = true;
      this.canPlaceOnProtectedLevels = true;
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("item", this.getMultiTile(0).getMasterObject().getStringID());
   }

   public void setCoins(int var1, Level var2, int var3, int var4) {
      ObjectEntity var5 = var2.entityManager.getObjectEntity(var3, var4);
      if (var5 instanceof CoinPileObjectEntity) {
         ((CoinPileObjectEntity)var5).coinAmount = var1;
      }

   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      if (var1.getObjectID(var2, var3) != 0 && !var1.getObject(var2, var3).isGrass && var1.getObjectID(var2, var3) != this.getID()) {
         return "occupied";
      } else if (!this.canPlaceOnLiquid && var1.isLiquidTile(var2, var3)) {
         return "liquid";
      } else {
         return this.getCoinAmount(var1, var2, var3) >= this.maxStackSize ? "toomanycoins" : null;
      }
   }

   public void placeObject(Level var1, int var2, int var3, int var4) {
      boolean var5 = var1.getObjectID(var2, var3) == this.getID();
      int var6 = var5 ? this.getCoinAmount(var1, var2, var3) : 0;
      super.placeObject(var1, var2, var3, var4);
      this.setCoins(var6 + 1, var1, var2, var3);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      float var12 = 1.0F;
      int var13 = this.getCoinAmount(var3, var4, var5);
      if (var8 != null && !Settings.hideUI) {
         Rectangle var14 = new Rectangle(var4 * 32, var5 * 32 - var13, 32, var13 + 4);
         if (var8.getCollision().intersects(var14)) {
            var12 = 0.2F;
         } else if (var14.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
            var12 = 0.2F;
         }
      }

      final SharedTextureDrawOptions var19 = new SharedTextureDrawOptions(this.texture);

      for(int var15 = 0; var15 < var13; ++var15) {
         int var16 = var15 % this.stackOffsets.length;
         int var17 = var15 / this.stackOffsets.length;
         Point var18 = this.stackOffsets[var16];
         var19.addSprite(var17 == 0 ? 0 : 1, 0, 32).alpha(var12).light(var9).pos(var10 + var18.x, var11 + var18.y - var17 * 4);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var19.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      GameLight var8 = var1.getLightLevel(var2, var3);
      int var9 = var7.getTileDrawX(var2);
      int var10 = var7.getTileDrawY(var3);
      int var11;
      if (var1.getObjectID(var2, var3) != this.getID()) {
         var11 = 1;
      } else {
         var11 = this.getCoinAmount(var1, var2, var3) + 1;
      }

      SharedTextureDrawOptions var12 = new SharedTextureDrawOptions(this.texture);
      int var13 = var11 - 1 > 0 ? (var11 - 1) % this.stackOffsets.length : 0;
      int var14 = var11 / this.stackOffsets.length;
      Point var15 = this.stackOffsets[var13];
      var12.addSprite(var14 == 0 ? 0 : 1, 0, 32).light(var8).alpha(var5).pos(var9 + var15.x, var10 + var15.y - var14 * 4);
      var12.draw();
   }

   private int getCoinAmount(Level var1, int var2, int var3) {
      int var4 = 1;
      ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
      if (var5 instanceof CoinPileObjectEntity) {
         var4 = ((CoinPileObjectEntity)var5).coinAmount;
      }

      return var4;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/coinpile");
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      this.playDamageSound(var1, var2, var3, true);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new CoinPileObjectEntity(var1, var2, var3);
   }

   public Item generateNewObjectItem() {
      return new CoinItem(this);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      int var4 = this.getCoinAmount(var1, var2, var3);
      return var4 == 0 ? new LootTable() : new LootTable(new LootItemInterface[]{(new LootItem("coin", var4)).splitItems(10).preventLootMultiplier()});
   }

   public void playPlaceSound(int var1, int var2) {
      Screen.playSound(GameResources.coins, SoundEffect.effect((float)(var1 * 32 + 16), (float)(var2 * 32 + 16)));
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.coins, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }
}
