package necesse.engine.network.packet;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.floatText.FloatTextFade;

public class PacketShowDPS extends Packet {
   public final int mobUniqueID;
   public final float dps;

   public PacketShowDPS(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.dps = var2.getNextFloat();
   }

   public PacketShowDPS(int var1, float var2) {
      this.mobUniqueID = var1;
      this.dps = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextFloat(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 != null) {
            String var4 = Localization.translate("misc", "dpscount", "dps", GameUtils.formatNumber((double)this.dps));
            FloatTextFade var5 = new FloatTextFade(var3.getX() + (int)(GameRandom.globalRandom.nextGaussian() * 6.0), var3.getY() - 32, var4, (new FontOptions(16)).outline().color(Color.ORANGE));
            var2.getLevel().hudManager.addElement(var5);
         }
      }
   }
}
