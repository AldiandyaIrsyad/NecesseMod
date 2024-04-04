package necesse.entity.mobs.job.activeJob;

public enum ActiveJobHitResult {
   CONTINUE,
   CLEAR_THIS,
   CLEAR_SEQUENCE,
   MOVE_TO;

   private ActiveJobHitResult() {
   }

   // $FF: synthetic method
   private static ActiveJobHitResult[] $values() {
      return new ActiveJobHitResult[]{CONTINUE, CLEAR_THIS, CLEAR_SEQUENCE, MOVE_TO};
   }
}
