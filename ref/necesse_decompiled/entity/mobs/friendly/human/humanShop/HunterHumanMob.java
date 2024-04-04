package necesse.entity.mobs.friendly.human.humanShop;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;

public class HunterHumanMob extends HumanShop {
   protected boolean isLost;

   public HunterHumanMob() {
      super(500, 200, "hunter");
      this.attackCooldown = 600;
      this.attackAnimTime = 500;
      this.setSwimSpeed(1.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.jobTypeHandler.getPriority("hunting").disabledBySettler = false;
      this.equipmentInventory.setItem(6, new InventoryItem("copperbow"));
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
      this.updateAI();
   }

   public void updateAI() {
      this.ai = new BehaviourTreeAI(this, new HumanAI(320, true, false, this.isLost ? 5000 : 25000), new AIMover(HumanMob.humanPathIterations));
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.hunter.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public boolean shouldSave() {
      return this.isLost ? false : super.shouldSave();
   }

   public boolean canDespawn() {
      return super.canDespawn();
   }

   public void setLost(boolean var1) {
      if (this.isLost != var1) {
         this.isLost = var1;
         this.updateTeam();
         this.updateAI();
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
         this.updateAI();
      }

      super.makeSettler(var1, var2);
   }

   public Predicate<Mob> filterHumanTargets() {
      return super.filterHumanTargets();
   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      Point var11 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var12 = (new HumanDrawOptions(var1, MobRegistry.Textures.hunter)).helmet(this.getDisplayArmor(0, "hunterhood")).chestplate(this.getDisplayArmor(1, "huntershirt")).boots(this.getDisplayArmor(2, "hunterboots")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var11).dir(this.dir).light(var8);
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
            HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.hunter)).helmet(this.getDisplayArmor(0, "hunterhood")).chestplate(this.getDisplayArmor(1, "huntershirt")).boots(this.getDisplayArmor(2, "hunterboots")).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var13).dir(this.dir).light(var10);
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

   public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
      return this.isTravelingHuman() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(var1);
   }

   public HumanMob.HumanGender getHumanGender() {
      return HumanMob.HumanGender.MALE;
   }

   protected ArrayList<GameMessage> getMessages(ServerClient var1) {
      ArrayList var2 = this.getLocalMessages("huntertalk", 5);
      HumanMob var3 = this.getRandomHuman("gunsmith");
      if (var3 != null) {
         var2.add(new LocalMessage("mobmsg", "huntertalkspecial", "gunsmith", var3.getSettlerName()));
      }

      return var2;
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.isClient() && this.customShowAttack == null) {
         Screen.playSound(GameResources.bow, SoundEffect.effect(this));
      }

   }

   public LevelIdentifier getRecruitedToLevel(ServerClient var1) {
      if (this.isLost) {
         return null;
      } else {
         return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
      }
   }

   public GameMessage getRecruitError(ServerClient var1) {
      GameMessage var2 = super.getRecruitError(var1);
      if (var2 != null) {
         return var2;
      } else if (this.isSettler()) {
         return null;
      } else if (!this.isLost && !this.isTravelingHuman()) {
         return var1.characterStats().mob_kills.getKills("ancientvulture") > 0 ? null : new LocalMessage("ui", "settlernorecruit");
      } else {
         return null;
      }
   }

   public List<InventoryItem> getRecruitItems(ServerClient var1) {
      if (this.isSettler()) {
         return null;
      } else {
         GameRandom var2 = new GameRandom((long)(this.getSettlerSeed() * 193));
         if (this.isTravelingHuman()) {
            return Collections.singletonList(new InventoryItem("coin", var2.getIntBetween(250, 450)));
         } else {
            LootTable var3 = new LootTable(new LootItemInterface[]{new LootItem("coin", Integer.MAX_VALUE), new CountOfTicketLootItems(1, new Object[]{100, new LootItem("wool", Integer.MAX_VALUE), 100, new LootItem("leather", Integer.MAX_VALUE)})});
            int var4 = var2.getIntBetween(300, 500);
            ArrayList var5 = GameLootUtils.getItemsValuedAt(var2, var4, 0.20000000298023224, var3);
            var5.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
            return var5;
         }
      }
   }

   public ArrayList<ShopItem> getShopItems(VillageShopsData var1, ServerClient var2) {
      if (this.isTravelingHuman()) {
         return null;
      } else {
         ArrayList var3 = new ArrayList();
         GameRandom var4 = new GameRandom(this.getShopSeed() + 5L);
         var3.add(ShopItem.item("ironbow", this.getRandomHappinessPrice(var4, 60, 120, 20)));
         var3.add(ShopItem.item(new InventoryItem("stonearrow", 5), this.getRandomHappinessPrice(var4, 5, 10, 2)));
         var3.add(ShopItem.item(new InventoryItem("firearrow", 5), this.getRandomHappinessPrice(var4, 8, 14, 3)));
         var3.add(ShopItem.item(new InventoryItem("ironarrow", 5), this.getRandomHappinessPrice(var4, 12, 18, 3)));
         var3.add(ShopItem.item(new InventoryItem("ninjastar", 5), this.getRandomHappinessPrice(var4, 16, 22, 3)));
         conditionSection(var4, var2.characterStats().mob_kills.getKills("evilsprotector") > 0, (var2x) -> {
            var3.add(ShopItem.item(new InventoryItem("icejavelin", 5), this.getRandomHappinessPrice(var2x, 25, 45, 10)));
         });
         conditionSection(var4, var2.characterStats().mob_kills.getKills("ancientvulture") > 0, (var2x) -> {
            var3.add(ShopItem.item("magicalquiver", this.getRandomHappinessPrice(var2x, 600, 1000, 100)));
         });
         ShopItem.addStockedItem(var3, var1, "wool", this.getRandomHappinessPrice(var4, -13, -7, 2));
         ShopItem.addStockedItem(var3, var1, "leather", this.getRandomHappinessPrice(var4, -16, -8, 2));
         return var3;
      }
   }
}
