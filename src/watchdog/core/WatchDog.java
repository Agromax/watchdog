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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import watchdog.util.PathUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Dell on 15-05-2016.
 */
public class WatchDog {
    public static void main(String[] args) throws IOException, InterruptedException {
        final Scanner cin = new Scanner(System.in);

        Observer observer = new Observer((p) -> {
            System.out.print("> ");
            String input = cin.nextLine();
            if (input.equalsIgnoreCase("go")) {
                Model model = ModelFactory.createDefaultModel();
                InputStream in = FileManager.get().open(PathUtil.getFilePath("tom.xml").toString());
                if (in == null) {
                    throw new IllegalArgumentException();
                }

                model.read(in, null);
                VersionController.commit(model);
            } else {
                System.out.println("No GO!, ignoring");
            }
            return null;
        }, "E:\\Coding\\Java_Projects\\WatchDog\\t");
        observer.run();
    }
}
