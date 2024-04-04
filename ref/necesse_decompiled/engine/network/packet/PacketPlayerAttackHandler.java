package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerAttackHandler extends Packet {
   public final int subType;
   public final Packet content;

   public PacketPlayerAttackHandler(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.subType = var2.getNextByteUnsigned();
      this.content = var2.getNextContentPacket();
   }

   private PacketPlayerAttackHandler(int var1, Packet var2) {
      this.subType = var1;
      this.content = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextContentPacket(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.checkHasRequestedSelf() && !var3.isDead()) {
         var3.checkSpawned();
         if (var3.playerMob.getAttackHandler() != null) {
            switch (this.subType) {
               case 0:
                  var3.playerMob.endAttackHandler(true);
                  break;
               case 1:
               default:
                  System.out.println(var3.getName() + " sent invalid attack handler update");
                  break;
               case 2:
                  PacketReader var4 = new PacketReader(this.content);
                  if (var4.getNextBoolean()) {
                     int var5 = var4.getNextInt();
                     int var6 = var4.getNextInt();
                     var3.playerMob.getAttackHandler().onMouseInteracted(var5, var6);
                  } else {
                     float var7 = var4.getNextFloat();
                     float var8 = var4.getNextFloat();
                     var3.playerMob.getAttackHandler().onControllerInteracted(var7, var8);
                  }
                  break;
               case 3:
                  var3.playerMob.getAttackHandler().onPacketUpdate(new PacketReader(this.content));
            }
         } else {
            var3.sendPacket(new PacketPlayerAttackHandler(1, new Packet()));
         }

      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer();
      if (var3.getAttackHandler() != null) {
         switch (this.subType) {
            case 1:
               var3.endAttackHandler(false);
               break;
            case 3:
               var3.getAttackHandler().onPacketUpdate(new PacketReader(this.content));
               break;
            default:
               System.out.println("Got invalid attack handler update from server");
         }
      }

   }

   public static PacketPlayerAttackHandler clientEnd() {
      return new PacketPlayerAttackHandler(0, new Packet());
   }

   public static PacketPlayerAttackHandler clientInteractMouse(int var0, int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextBoolean(true);
      var3.putNextInt(var0);
      var3.putNextInt(var1);
      return new PacketPlayerAttackHandler(2, var2);
   }

   public static PacketPlayerAttackHandler clientInteractController(float var0, float var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextBoolean(false);
      var3.putNextFloat(var0);
      var3.putNextFloat(var1);
      return new PacketPlayerAttackHandler(2, var2);
   }

   public static PacketPlayerAttackHandler update(Packet var0) {
      return new PacketPlayerAttackHandler(3, var0);
   }
}
