package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.layers.SimulatePriorityList;

public class CryptAshTile extends TerrainSplatterTile {
   public static double growChance = GameMath.getAverageSuccessRuns(12000.0);
   public static MobSpawnTable cryptSpawnTable = (new MobSpawnTable()).add(100, (String)"vampire");
   private final GameRandom drawRandom;

   public CryptAshTile() {
      super(false, "cryptash", "splattingmaskwide");
      this.mapColor = new Color(38, 38, 38);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      GrassTile.addSimulateGrow(var1, var2, var3, growChance, var4, "cryptgrass", var6, var7);
   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         if (var1.getObjectID(var2, var3) == 0 && GameRandom.globalRandom.getChance(growChance)) {
            GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cryptgrass"));
            if (var4.canPlace(var1, var2, var3, 0) == null) {
               var4.placeObject(var1, var2, var3, 0);
               var1.sendObjectUpdatePacket(var2, var3);
            }
         }

      }
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      if (GameRandom.globalRandom.getChance(0.025F) && !var1.getObject(var2, var3).drawsFullTile() && var1.getLightLevel(var2, var3).getLevel() > 0.0F) {
         int var4 = var2 * 32 + GameRandom.globalRandom.nextInt(32);
         int var5 = var3 * 32 + GameRandom.globalRandom.nextInt(32);
         boolean var6 = GameRandom.globalRandom.nextBoolean();
         var1.entityManager.addParticle((float)var4, (float)(var5 + 30), Particle.GType.COSMETIC).sprite(GameResources.fogParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 32, 16)).fadesAlpha(0.4F, 0.4F).size((var0, var1x, var2x, var3x) -> {
         }).height(30.0F).dontRotate().movesConstant(GameRandom.globalRandom.getFloatBetween(2.0F, 5.0F) * (Float)GameRandom.globalRandom.getOneOf((Object[])(1.0F, -1.0F)), 0.0F).modify((var1x, var2x, var3x, var4x) -> {
            var1x.mirror(var6, false);
         }).lifeTime(3000);
      }

   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public int getTerrainPriority() {
      return 200;
   }

   public MobSpawnTable getMobSpawnTable(TilePosition var1, MobSpawnTable var2) {
      return var1.level instanceof IncursionLevel ? var2 : cryptSpawnTable;
   }

   public int getMobSpawnPositionTickets(Level var1, int var2, int var3) {
      return 500;
   }
}
