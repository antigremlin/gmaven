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

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.gmaven.adapter.ClosureTarget;

/**
 * Target implementation of {@code fail} closure.
 *
 * <br/>
 * Usage:
 * <ul>
 *   <li>{@code fail()}</li>
 *   <li>{@code fail(Object)}</li>
 *   <li>{@code fail(Throwable)}</li>
 *   <li>{@code fail(Object, Throwable)}</li>
 * </ul>
 *
 * <br/>
 * Failing with a simple string:
 * <pre>
 * fail('I done goofed')
 * </pre>
 *
 * <br/>
 * Failing with an exception detail:
 * <pre>
 * try {
 *   ....
 * }
 * catch (e) {
 *   fail(e)
 * }
 * </pre>
 *
 * <br/>
 * Failing with an exception detail and a message:
 * <pre>
 * try {
 *   ....
 * }
 * catch (e) {
 *   fail('Houston we have a problem', e)
 * }
 * </pre>
 *
 * @since 2.0
 */
public class FailClosureTarget
    implements ClosureTarget
{
  @VisibleForTesting
  static final String FAILED = "Failed";

  /**
   * Throws {@link MojoExecutionException}.
   */
  public Object call(final Object[] args) throws Exception {
    if (args == null || args.length == 0) {
      throw new MojoExecutionException(FAILED);
    }
    else if (args.length == 1) {
      if (args[0] instanceof Throwable) {
        Throwable cause = (Throwable) args[0];
        throw new MojoExecutionException(cause.getMessage(), cause);
      }
      else {
        throw new MojoExecutionException(String.valueOf(args[0]));
      }
    }
    else if (args.length == 2) {
      if (args[1] instanceof Throwable) {
        throw new MojoExecutionException(String.valueOf(args[0]), (Throwable) args[1]);
      }
      else {
        throw new Error("Invalid arguments to fail(Object, Throwable), second argument must be a Throwable");
      }
    }
    else {
      throw new Error("Too many arguments; expected one of: fail(), fail(Object) or fail(Object, Throwable)");
    }
  }
}