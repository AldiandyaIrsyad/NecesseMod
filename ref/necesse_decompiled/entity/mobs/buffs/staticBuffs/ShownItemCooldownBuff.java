package necesse.entity.mobs.buffs.staticBuffs;

import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public class ShownItemCooldownBuff extends ShownCooldownBuff {
   String texturePath;

   public ShownItemCooldownBuff(int var1, boolean var2, String var3) {
      super(var1, var2);
      this.texturePath = var3;
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("buffs/mask", true);
      GameTexture var2 = GameTexture.fromFile("buffs/negative", true);
      GameTexture var3 = GameTexture.fromFile(this.texturePath, true);
      GameTexture var4;
      int var5;
      int var6;
      int var7;
      if (var3.getWidth() != var3.getHeight()) {
         var5 = Math.max(var3.getWidth(), var3.getHeight());
         var4 = new GameTexture("buffs/cooldown " + this.getStringID(), var5, var5);
         var6 = (var5 - var3.getWidth()) / 2;
         var7 = (var5 - var3.getHeight()) / 2;
         var4.copy(var3, var6, var7);
         var4 = var4.resize(var2.getWidth() - 6, var2.getHeight() - 6);
      } else {
         var4 = var3.resize(var2.getWidth() - 6, var2.getHeight() - 6);
      }

      this.iconTexture = new GameTexture(var2);
      var5 = (var4.getWidth() - var1.getWidth()) / 2;
      var6 = (var4.getHeight() - var1.getHeight()) / 2;
      var4.merge(var1, var5, var6, MergeFunction.GLBLEND);
      var7 = (this.iconTexture.getWidth() - var4.getWidth()) / 2;
      int var8 = (this.iconTexture.getHeight() - var4.getHeight()) / 2;
      this.iconTexture.merge(var4, var7, var8, MergeFunction.NORMAL);
      this.iconTexture.makeFinal();
   }
}
