/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.as4.server;

import java.io.File;

import javax.annotation.Nonnull;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.helger.as4.servlet.mgr.AS4ServerConfiguration;
import com.helger.as4.util.AS4ResourceManager;
import com.helger.commons.id.factory.FileIntIDFactory;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.url.URLHelper;
import com.helger.photon.basic.app.io.WebFileIO;
import com.helger.photon.core.requesttrack.RequestTracker;
import com.helger.photon.jetty.JettyRunner;
import com.helger.servlet.mock.MockServletContext;
import com.helger.web.scope.mgr.WebScopeManager;

public final class MockJettySetup extends AbstractClientSetUp
{
  private static JettyRunner s_aJetty;
  private static AS4ResourceManager s_aResMgr;

  private MockJettySetup ()
  {}

  private static boolean _isRunJetty ()
  {
    return AS4ServerConfiguration.getSettings ().getAsBoolean ("server.jetty.enabled", false);
  }

  private static int _getJettyPort ()
  {
    return URLHelper.getAsURL (AS4ServerConfiguration.getSettings ().getAsString ("server.address")).getPort ();
  }

  @BeforeClass
  public static void startServer () throws Exception
  {
    if (_isRunJetty ())
    {
      final int nPort = _getJettyPort ();
      s_aJetty = new JettyRunner (nPort, nPort + 1000);
      s_aJetty.startServer ();
    }
    else
    {
      s_aJetty = null;
      WebScopeManager.onGlobalBegin (MockServletContext.create ());
      final File aSCPath = new File ("target/junittest").getAbsoluteFile ();
      WebFileIO.initPaths (new File (AS4ServerConfiguration.getDataPath ()).getAbsoluteFile (), aSCPath, false);
      GlobalIDFactory.setPersistentIntIDFactory (new FileIntIDFactory (WebFileIO.getDataIO ().getFile ("ids.dat")));
    }
    RequestTracker.getInstance ().getRequestTrackingMgr ().setLongRunningCheckEnabled (false);
    s_aResMgr = new AS4ResourceManager ();
  }

  @AfterClass
  public static void shutDownServer () throws Exception
  {
    s_aResMgr.close ();
    if (_isRunJetty ())
    {
      if (s_aJetty != null)
        s_aJetty.shutDownServer ();
      s_aJetty = null;
    }
    else
    {
      WebFileIO.resetPaths ();
      WebScopeManager.onGlobalEnd ();
    }
  }

  @Nonnull
  public static AS4ResourceManager getResourceManagerInstance ()
  {
    return s_aResMgr;
  }
}
