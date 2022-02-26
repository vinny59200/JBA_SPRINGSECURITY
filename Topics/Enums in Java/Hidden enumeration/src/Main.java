public class Main {

    public static void main(String[] args) {
    // write your program here
        int count=0;
        String prefix="STAR";
        for (Secret secret   : Secret.values()) {
            if (secret.name().startsWith(prefix)) {
                count++;
            }
        }
        System.out.println(count);
    }
}

/* sample enum for inspiration
   enum Secret {
    STAR, CRASH, START, // ...
}
*/