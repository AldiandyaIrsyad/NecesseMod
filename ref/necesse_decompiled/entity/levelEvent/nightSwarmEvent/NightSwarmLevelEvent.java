package necesse.entity.levelEvent.nightSwarmEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.GameDifficulty;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.actions.DoubleIntLevelEventAction;
import necesse.entity.levelEvent.actions.EmptyLevelEventAction;
import necesse.entity.levelEvent.actions.IntLevelEventAction;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.ChargeNightSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.CircleChargeNightSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.FlyByNightSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.JailNightSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.MoveAroundNightSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.WaitMajorityCounterSwarmEventStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.WaitNightSwarmEventStage;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmStartMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class NightSwarmLevelEvent extends LevelEvent {
   public static int START_BAT_COUNT = 75;
   public static MaxHealthGetter BAT_MAX_HEALTH = new MaxHealthGetter(1000, 1500, 1800, 2200, 2800);
   public static LootTable lootTable = new LootTable();
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation();
   public static LootTable privateLootTable;
   public int startBatCount;
   public ArrayList<LevelMob<NightSwarmBatMob>> bats;
   public float levelX;
   public float levelY;
   public int playerCount;
   public int partyMemberCount;
   public int batsMaxHealth;
   public GameDifficulty difficulty;
   public final EmptyLevelEventAction eventOverAction;
   public final IntLevelEventAction removeBatAction;
   public final DoubleIntLevelEventAction updatePlayerCountAction;
   public LevelMob<NightSwarmStartMob> masterMob;
   public float nextLevelX;
   public float nextLevelY;
   public long nextUpdateTargetTime;
   public Mob currentTarget;
   public int despawnTimer;
   public int batsDoneWithStages;
   public float lastHealthProgress;
   public ArrayList<NightSwarmEventStage> stages;
   public int currentStage;
   public HashSet<Attacker> attackers;

   public NightSwarmLevelEvent() {
      this.startBatCount = START_BAT_COUNT;
      this.bats = new ArrayList();
      this.stages = new ArrayList();
      this.currentStage = -1;
      this.attackers = new HashSet();
      this.shouldSave = false;
      this.eventOverAction = (EmptyLevelEventAction)this.registerAction(new EmptyLevelEventAction() {
         protected void run() {
            NightSwarmLevelEvent.this.over();
         }
      });
      this.removeBatAction = (IntLevelEventAction)this.registerAction(new IntLevelEventAction() {
         protected void run(int var1) {
            NightSwarmLevelEvent.this.bats.removeIf((var1x) -> {
               return var1x.uniqueID == var1;
            });
         }
      });
      this.updatePlayerCountAction = (DoubleIntLevelEventAction)this.registerAction(new DoubleIntLevelEventAction() {
         protected void run(int var1, int var2) {
            NightSwarmLevelEvent.this.updateBatMaxHealthValue(var1, var2);
            Iterator var3 = NightSwarmLevelEvent.this.bats.iterator();

            while(var3.hasNext()) {
               LevelMob var4 = (LevelMob)var3.next();
               NightSwarmBatMob var5 = (NightSwarmBatMob)var4.get(NightSwarmLevelEvent.this.level);
               if (var5 != null && !var5.removed()) {
                  float var6 = (float)var5.getHealth() / (float)var5.getMaxHealth();
                  var5.setMaxHealth(NightSwarmLevelEvent.this.batsMaxHealth);
                  int var7 = (int)((float)var5.getMaxHealth() * var6);
                  var5.setHealthHidden(var7);
                  var5.sendHealthPacket(false);
               }
            }

         }
      });
   }

   public NightSwarmLevelEvent(NightSwarmStartMob var1, float var2, float var3) {
      this();
      this.masterMob = new LevelMob(var1);
      this.levelX = var2;
      this.levelY = var3;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.masterMob.uniqueID);
      var1.putNextInt(this.startBatCount);
      var1.putNextInt(this.batsMaxHealth);
      var1.putNextFloat(this.levelX);
      var1.putNextFloat(this.levelY);
      var1.putNextShortUnsigned(this.bats.size());
      Iterator var2 = this.bats.iterator();

      while(var2.hasNext()) {
         LevelMob var3 = (LevelMob)var2.next();
         var1.putNextInt(var3.uniqueID);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.masterMob = new LevelMob(var1.getNextInt());
      this.startBatCount = var1.getNextInt();
      this.batsMaxHealth = var1.getNextInt();
      this.levelX = var1.getNextFloat();
      this.levelY = var1.getNextFloat();
      this.bats.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bats.add(new LevelMob(var1.getNextInt()));
      }

   }

   public void init() {
      super.init();
      if (!this.isClient()) {
         this.updateBatMaxHealthValue(this.level.presentPlayers, this.level.presentAdventurePartyMembers);
         int var1 = this.startBatCount / 5;
         if (this.bats.size() < this.startBatCount) {
            for(int var2 = this.bats.size(); var2 < this.startBatCount; ++var2) {
               int var3 = var2 / var1 * var1;
               NightSwarmBatMob var4 = (NightSwarmBatMob)MobRegistry.getMob("nightswarmbat", this.level);
               var4.setMaxHealth(this.batsMaxHealth);
               var4.setHealthHidden(var4.getMaxHealth());
               var4.setPos(this.levelX + (float)GameRandom.globalRandom.getIntBetween(-100, 100), this.levelY + (float)GameRandom.globalRandom.getIntBetween(-100, 100), true);
               var4.nightSwarmEventUniqueID = this.getUniqueID();
               if (var3 != var2) {
                  var4.shareHitCooldownUniqueID = ((LevelMob)this.bats.get(var3)).uniqueID;
               }

               var4.batIndex = var2;
               var4.idleXPos = this.levelX;
               var4.idleYPos = this.levelY;
               this.level.entityManager.mobs.add(var4);
               this.bats.add(new LevelMob(var4.getUniqueID()));
            }
         }
      } else {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect());
      }

   }

   public void clientTick() {
      super.clientTick();
      this.updateBats(false);
      Screen.setMusic(MusicRegistry.TheControlRoom, Screen.MusicPriority.EVENT, 1.5F);
      if (this.bats.isEmpty()) {
         this.over();
      }

   }

   public void serverTick() {
      super.serverTick();
      this.updateBats(true);
      long var1 = this.level.getWorldEntity().getTime();
      if (this.nextUpdateTargetTime <= var1) {
         this.currentTarget = (Mob)this.level.entityManager.players.streamAreaTileRange((int)this.levelX, (int)this.levelY, 100).filter((var0) -> {
            return var0 != null && !var0.removed() && var0.isVisible();
         }).findBestDistance(0, Comparator.comparingDouble((var1x) -> {
            return (double)var1x.getDistance(this.levelX, this.levelY);
         })).orElse((Object)null);
         this.nextUpdateTargetTime = var1 + (long)GameRandom.globalRandom.getIntBetween(4000, 6000);
      } else if (this.currentTarget != null && (this.currentTarget.removed() || !this.currentTarget.isVisible())) {
         this.nextUpdateTargetTime = Math.min(this.nextUpdateTargetTime, var1 + (long)GameRandom.globalRandom.getIntBetween(1000, 2000));
      }

      if (this.currentTarget != null) {
         if (this.currentStage == -1) {
            this.stages.clear();
            this.stages.add(new MoveAroundNightSwarmEventStage(1, 500, false));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new FlyByNightSwarmEventStage(250, 150, 200, 150, 500, 400));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new MoveAroundNightSwarmEventStage(1, 450, false, 160));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new FlyByNightSwarmEventStage(200, 150, 200, 150, 450, 400));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new MoveAroundNightSwarmEventStage(1, 400, false, 160));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new FlyByNightSwarmEventStage(150, 150, 150, 200, 400, 350));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new WaitNightSwarmEventStage(100, 2000));
            this.stages.add(new MoveAroundNightSwarmEventStage(3, 600, true));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new ChargeNightSwarmEventStage(400));
            this.stages.add(new WaitNightSwarmEventStage(100, 2000));
            this.stages.add(new MoveAroundNightSwarmEventStage(4, 600, true));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new ChargeNightSwarmEventStage(400));
            this.stages.add(new WaitNightSwarmEventStage(100, 2000));
            this.stages.add(new MoveAroundNightSwarmEventStage(4, 600, true));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new ChargeNightSwarmEventStage(400));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new CircleChargeNightSwarmEventStage());
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new JailNightSwarmEventStage());
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            this.stages.add(new WaitNightSwarmEventStage(100, 2000));
            this.stages.add(new MoveAroundNightSwarmEventStage(2, 600, true));
            this.stages.add(new WaitMajorityCounterSwarmEventStage());
            ++this.currentStage;
            ((NightSwarmEventStage)this.stages.get(this.currentStage)).onStarted(this);
         }

         NightSwarmEventStage var3 = (NightSwarmEventStage)this.stages.get(this.currentStage);
         var3.serverTick(this);
         if (var3.hasCompleted(this)) {
            var3.onCompleted(this);
            ++this.currentStage;
            if (this.currentStage >= this.stages.size()) {
               this.currentStage = -1;
            } else {
               ((NightSwarmEventStage)this.stages.get(this.currentStage)).onStarted(this);
            }
         }
      }

      if (this.currentTarget == null) {
         this.despawnTimer += 50;
         if (this.despawnTimer >= 5000) {
            Iterator var6 = this.bats.iterator();

            while(var6.hasNext()) {
               LevelMob var4 = (LevelMob)var6.next();
               NightSwarmBatMob var5 = (NightSwarmBatMob)var4.get(this.level);
               if (var5 != null) {
                  var5.remove();
               }
            }

            this.eventOverAction.runAndSend();
         }
      } else {
         this.despawnTimer = 0;
      }

      if (!this.isOver() && this.bats.isEmpty()) {
         NightSwarmStartMob var7 = (NightSwarmStartMob)this.masterMob.get(this.level);
         if (var7 != null) {
            var7.setPos(this.levelX, this.levelY, true);
            var7.addAttackers(this.attackers);
            var7.remove(0.0F, 0.0F, (Attacker)null, true);
         }

         if (this.isServer()) {
            this.attackers.stream().map(Attacker::getAttackOwner).filter((var0) -> {
               return var0 != null && var0.isPlayer;
            }).distinct().forEach((var0) -> {
               ServerClient var1 = ((PlayerMob)var0).getServerClient();
               var1.sendChatMessage((GameMessage)(new LocalMessage("misc", "bossdefeat", "name", MobRegistry.getLocalization("nightswarm"))));
            });
         }

         this.eventOverAction.runAndSend();
      }

   }

   public void updateBats(boolean var1) {
      int var2 = 0;
      ListIterator var3 = this.bats.listIterator();
      float var4 = 0.0F;
      float var5 = 0.0F;
      int var6 = 0;
      HashSet var7 = new HashSet();
      if (var1) {
         this.updatePlayerAndPartyMemberCount();
      }

      LinkedList var8 = new LinkedList();
      this.batsDoneWithStages = 0;

      while(true) {
         while(var3.hasNext()) {
            LevelMob var9 = (LevelMob)var3.next();
            NightSwarmBatMob var10 = (NightSwarmBatMob)var9.get(this.level);
            if (var10 != null && !var10.removed()) {
               ++var6;
               var2 += var10.getHealth();
               var4 += var10.x;
               var5 += var10.y;
               if (var10.currentStage == null && var10.stages.isEmpty()) {
                  ++this.batsDoneWithStages;
               }

               var8.add(var10);
            } else if (var1) {
               var3.remove();
               var7.add(var9.uniqueID);
            }
         }

         Iterator var11 = var7.iterator();

         int var13;
         while(var11.hasNext()) {
            var13 = (Integer)var11.next();
            this.removeBatAction.runAndSend(var13);
         }

         if (var6 > 0) {
            this.levelX = var4 / (float)var6;
            this.levelY = var5 / (float)var6;
         }

         BossNearbyBuff.applyAround(this.level, this.levelX, this.levelY, 1600);
         NightSwarmStartMob var12 = (NightSwarmStartMob)this.masterMob.get(this.level);
         if (var12 != null) {
            var12.setPos(this.levelX, this.levelY, true);
         }

         var13 = (int)((float)(this.startBatCount * this.batsMaxHealth) * (Float)this.level.buffManager.getModifier(LevelModifiers.ENEMY_MAX_HEALTH));
         this.lastHealthProgress = GameMath.limit(1.0F - (float)var2 / (float)var13, 0.0F, 1.0F);
         var8.forEach((var1x) -> {
            var1x.setSpeed(100.0F + this.lastHealthProgress * 80.0F);
         });
         if (this.isClient()) {
            Screen.registerEventStatusBar(this.getUniqueID(), var2, var13, (GameMessage)(new LocalMessage("mob", "nightswarm")));
         }

         return;
      }
   }

   public void updatePlayerAndPartyMemberCount() {
      int var1 = this.level.presentPlayers;
      int var2 = this.level.presentAdventurePartyMembers;
      GameDifficulty var3 = this.level.getWorldSettings().difficulty;
      if (this.playerCount < var1 || this.partyMemberCount != var2 || this.difficulty != var3) {
         this.updatePlayerCountAction.runAndSend(var1, var2);
      }

   }

   public void updateBatMaxHealthValue(int var1, int var2) {
      this.playerCount = var1;
      this.partyMemberCount = var2;
      this.difficulty = this.level.getWorldSettings().difficulty;
      this.batsMaxHealth = Math.round((float)(Integer)BAT_MAX_HEALTH.get(this.level) * GameUtils.getMultiplayerScaling(this.playerCount) * GameUtils.getMultiplayerScaling(this.partyMemberCount + 1, Integer.MAX_VALUE, 0.2F, 0.02F));
   }

   public Iterable<NightSwarmBatMob> getBats(boolean var1) {
      return () -> {
         return this.bats.stream().map((var1x) -> {
            return (NightSwarmBatMob)var1x.get(this.level);
         }).filter((var1x) -> {
            return var1 || var1x != null && !var1x.removed();
         }).iterator();
      };
   }

   public void onDispose() {
      super.onDispose();
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(uniqueDrops)});
   }

   public interface DebugHudDraw {
      DrawOptions get(GameCamera var1, PlayerMob var2);
   }
}
