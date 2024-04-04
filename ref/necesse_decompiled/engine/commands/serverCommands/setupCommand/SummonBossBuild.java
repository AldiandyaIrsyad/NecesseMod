package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;

public class SummonBossBuild extends CharacterBuild {
   public String mobStringID;
   public int tileRange;

   public SummonBossBuild(String var1, int var2) {
      this.mobStringID = var1;
      this.tileRange = var2;
   }

   public SummonBossBuild(String var1) {
      this(var1, 30);
   }

   public void apply(ServerClient var1) {
      float var2 = (float)GameRandom.globalRandom.nextInt(360);
      float var3 = (float)Math.cos(Math.toRadians((double)var2));
      float var4 = (float)Math.sin(Math.toRadians((double)var2));
      float var5 = (float)(this.tileRange * 32);
      Mob var6 = MobRegistry.getMob(this.mobStringID, var1.getLevel());
      var1.getLevel().entityManager.addMob(var6, (float)(var1.playerMob.getX() + (int)(var3 * var5)), (float)(var1.playerMob.getY() + (int)(var4 * var5)));
      var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", var6.getLocalization())), (ServerClient)var1);
   }
}
