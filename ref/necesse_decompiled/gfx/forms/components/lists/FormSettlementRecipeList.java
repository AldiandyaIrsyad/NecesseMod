package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.function.Predicate;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;

public abstract class FormSettlementRecipeList extends FormGeneralGridList<RecipeElement> {
   protected Predicate<Recipe> filter;
   protected boolean shouldUpdate;

   public FormSettlementRecipeList(int var1, int var2, int var3, int var4, Predicate<Recipe> var5) {
      super(var1, var2, var3, var4, 36, 36);
      this.filter = var5;
      this.shouldUpdate = true;
   }

   public FormSettlementRecipeList(int var1, int var2, int var3, int var4, Tech... var5) {
      this(var1, var2, var3, var4, (var1x) -> {
         return var1x.matchesTechs(var5);
      });
   }

   public void setFilter(Predicate<Recipe> var1) {
      this.filter = var1;
      this.shouldUpdate = true;
   }

   public void update() {
      this.shouldUpdate = true;
   }

   protected void forceUpdate() {
      this.shouldUpdate = false;
      this.elements.clear();
      Recipes.streamRecipes().filter((var1) -> {
         return this.filter == null || this.filter.test(var1);
      }).forEach((var1) -> {
         this.elements.add(new RecipeElement(var1));
      });
      this.limitMaxScroll();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.shouldUpdate) {
         this.forceUpdate();
      }

      super.draw(var1, var2, var3);
   }

   public abstract void onRecipeClicked(Recipe var1, PlayerMob var2);

   public class RecipeElement extends FormListGridElement<FormSettlementRecipeList> {
      public final Recipe recipe;

      public RecipeElement(Recipe var2) {
         this.recipe = var2;
      }

      void draw(FormSettlementRecipeList var1, TickManager var2, PlayerMob var3, int var4) {
         boolean var5 = this.isMouseOver(var1);
         Color var6 = Settings.UI.activeElementColor;
         if (var5) {
            var6 = Settings.UI.highlightElementColor;
         }

         GameTexture var7 = var5 ? Settings.UI.inventoryslot_small.highlighted : Settings.UI.inventoryslot_small.active;
         var7.initDraw().color(var6).draw(2, 2);
         this.recipe.draw(2, 2, var3);
         if (var5) {
            Screen.addTooltip(this.recipe.getTooltip((CanCraft)null, var3, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }

      }

      void onClick(FormSettlementRecipeList var1, int var2, InputEvent var3, PlayerMob var4) {
         FormSettlementRecipeList.this.onRecipeClicked(this.recipe, var4);
      }

      void onControllerEvent(FormSettlementRecipeList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormSettlementRecipeList.this.onRecipeClicked(this.recipe, var5);
            var3.use();
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormSettlementRecipeList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormSettlementRecipeList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormSettlementRecipeList)var1, var2, var3, var4);
      }
   }
}
