package Root;

// FIFO (First In First Out) Queue
public class FIFO <E> implements DanMethods <E>{
   LinkedChildElement first = null;
   LinkedChildElement last = null;
   @SuppressWarnings("unchecked")
   public void push(E e){
      if(first == null){
         last = new LinkedChildElement(e);
         first = last;
      }
      else{
         last = last.add(e);
      }
   }
   
   @SuppressWarnings("unchecked")
   public E pop(){
      if(first == null){
         return null;
      }
      E tmpfirst = (E) first.get(0);
      first = first.getNextElement();
      return (E) tmpfirst;
   }
   
   @SuppressWarnings("unchecked")
   public E peek(){
      if(first == null){
         return null;
      }
      return (E) first.get(0);
   }
   
   public int size(){
      if(first == null){
         return 0;
      }
      return first.count();
   }
   
   public void grow(){
      //Does nothing
   }

   public E get(int i){
      if(first == null || i < 0){
         return null;
      }
      return (E) first.get(i);
   }
}

//Used for FIFO
//Each parent knows its child
class LinkedChildElement <E> {
   LinkedChildElement element = null;
   E payload = null;
   
   public LinkedChildElement(E payload){
      this.payload = payload;
   }
   
   @SuppressWarnings("unchecked")
   public LinkedChildElement add(E elementpayload){
      LinkedChildElement last = null;
      if(this.element == null){
         last = this.element = new LinkedChildElement(elementpayload);
      }
      else{
         last = this.element.add(elementpayload);
      }
      return last;
   }
   
   @SuppressWarnings("unchecked")
   public E get(int i){
      if(i == 0){
         return (E) payload;
      }
      if(element != null){
         return (E) element.get((i-1));
      }
      return null;
   }
   
   @SuppressWarnings("unchecked")
   public boolean contains(E testpayload){
      return payload != null && payload.equals(testpayload) || element != null && element.contains(testpayload);
   }
   
   public LinkedChildElement getNextElement(){
      return element;
   }
   
   public int count(){
      if(element != null){
         return 1 + element.count();
      }
      return 1;
   }
}

interface DanMethods <E> {
   void push(E e);
   E pop();
   E peek();
   int size();
   void grow();
}