package necesse.engine.util;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import org.lwjgl.system.Platform;

public final class GameUtils {
   public static final MetricValue[] normalMetricSystem = new MetricValue[]{new MetricValue("G", 9), new MetricValue("M", 6), new MetricValue("k", 3), new MetricValue("", 0), new MetricValue("m", -3), new MetricValue("u", -6), new MetricValue("n", -9)};
   public static Pattern playerNameSymbolsPattern = Pattern.compile("[\\p{L}\\p{N} ]+");
   public static final String[] invalidFileNameSequences = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "\u0000"};
   public static final Pattern validFileNamePattern;

   private GameUtils() {
      throw new IllegalStateException("GameUtils cannot be instantiated");
   }

   public static boolean deleteFileOrFolder(String var0) {
      return deleteFileOrFolder(new File(var0));
   }

   public static boolean deleteFileOrFolder(File var0) {
      if (!var0.isDirectory()) {
         return var0.delete();
      } else {
         File[] var1 = var0.listFiles();
         if (var1 != null) {
            File[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File var5 = var2[var4];
               if (!deleteFileOrFolder(var5.getPath())) {
                  return false;
               }
            }
         }

         return var0.delete();
      }
   }

   private static void _copyFileOrFolder(File var0, File var1, CopyOption... var2) throws IOException {
      if (!var0.isDirectory() || !var1.isDirectory()) {
         Files.copy(var0.toPath(), var1.toPath(), var2);
      }

      if (var0.isDirectory()) {
         File[] var3 = var0.listFiles();
         if (var3 != null) {
            File[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File var7 = var4[var6];
               _copyFileOrFolder(var7, resolveFile(var1, var7.getName()), var2);
            }
         }
      }

   }

   public static void copyFileOrFolder(File var0, File var1, CopyOption... var2) throws IOException {
      File var3;
      if (var0.isFile()) {
         var3 = var1.getAbsoluteFile().getParentFile();
         if (var3 != null && !var3.exists() && !var3.mkdirs()) {
            throw new IOException("Could not make target directories for move");
         }
      } else {
         var3 = var1.getAbsoluteFile();
         if (!var3.exists() && !var3.mkdirs()) {
            throw new IOException("Could not make target directories for move");
         }
      }

      _copyFileOrFolder(var0, var1, var2);
   }

   public static void copyFileOrFolderReplaceExisting(File var0, File var1) throws IOException {
      copyFileOrFolder(var0, var1, StandardCopyOption.REPLACE_EXISTING);
   }

   private static void _moveFileOrFolder(File var0, File var1, CopyOption... var2) throws IOException {
      if (!var0.isDirectory() || !var1.isDirectory()) {
         Files.move(var0.toPath(), var1.toPath(), var2);
      }

      if (var0.isDirectory()) {
         File[] var3 = var0.listFiles();
         if (var3 != null) {
            File[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File var7 = var4[var6];
               _moveFileOrFolder(var7, resolveFile(var1, var7.getName()), var2);
            }
         }
      }

   }

   public static void moveFileOrFolder(File var0, File var1, CopyOption... var2) throws IOException {
      File var3;
      if (var0.isFile()) {
         var3 = var1.getAbsoluteFile().getParentFile();
         if (var3 != null && !var3.exists() && !var3.mkdirs()) {
            throw new IOException("Could not make target directories for move");
         }
      } else {
         var3 = var1.getAbsoluteFile();
         if (!var3.exists() && !var3.mkdirs()) {
            throw new IOException("Could not make target directories for move");
         }
      }

      _moveFileOrFolder(var0, var1, var2);
   }

   public static void moveFileOrFolderReplaceExisting(File var0, File var1) throws IOException {
      moveFileOrFolder(var0, var1, StandardCopyOption.REPLACE_EXISTING);
   }

   public static File resolveFile(File var0, String var1) {
      return var0.toPath().resolve(var1).toFile();
   }

   public static void collectFiles(File var0, Collection<File> var1, Predicate<File> var2) {
      collectFiles(var0, var1, var2, false);
   }

   public static void collectFiles(File var0, Collection<File> var1, Predicate<File> var2, boolean var3) {
      if (var0.isDirectory()) {
         if (var3 && var2 != null && !var2.test(var0)) {
            return;
         }

         File[] var4 = var0.listFiles();
         if (var4 == null) {
            throw new NullPointerException("Could not read files inside folder " + var0.getPath());
         }

         File[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File var8 = var5[var7];
            collectFiles(var8, var1, var2, var3);
         }
      } else {
         if (var2 != null && !var2.test(var0)) {
            return;
         }

         var1.add(var0);
      }

   }

   public static String getNextUniqueFilename(String var0, String var1, String var2) {
      return getNextUniqueFilename(var0, var1, var2, (var0x) -> {
         return (new File(var0x)).exists();
      });
   }

   public static String getNextUniqueFilename(String var0, String var1, String var2, Function<String, Boolean> var3) {
      int var5 = 1;
      String var6 = "";

      while(true) {
         String var4 = var0 + var1 + var6 + (var2 != null && var2.length() != 0 ? "." + var2 : "");
         if (!(Boolean)var3.apply(var4)) {
            return var4;
         }

         ++var5;
         var6 = Integer.toString(var5);
      }
   }

   public static boolean mkDirs(File var0) {
      if (var0.isDirectory()) {
         return var0.mkdirs();
      } else {
         File var1 = var0.getParentFile();
         return var1 == null || var1.exists() || var1.mkdirs();
      }
   }

   public static void saveByteFile(byte[] var0, File var1) throws IOException {
      if (!mkDirs(var1)) {
         throw new IllegalArgumentException("Could not create folder for file: " + var1.getAbsolutePath());
      } else {
         FileOutputStream var2 = new FileOutputStream(var1);
         var2.write(var0);
         var2.close();
      }
   }

   public static void saveByteFile(byte[] var0, String var1) throws IOException {
      saveByteFile(var0, new File(var1));
   }

   public static byte[] loadByteFile(File var0) throws IOException {
      if (!var0.exists()) {
         throw new FileNotFoundException("Could not find byte file");
      } else {
         FileInputStream var1 = new FileInputStream(var0);
         long var2 = var0.length();
         if (var2 > 2147483647L) {
            throw new IOException("Byte file too large");
         } else {
            return loadInputStream(var1);
         }
      }
   }

   public static byte[] loadInputStream(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      byte[] var3 = new byte[16384];

      int var2;
      while((var2 = var0.read(var3, 0, var3.length)) != -1) {
         var1.write(var3, 0, var2);
      }

      var0.close();
      return var1.toByteArray();
   }

   public static byte[] loadByteFile(String var0) throws IOException {
      return loadByteFile(new File(var0));
   }

   public static byte[] compressData(byte[] var0) throws IOException {
      Deflater var1 = new Deflater();
      var1.setLevel(9);
      var1.setInput(var0);
      var1.finish();
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(var0.length);
      byte[] var3 = new byte[1024];

      while(!var1.finished()) {
         int var4 = var1.deflate(var3);
         var2.write(var3, 0, var4);
      }

      var2.close();
      return var2.toByteArray();
   }

   public static byte[] decompressData(byte[] var0) throws DataFormatException, IOException {
      if (var0.length == 0) {
         return new byte[0];
      } else {
         Inflater var1 = new Inflater();
         var1.setInput(var0);
         ByteArrayOutputStream var2 = new ByteArrayOutputStream(var0.length);
         byte[] var3 = new byte[1024];

         while(!var1.finished()) {
            int var4 = var1.inflate(var3);
            if (var4 == 0) {
               break;
            }

            var2.write(var3, 0, var4);
         }

         var2.close();
         return var2.toByteArray();
      }
   }

   public static String insertNewLines(String var0, FontOptions var1, int var2, String var3) {
      return insertNewLines(var0, var1, var2, var3, true);
   }

   public static String insertNewLines(String var0, FontOptions var1, int var2, String var3, boolean var4) {
      String[] var5;
      int var9;
      String var10;
      if (var4) {
         var5 = var0.split("\\n");
         StringBuilder var20 = new StringBuilder();
         String[] var21 = var5;
         int var22 = var5.length;

         for(var9 = 0; var9 < var22; ++var9) {
            var10 = var21[var9];
            if (var20.length() != 0) {
               var20.append("\n");
            }

            var20.append(insertNewLines(var10, var1, var2, var3, false));
         }

         return var20.toString().trim();
      } else {
         var5 = var0.split(" ");
         if (var5.length == 0) {
            return var0;
         } else {
            float var6 = FontManager.bit.getWidth(' ', var1);
            float var7 = 0.0F;
            StringBuilder var8 = new StringBuilder();

            for(var9 = 0; var9 < var5.length; ++var9) {
               var10 = var5[var9];
               if (var10.length() != 0) {
                  String var11 = var10.replace("\n", "").trim();
                  String var12 = var3 == null ? var11 : var11.replaceAll(var3, "");
                  float var13 = FontManager.bit.getWidth(var12, var1);
                  if (var2 > 0 && var7 + var13 > (float)var2) {
                     if (var7 != 0.0F) {
                        var8 = new StringBuilder(var8.toString().trim() + "\n");
                        var7 = 0.0F;
                        --var9;
                        continue;
                     }

                     char[] var14 = var10.toCharArray();
                     char[] var15 = var12.toCharArray();
                     int var16 = 0;

                     for(int var17 = 0; var16 < var14.length; ++var16) {
                        char var18 = var14[var16];
                        boolean var19;
                        if (var17 >= var15.length) {
                           var19 = false;
                        } else {
                           var19 = var18 == var15[var17];
                        }

                        var8.append(var18);
                        if (var19) {
                           var7 += FontManager.bit.getWidth(var18, var1);
                           if (var7 > (float)var2) {
                              var8 = new StringBuilder(var8.toString().trim() + "\n");
                              var7 = 0.0F;
                           }
                        }

                        if (var19) {
                           ++var17;
                        }
                     }

                     var10 = null;
                  }

                  if (var10 != null) {
                     var8.append(var10).append(" ");
                     var7 += var13 + var6;
                  }
               }
            }

            return var8.toString().trim();
         }
      }
   }

   public static ArrayList<String> breakString(String var0, FontOptions var1, int var2) {
      return breakString(var0, var1, var2, (String)null);
   }

   public static ArrayList<String> breakString(String var0, FontOptions var1, int var2, String var3) {
      String[] var4 = insertNewLines(var0, var1, var2, var3).split("\\n");
      ArrayList var5 = new ArrayList();
      Collections.addAll(var5, var4);
      return var5;
   }

   public static String maxString(String var0, FontOptions var1, int var2) {
      return maxString(var0, var1, var2, (String)null);
   }

   public static String maxString(String var0, FontOptions var1, int var2, String var3) {
      String var4;
      for(var4 = var0; FontManager.bit.getWidthCeil(var3 == null ? var4 : var4.replaceAll(var3, ""), var1) >= var2; var4 = var0.substring(0, var4.length() - 1)) {
         if (var4.length() == 1) {
            return "";
         }
      }

      return var4;
   }

   public static String capitalize(String var0) {
      return var0.substring(0, 1).toUpperCase() + var0.substring(1);
   }

   public static BigDecimal exponent(BigDecimal var0, int var1) {
      return var1 < 0 ? var0.divide(BigDecimal.valueOf(10L).pow(Math.abs(var1)), new MathContext(0, RoundingMode.DOWN)) : var0.multiply(BigDecimal.valueOf(10L).pow(var1));
   }

   public static String metricNumber(BigDecimal var0, int var1, int var2, boolean var3, RoundingMode var4, String var5, MetricValue[] var6) {
      if (var6.length == 0) {
         throw new IllegalArgumentException("metricSystem must have a least one value");
      } else {
         if (var1 != 0) {
            var0 = exponent(var0, var1);
         }

         if (var5 == null) {
            var5 = "";
         }

         int var7 = var0.signum();
         if (var7 == 0) {
            return "0" + var5;
         } else {
            String var8 = "";
            if (var7 < 0) {
               var8 = "-";
               var0 = var0.abs();
            }

            MetricValue var9 = null;
            MetricValue[] var10 = var6;
            int var11 = var6.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               MetricValue var13 = var10[var12];
               var9 = var13;
               if (var0.compareTo(var13.divider) >= 0) {
                  break;
               }
            }

            BigDecimal var15 = var0.divide(var9.divider, new MathContext(0, RoundingMode.DOWN));
            String var16;
            if (var15.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO)) {
               var16 = var15.toString();
            } else if (var3) {
               BigDecimal var17 = var15.abs();
               int var18 = var17.toString().length() - var17.scale() - 1;
               if (!var17.equals(var15)) {
                  ++var18;
               }

               int var14 = Math.max(0, 2 - var18);
               var16 = var15.setScale(Math.min(var2, var14), var4).toString();
            } else {
               var16 = var15.setScale(var2, var4).toString();
            }

            return var8 + var16 + var5 + var9.suffix;
         }
      }
   }

   public static String metricNumber(long var0, int var2, boolean var3, RoundingMode var4, String var5) {
      return metricNumber(BigDecimal.valueOf(var0), 0, var2, var3, var4, var5, normalMetricSystem);
   }

   public static String metricNumber(long var0, int var2, boolean var3, String var4) {
      return metricNumber(var0, var2, var3, RoundingMode.HALF_UP, var4);
   }

   public static String metricNumber(long var0, int var2, String var3) {
      return metricNumber(var0, var2, false, var3);
   }

   public static String metricNumber(long var0, String var2) {
      return metricNumber(var0, 2, var2);
   }

   public static String metricNumber(long var0, int var2) {
      return metricNumber(var0, var2, (String)null);
   }

   public static String metricNumber(long var0) {
      return metricNumber(var0, (String)null);
   }

   public static String getByteString(long var0) {
      return metricNumber(var0, " ") + "B";
   }

   public static String getTimeStringNano(long var0) {
      long var2 = var0 / 1000000000L;
      String var4 = "";
      if (var2 > 60L) {
         var4 = formatSecondsMinutes(var2, true) + " ";
         long var5 = var2 / 60L * 60L;
         var0 -= var5 * 1000000000L;
      }

      return var4 + metricNumber(BigDecimal.valueOf(var0), -9, 2, false, RoundingMode.HALF_UP, " ", normalMetricSystem) + "s";
   }

   public static String getTimeStringMillis(long var0) {
      long var2 = var0 / 1000L;
      String var4 = "";
      if (var2 > 60L) {
         var4 = formatSecondsMinutes(var2, true) + " ";
         long var5 = var2 / 60L * 60L;
         var0 -= var5 * 1000L;
      }

      return var4 + metricNumber(BigDecimal.valueOf(var0), -3, 2, false, RoundingMode.HALF_UP, " ", normalMetricSystem) + "s";
   }

   private static String formatSecondsMinutes(long var0, boolean var2) {
      long var3 = var0 / 3600L / 24L;
      long var5 = var0 / 3600L % 24L;
      long var7 = var0 / 60L % 60L;
      if (var3 > 0L) {
         return var3 + "d" + (var2 ? " " : "") + var5 + "h" + (var2 ? " " : "") + var7 + "m";
      } else if (var5 > 0L) {
         return var5 + "h" + (var2 ? " " : "") + var7 + "m";
      } else {
         return var7 > 0L ? var7 + "m" : "";
      }
   }

   public static String formatSeconds(long var0, boolean var2) {
      long var3 = var0 % 60L;
      return formatSecondsMinutes(var0, var2) + var3 + (var2 ? " " : "") + "s";
   }

   public static String formatSeconds(long var0) {
      return formatSeconds(var0, false);
   }

   public static String formatNumber(long var0) {
      return NumberFormat.getNumberInstance(Locale.ENGLISH).format(var0);
   }

   public static String formatNumber(double var0) {
      return NumberFormat.getNumberInstance(Locale.ENGLISH).format(var0);
   }

   public static String padString(String var0, int var1, char var2) {
      if (var0.length() >= var1) {
         return var0;
      } else {
         StringBuilder var3 = new StringBuilder(var0);

         while(var3.length() < var1) {
            var3.append(var2);
         }

         return var3.toString();
      }
   }

   public static void forEachMatcherResult(Pattern var0, String var1, Consumer<MatchResult> var2) {
      Matcher var3 = var0.matcher(var1);

      while(var3.find()) {
         var2.accept(var3.toMatchResult());
      }

   }

   public static String matcherReplaceAll(Pattern var0, String var1, Function<MatchResult, String> var2) {
      StringBuilder var3 = new StringBuilder();
      AtomicInteger var4 = new AtomicInteger();
      forEachMatcherResult(var0, var1, (var4x) -> {
         String var5 = (String)var2.apply(var4x);
         if (var5 != null) {
            var3.append(var1, var4.get(), var4x.start());
            var3.append(var5);
            var4.set(var4x.end());
         }

      });
      var3.append(var1, var4.get(), var1.length());
      return var3.toString();
   }

   public static Dimension getPlayerNameLength() {
      return new Dimension(1, 30);
   }

   public static GameMessage isValidPlayerName(String var0) {
      var0 = var0.trim();
      Dimension var1 = getPlayerNameLength();
      if (var0.length() >= var1.width && var0.length() <= var1.height) {
         return !playerNameSymbolsPattern.matcher(var0).matches() ? new LocalMessage("ui", "playernamesymbols") : null;
      } else {
         return new LocalMessage("ui", "playernamesize");
      }
   }

   public static Stream<? extends NetworkClient> streamNetworkClients(Level var0) {
      if (var0.isServer()) {
         return streamServerClients(var0);
      } else {
         return var0.isClient() ? streamClientClients(var0) : Stream.empty();
      }
   }

   public static Stream<ClientClient> streamClientClients(Client var0, LevelIdentifier var1) {
      return var0 == null ? Stream.empty() : var0.streamClients().filter((var1x) -> {
         return var1x != null && var1x.loadedPlayer && var1x.hasSpawned() && !var1x.isDead() && var1x.isSamePlace(var1);
      });
   }

   public static Stream<ClientClient> streamClientClients(Client var0, Level var1) {
      return streamClientClients(var0, var1.getIdentifier());
   }

   public static Stream<ClientClient> streamClientClients(Level var0) {
      return streamClientClients(var0.getClient(), var0);
   }

   public static Stream<ServerClient> streamServerClients(Server var0, LevelIdentifier var1) {
      return var0 == null ? Stream.empty() : var0.streamClients().filter((var1x) -> {
         return var1x.playerMob != null && var1x.hasSpawned() && !var1x.playerMob.removed() && var1x.isSamePlace(var1);
      });
   }

   public static Stream<ServerClient> streamServerClients(Server var0, Level var1) {
      return streamServerClients(var0, var1.getIdentifier());
   }

   public static Stream<ServerClient> streamServerClients(Level var0) {
      return streamServerClients(var0.getServer(), var0);
   }

   public static Mob getLevelMob(int var0, Level var1) {
      return getLevelMob(var0, var1, false);
   }

   public static Mob getLevelMob(int var0, Level var1, boolean var2) {
      if (var1 == null) {
         return null;
      } else {
         Object var3 = null;
         if (var1.isServer()) {
            if (var0 < var1.getServer().getSlots() && var0 >= 0) {
               if (var1.getServer().getClient(var0) == null || !var1.getServer().getClient(var0).isSamePlace(var1)) {
                  return null;
               }

               var3 = var1.getServer().getClient(var0).playerMob;
            }
         } else if (var1.isClient() && var0 < var1.getClient().getSlots() && var0 >= 0) {
            if (var1.getClient().getClient(var0) == null || !var1.getClient().getClient(var0).isSamePlace(var1)) {
               return null;
            }

            var3 = var1.getClient().getClient(var0).playerMob;
         }

         if (var3 == null) {
            var3 = (Mob)var1.entityManager.mobs.get(var0, var2);
         }

         return (Mob)var3;
      }
   }

   public static Attacker getLevelAttacker(int var0, Level var1) {
      if (var1 == null) {
         return null;
      } else {
         Object var2 = getLevelMob(var0, var1);
         if (var2 == null) {
            var2 = (Attacker)var1.entityManager.projectiles.get(var0, true);
         }

         return (Attacker)var2;
      }
   }

   public static Rectangle rangeBounds(int var0, int var1, int var2) {
      return new Rectangle(var0 - var2, var1 - var2, var2 * 2, var2 * 2);
   }

   public static Rectangle rangeBounds(float var0, float var1, int var2) {
      return rangeBounds((int)var0, (int)var1, var2);
   }

   public static Rectangle rangeTileBounds(int var0, int var1, int var2) {
      return rangeBounds(var0, var1, var2 * 32);
   }

   public static Stream<Mob> streamTargetsRange(Mob var0, int var1) {
      return streamTargetsRange(var0, var0.getX(), var0.getY(), var1);
   }

   public static Stream<Mob> streamTargetsRange(Mob var0, int var1, int var2, int var3) {
      return streamTargets(var0, rangeBounds(var1, var2, var3));
   }

   public static Stream<Mob> streamTargetsTileRange(Mob var0, int var1) {
      return streamTargetsTileRange(var0, var0.getX(), var0.getY(), var1);
   }

   public static Stream<Mob> streamTargetsTileRange(Mob var0, int var1, int var2, int var3) {
      return streamTargets(var0, rangeTileBounds(var1, var2, var3));
   }

   public static Stream<Mob> streamTargets(Mob var0, Shape var1) {
      return streamTargets(var0, var1, 1);
   }

   public static NetworkClient getAttackerClient(Mob var0) {
      if (var0.isFollowing()) {
         if (var0.isServer()) {
            if (var0.isFollowing()) {
               return var0.getFollowingServerClient();
            }
         } else if (var0.isClient() && var0.isFollowing()) {
            return var0.getFollowingClientClient();
         }
      } else if (var0.isPlayer) {
         return ((PlayerMob)var0).getNetworkClient();
      }

      return null;
   }

   public static Stream<Mob> streamTargets(Mob var0, Shape var1, int var2) {
      if (var0 != null && var0.getLevel() != null) {
         NetworkClient var3 = getAttackerClient(var0);
         Stream var4;
         if (var1 == null) {
            var4 = var0.getLevel().entityManager.mobs.stream();
         } else {
            var4 = var0.getLevel().entityManager.mobs.streamInRegionsShape(var1, var2);
         }

         var4 = var4.filter((var2x) -> {
            return var2x.canBeTargeted(var0, var3);
         });
         if (var3 != null && !var3.pvpEnabled()) {
            return var4;
         } else {
            Stream var5;
            if (var1 == null) {
               var5 = streamNetworkClients(var0.getLevel()).map((var0x) -> {
                  return var0x.playerMob;
               });
            } else {
               var5 = var0.getLevel().entityManager.players.streamInRegionsShape(var1, var2);
            }

            var5 = var5.filter((var2x) -> {
               NetworkClient var3x = var2x.getNetworkClient();
               if (var3x != null && var3x.playerMob != null && var3x.playerMob.getLevel() != null) {
                  if (!var3x.isDead() && var3x.hasSpawned()) {
                     return var3 != null && !var3x.pvpEnabled() ? false : var3x.playerMob.canBeTargeted(var0, var3);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            });
            return Stream.concat(var4, var5);
         }
      } else {
         return Stream.empty();
      }
   }

   public static <T> String join(T[] var0, Function<T, String> var1, String var2, String var3) {
      StringBuilder var4 = new StringBuilder();

      for(int var5 = 0; var5 < var0.length; ++var5) {
         var4.append(var1 == null ? var0[var5] : var1.apply(var0[var5]));
         if (var5 == var0.length - 2) {
            var4.append(var3);
         } else if (var5 < var0.length - 1) {
            var4.append(var2);
         }
      }

      return var4.toString();
   }

   public static String join(Object[] var0, String var1, String var2) {
      return join(var0, (Function)null, var1, var2);
   }

   public static <T> String join(T[] var0, Function<T, String> var1, String var2) {
      return join(var0, var1, var2, var2);
   }

   public static String join(Object[] var0, String var1) {
      return join(var0, (Function)null, var1, var1);
   }

   public static <T> void insertSortedList(List<T> var0, T var1, Comparator<T> var2) {
      insertSortedList(var0.listIterator(), var1, var2);
   }

   public static <T> void insertSortedList(ListIterator<T> var0, T var1, Comparator<T> var2) {
      if (var2 == null) {
         if (!(var1 instanceof Comparable)) {
            throw new IllegalArgumentException("Must either supply comparator or object must be comparable.");
         }

         var2 = (var0x, var1x) -> {
            return ((Comparable)var0x).compareTo(var1x);
         };
      }

      Object var3;
      do {
         if (!var0.hasNext()) {
            var0.add(var1);
            return;
         }

         var3 = var0.next();
      } while(var2.compare(var3, var1) <= 0);

      var0.previous();
      var0.add(var1);
   }

   public static <T> GameLinkedList<T>.Element insertSortedList(GameLinkedList<T> var0, T var1, Comparator<T> var2) {
      if (var2 == null) {
         if (!(var1 instanceof Comparable)) {
            throw new IllegalArgumentException("Must either supply comparator or object must be comparable.");
         }

         var2 = (var0x, var1x) -> {
            return ((Comparable)var0x).compareTo(var1x);
         };
      }

      for(GameLinkedList.Element var3 = var0.getFirstElement(); var3 != null; var3 = var3.next()) {
         if (var2.compare(var3.object, var1) > 0) {
            return var3.insertBefore(var1);
         }
      }

      return var0.addLast(var1);
   }

   public static <T> GameLinkedList<T>.Element insertSortedListReversed(GameLinkedList<T> var0, T var1, Comparator<T> var2) {
      if (var2 == null) {
         if (!(var1 instanceof Comparable)) {
            throw new IllegalArgumentException("Must either supply comparator or object must be comparable.");
         }

         var2 = (var0x, var1x) -> {
            return ((Comparable)var0x).compareTo(var1x);
         };
      }

      for(GameLinkedList.Element var3 = var0.getLastElement(); var3 != null; var3 = var3.prev()) {
         if (var2.compare(var3.object, var1) < 0) {
            return var3.insertAfter(var1);
         }
      }

      return var0.addFirst(var1);
   }

   public static Color getBrighterColor(Color var0, float var1) {
      int var2 = var0.getRed();
      int var3 = var0.getGreen();
      int var4 = var0.getBlue();
      int var5 = (int)(1.0F / var1);
      if (var2 == 0 && var3 == 0 && var4 == 0) {
         return new Color(var5, var5, var5, var0.getAlpha());
      } else {
         if (var2 > 0 && var2 < var5) {
            var2 = var5;
         }

         if (var3 > 0 && var3 < var5) {
            var3 = var5;
         }

         if (var4 > 0 && var4 < var5) {
            var4 = var5;
         }

         return new Color(Math.min((int)((float)var2 / var1), 255), Math.min((int)((float)var3 / var1), 255), Math.min((int)((float)var4 / var1), 255), var0.getAlpha());
      }
   }

   public static Color getDarkerColor(Color var0, float var1) {
      return new Color(Math.max((int)((float)var0.getRed() * var1), 0), Math.max((int)((float)var0.getGreen() * var1), 0), Math.max((int)((float)var0.getBlue() * var1), 0), var0.getAlpha());
   }

   public static float getStatusColorHue(float var0) {
      return GameMath.limit(var0, 0.0F, 1.0F) / 3.0F;
   }

   public static Color getStatusColor(float var0, float var1, float var2) {
      return Color.getHSBColor(getStatusColorHue(var0), var1, var2);
   }

   public static Color getStatusColorRedPref(float var0, float var1, float var2, float var3) {
      var0 = (float)Math.pow((double)var0, (double)var3);
      return Color.getHSBColor(getStatusColorHue(var0), var1, var2);
   }

   public static Color getStatusColorGreenPref(float var0, float var1, float var2, float var3) {
      var0 = Math.abs((float)Math.pow((double)Math.abs(var0 - 1.0F), (double)var3) - 1.0F);
      return Color.getHSBColor(getStatusColorHue(var0), var1, var2);
   }

   public static Color getStatusColorLerp(float var0, float var1, float var2) {
      float var3 = Math.abs(var0 - 1.0F);
      float var4 = GameMath.limit(var3 * 2.0F, 0.0F, 1.0F);
      float var5 = GameMath.limit(var0 * 2.0F, 0.0F, 1.0F);
      return new Color(GameMath.lerp(var4, var1, var2), GameMath.lerp(var5, var1, var2), 0.0F);
   }

   public static Color getStatusColorLerpRedPref(float var0, float var1, float var2, float var3) {
      var0 = (float)Math.pow((double)var0, (double)var3);
      return getStatusColorLerp(var0, var1, var2);
   }

   public static Color getStatusColorLerpGreenPref(float var0, float var1, float var2, float var3) {
      var0 = Math.abs((float)Math.pow((double)Math.abs(var0 - 1.0F), (double)var3) - 1.0F);
      return getStatusColorLerp(var0, var1, var2);
   }

   public static float getBobbing(long var0, int var2) {
      int var3 = var2 / 2;
      long var4 = var0 % (long)var2;
      if (var4 > (long)var3) {
         var4 = (long)var3 - var4 % (long)var3;
      }

      return (float)var4 / (float)var3;
   }

   public static float getTimeRotation(long var0, int var2, int var3) {
      return var3 <= 1 ? (float)(var0 % 360L) : (float)(var0 * (long)var3 / (long)var2 % (long)(360 * var3)) / (float)var3;
   }

   public static float getTimeRotation(long var0, int var2) {
      return getTimeRotation(var0, var2, 10);
   }

   public static float getAnimFloat(long var0, int var2) {
      return (float)(var0 % (long)var2) / (float)var2;
   }

   public static float getAnimFloatContinuous(long var0, int var2) {
      float var3 = getAnimFloat(var0, var2);
      return var3 > 0.5F ? Math.abs((var3 - 0.5F) * 2.0F - 1.0F) : var3 * 2.0F;
   }

   public static int getAnim(long var0, int var2, int var3) {
      float var4 = (float)var3 / (float)var2;
      return (int)Math.min((float)(var0 % (long)var3) / var4, (float)(var2 - 1));
   }

   public static int getAnimContinuous(long var0, int[] var2) {
      if (var0 < 0L) {
         return -1;
      } else {
         int var3 = 0;

         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var0 < (long)var2[var4]) {
               return var4;
            }

            var3 += var2[var4];
            var0 -= (long)var2[var4];
         }

         var0 %= (long)var3;
         return getAnim(var0, var2);
      }
   }

   public static int getAnim(long var0, int[] var2) {
      if (var0 < 0L) {
         return -1;
      } else {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var0 < (long)var2[var3]) {
               return var3;
            }

            var0 -= (long)var2[var3];
         }

         return -1;
      }
   }

   public static float getMultiplayerScaling(int var0, int var1, float var2, float var3) {
      if (var3 == 0.0F) {
         return 1.0F + (float)(Math.min(var0, var1) - 1) * var2;
      } else {
         float var4 = 1.0F;

         for(int var5 = 2; var5 <= Math.min(var0, var1); ++var5) {
            float var6 = var2 - var3 * (float)(var5 - 2);
            if (var6 <= 0.0F) {
               break;
            }

            var4 += var6;
         }

         return var4;
      }
   }

   public static float getMultiplayerScaling(int var0, float var1, float var2) {
      return getMultiplayerScaling(var0, 20, var1, var2);
   }

   public static float getMultiplayerScaling(int var0) {
      return getMultiplayerScaling(var0, 0.8F, 0.04F);
   }

   public static <T> RayLinkedList<T> castRay(double var0, double var2, double var4, double var6, double var8, double var10, int var12, Function<Line2D, IntersectionPoint<T>> var13) {
      Point2D.Double var14 = GameMath.normalize(var4, var6);
      if (!Double.isNaN(var14.x) && !Double.isNaN(var14.y)) {
         Point2D.Double var15 = new Point2D.Double(var0, var2);
         Line2D.Double var16 = new Line2D.Double(var15.x, var15.y, var15.x + var14.x * Math.min(var10, var8), var15.y + var14.y * Math.min(var10, var8));
         double var17 = 0.0;
         RayLinkedList var19 = new RayLinkedList();
         boolean var20 = false;

         while(var19.size() <= var12) {
            IntersectionPoint var21 = (IntersectionPoint)var13.apply(var16);
            if (var21 == null) {
               if (var20) {
                  var19.addLast(new Ray(var15.x, var15.y, var15.x + var14.x * var8, var15.y + var14.y * var8, (Object)null));
                  var19.totalDist += var8;
                  break;
               }

               var17 += var16.getP1().distance(var16.getP2());
               double var24 = var8 - var17;
               if (var24 <= var10) {
                  var16 = new Line2D.Double(var16.getX2(), var16.getY2(), var16.getX2() + var14.x * var24, var16.getY2() + var14.y * var24);
                  var20 = true;
               } else {
                  var16 = new Line2D.Double(var16.getX2(), var16.getY2(), var16.getX2() + var14.x * var10, var16.getY2() + var14.y * var10);
               }
            } else {
               if (var21.dir == IntersectionPoint.Dir.UP) {
                  var14.y = -var14.y;
                  var21.y += 0.1;
               } else if (var21.dir == IntersectionPoint.Dir.DOWN) {
                  var14.y = -var14.y;
                  var21.y -= 0.1;
               } else if (var21.dir == IntersectionPoint.Dir.LEFT) {
                  var14.x = -var14.x;
                  var21.x += 0.1;
               } else if (var21.dir == IntersectionPoint.Dir.RIGHT) {
                  var14.x = -var14.x;
                  var21.x -= 0.1;
               }

               Ray var22 = new Ray(var15.x, var15.y, var21.x, var21.y, var21.target);
               var19.addLast(var22);
               var19.totalDist += var22.dist;
               var8 -= var22.dist;
               var16 = new Line2D.Double(var21.x, var21.y, var21.x + var14.x * Math.min(var10, var8), var21.y + var14.y * Math.min(var10, var8));
               var20 = false;
               var17 = 0.0;
               var15 = new Point2D.Double(var21.x, var21.y);
            }
         }

         return var19;
      } else {
         return new RayLinkedList();
      }
   }

   public static <T> RayLinkedList<T> castRay(Line2D var0, double var1, int var3, Function<Line2D, IntersectionPoint<T>> var4) {
      return castRay(var0.getX1(), var0.getY1(), var0.getX2() - var0.getX1(), var0.getY2() - var0.getY1(), var0.getP1().distance(var0.getP2()), var1, var3, var4);
   }

   public static <T> T castRayFirstHit(double var0, double var2, double var4, double var6, double var8, double var10, Function<Line2D, T> var12) {
      RayLinkedList var13 = castRay(var0, var2, var4, var6, var8, var10, 0, (var1) -> {
         Object var2 = var12.apply(var1);
         return var2 == null ? null : new IntersectionPoint(var1.getX2(), var1.getY2(), var2, IntersectionPoint.Dir.DOWN);
      });
      Ray var14 = (Ray)var13.getLast();
      return var14 != null && var14.targetHit != null ? var14.targetHit : null;
   }

   public static <T> T castRayFirstHit(Line2D var0, double var1, Function<Line2D, T> var3) {
      return castRayFirstHit(var0.getX1(), var0.getY1(), var0.getX2() - var0.getX1(), var0.getY2() - var0.getY1(), var0.getP1().distance(var0.getP2()), var1, var3);
   }

   public static RayLinkedList<LevelObjectHit> castRay(Level var0, double var1, double var3, double var5, double var7, double var9, double var11, int var13, CollisionFilter var14, boolean var15) {
      return castRay(var1, var3, var5, var7, var9, var11, var13, (var3x) -> {
         return var0.getCollisionPoint(var0.getCollisions(var3x, var14), var3x, var15);
      });
   }

   public static RayLinkedList<LevelObjectHit> castRay(Level var0, double var1, double var3, double var5, double var7, double var9, int var11, CollisionFilter var12) {
      return castRay(var0, var1, var3, var5, var7, var9, 100.0, var11, var12, false);
   }

   public static LevelObject getInteractObjectHit(Level var0, int var1, int var2, Predicate<LevelObject> var3) {
      return getInteractObjectHit(var0, var1, var2, var3, new LevelObject(var0, var1 / 32, var2 / 32));
   }

   public static LevelObject getInteractObjectHit(Level var0, int var1, int var2, Predicate<LevelObject> var3, LevelObject var4) {
      if (Settings.useTileObjectHitboxes) {
         LevelObject var15 = new LevelObject(var0, var1 / 32, var2 / 32);
         return var3 != null && !var3.test(var15) ? var4 : var15;
      } else {
         int var5 = (var1 - 100) / 32;
         int var6 = (var2 - 50) / 32;
         int var7 = (var1 + 100) / 32;
         int var8 = (var2 + 200) / 32;
         ObjectHoverHitbox var9 = null;

         for(int var10 = var8; var10 >= var6; --var10) {
            label62:
            for(int var11 = var5; var11 <= var7; ++var11) {
               LevelObject var12 = new LevelObject(var0, var11, var10);
               if (var3 == null || var3.test(var12)) {
                  Iterator var13 = var12.getHoverHitboxes().iterator();

                  while(true) {
                     ObjectHoverHitbox var14;
                     do {
                        do {
                           if (!var13.hasNext()) {
                              continue label62;
                           }

                           var14 = (ObjectHoverHitbox)var13.next();
                        } while(!var14.contains(var1, var2));
                     } while(var9 != null && var9.sortY > var14.sortY);

                     var9 = var14;
                  }
               }
            }

            if (var9 != null) {
               break;
            }
         }

         return var9 != null ? new LevelObject(var0, var9.tileX, var9.tileY) : var4;
      }
   }

   public static <T, R> Iterable<R> mapIterable(Iterator<T> var0, Function<T, R> var1) {
      return () -> {
         return mapIterator(var0, var1);
      };
   }

   public static <T, R> Iterator<R> mapIterator(final Iterator<T> var0, final Function<T, R> var1) {
      return new Iterator<R>() {
         public boolean hasNext() {
            return var0.hasNext();
         }

         public R next() {
            return var1.apply(var0.next());
         }
      };
   }

   public static <T> Stream<? extends T> concat(Stream<? extends T>... var0) {
      if (var0.length == 0) {
         return Stream.empty();
      } else {
         Stream var1 = var0[0];

         for(int var2 = 1; var2 < var0.length; ++var2) {
            var1 = Stream.concat(var1, var0[var2]);
         }

         return var1;
      }
   }

   public static <T> T[] concat(T[] var0, T[] var1) {
      Object[] var2 = Arrays.copyOf(var0, var0.length + var1.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   public static <T, R> R[] mapArray(T[] var0, R[] var1, Function<T, R> var2) {
      Object[] var3 = Arrays.copyOf(var1, var0.length);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = var2.apply(var0[var4]);
      }

      return var3;
   }

   public static <T> Iterator<T> concatIterators(Collection<Iterator<? extends T>> var0) {
      return new ConcatIterator(var0);
   }

   @SafeVarargs
   public static <T> Iterator<T> concatIterators(Iterator<? extends T>... var0) {
      return new ConcatIterator(var0);
   }

   public static <T> Iterator<T> array2DIterator(T[][] var0) {
      LinkedList var1 = new LinkedList();
      Object[][] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object[] var5 = var2[var4];
         var1.add(arrayIterator(var5));
      }

      return concatIterators((Collection)var1);
   }

   public static <T> Iterator<T> arrayIterator(T[] var0) {
      return new ArrayIterator(var0);
   }

   public static String toValidFileName(String var0) {
      String[] var1 = invalidFileNameSequences;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         var0 = var0.replace(var4, "");
      }

      return var0;
   }

   public static String formatFileExtension(String var0, String var1) {
      String var2;
      try {
         var2 = (new File(var0)).getName();
      } catch (Exception var4) {
         var2 = var0;
      }

      if (!var2.contains(".")) {
         var0 = var0 + "." + var1;
      }

      return var0;
   }

   public static String getFileExtension(String var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1;
         try {
            var1 = (new File(var0)).getName();
         } catch (Exception var3) {
            var1 = var0;
         }

         int var2 = var1.lastIndexOf(".");
         return var2 != -1 ? var1.substring(var2 + 1) : null;
      }
   }

   public static String removeFileExtension(String var0) {
      String var1 = getFileExtension(var0);
      return var1 != null ? var0.substring(0, var0.length() - var1.length() - 1) : var0;
   }

   public static void openURL(String var0) {
      try {
         openURL(new URI(var0));
      } catch (URISyntaxException var2) {
         System.err.println("Could not open invalid url :" + var0);
      }

   }

   public static void openURL(URI var0) {
      System.out.println("Opening URL " + var0.toString());
      Thread var1 = new Thread(() -> {
         try {
            if (Desktop.isDesktopSupported()) {
               Desktop.getDesktop().browse(var0);
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      });
      var1.start();
   }

   public static boolean openExplorerAtFile(File var0) {
      if (var0.exists()) {
         Thread var1 = new Thread(() -> {
            try {
               if (Platform.get() == Platform.WINDOWS) {
                  Runtime.getRuntime().exec("explorer.exe /select," + var0.getAbsolutePath());
               } else {
                  File var1 = var0.getParentFile();
                  if (var1.exists() && var1.isDirectory() && Desktop.isDesktopSupported()) {
                     Desktop.getDesktop().open(var1);
                  }
               }
            } catch (Exception var2) {
               var2.printStackTrace();
            }

         });
         var1.start();
         return true;
      } else {
         GameLog.warn.println("Tried to open non existing file: " + var0.getAbsolutePath());
         return false;
      }
   }

   static {
      validFileNamePattern = Pattern.compile("[^" + (String)Stream.of(invalidFileNameSequences).map((var0) -> {
         return "\\" + var0;
      }).reduce("", (var0, var1) -> {
         return var0 + var1;
      }) + "]+");
   }

   public static class MetricValue {
      public final String suffix;
      public final BigDecimal divider;

      public MetricValue(String var1, BigDecimal var2) {
         this.suffix = var1;
         this.divider = var2;
      }

      public MetricValue(String var1, int var2) {
         this(var1, GameUtils.exponent(BigDecimal.ONE, var2));
      }
   }

   private static class ConcatIterator<T> implements Iterator<T> {
      public final LinkedList<Iterator<? extends T>> iterators;

      public ConcatIterator(Collection<Iterator<? extends T>> var1) {
         this.iterators = new LinkedList();
         this.iterators.addAll(var1);
      }

      @SafeVarargs
      public ConcatIterator(Iterator<? extends T>... var1) {
         this((Collection)Arrays.asList(var1));
      }

      private Iterator<? extends T> nextIterator() {
         while(!this.iterators.isEmpty()) {
            if (((Iterator)this.iterators.getFirst()).hasNext()) {
               return (Iterator)this.iterators.getFirst();
            }

            this.iterators.removeFirst();
         }

         return null;
      }

      public boolean hasNext() {
         return this.nextIterator() != null;
      }

      public T next() {
         Iterator var1 = this.nextIterator();
         if (var1 != null) {
            return var1.next();
         } else {
            throw new NoSuchElementException();
         }
      }
   }

   private static class ArrayIterator<T> implements Iterator<T> {
      public final T[] array;
      public int i;

      public ArrayIterator(T[] var1) {
         this.array = var1;
      }

      public boolean hasNext() {
         return this.i < this.array.length;
      }

      public T next() {
         return this.array[this.i++];
      }
   }
}
