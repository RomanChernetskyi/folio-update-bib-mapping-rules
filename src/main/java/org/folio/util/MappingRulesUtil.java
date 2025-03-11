package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.folio.exception.MarcRulesNotFoundException;

import java.util.Objects;

import static java.lang.String.format;

@Slf4j
public class MappingRulesUtil {
    private static final String NO_RULES_FOUND_FOR_MARC_FIELD = "No rules found for MARC field \"%s\"";
    private static final String LEADER_RULES = "rules/leaderFieldRules.json";
    private static final String DEFAULT_MODE_OF_ISSUANCE_ID_RULE = "rules/defaultModeOfIssuanceIdRule.json";
    private static final String TARGET = "target";
    private static final String LDR = "LDR";
    private static final String RULE_001 = "001";
    private static final String RULES = "rules";
    public static final String MODE_OF_ISSUANCE_ID = "modeOfIssuanceId";
    private static final String CONDITIONS = "conditions";

    public static void updateMappingRules(JsonNode mappingRules) {
        addLeaderFieldRules((ObjectNode) mappingRules);
    }

    private static void addLeaderFieldRules(ObjectNode mappingRules) {
        ArrayNode leaderRules = (ArrayNode) Objects.requireNonNull(FileWorker.getJsonObject(LEADER_RULES));
        JsonNode modeOfIssuanceIdRule = getModeOfIssuanceIdRule(mappingRules);
        leaderRules.add(modeOfIssuanceIdRule);

        mappingRules.set(LDR, leaderRules);
    }

    private static JsonNode getModeOfIssuanceIdRule(JsonNode mappingRules) {
        try {
            ArrayNode marcRulesNode = (ArrayNode) getMarcRulesNode(mappingRules, RULE_001);

            for (int i = 0; i < marcRulesNode.size(); i++) {
                JsonNode rule = marcRulesNode.get(i);
                if (rule.has(TARGET) && MODE_OF_ISSUANCE_ID.equals(rule.get(TARGET).asText())) {
                    marcRulesNode.remove(i);
                    removeLeaderCondition(rule);
                    return rule;
                }
            }
            return Objects.requireNonNull(FileWorker.getJsonObject(DEFAULT_MODE_OF_ISSUANCE_ID_RULE));
        } catch (MarcRulesNotFoundException e) {
            log.warn("Failed to get 001 rule");
            return Objects.requireNonNull(FileWorker.getJsonObject(DEFAULT_MODE_OF_ISSUANCE_ID_RULE));
        }
    }

    private static void removeLeaderCondition(JsonNode rule) {
        if (rule.has(RULES)) {
            rule.get(RULES).forEach(ruleNode -> {
                if (ruleNode.has(CONDITIONS)) {
                    ruleNode.get(CONDITIONS).forEach(condition -> ((ObjectNode) condition).remove(LDR));
                }
            });
        }
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