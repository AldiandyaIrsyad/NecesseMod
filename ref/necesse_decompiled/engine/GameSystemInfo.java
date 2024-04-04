package necesse.engine;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.util.GameUtils;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Platform;
import oshi.SystemInfo;
import oshi.hardware.Display;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.EdidUtil;

public class GameSystemInfo {
   private static SystemInfo info = new SystemInfo();
   private static HardwareAbstractionLayer hardware;
   private static OperatingSystem os;

   public GameSystemInfo() {
   }

   public static void printSystemInfo(PrintStream var0) {
      var0.println("SYSTEM INFO:");
      printSystemInfo(var0, "\t");
   }

   public static void printSystemInfo(PrintStream var0, String var1) {
      var0.println(var1 + "Username: " + System.getProperty("user.name"));
      var0.println(var1 + "OS: " + getOSName());
      var0.println(var1 + "CPU: " + getCPUName());
      var0.println(var1 + "OS arch: " + System.getProperty("os.arch"));
      var0.println(var1 + "Memory total: " + getTotalMemoryString());
      var0.println(var1 + "Memory max: " + getSafeString(() -> {
         return GameUtils.getByteString(Runtime.getRuntime().maxMemory());
      }));
      var0.println(var1 + "Java path: " + System.getProperty("java.home"));
      var0.println(var1 + "Java version: " + System.getProperty("java.version"));

      try {
         var0.println(var1 + "JVM arguments:");
         Iterator var2 = ManagementFactory.getRuntimeMXBean().getInputArguments().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var0.println(var1 + "\t" + var3);
         }
      } catch (Exception var4) {
         var0.println(var1 + "\tERR: " + var4.getMessage());
      }

      var0.println(var1 + "LWJGL version: " + Version.getVersion());
      var0.println(var1 + "Root path: " + GlobalData.rootPath());
      var0.println(var1 + "AppData path: " + GlobalData.appDataPath());
      var0.println(var1 + "Working dir: " + System.getProperty("user.dir"));
      String var5 = System.getProperty("org.lwjgl.librarypath");
      if (var5 == null) {
         var5 = "INTERNAL";
      }

      var0.println(var1 + "Natives path: " + var5);
   }

   public static void printGraphicsInfo(PrintStream var0) {
      var0.println("GRAPHICS INFO:");
      printGraphicsInfo(var0, "\t");
   }

   public static void printGraphicsInfo(PrintStream var0, String var1) {
      var0.println(var1 + "Graphics card: " + getGraphicsCard());
      var0.println(var1 + "OpenGL version: " + getOpenGLVersion());

      try {
         ArrayList var2 = getDisplays();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            String var4 = (String)var2.get(var3);
            var0.println(var1 + "Display " + var3 + ": " + var4);
         }
      } catch (Exception var5) {
      }

   }

   public static String getGraphicsCard() {
      return getSafeString(() -> {
         return GL11.glGetString(7936) + ", " + GL11.glGetString(7937);
      });
   }

   public static String getOpenGLVersion() {
      return getSafeString(() -> {
         return GL11.glGetString(7938);
      });
   }

   public static ArrayList<String> getDisplays() {
      if (Platform.get() == Platform.MACOSX) {
         return new ArrayList(Collections.singletonList("MacOS display"));
      } else {
         List var0 = hardware.getDisplays();
         ArrayList var1 = new ArrayList(var0.size());
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            Display var3 = (Display)var2.next();
            byte[] var4 = var3.getEdid();
            byte[][] var5 = EdidUtil.getDescriptors(var4);
            byte[][] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               byte[] var9 = var6[var8];
               if (EdidUtil.getDescriptorType(var9) == 252) {
                  var1.add(getSafeString(() -> {
                     return EdidUtil.getDescriptorText(var9) + " (" + EdidUtil.getManufacturerID(var4) + ", " + EdidUtil.getProductID(var4) + ")";
                  }));
               }
            }
         }

         return var1;
      }
   }

   public static String getOSName() {
      return getSafeString(() -> {
         return os.toString();
      });
   }

   public static String getCPUName() {
      return getSafeString(() -> {
         return hardware.getProcessor().getProcessorIdentifier().getName();
      });
   }

   public static long getTotalMemory() {
      return hardware.getMemory().getTotal();
   }

   public static long getAvailableMemory() {
      return hardware.getMemory().getAvailable();
   }

   public static String getTotalMemoryString() {
      return getSafeString(() -> {
         return GameUtils.getByteString(getTotalMemory());
      });
   }

   public static String getAvailableMemoryString() {
      return getSafeString(() -> {
         return GameUtils.getByteString(getAvailableMemory());
      });
   }

   public static String getUsedMemoryString() {
      return getSafeString(() -> {
         return GameUtils.getByteString(getTotalMemory() - getAvailableMemory());
      });
   }

   private static String getSafeString(Supplier<String> var0) {
      try {
         return (String)var0.get();
      } catch (Exception var2) {
         return "ERR: " + var2.getClass().getSimpleName();
      }
   }

   static {
      hardware = info.getHardware();
      os = info.getOperatingSystem();
   }
}
