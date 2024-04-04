package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import necesse.engine.modLoader.LoadedMod;
import necesse.level.maps.Level;
import necesse.level.maps.layers.LevelLayer;
import necesse.level.maps.layers.LogicLevelLayer;
import necesse.level.maps.layers.ObjectLevelLayer;
import necesse.level.maps.layers.ObjectRotationLevelLayer;
import necesse.level.maps.layers.RainingLevelLayer;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.layers.TileLevelLayer;
import necesse.level.maps.layers.WireLevelLayer;

public class LevelLayerRegistry extends ClassedGameRegistry<LevelLayer, LayerRegistryElement> {
   public static int TILE_LAYER;
   public static int OBJECT_LAYER;
   public static int OBJECT_ROTATIONS_LAYER;
   public static int LOGIC_LAYER;
   public static int WIRE_LAYER;
   public static int RAINING_LAYER;
   public static int SETTLEMENT_LAYER;
   public static final LevelLayerRegistry instance = new LevelLayerRegistry();

   public LevelLayerRegistry() {
      super("LevelLayer", 32767);
   }

   public void registerCore() {
      TILE_LAYER = registerLayer("tiles", TileLevelLayer.class);
      OBJECT_LAYER = registerLayer("objects", ObjectLevelLayer.class);
      OBJECT_ROTATIONS_LAYER = registerLayer("objectRotations", ObjectRotationLevelLayer.class);
      LOGIC_LAYER = registerLayer("logic", LogicLevelLayer.class);
      WIRE_LAYER = registerLayer("wire", WireLevelLayer.class);
      RAINING_LAYER = registerLayer("raining", RainingLevelLayer.class);
      SETTLEMENT_LAYER = registerLayer("settlement", SettlementLevelLayer.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerLayer(String var0, Class<? extends LevelLayer> var1) {
      try {
         if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register level layers");
         } else {
            return instance.register(var0, new LayerRegistryElement(var1));
         }
      } catch (NoSuchMethodException var3) {
         throw new IllegalArgumentException(var1.getSimpleName() + " does not have a constructor with level parameter");
      }
   }

   public static int replaceLayer(String var0, Class<? extends LevelLayer> var1) {
      try {
         return instance.replace(var0, new LayerRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         throw new IllegalArgumentException(var1.getSimpleName() + " does not have a constructor with level parameter");
      }
   }

   public static int getLayerID(Class<? extends LevelLayer> var0) {
      try {
         return instance.getElementIDRaw(var0);
      } catch (NoSuchElementException var2) {
         System.err.println("Could not find LevelLayer id for " + var0.getSimpleName());
         return -1;
      }
   }

   public static int getLayerID(String var0) {
      return instance.getElementID(var0);
   }

   public static LevelLayer[] getNewLayersArray(Level var0) {
      LevelLayer[] var1 = new LevelLayer[instance.size()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         LayerRegistryElement var3 = (LayerRegistryElement)instance.getElement(var2);

         try {
            LevelLayer var4 = (LevelLayer)var3.newInstance(new Object[]{var0});
            var4.idData.setData(var3.getIDData().getID(), var3.getIDData().getStringID());
            var1[var2] = var4;
         } catch (InvocationTargetException | InstantiationException | IllegalAccessException var5) {
            throw new RuntimeException("Could not create new LevelLayer object for " + var3.layerClass.getSimpleName(), var5);
         }
      }

      return var1;
   }

   protected static class LayerRegistryElement extends ClassIDDataContainer<LevelLayer> {
      private Class<? extends LevelLayer> layerClass;

      public LayerRegistryElement(Class<? extends LevelLayer> var1) throws NoSuchMethodException {
         super(var1, Level.class);
         this.layerClass = var1;
      }
   }
}
