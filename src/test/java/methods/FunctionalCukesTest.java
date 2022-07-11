package methods;

import etaf.helperutils.screenRecorder.VideoRecord;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import etaf.helperutils.filehelper.GlobalProperties;

import static etaf.helperutils.constants.Constants.*;
import static etaf.helperutils.filehelper.GlobalProperties.getConfigProperties;


@CucumberOptions(
        tags = "@Test",
        plugin = {"pretty", "html:target/reports/cucumberHtmlReport", "json:target/reports/cucumberJSONReport.json", "rerun:rerun.txt"},
        features = {"src/test/resources/ngtpassesment/features"},
        glue = { "seleniumutils.applicationlayer", "seleniumutils.frameworklayer", "methods", "stepimplementation", "application", "com.intuit.karate"}
)

@RunWith(Cucumber.class)
public class FunctionalCukesTest extends AbstractTestNGCucumberTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalCukesTest.class);

    @AfterClass
    public static void beforeScenario() throws Exception {
        try {
            if (getConfigProperties().getProperty("record_test_video").equals("true")) {
                VideoRecord.stopRecording();
                LOGGER.info("Started video recording");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to start video recording", e);
        } finally {
            Runtime r = Runtime.getRuntime();
            r.addShutdownHook(new MyThread());
            if (getConfigProperties().getProperty(POPUP_REPORT).equalsIgnoreCase("true"))
                r.exec(new String[]{"cmd.exe", "/c", "ReportPopup.bat"});
        }
    }

    public Logger getLogger() {
        return LOGGER;
    }

    static void generateReport() {

        File dir = new File(REPORTS_PATH);

        File[] files = dir.listFiles((dir1, name) -> name.startsWith("cucumberJSONReport") && name.endsWith(".json"));
        if (files != null) {
            Collection<File> jsonFiles = Arrays.asList(files);
            List<String> jsonPaths = new ArrayList<String>(jsonFiles.size());
            for (File file : jsonFiles) {
                jsonPaths.add(file.getAbsolutePath());
            }
            Configuration config = new Configuration(new File("target"), "CucumberBDD");

            config.addClassifications(StringUtils.capitalize(PLATFORM), getConfigProperties().getProperty(PLATFORM));
            config.addClassifications(StringUtils.capitalize(PLATFORM_VERSION), getConfigProperties().getProperty(PLATFORM_VERSION));
            config.addClassifications(StringUtils.capitalize(BROWSER), getConfigProperties().getProperty(BROWSER));

            ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
            reportBuilder.generateReports();
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        if (getConfigProperties().getProperty("record_test_video").equals("true")) {
            try {
                GlobalProperties globalProperties = getConfigProperties();
                VideoRecord.startRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}

class MyThread extends Thread {
    public void run() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        File cucumberReports = new File(REPORTS_PATH + "cucumberJSONReport.json");
        File cucumberReportsHistory = new File(REPORTS_PATH + "cucumberJSONReport_" + timeStamp + ".json");

        cucumberReports.renameTo(cucumberReportsHistory);
        FunctionalCukesTest.generateReport();
    }
}


