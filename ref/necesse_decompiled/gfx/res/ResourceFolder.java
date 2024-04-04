package necesse.gfx.res;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import necesse.engine.modLoader.GameFileEntry;
import necesse.engine.modLoader.LoadedMod;

public class ResourceFolder {
   public final HashMap<String, ResourceFile> files;

   public ResourceFolder(List<GameFileEntry> var1) throws IOException {
      this.files = new HashMap();
      this.addModResources(var1, (LoadedMod)null);
   }

   public ResourceFolder(File var1) throws IOException {
      Objects.requireNonNull(var1);
      if (!var1.isDirectory()) {
         throw new IllegalStateException("Folder doesn't exist or is not directory");
      } else {
         HashMap var2 = new HashMap();
         this.addResourceFiles("", var1, var2);
         this.files = var2;
      }
   }

   public ResourceFolder() {
      this.files = new HashMap();
   }

   private void addResourceFiles(String var1, File var2, HashMap<String, ResourceFile> var3) throws IOException {
      File[] var4 = var2.listFiles();
      if (var4 == null) {
         throw new NullPointerException("Could not read files inside folder " + var2.getPath());
      } else {
         File[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File var8 = var5[var7];
            if (var8.isDirectory()) {
               this.addResourceFiles(var1 + var8.getName() + "/", var8, var3);
            } else {
               String var9 = var1 + var8.getName();
               boolean var10 = true;
               String[] var11 = ResourceEncoder.ignoreFiles;
               int var12 = var11.length;

               int var13;
               String var14;
               for(var13 = 0; var13 < var12; ++var13) {
                  var14 = var11[var13];
                  if (var14.equals(var9)) {
                     var10 = false;
                     break;
                  }
               }

               if (var10) {
                  var11 = ResourceEncoder.fileExtensions;
                  var12 = var11.length;

                  for(var13 = 0; var13 < var12; ++var13) {
                     var14 = var11[var13];
                     if (var8.getName().endsWith(var14)) {
                        var3.put(var9, new ResourceFile(var9, -1, () -> {
                           return Files.newInputStream(var8.toPath());
                        }));
                        break;
                     }
                  }
               }
            }
         }

      }
   }

   public void addModResources(Iterable<GameFileEntry> var1, LoadedMod var2) throws IOException {
      Iterator var3 = var1.iterator();

      while(true) {
         while(true) {
            GameFileEntry var4;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  var4 = (GameFileEntry)var3.next();
               } while(!var4.getPath().startsWith("resources/"));
            } while(var4.getPath().equals("resources/preview.png"));

            String var5 = var4.getPath().substring("resources/".length());
            if (var5.equals("res.data")) {
               Objects.requireNonNull(var4);
               GameStreamReader var12 = new GameStreamReader(var4::getFileInputStream);

               try {
                  this.read(var12);
               } catch (Throwable var11) {
                  try {
                     var12.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               var12.close();
            } else {
               String[] var6 = ResourceEncoder.fileExtensions;
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String var9 = var6[var8];
                  if (var5.endsWith(var9)) {
                     if (var2 != null && this.files.containsKey(var5)) {
                        System.out.println("Overwriting resource at " + var5 + " by mod " + var2.id);
                     }

                     HashMap var10000 = this.files;
                     Objects.requireNonNull(var4);
                     var10000.put(var5, new ResourceFile(var5, -1, var4::getFileInputStream));
                     break;
                  }
               }
            }
         }
      }
   }

   public void read(GameStreamReader var1) throws IOException {
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.readInt();
         byte[] var5 = var1.readBytes(var4);
         String var6 = new String(var5);
         int var7 = var1.readInt();
         this.files.put(var6, new ResourceFile(var6, var7, var1.getSupplierAtCurrent()));
         var1.skipBytes((long)var7);
      }

   }

   public long write(GameStreamWriter var1) throws IOException {
      long var2 = 0L;
      var1.writeInt(this.files.size());
      Iterator var4 = this.files.values().iterator();

      while(var4.hasNext()) {
         ResourceFile var5 = (ResourceFile)var4.next();
         byte[] var6 = var5.path.getBytes();
         var1.writeInt(var6.length);
         var1.writeBytes(var6);
         byte[] var7 = var5.loadBytes(true);
         var2 += (long)var7.length;
         var1.writeInt(var7.length);
         var1.writeBytes(var7);
      }

      return var2;
   }

   public void printTree() {
      ArrayList var1 = new ArrayList(this.files.values());
      var1.sort(Comparator.comparing((var0) -> {
         return var0.path;
      }));
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ResourceFile var3 = (ResourceFile)var2.next();
         System.out.println(var3.path);
      }

   }
}
