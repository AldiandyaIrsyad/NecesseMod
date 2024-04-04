package necesse.engine.registries;

public class VersionMigration {
   public static String[][] oldTileStringIDs = new String[][]{{"stonefloor", "rockfloor"}, {"deepstonefloor", "deeprockfloor"}};
   public static String[][] oldObjectStringIDs = new String[][]{{"sprucedinnertable", "wooddinnertable"}, {"sprucedinnertable2", "wooddinnertable2"}, {"sprucedesk", "wooddesk"}, {"sprucemodulartable", "woodmodulartable"}, {"sprucechair", "woodchair"}, {"sprucebookshelf", "woodbookshelf"}, {"sprucebathtub", "woodbathtub"}, {"sprucebathtub2", "woodbathtubr"}, {"sprucetoilet", "woodtoilet"}, {"sprucebed", "woodbed"}, {"sprucebed2", "woodbed2"}, {"sprucedisplay", "itemstand"}, {"woolcarpet", "woolcarpetr", "woolcarpetd", "woolcarpetdr"}, {"leathercarpet", "leathercarpetr", "leathercarpetd", "leathercarpetdr"}, {"stonepressureplate", "rockpressureplate"}, {"deepstonepressureplate", "deeprockpressureplate"}};
   public static String[][] oldLogicGateStringIDs = new String[0][];
   public static String[][] oldItemStringIDs = new String[][]{{"brainonastick", "babyzombie"}, {"inefficientfeather", "tameostrich"}, {"weticicle", "petpenguin"}, {"exoticseeds", "petparrot"}, {"magicstilts", "petwalkingtorch"}, {"sprucelog", "log"}, {"healthregenpotion", "combatregenpotion"}, {"waterfaevinyl", "surfacevinyl"}, {"musesvinyl", "surfacenightvinyl"}, {"runningvinyl", "cavevinyl"}, {"grindthealarmsvinyl", "deepcavevinyl"}, {"bythefieldvinyl", "desertsurfacevinyl"}, {"sunstonesvinyl", "desertnightvinyl"}, {"caravantusksvinyl", "desertcavevinyl"}, {"homeatlastvinyl", "snowsurfacevinyl"}, {"telltalevinyl", "snownightvinyl"}, {"icyrusevinyl", "snowcavevinyl"}, {"icestarvinyl", "deepsnowcavevinyl"}, {"eyesofthedesertvinyl", "swampsurfacevinyl"}, {"rialtovinyl", "swampnightvinyl"}, {"silverlakevinyl", "swampcavevinyl"}, {"awayvinyl", "piratesurfacevinyl"}, {"kronosvinyl", "dungeonvinyl"}, {"lostgripvinyl", "raidvinyl"}, {"elektrakvinyl", "boss1vinyl"}, {"thecontrolroomvinyl", "boss2vinyl"}, {"airlockfailurevinyl", "boss3vinyl"}, {"konsoleglitchvinyl", "boss4vinyl"}, {"beatdownvinyl", "boss5vinyl"}, {"siegevinyl", "boss6vinyl"}, {"halodromevinyl", "boss7vinyl"}, {"milleniumvinyl", "boss8vinyl"}, {"kandiruvinyl", "boss9vinyl"}};
   public static String[][] oldMobStringIDs = new String[0][];
   public static String[][] oldBiomeStringIDs = new String[][]{{"forestvillage", "villageisland", "VillageIsland"}, {"dungeon", "dungeonisland", "DungeonIsland"}, {"piratevillage", "pirateisland", "PirateIsland"}};

   public VersionMigration() {
   }

   public static String tryFixStringID(String var0, String[][] var1) {
      int var2 = getFixedConversionIndex(var0, var1);
      return var2 != -1 ? var1[var2][0] : var0;
   }

   private static int getFixedConversionIndex(String var0, String[][] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].length >= 2) {
            for(int var3 = 0; var3 < var1[var2].length; ++var3) {
               if (var0.equals(var1[var2][var3])) {
                  return var2;
               }
            }
         }
      }

      return -1;
   }

   private static int getNewStringIDIndex(String var0, String[] var1, String[][] var2, int var3) {
      int var4;
      for(var4 = 0; var4 < var1.length; ++var4) {
         if (var1[var4] != null && var1[var4].equals(var0)) {
            return var4;
         }
      }

      if (var2 != null) {
         var4 = getFixedConversionIndex(var0, var2);
         if (var4 != -1) {
            return getNewStringIDIndex(var2[var4][0], var1, (String[][])null, var3);
         }
      }

      return var3;
   }

   private static int[] getConversionArray(String[] var0, String[] var1, String[][] var2, int var3) {
      if (var0 != null && var1 != null) {
         int[] var4 = new int[var0.length];

         for(int var5 = 0; var5 < var0.length; ++var5) {
            String var6 = var0[var5];
            if (var6 != null) {
               var4[var5] = getNewStringIDIndex(var6, var1, var2, var3);
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   private static boolean convertArray(int[] var0, int[] var1) {
      if (var1 == null) {
         return false;
      } else {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            int var3 = var0[var2];
            if (var3 >= 0 && var3 < var1.length) {
               var0[var2] = var1[var3];
            }
         }

         return true;
      }
   }

   public static String[][] concatOldStringIDs(String[][]... var0) {
      int var1 = 0;
      String[][][] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String[][] var5 = var2[var4];
         var1 += var5.length;
      }

      String[][] var8 = new String[var1][];
      var3 = 0;
      String[][][] var9 = var0;
      int var10 = var0.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         String[][] var7 = var9[var6];
         System.arraycopy(var7, 0, var8, var3, var7.length);
         var3 += var7.length;
      }

      return var8;
   }

   public static boolean convertArray(int[] var0, String[] var1, String[] var2, int var3, String[][] var4) {
      return convertArray(var0, getConversionArray(var1, var2, var4, var3));
   }

   public static boolean convertArray(int[] var0, String[] var1, String[] var2, int var3) {
      return convertArray(var0, var1, var2, var3, (String[][])null);
   }

   static {
      oldItemStringIDs = concatOldStringIDs(oldTileStringIDs, oldObjectStringIDs, oldItemStringIDs);
   }
}
