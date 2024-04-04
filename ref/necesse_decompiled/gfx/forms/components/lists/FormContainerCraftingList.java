package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCraftAction;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeFilter;
import necesse.inventory.recipe.Tech;

public class FormContainerCraftingList extends FormGeneralGridList<CraftableRecipe> implements FormRecipeList {
   protected Client client;
   protected List<CraftableRecipe> allRecipes;
   protected Tech[] techs;
   protected RecipeFilter filter;
   protected Supplier<Boolean> shouldUpdate;
   protected boolean isInventoryCrafting;
   protected boolean onlyCraftable;
   protected boolean showHidden;
   private boolean updateCraftable;
   public GameMessage usableError;
   public boolean showRecipeOnUsableError = true;

   public FormContainerCraftingList(int var1, int var2, int var3, int var4, Client var5, boolean var6, boolean var7, Tech... var8) {
      super(var1, var2, var3, var4, 36, 36);
      this.client = var5;
      this.techs = var8;
      this.isInventoryCrafting = var7;
      this.showHidden = var6;
      this.acceptMouseRepeatEvents = true;
   }

   protected void init() {
      super.init();
      this.updateRecipes();
      GlobalData.craftingLists.add(this);
   }

   public void setFilter(RecipeFilter var1) {
      if (this.filter != null) {
         this.filter.removeMonitor(this);
         this.shouldUpdate = null;
      }

      this.filter = var1;
      if (this.filter != null) {
         this.shouldUpdate = this.filter.addMonitor(this);
      }

      if (this.allRecipes != null) {
         this.updateList();
      }

   }

   public void setOnlyCraftable(boolean var1) {
      this.onlyCraftable = var1;
      if (this.allRecipes != null) {
         this.updateList();
      }

   }

   public void setShowHidden(boolean var1) {
      this.showHidden = var1;
      if (this.allRecipes != null) {
         this.updateList();
      }

   }

   protected void updateList() {
      Collection var1 = this.getContainer().getCraftInventories();
      this.allRecipes.forEach((var2x) -> {
         var2x.canCraft = this.getContainer().canCraftRecipe(var2x.recipe, var1, true);
         var2x.doesShow = this.getContainer().doesShowRecipe(var2x.recipe, var1);
      });
      this.elements.clear();
      boolean var2 = false;
      Iterator var3 = this.allRecipes.iterator();

      while(true) {
         while(var3.hasNext()) {
            CraftableRecipe var4 = (CraftableRecipe)var3.next();
            if (!this.showHidden && !var4.doesShow || this.onlyCraftable && !var4.canCraft.canCraft() || this.filter != null && !this.filter.isValid(var4.recipe, var4.doesShow, var4.canCraft.canCraft())) {
               var4.isCurrentlyShown = false;
               var4.setMoveEvent((InputEvent)null);
            } else {
               if (!var4.isCurrentlyShown) {
                  var2 = true;
                  var4.isCurrentlyShown = true;
               }

               this.elements.add(var4);
            }
         }

         this.updateCraftable = false;
         this.limitMaxScroll();
         if (var2) {
            Screen.submitNextMoveEvent();
         }

         return;
      }
   }

   public void updateCraftable() {
      this.updateCraftable = true;
   }

   public void updateRecipes() {
      this.allRecipes = (List)this.getContainer().streamRecipes(this.techs).filter((var1) -> {
         return this.isInventoryCrafting == var1.isInventory;
      }).map((var1) -> {
         return new CraftableRecipe(var1, new CanCraft(var1.recipe, true));
      }).collect(Collectors.toList());
      this.updateCraftable();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.shouldUpdate != null && (Boolean)this.shouldUpdate.get()) {
         this.updateList();
      }

      if (this.updateCraftable) {
         this.updateList();
      }

