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
package com.helger.as4.messaging.sign;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

import com.helger.as4.attachment.WSS4JAttachment;
import com.helger.as4.attachment.WSS4JAttachmentCallbackHandler;
import com.helger.as4.crypto.AS4CryptoFactory;
import com.helger.as4.crypto.CryptoProperties;
import com.helger.as4.crypto.ECryptoAlgorithmSign;
import com.helger.as4.crypto.ECryptoAlgorithmSignDigest;
import com.helger.as4.messaging.domain.CreateUserMessage;
import com.helger.as4.soap.ESOAPVersion;
import com.helger.as4.util.AS4ResourceManager;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.ICommonsList;

public class SignedMessageCreator
{
  private final AS4CryptoFactory m_aCryptoFactory;

  public SignedMessageCreator ()
  {
    this (new AS4CryptoFactory ());
  }

  public SignedMessageCreator (@Nonnull final AS4CryptoFactory aCryptoFactory)
  {
    ValueEnforcer.notNull (aCryptoFactory, "CryptoFactory");
    m_aCryptoFactory = aCryptoFactory;
  }

  @Nonnull
  private WSSecSignature _getBasicBuilder (@Nonnull final ECryptoAlgorithmSign eCryptoAlgorithmSign,
                                           @Nonnull final ECryptoAlgorithmSignDigest eCryptoAlgorithmSignDigest)
  {
    final CryptoProperties aCryptoProps = m_aCryptoFactory.getCryptoProperties ();

    final WSSecSignature aBuilder = new WSSecSignature ();
    aBuilder.setUserInfo (aCryptoProps.getKeyAlias (), aCryptoProps.getKeyPassword ());
    aBuilder.setKeyIdentifierType (WSConstants.BST_DIRECT_REFERENCE);
    aBuilder.setSignatureAlgorithm (eCryptoAlgorithmSign.getAlgorithmURI ());
    // PMode indicates the DigestAlgorithm as Hash Function
    aBuilder.setDigestAlgo (eCryptoAlgorithmSignDigest.getAlgorithmURI ());
    return aBuilder;
  }

  /**
   * This method must be used if the message does not contain attachments, that
   * should be in a additional mime message part.
   *
   * @param aPreSigningMessage
   *        SOAP Document before signing
   * @param eSOAPVersion
   *        SOAP version to use
   * @param aAttachments
   *        Optional list of attachments
   * @param aResMgr
   *        Resource manager to be used.
   * @param bMustUnderstand
   *        Must understand?
   * @param eCryptoAlgorithmSign
   *        Signing algorithm
   * @param eCryptoAlgorithmSignDigest
   *        Signing digest algorithm
   * @return The created signed SOAP document
   * @throws WSSecurityException
   *         If an error occurs during signing
   */
  @Nonnull
  public Document createSignedMessage (@Nonnull final Document aPreSigningMessage,
                                       @Nonnull final ESOAPVersion eSOAPVersion,
                                       @Nullable final ICommonsList <WSS4JAttachment> aAttachments,
                                       @Nonnull final AS4ResourceManager aResMgr,
                                       final boolean bMustUnderstand,
                                       @Nonnull final ECryptoAlgorithmSign eCryptoAlgorithmSign,
                                       @Nonnull final ECryptoAlgorithmSignDigest eCryptoAlgorithmSignDigest) throws WSSecurityException
  {
    final WSSecSignature aBuilder = _getBasicBuilder (eCryptoAlgorithmSign, eCryptoAlgorithmSignDigest);

    if (CollectionHelper.isNotEmpty (aAttachments))
    {
      // Modify builder for attachments
      aBuilder.getParts ().add (new WSEncryptionPart ("Body", eSOAPVersion.getNamespaceURI (), "Content"));
      // XXX where is this ID used????
      aBuilder.getParts ().add (new WSEncryptionPart (CreateUserMessage.PREFIX_CID + "Attachments", "Content"));

      final WSS4JAttachmentCallbackHandler aAttachmentCallbackHandler = new WSS4JAttachmentCallbackHandler (aAttachments,
                                                                                                            aResMgr);
      aBuilder.setAttachmentCallbackHandler (aAttachmentCallbackHandler);
    }

    // Start signing the document
    final WSSecHeader aSecHeader = new WSSecHeader (aPreSigningMessage);
    aSecHeader.insertSecurityHeader ();
    // Set the mustUnderstand header of the wsse:Security element as well
    final Attr aMustUnderstand = aSecHeader.getSecurityHeader ().getAttributeNodeNS (eSOAPVersion.getNamespaceURI (),
                                                                                     "mustUnderstand");
    if (aMustUnderstand != null)
      aMustUnderstand.setValue (eSOAPVersion.getMustUnderstandValue (bMustUnderstand));

    return aBuilder.build (aPreSigningMessage, m_aCryptoFactory.getCrypto (), aSecHeader);
  }
}
