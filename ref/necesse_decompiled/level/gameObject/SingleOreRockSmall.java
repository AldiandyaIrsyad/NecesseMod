package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SingleOreRockSmall extends GameObject {
   public String droppedStone;
   public long oreHash;
   public String rockTextureName;
   public String oreTextureName;
   public GameTexture rockTexture;
   public GameTexture oreTexture;
   public String droppedOre;
   public int droppedOreMin;
   public int droppedOreMax;
   protected final GameRandom drawRandom;

   public SingleOreRockSmall(String var1, int var2, String var3, String var4, Color var5, String var6, int var7, int var8, boolean var9) {
      super(new Rectangle(2, 10, 28, 20));
      this.droppedStone = var1;
      this.toolTier = var2;
      this.rockTextureName = var3;
      this.oreTextureName = var4;
      this.mapColor = var5;
      this.droppedOre = var6;
      this.droppedOreMin = var7;
      this.droppedOreMax = var8;
      this.isIncursionExtractionObject = var9;
      this.oreHash = (long)var5.hashCode();
      this.isOre = true;
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
      this.canPlaceOnLiquid = true;
   }

   public SingleOreRockSmall(String var1, int var2, String var3, String var4, Color var5, String var6, int var7, int var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", this.droppedOre);
   }

   public void loadTextures() {
      super.loadTextures();
      this.rockTexture = GameTexture.fromFile("objects/" + this.rockTextureName);
      this.oreTexture = GameTexture.fromFile("objects/" + this.oreTextureName);
   }

   public int getRandomYOffset(int var1, int var2) {
      synchronized(this.drawRandom) {
         return (int)((this.drawRandom.seeded(this.getTileSeed(var1, var2, 1)).nextFloat() * 2.0F - 1.0F) * 8.0F) - 4;
      }
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      Rectangle var5 = super.getCollision(var1, var2, var3, var4);
      var5.y += this.getRandomYOffset(var2, var3);
      return var5;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      GameLight var10 = var9;
      if (this.isIncursionExtractionObject && var3.isIncursionLevel) {
         float var11 = GameUtils.getAnimFloatContinuous(Math.abs(var3.getTime() + 2500L * this.oreHash), 2500);
         var10 = var9.minLevelCopy((float)GameMath.lerp(var11, 80, 100));
      }

      boolean var20 = var8 != null && (Boolean)var8.buffManager.getModifier(BuffModifiers.SPELUNKER);
      int var12 = var7.getTileDrawX(var4);
      int var13 = var7.getTileDrawY(var5);
      final int var16 = this.getRandomYOffset(var4, var5);
      int var14;
      boolean var15;
      synchronized(this.drawRandom) {
         var14 = this.drawRandom.seeded(this.getTileSeed(var4, var5)).nextInt(this.rockTexture.getWidth() / 32);
         var15 = this.drawRandom.nextBoolean();
      }

      var13 += var16;
      final DrawOptionsList var17 = new DrawOptionsList();
      var17.add(this.rockTexture.initDraw().sprite(var14, 0, 32, this.rockTexture.getHeight()).mirror(var15, false).light(var9).pos(var12, var13 - this.rockTexture.getHeight() + 32));
      var17.add(this.oreTexture.initDraw().sprite(var14, 0, 32, this.oreTexture.getHeight()).mirror(var15, false).spelunkerLight(var10, var20, this.oreHash, var3).pos(var12, var13 - this.oreTexture.getHeight() + 32));
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16 + var16;
         }

         public void draw(TickManager var1) {
            var17.draw();
         }
      });
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = new LootTable(new LootItemInterface[]{LootItem.between(this.droppedOre, this.droppedOreMin, this.droppedOreMax).splitItems(5)});
      if (this.droppedStone != null) {
         var4.items.add(LootItem.between(this.droppedStone, 15, 25).splitItems(5));
      }

      return var4;
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return !var1.isShore(var2, var3);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }
}
