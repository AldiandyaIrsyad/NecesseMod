package necesse.entity.mobs.buffs;

import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketBuffAbility;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;

public interface BuffAbility {
   void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3);

   boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3);

   default Packet getAbilityContent(PlayerMob var1, ActiveBuff var2, GameCamera var3) {
      return new Packet();
   }

   default void runAndSendAbility(Client var1, PlayerMob var2, ActiveBuff var3, Packet var4) {
      this.runAbility(var2, var3, var4);
      var1.network.sendPacket(new PacketBuffAbility(var1.getSlot(), var3.buff, var4));
   }
}
