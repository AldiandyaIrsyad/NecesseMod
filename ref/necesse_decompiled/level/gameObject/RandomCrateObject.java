package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.seasons.SeasonCrate;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.gfx.GameResources;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class RandomCrateObject extends RandomBreakObject {
   public RandomCrateObject(String var1) {
      super(new Rectangle(5, 12, 22, 12), var1, new Color(112, 89, 52));
   }

   public LootTable getBreakLootTable(Level var1, int var2, int var3) {
      LootTable var4 = var1.getCrateLootTable();
      if (this.useEventTexture(new GameRandom(), var2, var3)) {
         var4 = new LootTable(new LootItemInterface[]{var4, var4});
      }

      return var4;
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound((GameSound)GameRandom.globalRandom.getOneOf((Object[])(GameResources.cratebreak1, GameResources.cratebreak2, GameResources.cratebreak3)), SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   protected SeasonCrate getSeasonCrate(GameRandom var1, int var2, int var3) {
      return GameSeasons.getCrate(var1.seeded(this.getTileSeed(var2, var3, 38813)));
   }

   public boolean useEventTexture(GameRandom var1, int var2, int var3) {
      return this.getSeasonCrate(var1, var2, var3) != null;
   }

   public GameTextureSection getSprite(GameRandom var1, int var2, int var3) {
      SeasonCrate var4 = this.getSeasonCrate(var1, var2, var3);
      GameTexture var5 = var4 != null ? var4.getTexture() : this.objectTexture;
      int var6 = var5.getWidth() / 32;
      int var7 = this.getSprite(var1, var2, var3, var6);
      return (new GameTextureSection(var5)).sprite(var7, 0, 32, var5.getHeight());
   }

   public GameTextureSection[] getDebrisSprites(GameRandom var1, int var2, int var3) {
      SeasonCrate var4 = this.getSeasonCrate(var1, var2, var3);
      GameTexture var5 = var4 != null ? var4.getDebrisTexture() : this.debrisTexture;
      int var6 = var5.getWidth() / 32;
      GameTextureSection[] var7 = new GameTextureSection[var6];

      for(int var8 = 0; var8 < var6; ++var8) {
         int var9 = var8 * 32;
         var7[var8] = new GameTextureSection(var5, var9, var9 + 32, 0, 32);
      }

      return var7;
   }
}
