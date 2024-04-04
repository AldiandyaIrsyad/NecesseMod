package necesse.engine.sound;

import java.awt.geom.Point2D;

public interface PrimitiveSoundEmitter {
   float getSoundDistance(float var1, float var2);

   Point2D.Float getSoundDirection(float var1, float var2);
}
