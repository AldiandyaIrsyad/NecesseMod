package necesse.engine.save;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import necesse.engine.GameLog;
import necesse.engine.util.GameMath;
import necesse.engine.world.WorldFile;

public class LoadData {
   private SaveComponent save;

   public static LoadData newRaw(File var0, boolean var1) throws IOException, DataFormatException {
      return new LoadData(SaveComponent.loadScriptRaw(var0, var1));
   }

   private LoadData(File var1, boolean var2) {
      this(SaveComponent.loadScript(var1, var2));
   }

   public LoadData(File var1) {
      this(SaveComponent.loadScript(var1));
   }

   public LoadData(WorldFile var1) {
      this(SaveComponent.loadScript(var1));
   }

   public LoadData(String var1) {
      this(SaveComponent.loadScript(var1));
   }

   public LoadData(SaveComponent var1) {
      Objects.requireNonNull(var1);
      this.save = var1;
   }

   public String getName() {
      return this.save.getName();
   }

   public String getData() {
      return this.save.getData();
   }

   public boolean isArray() {
      return this.save.getType() == 2;
   }

   public boolean isData() {
      return this.save.getType() == 1;
   }

   public List<LoadData> getLoadData() {
      return (List)this.save.getComponents().stream().map(LoadData::new).collect(Collectors.toList());
   }

   public boolean isEmpty() {
      return this.save.isEmpty();
   }

   public List<LoadData> getLoadDataByName(String var1) {
      return (List)this.save.getComponentsByName(var1).stream().map(LoadData::new).collect(Collectors.toList());
   }

   public LoadData getFirstLoadDataByName(String var1) {
      SaveComponent var2 = this.save.getFirstComponentByName(var1);
      return var2 != null ? new LoadData(var2) : null;
   }

   public String getFirstDataByName(String var1) {
      return this.save.getFirstDataByName(var1);
   }

   public boolean hasLoadDataByName(String var1) {
      return this.save.hasComponentByName(var1);
   }

   public String getScript() {
      return this.save.getScript();
   }

   public String getScript(boolean var1) {
      return this.save.getScript(var1);
   }

   public SaveData toSaveData() {
      return new SaveData(this.save);
   }

   public static boolean getBoolean(LoadData var0) {
      return Boolean.parseBoolean(var0.getData());
   }

   public boolean getBoolean(String var1) {
      return getBoolean(this.getFirstLoadDataByName(var1));
   }

