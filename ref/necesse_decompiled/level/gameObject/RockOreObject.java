package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;

public class RockOreObject extends RockObject {
   protected String oreMaskTextureName;
   protected String oreTextureName;
   public RockObject parentRock;
   public GameTexture[] oreTextures;
   public long oreHash;
   public String droppedOre;
   public int droppedOreMin;
   public int droppedOreMax;
   protected final GameRandom oreTextureRandom;

   public RockOreObject(RockObject var1, String var2, String var3, Color var4, String var5, int var6, int var7, boolean var8) {
      super("rock", var1.mapColor, var1.droppedStone);
      this.parentRock = var1;
      this.toolTier = var1.toolTier;
      this.isOre = true;
      this.oreMaskTextureName = var2;
      this.oreTextureName = var3;
      this.mapColor = var4;
      this.oreHash = (long)var4.hashCode();
      this.droppedOre = var5;
      this.droppedOreMin = var6;
      this.droppedOreMax = var7;
      this.oreTextureRandom = new GameRandom();
      this.isIncursionExtractionObject = var8;
      this.displayMapTooltip = true;
   }

   public RockOreObject(RockObject var1, String var2, String var3, Color var4, String var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, true);
   }

   public RockOreObject(RockObject var1, String var2, String var3, Color var4, String var5) {
      this(var1, var2, var3, var4, var5, 1, 3);
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", this.oreTextureName);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      LootTable var4 = super.getLootTable(var1, var2, var3);
      if (this.droppedOre != null) {
         var4.items.add(LootItem.between(this.droppedOre, this.droppedOreMin, this.droppedOreMax).splitItems(5));
      }

      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      int var9;
      synchronized(this.oreTextureRandom) {
         var9 = this.oreTextureRandom.seeded(this.getTileSeed(var4, var5) * 8629L).nextInt(this.oreTextures.length);
      }

      this.addRockDrawables(var1, var3, var4, var5, this.parentRock.rockTexture, this.oreTextures[var9], this.oreHash, var6, var7, var8);
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("objects/" + this.oreTextureName);
      this.oreTextures = new GameTexture[var1.getWidth() / 32];

      for(int var2 = 0; var2 < this.oreTextures.length; ++var2) {
         GameTexture var3 = new GameTexture(GameTexture.fromFile("objects/" + this.oreMaskTextureName));

         for(int var4 = 0; var4 < var3.getWidth(); var4 += 32) {
            for(int var5 = 0; var5 < var3.getHeight(); var5 += 32) {
               var3.merge(var1, var4, var5, var2 * 32, 0, 32, 32, (var0, var1x) -> {
                  float var2 = (float)var0.getRed() / 255.0F;
                  float var3 = (float)var0.getGreen() / 255.0F;
                  float var4 = (float)var0.getBlue() / 255.0F;
                  float var5 = (float)var0.getAlpha() / 255.0F;
                  float var6 = (float)var1x.getRed() / 255.0F;
                  float var7 = (float)var1x.getGreen() / 255.0F;
                  float var8 = (float)var1x.getBlue() / 255.0F;
                  float var9 = (float)var1x.getAlpha() / 255.0F;
                  return new Color(var2 * var6, var3 * var7, var4 * var8, var5 * var9);
               });
            }
         }

         var3.makeFinal();
         this.oreTextures[var2] = var3;
      }

   }
}
