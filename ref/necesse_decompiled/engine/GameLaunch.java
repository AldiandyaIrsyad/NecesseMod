package necesse.engine;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTexture.GameTexture;

public class GameLaunch {
   public static HashMap<String, String> launchOptions;
   public static String fullLaunchParameters = null;
   public static String instantConnect = null;
   public static String instantLobbyConnect = null;
   private static final Pattern argsPattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

   public GameLaunch() {
   }

   public static String[] quoteArgs(String[] var0) {
      String[] var1 = new String[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         String var3 = var0[var2];
         if (!var3.contains(" ") || var3.startsWith("\"") && var3.endsWith("\"")) {
            var1[var2] = var3;
         } else {
            var1[var2] = "\"" + var3 + "\"";
         }
      }

      return var1;
   }

   public static HashMap<String, String> parseLaunchOptions(String[] var0) {
      var0 = quoteArgs(var0);
      String var1 = GameUtils.join(var0, " ");
      int var2 = 0;
      HashMap var3 = new HashMap();

      while(var2 < var1.length()) {
         char var5 = var1.charAt(var2);
         int var6;
         if (var5 != '-' && var5 != '+') {
            var6 = Math.max(var1.indexOf("-", var2), var1.indexOf("+", var2));
            if (var6 == -1) {
               break;
            }

            var2 = var6;
         } else {
            var6 = var1.indexOf(" ", var2);
            String var4;
            if (var6 != -1) {
               var4 = var1.substring(var2 + 1, var6);
               var2 = var6 + 1;
               String var7 = null;
               Matcher var8 = argsPattern.matcher(var1.substring(var2));
               if (var8.find()) {
                  if (var8.group(1) != null) {
                     var7 = var8.group(1);
                  } else if (var8.group(2) != null) {
                     var7 = var8.group(2);
                  } else {
                     var7 = var8.group();
                  }
               }

               if (var7 != null) {
                  if (!var7.startsWith("-") && !var7.startsWith("+")) {
                     var3.put(var4, var7);
                     continue;
                  }

                  var3.put(var4, "");
                  continue;
               }

               var3.put(var4, "");
               break;
            }

            var4 = var1.substring(var2 + 1);
            var3.put(var4, "");
            break;
         }
      }

      return var3;
   }

   public static HashMap<String, String> parseAndHandleLaunchOptions(String[] var0) {
      HashMap var1 = parseLaunchOptions(var0);
      fullLaunchParameters = GameUtils.join(quoteArgs(var0), " ");
      if (var0.length > 0) {
         System.out.println("Launched game with arguments: " + fullLaunchParameters);
      }

      if (var1.containsKey("dev")) {
         GlobalData.setDevMode();
         String var2 = (String)var1.get("dev");
         if (!var2.isEmpty()) {
            try {
               long var3 = Long.parseLong(var2);
               if (var3 > 0L && var3 <= 500L) {
                  GameAuth.setTempAuth(var3);
               } else {
                  System.err.println("Invalid authentication number: " + var3);
                  System.exit(0);
               }
            } catch (NumberFormatException var5) {
               System.err.println("Authentication argument must be a number.");
               System.exit(0);
            }
         }
      }

      if (var1.containsKey("lowmemory")) {
         GlobalData.setLowMemoryMode();
      }

      if (var1.containsKey("memorydebug")) {
         GameTexture.memoryDebug = true;
      }

      if (var1.containsKey("mod")) {
         ModLoader.devMod = (String)var1.get("mod");
      }

      if (var1.containsKey("disablemods")) {
         ModLoader.disableMods = true;
      }

      if (var1.containsKey("connect")) {
         instantConnect = (String)var1.get("connect");
      }

      if (var1.containsKey("connect_lobby")) {
         instantLobbyConnect = (String)var1.get("connect_lobby");
      }

      return var1;
   }
}
