import processing.core.PApplet;

import processing.core.PVector;
import processing.net.Client;
import processing.net.Server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends PApplet {

    Server s;

    Map<Long, Client> connectedClients;

    long id = 0;

    final float clientWidth = 600;
    final float clientHeight = 600;

    final float xOffset = 300;
    final float yOffset = 300;

    private Map<Long, PVector> players;

    public void settings() {
        size(1000, 1000);
    }

    public void setup() {
        surface.setTitle("Server");
        frameRate(20);
        s = new Server(this, 1337);

        //textAlign(CENTER);
        textSize(14);

        connectedClients = new HashMap<>();
        players = new HashMap<>();
    }

    public void draw() {
        background(255);

        fill(255);
        rect(xOffset, yOffset, clientWidth, clientHeight);

        fill(0);

        text("Connected clients: ", 10, 30);

        Set<Long> keys = new HashSet<>(connectedClients.keySet());
        int i = 1;
        for (Long id : keys) {
            Client client = connectedClients.get(id);
            PVector pos = players.get(id);
            if (client.active()) {

                Map<String, String> data = new HashMap<>();

                println();
                while (client.active() && client.available() > 0) {
                    String key = client.readStringUntil('\n');
                    String val = client.readStringUntil('\n');
                    if (key != null && val != null) {
                        data.put(key.trim(), val.trim());
                    }

                }

                if (data.get("newpos") != null) {
                    String[] splitData = splitTokens(data.get("newpos"), ",");
                    PVector newPos = new PVector(Float.valueOf(splitData[0]), Float.valueOf(splitData[1]));
                    players.put(id, newPos);
                }

                fill(255, 0, 0);
                rect(players.get(id).x + xOffset, players.get(id).y + yOffset, 20, 20);

                fill(0);

                client.write("players\n");
                for (Long idInner : players.keySet()) {
                    client.write(idInner + "," + players.get(idInner).x + "," + players.get(idInner).y + ",");
                }
                client.write("\n");

                client.write("id\n");
                client.write(id + "\n");

                text(id + ": " + client.ip(), 20, 30 + i * 30);
                i++;
            } else {
                connectedClients.remove(id);
                players.remove(id);
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(Main.class);
    }

    public void serverEvent(Server server, Client client) {
        println("Client with ip: " + client.ip() + " has connected!");
//        client.write("ID\n");
//        client.write(id + "\n");
        players.put(id, new PVector(random(30, clientWidth-3), random(30, clientHeight-30)));
        connectedClients.put(id++, client);
    }
}
