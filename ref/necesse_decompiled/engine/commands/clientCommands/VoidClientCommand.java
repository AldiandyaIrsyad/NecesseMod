package necesse.engine.commands.clientCommands;

import java.util.function.BiConsumer;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class VoidClientCommand extends ModularChatCommand {
   private final BiConsumer<Client, CommandLog> logic;

   public VoidClientCommand(String var1, String var2, PermissionLevel var3, BiConsumer<Client, CommandLog> var4) {
      super(var1, var2, var3, false);
      this.logic = var4;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      this.logic.accept(var1, var6);
   }
}
