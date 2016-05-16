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

package watchdog.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by Dell on 15-05-2016.
 */
public class LogUtil {
    public static Logger L = Logger.getLogger(LogUtil.class.getName());

    static {
        L.setUseParentHandlers(false);
        L.addHandler(new ConsoleHandler() {
            public void publish(LogRecord logRecord) {
                System.out.println(logRecord.getLevel().getName().charAt(0) + " [" + logRecord.getSourceClassName() + "#" + logRecord.getSourceMethodName() + "]: " + logRecord.getMessage());
            }
        });
    }
}
