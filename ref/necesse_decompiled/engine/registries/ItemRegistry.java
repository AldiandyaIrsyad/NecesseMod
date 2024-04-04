package necesse.engine.registries;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.GameMusic;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.FishingPoleHolding;
import necesse.inventory.item.Item;
import necesse.inventory.item.SwingSpriteAttackItem;
import necesse.inventory.item.WorkSpriteAttackItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.armorItem.HelmetArmorItem;
import necesse.inventory.item.armorItem.LightHelmetArmorItem;
import necesse.inventory.item.armorItem.ShirtArmorItem;
import necesse.inventory.item.armorItem.ShoesArmorItem;
import necesse.inventory.item.armorItem.SurgicalMaskArmorItem;
import necesse.inventory.item.armorItem.WigArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilBootsArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilChestplateArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilHelmetArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilMaskArmorItem;
import necesse.inventory.item.armorItem.bloodplate.BloodplateBootsArmorItem;
import necesse.inventory.item.armorItem.bloodplate.BloodplateChestplateArmorItem;
import necesse.inventory.item.armorItem.bloodplate.BloodplateCowlArmorItem;
import necesse.inventory.item.armorItem.cloth.ClothBootsArmorItem;
import necesse.inventory.item.armorItem.cloth.ClothHatArmorItem;
import necesse.inventory.item.armorItem.cloth.ClothRobeArmorItem;
import necesse.inventory.item.armorItem.copper.CopperBootsArmorItem;
import necesse.inventory.item.armorItem.copper.CopperChestplateArmorItem;
import necesse.inventory.item.armorItem.copper.CopperHelmetArmorItem;
import necesse.inventory.item.armorItem.dawn.DawnBootsArmorItem;
import necesse.inventory.item.armorItem.dawn.DawnChestplateArmorItem;
import necesse.inventory.item.armorItem.dawn.DawnHelmetArmorItem;
import necesse.inventory.item.armorItem.demonic.DemonicBootsArmorItem;
import necesse.inventory.item.armorItem.demonic.DemonicChestplateArmorItem;
import necesse.inventory.item.armorItem.demonic.DemonicHelmetArmorItem;
import necesse.inventory.item.armorItem.dusk.DuskBootsArmorItem;
import necesse.inventory.item.armorItem.dusk.DuskChestplateArmorItem;
import necesse.inventory.item.armorItem.dusk.DuskHelmetArmorItem;
import necesse.inventory.item.armorItem.frost.FrostBootsArmorItem;
import necesse.inventory.item.armorItem.frost.FrostChestplateArmorItem;
import necesse.inventory.item.armorItem.frost.FrostHatArmorItem;
import necesse.inventory.item.armorItem.frost.FrostHelmetArmorItem;
import necesse.inventory.item.armorItem.frost.FrostHoodArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialBootsArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialChestplateArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialCircletArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialHelmetArmorItem;
import necesse.inventory.item.armorItem.gold.GoldBootsArmorItem;
import necesse.inventory.item.armorItem.gold.GoldChestplateArmorItem;
import necesse.inventory.item.armorItem.gold.GoldCrownArmorItem;
import necesse.inventory.item.armorItem.gold.GoldHelmetArmorItem;
import necesse.inventory.item.armorItem.iron.IronBootsArmorItem;
import necesse.inventory.item.armorItem.iron.IronChestplateArmorItem;
import necesse.inventory.item.armorItem.iron.IronHelmetArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyBootsArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyChestplateArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyCircletArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyHatArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyHelmetArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyHoodArmorItem;
import necesse.inventory.item.armorItem.leather.LeatherBootsArmorItem;
import necesse.inventory.item.armorItem.leather.LeatherHoodArmorItem;
import necesse.inventory.item.armorItem.leather.LeatherShirtArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumBootsArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumChestplateArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumHoodArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumScarfArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelBootsArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelChestplateArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelCircletArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelHelmetArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelMaskArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelVeilArmorItem;
import necesse.inventory.item.armorItem.ninja.NinjaHoodArmorItem;
import necesse.inventory.item.armorItem.ninja.NinjaRobeArmorItem;
import necesse.inventory.item.armorItem.ninja.NinjaShoesArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzBootsArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzChestplateArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzCrownArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzHelmetArmorItem;
import necesse.inventory.item.armorItem.ravenlords.RavenlordsBootsArmorItem;
import necesse.inventory.item.armorItem.ravenlords.RavenlordsChestplateArmorItem;
import necesse.inventory.item.armorItem.ravenlords.RavenlordsHeaddressArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowBootsArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowHatArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowHoodArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowMantleArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeBootsArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeChestplateArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeHatArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeHelmetArmorItem;
import necesse.inventory.item.armorItem.spider.SpiderBootsArmorItem;
import necesse.inventory.item.armorItem.spider.SpiderChestplateArmorItem;
import necesse.inventory.item.armorItem.spider.SpiderHelmetArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteChestplateArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteCrownArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteGreavesArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteHatArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteHelmetArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteHoodArmorItem;
import necesse.inventory.item.armorItem.tungsten.TungstenBootsArmorItem;
import necesse.inventory.item.armorItem.tungsten.TungstenChestplateArmorItem;
import necesse.inventory.item.armorItem.tungsten.TungstenHelmetArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidBootsArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidHatArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidMaskArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidRobeArmorItem;
import necesse.inventory.item.armorItem.widow.WidowBootsArmorItem;
import necesse.inventory.item.armorItem.widow.WidowChestplateArmorItem;
import necesse.inventory.item.armorItem.widow.WidowHelmetArmorItem;
import necesse.inventory.item.arrowItem.BoneArrowItem;
import necesse.inventory.item.arrowItem.BouncingArrowItem;
import necesse.inventory.item.arrowItem.FireArrowItem;
import necesse.inventory.item.arrowItem.FrostArrowItem;
import necesse.inventory.item.arrowItem.IronArrowItem;
import necesse.inventory.item.arrowItem.PoisonArrowItem;
import necesse.inventory.item.arrowItem.SpideriteArrowItem;
import necesse.inventory.item.arrowItem.StoneArrowItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.bulletItem.BouncingBulletItem;
import necesse.inventory.item.bulletItem.CannonballAmmoItem;
import necesse.inventory.item.bulletItem.FrostBulletItem;
import necesse.inventory.item.bulletItem.SimpleBulletItem;
import necesse.inventory.item.bulletItem.VoidBulletItem;
import necesse.inventory.item.matItem.BookMatItem;
import necesse.inventory.item.matItem.EssenceMatItem;
import necesse.inventory.item.matItem.FishItem;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.item.miscItem.AmmoBag;
import necesse.inventory.item.miscItem.AmmoPouch;
import necesse.inventory.item.miscItem.BannerItem;
import necesse.inventory.item.miscItem.BinocularsItem;
import necesse.inventory.item.miscItem.ChristmasPresentItem;
import necesse.inventory.item.miscItem.CoinPouch;
import necesse.inventory.item.miscItem.CraftingGuideBookItem;
import necesse.inventory.item.miscItem.DragonSoulsItem;
import necesse.inventory.item.miscItem.EnchantingScrollItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.miscItem.InfiniteRopeItem;
import necesse.inventory.item.miscItem.Lunchbox;
import necesse.inventory.item.miscItem.PortableMusicPlayerItem;
import necesse.inventory.item.miscItem.PotionBag;
import necesse.inventory.item.miscItem.PotionPouch;
import necesse.inventory.item.miscItem.PresentItem;
import necesse.inventory.item.miscItem.RecipeBookItem;
import necesse.inventory.item.miscItem.RopeItem;
import necesse.inventory.item.miscItem.ShearsItem;
import necesse.inventory.item.miscItem.StrikeBannerItem;
import necesse.inventory.item.miscItem.TabletBox;
import necesse.inventory.item.miscItem.TelescopeItem;
import necesse.inventory.item.miscItem.VinylItem;
import necesse.inventory.item.miscItem.VoidBagItem;
import necesse.inventory.item.miscItem.VoidPouchItem;
import necesse.inventory.item.miscItem.WorkInProgressItem;
import necesse.inventory.item.miscItem.WrappingPaperItem;
import necesse.inventory.item.mountItem.HoverBoardMountItem;
import necesse.inventory.item.mountItem.JumpingBallMountItem;
import necesse.inventory.item.mountItem.MinecartMountItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.inventory.item.mountItem.SteelBoatMountItem;
import necesse.inventory.item.mountItem.WoodBoatMountItem;
import necesse.inventory.item.placeableItem.ApiaryFramePlaceableItem;
import necesse.inventory.item.placeableItem.CutterPlaceableItem;
import necesse.inventory.item.placeableItem.FertilizerPlaceableItem;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;
import necesse.inventory.item.placeableItem.HoneyBeePlaceableItem;
import necesse.inventory.item.placeableItem.ImportedAnimalSpawnItem;
import necesse.inventory.item.placeableItem.StonePlaceableItem;
import necesse.inventory.item.placeableItem.WrenchPlaceableItem;
import necesse.inventory.item.placeableItem.bucketItem.BucketItem;
import necesse.inventory.item.placeableItem.bucketItem.InfiniteWaterBucketItem;
import necesse.inventory.item.placeableItem.consumableItem.DemonHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.LifeElixirItem;
import necesse.inventory.item.placeableItem.consumableItem.PortalFlaskItem;
import necesse.inventory.item.placeableItem.consumableItem.RecallFlaskItem;
import necesse.inventory.item.placeableItem.consumableItem.RecallScrollItem;
import necesse.inventory.item.placeableItem.consumableItem.StinkFlaskItem;
import necesse.inventory.item.placeableItem.consumableItem.TestChangeTrinketSlotsItem;
import necesse.inventory.item.placeableItem.consumableItem.TravelScrollItem;
import necesse.inventory.item.placeableItem.consumableItem.TrinketSlotsIncreaseItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodMatItem;
import necesse.inventory.item.placeableItem.consumableItem.food.GrainItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.HealthPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.ManaPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.AncientVultureSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.CryoQueenSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.EvilsProtectorSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.MotherSlimeSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.NightSwarmSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.PestWardenSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.QueenSpiderSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.ReaperSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.SpiderEmpressSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.SwampGuardianSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.VoidWizardSpawnItem;
import necesse.inventory.item.placeableItem.fishingRodItem.DepthsCatcherRodItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.inventory.item.placeableItem.followerSummonItem.petFollowerPlaceableItem.PetFollowerPlaceableItem;
import necesse.inventory.item.placeableItem.mapItem.BiomeMapItem;
import necesse.inventory.item.placeableItem.tileItem.GrassSeedItem;
import necesse.inventory.item.placeableItem.tileItem.LandfillItem;
import necesse.inventory.item.questItem.ApprenticeScrollQuestItem;
import necesse.inventory.item.questItem.BabySharkQuestItem;
import necesse.inventory.item.questItem.BabySwordfishQuestItem;
import necesse.inventory.item.questItem.BrokenLimbQuestItem;
import necesse.inventory.item.questItem.CapturedSpiritQuestItem;
import necesse.inventory.item.questItem.CaveOysterQuestItem;
import necesse.inventory.item.questItem.CrabClawQuestItem;
import necesse.inventory.item.questItem.CrawlersFootQuestItem;
import necesse.inventory.item.questItem.DarkGemQuestItem;
import necesse.inventory.item.questItem.DeepSpiritSwabQuestItem;
import necesse.inventory.item.questItem.EnchantedCollarQuestItem;
import necesse.inventory.item.questItem.EyePatchQuestItem;
import necesse.inventory.item.questItem.FakeFangsQuestItem;
import necesse.inventory.item.questItem.FeralTailQuestItem;
import necesse.inventory.item.questItem.FrozenBeardQuestItem;
import necesse.inventory.item.questItem.GoblinRingQuestItem;
import necesse.inventory.item.questItem.MagicSandQuestItem;
import necesse.inventory.item.questItem.MummysBandageQuestItem;
import necesse.inventory.item.questItem.PegLegQuestItem;
import necesse.inventory.item.questItem.RazorIcicleQuestItem;
import necesse.inventory.item.questItem.RumBottleQuestItem;
import necesse.inventory.item.questItem.SandRayQuestItem;
import necesse.inventory.item.questItem.SlimeChunkQuestItem;
import necesse.inventory.item.questItem.SlimeSampleQuestItem;
import necesse.inventory.item.questItem.SlimyLauncherQuestItem;
import necesse.inventory.item.questItem.SoakedBowQuestItem;
import necesse.inventory.item.questItem.SpiderLegQuestItem;
import necesse.inventory.item.questItem.SwampEelQuestItem;
import necesse.inventory.item.questItem.WormToothQuestItem;
import necesse.inventory.item.questItem.ZombieArmQuestItem;
import necesse.inventory.item.toolItem.MultiToolItem;
import necesse.inventory.item.toolItem.axeToolItem.CustomAxeToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.CryoGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.FrostGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.GoldGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.QuartzGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.SlimeGlaiveToolItem;
import necesse.inventory.item.toolItem.miscToolItem.FarmingScytheToolItem;
import necesse.inventory.item.toolItem.miscToolItem.NetToolItem;
import necesse.inventory.item.toolItem.miscToolItem.SickleToolItem;
import necesse.inventory.item.toolItem.miscToolItem.TestToolItem;
import necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.AntiqueBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.ArachnidWebBowToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowOfDualismProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.CopperBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.DemonicBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.FrostBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.GlacialBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.GoldBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.IronBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.IvyBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.TheCrimsonSkyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.TungstenBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.VulturesBurstProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.WoodBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.DruidsGreatBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GoldGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.IvyGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.MyceliumGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.NightPiercerGreatBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.SlimeGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.TheRavensNestProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.TungstenGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.VoidGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.AntiqueRifleProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.CryoBlasterProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.DeathRipperProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.FlintlockProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.HandCannonProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.HandGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.LivingShottyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.MachineGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.ShotgunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SniperProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SnowLauncherProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.WebbedGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.AncientDredgingStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodBoltProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodGrimoireProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodVolleyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BoulderStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.CryoQuakeProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.DragonLanceProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.DredgingStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ElderlyWandProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FlamelingOrbProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FrostStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.GenieLampProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.IcicleStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MouseBeamProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MouseTestProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.PhantomPopperProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.QuartzStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ShadowBeamProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ShadowBoltProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SlimeStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SparklerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SwampDwellerStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SwampTomeProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VenomShowerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VenomStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VoidMissileProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VoidStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.WebWeaverToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.WoodStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.BoxingGloveGunToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.HeavyHammerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.ReaperScytheProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.CarapaceDaggerToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.DynamiteStickToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.IceJavelinToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.IronBombToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.NinjaStarToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.SnowBallToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.TileBombToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.DragonsReboundToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.FrostBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.GlacialBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.HookBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.NightRazorBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.RazorBladeBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.SpiderBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.TungstenBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.VoidBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.WoodBoomerangToolItem;
import necesse.inventory.item.toolItem.shovelToolItem.CustomShovelToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CopperPitchforkToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CopperSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CryoSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.DemonicSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.FrostSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.GoldSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.IronSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.IvySpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.TungstenSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.VoidSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.VulturesTalonToolItem;
import necesse.inventory.item.toolItem.spearToolItem.WoodSpearToolItem;
import necesse.inventory.item.toolItem.summonToolItem.BrainOnAStickToolItem;
import necesse.inventory.item.toolItem.summonToolItem.CryoStaffSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.EmpressCommandToolItem;
import necesse.inventory.item.toolItem.summonToolItem.FrostPiercerSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.MagicBranchSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.OrbOfSlimesToolItem;
import necesse.inventory.item.toolItem.summonToolItem.PhantomCallerSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.ReapersCallSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SkeletonStaffToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SlimeCanisterSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SpiderStaffSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SwampsGraspToolItem;
import necesse.inventory.item.toolItem.summonToolItem.VultureStaffSummonToolItem;
import necesse.inventory.item.toolItem.swordToolItem.AntiqueSwordSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.BloodClawToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CausticExecutionerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CopperSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CutlassSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.DemonicSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.FrostSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.GoldSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.IronSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.IvySwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.LightningHammerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.MLG1SwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.MLG2SwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.NunchucksToolItem;
import necesse.inventory.item.toolItem.swordToolItem.SandKnifeToolItem;
import necesse.inventory.item.toolItem.swordToolItem.SpiderClawSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.TungstenSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.VenomSlasherToolItem;
import necesse.inventory.item.toolItem.swordToolItem.WoodSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.FrostGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GlacialGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.IronGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.IvyGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.QuartzGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.RavenwingGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.SlimeGreatswordToolItem;
import necesse.inventory.item.trinketItem.BlinkScepterTrinketItem;
import necesse.inventory.item.trinketItem.CactusShieldTrinketItem;
import necesse.inventory.item.trinketItem.CalmingMinersBouquetTrinketItem;
import necesse.inventory.item.trinketItem.CombinedTrinketItem;
import necesse.inventory.item.trinketItem.FoolsGambitTrinketItem;
import necesse.inventory.item.trinketItem.ForceOfWindTrinketItem;
import necesse.inventory.item.trinketItem.LeatherDashersTrinketItem;
import necesse.inventory.item.trinketItem.MinersBouquetTrinketItem;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.item.trinketItem.SiphonShieldTrinketItem;
import necesse.inventory.item.trinketItem.WoodShieldTrinketItem;
import necesse.inventory.item.trinketItem.ZephyrBootsTrinketItem;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class ItemRegistry extends GameRegistry<ItemRegistryElement> {
   public static final ItemRegistry instance = new ItemRegistry();
   private static int totalItemsObtainable = 0;
   private static int totalStatItemsObtainable = 0;
   private static int totalItems = 0;
   private static int totalTrinkets = 0;
   public static final int WOOD_TOOL_DPS = 50;
   public static final int COPPER_TOOL_DPS = 65;
   public static final int IRON_TOOL_DPS = 80;
   public static final int GOLD_TOOL_DPS = 90;
   public static final int FROST_TOOL_DPS = 100;
   public static final int DEMONIC_TOOL_DPS = 115;
   public static final int IVY_TOOL_DPS = 125;
   public static final int QUARTZ_TOOL_DPS = 135;
   public static final int TUNGSTEN_TOOL_DPS = 150;
   public static final int GLACIAL_TOOLS_DPS = 165;
   public static final int MYCELIUM_TOOLS_DPS = 180;
   public static final int ANCIENT_FOSSIl_TOOLS_DPS = 195;

   private ItemRegistry() {
      super("Item", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "items"));
      registerItem("oaklog", (new MatItem(250, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
      registerItem("sprucelog", (new MatItem(250, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
      registerItem("pinelog", (new MatItem(250, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
      registerItem("palmlog", (new MatItem(250, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
      registerItem("willowlog", (new MatItem(250, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
      registerItem("deadwoodlog", (new MatItem(250, new String[]{"anylog"})).setItemCategory(new String[]{"materials", "logs"}), 2.0F, true);
      registerItem("stone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("sandstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("swampstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("snowstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("deepstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("deepsnowstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("deepswampstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("deepsandstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("cryptstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("spiderstone", new StonePlaceableItem(500), 0.1F, true);
      registerItem("batwing", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 10.0F, true);
      registerItem("wool", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 7.0F, true);
      registerItem("leather", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 7.0F, true);
      registerItem("clay", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "minerals"}), 4.0F, true);
      registerItem("copperore", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 1.0F, true);
      registerItem("copperbar", (new MatItem(100, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 4.0F, true);
      registerItem("ironore", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 1.5F, true);
      registerItem("ironbar", (new MatItem(100, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 6.0F, true);
      registerItem("goldore", (new MatItem(250, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 2.5F, true);
      registerItem("goldbar", (new MatItem(100, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 10.0F, true);
      registerItem("frostshard", (new MatItem(100, Item.Rarity.COMMON, new String[0])).setItemCategory(new String[]{"materials", "minerals"}), 10.0F, true);
      registerItem("ivyore", (new MatItem(250, Item.Rarity.COMMON, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 3.0F, true);
      registerItem("ivybar", (new MatItem(100, Item.Rarity.COMMON, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 12.0F, true);
      registerItem("quartz", (new MatItem(100, Item.Rarity.COMMON, new String[0])).setItemCategory(new String[]{"materials", "minerals"}), 15.0F, true);
      registerItem("lifequartz", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "minerals"}), 20.0F, true);
      registerItem("demonicbar", (new MatItem(100, Item.Rarity.COMMON, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 10.0F, true);
      registerItem("tungstenore", (new MatItem(250, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 6.0F, true);
      registerItem("tungstenbar", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 20.0F, true);
      registerItem("glacialore", (new MatItem(250, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 6.0F, true);
      registerItem("glacialbar", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 20.0F, true);
      registerItem("myceliumore", (new MatItem(250, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 6.0F, true);
      registerItem("myceliumbar", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 20.0F, true);
      registerItem("ancientfossilore", (new MatItem(250, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 6.0F, true);
      registerItem("ancientfossilbar", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 20.0F, true);
      registerItem("obsidian", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "minerals"}), 2.0F, true);
      registerItem("slimeum", (new MatItem(500, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "minerals"}), 10.0F, true);
      registerItem("nightsteelore", (new MatItem(250, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 8.0F, true);
      registerItem("nightsteelbar", (new MatItem(100, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 24.0F, true);
      registerItem("spideriteore", (new MatItem(250, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "ore"}), 10.0F, true);
      registerItem("spideritebar", (new MatItem(100, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "bars"}), 28.0F, true);
      registerItem("cavespidergland", (new MultiTextureMatItem(3, 100, Item.Rarity.COMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 15.0F, true);
      registerItem("voidshard", (new MatItem(100, Item.Rarity.UNCOMMON, "voidshardtip")).setItemCategory(new String[]{"materials", "mobdrops"}), 10.0F, true);
      registerItem("swampsludge", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 10.0F, true);
      registerItem("bone", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 8.0F, true);
      registerItem("ectoplasm", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 12.0F, true);
      registerItem("glacialshard", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 12.0F, true);
      registerItem("silk", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 12.0F, true);
      registerItem("wormcarapace", (new MatItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 12.0F, true);
      registerItem("phantomdust", (new MatItem(100, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 14.0F, true);
      registerItem("glassbottle", new MatItem(100, Item.Rarity.NORMAL, "glassbottletip"), 1.0F, true);
      registerItem("book", new BookMatItem(), 15.0F, true);
      registerItem("slimematter", (new MatItem(100, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 15.0F, true);
      registerItem("spidervenom", (new MatItem(100, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "mobdrops"}), 15.0F, true);
      registerItem("alchemyshard", (new MatItem(250, Item.Rarity.RARE, "alchemyshardtip")).setItemCategory(new String[]{"materials", "minerals"}), 8.0F, true);
      registerItem("upgradeshard", (new MatItem(250, Item.Rarity.RARE, "upgradeshardtip")).setItemCategory(new String[]{"materials", "minerals"}), 8.0F, true);
      registerItem("shadowessence", new EssenceMatItem(100, Item.Rarity.RARE, 1), 25.0F, true);
      registerItem("cryoessence", new EssenceMatItem(100, Item.Rarity.RARE, 1), 25.0F, true);
      registerItem("bioessence", new EssenceMatItem(100, Item.Rarity.RARE, 1), 25.0F, true);
      registerItem("primordialessence", new EssenceMatItem(100, Item.Rarity.RARE, 1), 25.0F, true);
      registerItem("slimeessence", new EssenceMatItem(100, Item.Rarity.EPIC, 2), 30.0F, true);
      registerItem("bloodessence", new EssenceMatItem(100, Item.Rarity.EPIC, 2), 30.0F, true);
      registerItem("spideressence", new EssenceMatItem(100, Item.Rarity.EPIC, 2), 30.0F, true);
      registerItem("mapfragment", (new MatItem(50, Item.Rarity.UNCOMMON, "mapfragmenttip")).setItemCategory(new String[]{"materials", "mobdrops"}), 30.0F, true);
      registerItem("spareboatparts", new MatItem(1, Item.Rarity.RARE, "sparepartstip"), 250.0F, true);
      registerItem("brokencoppertool", (new MultiTextureMatItem(4, 50, Item.Rarity.COMMON, "brokentooltip")).setItemCategory(new String[]{"materials", "mobdrops"}), 20.0F, true);
      registerItem("brokenirontool", (new MultiTextureMatItem(4, 50, Item.Rarity.COMMON, "brokentooltip")).setItemCategory(new String[]{"materials", "mobdrops"}), 30.0F, true);
      registerItem("woodpickaxe", new CustomPickaxeToolItem(500, 50, 0, 10, 50, 50, 100), 8.0F, true);
      registerItem("copperpickaxe", new CustomPickaxeToolItem(500, 65, 0, 12, 50, 50, 200), 17.0F, true);
      registerItem("ironpickaxe", new CustomPickaxeToolItem(500, 80, 0, 14, 50, 50, 400), 25.0F, true);
      registerItem("goldpickaxe", new CustomPickaxeToolItem(500, 90, 0, 15, 50, 50, 450), 40.0F, true);
      registerItem("frostpickaxe", new CustomPickaxeToolItem(500, 100, 0, 16, 50, 50, 500), 60.0F, true);
      registerItem("demonicpickaxe", new CustomPickaxeToolItem(500, 115, 1, 17, 50, 50, 600, Item.Rarity.COMMON) {
         public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
            ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
            var4.add((String)Localization.translate("itemtooltip", "demonictooltip"), 350);
            return var4;
         }
      }, 80.0F, true);
      registerItem("ivypickaxe", new CustomPickaxeToolItem(450, 125, 1, 18, 50, 50, 700), 100.0F, true);
      registerItem("tungstenpickaxe", new CustomPickaxeToolItem(400, 150, 2, 20, 50, 50, 800, Item.Rarity.UNCOMMON) {
         public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
            ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
            var4.add((String)Localization.translate("itemtooltip", "tungstentooltip"), 350);
            return var4;
         }
      }, 160.0F, true);
      registerItem("glacialpickaxe", new CustomPickaxeToolItem(400, 165, 3, 22, 50, 50, 900, Item.Rarity.UNCOMMON) {
         public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
            ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
            var4.add((String)Localization.translate("itemtooltip", "glacialtooltip"), 350);
            return var4;
         }
      }, 160.0F, true);
      registerItem("icepickaxe", new CustomPickaxeToolItem(400, 195, 4, 24, 50, 50, 1000, Item.Rarity.RARE, 1), 400.0F, true);
      registerItem("myceliumpickaxe", new CustomPickaxeToolItem(400, 180, 4, 24, 50, 50, 1000, Item.Rarity.UNCOMMON) {
         public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
            ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
            var4.add((String)Localization.translate("itemtooltip", "myceliumtooltip"), 350);
            return var4;
         }
      }, 160.0F, true);
      registerItem("ancientfossilpickaxe", new CustomPickaxeToolItem(400, 195, 5, 26, 50, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("woodaxe", new CustomAxeToolItem(500, 50, 0, 10, 50, 50, 100), 8.0F, true);
      registerItem("copperaxe", new CustomAxeToolItem(500, 65, 0, 12, 50, 50, 200), 17.0F, true);
      registerItem("ironaxe", new CustomAxeToolItem(500, 80, 0, 14, 50, 50, 400), 25.0F, true);
      registerItem("goldaxe", new CustomAxeToolItem(500, 90, 0, 15, 50, 50, 450), 40.0F, true);
      registerItem("frostaxe", new CustomAxeToolItem(500, 100, 0, 16, 50, 50, 500), 60.0F, true);
      registerItem("demonicaxe", new CustomAxeToolItem(500, 115, 1, 17, 50, 50, 600, Item.Rarity.COMMON), 80.0F, true);
      registerItem("ivyaxe", new CustomAxeToolItem(450, 125, 1, 18, 50, 50, 700, Item.Rarity.COMMON), 100.0F, true);
      registerItem("tungstenaxe", new CustomAxeToolItem(400, 150, 2, 20, 50, 50, 800, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("glacialaxe", new CustomAxeToolItem(400, 165, 3, 22, 50, 50, 900, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("myceliumaxe", new CustomAxeToolItem(400, 180, 4, 24, 50, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("ancientfossilaxe", new CustomAxeToolItem(400, 195, 5, 26, 50, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("woodshovel", new CustomShovelToolItem(500, 50, 0, 10, 50, 50, 100), 8.0F, true);
      registerItem("coppershovel", new CustomShovelToolItem(500, 65, 0, 12, 50, 50, 200), 17.0F, true);
      registerItem("ironshovel", new CustomShovelToolItem(500, 80, 0, 14, 50, 50, 400), 25.0F, true);
      registerItem("goldshovel", new CustomShovelToolItem(500, 90, 0, 15, 50, 50, 450), 40.0F, true);
      registerItem("frostshovel", new CustomShovelToolItem(500, 100, 0, 16, 50, 50, 500), 60.0F, true);
      registerItem("demonicshovel", new CustomShovelToolItem(500, 115, 1, 17, 50, 50, 600, Item.Rarity.COMMON), 80.0F, true);
      registerItem("ivyshovel", new CustomShovelToolItem(450, 125, 1, 18, 50, 50, 700, Item.Rarity.COMMON), 100.0F, true);
      registerItem("tungstenshovel", new CustomShovelToolItem(400, 150, 2, 20, 50, 50, 800, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("glacialshovel", new CustomShovelToolItem(400, 165, 3, 22, 50, 50, 900, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("myceliumshovel", new CustomShovelToolItem(400, 180, 4, 24, 50, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("ancientfossilshovel", new CustomShovelToolItem(400, 195, 5, 26, 50, 50, 1000, Item.Rarity.UNCOMMON), 160.0F, true);
      registerItem("godrod", new FishingRodItem(100, 40, 35, 400, 5000, 5, 30, 0, Item.Rarity.LEGENDARY), 0.0F, false);
      registerItem("woodfishingrod", new FishingRodItem(5, Item.Rarity.NORMAL), 20.0F, true);
      registerItem("ironfishingrod", new FishingRodItem(15, Item.Rarity.NORMAL), 150.0F, true);
      registerItem("overgrownfishingrod", new FishingRodItem(25, 42, 18, Item.Rarity.COMMON), 200.0F, true);
      registerItem("goldfishingrod", new FishingRodItem(30, Item.Rarity.COMMON), 250.0F, true);
      registerItem("depthscatcher", new DepthsCatcherRodItem(), 400.0F, true);
      registerItem("godbait", new BaitItem(false, 100), 0.0F, false);
      registerItem("wormbait", new BaitItem(true, 10), 2.0F, true);
      registerItem("swamplarva", new BaitItem(true, 20), 3.0F, true);
      registerItem("anglersbait", new BaitItem(false, 25), 5.0F, true);
      registerItem("cheattool", new TestToolItem(), 0.0F, false);
      registerItem("sickle", new SickleToolItem(), 50.0F, true);
      registerItem("farmingscythe", new FarmingScytheToolItem(), 550.0F, true);
      registerItem("net", new NetToolItem(), 40.0F, true);
      registerItem("wrench", new WrenchPlaceableItem(), 50.0F, true);
      registerItem("cutter", new CutterPlaceableItem(), 50.0F, true);
      registerItem("wire", (new MatItem(1000, Item.Rarity.NORMAL, "wiretip")).setItemCategory(new String[]{"wiring"}), 0.1F, true);
      registerItem("multitool", new MultiToolItem(1000), 50.0F, false);
      registerItem("woodsword", new WoodSwordToolItem(), 10.0F, true);
      registerItem("coppersword", new CopperSwordToolItem(), 25.0F, true);
      registerItem("ironsword", new IronSwordToolItem(), 40.0F, true);
      registerItem("goldsword", new GoldSwordToolItem(), 50.0F, true);
      registerItem("nunchucks", new NunchucksToolItem(), 50.0F, true);
      registerItem("heavyhammer", new HeavyHammerProjectileToolItem(), 120.0F, true);
      registerItem("frostsword", new FrostSwordToolItem(), 80.0F, true);
      registerItem("lightninghammer", new LightningHammerToolItem(), 150.0F, true);
      registerItem("demonicsword", new DemonicSwordToolItem(), 120.0F, true);
      registerItem("spiderclaw", new SpiderClawSwordToolItem(), 300.0F, true);
      registerItem("ivysword", new IvySwordToolItem(), 110.0F, true);
      registerItem("cutlass", new CutlassSwordToolItem(), 500.0F, true);
      registerItem("tungstensword", new TungstenSwordToolItem(), 200.0F, true);
      registerItem("reaperscythe", new ReaperScytheProjectileToolItem(), 650.0F, true);
      registerItem("sandknife", new SandKnifeToolItem(), 450.0F, true);
      registerItem("venomslasher", new VenomSlasherToolItem(), 750.0F, true);
      registerItem("antiquesword", new AntiqueSwordSwordToolItem(), 800.0F, true);
      registerItem("bloodclaw", new BloodClawToolItem(), 750.0F, true);
      registerItem("causticexecutioner", new CausticExecutionerToolItem(), 850.0F, true);
      registerItem("goldencausticexecutioner", new CausticExecutionerToolItem(), 850.0F, false);
      registerItem("irongreatsword", new IronGreatswordToolItem(), 60.0F, true);
      registerItem("frostgreatsword", new FrostGreatswordToolItem(), 120.0F, true);
      registerItem("ivygreatsword", new IvyGreatswordToolItem(), 160.0F, true);
      registerItem("quartzgreatsword", new QuartzGreatswordToolItem(), 220.0F, true);
      registerItem("glacialgreatsword", new GlacialGreatswordToolItem(), 280.0F, true);
      registerItem("slimegreatsword", new SlimeGreatswordToolItem(), 400.0F, true);
      registerItem("ravenwinggreatsword", new RavenwingGreatswordToolItem(), 450.0F, true);
      registerItem("woodspear", new WoodSpearToolItem(), 15.0F, true);
      registerItem("copperpitchfork", new CopperPitchforkToolItem(), 60.0F, true);
      registerItem("copperspear", new CopperSpearToolItem(), 45.0F, true);
      registerItem("ironspear", new IronSpearToolItem(), 75.0F, true);
      registerItem("goldspear", new GoldSpearToolItem(), 90.0F, true);
      registerItem("frostspear", new FrostSpearToolItem(), 100.0F, true);
      registerItem("demonicspear", new DemonicSpearToolItem(), 120.0F, true);
      registerItem("voidspear", new VoidSpearToolItem(), 150.0F, true);
      registerItem("ivyspear", new IvySpearToolItem(), 130.0F, true);
      registerItem("vulturestalon", new VulturesTalonToolItem(), 300.0F, true);
      registerItem("tungstenspear", new TungstenSpearToolItem(), 200.0F, true);
      registerItem("cryospear", new CryoSpearToolItem(), 650.0F, true);
      registerItem("goldglaive", new GoldGlaiveToolItem(), 100.0F, true);
      registerItem("frostglaive", new FrostGlaiveToolItem(), 120.0F, true);
      registerItem("quartzglaive", new QuartzGlaiveToolItem(), 200.0F, true);
      registerItem("cryoglaive", new CryoGlaiveToolItem(), 700.0F, true);
      registerItem("slimeglaive", new SlimeGlaiveToolItem(), 400.0F, true);
      registerItem("woodbow", new WoodBowProjectileToolItem(), 12.0F, true);
      registerItem("copperbow", new CopperBowProjectileToolItem(), 25.0F, true);
      registerItem("ironbow", new IronBowProjectileToolItem(), 40.0F, true);
      registerItem("goldbow", new GoldBowProjectileToolItem(), 60.0F, true);
      registerItem("frostbow", new FrostBowProjectileToolItem(), 80.0F, true);
      registerItem("demonicbow", new DemonicBowProjectileToolItem(), 100.0F, true);
      registerItem("ivybow", new IvyBowProjectileToolItem(), 110.0F, true);
      registerItem("vulturesburst", new VulturesBurstProjectileToolItem(), 300.0F, true);
      registerItem("tungstenbow", new TungstenBowProjectileToolItem(), 200.0F, true);
      registerItem("glacialbow", new GlacialBowProjectileToolItem(), 200.0F, true);
      registerItem("bowofdualism", new BowOfDualismProjectileToolItem(), 700.0F, true);
      registerItem("antiquebow", new AntiqueBowProjectileToolItem(), 800.0F, true);
      registerItem("thecrimsonsky", new TheCrimsonSkyProjectileToolItem(), 400.0F, true);
      registerItem("arachnidwebbow", new ArachnidWebBowToolItem(), 850.0F, true);
      registerItem("goldenarachnidwebbow", new ArachnidWebBowToolItem(), 850.0F, false);
      registerItem("goldgreatbow", new GoldGreatbowProjectileToolItem(), 80.0F, true);
      registerItem("voidgreatbow", new VoidGreatbowProjectileToolItem(), 120.0F, true);
      registerItem("ivygreatbow", new IvyGreatbowProjectileToolItem(), 140.0F, true);
      registerItem("tungstengreatbow", new TungstenGreatbowProjectileToolItem(), 250.0F, true);
      registerItem("myceliumgreatbow", new MyceliumGreatbowProjectileToolItem(), 300.0F, true);
      registerItem("druidsgreatbow", new DruidsGreatBowProjectileToolItem(), 800.0F, true);
      registerItem("slimegreatbow", new SlimeGreatbowProjectileToolItem(), 400.0F, true);
      registerItem("nightpiercer", new NightPiercerGreatBowProjectileToolItem(), 400.0F, true);
      registerItem("theravensnest", new TheRavensNestProjectileToolItem(), 475.0F, true);
      registerItem("stonearrow", new StoneArrowItem(), 0.2F, true);
      registerItem("firearrow", new FireArrowItem(), 0.2F, true);
      registerItem("frostarrow", new FrostArrowItem(), 0.2F, true);
      registerItem("poisonarrow", new PoisonArrowItem(), 0.2F, true);
      registerItem("bouncingarrow", new BouncingArrowItem(), 0.5F, true);
      registerItem("ironarrow", new IronArrowItem(), 0.8F, true);
      registerItem("bonearrow", new BoneArrowItem(), 1.2F, true);
      registerItem("spideritearrow", new SpideriteArrowItem(), 2.0F, true);
      registerItem("handgun", new HandGunProjectileToolItem(), 150.0F, true);
      registerItem("webbedgun", new WebbedGunProjectileToolItem(), 300.0F, true);
      registerItem("machinegun", new MachineGunProjectileToolItem(), 400.0F, true);
      registerItem("shotgun", new ShotgunProjectileToolItem(), 450.0F, true);
      registerItem("sniperrifle", new SniperProjectileToolItem(), 450.0F, true);
      registerItem("flintlock", new FlintlockProjectileToolItem(), 500.0F, true);
      registerItem("handcannon", new HandCannonProjectileToolItem(), 500.0F, true);
      registerItem("deathripper", new DeathRipperProjectileToolItem(), 600.0F, true);
      registerItem("cryoblaster", new CryoBlasterProjectileToolItem(), 650.0F, true);
      registerItem("livingshotty", new LivingShottyProjectileToolItem(), 750.0F, true);
      registerItem("antiquerifle", new AntiqueRifleProjectileToolItem(), 800.0F, true);
      registerItem("snowlauncher", new SnowLauncherProjectileToolItem(), 425.0F, true, false);
      registerItem("simplebullet", new SimpleBulletItem(), 0.1F, true);
      registerItem("frostbullet", new FrostBulletItem(), 0.1F, true);
      registerItem("bouncingbullet", new BouncingBulletItem(), 0.1F, true);
      registerItem("voidbullet", new VoidBulletItem(), 0.1F, true);
      registerItem("cannonball", new CannonballAmmoItem(), 2.0F, true);
      registerItem("woodstaff", new WoodStaffProjectileToolItem(), 30.0F, true);
      registerItem("sparkler", new SparklerProjectileToolItem(), 40.0F, true);
      registerItem("bloodbolt", new BloodBoltProjectileToolItem(), 60.0F, true);
      registerItem("venomstaff", new VenomStaffProjectileToolItem(), 100.0F, true);
      registerItem("froststaff", new FrostStaffProjectileToolItem(), 150.0F, true);
      registerItem("bloodvolley", new BloodVolleyProjectileToolItem(), 150.0F, true);
      registerItem("voidstaff", new VoidStaffProjectileToolItem(), 200.0F, true);
      registerItem("voidmissile", new VoidMissileProjectileToolItem(), 400.0F, true);
      registerItem("swamptome", new SwampTomeProjectileToolItem(), 500.0F, true);
      registerItem("boulderstaff", new BoulderStaffProjectileToolItem(), 400.0F, true);
      registerItem("quartzstaff", new QuartzStaffProjectileToolItem(), 200.0F, true);
      registerItem("dredgingstaff", new DredgingStaffProjectileToolItem(), 500.0F, true);
      registerItem("genielamp", new GenieLampProjectileToolItem(), 600.0F, true);
      registerItem("elderlywand", new ElderlyWandProjectileToolItem(), 600.0F, true);
      registerItem("shadowbolt", new ShadowBoltProjectileToolItem(), 100.0F, true);
      registerItem("iciclestaff", new IcicleStaffProjectileToolItem(), 260.0F, true);
      registerItem("shadowbeam", new ShadowBeamProjectileToolItem(), 650.0F, true);
      registerItem("cryoquake", new CryoQuakeProjectileToolItem(), 700.0F, true);
      registerItem("swampdwellerstaff", new SwampDwellerStaffProjectileToolItem(), 800.0F, true);
      registerItem("venomshower", new VenomShowerProjectileToolItem(), 800.0F, true);
      registerItem("ancientdredgingstaff", new AncientDredgingStaffProjectileToolItem(), 600.0F, true);
      registerItem("dragonlance", new DragonLanceProjectileToolItem(), 700.0F, true);
      registerItem("slimestaff", new SlimeStaffProjectileToolItem(), 400.0F, true);
      registerItem("phantompopper", new PhantomPopperProjectileToolItem(), 400.0F, true);
      registerItem("bloodgrimoire", new BloodGrimoireProjectileToolItem(), 700.0F, true);
      registerItem("webweaver", new WebWeaverToolItem(), 1000.0F, true);
      registerItem("goldenwebweaver", new WebWeaverToolItem(), 1000.0F, false);
      registerItem("flamelingorb", new FlamelingOrbProjectileToolItem(), 0.0F, false);
      registerItem("ninjastar", new NinjaStarToolItem(), 0.5F, true);
      registerItem("icejavelin", new IceJavelinToolItem(), 1.0F, true);
      registerItem("woodboomerang", new WoodBoomerangToolItem(), 20.0F, true);
      registerItem("spiderboomerang", new SpiderBoomerangToolItem(), 80.0F, true);
      registerItem("frostboomerang", new FrostBoomerangToolItem(), 90.0F, true);
      registerItem("hook", new HookBoomerangToolItem(), 200.0F, false);
      registerItem("voidboomerang", new VoidBoomerangToolItem(), 100.0F, true);
      registerItem("razorbladeboomerang", new RazorBladeBoomerangToolItem(), 150.0F, true);
      registerItem("tungstenboomerang", new TungstenBoomerangToolItem(), 60.0F, true);
      registerItem("boxingglovegun", new BoxingGloveGunToolItem(), 150.0F, true);
      registerItem("glacialboomerang", new GlacialBoomerangToolItem(), 180.0F, true);
      registerItem("carapacedagger", new CarapaceDaggerToolItem(), 220.0F, true);
      registerItem("dragonsrebound", new DragonsReboundToolItem(), 600.0F, true);
      registerItem("nightrazorboomerang", new NightRazorBoomerangToolItem(), 150.0F, true);
      registerItem("snowball", new SnowBallToolItem(), 0.1F, true);
      registerItem("brainonastick", new BrainOnAStickToolItem(), 400.0F, true);
      registerItem("spiderstaff", new SpiderStaffSummonToolItem(), 120.0F, true);
      registerItem("frostpiercer", new FrostPiercerSummonToolItem(), 200.0F, true);
      registerItem("magicbranch", new MagicBranchSummonToolItem(), 250.0F, true);
      registerItem("slimecanister", new SlimeCanisterSummonToolItem(), 400.0F, true);
      registerItem("vulturestaff", new VultureStaffSummonToolItem(), 400.0F, true);
      registerItem("cryostaff", new CryoStaffSummonToolItem(), 350.0F, true);
      registerItem("reaperscall", new ReapersCallSummonToolItem(), 700.0F, true);
      registerItem("swampsgrasp", new SwampsGraspToolItem(), 800.0F, true);
      registerItem("skeletonstaff", new SkeletonStaffToolItem(), 750.0F, true);
      registerItem("orbofslimes", new OrbOfSlimesToolItem(), 350.0F, true);
      registerItem("phantomcaller", new PhantomCallerSummonToolItem(), 400.0F, true);
      registerItem("empresscommand", new EmpressCommandToolItem(), 750.0F, true);
      registerItem("ironbomb", new IronBombToolItem(), 20.0F, true);
      registerItem("dynamitestick", new DynamiteStickToolItem(), 60.0F, true);
      registerItem("tilebomb", new TileBombToolItem(), 20.0F, true);
      registerItem("bannerofspeed", new BannerItem(Item.Rarity.COMMON, 480, (var0) -> {
         return BuffRegistry.Banners.SPEED;
      }), 200.0F, true);
      registerItem("bannerofdamage", new BannerItem(Item.Rarity.COMMON, 480, (var0) -> {
         return BuffRegistry.Banners.DAMAGE;
      }), 200.0F, true);
      registerItem("bannerofsummonspeed", new BannerItem(Item.Rarity.COMMON, 480, (var0) -> {
         return BuffRegistry.Banners.SUMMON_SPEED;
      }), 200.0F, true);
      registerItem("bannerofdefense", new BannerItem(Item.Rarity.COMMON, 480, (var0) -> {
         return BuffRegistry.Banners.DEFENSE;
      }), 200.0F, true);
      registerItem("mlg1", new MLG1SwordToolItem(), 1000.0F, false);
      registerItem("mlg2", new MLG2SwordToolItem(), 1000.0F, false);
      registerItem("mousetest", new MouseTestProjectileToolItem(), 0.0F, false);
      registerItem("mousebeam", new MouseBeamProjectileToolItem(), 0.0F, false);
      registerItem("steelboat", new SteelBoatMountItem(), 500.0F, true);
      registerItem("inefficientfeather", new MountItem("tameostrich"), 100.0F, true);
      registerItem("jumpingball", new JumpingBallMountItem(), 400.0F, true);
      registerItem("hoverboard", new HoverBoardMountItem(), 800.0F, true);
      registerItem("leatherglove", new SimpleTrinketItem(Item.Rarity.COMMON, "leatherglovetrinket", 200), 50.0F, true);
      registerItem("trackerboot", new SimpleTrinketItem(Item.Rarity.COMMON, "trackerboottrinket", 200), 60.0F, true);
      registerItem("shinebelt", new SimpleTrinketItem(Item.Rarity.COMMON, "shinebelttrinket", 200), 100.0F, true);
      registerItem("dreamcatcher", new SimpleTrinketItem(Item.Rarity.COMMON, "dreamcatchertrinket", 200), 50.0F, true);
      registerItem("nightmaretalisman", (new SimpleTrinketItem(Item.Rarity.COMMON, "nightmaretalismantrinket", 500)).addDisables(new String[]{"dreamcatcher"}), 100.0F, true);
      registerItem("prophecyslab", new SimpleTrinketItem(Item.Rarity.COMMON, "prophecyslabtrinket", 200), 100.0F, true);
      registerItem("magicmanual", new SimpleTrinketItem(Item.Rarity.COMMON, "magicmanualtrinket", 200), 100.0F, true);
      registerItem("scryingcards", (new SimpleTrinketItem(Item.Rarity.COMMON, "scryingcardstrinket", 200)).addDisables(new String[]{"magicmanual", "prophecyslab"}), 100.0F, true);
      registerItem("forbiddenspellbook", new SimpleTrinketItem(Item.Rarity.COMMON, "forbiddenspellbooktrinket", 200), 100.0F, true);
      registerItem("explorersatchel", new CombinedTrinketItem(Item.Rarity.UNCOMMON, 500, new String[]{"leatherglove", "trackerboot", "shinebelt"}), 200.0F, true);
      registerItem("vampiresgift", new SimpleTrinketItem(Item.Rarity.COMMON, "vampiresgifttrinket", 300), 150.0F, true);
      registerItem("leatherdashers", new LeatherDashersTrinketItem(), 50.0F, true);
      registerItem("zephyrcharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "zephyrcharmtrinket", 200), 50.0F, true);
      registerItem("zephyrboots", (new ZephyrBootsTrinketItem()).addDisables(new String[]{"zephyrcharm", "leatherdashers"}), 200.0F, true);
      registerItem("forceofwind", new ForceOfWindTrinketItem(), 200.0F, true);
      registerItem("woodshield", new WoodShieldTrinketItem(Item.Rarity.COMMON, 2, 0.5F, 10000, 0.25F, 60, 360.0F, 200), 100.0F, true);
      registerItem("hardenedshield", new ShieldTrinketItem(Item.Rarity.COMMON, 4, 0.5F, 9000, 0.25F, 50, 360.0F, 400), 200.0F, true);
      registerItem("cactusshield", new CactusShieldTrinketItem(Item.Rarity.UNCOMMON, 400), 300.0F, true);
      registerItem("shellofretribution", (new CactusShieldTrinketItem(Item.Rarity.RARE, 600)).addCombinedTrinkets(new String[]{"spidercharm"}), 600.0F, true);
      registerItem("tungstenshield", new ShieldTrinketItem(Item.Rarity.UNCOMMON, 6, 0.5F, 7000, 0.25F, 30, 360.0F, 800), 250.0F, true);
      registerItem("agedchampionshield", new ShieldTrinketItem(Item.Rarity.RARE, 9, 0.5F, 2000, 100.0F, 0, 360.0F, 900), 600.0F, true);
      registerItem("siphonshield", new SiphonShieldTrinketItem(Item.Rarity.RARE, 900), 500.0F, true);
      registerItem("carapaceshield", new ShieldTrinketItem(Item.Rarity.RARE, 12, 1.0F, 5000, 0.1F, 10, 360.0F, 1000), 500.0F, true);
      registerItem("sparegemstones", new SimpleTrinketItem(Item.Rarity.COMMON, "sparegemstonestrinket", 200), 100.0F, true);
      registerItem("spellstone", (new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spellstonetrinket", 200)).addDisables(new String[]{"sparegemstones"}), 300.0F, true);
      registerItem("blinkscepter", new BlinkScepterTrinketItem(), 800.0F, true);
      registerItem("magicfoci", (new SimpleTrinketItem(Item.Rarity.COMMON, "magicfocitrinket", 500)).addDisables(new String[]{"rangefoci", "meleefoci", "summonfoci"}), 200.0F, true);
      registerItem("rangefoci", (new SimpleTrinketItem(Item.Rarity.COMMON, "rangefocitrinket", 500)).addDisables(new String[]{"magicfoci", "meleefoci", "summonfoci"}), 200.0F, true);
      registerItem("meleefoci", (new SimpleTrinketItem(Item.Rarity.COMMON, "meleefocitrinket", 500)).addDisables(new String[]{"magicfoci", "rangefoci", "summonfoci"}), 200.0F, true);
      registerItem("summonfoci", (new SimpleTrinketItem(Item.Rarity.COMMON, "summonfocitrinket", 500)).addDisables(new String[]{"magicfoci", "rangefoci", "meleefoci"}), 200.0F, true);
      registerItem("balancedfoci", (new SimpleTrinketItem(Item.Rarity.UNCOMMON, "balancedfocitrinket", 600)).addDisables(new String[]{"magicfoci", "rangefoci", "meleefoci", "summonfoci"}), 600.0F, true);
      registerItem("demonclaw", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "demonclawtrinket", 500), 40.0F, true);
      registerItem("fins", new SimpleTrinketItem(Item.Rarity.COMMON, "finstrinket", 200), 150.0F, true);
      registerItem("fuzzydice", new SimpleTrinketItem(Item.Rarity.COMMON, "fuzzydicetrinket", 200), 100.0F, true);
      registerItem("noblehorseshoe", new SimpleTrinketItem(Item.Rarity.COMMON, "noblehorseshoetrinket", 250), 150.0F, true);
      registerItem("luckycape", (new SimpleTrinketItem(Item.Rarity.UNCOMMON, "luckycapetrinket", 350)).addDisables(new String[]{"fuzzydice", "noblehorseshoe"}), 200.0F, true);
      registerItem("miningcharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "miningcharmtrinket", 300), 250.0F, true);
      registerItem("ancientfeather", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "ancientfeathertrinket", 500), 300.0F, true);
      registerItem("assassinscowl", new CombinedTrinketItem(Item.Rarity.RARE, 600, new String[]{"luckycape", "ancientfeather"}), 400.0F, true);
      registerItem("airvessel", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "airvesseltrinket", 500), 300.0F, true);
      registerItem("regenpendant", new SimpleTrinketItem(Item.Rarity.COMMON, "regenpendanttrinket", 400), 75.0F, true);
      registerItem("calmingrose", new SimpleTrinketItem(Item.Rarity.RARE, "calmingrosetrinket", 400), 300.0F, true);
      registerItem("calmingminersbouquet", (new CalmingMinersBouquetTrinketItem()).addDisables(new String[]{"miningcharm", "calmingrose"}), 500.0F, true);
      registerItem("minersbouquet", (new MinersBouquetTrinketItem()).addDisables(new String[]{"miningcharm", "calmingrose"}).addDisabledBy("calmingminersbouquet"), 500.0F, false);
      registerItem("mobilitycloak", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "mobilitycloaktrinket", 400), 200.0F, true);
      registerItem("travelercloak", (new SimpleTrinketItem(Item.Rarity.UNCOMMON, "travelercloaktrinket", 600)).addDisables(new String[]{"mobilitycloak", "fins"}), 320.0F, true);
      registerItem("mesmertablet", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "mesmertablettrinket", 600), 200.0F, true);
      registerItem("magicalquiver", new SimpleTrinketItem(Item.Rarity.RARE, "magicalquivertrinket", 500), 500.0F, true);
      registerItem("ammobox", new SimpleTrinketItem(Item.Rarity.RARE, "ammoboxtrinket", 700), 700.0F, true);
      registerItem("frozenheart", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "frozenhearttrinket", 500), 300.0F, true);
      registerItem("frozenwave", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "frozenwavetrinket", 500), 300.0F, true);
      registerItem("spidercharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spidercharmtrinket", 600), 400.0F, true);
      registerItem("guardianshell", new SimpleTrinketItem(Item.Rarity.RARE, "guardianshelltrinket", 700), 500.0F, true);
      registerItem("inducingamulet", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "inducingamulettrinket", 700), 300.0F, true);
      registerItem("polarclaw", new SimpleTrinketItem(Item.Rarity.RARE, "polarclawtrinket", 800), 200.0F, true);
      registerItem("piratetelescope", new SimpleTrinketItem(Item.Rarity.RARE, "piratetelescopetrinket", 800), 500.0F, true);
      registerItem("lifeline", new SimpleTrinketItem(Item.Rarity.RARE, "lifelinettrinket", 800), 350.0F, true);
      registerItem("explorercloak", (new SimpleTrinketItem(Item.Rarity.RARE, "explorercloaktrinket", 800)).addDisables(new String[]{"travelercloak", "mobilitycloak", "fins"}), 700.0F, true);
      registerItem("frozensoul", new CombinedTrinketItem(Item.Rarity.RARE, 800, new String[]{"lifeline", "frozenheart"}), 600.0F, true);
      registerItem("ninjasmark", new SimpleTrinketItem(Item.Rarity.RARE, "ninjasmarktrinket", 800), 350.0F, true);
      registerItem("bonehilt", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "bonehilttrinket", 500), 160.0F, true);
      registerItem("firestone", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "firestonetrinket", 600), 400.0F, true);
      registerItem("lifependant", (new SimpleTrinketItem(Item.Rarity.UNCOMMON, "lifependanttrinket", 600)).addDisables(new String[]{"regenpendant"}), 200.0F, true);
      registerItem("spikedboots", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spikedbootstrinket", 600), 350.0F, true);
      registerItem("spikedbatboots", (new SimpleTrinketItem(Item.Rarity.RARE, "spikedbatbootstrinket", 1000)).addDisables(new String[]{"spikedboots", "vampiresgift"}), 450.0F, true);
      registerItem("froststone", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "froststonetrinket", 600), 400.0F, true);
      registerItem("frostflame", (new SimpleTrinketItem(Item.Rarity.RARE, "frostflametrinket", 1000)).addDisables(new String[]{"froststone", "firestone"}), 600.0F, true);
      registerItem("balancedfrostfirefoci", new CombinedTrinketItem(Item.Rarity.EPIC, 1200, new String[]{"balancedfoci", "frostflame"}), 900.0F, true);
      registerItem("hysteriatablet", (new SimpleTrinketItem(Item.Rarity.RARE, "hysteriatablettrinket", 800)).addDisables(new String[]{"mesmertablet", "inducingamulet"}), 400.0F, true);
      registerItem("frenzyorb", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "frenzyorbtrinket", 1000), 200.0F, true);
      registerItem("scryingmirror", new SimpleTrinketItem(Item.Rarity.RARE, "scryingmirrortrinket", 1100), 600.0F, true);
      registerItem("diggingclaw", new SimpleTrinketItem(Item.Rarity.RARE, "diggingclawtrinket", 1000), 600.0F, true);
      registerItem("templependant", new SimpleTrinketItem(Item.Rarity.RARE, "templependanttrinket", 1000), 700.0F, true);
      registerItem("ancientrelics", (new SimpleTrinketItem(Item.Rarity.RARE, "ancientrelicstrinket", 1200)).addDisables(new String[]{"airvessel", "templependant"}), 800.0F, true);
      registerItem("gelatincasings", new SimpleTrinketItem(Item.Rarity.EPIC, "gelatincasingstrinket", 1000), 250.0F, true);
      registerItem("bloodstonering", new SimpleTrinketItem(Item.Rarity.EPIC, "bloodstoneringtrinket", 1000), 250.0F, true);
      registerItem("claygauntlet", new SimpleTrinketItem(Item.Rarity.COMMON, "claygauntlettrinket", 300), 100.0F, true);
      registerItem("chainshirt", new SimpleTrinketItem(Item.Rarity.COMMON, "chainshirttrinket", 300), 100.0F, true);
      registerItem("vambrace", new SimpleTrinketItem(Item.Rarity.RARE, "vambracetrinket", 300), 100.0F, true);
      registerItem("manica", (new SimpleTrinketItem(Item.Rarity.EPIC, "manicatrinket", 800)).addDisables(new String[]{"vambrace", "chainshirt"}), 200.0F, true);
      registerItem("agedchampionscabbard", new SimpleTrinketItem(Item.Rarity.RARE, "agedchampionscabbardtrinket", 600), 350.0F, true);
      registerItem("challengerspauldron", new SimpleTrinketItem(Item.Rarity.RARE, "challengerspauldrontrinket", 800), 350.0F, true);
      registerItem("clockworkheart", new SimpleTrinketItem(Item.Rarity.EPIC, "clockworkhearttrinket", 1000), 800.0F, true);
      registerItem("foolsgambit", new FoolsGambitTrinketItem(Item.Rarity.EPIC, "foolsgambittrinket", 1100), 850.0F, true);
      registerItem("constructionhammer", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "constructionhammertrinket", 400), 200.0F, true);
      registerItem("telescopicladder", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "telescopicladdertrinket", 400), 200.0F, true);
      registerItem("toolextender", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "toolextendertrinket", 400), 200.0F, true);
      registerItem("itemattractor", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "itemattractortrinket", 400), 200.0F, true);
      registerItem("toolbox", new CombinedTrinketItem(Item.Rarity.RARE, 800, new String[]{"constructionhammer", "telescopicladder", "toolextender", "itemattractor"}), 600.0F, true);
      registerItem("leatherhood", new LeatherHoodArmorItem(), 25.0F, true);
      registerItem("leathershirt", new LeatherShirtArmorItem(), 55.0F, true);
      registerItem("leatherboots", new LeatherBootsArmorItem(), 10.0F, true);
      registerItem("clothhat", new ClothHatArmorItem(), 25.0F, true);
      registerItem("clothrobe", new ClothRobeArmorItem(), 55.0F, true);
      registerItem("clothboots", new ClothBootsArmorItem(), 10.0F, true);
      registerItem("copperhelmet", new CopperHelmetArmorItem(), 15.0F, true);
      registerItem("copperchestplate", new CopperChestplateArmorItem(), 30.0F, true);
      registerItem("copperboots", new CopperBootsArmorItem(), 5.0F, true);
      registerItem("ironhelmet", new IronHelmetArmorItem(), 20.0F, true);
      registerItem("ironchestplate", new IronChestplateArmorItem(), 45.0F, true);
      registerItem("ironboots", new IronBootsArmorItem(), 8.0F, true);
      registerItem("goldcrown", new GoldCrownArmorItem(), 50.0F, true);
      registerItem("goldhelmet", new GoldHelmetArmorItem(), 50.0F, true);
      registerItem("goldchestplate", new GoldChestplateArmorItem(), 80.0F, true);
      registerItem("goldboots", new GoldBootsArmorItem(), 30.0F, true);
      registerItem("spiderhelmet", new SpiderHelmetArmorItem(), 50.0F, true);
      registerItem("spiderchestplate", new SpiderChestplateArmorItem(), 75.0F, true);
      registerItem("spiderboots", new SpiderBootsArmorItem(), 40.0F, true);
      registerItem("frosthelmet", new FrostHelmetArmorItem(), 60.0F, true);
      registerItem("frosthood", new FrostHoodArmorItem(), 60.0F, true);
      registerItem("frosthat", new FrostHatArmorItem(), 60.0F, true);
      registerItem("frostchestplate", new FrostChestplateArmorItem(), 80.0F, true);
      registerItem("frostboots", new FrostBootsArmorItem(), 40.0F, true);
      registerItem("demonichelmet", new DemonicHelmetArmorItem(), 45.0F, true);
      registerItem("demonicchestplate", new DemonicChestplateArmorItem(), 80.0F, true);
      registerItem("demonicboots", new DemonicBootsArmorItem(), 20.0F, true);
      registerItem("voidmask", new VoidMaskArmorItem(), 100.0F, true);
      registerItem("voidhat", new VoidHatArmorItem(), 100.0F, true);
      registerItem("voidrobe", new VoidRobeArmorItem(), 140.0F, true);
      registerItem("voidboots", new VoidBootsArmorItem(), 60.0F, true);
      registerItem("bloodplatecowl", new BloodplateCowlArmorItem(), 55.0F, true);
      registerItem("bloodplatechestplate", new BloodplateChestplateArmorItem(), 65.0F, true);
      registerItem("bloodplateboots", new BloodplateBootsArmorItem(), 50.0F, true);
      registerItem("ivyhelmet", new IvyHelmetArmorItem(), 75.0F, true);
      registerItem("ivyhood", new IvyHoodArmorItem(), 75.0F, true);
      registerItem("ivyhat", new IvyHatArmorItem(), 75.0F, true);
      registerItem("ivycirclet", new IvyCircletArmorItem(), 75.0F, true);
      registerItem("ivychestplate", new IvyChestplateArmorItem(), 125.0F, true);
      registerItem("ivyboots", new IvyBootsArmorItem(), 50.0F, true);
      registerItem("quartzhelmet", new QuartzHelmetArmorItem(), 100.0F, true);
      registerItem("quartzcrown", new QuartzCrownArmorItem(), 100.0F, true);
      registerItem("quartzchestplate", new QuartzChestplateArmorItem(), 150.0F, true);
      registerItem("quartzboots", new QuartzBootsArmorItem(), 50.0F, true);
      registerItem("tungstenhelmet", new TungstenHelmetArmorItem(), 110.0F, true);
      registerItem("tungstenchestplate", new TungstenChestplateArmorItem(), 160.0F, true);
      registerItem("tungstenboots", new TungstenBootsArmorItem(), 80.0F, true);
      registerItem("shadowhat", new ShadowHatArmorItem(), 110.0F, true);
      registerItem("shadowhood", new ShadowHoodArmorItem(), 110.0F, true);
      registerItem("shadowmantle", new ShadowMantleArmorItem(), 160.0F, true);
      registerItem("shadowboots", new ShadowBootsArmorItem(), 80.0F, true);
      registerItem("ninjahood", new NinjaHoodArmorItem(), 120.0F, true);
      registerItem("ninjarobe", new NinjaRobeArmorItem(), 200.0F, true);
      registerItem("ninjashoes", new NinjaShoesArmorItem(), 80.0F, true);
      registerItem("glacialcirclet", new GlacialCircletArmorItem(), 110.0F, true);
      registerItem("glacialhelmet", new GlacialHelmetArmorItem(), 110.0F, true);
      registerItem("glacialchestplate", new GlacialChestplateArmorItem(), 160.0F, true);
      registerItem("glacialboots", new GlacialBootsArmorItem(), 80.0F, true);
      registerItem("myceliumhood", new MyceliumHoodArmorItem(), 110.0F, true);
      registerItem("myceliumscarf", new MyceliumScarfArmorItem(), 110.0F, true);
      registerItem("myceliumchestplate", new MyceliumChestplateArmorItem(), 160.0F, true);
      registerItem("myceliumboots", new MyceliumBootsArmorItem(), 80.0F, true);
      registerItem("widowhelmet", new WidowHelmetArmorItem(), 120.0F, true);
      registerItem("widowchestplate", new WidowChestplateArmorItem(), 160.0F, true);
      registerItem("widowboots", new WidowBootsArmorItem(), 80.0F, true);
      registerItem("ancientfossilmask", new AncientFossilMaskArmorItem(), 110.0F, true);
      registerItem("ancientfossilhelmet", new AncientFossilHelmetArmorItem(), 110.0F, true);
      registerItem("ancientfossilchestplate", new AncientFossilChestplateArmorItem(), 160.0F, true);
      registerItem("ancientfossilboots", new AncientFossilBootsArmorItem(), 80.0F, true);
      registerItem("slimehat", new SlimeHatArmorItem(), 300.0F, true);
      registerItem("slimehelmet", new SlimeHelmetArmorItem(), 300.0F, true);
      registerItem("slimechestplate", new SlimeChestplateArmorItem(), 400.0F, true);
      registerItem("slimeboots", new SlimeBootsArmorItem(), 200.0F, true);
      registerItem("nightsteelhelmet", new NightsteelHelmetArmorItem(), 150.0F, true);
      registerItem("nightsteelmask", new NightsteelMaskArmorItem(), 150.0F, true);
      registerItem("nightsteelveil", new NightsteelVeilArmorItem(), 150.0F, true);
      registerItem("nightsteelcirclet", new NightsteelCircletArmorItem(), 150.0F, true);
      registerItem("nightsteelchestplate", new NightsteelChestplateArmorItem(), 200.0F, true);
      registerItem("nightsteelboots", new NightsteelBootsArmorItem(), 120.0F, true);
      registerItem("spideritehelmet", new SpideriteHelmetArmorItem(), 200.0F, true);
      registerItem("spideritehat", new SpideriteHatArmorItem(), 200.0F, true);
      registerItem("spideritehood", new SpideriteHoodArmorItem(), 200.0F, true);
      registerItem("spideritecrown", new SpideriteCrownArmorItem(), 200.0F, true);
      registerItem("spideritechestplate", new SpideriteChestplateArmorItem(), 300.0F, true);
      registerItem("spideritegreaves", new SpideriteGreavesArmorItem(), 150.0F, true);
      registerItem("ravenlordsheaddress", new RavenlordsHeaddressArmorItem(), 150.0F, true);
      registerItem("ravenlordschestplate", new RavenlordsChestplateArmorItem(), 200.0F, true);
      registerItem("ravenlordsboots", new RavenlordsBootsArmorItem(), 120.0F, true);
      registerItem("dawnhelmet", new DawnHelmetArmorItem(), 150.0F, true);
      registerItem("dawnchestplate", new DawnChestplateArmorItem(), 150.0F, true);
      registerItem("dawnboots", new DawnBootsArmorItem(), 150.0F, true);
      registerItem("duskhelmet", new DuskHelmetArmorItem(), 150.0F, true);
      registerItem("duskchestplate", new DuskChestplateArmorItem(), 150.0F, true);
      registerItem("duskboots", new DuskBootsArmorItem(), 150.0F, true);
      registerItem("shoes", new ShoesArmorItem(0), 100.0F, true);
      registerItem("shirt", new ShirtArmorItem(0), 100.0F, true);
      registerItem("wig", new WigArmorItem(0), 100.0F, true);
      registerItem("cheatshoes", new ShoesArmorItem(1) {
         public GameMessage getNewLocalization() {
            return new StaticMessage("Cheat Shoes");
         }

         public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
            return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 1000), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 50.0F), new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 1000), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN_FLAT, 10.0F)});
         }
      }, 0.0F, false);
      registerItem("cheatshirt", new ShirtArmorItem(1) {
         public GameMessage getNewLocalization() {
            return new StaticMessage("Cheat Shirt");
         }

         public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
            return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 1000), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 50.0F), new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 1000), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN_FLAT, 10.0F)});
         }
      }, 0.0F, false);
      registerItem("cheatwig", new WigArmorItem(1) {
         public GameMessage getNewLocalization() {
            return new StaticMessage("Cheat Wig");
         }

         public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
            return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 1000), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 50.0F), new ModifierValue(BuffModifiers.MAX_MANA_FLAT, 1000), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN_FLAT, 10.0F)});
         }
      }, 0.0F, false);
      registerItem("farmerhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "farmerhat"), 15.0F, true);
      registerItem("farmershirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "farmershirt", "farmershirtarms"), 35.0F, true);
      registerItem("farmershoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "farmershoes"), 10.0F, true);
      registerItem("farmerpitchfork", new Item(1) {
         public GameMessage getNewLocalization() {
            return new StaticMessage("farmerpitchfork");
         }
      }, 50.0F, false);
      registerItem("labcoat", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "labcoat", "labcoatarms"), 50.0F, false);
      registerItem("labapron", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "labapron", "labapronarms"), 50.0F, false);
      registerItem("labboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "labboots"), 50.0F, false);
      registerItem("rainhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "rainhat"), 50.0F, false);
      registerItem("raincoat", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "raincoat", "raincoatarms"), 50.0F, false);
      registerItem("rainboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "rainboots"), 50.0F, false);
      registerItem("animalkeepershirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "animalkeepershirt", "animalkeepershirtarms"), 50.0F, false);
      registerItem("animalkeepershoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "animalkeepershoes"), 50.0F, false);
      registerItem("smithingapron", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "smithingapron", "smithingapronarms"), 50.0F, false);
      registerItem("smithingshoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "smithingshoes"), 50.0F, false);
      registerItem("hardhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "hardhat"), 50.0F, false);
      registerItem("elderhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "elderhat"), 100.0F, true);
      registerItem("eldershirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "eldershirt", "eldershirtarms"), 50.0F, false);
      registerItem("eldershoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "eldershoes"), 50.0F, false);
      registerItem("safarihat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "safarihat"), 50.0F, false);
      registerItem("safarishirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "safarishirt", "safarishirtarms"), 50.0F, false);
      registerItem("safarishoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "safarishoes"), 50.0F, false);
      registerItem("minershirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "minershirt", "minershirtarms"), 50.0F, false);
      registerItem("minerboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "minerboots"), 50.0F, false);
      registerItem("hunterhood", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "hunterhood"), 50.0F, false);
      registerItem("huntershirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "huntershirt", "huntershirtarms"), 50.0F, false);
      registerItem("hunterboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "hunterboots"), 50.0F, false);
      registerItem("magehat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "magehat"), 50.0F, false);
      registerItem("magerobe", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "magerobe", "magerobearms"), 50.0F, false);
      registerItem("mageshoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "mageshoes"), 50.0F, false);
      registerItem("tophat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "tophat"), 50.0F, false);
      registerItem("blazer", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "blazer", "blazerarms"), 50.0F, false);
      registerItem("dressshoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "dressshoes"), 50.0F, false);
      registerItem("stylistshirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "stylistshirt", "stylistshirtarms"), 50.0F, false);
      registerItem("stylistshoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "stylistshoes"), 50.0F, false);
      registerItem("merchantshirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "merchantshirt", "merchantshirtarms"), 50.0F, false);
      registerItem("merchantboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "merchantboots"), 50.0F, false);
      registerItem("captainshat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "captainshat"), 200.0F, true);
      registerItem("captainsshirt", new ChestArmorItem(0, 0, Item.Rarity.UNCOMMON, "captainsshirt", "captainsarms"), 200.0F, true);
      registerItem("pirateboots", new BootsArmorItem(0, 0, Item.Rarity.UNCOMMON, "pirateboots"), 150.0F, true);
      registerItem("piratebandana", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "piratebandana"), 150.0F, true);
      registerItem("pirateshirt", new ChestArmorItem(0, 0, Item.Rarity.UNCOMMON, "pirateshirt", "piratearms"), 150.0F, true);
      registerItem("vulturemask", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.RARE, "vulturemask"), 200.0F, true);
      registerItem("plaguemask", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "plaguemask"), 150.0F, true);
      registerItem("plaguerobe", new ChestArmorItem(0, 0, Item.Rarity.UNCOMMON, "plaguerobe", "plaguearms"), 150.0F, true);
      registerItem("plagueboots", new BootsArmorItem(0, 0, Item.Rarity.UNCOMMON, "plagueboots"), 150.0F, true);
      registerItem("surgicalmask", new SurgicalMaskArmorItem(), 50.0F, true);
      registerItem("trapperhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "trapperhat"), 20.0F, true);
      registerItem("horsemask", (new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.RARE, "horsemask")).drawBodyPart(false), 50.0F, true);
      registerItem("horsecostumeshirt", new ChestArmorItem(0, 0, Item.Rarity.RARE, "horsecostumeshirt", "horsecostumearms"), 50.0F, true);
      registerItem("horsecostumeboots", new BootsArmorItem(0, 0, Item.Rarity.RARE, "horsecostumeboots"), 50.0F, true);
      registerItem("christmashat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "christmashat"), 10.0F, true, false);
      registerItem("partyhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "partyhat"), 10.0F, true, false);
      registerItem("pumpkinmask", new LightHelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "pumpkinmask"), 10.0F, true, false);
      registerItem("chickenmask", (new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.RARE, "chickenmask")).drawBodyPart(false), 50.0F, true);
      registerItem("chickencostumeshirt", new ChestArmorItem(0, 0, Item.Rarity.RARE, "chickencostumeshirt", "chickencostumearms"), 50.0F, true);
      registerItem("chickencostumeboots", new BootsArmorItem(0, 0, Item.Rarity.RARE, "chickencostumeboots"), 50.0F, true);
      registerItem("frogmask", (new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.RARE, "frogmask")).headArmorBackTexture("frogmask_back"), 50.0F, true);
      registerItem("frogcostumeshirt", new ChestArmorItem(0, 0, Item.Rarity.RARE, "frogcostumeshirt", "frogcostumearms"), 50.0F, true);
      registerItem("frogcostumeboots", new BootsArmorItem(0, 0, Item.Rarity.RARE, "frogcostumeboots"), 50.0F, true);
      registerItem("alienmask", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.RARE, "alienmask"), 50.0F, true);
      registerItem("aliencostumeshirt", new ChestArmorItem(0, 0, Item.Rarity.RARE, "aliencostumeshirt", "aliencostumearms"), 50.0F, true);
      registerItem("aliencostumeboots", new BootsArmorItem(0, 0, Item.Rarity.RARE, "aliencostumeboots"), 50.0F, true);
      registerItem("sunglasses", (new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "sunglasses")).headArmorBackTexture("sunglassesback").hairDrawMode(ArmorItem.HairDrawMode.OVER_HAIR), 50.0F, true);
      registerItem("jesterhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "jesterhat"), 50.0F, true);
      registerItem("jestershirt", new ChestArmorItem(0, 0, Item.Rarity.UNCOMMON, "jestershirt", "jesterarms"), 50.0F, true);
      registerItem("jesterboots", new BootsArmorItem(0, 0, Item.Rarity.UNCOMMON, "jesterboots"), 50.0F, true);
      registerItem("hulahat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.UNCOMMON, "hulahat"), 50.0F, true);
      registerItem("hulaskirtwithtop", new ChestArmorItem(0, 0, Item.Rarity.UNCOMMON, "hulaskirtwithtop", (String)null), 50.0F, true);
      registerItem("hulaskirt", new ChestArmorItem(0, 0, Item.Rarity.UNCOMMON, "hulaskirt", (String)null), 50.0F, true);
      registerItem("swimsuit", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "swimsuit", (String)null), 50.0F, true);
      registerItem("swimtrunks", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "swimtrunks", (String)null), 50.0F, true);
      registerItem("snowhood", (new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "snowhood")).headArmorBackTexture("snowhoodback"), 50.0F, true);
      registerItem("snowcloak", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "snowcloak", "snowarms"), 50.0F, true);
      registerItem("snowboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "snowboots"), 50.0F, true);
      registerItem("sailorhat", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "sailorhat"), 50.0F, true);
      registerItem("sailorshirt", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "sailorshirt", "sailorarms"), 50.0F, true);
      registerItem("sailorshoes", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "sailorshoes"), 50.0F, true);
      registerItem("spacehelmet", new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.COMMON, "spacehelmet"), 50.0F, true);
      registerItem("spacesuit", new ChestArmorItem(0, 0, Item.Rarity.COMMON, "spacesuit", "spacesuitarms"), 50.0F, true);
      registerItem("spaceboots", new BootsArmorItem(0, 0, Item.Rarity.COMMON, "spaceboots"), 50.0F, true);
      registerItem("empressmask", (new HelmetArmorItem(0, (DamageType)null, 0, Item.Rarity.LEGENDARY, "empressmask")).headArmorBackTexture("empressmaskback").hairDrawMode(ArmorItem.HairDrawMode.OVER_HAIR), 50.0F, true);
      registerItem("carp", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("cod", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("herring", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("mackerel", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("salmon", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("trout", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("tuna", (new FishItem(100, Item.Rarity.COMMON, new String[]{"anycommonfish"})).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0F, true);
      registerItem("gobfish", (new FishItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 16.0F, true);
      registerItem("terrorfish", (new FishItem(100, Item.Rarity.RARE, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 30.0F, true);
      registerItem("halffish", (new FishItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 16.0F, true);
      registerItem("rockfish", (new FishItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 16.0F, true);
      registerItem("furfish", (new FishItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 16.0F, true);
      registerItem("icefish", (new FishItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 16.0F, true);
      registerItem("swampfish", (new FishItem(100, Item.Rarity.UNCOMMON, new String[0])).setItemCategory(new String[]{"materials", "specialfish"}), 16.0F, true);
      registerItem("spoiledfood", (new MatItem(500, Item.Rarity.NORMAL, "compostabletip", new String[]{"anycompostable"})).setItemCategory(new String[]{"materials"}), 0.1F, true);
      getItem("mushroom").setItemCategory("materials", "flowers");
      registerItem("wheat", (new GrainItem(100, Item.Rarity.NORMAL, new String[0])).cropTexture("wheat").spoilDuration(960).addGlobalIngredient("anycompostable"), 2.0F, true);
      registerItem("sugarbeet", (new FoodMatItem(100, Item.Rarity.NORMAL, new String[0])).cropTexture("sugarbeet").spoilDuration(480).addGlobalIngredient("anycompostable"), 2.0F, true);
      registerItem("flour", (new FoodMatItem(100, Item.Rarity.NORMAL, new String[0])).spoilDuration(960), 3.0F, true);
      registerItem("sugar", (new FoodMatItem(100, Item.Rarity.NORMAL, new String[0])).spoilDuration(960), 3.0F, true);
      registerItem("honey", new FoodMatItem(100, Item.Rarity.NORMAL, new String[0]), 5.0F, true);
      registerItem("rice", (new FoodMatItem(100, Item.Rarity.NORMAL, new String[0])).cropTexture("rice").spoilDuration(960), 3.0F, true);
      registerItem("groundcoffee", (new FoodMatItem(100, Item.Rarity.NORMAL, new String[0])).cropTexture("coffee").spoilDuration(480), 6.0F, true);
      registerItem("beef", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)})).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 4.0F, true);
      registerItem("rawmutton", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)})).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 4.0F, true);
      registerItem("rawpork", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)})).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0F, true);
      registerItem("rabbitleg", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)})).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0F, true);
      registerItem("duckbreast", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)})).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0F, true);
      registerItem("frogleg", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, -10)})).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0F, true);
      registerItem("corn", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10)})).cropTexture("corn").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("tomato", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F)})).cropTexture("tomato").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("cabbage", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F)})).cropTexture("cabbage").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("chilipepper", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.02F)})).cropTexture("chilipepper").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("eggplant", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.02F)})).cropTexture("eggplant").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("potato", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F)})).cropTexture("potato").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("carrot", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F)})).cropTexture("carrot").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("onion", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F)})).cropTexture("onion").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 3.0F, true);
      registerItem("pumpkin", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 30, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20)})).cropTexture("pumpkin").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 5.0F, true);
      registerItem("strawberry", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)})).cropTexture("strawberry").spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 3.0F, true);
      registerItem("apple", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.02F)})).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 14.0F, true);
      registerItem("lemon", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20)})).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 16.0F, true);
      registerItem("coconut", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F)})).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 14.0F, true);
      registerItem("banana", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F)})).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 16.0F, true);
      registerItem("blueberry", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.02F)})).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 4.0F, true);
      registerItem("blackberry", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.02F)})).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 4.0F, true);
      registerItem("milk", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 480, true, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10)})).spoilDuration(240).setItemCategory("consumable", "rawfood"), 2.0F, true);
      registerItem("steak", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.02F)})).spoilDuration(240), 5.0F, true);
      registerItem("roastedmutton", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 240, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.02F)})).spoilDuration(240), 5.0F, true);
      registerItem("roastedpork", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.02F)})).spoilDuration(240), 12.0F, true);
      registerItem("roastedrabbitleg", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F)})).spoilDuration(240), 7.0F, true);
      registerItem("roastedduckbreast", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20)})).spoilDuration(240), 7.0F, true);
      registerItem("roastedfrogleg", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F)})).spoilDuration(240), 7.0F, true);
      registerItem("roastedfish", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F)})).spoilDuration(240), 14.0F, true);
      registerItem("bread", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 20, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20)})).spoilDuration(480), 8.0F, true);
      registerItem("cheese", (new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20)})).spoilDuration(480), 5.0F, true);
      registerItem("candyapple", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 30, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.05F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.02F)})).spoilDuration(240), 22.0F, true);
      registerItem("popcorn", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 30, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20)})).spoilDuration(240), 10.0F, true);
      registerItem("donut", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 20, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F)})).spoilDuration(240), 10.0F, true);
      registerItem("cookies", (new FoodConsumableItem(100, Item.Rarity.COMMON, Settler.FOOD_FINE, 15, 480, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 10)})).spoilDuration(360), 10.0F, true);
      registerItem("candycane", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_FINE, 10, 300, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.15F)})).spoilDuration(1080), 10.0F, true, false);
      registerItem("meatballs", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)})).spoilDuration(120), 14.0F, true);
      registerItem("smokedfillet", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.07F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F)})).spoilDuration(120), 18.0F, true);
      registerItem("blueberrycake", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F)})).spoilDuration(120), 26.0F, true);
      registerItem("blackberryicecream", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.05F)})).spoilDuration(120), 24.0F, true);
      registerItem("fruitsmoothie", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, true, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)})).spoilDuration(120), 36.0F, true);
      registerItem("fishtaco", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F)})).spoilDuration(120), 24.0F, true);
      registerItem("juniorburger", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 20), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)})).spoilDuration(120), 22.0F, true);
      registerItem("cheeseburger", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)})).spoilDuration(120), 26.0F, true);
      registerItem("nachos", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F)})).spoilDuration(120), 20.0F, true);
      registerItem("eggplantparmesan", (new FoodConsumableItem(100, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F), new ModifierValue(BuffModifiers.SPEED, 0.1F)})).spoilDuration(120), 20.0F, true);
      registerItem("tropicalstew", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.05F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.ARMOR_FLAT, 4)})).spoilDuration(120), 40.0F, true);
      registerItem("fishandchips", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.SPEED, 0.1F)})).spoilDuration(120), 30.0F, true);
      registerItem("freshpotatosalad", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 30), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)})).spoilDuration(120), 45.0F, true);
      registerItem("hotdog", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 1.0F)})).spoilDuration(120), 45.0F, true);
      registerItem("ricepudding", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.SPEED, 0.15F)})).spoilDuration(120), 22.0F, true);
      registerItem("minersstew", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 50, 960, new ModifierValue[]{new ModifierValue(BuffModifiers.MINING_SPEED, 0.4F), new ModifierValue(BuffModifiers.MINING_RANGE, 1.0F), new ModifierValue(BuffModifiers.SPEED, 0.1F)})).spoilDuration(120), 24.0F, true);
      registerItem("sushirolls", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.15F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50)})).spoilDuration(120), 32.0F, true);
      registerItem("friedpork", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.15F), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F)})).spoilDuration(120), 29.0F, true);
      registerItem("bananapudding", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.15F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.ARMOR_FLAT, 6)})).spoilDuration(120), 84.0F, true);
      registerItem("lemontart", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.1F), new ModifierValue(BuffModifiers.ARMOR_PEN_FLAT, 8)})).spoilDuration(120), 90.0F, true);
      registerItem("spaghettibolognese", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.75F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50)})).spoilDuration(120), 30.0F, true);
      registerItem("porktenderloin", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 80), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 1.0F)})).spoilDuration(120), 35.0F, true);
      registerItem("beefgoulash", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.2F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.15F)})).spoilDuration(120), 30.0F, true);
      registerItem("shishkebab", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.2F), new ModifierValue(BuffModifiers.ARMOR_FLAT, 8)})).spoilDuration(120), 28.0F, true);
      registerItem("pumpkinpie", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.15F), new ModifierValue(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.75F)})).spoilDuration(120), 30.0F, true);
      registerItem("sweetlemonade", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_GOURMET, 25, 960, true, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.15F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.15F)})).spoilDuration(120), 100.0F, true);
      registerItem("strawberrypie", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.1F), new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.2F), new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, 50)})).spoilDuration(120), 35.0F, true);
      registerItem("blackcoffee", (new FoodConsumableItem(100, Item.Rarity.RARE, Settler.FOOD_FINE, 20, 1800, true, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.25F)})).spoilDuration(120), 40.0F, true);
      registerItem("cappuccino", (new FoodConsumableItem(100, Item.Rarity.EPIC, Settler.FOOD_FINE, 25, 2700, true, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.3F)})).spoilDuration(120), 58.0F, true);
      registerItem("healthpotion", new HealthPotionItem(Item.Rarity.COMMON, 50), 5.0F, true);
      registerItem("greaterhealthpotion", new HealthPotionItem(Item.Rarity.UNCOMMON, 75), 6.0F, true);
      registerItem("superiorhealthpotion", new HealthPotionItem(Item.Rarity.RARE, 100), 10.0F, true);
      registerItem("manapotion", new ManaPotionItem(Item.Rarity.COMMON, 50), 5.0F, true);
      registerItem("greatermanapotion", new ManaPotionItem(Item.Rarity.UNCOMMON, 150), 6.0F, true);
      registerItem("superiormanapotion", new ManaPotionItem(Item.Rarity.RARE, 300), 10.0F, true);
      registerItem("speedpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "speedpotion", 300, new String[]{"speedpot"}), 10.0F, true);
      registerItem("greaterspeedpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterspeedpotion", 300, new String[]{"greaterspeedpot"})).overridePotion("speedpotion"), 20.0F, true);
      registerItem("healthregenpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "healthregenpotion", 300, new String[]{"healthregenpot"}), 10.0F, true);
      registerItem("greaterhealthregenpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterhealthregenpotion", 300, new String[]{"greaterhealthregenpot"})).overridePotion("healthregenpotion"), 20.0F, true);
      registerItem("resistancepotion", new SimplePotionItem(50, Item.Rarity.COMMON, "resistancepotion", 300, new String[]{"resistpot"}), 15.0F, true);
      registerItem("greaterresistancepotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterresistancepotion", 300, new String[]{"greaterresistpot"})).overridePotion("resistancepotion"), 30.0F, true);
      registerItem("battlepotion", new SimplePotionItem(50, Item.Rarity.COMMON, "battlepotion", 300, new String[]{"battlepot"}), 35.0F, true);
      registerItem("greaterbattlepotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterbattlepotion", 300, new String[]{"greaterbattlepot"})).overridePotion("battlepotion"), 50.0F, true);
      registerItem("attackspeedpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "attackspeedpotion", 300, new String[]{"attackspeedpot"}), 10.0F, true);
      registerItem("greaterattackspeedpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterattackspeedpotion", 300, new String[]{"greaterattackspeedpot"})).overridePotion("attackspeedpotion"), 20.0F, true);
      registerItem("manaregenpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "manaregenpotion", 300, new String[]{"manaregenpot"}), 10.0F, true);
      registerItem("greatermanaregenpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greatermanaregenpotion", 300, new String[]{"greatermanaregenpot"})).overridePotion("manaregenpotion"), 20.0F, true);
      registerItem("accuracypotion", new SimplePotionItem(50, Item.Rarity.COMMON, "accuracypotion", 300, new String[]{"accuracypot"}), 10.0F, true);
      registerItem("greateraccuracypotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greateraccuracypotion", 300, new String[]{"greateraccuracypot"})).overridePotion("accuracypotion"), 20.0F, true);
      registerItem("rapidpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "rapidpotion", 300, new String[]{"rapidpot"}), 10.0F, true);
      registerItem("greaterrapidpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterrapidpotion", 300, new String[]{"greaterrapidpot"})).overridePotion("rapidpotion"), 20.0F, true);
      registerItem("knockbackpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "knockbackpotion", 300, new String[]{"knockbackpot"}), 10.0F, true);
      registerItem("thornspotion", new SimplePotionItem(50, Item.Rarity.COMMON, "thornspotion", 300, new String[]{"thornspot"}), 10.0F, true);
      registerItem("fireresistancepotion", new SimplePotionItem(50, Item.Rarity.COMMON, "fireresistancepotion", 300, new String[]{"fireresistpot"}), 15.0F, true);
      registerItem("invisibilitypotion", new SimplePotionItem(50, Item.Rarity.COMMON, "invisibilitypotion", 600, new String[]{"invispot"}), 40.0F, true);
      registerItem("fishingpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "fishingpotion", 300, new String[]{"fishingpot1", "fishingpot2"}), 20.0F, true);
      registerItem("greaterfishingpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterfishingpotion", 300, new String[]{"greaterfishingpot1", "greaterfishingpot2"})).overridePotion("fishingpotion"), 40.0F, true);
      registerItem("miningpotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "miningpotion", 900, new String[]{"miningpot"}), 20.0F, true);
      registerItem("greaterminingpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterminingpotion", 900, new String[]{"greaterminingpot"})).overridePotion("miningpotion"), 40.0F, true);
      registerItem("spelunkerpotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "spelunkerpotion", 600, new String[]{"spelunkerpot"}), 30.0F, true);
      registerItem("treasurepotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "treasurepotion", 600, new String[]{"treasurepot"}), 30.0F, true);
      registerItem("passivepotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "passivepotion", 900, new String[]{"passivepot"}), 10.0F, true);
      registerItem("buildingpotion", new SimplePotionItem(50, Item.Rarity.COMMON, "buildingpotion", 1800, new String[]{"buildingpot"}), 15.0F, true);
      registerItem("greaterbuildingpotion", (new SimplePotionItem(50, Item.Rarity.RARE, "greaterbuildingpotion", 1800, new String[]{"greaterbuildingpot"})).overridePotion("buildingpotion"), 30.0F, true);
      registerItem("strengthpotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "strengthpotion", 300, new String[]{"strengthpot"}), 20.0F, true);
      registerItem("rangerpotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "rangerpotion", 300, new String[]{"rangerpot"}), 20.0F, true);
      registerItem("wisdompotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "wisdompotion", 300, new String[]{"wisdompot"}), 20.0F, true);
      registerItem("minionpotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "minionpotion", 300, new String[]{"minionpot"}), 20.0F, true);
      registerItem("webpotion", new SimplePotionItem(50, Item.Rarity.UNCOMMON, "webpotion", 300, new String[]{"webpot"}), 20.0F, true);
      registerItem("recallscroll", new RecallScrollItem(), 25.0F, true);
      registerItem("travelscroll", new TravelScrollItem(), 50.0F, true);
      registerItem("recallflask", new RecallFlaskItem(), 400.0F, true);
      registerItem("stinkflask", new StinkFlaskItem(), 200.0F, true);
      registerItem("portalflask", new PortalFlaskItem(), 800.0F, true);
      registerItem("enchantingscroll", new EnchantingScrollItem(), 200.0F, true);
      registerItem("gatewaytablet", new GatewayTabletItem(), 500.0F, true);
      registerItem("craftingguide", new CraftingGuideBookItem(), 20.0F, true);
      registerItem("recipebook", new RecipeBookItem(), 400.0F, true);
      registerItem("voidpouch", new VoidPouchItem(), 400.0F, true);
      registerItem("voidbag", new VoidBagItem(), 1200.0F, true);
      registerItem("coinpouch", new CoinPouch(), 600.0F, true);
      registerItem("ammopouch", new AmmoPouch(), 200.0F, true);
      registerItem("ammobag", new AmmoBag(), 400.0F, true);
      registerItem("potionpouch", new PotionPouch(), 800.0F, true);
      registerItem("potionbag", new PotionBag(), 1000.0F, true);
      registerItem("lunchbox", new Lunchbox(), 400.0F, true);
      registerItem("tabletbox", new TabletBox(), 1000.0F, true);
      registerItem("portablemusicplayer", new PortableMusicPlayerItem(), 800.0F, true);
      registerItem("fireworkrocket", new FireworkPlaceableItem(), 5.0F, true);
      registerItem("woodboat", new WoodBoatMountItem(), 10.0F, true);
      registerItem("minecart", new MinecartMountItem(), 50.0F, true);
      registerItem("shears", new ShearsItem(), 50.0F, true);
      registerItem("bucket", new BucketItem(), 10.0F, true);
      registerItem("infinitewaterbucket", new InfiniteWaterBucketItem(), 250.0F, true);
      registerItem("rope", new RopeItem(), 50.0F, true);
      registerItem("infiniterope", new InfiniteRopeItem(), 250.0F, true);
      registerItem("binoculars", new BinocularsItem(), 100.0F, true);
      registerItem("telescope", new TelescopeItem(), 1000.0F, false);
      registerItem("greenpresent", new PresentItem(), 10.0F, true, false);
      registerItem("bluepresent", new PresentItem(), 10.0F, true, false);
      registerItem("redpresent", new PresentItem(), 10.0F, true, false);
      registerItem("yellowpresent", new PresentItem(), 10.0F, true, false);
      registerItem("christmaspresent", new ChristmasPresentItem(), 10.0F, true, false);
      registerItem("greenwrappingpaper", new WrappingPaperItem("greenpresent"), 10.0F, true, false);
      registerItem("bluewrappingpaper", new WrappingPaperItem("bluepresent"), 10.0F, true, false);
      registerItem("redwrappingpaper", new WrappingPaperItem("redpresent"), 10.0F, true, false);
      registerItem("yellowwrappingpaper", new WrappingPaperItem("yellowpresent"), 10.0F, true, false);
      registerItem("landfill", new LandfillItem(), 0.1F, true);
      registerItem("grassseed", new GrassSeedItem("grasstile"), 2.0F, true);
      registerItem("swampgrassseed", new GrassSeedItem("swampgrasstile"), 2.0F, true);
      registerItem("fertilizer", new FertilizerPlaceableItem(), 4.0F, true);
      registerItem("mysteriousportal", new EvilsProtectorSpawnItem(), 50.0F, true);
      registerItem("royalegg", new QueenSpiderSpawnItem(), 50.0F, true);
      registerItem("voidcaller", new VoidWizardSpawnItem(), 50.0F, true);
      registerItem("spikedfossil", new SwampGuardianSpawnItem(), 70.0F, true);
      registerItem("ancientstatue", new AncientVultureSpawnItem(), 100.0F, true);
      registerItem("shadowgate", new ReaperSpawnItem(), 140.0F, true);
      registerItem("icecrown", new CryoQueenSpawnItem(), 140.0F, true);
      registerItem("decayingleaf", new PestWardenSpawnItem(), 150.0F, true);
      registerItem("dragonsouls", new DragonSoulsItem(), 160.0F, true);
      registerItem("slimeeggs", new MotherSlimeSpawnItem(), 170.0F, false);
      registerItem("swarmsignal", new NightSwarmSpawnItem(), 180.0F, false);
      registerItem("crownofspiderkin", new SpiderEmpressSpawnItem(), 190.0F, false);
      registerItem("fishinghold", new FishingPoleHolding(), 0.0F, false);
      registerItem("villagemap", new BiomeMapItem(Item.Rarity.RARE, 9, new String[]{"forestvillage", "desertvillage", "snowvillage"}), 40.0F, true);
      registerItem("dungeonmap", new BiomeMapItem(Item.Rarity.RARE, 9, new String[]{"dungeon"}), 40.0F, true);
      registerItem("piratemap", new BiomeMapItem(Item.Rarity.RARE, 9, new String[]{"piratevillage"}), 120.0F, true);
      registerItem("honeybee", new HoneyBeePlaceableItem(false), 10.0F, true);
      registerItem("queenbee", new HoneyBeePlaceableItem(true), 100.0F, true);
      registerItem("apiaryframe", new ApiaryFramePlaceableItem(), 30.0F, true);
      registerItem("importedcow", new ImportedAnimalSpawnItem(1, true, "cow"), 150.0F, true);
      registerItem("importedsheep", new ImportedAnimalSpawnItem(1, true, "sheep"), 150.0F, true);
      registerItem("importedpig", new ImportedAnimalSpawnItem(1, true, "pig"), 150.0F, true);
      registerItem("weticicle", new PetFollowerPlaceableItem("petpenguin", Item.Rarity.UNCOMMON), 250.0F, true);
      registerItem("exoticseeds", new PetFollowerPlaceableItem("petparrot", Item.Rarity.UNCOMMON), 300.0F, true);
      registerItem("magicstilts", new PetFollowerPlaceableItem("petwalkingtorch", Item.Rarity.UNCOMMON), 500.0F, true);
      registerItem("petrock", new PetFollowerPlaceableItem("petcavelingelder", Item.Rarity.RARE), 5.0F, false);
      registerItem("demonheart", new DemonHeartItem(), 30.0F, true);
      registerItem("emptypendant", new TrinketSlotsIncreaseItem(5), 40.0F, true);
      registerItem("piratesheath", new TrinketSlotsIncreaseItem(6), 75.0F, true);
      registerItem("wizardsocket", new TrinketSlotsIncreaseItem(7), 100.0F, true);
      registerItem("lifeelixir", new LifeElixirItem(), 50.0F, true);
      registerItem("workinprogress", new WorkInProgressItem(), 1.0F, true, false);
      registerItem("inctrinkets", new TestChangeTrinketSlotsItem(1), 0.0F, false);
      registerItem("dectrinkets", new TestChangeTrinketSlotsItem(-1), 0.0F, false);
      Iterator var1 = MusicRegistry.getMusic().iterator();

      while(var1.hasNext()) {
         GameMusic var2 = (GameMusic)var1.next();
         registerItem(var2.getStringID() + "vinyl", new VinylItem(var2), 50.0F, true, false);
      }

      registerItem("zombiearm", new ZombieArmQuestItem(), 0.0F, true, false);
      registerItem("goblinring", new GoblinRingQuestItem(), 0.0F, true, false);
      registerItem("swampeel", new SwampEelQuestItem(), 0.0F, true, false);
      registerItem("babyshark", new BabySharkQuestItem(), 0.0F, true, false);
      registerItem("babyswordfish", new BabySwordfishQuestItem(), 0.0F, true, false);
      registerItem("frozenbeard", new FrozenBeardQuestItem(), 0.0F, true, false);
      registerItem("spiderleg", new SpiderLegQuestItem(), 0.0F, true, false);
      registerItem("crabclaw", new CrabClawQuestItem(), 0.0F, true, false);
      registerItem("sandray", new SandRayQuestItem(), 0.0F, true, false);
      registerItem("fakefangs", new FakeFangsQuestItem(), 0.0F, true, false);
      registerItem("slimechunk", new SlimeChunkQuestItem(), 0.0F, true, false);
      registerItem("enchantedcollar", new EnchantedCollarQuestItem(), 0.0F, true, false);
      registerItem("apprenticescroll", new ApprenticeScrollQuestItem(), 0.0F, true, false);
      registerItem("darkgem", new DarkGemQuestItem(), 0.0F, true, false);
      registerItem("slimylauncher", new SlimyLauncherQuestItem(), 0.0F, true, false);
      registerItem("mummysbandage", new MummysBandageQuestItem(), 0.0F, true, false);
      registerItem("magicsand", new MagicSandQuestItem(), 0.0F, true, false);
      registerItem("caveoyster", new CaveOysterQuestItem(), 0.0F, true, false);
      registerItem("capturedspirit", new CapturedSpiritQuestItem(), 0.0F, true, false);
      registerItem("pegleg", new PegLegQuestItem(), 0.0F, true, false);
      registerItem("eyepatch", new EyePatchQuestItem(), 0.0F, true, false);
      registerItem("rumbottle", new RumBottleQuestItem(), 0.0F, true, false);
      registerItem("deepspiritswab", new DeepSpiritSwabQuestItem(), 0.0F, true, false);
      registerItem("brokenlimb", new BrokenLimbQuestItem(), 0.0F, true, false);
      registerItem("feraltail", new FeralTailQuestItem(), 0.0F, true, false);
      registerItem("razoricicle", new RazorIcicleQuestItem(), 0.0F, true, false);
      registerItem("slimesample", new SlimeSampleQuestItem(), 0.0F, true, false);
      registerItem("soakedbow", new SoakedBowQuestItem(), 0.0F, true, false);
      registerItem("wormtooth", new WormToothQuestItem(), 0.0F, true, false);
      registerItem("crawlersfoot", new CrawlersFootQuestItem(), 0.0F, true, false);
      registerItem("swingspriteattack", new SwingSpriteAttackItem(), 0.0F, false);
      registerItem("workspriteattack", new WorkSpriteAttackItem(), 0.0F, false);
      registerItem("strikebanner", new StrikeBannerItem(), 0.0F, false);
   }

   protected void onRegister(ItemRegistryElement var1, int var2, String var3, boolean var4) {
      Iterator var5 = var1.item.getGlobalIngredients().iterator();

      while(var5.hasNext()) {
         Integer var6 = (Integer)var5.next();
         GlobalIngredientRegistry.getGlobalIngredient(var6).registerItemID(var2);
      }

      var1.item.registerItemCategory();
   }

   protected void onRegistryClose() {
      Iterator var1;
      ItemRegistryElement var2;
      for(var1 = this.getElements().iterator(); var1.hasNext(); var2.displayName = var2.item.getNewLocalization()) {
         var2 = (ItemRegistryElement)var1.next();
      }

      var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         var2 = (ItemRegistryElement)var1.next();
         var2.item.onItemRegistryClosed();
      }

      totalItems = (int)this.streamElements().count();
      totalItemsObtainable = (int)this.streamElements().filter((var0) -> {
         return var0.isObtainable;
      }).count();
      totalStatItemsObtainable = (int)this.streamElements().filter((var0) -> {
         return var0.isObtainable && var0.countInStats;
      }).count();
      totalTrinkets = (int)this.streamElements().filter((var0) -> {
         return var0.item.isTrinketItem() && var0.isObtainable;
      }).count();
   }

   public static Stream<Item> streamItems() {
      return instance.streamElements().map((var0) -> {
         return var0.item;
      });
   }

   public static List<Item> getItems() {
      return (List)streamItems().collect(Collectors.toList());
   }

   public static GameMessage getLocalization(int var0) {
      return (GameMessage)(var0 == -1 ? new StaticMessage("N/A") : ((ItemRegistryElement)instance.getElement(var0)).displayName);
   }

   public static String getDisplayName(int var0) {
      return var0 == -1 ? null : ((GameMessage)Objects.requireNonNull(getLocalization(var0))).translate();
   }

   public static int registerItem(String var0, Item var1, float var2, boolean var3) {
      return registerItem(var0, var1, var2, var3, var3);
   }

   public static int registerItem(String var0, Item var1, float var2, boolean var3, boolean var4) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register items");
      } else {
         return instance.register(var0, new ItemRegistryElement(var1, var2, var3, var4));
      }
   }

   public static int replaceItem(String var0, Item var1, float var2, boolean var3) {
      return replaceItem(var0, var1, var2, var3, var3);
   }

   public static int replaceItem(String var0, Item var1, float var2, boolean var3, boolean var4) {
      return instance.replace(var0, new ItemRegistryElement(var1, var2, var3, var4));
   }

   public static Item getItem(String var0) {
      return getItem(getItemID(var0));
   }

   public static Item getItem(int var0) {
      if (var0 == -1) {
         return null;
      } else {
         ItemRegistryElement var1 = (ItemRegistryElement)instance.getElement(var0);
         return var1 == null ? null : var1.item;
      }
   }

   public static int getItemID(String var0) {
      try {
         return instance.getElementIDRaw(var0);
      } catch (NoSuchElementException var2) {
         return -1;
      }
   }

   public static String getItemStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static boolean itemExists(String var0) {
      try {
         return instance.getElementIDRaw(var0) >= 0;
      } catch (NoSuchElementException var2) {
         return false;
      }
   }

   public static boolean isObtainable(int var0) {
      return var0 == -1 ? false : ((ItemRegistryElement)instance.getElement(var0)).isObtainable;
   }

   public static boolean countsInStats(int var0) {
      return var0 == -1 ? false : ((ItemRegistryElement)instance.getElement(var0)).countInStats;
   }

   public static float getBrokerValue(int var0) {
      return var0 == -1 ? 0.0F : ((ItemRegistryElement)instance.getElement(var0)).brokerValue;
   }

   public static LoadedMod getItemMod(int var0) {
      return var0 == -1 ? null : ((ItemRegistryElement)instance.getElement(var0)).mod;
   }

   public static int getTotalItemsObtainable() {
      return totalItemsObtainable;
   }

   public static int getTotalStatItemsObtainable() {
      return totalStatItemsObtainable;
   }

   public static int getTotalItems() {
      return totalItems;
   }

   public static int getTotalTrinkets() {
      return totalTrinkets;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((ItemRegistryElement)var1, var2, var3, var4);
   }

   protected static class ItemRegistryElement implements IDDataContainer {
      public final Item item;
      public final boolean isObtainable;
      public final boolean countInStats;
      public final float brokerValue;
      public final LoadedMod mod;
      public GameMessage displayName;

      public ItemRegistryElement(Item var1, float var2, boolean var3, boolean var4) {
         this.item = var1;
         this.brokerValue = var2;
         this.isObtainable = var3;
         this.countInStats = var4;
         this.mod = LoadedMod.getRunningMod();
      }

      public IDData getIDData() {
         return this.item.idData;
      }
   }
}
