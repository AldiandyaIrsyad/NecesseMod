package necesse.inventory.container.settlement.events;

import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;

public class SettlementWorkZonesEvent extends ContainerEvent {
   public final HashMap<Integer, SettlementWorkZone> zones;

   public SettlementWorkZonesEvent(SettlementLevelData var1) {
      this.zones = new HashMap(var1.getWorkZones().getZones());
   }

   public SettlementWorkZonesEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      this.zones = new HashMap();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         int var5 = var1.getNextInt();
         SettlementWorkZone var6 = SettlementWorkZoneRegistry.getNewZone(var4);
         var6.setUniqueID(var5);
         var6.applyPacket(var1);
         this.zones.put(var5, var6);
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.zones.size());
      Iterator var2 = this.zones.values().iterator();

      while(var2.hasNext()) {
         SettlementWorkZone var3 = (SettlementWorkZone)var2.next();
         var1.putNextShortUnsigned(var3.getID());
         var1.putNextInt(var3.getUniqueID());
         var3.writePacket(var1);
      }

   }
}
