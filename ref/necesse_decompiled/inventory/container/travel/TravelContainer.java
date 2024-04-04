package necesse.inventory.container.travel;

import java.awt.Point;
import java.util.HashMap;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.PointCustomAction;

public class TravelContainer extends Container {
   public final RequestIslandsAction requestIslandsAction;
   public final PointCustomAction travelToDestination;
   public final HashMap<Point, IslandData> destinations;
   public final TravelDir travelDir;
   public final int knowRange;
   public final int travelRange;
   public final LevelIdentifier playerSpawnLevel;
   public final LevelIdentifier worldSpawnLevel;
   public final LevelIdentifier playerLevel;
   public static final int edgeTravelRangeTiles = 10;

   public TravelContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      this.destinations = new HashMap();
      this.travelDir = TravelDir.values()[var4.getNextByteUnsigned()];
      this.knowRange = var4.getNextInt();
      this.travelRange = var4.getNextInt();
      this.playerSpawnLevel = new LevelIdentifier(var4);
      this.worldSpawnLevel = new LevelIdentifier(var4);
      if (var1.isServer()) {
         this.playerLevel = var1.getServerClient().getLevelIdentifier();

         for(int var5 = -this.knowRange; var5 <= this.knowRange; ++var5) {
            for(int var6 = -this.knowRange; var6 <= this.knowRange; ++var6) {
               int var7 = dist(0, 0, var5, var6);
               if (this.knowRange >= var7) {
                  int var8 = this.playerLevel.getIslandX() + var5;
                  int var9 = this.playerLevel.getIslandY() + var6;
                  var1.getServerClient().addDiscoveredIsland(var8, var9);
               }
            }
         }
      } else {
         this.playerLevel = var1.getClientClient().getLevelIdentifier();
      }

      this.requestIslandsAction = (RequestIslandsAction)this.registerAction(new RequestIslandsAction(this));
      this.travelToDestination = (PointCustomAction)this.registerAction(new PointCustomAction() {
         protected void run(int var1x, int var2) {
            if (var1.isServer()) {
               ServerClient var3 = var1.getServerClient();
               IslandData var4 = IslandData.generateIslandData(var3.getServer(), var3, TravelContainer.this, var1x, var2);
               if (var4.canTravel) {
                  TravelContainer.this.travelTo(var4, 0);
               } else {
                  var3.closeContainer(true);
                  var3.sendChatMessage((GameMessage)(new LocalMessage("ui", "travelinvaliddestination")));
               }

            }
         }
      });
      this.subscribeEvent(IslandsResponseEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
   }

   public static Packet getContainerContentPacket(Server var0, ServerClient var1, TravelDir var2) {
      int var3;
      int var4;
      if (var1.playerMob == null) {
         var3 = 0;
         var4 = 0;
      } else {
         var3 = Math.max(0, (Integer)var1.playerMob.buffManager.getModifier(BuffModifiers.BIOME_VIEW_DISTANCE));
         var4 = Math.max(1, (Integer)var1.playerMob.buffManager.getModifier(BuffModifiers.TRAVEL_DISTANCE));
      }

      return getContainerContentPacket(var0, var1, var2, var3, var4);
   }

   public static Packet getContainerContentPacket(Server var0, ServerClient var1, TravelDir var2, int var3, int var4) {
      if (var1.playerMob == null) {
         return new Packet();
      } else {
         Packet var5 = new Packet();
         PacketWriter var6 = new PacketWriter(var5);
         var6.putNextByteUnsigned(var2.ordinal());
         var6.putNextInt(var3);
         var6.putNextInt(var4);
         var1.spawnLevelIdentifier.writePacket(var6);
         var0.world.worldEntity.spawnLevelIdentifier.writePacket(var6);
         return var5;
      }
   }

   public static int dist(int var0, int var1, int var2, int var3) {
      return Math.max(Math.abs(var0 - var2), Math.abs(var1 - var3));
   }

   public boolean isWithinTravelRange(int var1, int var2) {
      int var3 = this.travelRange;
      int var4 = this.travelRange;
      if (this.travelDir == TravelDir.All || this.travelDir == TravelDir.North || this.travelDir == TravelDir.South) {
         var3 = (int)Math.ceil((double)this.travelRange / 2.0);
      }

      if (this.travelDir == TravelDir.All || this.travelDir == TravelDir.West || this.travelDir == TravelDir.East) {
         var4 = (int)Math.ceil((double)this.travelRange / 2.0);
      }

      int var5 = this.playerLevel.getIslandX() - var3;
      int var6 = this.playerLevel.getIslandY() - var4;
      int var7 = this.playerLevel.getIslandX() + var3;
      int var8 = this.playerLevel.getIslandY() + var4;
      if (this.travelDir != TravelDir.NorthWest && this.travelDir != TravelDir.North && this.travelDir != TravelDir.NorthEast) {
         if (this.travelDir == TravelDir.SouthWest || this.travelDir == TravelDir.South || this.travelDir == TravelDir.SouthEast) {
            var6 = this.playerLevel.getIslandY();
         }
      } else {
         var8 = this.playerLevel.getIslandY();
      }

      if (this.travelDir != TravelDir.NorthWest && this.travelDir != TravelDir.West && this.travelDir != TravelDir.SouthWest) {
         if (this.travelDir == TravelDir.NorthEast || this.travelDir == TravelDir.East || this.travelDir == TravelDir.SouthEast) {
            var5 = this.playerLevel.getIslandX();
         }
      } else {
         var7 = this.playerLevel.getIslandX();
      }

      return var5 <= var1 && var1 <= var7 && var6 <= var2 && var2 <= var8;
   }

   public void travelTo(IslandData var1, int var2) {
      if (this.client.isClient()) {
         throw new IllegalStateException("Cannot be called client side, send a request travel packet instead");
      } else {
         this.client.getServerClient().changeIsland(var1.islandX, var1.islandY, var2);
         this.client.getServerClient().newStats.island_travels.increment(1);
      }
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else if (!this.playerLevel.isIslandPosition()) {
         return false;
      } else if (var1.isDead()) {
         return false;
      } else if (this.travelDir != TravelDir.None && this.travelDir != TravelDir.All) {
         return getTravelDir(var1.playerMob) == this.travelDir;
      } else {
         return true;
      }
   }

   public void onClose() {
      super.onClose();
      if (this.client.isClient() && GlobalData.getCurrentState() instanceof MainGame) {
         ((MainGame)GlobalData.getCurrentState()).formManager.resetTravelCooldown();
      }

   }

   public static TravelDir getTravelDir(PlayerMob var0) {
      if (var0.getX() < 320 & var0.getY() < 320) {
         return TravelDir.NorthWest;
      } else if (var0.getX() < 320 & var0.getY() > var0.getLevel().height * 32 - 320) {
         return TravelDir.SouthWest;
      } else if (var0.getX() > var0.getLevel().width * 32 - 320 & var0.getY() < 320) {
         return TravelDir.NorthEast;
      } else if (var0.getX() > var0.getLevel().width * 32 - 320 & var0.getY() > var0.getLevel().height * 32 - 320) {
         return TravelDir.SouthEast;
      } else if (var0.getX() < 320) {
         return TravelDir.West;
      } else if (var0.getX() > var0.getLevel().width * 32 - 320) {
         return TravelDir.East;
      } else if (var0.getY() < 320) {
         return TravelDir.North;
      } else {
         return var0.getY() > var0.getLevel().width * 32 - 320 ? TravelDir.South : null;
      }
   }
}
