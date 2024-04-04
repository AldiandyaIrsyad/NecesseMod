package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import necesse.engine.control.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormQuestComponent;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.lists.FormIngredientRecipeList;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.events.ElderQuestUpdateEvent;
import necesse.inventory.container.mob.ElderContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.inventory.recipe.Recipes;

public class ElderContainerForm extends MobContainerFormSwitcher<ElderContainer> {
   public static int TOTAL_TIPS = 15;
   public DialogueForm dialogueForm;
   public DialogueForm noQuestForm;
   public DialogueForm questDialogueForm;
   public DialogueForm tierQuestDialogueForm;
   public DialogueForm tipsForm;
   public DialogueForm helpForm;
   public ArrayList<HelpDialogueOption> helpForms;
   private FormDialogueOption acceptQuest;
   private FormDialogueOption skipQuest;
   private FormDialogueOption acceptTierQuest;
   private int tierQuestUniqueID;
   public int currentTip;
   public FormContainerSlot ingredientSlot;
   public FormIngredientRecipeList ingredientList;
   private int itemID;
   public EquipmentForm equipmentForm;
   private int width = 480;
   private int height = 160;

   public ElderContainerForm(Client var1, ElderContainer var2) {
      super(var1, var2);
      var2.containerForm = this;
      GameMessage var3 = MobRegistry.getLocalization(var2.elderMob.getID());
      this.dialogueForm = (DialogueForm)this.addComponent(new DialogueForm("dialogue", this.width, this.height, var3, var2.introMessage));
      this.dialogueForm.addDialogueOption(new LocalMessage("ui", "elderwantquest"), () -> {
         if (var2.quest == null) {
            this.makeCurrent(this.noQuestForm);
         } else if (this.canCompleteQuest(var2.quest)) {
            this.makeCurrent(this.questDialogueForm);
         } else if (var2.tierQuest != null && this.canCompleteQuest(var2.tierQuest)) {
            this.makeCurrent(this.tierQuestDialogueForm);
         } else if (var2.tierQuest != null && !this.hasQuest(var2.tierQuest)) {
            this.makeCurrent(this.tierQuestDialogueForm);
         } else {
            this.makeCurrent(this.questDialogueForm);
         }

      });
      this.dialogueForm.addDialogueOption(new LocalMessage("ui", "eldertips"), () -> {
         this.currentTip = GameRandom.globalRandom.nextInt(TOTAL_TIPS);
         this.updateTip();
         this.makeCurrent(this.tipsForm);
      });
      this.dialogueForm.addDialogueOption(new LocalMessage("ui", "elderhelpbutton"), () -> {
         this.makeCurrent(this.helpForm);
      });
      if (var2.canEditEquipment) {
         String var10007 = var3.translate();
         int var10008 = var2.EQUIPMENT_COSM_HEAD_SLOT;
         int var10009 = var2.EQUIPMENT_COSM_CHEST_SLOT;
         int var10010 = var2.EQUIPMENT_COSM_FEET_SLOT;
         int var10011 = var2.EQUIPMENT_HEAD_SLOT;
         int var10012 = var2.EQUIPMENT_CHEST_SLOT;
         int var10013 = var2.EQUIPMENT_FEET_SLOT;
         int var10014 = var2.EQUIPMENT_WEAPON_SLOT;
         BooleanCustomAction var10015 = var2.setSelfManageEquipment;
         Objects.requireNonNull(var10015);
         this.equipmentForm = (EquipmentForm)this.addComponent(new EquipmentForm(var1, var2, var10007, var10008, var10009, var10010, var10011, var10012, var10013, var10014, var10015::runAndSend, (var2x) -> {
            var2.setIsInEquipment.runAndSend(false);
            this.makeCurrent(this.dialogueForm);
         }) {
            public HumanMob getMob() {
               return ((ElderContainer)ElderContainerForm.this.container).elderMob;
            }

            public EquipmentBuffManager getEquipmentManager() {
               return ((ElderContainer)ElderContainerForm.this.container).elderMob.equipmentBuffManager;
            }
         });
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerchangeequipment"), () -> {
            var2.setIsInEquipment.runAndSend(true);
            this.makeCurrent(this.equipmentForm);
         });
         this.equipmentForm.filterEquipmentButtonPressed = () -> {
            AtomicBoolean var3 = new AtomicBoolean(true);
            AtomicReference var4 = new AtomicReference();
            ItemCategoriesFilter var5 = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
            AtomicBoolean var6 = new AtomicBoolean();
            Consumer var10002 = (var7) -> {
               if (var4.get() == null) {
                  if (var7.change != null) {
                     var7.change.applyTo(var5);
                  }

                  var6.set(var7.preferArmorSets);
                  Mob var10006 = this.mob;
                  Objects.requireNonNull(var6);
                  EquipmentFiltersForm var8 = new EquipmentFiltersForm("equipmentFilter", 408, 300, var10006, var5, var6::get, var1) {
                     public void onSetPreferArmorSets(boolean var1) {
                        var3.set(var1);
                        var5.setEquipmentFilter.runAndSendPreferArmorSets(var1);
                     }

                     public void onItemsChanged(Item[] var1, boolean var2) {
                        var5.setEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(var1, var2));
                     }

                     public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2) {
                        var5.setEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(var1, var2));
                     }

                     public void onFullChange(ItemCategoriesFilter var1) {
                        var5.setEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.fullChange(var1));
                     }

