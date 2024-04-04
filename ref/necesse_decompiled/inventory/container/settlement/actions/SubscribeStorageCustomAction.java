package necesse.inventory.container.settlement.actions;

import java.awt.Point;
import java.util.function.BooleanSupplier;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EventSubscribeCustomAction;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;

public class SubscribeStorageCustomAction extends EventSubscribeCustomAction<Point> {
   public final Container container;

   public SubscribeStorageCustomAction(Container var1) {
      this.container = var1;
   }

   public void writeData(PacketWriter var1, Point var2) {
      var1.putNextInt(var2.x);
      var1.putNextInt(var2.y);
   }

   public Point readData(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      return new Point(var2, var3);
   }

   public void onSubscribed(BooleanSupplier var1, Point var2) {
      this.container.subscribeEvent(SettlementStorageChangeAllowedEvent.class, (var1x) -> {
         return var1x.tileX == var2.x && var1x.tileY == var2.y;
      }, var1);
      this.container.subscribeEvent(SettlementStorageLimitsEvent.class, (var1x) -> {
         return var1x.tileX == var2.x && var1x.tileY == var2.y;
      }, var1);
      this.container.subscribeEvent(SettlementStoragePriorityLimitEvent.class, (var1x) -> {
         return var1x.tileX == var2.x && var1x.tileY == var2.y;
      }, var1);
      this.container.subscribeEvent(SettlementStorageFullUpdateEvent.class, (var1x) -> {
         return var1x.tileX == var2.x && var1x.tileY == var2.y;
      }, var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onSubscribed(BooleanSupplier var1, Object var2) {
      this.onSubscribed(var1, (Point)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readData(PacketReader var1) {
      return this.readData(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writeData(PacketWriter var1, Object var2) {
      this.writeData(var1, (Point)var2);
   }
}
