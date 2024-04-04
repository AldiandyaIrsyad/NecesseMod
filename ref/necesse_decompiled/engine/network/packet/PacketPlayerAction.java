package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerAction extends Packet {
   public final PlayerAction action;

   public PacketPlayerAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.action = PacketPlayerAction.PlayerAction.getPlayerAction(var2.getNextByteUnsigned());
   }

   public PacketPlayerAction(PlayerAction var1) {
      this.action = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1.getID());
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.checkHasRequestedSelf() && !var3.isDead()) {
         var3.checkSpawned();
         var3.playerMob.runPlayerAction(this.action);
         var3.refreshAFKTimer();
      }
   }

   public static enum PlayerAction {
      USE_HEALTH_POTION,
      USE_MANA_POTION,
      EAT_FOOD,
      USE_BUFF_POTION;

      private PlayerAction() {
      }

      public int getID() {
         return this.ordinal();
      }

      public static PlayerAction getPlayerAction(int var0) {
         PlayerAction[] var1 = values();
         return var0 >= 0 && var0 < var1.length ? var1[var0] : null;
      }

      // $FF: synthetic method
      private static PlayerAction[] $values() {
         return new PlayerAction[]{USE_HEALTH_POTION, USE_MANA_POTION, EAT_FOOD, USE_BUFF_POTION};
      }
   }
}
