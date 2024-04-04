package necesse.level.maps.biomes.temple;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldGenerator;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.furniturePresets.BedDresserPreset;
import necesse.level.maps.presets.furniturePresets.BenchPreset;
import necesse.level.maps.presets.furniturePresets.BookshelfClockPreset;
import necesse.level.maps.presets.furniturePresets.BookshelvesPreset;
import necesse.level.maps.presets.furniturePresets.CabinetsPreset;
import necesse.level.maps.presets.furniturePresets.DeskBookshelfPreset;
import necesse.level.maps.presets.furniturePresets.DinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.DisplayStandClockPreset;
import necesse.level.maps.presets.furniturePresets.ModularDinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.ModularTablesPreset;
import necesse.level.maps.presets.set.FurnitureSet;

public class TempleLevel extends Level {
   public static int PADDING_TILES = 50;
   public static int CORRIDOR_MIN_WIDTH = 9;
   public static int CORRIDOR_MAX_WIDTH = 11;
   public static int LOOT_ROOM_CORRIDOR_MIN_WIDTH = 6;
   public static int LOOT_ROOM_CORRIDOR_MAX_WIDTH = 8;
   public static int LOOT_ROOM_MIN_SIZE = 15;
   public static int LOOT_ROOM_MAX_SIZE = 20;
   public static ArrayList<TempleLayout> layouts = new ArrayList();

   public static Level generateNew(int var0, int var1, int var2, WorldEntity var3) {
      TempleNode var4 = generateLayout(var0, var1);
      Rectangle var5 = getSize(var4);
      GameRandom var6 = new GameRandom(WorldGenerator.getSeed(var0, var1));
      TempleLevel var7 = new TempleLevel(var5.width + PADDING_TILES * 2, var5.height + PADDING_TILES * 2, var0, var1, var2, var3);
      var7.generateTemple(var4, var6, -var5.x + PADDING_TILES, -var5.y + PADDING_TILES);
      return var7;
   }

   public TempleLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   private TempleLevel(int var1, int var2, int var3, int var4, int var5, WorldEntity var6) {
      super(new LevelIdentifier(var3, var4, var5), var1, var2, var6);
      this.isCave = true;
   }

   public GameMessage getSetSpawnError(int var1, int var2, ServerClient var3) {
      return new LocalMessage("misc", "spawndungeon");
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.desertDeepCrate;
   }

