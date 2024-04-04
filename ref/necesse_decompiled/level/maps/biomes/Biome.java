package necesse.level.maps.biomes;

import java.awt.Color;
import java.io.FileNotFoundException;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldGenerator;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameMusic;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.plains.PlainsBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;

public class Biome {
   public final IDData idData = new IDData();
   public static MobSpawnTable defaultSurfaceMobs = (new MobSpawnTable()).add(80, (String)"zombie").add(20, (String)"zombiearcher");
   public static MobSpawnTable defaultCaveMobs = (new MobSpawnTable()).add(80, (String)"zombie").add(20, (String)"zombiearcher").add(10, MobSpawnTable.canSpawnEither(12, 11, 101), (String)"crawlingzombie").add(15, (String)"goblin").add(10, MobSpawnTable.canSpawnEither(6, 9, 101), (String)"vampire").add(3, MobSpawnTable.canSpawnEither(9, 14, 101), (String)"cavemole");
   public static MobSpawnTable forestCaveMobs;
   public static MobSpawnTable defaultDeepCaveMobs;
   public static FishingLootTable commonFish;
   public static FishingLootTable defaultSurfaceFish;
   public static FishingLootTable defaultCaveFish;
   public static MobSpawnTable defaultSurfaceCritters;
   public static MobSpawnTable defaultCaveCritters;
   public GameTexture iconTexture;
   public GameTexture rainTexture;
   protected GameMessage displayName;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public Biome() {
      if (BiomeRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct Biome objects when biome registry is closed, since they are a static registered objects. Use BiomeRegistry.getBiome(...) to get biomes.");
      }
   }

   public void onBiomeRegistryClosed() {
   }

   public void clientTick(Level var1) {
   }

   public void serverTick(Level var1) {
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("biome", this.getStringID());
   }

   public void updateLocalDisplayName() {
      this.displayName = this.getNewLocalization();
   }

   public GameMessage getLocalization() {
      return this.displayName;
   }

   protected void loadIconTexture() {
      try {
         this.iconTexture = GameTexture.fromFileRaw("biomes/" + this.getStringID());
      } catch (FileNotFoundException var2) {
         this.iconTexture = null;
      }

   }

   protected void loadRainTexture() {
      this.rainTexture = GameTexture.fromFile("rainfall");
   }

   public final void loadTextures() {
      this.loadIconTexture();
      this.loadRainTexture();
   }

   public GameTexture getIconTexture(boolean var1) {
      if (this.iconTexture == null) {
         return var1 ? Settings.UI.biome_unknown.highlighted : Settings.UI.biome_unknown.active;
      } else {
         return this.iconTexture;
      }
   }

   public GameTexture getRainTexture(Level var1) {
      return this.rainTexture;
   }

   public String getDisplayName() {
      return this.displayName.translate();
   }

   public boolean canRain(Level var1) {
      return !var1.isCave;
   }

   public int getRainTimeInSeconds(Level var1, GameRandom var2) {
      return var2.getIntBetween(240, 420);
   }

   public int getDryTimeInSeconds(Level var1, GameRandom var2) {
      return var2.getIntBetween(1200, 1800);
   }

   public float getAverageRainingPercent(Level var1) {
      if (!this.canRain(var1)) {
         return 0.0F;
      } else {
         float var2 = 330.0F;
         float var3 = 1500.0F;
         return var2 / (var2 + var3);
      }
   }

   public Color getRainColor(Level var1) {
      return new Color(50, 50, 200, 75);
   }

   public void tickRainEffect(GameCamera var1, Level var2, int var3, int var4, float var5) {
      if (GameRandom.globalRandom.nextInt(60) == 0) {
         byte var6 = 20;
         Color var7 = this.getRainColor(var2);
         Color var8 = new Color(var7.getRed(), var7.getGreen(), var7.getBlue(), (int)((float)var7.getAlpha() * var5));
         var2.entityManager.addParticle(ParticleOption.base((float)(var3 * 32 + GameRandom.globalRandom.nextInt(32 - var6)), (float)(var4 * 32 + GameRandom.globalRandom.nextInt(32 - var6))), Particle.GType.COSMETIC).lifeTime(600).sprite((var1x, var2x, var3x, var4x) -> {
            int var5 = GameResources.rainBlobParticle.getWidth() / var6;
            return var1x.add(GameResources.rainBlobParticle.sprite((int)(var4x * (float)var5), 0, var6));
         }).color(var8);
      }

   }

