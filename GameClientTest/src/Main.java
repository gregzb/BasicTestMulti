import processing.core.PApplet;

import processing.core.PVector;
import processing.net.*;
import processing.net.Client;
import processing.net.Server;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main extends PApplet {

    private Client client;

    private float lastTime;
    private float dt;

    private final float secsBetweenTries = 3;
    private float retryTimer = 1;

    private Map<Long, PVector> players;
    private Long id = null;

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        surface.setTitle("Client");
        frameRate(60);
        client = null;
        //textAlign(CENTER);

        //pos = new PVector(width/2, height/2);
        players = new HashMap<>();
    }

    public void draw() {

        background(255);
        fill(0);

        float currentTime = millis() / 1000f;
        float dt = currentTime-lastTime;

        retryTimer -= dt;

        if (client != null && client.active()) {
            text("Connected!", 10, 10);

            if (id != null) {
                client.write("newpos\n");
                client.write(players.get(id).x + "," + players.get(id).y + "\n");
            }

            Map<String, String> data = new HashMap<>();

            while (client.active() && client.available() > 0) {
                String key = client.readStringUntil('\n');
                String val = client.readStringUntil('\n');
                if (key != null && val != null) {
                    data.put(key.trim(), val.trim());
                }

            }

            if (data.get("id") != null) {
                id = Long.valueOf(data.get("id"));
            }

            if (data.get("players") != null) {
                players.clear();
                String[] splitData = splitTokens(data.get("players"), ":,");
                println(data.get("players"));
                for (int i = 0; i < splitData.length; i+=3) {
                    Long id = Long.valueOf(splitData[i]);
                    PVector pos = new PVector(Float.valueOf(splitData[i + 1]), Float.valueOf(splitData[i + 2]));
                    players.put(id, pos);
                }
            }

            System.out.println(data);

        } else {
            text("No Connection :(", 10, 10);
            if (retryTimer <= 0 && (client == null || !client.active())) {
                retryTimer = secsBetweenTries;
                text("Connecting", 10, 10);
                client = new Client(this, "127.0.0.1", 1337);
            }
        }

        for (long id : players.keySet()) {
            fill(255, 0, 0);
            rect(players.get(id).x, players.get(id).y, 20, 20);
        }

        lastTime = currentTime;
    }

    public static void main(String[] args) {
        //System.setErr(null);
        PApplet.main(Main.class);
    }

    public void clientEvent(Client client) {
//        println();
//        println("Data arrived: ");
//        while (client.active() && client.available() > 0) {
//            String next = client.readStringUntil('\n');
//            println(next);
//        }
    }

    public void disconnectEvent(Client someClient) {
        println();
        println("Disconnected from the server!");
    }

    public void keyPressed() {
        if (key == CODED) {
            println(id + ", " + players.get(id));
            if (keyCode == LEFT) {
                players.get(id).x -= 5;
            } else if (keyCode == RIGHT) {
                players.get(id).x += 5;
            } else if (keyCode == UP) {
                players.get(id).y -= 5;
            } else if (keyCode == DOWN) {
                players.get(id).y += 5;
            }
        }
    }
}