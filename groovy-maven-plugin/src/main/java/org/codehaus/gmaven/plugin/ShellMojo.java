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

package org.codehaus.gmaven.plugin;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ShellRunner;
import org.codehaus.gmaven.plugin.util.SystemNoExitGuard;

/**
 * Run {@code groovysh} shell.
 *
 * @since 2.0
 */
@Mojo(name = "shell", aggregator = true)
public class ShellMojo
    extends RuntimeMojoSupport
{
  @Override
  protected void run() throws Exception {
    final ResourceLoader resourceLoader = new MojoResourceLoader(runtimeRealm, null, scriptpath);
    final Map<String, Object> context = createContext();
    final ShellRunner shell = runtime.getShellRunner();

    // run groovysh guarding against system exist and protecting system streams
    new SystemNoExitGuard().run(new Callable<Void>()
    {
      @Override
      public Void call() throws Exception {
        shell.run(runtimeRealm, resourceLoader, context);
        return null;
      }
    });
  }
}
