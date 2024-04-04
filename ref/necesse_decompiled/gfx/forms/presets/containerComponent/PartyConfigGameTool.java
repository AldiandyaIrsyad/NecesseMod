package necesse.gfx.forms.presets.containerComponent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.inventory.container.PartyConfigContainer;

public class PartyConfigGameTool extends SelectSettlersContainerGameTool {
   public final PartyConfigContainer container;

   public PartyConfigGameTool(Client var1, SelectedSettlersHandler var2, PartyConfigContainer var3) {
      super(var1, var2);
      this.container = var3;
   }

   public SettlementToolHandler getCurrentToolHandler() {
      return null;
   }

   public Stream<Mob> streamAllSettlers(Rectangle var1) {
      return this.client.getLevel().entityManager.mobs.streamInRegionsShape(var1, 1).filter((var0) -> {
         return var0 instanceof HumanMob;
      }).filter((var1x) -> {
         return ((HumanMob)var1x).canBeCommanded(this.client);
      });
   }

   public void commandAttack(Mob var1) {
      this.container.commandAttackAction.runAndSend(this.selectedSettlers.get(), var1);
   }

   public void commandGuard(int var1, int var2) {
      this.container.commandGuardAction.runAndSend(this.selectedSettlers.get(), var1, var2);
   }

   public void commandGuard(ArrayList<Point> var1) {
      this.container.commandGuardAction.runAndSend(this.selectedSettlers.get(), var1);
   }
}
