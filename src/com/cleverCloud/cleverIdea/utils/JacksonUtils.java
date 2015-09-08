package com.cleverCloud.cleverIdea.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class JacksonUtils {
  public static HashMap jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {
    HashMap map = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    map = mapper.readValue(json, HashMap.class);
    return map;
  }
}
