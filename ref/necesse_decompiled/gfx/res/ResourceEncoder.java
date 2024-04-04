package necesse.gfx.res;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import necesse.engine.GlobalData;
import necesse.engine.modLoader.GameFileEntry;
import necesse.engine.modLoader.LoadedMod;

public class ResourceEncoder {
   public static final String resDataFile = "res.data";
   public static final String jarResourcePath = "resources/";
   public static final String previewImagePath = "resources/preview.png";
   public static final String[] fileExtensions = new String[]{"png", "ogg", "glsl", "ttf"};
   public static final String[] ignoreFiles = new String[]{"examplefolder/examplefile.png"};
   private static ResourceFolder resources = null;

   public ResourceEncoder() {
   }

   public static boolean isLoaded() {
      return resources != null;
   }

   public static void loadResourceFile() {
      try {
         resources = new ResourceFolder(getFileResources());
      } catch (IOException var1) {
         throw new RuntimeException("Failed to load game resources", var1);
      }
   }

   private static List<GameFileEntry> getFileResources() {
      ArrayList var0 = new ArrayList();
      File var1 = new File(GlobalData.rootPath() + "res.data");
      if (var1.isFile()) {
         var0.add(new GameFileEntry("resources/res.data", var1));
      }

      return var0;
   }

   public static void addModResources(LoadedMod var0) {
      if (resources == null) {
         throw new IllegalStateException("Resources not loaded yet.");
      } else {
         try {
            Iterator var1 = var0.jarFile.stream().map((var1x) -> {
               return new GameFileEntry(var0.jarFile, var1x);
            }).iterator();
            resources.addModResources(() -> {
               return var1;
            }, var0);
         } catch (IOException var2) {
            System.err.println("Could not load mod " + var0.id + " resources");
            var2.printStackTrace();
         }

      }
   }

   public static Set<Map.Entry<String, ResourceFile>> getAllFiles() {
      return resources.files.entrySet();
   }

   public static byte[] getResourceBytes(String var0) throws IOException {
      if (resources == null) {
         throw new IllegalStateException("Resources not loaded yet.");
      } else {
         ResourceFile var1 = (ResourceFile)resources.files.get(var0);
         if (var1 != null) {
            return var1.loadBytes(true);
         } else {
            throw new FileNotFoundException("Could not find resource file \"" + var0 + "\"");
         }
      }
   }

   public static InputStream getResourceInputStream(String var0) throws IOException {
      return new ByteArrayInputStream(getResourceBytes(var0));
   }
}
