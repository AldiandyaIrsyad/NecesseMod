package necesse.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class GameCache {
   public static final String suffix = ".cache";
   private static boolean gameUpdated = false;
   private static String prevGameVersion = null;

   public GameCache() {
   }

   public static String cachePath() {
      return GlobalData.appDataPath() + "cache/";
   }

   public static boolean gameUpdated() {
      return gameUpdated;
   }

   public static String getPreviousGameVersion() {
      return prevGameVersion;
   }

   public static boolean isFirstLaunch() {
      return gameUpdated() && getPreviousGameVersion() == null;
   }

   public static void checkCacheVersion() {
      gameUpdated = false;
      prevGameVersion = null;
      if (cacheFileExists("version")) {
         try {
            prevGameVersion = new String(getBytes("version"));
            gameUpdated = !prevGameVersion.equals(GameInfo.getFullVersionString());
         } catch (Exception var1) {
            GameLog.warn.println("Could not read cache version file: " + var1.getMessage());
            gameUpdated = true;
         }
      } else {
         gameUpdated = true;
         cacheBytes(GameInfo.getFullVersionString().getBytes(), "version");
      }

      if (gameUpdated()) {
         if (isFirstLaunch()) {
            System.out.println("First time launched");
         } else {
            System.out.println("First time launch since version " + prevGameVersion);
         }

         clearCacheFolder("version");
         System.out.println("Cache version was not correct, deleted version cache files.");
         cacheBytes(GameInfo.getFullVersionString().getBytes(), "version");
      }

   }

   public static void clearCacheFolder(String var0) {
      File var1 = new File(cachePath() + (var0 == null ? "" : var0 + "/"));
      if (var1.exists() && var1.isDirectory()) {
         try {
            File[] var2 = var1.listFiles();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File var5 = var2[var4];
               GameUtils.deleteFileOrFolder(var5.getPath());
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

   }

   public static boolean cacheFileExists(String var0) {
      return (new File(cachePath() + var0 + ".cache")).exists();
   }

   public static boolean removeCache(String var0) {
      if (cacheFileExists(var0)) {
         File var1 = new File(cachePath() + var0 + ".cache");
         return var1.delete();
      } else {
         return false;
      }
   }

   public static void cacheObject(Object var0, String var1) {
      File var2 = new File(cachePath() + var1 + ".cache");
      if (!GameUtils.mkDirs(var2)) {
         System.out.println("Could not create folders for cache files");
      } else {
         try {
            FileOutputStream var3 = new FileOutputStream(var2);
            ObjectOutputStream var4 = new ObjectOutputStream(var3);
            var4.writeObject(var0);
            var4.close();
         } catch (Exception var5) {
            var5.printStackTrace();
         }

      }
   }

   public static Object getObject(String var0) {
      File var1 = new File(cachePath() + var0 + ".cache");
      if (!var1.exists()) {
         return null;
      } else {
         Object var2 = null;

         try {
            FileInputStream var3 = new FileInputStream(var1);
            ObjectInputStream var4 = new ObjectInputStream(var3);
            var2 = var4.readObject();
            var4.close();
         } catch (Exception var5) {
            if (!gameUpdated()) {
               GameLog.warn.println("Could not read object from cache file " + var1.getPath() + " : " + var5.getMessage());
            }
         }

         return var2;
      }
   }

   public static void cacheSave(SaveData var0, String var1) {
      File var2 = new File(cachePath() + var1 + ".cache");
      var0.saveScript(var2);
   }

   public static LoadData getSave(String var0) {
      File var1 = new File(cachePath() + var0 + ".cache");
      return !var1.exists() ? null : new LoadData(var1);
   }

   public static void cacheBytes(byte[] var0, String var1) {
      try {
         GameUtils.saveByteFile(var0, cachePath() + var1 + ".cache");
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static byte[] getBytes(String var0) {
      try {
         return GameUtils.loadByteFile(cachePath() + var0 + ".cache");
      } catch (Exception var2) {
         System.err.println("Could load byte cache file: " + var2.getMessage());
         return null;
      }
   }
}