   public GameSound getRainSound(Level var1) {
      return (GameSound)GameRandom.globalRandom.getOneOf((Object[])(GameResources.rain1, GameResources.rain2, GameResources.rain3, GameResources.rain4, GameResources.rain5));
   }

   public boolean hasVillage() {
      return false;
   }

   public Level getNewLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      if (var3 == 0) {
         return this.getNewSurfaceLevel(var1, var2, var4, var5);
      } else if (var3 == -1) {
         return this.getNewCaveLevel(var1, var2, var3, var4, var5);
      } else {
         return var3 < -1 ? this.getNewDeepCaveLevel(var1, var2, var3, var4, var5) : null;
      }
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return new BasicSurfaceLevel(var1, var2, var3, var5);
   }

   public final Level getNewSurfaceLevel(int var1, int var2, Server var3, WorldEntity var4) {
      return this.getNewSurfaceLevel(var1, var2, WorldGenerator.getIslandSize(var1, var2), var3, var4);
   }

   public Level getNewCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new BasicCaveLevel(var1, var2, var3, var5);
   }

   public Level getNewDeepCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new BasicDeepCaveLevel(var1, var2, var3, var5);
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      if (!var1.isCave) {
         return defaultSurfaceMobs;
      } else {
         return var1.getIslandDimension() == -2 ? defaultDeepCaveMobs : forestCaveMobs;
      }
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return !var1.isCave ? defaultSurfaceCritters : defaultCaveCritters;
   }

   public FishingLootTable getFishingLootTable(FishingSpot var1) {
      return !var1.tile.level.isCave ? defaultSurfaceFish : defaultCaveFish;
   }

   public LootTable getExtraMobDrops(Mob var1) {
      return new LootTable();
   }

   public LootTable getExtraPrivateMobDrops(Mob var1, ServerClient var2) {
      return new LootTable();
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      if (var1.isCave) {
         return var1.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.SecretsOfTheForest}) : new MusicList(new GameMusic[]{MusicRegistry.DepthsOfTheForest});
      } else {
         return var1.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.AwakeningTwilight}) : new MusicList(new GameMusic[]{MusicRegistry.ForestPath});
      }
   }

   public float getSpawnRateMod(Level var1) {
      return 1.0F;
   }

   public float getSpawnCapMod(Level var1) {
      return 1.0F;
   }

   public int getFindIslandCost(PlayerStats var1) {
      if (!this.isDiscoverable()) {
         return -1;
      } else {
         int var2 = BiomeRegistry.getTickets(this.getID());
         if (var2 == 0) {
            return -1;
         } else {
            float var3 = (float)GameMath.limit(var2, 0, 1000) / 1000.0F;
            float var4 = Math.abs(var3 - 1.0F);
            return 200 + (int)(var4 * 1000.0F);
         }
      }
   }

   public boolean isDiscoverable() {
      return BiomeRegistry.isDiscoverable(this.getID());
   }

   static {
      forestCaveMobs = (new MobSpawnTable()).include(defaultCaveMobs);
      defaultDeepCaveMobs = (new MobSpawnTable()).add(100, (String)"skeleton").add(40, (String)"skeletonthrower").add(45, (String)"deepcavespirit");
      commonFish = (new FishingLootTable()).addFreshWater(100, "carp").startCustom(100).onlySaltWater().onlyBiomes(SnowBiome.class).end("cod").startCustom(100).onlySaltWater().onlyBiomes(ForestBiome.class, PlainsBiome.class).end("herring").startCustom(100).onlySaltWater().onlyBiomes(ForestBiome.class, PlainsBiome.class, SwampBiome.class, DesertBiome.class).end("mackerel").addWater(100, (String)"salmon").startCustom(100).onlyFreshWater().onlyBiomes(ForestBiome.class, PlainsBiome.class, SnowBiome.class).end("trout").startCustom(100).onlySaltWater().onlyBiomes(DesertBiome.class, SwampBiome.class).end("tuna");
      defaultSurfaceFish = (new FishingLootTable()).addAll(commonFish).addWater(100, (String)"gobfish").addWater(100, (String)"halffish").addWater(5, (String)"fins");
      defaultCaveFish = (new FishingLootTable()).addAll(commonFish).addWater(100, (String)"rockfish").addWater(50, (String)"terrorfish");
      defaultSurfaceCritters = (new MobSpawnTable()).add(100, (String)"rabbit").add(80, (String)"squirrel").add(50, (String)"bird").add(20, (String)"bluebird").add(20, (String)"cardinalbird").add(60, (String)"duck");
      defaultCaveCritters = (new MobSpawnTable()).add(100, (String)"spider").add(100, (String)"mouse");
   }
}
