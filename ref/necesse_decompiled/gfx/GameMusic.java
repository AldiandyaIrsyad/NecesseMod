package necesse.gfx;

import java.awt.Color;
import necesse.engine.PlayingMusicManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public class GameMusic {
   public final IDData idData;
   public final String filePath;
   public GameMessage fromName;
   public GameMessage trackName;
   public float volumeModifier;
   public GameSound sound;
   public Color color1;
   public Color color2;
   public int fadeInMillis;
   public int fadeOutMillis;

   public final int getID() {
      return this.idData.getID();
   }

   public String getStringID() {
      return this.idData.getStringID();
   }

   public GameMusic(String var1, GameMessage var2, GameMessage var3, Color var4, Color var5, int var6, int var7) {
      this.idData = new IDData();
      this.volumeModifier = 1.0F;
      this.filePath = var1;
      this.fromName = var2;
      this.trackName = var3;
      this.color1 = var4;
      this.color2 = var5;
      this.fadeInMillis = var6;
      this.fadeOutMillis = var7;
   }

   public GameMusic(String var1, GameMessage var2, GameMessage var3, Color var4, Color var5) {
      this(var1, var2, var3, var4, var5, PlayingMusicManager.DEFAULT_FADE_IN_TIME, PlayingMusicManager.DEFAULT_FADE_OUT_TIME);
   }

   public GameTexture loadVinylTexture() {
      GameTexture var1 = new GameTexture(GameTexture.fromFile("items/vinylbase", true));
      GameTexture var2 = new GameTexture(GameTexture.fromFile("items/vinylcolor1", true));
      GameTexture var3 = new GameTexture(GameTexture.fromFile("items/vinylcolor2", true));
      var2.applyColor(this.color1, MergeFunction.GLBLEND);
      var3.applyColor(this.color2, MergeFunction.GLBLEND);
      var1.merge(var2, 0, 0, MergeFunction.NORMAL);
      var1.merge(var3, 0, 0, MergeFunction.NORMAL);
      var1.makeFinal();
      var2.makeFinal();
      var3.makeFinal();
      return var1;
   }

   public void loadSound() {
      this.sound = GameSound.fromFileMusic(this.filePath);
      if (this.sound != null) {
         this.sound.setVolumeModifier(this.volumeModifier);
      }

   }

   public GameMusic setVolumeModifier(float var1) {
      this.volumeModifier = var1;
      if (this.sound != null) {
         this.sound.setVolumeModifier(var1);
      }

      return this;
   }
}
