package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.FlowerObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FlowerObject extends FurnitureObject {
   protected String textureName;
   public String customDrop;
   public GameTexture texture;
   public GameTexture itemTexture;
   public int spriteX;
   public int itemSpoilDurationMinutes;

   public FlowerObject(String var1, int var2, int var3, int var4, Color var5) {
      this(var1, var2, var3, var4, (String)null, var5);
   }

   public FlowerObject(String var1, int var2, int var3, int var4, String var5, Color var6) {
      super(new Rectangle(0, 0));
      this.textureName = var1;
      this.spriteX = var2;
      this.stackSize = var3;
      this.customDrop = var5;
      this.itemSpoilDurationMinutes = var4;
      this.mapColor = var6;
      this.setItemCategory(new String[]{"materials", "flowers"});
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "flower";
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.textureName);
      this.itemTexture = new GameTexture(var1, 0, 0, 32);
      this.texture = new GameTexture(var1, this.spriteX * 32, 0, 32, var1.getHeight());
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = new LootTable();
      if (this.customDrop != null) {
         var4.items.add((new LootItem(this.customDrop)).preventLootMultiplier());
      } else {
         var4.items.add(super.getLootTable(var1, var2, var3));
      }

      var4.items.add((new LootItem("flowerpot")).preventLootMultiplier());
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - 8;
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).pos(var10, var11 - (this.texture.getHeight() - 32));
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3) - 8;
      this.texture.initDraw().alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 32));
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      return !var1.getObject(var2, var3).isFlowerpot ? "notflowerpot" : null;
   }

   public boolean canReplace(Level var1, int var2, int var3, int var4) {
      return false;
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "flowertip"));
      return var3;
   }

   public Item generateNewObjectItem() {
      return (new FlowerObjectItem(this, () -> {
         return this.itemTexture;
      })).spoilDuration(this.itemSpoilDurationMinutes).addGlobalIngredient("anycompostable");
   }
}
