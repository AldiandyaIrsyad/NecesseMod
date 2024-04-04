package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;

public class WaystoneObjectEntity extends ObjectEntity {
   public Point homeIsland;

   public WaystoneObjectEntity(Level var1, int var2, int var3) {
      super(var1, "waystone", var2, var3);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.homeIsland != null) {
         var1.addPoint("homeIsland", this.homeIsland);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.homeIsland = var1.getPoint("homeIsland", (Point)null, false);
   }
}
