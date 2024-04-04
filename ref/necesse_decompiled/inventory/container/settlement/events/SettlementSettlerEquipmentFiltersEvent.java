package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerEquipmentFilterData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerEquipmentFiltersEvent extends ContainerEvent {
   public final ArrayList<SettlementSettlerEquipmentFilterData> settlers;

   public SettlementSettlerEquipmentFiltersEvent(SettlementLevelData var1) {
      this.settlers = new ArrayList();
      Iterator var2 = var1.settlers.iterator();

      while(var2.hasNext()) {
         LevelSettler var3 = (LevelSettler)var2.next();
         SettlerMob var4 = var3.getMob();
         if (var4 != null) {
            this.settlers.add(new SettlementSettlerEquipmentFilterData(var3));
         }
      }

   }

   public SettlementSettlerEquipmentFiltersEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      this.settlers = new ArrayList(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.settlers.add(new SettlementSettlerEquipmentFilterData(var1));
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.settlers.size());
      Iterator var2 = this.settlers.iterator();

      while(var2.hasNext()) {
         SettlementSettlerEquipmentFilterData var3 = (SettlementSettlerEquipmentFilterData)var2.next();
         var3.writeContentPacket(var1);
      }

   }
}
