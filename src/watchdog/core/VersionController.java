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

import com.mongodb.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import watchdog.util.LogUtil;

import java.net.UnknownHostException;
import java.util.List;

public class VersionController {
    private static final String DB_NAME = "rdfdb";
    private static final String TRIPLE_STORE_COLLECTION_NAME = "triple_store";

    public static void commit(Model currentModel) {
        try {
            MongoClient client = new MongoClient();
            DB db = client.getDB(DB_NAME);

            DBCollection tripleStore = getTripleStore(db);
            List<Statement> statements = currentModel.listStatements().toList();
            int failureCount = 0;

            for (Statement stmt : statements) {
                String subj = stmt.getSubject().toString();
                String pre = stmt.getPredicate().toString();
                String obj = stmt.getObject().toString();

                BasicDBObject record = new BasicDBObject("sub", subj)
                        .append("pre", pre)
                        .append("obj", obj)
                        .append("grade", new BasicDBList())
                        .append("avgGrade", 0.0d);

                try {
                    WriteResult insertResult = tripleStore.insert(record);
                    if (!insertResult.isUpdateOfExisting()) {
                        failureCount++;
                    }
                } catch (MongoException ex) {
                    ex.printStackTrace();
                    failureCount++;
                }
            }

            LogUtil.L.info(failureCount + "/" + statements.size() + " triples successfully added");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static DBCollection getTripleStore(DB db) {
        return db.getCollection(TRIPLE_STORE_COLLECTION_NAME);
    }
}
