package necesse.level.maps.levelData;

import necesse.engine.GameState;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.level.maps.Level;

public class LevelData implements GameState, WorldEntityGameClock {
   public final IDData idData = new IDData();
   protected Level level;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public LevelData() {
      LevelDataRegistry.instance.applyIDData(this.getClass(), this.idData);
   }

   public boolean shouldSave() {
      return true;
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("stringID", this.getStringID());
   }

   public void applyLoadData(LoadData var1) {
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public Level getLevel() {
      return this.level;
   }

   public WorldEntity getWorldEntity() {
      return this.level == null ? null : this.level.getWorldEntity();
   }

   public boolean isClient() {
      return this.level != null && this.level.isClient();
   }

   public Client getClient() {
      return this.level == null ? null : this.level.getClient();
   }

   public boolean isServer() {
      return this.level != null && this.level.isServer();
   }

   public Server getServer() {
      return this.level == null ? null : this.level.getServer();
   }

   public void tick() {
   }
}
