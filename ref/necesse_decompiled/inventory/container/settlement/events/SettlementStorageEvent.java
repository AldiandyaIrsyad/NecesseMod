package necesse.inventory.container.settlement.events;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementStorageEvent extends ContainerEvent {
   public final ArrayList<Point> storage;

   public SettlementStorageEvent(SettlementLevelData var1) {
      ArrayList var2 = var1.getStorage();
      this.storage = new ArrayList(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         SettlementInventory var4 = (SettlementInventory)var3.next();
         this.storage.add(new Point(var4.tileX, var4.tileY));
      }

   }

   public SettlementStorageEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      this.storage = new ArrayList(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         int var5 = var1.getNextShortUnsigned();
         this.storage.add(new Point(var4, var5));
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.storage.size());
      Iterator var2 = this.storage.iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         var1.putNextShortUnsigned(var3.x);
         var1.putNextShortUnsigned(var3.y);
      }

   }
}
