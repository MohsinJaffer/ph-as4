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
package com.helger.as4.server.message;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.mail.internet.MimeMessage;

import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.helger.as4.attachment.WSS4JAttachment;
import com.helger.as4.client.HttpMimeMessageEntity;
import com.helger.as4.crypto.ECryptoAlgorithmCrypt;
import com.helger.as4.crypto.ECryptoAlgorithmSign;
import com.helger.as4.crypto.ECryptoAlgorithmSignDigest;
import com.helger.as4.messaging.encrypt.EncryptionCreator;
import com.helger.as4.messaging.mime.MimeMessageCreator;
import com.helger.as4.server.holodeck.IHolodeckTests;
import com.helger.as4.soap.ESOAPVersion;
import com.helger.as4.util.AS4XMLHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xml.serialize.read.DOMReader;

@RunWith (Parameterized.class)
@Category (IHolodeckTests.class)
public class UserMessageSoapBodyPayloadTest extends AbstractUserMessageTestSetUp
{
  @Parameters (name = "{index}: {0}")
  public static Collection <Object []> data ()
  {
    return CollectionHelper.newListMapped (ESOAPVersion.values (), x -> new Object [] { x });
  }

  private final ESOAPVersion m_eSOAPVersion;

  public UserMessageSoapBodyPayloadTest (@Nonnull final ESOAPVersion eSOAPVersion)
  {
    m_eSOAPVersion = eSOAPVersion;
  }

  @Test
  public void testSendUnsignedMessageSuccess () throws Exception
  {
    final Node aPayload = DOMReader.readXMLDOM (new ClassPathResource ("SOAPBodyPayload.xml"));
    final Document aDoc = MockMessages.testUserMessageSoapNotSigned (m_eSOAPVersion, aPayload, null);
    final String sResponse = sendPlainMessage (new StringEntity (AS4XMLHelper.serializeXML (aDoc)), true, null);

    assertTrue (sResponse.contains ("Receipt"));
  }

  @Test
  public void testUserMessageSOAPBodyPayloadSignedSuccess () throws Exception
  {
    final Node aPayload = DOMReader.readXMLDOM (new ClassPathResource ("SOAPBodyPayload.xml"));

    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList<> ();
    final Document aDoc = MockMessages.testSignedUserMessage (m_eSOAPVersion, aPayload, aAttachments, s_aResMgr);
    final String sResponse = sendPlainMessage (new StringEntity (AS4XMLHelper.serializeXML (aDoc)), true, null);

    assertTrue (sResponse.contains ("Receipt"));
    assertTrue (sResponse.contains ("NonRepudiationInformation"));
    assertTrue (sResponse.contains (ECryptoAlgorithmSign.SIGN_ALGORITHM_DEFAULT.getAlgorithmURI ()));
    assertTrue (sResponse.contains (ECryptoAlgorithmSignDigest.SIGN_DIGEST_ALGORITHM_DEFAULT.getAlgorithmURI ()));
  }

  @Test
  public void testUserMessageSOAPBodyPayloadSignedMimeSuccess () throws Exception
  {
    final Node aPayload = DOMReader.readXMLDOM (new ClassPathResource ("SOAPBodyPayload.xml"));
    final MimeMessage aMsg = new MimeMessageCreator (m_eSOAPVersion).generateMimeMessage (MockMessages.testSignedUserMessage (m_eSOAPVersion,
                                                                                                                              aPayload,
                                                                                                                              null,
                                                                                                                              s_aResMgr),
                                                                                          null);
    final String sResponse = sendMimeMessage (new HttpMimeMessageEntity (aMsg), true, null);

    assertTrue (sResponse.contains ("Receipt"));
    assertTrue (sResponse.contains ("NonRepudiationInformation"));
    assertTrue (sResponse.contains (ECryptoAlgorithmSign.SIGN_ALGORITHM_DEFAULT.getAlgorithmURI ()));
    assertTrue (sResponse.contains (ECryptoAlgorithmSignDigest.SIGN_DIGEST_ALGORITHM_DEFAULT.getAlgorithmURI ()));
  }

  @Test
  public void testUserMessageSOAPBodyPayloadEncryptSuccess () throws Exception
  {
    final Node aPayload = DOMReader.readXMLDOM (new ClassPathResource ("SOAPBodyPayload.xml"));

    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList<> ();
    Document aDoc = MockMessages.testUserMessageSoapNotSigned (m_eSOAPVersion, aPayload, aAttachments);
    aDoc = new EncryptionCreator ().encryptSoapBodyPayload (m_eSOAPVersion,
                                                            aDoc,
                                                            false,
                                                            ECryptoAlgorithmCrypt.ENCRPYTION_ALGORITHM_DEFAULT);

    final String sResponse = sendPlainMessage (new StringEntity (AS4XMLHelper.serializeXML (aDoc)), true, null);

    assertTrue (sResponse.contains ("Receipt"));
  }

  @Test
  public void testUserMessageSOAPBodyPayloadSignedEncryptedSuccess () throws Exception
  {
    final Node aPayload = DOMReader.readXMLDOM (new ClassPathResource ("SOAPBodyPayload.xml"));

    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList<> ();
    Document aDoc = MockMessages.testSignedUserMessage (m_eSOAPVersion, aPayload, aAttachments, s_aResMgr);
    aDoc = new EncryptionCreator ().encryptSoapBodyPayload (m_eSOAPVersion,
                                                            aDoc,
                                                            false,
                                                            ECryptoAlgorithmCrypt.ENCRPYTION_ALGORITHM_DEFAULT);

    final String sResponse = sendPlainMessage (new StringEntity (AS4XMLHelper.serializeXML (aDoc)), true, null);

    assertTrue (sResponse.contains ("Receipt"));
    assertTrue (sResponse.contains ("NonRepudiationInformation"));
    assertTrue (sResponse.contains (ECryptoAlgorithmSign.SIGN_ALGORITHM_DEFAULT.getAlgorithmURI ()));
    assertTrue (sResponse.contains (ECryptoAlgorithmSignDigest.SIGN_DIGEST_ALGORITHM_DEFAULT.getAlgorithmURI ()));
  }
}
