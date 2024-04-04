package necesse.engine.modLoader.classes;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModdedSignature;
import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModCustomPatch;
import necesse.engine.modLoader.annotations.ModCustomPatchMethod;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

public class ModPatchClasses extends ModClass {
   private List<Class<?>> modClasses = new LinkedList();
   private LoadedMod mod;

   public ModPatchClasses() {
   }

   public boolean shouldRegisterModClass(Class<?> var1) {
      return var1.isAnnotationPresent(ModMethodPatch.class) || var1.isAnnotationPresent(ModConstructorPatch.class) || var1.isAnnotationPresent(ModCustomPatch.class);
   }

   public void registerModClass(LoadedMod var1, Class<?> var2) throws ModLoadException {
      this.modClasses.add(var2);
   }

   public void finalizeLoading(LoadedMod var1) throws ModLoadException {
      this.mod = var1;
   }

   public void applyPatches(ComputedValue<Instrumentation> var1, HashSet<ModdedSignature> var2) {
      LinkedList var3 = new LinkedList();
      Iterator var4 = this.modClasses.iterator();

      while(var4.hasNext()) {
         Class var5 = (Class)var4.next();

         try {
            String var7;
            String var8;
            if (var5.isAnnotationPresent(ModMethodPatch.class)) {
               ModMethodPatch var6 = (ModMethodPatch)var5.getAnnotation(ModMethodPatch.class);
               var7 = (String)Arrays.stream(var6.arguments()).map(Class::getSimpleName).collect(Collectors.joining(", "));
               var8 = "." + var6.name() + "(" + var7 + ")";
               GameUtils.insertSortedList((List)var3, new PrioritizedPatchClass(var5, var6.target(), var6.name(), var6.priority(), var8, (var2x) -> {
                  return var2x.visit(Advice.to(var5).on(ElementMatchers.isMethod().and(ElementMatchers.named(var6.name())).and(ElementMatchers.takesArguments(var6.arguments()))));
               }), Comparator.comparingInt((var0) -> {
                  return -var0.priority;
               }));
            } else if (var5.isAnnotationPresent(ModConstructorPatch.class)) {
               ModConstructorPatch var15 = (ModConstructorPatch)var5.getAnnotation(ModConstructorPatch.class);
               var7 = (String)Arrays.stream(var15.arguments()).map(Class::getSimpleName).collect(Collectors.joining(", "));
               var8 = ".constructor(" + var7 + ")";
               GameUtils.insertSortedList((List)var3, new PrioritizedPatchClass(var5, var15.target(), "<init>", var15.priority(), var8, (var2x) -> {
                  return var2x.visit(Advice.to(var5).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(var15.arguments()))));
               }), Comparator.comparingInt((var0) -> {
                  return -var0.priority;
               }));
            } else if (var5.isAnnotationPresent(ModCustomPatch.class)) {
               ModCustomPatch var16 = (ModCustomPatch)var5.getAnnotation(ModCustomPatch.class);
               boolean var18 = false;
               Method[] var19 = var5.getDeclaredMethods();
               int var9 = var19.length;
               byte var10 = 0;
               if (var10 < var9) {
                  Method var11 = var19[var10];
                  if (var11.isAnnotationPresent(ModCustomPatchMethod.class) && var11.getParameterCount() == 1 && var11.getParameterTypes()[0] == Advice.class) {
                     GameUtils.insertSortedList((List)var3, new PrioritizedPatchClass(var5, var16.target(), (String)null, var16.priority(), " with custom patch", (var2x) -> {
                        try {
                           return var2x.visit((AsmVisitorWrapper)var11.invoke(Advice.to(var5)));
                        } catch (InvocationTargetException | IllegalAccessException var4) {
                           throw new RuntimeException(var4);
                        }
                     }), Comparator.comparingInt((var0) -> {
                        return -var0.priority;
                     }));
                  }

                  var18 = true;
               }

               if (!var18) {
                  throw new ModLoadException(this.mod, "@ModCustomPatch must have a method annotated with @ModCustomPatchMethod taking an Advice.class parameter");
               }
            }
         } catch (Exception var13) {
            System.err.println("Error loading mod patch: " + var5.getName());
            var13.printStackTrace();
         }
      }

      var4 = var3.iterator();

      while(var4.hasNext()) {
         PrioritizedPatchClass var14 = (PrioritizedPatchClass)var4.next();

         try {
            final AtomicInteger var17 = new AtomicInteger();
            (new AgentBuilder.Default()).disableClassFormatChanges().with(RedefinitionStrategy.RETRANSFORMATION).with(new AgentBuilder.RedefinitionStrategy.Listener() {
               public void onBatch(int var1, List<Class<?>> var2, List<Class<?>> var3) {
               }

               public Iterable<? extends List<Class<?>>> onError(int var1, List<Class<?>> var2, Throwable var3, List<Class<?>> var4) {
                  return null;
               }

               public void onComplete(int var1, List<Class<?>> var2, Map<List<Class<?>>, Throwable> var3) {
                  var17.addAndGet(var1);
               }
            }).type(ElementMatchers.is(var14.targetClass)).transform((var1x, var2x, var3x, var4x) -> {
               return (DynamicType.Builder)var14.builderFunc.apply(var1x);
            }).installOn((Instrumentation)var1.get());
            ((Instrumentation)var1.get()).retransformClasses((Class[])this.modClasses.toArray(new Class[1]));
            if (var17.get() > 0) {
               System.out.println(this.mod.id + " transformed " + var14.targetClass.getName() + var14.callName);
               var2.add(new ModdedSignature(var14.targetClass.getName(), var14.targetMethod));
            } else {
               GameLog.warn.println(this.mod.id + " advice class \"" + var14.adviceClass.getName() + "\" did not complete any patches. Are you sure you're targeting the right class and method?");
            }
         } catch (Exception var12) {
            System.err.println("Error loading mod patch: " + var14.adviceClass.getName());
            var12.printStackTrace();
         }
      }

   }

   private static class PrioritizedPatchClass {
      public final Class<?> adviceClass;
      public final Class<?> targetClass;
      public final String targetMethod;
      public final int priority;
      public final String callName;
      public final Function<DynamicType.Builder<?>, DynamicType.Builder<?>> builderFunc;

      public PrioritizedPatchClass(Class<?> var1, Class<?> var2, String var3, int var4, String var5, Function<DynamicType.Builder<?>, DynamicType.Builder<?>> var6) {
         this.adviceClass = var1;
         this.targetClass = var2;
         this.targetMethod = var3;
         this.priority = var4;
         this.callName = var5;
         this.builderFunc = var6;
      }
   }
}
