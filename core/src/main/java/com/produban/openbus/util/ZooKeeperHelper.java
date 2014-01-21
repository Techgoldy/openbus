package com.produban.openbus.util;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Class Description
 */
public class ZooKeeperHelper implements Watcher {

    private static final int SESSION_TIMEOUT = 5000;
    protected ZooKeeper zk;
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
        connectedSignal.await();
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            connectedSignal.countDown();
        }
    }

    public void close() throws InterruptedException {
        zk.close();
    }

    public void delete(String nodePath) throws KeeperException, InterruptedException {
        try {
            List<String> children = zk.getChildren(nodePath, false);
            for (String child : children) {
                zk.delete(nodePath + "/" + child, -1); }
            zk.delete(nodePath, -1);
        }
        catch (KeeperException.NoNodeException e) {
            System.out.printf("Node %s does not exist\n", nodePath);
            System.exit(1);
        }
    }
}

