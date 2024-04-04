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

public class PacketLifelineEvent extends Packet {
   public final int uniqueID;

   public PacketLifelineEvent(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.uniqueID = var2.getNextByteUnsigned();
   }

   public PacketLifelineEvent(int var1) {
      this.uniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.uniqueID, var2.getLevel());
         if (var3 != null) {
            if (var3 == var2.getPlayer()) {
               Screen.playSound(GameResources.teleportfail, SoundEffect.effect(var3).pitch(0.7F));
            }

            for(int var4 = 0; var4 < 10; ++var4) {
               var2.getLevel().entityManager.addParticle(var3.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var3.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 6.0), (float)(GameRandom.globalRandom.nextGaussian() * 6.0)).color(new Color(150, 50, 50)).heightMoves(16.0F, 48.0F);
            }
         }

      }
   }
}
