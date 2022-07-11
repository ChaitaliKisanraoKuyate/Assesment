package methods;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import etaf.helperutils.screenRecorder.VideoRecord;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static etaf.helperutils.constants.Constants.*;
import static etaf.helperutils.filehelper.GlobalProperties.getConfigProperties;
/**
 * Cucumber Option to run Karate API
 */
@KarateOptions(
        features = {"src/test/resources/APITests"}
)
//Junit
public class APICukesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(APICukesTest.class);

    @AfterClass
    public static void beforeScenario() throws Exception {
        generateReport(REPORTS_PATH);
        try {
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static void generateReport(String reportOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(reportOutputPath), new String[]{"json"}, true);
        jsonFiles.remove(new File(AXE_JSON_REPORT));
        jsonFiles.remove(new File(HTMLCS_JSON_REPORT));
        jsonFiles.remove(new File(TEST_ACCESSIBILITY_JSON_REPORT));
        List jsonPaths = new ArrayList(jsonFiles.size());
        for (File file : jsonFiles) {
            jsonPaths.add(file.getAbsolutePath());
        }
        Configuration config = new Configuration(new File(REPORTS_PATH), "CucumberBDD");

        config.addClassifications("Platform", getConfigProperties().getProperty(PLATFORM));
        config.addClassifications("Platform_version", getConfigProperties().getProperty(PLATFORM_VERSION));
        config.addClassifications("Browser", getConfigProperties().getProperty(BROWSER));

        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

    @BeforeClass
    public static void setup() {
        if (getConfigProperties().getProperty("record_test_video").equals("true")) {
            try {
                VideoRecord.startRecording();
                LOGGER.info("Started video recording");
            } catch (Exception e) {
                LOGGER.error("Unable to start video recording", e);
            }
        }
    }

    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * This is the API Test runner Junit
     */
    @Test
    public void testParallel() {
        String karateOutputPath = REPORTS_PATH;
        Results stats = Runner.parallel(getClass(), 2, karateOutputPath);
    }

}


