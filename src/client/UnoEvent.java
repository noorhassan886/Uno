package client;

public abstract class UnoEvent implements Runnable{
     String message;

     public void run(String message) {
         this.message = message;
         this.run();
     }
}
