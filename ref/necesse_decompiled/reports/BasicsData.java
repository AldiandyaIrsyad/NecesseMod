package necesse.reports;

import com.codedisaster.steamworks.SteamNativeHandle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameInfo;
import necesse.engine.GameLaunch;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.steam.SteamData;

public class BasicsData {
   public final HashMap<String, String> data = new HashMap();
   public final ArrayList<Exception> getErrors = new ArrayList();

   public BasicsData(String var1) {
      this.data.put("generated_on", this.getString(() -> {
         return (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
      }));
      this.data.put("authentication", this.getString(GameAuth::getAuthentication));
      this.data.put("steam_name", this.getString(SteamData::getSteamName));
      this.data.put("steam_build", this.getString(() -> {
         return SteamData.getApps().getAppBuildId();
      }));
      this.data.put("game_state", var1);
      this.data.put("game_version", this.getString(GameInfo::getFullVersionString));
      this.data.put("game_language", this.getString(() -> {
         return Localization.getCurrentLang().stringID;
      }));
      this.data.put("launch_parameters", GameLaunch.fullLaunchParameters == null ? "" : GameLaunch.fullLaunchParameters);
      this.addList("total_loaded_mods", "loaded_mod", ModLoader::getEnabledMods, this.data, (var0) -> {
         return var0.id + ", v" + var0.version + " - " + var0.name + " (" + (var0.workshopFileID == null ? -1L : SteamNativeHandle.getNativeHandle(var0.workshopFileID)) + ")";
      });
      this.addList("total_found_mods", "found_mod", ModLoader::getNotEnabledMods, this.data, (var0) -> {
         return var0.id + ", v" + var0.version + " - " + var0.name + " (" + (var0.workshopFileID == null ? -1L : SteamNativeHandle.getNativeHandle(var0.workshopFileID)) + ")";
      });
   }

   protected String getString(Supplier<Object> var1) {
      return this.getString(var1, (var0) -> {
         return "ERR: " + var0.getClass().getSimpleName();
      });
   }

   protected String getString(Supplier<Object> var1, String var2) {
      return this.getString(var1, (var1x) -> {
         return var2;
      });
   }

   protected String getString(Supplier<Object> var1, Function<Exception, Object> var2) {
      try {
         return String.valueOf(var1.get());
      } catch (Exception var6) {
         Exception var3 = var6;
         this.getErrors.add(var6);

         try {
            return String.valueOf(var2.apply(var3));
         } catch (Exception var5) {
            this.getErrors.add(var5);
            return "ERR_RETURN";
         }
      }
   }

   protected <T> T getObject(Supplier<T> var1, T var2) {
      try {
         return var1.get();
      } catch (Exception var4) {
         this.getErrors.add(var4);
         return var2;
      }
   }

   protected <T> void arrayObjects(Supplier<T[]> var1, BiFunction<T, Integer, Boolean> var2, Consumer<Integer> var3) {
      this.listObjects(() -> {
         return Arrays.asList((Object[])var1.get());
      }, var2, var3);
   }

   protected <T> void streamObjects(Supplier<Stream<T>> var1, BiFunction<T, Integer, Boolean> var2, Consumer<Integer> var3) {
      this.listObjects(() -> {
         return (Iterable)((Stream)var1.get()).collect(Collectors.toList());
      }, var2, var3);
   }

   protected <T> void listObjects(Supplier<Iterable<T>> var1, BiFunction<T, Integer, Boolean> var2, Consumer<Integer> var3) {
      int var4 = 0;

      try {
         Iterable var5 = (Iterable)var1.get();
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();

            try {
               if ((Boolean)var2.apply(var7, var4)) {
                  ++var4;
               }
            } catch (Exception var9) {
               this.getErrors.add(var9);
            }
         }
      } catch (Exception var10) {
         this.getErrors.add(var10);
      }

      var3.accept(var4);
   }

   protected <T> void addArray(String var1, String var2, Supplier<T[]> var3, HashMap<String, String> var4, Function<T, String> var5) {
      this.addList(var1, var2, () -> {
         return Arrays.asList((Object[])var3.get());
      }, var4, var5);
   }

   protected <T> void addStream(String var1, String var2, Supplier<Stream<T>> var3, HashMap<String, String> var4, Function<T, String> var5) {
      this.addList(var1, var2, () -> {
         return (Iterable)((Stream)var3.get()).collect(Collectors.toList());
      }, var4, var5);
   }

   protected <T> void addList(String var1, String var2, Supplier<Iterable<T>> var3, HashMap<String, String> var4, Function<T, String> var5) {
      this.listObjects(var3, (var3x, var4x) -> {
         String var5x = (String)var5.apply(var3x);
         if (var5x == null) {
            return false;
         } else {
            var4.put(var2 + var4x, var5x);
            return true;
         }
      }, (var2x) -> {
         var4.put(var1, Integer.toString(var2x));
      });
   }
}
