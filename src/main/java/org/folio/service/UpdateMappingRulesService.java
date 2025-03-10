package org.folio.service;

import static org.folio.FolioUpdateBibMappingRulesApplication.exitWithMessage;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import org.folio.client.AuthClient;
import org.folio.client.SRMClient;
import org.folio.model.Configuration;
import org.folio.util.FileWorker;
import org.folio.util.HttpWorker;
import org.folio.util.MappingRulesUtil;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateMappingRulesService {
    private Configuration configuration;
    private SRMClient srmClient;
    private static final String MARC_BIB = "marc-bib";

    public void start() {
        configuration = FileWorker.getConfiguration();
        var httpWorker = new HttpWorker(configuration);
        var authClient = new AuthClient(configuration, httpWorker);

        httpWorker.setCookie(authClient.authorize());
        srmClient = new SRMClient(httpWorker);

        updateMappingRules();

        exitWithMessage("Script execution completed");
    }

    private void updateMappingRules() {
        JsonNode existingMappingRules = srmClient.retrieveMappingRules(MARC_BIB);

        MappingRulesUtil.updateMappingRules(existingMappingRules);
        srmClient.updateMappingRules(existingMappingRules, MARC_BIB);
    }
}