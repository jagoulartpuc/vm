package pucrs;

public class Main {
    public static void main(String[] args) throws Exception {
        final int NUMBER_OF_PARTITIONS = 8;
        OpSystem os = new OpSystem(NUMBER_OF_PARTITIONS);
        os.startThreads();
    }
}
