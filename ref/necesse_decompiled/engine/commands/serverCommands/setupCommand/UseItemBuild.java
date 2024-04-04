package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;

public class UseItemBuild extends CharacterBuild {
   public String itemStringID;

   public UseItemBuild(String var1) {
      this.itemStringID = var1;
   }

   public void apply(ServerClient var1) {
      InventoryItem var2 = new InventoryItem(this.itemStringID);
      PlayerMob var3 = var1.playerMob;
      Packet var4 = new Packet();
      var2.item.setupAttackContentPacket(new PacketWriter(var4), var3.getLevel(), var3.getX(), var3.getY(), var3, var2);
      int var5 = Item.getRandomAttackSeed(GameRandom.globalRandom);
      var2.item.onAttack(var3.getLevel(), var3.getX(), var3.getY(), var3, var3.getCurrentAttackHeight(), var2, new PlayerInventorySlot(var3.getInv().drag, 0), 0, var5, new PacketReader(var4));
   }
}
