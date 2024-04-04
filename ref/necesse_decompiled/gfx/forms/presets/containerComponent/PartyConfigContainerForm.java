package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormComponent;
import necesse.inventory.container.PartyConfigContainer;

public class PartyConfigContainerForm extends ContainerFormSwitcher<PartyConfigContainer> {
   public static boolean startInCommand = true;
   public PartyConfigForm partyConfigForm;
   public PartyConfigCommandForm commandForm;
   public final SelectedSettlersHandler selectedSettlers;
   public SelectSettlersContainerGameTool tool;

   public PartyConfigContainerForm(Client var1, PartyConfigContainer var2) {
      super(var1, var2);
      this.selectedSettlers = new SelectedSettlersHandler(var1) {
         public void updateSelectedSettlers(boolean var1) {
            PartyConfigContainerForm.this.updateSelectedSettlers(var1);
         }
      };
      this.partyConfigForm = (PartyConfigForm)this.addComponent(new PartyConfigForm(var1, var2, var2.PARTY_SLOTS_START, var2.PARTY_SLOTS_END, 408, () -> {
         this.commandForm.updateCurrentForm();
         this.makeCurrent(this.commandForm);
         synchronized(this.selectedSettlers) {
            synchronized(var1.adventureParty) {
               this.selectedSettlers.selectSettlers((Iterable)var1.adventureParty.getMobUniqueIDs());
            }
         }

         this.commandForm.updateSelectedForm();
      }, (Runnable)null, () -> {
         ContainerComponent.setPosFocus(this.partyConfigForm);
      }));
      this.commandForm = (PartyConfigCommandForm)this.addComponent(new PartyConfigCommandForm(var1, var2, this.selectedSettlers, () -> {
         this.makeCurrent(this.partyConfigForm);
      }), (var3, var4) -> {
         if (!var4) {
            if (this.tool != null) {
               Screen.clearGameTool(this.tool);
            }

            this.tool = null;
         } else {
            if (this.tool != null) {
               Screen.clearGameTool(this.tool);
            }

            Screen.setGameTool(this.tool = new PartyConfigGameTool(var1, this.selectedSettlers, var2));
         }

      });
   }

   protected void init() {
      super.init();
      this.selectedSettlers.init();
      this.client.adventureParty.updateMobsFromLevel(this.client.getLevel());
      this.selectedSettlers.cleanUp((var1x) -> {
         if (this.client.adventureParty.contains(var1x)) {
            return true;
         } else {
            Mob var2 = (Mob)this.client.getLevel().entityManager.mobs.get(var1x, false);
            return var2 instanceof HumanMob && ((HumanMob)var2).canBeCommanded(this.client);
         }
      });
      this.commandForm.updateSelectedForm();
      this.commandForm.updateCurrentForm();
      boolean var1 = this.selectedSettlers.isEmpty() && this.client.adventureParty.isEmpty() && this.client.getPlayer().getInv().hasPartyItems();
      this.makeCurrent((FormComponent)(startInCommand && !var1 ? this.commandForm : this.partyConfigForm));
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void updateSelectedSettlers(boolean var1) {
      if (!this.selectedSettlers.isEmpty()) {
         if (var1 && !this.isCurrent(this.commandForm) || this.getCurrent() == null) {
            this.commandForm.updateCurrentForm();
            this.makeCurrent(this.commandForm);
         }

         this.commandForm.updateSelectedForm();
      } else if (this.isCurrent(this.commandForm)) {
         this.commandForm.updateCurrentForm();
      }

   }

   public boolean shouldShowInventory() {
      return !this.isCurrent(this.commandForm);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.partyConfigForm);
   }

   public void dispose() {
      startInCommand = this.isCurrent(this.commandForm);
      super.dispose();
      this.selectedSettlers.dispose();
      if (this.tool != null) {
         Screen.clearGameTool(this.tool);
      }

   }
}
