package necesse.gfx.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.gfx.res.ResourceEncoder;
import org.lwjgl.opengl.GL20;

public class ShaderLoader {
   public ShaderLoader() {
   }

   public static int loadFragmentShader(String var0) {
      return loadShader(var0, 35632);
   }

   public static int loadVertexShader(String var0) {
      return loadShader(var0, 35633);
   }

   public static int loadShader(String var0, int var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = GL20.glCreateShader(var1);

      try {
         if (var1 == 35633) {
            var0 = "shaders/vertex/" + var0 + ".glsl";
         } else if (var1 == 35632) {
            var0 = "shaders/fragment/" + var0 + ".glsl";
         }

         boolean var4 = false;
         File var6 = new File(GlobalData.rootPath() + "res/" + var0);
         Object var5;
         if (var6.exists()) {
            var5 = new FileInputStream(var6);
            var4 = true;
         } else {
            try {
               var5 = ResourceEncoder.getResourceInputStream(var0);
               var4 = true;
            } catch (FileNotFoundException var9) {
               var5 = new FileInputStream(var6);
            }
         }

         BufferedReader var7 = new BufferedReader(new InputStreamReader((InputStream)var5));

         String var8;
         while((var8 = var7.readLine()) != null) {
            var2.append(var8).append("\n");
         }

         var7.close();
         ((InputStream)var5).close();
         if (!var4 && !GlobalData.isDevMode()) {
            GameLog.warn.println(var0 + " was not found in resource file.");
         }
      } catch (IOException var10) {
         var10.printStackTrace();
      }

      GL20.glShaderSource(var3, var2);
      GL20.glCompileShader(var3);
      if (GL20.glGetShaderi(var3, 35713) == 0) {
         System.err.println(var0 + " shader was not compiled correctly.");
         System.err.println(GL20.glGetShaderInfoLog(var3, GL20.glGetShaderi(var3, 35716)));
      }

      return var3;
   }
}
