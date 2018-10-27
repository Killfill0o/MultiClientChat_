public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread firstThread = new Thread();
        Thread secondThread = new Thread("namedThread");
        System.out.println(firstThread.getName());
        System.out.println(secondThread.getName());
    }
}