   protected void generateTemple(TempleNode var1, GameRandom var2, int var3, int var4) {
      var1 = var1.first;
      int var5 = TileRegistry.getTileID("sandbrick");
      int var6 = TileRegistry.getTileID("woodfloor");
      int var7 = ObjectRegistry.getObjectID("deepsandstonewall");

      int var8;
      for(var8 = 0; var8 < this.width; ++var8) {
         for(int var9 = 0; var9 < this.height; ++var9) {
            if (var2.getChance(0.75F)) {
               this.setTile(var8, var9, var5);
            } else {
               this.setTile(var8, var9, var6);
            }

            this.setObject(var8, var9, var7);
         }
      }

      var8 = var2.getIntBetween(CORRIDOR_MIN_WIDTH, CORRIDOR_MAX_WIDTH);
      LinesGeneration var20 = new LinesGeneration(var1.tileX + var3, var1.tileY + var4, (float)var8);

      for(LinesGeneration var10 = var20; var1.next != null; var1 = var1.next) {
         TempleNode var11 = var1.next;
         var8 = GameMath.limit(var2.getIntOffset(var8, 1), CORRIDOR_MIN_WIDTH, CORRIDOR_MAX_WIDTH);
         var10 = var10.addLineTo(var11.tileX + var3, var11.tileY + var4, (float)var8);
         Iterator var12 = var11.lootRooms.iterator();

         while(var12.hasNext()) {
            Point var13 = (Point)var12.next();
            int var14 = GameMath.limit(var2.getIntOffset(var8, 1), LOOT_ROOM_CORRIDOR_MIN_WIDTH, LOOT_ROOM_CORRIDOR_MAX_WIDTH);
            var10.addLineTo(var13.x + var3, var13.y + var4, (float)var14);
         }
      }

      CellAutomaton var21 = var20.doCellularAutomaton(var2);
      var21.cleanHardEdges();
      var1 = var1.first;
      addAliveRoom(var21, var1.tileX + var3, var1.tileY + var4, 10);
      RoomLocation var22 = new RoomLocation(var1.tileX + var3, var1.tileY + var4, 10);

      LinkedList var23;
      for(var23 = new LinkedList(); var1.next != null; var1 = var1.next) {
         TempleNode var24 = var1.next;
         Iterator var15 = var24.lootRooms.iterator();

         while(var15.hasNext()) {
            Point var16 = (Point)var15.next();
            int var17 = var2.getIntBetween(LOOT_ROOM_MIN_SIZE, LOOT_ROOM_MAX_SIZE) / 2;
            addAliveRoom(var21, var16.x + var3, var16.y + var4, var17);
            var23.add(new RoomLocation(var16.x + var3, var16.y + var4, var17));
         }
      }

      addAliveRoom(var21, var1.tileX + var3, var1.tileY + var4, 10);
      RoomLocation var25 = new RoomLocation(var1.tileX + var3, var1.tileY + var4, 10);
      var21.forEachTile(this, (var0, var1x, var2x) -> {
         var0.setObject(var1x, var2x, 0);
      });
      var21.placeEdgeWalls(this, var7, true);
      var21.forEachTile(this, (var1x, var2x, var3x) -> {
         if (var2.getChance(0.02F)) {
            GameObject var4 = ObjectRegistry.getObject((String)var2.getOneOf((Object[])("crate", "vase")));
            if (var4.canPlace(var1x, var2x, var3x, 0) == null) {
               var4.placeObject(var1x, var2x, var3x, 0);
            }
         }

      });
      TicketSystemList var26 = new TicketSystemList();
      var26.addObject(100, new BedDresserPreset(FurnitureSet.palm, 2));
      var26.addObject(100, new BenchPreset(FurnitureSet.palm, 2));
      var26.addObject(100, new BookshelfClockPreset(FurnitureSet.palm, 2));
      var26.addObject(100, new BookshelvesPreset(FurnitureSet.palm, 2, 3));
      var26.addObject(100, new CabinetsPreset(FurnitureSet.palm, 2, 3));
      var26.addObject(100, new DeskBookshelfPreset(FurnitureSet.palm, 2));
      var26.addObject(100, new DinnerTablePreset(FurnitureSet.palm, 2));
      var26.addObject(100, new DisplayStandClockPreset(FurnitureSet.palm, 2, var2, (LootTable)null, new Object[0]));
      var26.addObject(100, new ModularDinnerTablePreset(FurnitureSet.palm, 2, 1));
      var26.addObject(100, new ModularTablesPreset(FurnitureSet.palm, 2, 2, true));
      var21.placeFurniturePresets(var26, 0.4F, this, var2);
      generateColumns(this, var2, var22.x, var22.y, 4, var22.radius - var22.radius / 3, FurnitureSet.palm.candelabra);
      generateColumns(this, var2, var25.x, var25.y, 4, var25.radius - var25.radius / 3, FurnitureSet.palm.candelabra);
      ObjectRegistry.getObject("templeentrance").placeObject(this, var25.x - 1, var25.y, 0);
      int var27 = ObjectRegistry.getObjectID("deepsandstonecolumn");
      AtomicInteger var28 = new AtomicInteger();
      Iterator var18 = var23.iterator();

      while(var18.hasNext()) {
         RoomLocation var19 = (RoomLocation)var18.next();
         generateColumns(this, var2, var19.x, var19.y, var2.getIntBetween(3, 5), var19.radius - var19.radius / 3, var27);
         this.setObject(var19.x, var19.y, FurnitureSet.palm.chest, 2);
         LootTablePresets.templeChest.applyToLevel(var2, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, var19.x, var19.y, this, var28);
      }

   }

   protected static void addAliveRoom(CellAutomaton var0, int var1, int var2, int var3) {
      for(int var4 = var1 - var3; var4 <= var1 + var3; ++var4) {
         for(int var5 = var2 - var3; var5 <= var2 + var3; ++var5) {
            if ((new Point(var1, var2)).distance((double)var4, (double)var5) <= (double)((float)var3 + 0.5F)) {
               var0.setAlive(var4, var5);
            }
         }
      }

   }

