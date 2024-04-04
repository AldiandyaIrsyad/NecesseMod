package necesse.gfx.drawOptions.texture;

import java.awt.Color;
import java.awt.Dimension;
import necesse.engine.world.GameClock;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.ShaderState;
import necesse.level.maps.light.GameLight;

public class TextureDrawOptionsEnd extends TextureDrawOptions {
   TextureDrawOptionsEnd(TextureDrawOptions var1) {
      super(var1);
   }

   public TextureDrawOptionsEnd copy() {
      return new TextureDrawOptionsEnd(super.copy());
   }

   public TextureDrawOptionsEnd glRotate(float var1, int var2, int var3) {
      this.opts.rotation = var1;
      this.opts.rotTranslateX = var2;
      this.opts.rotTranslateY = var3;
      this.opts.useRotation = true;
      return this;
   }

   public TextureDrawOptionsEnd addShaderState(ShaderState var1) {
      this.opts.shaderStates.add(var1);
      return this;
   }

   public TextureDrawOptionsEnd addShaderTexture(ShaderTexture var1) {
      this.opts.shaderTextures.add(var1);
      return this;
   }

   public TextureDrawOptionsEnd addShaderTextureFit(GameTexture var1, int var2) {
      if (var1 == null) {
         return this;
      } else {
         float var3 = Math.abs(this.opts.spriteX1 - this.opts.spriteX2) * (float)this.texture.getWidth();
         float var4 = Math.abs(this.opts.spriteY2 - this.opts.spriteY3) * (float)this.texture.getHeight();
         float var5 = var3 / (float)var1.getWidth();
         float var6 = var4 / (float)var1.getHeight();
         return this.addShaderTexture(new ShaderTexture(var2, var1, 0.0F, var5, 0.0F, var6));
      }
   }

   public TextureDrawOptionsEnd addShaderTexture(GameTexture var1, int var2) {
      return var1 == null ? this : this.addShaderTexture(new ShaderTexture(var2, var1));
   }

   public TextureDrawOptionsEnd addShaderTexture(GameSprite var1, int var2) {
      return var1 == null ? this : this.addShaderTexture(new ShaderTexture(var2, var1));
   }