                     public void onButtonPressed() {
                        ElderContainerForm.this.makeCurrent(ElderContainerForm.this.equipmentForm);
                     }

                     public void onWindowResized() {
                        super.onWindowResized();
                        ContainerComponent.setPosFocus(this);
                     }
                  };
                  this.addAndMakeCurrentTemporary(var8, () -> {
                     var3.set(false);
                     var4.set((Object)null);
                     var2.subscribeEquipmentFilter.runAndSend(false);
                  });
                  var8.onWindowResized();
                  var4.set(var8);
               } else {
                  var6.set(var7.preferArmorSets);
                  if (var7.change != null) {
                     var7.change.applyTo(var5);
                  }
               }

            };
            Objects.requireNonNull(var3);
            var2.onEvent(SettlementSettlerEquipmentFilterChangedEvent.class, var10002, var3::get);
            var2.subscribeEquipmentFilter.runAndSend(true);
         };
      }

      this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlergoodbye"), () -> {
         var1.closeContainer(true);
      });
      this.dialogueForm.setHeight(Math.max(this.dialogueForm.getContentHeight() + 5, this.height));
      boolean var4 = var2.elderMob.getLevel().settlementLayer.isActive();
      boolean var5 = var2.elderMob.getLevel().settlementLayer.doIHaveAccess(var1);
      this.noQuestForm = (DialogueForm)this.addComponent(new DialogueForm("eldernoquest", this.width, this.height, var3, new LocalMessage("ui", var4 ? (var5 ? "eldernoquest" : "eldernoaccess") : "eldernoactive")));
      this.noQuestForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.noQuestForm.addDialogueOption(new LocalMessage("ui", "settlergoodbye"), () -> {
         var1.closeContainer(true);
      });
      this.questDialogueForm = (DialogueForm)this.addComponent(new DialogueForm("elderquest", this.width, this.height, var3, (GameMessage)null));
      this.tierQuestDialogueForm = (DialogueForm)this.addComponent(new DialogueForm("elderquest", this.width, this.height, var3, (GameMessage)null));
      this.updateQuestDialogues();
      this.tipsForm = (DialogueForm)this.addComponent(new DialogueForm("eldertips", this.width, this.height, (GameMessage)null, (GameMessage)null));
      this.helpForms = new ArrayList();
      this.helpForm = (DialogueForm)this.addComponent(new DialogueForm("elderhelp", this.width, 220, var3, new LocalMessage("ui", "elderpicktopic")));
      this.helpForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      DialogueForm var6 = new DialogueForm("crafting", this.width, this.height, new LocalMessage("ui", "eldercrafting"), (GameMessage)null);
      var6.content.addComponent((FormLabel)var6.flow.nextY(new FormLabel(Localization.translate("ui", "eldercraftingtip"), new FontOptions(16), 0, var6.getWidth() / 2, 0, var6.getWidth() - 20), 10));
      int var7 = var6.flow.next(100);
      var6.content.addComponent(this.ingredientSlot = new FormContainerMaterialSlot(var1, var2, var2.INGREDIENT_SLOT, var6.getWidth() - 110, var7 + 15));
      var6.content.addComponent(this.ingredientList = new FormIngredientRecipeList(0, var7, var6.getWidth() - 120, 100, var1));
      var6.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         var2.setInCraftingForm.runAndSend(false);
         this.makeCurrent(this.helpForm);
      });
      this.addHelpForm(new LocalMessage("ui", "eldercrafting"), var6, () -> {
         var2.setInCraftingForm.runAndSend(true);
      });
      var6.setHeight(Math.max(this.height, var6.getContentHeight() + 5));
      this.addHelpForm(new LocalMessage("ui", "elderexploring"), this.simpleDialogueForm("exploring", this.width, this.height, new LocalMessage("ui", "elderexploring"), new LocalMessage("ui", "elderexploringtip")));
      this.addHelpForm(new LocalMessage("ui", "eldertrading"), this.simpleDialogueForm("trading", this.width, this.height, new LocalMessage("ui", "eldertrading"), new LocalMessage("ui", "eldertradingtip")));
      this.addHelpForm(new LocalMessage("ui", "elderobjects"), this.simpleDialogueForm("objects", this.width, this.height, new LocalMessage("ui", "elderobjects"), new LocalMessage("ui", "elderobjectstip")));
      this.addHelpForm(new LocalMessage("ui", "eldertiles"), this.simpleDialogueForm("tiles", this.width, this.height, new LocalMessage("ui", "eldertiles"), new LocalMessage("ui", "eldertilestip")));
      this.addHelpForm(new LocalMessage("ui", "eldertools"), this.simpleDialogueForm("tools", this.width, this.height, new LocalMessage("ui", "eldertools"), new LocalMessage("ui", "eldertoolstip")));
      this.addHelpForm(new LocalMessage("ui", "elderweapons"), this.simpleDialogueForm("weapons", this.width, this.height, new LocalMessage("ui", "elderweapons"), new LocalMessage("ui", "elderweaponstip")));
      this.addHelpForm(new LocalMessage("ui", "elderarmor"), this.simpleDialogueForm("armor", this.width, this.height, new LocalMessage("ui", "elderarmor"), new LocalMessage("ui", "elderarmortip")));
      this.addHelpForm(new LocalMessage("ui", "eldertrinkets"), this.simpleDialogueForm("trinkets", this.width, this.height, new LocalMessage("ui", "eldertrinkets"), new LocalMessage("ui", "eldertrinketstip")));
      this.addHelpForm(new LocalMessage("ui", "elderenchants"), this.simpleDialogueForm("enchants", this.width, this.height, new LocalMessage("ui", "elderenchants"), new LocalMessage("ui", "elderenchantstip")));
      this.addHelpForm(new LocalMessage("ui", "elderpotions"), this.simpleDialogueForm("potions", this.width, this.height, new LocalMessage("ui", "elderpotions"), new LocalMessage("ui", "elderpotionstip")));
      this.addHelpForm(new LocalMessage("ui", "eldersettlements"), this.simpleDialogueForm("settlements", this.width, this.height, new LocalMessage("ui", "eldersettlements"), new LocalMessage("ui", "eldersettlementstip")));
      this.helpForms.sort(Comparator.comparing((var0) -> {
         return var0.text.translate();
      }));
      this.helpForms.forEach(HelpDialogueOption::addDialogueOption);
      var2.onEvent(ElderQuestUpdateEvent.class, (var1x) -> {
         this.updateQuestDialogues();
      });
      this.makeCurrent(this.dialogueForm);
   }

   private DialogueForm simpleDialogueForm(String var1, int var2, int var3, GameMessage var4, GameMessage var5) {
      DialogueForm var6 = new DialogueForm(var1, var2, var3, var4, (GameMessage)null);
      var6.content.addComponent((FormLabel)var6.flow.nextY(new FormLabel(var5.translate(), new FontOptions(16), 0, var6.getWidth() / 2, 0, var6.getWidth() - 20), 10));
      var6.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.helpForm);
      });
      var6.setHeight(Math.max(var3, var6.getContentHeight() + 5));
      return var6;
   }

   public void updateQuestDialogues() {
      GameMessage var1 = MobRegistry.getLocalization(((ElderContainer)this.container).elderMob.getID());
      boolean var2 = this.isCurrent(this.questDialogueForm) || this.isCurrent(this.noQuestForm);
      this.acceptQuest = null;
      this.questDialogueForm.reset(var1, (GameMessage)(new LocalMessage("ui", "elderquestintro")));
      if (((ElderContainer)this.container).quest != null) {
         this.questDialogueForm.flow.next(-5);
         FormQuestComponent var3 = new FormQuestComponent(0, 0, this.questDialogueForm.content.getWidth(), ((ElderContainer)this.container).quest);
         var3.showTitle = false;
         this.questDialogueForm.content.addComponent((FormQuestComponent)this.questDialogueForm.flow.nextY(var3, 10));
         this.acceptQuest = this.questDialogueForm.addDialogueOption(new LocalMessage("ui", "elderacceptquest"), () -> {
            if (!this.canCompleteQuest(((ElderContainer)this.container).quest) && !this.hasQuest(((ElderContainer)this.container).quest)) {
               ((ElderContainer)this.container).questTakeButton.runAndSend();
            } else {
               ((ElderContainer)this.container).questCompleteButton.runAndSend();
            }

         });
      }

      this.skipQuest = this.questDialogueForm.addDialogueOption(new LocalMessage("ui", "elderskipquest"), () -> {
         ((ElderContainer)this.container).questSkipButton.runAndSend();
      });
      if (((ElderContainer)this.container).questSkipErr != null) {
         this.skipQuest.setActive(false);
         this.skipQuest.tooltipsSupplier = () -> {
            return new StringTooltips(((ElderContainer)this.container).questSkipErr.translate());
         };
      }

      this.questDialogueForm.addDialogueOption(new LocalMessage("ui", "elderbiggerchallenge"), () -> {
         this.makeCurrent(this.tierQuestDialogueForm);
      });
      this.questDialogueForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.questDialogueForm.setHeight(Math.max(this.questDialogueForm.getContentHeight() + 5, this.height));
      ContainerComponent.setPosFocus(this.questDialogueForm);
      if (var2) {
         if (((ElderContainer)this.container).quest == null) {
            this.makeCurrent(this.noQuestForm);
         } else {
            this.makeCurrent(this.questDialogueForm);
         }
      }

      int var5 = this.tierQuestUniqueID;
      this.tierQuestUniqueID = ((ElderContainer)this.container).tierQuest == null ? 0 : ((ElderContainer)this.container).tierQuest.getUniqueID();
      boolean var6 = var5 != this.tierQuestUniqueID && ((ElderContainer)this.container).tierQuest != null && this.isCurrent(this.questDialogueForm) || this.isCurrent(this.tierQuestDialogueForm);
      this.acceptTierQuest = null;
      this.tierQuestDialogueForm.reset(var1, (GameMessage)(new LocalMessage("ui", "elderifready")));
      if (((ElderContainer)this.container).tierQuest != null) {
         this.tierQuestDialogueForm.flow.next(-5);
         FormQuestComponent var4 = new FormQuestComponent(10, 0, this.tierQuestDialogueForm.content.getWidth() - 20, ((ElderContainer)this.container).tierQuest);
         var4.showTitle = false;
         this.tierQuestDialogueForm.content.addComponent((FormQuestComponent)this.tierQuestDialogueForm.flow.nextY(var4, 10));
         this.acceptTierQuest = this.tierQuestDialogueForm.addDialogueOption(new LocalMessage("ui", "elderacceptquest"), () -> {
            if (!this.canCompleteQuest(((ElderContainer)this.container).tierQuest) && !this.hasQuest(((ElderContainer)this.container).tierQuest)) {
               ((ElderContainer)this.container).tierQuestTakeButton.runAndSend();
            } else {
               ((ElderContainer)this.container).tierQuestCompleteButton.runAndSend();
               this.makeCurrent(this.questDialogueForm);
            }

         });
         this.tierQuestDialogueForm.addDialogueOption(new LocalMessage("ui", "eldernotready"), () -> {
            this.makeCurrent(this.questDialogueForm);
         });
      } else {
         if (((ElderContainer)this.container).tierQuestErr != null) {
            this.tierQuestDialogueForm.reset(var1, ((ElderContainer)this.container).tierQuestErr);
         } else {
            this.tierQuestDialogueForm.reset(var1, (GameMessage)(new LocalMessage("ui", "eldernotierquest")));
         }

         this.tierQuestDialogueForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
            this.makeCurrent(this.questDialogueForm);
         });
      }

      this.tierQuestDialogueForm.setHeight(Math.max(this.tierQuestDialogueForm.getContentHeight() + 5, this.height));
      ContainerComponent.setPosFocus(this.tierQuestDialogueForm);
      if (var6) {
         this.makeCurrent(this.tierQuestDialogueForm);
      }

      this.updateAcceptQuestButtons();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updateAcceptQuestButtons();
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

   public void updateAcceptQuestButtons() {
      boolean var1;
      boolean var2;
      LocalMessage var3;
      if (this.acceptQuest != null && ((ElderContainer)this.container).quest != null) {
         var1 = this.hasQuest(((ElderContainer)this.container).quest);
         var2 = this.canCompleteQuest(((ElderContainer)this.container).quest);
         if (!var1 && !var2) {
            var3 = new LocalMessage("ui", "elderacceptquest");
         } else {
            var3 = new LocalMessage("ui", "eldercompletequest");
         }

         if (!this.acceptQuest.getText().equals(var3)) {
            this.acceptQuest.setText(var3, Integer.MAX_VALUE);
            ControllerInput.submitNextRefreshFocusEvent();
         }

         this.acceptQuest.setActive(!var1 || var2);
      }

      if (this.acceptTierQuest != null && ((ElderContainer)this.container).tierQuest != null) {
         var1 = this.hasQuest(((ElderContainer)this.container).tierQuest);
         var2 = this.canCompleteQuest(((ElderContainer)this.container).tierQuest);
         if (!var1 && !var2) {
            var3 = new LocalMessage("ui", "elderacceptquest");
         } else {
            var3 = new LocalMessage("ui", "eldercompletequest");
         }

         if (!this.acceptTierQuest.getText().equals(var3)) {
            this.acceptTierQuest.setText(var3, Integer.MAX_VALUE);
            ControllerInput.submitNextRefreshFocusEvent();
         }

         this.acceptTierQuest.setActive(!var1 || var2);
      }

   }

   private boolean hasQuest(Quest var1) {
      return this.client.quests.getQuest(var1.getUniqueID()) != null;
   }

   private boolean canCompleteQuest(Quest var1) {
      return var1.canComplete(((ElderContainer)this.container).getClient());
   }

   public void updateTip() {
      int var1 = Math.floorMod(this.currentTip, TOTAL_TIPS);
      this.tipsForm.reset(MobRegistry.getLocalization(((ElderContainer)this.container).elderMob.getID()), (GameMessage)(new LocalMessage("ui", "eldertip" + (var1 + 1))));
      this.tipsForm.addDialogueOption(new LocalMessage("ui", "eldernexttip"), () -> {
         ++this.currentTip;
         this.updateTip();
      });
      this.tipsForm.addDialogueOption(new LocalMessage("ui", "elderprevtip"), () -> {
         --this.currentTip;
         this.updateTip();
      });
      this.tipsForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.dialogueForm);
      ContainerComponent.setPosFocus(this.noQuestForm);
      ContainerComponent.setPosFocus(this.questDialogueForm);
      ContainerComponent.setPosFocus(this.tierQuestDialogueForm);
      ContainerComponent.setPosFocus(this.tipsForm);
      ContainerComponent.setPosFocus(this.helpForm);
      this.helpForms.forEach((var0) -> {
         ContainerComponent.setPosFocus(var0.form);
      });
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   private void addHelpForm(GameMessage var1, Form var2) {
      this.addHelpForm(var1, var2, (Runnable)null);
   }

   private void addHelpForm(GameMessage var1, Form var2, Runnable var3) {
      this.addComponent(var2);
      this.helpForms.add(new HelpDialogueOption(var1, var3, var2));
   }

   private class HelpDialogueOption {
      public final GameMessage text;
      public final Runnable entered;
      public final Form form;

      public HelpDialogueOption(GameMessage var2, Runnable var3, Form var4) {
         this.text = var2;
         this.entered = var3;
         this.form = var4;
      }

      public void addDialogueOption() {
         ElderContainerForm.this.helpForm.addDialogueOption(this.text, () -> {
            if (this.entered != null) {
               this.entered.run();
            }

            ElderContainerForm.this.makeCurrent(this.form);
         });
      }
   }
}
