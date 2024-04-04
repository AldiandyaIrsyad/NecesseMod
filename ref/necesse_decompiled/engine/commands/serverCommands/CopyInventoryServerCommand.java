package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.Inventory;
import necesse.inventory.PlayerInventoryManager;

public class CopyInventoryServerCommand extends ModularChatCommand {
   public CopyInventoryServerCommand() {
      super("copyplayer", "Copy a players inventory, position and health over to another", PermissionLevel.ADMIN, true, new CmdParameter("from", new ServerClientParameterHandler(true, false)), new CmdParameter("to", new ServerClientParameterHandler(true, false)));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      ServerClient var8 = (ServerClient)var4[1];
      PlayerInventoryManager var9 = var7.playerMob.getInv();
      PlayerInventoryManager var10 = var8.playerMob.getInv();
      this.copyInventory(var9.drag, var10.drag);
      this.copyInventory(var9.main, var10.main);
      this.copyInventory(var9.cosmetic, var10.cosmetic);
      this.copyInventory(var9.armor, var10.armor);
      this.copyInventory(var9.trinkets, var10.trinkets);
      this.copyInventory(var9.trash, var10.trash);
      this.copyInventory(var9.cloud, var10.cloud);
      var8.playerMob.setMaxHealth(var7.playerMob.getMaxHealth());
      var8.playerMob.setHealth(var7.playerMob.getHealth());
      var8.playerMob.setPos(var7.playerMob.x, var7.playerMob.y, true);
      var8.changeLevel(var7.getLevelIdentifier());
      var8.sendPacket(new PacketPlayerGeneral(var8));
   }

   private void copyInventory(Inventory var1, Inventory var2) {
      var2.override(var1);
      var2.markFullDirty();
   }
}
