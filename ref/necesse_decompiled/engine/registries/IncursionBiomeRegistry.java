package necesse.engine.registries;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameRandom;
import necesse.level.maps.incursion.DesertDeepCaveIncursionBiome;
import necesse.level.maps.incursion.ForestDeepCaveIncursionBiome;
import necesse.level.maps.incursion.GraveyardIncursionBiome;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.MoonArenaIncursionBiome;
import necesse.level.maps.incursion.SlimeCaveIncursionBiome;
import necesse.level.maps.incursion.SnowDeepCaveIncursionBiome;
import necesse.level.maps.incursion.SpiderCastleIncursionBiome;
import necesse.level.maps.incursion.SunArenaIncursionBiome;
import necesse.level.maps.incursion.SwampDeepCaveIncursionBiome;

public class IncursionBiomeRegistry extends GameRegistry<IncursionBiomeRegistryElement<?>> {
   public static final IncursionBiomeRegistry instance = new IncursionBiomeRegistry();

   public IncursionBiomeRegistry() {
      super("IncursionBiome", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "incursions"));
      registerBiome("forestcave", new ForestDeepCaveIncursionBiome(), 1);
      registerBiome("snowcave", new SnowDeepCaveIncursionBiome(), 1);
      registerBiome("swampcave", new SwampDeepCaveIncursionBiome(), 1);
      registerBiome("desertcave", new DesertDeepCaveIncursionBiome(), 1);
      registerBiome("slimecave", new SlimeCaveIncursionBiome(), 2);
      registerBiome("graveyard", new GraveyardIncursionBiome(), 2);
      registerBiome("spidercastle", new SpiderCastleIncursionBiome(), 2);
      registerBiome("sunarena", new SunArenaIncursionBiome(), 3);
      registerBiome("moonarena", new MoonArenaIncursionBiome(), 3);
   }

   protected void onRegister(IncursionBiomeRegistryElement<?> var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
      instance.streamElements().map((var0) -> {
         return var0.biome;
      }).forEach(IncursionBiome::updateLocalDisplayName);
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         IncursionBiomeRegistryElement var2 = (IncursionBiomeRegistryElement)var1.next();
         var2.biome.onIncursionBiomeRegistryClosed();
      }

   }

   public static <T extends IncursionBiome> T registerBiome(String var0, T var1, int var2) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register incursion biomes");
      } else {
         return ((IncursionBiomeRegistryElement)instance.registerObj(var0, new IncursionBiomeRegistryElement(var1, var2))).biome;
      }
   }

   public static List<IncursionBiome> getBiomes() {
      return (List)instance.streamElements().map((var0) -> {
         return var0.biome;
      }).collect(Collectors.toList());
   }

   public static int getTotalBiomes() {
      return instance.size();
   }

   public static IncursionBiome getBiome(String var0) {
      return getBiome(getBiomeID(var0));
   }

   public static IncursionBiome getBiome(int var0) {
      return var0 == -1 ? null : ((IncursionBiomeRegistryElement)instance.getElement(var0)).biome;
   }

   public static int getBiomeTier(int var0) {
      return ((IncursionBiomeRegistryElement)instance.getElement(var0)).baseTier;
   }

   public static int getBiomeID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getBiomeIDRaw(String var0) throws NoSuchElementException {
      return instance.getElementIDRaw(var0);
   }

   public static String getBiomeStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static IncursionBiome getRandomBiome(long var0) {
      int var2 = (new GameRandom(var0)).nextInt(instance.size());
      return ((IncursionBiomeRegistryElement)instance.getElement(var2)).biome;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((IncursionBiomeRegistryElement)var1, var2, var3, var4);
   }

   protected static class IncursionBiomeRegistryElement<T extends IncursionBiome> implements IDDataContainer {
      public final T biome;
      public final int baseTier;

      public IncursionBiomeRegistryElement(T var1, int var2) {
         this.biome = var1;
         this.baseTier = var2;
      }

      public IDData getIDData() {
         return this.biome.idData;
      }
   }
}
