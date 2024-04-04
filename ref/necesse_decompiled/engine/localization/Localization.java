package necesse.engine.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.fileLanguage.FileLanguage;
import necesse.engine.localization.fileLanguage.Translation;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.LocalReplacement;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.gfx.gameFont.FontManager;

public class Localization {
   private static final Object LOCK = new Object();
   private static final ArrayList<Language> officialLanguages = new ArrayList();
   private static final ArrayList<Language> languages = new ArrayList();
   public static final Translation EnglishTranslation = new Translation("en.lang");
   public static final Language NoTranslation = registerLanguage(new Language("no_translation", (String)null, "No translation", "No translation", (String)null) {
      public String translate(String var1, String var2) {
         return var1 + "." + var2;
      }

      public int countTranslationWords() {
         return 0;
      }

      public void exportTranslationCSV(String var1) {
      }

      public void importTranslationCSV(String var1) {
      }

      public boolean isDebugOnly() {
         return true;
      }

      public String getCharactersUsed() {
         return "";
      }
   }, true);
   public static final Language English;
   public static final Language ChineseSimplified;
   public static final Language ChineseTraditional;
   public static final Language Russian;
   public static final Language BrazilianPortuguese;
   public static final Language Spanish;
   public static final Language German;
   public static final Language Polish;
   public static final Language Czech;
   public static final Language Turkish;
   public static final Language Japanese;
   public static final Language French;
   public static final Language Ukrainian;
   public static final Language Danish;
   public static final Language Swedish;
   public static final Language Norwegian;
   public static final Language Hungarian;
   public static final Language Italian;
   public static final Language Korean;
   public static final Language Thai;
   public static final Language Vietnamese;
   public static final Language Indonesian;
   public static final Language Lithuanian;
   public static final Language Dutch;
   public static final Language Finnish;
   public static final Language Catalan;
   public static final Language defaultLang;
   private static Language currentLang;
   private static LinkedList<LocalizationChangeListener> nextListeners;
   private static LinkedList<LocalizationChangeListener> listeners;
   private static HashMap<Integer, Long> warnTimers;
   private static final int warnCooldown = 10000;

   public Localization() {
   }

   public static <T extends Language> T registerLanguage(T var0) {
      return registerLanguage(var0, false);
   }

   private static <T extends Language> T registerLanguage(T var0, boolean var1) {
      if (!var0.stringID.matches("[a-zA-Z0-9_\\-]+")) {
         throw new IllegalArgumentException("Tried to register language with invalid stringID: \"" + var0.stringID + "\"");
      } else if (languages.stream().anyMatch((var1x) -> {
         return var1x.stringID.equals(var0.stringID);
      })) {
         throw new IllegalArgumentException("Tried to register language with duplicate stringID: \"" + var0.stringID + "\"");
      } else {
         if (var1) {
            officialLanguages.add(var0);
         }

         languages.add(var0);
         return var0;
      }
   }

   public static void reloadLanguageFiles() {
      Iterator var0 = languages.iterator();

      while(var0.hasNext()) {
         Language var1 = (Language)var0.next();
         var1.reload(ModLoader.getEnabledMods());
         var1.updateUnique();
      }

      updateListeners();
   }

   public static Language getLanguageStringID(String var0) {
      return (Language)languages.stream().filter((var1) -> {
         return var1.stringID.equals(var0);
      }).findFirst().orElse((Object)null);
   }

   public static boolean isOfficial(Language var0) {
      return officialLanguages.stream().filter((var1) -> {
         return var1 == var0;
      }).findFirst().orElse((Object)null) != null;
   }

   public static Language getCurrentLang() {
      return currentLang;
   }

   private static void warn(String var0, String var1) {
      int var2 = (var0 + "." + var1).hashCode();
      long var3 = 0L;
      if (warnTimers.containsKey(var2)) {
         var3 = (Long)warnTimers.get(var2);
      }

      if (var3 < System.currentTimeMillis()) {
         GameLog.warn.println("Translation of " + var0 + "." + var1 + " is not found.");
         warnTimers.put(var2, System.currentTimeMillis() + 10000L);
      }

   }

   public static void setCurrentLang(Language var0) {
      setCurrentLang(var0, true);
   }

