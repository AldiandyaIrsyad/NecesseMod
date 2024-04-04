package necesse.engine;

import com.codedisaster.steamworks.SteamLibraryLoader;
import org.lwjgl.system.Library;
import org.lwjgl.system.Platform;
import org.lwjgl.system.Platform.Architecture;

public class GameSteamLibraryLoader implements SteamLibraryLoader {
   public GameSteamLibraryLoader() {
   }

   public void setLibraryPath(String var1) {
      System.setProperty("org.lwjgl.librarypath", var1);
   }

   public boolean loadLibrary(String var1) {
      Platform var2 = Platform.get();
      Platform.Architecture var3 = Platform.getArchitecture();
      if (var2 == Platform.WINDOWS && var3 == Architecture.X64) {
         var1 = var1 + "64";
      }

      try {
         Library.loadSystem("com.codedisaster.steamworks", var1);
         return true;
      } catch (Throwable var5) {
         return false;
      }
   }
}
