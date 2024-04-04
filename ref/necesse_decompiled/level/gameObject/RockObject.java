package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.GameClock;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class RockObject extends GameObject {
   protected String rockTextureName;
   String droppedStone;
   protected int minStoneAmount;
   protected int maxStoneAmount;
   protected GameTexture rockTexture;
   private final GameRandom oreTextureRandom;

   public RockObject(String var1, Color var2, String var3, int var4, int var5) {
      super(new Rectangle(0, 0, 32, 32));
      this.rockTextureName = var1;
      this.mapColor = var2;
      this.droppedStone = var3;
      this.regionType = SemiRegion.RegionType.WALL;
      this.minStoneAmount = var4;
      this.maxStoneAmount = var5;
      this.oreTextureRandom = new GameRandom();
      this.isRock = true;
   }

   public RockObject(String var1, Color var2, String var3) {
      this(var1, var2, var3, 3, 5);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return this.droppedStone != null ? new LootTable(new LootItemInterface[]{LootItem.between(this.droppedStone, this.minStoneAmount, this.maxStoneAmount).splitItems(5)}) : new LootTable();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      this.addRockDrawables(var1, var3, var4, var5, this.rockTexture, (GameTexture)null, 0L, var6, var7, var8);
   }

   public void loadTextures() {
      super.loadTextures();
      this.rockTexture = this.getNewRockTexture();
   }

   protected GameTexture getNewRockTexture() {
      GameTexture var1 = new GameTexture(GameTexture.fromFile("objects/" + this.rockTextureName));
      return var1;
   }

   public final void addRockDrawables(List<LevelSortedDrawable> var1, Level var2, int var3, int var4, GameTexture var5, GameTexture var6, long var7, TickManager var9, GameCamera var10, PlayerMob var11) {
      Performance.record(var9, "rockSetup", (Runnable)(() -> {
         GameObject[] var11x = var2.getAdjacentObjects(var3, var4);
         boolean[] var12 = new boolean[var11x.length];

         for(int var13 = 0; var13 < var12.length; ++var13) {
            var12[var13] = var11x[var13].isRock;
         }

         GameLight var32 = var2.getLightLevel(var3, var4);
         GameLight var14 = var32;
         if (this.isIncursionExtractionObject && var2.isIncursionLevel) {
            float var15 = GameUtils.getAnimFloatContinuous(Math.abs(var2.getTime() + 2500L * var7), 2500);
            var14 = var32.minLevelCopy((float)GameMath.lerp(var15, 80, 100));
         }

         boolean var33 = var11 != null && (Boolean)var11.buffManager.getModifier(BuffModifiers.SPELUNKER);
         float var16 = 1.0F;
         if (var11 != null && !Settings.hideUI) {
            Rectangle var17 = new Rectangle(var3 * 32 - 16, var4 * 32 - 32, 64, 48);
            if (var11.getCollision().intersects(var17)) {
               var16 = 0.5F;
            } else if (var17.contains(var10.getX() + Screen.mousePos().sceneX, var10.getY() + Screen.mousePos().sceneY)) {
               var16 = 0.5F;
            }
         }

         int var18 = var5.getWidth() / 32;
         int var34;
         synchronized(this.oreTextureRandom) {
            this.oreTextureRandom.setSeed(this.getTileSeed(var3, var4) * 4621L);
            var34 = this.oreTextureRandom.nextInt(var18) * 2;
         }

         int var19 = var10.getTileDrawX(var3);
         int var20 = var10.getTileDrawY(var4);
         byte var21 = 0;
         byte var22 = 1;
         byte var23 = 2;
         byte var24 = 3;
         byte var25 = 4;
         byte var26 = 5;
         byte var27 = 6;
         byte var28 = 7;
         final SharedTextureDrawOptions var29 = new SharedTextureDrawOptions(var5);
         final SharedTextureDrawOptions var30 = var6 == null ? null : new SharedTextureDrawOptions(var6);
         if (!var12[var22]) {
            if (var12[var24]) {
               addRockDraw(var29, var34, 5, var32, var16, var19, var20 - 16);
               addOreDraw(var30, var34, 5, var14, var33, var7, var2, var16, var19, var20 - 16);
            } else {
               addRockDraw(var29, var34, 0, var32, var16, var19, var20 - 16);
               addOreDraw(var30, var34, 0, var14, var33, var7, var2, var16, var19, var20 - 16);
            }

            if (var12[var25]) {
               addRockDraw(var29, var34 + 1, 5, var32, var16, var19 + 16, var20 - 16);
               addOreDraw(var30, var34 + 1, 5, var14, var33, var7, var2, var16, var19 + 16, var20 - 16);
            } else {
               addRockDraw(var29, var34 + 1, 0, var32, var16, var19 + 16, var20 - 16);
               addOreDraw(var30, var34 + 1, 0, var14, var33, var7, var2, var16, var19 + 16, var20 - 16);
            }
         } else if (var6 != null && !var11x[var22].isOre) {
            if (!var11x[var24].isOre && !var11x[var21].isOre) {
               addOreDraw(var30, var34, 0, var14, var33, var7, var2, var19, var20 - 16);
            } else {
               addOreDraw(var30, var34, 5, var14, var33, var7, var2, var19, var20 - 16);
            }

            if (!var11x[var25].isOre && !var11x[var23].isOre) {
               addOreDraw(var30, var34 + 1, 0, var14, var33, var7, var2, var19 + 16, var20 - 16);
            } else {
               addOreDraw(var30, var34 + 1, 5, var14, var33, var7, var2, var19 + 16, var20 - 16);
            }
         }

         if (var12[var27]) {
            if (var12[var24]) {
               if (var12[var26]) {
                  addRockDraw(var29, var34, 7, var32, var19, var20);
                  addRockDraw(var29, var34, 6, var32, var19, var20 + 16);
                  addOreDraw(var30, var34, 7, var14, var33, var7, var2, var19, var20);
                  addOreDraw(var30, var34, 6, var14, var33, var7, var2, var19, var20 + 16);
               } else {
                  addRockDraw(var29, var34, 10, var32, var19, var20);
                  addRockDraw(var29, var34, 11, var32, var19, var20 + 16);
                  addOreDraw(var30, var34, 10, var14, var33, var7, var2, var19, var20);
                  addOreDraw(var30, var34, 11, var14, var33, var7, var2, var19, var20 + 16);
               }
            } else if (var12[var26]) {
               addRockDraw(var29, var34, 2, var32, var19, var20);
               addRockDraw(var29, var34, 12, var32, var19, var20 + 16);
               addOreDraw(var30, var34, 2, var14, var33, var7, var2, var19, var20);
               addOreDraw(var30, var34, 12, var14, var33, var7, var2, var19, var20 + 16);
            } else {
               addRockDraw(var29, var34, 2, var32, var19, var20);
               addRockDraw(var29, var34, 1, var32, var19, var20 + 16);
               addOreDraw(var30, var34, 2, var14, var33, var7, var2, var19, var20);
               addOreDraw(var30, var34, 1, var14, var33, var7, var2, var19, var20 + 16);
            }

            if (var12[var25]) {
               if (var12[var28]) {
                  addRockDraw(var29, var34 + 1, 7, var32, var19 + 16, var20);
                  addRockDraw(var29, var34 + 1, 6, var32, var19 + 16, var20 + 16);
                  addOreDraw(var30, var34 + 1, 7, var14, var33, var7, var2, var19 + 16, var20);
                  addOreDraw(var30, var34 + 1, 6, var14, var33, var7, var2, var19 + 16, var20 + 16);
               } else {
                  addRockDraw(var29, var34 + 1, 10, var32, var19 + 16, var20);
                  addRockDraw(var29, var34 + 1, 11, var32, var19 + 16, var20 + 16);
                  addOreDraw(var30, var34 + 1, 10, var14, var33, var7, var2, var19 + 16, var20);
                  addOreDraw(var30, var34 + 1, 11, var14, var33, var7, var2, var19 + 16, var20 + 16);
               }
            } else if (var12[var28]) {
               addRockDraw(var29, var34 + 1, 2, var32, var19 + 16, var20);
               addRockDraw(var29, var34 + 1, 12, var32, var19 + 16, var20 + 16);
               addOreDraw(var30, var34 + 1, 2, var14, var33, var7, var2, var19 + 16, var20);
               addOreDraw(var30, var34 + 1, 12, var14, var33, var7, var2, var19 + 16, var20 + 16);
            } else {
               addRockDraw(var29, var34 + 1, 2, var32, var19 + 16, var20);
               addRockDraw(var29, var34 + 1, 1, var32, var19 + 16, var20 + 16);
               addOreDraw(var30, var34 + 1, 2, var14, var33, var7, var2, var19 + 16, var20);
               addOreDraw(var30, var34 + 1, 1, var14, var33, var7, var2, var19 + 16, var20 + 16);
            }
         } else {
            if (var12[var24]) {
               addRockDraw(var29, var34, 8, var32, var19, var20);
               addRockDraw(var29, var34, 9, var32, var19, var20 + 16);
               addOreDraw(var30, var34, 8, var14, var33, var7, var2, var19, var20);
               addOreDraw(var30, var34, 9, var14, var33, var7, var2, var19, var20 + 16);
            } else {
               addRockDraw(var29, var34, 3, var32, var19, var20);
               addRockDraw(var29, var34, 4, var32, var19, var20 + 16);
               addOreDraw(var30, var34, 3, var14, var33, var7, var2, var19, var20);
               addOreDraw(var30, var34, 4, var14, var33, var7, var2, var19, var20 + 16);
            }

            if (var12[var25]) {
               addRockDraw(var29, var34 + 1, 8, var32, var19 + 16, var20);
               addRockDraw(var29, var34 + 1, 9, var32, var19 + 16, var20 + 16);
               addOreDraw(var30, var34 + 1, 8, var14, var33, var7, var2, var19 + 16, var20);
               addOreDraw(var30, var34 + 1, 9, var14, var33, var7, var2, var19 + 16, var20 + 16);
            } else {
               addRockDraw(var29, var34 + 1, 3, var32, var19 + 16, var20);
               addRockDraw(var29, var34 + 1, 4, var32, var19 + 16, var20 + 16);
               addOreDraw(var30, var34 + 1, 3, var14, var33, var7, var2, var19 + 16, var20);
               addOreDraw(var30, var34 + 1, 4, var14, var33, var7, var2, var19 + 16, var20 + 16);
            }
         }

         var1.add(new LevelSortedDrawable(this, var3, var4) {
            public int getSortY() {
               return 16;
            }

            public void draw(TickManager var1) {
               Performance.record(var1, "rockDraw", (Runnable)(() -> {
                  var29.draw();
                  if (var30 != null) {
                     var30.draw();
                  }

               }));
            }
         });
      }));
   }

   private static void addRockDraw(SharedTextureDrawOptions var0, int var1, int var2, GameLight var3, float var4, int var5, int var6) {
      var0.addSprite(var1, var2, 16).light(var3).alpha(var4).pos(var5, var6);
   }

   private static void addRockDraw(SharedTextureDrawOptions var0, int var1, int var2, GameLight var3, int var4, int var5) {
      addRockDraw(var0, var1, var2, var3, 1.0F, var4, var5);
   }

   private static void addOreDraw(SharedTextureDrawOptions var0, int var1, int var2, GameLight var3, boolean var4, long var5, GameClock var7, float var8, int var9, int var10) {
      if (var0 != null) {
         var0.addSprite(var1, var2, 16).spelunkerLight(var3, var4, var5, var7).alpha(var8).pos(var9, var10);
      }

   }

   private static void addOreDraw(SharedTextureDrawOptions var0, int var1, int var2, GameLight var3, boolean var4, long var5, GameClock var7, int var8, int var9) {
      addOreDraw(var0, var1, var2, var3, var4, var5, var7, 1.0F, var8, var9);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return var1.getObject(var2, var3 - 1).isRock ? new Rectangle(var2 * 32, var3 * 32, 32, 32) : new Rectangle(var2 * 32, var3 * 32 + 4, 32, 28);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }
}
