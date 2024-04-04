package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameTile.SnowTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.light.GameLight;

public class SnowPileObject extends GameObject {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("snowball", 2, 5)});
   public GameTexture texture;
   public int number;
   public int nextPile;
   protected final GameRandom drawRandom;
   protected final GameRandom drawRandom1;
   protected final GameRandom drawRandom2;

   public SnowPileObject(int var1, int var2) {
      super(new Rectangle(0, 0));
      this.number = var1;
      this.nextPile = var2;
      this.mapColor = new Color(215, 215, 215);
      this.drawDamage = false;
      this.isGrass = true;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
      this.drawRandom1 = new GameRandom();
      this.drawRandom2 = new GameRandom();
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/snowpile");
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", "snowpile");
   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer() && this.nextPile != -1 && var1.biome instanceof SnowBiome && var1.rainingLayer.isRaining() && GameRandom.globalRandom.getChance(SnowTile.snowChance)) {
         GameObject var4 = ObjectRegistry.getObject(this.nextPile);
         var4.placeObject(var1, var2, var3, 0);
         var1.sendObjectUpdatePacket(var2, var3);
      }

   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return lootTable;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      Performance.record(var6, "snowPileSetup", (Runnable)(() -> {
         double var6;
         synchronized(this.drawRandom) {
            var6 = this.drawRandom.seeded(this.getTileSeed(var4, var5)).nextGaussian();
         }

         final int var8 = 3 + (int)(var6 * 2.0);
         final int var9 = 15 + (int)(var6 * 2.0);
         GameLight var10 = var3.getLightLevel(var4, var5);
         int var11 = var7.getTileDrawX(var4);
         int var12 = var7.getTileDrawY(var5);
         int var13 = var12 - 22 + (int)(var6 * 2.0);
         int var14;
         int var15;
         boolean var16;
         synchronized(this.drawRandom1) {
            var14 = var11 + (int)(this.drawRandom1.seeded(this.getTileSeed(var4, var5, 9000)).nextGaussian() * 4.0);
            var15 = this.drawRandom1.nextInt(4);
            var16 = this.drawRandom1.nextBoolean();
         }

         final TextureDrawOptionsEnd var17 = this.texture.initDraw().sprite(this.number, var15, 32).light(var10).mirror(var16, false).pos(var14, var13);
         int var18 = var12 - 10 + (int)(var6 * 2.0);
         int var19;
         int var20;
         boolean var21;
         synchronized(this.drawRandom2) {
            var19 = var11 + (int)(this.drawRandom2.seeded(this.getTileSeed(var4, var5, 8000)).nextGaussian() * 4.0);
            var20 = this.drawRandom2.nextInt(4);
            var21 = this.drawRandom2.nextBoolean();
         }

         final TextureDrawOptionsEnd var22 = this.texture.initDraw().sprite(this.number, var20, 32).light(var10).mirror(var21, false).pos(var19, var18);
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return var8;
            }

            public void draw(TickManager var1) {
               TextureDrawOptions var10002 = var17;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "snowPileDraw", (Runnable)(var10002::draw));
            }
         });
         var1.add(new LevelSortedDrawable(this, var4, var5) {
            public int getSortY() {
               return var9;
            }

            public void draw(TickManager var1) {
               TextureDrawOptions var10002 = var22;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "snowPileDraw", (Runnable)(var10002::draw));
            }
         });
      }));
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         Integer[] var6 = var1.getAdjacentObjectsInt(var2, var3);
         int var7 = 0;
         Integer[] var8 = var6;
         int var9 = var6.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            int var11 = var8[var10];
            if (var11 != 0) {
               ++var7;
            }

            if (var7 > 2) {
               return "snowspace";
            }
         }

         return null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.snowID;
   }
}
