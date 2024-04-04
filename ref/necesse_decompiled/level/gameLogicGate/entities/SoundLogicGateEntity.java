package necesse.level.gameLogicGate.entities;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class SoundLogicGateEntity extends LogicGateEntity {
   public int sound;
   public int semitone;
   private boolean active;

   public SoundLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.sound = -1;
      this.semitone = 0;
      this.active = false;
   }

   public SoundLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("sound", this.sound);
      var1.addInt("semitone", this.semitone);
      var1.addBoolean("active", this.active);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.sound = var1.getInt("sound", -1);
      this.semitone = var1.getInt("semitone", -1);
      this.active = var1.getBoolean("active", false);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);
      var1.putNextByteUnsigned(this.sound);
      var1.putNextByte((byte)this.semitone);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);
      this.sound = var1.getNextByteUnsigned();
      this.semitone = var1.getNextByte();
   }

   protected void onUpdate(int var1, boolean var2) {
      if (var2 && !this.active) {
         this.playSound();
         this.active = true;
      } else {
         boolean var3 = false;

         for(int var4 = 0; var4 < 4; ++var4) {
            if (this.isWireActive(var4)) {
               var3 = true;
            }
         }

         if (!var3) {
            this.active = false;
         }
      }

   }

   public void playSound() {
      if (this.isClient()) {
         playSound(this.sound, this.semitone, this.tileX * 32 + 16, this.tileY * 32 + 16);
      }

   }

   public static float getPitch(int var0) {
      return (float)Math.pow(2.0, (double)((float)var0 / 12.0F));
   }

   public static void playSound(int var0, int var1, int var2, int var3) {
      GameSound[] var4 = getSounds();
      if (var0 >= 0 && var0 < var4.length) {
         Screen.playSound(var4[var0], SoundEffect.effect((float)var2, (float)var3).pitch(getPitch(var1)));
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      String[] var4 = getSoundNames();
      String var5 = this.sound >= 0 && this.sound < var4.length ? var4[this.sound] : Localization.translate("logictooltips", "soundnone");
      var3.add(Localization.translate("logictooltips", "soundname", "name", var5));
      var3.add(Localization.translate("logictooltips", "soundsemitone", "value", (Object)this.semitone));
      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.SOUND_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }

   public static GameSound[] getSounds() {
      return new GameSound[]{GameResources.bassNote, GameResources.hatNote, GameResources.kickNote, GameResources.pianoNote, GameResources.snareNote};
   }

   public static String[] getSoundNames() {
      return new String[]{Localization.translate("logictooltips", "soundbass"), Localization.translate("logictooltips", "soundhat"), Localization.translate("logictooltips", "soundkick"), Localization.translate("logictooltips", "soundpiano"), Localization.translate("logictooltips", "soundsnare")};
   }
}
