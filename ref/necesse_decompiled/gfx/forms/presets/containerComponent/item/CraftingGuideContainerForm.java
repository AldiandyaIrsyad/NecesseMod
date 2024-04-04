package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import java.util.Collection;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.lists.FormIngredientRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.CraftingGuideContainer;
import necesse.inventory.recipe.Recipes;

public class CraftingGuideContainerForm<T extends CraftingGuideContainer> extends ContainerForm<T> {
   private FormContainerSlot ingredientSlot;
   private FormIngredientRecipeList ingredientList;
   private int itemID;

   public CraftingGuideContainerForm(Client var1, T var2) {
      super(var1, 400, 160, var2);
      InventoryItem var3 = var2.guideSlot.getItem(var2.client.playerMob.getInv());
      this.addComponent(new FormLocalLabel((GameMessage)(var3 == null ? new StaticMessage("NULL") : var3.getItemLocalization()), new FontOptions(20), -1, 10, 10));
      this.addComponent(this.ingredientSlot = new FormContainerMaterialSlot(var1, var2, var2.INGREDIENT_SLOT, this.getWidth() - 60, this.getHeight() - 80));
      this.addComponent(this.ingredientList = new FormIngredientRecipeList(0, this.getHeight() - 120 - 6, this.getWidth() - 80, this.getHeight() - 40, var1));
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      InventoryItem var4 = this.ingredientSlot.getContainerSlot().getItem();
      int var5 = var4 == null ? -1 : var4.item.getID();
      if (this.itemID != var5) {
         this.itemID = var5;
         if (this.itemID == -1) {
            this.ingredientList.setRecipes((Collection)null);
         } else {
            this.ingredientList.setRecipes(Recipes.getResultItems(var5));
         }
      }

      super.draw(var1, var2, var3);
   }
}