   protected static void generateColumns(Level var0, GameRandom var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = 360 / var4;
      int var8 = var1.nextInt(360);

      for(int var9 = 0; var9 < var4; ++var9) {
         var8 += var1.getIntOffset(var7, var7 / 5);
         Point2D.Float var10 = GameMath.getAngleDir((float)var8);
         var0.setObject(var2 + (int)(var10.x * (float)var5), var3 + (int)(var10.y * (float)var5), var6);
      }

   }

   protected static TempleNode generateLayout(int var0, int var1) {
      GameRandom var2 = new GameRandom(WorldGenerator.getSeed(var0, var1));
      TempleLayout var3 = (TempleLayout)var2.getOneOf((List)layouts);
      TempleNode var4 = new TempleNode(0, 0);
      var3.generate(var4, var2);
      if (var2.nextBoolean()) {
         var4 = var4.mirrorX();
      }

      if (var2.nextBoolean()) {
         var4 = var4.mirrorY();
      }

      PresetRotation var5 = (PresetRotation)var2.getOneOf((Object[])(PresetRotation.CLOCKWISE, PresetRotation.HALF_180, PresetRotation.ANTI_CLOCKWISE, null));
      if (var5 != null) {
         var4 = var4.rotate(var5);
      }

      return var4;
   }

   public static Point getEntranceSpawnPos(int var0, int var1) {
      TempleNode var2 = generateLayout(var0, var1);
      Rectangle var3 = getSize(var2);
      return new Point(PADDING_TILES - var3.x, PADDING_TILES - var3.y);
   }

   protected static Rectangle getSize(TempleNode var0) {
      var0 = var0.first;
      int var1 = Integer.MAX_VALUE;
      int var2 = Integer.MAX_VALUE;
      int var3 = Integer.MIN_VALUE;

      int var4;
      for(var4 = Integer.MIN_VALUE; var0 != null; var0 = var0.next) {
         if (var1 > var0.tileX) {
            var1 = var0.tileX;
         }

         if (var2 > var0.tileY) {
            var2 = var0.tileY;
         }

         if (var3 < var0.tileX) {
            var3 = var0.tileX;
         }

         if (var4 < var0.tileY) {
            var4 = var0.tileY;
         }

         Iterator var5 = var0.lootRooms.iterator();

         while(var5.hasNext()) {
            Point var6 = (Point)var5.next();
            if (var1 > var6.x) {
               var1 = var6.x;
            }

            if (var2 > var6.y) {
               var2 = var6.y;
            }

            if (var3 < var6.x) {
               var3 = var6.x;
            }

            if (var4 < var6.y) {
               var4 = var6.y;
            }
         }
      }

      return new Rectangle(var1, var2, var3 - var1 + 1, var4 - var2 + 1);
   }

   static {
      layouts.add((var0, var1) -> {
         byte var2 = 50;
         byte var3 = 60;
         byte var4 = 20;
         byte var5 = 30;
         var0.nextAngle(var1, 70.0F, 110.0F, (float)var2, (float)var3).lootRoomAngle(var1, 150.0F, 210.0F, (float)var4, (float)var5).nextAngle(var1, -20.0F, 20.0F, (float)var2, (float)var3).lootRoomAngle(var1, 250.0F, 380.0F, (float)var4, (float)var5).nextAngle(var1, 70.0F, 110.0F, (float)var2, (float)var3).lootRoomAngle(var1, -20.0F, 90.0F, (float)var4, (float)var5).nextAngle(var1, 170.0F, 190.0F, (float)var2, (float)var3).lootRoomAngle(var1, 250.0F, 290.0F, (float)(var4 * 2) / 3.0F, (float)(var5 * 2) / 3.0F).nextAngle(var1, 170.0F, 190.0F, (float)var2, (float)var3).lootRoomAngle(var1, 170.0F, 280.0F, (float)var4, (float)var5).nextAngle(var1, 70.0F, 110.0F, (float)var2, (float)var3).lootRoomAngle(var1, 70.0F, 190.0F, (float)var4, (float)var5).nextAngle(var1, -20.0F, 20.0F, (float)var2, (float)var3).nextAngle(var1, -10.0F, 120.0F, (float)var4, (float)var5);
      });
   }

