package com.teamrocket.server;

import com.teamrocket.control.RestaurantController;
import com.teamrocket.service.RestaurantManagement;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RestaurantManagerServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);
    private Server server;

    public void startServer(){
        int port = 9099;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new RestaurantManagement())
                    .build()
                    .start();
            LOGGER.info("Order Server started on port 9099");

            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    LOGGER.info("Clean server shutdown in case JVM was shutdown");
                    try {
                        RestaurantManagerServer.this.stopServer();
                    } catch (InterruptedException exception) {
                        LOGGER.error("Server shutdown interrupted, {}", exception);
                    }
                }
            });
        } catch (IOException exception) {
            LOGGER.error( "Server did not start {}",exception);
        }
    }

    public void stopServer() throws InterruptedException {
        if(server!=null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if(server!=null){
            server.awaitTermination();
        }
    }


}