   public TextureDrawOptionsEnd rotate(float var1, int var2, int var3) {
      super.rotate(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd rotate(float var1) {
      super.rotate(var1);
      return this;
   }

   public TextureDrawOptionsEnd addRotation(float var1, int var2, int var3) {
      super.addRotation(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd rotateTexture(int var1, int var2, int var3) {
      super.rotateTexture(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd rotateTexture(int var1) {
      super.rotateTexture(var1);
      return this;
   }

   public TextureDrawOptionsEnd size(int var1, int var2) {
      super.size(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd size(Dimension var1) {
      super.size(var1);
      return this;
   }

   public TextureDrawOptionsEnd shrinkWidth(int var1, boolean var2) {
      super.shrinkWidth(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd shrinkHeight(int var1, boolean var2) {
      super.shrinkHeight(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd size(int var1, boolean var2) {
      super.size(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd size(int var1) {
      super.size(var1);
      return this;
   }

   public TextureDrawOptionsEnd color(float var1, float var2, float var3, float var4) {
      super.color(var1, var2, var3, var4);
      return this;
   }

   public TextureDrawOptionsEnd color(float var1) {
      super.color(var1);
      return this;
   }

   public TextureDrawOptionsEnd brightness(float var1) {
      super.brightness(var1);
      return this;
   }

   public TextureDrawOptionsEnd color(float var1, float var2, float var3) {
      super.color(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd color(Color var1, boolean var2) {
      super.color(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd color(Color var1) {
      super.color(var1);
      return this;
   }

   public TextureDrawOptionsEnd alpha(float var1) {
      super.alpha(var1);
      return this;
   }

   public TextureDrawOptionsEnd light(GameLight var1) {
      super.light(var1);
      return this;
   }

   public TextureDrawOptionsEnd colorLight(float var1, float var2, float var3, GameLight var4) {
      super.colorLight(var1, var2, var3, var4);
      return this;
   }

   public TextureDrawOptionsEnd colorLight(float var1, float var2, float var3, float var4, GameLight var5) {
      super.colorLight(var1, var2, var3, var4, var5);
      return this;
   }

   public TextureDrawOptionsEnd colorLight(Color var1, GameLight var2) {
      super.colorLight(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd colorLight(Color var1, boolean var2, GameLight var3) {
      super.colorLight(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd colorMult(Color var1) {
      super.colorMult(var1);
      return this;
   }

   public TextureDrawOptionsEnd spelunkerColorLight(float var1, float var2, float var3, float var4, GameLight var5, boolean var6, long var7, GameClock var9, long var10, float var12, int var13) {
      super.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var7, var9, var10, var12, var13);
      return this;
   }

   public TextureDrawOptionsEnd spelunkerColorLight(float var1, float var2, float var3, GameLight var4, boolean var5, long var6, GameClock var8, long var9, float var11, int var12) {
      super.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var8, var9, var11, var12);
      return this;
   }

   public TextureDrawOptionsEnd spelunkerColorLight(Color var1, boolean var2, GameLight var3, boolean var4, long var5, GameClock var7, long var8, float var10, int var11) {
      super.spelunkerColorLight(var1, var2, var3, var4, var5, var7, var8, var10, var11);
      return this;
   }

   public TextureDrawOptionsEnd spelunkerColorLight(Color var1, GameLight var2, boolean var3, long var4, GameClock var6, long var7, float var9, int var10) {
      super.spelunkerColorLight(var1, var2, var3, var4, var6, var7, var9, var10);
      return this;
   }

   public TextureDrawOptionsEnd spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5, long var6, float var8, int var9) {
      super.spelunkerLight(var1, var2, var3, var5, var6, var8, var9);
      return this;
   }

   public TextureDrawOptionsEnd spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5) {
      super.spelunkerLight(var1, var2, var3, var5);
      return this;
   }

   public TextureDrawOptionsEnd advColor(float[] var1) {
      super.advColor(var1);
      return this;
   }

   public TextureDrawOptionsEnd translatePos(int var1, int var2) {
      super.translatePos(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd mirrorX() {
      super.mirrorX();
      return this;
   }

   public TextureDrawOptionsEnd mirrorY() {
      super.mirrorY();
      return this;
   }

   public TextureDrawOptionsEnd mirror(boolean var1, boolean var2) {
      super.mirror(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd depth(float var1) {
      super.depth(var1);
      return this;
   }

   public TextureDrawOptionsEnd blendFunc(int var1, int var2, int var3, int var4) {
      super.blendFunc(var1, var2, var3, var4);
      return this;
   }

   public TextureDrawOptionsEnd pos(int var1, int var2) {
      super.pos(var1, var2);
      return this;
   }

   public TextureDrawOptionsEnd pos(int var1, int var2, boolean var3) {
      super.pos(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd posMiddle(int var1, int var2, boolean var3) {
      super.posMiddle(var1, var2, var3);
      return this;
   }

   public TextureDrawOptionsEnd posMiddle(int var1, int var2) {
      super.posMiddle(var1, var2);
      return this;
   }

   public void draw(int var1, int var2) {
      this.pos(var1, var2);
      this.draw();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptions copy() {
      return this.copy();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods posMiddle(int var1, int var2) {
      return this.posMiddle(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods posMiddle(int var1, int var2, boolean var3) {
      return this.posMiddle(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods pos(int var1, int var2, boolean var3) {
      return this.pos(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods pos(int var1, int var2) {
      return this.pos(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods blendFunc(int var1, int var2, int var3, int var4) {
      return this.blendFunc(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods depth(float var1) {
      return this.depth(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods mirror(boolean var1, boolean var2) {
      return this.mirror(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods mirrorY() {
      return this.mirrorY();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods mirrorX() {
      return this.mirrorX();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods translatePos(int var1, int var2) {
      return this.translatePos(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods advColor(float[] var1) {
      return this.advColor(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5) {
      return this.spelunkerLight(var1, var2, var3, var5);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5, long var6, float var8, int var9) {
      return this.spelunkerLight(var1, var2, var3, var5, var6, var8, var9);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods spelunkerColorLight(Color var1, GameLight var2, boolean var3, long var4, GameClock var6, long var7, float var9, int var10) {
      return this.spelunkerColorLight(var1, var2, var3, var4, var6, var7, var9, var10);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods spelunkerColorLight(Color var1, boolean var2, GameLight var3, boolean var4, long var5, GameClock var7, long var8, float var10, int var11) {
      return this.spelunkerColorLight(var1, var2, var3, var4, var5, var7, var8, var10, var11);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods spelunkerColorLight(float var1, float var2, float var3, GameLight var4, boolean var5, long var6, GameClock var8, long var9, float var11, int var12) {
      return this.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var8, var9, var11, var12);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods spelunkerColorLight(float var1, float var2, float var3, float var4, GameLight var5, boolean var6, long var7, GameClock var9, long var10, float var12, int var13) {
      return this.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var7, var9, var10, var12, var13);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods colorMult(Color var1) {
      return this.colorMult(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods colorLight(Color var1, boolean var2, GameLight var3) {
      return this.colorLight(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods colorLight(Color var1, GameLight var2) {
      return this.colorLight(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods colorLight(float var1, float var2, float var3, float var4, GameLight var5) {
      return this.colorLight(var1, var2, var3, var4, var5);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods colorLight(float var1, float var2, float var3, GameLight var4) {
      return this.colorLight(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods light(GameLight var1) {
      return this.light(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods alpha(float var1) {
      return this.alpha(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods color(Color var1) {
      return this.color(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods color(Color var1, boolean var2) {
      return this.color(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods color(float var1, float var2, float var3) {
      return this.color(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods brightness(float var1) {
      return this.brightness(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods color(float var1) {
      return this.color(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods color(float var1, float var2, float var3, float var4) {
      return this.color(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods size(int var1) {
      return this.size(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods size(int var1, boolean var2) {
      return this.size(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods shrinkHeight(int var1, boolean var2) {
      return this.shrinkHeight(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods shrinkWidth(int var1, boolean var2) {
      return this.shrinkWidth(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods size(Dimension var1) {
      return this.size(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods size(int var1, int var2) {
      return this.size(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods rotateTexture(int var1) {
      return this.rotateTexture(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods rotateTexture(int var1, int var2, int var3) {
      return this.rotateTexture(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods addRotation(float var1, int var2, int var3) {
      return this.addRotation(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods rotate(float var1) {
      return this.rotate(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TextureDrawOptionsMods rotate(float var1, int var2, int var3) {
      return this.rotate(var1, var2, var3);
   }
}
