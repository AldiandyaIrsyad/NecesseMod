package necesse.entity.mobs.ai.behaviourTree.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import necesse.engine.DisposableExecutorService;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.level.maps.Level;

public class FutureAITask<T> {
   private final AtomicReference<State> state;
   private final DisposableExecutorService executor;
   private final Callable<T> task;
   private final Function<T, AINodeResult> handler;
   private long completeTime;
   private Exception exception;
   private T result;

   public FutureAITask(DisposableExecutorService var1, Callable<T> var2, Function<T, AINodeResult> var3) {
      this.state = new AtomicReference(FutureAITask.State.Initial);
      this.completeTime = -1L;
      this.executor = var1;
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      this.task = var2;
      this.handler = var3;
   }

   public FutureAITask(Level var1, Callable<T> var2, Function<T, AINodeResult> var3) {
      this(var1.executor(), var2, var3);
   }

   public synchronized void runConcurrently() {
      if (this.state.get() != FutureAITask.State.Initial) {
         throw new IllegalStateException("Cannot run task twice");
      } else if (this.executor == null) {
         throw new IllegalStateException("Executor not supplied");
      } else if (!this.executor.isDisposed()) {
         this.state.set(FutureAITask.State.Processing);
         this.executor.submit(() -> {
            long var1 = System.nanoTime();

            try {
               this.result = this.task.call();
            } catch (Exception var6) {
               this.exception = var6;
            }

            this.completeTime = System.nanoTime() - var1;
            this.state.set(FutureAITask.State.Complete);
            synchronized(this) {
               this.notifyAll();
            }
         });
      }
   }

   public synchronized void runNow() {
      if (this.state.get() != FutureAITask.State.Initial) {
         throw new IllegalStateException("Cannot run task twice");
      } else {
         this.state.set(FutureAITask.State.Processing);

         try {
            this.result = this.task.call();
         } catch (Exception var2) {
            this.exception = var2;
         }

         this.state.set(FutureAITask.State.Complete);
      }
   }

   public synchronized boolean isComplete() {
      return this.state.get() == FutureAITask.State.Complete;
   }

   public synchronized boolean isStarted() {
      return this.state.get() != FutureAITask.State.Initial;
   }

   public synchronized AINodeResult runComplete() throws Exception {
      if (!this.isComplete()) {
         throw new IllegalStateException("Task is not complete");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return (AINodeResult)this.handler.apply(this.result);
      }
   }

   public long getCompleteTime() {
      return this.completeTime;
   }

   private static enum State {
      Initial,
      Processing,
      Complete;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{Initial, Processing, Complete};
      }
   }
}
