package necesse.engine.registries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDIncursionDataItem;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemArray;
import necesse.engine.network.gameNetworkData.GNDItemBoolean;
import necesse.engine.network.gameNetworkData.GNDItemByte;
import necesse.engine.network.gameNetworkData.GNDItemDouble;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.gameNetworkData.GNDItemFloat;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemGameItem;
import necesse.engine.network.gameNetworkData.GNDItemInt;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.gameNetworkData.GNDItemLong;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.gameNetworkData.GNDItemNull;
import necesse.engine.network.gameNetworkData.GNDItemShort;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDRegistry extends GameRegistry<RegistryItem> {
   public static final GNDRegistry instance = new GNDRegistry();

   public GNDRegistry() {
      super("GNDItem", 32767);
   }

   public void registerCore() {
      registerGNDItem("null", GNDItemNull.class);
      registerGNDItem("bool", GNDItemBoolean.class);
      registerGNDItem("byte", GNDItemByte.class);
      registerGNDItem("short", GNDItemShort.class);
      registerGNDItem("int", GNDItemInt.class);
      registerGNDItem("long", GNDItemLong.class);
      registerGNDItem("float", GNDItemFloat.class);
      registerGNDItem("double", GNDItemDouble.class);
      registerGNDItem("string", GNDItemString.class);
      registerGNDItem("map", GNDItemMap.class);
      registerGNDItem("array", GNDItemArray.class);
      registerGNDItem("itemenchant", GNDItemEnchantment.class);
      registerGNDItem("inventory", GNDItemInventory.class);
      registerGNDItem("gameitem", GNDItemGameItem.class);
      registerGNDItem("gamedamage", GNDItemGameDamage.class);
      registerGNDItem("inventoryitem", GNDItemInventoryItem.class);
      registerGNDItem("incursiondata", GNDIncursionDataItem.class);
   }

   protected void onRegister(RegistryItem var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
   }

   public static int registerGNDItem(String var0, Class<? extends GNDItem> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register GND items");
      } else {
         try {
            return instance.register(var0, new RegistryItem(var1));
         } catch (NoSuchMethodException var3) {
            System.err.println("Could not register " + instance.objectCallName + " " + var1.getSimpleName() + ":");
            System.err.println("\tMust have a constructor with PacketReader parameter");
            System.err.println("\tMust have a constructor with LoadData parameter");
            return -1;
         }
      }
   }

   public static GNDItem loadGNDItem(LoadData var0) {
      int var1 = -1;
      String var2 = null;
      if (!var0.isArray()) {
         return null;
      } else {
         if (var0.hasLoadDataByName("gndType")) {
            var2 = var0.getUnsafeString("gndType");
         } else if (var0.hasLoadDataByName("index")) {
            var1 = var0.getInt("index");
         }

         try {
            if (var2 != null && !var2.isEmpty()) {
               return ((RegistryItem)instance.getElementRaw(instance.getElementID(var2))).newInstance(var0);
            } else {
               RegistryItem var3 = (RegistryItem)instance.getElementRaw(var1);
               LoadData var4 = var0.getFirstLoadDataByName("item");
               if (var4 != null) {
                  return var3.newInstance(var4);
               } else {
                  GameLog.warn.println("Failed GND item load: Could not find item data, {" + var0.getScript() + "}");
                  return null;
               }
            }
         } catch (NoSuchElementException var5) {
            if (var2 != null && !var2.isEmpty()) {
               GameLog.warn.println("Failed GND item load: Invalid gndType " + var2 + ", {" + var0.getScript() + "}");
            } else {
               GameLog.warn.println("Failed GND item load: Invalid index " + var1 + ", {" + var0.getScript() + "}");
            }

            return null;
         } catch (IllegalAccessException | InvocationTargetException | InstantiationException var6) {
            GameLog.warn.println("Failed GND item load: Error on instance creation");
            var6.printStackTrace(GameLog.warn);
            return null;
         }
      }
   }

   public static void writeGNDItem(SaveData var0, GNDItem var1) {
      var0.addUnsafeString("gndType", var1.getStringID());
      var1.addSaveData(var0);
   }

   public static GNDItem readGNDItem(PacketReader var0) {
      int var1 = var0.getNextShortUnsigned();

      try {
         return ((RegistryItem)instance.getElementRaw(var1)).newInstance(var0);
      } catch (NoSuchElementException var3) {
         GameLog.warn.println("Could not find GND item with id " + var1);
         return null;
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var4) {
         GameLog.warn.println("Failed GND item packet: Error on instance creation");
         var4.printStackTrace(GameLog.warn);
         return null;
      }
   }

   public static void writeGNDItem(PacketWriter var0, GNDItem var1) {
      var0.putNextShortUnsigned(var1.getID());
      var1.writePacket(var0);
   }

   public static <C extends GNDItem> void applyIDData(C var0) {
      try {
         RegistryItem var1 = (RegistryItem)instance.streamElements().filter((var1x) -> {
            return var1x.itemClass == var0.getClass();
         }).findFirst().orElseThrow(NoSuchElementException::new);
         var0.idData.setData(var1.data.getID(), var1.data.getStringID());
      } catch (NoSuchElementException var2) {
         throw new IllegalStateException("Cannot construct unregistered " + instance.objectCallName + " class " + var0.getClass().getSimpleName());
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((RegistryItem)var1, var2, var3, var4);
   }

   protected static class RegistryItem implements IDDataContainer {
      private final IDData data = new IDData();
      public final Class<? extends GNDItem> itemClass;
      private final Constructor<? extends GNDItem> packetConstructor;
      private final Constructor<? extends GNDItem> loadConstructor;

      public RegistryItem(Class<? extends GNDItem> var1) throws NoSuchMethodException {
         this.itemClass = var1;
         this.packetConstructor = var1.getConstructor(PacketReader.class);
         this.loadConstructor = var1.getConstructor(LoadData.class);
      }

      public IDData getIDData() {
         return this.data;
      }

      public GNDItem newInstance(PacketReader var1) throws IllegalAccessException, InvocationTargetException, InstantiationException {
         return (GNDItem)this.packetConstructor.newInstance(var1);
      }

      public GNDItem newInstance(LoadData var1) throws IllegalAccessException, InvocationTargetException, InstantiationException {
         return (GNDItem)this.loadConstructor.newInstance(var1);
      }
   }
}
