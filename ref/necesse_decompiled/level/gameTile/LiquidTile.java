package necesse.level.gameTile;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.FishingPositionLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;

public abstract class LiquidTile extends GameTile {
   public static Attacker LAVA_ATTACKER = new Attacker() {
      public GameMessage getAttackerName() {
         return new LocalMessage("deaths", "lavaname");
      }

      public DeathMessageTable getDeathMessages() {
         return this.getDeathMessages("fire", 3);
      }

      public Mob getFirstAttackOwner() {
         return null;
      }
   };
   private Color[] liquidColors;
   public Color liquidColor;
   private final GameRandom bobbingRandom;

   public LiquidTile(Color var1) {
      super(false);
      this.canBeMined = false;
      this.liquidColor = var1;
      this.mapColor = var1;
      this.bobbingRandom = new GameRandom();
      this.stackSize = 50;
   }

   public GameTexture generateItemTexture() {
      GameTexture var1 = GameTexture.fromFile("tiles/bucket", true);
      GameTexture var2 = new GameTexture(var1, 0, 0, 32, 32);
      GameTexture var3 = new GameTexture(var1, 0, 32, 32, 32);
      var3.applyColor(this.liquidColor, MergeFunction.GLBLEND);
      var2.merge(var3, 0, 0, MergeFunction.NORMAL);
      var2.makeFinal();
      return var2;
   }

   protected void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("tiles/liquidcolors", true);
      this.liquidColors = new Color[var1.getWidth()];

      for(int var2 = 0; var2 < this.liquidColors.length; ++var2) {
         this.liquidColors[var2] = var1.getColor(var2, 0);
      }

   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      return Collections.singletonList(new FishingPositionLevelJob(var2, var3));
   }

   protected Color getLiquidColor(int var1) {
      return var1 >= this.liquidColors.length ? this.liquidColor : this.liquidColors[var1];
   }

   public abstract Color getLiquidColor(Level var1, int var2, int var3);

   public double getPathCost(Level var1, int var2, int var3, Mob var4) {
      return 1.0 / (double)var4.getSwimSpeed();
   }

   public float getItemSinkingRate(float var1) {
      return TickManager.getTickDelta(10.0F);
   }

   public float getItemMaxSinking() {
      return 0.25F;
   }

   public final void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      Performance.record(var7, "liquidSetup", (Runnable)(() -> {
         int var8 = var6.getTileDrawX(var4);
         int var9 = var6.getTileDrawY(var5);
         var1.add(tileBlankTexture).size(32, 32).color(this.getLiquidColor(var3, var4, var5)).pos(var8, var9);
         if (var5 != 0) {
            GameTile var10 = var3.getTile(var4, var5 - 1);
            if (!var10.isLiquid) {
               var10.addBridgeDrawables(var1, var2, var3, var4, var5, var6, var7);
            }
         }

         this.addLiquidTopDrawables(var1, var2, var3, var4, var5, var6, var7);
      }));
   }

   protected abstract void addLiquidTopDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7);

   public GameTextureSection getShoreTexture() {
      return tileShoreTexture;
   }

   public void drawPreview(Level var1, int var2, int var3, float var4, PlayerMob var5, GameCamera var6) {
      int var7 = var6.getTileDrawX(var2);
      int var8 = var6.getTileDrawY(var3);
      Screen.initQuadDraw(32, 32).color(this.getLiquidColor(var1, var2, var3)).alpha(var4).draw(var7, var8);
   }

   public String canPlace(Level var1, int var2, int var3) {
      String var4 = super.canPlace(var1, var2, var3);
      if (var4 != null) {
         return var4;
      } else {
         return Arrays.stream(var1.getAdjacentObjects(var2, var3)).anyMatch((var0) -> {
            return var0.isWall || var0.isRock;
         }) ? "objectadjacent" : null;
      }
   }

   public void placeTile(Level var1, int var2, int var3) {
      super.placeTile(var1, var2, var3);
      if (var1.isLoadingComplete()) {
         for(int var4 = -1; var4 <= 1; ++var4) {
            int var5 = var2 + var4;

            for(int var6 = -1; var6 <= 1; ++var6) {
               int var7 = var3 + var6;
               var1.getTile(var5, var7).tickValid(var1, var5, var7, false);
            }
         }
      }

   }

   public int getLiquidBobbing(Level var1, int var2, int var3) {
      synchronized(this.bobbingRandom) {
         int var5 = (int)((var1.getWorldEntity().getTime() + (long)Math.abs(this.bobbingRandom.seeded(this.getTileSeed(var2, var3)).nextInt())) % 1600L) / 200;
         if (var5 > 4) {
            var5 = 4 - var5 % 4;
         }

         return var5;
      }
   }
}
