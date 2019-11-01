/*
    Copyright 2019 Ericsson AB.
    For a full list of individual contributors, please see the commit history.
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.ericsson.eiffel.remrem.generate.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ERLookupController {

    public static String getQueryfromLookup(JsonObject link) {
        String query = null;
        if (link.toString().contains("%lookup%")) {
            final JsonObject lookup = link.get("%lookup%").getAsJsonObject();
            final String eventType = lookup.get("eventType").getAsString();
            final JsonArray lookUpParams = lookup.get("properties").getAsJsonArray();
            final StringBuilder queryBuilder = new StringBuilder("/events?meta.type=" + eventType);
            StreamSupport.stream(lookUpParams.spliterator(), false)
            .forEach(jsonArray -> {
                for (Entry<String, JsonElement> entry : jsonArray.getAsJsonObject().entrySet()) {
                    final String propertyKey = entry.getKey();
                    final String propertyValue = entry.getValue().getAsString();
                    final String str = "&" + propertyKey + "=" + propertyValue;
                    queryBuilder.append(str);
                }
            });
            query = queryBuilder.toString();
        }
        return query;
    }

    public static String[] getIdsfromResponseBody(String responseBody) {

        final JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
        final JsonArray asJsonArray = jsonObject.get("items").getAsJsonArray();
        final int length = asJsonArray.size();
        if (length == 0) {
            return new String[0];
        } else {
            final List<String> list = new ArrayList<>();
            asJsonArray.forEach(
                    item -> list.add(item.getAsJsonObject().get("meta").getAsJsonObject().get("id").getAsString()));
            return list.toArray(new String[0]);
        }
    }

    public static void convertbodyJsontoLookupJson(String[] ids, JsonObject link, JsonArray links) {
        final String type = link.get("type").getAsString();
        for (int j = 0; j < ids.length; j++) {
            final JsonObject newJsonObject = new JsonObject();
            newJsonObject.addProperty("type", type);
            newJsonObject.addProperty("target", ids[j]);
            links.add(newJsonObject);
        }
    }
}