      super.draw(var1, var2, var3);
   }

   protected void drawEmptyMessage(TickManager var1) {
      if (this.allRecipes.size() != 0) {
         super.drawEmptyMessage(var1);
      }
   }

   public GameMessage getEmptyMessage() {
      return new LocalMessage("ui", "changefilters");
   }

   public FontOptions getEmptyMessageFontOptions() {
      return new FontOptions(16);
   }

   public Container getContainer() {
      return this.client.getContainer();
   }

   public void dispose() {
      super.dispose();
      GlobalData.craftingLists.remove(this);
      if (this.filter != null) {
         this.filter.removeMonitor(this);
      }

      this.shouldUpdate = null;
   }

   protected class CraftableRecipe extends FormListGridElement<FormContainerCraftingList> {
      private final Recipe recipe;
      private final int recipeID;
      public boolean isCurrentlyShown;
      private CanCraft canCraft;
      private boolean doesShow;

      public CraftableRecipe(ContainerRecipe var2, CanCraft var3) {
         this.recipe = var2.recipe;
         this.recipeID = var2.id;
         this.isCurrentlyShown = false;
         this.canCraft = var3;
      }

      void draw(FormContainerCraftingList var1, TickManager var2, PlayerMob var3, int var4) {
         boolean var5 = this.isMouseOver(var1);
         Color var6 = Settings.UI.activeElementColor;
         if (var5) {
            var6 = Settings.UI.highlightElementColor;
         }

         if (FormContainerCraftingList.this.usableError != null) {
            var6 = Settings.UI.deadElementColor;
         } else if (!this.canCraft.canCraft()) {
            var6 = Settings.UI.inactiveElementColor;
         }

         GameTexture var7 = var5 ? Settings.UI.inventoryslot_small.highlighted : Settings.UI.inventoryslot_small.active;
         var7.initDraw().color(var6).draw(2, 2);
         this.recipe.draw(2, 2, var3);
         if (var5 && !Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
            ListGameTooltips var8 = new ListGameTooltips();
            if (FormContainerCraftingList.this.usableError != null || FormContainerCraftingList.this.showRecipeOnUsableError) {
               var8.add((Object)this.recipe.getTooltip(this.canCraft, var3, new GameBlackboard()));
            }

            if (FormContainerCraftingList.this.usableError != null) {
               if (FormContainerCraftingList.this.showRecipeOnUsableError) {
                  var8.add((Object)(new SpacerGameTooltip(4)));
               }

               var8.add((Object)(new StringTooltips(FormContainerCraftingList.this.usableError.translate(), GameColor.RED)));
            }

            Screen.addTooltip(var8, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }

      }

      void onClick(FormContainerCraftingList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (FormContainerCraftingList.this.usableError == null) {
            if (var3.getID() == -100 || var3.isRepeatEvent((Object)this.recipe)) {
               var3.startRepeatEvents(this.recipe);
               int var5 = 1;
               boolean var6 = false;
               if (Control.CRAFT_ALL.isDown()) {
                  var5 = this.recipe.resultItem.itemStackSize() / this.recipe.resultAmount;
                  var6 = true;
               }

               if (FormContainerCraftingList.this.getContainer().canCraftRecipe(this.recipe, FormContainerCraftingList.this.getContainer().getCraftInventories(), false).canCraft()) {
                  int var7 = this.recipe.getRecipeHash();
                  int var8 = FormContainerCraftingList.this.getContainer().applyCraftingAction(this.recipeID, var7, var5, var6);
                  FormContainerCraftingList.this.client.network.sendPacket(new PacketCraftAction(this.recipeID, var7, var5, var8, var6));
                  if (var8 > 0 && var3.shouldSubmitSound()) {
                     FormContainerCraftingList.this.playTickSound();
                  }

                  GlobalData.updateCraftable();
               }
            }

         }
      }

      void onControllerEvent(FormContainerCraftingList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (FormContainerCraftingList.this.usableError == null) {
            if (var3.getState() == ControllerInput.MENU_SELECT || var3.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || var3.isRepeatEvent((Object)this.recipe)) {
               var3.startRepeatEvents(this.recipe);
               int var6 = 1;
               if (Control.CRAFT_ALL.isDown()) {
                  var6 = this.recipe.resultItem.itemStackSize() / this.recipe.resultAmount;
               }

               if (FormContainerCraftingList.this.getContainer().canCraftRecipe(this.recipe, FormContainerCraftingList.this.getContainer().getCraftInventories(), false).canCraft()) {
                  int var7 = this.recipe.getRecipeHash();
                  boolean var8 = var3.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || var3.getRepeatState() == ControllerInput.MENU_ITEM_ACTIONS_MENU;
                  int var9 = FormContainerCraftingList.this.getContainer().applyCraftingAction(this.recipeID, var7, var6, var8);
                  FormContainerCraftingList.this.client.network.sendPacket(new PacketCraftAction(this.recipeID, var7, var6, var9, var8));
                  if (var9 > 0 && var3.shouldSubmitSound()) {
                     FormContainerCraftingList.this.playTickSound();
                  }

                  GlobalData.updateCraftable();
               }
            }

         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("controls", "crafttohand"), ControllerInput.MENU_SELECT);
         Screen.addControllerGlyph(Localization.translate("controls", "crafttoinventory"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormContainerCraftingList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormContainerCraftingList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormContainerCraftingList)var1, var2, var3, var4);
      }
   }
}
