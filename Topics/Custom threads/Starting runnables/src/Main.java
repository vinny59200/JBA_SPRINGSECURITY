class Starter {

    public static void startRunnables(Runnable[] runnables) {
        // implement the method
        for(Runnable r : runnables) {
            new Thread(r).start();
        }
    }
}