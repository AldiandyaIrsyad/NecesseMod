package necesse.level.maps.light;

import java.awt.Point;
import java.util.List;

public interface LightMapInterface {
   void resetLights(LightManager var1);

   GameLight getLight(int var1, int var2);

   List<SourcedGameLight> getLightSources(int var1, int var2);

   void update(int var1, int var2, boolean var3);

   void update(int var1, int var2, int var3, int var4, boolean var5);

   void update(Iterable<Point> var1, boolean var2);
}
