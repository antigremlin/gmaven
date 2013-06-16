/*
 * Copyright (c) 2007-2013, the original author or authors.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.codehaus.gmaven.testsuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers;
import org.sonatype.sisu.litmus.testsupport.junit.TestIndexRule;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import static org.junit.Assert.assertThat;

/**
 * Support for integration tests.
 */
public abstract class ITSupport
    extends TestSupport
{
  public static final String DEFAULT_MAVEN_VERSION = "3.0.5";

  public static final String DEFAULT_GROOVY_VERSION = "2.1.5";

  public static final String DEFAULT_UNDER_TEST_VERSION = "2.0-SNAPSHOT";

  /**
   * The version of the groovy-maven-plugin which is under test.
   */
  protected String underTestVersion;

  protected String mavenVersion;

  protected File mavenHome;

  protected File buildRepository;

  protected File settingsFile;

  protected File localRepository;

  protected String groovyVersion;

  @Rule
  public final TestName testName = new TestName();

  @Rule
  public final TestIndexRule testIndex = new TestIndexRule(
      util.resolveFile("target/it-reports"),
      util.resolveFile("target/it-work")
  );

  @Before
  public void setUp() throws Exception {
    log("Index: {}", testIndex.getDirectory().getName());

    underTestVersion = detectUnderTestVersion();
    log("Under-test version: {}", underTestVersion);

    log("System properties:");
    logProperties(System.getProperties());

    mavenVersion = System.getProperty("maven.version", DEFAULT_MAVEN_VERSION);
    log("Maven version: {}", mavenVersion);
    mavenHome = util.resolveFile("target/filesets/apache-maven-" + mavenVersion);
    assertThat("Missing maven installation: " + mavenHome,
        mavenHome, FileMatchers.exists());
    System.setProperty("maven.home", mavenHome.getAbsolutePath());
    log("Maven home: {}", mavenHome);

    buildRepository = detectBuildRepository();
    log("Build repository: {}", buildRepository);

    settingsFile = createSettingsFile(buildRepository);
    log("Settings file: {}", settingsFile);

    // FIXME: Setting local repo here isn't working properly with settings. Just use the default for now.
    //localRepository = util.resolveFile("target/maven-localrepo");
    //log("Local repository: {}", localRepository);

    groovyVersion = System.getProperty("groovy.version", DEFAULT_GROOVY_VERSION);
    log("Groovy version: {}", groovyVersion);

    //System.setProperty("verifier.forkMode", "embedded");
    System.setProperty("verifier.forkMode", "fork");

    testIndex.recordInfo("maven", mavenVersion);
    testIndex.recordInfo("groovy", groovyVersion);

    System.out.println(">>>>>>>>>>");
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("<<<<<<<<<<");

    // FIXME: record failsafe details; this won't work because suite class is used
    //String failsafePrefix = "../../failsafe-reports/" + getClass().getName();
    //reportFile("test summary", failsafePrefix + ".txt");
    //reportFile("test output", failsafePrefix + "-output.txt");

    // record build log in index report
    reportFile("mvn log", "log.txt");
  }

  private void logProperties(final Properties source) {
    Map<String, String> map = Maps.fromProperties(source);
    List<String> keys = Lists.newArrayList(map.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      log("  {}={}", key, map.get(key));
    }
  }

  private Properties loadResourceProperties(final String resourceName) throws IOException {
    Properties props = new Properties();

    URL resource = getClass().getResource(resourceName);
    if (resource != null) {
      log("Loading properties from: {}", resource);
      InputStream stream = resource.openStream();
      try {
        props.load(stream);
      }
      finally {
        stream.close();
      }
    }

    return props;
  }

  protected String detectUnderTestVersion() throws IOException {
    Properties props = loadResourceProperties("/META-INF/maven/org.codehaus.gmaven/groovy-maven-plugin/pom.properties");
    String version = props.getProperty("version");

    // this can happen in IDE context, complain and move on
    if (version == null) {
      version = DEFAULT_UNDER_TEST_VERSION;
      log("WARNING: unable to detect under-test version!");
    }

    return version;
  }

  private File createSettingsFile(final File buildRepository) throws Exception {
    return interpolate(
        util.resolveFile("src/test/it-projects/settings.xml"),
        ImmutableMap.of(
            "localRepositoryUrl", buildRepository.toURI().toURL().toExternalForm()
        )
    );
  }

  private File interpolate(final File template, final Map<String, String> context) throws Exception {
    String content = FileUtils.readFileToString(template);
    Interpolator interpolator = new StringSearchInterpolator();
    interpolator.addValueSource(new MapBasedValueSource(context));
    content = interpolator.interpolate(content);
    File file = util.createTempFile(template.getName());
    FileUtils.write(file, content);
    return file;
  }

  private File detectBuildRepository() {
    String path = System.getProperty("buildRepository");
    if (path != null) {
      return new File(path).getAbsoluteFile();
    }

    // fall back to default
    File userHome = new File(System.getProperty("user.home"));
    return new File(userHome, ".m2/repository");
  }

  protected void reportFile(final String label, final String fileName) {
    testIndex.recordAndCopyLink(label, new File(testIndex.getDirectory(), fileName));
  }

  protected MavenVerifierBuilder verifier(final String projectName) throws Exception {
    File sourceDir = util.resolveFile("src/test/it-projects/" + projectName);
    File projectDir = testIndex.getDirectory();

    log("Copying {} -> {}", sourceDir, projectDir);
    FileUtils.copyDirectory(sourceDir, projectDir);

    MavenVerifierBuilder builder = new MavenVerifierBuilder(projectDir, settingsFile);

    // include env details in log
    builder.addArg("-V");

    builder.setProperty("underTest.version", underTestVersion);
    builder.setProperty("groovy.version", groovyVersion);
    builder.setProperty("gmaven.logging", "DEBUG");

    return builder;
  }

  protected String goal(final String execute) {
    return String.format("org.codehaus.gmaven:groovy-maven-plugin:%s:%s", underTestVersion, execute);
  }
}
