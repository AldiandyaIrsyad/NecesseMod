package necesse.engine.world.worldEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IDData;
import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;

public class WorldEvent {
   public final IDData idData = new IDData();
   public boolean shouldSave;
   private boolean isOver;
   public WorldEntity world;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public WorldEvent() {
      WorldEventRegistry.instance.applyIDData(this.getClass(), this.idData);
   }

   public void addSaveData(SaveData var1) {
   }

   public void applyLoadData(LoadData var1) {
   }

   public void applySpawnPacket(PacketReader var1) {
   }

   public void setupSpawnPacket(PacketWriter var1) {
   }

   public void init() {
   }

   public void tickMovement(float var1) {
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void over() {
      this.isOver = true;
   }

   public boolean isOver() {
      return this.isOver;
   }
}
