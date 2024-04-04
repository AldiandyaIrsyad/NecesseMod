package necesse.engine.commands.serverCommands.setupCommand;

import java.util.List;
import java.util.function.Function;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class WorldSetupParameterHandler extends ParameterHandler<WorldSetupEntry> {
   public WorldSetupParameterHandler() {
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromArray((String[])DemoServerCommand.setups.keySet().toArray(new String[0]), (Function)null, (Function)null, var4);
   }

   public WorldSetupEntry parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      WorldSetup var6 = (WorldSetup)DemoServerCommand.setups.get(var4);
      if (var6 != null) {
         return new WorldSetupEntry(var4, var6);
      } else {
         throw new IllegalArgumentException("Could not find setup with name \"" + var4 + "\" for <" + var5.name + ">");
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public WorldSetupEntry getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return null;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.getDefault(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      return this.parse(var1, var2, var3, var4, var5);
   }
}
