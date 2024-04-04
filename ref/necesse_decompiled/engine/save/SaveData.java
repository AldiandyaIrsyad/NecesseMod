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
import necesse.engine.world.WorldFile;

public class SaveData {
   private SaveComponent save;

   public SaveData(String var1) {
      this(new SaveComponent(var1));
   }

   public SaveData(String var1, String var2) {
      this(new SaveComponent(var1, var2));
   }

   public SaveData(SaveComponent var1) {
      Objects.requireNonNull(var1);
      this.save = var1;
   }

   public boolean isEmpty() {
      return this.save.isEmpty();
   }

   public void addSaveData(SaveData var1) {
      this.save.addComponent(var1.save);
   }

   public void removeSaveDataByName(String var1) {
      this.save.removeComponentsByName(var1);
   }

   public boolean removeFirstSaveDataByName(String var1) {
      return this.save.removeFirstComponentByName(var1);
   }

   public String getScript() {
      return this.save.getScript();
   }

   public String getScript(boolean var1) {
      return this.save.getScript(var1);
   }

   public LoadData toLoadData() {
      return new LoadData(this.save);
   }

   public void saveScriptRaw(File var1, boolean var2) throws IOException {
      SaveComponent.saveScriptRaw(var1, this.save, var2);
   }

   public void saveScript(File var1, boolean var2) {
      SaveComponent.saveScript(var1, this.save, var2);
   }

   public void saveScript(File var1) {
      SaveComponent.saveScript(var1, this.save);
   }

   public void saveScriptRaw(WorldFile var1, boolean var2) throws IOException {
      SaveComponent.saveScriptRaw(var1, this.save, var2);
   }

   public void saveScript(WorldFile var1, boolean var2) {
      SaveComponent.saveScript(var1, this.save, var2);
   }

   public void saveScript(WorldFile var1) {
      SaveComponent.saveScript(var1, this.save);
   }

   public void addBoolean(String var1, boolean var2) {
      this.save.addData(var1, var2);
   }

   public void addBoolean(String var1, boolean var2, String var3) {
      this.save.addData(var1, var2, var3);
   }

   public void addByte(String var1, byte var2) {
      this.save.addData(var1, var2);
   }

   public void addByte(String var1, byte var2, String var3) {
      this.save.addData(var1, var2, var3);
   }

   public void addShort(String var1, short var2) {
      this.save.addData(var1, var2);
   }

   public void addShort(String var1, short var2, String var3) {
      this.save.addData(var1, var2, var3);
   }

   public void addInt(String var1, int var2) {
      this.save.addData(var1, var2);
   }

   public void addInt(String var1, int var2, String var3) {
      this.save.addData(var1, var2, var3);
   }

   public void addLong(String var1, long var2) {
      this.save.addData(var1, var2);
   }

   public void addLong(String var1, long var2, String var4) {
      this.save.addData(var1, var2, var4);
   }

   public void addFloat(String var1, float var2) {
      this.save.addData(var1, var2);
   }

   public void addFloat(String var1, float var2, String var3) {
      this.save.addData(var1, var2, var3);
   }

   public void addDouble(String var1, double var2) {
      this.save.addData(var1, var2);
   }

   public void addDouble(String var1, double var2, String var4) {
      this.save.addData(var1, var2, var4);
   }

   public void addEnum(String var1, Enum var2) {
      this.save.addData(var1, SaveSerialize.serializeEnum(var2));
   }

   public void addEnum(String var1, Enum var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeEnum(var2), var3);
   }

   public void addUnsafeString(String var1, String var2) {
      this.save.addData(var1, var2);
   }

   public void addUnsafeString(String var1, String var2, String var3) {
      this.save.addData(var1, var2, var3);
   }

   public void addSafeString(String var1, String var2) {
      this.save.addData(var1, SaveComponent.toSafeData(var2));
   }

   public void addSafeString(String var1, String var2, String var3) {
      this.save.addData(var1, SaveComponent.toSafeData(var2), var3);
   }

   public void addPoint(String var1, Point var2) {
      this.save.addData(var1, SaveSerialize.serializePoint(var2));
   }

   public void addPoint(String var1, Point var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializePoint(var2), var3);
   }

   public void addDimension(String var1, Dimension var2) {
      this.save.addData(var1, SaveSerialize.serializeDimension(var2));
   }

   public void addDimension(String var1, Dimension var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeDimension(var2), var3);
   }

   public void addColor(String var1, Color var2) {
      this.save.addData(var1, SaveSerialize.serializeColor(var2));
   }

   public void addColor(String var1, Color var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeColor(var2), var3);
   }

   public void addSafeStringCollection(String var1, Collection<String> var2) {
      this.save.addData(var1, SaveSerialize.serializeSafeStringCollection(var2));
   }

   public void addSafeStringCollection(String var1, Collection<String> var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeSafeStringCollection(var2), var3);
   }

   public void addStringList(String var1, List<String> var2) {
      this.save.addData(var1, SaveSerialize.serializeStringList(var2));
   }

   public void addStringList(String var1, List<String> var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeStringList(var2), var3);
   }

   public void addStringHashSet(String var1, HashSet<String> var2) {
      this.save.addData(var1, SaveSerialize.serializeStringHashSet(var2));
   }

   public void addStringHashSet(String var1, HashSet<String> var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeStringHashSet(var2), var3);
   }

   public void addStringArray(String var1, String[] var2) {
      this.save.addData(var1, SaveSerialize.serializeStringArray(var2));
   }

   public void addStringArray(String var1, String[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeStringArray(var2), var3);
   }

   public void addShortArray(String var1, short[] var2) {
      this.save.addData(var1, SaveSerialize.serializeShortArray(var2));
   }

   public void addShortArray(String var1, short[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeShortArray(var2), var3);
   }

   public void addIntArray(String var1, int[] var2) {
      this.save.addData(var1, SaveSerialize.serializeIntArray(var2));
   }

   public void addIntArray(String var1, int[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeIntArray(var2), var3);
   }

   public void addByteArray(String var1, byte[] var2) {
      this.save.addData(var1, SaveSerialize.serializeByteArray(var2));
   }

   public void addByteArray(String var1, byte[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeByteArray(var2), var3);
   }

   public void addLongArray(String var1, long[] var2) {
      this.save.addData(var1, SaveSerialize.serializeLongArray(var2));
   }

   public void addLongArray(String var1, long[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeLongArray(var2), var3);
   }

   public void addBooleanArray(String var1, boolean[] var2) {
      this.save.addData(var1, SaveSerialize.serializeBooleanArray(var2));
   }

   public void addBooleanArray(String var1, boolean[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeBooleanArray(var2), var3);
   }

   public void addSmallBooleanArray(String var1, boolean[] var2) {
      this.save.addData(var1, SaveSerialize.serializeSmallBooleanArray(var2));
   }

   public void addSmallBooleanArray(String var1, boolean[] var2, String var3) {
      this.save.addData(var1, SaveSerialize.serializeSmallBooleanArray(var2), var3);
   }
}
