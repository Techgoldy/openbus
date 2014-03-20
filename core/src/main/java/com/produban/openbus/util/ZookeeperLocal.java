package com.produban.openbus.util;

import org.apache.curator.test.TestingServer;

import java.io.IOException;

/**
 * Class Description
 */
public class ZookeeperLocal {
    TestingServer zkServer;

    public void startzkServer(int port) throws Exception {
        zkServer = new TestingServer(port);
    }

    public void stopZkServer() throws IOException {
        zkServer.close();
    }
}


