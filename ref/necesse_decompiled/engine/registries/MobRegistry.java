package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.HumanTextureFull;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.ProjectileHitboxMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.friendly.CowMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.PenguinMob;
import necesse.entity.mobs.friendly.PigMob;
import necesse.entity.mobs.friendly.PolarBearMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.mobs.friendly.SheepMob;
import necesse.entity.mobs.friendly.WildOstrichMob;
import necesse.entity.mobs.friendly.critters.BirdMob;
import necesse.entity.mobs.friendly.critters.BluebirdMob;
import necesse.entity.mobs.friendly.critters.CanaryBirdMob;
import necesse.entity.mobs.friendly.critters.CardinalBirdMob;
import necesse.entity.mobs.friendly.critters.CrabMob;
import necesse.entity.mobs.friendly.critters.DuckMob;
import necesse.entity.mobs.friendly.critters.FrogMob;
import necesse.entity.mobs.friendly.critters.MouseMob;
import necesse.entity.mobs.friendly.critters.RabbitMob;
import necesse.entity.mobs.friendly.critters.ScorpionMob;
import necesse.entity.mobs.friendly.critters.SnowHareMob;
import necesse.entity.mobs.friendly.critters.SpiderCritterMob;
import necesse.entity.mobs.friendly.critters.SquirrelMob;
import necesse.entity.mobs.friendly.critters.SwampSlugMob;
import necesse.entity.mobs.friendly.critters.TurtleMob;
import necesse.entity.mobs.friendly.critters.caveling.DeepSandStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DeepSnowStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DeepStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DeepSwampStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.Flameling;
import necesse.entity.mobs.friendly.critters.caveling.SandStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.SnowStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.StoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.SwampStoneCaveling;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.entity.mobs.friendly.human.GenericHumanMob;
import necesse.entity.mobs.friendly.human.GuardHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AlchemistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AnglerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AnimalKeeperHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.FarmerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.GunsmithHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HunterHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.PawnBrokerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.PirateHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.TravelingMerchantMob;
import necesse.entity.mobs.hostile.AncientArmoredSkeletonMob;
import necesse.entity.mobs.hostile.AncientSkeletonMageMob;
import necesse.entity.mobs.hostile.AncientSkeletonMob;
import necesse.entity.mobs.hostile.AncientSkeletonThrowerMob;
import necesse.entity.mobs.hostile.BlackCaveSpiderMob;
import necesse.entity.mobs.hostile.BloatedSpiderMob;
import necesse.entity.mobs.hostile.CaveMoleMob;
import necesse.entity.mobs.hostile.CrawlingZombieMob;
import necesse.entity.mobs.hostile.CryoFlakeMob;
import necesse.entity.mobs.hostile.CryptBatMob;
import necesse.entity.mobs.hostile.CryptVampireMob;
import necesse.entity.mobs.hostile.DeepCaveSpiritMob;
import necesse.entity.mobs.hostile.DesertCrawlerMob;
import necesse.entity.mobs.hostile.EnchantedCrawlingZombieMob;
import necesse.entity.mobs.hostile.EnchantedZombieArcherMob;
import necesse.entity.mobs.hostile.EnchantedZombieMob;
import necesse.entity.mobs.hostile.FrostSentryMob;
import necesse.entity.mobs.hostile.FrozenDwarfMob;
import necesse.entity.mobs.hostile.GhostSlimeMob;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.mobs.hostile.GiantSwampSlimeMob;
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.entity.mobs.hostile.HumanRaiderMob;
import necesse.entity.mobs.hostile.IncursionCrawlingZombieMob;
import necesse.entity.mobs.hostile.JackalMob;
import necesse.entity.mobs.hostile.LeggedSlimeThrowerMob;
import necesse.entity.mobs.hostile.MageSlimeMob;
import necesse.entity.mobs.hostile.MummyMageMob;
import necesse.entity.mobs.hostile.MummyMob;
import necesse.entity.mobs.hostile.NinjaMob;
import necesse.entity.mobs.hostile.PhantomMob;
import necesse.entity.mobs.hostile.SandSpiritMob;
import necesse.entity.mobs.hostile.SandwormBody;
import necesse.entity.mobs.hostile.SandwormHead;
import necesse.entity.mobs.hostile.SandwormTail;
import necesse.entity.mobs.hostile.SkeletonMinerMob;
import necesse.entity.mobs.hostile.SkeletonMob;
import necesse.entity.mobs.hostile.SkeletonThrowerMob;
import necesse.entity.mobs.hostile.SlimeWormBody;
import necesse.entity.mobs.hostile.SlimeWormHead;
import necesse.entity.mobs.hostile.SmallCaveSpiderMob;
import necesse.entity.mobs.hostile.SnowWolfMob;
import necesse.entity.mobs.hostile.SpiderkinArcherMob;
import necesse.entity.mobs.hostile.SpiderkinMageMob;
import necesse.entity.mobs.hostile.SpiderkinMob;
import necesse.entity.mobs.hostile.SpiderkinWarriorMob;
import necesse.entity.mobs.hostile.SwampCaveSpiderMob;
import necesse.entity.mobs.hostile.SwampDwellerMob;
import necesse.entity.mobs.hostile.SwampShooterMob;
import necesse.entity.mobs.hostile.SwampSkeletonMob;
import necesse.entity.mobs.hostile.SwampSlimeMob;
import necesse.entity.mobs.hostile.SwampZombieMob;
import necesse.entity.mobs.hostile.TrapperZombieMob;
import necesse.entity.mobs.hostile.VampireMob;
import necesse.entity.mobs.hostile.VoidApprentice;
import necesse.entity.mobs.hostile.WarriorSlimeMob;
import necesse.entity.mobs.hostile.WebSpinnerMob;
import necesse.entity.mobs.hostile.ZombieArcherMob;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.entity.mobs.hostile.bosses.AncientVultureEggMob;
import necesse.entity.mobs.hostile.bosses.AncientVultureMob;
import necesse.entity.mobs.hostile.bosses.BossSpawnPortalMob;
import necesse.entity.mobs.hostile.bosses.CryoQueenMob;
import necesse.entity.mobs.hostile.bosses.EvilsPortalMob;
import necesse.entity.mobs.hostile.bosses.EvilsProtectorMob;
import necesse.entity.mobs.hostile.bosses.FallenDragonBody;
import necesse.entity.mobs.hostile.bosses.FallenDragonHead;
import necesse.entity.mobs.hostile.bosses.FallenWizardGhostMob;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsBody;
import necesse.entity.mobs.hostile.bosses.GritHead;
import necesse.entity.mobs.hostile.bosses.HomePortalMob;
import necesse.entity.mobs.hostile.bosses.MoonlightDancerMob;
import necesse.entity.mobs.hostile.bosses.MotherSlimeMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmStartMob;
import necesse.entity.mobs.hostile.bosses.PestWardenBody;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.entity.mobs.hostile.bosses.PortalMinion;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.entity.mobs.hostile.bosses.ReaperSpiritMob;
import necesse.entity.mobs.hostile.bosses.ReaperSpiritPortalMob;
import necesse.entity.mobs.hostile.bosses.ReturnPortalMob;
import necesse.entity.mobs.hostile.bosses.SageAndGritStartMob;
import necesse.entity.mobs.hostile.bosses.SageHead;
import necesse.entity.mobs.hostile.bosses.SpiderEmpressMob;
import necesse.entity.mobs.hostile.bosses.SpiderHatchlingMob;
import necesse.entity.mobs.hostile.bosses.SunlightChampionMob;
import necesse.entity.mobs.hostile.bosses.SwampGuardianBody;
import necesse.entity.mobs.hostile.bosses.SwampGuardianHead;
import necesse.entity.mobs.hostile.bosses.SwampGuardianTail;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.mobs.hostile.bosses.VoidWizardClone;
import necesse.entity.mobs.hostile.bosses.VultureHatchling;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.entity.mobs.hostile.pirates.PirateParrotMob;
import necesse.entity.mobs.hostile.pirates.PirateRecruit;
import necesse.entity.mobs.summon.MinecartMob;
import necesse.entity.mobs.summon.SawBladeMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMageMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySnowmanMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySpiderMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySpiderkinArcher;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySpiderkinWarrior;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabyZombieArcherMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabyZombieMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ChargingPhantomFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.CryoFlakeFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DuskMoonDiscFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FrostPiercerFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.OrbOfSlimesFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.PoisonSlimeFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.PouncingSlimeFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RavenLordFeatherFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ReaperSpiritFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.SlimeGreatswordFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.VultureHatchlingFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.HoverboardMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.JumpingBallMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MinecartMountMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.SteelBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.TameOstrichMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.WoodBoatMountMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetCavelingElder;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetParrotMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetPenguinMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetWalkingTorchMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class MobRegistry extends ClassedGameRegistry<Mob, MobRegistryElement> {
   public static final MobRegistry instance = new MobRegistry();

   private MobRegistry() {
      super("Mob", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "mobs"));
      registerMob("sheep", SheepMob.class, true);
      registerMob("wildostrich", WildOstrichMob.class, true);
      registerMob("cow", CowMob.class, true);
      registerMob("pig", PigMob.class, true);
      registerMob("honeybee", HoneyBeeMob.class, true, false, new LocalMessage("item", "honeybee"), (GameMessage)null);
      registerMob("queenbee", QueenBeeMob.class, true, false, new LocalMessage("item", "queenbee"), (GameMessage)null);
      registerMob("penguin", PenguinMob.class, true);
      registerMob("polarbear", PolarBearMob.class, true);
      registerMob("rabbit", RabbitMob.class, true);
      registerMob("squirrel", SquirrelMob.class, true);
      registerMob("snowhare", SnowHareMob.class, true);
      registerMob("crab", CrabMob.class, true);
      registerMob("scorpion", ScorpionMob.class, true);
      registerMob("turtle", TurtleMob.class, true);
      registerMob("swampslug", SwampSlugMob.class, true);
      registerMob("frog", FrogMob.class, true);
      registerMob("duck", DuckMob.class, true);
      registerMob("bird", BirdMob.class, true);
      registerMob("bluebird", BluebirdMob.class, true);
      registerMob("canarybird", CanaryBirdMob.class, true);
      registerMob("cardinalbird", CardinalBirdMob.class, true);
      registerMob("spider", SpiderCritterMob.class, true);
      registerMob("mouse", MouseMob.class, true);
      registerMob("stonecaveling", StoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("snowstonecaveling", SnowStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("swampstonecaveling", SwampStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("sandstonecaveling", SandStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("deepstonecaveling", DeepStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("deepsnowstonecaveling", DeepSnowStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("deepswampstonecaveling", DeepSwampStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("deepsandstonecaveling", DeepSandStoneCaveling.class, true, false, new LocalMessage("mob", "caveling"), (GameMessage)null);
      registerMob("flameling", Flameling.class, true, false, new LocalMessage("mob", "flameling"), (GameMessage)null);
      registerMob("human", GenericHumanMob.class, true);
      registerMob("farmerhuman", FarmerHumanMob.class, true);
      registerMob("blacksmithhuman", BlacksmithHumanMob.class, true);
      registerMob("guardhuman", GuardHumanMob.class, true);
      registerMob("magehuman", MageHumanMob.class, true);
      registerMob("gunsmithhuman", GunsmithHumanMob.class, true);
      registerMob("alchemisthuman", AlchemistHumanMob.class, true);
      registerMob("hunterhuman", HunterHumanMob.class, true);
      registerMob("elderhuman", ElderHumanMob.class, true);
      registerMob("anglerhuman", AnglerHumanMob.class, true);
      registerMob("pawnbrokerhuman", PawnBrokerHumanMob.class, true);
      registerMob("animalkeeperhuman", AnimalKeeperHumanMob.class, true);
      registerMob("stylisthuman", StylistHumanMob.class, true);
      registerMob("piratehuman", PirateHumanMob.class, true);
      registerMob("explorerhuman", ExplorerHumanMob.class, true);
      registerMob("minerhuman", MinerHumanMob.class, true);
      registerMob("travelingmerchant", TravelingMerchantMob.class, true);
      registerMob("zombie", ZombieMob.class, true);
      registerMob("trapperzombie", TrapperZombieMob.class, true);
      registerMob("goblin", GoblinMob.class, true);
      registerMob("vampire", VampireMob.class, true);
      registerMob("zombiearcher", ZombieArcherMob.class, true);
      registerMob("crawlingzombie", CrawlingZombieMob.class, true);
      registerMob("giantcavespider", GiantCaveSpiderMob.class, true);
      registerMob("blackcavespider", BlackCaveSpiderMob.class, true);
      registerMob("swampcavespider", SwampCaveSpiderMob.class, true);
      registerMob("cavemole", CaveMoleMob.class, true);
      registerMob("frozendwarf", FrozenDwarfMob.class, true);
      registerMob("frostsentry", FrostSentryMob.class, true);
      registerMob("swampzombie", SwampZombieMob.class, true);
      registerMob("swampslime", SwampSlimeMob.class, true);
      registerMob("swampshooter", SwampShooterMob.class, true);
      registerMob("enchantedzombie", EnchantedZombieMob.class, true);
      registerMob("enchantedzombiearcher", EnchantedZombieArcherMob.class, true);
      registerMob("enchantedcrawlingzombie", EnchantedCrawlingZombieMob.class, true);
      registerMob("voidapprentice", VoidApprentice.class, true);
      registerMob("mummy", MummyMob.class, true);
      registerMob("mummymage", MummyMageMob.class, true);
      registerMob("sandspirit", SandSpiritMob.class, true);
      registerMob("jackal", JackalMob.class, true);
      registerMob("skeleton", SkeletonMob.class, true);
      registerMob("skeletonthrower", SkeletonThrowerMob.class, true);
      registerMob("deepcavespirit", DeepCaveSpiritMob.class, true);
      registerMob("skeletonminer", SkeletonMinerMob.class, true);
      registerMob("ninja", NinjaMob.class, true);
      registerMob("snowwolf", SnowWolfMob.class, true);
      registerMob("cryoflake", CryoFlakeMob.class, true);
      registerMob("giantswampslime", GiantSwampSlimeMob.class, true);
      registerMob("swampskeleton", SwampSkeletonMob.class, true);
      registerMob("smallswampcavespider", SmallCaveSpiderMob.class, true);
      registerMob("swampdweller", SwampDwellerMob.class, true);
      registerMob("sandworm", SandwormHead.class, true);
      registerMob("sandwormbody", SandwormBody.class, false);
      registerMob("sandwormtail", SandwormTail.class, false);
      registerMob("desertcrawler", DesertCrawlerMob.class, true);
      registerMob("ancientskeleton", AncientSkeletonMob.class, true);
      registerMob("ancientskeletonthrower", AncientSkeletonThrowerMob.class, true);
      registerMob("ancientarmoredskeleton", AncientArmoredSkeletonMob.class, true);
      registerMob("ancientskeletonmage", AncientSkeletonMageMob.class, true);
      registerMob("leggedslimethrower", LeggedSlimeThrowerMob.class, true);
      registerMob("mageslime", MageSlimeMob.class, true);
      registerMob("ghostslime", GhostSlimeMob.class, true);
      registerMob("warriorslime", WarriorSlimeMob.class, true);
      registerMob("slimeworm", SlimeWormHead.class, true);
      registerMob("slimewormbody", SlimeWormBody.class, false);
      registerMob("cryptbat", CryptBatMob.class, true);
      registerMob("phantom", PhantomMob.class, true);
      registerMob("cryptvampire", CryptVampireMob.class, true);
      registerMob("webspinner", WebSpinnerMob.class, true);
      registerMob("bloatedspider", BloatedSpiderMob.class, true);
      registerMob("spiderkin", SpiderkinMob.class, true);
      registerMob("spiderkinwarrior", SpiderkinWarriorMob.class, true);
      registerMob("spiderkinarcher", SpiderkinArcherMob.class, true);
      registerMob("spiderkinmage", SpiderkinMageMob.class, true);
      registerMob("humanraider", HumanRaiderMob.class, true);
      registerMob("tameostrich", TameOstrichMob.class, false);
      registerMob("petpenguin", PetPenguinMob.class, false);
      registerMob("petparrot", PetParrotMob.class, false);
      registerMob("minecartmount", MinecartMountMob.class, false, false, new LocalMessage("mob", "minecart"), (GameMessage)null);
      registerMob("woodboatmount", WoodBoatMountMob.class, false, false, new LocalMessage("mob", "woodboat"), (GameMessage)null);
      registerMob("steelboat", SteelBoatMob.class, false);
      registerMob("petwalkingtorch", PetWalkingTorchMob.class, false);
      registerMob("jumpingball", JumpingBallMob.class, false);
      registerMob("hoverboard", HoverboardMob.class, false);
      registerMob("petcavelingelder", PetCavelingElder.class, false);
      registerMob("babyzombie", BabyZombieMob.class, false);
      registerMob("babyzombiearcher", BabyZombieArcherMob.class, false);
      registerMob("babyspider", BabySpiderMob.class, false);
      registerMob("frostpiercer", FrostPiercerFollowingMob.class, false);
      registerMob("babysnowman", BabySnowmanMob.class, false);
      registerMob("playerpoisonslime", PoisonSlimeFollowingMob.class, false);
      registerMob("playervulturehatchling", VultureHatchlingFollowingMob.class, false);
      registerMob("playerreaperspirit", ReaperSpiritFollowingMob.class, false);
      registerMob("playercryoflake", CryoFlakeFollowingMob.class, false);
      registerMob("playerpouncingslime", PouncingSlimeFollowingMob.class, false);
      registerMob("slimegreatswordslime", SlimeGreatswordFollowingMob.class, false);
      registerMob("babyskeleton", BabySkeletonMob.class, false);
      registerMob("babyskeletonmage", BabySkeletonMageMob.class, false);
      registerMob("playerchargingphantom", ChargingPhantomFollowingMob.class, false);
      registerMob("orbofslimesslime", OrbOfSlimesFollowingMob.class, false);
      registerMob("babyspiderkinwarrior", BabySpiderkinWarrior.class, false);
      registerMob("babyspiderkinarcher", BabySpiderkinArcher.class, false);
      registerMob("ravenlordfeather", RavenLordFeatherFollowingMob.class, false);
      registerMob("duskmoondisc", DuskMoonDiscFollowingMob.class, false);
      registerMob("evilsprotector", EvilsProtectorMob.class, true, true, new LocalMessage("quests", "evilsprotectortip"));
      registerMob("evilsportal", EvilsPortalMob.class, true);
      registerMob("portalminion", PortalMinion.class, true);
      registerMob("queenspider", QueenSpiderMob.class, true, true, new LocalMessage("quests", "queenspidertip"));
      registerMob("spiderhatchling", SpiderHatchlingMob.class, true);
      registerMob("voidwizard", VoidWizard.class, true, true, new LocalMessage("quests", "voidwizardtip"));
      registerMob("voidwizardclone", VoidWizardClone.class, false, false, new LocalMessage("mob", "voidwizard"), (GameMessage)null);
      registerMob("swampguardian", SwampGuardianHead.class, true, true, new LocalMessage("quests", "swampguardiantip"));
      registerMob("swampguardianbody", SwampGuardianBody.class, false, true);
      registerMob("swampguardiantail", SwampGuardianTail.class, false, true);
      registerMob("ancientvulture", AncientVultureMob.class, true, true, new LocalMessage("quests", "ancientvulturetip"));
      registerMob("ancientvultureegg", AncientVultureEggMob.class, true);
      registerMob("vulturehatchling", VultureHatchling.class, true);
      registerMob("piratecaptain", PirateCaptainMob.class, true, true, new LocalMessage("quests", "piratecaptaintip"));
      registerMob("piraterecruit", PirateRecruit.class, true);
      registerMob("pirateparrot", PirateParrotMob.class, true);
      registerMob("reaper", ReaperMob.class, true, true, new LocalMessage("quests", "reapertip"));
      registerMob("reaperspiritportal", ReaperSpiritPortalMob.class, true);
      registerMob("reaperspirit", ReaperSpiritMob.class, true);
      registerMob("cryoqueen", CryoQueenMob.class, true, true, new LocalMessage("quests", "cryoqueentip"));
      registerMob("pestwarden", PestWardenHead.class, true, true, new LocalMessage("quests", "pestwardentip"));
      registerMob("pestwardenbody", PestWardenBody.class, true, true);
      registerMob("grit", GritHead.class, true, true);
      registerMob("sage", SageHead.class, true, true);
      registerMob("flyingspiritsbody", FlyingSpiritsBody.class, false, true);
      registerMob("sageandgrit", SageAndGritStartMob.class, true, false, new LocalMessage("quests", "sageandgrittip"));
      registerMob("fallenwizard", FallenWizardMob.class, true, true, new LocalMessage("quests", "fallenwizardtip"));
      registerMob("fallenwizardghost", FallenWizardGhostMob.class, false, false, new LocalMessage("mob", "fallenwizard"), (GameMessage)null);
      registerMob("fallendragon", FallenDragonHead.class, true);
      registerMob("fallendragonbody", FallenDragonBody.class, false);
      registerMob("motherslime", MotherSlimeMob.class, true, true);
      registerMob("nightswarm", NightSwarmStartMob.class, true, true);
      registerMob("nightswarmbat", NightSwarmBatMob.class, true);
      registerMob("spiderempress", SpiderEmpressMob.class, true, true);
      registerMob("sunlightchampion", SunlightChampionMob.class, true, true);
      registerMob("sunlightgauntlet", SunlightChampionMob.SunlightGauntletMob.class, false, false);
      registerMob("moonlightdancer", MoonlightDancerMob.class, true, true);
      registerMob("incursioncrawlingzombie", IncursionCrawlingZombieMob.class, true);
      registerMob("woodboat", WoodBoatMob.class, false);
      registerMob("minecart", MinecartMob.class, false);
      registerMob("sawblade", SawBladeMob.class, false);
      registerMob("trainingdummy", TrainingDummyMob.class, false, false, new LocalMessage("object", "trainingdummy"), (GameMessage)null);
      registerMob("projectilehitbox", ProjectileHitboxMob.class, false, false, new StaticMessage("PROJECTILE_HITBOX"), (GameMessage)null);
      registerMob("homeportal", HomePortalMob.class, false);
      registerMob("returnportal", ReturnPortalMob.class, false);
      registerMob("bossspawnportal", BossSpawnPortalMob.class, false);
   }

   protected void onRegistryClose() {
   }

   public static GameMessage getLocalization(int var0) {
      return (GameMessage)(var0 == -1 ? new StaticMessage("N/A") : ((MobRegistryElement)instance.getElement(var0)).displayName);
   }

   public static GameMessage getLocalization(String var0) {
      return getLocalization(getMobID(var0));
   }

   public static String getDisplayName(int var0) {
      return var0 == -1 ? null : getLocalization(var0).translate();
   }

   public static GameMessage getKillHintLocalization(int var0) {
      return var0 == -1 ? null : ((MobRegistryElement)instance.getElement(var0)).killHint;
   }

   public static GameMessage getKillHintLocalization(String var0) {
      return getKillHintLocalization(getMobID(var0));
   }

   public static String getKillHint(int var0) {
      GameMessage var1 = getKillHintLocalization(var0);
      return var1 != null ? var1.translate() : null;
   }

   public static int registerMob(String var0, Class<? extends Mob> var1, boolean var2) {
      return registerMob(var0, var1, var2, false);
   }

   public static int registerMob(String var0, Class<? extends Mob> var1, boolean var2, boolean var3) {
      return registerMob(var0, var1, var2, var3, new LocalMessage("mob", var0), (GameMessage)null);
   }

   public static int registerMob(String var0, Class<? extends Mob> var1, boolean var2, boolean var3, GameMessage var4) {
      return registerMob(var0, var1, var2, var3, new LocalMessage("mob", var0), var4);
   }

   public static int registerMob(String var0, Class<? extends Mob> var1, boolean var2, boolean var3, GameMessage var4, GameMessage var5) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register mobs");
      } else {
         try {
            return instance.register(var0, new MobRegistryElement(var1, var2, var3, var4, var5));
         } catch (NoSuchMethodException var7) {
            System.err.println("Could not register mob " + var1.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static Mob getMob(int var0) {
      try {
         return (Mob)((MobRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Mob getMob(int var0, Level var1) {
      try {
         Mob var2 = (Mob)((MobRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
         if (var2 != null) {
            var2.onConstructed(var1);
         }

         return var2;
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public static Mob getMob(String var0) {
      return getMob(getMobID(var0));
   }

   public static Mob getMob(String var0, Level var1) {
      return getMob(getMobID(var0), var1);
   }

   public static int getMobID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getMobID(Class<? extends Mob> var0) {
      return instance.getElementID(var0);
   }

   public static boolean mobExists(String var0) {
      try {
         return instance.getElementIDRaw(var0) >= 0;
      } catch (NoSuchElementException var2) {
         return false;
      }
   }

   public static String getMobStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static boolean countMobKillStat(int var0) {
      return var0 >= 0 && var0 < instance.size() ? ((MobRegistryElement)instance.getElement(var0)).countKillStat : false;
   }

   public static boolean isBossMob(int var0) {
      return var0 >= 0 && var0 < instance.size() ? ((MobRegistryElement)instance.getElement(var0)).isBossMob : false;
   }

   public static Stream<IDDataContainer> streamMobs() {
      return instance.streamElements().map((var0) -> {
         return var0;
      });
   }

   public static List<IDDataContainer> getMobs() {
      return (List)streamMobs().collect(Collectors.toList());
   }

   protected static class MobRegistryElement extends ClassIDDataContainer<Mob> {
      public boolean countKillStat;
      public boolean isBossMob;
      public GameMessage displayName;
      public GameMessage killHint;

      public MobRegistryElement(Class<? extends Mob> var1, boolean var2, boolean var3, GameMessage var4, GameMessage var5) throws NoSuchMethodException {
         super(var1);
         this.countKillStat = var2;
         this.isBossMob = var3;
         this.displayName = var4;
         this.killHint = var5;
      }
   }

   public static class Textures {
      public static GameTexture human_shadow;
      public static GameTexture human_baby_shadow;
      public static GameTexture human_big_shadow;
      public static GameTexture small_shadow;
      public static GameTexture sheep;
      public static GameTexture sheep_sheared;
      public static GameTexture sheep_shadow;
      public static GameTexture lamb;
      public static GameTexture lamb_shadow;
      public static GameTexture ostrich;
      public static GameTexture ostrichMount;
      public static GameTexture cow;
      public static GameTexture cow_shadow;
      public static GameTexture calf;
      public static GameTexture calf_shadow;
      public static GameTexture pig;
      public static GameTexture pig_shadow;
      public static GameTexture piglet;
      public static GameTexture piglet_shadow;
      public static GameTexture penguin;
      public static MobTexture honeyBee;
      public static MobTexture queenBee;
      public static GameTexture rabbit;
      public static GameTexture squirrel;
      public static GameTexture snowHare;
      public static GameTexture swampSlug;
      public static GameTexture bird_shadow;
      public static GameTexture bird;
      public static GameTexture bluebird;
      public static GameTexture canaryBird;
      public static GameTexture cardinalBird;
      public static MobTexture crab;
      public static MobTexture scorpion;
      public static MobTexture turtle;
      public static MobTexture duck;
      public static MobTexture frog;
      public static MobTexture spider;
      public static MobTexture mouse;
      public static HumanTexture stoneCaveling;
      public static HumanTexture snowStoneCaveling;
      public static HumanTexture swampStoneCaveling;
      public static HumanTexture sandStoneCaveling;
      public static HumanTexture flameling;
      public static HumanTexture deepStoneCaveling;
      public static HumanTexture deepSnowStoneCaveling;
      public static HumanTexture deepSwampStoneCaveling;
      public static HumanTexture deepSandStoneCaveling;
      public static GameTexture caveling_shadow;
      public static HumanTextureFull alchemist;
      public static HumanTextureFull angler;
      public static HumanTextureFull animalKeeper;
      public static HumanTextureFull blacksmith;
      public static HumanTextureFull elder;
      public static HumanTextureFull explorer;
      public static HumanTextureFull miner;
      public static HumanTextureFull farmer;
      public static HumanTextureFull gunsmith;
      public static HumanTextureFull hunter;
      public static HumanTextureFull mage;
      public static HumanTextureFull pawnBroker;
      public static HumanTextureFull pirate;
      public static HumanTextureFull stylist;
      public static HumanTextureFull travelingMerchant;
      public static HumanTexture zombie;
      public static HumanTexture zombieArcher;
      public static HumanTexture zombieArcherWithBow;
      public static HumanTexture trapperZombie;
      public static HumanTexture swampZombie;
      public static HumanTexture enchantedZombie;
      public static HumanTexture enchantedZombieArcher;
      public static HumanTexture enchantedZombieArcherWithBow;
      public static HumanTexture ninja;
      public static HumanTexture vampire;
      public static HumanTexture frozenDwarf;
      public static HumanTexture voidApprentice;
      public static HumanTexture mummy;
      public static HumanTexture mummyMage;
      public static HumanTexture skeleton;
      public static HumanTexture skeletonMiner;
      public static GameTexture goblin;
      public static GameTexture sandSpirit;
      public static GameTexture deepCaveSpirit;
      public static GameTexture swampSlime;
      public static GameTexture swampSlime_shadow;
      public static GameTexture cryoFlake;
      public static GameTexture cryoFlakePet;
      public static MobTexture crawlingZombie;
      public static MobTexture enchantedCrawlingZombie;
      public static MobTexture giantCaveSpider;
      public static MobTexture giantSnowCaveSpider;
      public static MobTexture giantSwampCaveSpider;
      public static MobTexture smallSwampCaveSpider;
      public static MobTexture jackal;
      public static MobTexture snowWolf;
      public static GameTexture swampShooter;
      public static GameTexture mole;
      public static GameTexture frostSentry;
      public static GameTexture giantSwampSlime;
      public static GameTexture giantSwampSlime_shadow;
      public static GameTexture sandWorm;
      public static GameTexture sandWorm_mask;
      public static GameTexture sandWorm_shadow;
      public static GameTexture desertCrawler;
      public static HumanTexture swampSkeleton;
      public static HumanTexture swampDweller;
      public static HumanTexture ancientSkeleton;
      public static HumanTexture ancientArmoredSkeleton;
      public static HumanTexture ancientSkeletonMage;
      public static MobTexture leggedSlime;
      public static MobTexture mageSlime;
      public static MobTexture ghostSlime;
      public static MobTexture warriorSlime;
      public static MobTexture slimeWorm;
      public static GameTexture slimeWorm_mask;
      public static GameTexture cryptBat;
      public static MobTexture phantom;
      public static HumanTexture cryptVampire;
      public static HumanTexture spiderkin;
      public static HumanTexture spiderkinWarrior;
      public static HumanTexture spiderkinArcher;
      public static HumanTexture spiderkinMage;
      public static GameTexture spiderkin_light;
      public static GameTexture spiderkinWarrior_light;
      public static GameTexture spiderkinArcher_light;
      public static GameTexture spiderkinMage_light;
      public static MobTexture webSpinner;
      public static MobTexture webSpinner_shadow;
      public static MobTexture bloatedSpider;
      public static MobTexture bloatedSpider_shadow;
      public static GameTexture frostPiercer;
      public static GameTexture steelBoat;
      public static GameTexture walkingTorch;
      public static GameTexture reaperSpiritPet;
      public static GameTexture jumpingBall;
      public static GameTexture jumpingBall_shadow;
      public static GameTexture cavelingElder;
      public static GameTexture poisonSlime;
      public static GameTexture poisonSlime_shadow;
      public static GameTexture pouncingSlime;
      public static GameTexture pouncingSlime_shadow;
      public static GameTexture greatswordSlime;
      public static GameTexture greatswordSlime_shadow;
      public static MobTexture chargingPhantom;
      public static MobTexture orbOfSlimesSlime;
      public static MobTexture babySpider;
      public static MobTexture hoverBoard;
      public static HumanTexture babyZombie;
      public static HumanTexture babyZombieArcher;
      public static HumanTexture babySnowman;
      public static HumanTexture babySkeleton;
      public static HumanTexture babySkeletonMage;
      public static HumanTexture babySpiderkinWarrior;
      public static HumanTexture babySpiderkinArcher;
      public static GameTexture duskMoonDisc;
      public static GameTexture evilsProtector;
      public static GameTexture evilsProtector_shadow;
      public static GameTexture evilsProtector2;
      public static GameTexture evilsProtectorBomb;
      public static GameTexture evilsProtectorBomb_shadow;
      public static GameTexture portalMinion;
      public static GameTexture queenSpiderBody;
      public static GameTexture queenSpiderHead;
      public static GameTexture queenSpiderLeg;
      public static GameTexture queenSpiderDebris;
      public static GameTexture queenSpider_shadow;
      public static GameTexture queenSpiderLeg_shadow;
      public static GameTexture queenSpider_spit;
      public static MobTexture spiderHatchling;
      public static GameTexture voidWizard2;
      public static GameTexture voidWizard3;
      public static GameTexture parrot;
      public static HumanTexture voidWizard;
      public static HumanTexture pirateCaptain;
      public static HumanTexture pirateRecruit1;
      public static HumanTexture pirateRecruit2;
      public static GameTexture pirateCaptainShip;
      public static GameTexture pirateCaptainShip_shadow;
      public static GameTexture pirateCaptainShip_mask;
      public static GameTexture ancientVulture;
      public static GameTexture ancientVulture_shadow;
      public static GameTexture ancientVultureEgg;
      public static GameTexture ancientVultureEgg_shadow;
      public static GameTexture vultureHatchling;
      public static GameTexture reaper;
      public static GameTexture reaper_shadow;
      public static GameTexture reaperGlow;
      public static GameTexture reaperSpirit;
      public static GameTexture reaperSpiritPortal;
      public static GameTexture cryoQueen;
      public static GameTexture swampGuardian;
      public static GameTexture swampGuardian_shadow;
      public static GameTexture swampGuardian_mask;
      public static GameTexture pestWarden;
      public static GameTexture pestWarden_shadow;
      public static GameTexture pestWarden_mask;
      public static GameTexture flyingSpirits;
      public static HumanTexture fallenWizard;
      public static GameTexture fallenWizardDragon;
      public static GameTexture nightSwarmBat;
      public static MobTexture motherSlime;
      public static GameTexture spiderEmpressHead;
      public static GameTexture spiderEmpressTorso;
      public static GameTexture spiderEmpressDress;
      public static GameTexture spiderEmpressLegTop;
      public static GameTexture spiderEmpressLegBottom;
      public static GameTexture spiderEmpressArmTop;
      public static GameTexture spiderEmpressArmBottom;
      public static GameTexture spiderEmpressHand;
      public static GameTexture spiderEmpressDebris;
      public static GameTexture spiderEmpressRageHead;
      public static GameTexture spiderEmpressRageTorso;
      public static GameTexture spiderEmpressRageDress;
      public static GameTexture spiderEmpressRageLegTop;
      public static GameTexture spiderEmpressRageLegBottom;
      public static GameTexture spiderEmpressRageArmTop;
      public static GameTexture spiderEmpressRageArmBottom;
      public static GameTexture spiderEmpressRageHand;
      public static GameTexture sunlightChampionEye;
      public static GameTexture sunlightChampionChestplate;
      public static GameTexture sunlightChampionJet;
      public static GameTexture sunlightGauntlet;
      public static GameTexture sunlightGauntletFire;
      public static GameTexture sunlightGauntletJet;
      public static GameTexture moonlightDancer;
      public static GameTexture moonlightDancerInvincible;
      public static GameTexture moonlightDancerDebris;
      public static GameTexture moonlightDancerHead;
      public static GameTexture mound1;
      public static GameTexture mound2;
      public static GameTexture mound3;
      public static GameTexture mounds32;
      public static GameTexture mountmask;
      public static GameTexture swimmask;
      public static GameTexture boat_shadow;
      public static GameTexture woodBoat;
      public static GameTexture minecart;
      public static GameTexture minecart_shadow;
      public static GameTexture sawblade;
      public static GameTexture[] boat_mask;
      public static GameTexture[] minecart_mask;
      public static GameTexture polarBear;
      public static GameTexture polarBear_shadow;
      public static GameTexture portalSphere;
      public static GameTexture spawnSphere;
      public static GameTexture bossPortal;
      public static GameTexture ravenlords_set_feather;

      public Textures() {
      }

      public static void load() {
         human_shadow = fromFile("human_shadow");
         human_baby_shadow = fromFile("human_baby_shadow");
         human_big_shadow = fromFile("human_big_shadow");
         small_shadow = fromFile("small_shadow");
         sheep = fromFile("sheep");
         sheep_sheared = fromFile("sheep_sheared");
         sheep_shadow = fromFile("sheep_shadow");
         lamb = fromFile("lamb");
         lamb_shadow = fromFile("lamb_shadow");
         ostrich = fromFile("ostrich");
         ostrichMount = fromFile("ostrichmount");
         cow = fromFile("cow");
         cow_shadow = fromFile("cow_shadow");
         calf = fromFile("calf");
         calf_shadow = fromFile("calf_shadow");
         pig = fromFile("pig");
         pig_shadow = fromFile("pig_shadow");
         piglet = fromFile("piglet");
         piglet_shadow = fromFile("piglet_shadow");
         penguin = fromFile("penguin");
         honeyBee = fromFiles("honeybee");
         queenBee = fromFiles("queenbee");
         rabbit = fromFile("rabbit");
         squirrel = fromFile("squirrel");
         snowHare = fromFile("snowhare");
         crab = fromFiles("crab");
         scorpion = fromFiles("scorpion");
         turtle = fromFiles("turtle");
         swampSlug = fromFile("swampslug");
         frog = fromFiles("frog");
         duck = fromFiles("duck");
         bird_shadow = fromFile("bird_shadow");
         bird = fromFile("bird");
         bluebird = fromFile("bluebird");
         canaryBird = fromFile("canary");
         cardinalBird = fromFile("cardinal");
         spider = fromFiles("spider");
         mouse = fromFiles("mouse");
         stoneCaveling = new HumanTexture(fromFile("stonecaveling"), fromFile("stonecavelingarms_front"), fromFile("stonecavelingarms_back"));
         snowStoneCaveling = new HumanTexture(fromFile("snowstonecaveling"), fromFile("snowstonecavelingarms_front"), fromFile("snowstonecavelingarms_back"));
         swampStoneCaveling = new HumanTexture(fromFile("swampstonecaveling"), fromFile("swampstonecavelingarms_front"), fromFile("swampstonecavelingarms_back"));
         sandStoneCaveling = new HumanTexture(fromFile("sandstonecaveling"), fromFile("sandstonecavelingarms_front"), fromFile("sandstonecavelingarms_back"));
         deepStoneCaveling = new HumanTexture(fromFile("deepstonecaveling"), fromFile("deepstonecavelingarms_front"), fromFile("deepstonecavelingarms_back"));
         deepSnowStoneCaveling = new HumanTexture(fromFile("deepsnowstonecaveling"), fromFile("deepsnowstonecavelingarms_front"), fromFile("deepsnowstonecavelingarms_back"));
         deepSwampStoneCaveling = new HumanTexture(fromFile("deepswampstonecaveling"), fromFile("deepswampstonecavelingarms_front"), fromFile("deepswampstonecavelingarms_back"));
         deepSandStoneCaveling = new HumanTexture(fromFile("deepsandstonecaveling"), fromFile("deepsandstonecavelingarms_front"), fromFile("deepsandstonecavelingarms_back"));
         flameling = new HumanTexture(fromFile("flameling"), fromFile("flamelingarms_front"), fromFile("flamelingarms_back"));
         caveling_shadow = fromFile("caveling_shadow");
         alchemist = humanTextureFull("humans/alchemist");
         angler = humanTextureFull("humans/angler");
         animalKeeper = humanTextureFull("humans/animalkeeper");
         blacksmith = humanTextureFull("humans/blacksmith");
         elder = humanTextureFull("humans/elder");
         explorer = humanTextureFull("humans/explorer");
         miner = humanTextureFull("humans/miner");
         farmer = humanTextureFull("humans/farmer");
         gunsmith = humanTextureFull("humans/gunsmith");
         hunter = humanTextureFull("humans/hunter");
         mage = humanTextureFull("humans/mage");
         pawnBroker = humanTextureFull("humans/pawnbroker");
         pirate = humanTextureFull("humans/pirate");
         stylist = humanTextureFull("humans/stylist");
         travelingMerchant = humanTextureFull("humans/travelingmerchant");
         zombie = humanTexture("zombie");
         zombieArcher = humanTexture("zombiearcher");
         zombieArcherWithBow = humanTexture("zombiearcherbow", "zombiearcherarms");
         trapperZombie = humanTexture("trapperzombie", "zombiearms");
         swampZombie = humanTexture("swampzombie", "swampzombiearms");
         goblin = fromFile("goblin");
         mummy = humanTexture("mummy");
         mummyMage = humanTexture("mummymage");
         vampire = humanTexture("vampire");
         frozenDwarf = humanTexture("frozendwarf");
         enchantedZombie = humanTexture("enchantedzombie");
         enchantedZombieArcher = humanTexture("enchantedzombiearcher");
         enchantedZombieArcherWithBow = humanTexture("enchantedzombiearcherbow", "enchantedzombiearms");
         ninja = humanTexture("ninja");
         voidApprentice = humanTexture("voidapprentice");
         sandSpirit = fromFile("sandspirit");
         skeleton = humanTexture("skeleton");
         deepCaveSpirit = fromFile("deepcavespirit");
         skeletonMiner = humanTexture("skeletonminer");
         swampSlime = fromFile("swampslime");
         swampSlime_shadow = fromFile("swampslime_shadow");
         crawlingZombie = fromFiles("crawlingzombie");
         enchantedCrawlingZombie = fromFiles("enchantedcrawlingzombie", "crawlingzombie_shadow");
         giantCaveSpider = fromFiles("giantcavespider", "giantspider_shadow");
         giantSnowCaveSpider = fromFiles("giantsnowcavespider", "giantspider_shadow");
         giantSwampCaveSpider = fromFiles("giantswampcavespider", "giantspider_shadow");
         smallSwampCaveSpider = fromFiles("smallswampcavespider", "smallswampcavespider_shadow");
         jackal = fromFiles("jackal", "wolf_shadow");
         snowWolf = fromFiles("snowwolf", "wolf_shadow");
         cryoFlake = fromFile("cryoflake");
         swampShooter = fromFile("swampshooter");
         mole = fromFile("mole");
         frostSentry = fromFile("frostsentry");
         giantSwampSlime = fromFile("giantswampslime");
         giantSwampSlime_shadow = fromFile("giantswampslime_shadow");
         swampDweller = humanTexture("swampdweller");
         sandWorm = fromFile("sandworm");
         sandWorm_shadow = fromFile("sandworm_shadow");
         sandWorm_mask = fromFile("sandworm_mask");
         desertCrawler = fromFile("desertcrawler");
         swampSkeleton = humanTexture("swampskeleton");
         ancientSkeleton = humanTexture("ancientskeleton");
         ancientArmoredSkeleton = humanTexture("ancientarmoredskeleton");
         ancientSkeletonMage = humanTexture("ancientskeletonmage");
         leggedSlime = fromFiles("leggedslime");
         mageSlime = fromFiles("mageslime");
         ghostSlime = fromFiles("ghostslime");
         warriorSlime = fromFiles("warriorslime");
         slimeWorm = fromFiles("slimeworm");
         slimeWorm_mask = fromFile("slimeworm_mask");
         cryptBat = fromFile("cryptbat");
         phantom = fromFiles("phantom");
         cryptVampire = humanTexture("cryptvampire");
         spiderkin = humanTexture("spiderkin/spiderkin");
         spiderkinWarrior = humanTexture("spiderkin/spiderkinwarrior");
         spiderkinArcher = humanTexture("spiderkin/spiderkinarcher");
         spiderkinMage = humanTexture("spiderkin/spiderkinmage");
         spiderkin_light = fromFile("spiderkin/spiderkin_light");
         spiderkinWarrior_light = fromFile("spiderkin/spiderkinwarrior_light");
         spiderkinArcher_light = fromFile("spiderkin/spiderkinarcher_light");
         spiderkinMage_light = fromFile("spiderkin/spiderkinmage_light");
         webSpinner = fromFiles("webspinner");
         webSpinner_shadow = fromFiles("webspinner_shadow");
         bloatedSpider = fromFiles("bloatedspider");
         bloatedSpider_shadow = fromFiles("bloatedspider_shadow");
         frostPiercer = fromFile("frostpiercer");
         steelBoat = fromFile("steelboat");
         walkingTorch = fromFile("walkingtorch");
         babyZombie = humanTexture("babyzombie");
         babyZombieArcher = humanTexture("babyzombiearcher", "babyzombiearms");
         babySnowman = humanTexture("babysnowman");
         poisonSlime = fromFile("poisonslime");
         poisonSlime_shadow = fromFile("poisonslime_shadow");
         pouncingSlime = fromFile("pouncingslime");
         pouncingSlime_shadow = fromFile("pouncingslime_shadow");
         greatswordSlime = fromFile("greatswordslime");
         greatswordSlime_shadow = fromFile("greatswordslime_shadow");
         chargingPhantom = fromFiles("playerchargingphantom");
         orbOfSlimesSlime = fromFiles("orbofslimesslime");
         reaperSpiritPet = fromFile("playerreaperspirit");
         jumpingBall = fromFile("jumpingball");
         jumpingBall_shadow = fromFile("jumpingball_shadow");
         babySpider = fromFiles("babyspider");
         cryoFlakePet = fromFile("playercryoflake");
         hoverBoard = fromFiles("hoverboard");
         babySkeleton = humanTexture("babyskeleton");
         babySkeletonMage = humanTexture("babyskeletonmage");
         babySpiderkinWarrior = humanTexture("babyspiderkinwarrior");
         babySpiderkinArcher = humanTexture("babyspiderkinarcher");
         duskMoonDisc = fromFile("duskmoondisc");
         cavelingElder = fromFile("cavelingelder");
         evilsProtector = fromFile("evilsprotector");
         evilsProtector_shadow = fromFile("evilsprotector_shadow");
         evilsProtector2 = fromFile("evilsprotector2");
         evilsProtectorBomb = fromFile("evilsprotectorbomb");
         evilsProtectorBomb_shadow = fromFile("evilsprotectorbomb_shadow");
         portalMinion = fromFile("portalminion");
         queenSpiderBody = fromFile("queenspiderbody");
         queenSpiderHead = fromFile("queenspiderhead");
         queenSpiderLeg = fromFile("queenspiderleg");
         queenSpiderDebris = fromFile("queenspiderdebris");
         queenSpider_shadow = fromFile("queenspider_shadow");
         queenSpiderLeg_shadow = fromFile("queenspiderleg_shadow");
         queenSpider_spit = fromFile("queenspider_spit");
         spiderHatchling = fromFiles("spiderhatchling");
         voidWizard = humanTexture("voidwizard");
         voidWizard2 = fromFile("voidwizard2");
         voidWizard3 = fromFile("voidwizard3");
         pirateCaptain = humanTexture("pirates/piratecaptain");
         pirateCaptainShip = fromFile("pirates/piratecaptainship");
         pirateCaptainShip_shadow = fromFile("pirates/piratecaptainship_shadow");
         pirateCaptainShip_mask = fromFile("pirates/piratecaptainship_mask");
         pirateRecruit1 = humanTexture("pirates/pirate1");
         pirateRecruit2 = humanTexture("pirates/pirate2");
         parrot = fromFile("pirates/parrot");
         ancientVulture = fromFile("ancientvulture");
         ancientVulture_shadow = fromFile("ancientvulture_shadow");
         ancientVultureEgg = fromFile("ancientvultureegg");
         ancientVultureEgg_shadow = fromFile("ancientvultureegg_shadow");
         vultureHatchling = fromFile("vulturehatchling");
         reaper = fromFile("reaper");
         reaperGlow = fromFile("reaperglow");
         reaper_shadow = fromFile("reaper_shadow");
         reaperSpirit = fromFile("reaperspirit");
         reaperSpiritPortal = fromFile("reaperspiritportal");
         cryoQueen = fromFile("cryoqueen");
         swampGuardian = fromFile("swampguardian");
         swampGuardian_shadow = fromFile("swampguardian_shadow");
         swampGuardian_mask = fromFile("swampguardian_mask");
         pestWarden = fromFile("pestwarden");
         pestWarden_shadow = fromFile("pestwarden_shadow");
         pestWarden_mask = fromFile("pestwarden_mask");
         flyingSpirits = fromFile("flyingspirits");
         fallenWizard = humanTexture("fallenwizard");
         fallenWizardDragon = fromFile("fallenwizarddragon");
         nightSwarmBat = fromFile("nightswarmbat");
         motherSlime = fromFiles("motherslime");
         spiderEmpressHead = fromFile("spiderempress_head");
         spiderEmpressTorso = fromFile("spiderempress_torso");
         spiderEmpressDress = fromFile("spiderempress_dress");
         spiderEmpressLegTop = fromFile("spiderempress_legtop");
         spiderEmpressLegBottom = fromFile("spiderempress_legbottom");
         spiderEmpressArmTop = fromFile("spiderempress_armtop");
         spiderEmpressArmBottom = fromFile("spiderempress_armbottom");
         spiderEmpressHand = fromFile("spiderempress_hand");
         spiderEmpressRageHead = fromFile("spiderempressrage_head");
         spiderEmpressRageTorso = fromFile("spiderempressrage_torso");
         spiderEmpressRageDress = fromFile("spiderempressrage_dress");
         spiderEmpressRageLegTop = fromFile("spiderempressrage_legtop");
         spiderEmpressRageLegBottom = fromFile("spiderempressrage_legbottom");
         spiderEmpressRageArmTop = fromFile("spiderempressrage_armtop");
         spiderEmpressRageArmBottom = fromFile("spiderempressrage_armbottom");
         spiderEmpressRageHand = fromFile("spiderempressrage_hand");
         spiderEmpressDebris = fromFile("spiderempress_debris");
         sunlightChampionEye = fromFile("sunlightchampioneye");
         sunlightChampionChestplate = fromFile("sunlightchampionchestplate");
         sunlightChampionJet = fromFile("sunlightchampionjet");
         sunlightGauntlet = fromFile("sunlightgauntlet");
         sunlightGauntletFire = fromFile("sunlightgauntletfire");
         sunlightGauntletJet = fromFile("sunlightgauntletjet");
         moonlightDancer = fromFile("moonlightdancer");
         moonlightDancerInvincible = fromFile("moonlightdancerinvincible");
         moonlightDancerDebris = fromFile("moonlightdancerdebris");
         moonlightDancerHead = fromFile("moonlightdancerhead");
         mound1 = fromFile("mound1");
         mound2 = fromFile("mound2");
         mound3 = fromFile("mound3");
         mounds32 = fromFile("mounds32");
         mountmask = GameTexture.fromFile("mobs/mountmask");
         swimmask = GameTexture.fromFile("mobs/swimmask");
         boat_shadow = fromFile("boat_shadow");
         woodBoat = fromFile("woodboat");
         GameTexture var0 = fromFile("boatmask");
         int var1 = var0.getHeight() / 64;
         boat_mask = new GameTexture[var1];

         for(int var2 = 0; var2 < var1; ++var2) {
            boat_mask[var2] = new GameTexture(var0, 0, var2, 64);
         }

         minecart = fromFile("minecart");
         minecart_shadow = fromFile("minecart_shadow");
         GameTexture var5 = fromFile("minecart_mask");
         int var3 = var5.getHeight() / 64;
         minecart_mask = new GameTexture[var3];

         for(int var4 = 0; var4 < var3; ++var4) {
            minecart_mask[var4] = new GameTexture(var5, 0, var4, 64);
         }

         sawblade = fromFile("sawblade");
         polarBear = fromFile("polarbear");
         polarBear_shadow = fromFile("polarbear_shadow");
         portalSphere = fromFile("portalsphere");
         spawnSphere = fromFile("spawnsphere");
         bossPortal = fromFile("bossportal");
         ravenlords_set_feather = fromFile("ravenlordssetfeather");
      }

      public static GameTexture fromFile(String var0) {
         return GameTexture.fromFile("mobs/" + var0);
      }

      public static GameTexture fromFile(String var0, GameTexture var1) {
         return GameTexture.fromFile("mobs/" + var0, var1);
      }

      public static MobTexture fromFiles(String var0, String var1) {
         return new MobTexture(fromFile(var0), fromFile(var1));
      }

      public static MobTexture fromFiles(String var0) {
         return fromFiles(var0, var0 + "_shadow");
      }

      public static HumanTexture humanTexture(String var0, String var1) {
         return new HumanTexture(fromFile(var0), fromFile(var1 + "_left"), fromFile(var1 + "_right"));
      }

      public static HumanTexture humanTexture(String var0) {
         return humanTexture(var0, var0 + "arms");
      }

      public static HumanTextureFull humanTextureFull(String var0, String var1, String var2, String var3, String var4, String var5) {
         return new HumanTextureFull(fromFile(var0, (GameTexture)null), fromFile(var1, (GameTexture)null), fromFile(var2, (GameTexture)null), fromFile(var3), fromFile(var4 + "_left"), fromFile(var4 + "_right"), fromFile(var5));
      }

      public static HumanTextureFull humanTextureFull(String var0) {
         return humanTextureFull(var0 + "head", var0 + "hair", var0 + "backhair", var0 + "body", var0 + "arms", var0 + "feet");
      }
   }
}
