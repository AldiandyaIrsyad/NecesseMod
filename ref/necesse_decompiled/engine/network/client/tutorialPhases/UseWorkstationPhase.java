package necesse.engine.network.client.tutorialPhases;

import necesse.engine.control.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.state.MainGame;

public class UseWorkstationPhase extends TutorialPhase {
   public UseWorkstationPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void updateObjective(MainGame var1) {
      LocalMessage var2 = new LocalMessage("tutorials", "craftwork");
      var2.addReplacement("key1", "[input=" + Control.MOUSE1.id + "]");
      var2.addReplacement("key2", "[input=" + Control.MOUSE2.id + "]");
      this.setObjective(var1, var2);
   }

   public void usedWorkstation() {
      this.over();
   }
}
