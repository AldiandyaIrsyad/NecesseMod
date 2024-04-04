package necesse.engine.save;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.zip.ZipError;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldFileSystem;
import necesse.engine.world.WorldSettings;

public class WorldSave implements Comparable<WorldSave> {
   public static String LATEST_BACKUP_NAME = "LATEST_BACKUP";
   public static Pattern BACKUP_PATTERN;
   public static int MAX_LATEST_BACKUPS;
   public final String displayName;
   public final File filePath;
   public final String archiveFolderName;
   private String dateModified;
   private long timeModified;
   private World world;
   public ServerSettings serverSettings;

   public static boolean isLatestBackup(String var0) {
      String var1 = GameUtils.removeFileExtension(var0);
      return BACKUP_PATTERN.matcher(var1).matches();
   }

   public static File getNextBackupPath(boolean var0) {
      String var1 = LATEST_BACKUP_NAME + (var0 ? "" : ".zip");
      long var2 = Long.MAX_VALUE;
      if (MAX_LATEST_BACKUPS <= 1) {
         return new File(World.getWorldsPath() + var1);
      } else {
         for(int var4 = 0; var4 < MAX_LATEST_BACKUPS; ++var4) {
            String var5 = LATEST_BACKUP_NAME + (var4 + 1) + (var0 ? "" : ".zip");
            File var6 = new File(World.getWorldsPath() + var5);
            if (!var6.exists()) {
               return new File(World.getWorldsPath() + var5);
            }

            long var7 = var6.lastModified();
            if (var6.isDirectory()) {
               File var9 = new File(World.getWorldsPath() + var5 + "/world" + ".dat");
               if (var9.exists()) {
                  var7 = var9.lastModified();
               }
            }

            if (var7 < var2) {
               var1 = var5;
               var2 = var7;
            }
         }

         return new File(World.getWorldsPath() + var1);
      }
   }

   public WorldSave(ServerSettings var1) throws IOException, FileSystemClosedException {
      this.filePath = var1.worldFilePath;
      this.displayName = World.getWorldDisplayName(var1.worldFilePath.getName());
      this.world = World.getSaveDataWorld(var1.worldFilePath);
      this.archiveFolderName = this.world.fileSystem.archiveFolderName;
      this.serverSettings = var1;
      this.world.closeFileSystem();
   }

