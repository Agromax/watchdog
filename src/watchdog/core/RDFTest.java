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

import java.io.InputStream;

/**
 * Created by Dell on 15-05-2016.
 */
public class RDFTest {
    public static void main(String[] args) {
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(PathUtil.getFilePath("tom.xml").toString());
        if (in == null) {
            throw new IllegalArgumentException();
        }

        model.read(in, null);

        model.listStatements().forEachRemaining(stmt -> {
            System.out.println(stmt.getSubject());
            System.out.println(stmt.getPredicate());
            System.out.println(stmt.getObject());
            System.out.println("\n==================\n");
        });
    }
}
