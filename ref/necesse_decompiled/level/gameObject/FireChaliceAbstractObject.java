package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

abstract class FireChaliceAbstractObject extends GameObject {
   protected String textureName;
   protected GameTexture texture;
   protected final GameRandom drawRandom;

   public FireChaliceAbstractObject(String var1, Color var2) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.mapColor = var2;
      this.lightLevel = 200;
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.roomProperties.add("lights");
      this.drawRandom = new GameRandom();
      this.displayMapTooltip = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   protected abstract void setCounterIDs(int var1, int var2, int var3, int var4);

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.isActive(var1, var2, var3) ? this.lightLevel : 0;
   }

   public boolean isActive(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      return this.getMultiTile(var4).streamIDs(var2, var3).noneMatch((var1x) -> {
         return var1.wireManager.isWireActiveAny(var1x.tileX, var1x.tileY);
      });
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      byte var6 = var1.getObjectRotation(var2, var3);
      Rectangle var7 = this.getMultiTile(var6).getTileRectangle(var2, var3);
      var1.lightManager.updateStaticLight(var7.x, var7.y, var7.x + var7.width - 1, var7.y + var7.height - 1, true);
   }
}
