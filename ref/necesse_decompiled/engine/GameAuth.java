package necesse.engine;

import com.codedisaster.steamworks.SteamNativeHandle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
import necesse.engine.steam.SteamData;
import necesse.engine.util.GameUtils;

public class GameAuth implements Serializable {
   private static GameAuth gameAuth;
   private static long tempAuth = 0L;
   private static long steamAuth = 0L;
   private UUID authentication;

   private GameAuth(UUID var1) {
      this.authentication = var1;
   }

   public static void setTempAuth(long var0) {
      tempAuth = var0;
   }

   public static void resetTempAuth() {
      tempAuth = 0L;
   }

   public static long getAuthentication() {
      if (tempAuth != 0L) {
         return tempAuth;
      } else {
         return steamAuth != 0L ? steamAuth : gameAuth.authentication.getMostSignificantBits() & Long.MAX_VALUE;
      }
   }

   private static String getFilePath() {
      return GlobalData.cfgPath() + "auth";
   }

   private static File getFile() {
      return new File(getFilePath());
   }

   public static void loadAuth() {
      if (SteamData.isCreated()) {
         steamAuth = SteamNativeHandle.getNativeHandle(SteamData.getSteamID());
      } else {
         File var0 = getFile();
         if (!var0.exists()) {
            generateDefaultAuth();
         } else {
            try {
               FileInputStream var2 = new FileInputStream(var0);
               ObjectInputStream var3 = new ObjectInputStream(var2);
               GameAuth var1 = (GameAuth)var3.readObject();
               var3.close();
               if (var1 == null) {
                  throw new FileNotFoundException();
               }

               gameAuth = var1;
            } catch (Exception var4) {
               GameUtils.deleteFileOrFolder(getFile());
               generateDefaultAuth();
            }

         }
      }
   }

   private static void generateDefaultAuth() {
      UUID var0 = UUID.randomUUID();
      gameAuth = new GameAuth(var0);
      saveAuth(getFile(), gameAuth);
   }

   public static void generateNewAuth() {
      resetTempAuth();
      backupAuth(gameAuth);
      GameAuth var0 = new GameAuth(UUID.randomUUID());
      if (saveAuth(getFile(), var0)) {
         gameAuth = var0;
         System.out.println("Changed authentication to " + getAuthentication());
      }

   }

   private static boolean backupAuth(GameAuth var0) {
      int var1 = 1;

      File var2;
      for(var2 = new File(getFilePath() + "bak" + var1); var2.exists(); var2 = new File(getFilePath() + "bak" + var1)) {
         ++var1;
      }

      return saveAuth(var2, var0);
   }

   private static boolean saveAuth(File var0, GameAuth var1) {
      if (!var0.getParentFile().exists() && !var0.getParentFile().mkdirs()) {
         System.out.print("Could not create folders for authentication");
         return false;
      } else {
         try {
            FileOutputStream var2 = new FileOutputStream(var0);
            ObjectOutputStream var3 = new ObjectOutputStream(var2);
            var3.writeObject(var1);
            var3.close();
            return true;
         } catch (Exception var4) {
            System.err.print("Could not save authentication");
            var4.printStackTrace();
            return false;
         }
      }
   }
}
