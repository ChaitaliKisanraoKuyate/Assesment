package methods;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/reports/cucumberHtmlReport", "json:target/reports/[CUCABLE:RUNNER].json", "rerun:rerun.txt"},
        features = {"target/parallel/features/[CUCABLE:FEATURE].feature"},
        glue = { "seleniumutils.applicationlayer", "seleniumutils.frameworklayer", "methods", "stepimplementation", "application", "com.intuit.karate"}
)
@RunWith(Cucumber.class)
//Junit
public class CucableJavaTemplate {

}


