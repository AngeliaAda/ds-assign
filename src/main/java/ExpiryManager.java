import java.util.concurrent.*;

public class ExpiryManager {
    private ConcurrentHashMap<String, Long> contentServerTimestamps = new ConcurrentHashMap<>();
    private static final long EXPIRY_TIME = 30000; // 30 seconds

    public void updateTimestamp(String serverId) {
        contentServerTimestamps.put(serverId, System.currentTimeMillis());
    }

    public void startExpiryChecker() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            contentServerTimestamps.forEach((serverId, timestamp) -> {
                if (currentTime - timestamp > EXPIRY_TIME) {
                    contentServerTimestamps.remove(serverId);
                    System.out.println("Data from " + serverId + " expired.");
                }
            });
        }, 0, 10, TimeUnit.SECONDS);
    }
}