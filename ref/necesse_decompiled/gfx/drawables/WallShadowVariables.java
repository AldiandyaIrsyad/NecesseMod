package necesse.gfx.drawables;

import necesse.engine.util.GameMath;

public class WallShadowVariables {
   public float lightLevel;
   public float angle;
   public float range;
   public double dirX;
   public double dirY;
   public float dirXOffset;
   public float dirYOffset;
   public boolean east;
   public boolean south;
   public boolean west;
   public boolean north;
   public float startAlpha;
   public float endAlpha;

   public WallShadowVariables(float var1, float var2, float var3) {
      this.lightLevel = var1;
      this.angle = var2;
      this.range = var3;
   }

   public static WallShadowVariables fromProgress(float var0, float var1, float var2, float var3) {
      float var4 = GameMath.fixAngle(180.0F - var1 * 180.0F);
      float var5 = Math.abs((float)Math.pow((double)(var1 * 2.0F - 1.0F), 4.0) - 1.0F);
      float var6 = var3 - var2;
      float var7 = var3 - var5 * var6;
      return new WallShadowVariables(var0, var4, var7);
   }

   public void calculate() {
      this.dirX = Math.cos(Math.toRadians((double)this.angle));
      this.dirY = Math.sin(Math.toRadians((double)this.angle));
      this.dirXOffset = (float)(this.dirX * (double)this.range);
      this.dirYOffset = (float)(this.dirY * (double)this.range);
      this.east = this.angle < 90.0F || this.angle > 270.0F;
      this.south = this.angle > 0.0F && this.angle < 180.0F;
      this.west = this.angle > 90.0F && this.angle < 270.0F;
      this.north = this.angle > 180.0F && this.angle < 360.0F;
      this.startAlpha = this.lightLevel * 0.3F;
      this.endAlpha = this.lightLevel * 0.3F;
   }
}