   public boolean getBoolean(String var1, boolean var2, boolean var3) {
      try {
         return this.getBoolean(var1);
      } catch (NullPointerException var5) {
         if (var3) {
            GameLog.warn.println("Could not load boolean " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public boolean getBoolean(String var1, boolean var2) {
      return this.getBoolean(var1, var2, true);
   }

   public static byte getByte(LoadData var0) {
      return Byte.parseByte(var0.getData());
   }

   public byte getByte(String var1) {
      return getByte(this.getFirstLoadDataByName(var1));
   }

   public byte getByte(String var1, byte var2, boolean var3) {
      try {
         return this.getByte(var1);
      } catch (NullPointerException | NumberFormatException var5) {
         if (var3) {
            GameLog.warn.println("Could not load byte " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public byte getByte(String var1, byte var2) {
      return this.getByte(var1, var2, true);
   }

   public byte getByte(String var1, byte var2, byte var3, byte var4, boolean var5) {
      return (byte)GameMath.limit(this.getByte(var1, var2, var5), var3, var4);
   }

   public byte getByte(String var1, byte var2, byte var3, byte var4) {
      return this.getByte(var1, var2, var3, var4, true);
   }

   public static short getShort(LoadData var0) {
      return Short.parseShort(var0.getData());
   }

   public short getShort(String var1) {
      return getShort(this.getFirstLoadDataByName(var1));
   }

   public short getShort(String var1, short var2, boolean var3) {
      try {
         return this.getShort(var1);
      } catch (NullPointerException | NumberFormatException var5) {
         if (var3) {
            GameLog.warn.println("Could not load short " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public short getShort(String var1, short var2) {
      return this.getShort(var1, var2, true);
   }

   public short getShort(String var1, short var2, short var3, short var4, boolean var5) {
      return (short)GameMath.limit(this.getShort(var1, var2, var5), var3, var4);
   }

   public short getShort(String var1, short var2, short var3, short var4) {
      return this.getShort(var1, var2, var3, var4, true);
   }

   public static int getInt(LoadData var0) {
      return Integer.parseInt(var0.getData());
   }

   public int getInt(String var1) {
      return getInt(this.getFirstLoadDataByName(var1));
   }

   public int getInt(String var1, int var2, boolean var3) {
      try {
         return this.getInt(var1);
      } catch (NullPointerException | NumberFormatException var5) {
         if (var3) {
            GameLog.warn.println("Could not load int " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public int getInt(String var1, int var2) {
      return this.getInt(var1, var2, true);
   }

   public int getInt(String var1, int var2, int var3, int var4, boolean var5) {
      return GameMath.limit(this.getInt(var1, var2, var5), var3, var4);
   }

   public int getInt(String var1, int var2, int var3, int var4) {
      return this.getInt(var1, var2, var3, var4, true);
   }

   public static long getLong(LoadData var0) {
      return Long.parseLong(var0.getData());
   }

   public long getLong(String var1) {
      return getLong(this.getFirstLoadDataByName(var1));
   }

   public long getLong(String var1, long var2, boolean var4) {
      try {
         return this.getLong(var1);
      } catch (NullPointerException | NumberFormatException var6) {
         if (var4) {
            GameLog.warn.println("Could not load long " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public long getLong(String var1, long var2) {
      return this.getLong(var1, var2, true);
   }

   public long getLong(String var1, long var2, long var4, long var6, boolean var8) {
      return GameMath.limit(this.getLong(var1, var2, var8), var4, var6);
   }

   public long getLong(String var1, long var2, long var4, long var6) {
      return this.getLong(var1, var2, var4, var6, true);
   }

   public static float getFloat(LoadData var0) {
      return Float.parseFloat(var0.getData());
   }

   public float getFloat(String var1) {
      return getFloat(this.getFirstLoadDataByName(var1));
   }

   public float getFloat(String var1, float var2, boolean var3) {
      try {
         return this.getFloat(var1);
      } catch (NullPointerException | NumberFormatException var5) {
         if (var3) {
            GameLog.warn.println("Could not load float " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public float getFloat(String var1, float var2) {
      return this.getFloat(var1, var2, true);
   }

   public float getFloat(String var1, float var2, float var3, float var4, boolean var5) {
      return GameMath.limit(this.getFloat(var1, var2, var5), var3, var4);
   }

   public float getFloat(String var1, float var2, float var3, float var4) {
      return this.getFloat(var1, var2, var3, var4, true);
   }

   public static double getDouble(LoadData var0) {
      return Double.parseDouble(var0.getData());
   }

   public double getDouble(String var1) {
      return getDouble(this.getFirstLoadDataByName(var1));
   }

   public double getDouble(String var1, double var2, boolean var4) {
      try {
         return this.getDouble(var1);
      } catch (NullPointerException | NumberFormatException var6) {
         if (var4) {
            GameLog.warn.println("Could not load double " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public double getDouble(String var1, double var2) {
      return this.getDouble(var1, var2, true);
   }

   public double getDouble(String var1, double var2, double var4, double var6, boolean var8) {
      return GameMath.limit(this.getDouble(var1, var2, var8), var4, var6);
   }

   public double getDouble(String var1, double var2, double var4, double var6) {
      return this.getDouble(var1, var2, var4, var6, true);
   }

   public static <T extends Enum<T>> T getEnum(Class<T> var0, LoadData var1) {
      return SaveSerialize.unserializeEnum(var0, var1.getData());
   }

   public <T extends Enum<T>> T getEnum(Class<T> var1, String var2) {
      return getEnum(var1, this.getFirstLoadDataByName(var2));
   }

   public <T extends Enum<T>> T getEnum(Class<T> var1, String var2, T var3, boolean var4) {
      try {
         return this.getEnum(var1, var2);
      } catch (Exception var6) {
         if (var4) {
            GameLog.warn.println("Could not load " + var1.getSimpleName() + " enum " + var2 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var3;
      }
   }

   public <T extends Enum<T>> T getEnum(Class<T> var1, String var2, T var3) {
      return this.getEnum(var1, var2, var3, true);
   }

   public static String getUnsafeString(LoadData var0) {
      return var0.getData();
   }

   public String getUnsafeString(String var1) {
      return getUnsafeString(this.getFirstLoadDataByName(var1));
   }

   public String getUnsafeString(String var1, String var2, boolean var3) {
      try {
         return this.getUnsafeString(var1);
      } catch (NullPointerException var5) {
         if (var3) {
            GameLog.warn.println("Could not load string " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public String getUnsafeString(String var1, String var2) {
      return this.getUnsafeString(var1, var2, true);
   }

   public static String getSafeString(LoadData var0) {
      return SaveComponent.fromSafeData(getUnsafeString(var0));
   }

   public String getSafeString(String var1) {
      return getSafeString(this.getFirstLoadDataByName(var1));
   }

   public String getSafeString(String var1, String var2, boolean var3) {
      String var4 = this.getUnsafeString(var1, var2, var3);
      return var4 == null ? null : SaveComponent.fromSafeData(var4);
   }

   public String getSafeString(String var1, String var2) {
      return this.getSafeString(var1, var2, true);
   }

   public static Point getPoint(LoadData var0) {
      return SaveSerialize.unserializePoint(var0.getData());
   }

   public Point getPoint(String var1) {
      return getPoint(this.getFirstLoadDataByName(var1));
   }

   public Point getPoint(String var1, Point var2, boolean var3) {
      try {
         return this.getPoint(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load point " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public Point getPoint(String var1, Point var2) {
      return this.getPoint(var1, var2, true);
   }

   public static Dimension getDimension(LoadData var0) {
      return SaveSerialize.unserializeDimension(var0.getData());
   }

   public Dimension getDimension(String var1) {
      return getDimension(this.getFirstLoadDataByName(var1));
   }

   public Dimension getDimension(String var1, Dimension var2, boolean var3) {
      try {
         return this.getDimension(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load dimension " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public Dimension getDimension(String var1, Dimension var2) {
      return this.getDimension(var1, var2, true);
   }

   public static Color getColor(LoadData var0) {
      return SaveSerialize.unserializeColor(var0.getData());
   }

   public Color getColor(String var1) {
      return getColor(this.getFirstLoadDataByName(var1));
   }

   public Color getColor(String var1, Color var2, boolean var3) {
      try {
         return this.getColor(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load color " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public Color getColor(String var1, Color var2) {
      return this.getColor(var1, var2, true);
   }

   public static Collection<String> getSafeStringCollection(LoadData var0) {
      return SaveSerialize.unserializeSafeStringCollection(var0.getData());
   }

   public Collection<String> getSafeStringCollection(String var1) {
      return getSafeStringCollection(this.getFirstLoadDataByName(var1));
   }

   public Collection<String> getSafeStringCollection(String var1, Collection<String> var2, boolean var3) {
      try {
         return this.getSafeStringCollection(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load string collection " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public Collection<String> getSafeStringCollection(String var1, Collection<String> var2) {
      return this.getSafeStringCollection(var1, var2, true);
   }

   public static List<String> getStringList(LoadData var0) {
      return SaveSerialize.unserializeStringList(var0.getData());
   }

   public List<String> getStringList(String var1) {
      return getStringList(this.getFirstLoadDataByName(var1));
   }

   public List<String> getStringList(String var1, List<String> var2, boolean var3) {
      try {
         return this.getStringList(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load string array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public List<String> getStringList(String var1, List<String> var2) {
      return this.getStringList(var1, var2, true);
   }

   public static HashSet<String> getStringHashSet(LoadData var0) {
      return SaveSerialize.unserializeStringHashSet(var0.getData());
   }

   public HashSet<String> getStringHashSet(String var1) {
      return getStringHashSet(this.getFirstLoadDataByName(var1));
   }

   public HashSet<String> getStringHashSet(String var1, HashSet<String> var2, boolean var3) {
      try {
         return this.getStringHashSet(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load string hash set " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public HashSet<String> getStringHashSet(String var1, HashSet<String> var2) {
      return this.getStringHashSet(var1, var2, true);
   }

   public static String[] getStringArray(LoadData var0) {
      return SaveSerialize.unserializeStringArray(var0.getData());
   }

   public String[] getStringArray(String var1) {
      return getStringArray(this.getFirstLoadDataByName(var1));
   }

   public String[] getStringArray(String var1, String[] var2, boolean var3) {
      try {
         return this.getStringArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load string array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public String[] getStringArray(String var1, String[] var2) {
      return this.getStringArray(var1, var2, true);
   }

   public static short[] getShortArray(LoadData var0) {
      return SaveSerialize.unserializeShortArray(var0.getData());
   }

   public short[] getShortArray(String var1) {
      return getShortArray(this.getFirstLoadDataByName(var1));
   }

   public short[] getShortArray(String var1, short[] var2, boolean var3) {
      try {
         return this.getShortArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load short array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public short[] getShortArray(String var1, short[] var2) {
      return this.getShortArray(var1, var2, true);
   }

   public static int[] getIntArray(LoadData var0) {
      return SaveSerialize.unserializeIntArray(var0.getData());
   }

   public int[] getIntArray(String var1) {
      return getIntArray(this.getFirstLoadDataByName(var1));
   }

   public int[] getIntArray(String var1, int[] var2, boolean var3) {
      try {
         return this.getIntArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load int array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public int[] getIntArray(String var1, int[] var2) {
      return this.getIntArray(var1, var2, true);
   }

   public static byte[] getByteArray(LoadData var0) {
      return SaveSerialize.unserializeByteArray(var0.getData());
   }

   public byte[] getByteArray(String var1) {
      return getByteArray(this.getFirstLoadDataByName(var1));
   }

   public byte[] getByteArray(String var1, byte[] var2, boolean var3) {
      try {
         return this.getByteArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load byte array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public byte[] getByteArray(String var1, byte[] var2) {
      return this.getByteArray(var1, var2, true);
   }

   public static long[] getLongArray(LoadData var0) {
      return SaveSerialize.unserializeLongArray(var0.getData());
   }

   public long[] getLongArray(String var1) {
      return getLongArray(this.getFirstLoadDataByName(var1));
   }

   public long[] getLongArray(String var1, long[] var2, boolean var3) {
      try {
         return this.getLongArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load long array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public long[] getLongArray(String var1, long[] var2) {
      return this.getLongArray(var1, var2, true);
   }

   public static boolean[] getBooleanArray(LoadData var0) {
      return SaveSerialize.unserializeBooleanArray(var0.getData());
   }

   public boolean[] getBooleanArray(String var1) {
      return getBooleanArray(this.getFirstLoadDataByName(var1));
   }

   public boolean[] getBooleanArray(String var1, boolean[] var2, boolean var3) {
      try {
         return this.getBooleanArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load boolean array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public boolean[] getBooleanArray(String var1, boolean[] var2) {
      return this.getBooleanArray(var1, var2, true);
   }

   public static boolean[] getSmallBooleanArray(LoadData var0) {
      return SaveSerialize.unserializeSmallBooleanArray(var0.getData());
   }

   public boolean[] getSmallBooleanArray(String var1) {
      return getSmallBooleanArray(this.getFirstLoadDataByName(var1));
   }

   public boolean[] getSmallBooleanArray(String var1, boolean[] var2, boolean var3) {
      try {
         return this.getSmallBooleanArray(var1);
      } catch (Exception var5) {
         if (var3) {
            GameLog.warn.println("Could not load small boolean array " + var1 + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
         }

         return var2;
      }
   }

   public boolean[] getSmallBooleanArray(String var1, boolean[] var2) {
      return this.getSmallBooleanArray(var1, var2, true);
   }
}
