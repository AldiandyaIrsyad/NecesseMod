package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.entity.pickup.QuestItemPickupEntity;
import necesse.entity.pickup.StarBarrierPickupEntity;

public class PickupRegistry extends ClassedGameRegistry<PickupEntity, PickupRegistryElement> {
   public static final PickupRegistry instance = new PickupRegistry();

   private PickupRegistry() {
      super("PickupEntity", 250);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "pickups"));
      registerPickup("item", ItemPickupEntity.class);
      registerPickup("questitem", QuestItemPickupEntity.class);
      registerPickup("starbarrier", StarBarrierPickupEntity.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerPickup(String var0, Class<? extends PickupEntity> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register pickups");
      } else {
         try {
            return instance.register(var0, new PickupRegistryElement(var1));
         } catch (NoSuchMethodException var3) {
            System.err.println("Could not register PickupEntity " + var1.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   public static PickupEntity getPickup(int var0) {
      try {
         return (PickupEntity)((PickupRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static PickupEntity getPickup(String var0) {
      return getPickup(getPickupID(var0));
   }

   public static int getPickupID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getPickupID(Class<? extends PickupEntity> var0) {
      return instance.getElementID(var0);
   }

   public static String getPickupStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   protected static class PickupRegistryElement extends ClassIDDataContainer<PickupEntity> {
      public PickupRegistryElement(Class<? extends PickupEntity> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
