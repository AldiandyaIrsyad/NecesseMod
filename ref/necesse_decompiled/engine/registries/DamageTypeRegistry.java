package necesse.engine.registries;

import java.util.Iterator;
import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.mobs.gameDamageType.MagicDamageType;
import necesse.entity.mobs.gameDamageType.MeleeDamageType;
import necesse.entity.mobs.gameDamageType.NormalDamageType;
import necesse.entity.mobs.gameDamageType.RangedDamageType;
import necesse.entity.mobs.gameDamageType.SummonDamageType;
import necesse.entity.mobs.gameDamageType.TrueDamageType;

public class DamageTypeRegistry extends GameRegistry<DamageTypeRegistryElement> {
   public static final DamageTypeRegistry instance = new DamageTypeRegistry();
   public static DamageType NORMAL;
   public static DamageType TRUE;
   public static DamageType MELEE;
   public static DamageType RANGED;
   public static DamageType MAGIC;
   public static DamageType SUMMON;

   private DamageTypeRegistry() {
      super("GameDamageType", 122);
   }

   public void registerCore() {
      registerDamageType("normal", NORMAL = new NormalDamageType());
      registerDamageType("true", TRUE = new TrueDamageType());
      registerDamageType("melee", MELEE = new MeleeDamageType());
      registerDamageType("ranged", RANGED = new RangedDamageType());
      registerDamageType("magic", MAGIC = new MagicDamageType());
      registerDamageType("summon", SUMMON = new SummonDamageType());
   }

   protected void onRegister(DamageTypeRegistryElement var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         DamageTypeRegistryElement var2 = (DamageTypeRegistryElement)var1.next();
         var2.type.onDamageTypeRegistryClosed();
      }

   }

   public static int registerDamageType(String var0, DamageType var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register damage types");
      } else {
         return instance.register(var0, new DamageTypeRegistryElement(var1));
      }
   }

   public static DamageType getDamageType(int var0) {
      if (var0 == -1) {
         return null;
      } else {
         DamageTypeRegistryElement var1 = (DamageTypeRegistryElement)instance.getElement(var0);
         return var1 == null ? null : var1.type;
      }
   }

   public static int getDamageTypeID(String var0) {
      return instance.getElementID(var0);
   }

   public static DamageType getDamageType(String var0) {
      return getDamageType(getDamageTypeID(var0));
   }

   public static String getDamageTypeStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static Iterable<DamageType> getDamageTypes() {
      return (Iterable)instance.streamElements().map((var0) -> {
         return var0.type;
      }).collect(Collectors.toList());
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((DamageTypeRegistryElement)var1, var2, var3, var4);
   }

   protected static class DamageTypeRegistryElement implements IDDataContainer {
      public final DamageType type;

      public DamageTypeRegistryElement(DamageType var1) {
         this.type = var1;
      }

      public IDData getIDData() {
         return this.type.idData;
      }
   }
}
