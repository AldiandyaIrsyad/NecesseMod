package necesse.inventory.container.travel;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class TravelStoneContainer extends TravelContainer {
   public TravelStoneContainer(NetworkClient var1, int var2, Packet var3) {
      super(var1, var2, var3);
   }

   public void travelTo(IslandData var1, int var2) {
      if (this.client.isClient()) {
         throw new IllegalStateException("Cannot be called client side, send a request travel packet instead");
      } else {
         ServerClient var3 = this.client.getServerClient();
         if (!var3.getLevelIdentifier().isIslandPosition()) {
            GameLog.warn.println(var3.getName() + " tried to use travel stone from non island level");
            this.close();
         } else {
            this.sendTeleportEvent(this.client.playerMob);
            int var4 = this.client.playerMob.getX();
            int var5 = this.client.playerMob.getY();
            int var6 = var3.getLevelIdentifier().getIslandX();
            int var7 = var3.getLevelIdentifier().getIslandY();
            int var8 = var1.islandX;
            int var9 = var1.islandY;
            var3.changeIsland(var8, var9, var2, (var6x) -> {
               int var7x;
               if (var6 < var8) {
                  var7x = 5;
               } else if (var6 > var8) {
                  var7x = var6x.width - 5;
               } else {
                  var7x = GameMath.limit(var4 / 32, 5, var6x.width - 5);
               }

               int var8x;
               if (var7 < var9) {
                  var8x = 5;
               } else if (var7 > var9) {
                  var8x = var6x.height - 5;
               } else {
                  var8x = GameMath.limit(var5 / 32, 5, var6x.height - 5);
               }

               Line2D.Float var9x = new Line2D.Float((float)(var7x * 32 + 16), (float)(var8x * 32 + 16), (float)var6x.width / 2.0F * 32.0F + 16.0F, (float)var6x.height / 2.0F * 32.0F + 16.0F);
               IntersectionPoint var10 = (IntersectionPoint)GameUtils.castRayFirstHit(var9x, 100.0, (var1) -> {
                  return var6x.getCollisionPoint(var6x.getCollisions(var1, (new CollisionFilter()).allLandTiles()), var1, true);
               });
               return var10 != null && var10.target != null ? new Point(((LevelObjectHit)var10.target).tileX * 32 + 16, ((LevelObjectHit)var10.target).tileY * 32 + 16) : new Point(var7x * 32 + 16, var8x * 32 + 16);
            }, true);
            var3.newStats.island_travels.increment(1);
            var3.closeContainer(false);
            this.client.playerMob.addBuff(new ActiveBuff("teleportsickness", this.client.playerMob, 10.0F, (Attacker)null), true);
            this.sendTeleportEvent(this.client.playerMob);
         }
      }
   }

   protected void sendTeleportEvent(Mob var1) {
      TeleportEvent var2 = new TeleportEvent(var1.getX(), var1.getY() + 5, var1.getUniqueID());
      var1.getLevel().getServer().network.sendToClientsWithEntity(new PacketLevelEvent(var2), var2);
   }
}
