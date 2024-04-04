package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeClaimAction extends BooleanCustomAction {
   public final SettlementContainer container;

   public ChangeClaimAction(SettlementContainer var1) {
      this.container = var1;
   }

   protected void run(boolean var1) {
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            ServerClient var3 = this.container.client.getServerClient();
            if (var1 && this.container.getLevelLayer().getOwnerAuth() != -1L) {
               (new SettlementBasicsEvent(var2)).applyAndSendToClient(var3);
               return;
            }

            if (var1) {
               int var4 = var3.getServer().world.settings.maxSettlementsPerPlayer;
               if (var4 > 0) {
                  long var5 = var3.getServer().levelCache.getSettlements().stream().filter((var1x) -> {
                     return var1x.ownerAuth == var3.authentication;
                  }).count();
                  if (var5 >= (long)var4) {
                     var3.sendChatMessage((GameMessage)(new LocalMessage("misc", "maxsettlementsreached", new Object[]{"count", var4})));
                     return;
                  }
               }
            }

            this.container.getLevelLayer().setOwner(var1 ? var3 : null);
            var2.sendEvent(SettlementBasicsEvent.class);
         }
      }

   }
}