   public WorldSave(File var1, boolean var2, boolean var3) throws IOException, ZipError, FileSystemClosedException {
      this.filePath = var1;
      this.displayName = World.getWorldDisplayName(var1.getName());
      this.dateModified = "N/A";
      if (var2) {
         this.world = World.getSaveDataWorld(var1);
         this.archiveFolderName = this.world.fileSystem.archiveFolderName;
         if (this.world.worldEntity != null) {
            SimpleDateFormat var4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.timeModified = this.world.getLastModified().toMillis();
            this.dateModified = var4.format(this.timeModified);
         }

         if (var3) {
            this.closeWorldFileSystem();
         }
      } else {
         this.world = null;
         String var11 = var1.getName();

         try {
            WorldFileSystem var5 = World.getFileSystem(var1, false);

            try {
               var11 = var5.archiveFolderName;
               if (var5.worldEntityFileExists()) {
                  this.timeModified = var5.getLastModified().toMillis();
               }
            } catch (Throwable var9) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (IOException var10) {
            this.timeModified = 0L;
         }

         this.archiveFolderName = var11;
         SimpleDateFormat var12 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         this.dateModified = var12.format(this.timeModified);
      }

   }

   public WorldSettings worldSettings() {
      return this.world.settings;
   }

   public boolean isValid() {
      return this.world.worldEntity != null;
   }

   public String getDate() {
      return this.dateModified;
   }

   public int getWorldDay() {
      return this.world.worldEntity != null ? this.world.worldEntity.getDay() : 0;
   }

   public int getWorldTime() {
      return this.world.worldEntity != null ? this.world.worldEntity.getDayTimeInt() : 0;
   }

   public String getWorldTimeReadable() {
      return this.world.worldEntity != null ? this.world.worldEntity.getDayTimeReadable() : "00:00";
   }

   public World getWorld() {
      return this.world;
   }

   public WorldEntity getWorldEntity() {
      return this.world.worldEntity;
   }

   public void closeWorldFileSystem() throws IOException {
      if (this.world != null) {
         this.world.closeFileSystem();
      }

   }

   public int compareTo(WorldSave var1) {
      return Long.compare(var1.timeModified, this.timeModified);
   }

   public static void loadSaves(Consumer<WorldSave> var0, Supplier<Boolean> var1, Consumer<Boolean> var2, int var3) {
      loadSaves(true, var0, var1, var2, var3);
   }

   public static void loadSaves(boolean var0, Consumer<WorldSave> var1, Supplier<Boolean> var2, Consumer<Boolean> var3, int var4) {
      if (var2 != null && (Boolean)var2.get()) {
         if (var3 != null) {
            var3.accept(true);
         }

      } else {
         ArrayList var5 = new ArrayList();
         String[] var6 = World.loadWorldsFromPaths();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            File[] var10 = (new File(var9)).listFiles();
            if (var10 == null) {
               var10 = new File[0];
            }

            File[] var11 = var10;
            int var12 = var10.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               File var14 = var11[var13];
               if (var14.isDirectory()) {
                  File var15 = GameUtils.resolveFile(var14, "world.dat");
                  if (var15.exists()) {
                     if (isLatestBackup(var14.getName())) {
                        var5.add(new ObjectValue(var14, (new ComparableSequence(-100000L)).thenBy((Comparable)var15.lastModified())));
                     } else {
                        var5.add(new ObjectValue(var14, new ComparableSequence(var15.lastModified())));
                     }
                  }
               } else if (var14.isFile()) {
                  if (isLatestBackup(var14.getName())) {
                     var5.add(new ObjectValue(var14, (new ComparableSequence(-100000L)).thenBy((Comparable)var14.lastModified())));
                  } else {
                     var5.add(new ObjectValue(var14, new ComparableSequence(var14.lastModified())));
                  }
               }
            }
         }

         Comparator var21 = Comparator.comparing((var0x) -> {
            return (ComparableSequence)var0x.value;
         });
         var5.sort(var21.reversed());
         var7 = 0;
         Iterator var22 = var5.iterator();

         while(var22.hasNext()) {
            ObjectValue var23 = (ObjectValue)var22.next();
            if (var2 != null && (Boolean)var2.get()) {
               if (var3 != null) {
                  var3.accept(true);
               }

               return;
            }

            if (var4 >= 0 && var7 >= var4) {
               break;
            }

            boolean var24 = false;

            try {
               WorldFileSystem var25 = World.getFileSystem((File)var23.object, true);

               try {
                  var24 = var25.worldEntityFileExists();
               } catch (Throwable var18) {
                  if (var25 != null) {
                     try {
                        var25.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }
                  }

                  throw var18;
               }

               if (var25 != null) {
                  var25.close();
               }
            } catch (ZipError | IOException var19) {
               System.err.println("Error loading save world: " + var23.object);
               var19.printStackTrace();
            } catch (FileSystemClosedException var20) {
               System.err.println("Error loading save world because file system is not closed: " + var23.object);
            }

            if (var24) {
               try {
                  WorldSave var26 = new WorldSave((File)var23.object, var0, true);
                  if (var2 != null && (Boolean)var2.get()) {
                     if (var3 != null) {
                        var3.accept(true);
                     }

                     return;
                  }

                  var1.accept(var26);
                  ++var7;
               } catch (Exception var16) {
                  System.err.println("Could not load save file " + var23.object);
                  var16.printStackTrace();
               }
            }
         }

         if (var3 != null) {
            var3.accept(false);
         }

      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((WorldSave)var1);
   }

   static {
      BACKUP_PATTERN = Pattern.compile(LATEST_BACKUP_NAME + "(\\d+)?");
      MAX_LATEST_BACKUPS = 5;
   }
}
