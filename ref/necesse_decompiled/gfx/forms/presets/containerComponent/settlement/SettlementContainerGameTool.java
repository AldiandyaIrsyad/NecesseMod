package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.containerComponent.SelectSettlersContainerGameTool;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.inventory.container.settlement.SettlementContainer;

public class SettlementContainerGameTool extends SelectSettlersContainerGameTool {
   public final SettlementContainer container;
   public final SettlementContainerForm<?> containerForm;

   public SettlementContainerGameTool(Client var1, SelectedSettlersHandler var2, SettlementContainer var3, SettlementContainerForm<?> var4) {
      super(var1, var2);
      this.container = var3;
      this.containerForm = var4;
   }

   public SettlementToolHandler getCurrentToolHandler() {
      return this.containerForm.getCurrentToolHandler();
   }

   public Stream<Mob> streamAllSettlers(Rectangle var1) {
      return this.containerForm.settlers.stream().map((var1x) -> {
         return (Mob)this.level.entityManager.mobs.get(var1x.mobUniqueID, false);
      });
   }

   public void commandAttack(Mob var1) {
      this.container.commandSettlersAttack.runAndSend(this.selectedSettlers.get(), var1);
   }

   public void commandGuard(int var1, int var2) {
      this.container.commandSettlersGuard.runAndSend(this.selectedSettlers.get(), var1, var2);
   }

   public void commandGuard(ArrayList<Point> var1) {
      this.container.commandSettlersGuard.runAndSend(this.selectedSettlers.get(), var1);
   }
}
