package necesse.engine.network.client.tutorialPhases;

import necesse.engine.control.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.state.MainGame;

public class IslandTravelTipPhase extends TutorialPhase {
   public IslandTravelTipPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void updateObjective(MainGame var1) {
      LocalMessage var2 = new LocalMessage("tutorials", "travel", "key", "[input=" + Control.SHOW_WORLD_MAP.id + "]");
      LocalMessage var3 = new LocalMessage("tutorials", "next");
      this.setObjective(var1, var2, var3, (var1x) -> {
         this.over();
      });
   }
}