   private static void setCurrentLang(Language var0, boolean var1) {
      if (getCurrentLang() != var0) {
         currentLang = var0;
         GameLog.debug.println("Changed current language to " + var0.stringID);
         if (FontManager.bit != null && var0 != defaultLang) {
            long var2 = System.currentTimeMillis();
            FontManager.updateFont(var0);
            GameLog.debug.println("Font update took " + (System.currentTimeMillis() - var2) + " ms");
         }

         if (var1) {
            updateListeners();
         }

      }
   }

   private static void updateListeners() {
      synchronized(LOCK) {
         listeners.addAll(nextListeners);
         nextListeners.clear();
         cleanListeners();
         Iterator var1 = listeners.iterator();

         while(var1.hasNext()) {
            LocalizationChangeListener var2 = (LocalizationChangeListener)var1.next();
            var2.onChange(currentLang);
         }

      }
   }

   public static void loadModsLanguage() {
      Iterator var0 = languages.iterator();

      while(var0.hasNext()) {
         Language var1 = (Language)var0.next();
         Iterator var2 = ModLoader.getEnabledMods().iterator();

         while(var2.hasNext()) {
            LoadedMod var3 = (LoadedMod)var2.next();
            var1.loadMod(var3);
         }
      }

   }

   public static Language[] getLanguages() {
      return (Language[])languages.toArray(new Language[0]);
   }

   public static void cleanListeners() {
      synchronized(LOCK) {
         listeners.removeIf(LocalizationChangeListener::isDisposed);
         nextListeners.removeIf(LocalizationChangeListener::isDisposed);
      }
   }

   public static int getListenersSize() {
      return listeners.size() + nextListeners.size();
   }

   public static LocalizationChangeListener addListener(LocalizationChangeListener var0) {
      synchronized(LOCK) {
         nextListeners.add(var0);
         return var0;
      }
   }

   public static String translate(String var0, String var1, boolean var2) {
      String var3 = getCurrentLang().translate(var0, var1);
      if (var3 == null) {
         var3 = defaultLang.translate(var0, var1);
         if (var3 == null) {
            if (var2) {
               warn(var0, var1);
            }

            return var0 + "." + var1;
         }
      }

      return var3;
   }

   public static String translate(String var0, String var1) {
      return translate(var0, var1, true);
   }

   public static String translate(String var0, String var1, String var2, String var3) {
      return (new LocalMessage(var0, var1, var2, var3)).translate();
   }

   public static String translate(String var0, String var1, String var2, Object var3) {
      return (new LocalMessage(var0, var1, var2, var3.toString())).translate();
   }

   public static String translate(String var0, String var1, String... var2) {
      return (new LocalMessage(var0, var1, var2)).translate();
   }

   public static String translate(String var0, String var1, Object... var2) {
      return (new LocalMessage(var0, var1, var2)).translate();
   }

   public static String translate(String var0, String var1, ArrayList<LocalReplacement> var2) {
      return (new LocalMessage(var0, var1, var2)).translate();
   }

