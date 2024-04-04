package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;

public class ProjectileModifierRegistry extends ClassedGameRegistry<ProjectileModifier, ProjectileModifierRegistryElement> {
   public static final ProjectileModifierRegistry instance = new ProjectileModifierRegistry();

   private ProjectileModifierRegistry() {
      super("ProjectileModifier", 32762);
   }

   public void registerCore() {
      registerModifier("resilienceonhit", ResilienceOnHitProjectileModifier.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerModifier(String var0, Class<? extends ProjectileModifier> var1) {
      try {
         return instance.register(var0, new ProjectileModifierRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         System.err.println("Could not register ProjectileModifier " + var1.getSimpleName() + ": Missing constructor with no parameters");
         return -1;
      }
   }

   public static ProjectileModifier getModifier(int var0) {
      try {
         return (ProjectileModifier)((ProjectileModifierRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static ProjectileModifier getModifier(String var0) {
      return getModifier(getModifierID(var0));
   }

   public static int getModifierID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getModifierID(Class<? extends ProjectileModifier> var0) {
      return instance.getElementID(var0);
   }

   public static String getModifierStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   protected static class ProjectileModifierRegistryElement extends ClassIDDataContainer<ProjectileModifier> {
      public ProjectileModifierRegistryElement(Class<? extends ProjectileModifier> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
