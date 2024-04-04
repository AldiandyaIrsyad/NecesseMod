package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLocalTextButtonToggle;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietsForm;
import necesse.gfx.forms.presets.containerComponent.settlement.equipment.SettlementEquipmentForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;

public class SettlementContainerForm<T extends SettlementContainer> extends ContainerFormList<T> {
   public static String lastOpenType;
   private Form menuBar;
   private FormSwitcher contentSwitcher;
   private LinkedList<SettlementSubForm> menus;
   private Form privateForm;
   public int settlerBasicsSubscription = -1;
   public ArrayList<SettlementSettlerBasicData> settlers = new ArrayList();
   public ArrayList<SettlementLockedBedData> lockedBeds = new ArrayList();
   private final SettlementCommandForm<T> commandForm;
   public final SelectedSettlersHandler selectedSettlers;
   public SettlementContainerGameTool tool;

   public SettlementContainerForm(Client var1, T var2) {
      super(var1, var2);
      this.selectedSettlers = new SelectedSettlersHandler(var1) {
         public void updateSelectedSettlers(boolean var1) {
            SettlementContainerForm.this.updateSelectedSettlers(var1);
         }
      };
      this.menuBar = (Form)this.addComponent(new Form("menubar", 800, 40));
      this.contentSwitcher = (FormSwitcher)this.addComponent(new FormSwitcher());
      this.menus = new LinkedList();
      this.menus.add(new SettlementSettingsForm(var1, var2, this));
      this.menus.add(new SettlementSettlersForm(var1, var2, this));
      this.menus.add(this.commandForm = new SettlementCommandForm(var1, var2, this));
      this.menus.add(new SettlementEquipmentForm(var1, var2, this));
      this.menus.add(new SettlementDietsForm(var1, var2, this));
      this.menus.add(new SettlementRestrictForm(var1, var2, this));
      this.menus.add(new SettlementDefendZoneForm(var1, var2, this));
      this.menus.add(new SettlementWorkPrioritiesForm(var1, var2, this));
      this.menus.add(new SettlementAssignWorkForm(var1, var2, this));
      Iterator var3 = this.menus.iterator();

      while(var3.hasNext()) {
         SettlementSubForm var4 = (SettlementSubForm)var3.next();
         this.contentSwitcher.addComponent((FormComponent)var4, (var0, var1x) -> {
            ((SettlementSubForm)var0).onSetCurrent(var1x);
         });
      }

      this.updateMenuBar();
      this.privateForm = (Form)this.contentSwitcher.addComponent(new Form(400, 40));
      this.onWindowResized();
   }

   protected void init() {
      ((SettlementContainer)this.container).onEvent(SettlementBasicsEvent.class, (var1x) -> {
         this.updatePrivateForm();
      });
      ((SettlementContainer)this.container).onEvent(SettlementSettlersChangedEvent.class, (var1x) -> {
         if (((SettlementContainer)this.container).basics.hasAccess(this.client)) {
            ((SettlementContainer)this.container).requestSettlerBasics.runAndSend();
         }

      });
      ((SettlementContainer)this.container).onEvent(SettlementSettlerBasicsEvent.class, (var1x) -> {
         this.settlers = var1x.settlers;
         this.lockedBeds = var1x.lockedBeds;
         synchronized(this.selectedSettlers) {
            Set var3 = (Set)this.settlers.stream().map((var0) -> {
               return var0.mobUniqueID;
            }).collect(Collectors.toSet());
            SelectedSettlersHandler var10000 = this.selectedSettlers;
            Objects.requireNonNull(var3);
            var10000.cleanUp(var3::contains);
         }
      });
      this.selectedSettlers.init();
      this.updatePrivateForm();
      if (lastOpenType != null) {
         if (!this.contentSwitcher.isCurrent(this.privateForm)) {
            Iterator var1 = this.menus.iterator();

            while(var1.hasNext()) {
               SettlementSubForm var2 = (SettlementSubForm)var1.next();
               if (lastOpenType.equals(var2.getTypeString())) {
                  var2.onMenuButtonClicked(this.contentSwitcher);
                  break;
               }
            }
         } else {
            lastOpenType = null;
         }
      }

      super.init();
   }

