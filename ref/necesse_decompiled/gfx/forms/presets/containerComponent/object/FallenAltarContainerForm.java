package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class FallenAltarContainerForm extends ContainerFormSwitcher<FallenAltarContainer> {
   public Form placeTabletForm = (Form)this.addComponent(new Form("placetablet", 408, 120));
   public Form displayIncursionForm;
   public Form openIncursionForm;
   public ConfirmationForm closeIncursionConfirmation;
   public FormContentBox displayIncursionDetailsBox;
   public FormContentBox openIncursionDetailsBox;
   private final FormContainerSlot tabletBox;
   public IncursionData currentIncursion = null;
   public FormLocalTextButton openButton;
   public FormLocalTextButton enterButton;
   public FormLocalTextButton closeButton;
   private final int displayIncursionFormBaseHeight = 315;
   private final int detailsIncursionFormBaseHeight = 205;
   private InventoryItem currentTablet;
   private int modifierHeight;
   private int lootHeight;

   public FallenAltarContainerForm(Client var1, FallenAltarContainer var2) {
      super(var1, var2);
      FormFlow var3 = new FormFlow(5);
      this.placeTabletForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "placegatewaytablet", new FontOptions(20), 0, this.placeTabletForm.getWidth() / 2, 4, this.placeTabletForm.getWidth() - 10), 5));
      ((FormContainerSlot)this.placeTabletForm.addComponent(new FormContainerSlot(var1, var2, var2.TABLET_SLOT, this.placeTabletForm.getWidth() / 2 - 20, var3.next(45)))).setDecal(Settings.UI.inventoryslot_icon_gatewaytablet);
      this.placeTabletForm.setHeight(var3.next());
      FormFlow var4 = new FormFlow(4);
      this.displayIncursionForm = (Form)this.addComponent(new Form("fallenaltar", 400, 315));
      this.displayIncursionForm.addComponent(new FormLocalLabel("object", "fallenaltar", new FontOptions(20), 0, this.displayIncursionForm.getWidth() / 2, 5));
      var4.next(20);
      this.displayIncursionForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 8, var4.next() + 4, this.displayIncursionForm.getWidth() - 16, true));
      var4.next(10);
      int var5 = var4.next();
      this.displayIncursionDetailsBox = (FormContentBox)this.displayIncursionForm.addComponent(new FormContentBox(4, var5, this.displayIncursionForm.getWidth(), 205));
      this.tabletBox = new FormContainerSlot(var1, var2, var2.TABLET_SLOT, this.displayIncursionForm.getWidth() / 2 - 20, this.displayIncursionDetailsBox.getHeight() + 36);
      ((FormContainerSlot)this.displayIncursionForm.addComponent(this.tabletBox)).setDecal(Settings.UI.inventoryslot_icon_gatewaytablet);
      FormFlow var6 = new FormFlow(4);
      this.currentTablet = var2.getSlot(var2.TABLET_SLOT).getItem();
      this.openIncursionForm = (Form)this.addComponent(new Form("openincursion", 400, 315));
      this.openIncursionForm.addComponent(new FormLocalLabel("ui", "incursioncurrent", new FontOptions(16), 0, this.openIncursionForm.getWidth() / 2, var6.next(16)));
      this.openIncursionDetailsBox = (FormContentBox)this.openIncursionForm.addComponent(new FormContentBox(4, var6.next(205), this.openIncursionForm.getWidth() - 8, 205));
      this.enterButton = (FormLocalTextButton)this.openIncursionForm.addComponent(new FormLocalTextButton("ui", "incursionenterbutton", 4, var6.next(0), this.openIncursionForm.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE));
      this.enterButton.onClicked((var1x) -> {
         if (var2.altarEntity.hasOpenIncursion()) {
            var2.enterIncursion.runAndSend();
         }

      });
      this.closeButton = (FormLocalTextButton)this.openIncursionForm.addComponent(new FormLocalTextButton("ui", "incursionclosebutton", 4, var6.next(40), this.openIncursionForm.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE));
      this.closeButton.onClicked((var3x) -> {
         OpenIncursion var4 = var2.altarEntity.getOpenIncursion();
         if (var4 != null) {
            GameMessageBuilder var5 = (new GameMessageBuilder()).append("ui", var4.canComplete ? "incursionconfirmcomplete" : "incursionconfirmclose").append("\n\n").append("ui", "incursionconfirmdelete");
            this.closeIncursionConfirmation.setupConfirmation((GameMessage)var5, () -> {
               OpenIncursion var3 = var2.altarEntity.getOpenIncursion();
               if (var3 != null) {
                  var2.closeIncursion.runAndSend(var3.canComplete);
                  this.currentIncursion = null;
               }

               this.makeCurrent(this.placeTabletForm);
               var1.getPlayer().setInventoryExtended(true);
            }, () -> {
               this.makeCurrent(this.openIncursionForm);
            });
            this.makeCurrent(this.closeIncursionConfirmation);
         }

      });
      this.openIncursionForm.setHeight(var6.next());
      this.closeIncursionConfirmation = (ConfirmationForm)this.addComponent(new ConfirmationForm("closeincursionconfirm"));
      this.refreshIncursionDetails();
      this.refreshOpenIncursionDetails();
      if (var2.altarEntity.hasOpenIncursion()) {
         this.makeCurrent(this.openIncursionForm);
      } else if (!this.updatePlaceTabletCurrent()) {
         this.makeCurrent(this.placeTabletForm);
         var1.getPlayer().setInventoryExtended(true);
      }

      this.updateFormSize();
   }

   public void refreshCurrentForm() {
      this.refreshIncursionDetails();
      this.refreshOpenIncursionDetails();
   }

   public void refreshIncursionDetails() {
      this.displayIncursionDetailsBox.clearComponents();
      if (this.openButton != null) {
         this.displayIncursionForm.removeComponent(this.openButton);
      }

      this.openButton = null;
      if (((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).getItem() != null) {
         this.currentIncursion = GatewayTabletItem.getIncursionData(((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).getItem());
      } else if (((FallenAltarContainer)this.container).altarEntity.hasOpenIncursion()) {
         this.currentIncursion = ((FallenAltarContainer)this.container).altarEntity.getOpenIncursion().incursionData;
      } else {
         this.currentIncursion = null;
      }

      if (this.currentIncursion == null) {
         if (((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).getItem() == null) {
            this.makeCurrent(this.placeTabletForm);
         }

      } else {
         if (this.currentIncursion instanceof BiomeMissionIncursionData) {
            this.updateFormSize();
         }

         this.currentIncursion.setUpDetails((FallenAltarContainer)this.container, this, this.displayIncursionDetailsBox, false);
         this.openButton = (FormLocalTextButton)this.displayIncursionForm.addComponent(new FormLocalTextButton("ui", "incursionopenbutton", 4, this.displayIncursionForm.getHeight() - 24 - 4, this.displayIncursionForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE) {
            protected void addTooltips(PlayerMob var1) {
               super.addTooltips(var1);
               GameTooltips var2 = FallenAltarContainerForm.this.currentIncursion.getOpenButtonTooltips((FallenAltarContainer)FallenAltarContainerForm.this.container);
               if (var2 != null) {
                  Screen.addTooltip(var2, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
               }

            }
         });
         this.openButton.onClicked((var1) -> {
            ((FallenAltarContainer)this.container).openIncursion.runAndSend(this.currentIncursion.getUniqueID());
         });
      }
   }

   public void refreshOpenIncursionDetails() {
      this.openIncursionDetailsBox.clearComponents();
      OpenIncursion var1 = ((FallenAltarContainer)this.container).altarEntity.getOpenIncursion();
      if (var1 != null) {
         var1.incursionData.setUpDetails((FallenAltarContainer)this.container, this, this.openIncursionDetailsBox, true);
         if (var1.canComplete) {
            this.closeButton.setLocalization("ui", "incursioncompletebutton");
            this.closeButton.color = ButtonColor.GREEN;
         } else {
            this.closeButton.setLocalization("ui", "incursionclosebutton");
            this.closeButton.color = ButtonColor.RED;
         }
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.refreshCurrentForm();
   }

   public void updateFormSize() {
      BiomeMissionIncursionData var1 = (BiomeMissionIncursionData)this.currentIncursion;
      int var2;
      if (var1 != null) {
         var2 = 0;

         Iterator var3;
         ArrayList var4;
         for(var3 = var1.playerPersonalIncursionCompleteRewards.iterator(); var3.hasNext(); var2 += var4.size()) {
            var4 = (ArrayList)var3.next();
         }

         for(var3 = var1.playerSharedIncursionCompleteRewards.iterator(); var3.hasNext(); var2 += var4.size()) {
            var4 = (ArrayList)var3.next();
         }

         this.modifierHeight = var1.levelModifiers.size() * 18;
         this.lootHeight = (var1.getLootCount() + var2) * 20;
      }

      this.displayIncursionForm.setHeight(315 + this.modifierHeight + this.lootHeight);
      this.openIncursionForm.setHeight(315 + this.modifierHeight + this.lootHeight);
      this.displayIncursionDetailsBox.setHeight(205 + this.modifierHeight + this.lootHeight);
      this.openIncursionDetailsBox.setHeight(205 + this.modifierHeight + this.lootHeight);
      this.tabletBox.setY(this.displayIncursionDetailsBox.getHeight() + 36);
      this.enterButton.setY(this.openIncursionDetailsBox.getHeight() + 30);
      this.closeButton.setY(this.openIncursionDetailsBox.getHeight() + 66);
      ContainerComponent.setPosFocus(this.placeTabletForm);
      ContainerComponent.setPosFocus(this.displayIncursionForm);
      ContainerComponent.setPosFocus(this.openIncursionForm);
      if (this.displayIncursionForm.getY() <= 0) {
         var2 = Math.abs(8 - this.displayIncursionForm.getY());
         this.displayIncursionForm.setHeight(this.displayIncursionForm.getHeight() - var2);
         this.displayIncursionForm.setY(8);
         this.openIncursionForm.setHeight(this.displayIncursionForm.getHeight());
         this.openIncursionForm.setY(8);
         this.displayIncursionDetailsBox.setHeight(this.displayIncursionDetailsBox.getHeight() - var2 - 5);
         this.openIncursionDetailsBox.setHeight(this.displayIncursionDetailsBox.getHeight());
         this.tabletBox.setY(this.displayIncursionDetailsBox.getHeight() + 40);
         this.enterButton.setY(this.openIncursionDetailsBox.getHeight() + 30);
         this.closeButton.setY(this.openIncursionDetailsBox.getHeight() + 66);
         ContainerComponent.setPosFocus(this.placeTabletForm);
         ContainerComponent.setPosFocus(this.displayIncursionForm);
         ContainerComponent.setPosFocus(this.openIncursionForm);
      }

   }

   public boolean updatePlaceTabletCurrent() {
      if (this.isCurrent(this.placeTabletForm) && this.currentIncursion != null && !((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).isClear()) {
         this.makeCurrent(this.displayIncursionForm);
         return true;
      } else if (!this.isCurrent(this.placeTabletForm) && this.currentIncursion != null && ((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT).isClear()) {
         this.makeCurrent(this.placeTabletForm);
         this.client.getPlayer().setInventoryExtended(true);
         return true;
      } else {
         return false;
      }
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      ContainerSlot var4 = ((FallenAltarContainer)this.container).getSlot(((FallenAltarContainer)this.container).TABLET_SLOT);
      if (this.currentTablet == null && var4.getItem() != null) {
         this.currentTablet = var4.getItem();
         this.refreshCurrentForm();
      }

      if (this.currentTablet != null && var4.getItem() != null && !var4.getItem().equals(var2.getLevel(), this.currentTablet, false, false, "equals")) {
         this.currentTablet = var4.getItem();
         this.refreshCurrentForm();
      }

      if (((FallenAltarContainer)this.container).altarEntity.hasOpenIncursion()) {
         if (this.isCurrent(this.displayIncursionForm)) {
            this.refreshOpenIncursionDetails();
            this.makeCurrent(this.openIncursionForm);
         }
      } else {
         if (this.isCurrent(this.openIncursionForm)) {
            this.refreshIncursionDetails();
         }

         this.updatePlaceTabletCurrent();
         if (this.openButton != null) {
            if (this.currentIncursion != null) {
               this.openButton.setActive(this.currentIncursion.canOpen((FallenAltarContainer)this.container));
            } else {
               this.openButton.setActive(true);
            }
         }
      }

      super.draw(var1, var2, var3);
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public boolean shouldShowInventory() {
      return true;
   }

   public boolean shouldShowToolbar() {
      return true;
   }
}
