package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.entity.levelEvent.BloodGrimoireParticleLevelEvent;
import necesse.entity.levelEvent.EmpressAcidGroundEvent;
import necesse.entity.levelEvent.ExtractionIncursionEvent;
import necesse.entity.levelEvent.FallenWizardRespawnEvent;
import necesse.entity.levelEvent.FlameTrapEvent;
import necesse.entity.levelEvent.HuntIncursionEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.MapDiscoverEvent;
import necesse.entity.levelEvent.ShowAttackTickEvent;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.levelEvent.SpikeTrapEvent;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.levelEvent.TeleportFailEvent;
import necesse.entity.levelEvent.TempleEntranceEvent;
import necesse.entity.levelEvent.TrialIncursionEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.explosionEvent.BloatedSpiderExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.BoulderHitExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.CannonBallExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.CaptainCannonBallExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosiveModifierChargeUpLevelEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosiveModifierExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.FlamelingsModifierSmokePuffLevelEvent;
import necesse.entity.levelEvent.explosionEvent.SupernovaExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.TNTExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.VoidWizardExplosionEvent;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.levelEvent.incursionModifiers.AlchemicalInterferenceModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.CrawlmageddonModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.ExplosiveModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.FlamelingsModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.FrenzyModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.TremorsModifierLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AncientDredgingStaffEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderSpitEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DawnSwirlEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DredgingStaffEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.EvilsProtectorBombAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FallenWizardBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FireDanceLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HydroPumpLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.LightningTrailEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobManaChangeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MoundShockWaveLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.QueenSpiderSpitEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ReturnLifeOnHitLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeGreatbowEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeWarningEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SmallGroundWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SpideriteWaveGroundWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SunlightOrbEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheCrimsonSkyEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VenomStaffEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WebWeaverWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardGooEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardHomingEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardMissileEvent;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.HumanSettlementRaidLevelEvent;

public class LevelEventRegistry extends ClassedGameRegistry<LevelEvent, LevelEventRegistryElement> {
   public static final LevelEventRegistry instance = new LevelEventRegistry();

