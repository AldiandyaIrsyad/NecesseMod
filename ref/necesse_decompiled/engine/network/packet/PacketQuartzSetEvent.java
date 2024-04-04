package necesse.engine.network.packet;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class PacketQuartzSetEvent extends Packet {
   public final int uniqueID;

   public PacketQuartzSetEvent(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.uniqueID = var2.getNextByteUnsigned();
   }

   public PacketQuartzSetEvent(int var1) {
      this.uniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.uniqueID, var2.getLevel());
         if (var3 != null && var3.getLevel() != null) {
            for(int var4 = 0; var4 < 12; ++var4) {
               var3.getLevel().entityManager.addParticle(var3.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var3.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).color(new Color(255, 245, 198)).movesConstant(var3.dx, var3.dy).height(16.0F);
            }

            Screen.playSound(GameResources.teleportfail, SoundEffect.effect(var3).pitch(1.3F));
         }

      }
   }
}
