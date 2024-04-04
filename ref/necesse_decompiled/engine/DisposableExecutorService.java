package necesse.engine;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DisposableExecutorService implements ExecutorService {
   private final ExecutorService service;
   private boolean disposed;

   public DisposableExecutorService(ExecutorService var1) {
      this.service = var1;
   }

   public void shutdown() {
      this.service.shutdown();
   }

   public List<Runnable> shutdownNow() {
      return this.service.shutdownNow();
   }

   public boolean isShutdown() {
      return this.service.isShutdown();
   }

   public boolean isTerminated() {
      return this.service.isTerminated();
   }

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      return this.service.awaitTermination(var1, var3);
   }

   public <T> Future<T> submit(Callable<T> var1) {
      return this.service.submit(var1);
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      return this.service.submit(var1, var2);
   }

   public Future<?> submit(Runnable var1) {
      return this.service.submit(var1);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      return this.service.invokeAll(var1);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.service.invokeAll(var1, var2, var4);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      return this.service.invokeAny(var1);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      return this.service.invokeAny(var1, var2, var4);
   }

   public void execute(Runnable var1) {
      this.service.execute(var1);
   }

   public void dispose() {
      this.disposed = true;
      this.shutdownNow();
   }

   public boolean isDisposed() {
      return this.disposed;
   }
}
