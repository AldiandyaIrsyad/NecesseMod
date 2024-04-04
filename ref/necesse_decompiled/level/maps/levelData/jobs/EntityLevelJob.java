package necesse.level.maps.levelData.jobs;

import necesse.engine.save.LoadData;
import necesse.entity.Entity;

public class EntityLevelJob<T extends Entity> extends LevelJob {
   public T target;

   public EntityLevelJob(T var1) {
      super(var1.getTileX(), var1.getTileY());
      this.target = var1;
   }

   public EntityLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isValid() {
      if (this.isRemoved()) {
         return false;
      } else {
         return !this.target.removed();
      }
   }
}
