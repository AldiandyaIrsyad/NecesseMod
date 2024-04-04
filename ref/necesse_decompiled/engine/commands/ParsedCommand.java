package necesse.engine.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedCommand {
   private static final Pattern argsPattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
   public final String fullCommand;
   public final String commandName;
   public final String[] args;

   public ParsedCommand(String var1) {
      this.fullCommand = var1;
      int var2 = var1.indexOf(32);
      if (var2 == -1) {
         this.commandName = var1;
         this.args = new String[0];
      } else {
         this.commandName = var1.substring(0, var2);
         String var3 = var1.substring(var2 + 1);
         this.args = parseArgs(var3);
      }

   }

   public static String[] parseArgs(String var0) {
      if (var0.isEmpty()) {
         return new String[]{""};
      } else {
         ArrayList var1 = new ArrayList();
         Matcher var2 = argsPattern.matcher(var0);

         while(var2.find()) {
            if (var2.group(1) != null) {
               var1.add(var2.group(1));
            } else if (var2.group(2) != null) {
               var1.add(var2.group(2));
            } else {
               var1.add(var2.group());
            }
         }

         if (var0.endsWith(" ")) {
            var1.add("");
         }

         return (String[])var1.toArray(new String[0]);
      }
   }

   public static String wrapArgument(String var0) {
      char var1 = 0;
      if (var0.contains(" ")) {
         if (var0.contains("\"")) {
            var1 = '\'';
         } else {
            var1 = '"';
         }
      } else if (var0.contains("\"")) {
         var1 = '\'';
      } else if (var0.contains("'")) {
         var1 = '"';
      }

      return var1 != 0 ? var1 + var0 + var1 : var0;
   }
}