   private void updateMenuBar() {
      int var1 = Math.min(50 * this.menus.size(), 200);
      int var2 = Screen.getHudWidth() - 200;
      this.menuBar.setWidth(GameMath.limit(200 * this.menus.size(), var1, Math.max(var2, var1)));
      this.menuBar.clearComponents();
      int var3 = 4;
      int var4 = 0;

      for(Iterator var5 = this.menus.iterator(); var5.hasNext(); ++var4) {
         final SettlementSubForm var6 = (SettlementSubForm)var5.next();
         int var7 = this.menus.size() - var4;
         int var8 = this.menuBar.getWidth() - 4 - var3;
         int var9 = var8 / var7;
         FormLocalTextButtonToggle var10 = (FormLocalTextButtonToggle)this.menuBar.addComponent(new FormLocalTextButtonToggle(var6.getMenuButtonName(), var3, 0, var9, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE) {
            public boolean isToggled() {
               return SettlementContainerForm.this.contentSwitcher.isCurrent((FormComponent)var6);
            }
         });
         var10.controllerFocusHashcode = "settlementMenuButton" + var6.getTypeString();
         var10.onClicked((var2x) -> {
            var2x.preventDefault();
            ((FormButton)var2x.from).playTickSound();
            if (this.contentSwitcher.isCurrent((FormComponent)var6)) {
               this.contentSwitcher.clearCurrent();
               lastOpenType = null;
            } else {
               var6.onMenuButtonClicked(this.contentSwitcher);
               lastOpenType = var6.getTypeString();
            }

         });
         var3 += var9;
      }

      this.menuBar.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - this.menuBar.getHeight() / 2 - Settings.UI.formSpacing - 28);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updatePrivateFormActive(false);
      super.draw(var1, var2, var3);
   }

   private void updatePrivateForm() {
      this.privateForm.clearComponents();
      FormFlow var1 = new FormFlow(5);
      this.privateForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(((SettlementContainer)this.container).basics.settlementName, new FontOptions(20), 0, this.privateForm.getWidth() / 2, 0, this.privateForm.getWidth() - 20), 10));
      this.privateForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementispriv"), new FontOptions(16), 0, this.privateForm.getWidth() / 2, 0, this.privateForm.getWidth() - 20), 10));
      this.privateForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementprivatetip"), new FontOptions(16), 0, this.privateForm.getWidth() / 2, 0, this.privateForm.getWidth() - 20), 10));
      this.privateForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementowner", "owner", ((SettlementContainer)this.container).basics.ownerName), new FontOptions(16), 0, this.privateForm.getWidth() / 2, 0), 10));
      int var2 = Math.min(this.privateForm.getWidth() - 8, 300);
      if (((SettlementContainer)this.container).basics.isTeamPublic) {
         ((FormLocalTextButton)this.privateForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "teamjoin", this.privateForm.getWidth() / 2 - var2 / 2, 0, var2, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 20))).onClicked((var1x) -> {
            ((SettlementContainer)this.container).requestJoin.runAndSend(true);
            ((FormButton)var1x.from).startCooldown(5000);
         });
      } else {
         ((FormLocalTextButton)this.privateForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "teamrequestjoin", this.privateForm.getWidth() / 2 - var2 / 2, 0, var2, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 20))).onClicked((var1x) -> {
            ((SettlementContainer)this.container).requestJoin.runAndSend(false);
            ((FormButton)var1x.from).startCooldown(5000);
         });
      }

      this.privateForm.setHeight(var1.next());
      this.updatePrivateFormActive(true);
   }

   private void updatePrivateFormActive(boolean var1) {
      if (!((SettlementContainer)this.container).basics.hasAccess(this.client)) {
         if (var1 || !this.contentSwitcher.isCurrent(this.privateForm)) {
            this.contentSwitcher.makeCurrent(this.privateForm);
            this.menuBar.setHidden(true);
            if (this.settlerBasicsSubscription != -1) {
               ((SettlementContainer)this.container).subscribeSettlerBasics.unsubscribe(this.settlerBasicsSubscription);
            }

            this.settlerBasicsSubscription = -1;
            if (!this.selectedSettlers.isEmpty()) {
               this.selectedSettlers.clear();
            }

            if (this.tool != null) {
               Screen.clearGameTool(this.tool);
            }

            this.tool = null;
         }
      } else if (var1 || this.contentSwitcher.isCurrent(this.privateForm)) {
         if (this.contentSwitcher.isCurrent(this.privateForm)) {
            this.contentSwitcher.clearCurrent();
         }

         this.menuBar.setHidden(false);
         if (this.settlerBasicsSubscription == -1) {
            this.settlerBasicsSubscription = ((SettlementContainer)this.container).subscribeSettlerBasics.subscribe();
            ((SettlementContainer)this.container).requestSettlerBasics.runAndSend();
            if (this.tool != null) {
               Screen.clearGameTool(this.tool);
            }

            Screen.setGameTool(this.tool = new SettlementContainerGameTool(this.client, this.selectedSettlers, (SettlementContainer)this.container, this));
         }
      }

   }

   public boolean isCurrent(SettlementSubForm var1) {
      return this.contentSwitcher.isCurrent((FormComponent)var1);
   }

   public SettlementSubForm getCurrentSubForm() {
      FormComponent var1 = this.contentSwitcher.getCurrent();
      return var1 instanceof SettlementSubForm ? (SettlementSubForm)var1 : null;
   }

   public SettlementToolHandler getCurrentToolHandler() {
      SettlementSubForm var1 = this.getCurrentSubForm();
      return var1 != null ? var1.getToolHandler() : null;
   }

   public void updateSelectedSettlers(boolean var1) {
      if (((SettlementContainer)this.container).basics.hasAccess(this.client)) {
         if (!this.selectedSettlers.isEmpty()) {
            if (var1 && !this.contentSwitcher.isCurrent(this.commandForm) || this.contentSwitcher.getCurrent() == null) {
               this.commandForm.onMenuButtonClicked(this.contentSwitcher);
               lastOpenType = this.commandForm.getTypeString();
            }

            this.commandForm.updateSelectedForm();
         } else if (this.contentSwitcher.isCurrent(this.commandForm)) {
            this.commandForm.updateCurrentForm();
         }

      }
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateMenuBar();
      this.privateForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void dispose() {
      this.selectedSettlers.dispose();
      Screen.clearGameTool(this.tool);
      super.dispose();
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }
}
