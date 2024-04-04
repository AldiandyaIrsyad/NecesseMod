package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class ObjectEntity extends Entity {
   public String type;
   public boolean shouldSave;
   private boolean oeInventory = this instanceof OEInventory;
   private boolean oeUsers = this instanceof OEUsers;

   public ObjectEntity(Level var1, String var2, int var3, int var4) {
      this.setLevel(var1);
      this.type = var2;
      this.x = (float)var3;
      this.y = (float)var4;
      this.shouldSave = true;
   }

   public void addSaveData(SaveData var1) {
   }

   public void applyLoadData(LoadData var1) {
   }

   public void setupContentPacket(PacketWriter var1) {
   }

   public void applyContentPacket(PacketReader var1) {
   }

   public boolean shouldRequestPacket() {
      return true;
   }

   public void init() {
      super.init();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void frameTick(float var1) {
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public ArrayList<InventoryItem> getDroppedItems() {
      ArrayList var1 = new ArrayList();
      return var1;
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
   }

   public boolean implementsOEInventory() {
      return this.oeInventory;
   }

   public boolean implementsOEUsers() {
      return this.oeUsers;
   }

   public boolean shouldSave() {
      return this.shouldSave;
   }

   public GameObject getObject() {
      return this.getLevel().getObject(this.getTileX(), this.getTileY());
   }

   public LevelObject getLevelObject() {
      return this.getLevel().getLevelObject(this.getTileX(), this.getTileY());
   }

   public int getTileX() {
      return this.getX();
   }

   public int getTileY() {
      return this.getY();
   }

   public Point getTilePoint() {
      return new Point(this.getX(), this.getY());
   }

   public Point getMapPos() {
      return new Point(this.getX() * 32 + 16, this.getY() * 32 + 16);
   }

   public float getSoundPositionX() {
      return (float)(this.getX() * 32 + 16);
   }

   public float getSoundPositionY() {
      return (float)(this.getY() * 32 + 16);
   }

   public void onObjectDestroyed(GameObject var1, ServerClient var2, ArrayList<ItemPickupEntity> var3) {
   }

   public String toString() {
      return super.toString() + "{" + this.getUniqueID() + ", " + this.getTileX() + "x" + this.getTileY() + ", " + this.getHostString() + ", " + this.getObject().getDisplayName() + "}";
   }
}
