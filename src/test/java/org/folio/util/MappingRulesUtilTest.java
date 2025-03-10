package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.Assert;

import static java.lang.String.format;

public class MappingRulesUtilTest {

    private static final String BASE_RULES_JSON = "rules/baseRules.json";
    private static final String EXPECTED_RULES_JSON = "rules/expectedRules.json";
    private static final String BASE_RULES_WITHOUT_FIELD_JSON = "rules/baseRules_without_%s_field.json";
    private static final String EXPECTED_RULES_WITHOUT_FIELD_JSON = "rules/expectedRules_without_%s_field.json";
    private static final String BASE_RULES_WITHOUT_6xx_FIELD_JSON = "rules/baseRules_with_modified_6xx_fields.json";
    private static final String EXPECTED_RULES_WITHOUT_6xx_FIELD_JSON = "rules/expectedRules_with_modified_6xx_fields.json";
    private static final String BASE_RULES_WITH_EXISTING_NEW_FIELD_JSON = "rules/baseRules_with_662_field.json";
    private static final String EXPECTED_RULES_WITH_EXISTING_NEW_FIELD_JSON = "rules/expectedRules_with_662_field.json";


    @Test
    public void shouldUpdateMappingRules() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(EXPECTED_RULES_JSON);
        JsonNode baseVersion = FileWorker.getJsonObject(BASE_RULES_JSON);

        assert baseVersion != null;
        MappingRulesUtil.updateMappingRules(baseVersion);
        Assert.assertEquals(rulesToCompare, baseVersion);
    }

    @Test
    public void shouldUpdateMappingRulesIf010FieldMissing() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(format(EXPECTED_RULES_WITHOUT_FIELD_JSON, "010"));
        JsonNode baseVersion = FileWorker.getJsonObject(format(BASE_RULES_WITHOUT_FIELD_JSON, "010"));

        assert baseVersion != null;
        MappingRulesUtil.updateMappingRules(baseVersion);
        Assert.assertEquals(rulesToCompare, baseVersion);
    }

    @Test
    public void shouldUpdateMappingRulesIf008FieldMissing() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(format(EXPECTED_RULES_WITHOUT_FIELD_JSON, "008"));
        JsonNode baseVersion = FileWorker.getJsonObject(format(BASE_RULES_WITHOUT_FIELD_JSON, "008"));

        assert baseVersion != null;
        MappingRulesUtil.updateMappingRules(baseVersion);
        Assert.assertEquals(rulesToCompare, baseVersion);
    }

    @Test
    public void shouldUpdateMappingRulesIf600FieldMissing() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(format(EXPECTED_RULES_WITHOUT_FIELD_JSON, "600"));
        JsonNode baseVersion = FileWorker.getJsonObject(format(BASE_RULES_WITHOUT_FIELD_JSON, "600"));

        assert baseVersion != null;
        MappingRulesUtil.updateMappingRules(baseVersion);
        Assert.assertEquals(rulesToCompare, baseVersion);
    }

    @Test
    public void shouldNotUpdateSubjectMappingRulesIfPreviouslyModified() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(EXPECTED_RULES_WITHOUT_6xx_FIELD_JSON);
        JsonNode baseVersion = FileWorker.getJsonObject(BASE_RULES_WITHOUT_6xx_FIELD_JSON);

        assert baseVersion != null;
        MappingRulesUtil.updateMappingRules(baseVersion);
        Assert.assertEquals(rulesToCompare, baseVersion);
    }

    @Test
    public void shouldNotModifyNewSubjectIfAlreadyExist() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(EXPECTED_RULES_WITH_EXISTING_NEW_FIELD_JSON);
        JsonNode baseVersion = FileWorker.getJsonObject(BASE_RULES_WITH_EXISTING_NEW_FIELD_JSON);

        assert baseVersion != null;
        MappingRulesUtil.updateMappingRules(baseVersion);
        Assert.assertEquals(rulesToCompare, baseVersion);
    }
}
