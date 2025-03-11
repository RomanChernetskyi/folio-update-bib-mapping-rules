package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MappingRulesUtilTest {
    private final String expectedRulesPath;
    private final String baseRulesPath;

    public MappingRulesUtilTest(String expectedRulesPath, String baseRulesPath) {
        this.expectedRulesPath = expectedRulesPath;
        this.baseRulesPath = baseRulesPath;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "rules/expectedRules.json", "rules/baseRules.json" },
            { "rules/expectedRulesWithCustomModeOfIssuance.json", "rules/baseRulesWithCustomModeOfIssuance.json" },
            { "rules/expectedRules.json", "rules/baseRulesWithoutModeOfIssuance.json" },
            { "rules/expectedRulesWithout001.json", "rules/baseRulesWithout001.json" }
        });
    }

    @Test
    public void shouldUpdateMappingRules() {
        JsonNode rulesToCompare = FileWorker.getJsonObject(expectedRulesPath);
        JsonNode baseVersion = FileWorker.getJsonObject(baseRulesPath);

        MappingRulesUtil.updateMappingRules(baseVersion);

        Assert.assertEquals(rulesToCompare, baseVersion);
    }
}