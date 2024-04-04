package necesse.engine.registries;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolDamageEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;

public class EnchantmentRegistry extends GameRegistry<ItemEnchantment> {
   public static final EnchantmentRegistry instance = new EnchantmentRegistry();
   public static Set<Integer> equipmentEnchantments = new HashSet();
   public static Set<Integer> meleeItemEnchantments = new HashSet();
   public static Set<Integer> rangedItemEnchantments = new HashSet();
   public static Set<Integer> magicItemEnchantments = new HashSet();
   public static Set<Integer> summonItemEnchantments = new HashSet();
   public static Set<Integer> toolDamageEnchantments = new HashSet();
   public static int keen;
   public static int blunt;
   public static int nimble;
   public static int sluggish;
   public static int quick;
   public static int clumsy;
   public static int sturdy;
   public static int weak;
   public static int precise;
   public static int sloppy;
   public static int bulky;
   public static int puny;
   public static int magical;
   public static int draining;
   public static int tenacious;
   public static int flimsy;
   public static int tough;
   public static int fragile;
   public static int berserk;
   public static int adamant;
   public static int agile;
   public static int harmful;
   public static int grand;
   public static int docile;
   public static int shoddy;
   public static int amateur;
   public static int envious;
   public static int masterful;
   public static int skillful;
   public static int tightened;
   public static int modern;
   public static int trained;
   public static int loose;
   public static int eroding;
   public static int primitive;
   public static int faulty;
   public static int divine;
   public static int wrathful;
   public static int wise;
   public static int adept;
   public static int apprentice;
   public static int decaying;
   public static int novice;
   public static int daft;
   public static int corrupt;
   public static int savage;
   public static int athletic;
   public static int mindful;
   public static int proud;
   public static int aware;
   public static int spoiled;
   public static int sick;
   public static int spiteful;
   public static int naive;
   public static int master;
   public static int shining;
   public static int sharp;
   public static int absurd;
   public static int used;

   public EnchantmentRegistry() {
      super("Enchantment", 32767);
   }

