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

package org.codehaus.gmaven.adapter;

import java.util.Map;

/**
 * Provides the ability to open a Groovy console window.
 *
 * @since 2.0
 */
public interface ConsoleWindow
{
  interface WindowHandle
  {
    void close();

    void await() throws InterruptedException;
  }

  WindowHandle open(ClassLoader classLoader,
            ResourceLoader resourceLoader,
            Map<String, Object> context)
      throws Exception;
}