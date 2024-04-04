package necesse.engine.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class WorldFileSystem implements AutoCloseable {
   private final FileSystem fs;
   public final String fileName;
   private final File archiveFile;
   public final String archiveFolderName;
   public final boolean isArchive;
   private String pathPrepend;

   public WorldFileSystem(String var1, boolean var2, boolean var3) throws IOException, FileSystemClosedException {
      this(var1, var1, var2, var3);
   }

   public WorldFileSystem(String var1, String var2, boolean var3, boolean var4) throws IOException, FileSystemClosedException {
      Path var15;
      if (!var1.endsWith(".zip") && !var1.endsWith(".rar")) {
         this.archiveFile = null;
         this.fileName = Paths.get(var1).getFileName().toString();
         if (var4) {
            String var14 = System.getProperty("user.dir").replace("\\", "/");
            if (var3) {
               var15 = FileSystems.getDefault().getPath(var14 + "/" + var1);
               if (!Files.exists(var15, new LinkOption[0]) && !Files.isDirectory(var15, new LinkOption[0])) {
                  Files.createDirectories(var15);
               }
            }

            this.pathPrepend = var14 + "/" + this.fileName + "/";
         } else {
            if (var3) {
               Path var16 = FileSystems.getDefault().getPath(var1);
               if (!Files.exists(var16, new LinkOption[0]) && !Files.isDirectory(var16, new LinkOption[0])) {
                  Files.createDirectories(var16);
               }
            }

            this.pathPrepend = var1 + "/";
         }

         this.archiveFolderName = this.fileName;
         this.fs = FileSystems.getDefault();
         this.isArchive = false;
      } else {
         var1 = var1.replace("\\", "/");
         var2 = var2.replace("\\", "/");
         this.fileName = Paths.get(var1).getFileName().toString();
         URI var5;
         if (var4) {
            String var6 = System.getProperty("user.dir").replace("\\", "/");
            if (var3) {
               Path var7 = FileSystems.getDefault().getPath(var6 + "/" + var1).getParent();
               if (var7 != null && !Files.exists(var7, new LinkOption[0]) && !Files.isDirectory(var7, new LinkOption[0])) {
                  Files.createDirectories(var7);
               }
            }

            var5 = URI.create("jar:file:" + (var6.startsWith("/") ? "" : "/") + var6 + "/" + var2.replace(" ", "%20"));
            this.archiveFile = new File(var6 + "/" + var1);
         } else {
            if (var3) {
               var15 = FileSystems.getDefault().getPath(var1).getParent();
               if (var15 != null && !Files.exists(var15, new LinkOption[0]) && !Files.isDirectory(var15, new LinkOption[0])) {
                  Files.createDirectories(var15);
               }
            }

            var5 = URI.create("jar:file:" + (var2.startsWith("/") ? "" : "/") + var2.replace(" ", "%20"));
            this.archiveFile = new File("/" + var1);
         }

         HashMap var17 = new HashMap();
         if (var3) {
            var17.put("create", "true");
         }

         FileSystem var18;
         try {
            var18 = FileSystems.getFileSystem(var5);
         } catch (FileSystemNotFoundException var13) {
            var18 = FileSystems.newFileSystem(var5, var17, (ClassLoader)null);
         }

         if (!var18.isOpen()) {
            throw new FileSystemClosedException("File system has been closed");
         }

         this.fs = var18;
         this.isArchive = true;
         String var8 = this.fileName.substring(0, this.fileName.length() - 4);
         this.pathPrepend = var8 + "/";
         if (!this.worldEntityFileExists()) {
            String var9 = var8;
            this.pathPrepend = "/";
            boolean var10 = false;
            Iterator var11 = this.getPathsInDirectory("").iterator();

            while(var11.hasNext()) {
               WorldFile var12 = (WorldFile)var11.next();
               if (var12.isDirectory()) {
                  this.pathPrepend = var12.getFileName().toString() + "/";
                  if (this.worldEntityFileExists()) {
                     var9 = var12.getFileName().toString();
                     var10 = true;
                     break;
                  }
               }
            }

            if (!var10) {
               this.pathPrepend = var8 + "/";
            }

            this.archiveFolderName = var9;
         } else {
            this.archiveFolderName = var8;
         }
      }

   }

   public WorldFileSystem(String var1, boolean var2) throws IOException, FileSystemClosedException {
      this(var1, var2, true);
   }

   public void fixArchiveFirstFolder(String var1, String var2) throws IOException {
      if (!this.isArchive) {
         throw new IllegalStateException("Should only be used on archive saves");
      } else {
         (new WorldFile(this.fs.getPath(var1))).moveTo(this.fs.getPath(var2), StandardCopyOption.REPLACE_EXISTING);
      }
   }

   public void fixArchiveFirstFolder(String var1) throws IOException {
      this.fixArchiveFirstFolder(this.archiveFolderName, var1);
   }

   public WorldFile file(String var1) {
      return new WorldFile(this.fs.getPath(this.pathPrepend + var1));
   }

   public boolean exists(String var1) {
      return this.file(var1).exists();
   }

   public boolean isDirectory(String var1) {
      return this.file(var1).isDirectory();
   }

   public Iterator<String> iterateFilesInDirectory(String var1) throws IOException {
      return this.file(var1).iterateFilesInDirectory();
   }

   public Iterable<String> getFilesInDirectory(String var1) throws IOException {
      return this.file(var1).getFilesInDirectory();
   }

   public Iterator<WorldFile> iteratePathsInDirectory(String var1) throws IOException {
      return this.file(var1).iteratePathsInDirectory();
   }

   public Iterable<WorldFile> getPathsInDirectory(String var1) throws IOException {
      return this.file(var1).getPathsInDirectory();
   }

   public BufferedWriter fileWriter(String var1, Charset var2, boolean var3) throws IOException {
      return this.file(var1).writer(var2, var3);
   }

   public BufferedWriter fileWriter(String var1, boolean var2) throws IOException {
      return this.file(var1).writer(var2);
   }

   public void writeFile(String var1, byte[] var2, boolean var3) throws IOException {
      this.file(var1).write(var2, var3);
   }

   public void writeFile(String var1, byte[] var2) throws IOException {
      this.file(var1).write(var2);
   }

   public byte[] readFile(String var1) throws IOException {
      return this.file(var1).read();
   }

   public BufferedReader fileReader(String var1, Charset var2) throws IOException {
      return this.file(var1).reader(var2);
   }

   public BufferedReader fileReader(String var1) throws IOException {
      return this.file(var1).reader();
   }

   public InputStream inputStream(String var1, OpenOption... var2) throws IOException {
      return this.file(var1).inputStream(var2);
   }

   public OutputStream outputStream(String var1, OpenOption... var2) throws IOException {
      return this.file(var1).outputStream(var2);
   }

   public boolean deleteFile(String var1) throws IOException {
      return this.file(var1).delete();
   }

   public FileTime getLastModified() throws IOException {
      if (this.archiveFile != null) {
         return FileTime.fromMillis(this.archiveFile.lastModified());
      } else {
         return this.worldEntityFileExists() ? Files.getLastModifiedTime(this.getWorldEntityFile().path) : FileTime.fromMillis(0L);
      }
   }

   public boolean isOpen() {
      return this.fs.isOpen();
   }

   public void close() throws IOException {
      if (this.isArchive) {
         this.fs.close();
      }

   }

   public boolean worldEntityFileExists() {
      WorldFile var1 = this.getWorldEntityFile();
      return var1.exists() && !var1.isDirectory();
   }

   public WorldFile getWorldEntityFile() {
      return this.file("world.dat");
   }

   public boolean worldSettingsFileExists() {
      WorldFile var1 = this.getWorldSettingsFile();
      return var1.exists() && !var1.isDirectory();
   }

   public WorldFile getWorldSettingsFile() {
      return this.file("worldSettings.cfg");
   }

   public boolean levelFileExists(Level var1) {
      WorldFile var2 = this.getLevelFile(var1);
      return var2.exists() && !var2.isDirectory();
   }

   public WorldFile getLevelFile(Level var1) {
      return this.getLevelFile(var1.getIdentifier());
   }

   public boolean levelFileExists(LevelIdentifier var1) {
      WorldFile var2 = this.getLevelFile(var1);
      return var2.exists() && !var2.isDirectory();
   }

   public WorldFile getLevelFile(LevelIdentifier var1) {
      return this.file("levels/" + var1.stringID + ".dat");
   }

   public boolean playerFileExists(ServerClient var1) {
      WorldFile var2 = this.getPlayerFile(var1);
      return var2.exists() && !var2.isDirectory();
   }

   public WorldFile getPlayerFile(ServerClient var1) {
      return this.getPlayerFile(var1.authentication);
   }

   public boolean playerFileExists(long var1) {
      WorldFile var3 = this.getPlayerFile(var1);
      return var3.exists() && !var3.isDirectory();
   }

   public WorldFile getPlayerFile(long var1) {
      return this.file("players/" + var1 + ".dat");
   }

   public boolean playerMapFileExists(ServerClient var1) {
      WorldFile var2 = this.getMapPlayerFile(var1);
      return var2.exists() && !var2.isDirectory();
   }

   public WorldFile getMapPlayerFile(ServerClient var1) {
      return this.getMapPlayerFile(var1.authentication);
   }

   public boolean playerMapFileExists(long var1) {
      WorldFile var3 = this.getMapPlayerFile(var1);
      return var3.exists() && !var3.isDirectory();
   }

   public WorldFile getMapPlayerFile(long var1) {
      return this.file("players/" + var1 + ".dat" + "map");
   }

   public LinkedList<WorldFile> getLevelFiles() {
      LinkedList var1 = new LinkedList();

      try {
         Iterator var2 = this.getPathsInDirectory("levels").iterator();

         while(var2.hasNext()) {
            WorldFile var3 = (WorldFile)var2.next();
            if (var3.getFileName().toString().endsWith(".dat")) {
               var1.add(var3);
            }
         }
      } catch (IOException var4) {
         System.err.println("Error getting level files");
         var4.printStackTrace();
      }

      return var1;
   }

   public LinkedList<WorldFile> getPlayerFiles() {
      LinkedList var1 = new LinkedList();

      try {
         Iterator var2 = this.getPathsInDirectory("players").iterator();

         while(var2.hasNext()) {
            WorldFile var3 = (WorldFile)var2.next();
            if (var3.getFileName().toString().endsWith(".dat")) {
               var1.add(var3);
            }
         }
      } catch (IOException var4) {
         System.err.println("Error getting player files");
         var4.printStackTrace();
      }

      return var1;
   }
}
