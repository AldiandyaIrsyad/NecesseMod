package necesse.engine.network.client.tutorialPhases;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.state.MainGame;

public class CraftPickaxePhase extends TutorialPhase {
   public CraftPickaxePhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void updateObjective(MainGame var1) {
      this.setObjective(var1, "craftpick");
   }

   public void tick() {
      if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem("woodpickaxe"), false, false, false, "tutorial") > 0) {
         this.over();
      }

   }
}
