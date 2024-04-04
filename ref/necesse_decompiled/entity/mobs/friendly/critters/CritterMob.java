package necesse.entity.mobs.friendly.critters;

import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CritterAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.gfx.GameResources;
import necesse.level.maps.levelData.jobs.HuntMobLevelJob;

public class CritterMob extends FriendlyMob {
   private boolean isRunning;
   private final BooleanMobAbility setRunningAbility;
   public HuntMobLevelJob huntJob;

   public CritterMob() {
      this(10);
   }

   public CritterMob(int var1) {
      super(var1);
      this.isCritter = true;
      this.canDespawn = true;
      this.setRunningAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            boolean var2 = CritterMob.this.isRunning;
            CritterMob.this.isRunning = var1;
            if (CritterMob.this.isRunning != var2) {
               CritterMob.this.changedRunning();
            }

         }
      });
   }

   public void serverTick() {
      super.serverTick();
      if (this.canTakeDamage()) {
         HuntMobLevelJob var1 = this.huntJob;
         if (var1 == null || var1.tileX != this.getTileX() || var1.tileY != this.getTileY()) {
            this.huntJob = new HuntMobLevelJob(this);
            if (var1 != null) {
               this.huntJob.reservable = var1.reservable;
            }
         }
      } else {
         this.huntJob = null;
      }

   }

   public boolean canDespawn() {
      return !this.canDespawn ? false : GameUtils.streamServerClients(this.getLevel()).noneMatch((var1) -> {
         return this.getDistance(var1.playerMob) < (float)(CRITTER_SPAWN_AREA.maxSpawnDistance + 100);
      });
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isRunning);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isRunning = var1.getNextBoolean();
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CritterAI());
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.squeak, SoundEffect.effect(this).volume(1.5F));
   }

   public boolean shouldSave() {
      return this.shouldSave && !this.canDespawn();
   }

   public void changedRunning() {
      this.buffManager.updateBuffs();
   }

   public void setRunning(boolean var1) {
      if (var1 != this.isRunning) {
         this.setRunningAbility.runAndSend(var1);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return this.isRunning ? this.getRunningModifiers() : super.getDefaultModifiers();
   }

   protected Stream<ModifierValue<?>> getRunningModifiers() {
      return Stream.of(new ModifierValue(BuffModifiers.SPEED, 0.5F));
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      return (new MobSpawnLocation(this, var3, var4)).checkMobSpawnLocation().validAndApply();
   }
}
