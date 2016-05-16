/*
 * Copyright 2016 Anurag Gautam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package watchdog.core;

import watchdog.util.LogUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Copyright
 * An observer class
 *
 * @author Anurag Gautam
 * @version 1.0.0
 */
public class Observer implements Runnable {

    private final ArrayList<Path> pathsToWatch = new ArrayList<>();
    private final Function<Path, Void> callback;
    private final ArrayList<Thread> watchDogs = new ArrayList<>();
    private ExecutorService threadPool;

    public Observer(Function<Path, Void> callback, String... dirs) throws IOException {
        if (dirs.length == 0) {
            throw new IllegalArgumentException("No. of directories to watch cannot be empty");
        }

        for (String path : dirs) {
            pathsToWatch.add(Paths.get(path.trim()));
        }

        this.callback = callback;
    }

    @Override
    public void run() {

        pathsToWatch.forEach(path -> {
            LogUtil.L.info("Adding path: " + path);
            try {
                final WatchService service = FileSystems.getDefault().newWatchService();
                path.register(service, ENTRY_CREATE, ENTRY_MODIFY);
                watchDogs.add(new Thread(() -> {
                    for (; ; ) {
                        try {
                            WatchKey key = service.take();
                            for (WatchEvent<?> event : key.pollEvents()) {
                                WatchEvent.Kind<?> kind = event.kind();
                                if (kind == OVERFLOW) {
                                    System.err.println("Overflow kind object found, ignoring");
                                    continue;
                                }

                                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                                Path filename = ev.context();
                                Path child = path.resolve(filename);
                                String mimeType = Files.probeContentType(child);

                                LogUtil.L.info(child + " mime-type: " + mimeType);

                                switch (mimeType) {
                                    case "text/xml":
                                    case "application/xml":
                                        callback.apply(child);
                                        break;
                                    default:
                                }
                            }
                            if (!key.reset()) {
                                break;
                            }
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                            if (e instanceof InterruptedException) {
                                break;
                            }
                        }
                    }
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        watchDogs.forEach(Thread::start);
        watchDogs.forEach(dog -> {
            try {
                dog.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        watchDogs.forEach(Thread::interrupt);
    }
}
