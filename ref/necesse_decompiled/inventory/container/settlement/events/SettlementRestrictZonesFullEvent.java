package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementRestrictZoneData;
import necesse.inventory.container.settlement.data.SettlementSettlerRestrictZoneData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementRestrictZonesFullEvent extends ContainerEvent {
   public final ArrayList<SettlementSettlerRestrictZoneData> settlers;
   public final HashMap<Integer, SettlementRestrictZoneData> zones;
   public final int newSettlerRestrictZoneUniqueID;

   public SettlementRestrictZonesFullEvent(SettlementLevelData var1) {
      this.settlers = new ArrayList();
      Iterator var2 = var1.settlers.iterator();

      while(var2.hasNext()) {
         LevelSettler var3 = (LevelSettler)var2.next();
         SettlerMob var4 = var3.getMob();
         if (var4 != null) {
            this.settlers.add(new SettlementSettlerRestrictZoneData(var3));
         }
      }

      this.zones = new HashMap();
      var2 = var1.getRestrictZones().iterator();

      while(var2.hasNext()) {
         RestrictZone var5 = (RestrictZone)var2.next();
         this.zones.put(var5.uniqueID, new SettlementRestrictZoneData(var5));
      }

      this.newSettlerRestrictZoneUniqueID = var1.getNewSettlerRestrictZoneUniqueID();
   }

   public SettlementRestrictZonesFullEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      this.settlers = new ArrayList(var2);

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.settlers.add(new SettlementSettlerRestrictZoneData(var1));
      }

      var3 = var1.getNextShortUnsigned();
      this.zones = new HashMap(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         SettlementRestrictZoneData var5 = new SettlementRestrictZoneData(var1);
         this.zones.put(var5.uniqueID, var5);
      }

      this.newSettlerRestrictZoneUniqueID = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.settlers.size());
      Iterator var2 = this.settlers.iterator();

      while(var2.hasNext()) {
         SettlementSettlerRestrictZoneData var3 = (SettlementSettlerRestrictZoneData)var2.next();
         var3.writeContentPacket(var1);
      }

      var1.putNextShortUnsigned(this.zones.size());
      var2 = this.zones.values().iterator();

      while(var2.hasNext()) {
         SettlementRestrictZoneData var4 = (SettlementRestrictZoneData)var2.next();
         var4.writeContentPacket(var1);
      }

      var1.putNextInt(this.newSettlerRestrictZoneUniqueID);
   }
}
