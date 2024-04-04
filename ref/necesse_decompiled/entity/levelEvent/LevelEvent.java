package necesse.entity.levelEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import necesse.engine.GameState;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.entity.Entity;
import necesse.entity.levelEvent.actions.LevelEventAction;
import necesse.entity.levelEvent.actions.LevelEventActionRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class LevelEvent implements RegionPositionGetter, GameState, WorldEntityGameClock {
   public final IDData idData;
   public boolean shouldSave;
   private int uniqueID;
   private boolean isOver;
   public Level level;
   private LevelEventActionRegistry actions;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   protected LevelEvent(boolean var1) {
      this.idData = new IDData();
      this.actions = new LevelEventActionRegistry(this);
      if (var1) {
         LevelEventRegistry.instance.applyIDData(this.getClass(), this.idData);
      }

   }

   protected LevelEvent() {
      this(true);
   }

   public boolean isNetworkImportant() {
      return false;
   }

   public boolean shouldSendOverPacket() {
      return this.isNetworkImportant();
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("uniqueID", this.getUniqueID());
   }

   public void applyLoadData(LoadData var1) {
      this.setUniqueID(var1.getInt("uniqueID", this.getRealUniqueID()));
   }

   public void applySpawnPacket(PacketReader var1) {
      this.setUniqueID(var1.getNextInt());
   }

   public void setupSpawnPacket(PacketWriter var1) {
      var1.putNextInt(this.getUniqueID());
   }

   public void init() {
      this.actions.closeRegistry();
   }

   public void tickMovement(float var1) {
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, LevelDrawUtils.DrawArea var4, Level var5, TickManager var6, GameCamera var7) {
   }

   public Level getLevel() {
      return this.level;
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.emptyList();
   }

   public void setUniqueID(int var1) {
      this.uniqueID = var1;
   }

   public int getUniqueID(GameRandom var1) {
      if (this.uniqueID == 0) {
         this.uniqueID = Entity.getNewUniqueID(this.level, var1);
      }

      return this.uniqueID;
   }

   public int getUniqueID() {
      return this.getUniqueID((GameRandom)null);
   }

   public int getRealUniqueID() {
      return this.uniqueID;
   }

   public int resetUniqueID(GameRandom var1) {
      this.uniqueID = 0;
      return this.getUniqueID(var1);
   }

   public final void runAction(int var1, PacketReader var2) {
      this.actions.runAction(var1, var2);
   }

   public <T extends LevelEventAction> T registerAction(T var1) {
      return this.actions.registerAction(var1);
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

   public void over() {
      this.isOver = true;
   }

   public final boolean isOver() {
      return this.isOver;
   }

   public void onLoadingComplete() {
   }

   public void onUnloading() {
   }

   public void onDispose() {
   }
}
