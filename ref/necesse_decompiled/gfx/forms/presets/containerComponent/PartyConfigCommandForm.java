package necesse.gfx.forms.presets.containerComponent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.PartyConfigContainer;

public class PartyConfigCommandForm extends FormSwitcher {
   public final Client client;
   public final PartyConfigContainer container;
   public final SelectedSettlersHandler selectedSettlers;
   protected Runnable inventoryPressed;
   protected Form noneSelectedForm;
   protected Form selectedForm;
   protected int lastPartySize;
   protected FormLocalLabel nonePartySizeLabel;
   protected FormLocalLabel selectedPartySizeLabel;

   public PartyConfigCommandForm(Client var1, PartyConfigContainer var2, SelectedSettlersHandler var3, Runnable var4) {
      this.client = var1;
      this.container = var2;
      this.selectedSettlers = var3;
      this.inventoryPressed = var4;
      this.noneSelectedForm = (Form)this.addComponent(new Form(408, 200));
      FormFlow var5 = new FormFlow(4);
      this.noneSelectedForm.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.noneSelectedForm.getWidth() / 2, 0), 4));
      this.nonePartySizeLabel = (FormLocalLabel)this.noneSelectedForm.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel(new LocalMessage("ui", "adventurepartysize", new Object[]{"size", this.lastPartySize}), new FontOptions(12), 0, this.noneSelectedForm.getWidth() / 2, 0), 5));
      this.noneSelectedForm.addComponent((FormLocalLabel)var5.nextY(new FormLocalLabel("ui", "settlementcommandtip", new FontOptions(16), 0, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() - 20), 8));
      ((FormLocalTextButton)this.noneSelectedForm.addComponent((FormLocalTextButton)var5.nextY(new FormLocalTextButton("ui", "adventurepartycommandall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var3x) -> {
         synchronized(var3) {
            synchronized(var1.adventureParty) {
               var3.selectSettlers((Iterable)var1.adventureParty.getMobUniqueIDs());
            }
         }

         this.updateSelectedForm();
      });
      if (var4 != null) {
         ((FormLocalTextButton)this.noneSelectedForm.addComponent((FormLocalTextButton)var5.nextY(new FormLocalTextButton("ui", "adventurepartyinventory", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
            var4.run();
         });
      }

      this.noneSelectedForm.setHeight(var5.next());
      this.updatePartySizeLabel();
      this.selectedForm = (Form)this.addComponent(new Form(408, 200));
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (!this.selectedSettlers.isEmpty() && var1.getID() == 256) {
         if (!var1.state) {
            synchronized(this.selectedSettlers) {
               this.selectedSettlers.clear();
               this.updateCurrentForm();
            }
         }

         var1.use();
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      super.handleControllerEvent(var1, var2, var3);
      if (!this.selectedSettlers.isEmpty() && var1.getState() == ControllerInput.MENU_BACK) {
         if (!var1.buttonState) {
            synchronized(this.selectedSettlers) {
               this.selectedSettlers.clear();
               this.updateCurrentForm();
            }
         }

         var1.use();
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updateCurrentForm();
      if (this.lastPartySize != this.client.adventureParty.getSize()) {
         this.updatePartySizeLabel();
      }

      super.draw(var1, var2, var3);
   }

   public void updatePartySizeLabel() {
      this.lastPartySize = this.client.adventureParty.getSize();
      this.nonePartySizeLabel.setLocalization(new LocalMessage("ui", "adventurepartysize", new Object[]{"size", this.lastPartySize}));
      if (this.selectedPartySizeLabel != null) {
         this.selectedPartySizeLabel.setLocalization(new LocalMessage("ui", "adventurepartysize", new Object[]{"size", this.lastPartySize}));
      }

   }

   public void updateCurrentForm() {
      if (this.selectedSettlers.isEmpty()) {
         if (!this.isCurrent(this.noneSelectedForm)) {
            this.makeCurrent(this.noneSelectedForm);
         }
      } else if (!this.isCurrent(this.selectedForm)) {
         this.updateSelectedForm();
         this.makeCurrent(this.selectedForm);
      }

   }

   public void updateSelectedForm() {
      this.selectedForm.clearComponents();
      FormFlow var1 = new FormFlow(4);
      this.selectedForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.selectedForm.getWidth() / 2, 0), 4));
      this.nonePartySizeLabel = (FormLocalLabel)this.selectedForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "adventurepartysize", new Object[]{"size", this.lastPartySize}), new FontOptions(12), 0, this.selectedForm.getWidth() / 2, 0), 5));
      this.updatePartySizeLabel();
      ArrayList var2 = new ArrayList(this.selectedSettlers.getSize());
      synchronized(this.selectedSettlers) {
         Iterator var4 = this.selectedSettlers.get().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break;
            }

            int var5 = (Integer)var4.next();
            var2.add((Mob)this.client.getLevel().entityManager.mobs.get(var5, false));
         }
      }

      Object var3;
      if (var2.size() == 1) {
         Mob var8 = (Mob)var2.get(0);
         if (var8 == null) {
            var3 = new LocalMessage("ui", "settlementcommandselected", new Object[]{"count", var2.size()});
         } else {
            var3 = var8.getLocalization();
         }
      } else {
         var3 = new LocalMessage("ui", "settlementcommandselected", new Object[]{"count", var2.size()});
      }

      this.selectedForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel((GameMessage)var3, new FontOptions(16), 0, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() - 20), 8));
      ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "settlementcommandfollow", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         synchronized(this.selectedSettlers) {
            this.container.commandFollowMeAction.runAndSend(this.selectedSettlers.get());
         }
      });
      ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "settlementcommandclear", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         synchronized(this.selectedSettlers) {
            this.container.commandDisbandAction.runAndSend(this.selectedSettlers.get());
            this.selectedSettlers.clear();
            this.updateCurrentForm();
         }
      });
      if (this.inventoryPressed != null) {
         ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "adventurepartyinventory", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
            this.inventoryPressed.run();
         });
      }

      ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "cancelbutton", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         synchronized(this.selectedSettlers) {
            this.selectedSettlers.clear();
            this.updateCurrentForm();
         }
      });
      this.selectedForm.setHeight(var1.next());
      ContainerComponent.setPosInventory(this.selectedForm);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosInventory(this.noneSelectedForm);
      ContainerComponent.setPosInventory(this.selectedForm);
   }
}
