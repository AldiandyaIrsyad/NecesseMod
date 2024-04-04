package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.AncientBoneProjectile;
import necesse.entity.projectile.AncientSkeletonMageProjectile;
import necesse.entity.projectile.AncientVultureProjectile;
import necesse.entity.projectile.BabyBoneProjectile;
import necesse.entity.projectile.BloodClawProjectile;
import necesse.entity.projectile.BloodGrimoireRightClickProjectile;
import necesse.entity.projectile.BoneArrowProjectile;
import necesse.entity.projectile.BoneProjectile;
import necesse.entity.projectile.BoulderStaffProjectile;
import necesse.entity.projectile.BouncingArrowProjectile;
import necesse.entity.projectile.BouncingSlimeBallProjectile;
import necesse.entity.projectile.CannonBallProjectile;
import necesse.entity.projectile.CaptainCannonBallProjectile;
import necesse.entity.projectile.CarapaceDaggerProjectile;
import necesse.entity.projectile.CausticExecutionerProjectile;
import necesse.entity.projectile.CaveSpiderSpitProjectile;
import necesse.entity.projectile.CaveSpiderWebProjectile;
import necesse.entity.projectile.CrescentDiscFollowingProjectile;
import necesse.entity.projectile.CrimsonSkyArrowProjectile;
import necesse.entity.projectile.CryoMissileProjectile;
import necesse.entity.projectile.CryoQuakeProjectile;
import necesse.entity.projectile.CryoQuakeWeaponProjectile;
import necesse.entity.projectile.CryoShardProjectile;
import necesse.entity.projectile.CryoSpearShardProjectile;
import necesse.entity.projectile.CryoVolleyProjectile;
import necesse.entity.projectile.CryoWarningProjectile;
import necesse.entity.projectile.CryoWaveProjectile;
import necesse.entity.projectile.DawnFireballProjectile;
import necesse.entity.projectile.DruidsGreatBowPetalProjectile;
import necesse.entity.projectile.DuskVolleyProjectile;
import necesse.entity.projectile.DynamiteStickProjectile;
import necesse.entity.projectile.EmpressAcidProjectile;
import necesse.entity.projectile.EmpressSlashProjectile;
import necesse.entity.projectile.EmpressSlashWarningProjectile;
import necesse.entity.projectile.EmpressWebBallProjectile;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;
import necesse.entity.projectile.FallenWizardBallProjectile;
import necesse.entity.projectile.FallenWizardWaveProjectile;
import necesse.entity.projectile.FireArrowProjectile;
import necesse.entity.projectile.FrostArrowProjectile;
import necesse.entity.projectile.FrostSentryProjectile;
import necesse.entity.projectile.FrostStaffProjectile;
import necesse.entity.projectile.GlacialBowProjectile;
import necesse.entity.projectile.GoldBoltProjectile;
import necesse.entity.projectile.GritArrowProjectile;
import necesse.entity.projectile.GritBoomerangProjectile;
import necesse.entity.projectile.HostileIceJavelinProjectile;
import necesse.entity.projectile.IceJavelinProjectile;
import necesse.entity.projectile.IcicleStaffProjectile;
import necesse.entity.projectile.IronArrowProjectile;
import necesse.entity.projectile.IronBombProjectile;
import necesse.entity.projectile.LivingShottyLeafProjectile;
import necesse.entity.projectile.NightPiercerArrowProjectile;
import necesse.entity.projectile.NinjaStarProjectile;
import necesse.entity.projectile.PathTestProjectile;
import necesse.entity.projectile.PhantomBobbleProjectile;
import necesse.entity.projectile.PhantomBoltProjectile;
import necesse.entity.projectile.PlayerSnowballProjectile;
import necesse.entity.projectile.PoisonArrowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.QuartzBoltProjectile;
import necesse.entity.projectile.QueenSpiderEggProjectile;
import necesse.entity.projectile.QueenSpiderSpitProjectile;
import necesse.entity.projectile.ReaperScythePlayerProjectile;
import necesse.entity.projectile.SageArrowProjectile;
import necesse.entity.projectile.SageBoomerangProjectile;
import necesse.entity.projectile.SlimeBoltProjectile;
import necesse.entity.projectile.SlimeEggProjectile;
import necesse.entity.projectile.SlimeGreatBowArrowProjectile;
import necesse.entity.projectile.SlimeGreatswordProjectile;
import necesse.entity.projectile.SnowballProjectile;
import necesse.entity.projectile.SparklerProjectile;
import necesse.entity.projectile.SpideriteArrowProjectile;
import necesse.entity.projectile.SpideriteWaveProjectile;
import necesse.entity.projectile.StarVeilProjectile;
import necesse.entity.projectile.StoneArrowProjectile;
import necesse.entity.projectile.StoneProjectile;
import necesse.entity.projectile.SwampBallProjectile;
import necesse.entity.projectile.SwampBoltProjectile;
import necesse.entity.projectile.SwampBoulderProjectile;
import necesse.entity.projectile.SwampDwellerStaffFlowerProjectile;
import necesse.entity.projectile.SwampDwellerStaffPetalProjectile;
import necesse.entity.projectile.SwampRazorProjectile;
import necesse.entity.projectile.SwampTomeProjectile;
import necesse.entity.projectile.TheRavensNestProjectile;
import necesse.entity.projectile.TileBombProjectile;
import necesse.entity.projectile.TrapArrowProjectile;
import necesse.entity.projectile.VampireProjectile;
import necesse.entity.projectile.VenomShowerProjectile;
import necesse.entity.projectile.VenomSlasherWaveProjectile;
import necesse.entity.projectile.VenomStaffProjectile;
import necesse.entity.projectile.VoidApprenticeProjectile;
import necesse.entity.projectile.VoidWizardCloneProjectile;
import necesse.entity.projectile.VoidWizardMissileProjectile;
import necesse.entity.projectile.VoidWizardWaveProjectile;
import necesse.entity.projectile.VultureHatchlingProjectile;
import necesse.entity.projectile.VulturesBurstProjectile;
import necesse.entity.projectile.WaterSprayProjectile;
import necesse.entity.projectile.WaterboltProjectile;
import necesse.entity.projectile.ZombieArrowProjectile;
import necesse.entity.projectile.boomerangProjectile.BoxingGloveBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.FrostBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.HookBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.RazorBladeBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.ReaperScytheProjectile;
import necesse.entity.projectile.boomerangProjectile.SpiderBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.TungstenBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.VoidBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.WoodBoomerangProjectile;
import necesse.entity.projectile.bulletProjectile.BouncingBulletProjectile;
import necesse.entity.projectile.bulletProjectile.FrostBulletProjectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.entity.projectile.bulletProjectile.SniperBulletProjectile;
import necesse.entity.projectile.bulletProjectile.WebbedGunBulletProjectile;
import necesse.entity.projectile.followingProjectile.BloodBoltProjectile;
import necesse.entity.projectile.followingProjectile.CryptVampireBoltProjectile;
import necesse.entity.projectile.followingProjectile.ElderlyWandProjectile;
import necesse.entity.projectile.followingProjectile.EvilsProtectorAttack2Projectile;
import necesse.entity.projectile.followingProjectile.FallenWizardScepterProjectile;
import necesse.entity.projectile.followingProjectile.GlacialBoomerangProjectile;
import necesse.entity.projectile.followingProjectile.MageSlimeBoltProjectile;
import necesse.entity.projectile.followingProjectile.MouseTestProjectile;
import necesse.entity.projectile.followingProjectile.NightRazorBoomerangProjectile;
import necesse.entity.projectile.followingProjectile.PhantomMissileProjectile;
import necesse.entity.projectile.followingProjectile.ShadowBoltProjectile;
import necesse.entity.projectile.followingProjectile.VoidBulletProjectile;
import necesse.entity.projectile.followingProjectile.VoidMissileProjectile;
import necesse.entity.projectile.followingProjectile.VoidWizardHomingProjectile;
import necesse.entity.projectile.laserProjectile.ShadowBeamProjectile;
import necesse.entity.projectile.laserProjectile.VoidLaserProjectile;
import necesse.entity.projectile.laserProjectile.VoidTrapProjectile;
import necesse.entity.projectile.pathProjectile.CrimsonSkyArrowPathProjectile;
import necesse.entity.projectile.pathProjectile.CryoQuakeCirclingProjectile;
import necesse.entity.projectile.pathProjectile.CryoWarningCirclingProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class ProjectileRegistry extends ClassedGameRegistry<Projectile, ProjectileRegistryElement> {
   public static final ProjectileRegistry instance = new ProjectileRegistry();

   private ProjectileRegistry() {
      super("Projectile", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "projectiles"));
      registerProjectile("stonearrow", StoneArrowProjectile.class, "stonearrow", "arrow_shadow");
      registerProjectile("ironarrow", IronArrowProjectile.class, "ironarrow", "arrow_shadow");
      registerProjectile("bouncingarrow", BouncingArrowProjectile.class, "bouncingarrow", "arrow_shadow");
      registerProjectile("vampirebolt", VampireProjectile.class, "bloodbolt", "bloodbolt_shadow");
      registerProjectile("ninjastar", NinjaStarProjectile.class, "ninjastar", "ninjastar_shadow");
      registerProjectile("woodboomerang", WoodBoomerangProjectile.class, "woodboomerang", "woodboomerang_shadow");
      registerProjectile("spiderboomerang", SpiderBoomerangProjectile.class, "spiderboomerang", "spiderboomerang_shadow");
      registerProjectile("frostboomerang", FrostBoomerangProjectile.class, "frostboomerang", "frostboomerang_shadow");
      registerProjectile("voidboomerang", VoidBoomerangProjectile.class, "voidboomerang", "voidboomerang_shadow");
      registerProjectile("bloodbolt", BloodBoltProjectile.class, "bloodbolt", "bloodbolt_shadow");
      registerProjectile("sparkler", SparklerProjectile.class, (String)null, (String)null);
      registerProjectile("hookboomerang", HookBoomerangProjectile.class, "hookboomerang", "hookboomerang_shadow");
      registerProjectile("firearrow", FireArrowProjectile.class, "firearrow", "arrow_shadow");
      registerProjectile("frostarrow", FrostArrowProjectile.class, "frostarrow", "arrow_shadow");
      registerProjectile("voidlaser", VoidLaserProjectile.class, (String)null, (String)null);
      registerProjectile("handgunbullet", HandGunBulletProjectile.class, (String)null, (String)null);
      registerProjectile("bouncingbullet", BouncingBulletProjectile.class, (String)null, (String)null);
      registerProjectile("frostbullet", FrostBulletProjectile.class, (String)null, (String)null);
      registerProjectile("waterbolt", WaterboltProjectile.class, "waterbolt", "waterbolt_shadow");
      registerProjectile("sniperbullet", SniperBulletProjectile.class, (String)null, (String)null);
      registerProjectile("webbedgunbullet", WebbedGunBulletProjectile.class, (String)null, (String)null);
      registerProjectile("evilsprotector1", EvilsProtectorAttack1Projectile.class, "evilsprotector1", "evilsprotector1_shadow");
      registerProjectile("traparrow", TrapArrowProjectile.class, "ironarrow", "arrow_shadow");
      registerProjectile("voidtrap", VoidTrapProjectile.class, (String)null, (String)null);
      registerProjectile("evilsprotector2", EvilsProtectorAttack2Projectile.class, "evilsprotector2", "evilsprotector2_shadow");
      registerProjectile("zombiearrow", ZombieArrowProjectile.class, "stonearrow", "arrow_shadow");
      registerProjectile("cannonball", CannonBallProjectile.class, "cannonball", "cannonball_shadow");
      registerProjectile("captaincannonball", CaptainCannonBallProjectile.class, "cannonball", "cannonball_shadow");
      registerProjectile("voidwizardhoming", VoidWizardHomingProjectile.class, "voidwizardhoming", "voidwizardhoming_shadow");
      registerProjectile("voidwizardclone", VoidWizardCloneProjectile.class, "voidwizardclone", "voidwizardclone_shadow");
      registerProjectile("voidwizardmissile", VoidWizardMissileProjectile.class, "voidwizardmissile", "voidwizardmissile_shadow");
      registerProjectile("voidwizardwave", VoidWizardWaveProjectile.class, "voidwizardwave", (String)null);
      registerProjectile("voidapprentice", VoidApprenticeProjectile.class, "voidapprentice", "voidapprentice_shadow");
      registerProjectile("waterspray", WaterSprayProjectile.class, (String)null, (String)null);
      registerProjectile("mousetest", MouseTestProjectile.class, "bloodbolt", "bloodbolt_shadow");
      registerProjectile("goldbolt", GoldBoltProjectile.class, (String)null, (String)null);
      registerProjectile("ironbomb", IronBombProjectile.class, "ironbomb", "ironbomb_shadow");
      registerProjectile("dynamitestick", DynamiteStickProjectile.class, "dynamitestick", "dynamitestick_shadow");
      registerProjectile("tilebomb", TileBombProjectile.class, "tilebomb", "tilebomb_shadow");
      registerProjectile("voidmissile", VoidMissileProjectile.class, (String)null, (String)null);
      registerProjectile("stone", StoneProjectile.class, "stone", "stone_shadow");
      registerProjectile("snowball", SnowballProjectile.class, "snowball", "snowball_shadow");
      registerProjectile("playersnowball", PlayerSnowballProjectile.class, "snowball", "snowball_shadow");
      registerProjectile("ancientvulture", AncientVultureProjectile.class, "ancientfeather", "ancientfeather_shadow");
      registerProjectile("vulturehatchling", VultureHatchlingProjectile.class, "hatchlingfeather", "hatchlingfeather_shadow");
      registerProjectile("quartzbolt", QuartzBoltProjectile.class, "quartzbolt", "bolt_shadow");
      registerProjectile("vulturesburst", VulturesBurstProjectile.class, "vulturesburst", (String)null);
      registerProjectile("froststaff", FrostStaffProjectile.class, "froststaff", "froststaff_shadow");
      registerProjectile("iciclestaff", IcicleStaffProjectile.class, "icicle", "icicle_shadow");
      registerProjectile("tungstenboomerang", TungstenBoomerangProjectile.class, "tungstenboomerang", "tungstenboomerang_shadow");
      registerProjectile("bone", BoneProjectile.class, "bone", "bone_shadow");
      registerProjectile("ancientbone", AncientBoneProjectile.class, "ancientbone", "bone_shadow");
      registerProjectile("bonearrow", BoneArrowProjectile.class, "bonearrow", "arrow_shadow");
      registerProjectile("elderlywand", ElderlyWandProjectile.class, (String)null, (String)null);
      registerProjectile("shadowbolt", ShadowBoltProjectile.class, "shadowbolt", (String)null);
      registerProjectile("reaperscythe", ReaperScytheProjectile.class, (String)null, (String)null);
      registerProjectile("shadowbeam", ShadowBeamProjectile.class, (String)null, (String)null);
      registerProjectile("reaperscytheplayer", ReaperScythePlayerProjectile.class, "reaperscytheplayer", (String)null);
      registerProjectile("boxingglove", BoxingGloveBoomerangProjectile.class, "boxingglove", (String)null);
      registerProjectile("cavespiderweb", CaveSpiderWebProjectile.class, (String)null, (String)null);
      registerProjectile("cavespiderspit", CaveSpiderSpitProjectile.class, (String)null, (String)null);
      registerProjectile("venomstaff", VenomStaffProjectile.class, (String)null, (String)null);
      registerProjectile("cryomissile", CryoMissileProjectile.class, "cryomissile", (String)null);
      registerProjectile("glacialbow", GlacialBowProjectile.class, "glacialbow", "glacialbow_shadow");
      registerProjectile("glacialboomerang", GlacialBoomerangProjectile.class, "glacialboomerang", "glacialboomerang_shadow");
      registerProjectile("swampbolt", SwampBoltProjectile.class, "swampbolt", "bolt_shadow");
      registerProjectile("cryowarning", CryoWarningProjectile.class, (String)null, (String)null);
      registerProjectile("cryoquake", CryoQuakeProjectile.class, (String)null, (String)null);
      registerProjectile("cryoquakecircle", CryoQuakeCirclingProjectile.class, (String)null, (String)null);
      registerProjectile("cryowarningcircle", CryoWarningCirclingProjectile.class, (String)null, (String)null);
      registerProjectile("cryoshard", CryoShardProjectile.class, (String)null, (String)null);
      registerProjectile("cryowave", CryoWaveProjectile.class, (String)null, (String)null);
      registerProjectile("cryovolley", CryoVolleyProjectile.class, "cryomissile", (String)null);
      registerProjectile("cryoquakeweapon", CryoQuakeWeaponProjectile.class, (String)null, (String)null);
      registerProjectile("cryospearshard", CryoSpearShardProjectile.class, "cryospearshard", "cryospearshard_shadow");
      registerProjectile("voidbullet", VoidBulletProjectile.class, (String)null, (String)null);
      registerProjectile("poisonarrow", PoisonArrowProjectile.class, "poisonarrow", "arrow_shadow");
      registerProjectile("icejavelin", IceJavelinProjectile.class, "icejavelin", "icejavelin_shadow");
      registerProjectile("frostsentry", FrostSentryProjectile.class, (String)null, (String)null);
      registerProjectile("hostileicejavelin", HostileIceJavelinProjectile.class, "hostileicejavelin", "hostileicejavelin_shadow");
      registerProjectile("swamprazor", SwampRazorProjectile.class, "swamprazor", "swamprazor_shadow");
      registerProjectile("swampboulder", SwampBoulderProjectile.class, "swampboulder", "swampboulder_shadow");
      registerProjectile("boulderstaff", BoulderStaffProjectile.class, "boulderstaff", "swampboulder_shadow");
      registerProjectile("razorbladeboomerang", RazorBladeBoomerangProjectile.class, "razorbladeboomerang", "swamprazor_shadow");
      registerProjectile("carapacedagger", CarapaceDaggerProjectile.class, "carapacedagger", "carapacedagger_shadow");
      registerProjectile("sageboomerang", SageBoomerangProjectile.class, "sageboomerang", "dragonsrebound_shadow");
      registerProjectile("gritboomerang", GritBoomerangProjectile.class, "gritboomerang", "dragonsrebound_shadow");
      registerProjectile("gritarrow", GritArrowProjectile.class, "gritarrow", (String)null);
      registerProjectile("sagearrow", SageArrowProjectile.class, "sagearrow", (String)null);
      registerProjectile("babybone", BabyBoneProjectile.class, "babybone", "babybone_shadow");
      registerProjectile("queenspideregg", QueenSpiderEggProjectile.class, "queenspideregg", "queenspideregg_shadow");
      registerProjectile("queenspiderspit", QueenSpiderSpitProjectile.class, (String)null, "queenspiderspit_shadow");
      registerProjectile("pathtest", PathTestProjectile.class, (String)null, "bolt_shadow");
      registerProjectile("swamptome", SwampTomeProjectile.class, "swamptome", "swamptome_shadow");
      registerProjectile("fallenwizardscepter", FallenWizardScepterProjectile.class, "fallenwizardscepter", "fallenwizardscepter_shadow");
      registerProjectile("fallenwizardwave", FallenWizardWaveProjectile.class, "fallenwizardwave", (String)null);
      registerProjectile("fallenwizardball", FallenWizardBallProjectile.class, "fallenwizardball", "fallenwizardball_shadow");
      registerProjectile("ancientskeletonmage", AncientSkeletonMageProjectile.class, "ancientskeletonmage", "ancientskeletonmage_shadow");
      registerProjectile("swampdwellerstaffflower", SwampDwellerStaffFlowerProjectile.class, "swampdwellerstaffflower", "swampdwellerstaffflower_shadow");
      registerProjectile("swampdwellerstaffpetal", SwampDwellerStaffPetalProjectile.class, "swampdwellerstaffpetal", "swampdwellerstaffpetal_shadow");
      registerProjectile("druidsgreatbowpetal", DruidsGreatBowPetalProjectile.class, "druidsgreatbowpetal", "druidsgreatbowpetal_shadow");
      registerProjectile("nightpiercerarrow", NightPiercerArrowProjectile.class, "nightpiercerarrow", "nightpiercerarrow_shadow");
      registerProjectile("spideritearrow", SpideriteArrowProjectile.class, "spideritearrow", "spideritearrow_shadow");
      registerProjectile("swampball", SwampBallProjectile.class, "swampball", "swampball_shadow");
      registerProjectile("venomshower", VenomShowerProjectile.class, (String)null, (String)null);
      registerProjectile("venomslasherwave", VenomSlasherWaveProjectile.class, "venomslasherwave", (String)null);
      registerProjectile("slimegreatswordprojectile", SlimeGreatswordProjectile.class, "slimegreatswordprojectile", (String)null);
      registerProjectile("slimebolt", SlimeBoltProjectile.class, "slimebolt", "slimebolt_shadow");
      registerProjectile("livingshotty", LivingShottyLeafProjectile.class, "livingshotty", "livingshotty_shadow");
      registerProjectile("bouncingslimeball", BouncingSlimeBallProjectile.class, "bouncingslimeball", "bouncingslimeball_shadow");
      registerProjectile("mageslimebolt", MageSlimeBoltProjectile.class, "mageslimebolt", "mageslimebolt_shadow");
      registerProjectile("phantombolt", PhantomBoltProjectile.class, "phantombolt", "phantombolt_shadow");
      registerProjectile("cryptvampirebolt", CryptVampireBoltProjectile.class, "cryptvampirebolt", "cryptvampirebolt_shadow");
      registerProjectile("slimeegg", SlimeEggProjectile.class, (String)null, (String)null);
      registerProjectile("phantombobble", PhantomBobbleProjectile.class, "phantombobble", "phantombobble_shadow");
      registerProjectile("phantommissile", PhantomMissileProjectile.class, "phantommissile", "phantommissile_shadow");
      registerProjectile("nightrazorboomerang", NightRazorBoomerangProjectile.class, "nightrazorboomerang", "nightrazorboomerang_shadow");
      registerProjectile("bloodgrimoire", BloodGrimoireRightClickProjectile.class, "bloodgrimoireprojectile", (String)null);
      registerProjectile("bloodclaw", BloodClawProjectile.class, "bloodclaw", (String)null);
      registerProjectile("thecrimsonskyarrow", CrimsonSkyArrowProjectile.class, "thecrimsonskyarrow", "nightpiercerarrow_shadow");
      registerProjectile("thecrimsonskypatharrow", CrimsonSkyArrowPathProjectile.class, "thecrimsonskyarrow", "nightpiercerarrow_shadow");
      registerProjectile("slimegreatbowarrow", SlimeGreatBowArrowProjectile.class, "slimegreatbowarrow", "nightpiercerarrow_shadow");
      registerProjectile("causticexecutioner", CausticExecutionerProjectile.class, (String)null, (String)null);
      registerProjectile("spideritewave", SpideriteWaveProjectile.class, (String)null, (String)null);
      registerProjectile("empresswebball", EmpressWebBallProjectile.class, "webball", "webball_shadow");
      registerProjectile("empressslashwarning", EmpressSlashWarningProjectile.class, (String)null, (String)null);
      registerProjectile("empressslash", EmpressSlashProjectile.class, (String)null, (String)null);
      registerProjectile("empressacid", EmpressAcidProjectile.class, (String)null, (String)null);
      registerProjectile("theravensnest", TheRavensNestProjectile.class, "theravensnest", "theravensnestshadow");
      registerProjectile("dawnfireball", DawnFireballProjectile.class, "dawnfireball", (String)null);
      registerProjectile("duskvolley", DuskVolleyProjectile.class, "duskvolley", (String)null);
      registerProjectile("starvail", StarVeilProjectile.class, "starveil", (String)null);
      registerProjectile("crescentdisc", CrescentDiscFollowingProjectile.class, "crescentdisc", (String)null);
   }

   protected void onRegistryClose() {
   }

   public static int registerProjectile(String var0, Class<? extends Projectile> var1, String var2, String var3) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register projectiles");
      } else {
         try {
            return instance.register(var0, new ProjectileRegistryElement(var1, var2, var3));
         } catch (NoSuchMethodException var5) {
            System.err.println("Could not register projectile " + var1.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   public static Projectile getProjectile(int var0) {
      try {
         return (Projectile)((ProjectileRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Projectile getProjectile(int var0, Level var1) {
      Projectile var2 = getProjectile(var0);
      var2.setLevel(var1);
      return var2;
   }

   public static Projectile getProjectile(String var0) {
      return getProjectile(getProjectileID(var0));
   }

   public static Projectile getProjectile(String var0, Level var1) {
      return getProjectile(getProjectileID(var0), var1);
   }

   public static Projectile getProjectile(int var0, Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9, Mob var10) {
      Projectile var11 = getProjectile(var0);
      var11.applyData(var2, var3, var4, var5, var6, var7, var8, var9, var10);
      var11.setLevel(var1);
      return var11;
   }

   public static Projectile getProjectile(int var0, Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
      Projectile var10 = getProjectile(var0, var1);
      var10.applyData(var2, var3, var4, var5, var6, var7, var8, var9);
      return var10;
   }

   public static Projectile getProjectile(String var0, Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9, Mob var10) {
      return getProjectile(getProjectileID(var0), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static Projectile getProjectile(String var0, Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
      return getProjectile(getProjectileID(var0), var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static int getProjectileID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getProjectileID(Class<? extends Projectile> var0) {
      return instance.getElementID(var0);
   }

   public static String getProjectileStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   protected static class ProjectileRegistryElement extends ClassIDDataContainer<Projectile> {
      public final String texturePath;
      public final String shadowTexturePath;
      public GameTexture texture;
      public GameTexture shadowTexture;

      public ProjectileRegistryElement(Class<? extends Projectile> var1, String var2, String var3) throws NoSuchMethodException {
         super(var1);
         this.texturePath = var2;
         this.shadowTexturePath = var3;
      }

      public void loadTexture() {
         if (this.texturePath != null) {
            this.texture = GameTexture.fromFile("projectiles/" + this.texturePath);
         } else {
            this.texture = null;
         }

         if (this.shadowTexturePath != null) {
            this.shadowTexture = GameTexture.fromFile("projectiles/" + this.shadowTexturePath);
         } else {
            this.shadowTexture = null;
         }

      }
   }

   public static class Textures {
      public Textures() {
      }

      public static void load() {
         ProjectileRegistry.instance.streamElements().forEach(ProjectileRegistryElement::loadTexture);
      }

      public static GameTexture getTexture(int var0) {
         if (var0 >= 0 && var0 <= ProjectileRegistry.instance.size() - 1) {
            GameTexture var1 = ((ProjectileRegistryElement)ProjectileRegistry.instance.getElement(var0)).texture;
            return var1 == null ? GameResources.error : var1;
         } else {
            return GameResources.error;
         }
      }

      public static GameTexture getShadowTexture(int var0) {
         return var0 >= 0 && var0 <= ProjectileRegistry.instance.size() - 1 ? ((ProjectileRegistryElement)ProjectileRegistry.instance.getElement(var0)).shadowTexture : null;
      }
   }
}