   static {
      English = registerLanguage(new FileLanguage("en", "english", EnglishTranslation, "English", "English"), true);
      ChineseSimplified = registerLanguage(new FileLanguage("zh-CN", "schinese", new Translation("zh-CN.lang", EnglishTranslation), "Chinese - Simplified", "\u7b80\u4f53\u4e2d\u6587"), false);
      ChineseTraditional = registerLanguage(new FileLanguage("zh-TW", "tchinese", new Translation("zh-TW.lang", EnglishTranslation), "Chinese - Traditional", "\u7e41\u9ad4\u4e2d\u6587"), false);
      Russian = registerLanguage(new FileLanguage("ru", "russian", new Translation("ru.lang", EnglishTranslation), "Russian", "\u0420\u0443\u0441\u0441\u043a\u0438\u0439"), false);
      BrazilianPortuguese = registerLanguage(new FileLanguage("pt-BR", "brazilian", new Translation("pt-BR.lang", EnglishTranslation), "Portuguese - Brazil", "Portugu\u00eas - Brasil"), false);
      Spanish = registerLanguage(new FileLanguage("es", "spanish", new Translation("es.lang", EnglishTranslation), "Spanish", "Espa\u00f1ol"), false);
      German = registerLanguage(new FileLanguage("de", "german", new Translation("de.lang", EnglishTranslation), "German", "Deutsch"), false);
      Polish = registerLanguage(new FileLanguage("pl", "polish", new Translation("pl.lang", EnglishTranslation), "Polish", "Polski"), false);
      Czech = registerLanguage(new FileLanguage("cs", "czech", new Translation("cs.lang", EnglishTranslation), "Czech", "\u010ce\u0161tina"), false);
      Turkish = registerLanguage(new FileLanguage("tr", "turkish", new Translation("tr.lang", EnglishTranslation), "Turkish", "T\u00fcrk\u00e7e"), false);
      Japanese = registerLanguage(new FileLanguage("ja", "japanese", new Translation("ja.lang", EnglishTranslation), "Japanese", "\u65e5\u672c\u8a9e"), false);
      French = registerLanguage(new FileLanguage("fr", "french", new Translation("fr.lang", EnglishTranslation), "French", "Fran\u00e7ais"), false);
      Ukrainian = registerLanguage(new FileLanguage("uk", "ukrainian", new Translation("uk.lang", EnglishTranslation), "Ukrainian", "\u0423\u043a\u0440\u0430\u0457\u043d\u0441\u044c\u043a\u0430"), false);
      Danish = registerLanguage(new FileLanguage("da", "danish", new Translation("da.lang", EnglishTranslation), "Danish", "Dansk"), false);
      Swedish = registerLanguage(new FileLanguage("se", "swedish", new Translation("se.lang", EnglishTranslation), "Swedish", "Svenska"), false);
      Norwegian = registerLanguage(new FileLanguage("no", "norwegian", new Translation("no.lang", EnglishTranslation), "Norwegian", "Norsk"), false);
      Hungarian = registerLanguage(new FileLanguage("hu", "hungarian", new Translation("hu.lang", EnglishTranslation), "Hungarian", "Magyar"), false);
      Italian = registerLanguage(new FileLanguage("it", "italian", new Translation("it.lang", EnglishTranslation), "Italian", "Italiano"), false);
      Korean = registerLanguage(new FileLanguage("kr", "koreana", new Translation("kr.lang", EnglishTranslation), "Korean", "\ud55c\uad6d\uc5b4"), false);
      Thai = registerLanguage(new FileLanguage("th", "thai", new Translation("th.lang", EnglishTranslation), "Thai", "\u0e44\u0e17\u0e22"), false);
      Vietnamese = registerLanguage(new FileLanguage("vi", "vietnamese", new Translation("vi.lang", EnglishTranslation), "Vietnamese", "Ti\u1ebfng Vi\u1ec7t"), false);
      Indonesian = registerLanguage(new FileLanguage("id", "indonesian", new Translation("id.lang", EnglishTranslation), "Indonesian", "Indonesia"), false);
      Lithuanian = registerLanguage(new FileLanguage("lt", "lithuanian", new Translation("lt.lang", EnglishTranslation), "Lithuanian", "Lietuvi\u0161kai"), false);
      Dutch = registerLanguage(new FileLanguage("nl", "dutch", new Translation("nl.lang", EnglishTranslation), "Dutch", "Nederlands"), false);
      Finnish = registerLanguage(new FileLanguage("fi", "finnish", new Translation("fi.lang", EnglishTranslation), "Finnish", "Suomi"), false);
      Catalan = registerLanguage(new FileLanguage("ca", "catalan", new Translation("ca.lang", EnglishTranslation), "Catalan", "Catal\u00e0"), false);
      String[] var0 = new String[]{".DS_Store", ".gitignore"};
      Pattern var1 = Pattern.compile("([a-zA-Z0-9-_]+)\\.lang");
      File[] var2 = (new File(GlobalData.rootPath() + "locale/")).listFiles();
      if (var2 != null) {
         File[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            if (!Arrays.stream(var0).anyMatch((var1x) -> {
               return var1x.equals(var6.getName());
            })) {
               Matcher var7 = var1.matcher(var6.getName());
               if (var6.isFile() && var7.find()) {
                  String var8 = var7.group(1);
                  if (!languages.stream().anyMatch((var1x) -> {
                     return var1x.stringID.equals(var8);
                  })) {
                     String var9 = FileLanguage.loadFileLanguage(var8, var6.getName());
                     if (var9 != null) {
                        GameLog.warn.println("Error loading " + var6.getName() + ": " + var9);
                     } else {
                        System.out.println("Registered unsupported language " + var8);
                     }
                  }
               } else {
                  GameLog.warn.println("Locale file " + var6.getName() + " does not match name requirements");
               }
            }
         }
      }

      defaultLang = English;
      currentLang = defaultLang;
      nextListeners = new LinkedList();
      listeners = new LinkedList();
      warnTimers = new HashMap();
   }
}
