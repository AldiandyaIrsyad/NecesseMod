package necesse.engine.commands.serverCommands.setupCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.network.server.ServerClient;

public class CharacterBuildConcat extends CharacterBuild {
   public final ArrayList<CharacterBuild> builds = new ArrayList();
   public final ArrayList<String> buildIDs = new ArrayList();

   public CharacterBuildConcat(Object... var1) {
      Object[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 instanceof CharacterBuild) {
            this.builds.add((CharacterBuild)var5);
         } else {
            if (!(var5 instanceof String)) {
               throw new IllegalArgumentException("Build parameters must either be CharacterBuild or String");
            }

            this.buildIDs.add((String)var5);
         }
      }

   }

   public void apply(ServerClient var1) {
   }

   public void addApplies(List<CharacterBuild> var1) {
      Iterator var2 = this.builds.iterator();

      while(var2.hasNext()) {
         CharacterBuild var3 = (CharacterBuild)var2.next();
         var3.addApplies(var1);
      }

      var2 = this.buildIDs.iterator();

      while(var2.hasNext()) {
         String var5 = (String)var2.next();
         CharacterBuild var4 = (CharacterBuild)DemoServerCommand.builds.get(var5);
         if (var4 != null) {
            var4.addApplies(var1);
         }
      }

   }
}
