package org.codehaus.gmaven.testsuite.suite;

import org.codehaus.gmaven.testsuite.ExecuteBasicsXT;
import org.codehaus.gmaven.testsuite.PropertyDefaultsXT;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Support for suites.
 */
@SuiteClasses({
    ExecuteBasicsXT.class,
    PropertyDefaultsXT.class
})
public abstract class SuiteSupport
{
  protected static void setMavenVersion(final String version) {
    System.setProperty("maven.version", version);
  }

  protected static void setGroovyVersion(final String version) {
    System.setProperty("groovy.version", version);
  }
}
