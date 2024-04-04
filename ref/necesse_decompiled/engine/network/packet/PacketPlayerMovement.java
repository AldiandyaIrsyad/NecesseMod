package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerMovement extends Packet {
   public final int slot;
   public final boolean hasSpawned;
   public final boolean isDirect;
   public final float x;
   public final float y;
   public final float dx;
   public final float dy;
   private final PacketReader reader;

   public PacketPlayerMovement(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.hasSpawned = var2.getNextBoolean();
      this.isDirect = var2.getNextBoolean();
      this.x = var2.getNextFloat();
      this.y = var2.getNextFloat();
      this.dx = var2.getNextFloat();
      this.dy = var2.getNextFloat();
      this.reader = var2;
   }

   public PacketPlayerMovement(ServerClient var1, boolean var2) {
      this(var1.slot, var1.hasSpawned(), var1.playerMob, var2);
   }

   public PacketPlayerMovement(Client var1, ClientClient var2, boolean var3) {
      this(var1.getSlot(), var2.hasSpawned(), var2.playerMob, var3);
   }

   private PacketPlayerMovement(int var1, boolean var2, PlayerMob var3, boolean var4) {
      this.slot = var1;
      this.hasSpawned = var2;
      this.isDirect = var4;
      this.x = var3.x;
      this.y = var3.y;
      this.dx = var3.dx;
      this.dy = var3.dy;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextByteUnsigned(var1);
      var5.putNextBoolean(var2);
      var5.putNextBoolean(var4);
      var5.putNextFloat(this.x);
      var5.putNextFloat(this.y);
      var5.putNextFloat(this.dx);
      var5.putNextFloat(this.dy);
      this.reader = new PacketReader(var5);
      var3.setupPlayerMovementPacket(var5);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot == var3.slot && var3.checkHasRequestedSelf() && !var3.isDead()) {
         var3.checkSpawned();
         double var4 = var3.playerMob.allowServerMovement(var2, var3, this.x, this.y, this.dx, this.dy);
         if (var4 <= 0.0) {
            var3.playerMob.applyPlayerMovementPacket(this, new PacketReader(this.reader));
            var2.network.sendToClientsAtExcept(new PacketPlayerMovement(var3, this.isDirect), (ServerClient)var3, var3);
         } else {
            GameLog.warn.println(var3.getName() + " moved wrongly, snapping back " + var4);
            var2.network.sendToClientsAt(new PacketPlayerMovement(var3, false), (ServerClient)var3);
         }

         if (var3.playerMob.moveX != 0.0F || var3.playerMob.moveY != 0.0F) {
            var3.refreshAFKTimer();
         }

      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else if (var3.loadedPlayer) {
         if (this.hasSpawned && !var3.hasSpawned()) {
            var3.applySpawned(0);
         }

         var3.playerMob.applyPlayerMovementPacket(this, new PacketReader(this.reader));
         if (this.slot == var2.getSlot()) {
            var2.resetPositionPointUpdate();
         }
      }

   }
}
