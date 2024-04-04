package necesse.level.maps;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class LevelObject {
   public final GameObject object;
   public final byte rotation;
   public final Level level;
   public final int tileX;
   public final int tileY;

   private LevelObject(Level var1, int var2, int var3, GameObject var4, byte var5) {
      this.object = var4;
      this.rotation = var5;
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
   }

   public LevelObject(Level var1, int var2, int var3) {
      this(var1, var2, var3, var1.getObject(var2, var3), var1.getObjectRotation(var2, var3));
   }

   public static LevelObject custom(Level var0, int var1, int var2, GameObject var3, byte var4) {
      return new LevelObject(var0, var1, var2, var3, var4);
   }

   public MultiTile getMultiTile() {
      return this.object.getMultiTile(this.level, this.tileX, this.tileY);
   }

   public boolean hasChanged() {
      return this.level.getObjectID(this.tileX, this.tileY) != this.object.getID() || this.level.getObjectRotation(this.tileX, this.tileY) != this.rotation;
   }

   public ObjectEntity getObjectEntity() {
      return this.level.entityManager.getObjectEntity(this.tileX, this.tileY);
   }

   public void drawPreview(byte var1, float var2, PlayerMob var3, GameCamera var4) {
      this.object.drawPreview(this.level, this.tileX, this.tileY, var1, var2, var3, var4);
   }

   public List<Rectangle> getCollisions(int var1) {
      return this.object.getCollisions(this.level, this.tileX, this.tileY, var1);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes() {
      return this.object.getHoverHitboxes(this.level, this.tileX, this.tileY);
   }

   public List<Rectangle> getProjectileCollisions(int var1) {
      return this.object.getProjectileCollisions(this.level, this.tileX, this.tileY, var1);
   }

   public List<Rectangle> getAttackThroughCollisions() {
      return this.object.getAttackThroughCollisions(this.level, this.tileX, this.tileY);
   }

   public int getLightLevel() {
      return this.object.getLightLevel(this.level, this.tileX, this.tileY);
   }

   public GameLight getLight() {
      return this.object.getLight(this.level, this.tileX, this.tileY);
   }

   public String canPlace(int var1) {
      return this.object.canPlace(this.level, this.tileX, this.tileY, var1);
   }

   public void attemptPlace(PlayerMob var1, String var2) {
      this.object.attemptPlace(this.level, this.tileX, this.tileY, var1, var2);
   }

   public boolean checkPlaceCollision(int var1, boolean var2) {
      return this.object.checkPlaceCollision(this.level, this.tileX, this.tileY, var1, var2);
   }

   public boolean isValid() {
      return this.object.isValid(this.level, this.tileX, this.tileY);
   }

   public void checkAround() {
      this.object.checkAround(this.level, this.tileX, this.tileY);
   }

   public boolean isSolid() {
      return this.object.isSolid(this.level, this.tileX, this.tileY);
   }

   public boolean canInteract(PlayerMob var1) {
      return this.object.canInteract(this.level, this.tileX, this.tileY, var1);
   }

   public String getInteractTip(PlayerMob var1, boolean var2) {
      return this.object.getInteractTip(this.level, this.tileX, this.tileY, var1, var2);
   }

   public void onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      this.object.onMouseHover(this.level, this.tileX, this.tileY, var1, var2, var3);
   }

   public boolean inInteractRange(PlayerMob var1) {
      return this.object.inInteractRange(this.level, this.tileX, this.tileY, var1);
   }

   public void interact(PlayerMob var1) {
      this.object.interact(this.level, this.tileX, this.tileY, var1);
   }

   public ObjectEntity getNewObjectEntity() {
      return this.object.getNewObjectEntity(this.level, this.tileX, this.tileY);
   }

   public ObjectEntity getCurrentObjectEntity() {
      return this.object.getCurrentObjectEntity(this.level, this.tileX, this.tileY);
   }

   public <T extends ObjectEntity> T getCurrentObjectEntity(Class<T> var1) {
      return this.object.getCurrentObjectEntity(this.level, this.tileX, this.tileY, var1);
   }

   public void onObjectDestroyed(ServerClient var1, ArrayList<ItemPickupEntity> var2) {
      this.object.onDestroyed(this.level, this.tileX, this.tileY, var1, var2);
   }

   public void tick(Mob var1) {
      this.object.tick(var1, this.level, this.tileX, this.tileY);
   }

   public void tick() {
      this.object.tick(this.level, this.tileX, this.tileY);
   }

   public boolean isWireActive(int var1) {
      return this.object.isWireActive(this.level, this.tileX, this.tileY, var1);
   }

   public void onDamaged(int var1, ServerClient var2, boolean var3, int var4, int var5) {
      this.object.onDamaged(this.level, this.tileX, this.tileY, var1, var2, var3, var4, var5);
   }

   public void attackThrough(GameDamage var1, Attacker var2) {
      this.object.attackThrough(this.level, this.tileX, this.tileY, var1, var2);
   }

   public void attackThrough(GameDamage var1) {
      this.object.attackThrough(this.level, this.tileX, this.tileY, var1);
   }

   public GameTooltips getMapTooltips() {
      return this.object.getMapTooltips(this.level, this.tileX, this.tileY);
   }

   public boolean isStillPresent(boolean var1) {
      return this.level.getObjectID(this.tileX, this.tileY) == this.object.getID() && (!var1 || this.level.getObjectRotation(this.tileX, this.tileY) == this.rotation);
   }

   public String toString() {
      return super.toString() + "{" + this.tileX + "x" + this.tileY + ", " + this.level.getHostString() + ", " + this.object.getDisplayName() + "}";
   }
}
