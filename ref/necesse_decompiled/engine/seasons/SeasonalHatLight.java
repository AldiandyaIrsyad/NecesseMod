package necesse.engine.seasons;

import java.util.function.Supplier;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;

public class SeasonalHatLight extends SeasonalHat {
   public GameTexture lightTexture;

   public SeasonalHatLight(Supplier<Boolean> var1, float var2, String var3, float var4, String var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected void loadTextures() {
      super.loadTextures();
      this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
   }

   public HumanDrawOptions.HumanDrawOptionsGetter getDrawOptions() {
      HumanDrawOptions.HumanDrawOptionsGetter var1 = super.getDrawOptions();
      return (var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15) -> {
         DrawOptionsList var16 = new DrawOptionsList();
         var16.add(var1.getDrawOptions(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15));
         var16.add(this.lightTexture.initDraw().sprite(var4, var5, var6).light(var13.minLevelCopy(150.0F)).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8));
         return var16;
      };
   }
}
