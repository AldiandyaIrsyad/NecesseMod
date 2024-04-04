package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class FloatPosDirMobAbility extends MobAbility {
   public FloatPosDirMobAbility() {
   }

   public void runAndSend(float var1, float var2, float var3, float var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextFloat(var1);
      var6.putNextFloat(var2);
      var6.putNextFloat(var3);
      var6.putNextFloat(var4);
      this.runAndSendAbility(var5);
   }

   public void executePacket(PacketReader var1) {
      float var2 = var1.getNextFloat();
      float var3 = var1.getNextFloat();
      float var4 = var1.getNextFloat();
      float var5 = var1.getNextFloat();
      this.run(var2, var3, var4, var5);
   }

   protected abstract void run(float var1, float var2, float var3, float var4);
}
