package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CheesePressObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.ProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.light.GameLight;

public class CheesePressObject extends GameObject implements SettlementWorkstationObject {
   public GameTexture texture;
   public GameTexture onTexture;

   public CheesePressObject() {
      super(new Rectangle(4, 6, 24, 20));
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.mapColor = new Color(137, 136, 138);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.rarity = Item.Rarity.COMMON;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.replaceCategories.add("workstation");
      this.canReplaceCategories.add("workstation");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("furniture");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/cheesepress");

      try {
         this.onTexture = GameTexture.fromFileRaw("objects/cheesepress_on");
      } catch (FileNotFoundException var2) {
         this.onTexture = this.texture;
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      byte var10 = var3.getObjectRotation(var4, var5);
      int var11 = var7.getTileDrawX(var4);
      int var12 = var7.getTileDrawY(var5);
      ObjectEntity var13 = var3.entityManager.getObjectEntity(var4, var5);
      GameTexture var14 = this.texture;
      if (var13 instanceof ProcessingInventoryObjectEntity && ((ProcessingInventoryObjectEntity)var13).isProcessing()) {
         var14 = this.onTexture;
      }

      final TextureDrawOptionsEnd var15 = var14.initDraw().sprite(var10 % var14.getWidth() / 32, 0, 32, var14.getHeight()).light(var9).pos(var11, var12 - (var14.getHeight() - 32));
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(var4 % this.texture.getWidth() / 32, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 32));
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.PROCESSING_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new CheesePressObjectEntity(var1, var2, var3);
   }

   public ProcessingTechInventoryObjectEntity getProcessingObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof ProcessingTechInventoryObjectEntity ? (ProcessingTechInventoryObjectEntity)var4 : null;
   }

   public Stream<Recipe> streamSettlementRecipes(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? Recipes.streamRecipes(var4.techs) : Stream.empty();
   }

   public boolean isProcessingInventory(Level var1, int var2, int var3) {
      return true;
   }

   public boolean canCurrentlyCraft(Level var1, int var2, int var3, Recipe var4) {
      ProcessingTechInventoryObjectEntity var5 = this.getProcessingObjectEntity(var1, var2, var3);
      if (var5 != null) {
         return var5.getExpectedResults().crafts < 10;
      } else {
         return false;
      }
   }

   public int getMaxCraftsAtOnce(Level var1, int var2, int var3, Recipe var4) {
      return 5;
   }

   public InventoryRange getProcessingInputRange(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getInputInventoryRange() : null;
   }

   public InventoryRange getProcessingOutputRange(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getOutputInventoryRange() : null;
   }

   public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getCurrentAndExpectedResults().items : new ArrayList();
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "cheesepresstip"));
      return var3;
   }
}
