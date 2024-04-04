package necesse.engine.util;

import java.util.HashMap;

public class GameBlackboard {
   private HashMap<String, Object> map = new HashMap();

   public GameBlackboard() {
   }

   public GameBlackboard copy() {
      GameBlackboard var1 = new GameBlackboard();
      var1.map.putAll(this.map);
      return var1;
   }

   public <C> C get(Class<? extends C> var1, String var2, C var3) {
      try {
         return var1.cast(this.map.getOrDefault(var2, var3));
      } catch (ClassCastException var5) {
         return var3;
      }
   }

   public <C> C get(Class<? extends C> var1, String var2) {
      return this.get(var1, var2, (Object)null);
   }

   public <C> C getNotNull(Class<? extends C> var1, String var2, C var3) {
      Object var4 = this.get(var1, var2, var3);
      return var4 == null ? var3 : var4;
   }

   public boolean getBoolean(String var1) {
      return (Boolean)this.getNotNull(Boolean.class, var1, false);
   }

   public byte getByte(String var1) {
      return (Byte)this.getNotNull(Byte.class, var1, (byte)0);
   }

   public short getShort(String var1) {
      return (Short)this.getNotNull(Short.class, var1, Short.valueOf((short)0));
   }

   public int getInt(String var1) {
      return (Integer)this.getNotNull(Integer.class, var1, 0);
   }

   public long getLong(String var1) {
      return (Long)this.getNotNull(Long.class, var1, 0L);
   }

   public float getFloat(String var1) {
      return (Float)this.getNotNull(Float.class, var1, 0.0F);
   }

   public double getDouble(String var1) {
      return (Double)this.getNotNull(Double.class, var1, 0.0);
   }

   public String getString(String var1, String var2) {
      return (String)this.getNotNull(String.class, var1, var2);
   }

   public String getString(String var1) {
      return this.getString(var1, (String)null);
   }

   public GameBlackboard set(String var1, Object var2) {
      this.map.put(var1, var2);
      return this;
   }
}
