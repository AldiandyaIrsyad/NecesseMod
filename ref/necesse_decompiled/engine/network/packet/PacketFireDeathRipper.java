package necesse.engine.network.packet;

import necesse.engine.Screen;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.sound.SoundEffect;
import necesse.gfx.GameResources;

public class PacketFireDeathRipper extends Packet {
   public final int slot;

   public PacketFireDeathRipper(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
   }

   public PacketFireDeathRipper(int var1) {
      this.slot = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 != null && var3.playerMob != null) {
         Screen.playSound(GameResources.handgun, SoundEffect.effect(var3.playerMob));
      }

   }
}
