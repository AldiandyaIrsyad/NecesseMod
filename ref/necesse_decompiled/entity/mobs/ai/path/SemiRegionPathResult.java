package necesse.entity.mobs.ai.path;

public enum SemiRegionPathResult {
   INVALID,
   VALID,
   CHECK_EACH_TILE;

   private SemiRegionPathResult() {
   }

   // $FF: synthetic method
   private static SemiRegionPathResult[] $values() {
      return new SemiRegionPathResult[]{INVALID, VALID, CHECK_EACH_TILE};
   }
}
