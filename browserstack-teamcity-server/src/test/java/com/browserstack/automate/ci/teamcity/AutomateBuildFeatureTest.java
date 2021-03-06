package com.browserstack.automate.ci.teamcity;

import com.browserstack.automate.ci.teamcity.config.AutomateBuildFeature;
import jetbrains.buildServer.BaseJMockTestCase;
import jetbrains.buildServer.Mocked;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class AutomateBuildFeatureTest extends BaseJMockTestCase {

    @Mocked
    private PluginDescriptor mockedPluginDescriptor;

    @Mocked
    private SBuildServer mockedSBuildServer;

    @Test
    public void testDescriptionWithNoParametersSet() throws Exception {
        /* =================== Prepare ================= */
        setUpExpectations();
        AutomateBuildFeature automateBuildFeature = new AutomateBuildFeature(mockedSBuildServer, mockedPluginDescriptor);
        Map<String, String> paramMap = new HashMap<String, String>();

        /* =================== Execute ================= */
        String featureDescription = automateBuildFeature.describeParameters(paramMap);

        /* =================== Verify ================= */
        Assert.assertEquals(featureDescription, "Requires configuration of credentials.", "Message for no credentials configured not as expected.");
    }

    @Test
    public void testDescriptionForLocalNotConfigured() throws Exception {
        /* =================== Prepare ================= */
        setUpExpectations();
        AutomateBuildFeature automateBuildFeature = new AutomateBuildFeature(mockedSBuildServer, mockedPluginDescriptor);
        Map<String, String> paramMap = createParams();

        /* =================== Execute ================= */
        String featureDescription = automateBuildFeature.describeParameters(paramMap);

        /* =================== Verify ================= */
        Assert.assertEquals(featureDescription, "Local Enabled: false", "Feature Description not as expected for Local false.");
    }


    @Test
    public void testDescriptionForLocalTrue() throws Exception {
        /* =================== Prepare ================= */
        setUpExpectations();
        AutomateBuildFeature automateBuildFeature = new AutomateBuildFeature(mockedSBuildServer, mockedPluginDescriptor);
        Map<String, String> paramMap = createParams();
        paramMap.put(BrowserStackParameters.EnvVars.BROWSERSTACK_LOCAL, "true");

        /* =================== Execute ================= */
        String featureDescription = automateBuildFeature.describeParameters(paramMap);

        /* =================== Verify ================= */
        Assert.assertEquals(featureDescription, "Local Enabled: true", "Feature Description not as expected for Local true.");
    }

    @Test
    public void testDescriptionForLocalFalse() throws Exception {
        /* =================== Prepare ================= */
        setUpExpectations();
        AutomateBuildFeature automateBuildFeature = new AutomateBuildFeature(mockedSBuildServer, mockedPluginDescriptor);
        Map<String, String> paramMap = createParams();
        paramMap.put(BrowserStackParameters.EnvVars.BROWSERSTACK_LOCAL, "false");

        /* =================== Execute ================= */
        String featureDescription = automateBuildFeature.describeParameters(paramMap);

        /* =================== Verify ================= */
        Assert.assertEquals(featureDescription, "Local Enabled: false", "Feature Description not as expected for Local false.");
    }

    @NotNull
    private Map<String, String> createParams() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(BrowserStackParameters.EnvVars.BROWSERSTACK_USERNAME, "RandomUser");
        paramMap.put(BrowserStackParameters.EnvVars.BROWSERSTACK_ACCESS_KEY, "RandomAccess456Key");
        return paramMap;
    }

    @Test
    public void testBuildFeatureGetters() throws Exception {
        /* =================== Prepare ================= */
        setUpExpectations();
        m.checking(new Expectations() {{
            oneOf(mockedPluginDescriptor).getPluginResourcesPath("automateSettings.jsp");
            will(returnValue("src/main/resources/buildServerResources/automateSettings.jsp"));
        }});

        AutomateBuildFeature automateBuildFeature = new AutomateBuildFeature(mockedSBuildServer, mockedPluginDescriptor);

        /* =================== Execute ================= */
        String type = automateBuildFeature.getType();
        String displayName = automateBuildFeature.getDisplayName();
        String editParamtersUrl = automateBuildFeature.getEditParametersUrl();
        boolean multipleFeaturesPerBuild = automateBuildFeature.isMultipleFeaturesPerBuildTypeAllowed();


        /* =================== Verify ================= */
        Assert.assertEquals(type, BrowserStackParameters.BUILD_FEATURE_TYPE, "Build feature type not as expected.");
        Assert.assertEquals(displayName, BrowserStackParameters.DISPLAY_NAME, "Display name not as expected.");
        Assert.assertEquals(editParamtersUrl, "src/main/resources/buildServerResources/automateSettings.jsp", "Parameters URL not as expected.");
        Assert.assertEquals(multipleFeaturesPerBuild, false, "Multiple features per build not as expected.");
    }

    private void setUpExpectations() {
        m.checking(new Expectations() {{
            oneOf(mockedSBuildServer).getFullServerVersion();
            will(returnValue("9.0.3"));

            oneOf(mockedPluginDescriptor).getPluginName();
            will(returnValue("BrowserStack"));

            oneOf(mockedPluginDescriptor).getPluginVersion();
            will(returnValue("1.0"));

            oneOf(mockedPluginDescriptor).getPluginRoot();
            will(returnValue(new File(".")));
        }});
    }
}
