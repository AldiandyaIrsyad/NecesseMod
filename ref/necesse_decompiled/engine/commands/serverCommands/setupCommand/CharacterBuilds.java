package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.server.ServerClient;

class CharacterBuilds {
   public final CharacterBuildEntry[] builds;

   public CharacterBuilds(CharacterBuildEntry... var1) {
      this.builds = var1;
   }

   public void apply(ServerClient var1) {
      CharacterBuild.apply(var1, this.builds);
   }
}
