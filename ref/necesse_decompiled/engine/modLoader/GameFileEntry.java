package necesse.engine.modLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GameFileEntry {
   public final String path;
   public final String name;
   public final boolean isDirectory;
   public final InputStreamSupplier inputStreamSupplier;

   public GameFileEntry(String var1, File var2) {
      this.path = var1.replace("\\", "/");
      this.name = (new File(var1)).getName();
      this.isDirectory = var2.isDirectory();
      this.inputStreamSupplier = () -> {
         return new FileInputStream(var2);
      };
   }

   public GameFileEntry(ZipFile var1, ZipEntry var2) {
      this.path = var2.getName();
      this.name = (new File(var2.getName())).getName();
      this.isDirectory = var2.isDirectory();
      this.inputStreamSupplier = () -> {
         return var1.getInputStream(var2);
      };
   }

   public String getPath() {
      return this.path;
   }

   public String getName() {
      return this.name;
   }

   public boolean isDirectory() {
      return this.isDirectory;
   }

   public InputStream getFileInputStream() throws IOException {
      return this.inputStreamSupplier.get();
   }

   public static void addFileEntries(File var0, String var1, List<GameFileEntry> var2) {
      File[] var3 = var0.listFiles();
      if (var3 != null) {
         File[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File var7 = var4[var6];
            if (!var7.getName().startsWith(".")) {
               String var8 = Paths.get(var1, var7.getName()).toString();
               if (var7.isDirectory()) {
                  var2.add(new GameFileEntry(var8, var7));
                  addFileEntries(var7, var8 + '/', var2);
               } else {
                  var2.add(new GameFileEntry(var8, var7));
               }
            }
         }

      }
   }
}
