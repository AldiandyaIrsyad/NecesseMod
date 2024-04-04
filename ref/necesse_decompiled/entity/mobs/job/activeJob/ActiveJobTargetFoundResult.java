package necesse.entity.mobs.job.activeJob;

public enum ActiveJobTargetFoundResult {
   CONTINUE,
   FAIL;

   private ActiveJobTargetFoundResult() {
   }

   // $FF: synthetic method
   private static ActiveJobTargetFoundResult[] $values() {
      return new ActiveJobTargetFoundResult[]{CONTINUE, FAIL};
   }
}
