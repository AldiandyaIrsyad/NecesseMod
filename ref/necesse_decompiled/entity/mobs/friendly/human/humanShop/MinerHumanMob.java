package necesse.entity.mobs.friendly.human.humanShop;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.MinerContainer;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.light.GameLight;

public class MinerHumanMob extends HumanShop {
   protected boolean isLost;

   public MinerHumanMob() {
      super(500, 200, "miner");
      this.attackCooldown = 500;
      this.attackAnimTime = 500;
      this.setSwimSpeed(1.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isLost);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isLost = var1.getNextBoolean();
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new HumanAI(320, true, false, 25000), new AIMover(HumanMob.humanPathIterations));
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.miner.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      Point var11 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var12 = (new HumanDrawOptions(var1, MobRegistry.Textures.miner)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, "minershirt")).boots(this.getDisplayArmor(2, "minerboots")).sprite(var11).dir(this.dir).light(var8);
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
            int var11 = var8.getDrawX(var5) - 22 - 10;
            int var12 = var8.getDrawY(var6) - 44 - 7;
            Point var13 = this.getAnimSprite(var5, var6, this.dir);
            var12 += this.getBobbing(var5, var6);
            var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
            HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.miner)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, "minershirt")).boots(this.getDisplayArmor(2, "minerboots")).sprite(var13).dir(this.dir).light(var10);
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
            if (!this.inLiquid(var5, var6)) {
               this.addShadowDrawables(var2, var5, var6, var10, var8);
            }

         }
      }
   }

   public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
      return this.isTravelingHuman() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(var1);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      if (!this.isVisible()) {
         return null;
      } else {
         Point var5 = this.getAnimSprite(var1, var2, this.dir);
         return var5.x == 5 ? WoodBoatMob.getShadowDrawOptions(this, var1, var2, 12, var3, var4) : super.getShadowDrawOptions(var1, var2, var3, var4);
      }
   }

   public LevelIdentifier getRecruitedToLevel(ServerClient var1) {
      if (this.isLost) {
         return null;
      } else {
         return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
      }
   }

   public List<InventoryItem> getRecruitItems(ServerClient var1) {
      if (this.isSettler()) {
         return null;
      } else {
         GameRandom var2 = new GameRandom((long)(this.getSettlerSeed() * 743));
         if (this.isTravelingHuman()) {
            return Collections.singletonList(new InventoryItem("coin", var2.getIntBetween(350, 500)));
         } else {
            LootTable var3 = new LootTable(new LootItemInterface[]{new CountOfTicketLootItems(var2.getIntBetween(1, 2), new Object[]{100, new LootItem("copperore", Integer.MAX_VALUE), 100, new LootItem("ironore", Integer.MAX_VALUE), 100, new LootItem("goldore", Integer.MAX_VALUE)})});
            ArrayList var4 = GameLootUtils.getItemsValuedAt(var2, var2.getIntBetween(350, 500), 0.20000000298023224, new LootItem("coin", Integer.MAX_VALUE));
            var4.addAll(GameLootUtils.getItemsValuedAt(var2, var2.getIntBetween(30, 75), 0.20000000298023224, var3));
            var4.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
            return var4;
         }
      }
   }

   public boolean shouldSave() {
      return this.isLost ? false : super.shouldSave();
   }

   public void setLost(boolean var1) {
      if (this.isLost != var1) {
         this.isLost = var1;
         this.updateTeam();
      }
   }

   public void updateTeam() {
      if (this.getLevel() != null && !this.isClient()) {
         if (this.isLost) {
            this.team.set(-1);
            this.owner.set(-1L);
         } else {
            super.updateTeam();
         }
      }
   }

   public void makeSettler(SettlementLevelData var1, LevelSettler var2) {
      if (this.isLost) {
         this.setLost(false);
      }

      super.makeSettler(var1, var2);
   }

   public List<ExpeditionList> getPossibleExpeditions() {
      if (this.isSettlerOnCurrentLevel()) {
         SettlementLevelData var1 = SettlementLevelData.getSettlementData(this.getLevel());
         if (var1 != null) {
            ExpeditionList var2 = new ExpeditionList(new LocalMessage("ui", "minerexpeditions"), new LocalMessage("ui", "minerselectexp"), new LocalMessage("ui", "minerexpcost"), new LocalMessage("ui", "minershowmore"), var1, this, (List)ExpeditionMissionRegistry.minerExpeditionIDs.stream().map(ExpeditionMissionRegistry::getExpedition).collect(Collectors.toList()));
            return Collections.singletonList(var2);
         }
      }

      return super.getPossibleExpeditions();
   }

   public GameMessage getWorkInvMessage() {
      return (GameMessage)(this.completedMission ? new LocalMessage("ui", "minerexpcomplete") : super.getWorkInvMessage());
   }

   public HumanMob.HumanGender getHumanGender() {
      return HumanMob.HumanGender.FEMALE;
   }

   protected ArrayList<GameMessage> getMessages(ServerClient var1) {
      return this.getLocalMessages("minertalk", 7);
   }

   public PacketOpenContainer getOpenShopPacket(Server var1, ServerClient var2) {
      return PacketOpenContainer.Mob(ContainerRegistry.MINER_CONTAINER, this, MinerContainer.getMinerContainerContent(this.getShopItemsContentPacket(var2)));
   }
}