   private LevelEventRegistry() {
      super("LevelEvent", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "levelevents"));
      registerEvent("lightningtrail", LightningTrailEvent.class);
      registerEvent("evilsprotectorbombattack", EvilsProtectorBombAttackEvent.class);
      registerEvent("voidwizardgoo", VoidWizardGooEvent.class);
      registerEvent("voidwizardhoming", VoidWizardHomingEvent.class);
      registerEvent("voidwizardexplosion", VoidWizardExplosionEvent.class);
      registerEvent("voidwizardmissile", VoidWizardMissileEvent.class);
      registerEvent("mobhealthinc", MobHealthChangeEvent.class);
      registerEvent("mobmanainc", MobManaChangeEvent.class);
      registerEvent("cavespiderweb", CaveSpiderWebEvent.class);
      registerEvent("cavespiderspit", CaveSpiderSpitEvent.class);
      registerEvent("venomstaff", VenomStaffEvent.class);
      registerEvent("dredgingstaff", DredgingStaffEvent.class);
      registerEvent("firedance", FireDanceLevelEvent.class);
      registerEvent("mousebeam", MouseBeamLevelEvent.class);
      registerEvent("queenspiderspit", QueenSpiderSpitEvent.class);
      registerEvent("fallenwizardbeam", FallenWizardBeamLevelEvent.class);
      registerEvent("webspinnerweb", SmallGroundWebEvent.class);
      registerEvent("teleport", TeleportEvent.class);
      registerEvent("teleportfail", TeleportFailEvent.class);
      registerEvent("mapdiscover", MapDiscoverEvent.class);
      registerEvent("smokepuff", SmokePuffLevelEvent.class);
      registerEvent("huntincursion", HuntIncursionEvent.class);
      registerEvent("extractionincursion", ExtractionIncursionEvent.class);
      registerEvent("trialincursion", TrialIncursionEvent.class);
      registerEvent("frenzymodifier", FrenzyModifierLevelEvent.class);
      registerEvent("tremorsmodifier", TremorsModifierLevelEvent.class);
      registerEvent("alchemicalinterferencemodifier", AlchemicalInterferenceModifierLevelEvent.class);
      registerEvent("crawlmageddonmodifier", CrawlmageddonModifierLevelEvent.class);
      registerEvent("explosivemodifier", ExplosiveModifierLevelEvent.class);
      registerEvent("explosivemodifierexplosion", ExplosiveModifierExplosionLevelEvent.class);
      registerEvent("explosivemodifierchargeup", ExplosiveModifierChargeUpLevelEvent.class);
      registerEvent("flamelingsmodifier", FlamelingsModifierLevelEvent.class);
      registerEvent("flamelingsmodifiersmokepuff", FlamelingsModifierSmokePuffLevelEvent.class);
      registerEvent("flametrap", FlameTrapEvent.class);
      registerEvent("spiketrap", SpikeTrapEvent.class);
      registerEvent("tntexplosion", TNTExplosionEvent.class);
      registerEvent("fishing", FishingEvent.class);
      registerEvent("cannonballexplosion", CannonBallExplosionEvent.class);
      registerEvent("captaincannonballexplosion", CaptainCannonBallExplosionEvent.class);
      registerEvent("bombexplosion", BombExplosionEvent.class);
      registerEvent("boulderhit", BoulderHitExplosionEvent.class);
      registerEvent("hydropump", HydroPumpLevelEvent.class);
      registerEvent("moundshockwave", MoundShockWaveLevelEvent.class);
      registerEvent("ancientdredgingstaff", AncientDredgingStaffEvent.class);
      registerEvent("humanraid", HumanSettlementRaidLevelEvent.class);
      registerEvent("templeentrance", TempleEntranceEvent.class);
      registerEvent("fallenwizardrespawn", FallenWizardRespawnEvent.class);
      registerEvent("slimequakewarning", SlimeQuakeWarningEvent.class);
      registerEvent("slimequake", SlimeQuakeEvent.class);
      registerEvent("nightswarm", NightSwarmLevelEvent.class);
      registerEvent("returnlifeonhit", ReturnLifeOnHitLevelEvent.class);
      registerEvent("bloodgrimoireparticle", BloodGrimoireParticleLevelEvent.class);
      registerEvent("showattacktick", ShowAttackTickEvent.class);
      registerEvent("swordcleansliceattack", SwordCleanSliceAttackEvent.class);
      registerEvent("thecrimsonsky", TheCrimsonSkyEvent.class);
      registerEvent("slimegreatbowevent", SlimeGreatbowEvent.class);
      registerEvent("waitforseconds", WaitForSecondsEvent.class);
      registerEvent("bloatedspiderexplosion", BloatedSpiderExplosionEvent.class);
      registerEvent("webweaverweb", WebWeaverWebEvent.class);
      registerEvent("spideritewaveweb", SpideriteWaveGroundWebEvent.class);
      registerEvent("empressacidwave", EmpressAcidGroundEvent.class);
      registerEvent("dawnswirl", DawnSwirlEvent.class);
      registerEvent("supernovaexplosion", SupernovaExplosionEvent.class);
      registerEvent("sunlightorb", SunlightOrbEvent.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerEvent(String var0, Class<? extends LevelEvent> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register level events");
      } else {
         try {
            return instance.register(var0, new LevelEventRegistryElement(var1));
         } catch (NoSuchMethodException var3) {
            System.err.println("Could not register LevelEvent " + var1.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   public static LevelEvent getEvent(int var0) {
      try {
         return (LevelEvent)((LevelEventRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static LevelEvent getEvent(String var0) {
      return getEvent(getEventID(var0));
   }

   public static int getEventID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getEventID(Class<? extends LevelEvent> var0) {
      return instance.getElementID(var0);
   }

   public static String getEventStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   protected static class LevelEventRegistryElement extends ClassIDDataContainer<LevelEvent> {
      public LevelEventRegistryElement(Class<? extends LevelEvent> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
