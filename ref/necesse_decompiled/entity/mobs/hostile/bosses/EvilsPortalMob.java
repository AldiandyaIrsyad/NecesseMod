package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsPortalMob extends BossMob {
   public static LootTable lootTable = new LootTable();
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(50, 65, 75, 85, 100);
   private long lifeTime;

   public EvilsPortalMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.isSummoned = true;
      this.collision = new Rectangle(-10, -12, 20, 20);
      this.hitBox = new Rectangle(-15, -17, 30, 30);
      this.selectBox = new Rectangle(-18, -58, 36, 58);
      this.setKnockbackModifier(0.0F);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("lifeTime", this.lifeTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.lifeTime = (long)var1.getInt("lifeTime", 0);
   }

   public void init() {
      super.init();
      this.lifeTime = 0L;
      this.ai = new BehaviourTreeAI(this, new DevilGateAINode());
      if (this.getLevel() != null) {
         this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)this.getY(), new Color(50, 50, 50))), Particle.GType.CRITICAL);
      }

   }

   public void tickMovement(float var1) {
      this.dx = 0.0F;
      this.dy = 0.0F;
   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 270.0F, 0.7F);
   }

   public void serverTick() {
      super.serverTick();
      ++this.lifeTime;
      if (this.lifeTime > 600L) {
         this.setHealth(0);
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)this.getY(), new Color(50, 50, 50))), Particle.GType.CRITICAL);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 62;
      int var13 = (int)(this.getWorldEntity().getTime() % 1600L) / 200;
      if (var13 > 4) {
         var13 = 4 - var13 % 4;
      }

      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.evilsProtector2.initDraw().sprite(2, 0, 64).light(var10).pos(var11, var12 + var13);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public class DevilGateAINode<T extends Mob> extends AINode<T> {
      private ArrayList<Mob> spawnedMobs = new ArrayList();

      public DevilGateAINode() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         var3.onRemoved((var1x) -> {
            this.spawnedMobs.forEach(Mob::remove);
         });
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (EvilsPortalMob.this.lifeTime % 120L == 0L) {
            Mob var3 = MobRegistry.getMob("portalminion", EvilsPortalMob.this.getLevel());
            EvilsPortalMob.this.getLevel().entityManager.addMob(var3, (float)(EvilsPortalMob.this.getX() + (int)(GameRandom.globalRandom.nextGaussian() * 3.0)), (float)(EvilsPortalMob.this.getY() + (int)(GameRandom.globalRandom.nextGaussian() * 3.0)));
            this.spawnedMobs.add(var3);
         }

         return AINodeResult.SUCCESS;
      }
   }
}
