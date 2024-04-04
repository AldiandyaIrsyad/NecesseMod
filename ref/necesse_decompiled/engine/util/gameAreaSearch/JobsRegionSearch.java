package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.GameLinkedList;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.regionSystem.RegionPosition;

public class JobsRegionSearch extends GameRegionSearch<Iterable<GameLinkedList<LevelJob>.Element>> {
   private final JobsLevelData jobsData;

   public JobsRegionSearch(Level var1, int var2, int var3, int var4) {
      super(var1, var1.regionManager.getRegionXByTile(var2), var1.regionManager.getRegionYByTile(var3), Math.max(var1.regionManager.getRegionsWidth(), var1.regionManager.getRegionsHeight()) + 1);
      this.jobsData = JobsLevelData.getJobsLevelData(var1);
      RegionPosition var5 = var1.regionManager.getRegionPosByTile(var2 - var4, var3 - var4);
      RegionPosition var6 = var1.regionManager.getRegionPosByTile(var2 + var4, var3 + var4);
      this.shrinkLimit(var5.regionX, var5.regionY, var6.regionX, var6.regionY);
   }

   protected Iterable<GameLinkedList<LevelJob>.Element> get(int var1, int var2) {
      return this.jobsData.getJobsInRegion(var1, var2).elements();
   }

   public GameAreaStream<GameLinkedList<LevelJob>.Element> streamEach() {
      return this.stream().flatMap((var0) -> {
         return var0;
      });
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object get(int var1, int var2) {
      return this.get(var1, var2);
   }
}
