package necesse.entity.mobs;

import necesse.gfx.gameTexture.GameTexture;

public class HumanTextureFull extends HumanTexture {
   public final GameTexture head;
   public final GameTexture hair;
   public final GameTexture backHair;
   public final GameTexture feet;

   public HumanTextureFull(GameTexture var1, GameTexture var2, GameTexture var3, GameTexture var4, GameTexture var5, GameTexture var6, GameTexture var7) {
      super(var4, var5, var6);
      this.head = var1;
      this.hair = var2;
      this.backHair = var3;
      this.feet = var7;
   }
}
