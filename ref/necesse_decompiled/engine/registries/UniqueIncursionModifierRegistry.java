package necesse.engine.registries;

import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.util.GameRandom;
import necesse.engine.util.HashMapLinkedList;
import necesse.engine.util.TicketSystemList;
import necesse.entity.levelEvent.incursionModifiers.AlchemicalInterferenceIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.CrawlmageddonIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.ExplosiveIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.FlamelingsIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.FrenzyIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.TremorsIncursionModifier;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class UniqueIncursionModifierRegistry extends GameRegistry<UniqueIncursionModifier> {
   public static final UniqueIncursionModifierRegistry instance = new UniqueIncursionModifierRegistry();
   protected HashMapLinkedList<ModifierChallengeLevel, UniqueIncursionModifier> challengeLevelMap = new HashMapLinkedList();

   private UniqueIncursionModifierRegistry() {
      super("UniqueIncursionModifier", 32766);
   }

   public void registerCore() {
      registerUniqueModifier("alchemicalinterference", new AlchemicalInterferenceIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerUniqueModifier("crawlmageddon", new CrawlmageddonIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel.Easy));
      registerUniqueModifier("frenzy", new FrenzyIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerUniqueModifier("tremors", new TremorsIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel.Medium));
      registerUniqueModifier("explosive", new ExplosiveIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel.Hard));
      registerUniqueModifier("flamelings", new FlamelingsIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel.Hard));
   }

   protected void onRegister(UniqueIncursionModifier var1, int var2, String var3, boolean var4) {
      this.challengeLevelMap.add(var1.challengeLevel, var1);
   }

   protected void onRegistryClose() {
   }

   public static int registerUniqueModifier(String var0, UniqueIncursionModifier var1) {
      return instance.register(var0, var1);
   }

   public static Iterable<UniqueIncursionModifier> getModifiers() {
      return instance.getElements();
   }

   public static Iterable<UniqueIncursionModifier> getModifiers(ModifierChallengeLevel var0) {
      return (Iterable)instance.challengeLevelMap.get(var0);
   }

   public static TicketSystemList<UniqueIncursionModifier> getAvailableIncursionModifiers(GameRandom var0, IncursionData var1, Function<UniqueIncursionModifier, Integer> var2) {
      ModifierChallengeLevel[] var3 = UniqueIncursionModifierRegistry.ModifierChallengeLevel.values();
      int var4 = var0.nextInt(var3.length);

      for(int var5 = 0; var5 < var3.length; ++var5) {
         int var6 = (var5 + var4) % var3.length;
         TicketSystemList var7 = new TicketSystemList();
         Iterator var8 = getModifiers(var3[var6]).iterator();

         while(var8.hasNext()) {
            UniqueIncursionModifier var9 = (UniqueIncursionModifier)var8.next();
            int var10 = var9.getModifierTickets(var1) * (var2 == null ? 1 : (Integer)var2.apply(var9));
            if (var10 > 0) {
               var7.addObject(var10, var9);
            }
         }

         if (!var7.isEmpty()) {
            return var7;
         }
      }

      return new TicketSystemList();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((UniqueIncursionModifier)var1, var2, var3, var4);
   }

   public static enum ModifierChallengeLevel {
      Easy,
      Medium,
      Hard;

      private ModifierChallengeLevel() {
      }

      // $FF: synthetic method
      private static ModifierChallengeLevel[] $values() {
         return new ModifierChallengeLevel[]{Easy, Medium, Hard};
      }
   }
}
