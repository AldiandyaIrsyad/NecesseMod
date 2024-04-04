package necesse.inventory.container.travel;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;

public class IslandData {
   public final int islandX;
   public final int islandY;
   public final int biome;
   public final boolean isOutsideWorldBorder;
   public final boolean discovered;
   public final boolean visited;
   public final boolean hasDeath;
   public final boolean canTravel;
   public final GameMessage settlementName;

   public IslandData(int var1, int var2, int var3, boolean var4, boolean var5, boolean var6, boolean var7, boolean var8, GameMessage var9) {
      this.islandX = var1;
      this.islandY = var2;
      this.biome = var3;
      this.isOutsideWorldBorder = var4;
      this.discovered = var5;
      this.visited = var6;
      this.hasDeath = var7;
      this.canTravel = var8;
      this.settlementName = var9;
   }

   public static IslandData generateIslandData(Server var0, ServerClient var1, TravelContainer var2, int var3, int var4) {
      boolean var5 = !var0.world.worldEntity.isWithinWorldBorder(var3, var4);
      boolean var6 = !var5 && var1.hasDiscoveredIsland(var3, var4);
      boolean var7 = !var5 && var1.hasVisitedIsland(var3, var4);
      boolean var8 = !var5 && (var6 || var2.playerLevel.isIslandPosition() && TravelContainer.dist(var2.playerLevel.getIslandX(), var2.playerLevel.getIslandY(), var3, var4) <= var2.knowRange);
      boolean var9 = !var5 && var2.isWithinTravelRange(var3, var4);
      boolean var10 = !var5 && var1.streamDeathLocations().anyMatch((var2x) -> {
         return var2x.levelIdentifier.isSameIsland(var3, var4);
      });
      int var11 = var8 ? var0.levelCache.getBiomeID(var3, var4) : BiomeRegistry.UNKNOWN.getID();
      GameMessage var12 = var5 ? null : var0.levelCache.getSettlement(var3, var4).name;
      return new IslandData(var3, var4, var11, var5, var6, var7, var10, var9, var12);
   }
}
