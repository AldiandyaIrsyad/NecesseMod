package necesse.inventory.container;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.level.maps.Level;

public class SettlementNameContainer extends Container {
   public final ContentCustomAction submitButton;
   public final Level level;

   public SettlementNameContainer(NetworkClient var1, int var2, final Level var3) {
      super(var1, var2);
      this.level = var3;
      this.submitButton = (ContentCustomAction)this.registerAction(new ContentCustomAction() {
         protected void run(Packet var1) {
            if (var3.isServer()) {
               GameMessage var2 = GameMessage.fromContentPacket(var1);
               var3.settlementLayer.setName(var2);
               SettlementNameContainer.this.close();
            }

         }
      });
   }
}
