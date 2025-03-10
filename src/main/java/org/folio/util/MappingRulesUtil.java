package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.folio.exception.MarcRulesNotFoundException;

import static java.lang.String.format;

@Slf4j
public class MappingRulesUtil {
    private static final String NO_RULES_FOUND_FOR_MARC_FIELD = "No rules found for MARC field \"%s\"";
    private static final String TARGET = "target";
    private static final String ENTITY = "entity";
    private static final String SUBFIELD = "subfield";

    public static void updateMappingRules(JsonNode mappingRules) {

    }

    private static boolean hasMarcRule(JsonNode mappingRules, String rule) {
        JsonNode marcRulesNode = mappingRules.get(rule);
        return marcRulesNode != null && marcRulesNode.isArray() && !marcRulesNode.isEmpty();
    }

    private static JsonNode getMarcRulesNode(JsonNode mappingRules, String rule) throws MarcRulesNotFoundException {
        JsonNode marcRulesNode = mappingRules.get(rule);
        if (marcRulesNode == null || !marcRulesNode.isArray() || marcRulesNode.isEmpty()) {
            String errorMessage = format(NO_RULES_FOUND_FOR_MARC_FIELD, rule);
            log.warn(errorMessage);
            throw new MarcRulesNotFoundException(errorMessage);
        }
        return marcRulesNode;
    }
}