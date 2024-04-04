package necesse.entity.mobs.ai.behaviourTree;

public enum AINodeResult {
   SUCCESS,
   FAILURE,
   RUNNING;

   private AINodeResult() {
   }

   // $FF: synthetic method
   private static AINodeResult[] $values() {
      return new AINodeResult[]{SUCCESS, FAILURE, RUNNING};
   }
}
