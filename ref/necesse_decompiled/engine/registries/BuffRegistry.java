package necesse.engine.registries;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modifiers.ModifierUpgradeValue;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.AblazeBuff;
import necesse.entity.mobs.buffs.staticBuffs.BannerOfDamageBuff;
import necesse.entity.mobs.buffs.staticBuffs.BannerOfDefenseBuff;
import necesse.entity.mobs.buffs.staticBuffs.BannerOfSpeedBuff;
import necesse.entity.mobs.buffs.staticBuffs.BannerOfSummonSpeedBuff;
import necesse.entity.mobs.buffs.staticBuffs.BloodClawStacksBuff;
import necesse.entity.mobs.buffs.staticBuffs.BloodGrimoireMarkedBuff;
import necesse.entity.mobs.buffs.staticBuffs.BloodplateCowlActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.buffs.staticBuffs.BrokenArmorBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.CampfireBuff;
import necesse.entity.mobs.buffs.staticBuffs.ChilledBuff;
import necesse.entity.mobs.buffs.staticBuffs.CrushingDarknessBuff;
import necesse.entity.mobs.buffs.staticBuffs.DebugBuff;
import necesse.entity.mobs.buffs.staticBuffs.DebugInvisibilityBuff;
import necesse.entity.mobs.buffs.staticBuffs.FoodBuff;
import necesse.entity.mobs.buffs.staticBuffs.ForceOfWindActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FreezingBuff;
import necesse.entity.mobs.buffs.staticBuffs.FrostSlowBuff;
import necesse.entity.mobs.buffs.staticBuffs.FrostburnBuff;
import necesse.entity.mobs.buffs.staticBuffs.GuardianShellActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.HauntedBuff;
import necesse.entity.mobs.buffs.staticBuffs.HealthPotionFatigueBuff;
import necesse.entity.mobs.buffs.staticBuffs.HiddenCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.HumanAngryBuff;
import necesse.entity.mobs.buffs.staticBuffs.HungryBuff;
import necesse.entity.mobs.buffs.staticBuffs.IvyPoisonBuff;
import necesse.entity.mobs.buffs.staticBuffs.LeatherDashersActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.LifelineCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.ManaExhaustionBuff;
import necesse.entity.mobs.buffs.staticBuffs.ManaPotionFatigueBuff;
import necesse.entity.mobs.buffs.staticBuffs.MoveSpeedBurstBuff;
import necesse.entity.mobs.buffs.staticBuffs.MyceliumHoodActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.MyceliumScarfActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.NightsteelActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.OnFireBuff;
import necesse.entity.mobs.buffs.staticBuffs.PirateEscapeBuff;
import necesse.entity.mobs.buffs.staticBuffs.PiratePassiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.SandKnifeWoundBuff;
import necesse.entity.mobs.buffs.staticBuffs.SettlementFlagBuff;
import necesse.entity.mobs.buffs.staticBuffs.SettlerSprintBuff;
import necesse.entity.mobs.buffs.staticBuffs.ShadowHoodSetBuff;
import necesse.entity.mobs.buffs.staticBuffs.ShieldActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.ShownCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.ShownItemCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.SimpleModifierBuff;
import necesse.entity.mobs.buffs.staticBuffs.SimplePotionBuff;
import necesse.entity.mobs.buffs.staticBuffs.SleepingBuff;
import necesse.entity.mobs.buffs.staticBuffs.SlimeDomeActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.SlimeGreatbowSlowTargetBuff;
import necesse.entity.mobs.buffs.staticBuffs.SlimePoisonBuff;
import necesse.entity.mobs.buffs.staticBuffs.SnowCoveredBuff;
import necesse.entity.mobs.buffs.staticBuffs.SnowCoveredSlowBuff;
import necesse.entity.mobs.buffs.staticBuffs.SpiderCharmPoisonBuff;
import necesse.entity.mobs.buffs.staticBuffs.SpiderVenomBuff;
import necesse.entity.mobs.buffs.staticBuffs.SpiderWebSlowBuff;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.mobs.buffs.staticBuffs.StarBarrierBuff;
import necesse.entity.mobs.buffs.staticBuffs.StarvingBuff;
import necesse.entity.mobs.buffs.staticBuffs.StatsBuff;
import necesse.entity.mobs.buffs.staticBuffs.StinkFlaskBuff;
import necesse.entity.mobs.buffs.staticBuffs.SummonedMobBuff;
import necesse.entity.mobs.buffs.staticBuffs.SummonedMountBuff;
import necesse.entity.mobs.buffs.staticBuffs.SummonedPetBuff;
import necesse.entity.mobs.buffs.staticBuffs.TeleportSicknessBuff;
import necesse.entity.mobs.buffs.staticBuffs.ThornsPotionBuff;
import necesse.entity.mobs.buffs.staticBuffs.WebPotionBuff;
import necesse.entity.mobs.buffs.staticBuffs.WebPotionSlowBuff;
import necesse.entity.mobs.buffs.staticBuffs.WidowPoisonBuff;
import necesse.entity.mobs.buffs.staticBuffs.ZephyrBootsActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.AncientFossilHelmetSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.AncientFossilMaskSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.BloodplateCowlSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DawnSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DemonicSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DuskSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.FrostSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.GlacialCircletBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.GlacialHelmetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.IvyCircletSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.IvyHatSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.IvyHelmetSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.IvyHoodSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.MyceliumHoodSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.MyceliumScarfSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.NightSteelCircletSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.NightSteelHelmetSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.NightSteelSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.NightSteelVeilSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.NinjaSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.QuartzCrownSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.QuartzHelmetSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.RavenlordsHeaddressSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.ShadowHatSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.ShadowHoodSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SimpleUpgradeSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SlimeHatSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SlimeHelmetSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SpiderSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SpideriteCrownSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SpideriteHatSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SpideriteHelmetSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SpideriteSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.TungstenSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.VoidHatSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.VoidMaskSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.WidowSetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.AgedChampionScabbardBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.BalancedFociBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.BlinkScepterTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.BloodstoneRingBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.BloodstoneRingRegenActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ChallengersPauldronBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ClockworkHeartBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ExplorerCloakBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FinsBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FireStoneBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FoolsGambitTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ForceOfWindTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FrenzyOrbBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FrostFlameBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FrostStoneBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.GuardianShellTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.LeatherDashersTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.LifelineBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.MagicFociBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.MeleeFociBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.MobilityCloakBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.NinjasMarkBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.PolarClawBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.RangeFociBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ShieldTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ShineBeltBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SpiderCharmBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SummonFociBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TravelerCloakBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ZephyrBootsTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.incursionBuffs.AlchemicalInterferenceBuff;
import necesse.entity.mobs.buffs.staticBuffs.incursionBuffs.FrenzyBuff;
import necesse.entity.mobs.buffs.staticBuffs.incursionBuffs.TremorHappeningBuff;
import necesse.entity.mobs.buffs.staticBuffs.incursionBuffs.TremorsIncomingBuff;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class BuffRegistry extends GameRegistry<BuffRegistryElement<?>> {
   public static Buff FRENZY;
   public static Buff TREMORS_INCOMING;
   public static Buff TREMOR_HAPPENING;
   public static Buff ALCHEMICAL_INTERFERENCE;
   public static Buff DEBUG_BUFF;
   public static Buff DEBUG_INVIS;
   public static Buff PIRATE_ESCAPE;
   public static Buff PIRATE_PASSIVE;
   public static Buff SUMMONED_PET;
   public static Buff SUMMONED_MOUNT;
   public static Buff SUMMONED_MOB;
   public static Buff HUMAN_ANGRY;
   public static Buff MOVE_SPEED_BURST;
   public static Buff BOSS_NEARBY;
   public static Buff SETTLEMENT_FLAG;
   public static Buff CAMPFIRE;
   public static Buff SHADOWHOOD_SET;
   public static Buff ANCIENT_FOSSIL_ACTIVE;
   public static Buff SPIDER_CHARGE;
   public static Buff BLOODPLATE_COWL_ACTIVE;
   public static Buff BLOOD_CLAW_STACKS_BUFF;
   public static Buff BLOODSTONE_RING_REGEN_ACTIVE_BUFF;
   public static Buff STAR_BARRIER_BUFF;
   public static Buff HARDENED;
   public static Buff SETTLER_SPRINT;
   public static Buff LEATHER_DASHERS_ACTIVE;
   public static Buff ZEPHYR_BOOTS_ACTIVE;
   public static Buff FOW_ACTIVE;
   public static Buff GUARDIAN_SHELL_ACTIVE;
   public static Buff MYCELIUM_HOOD_ACTIVE;
   public static Buff MYCELIUM_SCARF_ACTIVE;
   public static Buff NIGHTSTEEL_ACTIVE;
   public static Buff SLIME_DOME_ACTIVE;
   public static Buff SHIELD_ACTIVE;
   public static Buff SLEEPING;
   public static StatsBuff EQUIPMENT_BUFF;
   public static FoodBuff FOOD_BUFF;
   public static FoodBuff FOOD_DEBUFF;
   public static Buff HUNGRY_BUFF;
   public static Buff STARVING_BUFF;
   public static Buff STAMINA_BUFF;
   public static StatsBuff SETTLER_STATS_BUFF;
   public static final BuffRegistry instance = new BuffRegistry();
   private static int totalSetBonuses = 0;

   private BuffRegistry() {
      super("Buff", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "buffs"));
      DEBUG_BUFF = registerBuff("debugbuff", new DebugBuff());
      DEBUG_INVIS = registerBuff("debuginvisibility", new DebugInvisibilityBuff());
      registerBuff("speedpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.2F)}));
      registerBuff("greaterspeedpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.3F)}));
      registerBuff("healthregenpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)}));
      registerBuff("greaterhealthregenpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 1.0F)}));
      registerBuff("manaregenpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 2.0F)}));
      registerBuff("greatermanaregenpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 4.0F)}));
      registerBuff("attackspeedpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.15F)}));
      registerBuff("greaterattackspeedpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.3F)}));
      registerBuff("invisibilitypotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.INVISIBILITY, true)}));
      registerBuff("fireresistancepotion", new SimplePotionBuff(new ModifierValue[]{(new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F)).max(0.0F)}));
      registerBuff("fishingpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.FISHING_POWER, 20), new ModifierValue(BuffModifiers.FISHING_LINES, 1)}));
      registerBuff("greaterfishingpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.FISHING_POWER, 30), new ModifierValue(BuffModifiers.FISHING_LINES, 2)}));
      registerBuff("battlepotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F)}));
      registerBuff("greaterbattlepotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.15F)}));
      registerBuff("resistancepotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ARMOR_FLAT, 8)}));
      registerBuff("greaterresistancepotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.ARMOR_FLAT, 12)}));
      registerBuff("spelunkerpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.SPELUNKER, true)}));
      registerBuff("treasurepotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.TREASURE_HUNTER, true)}));
      registerBuff("passivepotion", new SimplePotionBuff(new ModifierValue[]{(new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 0.0F)).max(0.1F)}));
      registerBuff("buildingpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.BUILDING_SPEED, 0.5F)}));
      registerBuff("greaterbuildingpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.BUILDING_SPEED, 1.0F)}));
      registerBuff("miningpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MINING_SPEED, 0.4F)}));
      registerBuff("greaterminingpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MINING_SPEED, 0.8F)}));
      registerBuff("thornspotion", new ThornsPotionBuff());
      registerBuff("knockbackpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.KNOCKBACK_OUT, 0.5F)}));
      registerBuff("rapidpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.PROJECTILE_VELOCITY, 0.5F)}));
      registerBuff("greaterrapidpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.PROJECTILE_VELOCITY, 1.0F)}));
      registerBuff("accuracypotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.1F)}));
      registerBuff("greateraccuracypotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.2F)}));
      registerBuff("webpotion", new WebPotionBuff());
      registerBuff("strengthpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.1F)}));
      registerBuff("rangerpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_DAMAGE, 0.1F)}));
      registerBuff("wisdompotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.1F)}));
      registerBuff("minionpotion", new SimplePotionBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)}));
      BuffRegistry.Potions.STINKFLASK = registerBuff("stinkflask", new StinkFlaskBuff());
      BuffRegistry.Debuffs.HEALTH_POT_FATIGUE = registerBuff("healthpotionfatigue", new HealthPotionFatigueBuff());
      BuffRegistry.Debuffs.MANA_POT_FATIGUE = registerBuff("manapotionfatigue", new ManaPotionFatigueBuff());
      BuffRegistry.Debuffs.MANA_EXHAUSTION = registerBuff("manaexhaustion", new ManaExhaustionBuff());
      BuffRegistry.Debuffs.ON_FIRE = registerBuff("onfire", new OnFireBuff());
      BuffRegistry.Debuffs.ABLAZE = registerBuff("ablaze", new AblazeBuff());
      BuffRegistry.Debuffs.BROKEN_ARMOR = registerBuff("brokenarmor", new BrokenArmorBuff());
      BuffRegistry.Debuffs.TELEPORT_SICKNESS = registerBuff("teleportsickness", new TeleportSicknessBuff());
      BuffRegistry.Debuffs.LIFELINE_COOLDOWN = registerBuff("lifelinecooldown", new LifelineCooldownBuff());
      BuffRegistry.Debuffs.QUARTZ_SET_COOLDOWN = registerBuff("quartzsetcooldown", new HiddenCooldownBuff());
      BuffRegistry.Debuffs.VOID_SET_COOLDOWN = registerBuff("voidsetcooldown", new ShownCooldownBuff());
      BuffRegistry.Debuffs.DASH_COOLDOWN = registerBuff("dashcooldown", new ShownCooldownBuff(250, true));
      BuffRegistry.Debuffs.SPIDER_WEB_SLOW = registerBuff("spiderwebslow", new SpiderWebSlowBuff());
      BuffRegistry.Debuffs.WEB_POTION_SLOW = registerBuff("webpotionslow", new WebPotionSlowBuff());
      BuffRegistry.Debuffs.SPIDER_VENOM = registerBuff("spidervenom", new SpiderVenomBuff());
      BuffRegistry.Debuffs.IVY_POISON = registerBuff("ivypoison", new IvyPoisonBuff());
      BuffRegistry.Debuffs.WIDOW_POISON = registerBuff("widowpoison", new WidowPoisonBuff());
      BuffRegistry.Debuffs.CHILLED = registerBuff("chilled", new ChilledBuff());
      BuffRegistry.Debuffs.FREEZING = registerBuff("freezing", new FreezingBuff());
      BuffRegistry.Debuffs.FROSTBURN = registerBuff("frostburn", new FrostburnBuff());
      BuffRegistry.Debuffs.FROSTSLOW = registerBuff("frostslow", new FrostSlowBuff());
      BuffRegistry.Debuffs.SNOW_COVERED_DEBUFF = registerBuff("snowcovered", new SnowCoveredBuff());
      BuffRegistry.Debuffs.SNOW_COVERED_SLOW_DEBUFF = registerBuff("snowcoveredslow", new SnowCoveredSlowBuff());
      BuffRegistry.Debuffs.HAUNTED = registerBuff("haunted", new HauntedBuff());
      BuffRegistry.Debuffs.GUARDIAN_SHELL_COOLDOWN = registerBuff("guardianshellcooldown", new ShownCooldownBuff());
      BuffRegistry.Debuffs.ANCIENT_FOSSIL_SET_COOLDOWN = registerBuff("ancientfossilsetcooldown", new ShownCooldownBuff());
      BuffRegistry.Debuffs.SLIME_SET_COOLDOWN = registerBuff("slimesetcooldown", new ShownCooldownBuff());
      BuffRegistry.Debuffs.NIGHTSTEEL_SET_COOLDOWN = registerBuff("nightsteelsetcooldown", new ShownCooldownBuff());
      BuffRegistry.Debuffs.SPIDERITE_SET_COOLDOWN = registerBuff("spideritesetcooldown", new ShownCooldownBuff());
      BuffRegistry.Debuffs.SPIDER_CHARM_POISON = registerBuff("spidercharmpoison", new SpiderCharmPoisonBuff());
      BuffRegistry.Debuffs.SLIME_POISON = registerBuff("slimepoison", new SlimePoisonBuff());
      BuffRegistry.Debuffs.SAND_KNIFE_WOUND_BUFF = registerBuff("sandknifewound", new SandKnifeWoundBuff());
      BuffRegistry.Debuffs.BLOOD_GRIMOIRE_MARKED_DEBUFF = registerBuff("bloodgrimoiredebuff", new BloodGrimoireMarkedBuff());
      BuffRegistry.Debuffs.SLIME_GREATBOW_SLOW_TARGET_DEBUFF = registerBuff("slimegreatbowdebuff", new SlimeGreatbowSlowTargetBuff());
      BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN = registerBuff("bloodgrimoirecooldown", new ShownItemCooldownBuff(1, true, "items/bloodgrimoire"));
      BuffRegistry.Debuffs.THE_CRIMSON_SKY_COOLDOWN = registerBuff("thecrimsonskycooldown", new ShownItemCooldownBuff(1, true, "items/thecrimsonsky"));
      BuffRegistry.Debuffs.CRUSHING_DARKNESS = registerBuff("crushingdarkness", new CrushingDarknessBuff());
      BuffRegistry.SetBonuses.LEATHER = (SetBonusBuff)registerBuff("leathersetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.ATTACK_SPEED, (new FloatUpgradeValue()).setBaseValue(0.1F).setUpgradedValue(1.0F, 0.25F))}));
      BuffRegistry.SetBonuses.CLOTH_ROBE = (SetBonusBuff)registerBuff("clothrobesetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.MAGIC_DAMAGE, (new FloatUpgradeValue()).setBaseValue(0.15F).setUpgradedValue(1.0F, 0.25F)), new ModifierUpgradeValue(BuffModifiers.MAX_MANA_FLAT, (new IntUpgradeValue(0, 0.1F)).setBaseValue(30).setUpgradedValue(1.0F, 250))}));
      BuffRegistry.SetBonuses.COPPER = (SetBonusBuff)registerBuff("coppersetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.ARMOR_FLAT, (new IntUpgradeValue()).setBaseValue(1).setUpgradedValue(1.0F, 6))}));
      BuffRegistry.SetBonuses.IRON = (SetBonusBuff)registerBuff("ironsetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.ARMOR_FLAT, (new IntUpgradeValue()).setBaseValue(2).setUpgradedValue(1.0F, 6))}));
      BuffRegistry.SetBonuses.GOLD_CROWN = (SetBonusBuff)registerBuff("goldcrownsetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.MAX_SUMMONS, (new IntUpgradeValue()).setBaseValue(1).setUpgradedValue(1.0F, 2))}));
      BuffRegistry.SetBonuses.GOLD = (SetBonusBuff)registerBuff("goldsetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.ARMOR_FLAT, (new IntUpgradeValue()).setBaseValue(3).setUpgradedValue(1.0F, 6))}));
      BuffRegistry.SetBonuses.SPIDER = (SetBonusBuff)registerBuff("spidersetbonus", new SpiderSetBonusBuff());
      BuffRegistry.SetBonuses.FROST_HELMET = (SetBonusBuff)registerBuff("frostsetbonus", new FrostSetBonusBuff());
      BuffRegistry.SetBonuses.FROST_HOOD = (SetBonusBuff)registerBuff("frosthoodsetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.CRIT_CHANCE, (new FloatUpgradeValue()).setBaseValue(0.1F).setUpgradedValue(1.0F, 0.2F)), new ModifierUpgradeValue(BuffModifiers.PROJECTILE_VELOCITY, (new FloatUpgradeValue()).setBaseValue(0.5F).setUpgradedValue(1.0F, 1.0F))}));
      BuffRegistry.SetBonuses.FROST_HAT = (SetBonusBuff)registerBuff("frosthatsetbonus", new SimpleUpgradeSetBonusBuff(new ModifierUpgradeValue[]{new ModifierUpgradeValue(BuffModifiers.MAGIC_CRIT_CHANCE, (new FloatUpgradeValue()).setBaseValue(0.1F).setUpgradedValue(1.0F, 0.2F)), new ModifierUpgradeValue(BuffModifiers.MAX_MANA_FLAT, (new IntUpgradeValue(0, 0.1F)).setBaseValue(60).setUpgradedValue(1.0F, 250))}));
      BuffRegistry.SetBonuses.DEMONIC = (SetBonusBuff)registerBuff("demonicsetbonus", new DemonicSetBonusBuff());
      BuffRegistry.SetBonuses.VOIDMASK = (SetBonusBuff)registerBuff("voidmasksetbonus", new VoidMaskSetBonusBuff());
      BuffRegistry.SetBonuses.VOIDHAT = (SetBonusBuff)registerBuff("voidhatsetbonus", new VoidHatSetBonusBuff());
      BuffRegistry.SetBonuses.NINJA = (SetBonusBuff)registerBuff("ninjasetbonus", new NinjaSetBonusBuff());
      BuffRegistry.SetBonuses.IVYHELMET = (SetBonusBuff)registerBuff("ivyhelmetsetbonus", new IvyHelmetSetBonusBuff());
      BuffRegistry.SetBonuses.IVYHOOD = (SetBonusBuff)registerBuff("ivyhoodsetbonus", new IvyHoodSetBonusBuff());
      BuffRegistry.SetBonuses.IVYHAT = (SetBonusBuff)registerBuff("ivyhatsetbonus", new IvyHatSetBonusBuff());
      BuffRegistry.SetBonuses.IVYCIRCLET = (SetBonusBuff)registerBuff("ivycircletsetbonus", new IvyCircletSetBonusBuff());
      BuffRegistry.SetBonuses.QUARTZHELMET = (SetBonusBuff)registerBuff("quartzhelmetsetbonus", new QuartzHelmetSetBonusBuff());
      BuffRegistry.SetBonuses.QUARTZCROWN = (SetBonusBuff)registerBuff("quartzcrownsetbonus", new QuartzCrownSetBonusBuff());
      BuffRegistry.SetBonuses.TUNGSTEN = (SetBonusBuff)registerBuff("tungstensetbonus", new TungstenSetBonusBuff());
      BuffRegistry.SetBonuses.SHADOWHOOD = (SetBonusBuff)registerBuff("shadowhoodsetbonus", new ShadowHoodSetBonusBuff());
      BuffRegistry.SetBonuses.SHADOWHAT = (SetBonusBuff)registerBuff("shadowhatsetbonus", new ShadowHatSetBonusBuff());
      BuffRegistry.SetBonuses.GLACIALHELMET = (SetBonusBuff)registerBuff("glacialhelmetsetbonus", new GlacialHelmetBonusBuff());
      BuffRegistry.SetBonuses.GLACIALCIRCLET = (SetBonusBuff)registerBuff("glacialcircletsetbonus", new GlacialCircletBonusBuff());
      BuffRegistry.SetBonuses.MYCELIUMHOOD = (SetBonusBuff)registerBuff("myceliumhoodsetbonus", new MyceliumHoodSetBonusBuff());
      BuffRegistry.SetBonuses.MYCELIUMSCARF = (SetBonusBuff)registerBuff("myceliumscarfsetbonus", new MyceliumScarfSetBonusBuff());
      BuffRegistry.SetBonuses.WIDOW = (SetBonusBuff)registerBuff("widowsetbonus", new WidowSetBonusBuff());
      BuffRegistry.SetBonuses.ANCIENT_FOSSIL_HELMET = (SetBonusBuff)registerBuff("ancientfossilhelmetsetbonus", new AncientFossilHelmetSetBonusBuff());
      BuffRegistry.SetBonuses.ANCIENT_FOSSIL_MASK = (SetBonusBuff)registerBuff("ancientfossilmasksetbonus", new AncientFossilMaskSetBonusBuff());
      BuffRegistry.SetBonuses.SLIMEHELMET = (SetBonusBuff)registerBuff("slimehelmetsetbonus", new SlimeHelmetSetBonusBuff());
      BuffRegistry.SetBonuses.SLIMEHAT = (SetBonusBuff)registerBuff("slimehatsetbonus", new SlimeHatSetBonusBuff());
      BuffRegistry.SetBonuses.NIGHTSTEEL = (SetBonusBuff)registerBuff("nightsteelsetbonus", new NightSteelSetBonusBuff());
      BuffRegistry.SetBonuses.NIGHTSTEEL_HELMET = (SetBonusBuff)registerBuff("nightsteelhelmetsetbonus", new NightSteelHelmetSetBonusBuff());
      BuffRegistry.SetBonuses.NIGHTSTEEL_VEIL = (SetBonusBuff)registerBuff("nightsteelveilsetbonus", new NightSteelVeilSetBonusBuff());
      BuffRegistry.SetBonuses.NIGHTSTEEL_CIRCLET = (SetBonusBuff)registerBuff("nightsteelcircletsetbonus", new NightSteelCircletSetBonusBuff());
      BuffRegistry.SetBonuses.BLOODPLATE_COWL = (SetBonusBuff)registerBuff("bloodplatecowlsetbonus", new BloodplateCowlSetBonusBuff());
      BuffRegistry.SetBonuses.SPIDERITE = (SetBonusBuff)registerBuff("spideritesetbonus", new SpideriteSetBonusBuff());
      BuffRegistry.SetBonuses.SPIDERITE_HELMET = (SetBonusBuff)registerBuff("spideritehelmetsetbonus", new SpideriteHelmetSetBonusBuff());
      BuffRegistry.SetBonuses.SPIDERITE_HAT = (SetBonusBuff)registerBuff("spideritehatsetbonus", new SpideriteHatSetBonusBuff());
      BuffRegistry.SetBonuses.SPIDERITE_CROWN = (SetBonusBuff)registerBuff("spideritecrownsetbonus", new SpideriteCrownSetBonusBuff());
      BuffRegistry.SetBonuses.RAVENLORDS_HEADDRESS = (SetBonusBuff)registerBuff("ravenlordsheaddresssetbonus", new RavenlordsHeaddressSetBonusBuff());
      BuffRegistry.SetBonuses.DAWN = (SetBonusBuff)registerBuff("dawnsetbonus", new DawnSetBonusBuff());
      BuffRegistry.SetBonuses.DUSK = (SetBonusBuff)registerBuff("dusksetbonus", new DuskSetBonusBuff());
      registerBuff("ninjasmarktrinket", new NinjasMarkBuff());
      registerBuff("leatherglovetrinket", new SimpleTrinketBuff("leatherglovetip", new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F)}));
      registerBuff("trackerboottrinket", new SimpleTrinketBuff("trackerboottip", new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F)}));
      registerBuff("shinebelttrinket", new ShineBeltBuff());
      registerBuff("vampiresgifttrinket", new SimpleTrinketBuff("vampiresgift", new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.15F)}));
      registerBuff("zephyrcharmtrinket", new SimpleTrinketBuff("zephyrcharmtip", new ModifierValue[]{new ModifierValue(BuffModifiers.STAMINA_CAPACITY, 0.5F)}));
      registerBuff("demonclawtrinket", new SimpleTrinketBuff("demonclawtip", new ModifierValue[]{(new ModifierValue(BuffModifiers.KNOCKBACK_INCOMING_MOD)).max(0.0F)}));
      registerBuff("polarclawtrinket", new PolarClawBuff());
      registerBuff("finstrinket", new FinsBuff());
      registerBuff("piratetelescopetrinket", new SimpleTrinketBuff("piratetelescope", new ModifierValue[]{new ModifierValue(BuffModifiers.TRAVEL_DISTANCE, 1), new ModifierValue(BuffModifiers.BIOME_VIEW_DISTANCE, 1)}));
      registerBuff("regenpendanttrinket", new SimpleTrinketBuff("regenpendant", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F)}));
      registerBuff("mobilitycloaktrinket", new MobilityCloakBuff());
      registerBuff("travelercloaktrinket", new TravelerCloakBuff());
      registerBuff("explorercloaktrinket", new ExplorerCloakBuff());
      registerBuff("mesmertablettrinket", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)}));
      registerBuff("inducingamulettrinket", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.3F)}));
      registerBuff("lifelinettrinket", new LifelineBuff());
      registerBuff("magicfocitrinket", new MagicFociBuff());
      registerBuff("rangefocitrinket", new RangeFociBuff());
      registerBuff("meleefocitrinket", new MeleeFociBuff());
      registerBuff("summonfocitrinket", new SummonFociBuff());
      registerBuff("balancedfocitrinket", new BalancedFociBuff());
      registerBuff("ancientfeathertrinket", new SimpleTrinketBuff("ancientfeather", new ModifierValue[]{new ModifierValue(BuffModifiers.PROJECTILE_VELOCITY, 0.5F)}));
      registerBuff("airvesseltrinket", new SimpleTrinketBuff("airvesseltip", new ModifierValue[]{new ModifierValue(BuffModifiers.DASH_STACKS, 2)}));
      registerBuff("miningcharmtrinket", new SimpleTrinketBuff("miningcharm", new ModifierValue[]{new ModifierValue(BuffModifiers.TOOL_DAMAGE, 0.4F)}));
      registerBuff("minersbouquettrinket", new SimpleTrinketBuff(new String[]{"miningcharm", "minersbouquettip"}, new ModifierValue[]{new ModifierValue(BuffModifiers.TOOL_DAMAGE, 0.4F), new ModifierValue(BuffModifiers.MINING_SPEED, 0.25F)}));
      registerBuff("calmingminersbouquettrinket", new SimpleTrinketBuff(new String[]{"miningcharm", "minersbouquettip", "calmingrose"}, new ModifierValue[]{new ModifierValue(BuffModifiers.TOOL_DAMAGE, 0.4F), new ModifierValue(BuffModifiers.MINING_SPEED, 0.25F), (new ModifierValue(BuffModifiers.MOB_SPAWN_RATE)).max(0.4F)}));
      registerBuff("magicalquivertrinket", new SimpleTrinketBuff("magicalquiver", new ModifierValue[]{new ModifierValue(BuffModifiers.ARROW_USAGE, -0.75F)}));
      registerBuff("ammoboxtrinket", new SimpleTrinketBuff("ammobox", new ModifierValue[]{new ModifierValue(BuffModifiers.BULLET_USAGE, -0.75F)}));
      registerBuff("bonehilttrinket", new SimpleTrinketBuff("bonehilt", new ModifierValue[]{new ModifierValue(BuffModifiers.ARMOR_PEN_FLAT, 20)}));
      registerBuff("firestonetrinket", new FireStoneBuff());
      registerBuff("lifependanttrinket", new SimpleTrinketBuff("lifependant", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.75F)}));
      registerBuff("constructionhammertrinket", new SimpleTrinketBuff("constructionhammer", new ModifierValue[]{new ModifierValue(BuffModifiers.BUILDING_SPEED, 0.5F)}));
      registerBuff("telescopicladdertrinket", new SimpleTrinketBuff("telescopicladder", new ModifierValue[]{new ModifierValue(BuffModifiers.BUILD_RANGE, 1.0F)}));
      registerBuff("toolextendertrinket", new SimpleTrinketBuff("toolextender", new ModifierValue[]{new ModifierValue(BuffModifiers.MINING_RANGE, 1.0F)}));
      registerBuff("itemattractortrinket", new SimpleTrinketBuff("itemattractortip", new ModifierValue[]{new ModifierValue(BuffModifiers.ITEM_PICKUP_RANGE, 5.0F)}));
      registerBuff("leatherdasherstrinket", new LeatherDashersTrinketBuff());
      registerBuff("zephyrbootstrinket", new ZephyrBootsTrinketBuff());
      registerBuff("forceofwindtrinket", new ForceOfWindTrinketBuff());
      registerBuff("shieldtrinket", new ShieldTrinketBuff());
      registerBuff("blinksceptertrinket", new BlinkScepterTrinketBuff());
      registerBuff("spikedbootstrinket", new SimpleTrinketBuff("spikedboots", new ModifierValue[]{(new ModifierValue(BuffModifiers.FRICTION, 0.0F)).min(1.0F), new ModifierValue(BuffModifiers.SPEED, 0.15F)}));
      registerBuff("spikedbatbootstrinket", new SimpleTrinketBuff("spikedbatboots", new ModifierValue[]{(new ModifierValue(BuffModifiers.FRICTION, 0.0F)).min(1.0F), new ModifierValue(BuffModifiers.SPEED, 0.25F)}));
      registerBuff("froststonetrinket", new FrostStoneBuff());
      registerBuff("frostflametrinket", new FrostFlameBuff());
      registerBuff("calmingrosetrinket", new SimpleTrinketBuff("calmingrose", new ModifierValue[]{(new ModifierValue(BuffModifiers.MOB_SPAWN_RATE)).max(0.4F)}));
      registerBuff("hysteriatablettrinket", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.3F)}));
      registerBuff("frenzyorbtrinket", new FrenzyOrbBuff());
      registerBuff("frozenhearttrinket", new SimpleTrinketBuff("frozenhearttip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50)}));
      registerBuff("frozenwavetrinket", new SimpleTrinketBuff("frozenwavetip", new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_DAMAGE, 0.25F)}));
      registerBuff("fuzzydicetrinket", new SimpleTrinketBuff("fuzzydicetip", new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)}));
      registerBuff("noblehorseshoetrinket", new SimpleTrinketBuff("noblehorseshoetip", new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)}));
      registerBuff("luckycapetrinket", new SimpleTrinketBuff("luckycapetip", new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.1F)}));
      registerBuff("guardianshelltrinket", new GuardianShellTrinketBuff());
      registerBuff("scryingmirrortrinket", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)}));
      registerBuff("diggingclawtrinket", new SimpleTrinketBuff(new ModifierValue[]{new ModifierValue(BuffModifiers.TOOL_DAMAGE, 0.5F), new ModifierValue(BuffModifiers.MINING_SPEED, 0.5F), new ModifierValue(BuffModifiers.MINING_RANGE, -2.0F)}));
      registerBuff("spidercharmtrinket", new SpiderCharmBuff());
      registerBuff("templependanttrinket", new SimpleTrinketBuff("templependanttip", new ModifierValue[]{new ModifierValue(BuffModifiers.DASH_COOLDOWN, -0.25F)}));
      registerBuff("gelatincasingstrinket", new SimpleTrinketBuff("gelatincasingstip", new ModifierValue[]{new ModifierValue(BuffModifiers.PROJECTILE_BOUNCES, 10)}));
      registerBuff("ancientrelicstrinket", new SimpleTrinketBuff(new String[]{"airvesseltip", "templependanttip"}, new ModifierValue[]{new ModifierValue(BuffModifiers.DASH_STACKS, 2), new ModifierValue(BuffModifiers.DASH_COOLDOWN, -0.25F)}));
      registerBuff("sparegemstonestrinket", new SimpleTrinketBuff("sparegemstonestip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_MANA, 0.2F)}));
      registerBuff("spellstonetrinket", new SimpleTrinketBuff("spellstonetip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_MANA, 0.5F)}));
      registerBuff("dreamcatchertrinket", new SimpleTrinketBuff("dreamcatchertip", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 1.0F)}));
      registerBuff("nightmaretalismantrinket", new SimpleTrinketBuff("nightmaretalismantip", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 2.5F)}));
      registerBuff("prophecyslabtrinket", new SimpleTrinketBuff("prophecyslabtip", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 1.0F)}));
      registerBuff("magicmanualtrinket", new SimpleTrinketBuff("magicmanualtip", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 1.0F)}));
      registerBuff("scryingcardstrinket", new SimpleTrinketBuff("scryingcardstip", new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, 2.5F)}));
      registerBuff("forbiddenspellbooktrinket", new SimpleTrinketBuff("forbiddenspellbooktip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.5F), new ModifierValue(BuffModifiers.MANA_USAGE, 1.0F)}));
      registerBuff("bloodstoneringtrinket", new BloodstoneRingBuff());
      registerBuff("claygauntlettrinket", new SimpleTrinketBuff("claygauntlettip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_RESILIENCE_FLAT, 10), new ModifierValue(BuffModifiers.RESILIENCE_DECAY, -0.75F)}));
      registerBuff("vambracetrinket", new SimpleTrinketBuff("vambracetip", new ModifierValue[]{new ModifierValue(BuffModifiers.RESILIENCE_GAIN, 0.5F)}));
      registerBuff("chainshirttrinket", new SimpleTrinketBuff("chainshirttip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_RESILIENCE_FLAT, 30)}));
      registerBuff("manicatrinket", new SimpleTrinketBuff("manicatip", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_RESILIENCE_FLAT, 50), new ModifierValue(BuffModifiers.RESILIENCE_GAIN, 0.5F)}));
      registerBuff("clockworkhearttrinket", new ClockworkHeartBuff());
      registerBuff("agedchampionscabbardtrinket", new AgedChampionScabbardBuff());
      registerBuff("challengerspauldrontrinket", new ChallengersPauldronBuff());
      registerBuff("foolsgambittrinket", new FoolsGambitTrinketBuff());
      BuffRegistry.Banners.SPEED = registerBuff("bannerofspeed", new BannerOfSpeedBuff());
      BuffRegistry.Banners.DAMAGE = registerBuff("bannerofdamage", new BannerOfDamageBuff());
      BuffRegistry.Banners.SUMMON_SPEED = registerBuff("bannerofsummonspeed", new BannerOfSummonSpeedBuff());
      BuffRegistry.Banners.DEFENSE = registerBuff("bannerofdefense", new BannerOfDefenseBuff());
      FRENZY = registerBuff("frenzy", new FrenzyBuff());
      TREMORS_INCOMING = registerBuff("tremorincoming", new TremorsIncomingBuff());
      TREMOR_HAPPENING = registerBuff("tremorhappening", new TremorHappeningBuff());
      ALCHEMICAL_INTERFERENCE = registerBuff("alchemicalinterference", new AlchemicalInterferenceBuff());
      PIRATE_ESCAPE = registerBuff("pirateescape", new PirateEscapeBuff());
      PIRATE_PASSIVE = registerBuff("piratepassive", new PiratePassiveBuff());
      SUMMONED_PET = registerBuff("summonedpet", new SummonedPetBuff());
      SUMMONED_MOUNT = registerBuff("summonedmount", new SummonedMountBuff());
      SUMMONED_MOB = registerBuff("summonedmob", new SummonedMobBuff());
      HUMAN_ANGRY = registerBuff("humanangry", new HumanAngryBuff());
      MOVE_SPEED_BURST = registerBuff("movespeedburst", new MoveSpeedBurstBuff());
      BOSS_NEARBY = registerBuff("bossnearby", new BossNearbyBuff());
      SETTLEMENT_FLAG = registerBuff("settlementflag", new SettlementFlagBuff());
      CAMPFIRE = registerBuff("campfire", new CampfireBuff());
      SHADOWHOOD_SET = registerBuff("shadowhoodset", new ShadowHoodSetBuff());
      SPIDER_CHARGE = registerBuff("spidercharge", new SimpleModifierBuff(false, true, false, true, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.75F)}));
      HARDENED = registerBuff("hardened", new SimpleModifierBuff(false, true, false, true, new ModifierValue[]{new ModifierValue(BuffModifiers.INCOMING_DAMAGE_MOD, 0.1F)}));
      ANCIENT_FOSSIL_ACTIVE = registerBuff("ancientfossilactive", new SimpleModifierBuff(true, true, true, true, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 1.0F)}));
      SETTLER_SPRINT = registerBuff("settlersprint", new SettlerSprintBuff());
      LEATHER_DASHERS_ACTIVE = registerBuff("leatherdashersactive", new LeatherDashersActiveBuff());
      ZEPHYR_BOOTS_ACTIVE = registerBuff("zephyrbootsactive", new ZephyrBootsActiveBuff());
      FOW_ACTIVE = registerBuff("fowactive", new ForceOfWindActiveBuff());
      GUARDIAN_SHELL_ACTIVE = registerBuff("guardianshellactive", new GuardianShellActiveBuff());
      SLIME_DOME_ACTIVE = registerBuff("slimedomeactive", new SlimeDomeActiveBuff());
      MYCELIUM_HOOD_ACTIVE = registerBuff("myceliumhoodactive", new MyceliumHoodActiveBuff());
      MYCELIUM_SCARF_ACTIVE = registerBuff("myceliumscarfactive", new MyceliumScarfActiveBuff());
      NIGHTSTEEL_ACTIVE = registerBuff("nightsteelactive", new NightsteelActiveBuff());
      SHIELD_ACTIVE = registerBuff("shieldActive", new ShieldActiveBuff());
      SLEEPING = registerBuff("sleeping", new SleepingBuff());
      EQUIPMENT_BUFF = (StatsBuff)registerBuff("equipmentbuff", new StatsBuff());
      FOOD_BUFF = (FoodBuff)registerBuff("foodbuff", new FoodBuff(true));
      FOOD_DEBUFF = (FoodBuff)registerBuff("fooddebuff", new FoodBuff(false));
      HUNGRY_BUFF = registerBuff("hungry", new HungryBuff());
      STARVING_BUFF = registerBuff("starving", new StarvingBuff());
      STAMINA_BUFF = registerBuff("stamina", new StaminaBuff());
      SETTLER_STATS_BUFF = (StatsBuff)registerBuff("settlerstats", new StatsBuff());
      BLOODPLATE_COWL_ACTIVE = registerBuff("bloodplatecowlactive", new BloodplateCowlActiveBuff());
      BLOOD_CLAW_STACKS_BUFF = registerBuff("bloodclaw", new BloodClawStacksBuff());
      BLOODSTONE_RING_REGEN_ACTIVE_BUFF = registerBuff("bloodstonering", new BloodstoneRingRegenActiveBuff());
      STAR_BARRIER_BUFF = registerBuff("starbarrier", new StarBarrierBuff());
   }

   protected void onRegister(BuffRegistryElement<?> var1, int var2, String var3, boolean var4) {
      if (var1.buff instanceof SetBonusBuff) {
         ++totalSetBonuses;
      }

   }

   protected void onRegistryClose() {
      instance.streamElements().map((var0) -> {
         return var0.buff;
      }).forEach(Buff::updateLocalDisplayName);
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         BuffRegistryElement var2 = (BuffRegistryElement)var1.next();
         var2.buff.onBuffRegistryClosed();
      }

   }

   public static <T extends Buff> T registerBuff(String var0, T var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register buffs");
      } else {
         return ((BuffRegistryElement)instance.registerObj(var0, new BuffRegistryElement(var1))).buff;
      }
   }

   public static List<Buff> getBuffs() {
      return (List)instance.streamElements().map((var0) -> {
         return var0.buff;
      }).collect(Collectors.toList());
   }

   public static Buff getBuff(int var0) {
      return ((BuffRegistryElement)instance.getElement(var0)).buff;
   }

   public static Buff getBuff(String var0) {
      BuffRegistryElement var1 = (BuffRegistryElement)instance.getElement(var0);
      return var1 == null ? null : var1.buff;
   }

   public static int getBuffID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getBuffIDRaw(String var0) throws NoSuchElementException {
      return instance.getElementIDRaw(var0);
   }

   public static String getBuffStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static int getTotalSetBonuses() {
      return totalSetBonuses;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((BuffRegistryElement)var1, var2, var3, var4);
   }

   public static class Potions {
      public static Buff STINKFLASK;

      public Potions() {
      }
   }

   public static class Debuffs {
      public static Buff HEALTH_POT_FATIGUE;
      public static Buff MANA_POT_FATIGUE;
      public static Buff MANA_EXHAUSTION;
      public static Buff ON_FIRE;
      public static Buff ABLAZE;
      public static Buff BROKEN_ARMOR;
      public static Buff TELEPORT_SICKNESS;
      public static Buff LIFELINE_COOLDOWN;
      public static Buff QUARTZ_SET_COOLDOWN;
      public static Buff VOID_SET_COOLDOWN;
      public static Buff DASH_COOLDOWN;
      public static Buff SPIDER_WEB_SLOW;
      public static Buff WEB_POTION_SLOW;
      public static Buff SPIDER_VENOM;
      public static Buff IVY_POISON;
      public static Buff WIDOW_POISON;
      public static Buff CHILLED;
      public static Buff FREEZING;
      public static Buff FROSTBURN;
      public static Buff FROSTSLOW;
      public static Buff HAUNTED;
      public static Buff GUARDIAN_SHELL_COOLDOWN;
      public static Buff ANCIENT_FOSSIL_SET_COOLDOWN;
      public static Buff SLIME_SET_COOLDOWN;
      public static Buff NIGHTSTEEL_SET_COOLDOWN;
      public static Buff SPIDERITE_SET_COOLDOWN;
      public static Buff SPIDER_CHARM_POISON;
      public static Buff SLIME_POISON;
      public static Buff SAND_KNIFE_WOUND_BUFF;
      public static Buff BLOOD_GRIMOIRE_MARKED_DEBUFF;
      public static Buff SLIME_GREATBOW_SLOW_TARGET_DEBUFF;
      public static Buff BLOOD_GRIMOIRE_COOLDOWN;
      public static Buff THE_CRIMSON_SKY_COOLDOWN;
      public static Buff CRUSHING_DARKNESS;
      public static Buff SNOW_COVERED_DEBUFF;
      public static Buff SNOW_COVERED_SLOW_DEBUFF;

      public Debuffs() {
      }
   }

   public static class SetBonuses {
      public static SetBonusBuff LEATHER;
      public static SetBonusBuff CLOTH_ROBE;
      public static SetBonusBuff COPPER;
      public static SetBonusBuff IRON;
      public static SetBonusBuff GOLD_CROWN;
      public static SetBonusBuff GOLD;
      public static SetBonusBuff SPIDER;
      public static SetBonusBuff FROST_HELMET;
      public static SetBonusBuff FROST_HOOD;
      public static SetBonusBuff FROST_HAT;
      public static SetBonusBuff DEMONIC;
      public static SetBonusBuff VOIDHAT;
      public static SetBonusBuff VOIDMASK;
      public static SetBonusBuff NINJA;
      public static SetBonusBuff IVYHELMET;
      public static SetBonusBuff IVYHOOD;
      public static SetBonusBuff IVYHAT;
      public static SetBonusBuff IVYCIRCLET;
      public static SetBonusBuff QUARTZHELMET;
      public static SetBonusBuff QUARTZCROWN;
      public static SetBonusBuff TUNGSTEN;
      public static SetBonusBuff SHADOWHOOD;
      public static SetBonusBuff SHADOWHAT;
      public static SetBonusBuff GLACIALHELMET;
      public static SetBonusBuff GLACIALCIRCLET;
      public static SetBonusBuff MYCELIUMHOOD;
      public static SetBonusBuff MYCELIUMSCARF;
      public static SetBonusBuff WIDOW;
      public static SetBonusBuff ANCIENT_FOSSIL_HELMET;
      public static SetBonusBuff ANCIENT_FOSSIL_MASK;
      public static SetBonusBuff SLIMEHELMET;
      public static SetBonusBuff SLIMEHAT;
      public static SetBonusBuff NIGHTSTEEL;
      public static SetBonusBuff NIGHTSTEEL_HELMET;
      public static SetBonusBuff NIGHTSTEEL_VEIL;
      public static SetBonusBuff NIGHTSTEEL_CIRCLET;
      public static SetBonusBuff BLOODPLATE_COWL;
      public static SetBonusBuff SPIDERITE;
      public static SetBonusBuff SPIDERITE_HELMET;
      public static SetBonusBuff SPIDERITE_HAT;
      public static SetBonusBuff SPIDERITE_CROWN;
      public static SetBonusBuff RAVENLORDS_HEADDRESS;
      public static SetBonusBuff DAWN;
      public static SetBonusBuff DUSK;

      public SetBonuses() {
      }
   }

   public static class Banners {
      public static Buff SPEED;
      public static Buff DAMAGE;
      public static Buff SUMMON_SPEED;
      public static Buff DEFENSE;

      public Banners() {
      }
   }

   protected static class BuffRegistryElement<T extends Buff> implements IDDataContainer {
      public final T buff;

      public BuffRegistryElement(T var1) {
         this.buff = var1;
      }

      public IDData getIDData() {
         return this.buff.idData;
      }
   }
}
