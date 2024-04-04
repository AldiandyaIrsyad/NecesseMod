package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerBasicsEvent extends ContainerEvent {
   public final ArrayList<SettlementSettlerBasicData> settlers;
   public final ArrayList<SettlementLockedBedData> lockedBeds;

   public SettlementSettlerBasicsEvent(SettlementLevelData var1) {
      this.settlers = new ArrayList();
      this.lockedBeds = new ArrayList();
      Iterator var2 = var1.settlers.iterator();

      while(var2.hasNext()) {
         LevelSettler var3 = (LevelSettler)var2.next();
         SettlerMob var4 = var3.getMob();
         if (var4 != null) {
            this.settlers.add(new SettlementSettlerBasicData(var3));
         }
      }

      var2 = var1.getBeds().iterator();

      while(var2.hasNext()) {
         SettlementBed var5 = (SettlementBed)var2.next();
         if (var5.isLocked) {
            this.lockedBeds.add(new SettlementLockedBedData(var5.tileX, var5.tileY));
         }
      }

   }

   public SettlementSettlerBasicsEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      this.settlers = new ArrayList(var2);

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.settlers.add(new SettlementSettlerBasicData(var1));
      }

      var3 = var1.getNextShortUnsigned();
      this.lockedBeds = new ArrayList(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.lockedBeds.add(new SettlementLockedBedData(var1));
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.settlers.size());
      Iterator var2 = this.settlers.iterator();

      while(var2.hasNext()) {
         SettlementSettlerBasicData var3 = (SettlementSettlerBasicData)var2.next();
         var3.writeContentPacket(var1);
      }

      var1.putNextShortUnsigned(this.lockedBeds.size());
      var2 = this.lockedBeds.iterator();

      while(var2.hasNext()) {
         SettlementLockedBedData var4 = (SettlementLockedBedData)var2.next();
         var4.writeContentPacket(var1);
      }

   }
}