   public static class TempleNode {
      protected final int tileX;
      protected final int tileY;
      protected LinkedList<Point> lootRooms = new LinkedList();
      protected TempleNode first;
      protected TempleNode prev;
      protected TempleNode next;

      protected TempleNode(int var1, int var2) {
         this.tileX = var1;
         this.tileY = var2;
         this.first = this;
      }

      protected TempleNode mirrorX() {
         TempleNode var1 = null;

         for(TempleNode var2 = this.first; var2 != null; var2 = var2.next) {
            TempleNode var3 = new TempleNode(-var2.tileX, var2.tileY);
            if (var1 != null) {
               var1.next = var3;
               var3.first = var1.first;
            }

            var3.prev = var1;
            Iterator var4 = var2.lootRooms.iterator();

            while(var4.hasNext()) {
               Point var5 = (Point)var4.next();
               var3.lootRooms.add(new Point(-var5.x, var5.y));
            }

            var1 = var3;
         }

         return var1 == null ? null : var1.first;
      }

      protected TempleNode mirrorY() {
         TempleNode var1 = null;

         for(TempleNode var2 = this.first; var2 != null; var2 = var2.next) {
            TempleNode var3 = new TempleNode(var2.tileX, -var2.tileY);
            if (var1 != null) {
               var1.next = var3;
               var3.first = var1.first;
            }

            var3.prev = var1;
            Iterator var4 = var2.lootRooms.iterator();

            while(var4.hasNext()) {
               Point var5 = (Point)var4.next();
               var3.lootRooms.add(new Point(var5.x, -var5.y));
            }

            var1 = var3;
         }

         return var1 == null ? null : var1.first;
      }

      protected TempleNode rotate(int var1) {
         return this.rotate(PresetRotation.toRotationAngle(var1));
      }

      protected TempleNode rotate(PresetRotation var1) {
         if (var1 == null) {
            return this.first;
         } else {
            TempleNode var2 = null;

            for(TempleNode var3 = this.first; var3 != null; var3 = var3.next) {
               Point var4 = PresetUtils.getRotatedPoint(var3.tileX, var3.tileY, 0, 0, var1);
               TempleNode var5 = new TempleNode(var4.x, var4.y);
               if (var2 != null) {
                  var2.next = var5;
                  var5.first = var2.first;
               }

               var5.prev = var2;
               Iterator var6 = var3.lootRooms.iterator();

               while(var6.hasNext()) {
                  Point var7 = (Point)var6.next();
                  Point var8 = PresetUtils.getRotatedPoint(var7.x, var7.y, 0, 0, var1);
                  var5.lootRooms.add(new Point(var8.x, var8.y));
               }

               var2 = var5;
            }

            return var2 == null ? null : var2.first;
         }
      }

      public TempleNode lootRoom(int var1, int var2) {
         this.lootRooms.add(new Point(this.tileX + var1, this.tileY + var2));
         return this;
      }

      public TempleNode lootRoomAngle(float var1, float var2) {
         Point2D.Float var3 = GameMath.getAngleDir(var1);
         return this.lootRoom((int)(var3.x * var2), (int)(var3.y * var2));
      }

      public TempleNode lootRoomAngle(GameRandom var1, float var2, float var3, float var4, float var5) {
         return this.lootRoomAngle(var1.getFloatBetween(var2, var3), var1.getFloatBetween(var4, var5));
      }

      public TempleNode next(int var1, int var2) {
         this.next = new TempleNode(this.tileX + var1, this.tileY + var2);
         this.next.prev = this;
         this.next.first = this.first;
         return this.next;
      }

      public TempleNode nextAngle(float var1, float var2) {
         Point2D.Float var3 = GameMath.getAngleDir(var1);
         return this.next((int)(var3.x * var2), (int)(var3.y * var2));
      }

      public TempleNode nextAngle(GameRandom var1, float var2, float var3, float var4, float var5) {
         return this.nextAngle(var1.getFloatBetween(var2, var3), var1.getFloatBetween(var4, var5));
      }
   }

   protected static class RoomLocation extends Point {
      public int radius;

      public RoomLocation(int var1, int var2, int var3) {
         super(var1, var2);
         this.radius = var3;
      }
   }

   @FunctionalInterface
   public interface TempleLayout {
      void generate(TempleNode var1, GameRandom var2);
   }
}
