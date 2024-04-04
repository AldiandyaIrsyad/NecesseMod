package necesse.inventory.container;

import java.awt.Point;
import java.util.function.Predicate;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.events.SleepUpdateContainerEvent;
import necesse.inventory.container.events.SpawnUpdateContainerEvent;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RespawnObject;

public class BedContainer extends Container {
   public static int WAIT_TIME = 4000;
   public final int tileX;
   public final int tileY;
   public final ObjectEntity objectEntity;
   public final OEUsers oeUsers;
   public int sleepTimer;
   public int sleepingPlayers;
   public int darknessTimer;
   public int updateTimer;
   public long nextWakeUpTime;
   public boolean isCurrentSpawn;
   public EmptyCustomAction setSpawn;
   public EmptyCustomAction clearSpawn;

   public BedContainer(final NetworkClient var1, int var2, final ObjectEntity var3, Packet var4) {
      super(var1, var2);
      this.tileX = var3.getTileX();
      this.tileY = var3.getTileY();
      this.objectEntity = var3;
      this.oeUsers = var3 instanceof OEUsers ? (OEUsers)var3 : null;
      if (var1.isServer() & this.oeUsers != null) {
         this.oeUsers.startUser(var1.playerMob);
      }

      PacketReader var5 = new PacketReader(var4);
      this.isCurrentSpawn = var5.getNextBoolean();
      this.sleepingPlayers = var5.getNextInt();
      this.subscribeEvent(SleepUpdateContainerEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(SleepUpdateContainerEvent.class, (var1x) -> {
         this.sleepingPlayers = var1x.totalSleeping;
      });
      this.subscribeEvent(SpawnUpdateContainerEvent.class, (var1x) -> {
         return var1x.tileX == this.tileX && var1x.tileY == this.tileY;
      }, () -> {
         return true;
      });
      this.onEvent(SpawnUpdateContainerEvent.class, (var1x) -> {
         this.isCurrentSpawn = var1x.isCurrentSpawn;
      });
      this.updateTimer = 60000;
      this.setSpawn = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               GameObject var1x = var3.getObject();
               if (var1x instanceof RespawnObject) {
                  ServerClient var2 = var1.getServerClient();
                  boolean var3x = ((RespawnObject)var1x).isCurrentSpawn(var3.getLevel(), BedContainer.this.tileX, BedContainer.this.tileY, var2);
                  ((RespawnObject)var1x).setSpawn(var3.getLevel(), BedContainer.this.tileX, BedContainer.this.tileY, var2, !var3x);
                  (new SpawnUpdateContainerEvent(BedContainer.this.tileX, BedContainer.this.tileY, true)).applyAndSendToClient(var2);
               }

            }
         }
      });
      this.clearSpawn = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               GameObject var1x = var3.getObject();
               if (var1x instanceof RespawnObject) {
                  ServerClient var2 = var1.getServerClient();
                  ((RespawnObject)var1x).removeSpawn(var3.getLevel(), BedContainer.this.tileX, BedContainer.this.tileY, var2, true);
                  (new SpawnUpdateContainerEvent(BedContainer.this.tileX, BedContainer.this.tileY, false)).applyAndSendToClient(var2);
               }

            }
         }
      });
   }

   public void tick() {
      super.tick();
      if (this.client.isServer() && this.objectEntity.removed()) {
         this.close();
      } else {
         if (this.client.isServer() & this.oeUsers != null) {
            this.oeUsers.startUser(this.client.playerMob);
         }

         PlayerMob var1 = this.getClient().playerMob;
         if (this.client.isServer() && (var1.getLevel().isSleepPrevented() || var1.getWorldEntity().isSleepPrevented())) {
            this.close();
         } else if (this.nextWakeUpTime > 0L && this.nextWakeUpTime <= var1.getWorldEntity().getWorldTime()) {
            this.close();
         } else {
            this.updateTimer += 50;
            if (this.updateTimer >= 5000) {
               this.sendUpdate((Predicate)null);
               this.updateTimer = 0;
            }

            this.sleepTimer += 50;
            if (this.client.isClient()) {
               ClientClient var2 = this.client.getClientClient();
               if ((long)this.sleepingPlayers < var2.getClient().streamClients().count()) {
                  if (this.darknessTimer != 0) {
                     float var3 = this.getCurrentDarkness();
                     Screen.setSceneDarknessFade(var3, 0.0F, (int)(500.0F * var3), () -> {
                        GameCamera var1 = GlobalData.getCurrentState().getCamera();
                        return new Point(var1.getDrawX(var2.playerMob.getX()), var1.getDrawY(var2.playerMob.getY()));
                     }, () -> {
                        return !this.client.getClientClient().getClient().isDisconnected() && this.client.hasSpawned();
                     });
                  }

                  this.darknessTimer = 0;
               } else {
                  this.darknessTimer += 50;
                  GameCamera var8 = GlobalData.getCurrentState().getCamera();
                  GameResources.darknessShader.midScreenX = var8.getDrawX(var2.playerMob.getX());
                  GameResources.darknessShader.midScreenY = var8.getDrawY(var2.playerMob.getY());
                  Screen.setSceneDarkness(this.getCurrentDarkness());
               }
            }

            if (this.sleepTimer > WAIT_TIME && this.getClient().isServer()) {
               Server var7 = this.getClient().getServerClient().getServer();
               boolean var9 = var7.streamClients().allMatch((var0) -> {
                  Container var1 = var0.getContainer();
                  if (var1 instanceof BedContainer) {
                     BedContainer var2 = (BedContainer)var1;
                     return var2.sleepTimer > WAIT_TIME;
                  } else {
                     return false;
                  }
               });
               if (this.nextWakeUpTime == 0L && var9) {
                  float var4 = var7.world.worldEntity.hourToDayTime(7.0F);
                  long var5 = var7.world.getTimeToNextTimeOfDay((int)var4);
                  if (var5 < 30000L) {
                     var5 += (long)var7.world.worldEntity.getDayTimeMax() * 1000L;
                  }

                  this.nextWakeUpTime = var7.world.getWorldTime() + var5;
               } else if (!var9) {
                  this.nextWakeUpTime = 0L;
               }

               if (var9) {
                  var7.world.worldEntity.keepSleeping();
               }
            }

         }
      }
   }

   public float getCurrentDarkness() {
      return Math.min((float)this.darknessTimer / (float)WAIT_TIME, 1.0F);
   }

   public void sendUpdate(Predicate<ServerClient> var1) {
      if (this.client.isServer()) {
         Server var2 = this.client.getServerClient().getServer();
         SleepUpdateContainerEvent var3 = new SleepUpdateContainerEvent(var2, var1);
         var3.applyAndSendToAllClients(var2);
      }
   }

   public void onClose() {
      super.onClose();
      if (this.client.isServer() & this.oeUsers != null) {
         this.oeUsers.stopUser(this.client.playerMob);
      }

      this.sendUpdate((var1x) -> {
         return var1x != this.getClient();
      });
      if (this.client.isClient()) {
         float var1 = this.getCurrentDarkness();
         Screen.setSceneDarknessFade(var1, 0.0F, (int)(500.0F * var1), () -> {
            ClientClient var1 = this.client.getClientClient();
            GameCamera var2 = GlobalData.getCurrentState().getCamera();
            return new Point(var2.getDrawX(var1.playerMob.getX()), var2.getDrawY(var1.playerMob.getY()));
         }, () -> {
            return !this.client.getClientClient().getClient().isDisconnected() && this.client.hasSpawned();
         });
      }

   }

   public static Packet getContainerContent(ServerClient var0, boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextBoolean(var1);
      int var4 = (int)var0.getServer().streamClients().filter((var1x) -> {
         return var1x == var0 || var1x.getContainer() instanceof BedContainer;
      }).count();
      var3.putNextInt(var4);
      return var2;
   }
}
