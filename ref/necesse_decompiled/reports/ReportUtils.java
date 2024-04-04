package necesse.reports;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;

public class ReportUtils {
   public ReportUtils() {
   }

   private static String getLocale(String var0, String var1, String var2) {
      try {
         return Localization.translate(var0, var1);
      } catch (Exception var4) {
         return var2;
      }
   }

   private static String getString(Supplier<Object> var0) {
      return getString(var0, (var0x) -> {
         return "ERR: " + var0x.getClass().getSimpleName();
      });
   }

   private static String getString(Supplier<Object> var0, Function<Exception, Object> var1) {
      try {
         return String.valueOf(var0.get());
      } catch (Exception var5) {
         Exception var2 = var5;

         try {
            return String.valueOf(var1.apply(var2));
         } catch (Exception var4) {
            return "ERR_RETURN";
         }
      }
   }

   public static String sendCrashReport(CrashReportData var0, String var1) {
      try {
         HashMap var2 = new HashMap(var0.data);
         var2.put("user_details", var1);
         var2.put("report", getString(() -> {
            return var0.getFullReport((File)null);
         }));
         var2.put("details", var1);
         sendBody(var2);
         return null;
      } catch (UnsupportedEncodingException | ProtocolException | MalformedURLException var3) {
         var3.printStackTrace();
         return getLocale("ui", "sendreporterr", "Unknown error sending report");
      } catch (IOException var4) {
         var4.printStackTrace();
         return getLocale("ui", "sendreportfail", "Could not send report, please try again!");
      }
   }

   public static String sendFeedback(FeedbackData var0, String var1) {
      try {
         HashMap var2 = new HashMap(var0.data);
         var2.put("user_message", var1);
         var2.put("feedback", var0.generateFullFeedback(var1));
         sendBody(var2);
         return null;
      } catch (UnsupportedEncodingException | ProtocolException | MalformedURLException var3) {
         var3.printStackTrace();
         return getLocale("ui", "sendfeedbackerr", "Unknown error sending feedback");
      } catch (IOException var4) {
         var4.printStackTrace();
         return getLocale("ui", "sendfeedbackfail", "Could not send feedback, please try again!");
      }
   }

   private static void sendBody(Map<String, String> var0) throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException {
      URL var1 = new URL(new String((new BigInteger("2561226737445096045315360388843531708906633749058401940533")).toByteArray()));
      URLConnection var2 = var1.openConnection();
      HttpURLConnection var3 = (HttpURLConnection)var2;
      var3.setRequestMethod("POST");
      var3.setDoOutput(true);
      StringJoiner var4 = new StringJoiner("&");
      Iterator var5 = var0.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         var4.add(URLEncoder.encode((String)var6.getKey(), "UTF-8") + "=" + URLEncoder.encode((String)var6.getValue(), "UTF-8"));
      }

      byte[] var12 = var4.toString().getBytes(StandardCharsets.UTF_8);
      int var13 = var12.length;
      var3.setFixedLengthStreamingMode(var13);
      var3.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      var3.setRequestProperty("Connection", "close");
      var3.connect();
      var3.setConnectTimeout(30000);
      OutputStream var7 = var3.getOutputStream();

      try {
         var7.write(var12);
      } catch (Throwable var11) {
         if (var7 != null) {
            try {
               var7.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (var7 != null) {
         var7.close();
      }

   }
}
