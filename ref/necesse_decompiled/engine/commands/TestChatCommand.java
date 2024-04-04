package necesse.engine.commands;

import java.util.Objects;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class TestChatCommand extends ModularChatCommand {
   public TestChatCommand() {
      super("test", "Tests the command", PermissionLevel.USER, true, new CmdParameter("x", new FloatParameterHandler(), new CmdParameter[]{new CmdParameter("y", new FloatParameterHandler())}), new CmdParameter("text", new StringParameterHandler("def")), new CmdParameter("clear/set/random", new PresetStringParameterHandler(true, new String[]{"clear", "set", "random", "actual"}), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var6.addConsole("Successful test run with " + var4.length + " arguments:");

      for(int var7 = 0; var7 < var4.length; ++var7) {
         var6.addConsole("Arg " + var7 + " (" + (var4[var7] == null ? "null" : var4[var7].getClass().getSimpleName()) + "): " + Objects.toString(var4[var7]));
      }

   }
}
