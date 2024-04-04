package necesse.entity.mobs.job.activeJob;

public enum ActiveJobResult {
   FINISHED,
   FAILED,
   PERFORMING,
   MOVE_TO;

   private ActiveJobResult() {
   }

   // $FF: synthetic method
   private static ActiveJobResult[] $values() {
      return new ActiveJobResult[]{FINISHED, FAILED, PERFORMING, MOVE_TO};
   }
}
