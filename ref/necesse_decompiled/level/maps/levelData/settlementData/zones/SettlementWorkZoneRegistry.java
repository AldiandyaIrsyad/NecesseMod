package necesse.level.maps.levelData.settlementData.zones;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;

public class SettlementWorkZoneRegistry extends ClassedGameRegistry<SettlementWorkZone, ZoneRegistryElement> {
   public static int FORESTRY_ID;
   public static int HUSBANDRY_ID;
   public static int FERTILIZE_ID;
   public static final SettlementWorkZoneRegistry instance = new SettlementWorkZoneRegistry();

   public SettlementWorkZoneRegistry() {
      super("SettlementZone", 32767);
   }

   public void registerCore() {
      FORESTRY_ID = registerZone("forestry", SettlementForestryZone.class);
      HUSBANDRY_ID = registerZone("husbandry", SettlementHusbandryZone.class);
      FERTILIZE_ID = registerZone("fertilize", SettlementFertilizeZone.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerZone(String var0, Class<? extends SettlementWorkZone> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register work zones");
      } else {
         try {
            return instance.register(var0, new ZoneRegistryElement(var1));
         } catch (NoSuchMethodException var3) {
            throw new IllegalArgumentException(var1.getSimpleName() + " must have 3 different constructors: One with no parameters, one with LoadData parameter and one with PacketReader parameter.");
         }
      }
   }

   public static int replaceZone(String var0, Class<? extends SettlementWorkZone> var1) {
      try {
         return instance.replace(var0, new ZoneRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         throw new IllegalArgumentException(var1.getSimpleName() + " must have 3 different constructors: One with no parameters, one with LoadData parameter and one with PacketReader parameter.");
      }
   }

   public static int getZoneID(Class<? extends SettlementWorkZone> var0) {
      return instance.getElementID(var0);
   }

   public static int getZoneID(String var0) {
      return instance.getElementID(var0);
   }

   public static SettlementWorkZone getNewZone(int var0) {
      ZoneRegistryElement var1 = (ZoneRegistryElement)instance.getElement(var0);

      try {
         SettlementWorkZone var2 = (SettlementWorkZone)var1.newInstance(new Object[0]);
         var2.idData.setData(var1.getID(), var1.getStringID());
         return var2;
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static <T extends SettlementWorkZone> T getNewZone(Class<T> var0) {
      return (SettlementWorkZone)var0.cast(getNewZone(getZoneID(var0)));
   }

   protected static class ZoneRegistryElement extends ClassIDDataContainer<SettlementWorkZone> {
      public ZoneRegistryElement(Class<? extends SettlementWorkZone> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
