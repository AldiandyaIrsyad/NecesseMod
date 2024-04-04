package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import necesse.engine.ClipboardTracker;
import necesse.engine.GameLog;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairButtonGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.floatMenu.SettlementRecipeFloatMenu;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public abstract class SettlementWorkstationConfigForm extends FormSwitcher {
   public final Point tile;
   public final Client client;
   private Form mainForm;
   private Form recipeConfigForm;
   private ClipboardTracker<ConfigData> listClipboard;
   private FormContentIconButton pasteButton;
   private FormLocalTextButton addRecipeButton;
   private FormContentBox recipeContent;
   private FormFlow recipeContentFlow;
   private SettlementWorkstationLevelObject workstationObject;
   private ArrayList<RecipeForm> recipes = new ArrayList();

   public SettlementWorkstationConfigForm(String var1, int var2, int var3, Point var4, Client var5, GameMessage var6, PlayerMob var7, SettlementWorkstationLevelObject var8, ArrayList<SettlementWorkstationRecipe> var9) {
      this.tile = var4;
      this.client = var5;
      this.workstationObject = var8;
      this.mainForm = (Form)this.addComponent(new Form(var1, var2, var3));
      FormFlow var10 = new FormFlow(5);
      if (var6 != null) {
         this.mainForm.addComponent(new FormLocalLabel(var6, new FontOptions(20), -1, 5, var10.next(30)));
      }

      int var11 = var10.next(28);
      int var12 = this.mainForm.getWidth() - 28;
      ((FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var12, var11, FormInputSize.SIZE_24, ButtonColor.RED, Settings.UI.container_storage_remove, new GameMessage[]{new LocalMessage("ui", "settlementremoveworkstation")}))).onClicked((var1x) -> {
         this.onRemove();
      });
      var12 -= 26;
      this.pasteButton = (FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var12, var11, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
      this.pasteButton.onClicked((var1x) -> {
         ConfigData var2 = (ConfigData)this.listClipboard.getValue();
         if (var2 != null) {
            Iterator var3 = var2.recipes.iterator();

            while(var3.hasNext()) {
               SettlementWorkstationRecipe var4 = (SettlementWorkstationRecipe)var3.next();
               this.addRecipe(var4);
            }

            this.updateRecipesContent();
         }

      });
      var12 -= 26;
      this.listClipboard = new ClipboardTracker<ConfigData>() {
         public ConfigData parse(String var1) {
            try {
               return SettlementWorkstationConfigForm.this.new ConfigData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(ConfigData var1) {
            SettlementWorkstationConfigForm.this.pasteButton.setActive(var1 != null && !var1.recipes.isEmpty());
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((ConfigData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
      ((FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var12, var11, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var1x) -> {
         ArrayList var2 = (ArrayList)this.recipes.stream().map((var0) -> {
            return var0.element;
         }).collect(Collectors.toCollection(ArrayList::new));
         SaveData var3 = (new ConfigData(var2)).getSaveData();
         Screen.putClipboard(var3.getScript());
         this.listClipboard.forceUpdate();
      });
      var12 -= 26;
      int var13 = Math.min(200, var12 - 8);
      this.addRecipeButton = (FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "workstationaddrecipe", 4, var11, var13, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.addRecipeButton.onClicked((var4x) -> {
         SettlementRecipeFloatMenu var5 = new SettlementRecipeFloatMenu(this, var13 - 4, 200, var7, var8) {
            public void onRecipeClicked(Recipe var1, PlayerMob var2) {
               SettlementWorkstationConfigForm.this.addRecipe(var1);
               SettlementWorkstationConfigForm.this.updateRecipesContent();
               SettlementWorkstationConfigForm.this.playTickSound();
               this.remove();
            }
         };
         this.getManager().openFloatMenu(var5, this.addRecipeButton.getX() - var4x.event.pos.hudX + 2, this.addRecipeButton.getY() - var4x.event.pos.hudY - 200 + 22);
      });
      int var14 = var10.next();
      this.recipeContent = (FormContentBox)this.mainForm.addComponent(new FormContentBox(0, var14, var2, var3 - var14 - 32));
      this.recipeContentFlow = new FormFlow();
      this.setRecipes(var9);
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", var2 / 2 - 4, this.mainForm.getHeight() - 28, var2 / 2, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         this.onBack();
      });
      this.recipeConfigForm = (Form)this.addComponent(new Form(var1 + "-config", var2, var3));
      this.makeCurrent(this.mainForm);
   }

   public void addRecipe(SettlementWorkstationRecipe var1) {
      if (this.recipes.size() < SettlementWorkstation.maxRecipes) {
         int var3;
         do {
            var3 = GameRandom.globalRandom.nextInt();
         } while(!this.recipes.stream().noneMatch((var1x) -> {
            return var1x.element.uniqueID == var3;
         }));

         int var2 = var3;
         var3 = this.recipes.size();
         Packet var4 = new Packet();
         var1.writePacket(new PacketWriter(var4));
         SettlementWorkstationRecipe var5 = new SettlementWorkstationRecipe(var2, new PacketReader(var4));
         this.recipes.add(var3, new RecipeForm(this.recipeContent, this.recipes.size(), var5));
         this.onSubmitUpdate(var3, var5);
      }

   }

   public void addRecipe(Recipe var1) {
      this.addRecipe(new SettlementWorkstationRecipe(-1, var1));
   }

   public void onRecipeRemove(SettlementWorkstationRecipeRemoveEvent var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.recipes.size(); ++var3) {
         RecipeForm var4 = (RecipeForm)this.recipes.get(var3);
         if (var4.element.uniqueID == var1.uniqueID) {
            this.recipes.remove(var3);
            this.recipeContent.removeComponent(var4);
            var2 = true;
            --var3;
         }
      }

      if (var2) {
         this.updateRecipesContent();
      }

   }

   public void onRecipeUpdate(SettlementWorkstationRecipeUpdateEvent var1) {
      this.onRecipeUpdate(var1.index, var1.uniqueID, new PacketReader(var1.recipeContent), true);
   }

   private void onRecipeUpdate(int var1, int var2, PacketReader var3, boolean var4) {
      if (var1 >= 0 && var1 > this.recipes.size()) {
         GameLog.warn.println("Received invalid settlement recipe index");
         this.onBack();
      } else {
         int var5 = -1;

         for(int var6 = 0; var6 < this.recipes.size(); ++var6) {
            if (((RecipeForm)this.recipes.get(var6)).element.uniqueID == var2) {
               var5 = var6;
               break;
            }
         }

         RecipeForm var7;
         if (var5 != -1) {
            if (var1 < 0) {
               ((RecipeForm)this.recipes.get(var5)).update(var3);
            } else if (var5 == var1) {
               ((RecipeForm)this.recipes.get(var5)).update(var3);
            } else {
               var7 = (RecipeForm)this.recipes.remove(var5);
               this.recipes.add(var1, var7);
               var7.update(var3);
               if (var4) {
                  this.updateRecipesContent();
               }
            }
         } else if (var1 < 0) {
            GameLog.warn.println("Could not find recipe for update");
         } else if (var1 == this.recipes.size()) {
            var7 = (RecipeForm)this.recipeContentFlow.nextY(new RecipeForm(this.recipeContent, var1, new SettlementWorkstationRecipe(var2, var3)));
            this.recipes.add(var1, var7);
            this.recipeContent.addComponent(var7);
            this.recipeContent.setContentBox(new Rectangle(this.recipeContent.getWidth(), this.recipeContentFlow.next()));
         } else {
            this.recipes.add(var1, new RecipeForm(this.recipeContent, var1, new SettlementWorkstationRecipe(var2, var3)));
            if (var4) {
               this.updateRecipesContent();
            }
         }

      }
   }

   public void setRecipes(ArrayList<SettlementWorkstationRecipe> var1) {
      for(int var2 = 0; var2 < Math.max(var1.size(), this.recipes.size()); ++var2) {
         if (var2 < var1.size()) {
            SettlementWorkstationRecipe var3 = (SettlementWorkstationRecipe)var1.get(var2);
            Packet var4 = new Packet();
            var3.writePacket(new PacketWriter(var4));
            this.onRecipeUpdate(var2, var3.uniqueID, new PacketReader(var4), false);
         } else {
            RecipeForm var5 = (RecipeForm)this.recipes.remove(var2);
            this.recipeContent.removeComponent(var5);
            --var2;
         }
      }

      this.updateRecipesContent();
   }

   public void updateRecipesContent() {
      this.recipeContentFlow = new FormFlow();
      int var1 = 0;

      for(int var2 = this.recipes.size(); var1 < var2; ++var1) {
         RecipeForm var3 = (RecipeForm)this.recipes.get(var1);
         var3.index = var1;
         if (!this.recipeContent.hasComponent(var3)) {
            this.recipeContent.addComponent((RecipeForm)this.recipeContentFlow.nextY(var3));
         } else {
            var3.setPosition(0, this.recipeContentFlow.next(var3.getHeight()));
         }
      }

      Iterator var4 = this.recipes.iterator();

      while(var4.hasNext()) {
         RecipeForm var5 = (RecipeForm)var4.next();
         var5.updateIndex(var5.index);
      }

      this.recipeContent.setContentBox(new Rectangle(this.recipeContent.getWidth(), this.recipeContentFlow.next()));
      Screen.submitNextMoveEvent();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.listClipboard.update();
      this.addRecipeButton.setActive(this.recipes.size() < SettlementWorkstation.maxRecipes);
      super.draw(var1, var2, var3);
   }

   public void setPosFocus() {
      ContainerComponent.setPosFocus(this.mainForm);
      ContainerComponent.setPosFocus(this.recipeConfigForm);
   }

   public void setPosInventory() {
      ContainerComponent.setPosInventory(this.mainForm);
      ContainerComponent.setPosInventory(this.recipeConfigForm);
   }

   public abstract void onSubmitRemove(int var1);

   public abstract void onSubmitUpdate(int var1, SettlementWorkstationRecipe var2);

   public abstract void onRemove();

   public abstract void onBack();

   private class RecipeForm extends Form {
      public int index;
      public SettlementWorkstationRecipe element;
      public FormFairTypeLabel subtitle;
      public FormLabelEdit label;
      public FormIconButton moveUpButton;
      public FormIconButton moveDownButton;
      public FormContentIconButton renameButton;

      public RecipeForm(FormContentBox var2, int var3, SettlementWorkstationRecipe var4) {
         super(var2.getWidth(), 44);
         this.index = var3;
         this.element = var4;
         this.drawBase = false;
         int var5 = this.getWidth() - 26 - 8;
         ((FormContentIconButton)this.addComponent(new FormContentIconButton(var5, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_remove, new GameMessage[]{new LocalMessage("ui", "removebutton")}))).onClicked((var1x) -> {
            SettlementWorkstationConfigForm.this.onSubmitRemove(this.element.uniqueID);
         });
         var5 -= 26;
         ((FormContentIconButton)this.addComponent(new FormContentIconButton(var5, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var1x) -> {
            ArrayList var2 = new ArrayList(1);
            var2.add(this.element);
            SaveData var3 = (SettlementWorkstationConfigForm.this.new ConfigData(var2)).getSaveData();
            Screen.putClipboard(var3.getScript());
            SettlementWorkstationConfigForm.this.listClipboard.forceUpdate();
         });
         var5 -= 26;
         if (var4.canConfigureIngredientFilter()) {
            FormContentIconButton var6 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var5, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_config, new GameMessage[]{new LocalMessage("ui", "recipeconfigureingredients")}));
            var6.onClicked((var3x) -> {
               Form var4x = new Form(this.getWidth() - 4, 400);
               final FormContentBox var5 = (FormContentBox)var4x.addComponent(new FormContentBox(0, 32, var4x.getWidth(), var4x.getHeight() - 32));
               final ItemCategoriesFilter var6x = var4.ingredientFilter;
               ItemCategoryExpandedSetting var7 = Settings.getItemCategoryExpandedSetting(var4.recipe);
               ItemCategoriesFilterForm var8 = (ItemCategoriesFilterForm)var5.addComponent(new ItemCategoriesFilterForm(4, 28, var6x, false, var7, SettlementWorkstationConfigForm.this.client.characterStats.items_obtained, true) {
                  public void onItemsChanged(Item[] var1x, boolean var2) {
                     var1.ingredientFilter = var6;
                     SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                  }

                  public void onItemLimitsChanged(Item var1x, ItemCategoriesFilter.ItemLimits var2) {
                  }

                  public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, boolean var2) {
                     var1.ingredientFilter = var6;
                     SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                  }

                  public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, int var2) {
                  }

                  public void onDimensionsChanged(int var1x, int var2) {
                     var5.setContentBox(new Rectangle(0, 0, Math.max(this.getWidth(), var1x), this.getY() + var2));
                  }
               });
               ((FormLocalTextButton)var5.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, var5.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var2) -> {
                  if (!var6x.master.isAllAllowed()) {
                     var6x.master.setAllowed(true);
                     var8.updateAllButtons();
                     var8.onCategoryChanged(var8.filter.master, true);
                  }

               });
               ((FormLocalTextButton)var5.addComponent(new FormLocalTextButton("ui", "clearallbutton", var5.getWidth() / 2 + 2, 0, var5.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var2) -> {
                  if (var6x.master.isAnyAllowed()) {
                     var6x.master.setAllowed(false);
                     var8.updateAllButtons();
                     var8.onCategoryChanged(var8.filter.master, false);
                  }

               });
               FormTextInput var9 = (FormTextInput)var4x.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, var5.getWidth() - 8, -1, 500));
               var9.placeHolder = new LocalMessage("ui", "searchtip");
               var9.onChange((var2) -> {
                  var8.setSearch(var9.getText());
               });
               FormFloatMenu var10 = new FormFloatMenu(var6, var4x);
               this.getManager().openFloatMenu(var10, this.getX() - var3x.event.pos.hudX + 2, this.getY() - var3x.event.pos.hudY + 2);
            });
            var5 -= 26;
         }

         this.moveUpButton = (FormIconButton)this.addComponent(new FormIconButton(5, this.getHeight() / 2 - 13, Settings.UI.button_moveup, 16, 13, new GameMessage[]{new LocalMessage("ui", "moveupbutton")}));
         this.moveUpButton.onClicked((var1x) -> {
            if (!Screen.input().isKeyDown(340) && !Screen.input().isKeyDown(344)) {
               SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index - 1, this.element);
            } else {
               SettlementWorkstationConfigForm.this.onSubmitUpdate(0, this.element);
            }

         });
         this.moveDownButton = (FormIconButton)this.addComponent(new FormIconButton(5, this.getHeight() / 2, Settings.UI.button_movedown, 16, 13, new GameMessage[]{new LocalMessage("ui", "movedownbutton")}));
         this.moveDownButton.onClicked((var1x) -> {
            if (!Screen.input().isKeyDown(340) && !Screen.input().isKeyDown(344)) {
               SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index + 1, this.element);
            } else {
               SettlementWorkstationConfigForm.this.onSubmitUpdate(SettlementWorkstationConfigForm.this.recipes.size() - 1, this.element);
            }

         });
         final Object var11 = new Object();
         final Object var7 = new Object();
         this.addComponent(new FormCustomDraw(25, this.getHeight() / 2 - 16, 32, 32) {
            public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
               if (this.isControllerFocus()) {
                  if (var1.getState() == ControllerInput.MENU_SELECT) {
                     if (!var1.buttonState) {
                        this.playTickSound();
                        SelectionFloatMenu var4 = new SelectionFloatMenu(RecipeForm.this);
                        var4.add(((GameMessage)SettlementWorkstationRecipe.Mode.DO_COUNT.countMessageFunction.apply("X")).translate(), () -> {
                           if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_COUNT) {
                              RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_COUNT;
                              SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                              RecipeForm.this.updateSubtitleText();
                           }

                           var4.remove();
                        });
                        var4.add(((GameMessage)SettlementWorkstationRecipe.Mode.DO_UNTIL.countMessageFunction.apply("X")).translate(), () -> {
                           if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_UNTIL) {
                              RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_UNTIL;
                              SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                              RecipeForm.this.updateSubtitleText();
                           }

                           var4.remove();
                        });
                        var4.add(((GameMessage)SettlementWorkstationRecipe.Mode.DO_FOREVER.countMessageFunction.apply("X")).translate(), () -> {
                           if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_FOREVER) {
                              RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_FOREVER;
                              SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                              RecipeForm.this.updateSubtitleText();
                           }

                           var4.remove();
                        });
                        ControllerFocus var5 = this.getManager().getCurrentFocus();
                        if (var5 != null) {
                           this.getManager().openFloatMenuAt(var4, var5.boundingBox.x, var5.boundingBox.y + 32);
                        } else {
                           this.getManager().openFloatMenuAt(var4, 0, 0);
                        }

                        var1.use();
                     }
                  } else if (var1.getState() != ControllerInput.MENU_NEXT && !var1.isRepeatEvent(var11)) {
                     if (var1.getState() == ControllerInput.MENU_PREV || var1.isRepeatEvent(var7)) {
                        if (var1.buttonState) {
                           ControllerInput.startRepeatEvents(var1, var7);
                           RecipeForm.this.element.modeCount = Math.max(0, RecipeForm.this.element.modeCount - 1);
                           SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                           if (var1.shouldSubmitSound()) {
                              this.playTickSound();
                           }

                           RecipeForm.this.updateSubtitleText();
                        }

                        var1.use();
                     }
                  } else {
                     if (var1.buttonState) {
                        ControllerInput.startRepeatEvents(var1, var11);
                        RecipeForm.this.element.modeCount = Math.min(65535, RecipeForm.this.element.modeCount + 1);
                        SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                        if (var1.shouldSubmitSound()) {
                           this.playTickSound();
                        }

                        RecipeForm.this.updateSubtitleText();
                     }

                     var1.use();
                  }
               }

            }

            public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
               ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
            }

            public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
               RecipeForm.this.element.recipe.draw(this.getX(), this.getY(), var2);
               if (this.isHovering()) {
                  ListGameTooltips var4 = new ListGameTooltips(RecipeForm.this.element.recipe.getTooltip((CanCraft)null, var2, new GameBlackboard()));
                  if (Input.lastInputIsController) {
                     var4.add((Object)(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("ui", "configurebutton"))));
                     var4.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "increasebutton"))));
                     var4.add((Object)(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "decreasebutton"))));
                  }

                  Screen.addTooltip(var4, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
               }

            }
         });
         String var8 = this.element.recipe.resultItem.getItemDisplayName();
         this.label = (FormLabelEdit)this.addComponent(new FormLabelEdit(this.element.name == null ? var8 : this.element.name, new FontOptions(20), Settings.UI.activeTextColor, 57, 0, 100, 30), -1000);
         this.renameButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(var5, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[0]));
         AtomicBoolean var9 = new AtomicBoolean(false);
         this.label.onMouseChangedTyping((var2x) -> {
            var9.set(this.label.isTyping());
            this.runRenameUpdate();
         });
         this.label.onSubmit((var2x) -> {
            var9.set(this.label.isTyping());
            this.runRenameUpdate();
         });
         this.renameButton.onClicked((var2x) -> {
            var9.set(!this.label.isTyping());
            this.label.setTyping(!this.label.isTyping());
            this.runRenameUpdate();
         });
         this.runRenameUpdate();
         var5 -= 26;
         this.label.setWidth(var5 - 4 - 32);
         FontOptions var10 = new FontOptions(16);
         this.subtitle = (FormFairTypeLabel)this.addComponent((new FormFairTypeLabel("", 57, 22)).setFontOptions(var10));
         this.subtitle.setParsers(TypeParsers.replaceParser("[[-]]", new FairButtonGlyph(16, 16) {
            public void handleEvent(float var1, float var2, InputEvent var3) {
               if ((var3.getID() == -100 || var3.isRepeatEvent(var7)) && var3.state) {
                  var3.startRepeatEvents(var7);
                  byte var4 = 1;
                  if (Control.INV_QUICK_MOVE.isDown()) {
                     var4 = 10;
                  } else if (Control.INV_QUICK_TRASH.isDown()) {
                     var4 = 100;
                  }

                  RecipeForm.this.element.modeCount = Math.max(0, RecipeForm.this.element.modeCount - var4);
                  SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                  if (var3.shouldSubmitSound()) {
                     RecipeForm.this.playTickSound();
                  }

                  RecipeForm.this.updateSubtitleText();
               }

            }

            public void draw(float var1, float var2, Color var3) {
               Color var4;
               if (this.isHovering()) {
                  var4 = (Color)Settings.UI.button_minus_20.colorGetter.apply(ButtonState.HIGHLIGHTED);
               } else {
                  var4 = (Color)Settings.UI.button_minus_20.colorGetter.apply(ButtonState.ACTIVE);
               }

               Settings.UI.button_minus_20.texture.initDraw().color(var4).posMiddle((int)var1 + 8, (int)var2 - 8).draw();
            }
         }), TypeParsers.replaceParser("[[+]]", new FairButtonGlyph(16, 16) {
            public void handleEvent(float var1, float var2, InputEvent var3) {
               if ((var3.getID() == -100 || var3.isRepeatEvent(var11)) && var3.state) {
                  var3.startRepeatEvents(var11);
                  byte var4 = 1;
                  if (Control.INV_QUICK_MOVE.isDown()) {
                     var4 = 10;
                  } else if (Control.INV_QUICK_TRASH.isDown()) {
                     var4 = 100;
                  }

                  RecipeForm.this.element.modeCount = Math.min(65535, RecipeForm.this.element.modeCount + var4);
                  SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                  if (var3.shouldSubmitSound()) {
                     RecipeForm.this.playTickSound();
                  }

                  RecipeForm.this.updateSubtitleText();
               }

            }

            public void draw(float var1, float var2, Color var3) {
               Color var4;
               if (this.isHovering()) {
                  var4 = (Color)Settings.UI.button_plus_20.colorGetter.apply(ButtonState.HIGHLIGHTED);
               } else {
                  var4 = (Color)Settings.UI.button_plus_20.colorGetter.apply(ButtonState.ACTIVE);
               }

               Settings.UI.button_plus_20.texture.initDraw().color(var4).posMiddle((int)var1 + 8, (int)var2 - 8).draw();
            }
         }), TypeParsers.replaceParser("[[v]]", new FairButtonGlyph(16, 16) {
            public void handleEvent(float var1, float var2, InputEvent var3) {
               if (var3.getID() == -100) {
                  if (var3.state) {
                     RecipeForm.this.playTickSound();
                     SelectionFloatMenu var4 = new SelectionFloatMenu(RecipeForm.this);
                     var4.add(((GameMessage)SettlementWorkstationRecipe.Mode.DO_COUNT.countMessageFunction.apply("X")).translate(), () -> {
                        if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_COUNT) {
                           RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_COUNT;
                           SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                           RecipeForm.this.updateSubtitleText();
                        }

                        var4.remove();
                     });
                     var4.add(((GameMessage)SettlementWorkstationRecipe.Mode.DO_UNTIL.countMessageFunction.apply("X")).translate(), () -> {
                        if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_UNTIL) {
                           RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_UNTIL;
                           SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                           RecipeForm.this.updateSubtitleText();
                        }

                        var4.remove();
                     });
                     var4.add(((GameMessage)SettlementWorkstationRecipe.Mode.DO_FOREVER.countMessageFunction.apply("X")).translate(), () -> {
                        if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_FOREVER) {
                           RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_FOREVER;
                           SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                           RecipeForm.this.updateSubtitleText();
                        }

                        var4.remove();
                     });
                     RecipeForm.this.getManager().openFloatMenu(var4, (int)var1 - var3.pos.hudX, (int)var2 - var3.pos.hudY - 12);
                  }

                  var3.use();
               }

            }

            public void draw(float var1, float var2, Color var3) {
               GameTexture var4;
               Color var5;
               if (this.isHovering()) {
                  var4 = Settings.UI.config_icon.highlighted;
                  var5 = Settings.UI.highlightTextColor;
               } else {
                  var4 = Settings.UI.config_icon.active;
                  var5 = Settings.UI.activeTextColor;
               }

               var4.initDraw().color(var5).posMiddle((int)var1 + 8, (int)var2 - 8).draw();
               if (this.isHovering()) {
                  Screen.addTooltip(new StringTooltips(Localization.translate("ui", "configurebutton")), TooltipLocation.FORM_FOCUS);
               }

            }
         }));
         this.updateSubtitleText();
         this.updateIndex(var3);
      }

      private void updateSubtitleText() {
         GameMessageBuilder var1 = (new GameMessageBuilder()).append("[[v]] ").append((GameMessage)this.element.mode.countMessageFunction.apply("[[-]] " + this.element.modeCount + " [[+]]"));
         this.subtitle.setText((GameMessage)var1);
      }

      private void runRenameUpdate() {
         if (this.label.isTyping()) {
            this.renameButton.setIcon(Settings.UI.container_rename_save);
            this.renameButton.setTooltips(new LocalMessage("ui", "recipesavename"));
         } else {
            String var1 = this.element.recipe.resultItem.getItemDisplayName();
            String var2 = this.element.name == null ? var1 : this.element.name;
            if (!this.label.getText().equals(var2)) {
               if (!this.label.getText().isEmpty() && !this.label.getText().equals(var1)) {
                  this.element.name = this.label.getText();
                  SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index, this.element);
               } else {
                  this.label.setText(var1);
                  if (this.element.name != null) {
                     this.element.name = null;
                     SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index, this.element);
                  }
               }
            }

            this.renameButton.setIcon(Settings.UI.container_rename);
            this.renameButton.setTooltips(new LocalMessage("ui", "recipechangename"));
         }

      }

      public void update(PacketReader var1) {
         this.element.applyPacket(var1);
         this.updateSubtitleText();
         if (!this.label.isTyping()) {
            String var2 = this.element.recipe.resultItem.getItemDisplayName();
            this.label.setText(this.element.name == null ? var2 : this.element.name);
         }

      }

      public void updateIndex(int var1) {
         this.index = var1;
         this.moveUpButton.setActive(this.index > 0);
         this.moveDownButton.setActive(this.index < SettlementWorkstationConfigForm.this.recipes.size() - 1);
      }
   }

   private class ConfigData {
      public final ArrayList<SettlementWorkstationRecipe> recipes;

      public ConfigData(ArrayList<SettlementWorkstationRecipe> var2) {
         this.recipes = var2;
      }

      public ConfigData(LoadData var2) {
         this.recipes = new ArrayList();
         Iterator var3 = var2.getLoadDataByName("RECIPE").iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();
            SettlementWorkstationRecipe var5 = new SettlementWorkstationRecipe(var4, false);
            if (SettlementWorkstationConfigForm.this.workstationObject.streamSettlementRecipes().anyMatch((var1x) -> {
               return var1x.getRecipeHash() == var5.recipe.getRecipeHash();
            })) {
               this.recipes.add(var5);
            }
         }

      }

      public SaveData getSaveData() {
         SaveData var1 = new SaveData("config");
         Iterator var2 = this.recipes.iterator();

         while(var2.hasNext()) {
            SettlementWorkstationRecipe var3 = (SettlementWorkstationRecipe)var2.next();
            SaveData var4 = new SaveData("RECIPE");
            var3.addSaveData(var4, false);
            var1.addSaveData(var4);
         }

         return var1;
      }
   }
}