   public void registerCore() {
      registerEnchantment("noenchant", new ItemEnchantment(new ModifierList(), 0));
      keen = registerEquipmentEnchantment("keen", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.04F)}));
      blunt = registerEquipmentEnchantment("blunt", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, -0.04F)}));
      nimble = registerEquipmentEnchantment("nimble", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.04F)}));
      sluggish = registerEquipmentEnchantment("sluggish", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, -0.04F)}));
      quick = registerEquipmentEnchantment("quick", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.04F)}));
      clumsy = registerEquipmentEnchantment("clumsy", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, -0.04F)}));
      sturdy = registerEquipmentEnchantment("sturdy", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.ARMOR_FLAT, 4)}));
      weak = registerEquipmentEnchantment("weak", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.ARMOR_FLAT, -4)}));
      precise = registerEquipmentEnchantment("precise", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.04F)}));
      sloppy = registerEquipmentEnchantment("sloppy", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, -0.04F)}));
      bulky = registerEquipmentEnchantment("bulky", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10)}));
      puny = registerEquipmentEnchantment("puny", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)}));
      magical = registerEquipmentEnchantment("magical", new EquipmentItemEnchant(5, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 0.2F)}));
      draining = registerEquipmentEnchantment("draining", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, -0.2F)}));
      tenacious = registerEquipmentEnchantment("tenacious", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.RESILIENCE_GAIN, 0.04F)}));
      flimsy = registerEquipmentEnchantment("flimsy", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.RESILIENCE_GAIN, -0.04F)}));
      tough = registerEquipmentEnchantment("tough", new EquipmentItemEnchant(10, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_RESILIENCE_FLAT, 10)}));
      fragile = registerEquipmentEnchantment("fragile", new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_RESILIENCE_FLAT, -10)}));
      berserk = registerMeleeEnchantment("berserk", new ToolItemEnchantment(15, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.15F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F)}));
      adamant = registerMeleeEnchantment("adamant", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.2F), new ModifierValue(ToolItemModifiers.KNOCKBACK, -1.0F)}));
      agile = registerMeleeEnchantment("agile", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.3F)}));
      harmful = registerMeleeEnchantment("harmful", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.2F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F)}));
      grand = registerMeleeEnchantment("grand", new ToolItemEnchantment(0, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.2F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.2F), new ModifierValue(ToolItemModifiers.KNOCKBACK, 0.5F)}));
      docile = registerMeleeEnchantment("docile", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.2F)}));
      shoddy = registerMeleeEnchantment("shoddy", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.15F)}));
      amateur = registerMeleeEnchantment("amateur", new ToolItemEnchantment(-30, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.15F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.1F)}));
      envious = registerMeleeEnchantment("envious", new ToolItemEnchantment(-40, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.2F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.1F)}));
      masterful = registerRangedEnchantment("masterful", new ToolItemEnchantment(20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.15F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.2F), new ModifierValue(ToolItemModifiers.RANGE, 0.15F)}));
      skillful = registerRangedEnchantment("skillful", new ToolItemEnchantment(15, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.15F), new ModifierValue(ToolItemModifiers.RANGE, 0.2F)}));
      tightened = registerRangedEnchantment("tightened", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.25F)}));
      modern = registerRangedEnchantment("modern", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.25F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F)}));
      trained = registerRangedEnchantment("trained", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.05F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F)}));
      loose = registerRangedEnchantment("loose", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.05F), new ModifierValue(ToolItemModifiers.VELOCITY, -0.25F)}));
      eroding = registerRangedEnchantment("eroding", new ToolItemEnchantment(-30, new ModifierValue[]{new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.15F), new ModifierValue(ToolItemModifiers.RANGE, -0.1F)}));
      primitive = registerRangedEnchantment("primitive", new ToolItemEnchantment(-30, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.1F), new ModifierValue(ToolItemModifiers.RANGE, -0.2F)}));
      faulty = registerRangedEnchantment("faulty", new ToolItemEnchantment(-40, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.15F), new ModifierValue(ToolItemModifiers.VELOCITY, -0.1F)}));
      divine = registerMagicEnchantment("divine", new ToolItemEnchantment(20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.15F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.1F), new ModifierValue(ToolItemModifiers.RANGE, 0.2F), new ModifierValue(ToolItemModifiers.MANA_USAGE, -0.3F)}));
      wrathful = registerMagicEnchantment("wrathful", new ToolItemEnchantment(15, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F)}));
      wise = registerMagicEnchantment("wise", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.15F), new ModifierValue(ToolItemModifiers.RANGE, 0.25F), new ModifierValue(ToolItemModifiers.MANA_USAGE, -0.25F)}));
      adept = registerMagicEnchantment("adept", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.05F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(ToolItemModifiers.MANA_USAGE, -0.1F)}));
      apprentice = registerMagicEnchantment("apprentice", new ToolItemEnchantment(5, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.05F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(ToolItemModifiers.VELOCITY, 0.1F)}));
      decaying = registerMagicEnchantment("decaying", new ToolItemEnchantment(-10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.05F), new ModifierValue(ToolItemModifiers.VELOCITY, -0.1F), new ModifierValue(ToolItemModifiers.MANA_USAGE, 0.1F)}));
      novice = registerMagicEnchantment("novice", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.1F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.05F), new ModifierValue(ToolItemModifiers.MANA_USAGE, 0.2F)}));
      daft = registerMagicEnchantment("daft", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.1F), new ModifierValue(ToolItemModifiers.RANGE, -0.2F), new ModifierValue(ToolItemModifiers.MANA_USAGE, 0.3F)}));
      corrupt = registerMagicEnchantment("corrupt", new ToolItemEnchantment(-40, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.15F), new ModifierValue(ToolItemModifiers.ATTACK_SPEED, -0.1F), new ModifierValue(ToolItemModifiers.VELOCITY, -0.1F)}));
      savage = registerSummonEnchantment("savage", new ToolItemEnchantment(20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.15F), new ModifierValue(ToolItemModifiers.SUMMONS_SPEED, 0.1F)}));
      athletic = registerSummonEnchantment("athletic", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.SUMMONS_SPEED, 0.25F), new ModifierValue(ToolItemModifiers.SUMMONS_TARGET_RANGE, 0.2F)}));
      mindful = registerSummonEnchantment("mindful", new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F)}));
      proud = registerSummonEnchantment("proud", new ToolItemEnchantment(5, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.05F), new ModifierValue(ToolItemModifiers.SUMMONS_SPEED, 0.1F)}));
      aware = registerSummonEnchantment("aware", new ToolItemEnchantment(5, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, 0.05F), new ModifierValue(ToolItemModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(ToolItemModifiers.SUMMONS_TARGET_RANGE, 0.2F)}));
      spoiled = registerSummonEnchantment("spoiled", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.1F)}));
      sick = registerSummonEnchantment("sick", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.SUMMONS_SPEED, -0.15F), new ModifierValue(ToolItemModifiers.SUMMONS_TARGET_RANGE, -0.1F)}));
      spiteful = registerSummonEnchantment("spiteful", new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.1F), new ModifierValue(ToolItemModifiers.SUMMONS_SPEED, -0.05F)}));
      naive = registerSummonEnchantment("naive", new ToolItemEnchantment(-30, new ModifierValue[]{new ModifierValue(ToolItemModifiers.DAMAGE, -0.15F), new ModifierValue(ToolItemModifiers.SUMMONS_TARGET_RANGE, -0.1F)}));
      master = registerToolEnchantment("master", new ToolDamageEnchantment(20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.TOOL_DAMAGE, 0.2F), new ModifierValue(ToolItemModifiers.MINING_SPEED, 0.15F)}));
      shining = registerToolEnchantment("shining", new ToolDamageEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.TOOL_DAMAGE, 0.1F), new ModifierValue(ToolItemModifiers.MINING_SPEED, 0.1F)}));
      sharp = registerToolEnchantment("sharp", new ToolDamageEnchantment(5, new ModifierValue[]{new ModifierValue(ToolItemModifiers.TOOL_DAMAGE, 0.1F)}));
      absurd = registerToolEnchantment("absurd", new ToolDamageEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.MINING_SPEED, -0.1F)}));
      used = registerToolEnchantment("used", new ToolDamageEnchantment(-30, new ModifierValue[]{new ModifierValue(ToolItemModifiers.TOOL_DAMAGE, -0.15F)}));
   }

   public static int registerEnchantment(String var0, ItemEnchantment var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register enchantments");
      } else {
         return instance.register(var0, var1);
      }
   }

   public static int registerEquipmentEnchantment(String var0, EquipmentItemEnchant var1) {
      int var2 = registerEnchantment(var0, var1);
      equipmentEnchantments.add(var2);
      return var2;
   }

   public static int registerMeleeEnchantment(String var0, ToolItemEnchantment var1) {
      int var2 = registerEnchantment(var0, var1);
      meleeItemEnchantments.add(var2);
      return var2;
   }

   public static int registerRangedEnchantment(String var0, ToolItemEnchantment var1) {
      int var2 = registerEnchantment(var0, var1);
      rangedItemEnchantments.add(var2);
      return var2;
   }

   public static int registerMagicEnchantment(String var0, ToolItemEnchantment var1) {
      int var2 = registerEnchantment(var0, var1);
      magicItemEnchantments.add(var2);
      return var2;
   }

   public static int registerSummonEnchantment(String var0, ToolItemEnchantment var1) {
      int var2 = registerEnchantment(var0, var1);
      summonItemEnchantments.add(var2);
      return var2;
   }

   public static int registerToolEnchantment(String var0, ToolDamageEnchantment var1) {
      int var2 = registerEnchantment(var0, var1);
      toolDamageEnchantments.add(var2);
      return var2;
   }

   protected void onRegister(ItemEnchantment var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         ItemEnchantment var2 = (ItemEnchantment)var1.next();
         var2.onEnchantmentRegistryClosed();
      }

      equipmentEnchantments = Collections.unmodifiableSet(equipmentEnchantments);
      meleeItemEnchantments = Collections.unmodifiableSet(meleeItemEnchantments);
      rangedItemEnchantments = Collections.unmodifiableSet(rangedItemEnchantments);
      magicItemEnchantments = Collections.unmodifiableSet(magicItemEnchantments);
      summonItemEnchantments = Collections.unmodifiableSet(summonItemEnchantments);
      toolDamageEnchantments = Collections.unmodifiableSet(toolDamageEnchantments);
   }

   public static ItemEnchantment getEnchantment(int var0) {
      try {
         return (ItemEnchantment)instance.getElementRaw(var0);
      } catch (NoSuchElementException var2) {
         return null;
      }
   }

   public static <T extends ItemEnchantment> T getEnchantment(int var0, Class<T> var1) {
      ItemEnchantment var2 = getEnchantment(var0);
      return var1.isInstance(var2) ? (ItemEnchantment)var1.cast(var2) : null;
   }

   public static <T extends ItemEnchantment> T getEnchantment(int var0, Class<T> var1, T var2) {
      if (var0 == 0) {
         return var2;
      } else {
         ItemEnchantment var3 = getEnchantment(var0, var1);
         return var3 == null ? var2 : var3;
      }
   }

   public static int getEnchantmentID(String var0) {
      try {
         return instance.getElementIDRaw(var0);
      } catch (NoSuchElementException var2) {
         return -1;
      }
   }

   public static String getEnchantmentStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static ItemEnchantment getEnchantment(String var0) {
      return getEnchantment(getEnchantmentID(var0));
   }

   public static <T extends ItemEnchantment> T getEnchantment(String var0, Class<T> var1) {
      return getEnchantment(getEnchantmentID(var0), var1);
   }

   public static List<ItemEnchantment> getEnchantments() {
      return (List)instance.streamElements().collect(Collectors.toList());
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((ItemEnchantment)var1, var2, var3, var4);
   }
}
