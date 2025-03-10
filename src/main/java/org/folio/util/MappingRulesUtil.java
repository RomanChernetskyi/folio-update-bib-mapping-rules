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
    private static final String CANCELED_LCCN_ID_RULE = "rules/canceledLCCN/cancelledLCCN_type_id.json";
    private static final String CANCELED_LCCN_VALUE_RULE = "rules/canceledLCCN/cancelledLCCN_value.json";
    private static final String DATES_TARGET_RULES = "rules/dates/dates_target_rules.json";
    private static final String UNMODIFIED_RULE = "rules/subject/%s/unmodified_rule.json";
    private static final String UPDATED_RULE = "rules/subject/%s/updated_rule.json";
    private static final String NEW_RULE = "rules/subject/%s/new_rule.json";
    private static final String NO_RULES_FOUND_FOR_MARC_FIELD = "No rules found for MARC field \"%s\"";
    private static final String RULE_010 = "010";
    private static final String RULE_008 = "008";
    private static final String TARGET = "target";
    private static final String ENTITY = "entity";
    private static final String Z_SUBFIELD = "z";
    private static final String SUBFIELD = "subfield";
    private static final Set<String> SUBJECTS_RULES_TO_UPDATED = Set.of("600", "610", "611", "630", "647", "648", "650", "651", "655");
    private static final Set<String> NEW_SUBJECTS_RULES = Set.of("653", "654", "656", "657", "658", "662", "688");
    public static final String MAPPING_RULES_WAS_UPDATED = "Mapping rules was updated for the following MARC field: {}";
    public static final String MAPPING_RULES_WAS_ADDED = "Mapping rules was added for the following MARC field: {}";


    public static void updateMappingRules(JsonNode mappingRules) {
        updateLCCNMappingRules(mappingRules);
        updateDatesMappingRules(mappingRules);
        updateSubjectMappingRules(mappingRules);
        addNewSubjectRules(mappingRules);
    }

    private static void updateLCCNMappingRules(JsonNode mappingRules) {
        try {
            JsonNode marcBibTypeId = Objects.requireNonNull(FileWorker.getJsonObject(CANCELED_LCCN_ID_RULE));
            JsonNode marcBibValue = Objects.requireNonNull(FileWorker.getJsonObject(CANCELED_LCCN_VALUE_RULE));

            JsonNode marcRulesNode = getMarcRulesNode(mappingRules, RULE_010);
            addCancelledLCCNTarget(marcRulesNode, marcBibTypeId);
            addCancelledLCCNTarget(marcRulesNode, marcBibValue);
            log.info(MAPPING_RULES_WAS_UPDATED, RULE_010);
        } catch (Exception e) {
            log.warn("Cannot update Canceled LCCN field due to {}", e.getMessage());
        }
    }

    private static void updateDatesMappingRules(JsonNode mappingRules) {
        try {
            ArrayNode dateRules = (ArrayNode) Objects.requireNonNull(FileWorker.getJsonObject(DATES_TARGET_RULES));
            JsonNode marcRulesNode = getMarcRulesNode(mappingRules, RULE_008);
            if (marcRulesNode.isArray()) {
                ArrayNode rules = (ArrayNode) marcRulesNode;
                rules.addAll(dateRules);
                log.info(MAPPING_RULES_WAS_UPDATED, RULE_008);
            }
        } catch (Exception e) {
            log.warn("Cannot update dates mapping rules due to {}", e.getMessage());
        }
    }

    private static void updateSubjectMappingRules(JsonNode mappingRules) {
        for (String rule : SUBJECTS_RULES_TO_UPDATED) {
            try {
                JsonNode unmodifiedRuleNode = Objects.requireNonNull(FileWorker.getJsonObject(format(UNMODIFIED_RULE, rule)));
                JsonNode marcRulesNode = getMarcRulesNode(mappingRules, rule);
                if (marcRulesNode.equals(unmodifiedRuleNode)) {
                    JsonNode updatedRuleNode = Objects.requireNonNull(FileWorker.getJsonObject(format(UPDATED_RULE, rule)));
                    ((ObjectNode) mappingRules).set(rule, updatedRuleNode);
                    log.info(MAPPING_RULES_WAS_UPDATED, rule);
                } else {
                    log.warn("Do not retain mapping rule for {}, it was customized previously", rule);
                }
            } catch (Exception e) {
                log.warn("Cannot update subject-source/subject-type mapping rule: {} due to {}", rule, e.getMessage());
            }
        }
    }

    private static void addNewSubjectRules(JsonNode mappingRules) {
        for (String rule : NEW_SUBJECTS_RULES) {
            try {
                if (!hasMarcRule(mappingRules, rule)) {
                    JsonNode newRuleNode = Objects.requireNonNull(FileWorker.getJsonObject(format(NEW_RULE, rule)));
                    ((ObjectNode) mappingRules).set(rule, newRuleNode);
                    log.info(MAPPING_RULES_WAS_ADDED, rule);
                } else {
                    log.warn("Do not retain mapping rule for {}, it already exist", rule);
                }
            } catch (Exception e) {
                log.warn("Cannot add new subject-source/subject-type mapping rule: {} due to {}", rule, e.getMessage());
            }
        }
    }

    private static void addCancelledLCCNTarget(JsonNode marcRulesNode, JsonNode targetRule) {
        for (JsonNode entityRule : marcRulesNode) {
            if (entityRule.get(ENTITY).isArray()) {
                ArrayNode rules = (ArrayNode) entityRule.get(ENTITY);
                if (!hasLCCNTarget(rules, targetRule)) {
                    rules.add(targetRule);
                }
            }
        }
    }

    private static boolean hasLCCNTarget(ArrayNode rules, JsonNode targetRule) {
        for (JsonNode rule : rules) {
            if (rule.get(TARGET).asText().equals(targetRule.get(TARGET).asText())) {
                ArrayNode subfields = (ArrayNode) rule.get(SUBFIELD);
                if (subfields.size() == 1 && subfields.get(0).asText().equals(Z_SUBFIELD)) {
                    log.warn("Mapping rules for 010$z already exist {}", rule.asText());
                    return true;
                }
            }
        }
        return false;
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