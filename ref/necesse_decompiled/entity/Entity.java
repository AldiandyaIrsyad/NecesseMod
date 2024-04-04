package necesse.entity;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import necesse.engine.GameState;
import necesse.engine.GlobalData;
import necesse.engine.WorldSettingsGetter;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.chains.ChainLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.regionSystem.RegionPosition;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public abstract class Entity implements SoundEmitter, ChainLocation, RegionPositionGetter, GameState, WorldSettingsGetter, WorldEntityGameClock {
   public static final float SPEED_DELTA_DIV = 250.0F;
   public static final float SPEED_DELTA_DIV_SQRT = (float)Math.sqrt(250.0);
   private boolean isInitialized;
   private boolean removed;
   private boolean disposed;
   public float x;
   public float y;
   private Point lastRegionPos;
   private Level level;
   private WorldEntity worldEntity;
   private WorldSettings worldSettings;
   private long clientUpdateTime;
   private int uniqueID;
   private boolean isDirty;
   private GameLinkedList<? super Entity>.Element regionElement;
   private final Object regionLock = new Object();

   public Entity() {
   }

   public void updateRegion(GameLinkedList<? super Entity> var1) {
      if (!this.removed()) {
         synchronized(this.regionLock) {
            if (this.regionElement != null) {
               this.regionElement.remove();
               this.regionElement = null;
            }

            if (var1 != null) {
               this.regionElement = var1.addLast(this);
            }

         }
      }
   }

   public abstract void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8);

   public boolean shouldDrawOnMap() {
      return false;
   }

   public boolean isVisibleOnMap(Client var1, LevelMap var2) {
      return GlobalData.debugCheatActive() || var2.isTileKnown(this.getTileX(), this.getTileY());
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-8, -8, 16, 16);
   }

   public Point getMapPos() {
      return new Point(this.getX(), this.getY());
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
   }

   public GameTooltips getMapTooltips() {
      return null;
   }

   public String getMapInteractTooltip() {
      return null;
   }

   public void onMapInteract(InputEvent var1, PlayerMob var2) {
   }

   public void init() {
      this.isInitialized = true;
   }

   public void postInit() {
   }

   public void onLevelChanged() {
   }

   public abstract void clientTick();

   public abstract void serverTick();

   public void updateRegionPos() {
      Point var1 = this.getLevel().regionManager.getRegionPosByTile(this.getTileX(), this.getTileY()).point();
      if (this.lastRegionPos == null) {
         this.lastRegionPos = var1;
      } else if (var1.x != this.lastRegionPos.x || var1.y != this.lastRegionPos.y) {
         this.onRegionChanged(this.lastRegionPos, var1);
         this.lastRegionPos = var1;
      }

   }

   public void onRegionChanged(Point var1, Point var2) {
      if (this.isClient() && !this.checkIfOccupyingRegions((var1x) -> {
         return this.getClient().levelManager.isRegionLoaded(var1x.regionX, var1x.regionY);
      })) {
         this.remove();
      }

   }

   public Level getLevel() {
      return this.level;
   }

   public WorldEntity getWorldEntity() {
      if (this.worldEntity != null) {
         return this.worldEntity;
      } else {
         return this.level != null ? this.level.getWorldEntity() : null;
      }
   }

   public WorldSettings getWorldSettings() {
      if (this.worldSettings != null) {
         return this.worldSettings;
      } else {
         return this.level != null ? this.level.getWorldSettings() : null;
      }
   }

   public void setWorldData(WorldEntity var1, WorldSettings var2) {
      if (var1 != null) {
         this.worldEntity = var1;
      }

      if (var2 != null) {
         this.worldSettings = var2;
      }

   }

   public void setLevel(Level var1) {
      this.level = var1;
      if (var1 != null) {
         this.setWorldData(var1.getWorldEntity(), var1.getWorldSettings());
      }

   }

   public void refreshClientUpdateTime() {
      WorldEntity var1 = this.getWorldEntity();
      if (var1 != null) {
         this.clientUpdateTime = var1.getLocalTime();
      }

   }

   public long getTimeSinceClientUpdate() {
      WorldEntity var1 = this.getWorldEntity();
      return var1 != null ? var1.getLocalTime() - this.clientUpdateTime : 0L;
   }

   public int getUniqueID(GameRandom var1) {
      if (this.uniqueID == 0) {
         this.uniqueID = getNewUniqueID(this.level, var1);
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

   public int resetUniqueID() {
      return this.resetUniqueID((GameRandom)null);
   }

   public void setUniqueID(int var1) {
      this.uniqueID = var1;
   }

   public int getX() {
      return (int)this.x;
   }

   public int getY() {
      return (int)this.y;
   }

   public int getTileX() {
      return this.getX() / 32;
   }

   public int getTileY() {
      return this.getY() / 32;
   }

   public Point getPositionPoint() {
      return new Point(this.getX(), this.getY());
   }

   public Point getTilePoint() {
      return new Point(this.getTileX(), this.getTileY());
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.getLevel().regionManager.getRegionPosByTile(this.getTileX(), this.getTileY()));
   }

   public void setX(int var1) {
      this.x = (float)var1;
   }

   public void setY(int var1) {
      this.y = (float)var1;
   }

   public void onLoadingComplete() {
   }

   public void onUnloading() {
   }

   public void dispose() {
      this.disposed = true;
   }

   public boolean isDisposed() {
      return this.disposed;
   }

   public void remove() {
      if (!this.removed) {
         synchronized(this.regionLock) {
            if (this.regionElement != null) {
               this.regionElement.remove();
               this.regionElement = null;
            }
         }
      }

      this.removed = true;
   }

   public boolean removed() {
      return this.removed;
   }

   public void onRemovedFromManager() {
   }

   public void restore() {
      this.removed = false;
   }

   public float getSoundPositionX() {
      return this.x;
   }

   public float getSoundPositionY() {
      return this.y;
   }

   public Point getDrawPos() {
      return new Point(this.getDrawX(), this.getDrawY());
   }

   public int getDrawX() {
      return (int)this.x;
   }

   public int getDrawY() {
      return (int)this.y;
   }

   public boolean shouldSave() {
      return true;
   }

   public static int getNewUniqueID(Level var0, GameRandom var1) {
      int var2 = GameRandom.getNewUniqueID(var1);
      if (var0 != null) {
         while(var0.entityManager.uniqueIDOccupied(var2)) {
            var2 = GameRandom.getNewUniqueID(var1);
         }
      }

      return var2;
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public void markClean() {
      this.isDirty = false;
   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public boolean isSamePlace(Entity var1) {
      return this.getLevel() != null && var1.getLevel() != null ? this.getLevel().isSamePlace(var1.getLevel()) : false;
   }

   /** @deprecated */
   @Deprecated
   public boolean isClientLevel() {
      return this.isClient();
   }

   public boolean isClient() {
      return this.level != null && this.level.isClient();
   }

   public Client getClient() {
      return this.level == null ? null : this.level.getClient();
   }

   /** @deprecated */
   @Deprecated
   public boolean isServerLevel() {
      return this.isServer();
   }

   public boolean isServer() {
      return this.level != null && this.level.isServer();
   }

   public Server getServer() {
      return this.level == null ? null : this.level.getServer();
   }

   public boolean isInitialized() {
      return this.isInitialized;
   }

   public static float getTravelTimeMillis(float var0, float var1) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         float var2 = var0 / 250.0F;
         return var1 / var2;
      }
   }

   public static float getPositionAfterMillis(float var0, float var1) {
      return var0 * var1 / 250.0F;
   }
}
