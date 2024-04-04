package necesse.engine.sound;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;

public interface SoundEmitter extends PrimitiveSoundEmitter {
   float getSoundPositionX();

   float getSoundPositionY();

   default float getSoundDistance(float var1, float var2) {
      return (float)(new Point2D.Float(this.getSoundPositionX(), this.getSoundPositionY())).distance((double)var1, (double)var2);
   }

   default Point2D.Float getSoundDirection(float var1, float var2) {
      return GameMath.normalize(this.getSoundPositionX() - var1, this.getSoundPositionY() - var2);
   }
}
