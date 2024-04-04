package necesse.level.gameObject;

public interface ForestrySaplingObject {
   String getForestryResultObjectStringID();

   default boolean shouldForestryPlantAtTile(int var1, int var2) {
      return var1 % 2 == 0 && var2 % 2 == 0;
   }
}
