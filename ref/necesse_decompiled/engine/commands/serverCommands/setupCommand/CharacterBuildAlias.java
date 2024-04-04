package necesse.engine.commands.serverCommands.setupCommand;

import java.util.List;
import necesse.engine.network.server.ServerClient;

public class CharacterBuildAlias extends CharacterBuild {
   public final String buildID;

   public CharacterBuildAlias(String var1) {
      this.buildID = var1;
   }

   public void apply(ServerClient var1) {
   }

   public void addApplies(List<CharacterBuild> var1) {
      CharacterBuild var2 = (CharacterBuild)DemoServerCommand.builds.get(this.buildID);
      if (var2 != null) {
         var2.addApplies(var1);
      }

   }
}
