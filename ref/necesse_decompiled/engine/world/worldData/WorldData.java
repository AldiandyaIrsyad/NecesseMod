package necesse.engine.world.worldData;

import necesse.engine.registries.IDData;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;

public class WorldData {
   public final IDData idData = new IDData();
   protected WorldEntity worldEntity;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public WorldData() {
      WorldDataRegistry.instance.applyIDData(this.getClass(), this.idData);
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("stringID", this.getStringID());
   }

   public void applyLoadData(LoadData var1) {
   }

   public void setWorldEntity(WorldEntity var1) {
      this.worldEntity = var1;
   }

   public WorldEntity getWorldEntity() {
      return this.worldEntity;
   }

   public void tick() {
   }
}
