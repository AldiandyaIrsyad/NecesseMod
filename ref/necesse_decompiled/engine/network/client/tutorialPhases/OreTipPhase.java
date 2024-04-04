package necesse.engine.network.client.tutorialPhases;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.state.MainGame;

public class OreTipPhase extends TutorialPhase {
   public OreTipPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void updateObjective(MainGame var1) {
      this.setObjective(var1, "ores", "next", (var1x) -> {
         this.over();
      });
   }
}
