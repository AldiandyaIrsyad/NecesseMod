package necesse.engine;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class WindowUtils {
   public WindowUtils() {
   }

   public static long[] getMonitors() {
      PointerBuffer var0 = GLFW.glfwGetMonitors();
      if (var0 == null) {
         return new long[0];
      } else {
         long var1 = GLFW.glfwGetPrimaryMonitor();
         long[] var3 = new long[var0.remaining()];
         int var4 = 0;
         if (var1 != 0L && var3.length > 0) {
            var3[var4++] = var1;
         }

         while(var0.hasRemaining() && var4 < var3.length) {
            long var5 = var0.get();
            if (var5 != var1) {
               var3[var4++] = var5;
            }
         }

         return var3;
      }
   }

   public static long getMonitor(int var0) {
      long[] var1 = getMonitors();
      if (var1.length == 0) {
         return 0L;
      } else {
         if (var0 < 0 || var0 >= var1.length) {
            var0 = 0;
         }

         return var1[var0];
      }
   }

   public static Dimension[] getVideoModes(long var0) {
      GLFWVidMode.Buffer var2 = GLFW.glfwGetVideoModes(var0);
      if (var2 == null) {
         return new Dimension[]{new Dimension(Screen.getWindowWidth(), Screen.getWindowHeight())};
      } else {
         ArrayList var3 = new ArrayList();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            GLFWVidMode var5 = (GLFWVidMode)var4.next();
            Dimension var6 = new Dimension(var5.width(), var5.height());
            if (!var3.contains(var6)) {
               var3.add(var6);
            }
         }

         var3.sort((var0x, var1) -> {
            return var0x.height == var1.height ? Integer.compare(var0x.width, var1.width) : Integer.compare(var0x.height, var1.height);
         });
         return (Dimension[])var3.toArray(new Dimension[0]);
      }
   }
}
