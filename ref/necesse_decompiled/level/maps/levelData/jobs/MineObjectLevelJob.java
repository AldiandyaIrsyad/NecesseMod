package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import necesse.engine.save.LoadData;
import necesse.inventory.InventoryItem;
import necesse.level.maps.LevelObject;

public abstract class MineObjectLevelJob extends LevelJob {
   public MineObjectLevelJob(int var1, int var2) {
      super(var1, var2);
   }

   public MineObjectLevelJob(LoadData var1) {
      super(var1);
   }

   public ArrayList<InventoryItem> getDroppedItems() {
      return this.getLevel().getObject(this.tileX, this.tileY).getDroppedItems(this.getLevel(), this.tileX, this.tileY);
   }

   public boolean isValid() {
      return this.isValidObject(this.getObject());
   }

   public LevelObject getObject() {
      return this.getLevel().getLevelObject(this.tileX, this.tileY);
   }

   public abstract boolean isValidObject(LevelObject var1);
}
