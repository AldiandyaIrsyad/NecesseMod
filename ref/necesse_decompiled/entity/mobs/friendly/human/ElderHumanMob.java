package necesse.entity.mobs.friendly.human;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.packet.PacketQuestGiverRequest;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestGiver;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.ElderContainer;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.ConsumeFoodLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementClientQuests;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.light.GameLight;

public class ElderHumanMob extends HumanMob implements QuestGiver {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ConditionLootItem("petrock", (var0, var1) -> {
      return GameSeasons.isAprilFools();
   }), new LootItem("elderhat")});
   public final QuestGiver.QuestGiverObject quest = new QuestGiver.QuestGiverObject(this, false);

   public ElderHumanMob() {
      super(500, 500, "elder");
      this.setSpeed(30.0F);
      this.attackCooldown = 500;
      this.attackAnimTime = 500;
      this.setSwimSpeed(1.0F);
      this.canJoinAdventureParties = false;
      this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).disabledBySettler = true;
      this.jobTypeHandler.getTypePriorities().stream().filter((var0) -> {
         return !var0.type.getStringID().equals("needs");
      }).forEach((var0) -> {
         var0.disabledBySettler = true;
      });
      this.equipmentInventory.setItem(6, new InventoryItem("coppersword"));
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new HumanAI(480, true, true, 25000), new AIMover(humanPathIterations));
      if (this.isClient()) {
         this.getLevel().getClient().network.sendPacket(new PacketQuestGiverRequest(this.getUniqueID()));
      }

   }

   public QuestGiver.QuestGiverObject getQuestGiverObject() {
      return this.quest;
   }

   public SettlementClientQuests getSettlementClientQuests(ServerClient var1) {
      if (!this.isSettler()) {
         return null;
      } else if (!this.getLevel().settlementLayer.doesClientHaveAccess(var1)) {
         return null;
      } else {
         SettlementLevelData var2 = this.getSettlementLevelData();
         return var2 != null && var2.getObjectEntityPos() != null ? var2.getClientsQuests(var1) : null;
      }
   }

   public List<Quest> getGivenQuests(ServerClient var1) {
      ArrayList var2 = new ArrayList();
      SettlementClientQuests var3 = this.getSettlementClientQuests(var1);
      if (var3 != null) {
         Quest var4 = var3.getQuest();
         if (var4 != null) {
            var2.add(var4);
         }

         Quest var5 = var3.getTierQuest();
         if (var5 != null) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      this.quest.addSaveData(var1);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.quest.applyLoadData(var1);
   }

   public LootTable getLootTable() {
      return new LootTable(new LootItemInterface[]{lootTable, super.getLootTable()});
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.elder.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public int getSettlerHappiness() {
      return 100;
   }

   public float getRegenFlat() {
      return this.adventureParty.isInAdventureParty() && !this.isSettlerOnCurrentLevel() ? super.getRegenFlat() : super.getRegenFlat() * 2.5F;
   }

   public void clientTick() {
      super.clientTick();
      this.quest.clientTick();
   }

   public void serverTick() {
      super.serverTick();
      this.quest.serverTick();
   }

   public void tickHunger() {
      this.hungerLevel = 1.0F;
   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (this.isServer() && var1.isServerClient()) {
         ServerClient var2 = var1.getServerClient();
         PacketOpenContainer var3 = PacketOpenContainer.Mob(ContainerRegistry.ELDER_CONTAINER, this, ElderContainer.getElderContainerContent(this, var2.getServer(), var2));
         ContainerRegistry.openAndSendContainer(var2, var3);
      }

      if (this.isClient() && this.getLevel().getClient().getPlayer() == var1) {
         this.getLevel().getClient().tutorial.elderInteracted();
      }

   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      Point var11 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var12 = (new HumanDrawOptions(var1, MobRegistry.Textures.elder)).helmet(this.getDisplayArmor(0, "elderhat")).chestplate(this.getDisplayArmor(1, "eldershirt")).boots(this.getDisplayArmor(2, "eldershoes")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var11).dir(this.dir).light(var8);
      var7.accept(var12);
      DrawOptions var13 = var12.pos(var9, var10);
      DrawOptions var14 = this.getMarkerDrawOptions(var2, var3, var8, var5, 0, -45, var6);
      return () -> {
         var13.draw();
         var14.draw();
      };
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.objectUser == null || this.objectUser.object.drawsUsers()) {
         if (this.isVisible()) {
            GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
            int var11;
            int var12;
            Point var13;
            if (GameSeasons.isAprilFools() && !this.isAttacking) {
               var11 = var8.getDrawX(var5) - 32;
               var12 = var8.getDrawY(var6) - 48;
               var13 = this.getAnimSprite(var5, var6, this.dir);
               final TextureDrawOptionsEnd var18 = MobRegistry.Textures.cavelingElder.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
               var1.add(new MobDrawable() {
                  public void draw(TickManager var1) {
                     var18.draw();
                  }
               });
            } else {
               var11 = var8.getDrawX(var5) - 22 - 10;
               var12 = var8.getDrawY(var6) - 44 - 7;
               var13 = this.getAnimSprite(var5, var6, this.dir);
               var12 += this.getBobbing(var5, var6);
               var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
               HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.elder)).helmet(this.getDisplayArmor(0, "elderhat")).chestplate(this.getDisplayArmor(1, "eldershirt")).boots(this.getDisplayArmor(2, "eldershoes")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var13).dir(this.dir).light(var10);
               if (this.inLiquid(var5, var6)) {
                  var12 -= 10;
                  var14.armSprite(2);
                  var14.mask(MobRegistry.Textures.boat_mask[var13.y % 4], 0, -7);
               }

               var14 = this.setCustomItemAttackOptions(var14);
               final DrawOptions var15 = var14.pos(var11, var12);
               final TextureDrawOptionsEnd var16 = this.inLiquid(var5, var6) ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var11, var12 + 7) : null;
               final DrawOptions var17 = this.getMarkerDrawOptions(var5, var6, var10, var8, 0, -45, var9);
               var1.add(new MobDrawable() {
                  public void draw(TickManager var1) {
                     if (var16 != null) {
                        var16.draw();
                     }

                     var15.draw();
                     var17.draw();
                  }
               });
               this.addShadowDrawables(var2, var5, var6, var10, var8);
            }

         }
      }
   }

   public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
      return QuestMarkerOptions.combine(this.quest.getMarkerOptions(var1), super.getMarkerOptions(var1));
   }

   public HumanMob.HumanGender getHumanGender() {
      return HumanMob.HumanGender.MALE;
   }

   protected String getRandomName(GameRandom var1) {
      return var1.getChance(0.1F) ? getRandomName(var1, elderNames) : super.getRandomName(var1);
   }

   protected ArrayList<GameMessage> getMessages(ServerClient var1) {
      return GameSeasons.isAprilFools() ? this.getLocalMessages("elderapriltalk", 5) : this.getLocalMessages("eldertalk", 6);
   }
}
