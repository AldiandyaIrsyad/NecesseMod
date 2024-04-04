package necesse.level.maps.layers;

import java.util.Collections;
import java.util.List;
import necesse.apiDoc.APIDocInclude;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.regionSystem.Region;

@APIDocInclude
public abstract class LevelLayer {
   public final IDData idData = new IDData();
   public final Level level;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public LevelLayer(Level var1) {
      this.level = var1;
   }

   public abstract void init();

   public abstract void onLoadingComplete();

   public void frameTick(TickManager var1) {
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void simulateWorld(long var1, boolean var3) {
   }

   public void tickTile(int var1, int var2) {
   }

   public void tickTileEffect(GameCamera var1, PlayerMob var2, int var3, int var4) {
   }

   public void onWireUpdate(int var1, int var2, int var3, boolean var4) {
   }

   public List<LevelJob> getLevelJobs(int var1, int var2) {
      return Collections.emptyList();
   }

   public void writeLevelDataPacket(PacketWriter var1) {
   }

   public void readLevelDataPacket(PacketReader var1) {
   }

   public abstract void writeRegionPacket(Region var1, PacketWriter var2);

   public abstract boolean readRegionPacket(Region var1, PacketReader var2);

   public abstract void unloadRegion(Region var1);

   public abstract void addSaveData(SaveData var1);

   public abstract void loadSaveData(LoadData var1);
}
