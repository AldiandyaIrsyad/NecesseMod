package necesse.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import necesse.server.ServerJFrame;

public class GameLog {
   public static final PrintStream out;
   public static final PrintStream warn;
   public static final PrintStream debug;
   public static final PrintStream file;
   public static final PrintStream err;

   public GameLog() {
   }

   public static void startLogging(String... var0) {
      System.setOut(out);
      System.setErr(err);
      String[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         addLoggingPath(var4);
      }

   }

   public static void addLoggingPath(String var0) {
      var0 = GlobalData.appDataPath() + var0;
      File var1 = (new File(var0)).getParentFile();
      if (var1 != null && !var1.exists() && !var1.mkdirs()) {
         System.err.println("Could not create log directory: " + var1.getAbsolutePath());
      }

      try {
         FileOutputStream var2 = new FileOutputStream(new File(var0));
         ((FileConsoleStream)out).addFileOut(var2);
         ((FileConsoleStream)warn).addFileOut(var2);
         ((FileConsoleStream)file).addFileOut(var2);
         ((FileConsoleStream)err).addFileOut(var2);
      } catch (FileNotFoundException var3) {
         System.err.println("Could not write to log file: " + var0);
      }

   }

   public static void setServerWrite(ServerJFrame var0) {
      ((FileConsoleStream)out).serverForm = var0;
      ((FileConsoleStream)warn).serverForm = var0;
      ((FileConsoleStream)err).serverForm = var0;
   }

   static {
      out = new FileConsoleStream(System.out, "", new FormatPrefix[]{GameLog.FormatPrefix.WHITE});
      warn = new FileConsoleStream(System.out, "(WARN) ", new FormatPrefix[]{GameLog.FormatPrefix.YELLOW});
      debug = new FileConsoleStream(System.out, "(DEBUG) ", new FormatPrefix[]{GameLog.FormatPrefix.BLUE});
      file = new FileConsoleStream(new NoPrintStream(), "", new FormatPrefix[]{GameLog.FormatPrefix.WHITE});
      err = new FileConsoleStream(System.err, "(ERR) ", new FormatPrefix[]{GameLog.FormatPrefix.RED});
   }

   private static class FileConsoleStream extends PrintStream {
      private List<FileOutputStream> fileOuts = new ArrayList();
      private SimpleDateFormat dateFormat;
      private String formatPrefix;
      private String prefix = "";
      private ServerJFrame serverForm = null;
      private boolean fileOut;
      private String lineSeparator;
      private boolean nextLine = true;

      public FileConsoleStream(PrintStream var1, String var2, FormatPrefix... var3) {
         super(var1);
         if (var3.length == 0) {
            this.formatPrefix = GameLog.FormatPrefix.WHITE.prefix;
         } else {
            this.formatPrefix = "";
            FormatPrefix[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               FormatPrefix var7 = var4[var6];
               this.formatPrefix = this.formatPrefix + var7.prefix;
            }
         }

         this.prefix = var2 == null ? "" : var2;
         this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         this.lineSeparator = System.getProperty("line.separator");
         if (this.lineSeparator == null || this.lineSeparator.isEmpty()) {
            this.lineSeparator = "\n";
         }

         this.lineSeparator = "\n";
      }

      public void addFileOut(FileOutputStream var1) {
         this.fileOuts.add(var1);
      }

      public void flush() {
         super.flush();
         Iterator var1 = this.fileOuts.iterator();

         while(var1.hasNext()) {
            FileOutputStream var2 = (FileOutputStream)var1.next();

            try {
               var2.flush();
            } catch (IOException var4) {
               this.setError();
            }
         }

      }

      public synchronized void println(String var1) {
         super.println(var1);
         this.nextLine = true;
         this.newFileLine();
      }

      public synchronized void println() {
         this.println("");
      }

      public synchronized void println(boolean var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(char var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(int var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(long var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(float var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(double var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(char[] var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void println(Object var1) {
         this.println(String.valueOf(var1));
      }

      public synchronized void print(boolean var1) {
         this.print(var1 ? "true" : "false");
      }

      public synchronized void print(char var1) {
         this.print(String.valueOf(var1));
      }

      public synchronized void print(int var1) {
         this.print(String.valueOf(var1));
      }

      public synchronized void print(long var1) {
         this.print(String.valueOf(var1));
      }

      public synchronized void print(float var1) {
         this.print(String.valueOf(var1));
      }

      public synchronized void print(double var1) {
         this.print(String.valueOf(var1));
      }

      public synchronized void print(Object var1) {
         this.print(String.valueOf(var1));
      }

      public synchronized void print(String var1) {
         String var2 = this.formatString(var1);
         this.fileOut = true;
         super.print(var2);
         this.fileOut = false;
         super.print(this.formatPrefix + var2);
         if (this.serverForm != null) {
            this.serverForm.writeConsole(this.prefix + var1);
         }

      }

      private synchronized void newFileLine() {
         this.fileOut = true;
         super.print("\n");
         this.fileOut = false;
      }

      private String formatString(String var1) {
         if (var1 == null) {
            return null;
         } else {
            boolean var2 = false;
            boolean var3 = this.nextLine;
            this.nextLine = false;
            if (var1.endsWith(this.lineSeparator)) {
               var2 = true;
               var1 = var1.substring(0, var1.length() - this.lineSeparator.length());
               this.nextLine = true;
            }

            String var4 = "[" + this.dateFormat.format(new Date()) + "] " + this.prefix;
            String var5 = var1.replace(this.lineSeparator, this.lineSeparator + var4);
            if (var3) {
               var5 = var4 + var5;
            }

            if (var2) {
               var5 = var5 + this.lineSeparator;
            }

            return var5;
         }
      }

      public synchronized void write(byte[] var1, int var2, int var3) {
         if (this.fileOut) {
            Iterator var4 = this.fileOuts.iterator();

            while(var4.hasNext()) {
               FileOutputStream var5 = (FileOutputStream)var4.next();

               try {
                  var5.write(var1, var2, var3);
               } catch (IOException var7) {
                  this.setError();
               }
            }
         } else {
            super.write(var1, var2, var3);
         }

      }
   }

   public static enum FormatPrefix {
      BLACK(30),
      RED(31),
      GREEN(32),
      YELLOW(33),
      BLUE(34),
      PURPLE(35),
      CYAN(36),
      GRAY(37),
      WHITE(39);

      private String prefix;

      private FormatPrefix(int var3) {
         this.prefix = "\u001b[" + var3 + "m";
      }

      // $FF: synthetic method
      private static FormatPrefix[] $values() {
         return new FormatPrefix[]{BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, GRAY, WHITE};
      }
   }

   private static class NoPrintStream extends PrintStream {
      public NoPrintStream() {
         super(new OutputStream() {
            public void write(int var1) throws IOException {
            }
         });
      }
   }
}
