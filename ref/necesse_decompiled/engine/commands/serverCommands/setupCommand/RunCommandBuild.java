package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.server.ServerClient;

public class RunCommandBuild extends CharacterBuild {
   public String command;

   public RunCommandBuild(String var1) {
      this.command = var1;
   }

   public void apply(ServerClient var1) {
      var1.getServer().sendCommand(this.command, var1);
   }
}
