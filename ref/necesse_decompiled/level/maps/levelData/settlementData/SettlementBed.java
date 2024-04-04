package necesse.level.maps.levelData.settlementData;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.BedObject;
import necesse.level.maps.Level;

public class SettlementBed {
   public final SettlementLevelData data;
   public final int tileX;
   public final int tileY;
   protected LevelSettler settler;
   public boolean isLocked;

   public SettlementBed(SettlementLevelData var1, int var2, int var3) {
      this.data = var1;
      this.tileX = var2;
      this.tileY = var3;
   }

   public SettlementBed(SettlementLevelData var1, LoadData var2) {
      this.data = var1;
      this.tileX = var2.getInt("tileX");
      this.tileY = var2.getInt("tileY");
      this.isLocked = var2.getBoolean("isLocked", this.isLocked, false);
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("tileX", this.tileX);
      var1.addInt("tileY", this.tileY);
      var1.addBoolean("isLocked", this.isLocked);
   }

   public boolean isValidBed() {
      return isValidBed(this.data.getLevel(), this.tileX, this.tileY);
   }

   public static boolean isValidBed(Level var0, int var1, int var2) {
      GameObject var3 = var0.getObject(var1, var2);
      return var3 instanceof BedObject;
   }

   public SettlementRoom getRoom() {
      return this.data.rooms.getRoom(this.tileX, this.tileY);
   }

   public LevelSettler getSettler() {
      return this.settler;
   }

   public boolean clearSettler() {
      if (this.settler != null) {
         if (this.settler.getBed() == this) {
            this.settler.assignBed((SettlementBed)null);
         }

         this.settler = null;
         this.data.sendEvent(SettlementSettlersChangedEvent.class);
         return true;
      } else {
         return false;
      }
   }
}
