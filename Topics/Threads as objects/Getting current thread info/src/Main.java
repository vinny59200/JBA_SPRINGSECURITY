class Info {

    public static void printCurrentThreadInfo() {
        // get the thread and print its info
        Thread t = Thread.currentThread();
        System.out.println("name: " + t.getName());
        System.out.println("priority: " + t.getPriority());
//        System.out.println("Thread group: " + t.getThreadGroup());
//        System.out.println("Thread state: " + t.getState());
//        System.out.println("Thread id: " + t.getId());
//        System.out.println("Thread is daemon: " + t.isDaemon());
//        System.out.println("Thread is alive: " + t.isAlive());
    }
}