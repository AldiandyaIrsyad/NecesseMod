package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class FormIngredientRecipeList extends FormGeneralGridList<IngredientRecipe> {
   private Client client;

   public FormIngredientRecipeList(int var1, int var2, int var3, int var4, Client var5) {
      super(var1, var2, var3, var4, 36, 36);
      this.client = var5;
      this.setFilter((var0) -> {
         return true;
      });
   }

   public void setFilter(Predicate<Recipe> var1) {
      this.setRecipes((Collection)Recipes.streamRecipes().filter(var1).collect(Collectors.toList()));
   }

   public void setRecipes(Collection<Recipe> var1) {
      this.elements = new ArrayList();
      if (var1 != null) {
         this.elements.addAll((Collection)var1.stream().map((var1x) -> {
            return new IngredientRecipe(var1x);
         }).collect(Collectors.toList()));
      }

      this.limitMaxScroll();
   }

   public void onRecipeClicked(Recipe var1, InputEvent var2) {
   }

   public void addTooltips(Recipe var1, ListGameTooltips var2) {
   }

   public GameMessage getEmptyMessage() {
      return new LocalMessage("ui", "insertmat");
   }

   public class IngredientRecipe extends FormListGridElement<FormIngredientRecipeList> {
      public final Recipe recipe;

      public IngredientRecipe(Recipe var2) {
         this.recipe = var2;
      }

      void draw(FormIngredientRecipeList var1, TickManager var2, PlayerMob var3, int var4) {
         Color var5 = Settings.UI.activeElementColor;
         if (this.isMouseOver(var1)) {
            var5 = Settings.UI.highlightElementColor;
            if (!Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
               ListGameTooltips var6 = new ListGameTooltips(this.recipe.getTooltip(var3, new GameBlackboard()));
               if (this.recipe.isHidden) {
                  var6.add((new LocalMessage("tech", "madeinhidden", "tech", this.recipe.tech.displayName)).translate());
               } else {
                  var6.add((new LocalMessage("tech", "madein", "tech", this.recipe.tech.displayName)).translate());
               }

               FormIngredientRecipeList.this.addTooltips(this.recipe, var6);
               Screen.addTooltip(var6, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
         }

         GameTexture var7 = this.isMouseOver(var1) ? Settings.UI.inventoryslot_small.highlighted : Settings.UI.inventoryslot_small.active;
         var7.initDraw().color(var5).draw(2, 2);
         this.recipe.draw(2, 2, var3);
      }

      void onClick(FormIngredientRecipeList var1, int var2, InputEvent var3, PlayerMob var4) {
         FormIngredientRecipeList.this.onRecipeClicked(this.recipe, var3);
      }

      void onControllerEvent(FormIngredientRecipeList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormIngredientRecipeList.this.onRecipeClicked(this.recipe, InputEvent.ControllerButtonEvent(var3, var4));
            var3.use();
         }
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormIngredientRecipeList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormIngredientRecipeList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormIngredientRecipeList)var1, var2, var3, var4);
      }
   }
}
