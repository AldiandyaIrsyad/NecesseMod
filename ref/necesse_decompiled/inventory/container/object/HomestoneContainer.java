package necesse.inventory.container.object;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.IntBooleanCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.Waystone;

public class HomestoneContainer extends Container {
   public final LevelObject levelObject;
   public int maxWaystones;
   public ArrayList<Waystone> waystones;
   public IntCustomAction useWaystone;
   public IntBooleanCustomAction moveWaystoneUp;
   public IntBooleanCustomAction moveWaystoneDown;
   public WaystoneRenameCustomAction renameWaystone;

   public HomestoneContainer(final NetworkClient var1, int var2, LevelObject var3, Packet var4) {
      super(var1, var2);
      this.levelObject = var3;
      this.update(var4);
      this.subscribeEvent(HomestoneUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(HomestoneUpdateEvent.class, (var1x) -> {
         this.update(var1x.content);
      });
      this.useWaystone = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            if (var1.isServer()) {
               ServerClient var2 = var1.getServerClient();
               Server var3 = var2.getServer();
               Level var4 = var2.getLevel();
               SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4);
               if (var5 != null) {
                  ArrayList var6 = var5.getWaystones();
                  if (var1x >= 0 && var1x < var6.size()) {
                     Waystone var7 = (Waystone)var6.get(var1x);
                     var4.entityManager.addLevelEventHidden(new TeleportEvent(var2, 0, var7.destination, 0.0F, (Function)null, (var7x) -> {
                        if (var7.checkIsValid(var4, var3)) {
                           var2.closeContainer(true);
                           var2.newStats.homestones_used.increment(1);
                           return new TeleportResult(true, var7.findTeleportLocation(var3, var1.playerMob));
                        } else {
                           var6.remove(var7);
                           var2.sendChatMessage((GameMessage)(new LocalMessage("ui", "waystoneinvalid")));
                           var5.sendEvent(HomestoneUpdateEvent.class);
                           return new TeleportResult(false, (Point)null);
                        }
                     }));
                  } else {
                     (new HomestoneUpdateEvent(var5)).applyAndSendToClient(var2);
                  }
               }
            }

         }
      });
      this.moveWaystoneUp = (IntBooleanCustomAction)this.registerAction(new IntBooleanCustomAction() {
         protected void run(int var1x, boolean var2) {
            if (var1x > 0 && var1x <= HomestoneContainer.this.waystones.size() - 1) {
               Waystone var3 = (Waystone)HomestoneContainer.this.waystones.remove(var1x);
               if (var2) {
                  HomestoneContainer.this.waystones.add(0, var3);
               } else {
                  HomestoneContainer.this.waystones.add(var1x - 1, var3);
               }

               if (var1.isServer()) {
                  ServerClient var4 = var1.getServerClient();
                  Level var5 = var4.getLevel();
                  SettlementLevelData var6 = SettlementLevelData.getSettlementData(var5);
                  if (var6 != null) {
                     ArrayList var7 = var6.getWaystones();
                     if (var1x > var7.size() - 1) {
                        return;
                     }

                     Waystone var8 = (Waystone)var7.remove(var1x);
                     if (var2) {
                        var7.add(0, var8);
                     } else {
                        var7.add(var1x - 1, var8);
                     }

                     var6.sendEvent(HomestoneUpdateEvent.class);
                  }
               }

            }
         }
      });
      this.moveWaystoneDown = (IntBooleanCustomAction)this.registerAction(new IntBooleanCustomAction() {
         protected void run(int var1x, boolean var2) {
            if (var1x >= 0 && var1x <= HomestoneContainer.this.waystones.size() - 2) {
               Waystone var3 = (Waystone)HomestoneContainer.this.waystones.remove(var1x);
               if (var2) {
                  HomestoneContainer.this.waystones.add(var3);
               } else {
                  HomestoneContainer.this.waystones.add(var1x + 1, var3);
               }

               if (var1.isServer()) {
                  ServerClient var4 = var1.getServerClient();
                  Level var5 = var4.getLevel();
                  SettlementLevelData var6 = SettlementLevelData.getSettlementData(var5);
                  if (var6 != null) {
                     ArrayList var7 = var6.getWaystones();
                     if (var1x > var7.size() - 2) {
                        return;
                     }

                     Waystone var8 = (Waystone)var7.remove(var1x);
                     if (var2) {
                        var7.add(var8);
                     } else {
                        var7.add(var1x + 1, var8);
                     }

                     var6.sendEvent(HomestoneUpdateEvent.class);
                  }
               }

            }
         }
      });
      this.renameWaystone = (WaystoneRenameCustomAction)this.registerAction(new WaystoneRenameCustomAction());
   }

   public void update(Packet var1) {
      PacketReader var2 = new PacketReader(var1);
      this.maxWaystones = var2.getNextShortUnsigned();
      int var3 = var2.getNextShortUnsigned();
      this.waystones = new ArrayList(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.waystones.add(new Waystone(var2));
      }

   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         return !this.levelObject.hasChanged() && this.levelObject.inInteractRange(var1.playerMob);
      }
   }

   public static Packet getContainerContent(Level var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      SettlementLevelData var3 = SettlementLevelData.getSettlementData(var0);
      if (var3 != null) {
         int var4 = var3.getMaxWaystones();
         ArrayList var5 = var3.getWaystones();
         var2.putNextShortUnsigned(var4);
         var2.putNextShortUnsigned(var5.size());
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Waystone var7 = (Waystone)var6.next();
            var7.writePacket(var2);
         }
      } else {
         var2.putNextShortUnsigned(0);
         var2.putNextShortUnsigned(0);
      }

      return var1;
   }

   public class WaystoneRenameCustomAction extends ContainerCustomAction {
      protected WaystoneRenameCustomAction() {
      }

      public void runAndSend(int var1, String var2) {
         Packet var3 = new Packet();
         PacketWriter var4 = new PacketWriter(var3);
         var4.putNextInt(var1);
         var4.putNextString(var2);
         this.runAndSendAction(var3);
      }

      public void executePacket(PacketReader var1) {
         int var2 = var1.getNextInt();
         String var3 = var1.getNextString();
         if (HomestoneContainer.this.client.isServer()) {
            ServerClient var4 = HomestoneContainer.this.client.getServerClient();
            SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4.getLevel());
            if (var5 != null) {
               ArrayList var6 = var5.getWaystones();
               if (var2 >= 0 && var2 < var6.size()) {
                  Waystone var7 = (Waystone)var6.get(var2);
                  var7.name = var3;
                  var5.sendEvent(HomestoneUpdateEvent.class);
               } else {
                  (new HomestoneUpdateEvent(var5)).applyAndSendToClient(var4);
               }
            }
         }

      }
   }
}
