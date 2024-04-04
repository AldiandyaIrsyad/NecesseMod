package necesse.engine.seasons;

import java.util.function.Supplier;
import necesse.gfx.gameTexture.GameTexture;

public class SeasonCrate {
   public Supplier<Boolean> isActive;
   public float crateChance;
   protected String textureName;
   public GameTexture texture;
   public GameTexture debrisTexture;

   public SeasonCrate(Supplier<Boolean> var1, float var2, String var3) {
      this.isActive = var1;
      this.crateChance = var2;
      this.textureName = var3;
   }

   protected void loadTextures() {
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
      this.debrisTexture = GameTexture.fromFile("objects/" + this.textureName + "debris");
   }

   public GameTexture getTexture() {
      return this.texture;
   }

   public GameTexture getDebrisTexture() {
      return this.debrisTexture;
   }
}
