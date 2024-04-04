package necesse.engine.network.gameNetworkData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemMap extends GNDItem {
   private HashMap<Integer, MapValue> dataMap;

   public GNDItemMap() {
      this.dataMap = new HashMap();
   }

   public GNDItemMap(Packet var1) {
      this(new PacketReader(var1));
   }

   public GNDItemMap(PacketReader var1) {
      this();
      this.readPacket(var1);
   }

   public GNDItemMap(LoadData var1) {
      this();
      this.applyLoadData(var1);
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.dataMap.keySet().iterator();

      while(var2.hasNext()) {
         int var3 = (Integer)var2.next();
         MapValue var4 = (MapValue)this.dataMap.get(var3);
         GNDItem var5 = var4.item;
         SaveData var6 = new SaveData(var4.hashString != null ? var4.hashString : Integer.toString(var3));
         GNDRegistry.writeGNDItem(var6, var5);
         var1.addSaveData(var6);
      }

   }

   public void applyLoadData(LoadData var1) {
      this.dataMap.clear();
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         String var4 = var3.getName();
         this.setItem(var4, GNDRegistry.loadGNDItem(var3));
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("map[");
      Iterator var2 = this.dataMap.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.append(((MapValue)var3.getValue()).hashString != null ? ((MapValue)var3.getValue()).hashString : ((Integer)var3.getKey()).toString()).append(" = ").append(((MapValue)var3.getValue()).item.toString());
         if (var2.hasNext()) {
            var1.append(",");
         }
      }

      return var1 + "]";
   }

   public boolean isDefault() {
      return false;
   }

   public boolean equals(GNDItem var1) {
      return var1 instanceof GNDItemMap ? this.sameKeys((GNDItemMap)var1) : false;
   }

   public GNDItemMap copy() {
      GNDItemMap var1 = new GNDItemMap();
      Iterator var2 = this.dataMap.keySet().iterator();

      while(var2.hasNext()) {
         int var3 = (Integer)var2.next();
         MapValue var4 = (MapValue)this.dataMap.get(var3);
         if (var4.hashString != null) {
            var1.setItem(var4.hashString, var4.item.copy());
         } else {
            var1.setItem(var3, var4.item.copy());
         }
      }

      return var1;
   }

   public Packet getContentPacket() {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      this.writePacket(var2);
      return var1;
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dataMap.size());
      Iterator var2 = this.dataMap.keySet().iterator();

      while(var2.hasNext()) {
         int var3 = (Integer)var2.next();
         GNDItem var4 = ((MapValue)this.dataMap.get(var3)).item;
         var1.putNextInt(var3);
         GNDRegistry.writeGNDItem(var1, var4);
      }

   }

   public void readPacket(PacketReader var1) {
      this.dataMap.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextInt();
         this.setItem(var4, GNDRegistry.readGNDItem(var1));
      }

   }

   public boolean hasKey(String var1) {
      return this.dataMap.containsKey(var1.hashCode());
   }

   public int getMapSize() {
      return this.dataMap.size();
   }

   private GNDItemMap setItem(int var1, MapValue var2) {
      if (var2 != null && var2.item != null && !var2.item.isDefault()) {
         if (var2.hashString == null) {
            MapValue var3 = (MapValue)this.dataMap.get(var1);
            if (var3 != null) {
               var2 = new MapValue(var3.hashString, var2.item);
            }
         }

         this.dataMap.put(var1, var2);
      } else {
         this.dataMap.remove(var1);
      }

      return this;
   }

   public GNDItemMap setItem(int var1, GNDItem var2) {
      return this.setItem(var1, new MapValue((String)null, var2));
   }

   public GNDItem getItem(int var1) {
      MapValue var2 = (MapValue)this.dataMap.get(var1);
      return var2 == null ? null : var2.item;
   }

   public Set<Integer> getKeySet() {
      return this.dataMap.keySet();
   }

   public Set<String> getKeyStringSet() {
      return (Set)this.dataMap.entrySet().stream().map((var0) -> {
         return ((MapValue)var0.getValue()).hashString == null ? Integer.toString((Integer)var0.getKey()) : ((MapValue)var0.getValue()).hashString;
      }).collect(Collectors.toSet());
   }

   public GNDItemMap setItem(String var1, GNDItem var2) {
      MapValue var4 = new MapValue(var1, var2);

      int var3;
      try {
         var3 = Integer.parseInt(var1);
         var4.hashString = null;
      } catch (NumberFormatException var6) {
         var3 = var1.hashCode();
      }

      return this.setItem(var3, var4);
   }

   public GNDItem getItem(String var1) {
      boolean var2;
      int var3;
      try {
         var3 = Integer.parseInt(var1);
         var2 = true;
      } catch (NumberFormatException var5) {
         var3 = var1.hashCode();
         var2 = false;
      }

      MapValue var4 = (MapValue)this.dataMap.get(var3);
      if (var4 != null) {
         if (!var2) {
            var4.hashString = var1;
         }

         return var4.item;
      } else {
         return null;
      }
   }

   public GNDItemMap setBoolean(String var1, boolean var2) {
      return this.setItem(var1, new GNDItemBoolean(var2));
   }

   public boolean getBoolean(String var1) {
      try {
         GNDItem var2 = this.getItem(var1);
         return var2 != null && ((GNDItem.GNDPrimitive)var2).getBoolean();
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public GNDItemMap setByte(String var1, byte var2) {
      return this.setItem(var1, new GNDItemByte(var2));
   }

   public byte getByte(String var1) {
      return this.getByte(var1, (byte)0);
   }

   public byte getByte(String var1, byte var2) {
      try {
         GNDItem var3 = this.getItem(var1);
         return var3 == null ? var2 : ((GNDItem.GNDPrimitive)var3).getByte();
      } catch (ClassCastException var4) {
         return var2;
      }
   }

   public GNDItemMap setShort(String var1, short var2) {
      return this.setItem(var1, new GNDItemShort(var2));
   }

   public short getShort(String var1) {
      return this.getShort(var1, (short)0);
   }

   public short getShort(String var1, short var2) {
      try {
         GNDItem var3 = this.getItem(var1);
         return var3 == null ? var2 : ((GNDItem.GNDPrimitive)var3).getShort();
      } catch (ClassCastException var4) {
         return var2;
      }
   }

   public GNDItemMap setInt(String var1, int var2) {
      return this.setItem(var1, new GNDItemInt(var2));
   }

   public int getInt(String var1) {
      return this.getInt(var1, 0);
   }

   public int getInt(String var1, int var2) {
      try {
         GNDItem var3 = this.getItem(var1);
         return var3 == null ? var2 : ((GNDItem.GNDPrimitive)var3).getInt();
      } catch (ClassCastException var4) {
         return var2;
      }
   }

   public GNDItemMap setLong(String var1, long var2) {
      return this.setItem(var1, new GNDItemLong(var2));
   }

   public long getLong(String var1) {
      return this.getLong(var1, 0L);
   }

   public long getLong(String var1, long var2) {
      try {
         GNDItem var4 = this.getItem(var1);
         return var4 == null ? var2 : ((GNDItem.GNDPrimitive)var4).getLong();
      } catch (ClassCastException var5) {
         return var2;
      }
   }

   public GNDItemMap setFloat(String var1, float var2) {
      return this.setItem(var1, new GNDItemFloat(var2));
   }

   public float getFloat(String var1) {
      return this.getFloat(var1, 0.0F);
   }

   public float getFloat(String var1, float var2) {
      try {
         GNDItem var3 = this.getItem(var1);
         return var3 == null ? var2 : ((GNDItem.GNDPrimitive)var3).getFloat();
      } catch (ClassCastException var4) {
         return var2;
      }
   }

   public GNDItemMap setDouble(String var1, double var2) {
      return this.setItem(var1, new GNDItemDouble(var2));
   }

   public double getDouble(String var1) {
      return this.getDouble(var1, 0.0);
   }

   public double getDouble(String var1, double var2) {
      try {
         GNDItem var4 = this.getItem(var1);
         return var4 == null ? var2 : ((GNDItem.GNDPrimitive)var4).getDouble();
      } catch (ClassCastException var5) {
         return var2;
      }
   }

   public GNDItemMap setString(String var1, String var2) {
      return this.setItem(var1, new GNDItemString(var2));
   }

   public String getString(String var1) {
      return this.getString(var1, "");
   }

   public String getString(String var1, String var2) {
      GNDItem var3 = this.getItem(var1);
      return var3 == null ? var2 : var3.toString();
   }

   public boolean sameKeys(GNDItemMap var1, String... var2) {
      return sameKeys(this, var1, var2);
   }

   public static boolean sameKeys(GNDItemMap var0, GNDItemMap var1, String... var2) {
      HashSet var3 = new HashSet();
      if (var2.length == 0) {
         Iterator var4;
         String var5;
         for(var4 = var0.getKeyStringSet().iterator(); var4.hasNext(); var3.add(var5)) {
            var5 = (String)var4.next();
            if (var1.hasKey(var5)) {
               if (!equals(var0.getItem(var5), var1.getItem(var5))) {
                  return false;
               }
            } else if (!var0.getItem(var5).isDefault()) {
               return false;
            }
         }

         var4 = var1.getKeyStringSet().iterator();

         while(var4.hasNext()) {
            var5 = (String)var4.next();
            if (!var3.contains(var5)) {
               if (var0.hasKey(var5)) {
                  if (!equals(var1.getItem(var5), var0.getItem(var5))) {
                     return false;
                  }
               } else if (!var1.getItem(var5).isDefault()) {
                  return false;
               }
            }
         }
      } else {
         String[] var10 = var2;
         int var11 = var2.length;

         for(int var6 = 0; var6 < var11; ++var6) {
            String var7 = var10[var6];
            boolean var8 = var0.hasKey(var7);
            boolean var9 = var1.hasKey(var7);
            if (var8 && var9) {
               if (!equals(var0.getItem(var7), var1.getItem(var7))) {
                  return false;
               }
            } else if (var8) {
               if (!var0.getItem(var7).isDefault()) {
                  return false;
               }
            } else if (var9 && !var1.getItem(var7).isDefault()) {
               return false;
            }
         }
      }

      return true;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }

   private static class MapValue {
      private String hashString;
      private final GNDItem item;

      public MapValue(String var1, GNDItem var2) {
         this.hashString = var1;
         this.item = var2;
      }
   }
}
