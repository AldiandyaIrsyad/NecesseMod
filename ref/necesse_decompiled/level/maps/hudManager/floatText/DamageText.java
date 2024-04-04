package necesse.level.maps.hudManager.floatText;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.gfx.gameFont.FontOptions;

public class DamageText extends FloatTextFade {
   public DamageText(int var1, int var2, int var3, FontOptions var4, int var5) {
      super(var1 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), var2 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), String.valueOf(var3), var4);
      this.heightIncrease = var5;
   }

   public DamageText(int var1, int var2, int var3, Color var4, int var5) {
      this(var1, var2, var3, (new FontOptions(16)).outline().color(var4), var5);
   }

   public DamageText(Mob var1, int var2, FontOptions var3, int var4) {
      this(var1.getX(), var1.getY() - 16, var2, var3, var4);
   }

   public DamageText(Mob var1, int var2, Color var3, int var4) {
      this(var1, var2, (new FontOptions(16)).outline().color(var3), var4);
   }
}
