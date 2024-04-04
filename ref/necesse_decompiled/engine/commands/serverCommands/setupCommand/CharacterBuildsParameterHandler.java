package necesse.engine.commands.serverCommands.setupCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class CharacterBuildsParameterHandler extends ParameterHandler<CharacterBuilds> {
   public CharacterBuildsParameterHandler() {
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      String[] var5 = var4.arg.split(" ");
      if (var4.arg.endsWith(" ")) {
         var5 = (String[])GameUtils.concat(var5, new String[]{""});
      }

      LinkedList var6 = new LinkedList(DemoServerCommand.builds.keySet());

      for(int var7 = 0; var7 < var5.length; ++var7) {
         String var8 = var5[var7];
         ListIterator var9 = var6.listIterator();
         boolean var10 = false;

         while(var9.hasNext()) {
            String var11 = (String)var9.next();
            if (var11.equals(var8)) {
               if (var7 == var5.length - 1) {
                  return Collections.singletonList(new AutoComplete(1, var11, true));
               }

               var9.remove();
               var10 = true;
               break;
            }
         }

         if (var7 < var5.length - 1 && !var10) {
            return Collections.emptyList();
         }

         if (!var10) {
            List var15 = autocompleteFromArray((String[])var6.toArray(new String[0]), (Function)null, (Function)null, new CmdArgument(var4.param, var8, var4.argCount));
            ArrayList var12 = new ArrayList(var15.size());
            Iterator var13 = var15.iterator();

            while(var13.hasNext()) {
               AutoComplete var14 = (AutoComplete)var13.next();
               var12.add(new AutoComplete(1, var14.newArgs, true));
            }

            return var12;
         }
      }

      return Collections.emptyList();
   }

   public CharacterBuilds parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      ArrayList var6 = new ArrayList();
      String[] var7 = var4.split(" ");
      String[] var8 = var7;
      int var9 = var7.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String var11 = var8[var10];
         boolean var12 = false;
         Iterator var13 = DemoServerCommand.builds.entrySet().iterator();

         while(var13.hasNext()) {
            Map.Entry var14 = (Map.Entry)var13.next();
            if (((String)var14.getKey()).equals(var11)) {
               var6.add(new CharacterBuildEntry((String)var14.getKey(), (CharacterBuild)var14.getValue()));
               var12 = true;
               break;
            }
         }

         if (!var12) {
            throw new IllegalArgumentException("Could not find build with name \"" + var11 + "\" for <" + var5.name + ">");
         }
      }

      return new CharacterBuilds((CharacterBuildEntry[])var6.toArray(new CharacterBuildEntry[0]));
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public CharacterBuilds getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return new CharacterBuilds(new CharacterBuildEntry[0]);
   }

   public int getArgsUsed() {
      return 10000;
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
