package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
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
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class SettlementCommandForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   protected Form noneSelectedForm;
   protected Form selectedForm;

   public SettlementCommandForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.noneSelectedForm = (Form)this.addComponent(new Form(300, 200));
      FormFlow var4 = new FormFlow(4);
      this.noneSelectedForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.noneSelectedForm.getWidth() / 2, 0), 4));
      this.noneSelectedForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "settlementcommandtip", new FontOptions(16), 0, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() - 20), 8));
      ((FormLocalTextButton)this.noneSelectedForm.addComponent((FormLocalTextButton)var4.nextY(new FormLocalTextButton("ui", "settlementcommandall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((var2x) -> {
         synchronized(var3.selectedSettlers) {
            List var4 = (List)var3.settlers.stream().map((var0) -> {
               return var0.mobUniqueID;
            }).collect(Collectors.toList());
            var3.selectedSettlers.selectSettlers((Iterable)var4);
         }

         this.updateSelectedForm();
      });
      ((FormLocalTextButton)this.noneSelectedForm.addComponent((FormLocalTextButton)var4.nextY(new FormLocalTextButton("ui", "settlementcommandclearall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((var2x) -> {
         HashSet var3x = new HashSet();
         Iterator var4 = var3.settlers.iterator();

         while(var4.hasNext()) {
            SettlementSettlerBasicData var5 = (SettlementSettlerBasicData)var4.next();
            var3x.add(var5.mobUniqueID);
         }

         var2.commandSettlersClearOrders.runAndSend(var3x);
      });
      this.noneSelectedForm.setHeight(var4.next());
      this.selectedForm = (Form)this.addComponent(new Form(300, 200));
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (!this.containerForm.selectedSettlers.isEmpty() && var1.getID() == 256) {
         if (!var1.state) {
            synchronized(this.containerForm.selectedSettlers) {
               this.containerForm.selectedSettlers.clear();
               this.updateCurrentForm();
            }
         }

         var1.use();
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      super.handleControllerEvent(var1, var2, var3);
      if (!this.containerForm.selectedSettlers.isEmpty() && var1.getState() == ControllerInput.MENU_BACK) {
         if (!var1.buttonState) {
            synchronized(this.containerForm.selectedSettlers) {
               this.containerForm.selectedSettlers.clear();
               this.updateCurrentForm();
            }
         }

         var1.use();
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updateCurrentForm();
      super.draw(var1, var2, var3);
   }

   public void onSetCurrent(boolean var1) {
      if (var1) {
         this.updateCurrentForm();
      }

   }

   public void updateCurrentForm() {
      if (this.containerForm.selectedSettlers.isEmpty()) {
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
      ArrayList var2 = new ArrayList(this.containerForm.selectedSettlers.getSize());
      synchronized(this.containerForm.selectedSettlers) {
         Iterator var4 = this.containerForm.selectedSettlers.get().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break;
            }

            int var5 = (Integer)var4.next();
            Mob var6 = (Mob)this.client.getLevel().entityManager.mobs.get(var5, false);
            if (var6 instanceof CommandMob) {
               var2.add((CommandMob)var6);
            }
         }
      }

      Object var3;
      if (var2.size() == 1) {
         Mob var9 = (Mob)var2.get(0);
         var3 = var9.getLocalization();
      } else {
         var3 = new LocalMessage("ui", "settlementcommandselected", new Object[]{"count", var2.size()});
      }

      this.selectedForm.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel((GameMessage)var3, new FontOptions(16), 0, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() - 20), 8));
      boolean var10 = var2.stream().allMatch(CommandMob::getHideOnLowHealth);
      ((FormLocalCheckBox)this.selectedForm.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("ui", "settlementcommandhidelowhealth", 4, 0, var10, this.selectedForm.getWidth() - 8), 4))).onClicked((var2x) -> {
         var2.forEach((var1) -> {
            var1.setHideOnLowHealth(((FormCheckBox)var2x.from).checked);
         });
         synchronized(this.containerForm.selectedSettlers) {
            this.container.commandSettlersSetHideOnLowHealth.runAndSend(this.containerForm.selectedSettlers.get(), ((FormCheckBox)var2x.from).checked);
         }
      });
      ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "settlementcommandfollow", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         synchronized(this.containerForm.selectedSettlers) {
            this.container.commandSettlersFollow.runAndSend(this.containerForm.selectedSettlers.get());
         }
      });
      ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "settlementcommandclear", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         synchronized(this.containerForm.selectedSettlers) {
            this.container.commandSettlersClearOrders.runAndSend(this.containerForm.selectedSettlers.get());
            this.containerForm.selectedSettlers.clear();
            this.updateCurrentForm();
         }
      });
      ((FormLocalTextButton)this.selectedForm.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton("ui", "cancelbutton", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         synchronized(this.containerForm.selectedSettlers) {
            this.containerForm.selectedSettlers.clear();
            this.updateCurrentForm();
         }
      });
      this.selectedForm.setHeight(var1.next());
      ContainerComponent.setPosInventory(this.selectedForm);
   }

   public void onMenuButtonClicked(FormSwitcher var1) {
      SettlementSubForm.super.onMenuButtonClicked(var1);
      this.updateCurrentForm();
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosInventory(this.noneSelectedForm);
      ContainerComponent.setPosInventory(this.selectedForm);
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementcommand");
   }

   public String getTypeString() {
      return "command";
   }
}
