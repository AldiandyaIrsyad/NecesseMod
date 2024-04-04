package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class LightningTrail extends Trail {
   public LightningTrail(TrailVector var1, Level var2, Color var3) {
      super(var1, var2, var3, 250);
      this.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
   }

   public void addNewPoint(float var1, float var2, float var3) {
      TrailPointList.TrailPoint var4 = this.points.get(0);
      Point2D.Float var5 = new Point2D.Float(-var4.vector.dy, var4.vector.dx);
      Point2D.Float var6 = new Point2D.Float(var4.vector.pos.x + var4.vector.dx * var1 - var5.x * var2, var4.vector.pos.y + var4.vector.dy * var1 - var5.y * var2);
      this.addNewPoint(new TrailVector(var6, var4.vector.dx, var4.vector.dy, this.thickness, var3));
   }

   public void addNewPoint(TrailVector var1) {
      this.addPoints(0, new TrailVector[]{var1});
   }

   protected DrawOptions getDrawSection(TrailDrawSection var1, GameCamera var2) {
      Color var3 = this.getColor();
      return var1.getSpriteTrailsDraw(this.sprite, var2, TrailDrawSection.lightColorSetter(this.level, var3));
   }

   protected DrawOptions getDrawNextSection(TrailDrawSection var1, TrailPointList var2, float var3, GameCamera var4) {
      Color var5 = this.getColor();
      return TrailDrawSection.getSpriteTrailsDraw(this.sprite, var2, 0, var2.size() - 1, var4, TrailDrawSection.lightColorSetter(this.level, var5));
   }
}
