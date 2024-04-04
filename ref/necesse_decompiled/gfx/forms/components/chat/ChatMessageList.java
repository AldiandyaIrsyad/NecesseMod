package necesse.gfx.forms.components.chat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Predicate;
import necesse.gfx.fairType.FairType;

public class ChatMessageList implements Iterable<ChatMessage> {
   public static final int maxSavedMessages = 250;
   private LinkedList<ChatMessage> messages = new LinkedList();
   private LinkedList<ChatMessageListener> listeners = new LinkedList();

   public ChatMessageList() {
   }

   public ChatMessage addMessage(String var1) {
      FairType var2 = new FairType();
      var2.append(ChatMessage.fontOptions, var1);
      return this.addMessage(var2);
   }

   public ChatMessage addMessage(FairType var1) {
      var1.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions));
      System.out.println("Chat > " + var1.getParseString());
      return this.addMessage(new ChatMessage(var1));
   }

   public <T extends ChatMessage> T addMessage(T var1) {
      this.messages.addLast(var1);
      if (this.messages.size() > 250) {
         this.messages.removeFirst();
      }

      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         ChatMessageListener var3 = (ChatMessageListener)var2.next();
         var3.onNewMessage(var1);
      }

      return var1;
   }

   public void removeMessage(ChatMessage var1) {
      if (this.messages.remove(var1)) {
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            ChatMessageListener var3 = (ChatMessageListener)var2.next();
            var3.onRemoveMessage(var1);
         }
      }

   }

   public boolean removeMessagesIf(Predicate<? super ChatMessage> var1) {
      LinkedList var2 = new LinkedList();
      ListIterator var3 = this.messages.listIterator();

      while(var3.hasNext()) {
         ChatMessage var4 = (ChatMessage)var3.next();
         if (var1.test(var4)) {
            var2.addFirst(var4);
            var3.remove();
         }
      }

      Iterator var8 = var2.iterator();

      while(var8.hasNext()) {
         ChatMessage var5 = (ChatMessage)var8.next();
         Iterator var6 = this.listeners.iterator();

         while(var6.hasNext()) {
            ChatMessageListener var7 = (ChatMessageListener)var6.next();
            var7.onRemoveMessage(var5);
         }
      }

      return !var2.isEmpty();
   }

   public void addListener(ChatMessageListener var1) {
      this.listeners.add(var1);
   }

   public void removeListener(ChatMessageListener var1) {
      this.listeners.remove(var1);
   }

   public int getTotalListeners() {
      return this.listeners.size();
   }

   public Iterator<ChatMessage> iterator() {
      return this.messages.iterator();
   }
}
