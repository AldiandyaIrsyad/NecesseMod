package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class CmdNameParameterHandler extends ParameterHandler<ChatCommand> {
   private boolean ignoreOnlyHelpCommands;

   public CmdNameParameterHandler(boolean var1) {
      this.ignoreOnlyHelpCommands = var1;
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      if (var1 != null) {
         return autocompleteFromList(var1.commandsManager.getCommands(), (var3x) -> {
            return !var3x.onlyForHelp() && var3x.havePermissions(var1, var2, var3);
         }, (var0) -> {
            return var0.name;
         }, var4);
      } else {
         return var2 != null ? autocompleteFromList(var2.commandsManager.getServerCommands(), (var3x) -> {
            return !var3x.onlyForHelp() && var3x.havePermissions(var1, var2, var3);
         }, (var0) -> {
            return var0.name;
         }, var4) : Collections.emptyList();
      }
   }

   public ChatCommand parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      ChatCommand var6 = null;
      List var7 = var1 != null ? var1.commandsManager.getCommands() : var2.commandsManager.getServerCommands();
      Iterator var8 = var7.iterator();

      while(var8.hasNext()) {
         ChatCommand var9 = (ChatCommand)var8.next();
         if ((!var9.onlyForHelp() || !this.ignoreOnlyHelpCommands) && var9.name.equalsIgnoreCase(var4)) {
            var6 = var9;
            break;
         }
      }

      if (var6 != null && var6.onlyForHelp() && this.ignoreOnlyHelpCommands) {
         var6 = null;
      }

      if (var6 == null) {
         throw new IllegalArgumentException("Could not find command \"" + var4 + "\" for <" + var5.name + ">");
      } else {
         return var6;
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public ChatCommand getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
