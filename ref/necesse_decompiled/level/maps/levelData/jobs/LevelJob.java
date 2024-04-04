package necesse.level.maps.levelData.jobs;

import necesse.engine.GameState;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public abstract class LevelJob implements GameState, WorldEntityGameClock {
   public final IDData idData = new IDData();
   public final int tileX;
   public final int tileY;
   public int sameTypePriority;
   private Level level;
   private GameLinkedList<LevelJob>.Element element;
   public GameObjectReservable reservable = new GameObjectReservable();

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public LevelJob(int var1, int var2) {
      LevelJobRegistry.instance.applyIDData(this.getClass(), this.idData);
      this.tileX = var1;
      this.tileY = var2;
   }

   public LevelJob(LoadData var1) {
      LevelJobRegistry.instance.applyIDData(this.getClass(), this.idData);
      this.tileX = var1.getInt("tileX");
      this.tileY = var1.getInt("tileY");
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("tileX", this.tileX);
      var1.addInt("tileY", this.tileY);
   }

   public final LevelJob init(Level var1, GameLinkedList<LevelJob>.Element var2) {
      this.level = var1;
      this.element = var2;
      return this;
   }

   public boolean isWithinRestrictZone(ZoneTester var1) {
      return var1.containsTile(this.tileX, this.tileY);
   }

   public void remove() {
      if (this.element != null && !this.element.isRemoved()) {
         this.element.remove();
      }

   }

   public boolean isRemoved() {
      return this.element != null && this.element.isRemoved();
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

   public boolean shouldSave() {
      return true;
   }

   public boolean isValid() {
      return true;
   }

   protected boolean isSameTile(LevelJob var1) {
      return this.tileX == var1.tileX && this.tileY == var1.tileY;
   }

   public boolean isSameJob(LevelJob var1) {
      return this.isSameTile(var1) && this.getID() == var1.getID();
   }

   public int getSameJobPriority() {
      return 0;
   }

   public int getSameTypePriority() {
      return this.sameTypePriority;
   }

   public int getFirstPriority() {
      return 0;
   }

   public boolean prioritizeForSameJobAgain() {
      return false;
   }

   public String toString() {
      return super.toString() + "{" + this.tileX + ", " + this.tileY + "}";
   }
}
