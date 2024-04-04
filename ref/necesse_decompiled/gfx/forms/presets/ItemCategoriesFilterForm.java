package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.stats.ItemsObtainedStat;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconValueButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public abstract class ItemCategoriesFilterForm extends Form {
   public final ItemCategoriesFilter filter;
   public final boolean allowMaxAmountSetting;
   private ItemCategoryExpandedSetting expandedSetting;
   private ItemsObtainedStat itemsObtainedStats;
   private LinkedForm contentForm;
   private HashMap<Integer, ItemForm> itemForms = new HashMap();
   private HashMap<Integer, CategoryForm> categoryForms = new HashMap();

   public ItemCategoriesFilterForm(int var1, int var2, ItemCategoriesFilter var3, boolean var4, ItemCategoryExpandedSetting var5, ItemsObtainedStat var6, boolean var7) {
      super(0, 0);
      this.drawBase = false;
      this.filter = var3;
      this.allowMaxAmountSetting = var4;
      this.expandedSetting = var5;
      this.itemsObtainedStats = var6;
      this.setPosition(var1, var2);
      this.contentForm = (LinkedForm)this.addComponent(new LinkedForm("master", 0, 0, (LinkedForm)null, (LinkedForm)null));
      this.contentForm.drawBase = false;
      ItemCategoriesFilter.ItemCategoryFilter var8 = var3.master;
      if (var7) {
         while(true) {
            List var9 = (List)var8.getChildren().stream().filter((var1x) -> {
               return !this.isCategoryEmptyRecursive(var1x);
            }).collect(Collectors.toList());
            if (var9.size() != 1) {
               break;
            }

            ItemCategoriesFilter.ItemCategoryFilter var10 = (ItemCategoriesFilter.ItemCategoryFilter)var9.get(0);
            var8 = var10;
            this.expandedSetting = var5 == null ? null : var5.getChild(var10.category);
         }
      }

      if (var8.parent != null) {
         var8 = var8.parent;
      }

      this.addChildren(this.contentForm, var8, var5);
      this.contentForm.updateYChildren();
      this.contentForm.updateDimensionsForward();
      this.updateDimensions();
   }

   public abstract void onItemsChanged(Item[] var1, boolean var2);

   public abstract void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2);

   public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

   public abstract void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2);

   public abstract void onDimensionsChanged(int var1, int var2);

   public void updateDimensions() {
      boolean var1 = false;
      int var2 = this.getWidth();
      int var3 = this.contentForm.getWidth();
      if (var2 != var3) {
         var1 = true;
         this.setWidth(var3);
      }

      int var4 = this.getHeight();
      int var5 = this.contentForm.getHeight();
      if (var4 != var5) {
         var1 = true;
         this.setHeight(var5);
      }

      if (var1) {
         this.onDimensionsChanged(var3, var5);
      }

   }

   public void updateAllButtons() {
      Iterator var1 = this.itemForms.values().iterator();

      while(var1.hasNext()) {
         ItemForm var2 = (ItemForm)var1.next();
         var2.updateButtonsForward();
         var2.updateButtonsBack();
      }

   }

   public void updateButton(int var1) {
      ItemForm var2 = (ItemForm)this.itemForms.get(var1);
      if (var2 != null) {
         var2.updateButtonsBack();
      }

   }

   public void updateButtons(ItemCategory var1) {
      CategoryForm var2 = (CategoryForm)this.categoryForms.get(var1.id);
      if (var2 != null) {
         var2.updateButtonsForward();
         var2.updateButtonsBack();
      }

   }

   public void setSearch(String var1) {
      this.contentForm.updateSearch(var1);
      this.contentForm.updateYChildren();
      this.contentForm.updateDimensionsForward();
      if (var1 != null && !var1.isEmpty()) {
         this.contentForm.setExpandedSetting(new ItemCategoryExpandedSetting(true));
      } else {
         this.contentForm.setExpandedSetting(this.expandedSetting);
      }

      this.updateDimensions();
   }

   private boolean isCategoryEmptyRecursive(ItemCategoriesFilter.ItemCategoryFilter var1) {
      return !var1.hasAnyItems ? var1.getChildren().stream().allMatch(this::isCategoryEmptyRecursive) : false;
   }

   protected void addChildren(LinkedForm var1, ItemCategoriesFilter.ItemCategoryFilter var2, ItemCategoryExpandedSetting var3) {
      FormFlow var4 = new FormFlow();
      AtomicReference var5 = new AtomicReference();
      var2.getChildren().stream().sorted().forEach((var5x) -> {
         ItemCategoryExpandedSetting var6 = var3 == null ? null : var3.getChild(var5x.category);
         CategoryForm var7 = (CategoryForm)var1.addComponent(new CategoryForm(var1, var5x, var6, (LinkedForm)var5.get()));
         var1.children.add(var7);
         var5.set(var7);
         var7.setPosition(0, var4.next(var7.isHidden() ? 0 : var7.getHeight()));
         var1.setHeight(var4.next());
      });
      LinkedList var6 = new LinkedList();
      LinkedList var7 = new LinkedList();
      var2.streamItems().sorted(Comparator.comparing((var0) -> {
         return ItemRegistry.getDisplayName(var0.getID());
      })).forEach((var3x) -> {
         if (!GlobalData.debugCheatActive() && (this.itemsObtainedStats == null || !this.itemsObtainedStats.isItemObtained(var3x.getStringID())) && !GlobalData.stats().items_obtained.isItemObtained(var3x.getStringID())) {
            var7.add(var3x);
         } else {
            var6.add(var3x);
         }

      });
      Iterator var8 = var6.iterator();

      while(var8.hasNext()) {
         Item var9 = (Item)var8.next();
         ItemForm var10 = (ItemForm)var1.addComponent(new ItemForm(var9.getStringID(), var1, (LinkedForm)var5.get(), var2, new Item[]{var9}));
         var1.children.add(var10);
         var10.setPosition(0, var4.next(var10.isHidden() ? 0 : var10.getHeight()));
         var1.setHeight(var4.next());
         var5.set(var10);
         var1.markHasItems();
      }

      if (!var7.isEmpty()) {
         ItemForm var11 = (ItemForm)var1.addComponent(new ItemForm(GameUtils.join((Item[])var7.toArray(new Item[0]), (Function)(Item::getStringID), "."), var1, (LinkedForm)var5.get(), var2, var7));
         var1.children.add(var11);
         var11.setPosition(0, var4.next(var11.isHidden() ? 0 : var11.getHeight()));
         var1.setHeight(var4.next());
         var1.markHasItems();
      }

   }

   protected static class LinkedForm extends Form {
      public boolean hasItems;
      public LinkedForm parent;
      public LinkedForm lastForm;
      public LinkedForm nextForm;
      public LinkedList<LinkedForm> children = new LinkedList();

      public LinkedForm(String var1, int var2, int var3, LinkedForm var4, LinkedForm var5) {
         super(var1, var2, var3);
         this.parent = var4;
         this.lastForm = var5;
         if (var5 != null) {
            var5.nextForm = this;
         }

      }

      public void markHasItems() {
         this.hasItems = true;
         if (this.parent != null && !this.parent.hasItems) {
            this.parent.markHasItems();
         }

      }

      public void updateY() {
         if (this.lastForm != null) {
            if (this.lastForm.isHidden()) {
               this.setY(this.lastForm.getY());
            } else {
               this.setY(this.lastForm.getY() + this.lastForm.getHeight());
            }
         }

         if (this.nextForm != null) {
            this.nextForm.updateY();
         }

      }

      public void updateYChildren() {
         if (this.lastForm != null) {
            if (this.lastForm.isHidden()) {
               this.setY(this.lastForm.getY());
            } else {
               this.setY(this.lastForm.getY() + this.lastForm.getHeight());
            }
         }

         Iterator var1 = this.children.iterator();

         while(var1.hasNext()) {
            LinkedForm var2 = (LinkedForm)var1.next();
            var2.updateYChildren();
         }

      }

      public final void updateDimensionsBack() {
         this.fixDimensions();
         this.updateY();
         if (this.parent != null) {
            this.parent.updateDimensionsBack();
         }

      }

      public final void updateDimensionsForward() {
         this.children.forEach(LinkedForm::updateDimensionsForward);
         this.fixDimensions();
         this.updateY();
      }

      public void fixDimensions() {
         this.setWidth((Integer)this.children.stream().filter((var0) -> {
            return !var0.isHidden();
         }).map((var0) -> {
            return var0.getX() + var0.getWidth();
         }).max(Comparator.comparingInt((var0) -> {
            return var0;
         })).orElse(0));
         this.setHeight((Integer)this.children.stream().filter((var0) -> {
            return !var0.isHidden();
         }).map((var0) -> {
            return var0.getY() + var0.getHeight();
         }).max(Comparator.comparingInt((var0) -> {
            return var0;
         })).orElse(0));
      }

      public void setExpandedSetting(ItemCategoryExpandedSetting var1) {
         Iterator var2 = this.children.iterator();

         while(var2.hasNext()) {
            LinkedForm var3 = (LinkedForm)var2.next();
            var3.setExpandedSetting(var1);
         }

      }

      public void updateSearch(String var1) {
         this.children.forEach((var1x) -> {
            var1x.updateSearch(var1);
         });
      }

      public final void updateButtonsBack() {
         this.updateButton();
         if (this.parent != null) {
            this.parent.updateButtonsBack();
         }

      }

      public final void updateButtonsForward() {
         this.updateButton();
         Iterator var1 = this.children.iterator();

         while(var1.hasNext()) {
            LinkedForm var2 = (LinkedForm)var1.next();
            var2.updateButtonsForward();
         }

      }

      public void updateButton() {
      }
   }

   protected class ItemForm extends LinkedForm {
      public final Item[] items;
      public Item[] searchedItems;
      private final FormContentIconValueButton<CheckedState> toggleButton;
      private CheckedState state;
      private ItemCategoriesFilter.ItemLimits lastLimits;
      private final FormLocalLabel label;
      private final FormMouseHover labelHover;

      public ItemForm(String var2, LinkedForm var3, LinkedForm var4, ItemCategoriesFilter.ItemCategoryFilter var5, final Item... var6) {
         super(var2, 20, 20, var3, var4);
         this.drawBase = false;
         Item[] var7 = var6;
         int var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Item var10 = var7[var9];
            ItemCategoriesFilterForm.this.itemForms.put(var10.getID(), this);
         }

         this.items = var6;
         this.searchedItems = var6;
         this.toggleButton = (FormContentIconValueButton)this.addComponent(new FormContentIconValueButton(0, 0, FormInputSize.SIZE_20, ButtonColor.BASE));
         ItemCategoriesFilterForm.CheckedState.CHECKED.updateButton(this.toggleButton);
         this.toggleButton.onClicked((var1x) -> {
            Item[] var2;
            int var3;
            int var4;
            Item var5;
            if (this.state != ItemCategoriesFilterForm.CheckedState.CHECKED && this.state != ItemCategoriesFilterForm.CheckedState.DASH) {
               var2 = this.searchedItems;
               var3 = var2.length;

               for(var4 = 0; var4 < var3; ++var4) {
                  var5 = var2[var4];
                  ItemCategoriesFilterForm.this.filter.setItemAllowed(var5, true);
               }

               ItemCategoriesFilterForm.this.onItemsChanged(this.searchedItems, true);
            } else {
               var2 = this.searchedItems;
               var3 = var2.length;

               for(var4 = 0; var4 < var3; ++var4) {
                  var5 = var2[var4];
                  ItemCategoriesFilterForm.this.filter.setItemAllowed(var5, false);
               }

               ItemCategoriesFilterForm.this.onItemsChanged(this.searchedItems, false);
            }

            this.updateButtonsBack();
         });
         this.toggleButton.setupDragToOtherButtons("pressItemCategory" + var5.category.stringID, true, (var1x) -> {
            if (var1x != ItemCategoriesFilterForm.CheckedState.CHECKED && var1x != ItemCategoriesFilterForm.CheckedState.ESCAPED) {
               return false;
            } else {
               Item[] var2 = this.searchedItems;
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  Item var5 = var2[var4];
                  ItemCategoriesFilterForm.this.filter.setItemAllowed(var5, var1x == ItemCategoriesFilterForm.CheckedState.CHECKED);
               }

               ItemCategoriesFilterForm.this.onItemsChanged(this.searchedItems, var1x == ItemCategoriesFilterForm.CheckedState.CHECKED);
               this.updateButtonsBack();
               return true;
            }
         });
         if (var6.length == 1) {
            this.addComponent(new FormItemPreview(20, 0, 20, var6[0]));
            this.label = (FormLocalLabel)this.addComponent(new FormLocalLabel(this.getLabelText(), new FontOptions(16), -1, 42, 2));
         } else {
            this.label = (FormLocalLabel)this.addComponent(new FormLocalLabel(this.getLabelText(), new FontOptions(16), -1, 22, 2));
         }

         Rectangle var11 = this.label.getBoundingBox();
         if (ItemCategoriesFilterForm.this.allowMaxAmountSetting) {
            this.labelHover = (FormMouseHover)this.addComponent(new FormMouseHover(42, 2, var11.width, var11.height) {
               public GameTooltips getTooltips(PlayerMob var1) {
                  if (var6.length == 1) {
                     ListGameTooltips var2 = new ListGameTooltips();
                     ItemCategoriesFilter.ItemLimits var3 = ItemCategoriesFilterForm.this.filter.getItemLimits(var6[0]);
                     var2.add((Object)(new InputTooltip(-100, Localization.translate("ui", "setstoragelimit"))));
                     if (var3 != null && !var3.isDefault()) {
                        var2.add((Object)(new InputTooltip(-99, Localization.translate("ui", "clearstoragelimit"))));
                     }

                     return var2;
                  } else {
                     return null;
                  }
               }

               public Screen.CURSOR getHoveringCursor(PlayerMob var1) {
                  return var6.length == 1 ? Screen.CURSOR.INTERACT : null;
               }
            }, 1000);
            this.labelHover.acceptRightClicks = true;
            this.labelHover.onClicked((var3x) -> {
               if (var6.length == 1) {
                  ItemCategoriesFilter.ItemLimits var4 = ItemCategoriesFilterForm.this.filter.getItemLimits(var6[0]);
                  if (var3x.event.getID() == -99) {
                     if (var4 != null && !var4.isDefault()) {
                        ItemCategoriesFilter.ItemLimits var8 = new ItemCategoriesFilter.ItemLimits();
                        ItemCategoriesFilterForm.this.filter.setItemAllowed(var6[0], var8);
                        ItemCategoriesFilterForm.this.onItemLimitsChanged(var6[0], var8);
                     }

                  } else {
                     Form var5 = new Form(200, 24);
                     FormTextInput var6x = (FormTextInput)var5.addComponent(new FormTextInput(0, 0, FormInputSize.SIZE_24, var5.getWidth(), 7));
                     var6x.placeHolder = new LocalMessage("ui", "storagelimit");
                     var6x.setRegexMatchFull("([0-9]+)?");
                     var6x.rightClickToClear = true;
                     var6x.setText(var4 != null && !var4.isDefault() ? "" + var4.getMaxItems() : "");
                     FormFloatMenu var7 = new FormFloatMenu(var3, var5);
                     this.getManager().openFloatMenu(var7, -10, -12);
                     var6x.onSubmit((var5x) -> {
                        try {
                           ItemCategoriesFilter.ItemLimits var6xx;
                           if (var6x.getText().isEmpty()) {
                              var6xx = var4 == null ? null : new ItemCategoriesFilter.ItemLimits();
                           } else {
                              var6xx = new ItemCategoriesFilter.ItemLimits(Integer.parseInt(var6x.getText()));
                           }

                           if (var4 != var6xx && (var4 == null || !var4.isSame(var6xx))) {
                              ItemCategoriesFilterForm.this.filter.setItemAllowed(var6[0], var6xx);
                              ItemCategoriesFilterForm.this.onItemLimitsChanged(var6[0], var6xx);
                           }

                           var7.remove();
                        } catch (NumberFormatException var7x) {
                        }

                     });
                     var6x.setTyping(true);
                  }
               }
            });
         } else {
            this.labelHover = null;
         }

         this.fixDimensions();
         this.updateButton();
      }

      public ItemForm(String var2, LinkedForm var3, LinkedForm var4, ItemCategoriesFilter.ItemCategoryFilter var5, List<Item> var6) {
         this(var2, var3, var4, var5, (Item[])((Item[])var6.toArray(new Item[0])));
      }

      public void fixDimensions() {
         Rectangle var1 = this.label.getBoundingBox();
         this.setWidth(var1.x + var1.width + 4);
      }

      public void updateSearch(String var1) {
         super.updateSearch(var1);
         this.searchedItems = (Item[])Arrays.stream(this.items).filter((var1x) -> {
            return var1x.matchesSearch(var1x.getDefaultItem((PlayerMob)null, 0), (PlayerMob)null, var1);
         }).toArray((var0) -> {
            return new Item[var0];
         });
         if (this.items.length != 1) {
            this.label.setLocalization(new LocalMessage("itemcategory", "unknownitems", new Object[]{"count", this.searchedItems.length}));
            this.fixDimensions();
         }

         this.setHidden(this.searchedItems.length == 0);
         this.updateButton();
      }

      public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
         CheckedState var4 = this.getNextState();
         if (var4 != this.state) {
            this.updateButtonsBack();
         }

         ItemCategoriesFilter.ItemLimits var5 = this.items.length == 1 ? ItemCategoriesFilterForm.this.filter.getItemLimits(this.items[0]) : null;
         if (var5 != this.lastLimits || var5 != null && var5.isSame(this.lastLimits)) {
            this.updateLabel();
         }

         super.draw(var1, var2, var3);
      }

      public void updateButton() {
         this.state = this.getNextState();
         this.state.updateButton(this.toggleButton);
      }

      public void updateLabel() {
         this.lastLimits = this.items.length == 1 ? ItemCategoriesFilterForm.this.filter.getItemLimits(this.items[0]) : null;
         this.label.setText(this.getLabelText());
         if (this.labelHover != null) {
            this.labelHover.width = this.label.getBoundingBox().width;
         }

         this.updateDimensionsBack();
         this.updateDimensionsForward();
         ItemCategoriesFilterForm.this.updateDimensions();
         Screen.submitNextMoveEvent();
      }

      public GameMessage getLabelText() {
         if (this.items.length == 1) {
            GameMessage var1 = ItemRegistry.getLocalization(this.items[0].getID());
            return (GameMessage)(this.lastLimits != null && !this.lastLimits.isDefault() ? new LocalMessage("ui", "storagelimitprefix", new Object[]{"count", this.lastLimits.getMaxItems(), "item", var1}) : var1);
         } else {
            return new LocalMessage("itemcategory", "unknownitems", new Object[]{"count", this.items.length});
         }
      }

      protected CheckedState getNextState() {
         int var1 = 0;
         int var2 = 0;
         Item[] var3 = this.searchedItems;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Item var6 = var3[var5];
            ItemCategoriesFilter.ItemLimits var7 = ItemCategoriesFilterForm.this.filter.getItemLimits(var6);
            if (var7 != null) {
               ++var1;
               if (var7.isDefault()) {
                  ++var2;
               }
            }
         }

         if (var1 == 0) {
            return ItemCategoriesFilterForm.CheckedState.ESCAPED;
         } else if (var1 == this.searchedItems.length && var2 == this.searchedItems.length) {
            return ItemCategoriesFilterForm.CheckedState.CHECKED;
         } else {
            return ItemCategoriesFilterForm.CheckedState.DASH;
         }
      }
   }

   protected class CategoryForm extends LinkedForm {
      public final ItemCategoriesFilter.ItemCategoryFilter itemCategoryFilter;
      private final LinkedForm childrenForm;
      private final FormContentIconValueButton<CheckedState> toggleButton;
      private CheckedState lastState;
      private int lastMaxItems;
      private final FormContentIconButton expandButton;
      private final FormLocalLabel label;
      private final FormMouseHover labelHover;
      private ItemCategoryExpandedSetting expandedSetting;
      private boolean isExpanded = false;

      public CategoryForm(LinkedForm var2, final ItemCategoriesFilter.ItemCategoryFilter var3, ItemCategoryExpandedSetting var4, LinkedForm var5) {
         super(var3.category.stringID, 20, 20, var2, var5);
         this.itemCategoryFilter = var3;
         this.expandedSetting = var4;
         ItemCategoriesFilterForm.this.categoryForms.put(var3.category.id, this);
         this.drawBase = false;
         this.childrenForm = (LinkedForm)this.addComponent(new LinkedForm(this.name + " children", 20, 0, this, (LinkedForm)null));
         this.children.add(this.childrenForm);
         this.childrenForm.drawBase = false;
         this.childrenForm.setPosition(20, 20);
         this.childrenForm.setHidden(true);
         this.toggleButton = (FormContentIconValueButton)this.addComponent(new FormContentIconValueButton(0, 0, FormInputSize.SIZE_20, ButtonColor.BASE));
         ItemCategoriesFilterForm.CheckedState.CHECKED.updateButton(this.toggleButton);
         this.toggleButton.onClicked((var2x) -> {
            if (this.lastState != ItemCategoriesFilterForm.CheckedState.CHECKED && this.lastState != ItemCategoriesFilterForm.CheckedState.DASH) {
               var3.setAllowed(true);
               ItemCategoriesFilterForm.this.onCategoryChanged(var3, true);
            } else {
               var3.setAllowed(false);
               ItemCategoriesFilterForm.this.onCategoryChanged(var3, false);
            }

            this.updateButtonsForward();
            this.updateButtonsBack();
         });
         this.toggleButton.setupDragToOtherButtons("pressItemCategory" + (var3.parent == null ? "Null" : var3.parent.category.stringID), true, (var2x) -> {
            if (var2x != ItemCategoriesFilterForm.CheckedState.CHECKED && var2x != ItemCategoriesFilterForm.CheckedState.ESCAPED) {
               return false;
            } else {
               var3.setAllowed(var2x == ItemCategoriesFilterForm.CheckedState.CHECKED);
               ItemCategoriesFilterForm.this.onCategoryChanged(var3, var2x == ItemCategoriesFilterForm.CheckedState.CHECKED);
               this.updateButtonsForward();
               this.updateButtonsBack();
               return true;
            }
         });
         this.updateButton();
         this.expandButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(20, 0, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_collapsed_16, new GameMessage[0]));
         this.expandButton.onClicked((var1x) -> {
            this.setExpanded(!this.isExpanded);
         });
         this.label = (FormLocalLabel)this.addComponent(new FormLocalLabel(this.getLabelText(), new FontOptions(16), -1, 42, 2));
         Rectangle var6 = this.label.getBoundingBox();
         if (ItemCategoriesFilterForm.this.allowMaxAmountSetting) {
            this.labelHover = (FormMouseHover)this.addComponent(new FormMouseHover(42, 2, var6.width, var6.height) {
               public GameTooltips getTooltips(PlayerMob var1) {
                  ListGameTooltips var2 = new ListGameTooltips();
                  var2.add((Object)(new InputTooltip(-100, Localization.translate("ui", "setstoragelimit"))));
                  if (!var3.isDefault()) {
                     var2.add((Object)(new InputTooltip(-99, Localization.translate("ui", "clearstoragelimit"))));
                  }

                  return var2;
               }

               public Screen.CURSOR getHoveringCursor(PlayerMob var1) {
                  return Screen.CURSOR.INTERACT;
               }
            }, 1000);
            this.labelHover.acceptRightClicks = true;
            this.labelHover.onClicked((var3x) -> {
               if (var3x.event.getID() == -99) {
                  if (!var3.isDefault()) {
                     var3.clearMaxItems();
                     ItemCategoriesFilterForm.this.onCategoryLimitsChanged(var3, var3.getMaxItems());
                  }

               } else {
                  Form var4 = new Form(200, 24);
                  FormTextInput var5 = (FormTextInput)var4.addComponent(new FormTextInput(0, 0, FormInputSize.SIZE_24, var4.getWidth(), 7));
                  var5.placeHolder = new LocalMessage("ui", "storagelimit");
                  var5.setRegexMatchFull("([0-9]+)?");
                  var5.rightClickToClear = true;
                  var5.setText(var3.isDefault() ? "" : "" + var3.getMaxItems());
                  FormFloatMenu var6 = new FormFloatMenu(var2, var4);
                  this.getManager().openFloatMenu(var6, -10, -12);
                  var5.onSubmit((var4x) -> {
                     try {
                        int var5x;
                        if (var5.getText().isEmpty()) {
                           var5x = Integer.MAX_VALUE;
                        } else {
                           var5x = Integer.parseInt(var5.getText());
                        }

                        if (var5x != var3.getMaxItems()) {
                           var3.setMaxItems(var5x);
                           ItemCategoriesFilterForm.this.onCategoryLimitsChanged(var3, var5x);
                        }

                        var6.remove();
                     } catch (NumberFormatException var6x) {
                     }

                  });
                  var5.setTyping(true);
               }
            });
         } else {
            this.labelHover = null;
         }

         this.setWidth(40 + var6.width + 10);
         ItemCategoriesFilterForm.this.addChildren(this.childrenForm, var3, this.expandedSetting);
         if (this.expandedSetting != null && this.expandedSetting.isExpanded()) {
            this.setExpanded(true);
         }

         this.setHidden(!this.hasItems);
      }

      public void setExpandedSetting(ItemCategoryExpandedSetting var1) {
         this.expandedSetting = var1 == null ? null : var1.getChild(this.itemCategoryFilter.category);
         if (this.expandedSetting != null) {
            this.setExpanded(this.expandedSetting.isExpanded());
            Iterator var2 = this.children.iterator();

            while(var2.hasNext()) {
               LinkedForm var3 = (LinkedForm)var2.next();
               var3.setExpandedSetting(this.expandedSetting);
            }
         }

      }

      public void setExpanded(boolean var1) {
         if (this.isExpanded != var1) {
            this.isExpanded = var1;
            if (this.expandedSetting != null) {
               this.expandedSetting.setExpanded(var1);
            }

            if (var1) {
               this.expandButton.setIcon(Settings.UI.button_expanded_16);
               this.childrenForm.setHidden(false);
               this.childrenForm.updateDimensionsBack();
            } else {
               this.expandButton.setIcon(Settings.UI.button_collapsed_16);
               this.childrenForm.setHidden(true);
               this.childrenForm.updateDimensionsBack();
            }

            this.updateY();
            if (this.parent != null) {
               this.parent.updateDimensionsBack();
            }

            ItemCategoriesFilterForm.this.updateDimensions();
         }

      }

      public void fixDimensions() {
         int var1 = (Integer)this.children.stream().filter((var0) -> {
            return !var0.isHidden();
         }).map((var0) -> {
            return var0.getX() + var0.getWidth();
         }).max(Comparator.comparingInt((var0) -> {
            return var0;
         })).orElse(0);
         this.setWidth(Math.max(var1, 40 + this.label.getBoundingBox().width + 10));
         this.setHeight((Integer)this.children.stream().filter((var0) -> {
            return !var0.isHidden();
         }).map((var0) -> {
            return var0.getY() + var0.getHeight();
         }).max(Comparator.comparingInt((var0) -> {
            return var0;
         })).orElse(20));
      }

      public void updateSearch(String var1) {
         super.updateSearch(var1);
         this.setHidden(!this.hasItems || this.childrenForm.children.stream().allMatch(Form::isHidden));
      }

      public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
         CheckedState var4 = this.getNextState();
         if (this.lastState != var4) {
            if (this.parent != null) {
               this.parent.updateButtonsBack();
            }

            this.updateButtonsForward();
         }

         int var5 = this.itemCategoryFilter.getMaxItems();
         if (var5 != this.lastMaxItems) {
            this.updateLabel();
         }

         super.draw(var1, var2, var3);
      }

      public void updateButton() {
         this.lastState = this.getNextState();
         this.lastState.updateButton(this.toggleButton);
      }

      public void updateLabel() {
         this.lastMaxItems = this.itemCategoryFilter.getMaxItems();
         this.label.setText(this.getLabelText());
         if (this.labelHover != null) {
            this.labelHover.width = this.label.getBoundingBox().width;
         }

         this.updateDimensionsBack();
         this.updateDimensionsForward();
         ItemCategoriesFilterForm.this.updateDimensions();
         Screen.submitNextMoveEvent();
      }

      public GameMessage getLabelText() {
         return (GameMessage)(this.itemCategoryFilter.isDefault() ? this.itemCategoryFilter.category.displayName : new LocalMessage("ui", "storagelimitprefix", new Object[]{"count", this.itemCategoryFilter.getMaxItems(), "item", this.itemCategoryFilter.category.displayName}));
      }

      protected CheckedState getNextState() {
         if (this.itemCategoryFilter.isAllAllowed()) {
            return this.itemCategoryFilter.isDefault() && this.itemCategoryFilter.isAllDefault() ? ItemCategoriesFilterForm.CheckedState.CHECKED : ItemCategoriesFilterForm.CheckedState.DASH;
         } else {
            return this.itemCategoryFilter.isAnyAllowed() ? ItemCategoriesFilterForm.CheckedState.DASH : ItemCategoriesFilterForm.CheckedState.ESCAPED;
         }
      }
   }

   private static enum CheckedState {
      CHECKED(() -> {
         return Settings.UI.button_checked_20;
      }),
      ESCAPED(() -> {
         return Settings.UI.button_escaped_20;
      }),
      DASH(() -> {
         return Settings.UI.button_dash_20;
      });

      public final Supplier<ButtonIcon> iconSupplier;

      private CheckedState(Supplier var3) {
         this.iconSupplier = var3;
      }

      public void updateButton(FormContentIconValueButton<CheckedState> var1) {
         var1.setCurrent(this, (ButtonIcon)this.iconSupplier.get());
      }

      // $FF: synthetic method
      private static CheckedState[] $values() {
         return new CheckedState[]{CHECKED, ESCAPED, DASH};
      }
   }
}
