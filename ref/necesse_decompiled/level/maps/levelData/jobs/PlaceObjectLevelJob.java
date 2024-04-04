package necesse.level.maps.levelData.jobs;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.level.gameObject.GameObject;

public abstract class PlaceObjectLevelJob extends LevelJob {
   public final int objectID;
   public final int objectRotation;

   public PlaceObjectLevelJob(int var1, int var2, int var3, int var4) {
      super(var1, var2);
      this.objectID = var3;
      this.objectRotation = var4;
   }

   public PlaceObjectLevelJob(int var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   public PlaceObjectLevelJob(LoadData var1) {
      super(var1);
      String var2 = var1.getUnsafeString("objectStringID");
      this.objectID = ObjectRegistry.getObjectID(var2);
      if (this.objectID == -1) {
         throw new LoadDataException("Could not find object with stringID \"" + var2 + "\"");
      } else {
         this.objectRotation = var1.getInt("objectRotation");
      }
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addUnsafeString("objectStringID", this.getObject().getStringID());
      var1.addInt("objectRotation", this.objectRotation);
   }

   public boolean isValid() {
      GameObject var1 = this.getObject();
      return var1.canPlace(this.getLevel(), this.tileX, this.tileY, this.objectRotation) == null;
   }

   public GameObject getObject() {
      return ObjectRegistry.getObject(this.objectID);
   }

   public boolean canPlaceCollision() {
      return !this.getObject().checkPlaceCollision(this.getLevel(), this.tileX, this.tileY, this.objectRotation, true);
   }
}
