package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketQuestAbandon;
import necesse.engine.network.packet.PacketQuestShare;
import necesse.engine.quest.Quest;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormQuestComponent;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;

public class QuestsContainerForm extends ContainerFormSwitcher<Container> {
   private Form questsForm = (Form)this.addComponent(new Form(350, 400));
   private ConfirmationForm confirmAbandonForm = (ConfirmationForm)this.addComponent(new ConfirmationForm("confirmAbandon"));
   private FormContentBox questsList;

   public QuestsContainerForm(Client var1, Container var2) {
      super(var1, var2);
      this.questsForm.addComponent(new FormLocalLabel("ui", "quests", new FontOptions(20), 0, this.questsForm.getWidth() / 2, 5));
      this.questsList = (FormContentBox)this.questsForm.addComponent(new FormContentBox(0, 30, this.questsForm.getWidth(), this.questsForm.getHeight() - 32 - 64));
      this.updateQuestsList();
      ((FormLocalCheckBox)this.questsForm.addComponent(new FormLocalCheckBox("ui", "tracknewquests", 5, this.questsForm.getHeight() - 60, Settings.trackNewQuests))).onClicked((var0) -> {
         Settings.trackNewQuests = ((FormCheckBox)var0.from).checked;
         Settings.saveClientSettings();
      });
      ((FormLocalTextButton)this.questsForm.addComponent(new FormLocalTextButton("ui", "closebutton", 4, this.questsForm.getHeight() - 40, this.questsForm.getWidth() - 8))).onClicked((var1x) -> {
         var1.closeContainer(true);
      });
      this.makeCurrent(this.questsForm);
   }

   public void updateQuestsList() {
      this.questsList.clearComponents();
      if (this.client.quests.getTotalQuests() == 0) {
         this.questsList.addComponent(new FormLocalLabel("ui", "noquests", new FontOptions(16), 0, this.questsForm.getWidth() / 2, 30, this.questsForm.getWidth() - 10));
      } else {
         int var1 = 10;
         boolean var2 = false;

         for(Iterator var3 = this.client.quests.getQuests().iterator(); var3.hasNext(); var2 = true) {
            Quest var4 = (Quest)var3.next();
            if (var2) {
               FormBreakLine var5 = (FormBreakLine)this.questsList.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, var1 + 5, this.questsForm.getWidth() - 20, true));
               var5.color = new Color(80, 80, 80);
               var1 += 10;
            }

            FormQuestComponent var9 = (FormQuestComponent)this.questsList.addComponent(new FormQuestComponent(0, var1, this.questsForm.getWidth(), var4));
            var1 += var9.getBoundingBox().height;
            FormLocalCheckBox var6 = (FormLocalCheckBox)this.questsList.addComponent(new FormLocalCheckBox("quests", "track", 10, var1 + 4, TrackedSidebarForm.isQuestTracked(var4)));
            var6.onClicked((var2x) -> {
               if (((FormCheckBox)var2x.from).checked) {
                  TrackedSidebarForm.addTrackedQuest(this.client, var4);
               } else {
                  TrackedSidebarForm.removeTrackedQuest(this.client, var4);
               }

            });
            Runnable var7 = () -> {
               this.confirmAbandonForm.setupConfirmation((GameMessage)(new LocalMessage("quests", "abandonconfirm")), () -> {
                  this.client.network.sendPacket(new PacketQuestAbandon(var4.getUniqueID()));
                  this.client.quests.removeQuest(var4);
                  this.updateQuestsList();
                  this.makeCurrent(this.questsForm);
               }, () -> {
                  this.makeCurrent(this.questsForm);
               });
               this.makeCurrent(this.confirmAbandonForm);
            };
            if (var4.canShare()) {
               FormDropdownButton var8 = (FormDropdownButton)this.questsList.addComponent(new FormDropdownButton(this.questsForm.getWidth() - 160, var1, FormInputSize.SIZE_20, ButtonColor.BASE, 150, new LocalMessage("quests", "actions")));
               if (var4.canShare()) {
                  var8.options.add(new LocalMessage("quests", "share"), () -> {
                     return this.client.getTeam() == -1 ? new LocalMessage("quests", "shareteamneeded") : null;
                  }, () -> {
                     return this.client.getTeam() != -1;
                  }, () -> {
                     this.client.network.sendPacket(new PacketQuestShare(var4.getUniqueID()));
                  });
               }

               var8.options.add(new LocalMessage("quests", "abandon"), var7);
               var1 += var8.getBoundingBox().height + 2;
            } else {
               FormLocalTextButton var10 = (FormLocalTextButton)this.questsList.addComponent(new FormLocalTextButton("quests", "abandon", this.questsForm.getWidth() - 160, var1, 150, FormInputSize.SIZE_20, ButtonColor.BASE));
               var10.onClicked((var1x) -> {
                  var7.run();
               });
               var1 += var10.getBoundingBox().height + 2;
            }
         }

         this.questsList.setContentBox(new Rectangle(0, 0, this.questsForm.getWidth(), var1 + 10));
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.questsForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }
}
