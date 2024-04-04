package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.EventSubscribeCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SubscribeWorkZoneConfigCustomAction extends EventSubscribeCustomAction<SettlementWorkZone> {
   public final SettlementContainer container;

   public SubscribeWorkZoneConfigCustomAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void writeData(PacketWriter var1, SettlementWorkZone var2) {
      var1.putNextInt(var2.getUniqueID());
   }

   public SettlementWorkZone readData(PacketReader var1) {
      int var2 = var1.getNextInt();
      if (this.container.client.isServer()) {
         SettlementLevelData var3 = this.container.getLevelData();
         return var3.getWorkZones().getZone(var2);
      } else {
         return null;
      }
   }

   public void onSubscribed(BooleanSupplier var1, SettlementWorkZone var2) {
      if (var2 != null) {
         var2.subscribeConfigEvents(this.container, var1);
      } else if (this.container.client.isServer()) {
         GameLog.warn.println(this.container.client.getName() + " subscribed to unknown work zone config events");
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onSubscribed(BooleanSupplier var1, Object var2) {
      this.onSubscribed(var1, (SettlementWorkZone)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readData(PacketReader var1) {
      return this.readData(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writeData(PacketWriter var1, Object var2) {
      this.writeData(var1, (SettlementWorkZone)var2);
   }
}
