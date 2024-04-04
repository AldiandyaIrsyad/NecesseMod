package necesse.engine.modLoader;

import java.util.Arrays;
import necesse.engine.modLoader.classes.EntryClass;
import necesse.engine.modLoader.classes.ModClass;
import necesse.engine.modLoader.classes.ModPatchClasses;

public class ModClasses {
   public final EntryClass entry;
   public final ModPatchClasses patchClasses;
   private final ModClass[] classes;

   public ModClasses() {
      this.classes = new ModClass[]{this.entry = new EntryClass(), this.patchClasses = new ModPatchClasses()};
   }

   public Iterable<ModClass> getAllClasses() {
      return Arrays.asList(this.classes);
   }
}
