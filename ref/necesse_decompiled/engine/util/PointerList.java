package necesse.engine.util;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;

public class PointerList {
   private List<Runnable> list;

   public PointerList(Pointer... var1) {
      this.list = (List)Arrays.stream(var1).map((var0) -> {
         return () -> {
            MemoryUtil.nmemFree(var0.address());
         };
      }).collect(Collectors.toList());
   }

   public <T extends Pointer> T add(T var1) {
      this.list.add(() -> {
         MemoryUtil.nmemFree(var1.address());
      });
      return var1;
   }

   public <T extends Buffer> T add(T var1) {
      this.list.add(() -> {
         MemoryUtil.memFree(var1);
      });
      return var1;
   }

   public PointerBuffer add(PointerBuffer var1) {
      this.list.add(() -> {
         MemoryUtil.memFree(var1);
      });
      return var1;
   }

   public void addFreeAction(Runnable var1) {
      this.list.add(var1);
   }

   public void freeAll() {
      Iterator var1 = this.list.iterator();

      while(var1.hasNext()) {
         Runnable var2 = (Runnable)var1.next();
         var2.run();
      }

   }
}
