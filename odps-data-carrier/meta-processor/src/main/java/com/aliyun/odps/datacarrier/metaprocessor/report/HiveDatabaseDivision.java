/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.odps.datacarrier.metaprocessor.report;

import htmlflow.HtmlView;
import java.util.HashMap;
import java.util.Map;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;

public class HiveDatabaseDivision {

  private class HiveDatabaseRecord {
    String hiveDatabaseName;
    String hiveTableName;
    int numberOfHighRisk = 0;
    int numberOfModerateRisk = 0;
  }

  private Map<String, Map<String, HiveDatabaseRecord>> hiveDatabaseRecords = new HashMap<>();

  public void addHighRisk(String hiveDatabaseName, String hiveTableName) {
    HiveDatabaseRecord record =
        getOrCreateRecord(hiveDatabaseName, hiveTableName);
    record.numberOfHighRisk += 1;
  }

  public void addModerateRisk(String hiveDatabaseName, String hiveTableName) {
    HiveDatabaseRecord record =
        getOrCreateRecord(hiveDatabaseName, hiveTableName);
    record.numberOfModerateRisk += 1;
  }

  private HiveDatabaseRecord getOrCreateRecord(String hiveDatabaseName, String hiveTableName) {
    if (!hiveDatabaseRecords.containsKey(hiveDatabaseName)) {
      hiveDatabaseRecords.put(hiveDatabaseName, new HashMap<>());
    }
    Map<String, HiveDatabaseRecord> hiveTableNameToRecord =
        hiveDatabaseRecords.get(hiveDatabaseName);
    if (!hiveTableNameToRecord.containsKey(hiveTableName)) {
      HiveDatabaseRecord record = new HiveDatabaseRecord();
      record.hiveDatabaseName = hiveDatabaseName;
      record.hiveTableName = hiveTableName;
      hiveTableNameToRecord.put(hiveTableName, record);
    }
    return hiveTableNameToRecord.get(hiveTableName);
  }

  public void addToBody(Body<Html<HtmlView>> body) {

    for (String hiveDatabaseName : hiveDatabaseRecords.keySet()) {
      Table<Div<Body<Html<HtmlView>>>> table = body
          .div().attrId(hiveDatabaseName)
          .h2().text(hiveDatabaseName.toUpperCase()).__()
          .table();

      addTableHeader(table);
      for (String hiveTableName : hiveDatabaseRecords.get(hiveDatabaseName).keySet()) {
        HiveDatabaseRecord record =
            getOrCreateRecord(hiveDatabaseName, hiveTableName);
        addTableRecord(table, record);
      }
      table.__().__();
    }
  }

  private void addTableHeader(Table<Div<Body<Html<HtmlView>>>> table) {
    table
        .tr()
          .th().text("HIVE TABLE NAME").__()
          .th().text("# HIGH RISK").__()
          .th().text("# MODERATE RISK").__()
        .__();
  }

  private void addTableRecord(Table<Div<Body<Html<HtmlView>>>> table, HiveDatabaseRecord record) {
    table
        .tr()
          .td()
            .a().attrHref("#" + record.hiveDatabaseName + "." + record.hiveTableName)
                .text(record.hiveTableName)
            .__()
          .__()
          .td().text(record.numberOfHighRisk).__()
          .td().text(record.numberOfModerateRisk).__()
        .__();
  }
}
