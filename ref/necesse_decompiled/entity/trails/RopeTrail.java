package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.entity.Entity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class RopeTrail extends Trail {
   private Entity e1;
   private Entity e2;

   public RopeTrail(Level var1, Entity var2, Entity var3, float var4, float var5, Color var6) {
      super(new TrailVector(var2.x, var2.y, var2.x - var3.x, var2.y - var3.y, 20.0F, 0.0F), var1, var6, 5000);
      this.e1 = var2;
      this.e2 = var3;
      this.sprite = new GameSprite(GameResources.chains, 5, 0, 32);
      this.update(var4, var5);
   }

   public RopeTrail(Level var1, Entity var2, Entity var3, float var4, float var5) {
      this(var1, var2, var3, var4, var5, new Color(76, 60, 47));
   }

   public synchronized void update(float var1, float var2) {
      int var3 = this.e1.getDrawX();
      int var4 = this.e1.getDrawY();
      int var5 = this.e2.getDrawX();
      int var6 = this.e2.getDrawY();
      this.reset(new TrailVector((float)var3, (float)var4, (float)(var3 - var5), (float)(var4 - var6), this.thickness, var1));
      float var7 = (float)(new Point2D.Float((float)var3, (float)var4)).distance((double)var5, (double)var6);
      float var8 = (float)(var3 - var5) / 2.0F + (float)var5;
      float var9 = (float)(var4 - var6) / 2.0F + (float)var6;
      float var10 = (var1 - var2) / 2.0F + var2 - var7 / 10.0F;
      this.addPoints(3, new TrailVector[]{new TrailVector(var8, var9, (float)var3 - var8, (float)var4 - var9, this.thickness, var10), new TrailVector((float)var5, (float)var6, var8 - (float)var5, var9 - (float)var6, this.thickness, var2)});
   }

   protected synchronized DrawOptions getDrawSection(TrailDrawSection var1, GameCamera var2) {
      return var1.getSpriteTrailsDraw(this.sprite, var2, TrailDrawSection.lightColorSetter(this.level, this.col));
   }

   protected synchronized DrawOptions getDrawNextSection(TrailDrawSection var1, TrailPointList var2, float var3, GameCamera var4) {
      return TrailDrawSection.getSpriteTrailsDraw(this.sprite, var2, 0, var2.size() - 1, var4, TrailDrawSection.lightColorSetter(this.level, this.col));
   }
}
