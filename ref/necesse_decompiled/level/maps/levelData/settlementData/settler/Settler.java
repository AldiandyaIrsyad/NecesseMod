package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Color;
import java.awt.Point;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.IDData;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public abstract class Settler {
   public static TreeSet<MoodDescription> moods = new TreeSet(Comparator.comparingInt((var0) -> {
      return var0.minHappiness;
   }));
   public static TreeSet<PopulationThought> populationThoughts = new TreeSet(Comparator.comparingInt((var0) -> {
      return var0.population;
   }));
   public static TreeSet<RoomSize> roomSizes = new TreeSet(Comparator.comparingInt((var0) -> {
      return var0.minSize;
   }));
   public static TreeSet<RoomQuality> roomQualities = new TreeSet(Comparator.comparingInt((var0) -> {
      return var0.minScore;
   }));
   public static TreeSet<FoodQuality> foodQualities = new TreeSet(Comparator.comparingInt((var0) -> {
      return var0.happinessIncrease;
   }));
   public static TreeSet<DietThought> dietThoughts = new TreeSet(Comparator.comparingInt((var0) -> {
      return var0.variety;
   }));
   public static FoodQuality FOOD_SIMPLE = new FoodQuality(new LocalMessage("settlement", "foodsimple"), 10, "A-A-A", new String[]{"simple"});
   public static FoodQuality FOOD_FINE = new FoodQuality(new LocalMessage("settlement", "foodfine"), 20, "B-A-A", new String[]{"fine"});
   public static FoodQuality FOOD_GOURMET = new FoodQuality(new LocalMessage("settlement", "foodgourmet"), 35, "C-A-A", new String[]{"gourmet"});
   public static MobSpawnArea SETTLER_SPAWN_AREA;
   public final IDData idData = new IDData();
   public final String mobStringID;
   public GameTexture texture;

   public static MoodDescription getMood(int var0) {
      Iterator var1 = moods.descendingSet().iterator();

      MoodDescription var2;
      do {
         if (!var1.hasNext()) {
            return (MoodDescription)moods.first();
         }

         var2 = (MoodDescription)var1.next();
      } while(var0 < var2.minHappiness);

      return var2;
   }

   public static PopulationThought getPopulationThough(int var0) {
      Iterator var1 = populationThoughts.descendingSet().iterator();

      PopulationThought var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (PopulationThought)var1.next();
      } while(var0 < var2.population);

      return var2;
   }

   public static RoomSize getRoomSize(int var0) {
      Iterator var1 = roomSizes.descendingSet().iterator();

      RoomSize var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (RoomSize)var1.next();
      } while(var0 < var2.minSize);

      return var2;
   }

   public static RoomQuality getRoomQuality(int var0) {
      Iterator var1 = roomQualities.descendingSet().iterator();

      RoomQuality var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (RoomQuality)var1.next();
      } while(var0 < var2.minScore);

      return var2;
   }

   public static DietThought getDietThought(int var0) {
      Iterator var1 = dietThoughts.descendingSet().iterator();

      DietThought var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (DietThought)var1.next();
      } while(var0 < var2.variety);

      return var2;
   }

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public Settler(String var1) {
      if (SettlerRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct Settler objects when settler registry is closed, since they are a static registered objects. Use SettlerRegistry.getSettler(...) to get settlers.");
      } else {
         this.mobStringID = var1;
      }
   }

   public void onSettlerRegistryClosed() {
      if (this.mobStringID != null) {
         Mob var1 = MobRegistry.getMob(this.mobStringID, (Level)null);
         if (!(var1 instanceof SettlerMob)) {
            throw new IllegalArgumentException(this.mobStringID + " mob does not exist or does not implement SettlerMob interface.");
         }
      }

   }

   public float getArriveAsRecruitAfterDeathChance(SettlementLevelData var1) {
      return 0.9F;
   }

   public boolean canSpawnInSettlement(SettlementLevelData var1, PlayerStats var2) {
      return false;
   }

   public boolean canMoveOut(LevelSettler var1, SettlementLevelData var2) {
      return true;
   }

   public boolean canBanish(LevelSettler var1, SettlementLevelData var2) {
      return true;
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return true;
   }

   public double getSpawnChance(Server var1, ServerClient var2, Level var3) {
      return 0.0;
   }

   public GameMessage getAcquireTip() {
      return null;
   }

   public void spawnAtClient(Server var1, ServerClient var2, Level var3) {
   }

   public boolean isValidBed(SettlementBed var1) {
      return this.canUseBed(var1) == null;
   }

   public GameMessage canUseBed(SettlementBed var1) {
      return null;
   }

   public DrawOptions getSettlerFaceDrawOptions(int var1, int var2, Color var3, Mob var4) {
      return this.getSettlerIcon().initDraw().size(32, 32).color(var3).pos(var1 - 16, var2 - 16);
   }

   public final DrawOptions getSettlerFaceDrawOptions(int var1, int var2, Mob var3) {
      return this.getSettlerFaceDrawOptions(var1, var2, Color.WHITE, var3);
   }

   public DrawOptions getSettlerFlagDrawOptions(int var1, int var2, Mob var3) {
      DrawOptionsList var4 = new DrawOptionsList();
      GameTexture var5 = Settings.UI.settler_house;
      var4.add(var5.initDraw().pos(var1 - var5.getWidth() / 2, var2));
      var4.add(this.getSettlerFaceDrawOptions(var1, var2 + var5.getHeight() / 2, var3));
      return var4;
   }

   public DrawOptions getSettlerFlagDrawOptionsTile(int var1, int var2, GameCamera var3, Mob var4) {
      int var5 = var3.getTileDrawX(var1) + 16;
      int var6 = var3.getTileDrawY(var2);
      return var5 >= -64 && var6 >= -64 && var5 <= var3.getWidth() && var6 <= var3.getHeight() ? this.getSettlerFlagDrawOptions(var5, var6, var4) : () -> {
      };
   }

   public boolean isMouseOverSettlerFlag(int var1, int var2, GameCamera var3) {
      return var1 == var3.getMouseLevelTilePosX() && var2 == var3.getMouseLevelTilePosY();
   }

   public GameTexture getSettlerIcon() {
      return this.texture;
   }

   public final void loadTextures() {
      this.texture = GameTexture.fromFile("settlers/" + this.getStringID());
   }

   public String getGenericMobName() {
      return MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobStringID));
   }

   public void onMoveIn(LevelSettler var1) {
   }

   public SettlerMob getNewSettlerMob(Level var1) {
      SettlerMob var2 = (SettlerMob)MobRegistry.getMob(this.mobStringID, var1);
      Point var3 = getNewSettlerSpawnPos(var2.getMob(), var1);
      if (var3 != null) {
         var2.getMob().setPos((float)var3.x, (float)var3.y, true);
         return var2;
      } else {
         return null;
      }
   }

   public static Point getNewSettlerSpawnPos(Mob var0, Level var1) {
      return SettlementLevelData.findRandomSpawnLevelPos(var1, var0, 100, 3, true);
   }

   protected Supplier<HumanMob> getNewRecruitMob(SettlementLevelData var1) {
      return () -> {
         Mob var2 = MobRegistry.getMob(this.mobStringID, var1.getLevel());
         return var2 instanceof HumanMob ? (HumanMob)var2 : null;
      };
   }

   protected boolean doesSettlementHaveThisSettler(SettlementLevelData var1) {
      return var1.settlers.stream().anyMatch((var1x) -> {
         return var1x.settler == this;
      });
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
   }

   public static void tickServerClientSpawn(Server var0, ServerClient var1) {
      Iterator var2 = SettlerRegistry.getSettlers().iterator();

      while(var2.hasNext()) {
         Settler var3 = (Settler)var2.next();
         if (GameRandom.globalRandom.getChance(var3.getSpawnChance(var0, var1, var1.getLevel()))) {
            var3.spawnAtClient(var0, var1, var1.getLevel());
         }
      }

   }

   protected Point getSpawnLocation(ServerClient var1, Level var2, Mob var3, MobSpawnArea var4, Function<Point, Integer> var5) {
      Point var6 = EntityManager.getMobSpawnTile(var2, var1.playerMob.getX(), var1.playerMob.getY(), var4, var5);
      return var6 != null && !var3.collidesWith(var2, var6.x * 32 + 16, var6.y * 32 + 16) ? new Point(var6.x * 32 + 16, var6.y * 32 + 16) : null;
   }

   protected Point getSpawnLocation(ServerClient var1, Level var2, Mob var3, MobSpawnArea var4) {
      return this.getSpawnLocation(var1, var2, var3, var4, (var1x) -> {
         if (var2.isSolidTile(var1x.x, var1x.y)) {
            return 0;
         } else {
            return var2.isLiquidTile(var1x.x, var1x.y) ? 0 : 100;
         }
      });
   }

   static {
      moods.add(new MoodDescription(new LocalMessage("settlement", "moodveryunhappy"), 0));
      moods.add(new MoodDescription(new LocalMessage("settlement", "moodunhappy"), 25));
      moods.add(new MoodDescription(new LocalMessage("settlement", "moodsomewhathappy"), 50));
      moods.add(new MoodDescription(new LocalMessage("settlement", "moodveryhappy"), 70));
      moods.add(new MoodDescription(new LocalMessage("settlement", "moodextremelyhappy"), 90));
      populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "tinysettlement"), 0, 40));
      populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "smallsettlement"), 6, 30));
      populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "averagesettlement"), 12, 20));
      populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "largesettlement"), 18, 10));
      populationThoughts.add(new PopulationThought(new LocalMessage("settlement", "hugesettlement"), 24, 0));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizebaby"), 0, 0));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizetiny"), 10, 4));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizesmall"), 20, 8));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizemediocre"), 25, 10));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizedecent"), 30, 12));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizelarge"), 40, 15));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizehuge"), 50, 18));
      roomSizes.add(new RoomSize(new LocalMessage("settlement", "sizeenormous"), 60, 20));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomplain"), 0, 0));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomdull"), 1, 4));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomsimple"), 2, 7));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomnormal"), 3, 10));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomgood"), 4, 13));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomimpressive"), 5, 15));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomwonderful"), 6, 17));
      roomQualities.add(new RoomQuality(new LocalMessage("settlement", "roomunrivaled"), 7, 20));
      foodQualities.add(FOOD_SIMPLE);
      foodQualities.add(FOOD_FINE);
      foodQualities.add(FOOD_GOURMET);
      dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietsame"), 0, 0));
      dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietslightly"), 2, 10));
      dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietsomewhat"), 5, 20));
      dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietnicely"), 8, 30));
      dietThoughts.add(new DietThought(new LocalMessage("settlement", "dietextremely"), 12, 40));
      SETTLER_SPAWN_AREA = new MobSpawnArea(800, 1280);
   }
}
