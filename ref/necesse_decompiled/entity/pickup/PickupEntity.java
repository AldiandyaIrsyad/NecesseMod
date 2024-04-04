package necesse.entity.pickup;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.packet.PacketPickupEntityTarget;
import necesse.engine.network.packet.PacketRequestPickupEntity;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.PickupRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public abstract class PickupEntity extends Entity {
   public final IDData idData;
   public float dx;
   public float dy;
   public long spawnTime;
   public int pickupCooldown;
   public HashMap<Long, Integer> authPickupCooldown;
   private NetworkClient target;
   private long reservedAuth;
   protected int targetUpdateCooldown;
   protected long targetUpdateTime;
   public float bouncy;
   float sinking;
   public Rectangle collisionBox;
   public Rectangle selectionBox;
   public float targetRange;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public PickupEntity() {
      this.idData = new IDData();
      this.authPickupCooldown = new HashMap();
      this.targetUpdateCooldown = 20000;
      this.bouncy = 0.0F;
      this.sinking = 0.0F;
      this.targetRange = 60.0F;
      PickupRegistry.instance.applyIDData(this.getClass(), this.idData);
      this.collisionBox = new Rectangle(-8, -8, 16, 16);
      this.selectionBox = new Rectangle(-12, -12, 24, 24);
   }

   public PickupEntity(Level var1, float var2, float var3, float var4, float var5) {
      this();
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.dx = var4;
      this.dy = var5;
      this.spawnTime = this.getWorldEntity().getTime();
      this.pickupCooldown = 500;
      this.reservedAuth = -1L;
      this.collisionBox = new Rectangle(-8, -8, 16, 16);
      this.selectionBox = new Rectangle(-12, -12, 24, 24);
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("uniqueID", this.getUniqueID());
      var1.addFloat("x", this.x);
      var1.addFloat("y", this.y);
      var1.addFloat("dx", this.dx);
      var1.addFloat("dy", this.dy);
      var1.addLong("spawnTime", this.spawnTime);
      var1.addInt("pickupCooldown", this.pickupCooldown);
      var1.addLong("reservedAuth", this.reservedAuth);
   }

   public void applyLoadData(LoadData var1) {
      this.setUniqueID(var1.getInt("uniqueID", 0));
      this.x = var1.getFloat("x", this.x);
      this.y = var1.getFloat("y", this.y);
      this.dx = var1.getFloat("dx", this.dx);
      this.dy = var1.getFloat("dy", this.dy);
      this.spawnTime = var1.getLong("spawnTime", this.spawnTime);
      this.pickupCooldown = var1.getInt("pickupCooldown", this.pickupCooldown);
      this.reservedAuth = var1.getLong("reservedAuth", this.reservedAuth);
   }

   public boolean shouldSendSpawnPacket() {
      return this.getID() != -1;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      var1.putNextInt(this.getUniqueID());
      var1.putNextFloat(this.x);
      var1.putNextFloat(this.y);
      var1.putNextFloat(this.dx);
      var1.putNextFloat(this.dy);
      var1.putNextLong(this.reservedAuth);
      var1.putNextLong(this.spawnTime);
      var1.putNextInt(this.pickupCooldown);
      var1.putNextFloat(this.sinking);
      this.writeTargetUpdatePacket(var1, false);
   }

   public void applySpawnPacket(PacketReader var1) {
      this.refreshClientUpdateTime();
      this.setUniqueID(var1.getNextInt());
      this.x = var1.getNextFloat();
      this.y = var1.getNextFloat();
      this.dx = var1.getNextFloat();
      this.dy = var1.getNextFloat();
      this.setReservedAuth(var1.getNextLong());
      this.spawnTime = var1.getNextLong();
      this.pickupCooldown = var1.getNextInt();
      this.sinking = var1.getNextFloat();
      this.readTargetUpdatePacket(var1, false);
   }

   public void writeTargetUpdatePacket(PacketWriter var1, boolean var2) {
      if (this.target == null) {
         var1.putNextByteUnsigned(0);
      } else {
         var1.putNextByteUnsigned(1);
         var1.putNextByteUnsigned(this.target.slot);
      }

      if (var2) {
         var1.putNextFloat(this.x);
         var1.putNextFloat(this.y);
         var1.putNextFloat(this.dx);
         var1.putNextFloat(this.dy);
      }

   }

   public void readTargetUpdatePacket(PacketReader var1, boolean var2) {
      this.refreshClientUpdateTime();
      int var3 = var1.getNextByteUnsigned();
      if (var3 == 0) {
         this.resetTarget();
      } else {
         int var4 = var1.getNextByteUnsigned();
         if (this.isClient()) {
            this.setTarget(this.getLevel().getClient().getClient(var4));
         } else if (this.isServer()) {
            this.setTarget(this.getLevel().getServer().getClient(var4));
         }
      }

      if (var2) {
         this.x = var1.getNextFloat();
         this.y = var1.getNextFloat();
         this.dx = var1.getNextFloat();
         this.dy = var1.getNextFloat();
      }

   }

   public void sendTargetUpdatePacket() {
      this.targetUpdateTime = this.getWorldEntity().getTime();
      if (this.isServer()) {
         this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityTarget(this), this);
      }

   }

   public void init() {
      super.init();
   }

   public void moveX(float var1) {
      this.x += this.dx * var1 / 250.0F;
   }

   public void moveY(float var1) {
      this.y += this.dy * var1 / 250.0F;
   }

   public void clientTick() {
      if (!this.removed()) {
         if (this.getTimeSinceClientUpdate() >= (long)(this.targetUpdateCooldown + 10000)) {
            this.refreshClientUpdateTime();
            this.requestServerUpdate();
         }

         float var1 = this.getSinkingRate();
         this.sinking = GameMath.limit(this.sinking + var1, 0.0F, this.getMaxSinking());
         if (this.sinking >= 1.0F) {
            this.remove();
         }

      }
   }

   public void requestServerUpdate() {
      if (this.isClient()) {
         GameLog.debug.println("Client requesting update for pickup " + this);
         this.getLevel().getClient().network.sendPacket(new PacketRequestPickupEntity(this.getUniqueID()));
      }

   }

   public float getTargetRange(ServerClient var1) {
      return this.targetRange;
   }

   public float getTargetStreamRange() {
      return this.targetRange;
   }

   public float getSinkingRate() {
      return 0.0F;
   }

   public float getMaxSinking() {
      return 1.0F;
   }

   public long getTimeSinceSpawned() {
      return this.getWorldEntity().getTime() - this.spawnTime;
   }

   public long getLifespanMillis() {
      return 600000L;
   }

   public boolean isOnPickupCooldown() {
      return this.getTimeSinceSpawned() <= (long)this.pickupCooldown;
   }

   public boolean isOnPickupCooldown(long var1) {
      return this.getTimeSinceSpawned() <= (long)(Integer)this.authPickupCooldown.getOrDefault(var1, 0);
   }

   public void serverTick() {
      if (!this.removed()) {
         long var1 = this.getLifespanMillis();
         if (var1 > 0L && this.getTimeSinceSpawned() >= var1) {
            this.remove();
         } else {
            float var3 = this.getSinkingRate();
            this.sinking = GameMath.limit(this.sinking + var3, 0.0F, this.getMaxSinking());
            if (this.sinking >= 1.0F) {
               this.remove();
            } else {
               this.checkCollision();
               if (!this.removed()) {
                  if (this.target == null && !this.isOnPickupCooldown()) {
                     this.getLevel().entityManager.players.streamArea(this.x, this.y, (int)this.getTargetStreamRange()).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).filter((var1x) -> {
                        return this.reservedAuth == -1L || var1x.authentication == this.reservedAuth;
                     }).filter((var1x) -> {
                        return !this.isOnPickupCooldown(var1x.authentication);
                     }).map((var1x) -> {
                        return new ComputedObjectValue(var1x, () -> {
                           return GameMath.squareDistance(this.x, this.y, var1x.playerMob.x, var1x.playerMob.y);
                        });
                     }).filter((var1x) -> {
                        return (Float)var1x.get() <= this.getTargetRange((ServerClient)var1x.object);
                     }).findBestDistance(0, Comparator.comparingDouble(ComputedValue::get)).filter((var1x) -> {
                        return this.isValidTarget((ServerClient)var1x.object);
                     }).ifPresent((var1x) -> {
                        this.setTarget((ServerClient)var1x.object);
                     });
                  }

                  if (this.targetUpdateTime + (long)this.targetUpdateCooldown < this.getWorldEntity().getTime()) {
                     this.sendTargetUpdatePacket();
                  }

                  if (this.target != null) {
                     if (this.target.playerMob != null && this.target.isServer() && this.isValidTarget(this.target.getServerClient())) {
                        if (this.collidesWith(this.target.getServerClient())) {
                           this.collidedWith(this.target.getServerClient());
                        }
                     } else {
                        this.resetTarget();
                        this.sendTargetUpdatePacket();
                     }
                  }

               }
            }
         }
      }
   }

   public boolean isValidTarget(ServerClient var1) {
      return true;
   }

   public void tickMovement(float var1) {
      if (!this.removed()) {
         this.calcAcceleration(var1);
         if (this.target != null && this.target.playerMob != null) {
            Point2D.Float var2 = new Point2D.Float((float)(this.target.playerMob.getX() - this.getX()), (float)(this.target.playerMob.getY() - this.getY()));
            float var3 = (float)var2.distance(0.0, 0.0);
            if (var3 <= 0.0F || Float.isNaN(var3)) {
               var3 = 1.0F;
            }

            float var4 = var2.x / var3;
            float var5 = var2.y / var3;
            Point2D.Float var6 = new Point2D.Float(var4, var5);
            this.dx = var6.x * Math.max(70.0F, var3 / 2.0F);
            this.dy = var6.y * Math.max(70.0F, var3 / 2.0F);
         }

         if (this.dx != 0.0F) {
            this.moveX(var1);
            if (this.target == null && this.getLevel().collides((Shape)this.getCollision(), (CollisionFilter)(new CollisionFilter()).mobCollision())) {
               this.moveX(-var1);
               this.dx = -this.dx * this.bouncy;
            }
         }

         if (this.dy != 0.0F) {
            this.moveY(var1);
            if (this.target == null && this.getLevel().collides((Shape)this.getCollision(), (CollisionFilter)(new CollisionFilter()).mobCollision())) {
               this.moveY(-var1);
               this.dy = -this.dy * this.bouncy;
            }
         }

         if (Math.abs(this.dx) < 0.01F) {
            this.dx = 0.0F;
         }

         if (Math.abs(this.dy) < 0.01F) {
            this.dy = 0.0F;
         }

      }
   }

   public void setReservedAuth(long var1) {
      this.reservedAuth = var1;
   }

   public long getReservedAuth() {
      return this.reservedAuth;
   }

   public void calcAcceleration(float var1) {
      float var2 = 2.0F;
      if (this.dx != 0.0F) {
         this.dx += (0.0F - var2 * this.dx) * var1 / 250.0F;
      }

      if (this.dy != 0.0F) {
         this.dy += (0.0F - var2 * this.dy) * var1 / 250.0F;
      }

   }

   public boolean shouldDraw() {
      if (GlobalData.debugCheatActive()) {
         return true;
      } else {
         long var1 = GameAuth.getAuthentication();
         return this.reservedAuth == -1L || this.reservedAuth == var1;
      }
   }

   public boolean inLiquid() {
      return this.inLiquid(this.getX(), this.getY());
   }

   public boolean inLiquid(int var1, int var2) {
      return this.getLevel() != null && this.getLevel().inLiquid(var1, var2);
   }

   public int getBobbing() {
      return this.getBobbing(this.getX(), this.getY());
   }

   public int getBobbing(int var1, int var2) {
      return !this.inLiquid(var1, var2) ? 0 : this.getLevel().getLevelTile(var1 / 32, var2 / 32).getLiquidBobbing();
   }

   public Rectangle getCollision() {
      return new Rectangle((int)((double)this.x + this.collisionBox.getX()), (int)((double)this.y + this.collisionBox.getY()), (int)this.collisionBox.getWidth(), (int)this.collisionBox.getHeight());
   }

   public Rectangle getSelectBox() {
      return new Rectangle((int)((double)this.x + this.selectionBox.getX()), (int)((double)this.y + this.selectionBox.getY()), (int)this.selectionBox.getWidth(), (int)this.selectionBox.getHeight());
   }

   public boolean collidesWith(ServerClient var1) {
      return this.getCollision().intersects(var1.playerMob.getCollision());
   }

   public boolean collidesWith(PickupEntity var1) {
      return this.getCollision().intersects(var1.getCollision());
   }

   public void collidedWith(ServerClient var1) {
      this.onPickup(var1);
   }

   public void collidedWith(PickupEntity var1) {
   }

   public void checkCollision() {
      Performance.record(this.getLevel().tickManager(), "checkCollision", (Runnable)(() -> {
         Rectangle var1 = this.getCollision();
         int var2 = (int)GameMath.max(GameMath.diagonalMoveDistance(0, 0, var1.width, var1.height), 100.0);
         this.getLevel().entityManager.pickups.streamArea(this.x, this.y, var2).filter((var1x) -> {
            return var1x != this && !var1x.removed() && this.collidesWith(var1x);
         }).forEach(this::collidedWith);
      }));
   }

   public Packet getPickupData() {
      return new Packet();
   }

   public void onPickup(ServerClient var1) {
      this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityPickup(this, new Packet()), this);
      this.remove();
   }

   public void onPickup(ClientClient var1, Packet var2) {
   }

   public NetworkClient getTarget() {
      return this.target;
   }

   public void setTarget(ServerClient var1) {
      this.target = var1;
      this.sendTargetUpdatePacket();
   }

   public void setTarget(ClientClient var1) {
      this.target = var1;
   }

   public void resetTarget() {
      this.target = null;
   }

   public boolean onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      if (var3) {
         StringTooltips var4 = new StringTooltips();
         long var5 = this.getWorldEntity().getTime();
         var4.add("Spawned: " + GameUtils.formatSeconds((var5 - this.spawnTime) / 1000L) + " ago");
         long var7 = this.getLifespanMillis();
         if (var7 > 0L) {
            var4.add("Despawns in: " + GameUtils.formatSeconds((var7 - this.getTimeSinceSpawned()) / 1000L));
         }

         var4.add("UniqueID: " + this.getRealUniqueID());
         boolean var9 = this.isOnPickupCooldown();
         if (!var9 && var2.isServerClient()) {
            var9 = this.isOnPickupCooldown(var2.getServerClient().authentication);
         }

         var4.add("Pickup: " + !var9);
         Screen.addTooltip(var4, TooltipLocation.INTERACT_FOCUS);
      }

      return false;
   }

   public String toString() {
      return super.toString() + "{" + this.getUniqueID() + "}";
   }
}
