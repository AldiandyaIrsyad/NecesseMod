package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.world.worldEvent.WorldEvent;

public class WorldEventRegistry extends ClassedGameRegistry<WorldEvent, WorldEventRegistryElement> {
   public static final WorldEventRegistry instance = new WorldEventRegistry();

   private WorldEventRegistry() {
      super("WorldEvent", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "worldevents"));
   }

   protected void onRegistryClose() {
   }

   public static int registerEvent(String var0, Class<? extends WorldEvent> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register world events");
      } else {
         try {
            return instance.register(var0, new WorldEventRegistryElement(var1));
         } catch (NoSuchMethodException var3) {
            System.err.println("Could not register WorldEvent " + var1.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   public static WorldEvent getEvent(int var0) {
      try {
         return (WorldEvent)((WorldEventRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static WorldEvent getEvent(String var0) {
      return getEvent(getEventID(var0));
   }

   public static int getEventID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getEventID(Class<? extends WorldEvent> var0) {
      return instance.getElementID(var0);
   }

   public static String getEventStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   protected static class WorldEventRegistryElement extends ClassIDDataContainer<WorldEvent> {
      public WorldEventRegistryElement(Class<? extends WorldEvent> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
